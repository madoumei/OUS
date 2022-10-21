package com.web.controller;

import com.client.service.EquipmentGroupService;
import com.client.service.VisitorService;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.utils.*;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "PersonalInfoController", tags = "API_人员信息管理", hidden = true)
public class PersonalInfoController {

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private ResidentVisitorService residentVisitorService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private SubAccountService subAccountService;

    @ApiOperation(value = "/addPersonInfo 人员注册",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"popenid\": popenid\n" +
                    "    \"userid\": 132465\n" +
                    "    \"type\": v 访客 i 员工\n" +
                    "    \"password\": password\n" +
                    "    \"pmobile\": pmobile\n" +
                    "    \"sid\": sid\n" +
                    "    \"avatar\": avatar\n" +
                    "    \"pname\": pname\n" +
                    "    \"pemail\": pemail\n" +
                    "    \"paddress\": paddress\n" +
                    "    \"pqq\": pqq\n" +
                    "    \"pcompany\": pcompany\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/addPersonInfo")
    @ResponseBody
    public RespInfo addPersonInfo(
            @ApiParam(value = "电子名片表", required = true) @Validated @RequestBody Person p,
            BindingResult result, HttpServletRequest request) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        if (null == p.getPopenid() || "".equals(p.getPopenid())) {
            SysLog.warn("openid="+p.getPopenid()+" phone="+p.getPmobile()+" name="+p.getPname());
            return new RespInfo(667, "openid is null");
        }

        //验证短信验证码
        {
            String phone = p.getPmobile();
            String smscode = p.getSmscode();
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            if (hashOperations.hasKey(phone + "-_-" + smscode, "smscode")) {
                Long expireDate = (long) hashOperations.get(phone + "-_-" + smscode, "smscode");
                Date currentDate = new Date();
                if (expireDate < currentDate.getTime()) {
                    hashOperations.delete(phone + "-_-" + smscode, "smscode");
                    return new RespInfo(ErrorEnum.E_144.getCode(), ErrorEnum.E_144.getMsg());
                }
            } else {
                return new RespInfo(ErrorEnum.E_145.getCode(), ErrorEnum.E_145.getMsg());
            }
            hashOperations.delete(phone + "-_-" + smscode, "smscode");
        }

        if ("v".equals(p.getType())) {
            Person oldperson = personInfoService.getVisitPersonByPhone(p.getPmobile());
            if (null == oldperson) {
                if (null != p.getPassword() && !"".equals(p.getPassword())) {
                    p.setPassword(MD5.crypt(p.getPassword()));
                }
                //手机号变更,但openid已存在，删除原有重新注册
                oldperson = personInfoService.getVisitPersonByOpenid(p.getPopenid());
                if(oldperson != null){
                    personInfoService.delVisitPerson(oldperson.getPopenid());
                }
                int i = personInfoService.addVisitPerson(p);
                if (i > 0) {
                    //创建token
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    Date date = new Date();
                    Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                    AuthToken at = new AuthToken(p.getPmobile(), "8", p.getUserid(), p.getPopenid(), System.currentTimeMillis());
                    String token = tokenServer.getAESEncoderTokenString(at);


                    if (null != hashOperations.get(token, "token")) {
                        hashOperations.delete(token, "token");
                    }
                    hashOperations.put(token, "id", exprieDate.getTime());
                    hashOperations.put(p.getPmobile(), "token", token);
                    redisTemplate.expire(token, 15, TimeUnit.HOURS);
                    redisTemplate.expire(p.getPmobile(), 15, TimeUnit.HOURS);
                    p.setAuthToken(token);
                    p.setType("v");
                    return new RespInfo(0, "success", p);
                } else {
                    SysLog.error("insert to database failed",p);
                    return new RespInfo(61, "add person failed");
                }
            } else {
                //创建token
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken at = new AuthToken(p.getPmobile(), "8", p.getUserid(), p.getPopenid(), System.currentTimeMillis());
                String token = tokenServer.getAESEncoderTokenString(at);
                if (null != hashOperations.get(token, "token")) {
                    hashOperations.delete(token, "token");
                }
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(p.getPmobile(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(p.getPmobile(), 15, TimeUnit.HOURS);
                p.setAuthToken(token);
                p.setType("i");
                p.setOldmobile(p.getPmobile());
                personInfoService.updateVisitPerson(p);
                return new RespInfo(0, "success",p);
            }
        } else {
            //员工
            List<Employee> emplist = new ArrayList<Employee>();

            RequestEmp rep = new RequestEmp();
            rep.setPhone(p.getPmobile());
            rep.setUserid(p.getUserid());
            rep.setSubaccountId(p.getSid());
            emplist = employeeService.getEmployeeList(rep);

            if (emplist.size()==0 ){
                SysLog.warn("error code=1,no employee,phone="+p.getPmobile()+" userid="+p.getUserid()+" sid="+p.getSid());
                return new RespInfo(1, "no employee:"+p.getPmobile());
            }
            
            Employee emp = emplist.get(0);
            
            if(emp.getSubaccountId()!=0) {
	            SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
	            if (null!=sa&&sa.getIsUse() == 0){
	                    return new RespInfo(94, "Account is disabled");
	            }
            }
            /**
             * 有效期判断
             */
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
            int d1 = Integer.parseInt(emp.getStartDate());
            int d2 = Integer.parseInt(emp.getEndDate());
            int today = Integer.parseInt(sd.format(new Date()));
            if (today < d1 || today > d2) {
                return new RespInfo(1113, "employee invalid date");
            }

            Person oldperson = personInfoService.getInvitePersonByPhone(p.getPmobile());
            if ( null == oldperson) {
                //手机号变更,但openid已存在，删除原有重新注册
                personInfoService.delInvitePersonByOpenid(p.getPopenid());
                int i = personInfoService.addInvitePerson(p);
                if (i == 0) {
                    return new RespInfo(61, "add person failed");
                }
            } else {
                    oldperson.setPname(p.getPname());
                    oldperson.setPemail(p.getPemail());
                    oldperson.setPposition(p.getPposition());
                    oldperson.setPaddress(p.getPaddress());
                    oldperson.setPopenid(p.getPopenid());
                    oldperson.setOldmobile(p.getPmobile());
                    if (StringUtils.isNotEmpty(p.getAvatar())) {
                        oldperson.setAvatar(p.getAvatar());
                    }
                    oldperson.setPqq(p.getPqq());
                    oldperson.setPcompany(p.getPcompany());
                    oldperson.setUserid(p.getUserid());
                    personInfoService.updateInvitePerson(oldperson);
            }

            emp.setUserid(p.getUserid());
            emp.setEmpPhone(p.getPmobile());
            emp.setOpenid(p.getPopenid());
            if (StringUtils.isNotEmpty(p.getAvatar())) {
                emp.setAvatar(p.getAvatar());
            }
            emp.setSubaccountId(p.getSid());
            employeeService.bindingOpenidByPhone(emp);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "employee_update");
            map.put("employee_id", emp.getEmpid());
            map.put("company_id", emp.getUserid());
            map.put("current_time", new Date().getTime());
            messageSender.updateFaceLib(map);

            //创建token
            HashOperations hashOperations = redisTemplate.opsForHash();
            Date date = new Date();
            Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
            AuthToken at = new AuthToken(emp.getEmpid()+"", "7", emp.getUserid(), emp.getOpenid(), System.currentTimeMillis());
            String token = tokenServer.getAESEncoderTokenString(at);
            if (null != hashOperations.get(token, "token")) {
                hashOperations.delete(token, "token");
            }
            hashOperations.put(token, "id", exprieDate.getTime());
            hashOperations.put(emp.getEmpPhone(), "token", token);
            redisTemplate.expire(token, 15, TimeUnit.HOURS);
            redisTemplate.expire(emp.getEmpPhone(), 15, TimeUnit.HOURS);
            emp.setToken(token);

            return new RespInfo(0, "success",emp);
        }
    }

    @ApiOperation(value = "/checkBlacklist 校验黑名单",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\": 123456789\n" +
                    "    \"credentialNo\": credentialNo\n" +
                    "    \"sids\": sids\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/checkBlacklist")
    @ResponseBody
    public RespInfo checkBlacklist(
            @ApiParam(value = "Blacklist 黑名单Bean", required = true) @Validated @RequestBody Blacklist bl,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        UserInfo userInfo = userService.getUserInfoByUserId(authToken.getUserid());
        List<Blacklist> bList = new ArrayList<>();
        if (userInfo.getBlackListSwitch() == 1) {
            bl.setUserid(userInfo.getUserid());
            bList = blacklistService.checkBlacklist(bl);
        }
        return new RespInfo(0, "success", bList);
    }

    @ApiOperation(value = "/cancelPersonInfo 注销人员信息",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"type\": v\n" +
                    "    \"popenid\": popenid\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/cancelPersonInfo")
    @ResponseBody
    public RespInfo cancelPersonInfo(
            @ApiParam(value = "电子名片表", required = true) @Validated @RequestBody Person p,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if(AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())){
            if(p.getPopenid()==null || !p.getPopenid().equals(authToken.getOpenid())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if ("v".equals(p.getType())) {
            Person visitor = personInfoService.getVisitPersonByOpenid(p.getPopenid());
            if (!authToken.getOpenid().equals(visitor.getPopenid())) {
                return new RespInfo(1, "invalid user");
            }
            personInfoService.delVisitPerson(p.getPopenid());

        } else {
            Person invite = personInfoService.getInvitePersonByOpenid(p.getPopenid());
            System.out.println("popenid:" + invite.toString());
            if (!authToken.getOpenid().equals(invite.getPopenid())) {
                return new RespInfo(1, "invalid user");
            }

            personInfoService.delInvitePersonByOpenid(p.getPopenid());
            Employee emp = new Employee();
            emp.setOpenid(p.getPopenid());
            employeeService.resetOpenid(emp);
        }

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(authToken.getOpenid(), "id")) {
            hashOperations.delete(authToken.getOpenid(), "id");
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addBlacklist 添加黑名单",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"credentialNo\":\"\",\n" +
                    "    \"name\":\"666666\",\n" +
                    "    \"phone\":\"13912518266\",\n" +
                    "    \"gids\":\"\",\n" +
                    "    \"sids\":\"\",\n" +
                    "    \"gname\":\"\",\n" +
                    "    \"sname\":\"\",\n" +
                    "    \"remark\":\"eeeee\"\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/addBlacklist")
    @ResponseBody
    public RespInfo addBlacklist(
            @ApiParam(value = "Blacklist 黑名单Bean", required = true) @Validated @RequestBody Blacklist bl,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        )
                ||authToken.getUserid() != bl.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        Blacklist rb = new Blacklist();
        rb.setPhone(bl.getPhone());
        rb.setUserid(bl.getUserid());
        rb.setCredentialNo(bl.getCredentialNo());

        List<Blacklist> blist = blacklistService.checkBlacklist(rb);
        if (blist.size() > 0) {
            return new RespInfo(3, "user already exist");
        }

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateStr = dateFormat.format(date);
        bl.setBid(dateStr);
        blacklistService.addBlacklist(bl);
        // TODO: 2020/4/7 日志记录
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(bl.getLoginAccount())) {
            Manager manager = managerService.getManagerByAccount(bl.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(bl.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(bl.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(bl.getName());
            log.setoTime(new Date());
            log.setOptEvent("增加");
            log.setOptClient("0");
            log.setOptModule("6");
            if (StringUtils.isNotEmpty(bl.getPhone())) {
                log.setOptDesc("成功，添加黑名单手机号:" + bl.getPhone());
            } else if (StringUtils.isNotEmpty(bl.getCredentialNo())) {
                log.setOptDesc("成功，添加黑名单身份证号:" + bl.getCredentialNo());
            }
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/ExportBlacklist 导出黑名单", httpMethod = "GET", notes = "Get示例入参：\n")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportBlacklist")
    public void ExportBlacklist(HttpServletRequest req, HttpServletResponse response) {
        String ctoken = req.getParameter("token");
        
        if(StringUtils.isNotBlank(ctoken)) {
        	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }
        
        if(StringUtils.isBlank(ctoken)){
        	System.out.println("token is null");
        	return;
        }
        
        int userid = 0;
        try {
            userid = Integer.parseInt(req.getParameter("userid"));
            String condition = req.getParameter("condition");
            String sids = req.getParameter("sids");
            String gid = "-1";//如果设为-1，子管理员不检查gid
            //检查权限
            if(tokenServer.checkUserAuthorityForExport(managerService,redisTemplate,response, ctoken, userid, gid)) return;

            ReqBlacklist rbl = new ReqBlacklist();
            rbl.setUserid(userid);
            rbl.setCondition(condition);
            rbl.setSids(sids);

            List<Blacklist> bList = blacklistService.getBlacklist(rbl);

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "黑名单记录";

            HSSFWorkbook wb = new HSSFWorkbook();
            ExcelDownLoad download = new AgentInfoExcelDownLoad();

            wb = download.createDownLoadRblExcel(bList, wb, userService,userid);

            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
            wb.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_BLACKLIST, bList.size(), "导出"+filename+":"+rbl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
