package com.web.controller;

import com.alibaba.fastjson.JSON;
import com.client.bean.*;
import com.client.service.InterimCardService;
import com.client.service.VisitorService;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.*;
import com.utils.FileUtils.DelFileThread;
import com.utils.FileUtils.MinioTools;
import com.utils.empUtils.EmpUtils;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.jsonUtils.JsonUtil;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "EmpController", tags = "API_员工管理", hidden = true)
public class EmpController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private VisitProxyService visitProxyService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private SuperAccountService superAccountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ResidentVisitorService residentVisitorService;

    @Autowired
    private InterimCardService interimCardService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private RoleService roleService;
    
    @ApiOperation(value = "/getEmployeeByEmail 根据邮箱获取员工信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"email\": 123456789\n" +
                    "    \"userid\": 123456789\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeByEmail")
    @ResponseBody
    public RespInfo getEmployeeByEmail(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
        if (respInfo != null) {
            return respInfo;
        }

        String email = reqemp.getEmail();
        List<Employee> emplist = employeeService.getEmployeeByEmail(email, reqemp.getUserid());
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        } catch (RuntimeException e) {
            //没有token的情况，隐藏敏感信息
            if (emplist != null) {
                List<Employee> respList = new ArrayList<Employee>();
                for (Employee employee : emplist) {
                    Employee newEmp = new Employee();
                    newEmp.setEmpid(employee.getEmpid());
                    newEmp.setEmpName(employee.getEmpName());
                    respList.add(newEmp);
                }
                emplist = respList;
            }

        }


        return new RespInfo(0, "success", emplist);

    }


    @ApiOperation(value = "/getEmployeeByPhone 根据手机号获取员工信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\": 123456789\n" +
                    "    \"userid\": 123456789\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeByPhone")
    @ResponseBody
    public RespInfo getEmployeeByPhone(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        String empPhone = reqemp.getPhone();
        List<Employee> emplist = employeeService.getEmpInfo(reqemp.getUserid(), empPhone);
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //token和userid检查
            RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
            if(authToken.getUserid()==0 && respInfo != null && respInfo.getStatus()==ErrorEnum.E_610.getCode()){
                //访客token没有userid,不检查userid的有效性
                //访客或者不是管理员和子管理员的情况，隐藏敏感信息
                emplist = getSimpleEmpList(emplist);
            }else if(respInfo != null){
                return respInfo;
            }else if(!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole()))
            {
                //非管理员或子管理员获得脱敏信息
                emplist = getSimpleEmpList(emplist);
            }
        } catch (RuntimeException e) {
            //没有token的情况，检查签名
            RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
            if(respInfo != null)
            {
                return respInfo;
            }
            //没有token的情况，隐藏敏感信息
            emplist = getSimpleEmpList(emplist);
        }
        return new RespInfo(0, "success", emplist);

    }

    @ApiOperation(value = "/getEmployeeByEncoderVid 根据加密后访客id获取员工信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"digest\":\"8EBC6964DAC1AEFF1A86727AB2DDE5B9\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeByEncoderVid")
    @ResponseBody
    public RespInfo getEmployeeByVid(@ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody RequestVisit req,
                                     BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        String vid = AESUtil.decode(req.getDigest(), Constant.AES_KEY);
        Visitor v = null;
        if(vid.startsWith("v")){
            v = visitorService.getVisitorById(Integer.parseInt(vid.substring(1)));
        }else if(vid.startsWith("a")){
            Appointment appointment = appointmentService.getAppointmentbyId(Integer.parseInt(vid.substring(1)));
            if(appointment != null){
                v = BeanUtils.appointmentToVisitor(appointment);
            }
        }else{
            v = visitorService.getVisitorById(Integer.parseInt(vid));
        }

        if (null == v) {
            return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
        } else {
            ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
            List<Employee> emplist = employeeService.checkEmployeeExists(v.getUserid(), v.getEmpPhone());
            List<Employee> resultlist = new ArrayList<Employee>();//只返回必要数据
            if (null != emplist && emplist.size() > 0) {
                for (int i = 0; i < emplist.size(); i++) {
                    AuthToken authToken = new AuthToken();
                    authToken.setUserid(emplist.get(i).getUserid());
                    authToken.setAccountRole("7");
                    authToken.setLoginAccountId(emplist.get(i).getEmpid() + "");
                    authToken.setDateTime(new Date().getTime());
                    String encoderToken = null;
                    try {
                        encoderToken = AESUtil.encode(mapperInstance.writeValueAsString(authToken), Constant.AES_KEY);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    hashOperations.put(encoderToken, "id", exprieDate.getTime());
                    emplist.get(i).setToken(encoderToken);
                    Employee e = new Employee();
                    e.setToken(encoderToken);
                    e.setSubaccountId(emplist.get(i).getSubaccountId());
                    e.setVegids(emplist.get(i).getVegids());
                    e.setEgids(emplist.get(i).getEgids());
                    resultlist.add(e);
                }
            }
            return new RespInfo(0, "success", resultlist);
        }
    }

    @ApiOperation(value = "/getSubEmpListPages 获取入驻企业员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubEmpListPages")
    @ResponseBody
    public RespInfo getSubEmpListPages(@RequestBody RequestEmp emp, HttpServletRequest request) {
//        boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(emp.getUserid()));
//        if (!v) {
//            return new RespInfo(1, "invalid user");
//        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != emp.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        //入驻企业管理员校验权限，未调试
        if(AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())){
            SubAccount subAccount = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if(!(subAccount != null && subAccount.getId()==emp.getSubaccountId())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }
        emp.setUserid(authToken.getUserid());

        Page rpage = new Page(emp.getStartIndex() / emp.getRequestedCount() + 1, emp.getRequestedCount(), 0);
        emp.setPage(rpage);
        List<Employee> emplist = employeeService.getSubEmpListPages(emp);
        rpage.setList(emplist);

        return new RespInfo(0, "success", rpage);
    }


    @ApiOperation(value = "/getEmpByName 根据姓名,工号，手机号模糊搜索员工信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"2147483647\",\n" +
                    "    \"name\":\"许怀文\"\n" +
                    "}"+
                    "限制条件：只有管理员，子管理员，入驻企业管理员可以模糊搜索，为了防止返回数据过大，最多返回100条数据，访客只能通过名字精确搜索，其他账号类型无法访问"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmpByName")
    @ResponseBody
    public RespInfo getEmpByName(@RequestBody RequestEmp reqemp,HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = null;
        try {
             authToken = tokenServer.getAuthTokenByRequest(request);
        }catch (Exception e){

        }
        //检查userid
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
        if (respInfo != null) {
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }

        //检查入驻企业id
        if(authToken != null && AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())){
            SubAccount subAccount = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if(!(subAccount != null && subAccount.getId()==reqemp.getSubaccountId())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }
        List<Employee> emplist = employeeService.getEmpListByKey(reqemp);
        if(emplist == null){
            return new RespInfo(0, "success");
        }

        if(authToken == null || authToken.getUserid() == 0){
            //访客token，只能返回少量数据
            if(emplist.size()>20)
            {
                //大公司同名的人比较多，限制为20个
                return new RespInfo(0, "success");
            }
            emplist = getSimpleEmpList(emplist);
        }else if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())
        ){
            //授权的账号类型
        }else{
            //未授权的账号类型
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        return new RespInfo(0, "success", emplist);
    }

    @ApiOperation(value = "/getVisitProxyForEmp 获取访客接待代理人", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"empid\": 123456\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitProxyForEmp")
    @ResponseBody
    public RespInfo getVisitProxyForEmp(
            @ApiParam(value = "EmpVisitProxy 员工访客代理Bean", required = true) @RequestBody EmpVisitProxy evp,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            //员工验证员工id
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == employee || employee.getEmpid() != evp.getEmpid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            evp.setEmpid(Integer.parseInt(authToken.getLoginAccountId()));
        } else if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            //管理员验证userid
            UserInfo userInfo = userService.getUserInfoByUserId(authToken.getUserid());
            if (null == userInfo) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        evp.setUserid(authToken.getUserid());
        EmpVisitProxy vp = visitProxyService.getProxyInfoByEid(evp);
        return new RespInfo(0, "success", vp);
    }


    @ApiOperation(value = "/getEmployeeByName 根据员工姓名精确搜索查询员工,只返回empid，empName,empPhone后4位(解决重名问题)，并且最多只返回20条数据", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"2147483647\",\n" +
                    "    \"name\": 张三\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeByName")
    @ResponseBody
    public RespInfo getEmployeeByName(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        List<Employee> emplist = employeeService.getEmpListByempName(reqemp);

        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //token和签名检查
            RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
            if(authToken.getUserid()==0 && respInfo != null && respInfo.getStatus()==ErrorEnum.E_610.getCode()){

            }else if(respInfo != null){
                return respInfo;
            }
        } catch (RuntimeException e) {
            //没有token的情况，检查签名
            RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
            if(respInfo != null)
            {
                return respInfo;
            }
        }
        return new RespInfo(0, "success", emplist);
    }

    //获得脱敏的员工信息
    private List<Employee> getSimpleEmpList(List<Employee> emplist){
        if (emplist != null) {
            List<Employee> respList = new ArrayList<Employee>();
            for (Employee employee : emplist) {
                Employee newEmp = new Employee();
                newEmp.setEmpid(employee.getEmpid());
                newEmp.setEmpName(employee.getEmpName());
                newEmp.setEmpPhone("");
                newEmp.setEmpType(employee.getEmpType());
                respList.add(newEmp);
            }
            return respList;
        }
        return null;
    }

    //暂时无人使用
    @ApiOperation(value = "/checkEmployeeExistsStrict 根据员工姓名和其他信息校验", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"2147483647\",\n" +
                    "    \"name\": 张三\n" +
                    "    \"empPhone\": 1381390000\n" +
                    "    \"empNo\": 100222\n" +
                    "    \"email\": xx@163.com\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/checkEmployeeExistsStrict")
    @ResponseBody
    public RespInfo checkEmployeeExistsStrict(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, reqemp.getUserid());
        //访客token没有userid,不检查userid的有效性
        if(respInfo != null && respInfo.getStatus()!=ErrorEnum.E_610.getCode()){
            return respInfo;
        }

        List<Employee> emplist = null;
        if (StringUtils.isNotBlank(reqemp.getName())
                && (StringUtils.isNotBlank(reqemp.getPhone()) || StringUtils.isNotBlank(reqemp.getEmail()) || StringUtils.isNotBlank(reqemp.getEmpNo()))) {
            emplist = employeeService.checkEmployeeExistsStrict(reqemp);
            if (emplist != null) {
                for (Employee employee : emplist) {
                    employee.setEmpNo("");
                    employee.setEmpPhone("");
                    employee.setEmpEmail("");
                }
            }
        }
        return new RespInfo(0, "success", emplist);
    }


    /*
     * proxyType 0  邮箱 1 手机
     */
    @ApiOperation(value = "/setVisitProxy 设置员工访客代理", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"proxyId\": 123\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/setVisitProxy")
    @ResponseBody
    public RespInfo setVisitProxy(
            @ApiParam(value = "EmpVisitProxy 员工访客代理Bean", required = true) @RequestBody EmpVisitProxy evp,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //TODO 权限校验管理员，子管理员，员工本人
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            //员工验证员工id
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == employee || employee.getEmpid() != evp.getEmpid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            evp.setEmpid(Integer.parseInt(authToken.getLoginAccountId()));
            evp.setEmpName(employee.getEmpName());
        } else if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            //管理员验证userid
            UserInfo userInfo = userService.getUserInfoByUserId(authToken.getUserid());
            if (null == userInfo) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Employee proxyEmp = employeeService.getEmployee(evp.getProxyId());
        if (null == proxyEmp) {
            return new RespInfo(1119, "invalid employee");
        }
        evp.setProxyName(proxyEmp.getEmpName());
        evp.setUserid(authToken.getUserid());
        EmpVisitProxy vp = visitProxyService.getProxyInfoByEid(evp);
        if (null == evp || 0 == evp.getProxyId()) {
            return new RespInfo(222, "proxy is null");
        }
        if (null != vp) {
            visitProxyService.updateProxy(evp);
        } else {
            visitProxyService.addProxy(evp);
        }
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/getVisitProxyForProxy 根据访客代理人id获取访客代理人，把该员工所代理的所有被代理人", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"proxyId\": 123\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitProxyForProxy")
    @ResponseBody
    public RespInfo getVisitProxyForProxy(
            @ApiParam(value = "EmpVisitProxy 员工访客代理Bean", required = true) @RequestBody EmpVisitProxy evp,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            //员工验证员工id
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == employee) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            evp.setProxyId(Integer.parseInt(authToken.getLoginAccountId()));
        } else if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            //管理员验证userid
            UserInfo userInfo = userService.getUserInfoByUserId(authToken.getUserid());
            if (null == userInfo) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        evp.setUserid(authToken.getUserid());
        List<EmpVisitProxy> vplist = visitProxyService.getProxyInfoByPId(evp);
        return new RespInfo(0, "success", vplist);
    }

    @ApiOperation(value = "/getEmployeeCount 获取员工总数", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\": 123\n" +
                    "    \"username\": 123\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeCount")
    @ResponseBody
    public RespInfo getEmployeeCount(
            @ApiParam(value = "Conditions 条件Bean", required = true) @RequestBody Conditions con,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, con.getUserid());
        if (respInfo != null) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }

        int userid = con.getUserid();
        SuperAccount supacc = superAccountService.getSuperAccount(con.getUsername());
        int total = 0;
        int empcount = employeeService.getEmployeeCount(userid);

        if (null != supacc) {
            Map<String, String> conditions = new HashMap<String, String>();
            conditions.put("userids", "(" + supacc.getUserid() + ")");
            total = employeeService.getRelAccEmpCount(conditions);
        } else {
            total = empcount;
        }

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("total", total);
        map.put("empcount", empcount);

        return new RespInfo(0, "success", map);

    }

    @ApiOperation(value = "/getDeptList 获取部门列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\": 123\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getDeptList")
    @ResponseBody
    public RespInfo getDeptList(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Department> deptlist = departmentService.getDepartmentList(authToken.getUserid());
        for (int i = 0; i < deptlist.size(); i++) {
            getChildDeptList(deptlist.get(i), departmentService);

        }
        return new RespInfo(0, "success", deptlist);
    }

    public void getChildDeptList(Department dept, DepartmentService departmentService) {
        List<Department> childDeptList = departmentService.getChildDeptList(dept.getDeptid(), dept.getUserid());
        dept.setChildDeptList(childDeptList);
        dept.setEmpCount(departmentService.getChildDeptCount(dept).getEmpCount());
        if(dept.getDeptManagerEmpid() != null &&  StringUtils.isNotEmpty(dept.getDeptManagerEmpid())) {
            String managerids = dept.getDeptManagerEmpid();
            String[] idList = managerids.split(",");
            List<Employee> managers = new ArrayList<>();

            for(int j=0;j<idList.length;j++){
                try {
                    managers.add(employeeService.getEmployee(Integer.parseInt(idList[j])));
                }catch (Exception e){

                }
            }
            dept.setDeptManager(managers);
        }

        for (int i = 0; i < childDeptList.size(); i++) {

            getChildDeptList(childDeptList.get(i), departmentService);
        }

    }

    @ApiOperation(value = "/GetEmpList 获取员工列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetEmpList")
    @ResponseBody
    public RespInfo GetEmpList(@RequestBody RequestEmp rep, HttpServletRequest request) {
//        int userid = rep.getUserid();
//        String token = request.getHeader("X-COOLVISIT-TOKEN");
//        boolean v = UtilTools.validateUser(token, String.valueOf(userid));
//        if (!v) {
//            String account = token.substring(0, token.indexOf("-"));
//            Manager mgr = mgrService.getManagerByAccount(account);
//            if (null == mgr || mgr.getUserid() != userid) {
//                return new RespInfo(1, "invalid user");
//            }
//        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        String timestamp = "";

        UserInfo userinfo = userService.getUserInfo(authToken.getUserid());
        if (null != userinfo.getRefreshDate()) {
            timestamp = userinfo.getRefreshDate().toString();

        }
        List<Employee> emplist = new ArrayList<Employee>();
        if (null == rep.getTimestamp() || !timestamp.equals(rep.getTimestamp())) {
            emplist = employeeService.getEmployeeList(rep);
        }

        return new RespInfo(0, timestamp, emplist);
    }

    @ApiOperation(value = "/getEmpListPages 获取员工分页列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\": 123\n" +
                    "    \"startIndex\": 1\n" +
                    "    \"requestedCount\": 20\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmpListPages")
    @ResponseBody
    public RespInfo getEmpListPages(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp emp,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != emp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page rpage = new Page(emp.getStartIndex() / emp.getRequestedCount() + 1, emp.getRequestedCount(), 0);
        emp.setPage(rpage);
        List<Employee> emplist = employeeService.getEmpListPages(emp);
        rpage.setList(emplist);

        return new RespInfo(0, "success", rpage);
    }


    @ApiOperation(value = "/addRARG 增加角色", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rid\":5,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addRARG")
    @ResponseBody
    public RespInfo addRARG(@ApiParam(value = "Role 角色Bean", required = true) @RequestBody Role ro,
                            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ro.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        roleService.addRole(ro);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateRARG 删除角色", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rid\":5,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delRARG")
    @ResponseBody
    public RespInfo delRARG(@ApiParam(value = "Role 角色Bean", required = true) @RequestBody Role ro,
                            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ro.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        ro = roleService.getRole(ro);
        if (ro.getParentId() > 0) {
            int i = roleService.deleteRole(ro);
            if (i > 0) {
                roleService.delRoleEmp(ro);
            }
        } else if (ro.getParentId() == 0) {
            List<Role> rlist = roleService.getRoleList(ro);
            for (int i = 0; i < rlist.size(); i++) {
                roleService.delRoleEmp(rlist.get(i));
            }
            roleService.deleteRole(ro);
        }
        return new RespInfo(0, "success");

    }

    @ApiOperation(value = "/updateRARG 更新角色", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rgName\":\"HSE\",\n" +
                    "    \"parentId\":3,\n" +
                    "    \"rid\":5,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateRARG")
    @ResponseBody
    public RespInfo updateRARG(@ApiParam(value = "Role 角色Bean", required = true) @RequestBody Role ro,
                               HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ro.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        roleService.updateRole(ro);
        return new RespInfo(0, "success");

    }

    @ApiOperation(value = "/getRARG 获取角色列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\": 123\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRARG")
    @ResponseBody
    public RespInfo getRARG(@ApiParam(value = "Role 角色Bean", required = true) @RequestBody Role ro,
                            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ro.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Role> rlist = roleService.getRoleGroupList(ro);
        for (int i = 0; i < rlist.size(); i++) {
            rlist.get(i).setChildRoleList(roleService.getRoleList(rlist.get(i)));
        }
        return new RespInfo(0, "success", rlist);
    }


    @ApiOperation(value = "/getEmpRoleList 获取员工角色集合", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rid\":3,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmpRoleList")
    @ResponseBody
    public RespInfo getEmpRoleList(@ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp emp,
                                   HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        emp.setUserid(authToken.getUserid());
        Page rpage = new Page(emp.getStartIndex() / emp.getRequestedCount() + 1, emp.getRequestedCount(), 0);
        emp.setPage(rpage);
        List<Employee> elist = employeeService.getEmpRoleList(emp);
        rpage.setList(elist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/addEmpRole 新增员工角色", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rgName\":\"666\",\n" +
                    "    \"parentId\":9,\n" +
                    "    \"rid\":\"\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEmpRole")
    @ResponseBody
    public RespInfo addEmpRole(@ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp emp,
                               HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != emp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int rid = emp.getRid();
        Role ro = new Role();
        ro.setRid(emp.getRid());
        ro = roleService.getRole(ro);

        List<Integer> elist = emp.getEmpids();
        List<Role> erlist = new ArrayList<Role>();

        for (int i = 0; i < elist.size(); i++) {
            Role r = new Role();
            r.setRid(rid);
            r.setEmpid(elist.get(i));
            erlist.add(r);
        }

        roleService.addEmpRole(erlist);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delRoleEmp 删除员工角色", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rid\":11,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delRoleEmp")
    @ResponseBody
    public RespInfo delRoleEmp(@ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp emp,
                               HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != emp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rid", emp.getRid());
        map.put("empids", emp.getEmpids());

        roleService.deleteRoleEmp(map);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addEmployees 批量添加员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEmployees")
    @ResponseBody
    public RespInfo addEmployees(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody List<Employee> emplist,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (authToken.getUserid() != emplist.get(0).getUserid()) {
            return new RespInfo(1, "invalid user");
        }


        employeeService.delEmployeesByUserid(emplist.get(0).getUserid());
        departmentService.delRelationByUserid(emplist.get(0).getUserid());

        employeeService.addEmployees(emplist);

        List<Employee> list = new ArrayList<Employee>();
        for (int i = 0; i < emplist.size(); i++) {
            for (int a = 0; a < emplist.get(a).getDeptids().size(); a++) {
                Employee e = new Employee();
                e.setUserid(emplist.get(i).getUserid());
                e.setDeptid(emplist.get(i).getDeptids().get(a));
                e.setEmpid(emplist.get(i).getEmpid());
                list.add(e);
            }
        }

        if (list.size() > 0) {
            departmentService.addDeptEmpRelation(list);
        }

        //日志
        String optId = "";
        String optName = "";
        String optRole = "";
        UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
        Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
        if (null != manager) {
            optId = manager.getAccount();
            optName = manager.getSname();
            optRole = String.valueOf(manager.getsType());
        } else {
            SubAccount subAccount = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if (null != subAccount) {
                optId = subAccount.getEmail();
                optName = subAccount.getCompanyName();
                optRole = OperateLog.ROLE_SUB_COMPANY;
            } else {

                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = OperateLog.ROLE_ADMIN;
            }
        }
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(authToken.getUserid());
        log.setOptId(optId);
        log.setOptName(optName);
        log.setOptRole(optRole);
        log.setIpAddr(ipAddr);
        log.setObjId("");
        log.setObjName(emplist.get(0).getEmpName());
        log.setoTime(new Date());
        log.setOptEvent("增加");
        log.setOptClient("0");
        log.setOptModule(OperateLog.MODULE_EMPLOYEE);
        if (StringUtils.isNotEmpty(userInfo.getEscapeClause())) {
            log.setOptDesc("成功,添加员工: " + emplist.size() + "人,同意免责条款");
        } else {
            log.setOptDesc("成功,添加员工: " + emplist.size() + "人");
        }
        operateLogService.addLog(log);

        return new RespInfo(0, "success", emplist.size());
    }

    @ApiOperation(value = "/updateEmployee 更新员工信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"avatar\":\"\",\n" +
                    "    \"deptIds\":[\n" +
                    "        34315162\n" +
                    "    ],\n" +
                    "    \"egids\":null,\n" +
                    "    \"email\":\"fengm@coolvisit.com\",\n" +
                    "    \"empNickname\":\"\",\n" +
                    "    \"empNo\":\"0119605869674367\",\n" +
                    "    \"cardNo\":\"\",\n" +
                    "    \"empPosition\":\"研发总监\",\n" +
                    "    \"employee_name\":\"冯明\",\n" +
                    "    \"employeeid\":1402,\n" +
                    "    \"emptype\":0,\n" +
                    "    \"endDate\":\"20510129\",\n" +
                    "    \"phone\":\"13813946371\",\n" +
                    "    \"remark\":\"\",\n" +
                    "    \"startDate\":\"20210129\",\n" +
                    "    \"subaccountId\":0,\n" +
                    "    \"telephone\":\"805\",\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"account\":\"南京访客乐网络科技有限公司\",\n" +
                    "    \"visitType\":\"\",\n" +
                    "    \"workbay\":null,\n" +
                    "    \"appType\":0\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateEmployee")
    @ResponseBody
    public RespInfo updateEmployee(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        //入驻企业管理员校验权限
        if(AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())){
            SubAccount subAccount = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if(null==subAccount||subAccount.getId()!=rep.getSubaccountId()){
                Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
                if(null==manager||manager.getSubAccountId()!=rep.getSubaccountId()) {
                    return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }
            }
        }

        int empid = rep.getEmployeeid();
        int userid = rep.getUserid();

        String empName = rep.getEmployee_name();
        String empEmail = rep.getEmail();
        String empNo = rep.getEmpNo();
        String empPhone = rep.getPhone().trim();
        String empNickname = rep.getEmpNickname().trim();
        String remark = rep.getRemark();
        String workbay = rep.getWorkbay();
        String telephone = rep.getTelephone();
        int empType = rep.getEmptype();
        int subaccountId = rep.getSubaccountId();
        String empPosition = rep.getEmpPosition();
        String cardNo = rep.getCardNo();
        String egids = rep.getEgids();
        String startDate = rep.getStartDate();
        String endDate = rep.getEndDate();
        String avatar = rep.getAvatar();
        String vegids = rep.getVegids();
        int ecStatus = rep.getEcStatus();
        String empSex = rep.getEmpSex();
        int appType = rep.getAppType();
        String plateNum = rep.getPlateNum();
        List<Integer> deptIds = rep.getDeptIds();


        Employee emp = employeeService.getEmployee(empid);

        List<Employee> emplist = employeeService.checkEmployeeExists(userid, empPhone);

        List<Employee> elist = employeeService.getEmpListByName(userid, empName);

        if (null != emp) {
            if (emplist.size() == 0 || elist.size() == 0 || emplist.get(0).getEmpid() == empid) {
                UserInfo ui = new UserInfo();
                ui.setUserid(userid);
                ui.setRefreshDate(new Date());
                userService.updateRefreshDate(ui);

                if (subaccountId != 0 && emp.getSubaccountId() != 0 && subaccountId != emp.getSubaccountId()) {
                    SubAccount sa = new SubAccount();
                    sa.setId(subaccountId);
                    sa.setRefreshDate(new Date());
                    subAccountService.updateSARefreshDate(sa);
                    sa.setId(emp.getSubaccountId());
                    subAccountService.updateSARefreshDate(sa);
                } else if (subaccountId != 0) {
                    SubAccount sa = new SubAccount();
                    sa.setId(subaccountId);
                    sa.setRefreshDate(new Date());
                    subAccountService.updateSARefreshDate(sa);
                } else if (emp.getSubaccountId() != 0) {
                    SubAccount sa = new SubAccount();
                    sa.setId(emp.getSubaccountId());
                    sa.setRefreshDate(new Date());
                    subAccountService.updateSARefreshDate(sa);
                }

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key", "employee_update");
                map.put("company_id", userid);
                map.put("employee_id", emp.getEmpid());
                map.put("current_time", new Date().getTime());
                messageSender.updateFaceLib(map);

                emp.setEmpName(empName);
                emp.setEmpEmail(empEmail);
                emp.setEmpPhone(empPhone);
                emp.setEmpType(empType);
                emp.setSubaccountId(subaccountId);
                emp.setRemark(remark);
                emp.setEmpNickname(empNickname);
                emp.setWorkbay(workbay);
                emp.setTelephone(telephone);
                emp.setEmpPosition(empPosition);
                emp.setEmpNo(empNo);
                emp.setCardNo(cardNo);
                emp.setEgids(egids);
                emp.setStartDate(startDate);
                emp.setEndDate(endDate);
                emp.setAvatar(avatar);
                emp.setPlateNum(plateNum);
                emp.setVegids(vegids);
                emp.setEcStatus(ecStatus);
                emp.setEmpSex(empSex);
                emp.setVegids(vegids);
                emp.setAppType(appType);
                employeeService.updateEmployee(emp);

                if (deptIds.size() > 0) {
                    List<Employee> list = new ArrayList<Employee>();
                    for (int i = 0; i < deptIds.size(); i++) {
                        Employee e = new Employee();
                        e.setUserid(userid);
                        e.setDeptid(deptIds.get(i));
                        e.setEmpid(emp.getEmpid());
                        list.add(e);
                    }

                    if (list.size() > 0) {
                        departmentService.delRelationByEmp(empid);
                        departmentService.addDeptEmpRelation(list);
                    }
                }

                //2020/4/3 员工修改操作记录
                if (StringUtils.isNotEmpty(rep.getLoginAccount())) {
                    String optId = "";
                    String optName = "";
                    String optRole = "";
                    UserInfo userInfo = userService.getUserInfoByUserId(rep.getUserid());
                    Manager manager = managerService.getManagerByAccount(rep.getLoginAccount());
                    if (null != manager) {
                        optId = manager.getAccount();
                        optName = manager.getSname();
                        optRole = String.valueOf(manager.getsType());
                    } else {
                        SubAccount subAccount = subAccountService.getSubAccountByEmail(rep.getLoginAccount());
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
                    log.setUserid(rep.getUserid());
                    log.setOptId(optId);
                    log.setOptName(optName);
                    log.setOptRole(optRole);
                    log.setIpAddr(ipAddr);
                    log.setObjId(String.valueOf(empid));
                    log.setObjName(rep.getEmployee_name());
                    log.setoTime(new Date());
                    log.setOptEvent("修改");
                    log.setOptClient("0");
                    log.setOptModule("1");
                    if (StringUtils.isNotEmpty(userInfo.getEscapeClause())) {
                        log.setOptDesc("成功,更新员工: " + rep.getEmployee_name() + ",同意免责条款");
                    } else {
                        log.setOptDesc("成功,更新员工: " + rep.getEmployee_name());
                    }
                    operateLogService.addLog(log);
                }

                return new RespInfo(0, "success", emp);
            } else {
                return new RespInfo(24, "phone no unique");
            }
        } else {
            return new RespInfo(55, "No employee");
        }
    }


    @Deprecated
    @ApiOperation(value = "/getUserInfoByOpenid 根据OpneId获取企业信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getUserInfoByOpenid")
    @ResponseBody
    public RespInfo getUserInfoByOpenid(HttpServletRequest request) {

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        String empid = authToken.getLoginAccountId();
        Employee emp = employeeService.getEmployee(Integer.parseInt(empid));
        if (emp != null) {
            if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                    ||authToken.getUserid() != emp.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

            UserInfo ui = userService.getUserSwitch(emp.getUserid());
            if (emp.getSubaccountId() != 0) {
                SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
                ui.setCompany(sa.getCompanyName());
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("company", ui.getCompany());
            map.put("ui", ui);
            return new RespInfo(0, "success", map);
        } else {
            return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
        }
    }


    @ApiOperation(value = "/getEmpDeptList 获取员工部门集合", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"deptid\":34315162,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmpDeptList")
    @ResponseBody
    public RespInfo getEmpDeptList(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp rep,
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
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page rpage = new Page(rep.getStartIndex() / rep.getRequestedCount() + 1, rep.getRequestedCount(), 0);
        rep.setPage(rpage);
        List<Employee> emplist = employeeService.getEmpDeptList(rep);
        rpage.setList(emplist);
        return new RespInfo(0, "success", rpage);
    }


    @ApiOperation(value = "/getBlacklist 获取黑名单", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"condition\":\"2\",\n" +
                    "    \"sids\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getBlacklist")
    @ResponseBody
    public RespInfo getBlacklist(@RequestBody ReqBlacklist rbl,HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
        )
                ||authToken.getUserid() != rbl.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page rpage = new Page(rbl.getStartIndex() / rbl.getRequestedCount() + 1, rbl.getRequestedCount(), 0);
        rbl.setPage(rpage);
        List<Blacklist> bList = blacklistService.getBlacklist(rbl);
        rpage.setList(bList);
        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/delBlacklist 删除黑名单人员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"bids\":[\n" +
                    "        \"20210223155345198\"\n" +
                    "    ]\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delBlacklist")
    @ResponseBody
    public RespInfo delBlacklist(
            @ApiParam(value = "ReqBlacklist 请求黑名单Bean", required = true) @RequestBody ReqBlacklist rbl,
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
                ||authToken.getUserid() != rbl.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Blacklist> blacklists = blacklistService.getBlacklistBybids(rbl);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userid", rbl.getUserid());
        map.put("bids", rbl.getBids());
        blacklistService.delBlacklist(map);
        // 2020/4/7 删除黑名单日志记录
        if (!blacklists.isEmpty()) {
            List<String> strings = blacklists.stream().map(Blacklist::getName).collect(Collectors.toList());
            String optId = "";
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(rbl.getLoginAccount())) {
                Manager manager = managerService.getManagerByAccount(rbl.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    UserInfo userInfo = userService.getUserInfo(rbl.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(rbl.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName(String.join(",", strings));
                log.setoTime(new Date());
                log.setOptEvent("删除");
                log.setOptClient("0");
                log.setOptModule("6");
                if (!strings.isEmpty()) {
                    log.setOptDesc("成功,删除黑名单: " + strings.toString());
                }
                operateLogService.addLog(log);
            }
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getAllResidentVisitor 获取所有供应商，管理员，子管理员，hse可用", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"rstatus\":-1,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAllResidentVisitor")
    @ResponseBody
    public RespInfo getAllResidentVisitor(
            @ApiParam(value = "ResidentVisitor 供应商Bean", required = true) @RequestBody ResidentVisitor rv,
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
                ||authToken.getUserid() != rv.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page<ResidentVisitor> rpage = new Page<ResidentVisitor>(rv.getStartIndex() / rv.getRequestedCount() + 1, rv.getRequestedCount(), 0);
        rv.setPage(rpage);
        List<ResidentVisitor> rplist = residentVisitorService.getResidentVisitorByUserid(rv);
        rpage.setList(rplist);

        return new RespInfo(0, "success", rpage);
    }


    @ApiOperation(value = "/getInterimCard 获取临时卡", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"empid\":empid,\n" +
                    "    \"name\":name,\n" +
                    "    \"empNo\":empNo,\n" +
                    "    \"cardNo\":cardNo,\n" +
                    "    \"submitTime\":submitTime,\n" +
                    "    \"rstatus\":-1,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getInterimCard")
    @ResponseBody
    public RespInfo getInterimCard(@ApiParam(value = "ReqInterimCard 临时卡Bean", required = true) @RequestBody ReqInterimCard ric,
                                   HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ric.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_HSE.equals(authToken.getAccountRole())){

        }
        else if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == employee) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            ric.setEmpid(Integer.parseInt(authToken.getLoginAccountId()));
            ric.setEmpNo(employee.getEmpNo());
        }else {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page<InterimCard> rpage = new Page<InterimCard>(ric.getStartIndex() / ric.getRequestedCount() + 1, ric.getRequestedCount(), 0);
        ric.setPage(rpage);
        List<InterimCard> icList = interimCardService.getInterimCard(ric);
        rpage.setList(icList);
        return new RespInfo(0, "success", rpage);
    }


    @ApiOperation(value = "/getDeptByEmpid 根据员工ID获取员工部门", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"empid\":1402,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getDeptByEmpid")
    @ResponseBody
    public RespInfo getDeptByEmpid(
            @ApiParam(value = "Employee 员工Bean", required = true) @RequestBody Employee emp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != emp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_HSE.equals(authToken.getAccountRole())){

        }
        else if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == employee) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            emp.setEmpid(Integer.parseInt(authToken.getLoginAccountId()));
        }else {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Department> deptlist = departmentService.getDeptByEmpid(emp.getEmpid(), emp.getUserid());
        return new RespInfo(0, "success", deptlist);
    }

    @ApiOperation(value = "/addEmployee 增加员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"avatar\":\"\",\n" +
                    "    \"deptIds\":[\n" +
                    "        34315162\n" +
                    "    ],\n" +
                    "    \"egids\":\"1390\",\n" +
                    "    \"email\":\"1183711908@qq.com\",\n" +
                    "    \"empNickname\":\"\",\n" +
                    "    \"empNo\":\"\",\n" +
                    "    \"cardNo\":\"\",\n" +
                    "    \"empPosition\":\"\",\n" +
                    "    \"employee_name\":\"测试\",\n" +
                    "    \"emptype\":2,\n" +
                    "    \"endDate\":\"20210430\",\n" +
                    "    \"phone\":\"15251805548\",\n" +
                    "    \"remark\":\"\",\n" +
                    "    \"startDate\":\"20210301\",\n" +
                    "    \"subaccountId\":0,\n" +
                    "    \"telephone\":\"15251805548\",\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"visitType\":\"\",\n" +
                    "    \"workbay\":\"\",\n" +
                    "    \"appType\":0\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEmployee")
    @ResponseBody
    public RespInfo addEmployee(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = rep.getUserid();
        String empName = rep.getEmployee_name();
        String empEmail = rep.getEmail();
        String empNo = rep.getEmpNo();
        String empPhone = rep.getPhone().trim();
        String visitType = rep.getVisitType();
        String remark = rep.getRemark();
        String empNickname = rep.getEmpNickname();
        String workbay = rep.getWorkbay();
        String telephone = rep.getTelephone();
        int empType = rep.getEmptype();
        int subaccountId = rep.getSubaccountId();
        String empPosition = rep.getEmpPosition();
        String cardNo = rep.getCardNo();
        String egids = rep.getEgids();
        String startDate = rep.getStartDate();
        String endDate = rep.getEndDate();
        String avatar = rep.getAvatar();
        String vegids = rep.getVegids();
        int ecStatus = rep.getEcStatus();
        String empSex = rep.getEmpSex();
        int appType = rep.getAppType();

        if (userid == 0) {
            return new RespInfo(50, "add employee failed");
        }

        List<Employee> emplist = employeeService.checkEmployeeExists(userid, empPhone);

        if (emplist.size() == 0) {
            Employee emp = new Employee();
            emp.setUserid(userid);
            emp.setEmpName(empName);
            emp.setEmpEmail(empEmail);
            emp.setEmpPhone(empPhone);
            emp.setEmpType(empType);
            emp.setVisitType(visitType);
            emp.setSubaccountId(subaccountId);
            emp.setRemark(remark);
            emp.setWorkbay(workbay);
            emp.setTelephone(telephone);
            emp.setEmpPosition(empPosition);
            emp.setEmpNo(empNo);
            emp.setCardNo(cardNo);
            emp.setEgids(egids);
            emp.setStartDate(startDate);
            emp.setEndDate(endDate);
            emp.setAvatar(avatar);
            emp.setPlateNum(rep.getPlateNum());
            emp.setVegids(vegids);
            emp.setEmpNickname(empNickname);
            emp.setEcStatus(ecStatus);
            emp.setEmpSex(empSex);
            emp.setAppType(appType);


            int empid = employeeService.addEmployee(emp);

            // TODO: 2020/6/5 判断添加免责条款日志

            //2020/4/3 添加员工操作日志
            if (StringUtils.isNotEmpty(rep.getLoginAccount())) {
                String optId = "";
                String optName = "";
                String optRole = "";
                UserInfo userInfo = userService.getUserInfo(rep.getUserid());
                Manager manager = managerService.getManagerByAccount(rep.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    SubAccount subAccount = subAccountService.getSubAccountByEmail(rep.getLoginAccount());
                    if (null != subAccount) {
                        optId = subAccount.getEmail();
                        optName = subAccount.getCompanyName();
                        optRole = OperateLog.ROLE_SUB_COMPANY;
                    } else {

                        optId = userInfo.getEmail();
                        optName = userInfo.getUsername();
                        optRole = OperateLog.ROLE_ADMIN;
                    }
                }
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(rep.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId(String.valueOf(empid));
                log.setObjName(rep.getEmployee_name());
                log.setoTime(new Date());
                log.setOptEvent("增加");
                log.setOptClient("0");
                log.setOptModule("1");
                if (StringUtils.isNotEmpty(userInfo.getEscapeClause())) {
                    log.setOptDesc("成功,添加员工: " + rep.getEmployee_name() + ",同意免责条款");
                } else {
                    log.setOptDesc("成功,添加员工: " + rep.getEmployee_name());
                }
                operateLogService.addLog(log);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "employee_update");
            map.put("company_id", userid);
            map.put("employee_id", emp.getEmpid());
            map.put("current_time", new Date().getTime());
            messageSender.updateFaceLib(map);

            UserInfo ui = new UserInfo();
            ui.setUserid(userid);
            ui.setRefreshDate(new Date());
            userService.updateRefreshDate(ui);

            SubAccount sa = new SubAccount();
            sa.setId(subaccountId);
            sa.setRefreshDate(new Date());
            subAccountService.updateSARefreshDate(sa);


            List<Employee> list = new ArrayList<Employee>();
            for (int i = 0; i < rep.getDeptIds().size(); i++) {
                Employee e = new Employee();
                e.setUserid(rep.getUserid());
                e.setDeptid(rep.getDeptIds().get(i));
                e.setEmpid(emp.getEmpid());
                list.add(e);
            }

            if (list.size() > 0) {
                departmentService.addDeptEmpRelation(list);
            }

            return new RespInfo(0, "success", emp);
        } else {
            return new RespInfo(ErrorEnum.E_024.getCode(), ErrorEnum.E_024.getMsg(), emplist);
        }
    }


    @ApiOperation(value = "/batchDelEmployee 批量删除员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"empids\":[\n" +
                    "        1411\n" +
                    "    ],\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/batchDelEmployee")
    @ResponseBody
    public RespInfo batchDelEmployee(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {
        try {
            List<ObjectError> allErrors = result.getAllErrors();
            if (!allErrors.isEmpty()) {
                throw new HttpMessageNotReadableException(allErrors.toString());
            }
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole()))
                    ||authToken.getUserid() != rep.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

            List<Integer> empids = rep.getEmpids();
            List<String> empAvatars = new ArrayList<String>();
            int userid = rep.getUserid();

            int subaccountId = rep.getSubaccountId();

            List<Employee> emplist = employeeService.getEmployeesByEmpids(empids);
            Map<String, Object> map = new HashMap<String, Object>();
            HashOperations hashOperations = redisTemplate.opsForHash();

            for (int i = 0; i < emplist.size(); i++) {
                Employee emp = emplist.get(i);
                map.put("key", "employee_delete");
                map.put("employee_id", emp.getEmpid());
                map.put("company_id", userid);
                messageSender.updateFaceLib(map);
                personInfoService.delInvitePersonByOpenid(emp.getOpenid());
                personInfoService.delInvitePerson(emp.getEmpPhone());
                personInfoService.delVisitPerson(emp.getEmpPhone());
                if (StringUtils.isNotBlank(emp.getAvatar())) {
                    empAvatars.add(emp.getAvatar());
                }

                if (hashOperations.hasKey(emp.getEmpPhone(), "token")) {
                    if (hashOperations.hasKey(hashOperations.get(emp.getEmpPhone(), "token"), "id")) {
                        hashOperations.delete(hashOperations.get(emp.getEmpPhone(), "token"), "id");
                    }
                    hashOperations.delete(emp.getEmpPhone(), "token");
                }



            }
            //2020/4/3 添加批量删除员工信息操作记录
            String optId = "";
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(rep.getLoginAccount())) {
                UserInfo userInfo = userService.getUserInfo(rep.getUserid());
                Manager manager = managerService.getManagerByAccount(rep.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    SubAccount subAccount = subAccountService.getSubAccountByEmail(rep.getLoginAccount());
                    if (null != subAccount) {
                        optId = subAccount.getEmail();
                        optName = subAccount.getCompanyName();
                        optRole = OperateLog.ROLE_SUB_COMPANY;
                    } else {

                        optId = userInfo.getEmail();
                        optName = userInfo.getUsername();
                        optRole = OperateLog.ROLE_ADMIN;
                    }
                }

            }

            try {
                List<String> empNames = emplist.stream().map(Employee::getEmpName).collect(Collectors.toList());
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(rep.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName(String.join(",", empNames));
                log.setoTime(new Date());
                log.setOptEvent("删除");
                log.setOptClient(OperateLog.CLIENT_PC);
                log.setOptModule(OperateLog.MODULE_EMPLOYEE);
                log.setOptDesc("成功: " + empNames.toString());
                operateLogService.addLog(log);
            }catch (Exception e){

            }


            employeeService.batchDelEmployee(empids);
            departmentService.batchDelRelationByEmp(empids);

            // TODO: 2020/4/24 删除图片文件线程
            if(empAvatars.size()>0) {
                new Thread(new DelFileThread(empAvatars, personInfoService)).start();
            }

            UserInfo ui = new UserInfo();
            ui.setUserid(userid);
            ui.setRefreshDate(new Date());
            userService.updateRefreshDate(ui);

            SubAccount sa = new SubAccount();
            sa.setId(subaccountId);
            sa.setRefreshDate(new Date());
            subAccountService.updateSARefreshDate(sa);
            return new RespInfo(0, "success");
        } finally {
        }
    }


    @ApiOperation(value = "/addDepartment 增加部门", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"deptName\":\"666\",\n" +
                    "    \"parentId\":154357112,\n" +
                    "    \"deptManagerEmpid\":\"\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addDepartment")
    @ResponseBody
    public RespInfo addDepartment(
            @ApiParam(value = "Department 部门Bean", required = true) @RequestBody Department dept,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != dept.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Department olddept = departmentService.getDepartmentByDeptName(dept);
        if (null != olddept) {
            return new RespInfo(123, "deptName already exist");
        }

        departmentService.addDepartment(dept);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addDepartment 增加部门", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"deptid\":328582057,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delDepartment")
    @ResponseBody
    public RespInfo delDepartment(@RequestBody Department rdept,
                                  HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rdept.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Department dept = departmentService.getDepartment(rdept.getDeptid(), rdept.getUserid());
        int a = departmentService.delDepartment(dept.getDeptid(), dept.getUserid());
        if (a > 0) {
            departmentService.delRelationByDept(dept.getDeptid(), dept.getUserid());
        }
        delChildDeptList(dept, departmentService);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateDepartment 修改部门", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"deptName\":\"研发部\",\n" +
                    "    \"parentId\":\"\",\n" +
                    "    \"deptid\":34315162,\n" +
                    "    \"deptManagerEmpid\":\"1402\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateDepartment")
    @ResponseBody
    public RespInfo updateDepartment(
            @ApiParam(value = "Department 部门Bean", required = true) @RequestBody Department dept,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != dept.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Department dm = departmentService.getDepartment(dept.getDeptid(), dept.getUserid());
        Department newdept = departmentService.getDepartmentByDeptName(dept);
        if (null != newdept && newdept.getDeptid() != dm.getDeptid() && newdept.getParentId() == dm.getParentId()) {
            return new RespInfo(123, "deptName already exist");
        }

        departmentService.updateDepartment(dept);

        return new RespInfo(0, "success");
    }

    /**
     * 添加申请临时卡记录
     *
     * @param ic 临时卡记录
     * @return
     */
    @ApiOperation(value = "/addInterimCard 添加临时卡", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"empid\":\"empid\",\n" +
                    "    \"userid\":\"userid\",\n" +
                    "    \"name\":姓名,\n" +
                    "    \"deptName\":\"1402\",\n" +
                    "    \"empNo\":工号\n" +
                    "    \"cardNo\":卡号\n" +
                    "    \"phone\":手机号\n" +
                    "    \"email\":邮箱\n" +
                    "    \"office\":办公地方\n" +
                    "    \"submitTime\":提交事件\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addInterimCard")
    @ResponseBody
    public RespInfo addInterimCard(
            @ApiParam(value = "InterimCard 临时卡Bean", required = true) @RequestBody InterimCard ic,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ic.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())){
            //允许
        }else if(AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            if(!String.valueOf(ic.getEmpid()).equals(authToken.getLoginAccountId())){
                //请求的不是自己的数据
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else {
            //其他账号类型不允许操作
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        //校验员工empid
        Employee employee = employeeService.getEmployee(ic.getEmpid());
        if (null == employee) {
            SysLog.error("申请临时卡失败，"+ErrorEnum.E_055.getMsg()+" empid:"+ic.getEmpid());
            return new RespInfo(ErrorEnum.E_055.getCode(), ErrorEnum.E_055.getMsg());
        }
        //设置初始值
        ic.setIcStatus(0);//当用户申请临时卡时，初始化状态为0,即0申请中 1未归还 2已归还
        int i = interimCardService.addInterimCard(ic);
        if (i > 0) {
            //日志
            String optId = "";
            String optName = "";
            String optRole = "";

            UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
            Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else if(AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
                optId = employee.getEmpid()+"";
                optName = employee.getEmpName();
                optRole = OperateLog.ROLE_ADMIN;
            }else{
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = OperateLog.ROLE_ADMIN;
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(authToken.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId(employee.getEmpid()+"");
            log.setObjName(employee.getEmpName());
            log.setoTime(new Date());
            log.setOptEvent("申请临时卡");
            log.setOptClient("0");
            log.setOptModule(OperateLog.MODULE_EMPLOYEE);
            log.setOptDesc("成功");
            operateLogService.addLog(log);
            return new RespInfo(0, "success");
        }
        return new RespInfo(21, "fail to upload");
    }

    @ApiOperation(value = "/updateInterimCard 修改更新临时卡记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateInterimCard")
    @ResponseBody
    public RespInfo updateInterimCard(@RequestBody InterimCard ic,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ic.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())){
            //允许
        }else {
            //其他账号类型不允许操作
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        ic.setIcStatus(1);
        interimCardService.updateInterimCard(ic);
        //日志
        String optId = "";
        String optName = "";
        String optRole = "";
        UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
        Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
        if (null != manager) {
            optId = manager.getAccount();
            optName = manager.getSname();
            optRole = String.valueOf(manager.getsType());
        } else {
            optId = userInfo.getEmail();
            optName = userInfo.getUsername();
            optRole = OperateLog.ROLE_ADMIN;
        }
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(authToken.getUserid());
        log.setOptId(optId);
        log.setOptName(optName);
        log.setOptRole(optRole);
        log.setIpAddr(ipAddr);
        log.setObjId(ic.getEmpid()+"");
        log.setObjName(ic.getName());
        log.setoTime(new Date());
        log.setOptEvent("审批临时卡");
        log.setOptClient("0");
        log.setOptModule(OperateLog.MODULE_EMPLOYEE);
        log.setOptDesc("status:"+ic.getIcStatus()+" NO:"+ic.getCardNo());
        operateLogService.addLog(log);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/refuseInterimCard ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/refuseInterimCard")
    @ResponseBody
    public RespInfo refuseInterimCard(@RequestBody InterimCard ic,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ic.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())){
            //允许
        }else {
            //其他账号类型不允许操作
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        ic.setIcStatus(3);
        interimCardService.updateInterimCard(ic);

        //日志
        String optId = "";
        String optName = "";
        String optRole = "";
        UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
        Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
        if (null != manager) {
            optId = manager.getAccount();
            optName = manager.getSname();
            optRole = String.valueOf(manager.getsType());
        } else {
            optId = userInfo.getEmail();
            optName = userInfo.getUsername();
            optRole = OperateLog.ROLE_ADMIN;
        }
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(authToken.getUserid());
        log.setOptId(optId);
        log.setOptName(optName);
        log.setOptRole(optRole);
        log.setIpAddr(ipAddr);
        log.setObjId(ic.getEmpid()+"");
        log.setObjName(ic.getName());
        log.setoTime(new Date());
        log.setOptEvent("更新临时卡");
        log.setOptClient("0");
        log.setOptModule(OperateLog.MODULE_EMPLOYEE);
        log.setOptDesc("status:"+ic.getIcStatus()+" NO:"+ic.getCardNo());
        operateLogService.addLog(log);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/returnInterimCard 归还临时卡", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/returnInterimCard")
    @ResponseBody
    public RespInfo returnInterimCard(@RequestBody InterimCard ic,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ic.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())){
            //允许
        }else {
            //其他账号类型不允许操作
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        interimCardService.returnInterimCard(ic);

        //日志
        String optId = "";
        String optName = "";
        String optRole = "";
        UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
        Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
        if (null != manager) {
            optId = manager.getAccount();
            optName = manager.getSname();
            optRole = String.valueOf(manager.getsType());
        } else {
            optId = userInfo.getEmail();
            optName = userInfo.getUsername();
            optRole = OperateLog.ROLE_ADMIN;
        }
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(authToken.getUserid());
        log.setOptId(optId);
        log.setOptName(optName);
        log.setOptRole(optRole);
        log.setIpAddr(ipAddr);
        log.setObjId(ic.getEmpid()+"");
        log.setObjName(ic.getName());
        log.setoTime(new Date());
        log.setOptEvent("更新临时卡");
        log.setOptClient("0");
        log.setOptModule(OperateLog.MODULE_EMPLOYEE);
        log.setOptDesc("status:"+ic.getIcStatus()+" NO:"+ic.getCardNo());
        operateLogService.addLog(log);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/ExportInterimCardList 导出临时卡", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportInterimCardList")
    public void ExportInterimCardList(HttpServletRequest req, HttpServletResponse response) {
        try {
            String ctoken = req.getParameter("token");
            
            if(StringUtils.isNotBlank(ctoken)) {
            	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
            }
            
            if(StringUtils.isBlank(ctoken)){
            	System.out.println("token is null");
            	return;
            }
            
            int userid = Integer.parseInt(req.getParameter("userid"));
            String startDate = req.getParameter("startDate");
            String endDate = req.getParameter("endDate");
            String cardNo = req.getParameter("cardNo");
            String empNo = req.getParameter("empNo");
            String name = req.getParameter("name");
            String icStatus = req.getParameter("icList");


            ReqInterimCard rc = new ReqInterimCard();
            rc.setUserid(userid);
            rc.setStartDate(startDate);
            rc.setEndDate(endDate);
            rc.setCardNo(cardNo);
            rc.setEmpNo(empNo);
            rc.setName(name);

            //检查权限
            if(tokenServer.checkUserAuthorityForExport(managerService,redisTemplate,response, ctoken, userid, "-1")) return;

            List<Integer> icStatusList = new ArrayList<Integer>();
            for (int i = 0; i < icStatus.length(); i++) {
                icStatusList.add(Integer.parseInt(icStatus.substring(i, i + 1)));
            }
            rc.setIcList(icStatusList);

            List<InterimCard> icList = interimCardService.getInterimCard(rc);

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "临时卡记录_" + startDate + "-" + endDate;
            ExcelModel excel = new ExcelModel();
            excel.setSheetName(filename);
            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            ExcelModel em = download.createDownLoadExcelIc(icList, excel);
            download.downLoad(filename + ".xls", em, req, response);

            //添加日志
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_EMPLOYEE, icList.size(), "导出"+filename+":"+rc.toString());
        } catch (Exception e) {
            e.printStackTrace();
            SysLog.error("导出临时卡",e);
        }
    }


    @ApiOperation(value = "/webActivateAccount 账号激活(web端)", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":\"123456789\",\n" +
                    "    \"verifyCode\":\"verifyCode\",\n" +
                    "    \"empPwd\":\"empPwd\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/webActivateAccount")
    @ResponseBody
    public RespInfo webActivateAccount(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        String phone = reqemp.getPhone();
        String smscode = reqemp.getVerifyCode();

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(phone + "-_-" + smscode, "smscode")) {
            Long expireDate = (long) hashOperations.get(phone + "-_-" + smscode, "smscode");
            Date currentDate = new Date();
            if (expireDate < currentDate.getTime()) {
                hashOperations.delete(phone + "-_-" + smscode, "smscode");
                return new RespInfo(144, "smscode expired");
            }
        } else {
            return new RespInfo(7, "invalid verify code");
        }

        hashOperations.delete(phone + "-_-" + smscode, "smscode");

        List<Employee> emplist = employeeService.getEmployeePassword(reqemp.getPhone());
        if (emplist.size() == 0) {
            return new RespInfo(1, "invalid user");
        }
        Employee oldemp = emplist.get(0);

        Person p = personInfoService.getVisitPersonByPhone(oldemp.getEmpPhone());
        if (null == p) {
            p = new Person();
            p.setPmobile(oldemp.getEmpPhone());
            p.setPname(oldemp.getEmpName());
            p.setPassword(MD5.crypt(reqemp.getEmpPwd()));
            personInfoService.addVisitPerson(p);

            oldemp.setEmpPwd(MD5.crypt(reqemp.getEmpPwd()));
            employeeService.updateEmpPwd(oldemp);
        } else {
            if (null != p.getPassword() && !"".equals(p.getPassword())) {
                return new RespInfo(613, "user already activate");
            } else {
                p.setPassword(MD5.crypt(reqemp.getEmpPwd()));
                personInfoService.updateVisitPersonPwd(p);
                oldemp.setEmpPwd(MD5.crypt(reqemp.getEmpPwd()));
                employeeService.updateEmpPwd(oldemp);
            }
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/GetProxyListByOpenid 员工获取自己的被代理人", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetProxyListByOpenid")
    @ResponseBody
    public RespInfo GetProxyListByOpenid(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp req,
            HttpServletRequest request, BindingResult result) {
        try {
            List<ObjectError> allErrors = result.getAllErrors();
            if (!allErrors.isEmpty()) {
                throw new HttpMessageNotReadableException(allErrors.toString());
            }
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            String strEmpId = authToken.getLoginAccountId();
            int empId = 0;
            try {
                empId = Integer.parseInt(strEmpId);
            }catch (Exception e){
                throw new NullPointerException("invalid token");
            }

            Employee emp = employeeService.getEmployee(empId);
            if (null == emp ) {
                return new RespInfo(1, "invalid employee");
            }

            EmpVisitProxy evp = new EmpVisitProxy();
            evp.setProxyId(emp.getEmpid());
            evp.setUserid(emp.getUserid());
            List<EmpVisitProxy> vplist = visitProxyService.getProxyInfoByPId(evp);
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
            List<RespVisitor> rvlist = new ArrayList<RespVisitor>();
            RespVisitor entity = new RespVisitor();
            for (int i = 0; i < vplist.size(); i++) {
                emp = employeeService.getEmployee(vplist.get(i).getEmpid());
                if (vplist.get(i).getProxyStatus() == 1) {
                    entity.setEmpPhone(emp.getEmpPhone());
                    entity.setStartDate(time.format(vplist.get(i).getStartDate()));
                    entity.setEndDate(time.format(vplist.get(i).getEndDate()));
                    List<RespVisitor> list = visitorService.getVistorProxyListByEmpPhone(entity);
                    rvlist.addAll(list);
                }
            }

            if (rvlist.size() > 0) {
                Collections.sort(rvlist, new Comparator<RespVisitor>() {
                    @Override
                    public int compare(RespVisitor o1, RespVisitor o2) {
                        if (o1.getAppointmentDate().before(o2.getAppointmentDate())) {
                            return 1;
                        } else {
                            return -1;
                        }

                    }
                });

                if (rvlist.size() > 50) {
                    rvlist = rvlist.subList(0, 50);
                }
            }

            return new RespInfo(0, "success", rvlist);
        } finally {
        }
    }

    @ApiOperation(value = "/updateDeptEmpRelation 更新部门员工关系", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"deptIds\":[\n" +
                    "        327548944\n" +
                    "    ],\n" +
                    "    \"empids\":[\n" +
                    "        1411\n" +
                    "    ],\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateDeptEmpRelation")
    @ResponseBody
    public RespInfo updateDeptEmpRelation(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Integer> empids = rep.getEmpids();
        List<Integer> deptids = rep.getDeptIds();
        List<Employee> emplist = new ArrayList<Employee>();

        departmentService.batchDelRelationByEmp(empids);
        if (rep.getUserid() == 0) {
            return new RespInfo(1, "invalid user");
        }
        if (deptids.size() != 0 && deptids.get(0) != 0) {
            for (int i = 0; i < deptids.size(); i++) {
                if (deptids.get(i) == 0) {
                    continue;
                }
                for (int s = 0; s < empids.size(); s++) {
                    if (empids.get(s) == 0) {
                        continue;
                    }
                    Employee e = new Employee();
                    e.setUserid(rep.getUserid());
                    e.setEmpid(empids.get(s));
                    e.setDeptid(deptids.get(i));
                    emplist.add(e);
                }
            }
            departmentService.addDeptEmpRelation(emplist);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getAbnormalEmployee 员工状态异常统计", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"exType\":1,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":-10\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAbnormalEmployee")
    @ResponseBody
    public RespInfo getAbnormalEmployee(
            @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rep.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page rpage = new Page(rep.getStartIndex() / rep.getRequestedCount() + 1, rep.getRequestedCount(), 0);
        rep.setPage(rpage);
        List<Employee> emplist = employeeService.getEmpByFaceResult(rep);
        rpage.setList(emplist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/ExportAbnormalEmployee 导出员工状态异常统计表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.GET, value = "/ExportAbnormalEmployee")
    public void ExportAbnormalEmployee(HttpServletRequest req, HttpServletResponse response) {
        String ctoken = req.getParameter("token");
        
        if(StringUtils.isNotBlank(ctoken)) {
        	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }
        
        if(StringUtils.isBlank(ctoken)){
        	System.out.println("token is null");
        	return;
        }
        
        int userid = Integer.parseInt(req.getParameter("userid"));
        try {
            String exType = req.getParameter("exType");
            //检查权限
            if(tokenServer.checkUserAuthorityForExport(managerService,redisTemplate,response, ctoken, userid, "-1")) return;

            RequestEmp remp = new RequestEmp();
            remp.setExType(Integer.parseInt(exType));
            remp.setUserid(userid);

            List<Employee> empList = employeeService.getEmpByFaceResult(remp);

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "员工列表";
            ExcelModel excel = new ExcelModel();
            excel.setSheetName(filename);
            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            ExcelModel em = download.createDownLoadEmpExcel(empList, excel);
            download.downLoad(filename + ".xls", em, req, response);

            //添加日志
            SysLog.info("导出"+filename+":"+remp.toString());
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_EMPLOYEE, empList.size(), "导出"+filename+":"+remp.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "/updateEmpPwd 修改员工密码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":123456789,\n" +
                    "    \"oldEmpPwd\":老密码,\n" +
                    "    \"empPwd\":密码,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateEmpPwd")
    @ResponseBody
    public RespInfo updateEmpPwd(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != reqemp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int failcount = 0;
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey("fail_login" + reqemp.getPhone(), "locktime")) {
            Long locktime = (long) hashOperations.get("fail_login" + reqemp.getPhone(), "locktime");
            if (new Date().getTime() - locktime > Constant.LOCK_TIME) {
                hashOperations.delete("fail_login" + reqemp.getPhone(), "locktime");
                hashOperations.delete("fail_login" + reqemp.getPhone(), "failcount");
            } else {
                return new RespInfo(301, "please retry  after 5 minutes");
            }
        }

        if (hashOperations.hasKey("fail_login" + reqemp.getPhone(), "failcount")) {
            failcount = (int) hashOperations.get("fail_login" + reqemp.getPhone(), "failcount");
        }

        if (failcount > 3) {
            hashOperations.put("fail_login" + reqemp.getPhone(), "locktime", new Date().getTime());
            return new RespInfo(301, "please retry  after 5 minutes");
        }

        List<Employee> emplist = employeeService.getEmployeePassword(reqemp.getPhone());
        String mobile = "";
        if (emplist.size() != 0) {
            Employee oldemp = emplist.get(0);
            Person p = personInfoService.getVisitPersonByPhone(emplist.get(0).getEmpPhone());
            if (null != p && null != p.getPassword() && !"".equals(p.getPassword())) {
                if (p.getPassword().compareTo(MD5.crypt(reqemp.getOldEmpPwd())) != 0) {
                    return new RespInfo(2, "invalid password");
                } else {
                    p.setPassword(MD5.crypt(reqemp.getEmpPwd()));
                    personInfoService.updateVisitPersonPwd(p);
//                    oldemp.setEmpPwd(MD5.crypt(reqemp.getEmpPwd()));
//                    employeeService.updateEmpPwd(oldemp);
                    if (hashOperations.hasKey("fail_login" + reqemp.getPhone(), "failcount")) {
                        hashOperations.delete("fail_login" + reqemp.getPhone(), "failcount");
                    }
                    mobile = p.getPmobile();
                }
            }
        }

        if (StringUtils.isBlank(mobile)) {
            return new RespInfo(1, "invalid user");
        }


        if (null != hashOperations.get(mobile, "token")) {
            String oldtoken = (String) hashOperations.get(mobile, "token");
            if (null != hashOperations.get(oldtoken, "id")) {
                hashOperations.delete(oldtoken, "id");
            }
        }
        if (null != hashOperations.get(mobile + "_empPwd", "empUser")) {
            hashOperations.delete(mobile + "_empPwd", "empUser");
            hashOperations.put(emplist.get(0).getEmpPhone() + "_empPwd", "empUser", System.currentTimeMillis());
        }

        return new RespInfo(0, "success");
    }

    public void delChildDeptList(Department dept, DepartmentService departmentService) {
        List<Department> childDeptList = departmentService.getChildDeptList(dept.getDeptid(), dept.getUserid());
        for (int i = 0; i < childDeptList.size(); i++) {
            delChildDeptList(childDeptList.get(i), departmentService);
            int a = departmentService.delDepartment(childDeptList.get(i).getDeptid(), childDeptList.get(i).getUserid());
            if (a > 0) {
                departmentService.delRelationByDept(childDeptList.get(i).getDeptid(), childDeptList.get(i).getUserid());
            }
        }

    }

    /**
     * 上传员工人脸
     *
     * @param rep
     * @return
     */
    @ApiOperation(value = "/uploadEmpFace 上传员工人脸", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/uploadEmpFace")
    @ResponseBody
    public RespInfo uploadEmpFace(@RequestBody RequestEmp rep) {
        String code = this.traverseFolderForEmp("/opt/facePicture/emp");

        if (!"1".equals(code)) {
            return new RespInfo(2100, "file folder is not found");
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/ImportEmpsByWechart 同步企业微信员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":123456789,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/ImportEmpsByWechart")
    @ResponseBody
    public RespInfo ImportEmpsByWechart(@RequestBody ReqUserInfo reqUserInfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqUserInfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userInfo = userService.getUserInfo(reqUserInfo.getUserid());
        RespInfo respInfo = employeeService.ImportEmpsByWechart(userInfo);

        if(respInfo.getStatus()!=0){
            return respInfo;
        }
        //添加日志
        String optId="";
        String optName="";
        String optRole="";
        if (StringUtils.isNotEmpty(authToken.getLoginAccountId())) {
            Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            }else {
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
        }
        String ipAddr = request.getHeader("X-Forwarded-For");
        OperateLog log = new OperateLog();
        log.setUserid(reqUserInfo.getUserid())
                .setOptRole(optRole)
                .setOptId(optId)
                .setOptName(optName)
                .setIpAddr(ipAddr)
                .setObjId("")
                .setObjName("员工")
                .setoTime(new Date())
                .setOptEvent("企业微信员工同步")
                .setOptModule(OperateLog.MODULE_EMPLOYEE)
                .setOptDesc("同步员工"+respInfo.getResult()+"人");
        operateLogService.addLog(log);

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/getEmployeeFromDD 同步钉钉员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":123456789,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeFromDD")
    @ResponseBody
    public RespInfo getEmployeeFromDD(@RequestBody RequestEmp rep, HttpServletRequest request) {
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
            ||authToken.getUserid() != rep.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

            int userid = rep.getUserid();
            UserInfo userinfo = userService.getUserInfo(userid);
            RespInfo respInfo = employeeService.ImportEmpsByDD(userinfo);
            userinfo.setRefreshCount(0);
            if(respInfo.getStatus()!=0){
                return respInfo;
            }

            //添加日志
            String optId="";
            String optName="";
            String optRole="";
            if (StringUtils.isNotEmpty(authToken.getLoginAccountId())) {
                Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                }else {
                    optId = userinfo.getEmail();
                    optName = userinfo.getUsername();
                    optRole = "0";
                }
            }
            String ipAddr = request.getHeader("X-Forwarded-For");
            OperateLog log = new OperateLog();
            log.setUserid(userid)
                    .setOptRole(optRole)
                    .setOptId(optId)
                    .setOptName(optName)
                    .setIpAddr(ipAddr)
                    .setObjId("")
                    .setObjName("员工")
                    .setOptEvent("丁丁员工同步")
                    .setOptModule(OperateLog.MODULE_EMPLOYEE)
                    .setOptDesc("同步员工"+respInfo.getResult()+"人");
            operateLogService.addLog(log);

            return new RespInfo(0, "success");
        } finally {
        }
    }

    @ApiOperation(value = "/ImportEmpsByFeiShu 同步飞书员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":123456789,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/ImportEmpsByFeiShu")
    @ResponseBody
    public RespInfo ImportEmpsByFeiShu(@RequestBody ReqUserInfo reqUserInfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqUserInfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userInfo = userService.getUserInfo(reqUserInfo.getUserid());
        RespInfo respInfo = employeeService.ImportEmpsByFeishu(userInfo);

        if(respInfo.getStatus()!=0){
            return respInfo;
        }

        //添加日志
        String optId="";
        String optName="";
        String optRole="";
        if (StringUtils.isNotEmpty(authToken.getLoginAccountId())) {
            Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            }else {
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
        }
        String ipAddr = request.getHeader("X-Forwarded-For");
        OperateLog log = new OperateLog();
        log.setUserid(reqUserInfo.getUserid())
                .setOptRole(optRole)
                .setOptId(optId)
                .setOptName(optName)
                .setIpAddr(ipAddr)
                .setObjId("")
                .setObjName("员工")
                .setoTime(new Date())
                .setOptEvent("飞书员工同步")
                .setOptModule(OperateLog.MODULE_EMPLOYEE)
                .setOptDesc("同步员工"+respInfo.getResult()+"人");
        operateLogService.addLog(log);

        return new RespInfo(0, "success");


    }


    /**
     * 用户AD域登录
     *
     * @param reqEmp
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/adDomainLogin")
    @ResponseBody
    public RespInfo adDomainLogin(@RequestBody Employee reqEmp, HttpServletRequest request) {
        Employee employee = employeeService.checkEmployeeExistsByRtxAccount(reqEmp.getEmpRtxAccount());
        if (null != employee) {
            String rtxPwd = AESUtil.decode(reqEmp.getEmpPwd(), Constant.AES_KEY);
            int i = ADdomain.checkPw(employee.getEmpRtxAccount(), rtxPwd);
            if (i == 0) {
                return new RespInfo(0, "success", employee);
            } else {
                return new RespInfo(1, "invalid user");
            }
        } else {
            return new RespInfo(1, "invalid user");
        }
    }

    public String traverseFolderForEmp(String path) {
    	InputStream input =null;
        File file = new File(path);
        if (file.exists()) {
            List<Employee> empList = new ArrayList<Employee>();
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
                return "-1";
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolderForEmp(file2.getAbsolutePath());
                    } else {
                        String empPhone = file2.getName();
						//fastdfs
//                      String result = UtilTools.fileToImage(file2,storageClient);
                        //fastdfs
                        
                        //minio
                        String prefix = empPhone.substring(empPhone.lastIndexOf(".") + 1);
                        String fileName=UtilTools.produceId(8)+System.currentTimeMillis()+"."+prefix;
                    	try {
							input = new FileInputStream(file2);
	                	    String result=MinioTools.uploadFile(input,Constant.BUCKET_NAME,"image/png","/facePicture/"+YearMonth.now()+"/"+fileName);
	                	//minio
                	    
	                	    if (result.equals("")) {
	                            continue;
	                        } else {
	                            System.out.println("文件名:" + empPhone.substring(0, empPhone.indexOf(".")));
	                            System.out.println("Url:" + result);
	                            Employee e = new Employee();
	                            e.setEmpPhone(empPhone.substring(0, empPhone.indexOf(".")));
	                            e.setAvatar(result);
	                            empList.add(e);
	                        }

                    	} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}finally {
							if(null!=input) {
								try {
									input.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
                    }
                }
                
                if(null!=input) {
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
                
                if (!empList.isEmpty()) {
                    employeeService.batchUpdateEmpAvatar(empList);
                }

                file.renameTo(new File("/opt/facePicture/emp" + System.currentTimeMillis()));

                return "1";
            }
        } else {
            System.out.println("文件不存在!");
            return "-2";
        }
    }


    @ApiOperation(value = "/customSyncEmps 自定义同步员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":123456789,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/customSyncEmps")
    @ResponseBody
    public RespInfo customSyncEmps(@RequestBody ReqUserInfo reqUserInfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqUserInfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userInfo = userService.getUserInfo(reqUserInfo.getUserid());
        if (null == userInfo) {
            return new RespInfo(1, "invalid user");
        }

        //TODO 在此添加同步代码
        int result = 0;//同步的员工人数

        //添加日志
        String optId="";
        String optName="";
        String optRole="";
        if (StringUtils.isNotEmpty(authToken.getLoginAccountId())) {
            Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            }else {
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
        }
        String ipAddr = request.getHeader("X-Forwarded-For");
        OperateLog log = new OperateLog();
        log.setUserid(reqUserInfo.getUserid())
                .setOptRole(optRole)
                .setOptId(optId)
                .setOptName(optName)
                .setIpAddr(ipAddr)
                .setObjId("")
                .setObjName("员工")
                .setoTime(new Date())
                .setOptEvent("手动员工同步")
                .setOptModule(OperateLog.MODULE_EMPLOYEE)
                .setOptDesc("同步员工"+result+"人");
        operateLogService.addLog(log);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/syncEmpMsg 获取员工同步信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":123456789,\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/syncEmpMsg")
    @ResponseBody
    public RespInfo getSyncEmpMsg(@RequestBody ReqUserInfo reqUserInfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqUserInfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map entries = hashOperations.entries(Constant.EMP_SYNC + reqUserInfo.getUserid());
        return new RespInfo(0, "success", entries);
    }


    @ApiOperation(value = "/uploadEmpFaceResult 上传人脸识别结果", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"empid\":124,\n" +
                    "    \"face\":0,\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/uploadEmpFaceResult")
    @ResponseBody
    public RespInfo uploadEmpFaceResult(@RequestBody Employee rep) {
        int empid = rep.getEmpid();
        int face = rep.getFace();

        Employee emp = new Employee();
        emp.setEmpid(empid);
        emp.setFace(face);
        employeeService.updateEmpFace(emp);

        emp = employeeService.getEmployee(empid);
        if (null != emp && StringUtils.isNotBlank(emp.getOpenid())) {
            Person p = new Person();
            p.setPopenid(emp.getOpenid());
            p.setFace(face);
            personInfoService.updateInviteFace(p);
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getDefaultReception 获取默认接待人", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":用户id（必填）,\n" +
                    "    \"subaccountId\":入驻企业id,\n" +
                    "}"+
                    "返回：empid,empName,userid,subaccountId"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getDefaultReception")
    @ResponseBody
    public RespInfo getDefaultReception(@RequestBody RequestEmp rep, HttpServletRequest request) {
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, rep.getUserid());
        if (respInfo != null) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }

        List<Employee> emplist = employeeService.getDefaultEmpList(rep);
        return new RespInfo(0, "success", emplist);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeById")
    @ResponseBody
    public RespInfo getEmpById(@RequestBody Employee employee, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != employee.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int empid = employee.getEmpid();
        int userid = employee.getUserid();
        Employee emp = employeeService.getEmployee(empid);
        String deptName = "";
        if (null != emp) {
            List<Department> dept = departmentService.getDeptByEmpid(empid, userid);
            if (!dept.isEmpty()) {
                //todo 封装员工部门名称关系
                deptName = departmentService.deptTreeNameByEmpId(dept.get(0));
                emp.setDeptName(deptName);
            }
        }
        return new RespInfo(0, "success", emp);
    }


    @ApiOperation(value = "/resetEmpPwd 重置员工密码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":员工账号,\n" +
                    "    \"empPwd\":密码,\n" +
                    "    \"verifyCode\":短信校验码,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/resetEmpPwd")
    @ResponseBody
    public RespInfo resetEmpPwd(@RequestBody RequestEmp reqemp, HttpServletRequest request) {
        String phone = reqemp.getPhone();
        String smscode = reqemp.getVerifyCode();

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(phone + "-_-" + smscode, "smscode")) {
            Long expireDate = (long) hashOperations.get(phone + "-_-" + smscode, "smscode");
            Date currentDate = new Date();
            if (expireDate < currentDate.getTime()) {
                hashOperations.delete(phone + "-_-" + smscode, "smscode");
                return new RespInfo(144, "smscode expired");
            }
        } else {
            return new RespInfo(7, "invalid verify code");
        }

        hashOperations.delete(phone + "-_-" + smscode, "smscode");

        List<Employee> emplist = employeeService.getEmployeePassword(reqemp.getPhone());
        if(emplist.size() == 0){
            return new RespInfo(1, "invalid user");
        }
        String mobile = "";
        Employee oldemp = emplist.get(0);
        oldemp.setEmpPwd(MD5.crypt(reqemp.getEmpPwd()));
        employeeService.updateEmpPwd(oldemp);
        mobile = emplist.get(0).getEmpPhone();
        //删除加锁账号
        if (hashOperations.hasKey("fail_login" + reqemp.getPhone(), "locktime")){
            hashOperations.delete("fail_login" + reqemp.getPhone(), "locktime");
            hashOperations.delete("fail_login" + reqemp.getPhone(), "failcount");
        }


        Person p = personInfoService.getVisitPersonByPhone(reqemp.getPhone());
        if (null != p) {
            p.setPassword(MD5.crypt(reqemp.getEmpPwd()));
            personInfoService.updateVisitPersonPwd(p);
            mobile = p.getPmobile();
        }

        if ("".equals(mobile)) {
            return new RespInfo(1, "invalid user");
        }

        if (null != hashOperations.get(mobile, "token")) {
            String oldtoken = (String) hashOperations.get(mobile, "token");
            if (null != hashOperations.get(oldtoken, "id")) {
                hashOperations.delete(oldtoken, "id");
            }
        }

        //添加操作日志
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(reqemp.getUserid());
        log.setOptId(phone);
        log.setOptName(oldemp.getEmpName());
        log.setOptRole(OperateLog.ROLE_EMPLOYEE);//角色 操作角色  0管理员 1hse 2员工 3前台
        log.setIpAddr(ipAddr);
        log.setObjId(oldemp.getEmpPhone());
        log.setObjName(oldemp.getEmpName());
        log.setoTime(new Date());
        log.setOptEvent("修改密码");//事件，修改、增加、删除、登录
        log.setOptClient("0");//操作端 0 PC  1 移动
        log.setOptModule(OperateLog.MODULE_EMPLOYEE);//操作模块 0登录 1员工 2账号
        log.setOptDesc("成功");
        operateLogService.addLog(log);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getEmployeeByToken 根据Token获取员工信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmployeeByToken")
    @ResponseBody
    public RespInfo getEmployeeByToken(HttpServletRequest request) {

        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            String empid = authToken.getLoginAccountId();
            if (StringUtils.isNotBlank(empid)){
                Employee employee = employeeService.getEmployee(Integer.parseInt(empid));
                return new RespInfo(0, "success", employee);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return new RespInfo(57, "no records");
    }


    @ApiOperation(value = "/importDepartment 导入组织架构", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/importDepartment")
    @ResponseBody
    public RespInfo importDepartment(@Validated @RequestBody ValidList<Department> departmentList,HttpServletRequest request) {

        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
            &&!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
            employeeService.ImportDepartment(userInfo,departmentList);
            return new RespInfo(0, "success");

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return new RespInfo(ErrorEnum.E_2000.getCode(), ErrorEnum.E_2000.getMsg());
    }

    @ApiOperation(value = "/importEmpList 导入员工", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/importEmpList")
    @ResponseBody
    public RespInfo importEmpList(@Validated  @RequestBody ValidList<Employee> employeeList, HttpServletRequest request) {

        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    &&!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            UserInfo userInfo = userService.getUserInfo(authToken.getUserid());
            employeeService.ImportEmpList(userInfo,employeeList);
            return new RespInfo(0, "success");

        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new RespInfo(ErrorEnum.E_2000.getCode(), ErrorEnum.E_2000.getMsg());
    }

    @ApiOperation(value = "/getEmpManagers 获取员工主管", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEmpManagers")
    @ResponseBody
    public RespInfo getEmpManagers(@RequestBody RequestEmp reqemp, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<Employee> managers= new ArrayList<Employee>();
        Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
        if (null == employee) {
            return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
        }
        List<Department> deptByEmpids = departmentService.getDeptByEmpid(employee.getEmpid(), employee.getUserid());
        for(Department department:deptByEmpids){
            //向上递归查找，直到查找到为止
            String deptManagerEmpid = department.getDeptManagerEmpid();
            while (StringUtils.isBlank(deptManagerEmpid)) {
                if(department.getParentId() == 0) {
                    break;
                }
                department = departmentService.getDepartment(department.getParentId(),employee.getUserid());
                if(department == null){
                    break;
                }
                deptManagerEmpid = department.getDeptManagerEmpid();
            }
            if(StringUtils.isNotBlank(deptManagerEmpid)) {
                String[] empids = deptManagerEmpid.split(",");
                for(int i=0;i<empids.length;i++){
                    try {
                        Employee manager = employeeService.getEmployee(Integer.parseInt(empids[i]));
                        if (null != manager) {
                            managers.add(manager);
                        }
                    }catch (Exception e){

                    }
                }

            }
        }


        return new RespInfo(0, "success",getSimpleEmpList(managers));
    }


}
