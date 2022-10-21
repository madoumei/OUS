package com.web.controller;

import com.client.bean.EquipmentGroup;
import com.client.bean.Visitor;
import com.client.service.EquipmentGroupService;
import com.client.service.VisitorService;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.MD5;
import com.utils.UtilTools;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "SubAccountController", tags = "API_入驻企业管理", hidden = true)
public class SubAccountController {

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private ManagerService mgrService;

    @Autowired
    private  OperateLogService operateLogService;

    @Autowired
    private  AppointmentService appointmentService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private ManagerService managerService;
    
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "/getAllCompany 获取所有入驻企业信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAllCompany")
    @ResponseBody
    public RespInfo getAllCompany() {
        List<CompanyInfo> listci = subAccountService.getAllCompanybySA();
        return new RespInfo(0, "success", listci);
    }


    @ApiOperation(value = "/getSubAccountById 根据入驻企业id获取企业", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"id\": 123456\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubAccountById")
    @ResponseBody
    public RespInfo getSubAccountById(
            @ApiParam(value = "SubAccount 入驻企业Bea", required = true) @Validated @RequestBody SubAccount sa,
            BindingResult result, HttpServletRequest request) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        sa = subAccountService.getSubAccountById(sa.getId());
        // TODO: 2020/4/30 获取子账户树结构
        if (null != sa) {
            ReqSubAccount rsa = new ReqSubAccount();
            rsa.setUserid(sa.getUserid());
            rsa.setIsUse(-1);
            List<SubAccount> subAccountList = subAccountService.getSubAccountByUserid(rsa);
            List<SubAccount> list = subAccountService.getSubAccountTree(subAccountList);
            sa.setSubAccountList(list);
            List<Permission> p = permissionService.getPermissionByaccount(sa.getCompanyName());
            if (p.size() > 0) {
                List<Permission> permissionTree = permissionService.getPermissionTree(p);
                sa.setModule(permissionTree);
            }
        }

        return new RespInfo(0, "success", sa);
    }

    @ApiOperation(value = "/getSubAccountEmpList 根据入驻企业id获取企业所有人员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"subaccountId\": 123456789\n" +
                    "    \"timestamp\": timestamp\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubAccountEmpList")
    @ResponseBody
    public RespInfo getSubAccountEmpList(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        List<Employee> emplist = new ArrayList<Employee>();
        String timestamp = "";
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getAccountRole().equals("2") || authToken.getAccountRole().equals("4") || authToken.getAccountRole().equals("6")) {
            int subaccountId = rep.getSubaccountId();

            if (subaccountId == 0) {
                UserInfo userinfo = userService.getUserInfoByUserId(authToken.getUserid());
                if (null != userinfo.getRefreshDate()) {
                    timestamp = userinfo.getRefreshDate().toString();
                }

                if (null == rep.getTimestamp() || !timestamp.equals(rep.getTimestamp())) {
                    emplist = employeeService.getSubAccountEmpList(authToken.getUserid(), subaccountId);
                }
            } else {
                SubAccount sa = subAccountService.getSubAccountById(subaccountId);
                if (null != sa.getRefreshDate()) {
                    timestamp = sa.getRefreshDate().toString();
                }

                if (null == rep.getTimestamp() || !timestamp.equals(rep.getTimestamp())) {
                    emplist = employeeService.getSubAccountEmpList(authToken.getUserid(), subaccountId);
                }
            }
        } else {
            throw new RuntimeException(ErrorEnum.E_610.getMsg());
        }

        return new RespInfo(0, timestamp, emplist);
    }

    @ApiOperation(value = "根据UserId查询入驻企业列表 /getSubAccountByUserid ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{userid: 2147483647}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubAccountByUserid")
    @ResponseBody
    public RespInfo getSubAccountByUserid(@ApiParam(value = "eqSubAccount 请求入驻企业Bean", required = true) @Validated @RequestBody ReqSubAccount reqSubAccount,
                                          HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqSubAccount.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
            String gids = manager.getGid();
            reqSubAccount.setGids(gids);
        }else if (AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())) {
            SubAccount oldsa = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if (null == oldsa) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            reqSubAccount.setGids(oldsa.getGids());
        }
        List<SubAccount> listsa = subAccountService.getSubAccountByUserid(reqSubAccount);
        if (listsa.size() > 0) {
            for (SubAccount subAccount : listsa) {
                List<Permission> permissions = permissionService.getPermissionByaccount(subAccount.getEmail());
                if (permissions.size() > 0) {
                    List<Permission> permissionTree = permissionService.getPermissionTree(permissions);
                    subAccount.setModule(permissionTree);
                }
            }
        }
        return new RespInfo(0, "success", listsa);

    }


    @ApiOperation(value = "根据subaccountId查询入驻企业 /getSubAccountInFoById ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "userid: 2147483647" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubAccountInFoById")
    @ResponseBody
    public RespInfo getSubAccountInFoById(
            @ApiParam(value = "SubAccount 入驻企业Bea", required = true) @Validated @RequestBody SubAccount sa,
            BindingResult result, HttpServletRequest request) {
        sa = subAccountService.getSubAccountById(sa.getId());
        return new RespInfo(0, "success", sa);
    }

    @ApiOperation(value = "增加入驻企业 /addSubAccount ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addSubAccount")
    @ResponseBody
    public RespInfo addSubAccount(@RequestBody SubAccount sa, HttpServletRequest request) {
//        boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(sa.getUserid()));
//        if (!v) {
//            return new RespInfo(1, "invalid user");
//        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());

        List<SubAccount> salist = subAccountService.CheckSubAccountByCompany(sa.getCompanyName());
        if (salist.size() == 0) {
            SubAccount oldsa = subAccountService.getSubAccountByEmail(sa.getEmail());
            if (null == oldsa) {
                int a = subAccountService.addSubAccount(sa);
                sa.setSubAccountiId(sa.getId());
                subAccountService.updateSubAccount(sa);
                if (a > 0) {
                    // TODO: 2020/4/28 添加子管理员权限菜单
                    if (null != sa.getModule() && sa.getModule().size() > 0) {
                        for (Permission permission : sa.getModule()) {
                            permission.setParentId(0);
                            permission.setAccount(sa.getCompanyName());
                            permissionService.addPermission(permission);
                            //递归添加子权限菜单
                            if (null != permission.getChildren() && permission.getChildren().size() > 0) {
                                permissionService.RecursiveAddPerChildren(permission.getId(), permission.getChildren(), sa.getCompanyName());
                            }
                        }
                    }
                    // TODO: 2020/4/7 添加日志log
                    if (StringUtils.isNotEmpty(sa.getLoginAccount())) {
                        String optId = "";
                        String optName = "";
                        String optRole = "";
                        Manager manager = managerService.getManagerByAccount(sa.getLoginAccount());
                        if (null != manager) {
                            optId = manager.getAccount();
                            optName = manager.getSname();
                            optRole = String.valueOf(manager.getsType());
                        } else {
                            UserInfo userInfo = userService.getUserInfo(sa.getUserid());
                            optId = userInfo.getEmail();
                            optName = userInfo.getUsername();
                            optRole = "0";
                        }
                        OperateLog log = new OperateLog();
                        String ipAddr = request.getHeader("X-Forwarded-For");
                        log.setUserid(sa.getUserid());
                        log.setOptId(optId);
                        log.setOptName(optName);
                        log.setOptRole(optRole);
                        log.setIpAddr(ipAddr);
                        log.setObjId("");
                        log.setObjName(sa.getCompanyName());
                        log.setoTime(new Date());
                        log.setOptEvent("增加");
                        log.setOptClient("0");
                        log.setOptModule("4");
                        log.setOptDesc("成功,添加企业: " + sa.getCompanyName());
                        operateLogService.addLog(log);

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("key", "company_batch");
                        map.put("company_id",sa.getUserid());

                        messageSender.updateFaceLib(map);
                    }
                    return new RespInfo(0, "success", sa);
                }
            } else {
                return new RespInfo(4, "email already exist");
            }
        } else {
            return new RespInfo(3, "companyName already exist");
        }
        return new RespInfo(1, "insert failed");
    }

    @ApiOperation(value = "删入驻企业 /delSubAccount ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delSubAccount")
    @ResponseBody
    public RespInfo delSubAccount(@RequestBody SubAccount sa, HttpServletRequest request) {
        SubAccount oldsa = subAccountService.getSubAccountById(sa.getId());
//        boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(oldsa.getUserid()));
//        if (!v) {
//            return new RespInfo(1, "invalid user");
//        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());
        // TODO: 2020/4/28 删除管理员权限菜单
        SubAccount s = subAccountService.getSubAccountById(sa.getId());
        if (null != s) {
            String email = s.getEmail();
            permissionService.delPermissionByaccount(email);
        }

        // TODO: 2020/4/7 日志记录
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(sa.getLoginAccount())) {
            UserInfo userInfo = userService.getUserInfo(sa.getUserid());
            optId = userInfo.getEmail();
            optName = userInfo.getUsername();
            optRole = "0";

            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(sa.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(oldsa.getCompanyName());
            log.setoTime(new Date());
            log.setOptEvent("删除");
            log.setOptClient("0");
            log.setOptModule("4");
            log.setOptDesc("成功,删除企业: " + oldsa.getCompanyName());
            operateLogService.addLog(log);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "company_batch");
            map.put("company_id",sa.getUserid());

            messageSender.updateFaceLib(map);
        }

        int a = subAccountService.delSubAccount(sa.getId());
        if (a > 0) {
            employeeService.delSAEmployees(sa.getId());
            SubAccountTemplate sat = new SubAccountTemplate();
            sat.setSubaccountId(sa.getId());
            appointmentService.delSAtemplate(sat);
            return new RespInfo(0, "success");
        }

        return new RespInfo(0, "delete failed");
    }

    @ApiOperation(value = "批量删入驻企业 /batchDelSubAccount ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/batchDelSubAccount")
    @ResponseBody
    public RespInfo batchDelSubAccount(@RequestBody ReqSubAccount rsa, HttpServletRequest request) {
        for (int i = 0; i < rsa.getIds().size(); i++) {
            SubAccount oldsa = subAccountService.getSubAccountById(rsa.getIds().get(i));
//            boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(rsa.getUserid()));
//            if (!v) {
//                return new RespInfo(1, "invalid user");
//            }
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            rsa.setUserid(authToken.getUserid());
            // TODO: 2020/4/28 删除管理员权限菜单
            SubAccount s = subAccountService.getSubAccountById(rsa.getIds().get(i));
            if (null != s) {
                String email = s.getEmail();
                permissionService.delPermissionByaccount(email);
            }

            // TODO: 2020/4/7 日志记录
            String optId = "";
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(oldsa.getLoginAccount())) {
                UserInfo userInfo = userService.getUserInfo(rsa.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";

                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(rsa.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName(oldsa.getCompanyName());
                log.setoTime(new Date());
                log.setOptEvent("删除");
                log.setOptClient("0");
                log.setOptModule("4");
                log.setOptDesc("成功,删除企业: " + oldsa.getCompanyName());
                operateLogService.addLog(log);
            }

            int a = subAccountService.delSubAccount(rsa.getIds().get(i));
            if (a > 0) {
                employeeService.delSAEmployees(rsa.getIds().get(i));
                SubAccountTemplate sat = new SubAccountTemplate();
                sat.setSubaccountId(rsa.getIds().get(i));
                appointmentService.delSAtemplate(sat);

            }
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "更新入驻企业 /updateSubAccount ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSubAccount")
    @ResponseBody
    public RespInfo updateSubAccount(@RequestBody SubAccount sa, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());

        SubAccount oldsa = subAccountService.getSubAccountByCompany(sa.getCompanyName());
        int a = 0;
        if (null == oldsa || oldsa.getId() == sa.getId() ) {
            UserInfo userinfo = userService.selectBycompany(sa.getCompanyName());
            if (null == userinfo) {
                oldsa = subAccountService.getSubAccountById(sa.getId());
//                boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(oldsa.getUserid()));
//                if (!v) {
//                    return new RespInfo(1, "invalid user");
//                }
//
                a = subAccountService.updateSubAccount(sa);

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key", "company_update");
                map.put("company_id",sa.getUserid());
                map.put("sub_company_id",sa.getId());
                map.put("group_ids",sa.getEgids());

                messageSender.updateFaceLib(map);

                // TODO: 2020/4/28 修改入驻企业管理员权限菜单
                if (sa.getModule().size() > 0 && null != sa.getModule()) {
                    permissionService.updatePermisionByAccount(sa.getModule(), sa.getEmail());
                }
            }
        }

        if (a > 0) {
            // TODO: 2020/4/7 日志记录
            String optId = "";
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(sa.getLoginAccount())) {
                Manager manager = managerService.getManagerByAccount(sa.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    UserInfo userInfo = userService.getUserInfo(sa.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(sa.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName(sa.getCompanyName());
                log.setoTime(new Date());
                log.setOptEvent("修改");
                log.setOptClient("0");
                log.setOptModule("4");
                log.setOptDesc("成功,更新企业: " + sa.getCompanyName());
                operateLogService.addLog(log);
            }
            return new RespInfo(0, "success");
        }

        return new RespInfo(3, "companyName already exist");
    }

    @ApiOperation(value = "启用/停用入驻企业 /updateIsUse ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateIsUse")
    @ResponseBody
    public RespInfo updateIsUse(@RequestBody SubAccount sa, HttpServletRequest request) {
    	HashOperations hashOperations = redisTemplate.opsForHash();
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());
        subAccountService.updateIsUse(sa);
        if (sa.getIsUse() == 0) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "company_disable");
            map.put("company_id", sa.getUserid());
            map.put("sub_company_id", sa.getId());
            messageSender.updateFaceLib(map);

            List<Employee> emplist = employeeService.getSubAccountEmpList(sa.getUserid(), sa.getId());

            //公司停用后，收回停用时间之后该公司所有邀请预约访客的通行权限（按拜访时间和有效期判断），推送取消邀请或取消预约的通知
            UserInfo userInfo = userService.getUserInfo(sa.getUserid());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Employee> employeeList = emplist.stream()
                            .filter(employee -> employee.getEmpType() == 1 && StringUtils.isNotEmpty(employee.getOpenid()))
                            .collect(Collectors.toList());
                    if (null != employeeList && employeeList.size() > 0) {
                        try {
                            for (Employee employee : employeeList) {
                            	 if(null!=employee.getEmpPhone()&&hashOperations.hasKey(employee.getEmpPhone(), "token")) {
                                 	String token=(String) hashOperations.get(employee.getEmpPhone(), "token");
                                 	hashOperations.delete(token, "id");
                                 }
                            	
                                //查询出所有的访客数据(邀请、预约)
                                List<Visitor> visitors = visitorService.getVListBySubAccountId(employee.getEmpid(), sa.getUserid());
                                if (null != visitors && visitors.size() > 0) {
                                    for (Visitor visitor : visitors) {
                                        if (StringUtils.isNotEmpty(visitor.getVphone())) {
                                            visitorService.sendCancelSMS(userInfo, employee, visitor);
                                            Person person = personInfoService.getVisitPersonByPhone(visitor.getVphone());
                                            if (null != person && StringUtils.isNotEmpty(person.getPopenid())) {
                                                visitorService.sendCancelVisitByWeixin(visitor, person);
                                            }
                                        }
                                        // TODO: 2020/8/3 收回拜访权限
                                        if (visitor.getVid() > 0) {
                                            visitor.setPermission(2);
                                            visitorService.updatePermission(visitor);
                                            System.out.println("收回预约访客："+visitor.getVname()+"邀请权限。");
                                        }
                                        if (visitor.getAppid() > 0) {
                                            Appointment at = new Appointment();
                                            at.setId(visitor.getAppid());
                                            at.setStatus(4);
                                            at.setClientNo(visitor.getClientNo());
                                            int i = appointmentService.AppointmentReply(at);
                                            System.out.println("收回邀请访客："+visitor.getVname()+"邀请权限。");
                                        }

                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } else if (sa.getIsUse() == 1) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "company_enable");
            map.put("company_id", sa.getUserid());
            map.put("sub_company_id", sa.getId());
            messageSender.updateFaceLib(map);
        }
        // TODO: 2020/7/31 停用企业操作日志
        if (StringUtils.isNotEmpty(sa.getLoginAccount())) {
            SubAccount account = subAccountService.getSubAccountById(sa.getId());
            String optId = "";
            String optName = "";
            String optRole = "";
            UserInfo userInfo = userService.getUserInfo(sa.getUserid());
            Manager manager = managerService.getManagerByAccount(sa.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                SubAccount subAccount = subAccountService.getSubAccountByEmail(sa.getLoginAccount());
                if (null != subAccount) {
                    optId = subAccount.getEmail();
                    optName = subAccount.getCompanyName();
                    optRole = "6";
                } else {
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(account.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(account.getCompanyName());
            log.setoTime(new Date());
            log.setOptEvent("修改");
            log.setOptClient("0");
            log.setOptModule("4");
            if (sa.getIsUse() == 1) {
                log.setOptDesc("成功,启用企业");
            } else {
                log.setOptDesc("成功,停用企业");
            }
            operateLogService.addLog(log);
        }

        return new RespInfo(0, "success", sa);
    }

    @ApiOperation(value = "修改入驻企业密码 /updateSubAccountPwd ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSubAccountPwd")
    @ResponseBody
    public RespInfo updateSubAccountPwd(@RequestBody SubAccount sa, HttpServletRequest request) {
        SubAccount oldsa = subAccountService.getSubAccountPassword(sa.getId());
//        boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(oldsa.getUserid()));
//        if (!v) {
//            return new RespInfo(1, "invalid user");
//        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());

        if (oldsa.getPassword().compareTo(MD5.crypt(sa.getOldPwd())) != 0) {
            return new RespInfo(2, "invalid password");
        }
        sa.setPassword(MD5.crypt(sa.getPassword()));

        int a = subAccountService.updateSubAccountPwd(sa);
        if (a > 0) {
            // TODO: 2020/4/7 日志记录
            SubAccount subAccount = subAccountService.getSubAccountById(sa.getId());
            if (subAccount != null) {
                String optId = subAccount.getEmail();
                String optName = subAccount.getCompanyName();
                String optRole = "6";
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(subAccount.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName(subAccount.getCompanyName());
                log.setoTime(new Date());
                log.setOptEvent("修改");
                log.setOptClient("0");
                log.setOptModule("4");
                log.setOptDesc("成功,修改入驻企业登录密码");
                operateLogService.addLog(log);
            }
            return new RespInfo(0, "success");
        }

        return new RespInfo(0, "update failed");
    }

    @ApiOperation(value = "重置入驻企业密码 /resetSubAccountPwd ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/resetSubAccountPwd")
    @ResponseBody
    public RespInfo resetSubAccountPwd(@RequestBody SubAccount sa, HttpServletRequest request) {
        SubAccount newsa = new SubAccount();
        newsa.setId(sa.getId());
        newsa.setPassword(MD5.crypt(sa.getPassword()));

        SubAccount oldsa = subAccountService.getSubAccountById(sa.getId());
//        boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(oldsa.getUserid()));
//        if (!v) {
//            return new RespInfo(1, "invalid user");
//        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sa.setUserid(authToken.getUserid());

        int a = subAccountService.updateSubAccountPwd(newsa);
        if (a > 0) {
            // TODO: 2020/4/7 日志记录
            String optId = "";
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(sa.getLoginAccount())) {
                Manager manager = managerService.getManagerByAccount(sa.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    UserInfo userInfo = userService.getUserInfo(sa.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(sa.getUserid());
                log.setOptId(optId);//操作者id
                log.setOptName(optName);//操作者姓名
                log.setOptRole(optRole);//角色 操作角色  0管理员 1hse 2员工 3前台
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName(oldsa.getCompanyName());
                log.setoTime(new Date());
                log.setOptEvent("修改");//事件，修改、增加、删除、登录
                log.setOptClient("0");//操作端 0 PC  1 移动
                log.setOptModule("4");//操作模块 0登录 1员工 2账号
                log.setOptDesc("成功,重置: " + oldsa.getCompanyName() + " 企业初始密码");
                operateLogService.addLog(log);
            }
            return new RespInfo(0, "success");
        }

        return new RespInfo(0, "update failed");
    }

    @ApiOperation(value = "获取入驻企业模板 /getSubAccountTemp ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubAccountTemp")
    @ResponseBody
    public RespInfo getSubAccountTemp(@RequestBody SubAccountTemplate sat,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        sat.setUserid(authToken.getUserid());
        SubAccountTemplate subacctemp = appointmentService.getSAtemplate(sat.getSubaccountId(), sat.getTemplateType(), sat.getGid());
        if (null == subacctemp) {
            Usertemplate ut = new Usertemplate();
            ut.setUserid(sat.getUserid());
            ut.setTemplateType(sat.getTemplateType());
            ut.setGid(sat.getGid());
            ut = appointmentService.getUsertemplate(ut);
            return new RespInfo(0, "success", ut);
        }

        return new RespInfo(0, "success", subacctemp);
    }

    @ApiOperation(value = "获取入驻企业管理员 /GetSubAccountManager ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetSubAccountManager")
    @ResponseBody
    public RespInfo GetSubAccountManager(@RequestBody Manager manager, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        manager.setUserid(authToken.getUserid());
        List<Manager> mgrList = mgrService.getManagerListBySubAccountId(manager);
        for (Manager m : mgrList) {
            List<Permission> allPermission = permissionService.getPermissionByaccount(m.getAccount());
            if (!allPermission.isEmpty() && allPermission.size() > 0) {
                List<Permission> list = permissionService.getPermissionTree(allPermission);
                m.setModule(list);
            }
        }
        return new RespInfo(0, "success", mgrList);
    }

}
