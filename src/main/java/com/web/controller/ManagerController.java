package com.web.controller;

import com.config.exception.ErrorEnum;
import com.utils.Constant;
import com.utils.MD5;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "FaceIdentifyController", tags = "API_管理员管理", hidden = true)
public class ManagerController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ManagerService mgrService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private OperateLogService operateLogService;

    @ApiOperation(value = "/AddManager 添加管理员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/AddManager")
    @ResponseBody
    public RespInfo addManager(@ApiParam(value = "Manager 管理员Bean", required = true) @Validated @RequestBody Manager manager,
                               HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
        || manager.getUserid() != authToken.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Manager m = mgrService.getManagerByAccount(manager.getAccount());
        if (null != m) {
            return new RespInfo(3, "account already exist");
        }

        UserInfo userinfo = userService.selectByName(manager.getAccount());

        if (userinfo != null) {
            return new RespInfo(1013, "user already exist");
        }

        if (StringUtils.isNotBlank(manager.getPassword())) {
            manager.setPassword(MD5.crypt(manager.getPassword()));
        }
        manager.setCreateTime(new Date());
        mgrService.addManager(manager);

        // TODO: 2020/4/22 添加权限角色
        List<Permission> modules = manager.getModule();
        if (null != manager.getModule() && manager.getModule().size() > 0) {
            for (Permission permission : modules) {
                //添加一级菜单
                permission.setParentId(0);
                permission.setAccount(manager.getAccount());
                permissionService.addPermission(permission);
                Integer parentid = permission.getId();
                //递归添加子权限菜单
                if (null != permission.getChildren() && permission.getChildren().size() > 0) {
                    permissionService.RecursiveAddPerChildren(parentid, permission.getChildren(), manager.getAccount());
                }
            }
        }
        //添加日志
        if (StringUtils.isNotEmpty(manager.getLoginAccount())) {
            Manager loginManage = null;
            String optName = "";
            String optId = "";
            String optRole = "";
            UserInfo userInfo = null;
            loginManage = managerService.getManagerByAccount(manager.getLoginAccount());
            if (null != loginManage) {
                optId = loginManage.getAccount();
                optName = loginManage.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                SubAccount subAccount = subAccountService.getSubAccountByEmail(manager.getLoginAccount());
                if (null != subAccount) {
                    optId = subAccount.getEmail();
                    optName = subAccount.getCompanyName();
                    optRole = "6";
                } else {
                    userInfo = userService.getUserInfo(manager.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(manager.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);//角色 操作角色  0管理员 1hse 2员工 3前台
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(manager.getAccount());
            log.setoTime(new Date());
            log.setOptEvent("增加");//事件，修改、增加、删除、登录
            log.setOptClient("0");//操作端 0 PC  1 移动
            log.setOptModule("2");//操作模块 0登录 1员工 2账号
            switch (manager.getsType()) {
                case 0:
                    log.setOptDesc("成功,增加HSE账号: " + manager.getAccount());
                    break;
                case 1:
                    log.setOptDesc("成功,增加劳务公司账号: " + manager.getAccount());
                    break;
                case 2:
                    log.setOptDesc("成功,增加前台账号: " + manager.getAccount());
                    break;
                case 3:
                    log.setOptDesc("成功,增加物管账号: " + manager.getAccount());
                    break;
                case 4:
                    log.setOptDesc("成功,增加访客机账号: " + manager.getAccount());
                    break;
                case 5:
                    log.setOptDesc("成功,增加子管理员账号: " + manager.getAccount());
                    break;
                default:
                    log.setOptDesc("成功,增加账号: " + manager.getAccount());
            }
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/GetManager 获取管理员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"" +
                    "account\":\"ceshi\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetManager")
    @ResponseBody
    public RespInfo getManager(@ApiParam(value = "Manager 管理员Bean", required = true) @Validated @RequestBody Manager manager,
                               HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {

        }else {
            manager.setAccount(authToken.getLoginAccountId());
        }
        Manager mgr = mgrService.getManagerByAccount(manager.getAccount());
        if(mgr.getUserid() != authToken.getUserid()){
            return new RespInfo(0, "success",null);
        }
        return new RespInfo(0, "success", mgr);
    }

    @ApiOperation(value = "/DeleteManager 删除子账号，管理员和子管理允许删除，hse允许删除劳务公司账号", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/DeleteManager")
    @ResponseBody
    public RespInfo deleteManager(@RequestBody Manager manager, HttpServletRequest request) {
//        String token = request.getHeader("X-COOLVISIT-TOKEN");
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            UserInfo userInfo = userService.getBaseUserInfo(authToken.getUserid());
            if (null == userInfo){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if (null == mgr || mgr.getUserid() != manager.getUserid()) {
                return new RespInfo(1, "invalid user");
            }
        }else if (AuthToken.ROLE_HSE.equals(authToken.getAccountRole())){
            //hse允许操作劳务公司账号
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if (null == mgr || mgr.getUserid() != manager.getUserid() ) {
                return new RespInfo(1, "invalid user");
            }
            Manager dmr = mgrService.getManagerByAccount(manager.getAccount());
            if(null == dmr  || dmr.getUserid() != manager.getUserid()
                    || !AuthToken.ROLE_SUPP_COMPANY.equals(dmr.getsType()+"")){
                return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
            }
        }else if (AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())){
            SubAccount subAccount = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if (null == subAccount || subAccount.getUserid() != manager.getUserid()) {
                return new RespInfo(1, "invalid user");
            }
        }else {
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }

        Manager managerByAccount = managerService.getManagerByAccount(manager.getAccount());
        Manager loginManage = mgrService.getManagerByAccount(authToken.getLoginAccountId());
        if(mgrService.deleteManager(manager) == 0){
            return new RespInfo(ErrorEnum.E_005.getCode(), ErrorEnum.E_005.getMsg());
        }
        // TODO: 2020/4/23 删除子账户权限菜单
        permissionService.delPermissionByaccount(manager.getAccount());

        // TODO: 2020/4/10 添加删除管理员账号日志
        if (StringUtils.isNotEmpty(manager.getLoginAccount())) {
            String optId = "";
            String optName = "";
            String optRole = "";
            UserInfo userInfo = null;
            if (null != loginManage) {
                optId = loginManage.getAccount();
                optName = loginManage.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                SubAccount subAccount = subAccountService.getSubAccountByEmail(manager.getLoginAccount());
                if (null != subAccount) {
                    optId = subAccount.getEmail();
                    optName = subAccount.getCompanyName();
                    optRole = "6";
                } else {
                    userInfo = userService.getUserInfo(manager.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(manager.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(manager.getAccount());
            log.setoTime(new Date());
            log.setOptEvent("删除");
            log.setOptClient("0");
            log.setOptModule("2");//操作模块 0登录 1员工 2账号
            switch (managerByAccount.getsType()) {
                //0 hse 1劳务公司 2 前台 3物管  4访客机账号
                case 0:
                    log.setOptDesc("成功,删除HSE账号: " + manager.getAccount());
                    break;
                case 1:
                    log.setOptDesc("成功,删除劳务公司账号: " + manager.getAccount());
                    break;
                case 2:
                    log.setOptDesc("成功,删除前台账号: " + manager.getAccount());
                    break;
                case 3:
                    log.setOptDesc("成功,删除物管账号: " + manager.getAccount());
                    break;
                case 4:
                    log.setOptDesc("成功,删除访客机账号: " + manager.getAccount());
                    break;
                default:
                    log.setOptDesc("成功,删除账号: " + manager.getAccount());
            }
            operateLogService.addLog(log);
        }

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/UpdateSubAccountManager 更新入驻企业管理员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateSubAccountManager")
    @ResponseBody
    public RespInfo UpdateSubAccountManager(@RequestBody Manager manager, HttpServletRequest request) {
//        boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(manager.getUserid()));
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ("6".equals(authToken.getAccountRole())) {
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if (null == mgr || mgr.getUserid() != manager.getUserid()) {
                return new RespInfo(1, "invalid user");
            }
        }else if ("10".equals(authToken.getAccountRole())){
            SubAccount subAccount = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if (null == subAccount || subAccount.getUserid() != manager.getUserid()) {
                return new RespInfo(1, "invalid user");
            }
        }

        if (StringUtils.isNotBlank(manager.getPassword())) {
            Manager ac = managerService.getManagerByAccount(manager.getAccount());
            if (!manager.getPassword().equals(ac.getPassword())) {
                manager.setPassword(MD5.crypt(manager.getPassword()));
            }
        }
        Date startDate = manager.getStartDate();
        Date endDate = manager.getEndDate();
        String password = manager.getPassword();
        if (StringUtils.isNotBlank(password) || startDate != null || endDate != null){
            managerService.updateManager(manager);
        }
        // TODO: 2020/4/14 根据子账户名更新管理账号添加操作日志
        permissionService.updatePermisionByAccount(manager.getModule(), manager.getAccount());

        // TODO: 2020/4/23 添加操作日志
        if (StringUtils.isNotEmpty(manager.getLoginAccount())) {
            Manager loginManage = null;
            String optName = "";
            String optId = "";
            String optRole = "";
            UserInfo userInfo = null;
            loginManage = managerService.getManagerByAccount(manager.getLoginAccount());
            if (null != loginManage) {
                optId = loginManage.getAccount();
                optName = loginManage.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                SubAccount subAccount = subAccountService.getSubAccountByEmail(manager.getLoginAccount());
                if (null != subAccount){
                    optId = subAccount.getEmail();
                    optName = subAccount.getCompanyName();
                    optRole = "6";
                }else {
                    userInfo = userService.getUserInfo(manager.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(manager.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);//角色 操作角色  0管理员 1hse 2员工 3前台
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(manager.getAccount());
            log.setoTime(new Date());
            log.setOptEvent("修改");//事件，修改、增加、删除、登录
            log.setOptClient("0");//操作端 0 PC  1 移动
            log.setOptModule("2");//操作模块 0登录 1员工 2账号
            switch (manager.getsType()) {
                case 0:
                    log.setOptDesc("成功,修改HSE账号: " + manager.getAccount());
                    break;
                case 1:
                    log.setOptDesc("成功,修改劳务公司账号: " + manager.getAccount());
                    break;
                case 2:
                    log.setOptDesc("成功,修改前台账号: " + manager.getAccount());
                    break;
                case 3:
                    log.setOptDesc("成功,修改物管账号: " + manager.getAccount());
                    break;
                case 4:
                    log.setOptDesc("成功,修改访客机账号: " + manager.getAccount());
                    break;
                default:
                    log.setOptDesc("成功,修改账号: " + manager.getAccount());
            }
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/UpdateManager 更新管理员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\"" +
                    "account\":\"ceshi\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateManager")
    @ResponseBody
    public RespInfo updateManager(@RequestBody Manager manager, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                || manager.getUserid() != authToken.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();


        Manager mgr = mgrService.getManagerByAccount(manager.getAccount());
        if (null == mgr || mgr.getUserid() != manager.getUserid()) {
            return new RespInfo(1, "invalid user");
        }

        if (StringUtils.isNotBlank(manager.getPassword())) {
            Manager ac = managerService.getManagerByAccount(manager.getAccount());
            if (!manager.getPassword().equals(ac.getPassword())) {
                manager.setPassword(MD5.crypt(manager.getPassword()));
            }
        }
        mgrService.updateManager(manager);
        if (hashOperations.hasKey("fail_login" + manager.getAccount(), "locktime")) {
          hashOperations.delete("fail_login" + manager.getAccount(), "locktime");
          hashOperations.delete("fail_login" + manager.getAccount(), "failcount");
        }
        // TODO: 2020/4/14 根据子账户名更新管理账号添加操作日志
        permissionService.updatePermisionByAccount(manager.getModule(), manager.getAccount());

        // TODO: 2020/4/23 添加操作日志
        if (StringUtils.isNotEmpty(manager.getLoginAccount())) {
            Manager loginManage = null;
            String optName = "";
            String optId = "";
            String optRole = "";
            loginManage = managerService.getManagerByAccount(manager.getLoginAccount());
            if (null != loginManage) {
                optId = loginManage.getAccount();
                optName = loginManage.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(manager.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(manager.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);//角色 操作角色  0管理员 1hse 2员工 3前台
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(manager.getAccount());
            log.setoTime(new Date());
            log.setOptEvent("修改");//事件，修改、增加、删除、登录
            log.setOptClient("0");//操作端 0 PC  1 移动
            log.setOptModule("2");//操作模块 0登录 1员工 2账号
            switch (manager.getsType()) {
                case 0:
                    log.setOptDesc("成功,修改HSE账号: " + manager.getAccount());
                    break;
                case 1:
                    log.setOptDesc("成功,修改劳务公司账号: " + manager.getAccount());
                    break;
                case 2:
                    log.setOptDesc("成功,修改前台账号: " + manager.getAccount());
                    break;
                case 3:
                    log.setOptDesc("成功,修改物管账号: " + manager.getAccount());
                    break;
                case 4:
                    log.setOptDesc("成功,修改访客机账号: " + manager.getAccount());
                    break;
                default:
                    log.setOptDesc("成功,修改账号: " + manager.getAccount());
            }
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/getManagerByMobile 根据手机号获取管理员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"moblie\":\"123456789\",\n" +
                    "\"stype\":\"stype\",\n" +
                    "\"userid\":\"userid\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getManagerByMobile")
    @ResponseBody
    public RespInfo getManagerByMobile(@RequestBody Manager manager) {
        Manager mgr = mgrService.getManagerByMobile(manager);
        return new RespInfo(0, "success", mgr);
    }


    @ApiOperation(value = "/updateServerManagerPwd 修改子帐号密码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"account\":\"account\",\n" +
                    "\"userid\":\"userid\",\n" +
                    "\"password\":\"password\",\n" +
                    "\"oldPwd\":\"oldPwd\",\n" +
                    "\"loginAccount\":\"loginAccount\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateServerManagerPwd")
    @ResponseBody
    public RespInfo updateServerManagerPwd(@RequestBody Manager manager, HttpServletRequest request) {
    	 AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
		 if (authToken.getUserid() != manager.getUserid()) {
	          return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
	     }
		 
		 
		manager.setsType(Integer.parseInt(authToken.getAccountRole()));
        Manager oldManager = mgrService.getManagerByAccount(manager.getAccount());
        if (null != oldManager && StringUtils.isNotBlank(manager.getOldPwd())) {
            if (oldManager.getPassword().compareTo(MD5.crypt(manager.getOldPwd())) !=0) {
                return new RespInfo(2, "invalid password");
            }
            oldManager.setPassword(MD5.crypt(manager.getPassword()));
            mgrService.updateServerManagerPwd(oldManager);
        }
        
        
        // TODO: 2020/4/23 添加操作日志
        if (StringUtils.isNotEmpty(manager.getLoginAccount())) {
            Manager loginManage = null;
            String optName = "";
            String optId = "";
            String optRole = "";
            loginManage = managerService.getManagerByAccount(manager.getLoginAccount());
            if (null != loginManage) {
                optId = loginManage.getAccount();
                optName = loginManage.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(manager.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(manager.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);//角色 操作角色  0管理员 1hse 2员工 3前台
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(manager.getAccount());
            log.setoTime(new Date());
            log.setOptEvent("修改密码");//事件，修改、增加、删除、登录
            log.setOptClient("0");//操作端 0 PC  1 移动
            log.setOptModule("2");//操作模块 0登录 1员工 2账号
            switch (manager.getsType()) {
                case 0:
                    log.setOptDesc("成功,修改HSE密码: " + manager.getAccount());
                    break;
                case 1:
                    log.setOptDesc("成功,修改劳务公司密码: " + manager.getAccount());
                    break;
                case 2:
                    log.setOptDesc("成功,修改前台密码: " + manager.getAccount());
                    break;
                case 3:
                    log.setOptDesc("成功,修改物管密码: " + manager.getAccount());
                    break;
                case 4:
                    log.setOptDesc("成功,修改访客机密码: " + manager.getAccount());
                    break;
                default:
                    log.setOptDesc("成功,修改密码: " + manager.getAccount());
            }
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }
}
