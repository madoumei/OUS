package com.client.controller;

import com.alibaba.compileflow.engine.ProcessEngine;
import com.alibaba.compileflow.engine.ProcessEngineFactory;
import com.alibaba.compileflow.engine.common.CompileFlowException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.client.bean.*;
import com.client.service.*;
import com.cloopen.rest.sdk.utils.encoder.BASE64Decoder;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.config.qicool.common.persistence.Page;
import com.config.qicool.common.utils.StringUtils;
import com.event.event.NotifyEvent;
import com.event.event.PassEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.*;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.jsonUtils.JsonUtil;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "VisitorController", tags = "API_访客管理", hidden = true)
public class VisitorController {

	private static Logger logger = Logger.getLogger("mylogger2");

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigureService configureService;

    @Autowired
    private ExtendVisitorService extendVisitorService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private PassRuleService passRuleService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ProcessService processService;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private VisitProxyService visitProxyService;

    @Autowired
    private OpendoorService opendoorService;

    @Autowired
    private ResidentVisitorService residentVisitorService;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private EGRelationService eGRelationService;

    @Autowired
    ManagerService mgrService;

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    MessageService messageService;

    @Autowired
    PassService passService;

    @ApiOperation(value = "/GetVisitor 获取访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetVisitor")
    @ResponseBody
    public RespInfo GetVisitor(@RequestBody RequestVisit req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Visitor v = null;
        int userid =authToken.getUserid();
        try {
            String phone = req.getPhone();
            String cardId = req.getCardId();
            String name = req.getName();
//            int userid = req.getUserid();

            if (null != cardId && !"".equals(cardId)) {
                v = visitorService.getVisitorByCardID(cardId, userid);
                return new RespInfo(0, "success", v);
            }

            Visitor vt = new Visitor();
            vt.setUserid(userid);
            vt.setVphone(phone);
            vt.setVname(name);

            v = visitorService.getVisitor(vt);
            if (null != v) {
                Employee emp = employeeService.getEmployee(v.getEmpid());
                if (null != emp) {
                    return new RespInfo(0, "success", v);
                } else {
                    v.setEmpid(0);
                    return new RespInfo(0, "success", v);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/GetVisitorByVid 根据访客id获取访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetVisitorByVid")
    @ResponseBody
    public RespInfo GetVisitorByVid(@RequestBody IvrData req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        Visitor v = null;
        Appointment at;
        Employee emp = new Employee();
        String visitType = req.getVid().substring(0, 1);
        if (visitType.compareToIgnoreCase(IvrData.NORMAL_VISIT_TYPE) == 0) {
            v = visitorService.getVisitorById(Integer.parseInt(req.getVid().substring(1)));
            if(v==null){
                return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
            }

        } else if (visitType.compareToIgnoreCase(IvrData.APPOINTMENT_VISIT_TYPE) == 0) {
            at = appointmentService.getAppointmentbyId(Integer.parseInt(req.getVid().substring(1)));
            if(at == null){
                return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
            }
            emp = employeeService.getEmployee(at.getEmpid());
            if (null == emp) {
                List<Employee> employeebyPhone = employeeService.getEmployeebyPhone(at.getEmpPhone(), null, at.getUserid());
                if(employeebyPhone.size()>0){
                    emp = employeebyPhone.get(0);
                }
            }
            if (null == emp) {
                return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
            }
            v = new Visitor();
            v.setAid(at.getId());
            v.setVname(at.getName());
            v.setVphone(at.getPhone());
            v.setVphoto(at.getPhotoUrl());
            v.setVisitType(at.getVisitType());
            v.setVcompany(at.getVcompany());
            v.setEmpPhone(at.getEmpPhone());
            v.setEmpid(at.getEmpid());
            v.setExtendCol(at.getAppExtendCol());
            v.setVisitdate(at.getVisitDate());
            v.setAppointmentDate(at.getAppointmentDate());
            v.setEmpName(at.getEmpName());
            v.setVemail(at.getVemail());
            v.setSubaccountId(at.getSubaccountId());
            v.setSigninType(1);
            v.setPermission(at.getPermission());
            v.setQrcodeConf(at.getQrcodeConf());
            v.setQrcodeType(at.getQrcodeType());
            v.setLeaveTime(at.getLeaveTime());
            v.setAid(at.getAreaid());
            v.setArea(at.getArea());
            v.setvType(at.getvType());
            v.setVisitReason(at.getVisitReason());
            v.setAppid(at.getId());
            v.setRemark(at.getRemark());
            v.setPlateNum(at.getPlateNum());
            v.setTid(at.getTid());
            v.setCardId(at.getCardId());
            v.setGid(at.getGid() + "");
            v.setStatus(at.getStatus());
            v.setUserid(at.getUserid());
            Visitor v1 = visitorService.getVisitorByAppId(v);
            if (null != v1) {
                v.setVid(v1.getVid());
                v.setSignOutDate(v1.getSignOutDate());
                v.setExtendCol(v1.getExtendCol());
            }

        } else {
            return new RespInfo(0, "success");
        }
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != v.getUserid()) {
//            throw new RuntimeException(ErrorEnum.E_610.getMsg());
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        return new RespInfo(0, "success",v);
    }

    @ApiOperation(value = "/getVisitorBySecId 根据访客SecId获取访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorBySecId")
    @ResponseBody
    public RespInfo getVisitorBySecId(@RequestBody IvrData req) {
        String vid = AESUtil.decode(req.getVid(), Constant.AES_KEY);
        Visitor v = visitorService.getVisitorById(Integer.parseInt(vid));
        if (null != v) {
            AuthToken authToken = new AuthToken();
            authToken.setAccountRole("8");
            authToken.setLoginAccountId(v.getVphone());
            authToken.setUserid(v.getUserid());
            authToken.setDateTime(new Date().getTime());
            String token = tokenServer.getAESEncoderTokenString(authToken);
            HashOperations hashOperations = redisTemplate.opsForHash();
            Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
            hashOperations.put(token, "id", exprieDate.getTime());
            redisTemplate.expire(token, 12, TimeUnit.HOURS);
            v.setToken(token);
        }
        return new RespInfo(0, "success", v);
    }

    @ApiOperation(value = "/getVisitorSignOutByPhone 根据手机号码获取签出访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorSignOutByPhone")
    @ResponseBody
    public RespInfo getVisitorSignOutByPhone(@RequestBody RequestVisit req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = req.getUserid();
        String vphone = req.getPhone();

        Visitor vt = new Visitor();
        vt.setUserid(userid);
        vt.setVphone(vphone);
        vt.setCardId(req.getCardId());
        vt.setVname(req.getName());

        List<RespVisitor> list = visitorService.checkSignOutRecords(vt);

        return new RespInfo(0, "success", list);
    }

    @ApiOperation(value = "/getVisitorAppointmentByPhone 根据手机号获取访客记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorAppointmentByPhone")
    @ResponseBody
    public RespInfo getVisitorAppointmentByPhone(@RequestBody RequestVisit req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = req.getUserid();
        String phone = req.getPhone();
        UserInfo userinfo = userService.getExtendsInfo(userid);
        int permission = userinfo.getPermissionSwitch();
        String email = req.getEmail();
        String gid = req.getGid();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("vphone", phone);
        map.put("userid", userid);
        map.put("gid", gid);
        map.put("vemail", email);
        if (!"1".equals(req.getTag())) {
            map.put("permission", "1");
        } else {
            map.put("permission", "0");
        }

        List<RespVisitor> list = visitorService.getVisitorAppointmentByPhone(map);

        return new RespInfo(0, "success", list);
    }

    @ApiOperation(value = "/getVisitorAppointmentByName 根据姓名获取访客记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"12312312\",\n" +
                    "    \"cardId\":\"身份证号\",\n" +
                    "    \"name\":\"姓名\",\n" +
                    "    \"tag\":\"1\",\n" +
                    "}" +
                    "cardId和name 二选一，或两个都填"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorAppointmentByName")
    @ResponseBody
    public RespInfo getVisitorAppointmentByName(@RequestBody RequestVisit req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = req.getUserid();
        String name = req.getName();
        String  cardId = req.getCardId();
        UserInfo userinfo = userService.getExtendsInfo(userid);
        int permission = userinfo.getPermissionSwitch();
        String gid = req.getGid();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("vname", name);
        map.put("userid", userid);
        map.put("cardId",cardId);
        map.put("gid", gid);
        if (!"1".equals(req.getTag())) {
            map.put("permission", "1");
        } else {
            map.put("permission", "0");
        }

        map.put("ctype", req.getCtype());

        List<RespVisitor> list = visitorService.getVisitorAppointmentByVname(map);

        return new RespInfo(0, "success", list);
    }

    @ApiOperation(value = "/GetExtendVisitor 获取访客拓展字段", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetExtendVisitor")
    @ResponseBody
    public RespInfo GetExtendVisitor(@RequestBody ExtendVisitor extreq,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != extreq.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<ExtendVisitor> ev = new ArrayList<ExtendVisitor>();
        try {
            int userid = extreq.getUserid();
            ev = extendVisitorService.getBaseExtendVisitor(userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RespInfo(0, "success", ev);
    }

    @ApiOperation(value = "/getVisitorStatistics 获取访客统计", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"signinType\":\"signinType\",\n" +
                    "    \"date\":\"signinType\",\n" +
                    "    \"endDate\":\"signinType\",\n" +
                    "    \"gid\":\"gid\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorStatistics")
    @ResponseBody
    public RespInfo getVisitorStatistics(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit rv,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = null;
        String ctoken = request.getHeader("X-COOLVISIT-TOKEN");
        String decode = AESUtil.decode(ctoken, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            authToken = mapperInstance.readValue(decode, AuthToken.class);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }

        if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
    		Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
    		String gids[]=rv.getGid().split(",");
    		String mgids[]=mgr.getGid().split(",");
    	    boolean auth=UtilTools.arrayContain(gids, mgids);
    		if(auth) {
    			return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
    		}
        }

        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
        && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
        && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        ){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        DataStatistics ds = new DataStatistics();
        ds.setUserid(authToken.getUserid());

        if ("0".equals(rv.getSigninType())) {
            ds = visitorService.getSignedCount(rv);
        } else if ("1".equals(rv.getSigninType())) {
            ds = appointmentService.getInviteCount(rv);
        } else if ("2".equals(rv.getSigninType())) {
            ds = visitorService.getAppointmentCount(rv);
        } else {
            ds = visitorService.getRvSignedCount(rv);
        }

        return new RespInfo(0, "success", ds);
    }

    @Deprecated
    @ApiOperation(value = "/addVisitorApponintmnet3 添加预约访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"appointmentDate\":\"appointmentDate\",\n" +
                    "    \"ctype\":\"s=入驻企业\",\n" +
                    "    \"company\":\"company\",\n" +
                    "    \"name\":\"name\",\n" +
                    "    \"phone\":\"phone\",\n" +
                    "    \"cardId\":\"cardId\",\n" +
                    "    \"memberName\":\"memberName\",\n" +
                    "    \"floors\":\"floors\",\n" +
                    "    \"extendCol\":\"extendCol\",\n" +
                    "    \"gid\":\"gid\",\n" +
                    "    \"empid\":\"empid\",\n" +
                    "    \"empName\":\"empName\",\n" +
                    "    \"empPhone\":\"empPhone\",\n" +
                    "    \"visitType\":\"visitType\",\n" +
                    "    \"photoUrl\":\"photoUrl\",\n" +
                    "    \"clientNo\":\"clientNo\",\n" +
                    "    \"peopleCount\":\"peopleCount\",\n" +
                    "    \"remark\":\"remark\",\n" +
                    "    \"vcompany\":\"vcompany\",\n" +
                    "    \"cardId\":\"cardId\",\n" +
                    "    \"plateNum\":\"plateNum\",\n" +
                    "    \"tid\":\"tid\",\n" +
                    "    \"vType\":\"vType\",\n" +
                    "    \"signInOpName\":\"signInOpName\",\n" +
                    "    \"cardNo\":\"cardNo\",\n" +
                    "}" +
                    "header:"+
                    "    \"appCode\":\"appCode\",\n" +
                    "    \"timestamp\":\"timestamp\",\n" +
                    "    \"authid\":\"authid\",\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addVisitorApponintmnet3")
    @ResponseBody
    public RespInfo addVisitorApponintmnet3(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult bindingResult) throws JsonProcessingException {

        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }


        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date appdate = new Date();
        List<Visitor> vlist = new ArrayList<Visitor>();
        SubAccount sa = new SubAccount();
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        JsonNode rootNode = null;
        int permission = 0;

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, req.getUserid());
        if (respInfo != null) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
            return respInfo;
        }
        }


        try {
            appdate = time.parse(req.getAppointmentDate());
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if ("s".equals(req.getCtype())) {
            sa = subAccountService.getSubAccountByCompany(req.getCompany());
            int purviewValue = 4;
            if ((sa.getVaPerm() & purviewValue) == purviewValue) {
                permission = 0;
            } else {
                permission = 1;
            }
        }

        UserInfo userinfo = userService.getUserInfoByUserId(req.getUserid());
        Map<String, String> bmap = new HashMap<String, String>();
        if (userinfo.getBlackListSwitch() == 1) {
            Blacklist bl = new Blacklist();
            bl.setUserid(userinfo.getUserid());
            bl.setPhone(req.getPhone());
            bl.setCredentialNo(req.getCardId());
            List<Blacklist> blList = blacklistService.checkBlacklist(bl);
            String sid = String.valueOf(sa.getId());
            if (blList.size() > 0 && StringUtils.isNotBlank(blList.get(0).getSids())) {
                String sids[] = blList.get(0).getSids().split(",");
                boolean b = false;
                for (int t = 0; t < sids.length; t++) {
                    if (sid.equals(sids[t])) {
                        b = true;
                    }
                }

                if (!b || sids.length > 1) {
                    bmap.put(req.getPhone(), blList.get(0).getSname());
                    if (!b) {
                        blList.clear();
                    }
                }
            }


            if (StringUtils.isNotBlank(req.getMemberName())) {
                try {
                    rootNode = mapper.readValue(req.getMemberName(), JsonNode.class);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Iterator<JsonNode> it = rootNode.iterator();
                Blacklist bli = new Blacklist();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    bli.setUserid(userinfo.getUserid());
                    bli.setPhone(jn.path("mobile").asText());
                    bli.setCredentialNo(jn.path("cardId").asText());
                    List<Blacklist> blist = blacklistService.checkBlacklist(bli);
                    if (blist.size() > 0 && org.apache.commons.lang3.StringUtils.isNotBlank(blist.get(0).getSids())) {
                        String sids[] = blist.get(0).getSids().split(",");
                        boolean b = false;
                        for (int t = 0; t < sids.length; t++) {
                            if (sid.equals(sids[t])) {
                                b = true;
                            }
                        }

                        if (!b || sids.length > 1) {
                            bmap.put(jn.path("mobile").asText(), blist.get(0).getSname());
                        }

                        if (b) {
                            blList.add(blist.get(0));
                        }
                    } else if (blist.size() > 0) {
                        blList.add(blist.get(0));
                    }
                }

            }

            if (blList.size() > 0) {
                return new RespInfo(66, "user was added to the blacklist", blList);
            }

        }

        Visitor vt = new Visitor();
        vt.setUserid(userinfo.getUserid());
        vt.setVphone(req.getPhone());
        vt.setAppointmentDate(appdate);
        if (bmap.containsKey(req.getPhone())) {
            vt.setbCompany(bmap.get(req.getPhone()));
        }


        //获取楼层信息
        String floors = "";
        List<EquipmentGroup> equipmentGroups = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getFloors())) {
            List<String> extendCol = req.getExtendCol();
            StringBuffer sb = new StringBuffer();
            for (String field : extendCol) {
                String[] split = field.split("=");
                if (split[0].equals("access") && org.apache.commons.lang3.StringUtils.isNotEmpty(split[1])) {
                    String egids = split[1];
                    equipmentGroups = equipmentGroupService.getEquipmentGroupByEgidArray(egids.split(","));
                    if (equipmentGroups.size() > 0) {
                        for (int i = 0; i < equipmentGroups.size(); i++) {
                            String egname = equipmentGroups.get(i).getEgname();
                            if (i < equipmentGroups.size() - 1) {
                                sb.append(egname + ",");
                            } else {
                                sb.append(egname);
                            }
                        }
                        floors = sb.toString();
                    }
                }
            }
        } else {
            floors = req.getFloors();
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(floors)) {
            StringBuffer sb = new StringBuffer();
            String[] floorArr = floors.split(",");
            String f = "";
            //截取字母F或M之前的数据统一进行处理 T1-15/33/39/40/42/43/44F&106
            for (int i = 0; i < floorArr.length; i++) {
                if (floorArr[i].contains("F")) {
                    floorArr[i] = floorArr[i].split("F")[0];
                }
                if (floorArr[i].contains("M")) {
                    floorArr[i] = floorArr[i].split("M")[0];
                }
                if (i < floorArr.length - 1) {
                    sb.append(floorArr[i] + ",");
                } else {
                    sb.append(floorArr[i]);
                }
            }
            floors = sb.toString();
            vt.setFloors(floors);
        }

        // TODO: 2020/7/2 判断是否周末、节假日、梯控、调休日时间拜访
        String weekendPass = "";
        String holidayPass = "";
        String scPass = "";
        String daysOffPass = "";
        if (StringUtils.isNotEmpty(floors)) {
            Boolean w = false;
            Boolean h = false;
            Boolean d = false;
            Date date = new Date();

            String[] gids = req.getGid().split(",");
            for (String gid : gids) {
                if (!w) {
                    w = passRuleService.isWeekendPass(date, Integer.parseInt(gid), userinfo.getUserid());
                }
                if (!h) {
                    h = passRuleService.isHolidayPass(date, Integer.parseInt(gid), userinfo.getUserid());
                }
                String[] f = floors.split(",");
                int status = passRuleService.getSendCardStatus(userinfo.getUserid(), appdate, f);
                scPass = status == 1 ? "是" : "否";
                if (!d) {
                    d = passRuleService.isDaysOffTranslation(date, Integer.parseInt(gid), Arrays.asList(f), userinfo.getUserid());
                }
            }
            weekendPass = w ? "是" : "否";
            holidayPass = h ? "是" : "否";
            daysOffPass = d ? "是" : "否";
            vt.setIsWeekendVisitor(weekendPass);
            vt.setIsHolidayVisitor(holidayPass);
            vt.setIsSCTimeVisitor(scPass);
            vt.setIsDaysOffVisitor(daysOffPass);
        }

        //限制每个被访人的访问量，考虑到随访人的情况，限制为15条每天
        List<RespVisitor> listrv = visitorService.getVisitorAppointmentList(vt);
        if (listrv.size() > 15) {
            return new RespInfo(ErrorEnum.E_100.getCode(), ErrorEnum.E_100.getMsg());
        }

            Person visitor = personInfoService.getVisitPersonByPhone(vt.getVphone());
            String name = req.getName();
            String empName = req.getEmpName();
            String empPhone = req.getEmpPhone();
            String visitType = req.getVisitType();

            String company = "";
            vt.setVname(name);
            vt.setVemail(req.getEmail());
            vt.setEmpName(empName);
            vt.setEmpPhone(empPhone);
            vt.setAppointmentDate(appdate);
            vt.setVisitType(visitType);
            vt.setSigninType(2);
            vt.setVphoto(req.getPhotoUrl());
            vt.setClientNo(req.getClientNo());
            vt.setQrcodeConf(1);
            req.setSubaccountId(sa.getId());
            VisitorChart vc = visitorService.getVisitSaCountByVphone(req);
            vt.setVisitorCount(Integer.parseInt(vc.getCount()));
            vt.setPeopleCount(req.getPeopleCount());
            if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getMemberName())) {
                try {
                    rootNode = mapper.readValue(req.getMemberName(), JsonNode.class);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Iterator<JsonNode> it = rootNode.iterator();
                if (it.hasNext()) {
                    vt.setVgroup(1);
                }
            }

            if (null != sa && sa.getId() != 0) {
                vt.setSubaccountId(sa.getId());
                company = sa.getCompanyName();
            } else {
                company = userinfo.getCompany();
            }
            vt.setRemark(req.getRemark());
            vt.setCompany(company);
            vt.setVcompany(req.getVcompany());
            vt.setCardId(req.getCardId());

            Employee emp = new Employee();
            emp.setEmpName(empName);
            emp.setEmpPhone(empPhone);
            emp.setUserid(userinfo.getUserid());

            if (req.getEmpid() != 0) {
                emp = employeeService.getEmployee(req.getEmpid());
            } else {
                emp = employeeService.getSendUrlEmp(emp);
            }

            if (null == emp) {
                    return new RespInfo(ErrorEnum.E_1119.getCode(), ErrorEnum.E_1119.getMsg());
            }
            vt.setEmpName(emp.getEmpName());
            vt.setEmpPhone(emp.getEmpPhone());

            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
            int d1 = Integer.parseInt(emp.getStartDate());
            int d2 = Integer.parseInt(emp.getEndDate());
            int today = Integer.parseInt(sd.format(new Date()));
            if (today < d1 || today > d2) {
                return new RespInfo(1113, "employee invalid date");
            }
            //查询员工部门
            List<Department> departments = departmentService.getDeptByEmpid(emp.getEmpid(), userinfo.getUserid());
            if (null!=departments&&departments.size()>0){
                vt.setEmpdeptid(departments.get(0).getDeptid());
            }else {
                vt.setEmpdeptid(0);
            }
            vt.setEmpid(emp.getEmpid());
            List<String> extcol = req.getExtendCol();
            if (null != extcol && extcol.size() > 0) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < extcol.size(); i++) {
                    String[] col = extcol.get(i).split("=");
                    map.put(col[0], col[1]);
                }
                String strext = mapper.writeValueAsString(map);
                vt.setExtendCol(strext);
            }
            vt.setPlateNum(req.getPlateNum());

            String mnames = "";
            if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getMemberName())) {
                Iterator<JsonNode> it = rootNode.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    if ("".equals(mnames)) {
                        mnames = jn.path("name").asText();
                    } else {
                        mnames = mnames + "," + jn.path("name").asText();
                    }
                }
                vt.setMemberName(mnames);
            }
            vt.setGid(req.getGid() + "");

            //检查门岗是否存在
            Gate gate = new Gate();
            gate.setGids(String.valueOf(vt.getGid()));
            gate.setUserid(userinfo.getUserid());
            List<Gate> glist = addressService.getGateById(gate);

            if (glist.size() > 0) {
                gate=glist.get(0);
            }else {
                return new RespInfo(ErrorEnum.E_125.getCode(), ErrorEnum.E_125.getMsg());
            }
            
            if (userinfo.getPermissionSwitch() == 0) {
            	vt.setPermission(1);
            }else {
            	vt.setPermission(permission);
            }
            
            if ((userinfo.getPermissionSwitch() == 0 && !"s".equals(req.getCtype()))
                    || (permission == 1 && "s".equals(req.getCtype()))) {
                //授权时设置这些数据
                vt.setTid(req.getTid());
                vt.setvType(req.getvType());
                vt.setPermissionName(empName);
            }
        /**
         * 移动端设置tid
         */
        Integer clientNo = req.getClientNo();
            if (1 == clientNo){
                vt.setTid(req.getTid());
            }
        vt.setSignInOpName(req.getSignInOpName());
            vt.setCardNo(req.getCardNo());
            visitorService.addVisitorApponintmnet(vt);
            if (permission == 1) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key", "visitor_update");
                map.put("visit_id", "v" + vt.getVid());
                map.put("company_id", userinfo.getUserid());
                messageSender.updateFaceLib(map);
            }

            if (userinfo.getProcessSwitch() == 1) {
                emp = employeeService.getSendUrlEmp(emp);

                ProcessRecord pr = new ProcessRecord();
                pr.setLevel(0);
                pr.setSubEmpId(0);
                pr.setSubEmpName(name);
                pr.setRvwEmpId(emp.getEmpid() + "");
                pr.setRvwEmpName(empName);
                pr.setVid(String.valueOf(vt.getVid()));
                pr.setUserid(userinfo.getUserid());
                pr.setPType(2);
                int i = processService.addProcessRecord(pr);
                if (i > 0) {
                    if (null != emp && null != emp.getOpenid() && !"".equals(emp.getOpenid())) {
                        processService.sendNewRequestWeiXin(pr, vt, emp);
                    }
                }
            }

            if (emp.getEmpType() == 1 && permission == 0) {
                ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
                valueOperations.set("dfvid_" + vt.getVid(), "", 5, TimeUnit.MINUTES);
            }

            if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getMemberName())) {
                Iterator<JsonNode> it = rootNode.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    Visitor v = new Visitor();
                    v.setUserid(userinfo.getUserid());
                    v.setVname(jn.path("name").asText());
                    v.setVphone(jn.path("mobile").asText());
                    v.setEmpName(emp.getEmpName());
                    v.setEmpPhone(emp.getEmpPhone());
                    v.setAppointmentDate(appdate);
                    v.setVisitType(visitType);
                    v.setSigninType(2);
                    v.setVemail(jn.path("email").asText());
                    v.setVphoto(jn.path("photo").asText());
                    v.setEmpid(emp.getEmpid());
                    v.setEmpdeptid(vt.getEmpdeptid());
                    v.setPeopleCount(req.getPeopleCount());
                    v.setVgroup(vt.getVid());
                    v.setSubaccountId(sa.getId());
                    v.setCardId(req.getCardId());
                    v.setCompany(company);
                    v.setExtendCol(vt.getExtendCol());
                    v.setGid(req.getGid());
                    v.setPlateNum(jn.path("plateNum").asText());
                    v.setClientNo(req.getClientNo());
                    v.setFloors(floors);
                    v.setQrcodeConf(1);
                    if (bmap.containsKey(jn.path("mobile").asText())) {
                        v.setbCompany(bmap.get(jn.path("mobile").asText()));
                    }
                    if (userinfo.getPermissionSwitch() == 0 && !"s".equals(req.getCtype()) || (permission == 1 && "s".equals(req.getCtype()))) {
                        v.setTid(req.getTid());
                        v.setvType(req.getvType());
                        v.setPermissionName(empName);
                    }
                    if (null != visitor) {
                        v.setVcompany(visitor.getPcompany());
                    } else {
                        v.setVcompany(req.getVcompany());
                    }
                    v.setPermission(permission);
                    vc = visitorService.getVisitSaCountByVphone(req);
                    v.setVisitorCount(Integer.parseInt(vc.getCount()));

                    // TODO: 2020/7/2 判断是否周末、节假日、梯控、调休日时间拜访
                    v.setIsWeekendVisitor(weekendPass);
                    v.setIsHolidayVisitor(holidayPass);
                    v.setIsSCTimeVisitor(scPass);
                    v.setIsDaysOffVisitor(daysOffPass);
                    vlist.add(v);
                }

                if (vlist.size() > 0) {
                    visitorService.addGroupVisitor(vlist);
                    for (int v = 0; v < vlist.size(); v++) {
                        if (permission == 1) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("key", "visitor_update");
                            map.put("visit_id", "v" + vlist.get(v).getVid());
                            map.put("company_id", userinfo.getUserid());
                            messageSender.updateFaceLib(map);
                        }
                    }
                }
            }


        messageService.sendAppoinmentPermissionNotify(vt);

        return new RespInfo(0, "success", vt.getVid());
    }

    @ApiOperation(value = "/addVisitorApponintmnet 添加预约访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"appointmentDate\":\"appointmentDate\",\n" +
                    "    \"ctype\":\"s=入驻企业\",\n" +
                    "    \"company\":\"company\",\n" +
                    "    \"name\":\"name\",\n" +
                    "    \"phone\":\"phone\",\n" +
                    "    \"cardId\":\"cardId\",\n" +
                    "    \"memberName\":\"memberName\",\n" +
                    "    \"floors\":\"floors\",\n" +
                    "    \"extendCol\":\"extendCol\",\n" +
                    "    \"gid\":\"gid\",\n" +
                    "    \"empid\":\"empid\",\n" +
                    "    \"empName\":\"empName\",\n" +
                    "    \"empPhone\":\"empPhone\",\n" +
                    "    \"visitType\":\"visitType\",\n" +
                    "    \"photoUrl\":\"photoUrl\",\n" +
                    "    \"clientNo\":\"clientNo\",\n" +
                    "    \"peopleCount\":\"peopleCount\",\n" +
                    "    \"remark\":\"remark\",\n" +
                    "    \"vcompany\":\"vcompany\",\n" +
                    "    \"cardId\":\"cardId\",\n" +
                    "    \"plateNum\":\"plateNum\",\n" +
                    "    \"tid\":\"tid\",\n" +
                    "    \"vType\":\"vType\",\n" +
                    "    \"signInOpName\":\"signInOpName\",\n" +
                    "    \"cardNo\":\"cardNo\",\n" +
                    "}" +
                    "header:"+
                    "    \"appCode\":\"appCode\",\n" +
                    "    \"timestamp\":\"timestamp\",\n" +
                    "    \"authid\":\"authid\",\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addVisitorApponintmnet")
    @ResponseBody
    public RespInfo addVisitorApponintmnet(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult bindingResult) throws JsonProcessingException {

        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }


        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date appdate = new Date();
        List<Visitor> vlist = new ArrayList<Visitor>();
        SubAccount sa = new SubAccount();
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        JsonNode rootNode = null;
        int permission = 0;

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, req.getUserid());
        if (respInfo != null) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }


        try {
            appdate = time.parse(req.getAppointmentDate());
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        UserInfo userinfo = userService.getUserInfoByUserId(req.getUserid());
        Map<String, String> bmap = new HashMap<String, String>();
        if (userinfo.getBlackListSwitch() == 1) {
            Blacklist bl = new Blacklist();
            bl.setUserid(userinfo.getUserid());
            bl.setPhone(req.getPhone());
            bl.setCredentialNo(req.getCardId());
            List<Blacklist> blList = blacklistService.checkBlacklist(bl);
            String sid = String.valueOf(sa.getId());
            if (blList.size() > 0 && StringUtils.isNotBlank(blList.get(0).getSids())) {
                String sids[] = blList.get(0).getSids().split(",");
                boolean b = false;
                for (int t = 0; t < sids.length; t++) {
                    if (sid.equals(sids[t])) {
                        b = true;
                    }
                }

                if (!b || sids.length > 1) {
                    bmap.put(req.getPhone(), blList.get(0).getSname());
                    if (!b) {
                        blList.clear();
                    }
                }
            }


            if (StringUtils.isNotBlank(req.getMemberName())) {
                try {
                    rootNode = mapper.readValue(req.getMemberName(), JsonNode.class);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Iterator<JsonNode> it = rootNode.iterator();
                Blacklist bli = new Blacklist();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    bli.setUserid(userinfo.getUserid());
                    bli.setPhone(jn.path("mobile").asText());
                    bli.setCredentialNo(jn.path("cardId").asText());
                    List<Blacklist> blist = blacklistService.checkBlacklist(bli);
                    if (blist.size() > 0 && org.apache.commons.lang3.StringUtils.isNotBlank(blist.get(0).getSids())) {
                        String sids[] = blist.get(0).getSids().split(",");
                        boolean b = false;
                        for (int t = 0; t < sids.length; t++) {
                            if (sid.equals(sids[t])) {
                                b = true;
                            }
                        }

                        if (!b || sids.length > 1) {
                            bmap.put(jn.path("mobile").asText(), blist.get(0).getSname());
                        }

                        if (b) {
                            blList.add(blist.get(0));
                        }
                    } else if (blist.size() > 0) {
                        blList.add(blist.get(0));
                    }
                }

            }

            if (blList.size() > 0) {
                return new RespInfo(66, "user was added to the blacklist", blList);
            }

        }

        Visitor vt = new Visitor();
        vt.setUserid(userinfo.getUserid());
        vt.setVphone(req.getPhone());
        vt.setAppointmentDate(appdate);
        vt.setvType(req.getvType());
        vt.setVisitType(req.getVisitType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date parse = dateFormat.parse(req.getAppointmentDate());
            vt.setAppointmentDate(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (bmap.containsKey(req.getPhone())) {
            vt.setbCompany(bmap.get(req.getPhone()));
        }


        //获取楼层信息
        String floors = "";
        List<EquipmentGroup> equipmentGroups = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getFloors())) {
            List<String> extendCol = req.getExtendCol();
            StringBuffer sb = new StringBuffer();
            for (String field : extendCol) {
                String[] split = field.split("=");
                if (split[0].equals("access") && org.apache.commons.lang3.StringUtils.isNotEmpty(split[1])) {
                    String egids = split[1];
                    equipmentGroups = equipmentGroupService.getEquipmentGroupByEgidArray(egids.split(","));
                    if (equipmentGroups.size() > 0) {
                        for (int i = 0; i < equipmentGroups.size(); i++) {
                            String egname = equipmentGroups.get(i).getEgname();
                            if (i < equipmentGroups.size() - 1) {
                                sb.append(egname + ",");
                            } else {
                                sb.append(egname);
                            }
                        }
                        floors = sb.toString();
                    }
                }
            }
        } else {
            floors = req.getFloors();
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(floors)) {
            StringBuffer sb = new StringBuffer();
            String[] floorArr = floors.split(",");
            String f = "";
            //截取字母F或M之前的数据统一进行处理 T1-15/33/39/40/42/43/44F&106
            for (int i = 0; i < floorArr.length; i++) {
                if (floorArr[i].contains("F")) {
                    floorArr[i] = floorArr[i].split("F")[0];
                }
                if (floorArr[i].contains("M")) {
                    floorArr[i] = floorArr[i].split("M")[0];
                }
                if (i < floorArr.length - 1) {
                    sb.append(floorArr[i] + ",");
                } else {
                    sb.append(floorArr[i]);
                }
            }
            floors = sb.toString();
            vt.setFloors(floors);
        }

        // TODO: 2020/7/2 判断是否周末、节假日、梯控、调休日时间拜访
        String weekendPass = "";
        String holidayPass = "";
        String scPass = "";
        String daysOffPass = "";
        if (StringUtils.isNotEmpty(floors)) {
            Boolean w = false;
            Boolean h = false;
            Boolean d = false;
            Date date = new Date();

            String[] gids = req.getGid().split(",");
            for (String gid : gids) {
                if (!w) {
                    w = passRuleService.isWeekendPass(date, Integer.parseInt(gid), userinfo.getUserid());
                }
                if (!h) {
                    h = passRuleService.isHolidayPass(date, Integer.parseInt(gid), userinfo.getUserid());
                }
                String[] f = floors.split(",");
                int status = passRuleService.getSendCardStatus(userinfo.getUserid(), appdate, f);
                scPass = status == 1 ? "是" : "否";
                if (!d) {
                    d = passRuleService.isDaysOffTranslation(date, Integer.parseInt(gid), Arrays.asList(f), userinfo.getUserid());
                }
            }
            weekendPass = w ? "是" : "否";
            holidayPass = h ? "是" : "否";
            daysOffPass = d ? "是" : "否";
            vt.setIsWeekendVisitor(weekendPass);
            vt.setIsHolidayVisitor(holidayPass);
            vt.setIsSCTimeVisitor(scPass);
            vt.setIsDaysOffVisitor(daysOffPass);
        }

        //限制每个被访人的访问量，考虑到随访人的情况，限制为15条每天
        List<RespVisitor> listrv = visitorService.getVisitorAppointmentList(vt);
        if (listrv.size() > 15) {
            return new RespInfo(ErrorEnum.E_100.getCode(), ErrorEnum.E_100.getMsg());
        }

        Person visitor = personInfoService.getVisitPersonByPhone(vt.getVphone());
        String name = req.getName();
        String empName = req.getEmpName();
        String empPhone = req.getEmpPhone();
        String visitType = req.getVisitType();

        String company = "";
        vt.setVname(name);
        vt.setVemail(req.getEmail());
        vt.setEmpName(empName);
        vt.setEmpPhone(empPhone);
        vt.setAppointmentDate(appdate);
        vt.setVisitType(visitType);
        vt.setSigninType(2);
        vt.setVphoto(req.getPhotoUrl());
        vt.setClientNo(req.getClientNo());
        vt.setQrcodeConf(1);
        req.setSubaccountId(sa.getId());
        VisitorChart vc = visitorService.getVisitSaCountByVphone(req);
        vt.setVisitorCount(Integer.parseInt(vc.getCount()));
        vt.setPeopleCount(req.getPeopleCount());
        if (StringUtils.isNotBlank(req.getMemberName())) {
            try {
                rootNode = mapper.readValue(req.getMemberName(), JsonNode.class);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Iterator<JsonNode> it = rootNode.iterator();
            if (it.hasNext()) {
                vt.setVgroup(1);
            }
        }

        if (null != sa && sa.getId() != 0) {
            vt.setSubaccountId(sa.getId());
            company = sa.getCompanyName();
        } else {
            company = userinfo.getCompany();
        }
        vt.setRemark(req.getRemark());
        vt.setCompany(company);
        vt.setVcompany(req.getVcompany());
        vt.setCardId(req.getCardId());

        Employee emp = new Employee();
        emp.setEmpName(empName);
        emp.setEmpPhone(empPhone);
        emp.setUserid(userinfo.getUserid());

        if (req.getEmpid() != 0) {
            emp = employeeService.getEmployee(req.getEmpid());
        } else {
            emp = employeeService.getSendUrlEmp(emp);
        }

        if (null == emp) {
            return new RespInfo(ErrorEnum.E_1119.getCode(), ErrorEnum.E_1119.getMsg());
        }
        vt.setEmpName(emp.getEmpName());
        vt.setEmpPhone(emp.getEmpPhone());

        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(emp.getStartDate());
        int d2 = Integer.parseInt(emp.getEndDate());
        int today = Integer.parseInt(sd.format(new Date()));
        if (today < d1 || today > d2) {
            return new RespInfo(1113, "employee invalid date");
        }
        //查询员工部门
        List<Department> departments = departmentService.getDeptByEmpid(emp.getEmpid(), userinfo.getUserid());
        if (null!=departments&&departments.size()>0){
            vt.setEmpdeptid(departments.get(0).getDeptid());
        }else {
            vt.setEmpdeptid(0);
        }
        vt.setEmpid(emp.getEmpid());
        List<String> extcol = req.getExtendCol();
        if (null != extcol && extcol.size() > 0) {
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < extcol.size(); i++) {
                String[] col = extcol.get(i).split("=");
                map.put(col[0], col[1]);
            }
            String strext = mapper.writeValueAsString(map);
            vt.setExtendCol(strext);
        }

        //access字段
        String access = vt.getExtendValue("access");
        if (StringUtils.isEmpty(access)) {
            //未设置access,插入默认门禁值

            //获取默认门禁
            access = visitorService.getDefaultAccess(userinfo, vt.getGid() + "", vt.getEmpid());
            vt.addExtendValue("access", access);
        }
        visitorService.addExtendSetting(userinfo,vt);
        String endDate = vt.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
        if(org.apache.commons.lang.StringUtils.isNotEmpty(endDate)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("gmt"));
                Date date = format.parse(endDate);
                int conf= UtilTools.differentDays(vt.getAppointmentDate(),date);
                vt.setQrcodeConf(conf+1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        vt.setPlateNum(req.getPlateNum());

        String mnames = "";
        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getMemberName())) {
            Iterator<JsonNode> it = rootNode.iterator();
            while (it.hasNext()) {
                JsonNode jn = it.next();
                if ("".equals(mnames)) {
                    mnames = jn.path("name").asText();
                } else {
                    mnames = mnames + "," + jn.path("name").asText();
                }
            }
            vt.setMemberName(mnames);
        }
        vt.setGid(req.getGid() + "");

        //检查门岗是否存在
        Gate gate = new Gate();
        gate.setGids(String.valueOf(vt.getGid()));
        gate.setUserid(userinfo.getUserid());
        List<Gate> glist = addressService.getGateById(gate);

        if (glist.size() > 0) {
            gate=glist.get(0);
        }else {
            return new RespInfo(ErrorEnum.E_125.getCode(), ErrorEnum.E_125.getMsg());
        }


        /**
         * 移动端设置tid
         */
        Integer clientNo = req.getClientNo();
        if (1 == clientNo){
            vt.setTid(req.getTid());
        }
        vt.setSignInOpName(req.getSignInOpName());
        vt.setCardNo(req.getCardNo());
        vt.setArea(req.getArea());
        vt.setAid(req.getAid());
        visitorService.addVisitorApponintmnet(vt);
        visitorService.updateVGroup(vt);


        if (emp.getEmpType() == 1 && permission == 0) {
            ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
            valueOperations.set("dfvid_" + vt.getVid(), "", 5, TimeUnit.MINUTES);
        }

        if (StringUtils.isNotBlank(req.getMemberName())) {
            Iterator<JsonNode> it = rootNode.iterator();
            while (it.hasNext()) {
                JsonNode jn = it.next();
                Visitor v = new Visitor();
                v.setUserid(userinfo.getUserid());
                v.setVname(jn.path("name").asText());
                v.setVphone(jn.path("mobile").asText());
                v.setEmpName(emp.getEmpName());
                v.setEmpPhone(emp.getEmpPhone());
                v.setAppointmentDate(appdate);
                v.setVisitType(visitType);
                v.setvType(req.getvType());
                v.setSigninType(2);
                v.setVemail(jn.path("email").asText());
                v.setVphoto(jn.path("photo").asText());
                v.setEmpid(emp.getEmpid());
                v.setEmpdeptid(vt.getEmpdeptid());
                v.setPeopleCount(req.getPeopleCount());
                v.setVgroup(vt.getVid());
                v.setSubaccountId(sa.getId());
                v.setCardId(req.getCardId());
                v.setCompany(company);
                v.setExtendCol(vt.getExtendCol());
                v.setGid(req.getGid());
                v.setPlateNum(jn.path("plateNum").asText());
                v.setClientNo(req.getClientNo());
                v.setFloors(floors);
                v.setQrcodeConf(vt.getQrcodeConf());
                if (bmap.containsKey(jn.path("mobile").asText())) {
                    v.setbCompany(bmap.get(jn.path("mobile").asText()));
                }
                if (null != visitor && StringUtils.isNotEmpty(visitor.getPcompany())) {
                    v.setVcompany(visitor.getPcompany());
                } else {
                    v.setVcompany(req.getVcompany());
                }
                v.setPermission(permission);
                vc = visitorService.getVisitSaCountByVphone(req);
                v.setVisitorCount(Integer.parseInt(vc.getCount()));

                // TODO: 2020/7/2 判断是否周末、节假日、梯控、调休日时间拜访
                v.setIsWeekendVisitor(weekendPass);
                v.setIsHolidayVisitor(holidayPass);
                v.setIsSCTimeVisitor(scPass);
                v.setIsDaysOffVisitor(daysOffPass);
                vlist.add(v);
            }

            if (vlist.size() > 0) {
                visitorService.addGroupVisitor(vlist);
            }
        }


        try {
            visitorService.firstPermissionSubBPM(vt,userinfo);
            return new RespInfo(0, "success", vt.getVid());
        }catch (CompileFlowException e){
            if(e.getCause() instanceof ErrorException){
                ErrorException errorException = (ErrorException)e.getCause();
                return new RespInfo(errorException.getErrorEnum().getCode(), errorException.getErrorEnum().getMsg());
            }
            System.out.println(e.getCause());
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return new RespInfo(ErrorEnum.E_2000.getCode(), ErrorEnum.E_2000.getMsg());
    }

    @ApiOperation(value = "/getArrivedVCount 获取到达访客总数", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"2021-10-02\",\n" +
                    "    \"endDate\":\"2021-10-02\"\n" +
                    "}"
    )
    @RequestMapping(value = "/getArrivedVCount", method = RequestMethod.POST)
    @ResponseBody
    public RespInfo getArrivedVCount(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit rv,
            HttpServletRequest request, BindingResult bindingResult) {

        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = null;
        String ctoken = request.getHeader("X-COOLVISIT-TOKEN");
        String decode = AESUtil.decode(ctoken, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            authToken = mapperInstance.readValue(decode, AuthToken.class);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }

        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        rv.setUserid(authToken.getUserid());
        List<VisitorChart> glist = opendoorService.getArrivedVCount(rv);
        return new RespInfo(0, "success", glist);

    }


    @ApiOperation(value = "/updatePermission 访客预约授权", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":430438,\n" +
                    "    \"type\":\"p\",\n" +
                    "    \"vType\":\"普通访客#Normal Visitor\",\n" +
                    "    \"tid\":221,\n" +
                    "    \"gid\":1,\n" +
                    "    \"remark\":\"稍后我就来了\",\n" +
                    "    \"vtExtendCol\":\"{\\\"access\\\":\\\"\\\",\\\"remark\\\":\\\"稍后我就来了\\\"}\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updatePermission")
    @ResponseBody
    public RespInfo updatePermission(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        int userid = req.getUserid();
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if (authToken.getUserid() != userid) {
//            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }

        String permissionName = "manager";
        //管理员：校验是否是所属userid
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()) && userid != authToken.getUserid()) {
            throw new RuntimeException(ErrorEnum.E_609.getMsg() + userid);
        }
        // 员工/代理：校验是否是自己的访客
        else if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            Visitor v = visitorService.getVisitorById(req.getVid());
            //判断是不是自己的访客
            Employee vEmployee = employeeService.getEmployee(v.getEmpid());
            if (null == vEmployee) {
                return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
            } else {
                permissionName = vEmployee.getEmpName();
                //访客记录的员工id跟请求的token中的empid不一致：查询自己的代理人
                if (vEmployee.getEmpid() != Integer.parseInt(authToken.getLoginAccountId())) {
                    //判断是不是自己代理人
                    EmpVisitProxy empVisitProxy = new EmpVisitProxy();
                    empVisitProxy.setEmpid(v.getEmpid());
                    empVisitProxy.setUserid(authToken.getUserid());
                    EmpVisitProxy proxyInfoByEid = visitProxyService.getProxyInfoByEid(empVisitProxy);
                    //如果既不是自己的访客记录，也不是自己的代理人的，直接返回非法请求
                    if (null == proxyInfoByEid || proxyInfoByEid.getProxyId() != Integer.parseInt(authToken.getLoginAccountId())) {
                        return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
                    }
                }
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }


        try {
            int vid = req.getVid();
            Visitor v = visitorService.getVisitorById(vid);
            if (null == v) {
                return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
            }
            if (v.getPermission() != 0) {
                return new RespInfo(ErrorEnum.E_070.getCode(), ErrorEnum.E_070.getMsg());
            }
            UserInfo userinfo = userService.getUserInfo(v.getUserid());
            if(userinfo == null){
                return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
            }

            v.setPermissionName(permissionName);
            v.setTid(req.getTid());
            v.setvType(req.getvType());
            if(StringUtils.isNotEmpty(req.getVtExtendCol())) {
                v.setExtendCol(req.getVtExtendCol());
            }
            v.setArea(req.getArea());
            v.setMeetingPoint(req.getMeetingPoint());
            if(StringUtils.isNotEmpty(req.getGid())) {
                v.setGid(req.getGid() + "");
            }
            v.setRemark(req.getRemark());
            visitorService.addExtendSetting(userinfo,v);


            if ("p".equals(req.getType())) {
                //access字段
                if(StringUtils.isEmpty(v.getExtendValue(VisitorService.EXTEND_KEY_ACCESS))) {
                String access = visitorService.getDefaultAccess(userinfo, v.getGid() + "", v.getEmpid());
                    v.addExtendValue(VisitorService.EXTEND_KEY_ACCESS, access);
                }

                v.setPermission(VisitorService.PERMISSION_SEC);
                visitorService.updateGroupPermission(v);
                visitorService.supplementSubBPM(v, userinfo);
            } else {
                v.setPermission(VisitorService.PERMISSION_REJECT);
                visitorService.updateGroupPermission(v);
            }

            ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
            if (StringUtils.isNotBlank(valueOperations.get("dfvid_" + v.getVid()))) {
                strRedisTemplate.delete("dfvid_" + v.getVid());
            }


            return new RespInfo(0, "success");
        } finally {
        }
    }

    @ApiOperation(value = "/getPermissionRecordByLink 获取访客短信预约授权记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"digest\":\"D130502066BBBF58B4DA4AE98877557C\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getPermissionRecordByLink")
    @ResponseBody
    public RespInfo getPermissionRecordByLink(@ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
                                              HttpServletRequest request, BindingResult bindingResult) {
        try {
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

            return new RespInfo(0, "success", v);
        } catch (Exception e) {
            return new RespInfo(0, "fail");
        }

    }

    @ApiOperation(value = "/updatePermissionByLink 访客预约短信授权", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updatePermissionByLink")
    @ResponseBody
    public RespInfo updatePermissionByLink(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult bindingResult) {
        try {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            if (!allErrors.isEmpty()) {
                throw new HttpMessageNotReadableException(allErrors.toString());
            }

            int vid = Integer.parseInt(AESUtil.decode(req.getDigest(), Constant.AES_KEY));
            Visitor v = visitorService.getVisitorById(vid);
            if (null == v || v.getPermission() != 0) {
                return new RespInfo(ErrorEnum.E_070.getCode(), ErrorEnum.E_070.getMsg());
            }

            Employee emp = employeeService.getEmployee(v.getEmpid());
            if (null == emp) {
                return new RespInfo(ErrorEnum.E_055.getCode(), ErrorEnum.E_055.getMsg());
            }
            UserInfo userinfo = userService.getUserInfo(v.getUserid());
            if(userinfo == null){
                return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
            }
            v.setTid(req.getTid());
            v.setvType(req.getvType());
            v.setExtendCol(req.getVtExtendCol());
            v.setArea(req.getArea());
            v.setMeetingPoint(req.getMeetingPoint());
            if(StringUtils.isNotEmpty(req.getGid())) {
                v.setGid(req.getGid() + "");
            }
            v.setRemark(req.getRemark());
            String floors = req.getFloors();
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(floors)) {
                floors = floors.substring(0, floors.length() - 1);
            }
            v.setFloors(floors);
            // TODO: 2020/7/3 计算预约->授权时间
            Date createTime = v.getCreateTime();
            if (createTime != null) {
                Date nowDate = new Date();
                v.setWaitPermissionSeconds((nowDate.getTime() - createTime.getTime()) / 1000 + "");
            }


            if ("p".equals(req.getType())) {
                //access字段
                if(StringUtils.isEmpty(v.getExtendValue(VisitorService.EXTEND_KEY_ACCESS))) {
                String access = visitorService.getDefaultAccess(userinfo, v.getGid() + "", v.getEmpid());
                v.addExtendValue("access", access);
                }

                visitorService.addExtendSetting(userinfo,v);
                v.setPermission(VisitorService.PERMISSION_SEC);
                visitorService.updateGroupPermission(v);
                visitorService.supplementSubBPM(v, userinfo);
            } else {
                v.setPermission(VisitorService.PERMISSION_REJECT);
                visitorService.updateGroupPermission(v);
            }
            ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(valueOperations.get("dfvid_" + v.getVid()))) {
                strRedisTemplate.delete("dfvid_" + v.getVid());
            }

            return new RespInfo(0, "success");
        } catch (Exception e) {
            e.printStackTrace();
            return new RespInfo(ErrorEnum.E_2000.getCode(), ErrorEnum.E_2000.getMsg());
        }
    }


    @ApiOperation(value = "/SearchRecordsByPhone 根据员工手机号查询自己的访客记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"123456\",\n" +
                    "    \"date\":\"date\",\n" +
                    "    \"endDate\":\"date\",\n" +
                    "    \"visitType\":\"visitType\",\n" +
                    "    \"name\":\"name\",\n" +
                    "    \"startIndex\":\"startIndex\",\n" +
                    "    \"requestedCount\":\"requestedCount\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchRecordsByPhone")
    @ResponseBody
    public RespInfo SearchRecordsByPhone(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
        if (employee == null) {
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }


        String startDate = req.getDate();
        String endDate = req.getEndDate();
        String empPhone = employee.getEmpPhone();
        String visitType = req.getVisitType();
        int userid = authToken.getUserid();
        int empid = Integer.parseInt(authToken.getLoginAccountId());
        String vname = req.getName();

        Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
        RespVisitor entity = new RespVisitor();
        entity.setEmpPhone(empPhone);
        entity.setEmpid(empid);
        entity.setPage(rvpage);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setVname(vname);

        List<RespVisitor> rvlist = new ArrayList<RespVisitor>();
        if ("1".equals(visitType)) {
            rvlist = visitorService.getAppointmentListByEmpPhone(entity);
            for (int i = 0; i < rvlist.size(); i++) {
                rvlist.get(i).setEncryption(AESUtil.encode(rvlist.get(i).getVid() + "", Constant.AES_KEY));
            }
        } else {
            rvlist = visitorService.getVistorAppListByEmpPhone(entity);
            for (int i = 0; i < rvlist.size(); i++) {
                rvlist.get(i).setEncryption(AESUtil.encode("v"+rvlist.get(i).getVid() + "", Constant.AES_KEY));
            }
        }

//        if (StringUtils.isNotBlank(authToken.getOpenid())) {
//            /**
//             * 优先通过手机号查找，这样可以兼容重新添加人员的情况
//             * 如果没有手机号，则通过empid匹配
//             */
//            //检查empPhone 与 openid的一致性
//            List<Employee> empList = employeeService.getEmployeebyPhone(empPhone, authToken.getOpenid(), userid);
//            if (empList.size() > 0) {
//
//            } else {
//                return new RespInfo(1, "invalid user");
//            }
//        } else {
//            List<Employee> empList = employeeService.getEmployeebyPhone(empPhone, null, userid);
//            if(empList.size() == 0){
//                Employee employee = employeeService.getEmployee(empid);
//                if(employee != null){
//                    empList.add(employee);
//                }
//            }
//            if (empList.size() > 0 && String.valueOf(empList.get(0).getEmpid()).equals(authToken.getLoginAccountId())) {
//                if ("1".equals(visitType)) {
//                    rvlist = visitorService.getAppointmentListByEmpPhone(entity);
//                    for (int i = 0; i < rvlist.size(); i++) {
//                        rvlist.get(i).setEncryption(AESUtil.encode(rvlist.get(i).getVid() + "", Constant.AES_KEY));
//                    }
//                } else {
//                    rvlist = visitorService.getVistorAppListByEmpPhone(entity);
//                    for (int i = 0; i < rvlist.size(); i++) {
//                        rvlist.get(i).setEncryption(AESUtil.encode("v"+rvlist.get(i).getVid(), Constant.AES_KEY));
//                    }
//                }
//            } else {
//                return new RespInfo(1, "invalid user");
//            }
//        }

        rvpage.setList(rvlist);

        return new RespInfo(0, "success", rvpage);
    }


    @ApiOperation(value = "/getAllArrivedVisitorChart 获取签到访客图表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"type\":\"sex\",\n" +
                    "    \"userid\":\"123456\",\n" +
                    "    \"date\":\"2020-01-01\",\n" +
                    "    \"endDate\":\"2020-01-01\",\n" +
                    "}"
    )
    @RequestMapping(value = "/getAllArrivedVisitorChart", method = RequestMethod.POST)
    @ResponseBody
    public RespInfoT<List<VisitorChart>> getAllArrivedVisitorChart(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit requestVisit,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != requestVisit.getUserid()) {
            throw new RuntimeException(ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){

        }
        else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
    		Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
    		String gids[]=requestVisit.getGid().split(",");
    		String mgids[]=mgr.getGid().split(",");
    	    boolean auth=UtilTools.arrayContain(gids, mgids);
    		if(auth) {
    			return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
    		}
        }else{
            return new RespInfoT(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }

        List<VisitorChart> chart = visitorService.getAllArrivedVisitorChart(requestVisit);
        return new RespInfoT(0, "success", chart);
    }

    @ApiOperation(value = "/getVisitorChartByDept 部门访客统计表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"signinType\":\"signinType\",\n" +
                    "    \"userid\":\"123456\",\n" +
                    "    \"chartStatus\":\"chartStatus\",\n" +
                    "    \"date\":\"2020-01-01\",\n" +
                    "    \"endDate\":\"2020-01-01\",\n" +
                    "}"
    )
    @RequestMapping(value = "/getVisitorChartByDept", method = RequestMethod.POST)
    @ResponseBody
    public RespInfo getVisitorChartByDept(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit requestVisit,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != requestVisit.getUserid()) {
            throw new RuntimeException(ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){

        }
        else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[]=requestVisit.getGid().split(",");
            String mgids[]=mgr.getGid().split(",");
            boolean auth=UtilTools.arrayContain(gids, mgids);
            if(auth) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }

        UserInfo userInfo = userService.getUserInfoByUserId(requestVisit.getUserid());
        Integer signinType = 0;
        if (StringUtils.isNotBlank(requestVisit.getSigninType())) {
            signinType = Integer.parseInt(requestVisit.getSigninType());
        }
        List<VisitorChart> visitorCharts = new ArrayList<>();
        List<VisitorChart> visitorChartList = new ArrayList<>();
        if (userInfo.getSubAccount() == 0 && StringUtils.isNotEmpty(requestVisit.getChartStatus())) {
            switch (signinType) {
                /**
                 * 默认
                 */
                case 0:
                    //查询签到访客的拜访部门数
                    visitorCharts = visitorService.getSignInVisitorByDept(requestVisit);
                    break;
                /**
                 * 邀请访客
                 */
                case 1:
                    visitorCharts = appointmentService.getDeptAppVisitByAppDate(requestVisit);
                    break;
                /**
                 * 预约
                 */
                case 2:
                    visitorCharts = visitorService.getDeptVisitByAppDate(requestVisit);
                    break;
                default:
                    visitorCharts = visitorService.getDeptVisitByAppDate(requestVisit);
                    break;
            }
            List<Department> secondDepartmentList = departmentService.getDepartmentList(userInfo.getUserid());
            Map<Integer, List<Department>> secondDeptIds = secondDepartmentList.stream().collect(Collectors.groupingBy(Department::getDeptid));
            Map<String, List<VisitorChart>> visitorsChartBydeptid = visitorCharts.stream().collect(Collectors.groupingBy(visitorChart -> visitorChart.getDeptId()));
            for (Map.Entry<String, List<VisitorChart>> entry:visitorsChartBydeptid.entrySet()) {
                if(!recursionGetSecondDept(entry.getValue(), Integer.parseInt(entry.getKey()), secondDeptIds, userInfo.getUserid()))
                {
                    continue;
                }
            }

            if (visitorCharts.size() > 0) {
            	for (Map.Entry<String, List<VisitorChart>> entry : visitorsChartBydeptid.entrySet()) {
            		visitorChartList.addAll(entry.getValue());
            	}
            	Map<String, List<VisitorChart>> vcMap = visitorChartList.stream().filter(p -> p.getDeptName() != null).collect(Collectors.groupingBy(VisitorChart::getDeptName));
                return new RespInfo(0, "success", vcMap);
            }
        }
        return new RespInfo(0, "success");
    }

    /**
     * @param empDeptid     员工的部门id
     * @param chartList 部门相同的访客集合
     * @param secondDeptIds 二级部门id集合
     * @param userId        企业id
     */
    public boolean  recursionGetSecondDept(List<VisitorChart> chartList, int empDeptid,  Map<Integer, List<Department>> secondDeptIds, Integer userId) {
        // TODO: 2020/6/3 查员工的部门
        Department department = departmentService.getDepartment(empDeptid, userId);
        if(null==department) {
        	return false;
        }
        if (secondDeptIds.get(department.getDeptid()) != null && secondDeptIds.get(department.getDeptid()).size() > 0) {
        	for(int a=0;a<chartList.size();a++) {
        		chartList.get(a).setDeptName(department.getDeptName());
        		chartList.get(a).setDeptId(String.valueOf(department.getDeptid()));
        	}
        } else {
            recursionGetSecondDept(chartList, department.getParentId(), secondDeptIds, userId);
        }

        return true;
    }

    @ApiOperation(value = "/updateLeaveTime 更新离开时间", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"signinType\":\"signinType\",\n" +
                    "    \"vid\":\"123456\",\n" +
                    "    \"userid\":\"123456\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateLeaveTime")
    @ResponseBody
    public RespInfo updateLeaveTime(
            @ApiParam(value = "Visitor 访客Bean", required = true) @RequestBody @Validated Visitor v,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != v.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){

        }
        else if(AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())){
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            int vEmpid = 0;//访客记录中的被访人id
            String vEmpPhone = null;//访客记录中的被访人手机号
            if(employee == null){
                return new RespInfo(ErrorEnum.E_608.getCode(),ErrorEnum.E_608.getMsg());
            }
            if (v.getSigninType() == 1) {
                Appointment app = appointmentService.getAppointmentbyId(v.getVid());
                if(app==null){
                    return new RespInfo(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
                }
                vEmpid = app.getEmpid();
                vEmpPhone = app.getEmpPhone();
            }else{
                Visitor visitor = visitorService.getVisitorById(v.getVid());
                if(visitor==null){
                    return new RespInfo(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
                }
                vEmpid = visitor.getEmpid();
                vEmpPhone = visitor.getEmpPhone();
            }
            if(vEmpPhone == null){
                vEmpPhone = "";
            }
            if(vEmpid != employee.getEmpid() && !vEmpPhone.equals(employee.getEmpPhone())){

                //判断是不是自己代理人
                EmpVisitProxy empVisitProxy = new EmpVisitProxy();
                empVisitProxy.setEmpid(vEmpid);
                empVisitProxy.setUserid(authToken.getUserid());
                EmpVisitProxy proxyInfoByEid = visitProxyService.getProxyInfoByEid(empVisitProxy);
                //如果既不是自己的访客记录，也不是自己的代理人的，直接返回非法请求
                if (null == proxyInfoByEid || proxyInfoByEid.getProxyId() != Integer.parseInt(authToken.getLoginAccountId())) {
                    return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
                }

            }
        }
        else{
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }

        UserInfo ui = userService.getUserInfo(v.getUserid());

        if (v.getSigninType() == 1) {
            Appointment at = new Appointment();
            at.setId(v.getVid());
            at.setLeaveTime(new Date());
            at.setUserid(v.getUserid());
            appointmentService.updateLeaveTime(at);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "visit_will_finish");
            map.put("visit_id", "a" + at.getId());
            map.put("company_id", v.getUserid());
            messageSender.updateFaceLib(map);

            v.setAppid(v.getVid());
            v = visitorService.getVisitorByAppId(v);
            if(v != null) {
                v.setLeaveTime(new Date());
                visitorService.updateLeaveTime(v);

                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("key", "visit_will_finish");
                map2.put("visit_id", "v" + v.getVid());
                map2.put("company_id", v.getUserid());
                messageSender.updateFaceLib(map2);

                if (ui.getLeaveExpiryTime() > 0) {
                    ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
                    valueOperations.set("aid_" + v.getAppid(), "", ui.getLeaveExpiryTime(), TimeUnit.MINUTES);
                }
            }
        } else {
            v.setLeaveTime(new Date());
            visitorService.updateLeaveTime(v);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "visit_will_finish");
            map.put("visit_id", "v" + v.getVid());
            map.put("company_id", v.getUserid());
            messageSender.updateFaceLib(map);

            if (ui.getLeaveExpiryTime() > 0) {
                ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
                valueOperations.set("vid_" + v.getVid(), "", ui.getLeaveExpiryTime(), TimeUnit.MINUTES);
            }
        }


        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateVisitInfo 更新访客信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"vid\":\"44147\"，\n" +
                    "\"visitReason\":\"商务谈判\",\n" +
                    "\"aid\":2,\n" +
                    "\"area\":\"B8\",\n" +
                    "\"remark\":\"携带比较多\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateVisitInfo")
    @ResponseBody
    public RespInfo updateVisitInfo(
            @ApiParam(value = "Visitor 访客Bean", required = true) @RequestBody @Validated Visitor vt,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
        ){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (vt.getSigninType() == 1) {
            Appointment at = new Appointment();
            at.setId(vt.getAppid());
            at.setArea(vt.getArea());
            at.setAreaid(vt.getAid());
            at.setVisitReason(vt.getVisitReason());
            appointmentService.updateAppAreaInfo(at);
        } else {
            visitorService.updateVisitInfo(vt);

        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/setCancelStatus 员工取消邀约", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "用于员工同意访客预约，或者发出邀请函后取消邀约请求，访客会收到取消邀约的通知 \n" +
                    "POST示例入参：\n" +
                    "{\n" +
                    "\"vid\":\"44147\"，\n" +
                    "\"type\":\"a\",\n" +
                    "}"
    )
//    @ApiOperationSupport(includeParameters = {"req.vid","req.type","x-coolvisit-token"})
    @RequestMapping(method = RequestMethod.POST, value = "/setCancelStatus")
    @ResponseBody
    public RespInfo setCancelStatus(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        String type = req.getType();
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){

        }
        else if(AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())){
            Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            int vEmpid = 0;//访客记录中的被访人id
            String vEmpPhone = null;//访客记录中的被访人手机号
            if(employee == null){
                return new RespInfo(ErrorEnum.E_608.getCode(),ErrorEnum.E_608.getMsg());
            }
            if (type.equals("a")) {
                Appointment app = appointmentService.getAppointmentbyId(req.getVid());
                if(app==null){
                    return new RespInfo(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
                }
                vEmpid = app.getEmpid();
                vEmpPhone = app.getEmpPhone();
            }else{
                Visitor visitor = visitorService.getVisitorById(req.getVid());
                if(visitor==null){
                    return new RespInfo(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
                }
                vEmpid = visitor.getEmpid();
                vEmpPhone = visitor.getEmpPhone();
            }
            if(vEmpPhone == null){
                vEmpPhone = "";
            }
            if(vEmpid != employee.getEmpid() && !vEmpPhone.equals(employee.getEmpPhone())){

                //判断是不是自己代理人
                EmpVisitProxy empVisitProxy = new EmpVisitProxy();
                empVisitProxy.setEmpid(vEmpid);
                empVisitProxy.setUserid(authToken.getUserid());
                EmpVisitProxy proxyInfoByEid = visitProxyService.getProxyInfoByEid(empVisitProxy);
                //如果既不是自己的访客记录，也不是自己的代理人的，直接返回非法请求
                if (null == proxyInfoByEid || proxyInfoByEid.getProxyId() != Integer.parseInt(authToken.getLoginAccountId())) {
                    return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
                }

            }
        }
        else{
            return new RespInfo(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
        }

        if (type.equals("a")) {
            Appointment a = appointmentService.getAppointmentbyId(req.getVid());
            if (null == a) {
                return new RespInfo(1, "invalid user");
            }
            Employee emp = employeeService.getEmployee(a.getEmpid());
            EmpVisitProxy vp = new EmpVisitProxy();
            vp.setEmpid(a.getEmpid());
            vp.setUserid(a.getUserid());
            vp = visitProxyService.getProxyInfoByEid(vp);
            if (null != vp) {
                List<Integer> empid = employeeService.getEmpIdByOpenid(authToken.getOpenid());
                if (empid.size() > 0 && vp.getProxyId() != empid.get(0) && empid.get(0) != vp.getEmpid()) {
                    return new RespInfo(1, "invalid user");
                }
            } else {
                if (null == emp || !authToken.getLoginAccountId().equals(emp.getEmpid()+"")) {
                    return new RespInfo(1, "invalid user");
                }
            }
            a.setPermission(3);//与预约保持一致
            UserInfo userinfo = userService.getUserInfo(a.getUserid());
            Visitor vt = new Visitor();
            vt.setVname(a.getName());
            vt.setVphone(a.getPhone());
            vt.setAppointmentDate(a.getAppointmentDate());
            appointmentService.updateAppointmentPermission(a);
            int i = appointmentService.AppointmentReply(a);

            List<Appointment> appointmentList = new ArrayList<Appointment>();
            appointmentList.add(a);
            passService.passAuth(appointmentList, PassEvent.Pass_Del);

        }

        if (type.equals("v")) {
            int vid = req.getVid();
            Visitor v = visitorService.getVisitorById(vid);
            if (null == v) {
                return new RespInfo(1, "invalid user");
            }
            Employee emp = employeeService.getEmployee(v.getEmpid());
            EmpVisitProxy vp = new EmpVisitProxy();
            vp.setEmpid(v.getEmpid());
            vp.setUserid(v.getUserid());
            vp = visitProxyService.getProxyInfoByEid(vp);
            if (null != vp) {
                List<Integer> empid = employeeService.getEmpIdByOpenid(authToken.getOpenid());
                if (empid.size() > 0 && vp.getProxyId() != empid.get(0) && empid.get(0) != vp.getEmpid()) {
                    return new RespInfo(1, "invalid user");
                }
            } else {
                if (null == emp || !authToken.getLoginAccountId().equals(emp.getEmpid()+"")) {
                    return new RespInfo(1, "invalid user");
                }
            }

            v.setPermission(3);
            if (v.getVgroup() == 1) {
                visitorService.updateGroupPermission(v);
            } else {
                visitorService.updatePermission(v);
            }

            List<Visitor> appointmentList = new ArrayList<Visitor>();
            appointmentList.add(v);
            passService.passAuth(appointmentList, PassEvent.Pass_Del);

        }


        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/SearchRecordsByVisitorPhone 访客通过手机号查询自己的访客记录根据", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"userid\":\"userid\",\n" +
                    "\"date\":\"2016-08-08\",\n" +
                    "\"endDate\":\"2016-08-08\",\n" +
                    "\"phone\":15251805548,\n" +
                    "\"startIndex\":1,\n" +
                    "\"requestedCount\":10,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchRecordsByVisitorPhone")
    @ResponseBody
    public RespInfo SearchRecordsByVisitorPhone(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        String startDate = req.getDate();
        String endDate = req.getEndDate();

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        访客校验访客手机号
        if (AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())) {
            Person personByOpenid = personInfoService.getVisitPersonByOpenid(authToken.getOpenid());
            if (null == personByOpenid ) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            req.setPhone(personByOpenid.getPmobile());
        } else if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        || AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            //管理员校验userid
            if (req.getUserid() != authToken.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
//            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
        RespVisitor entity = new RespVisitor();
        entity.setVphone(req.getPhone());
        entity.setPage(rvpage);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);

        List<RespVisitor> rvlist = visitorService.getAppointmentListByVPhone(entity);
        rvpage.setList(rvlist);
        return new RespInfo(0, "success", rvpage);
    }

    @ApiOperation(value = "/AppClientSignin 访客机签到", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"userid\",\n" +
                    "    \"verifyCode\":\"verifyCode\",\n" +
                    "    \"signInOpName\":\"signInOpName\",\n" +
                    "    \"signInGate\":\"signInGate\",\n" +
                    "    \"extendCol\":\"extendCol\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/AppClientSignin")
    @ResponseBody
    public RespInfo AppClientSignin(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            HttpServletRequest request, BindingResult bindingResult) throws JsonProcessingException {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
        ){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        UserInfo userinfo = userService.getUserInfoByUserId(req.getUserid());
        Employee emp=null;

        int vid = Integer.parseInt(req.getVerifyCode().substring(1));
        int type = Integer.parseInt(req.getVerifyCode().substring(0, 1));
        Visitor vt = new Visitor();
        Appointment ap = null;
        Map<String, Object> maps = new HashMap<String, Object>();
        String company = "";
        if (type == 2) {
            vt = visitorService.getTodayVisitorById(vid);
            if (null == vt) {
                return new RespInfo(106, "verify code invalid");
            }
            if (req.getUserid() != vt.getUserid()) {
                return new RespInfo(103, "inconsistent user id");
            } else if (vt.getPermission() != 1) {
                return new RespInfo(104, "no permission");
            } else if (vt.getVisitdate() != null) {
                maps.put("photoUrl", vt.getVphoto());
                maps.put("name", vt.getVname());
                maps.put("visitType", vt.getVisitType());
                maps.put("appointmentDate", vt.getAppointmentDate().getTime());
                maps.put("empName", vt.getEmpName());
                maps.put("empPhone", vt.getEmpPhone());
                maps.put("phone", vt.getVphone());
                if (null == vt.getSignOutDate()) {
                    maps.put("signOutDate", 0);
                } else {
                    maps.put("signOutDate", vt.getSignOutDate().getTime());
                }
                if (userinfo.getSubAccount() == 0 || vt.getSubaccountId() == 0) {
                    company = userinfo.getCompany();
                } else {
                    SubAccount sa = subAccountService.getSubAccountById(vt.getSubaccountId());
                    company = sa.getCompanyName();
                }
                maps.put("company", company);
                maps.put("peopleNum", vt.getPeopleCount());
                maps.put("vcompany", vt.getVcompany());
                maps.put("vgroup", vt.getVgroup());
                maps.put("remark", vt.getRemark());
                maps.put("vid", vt.getVid());
                return new RespInfo(105, "already sign in", maps);
            }

            long appdate = vt.getAppointmentDate().getTime();
            long time = new Date().getTime();

            if (userinfo.getPreExtendTime() != 0) {
                long preExtendTime = userinfo.getPreExtendTime() * 60000L;
                if ((appdate - time) > preExtendTime) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("appointmentDate", appdate);
                    map.put("preExtendTime", userinfo.getPreExtendTime());
                    return new RespInfo(64, "You are early for your appointment", map);
                }
            }

            if (userinfo.getLatExtendTime() != 0) {
                long latExtendTime = userinfo.getLatExtendTime() * 60000L;
                if ((time - appdate) > latExtendTime) {
                    return new RespInfo(65, "You are already late for your appointment");
                }
            }

            emp =employeeService.getOpenid(vt.getEmpid());
            if (null == emp) {
                return new RespInfo(1, "no employee");
            }

            vt.setVisitdate(new Date());
            vt.setRemark(vt.getRemark());

            if (userinfo.getSubAccount() == 0 || vt.getSubaccountId() == 0) {
                company = userinfo.getCompany();
            } else {
                SubAccount sa = subAccountService.getSubAccountById(ap.getSubaccountId());
                company = sa.getCompanyName();
            }
            vt.setSignInOpName(req.getSignInOpName());
            vt.setSignInGate(req.getSignInGate());

            List<String> extcol = req.getExtendCol();
            if (null != extcol && extcol.size() > 0) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < extcol.size(); i++) {
                    String[] col = extcol.get(i).split("=");
                    map.put(col[0], col[1]);
                }
                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
                String strext = mapper.writeValueAsString(map);
                vt.setExtendCol(strext);
            }

            int i = visitorService.updateSigninByAppClient(vt);
            if (i <= 0) {
                return new RespInfo(1, "insert failed");
            }

            maps.put("vcompany", vt.getVcompany());
            maps.put("remark", vt.getRemark());
        } else if (type == 1) {
            ap = appointmentService.getAppointmentByHash(vid);
            if (null == ap) {
                return new RespInfo(106, "verify code invalid");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String today = sdf.format(new Date());
            String vdate = sdf.format(ap.getVisitDate());

            if (req.getUserid() != ap.getUserid()) {
                return new RespInfo(103, "inconsistent user id");
            } else if (today.equals(vdate)) {
                Visitor rv = visitorService.getTodayVisitorByPhone(ap.getPhone(), ap.getUserid());
                maps.put("photoUrl", ap.getPhotoUrl());
                maps.put("name", ap.getName());
                maps.put("visitType", ap.getVisitType());
                maps.put("appointmentDate", ap.getAppointmentDate().getTime());
                maps.put("empName", ap.getEmpName());
                maps.put("empPhone", ap.getEmpPhone());
                maps.put("phone", ap.getPhone());
                if (userinfo.getSubAccount() == 0 || ap.getSubaccountId() == 0) {
                    company = userinfo.getCompany();
                } else {
                    SubAccount sa = subAccountService.getSubAccountById(ap.getSubaccountId());
                    company = sa.getCompanyName();
                }
                maps.put("company", company);
                maps.put("peopleNum", 1);
                maps.put("vcompany", ap.getVcompany());
                maps.put("remark", ap.getRemark());
                maps.put("vgroup", rv.getVgroup());
                if (null == rv.getSignOutDate()) {
                    maps.put("signOutDate", 0);
                } else {
                    maps.put("signOutDate", rv.getSignOutDate().getTime());
                }
                maps.put("vid", rv.getVid());

                return new RespInfo(105, "already sign in", maps);
            }

            long appdate = ap.getAppointmentDate().getTime();
            long time = new Date().getTime();

            if (userinfo.getPreExtendTime() != 0 && ap.getMid() == 0) {
                long preExtendTime = userinfo.getPreExtendTime() * 60000L;
                if ((appdate - time) > preExtendTime) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("appointmentDate", appdate);
                    map.put("preExtendTime", userinfo.getPreExtendTime());
                    return new RespInfo(64, "You are early for your appointment", map);
                }
            }

            if (userinfo.getLatExtendTime() != 0 && ap.getMid() == 0) {
                long latExtendTime = userinfo.getLatExtendTime() * 60000L;
                if ((time - appdate) > latExtendTime) {
                    return new RespInfo(65, "You are already late for your appointment");
                }
            }

            emp = employeeService.getOpenid(ap.getEmpid());
            if (null == emp) {
                return new RespInfo(1, "no employee");
            }
            ap.setVisitDate(new Date());
            appointmentService.updateAppointmentStatus(ap);

            if (userinfo.getSubAccount() == 0 || vt.getSubaccountId() == 0) {
                company = userinfo.getCompany();
            } else {
                SubAccount sa = subAccountService.getSubAccountById(ap.getSubaccountId());
                company = sa.getCompanyName();
            }

            vt.setEmpid(ap.getEmpid());
            vt.setEmpName(ap.getEmpName());
            vt.setUserid(ap.getUserid());
            vt.setVname(ap.getName());
            vt.setVisitdate(new Date());
            vt.setVphone(ap.getPhone());
            vt.setVisitType(ap.getVisitType());
            vt.setEmpPhone(ap.getEmpPhone());
            vt.setAppointmentDate(ap.getAppointmentDate());
            vt.setPermission(1);
            vt.setSigninType(1);
            vt.setRemark(ap.getRemark());
            vt.setVcompany(ap.getVcompany());
            vt.setSubaccountId(ap.getSubaccountId());
            vt.setCompany(company);
            vt.setSignInOpName(req.getSignInOpName());
            vt.setSignInGate(req.getSignInGate());
            vt.setLeaveTime(ap.getLeaveTime());
            List<String> extcol = req.getExtendCol();
            if (null != extcol && extcol.size() > 0) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < extcol.size(); i++) {
                    String[] col = extcol.get(i).split("=");
                    map.put(col[0], col[1]);
                }
                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
                String strext = mapper.writeValueAsString(map);
                vt.setExtendCol(strext);
            }
            vt.setAppid(ap.getId());
            vt.setvType(ap.getvType());
            visitorService.addApponintmnetVisitor(vt);

            vid = vt.getVid();

            maps.put("vcompany", ap.getVcompany());
            maps.put("remark", ap.getRemark());
        } else {
            return new RespInfo(106, "verify code error");
        }

        List<ExtendVisitor> evlist = extendVisitorService.getBaseExtendVisitor(userinfo.getUserid());

        sendNotify(null,null,userinfo,evlist,vt,emp);


        int count = visitorService.getVisitorCount(userinfo.getUserid());

        maps.put("vcount", count);
        maps.put("photoUrl", vt.getVphoto());
        maps.put("name", vt.getVname());
        maps.put("visitType", vt.getVisitType());
        maps.put("appointmentDate", vt.getAppointmentDate().getTime());
        maps.put("empName", vt.getEmpName());
        maps.put("empPhone", vt.getEmpPhone());
        maps.put("phone", vt.getVphone());
        maps.put("signOutDate", 0);
        maps.put("vgroup", vt.getVgroup());
        maps.put("company", company);
        maps.put("peopleNum", vt.getPeopleCount());
        maps.put("vid", vid);
        return new RespInfo(0, "success", maps);
    }

    @ApiOperation(value = "/getRvQrcodeByPhone 根据供应商手机号获取二维码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
//    @ApiOperationSupport(includeParameters = {"x-coolvisit-token"})
    @RequestMapping(method = RequestMethod.POST, value = "/getRvQrcodeByPhone")
    @ResponseBody
    public RespInfo getRvQrcodeByPhone(
            @RequestBody ResidentVisitor rv,
            HttpServletRequest request, BindingResult bindingResult) throws ParseException {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        Person person = personInfoService.getVisitPersonByOpenid(authToken.getOpenid());
        if (null == person || !authToken.getOpenid().equals(person.getPopenid())){
            return new RespInfo(1, "invalid user");
        }else {
            rv.setPhone(person.getPmobile());
        }

        rv = residentVisitorService.getResidentVisitorByPhone(rv);
        if (null == rv) {
            return new RespInfo(1117, "invalid visitor");
        }
        if (null == rv.getStartDate() || null == rv.getEndDate() || "".equals(rv.getStartDate()) || "".equals(rv.getEndDate())) {
            return new RespInfo(1118, "invalid StartDate or EndDate");
        }

        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("03");
        sb.append("30");
        sb.append("38965874575");
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String date2 = sdf.format(new Date().getTime() + 120000L);
        sb.append(date2);
        sb.append(rv.getRid().substring(1));
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);

        return new RespInfo(0, "success", sb.toString());

    }

    /**
     * aec95919d2632757 468 01 21 13813946371 1804201731 161968
     * 二维码编码方式：
     * 16位：校验码
     * 前三位：468 固定码
     * 两位：01表示员工，02表示访客，03常驻访客不同类型可能编码方式不同
     * 两位：访问类型  21 员工 23 邀请函  25 访客预约 30 常驻访客 40 签到二维码
     * 后11位：手机号
     * 有效期10位：
     * 最后：标识符号员工 id，访客 id
     */
    @ApiOperation(value = "/getQrcode 获取二维码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"openId\":\"oHriHwZNEkGDAov-Rphjv9GyQ1xg\",\n" +
                    "    \"vid\":\"123\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getQrcode")
    @ResponseBody
    public RespInfo getQrcode(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        //7-员工
        if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            //员工
            String strEmpId = authToken.getLoginAccountId();
            int empId = -1;
            try {
                empId = Integer.parseInt(strEmpId);
            }catch (Exception e){
                throw new NullPointerException("invalid token");
            }

            Employee emp = employeeService.getEmployee(empId);
            if (null == emp) {
                return new RespInfo(1119, "invalid employee");
            }
            if (StringUtils.isBlank(emp.getStartDate()) || StringUtils.isBlank(emp.getEndDate())) {
                return new RespInfo(1118, "invalid StartDate or EndDate");
            }
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
            int d1 = Integer.parseInt(emp.getStartDate());
            int d2 = Integer.parseInt(emp.getEndDate());
            int today = Integer.parseInt(sd.format(new Date()));
            if (today >= d1 && today <= d2) {
                StringBuffer sb = new StringBuffer();
                sb.append("468");
                sb.append("01");
                sb.append("21");
                sb.append(emp.getEmpPhone().substring(emp.getEmpPhone().indexOf("1")));
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
                //二维码有效期
                String date2 = sdf.format(new Date().getTime() + 120000L);
                sb.append(date2);
                sb.append(emp.getEmpid());
                String qrcode = sb.toString();
                String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
                sb.insert(0, aes);
                return new RespInfo(0, "success", sb.toString());
            } else {
                return new RespInfo(1113, "invalid date");
            }
        }
        //8-访客
        else if (AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())) {
            Visitor visitor = visitorService.getVisitorById(req.getVid());
            if (null == visitor) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            Person person = personInfoService.getVisitPersonByOpenid(authToken.getOpenid());
            if (null == person && person.getPmobile() != visitor.getVphone()){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }
        //4-访客机 2-前台；校验userid
        else if (AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                || AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                || AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                || AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                || AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        ) {
            Visitor visitor = visitorService.getVisitorById(req.getVid());
            if (null == visitor) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            //校验userid
            if (visitor.getUserid() != authToken.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("02");
        if (AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())) {
            //访客微信
            sb.append("25");
        }else{
            //打印贴纸
            sb.append("40");
        }
        sb.append("28965874589");
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String date2 = sdf.format(new Date().getTime() + 120000L);
        if (AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())) {
            //访客
            sb.append(date2);
        }else{
            //打印贴纸
            Visitor visitor = visitorService.getVisitorById(req.getVid());
            if(visitor == null){
                return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
            }
            int qrcodeConf = visitor.getQrcodeConf();
            if (qrcodeConf == 0) {
                qrcodeConf = 1;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(visitor.getAppointmentDate());
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            ExtendVisitor ev = new ExtendVisitor();
            ev.seteType(visitor.getvType());
            ev.setUserid(visitor.getUserid());
            List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
            String moreDays = "0";
            for (int i = 0; i < extendVisitors.size(); i++) {
                /**
                 * 进门次数约束： moreTimes:0-单次，1-多次
                 */
                if ("moreDays".equals(extendVisitors.get(i).getFieldName())) {
                    moreDays = extendVisitors.get(i).getInputValue();
                }

            }
            //多天有效
            if(!"0".equals(moreDays)) {
                calendar.add(Calendar.DAY_OF_MONTH, qrcodeConf - 1);
            }else{
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
            }

            String format = sdf.format(calendar.getTime());
            sb.append(format);
        }

        sb.append(req.getVid());
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);
        return new RespInfo(0, "success", sb.toString());


    }

    @ApiOperation(value = "/getFpQrcode 访客通过临时token获取签到通行二维码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getFpQrcode")
    @ResponseBody
    public RespInfo getFpQrcode(@RequestBody IvrData req,HttpServletRequest request) throws ParseException {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("02");
        sb.append("25");
        sb.append("28965874589");
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String date2 = sdf.format(new Date().getTime() + 120000L);
        sb.append(date2);
        sb.append(AESUtil.decode(req.getVid(), Constant.AES_KEY));
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);

        return new RespInfo(0, "success", sb.toString());
    }

    @ApiOperation(value = "/getRvQrcodeSec 供应商通过临时token获取签到通行二维码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +

                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRvQrcodeSec")
    @ResponseBody
    public RespInfo getRvQrcodeSec(@RequestBody ResidentVisitor rv,HttpServletRequest request) throws ParseException {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_SUPPLIER.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        rv.setRid(AESUtil.decode(rv.getRid(), Constant.AES_KEY));
        rv = residentVisitorService.getResidentVisitorByRid(rv);
        if (null == rv) {
            return new RespInfo(1117, "invalid visitor");
        }
        if (null == rv.getStartDate() || null == rv.getEndDate() || "".equals(rv.getStartDate()) || "".equals(rv.getEndDate())) {
            return new RespInfo(1118, "invalid StartDate or EndDate");
        }

        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("03");
        sb.append("30");
        sb.append("38965874575");
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String date2 = sdf.format(new Date().getTime() + 120000L);//120000L
        sb.append(date2);
        sb.append(rv.getRid().substring(1));
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);

        return new RespInfo(0, "success", sb.toString());

    }

    /**
     * vtype 23=邀请  25=预约 40=打印贴纸
     * 01 员工  02 访客  03 供应商
     * @param request
     * @param response {"retCode":"0","retMsg":"XXXX"}
     * @throws IOException
     */
    @ApiOperation(value = "/uploadQrcode 二维码通行", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/uploadQrcode")
    public void uploadQrcode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            String vgdecoderesult = body.substring(15, body.indexOf("&&"));
            String devicenumber = body.substring(body.lastIndexOf("devicenumber") + 13);
            if (devicenumber.indexOf("&&") != -1) {
                devicenumber = devicenumber.substring(0, devicenumber.indexOf("&&"));
            }
            uploadQrcodeWorkFlow(response, vgdecoderesult, devicenumber);
        }catch (Exception e){
            response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1112));
        }
    }

    protected void uploadQrcodeWorkFlow(HttpServletResponse response, String vgdecoderesult, String devicenumber) throws IOException {
        //code在bpm文件中定义
        String code = "bpm.checkQrcode";

        //执行流程的入参
        Map<String, Object> context = new HashMap<>();
        context.put("qrcode", vgdecoderesult);
        context.put("devicenumber", devicenumber);
        if(vgdecoderesult.length()>44){
            context.put("id",vgdecoderesult.substring(44));
        }else if(vgdecoderesult.length()<=10){
            //刷ic卡
            code = "bpm.checkICCard";
        }else{

        }

        ErrorEnum resultCode = ErrorEnum.E_2000;
        try {
            ProcessEngine processEngine = ProcessEngineFactory.getProcessEngine();
            Map<String, Object> result = processEngine.execute(code, context);
            System.out.println(result.get("businessKey"));
            resultCode = ErrorEnum.E_0;

            //通行记录
            Visitor vt = (Visitor) result.get("vt");
            Employee employee = (Employee) result.get("employee");
            Equipment equipment = (Equipment) result.get("eq");
            ResidentVisitor rv = (ResidentVisitor) result.get("rv");
            OpendoorInfo odi = new OpendoorInfo();

            odi.setDeviceCode(devicenumber);
            if(vt != null) {
                odi.setUserid(vt.getUserid());
                odi.setVname(vt.getVname());
                odi.setMobile(vt.getVphone());
                odi.setVtype("访客");
                odi.setCompany(vt.getCompany());
                odi.setVid(vt.getVid()+"");
            }else if(employee != null){
                odi.setUserid(employee.getUserid());
                odi.setVname(employee.getEmpName());
                odi.setMobile(employee.getEmpPhone());
                odi.setCompany(employee.getCompanyName());
                odi.setVtype("员工");
            }else if(rv != null){
                //供应商
                odi.setUserid(rv.getUserid());
                odi.setVname(rv.getName());
                odi.setMobile(rv.getPhone());
                odi.setVtype("供应商");
                odi.setCompany(rv.getCompany());
                odi.setVid(rv.getRid()+"");
            }
            if(equipment != null) {
                odi.setDeviceName(equipment.getDeviceName());
                odi.setDirection(equipment.getEnterStatus()==1?"进门":"出门");
            }
            odi.setOpenDate(new Date());
            odi.setOpenStatus("成功");
            opendoorService.addOpendoorInfo(odi);
        }catch (CompileFlowException e){
            if(e.getCause() instanceof ErrorException){
                ErrorException errorException = (ErrorException)e.getCause();
                resultCode = errorException.getErrorEnum();
             }else {
                SysLog.error(e);
            }
        }catch (Exception e){
            SysLog.error(e);
        }

        //异常记录
        if(resultCode.equals(ErrorEnum.E_0)){
            response.getWriter().write("code=0000&&desc=success");
        }else{
            LambdaQueryWrapper<Equipment> lambdaQueryWrapper = Wrappers.lambdaQuery(Equipment.class);
            lambdaQueryWrapper.eq(Equipment::getDeviceQrcode,devicenumber);

//            获取userid
           Equipment equipment= equipmentService.getOne(lambdaQueryWrapper,false);
            OpendoorInfo odi = new OpendoorInfo();
            if(equipment != null) {
                odi.setUserid(equipment.getUserid());
                odi.setDeviceName(equipment.getDeviceName());
                odi.setDirection(equipment.getEnterStatus()==1?"进门":"出门");
            }else{
                odi.setDeviceName("");
            }
            odi.setDeviceCode(devicenumber);
            odi.setVname("");
            odi.setMobile("");

            odi.setOpenDate(new Date());
            odi.setOpenStatus(resultCode.getMsg());
            opendoorService.addOpendoorInfo(odi);

            response.getWriter().write("code=" + resultCode.getCode() + "&&desc=" + resultCode.getMsg());
        }

    }


    protected void uploadQrcode(HttpServletResponse response, String vgdecoderesult, String devicenumber) {
        OpendoorInfo odi = new OpendoorInfo();
        odi.setDeviceCode(devicenumber);
        odi.setDeviceName("");
        odi.setVname("");
        odi.setMobile("");
        odi.setOpenDate(new Date());
        UserInfo userInfo = userService.getUserInfoWithExt(Constant.ACCOUNT);
        if(userInfo != null){
            //设置默认账号
            odi.setUserid(userInfo.getUserid());
        }
        try {
            if (vgdecoderesult.length() < 20) {
                odi.setOpenStatus(ErrorEnum.E_1112.getMsg());
                opendoorService.addOpendoorInfo(odi);
                response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1112));
                return;
            }
            String aes = DigestUtils.sha512Hex(vgdecoderesult.substring(16) + "csjm759").substring(8, 23);
            if (!aes.equals(vgdecoderesult.substring(1, 16))) {
                response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1112));
                odi.setOpenStatus(ErrorEnum.E_1112.getMsg());
                opendoorService.addOpendoorInfo(odi);
                return;
            }
            String etype = vgdecoderesult.substring(19, 21); // 01 员工  02 访客  03 常驻访客
            String vtype = vgdecoderesult.substring(21, 23); // 23 邀请  25 预约 40 打印贴纸二维码
            String expireddate = vgdecoderesult.substring(34, 44);//二维码有效期，针对动态二维码，一般为2分钟
            String strevid = "";
            int evid = 0;
            if (etype.equals("03")) {
                strevid = vgdecoderesult.substring(44);
            } else {
                evid = Integer.parseInt(vgdecoderesult.substring(44));
            }

            if (etype.equals("01")) {
                odi.setVtype("员工");
                //员工
                Employee emp = employeeService.getEmployee(evid);
                if(emp == null){
                    odi.setOpenStatus(ErrorEnum.E_055.getMsg());
                    opendoorService.addOpendoorInfo(odi);
                    response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_055));
                    return;
                }
                odi.setUserid(emp.getUserid());
                odi.setMobile(emp.getEmpPhone());
                odi.setVname(emp.getEmpName());
                if (emp.getSubaccountId() == 0) {
                    UserInfo ui = userService.getBaseUserInfo(emp.getUserid());
                    if(ui != null) {
                        odi.setCompany(ui.getCompany());
                    }
                } else {
                    SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
                    if(sa != null) {
                        odi.setCompany(sa.getCompanyName());
                    }
                }

                //检查是否被授权该门禁
                Map<String, String> map = new HashMap<String, String>();
                map.put("deviceQrcode", devicenumber);
                if(StringUtils.isNotBlank(emp.getEgids())) {
                    map.put("egids", "(" + emp.getEgids() + ")");
                }else {
                    map.put("egids", "(-1)");
                }
                Equipment eq = eGRelationService.getEquipmentByDq(map);
                if (null == eq) {
                    odi.setOpenStatus(ErrorEnum.E_1111.getMsg());
                    opendoorService.addOpendoorInfo(odi);
                    response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1111));
                    return;
                }
                odi.setDeviceCode(eq.getDeviceCode());
                odi.setDeviceName(eq.getDeviceName());
                if (eq.getEnterStatus() == 1) {
                    odi.setDirection("进门");
                } else {
                    odi.setDirection("出门");
                }



                //检查员工有效期,员工二维码生成时做了检查，在此不判断
                response.getWriter().write("code=0000&&desc=success");
                odi.setOpenStatus("成功");
                opendoorService.addOpendoorInfo(odi);
                return;

            } else if (etype.equals("02")) {
                //访客
                odi.setVtype("访客");
                if (vtype.equals("23")) {
                    //邀请码
                    Appointment app = appointmentService.getAppointmentbyId(evid);
                    UserInfo ui = userService.getUserInfo(app.getUserid());

                    //邀请函有效期限制
//                    if (checkAppointmentVisitorLimit(response, app, ui,odi)) return;
                    odi.setUserid(app.getUserid());
                    odi.setMobile(app.getPhone());
                    odi.setVname(app.getName());
                    odi.setCompany(app.getVcompany());
                    odi.setVid("a"+app.getId());

                    //检查是否为授权通行门禁设备
                    Equipment eq = checkEquipmentAuth(response, devicenumber, ui.getUserid(),app.getGid()+"");
                    if (eq == null) return;
                    odi.setDeviceCode(eq.getDeviceCode());
                    odi.setDeviceName(eq.getDeviceName());
                    if (eq.getEnterStatus() == 1) {
                        odi.setDirection("进门");
                    } else {
                        odi.setDirection("出门");
                    }

//                    if (checkTimesAndAccessType(response, ui, eq, app.getvType(), app.getPhone(),odi)) return;

                    //结束拜访后不允许再进
//                    if (checkLeaveStatus(response, app.getLeaveTime(),eq,odi)) return;

                    Visitor v=new Visitor();
                    v.setAppid(app.getId());
                    //v值不确保不为null，使用需要先检查
                    v = visitorService.getVisitorByAppId(v);

                    //检查是否已签到
//                    if (checkSigninStatus(response,v,odi)) return;

                    //是否允许刷邀请码开门
//                    if(Constant.AllowedAppCodeAccess == null
//                            || Constant.AllowedAppCodeAccess.equals("0")){
//                        odi.setOpenStatus(ErrorEnum.E_1127.getMsg());
//                        opendoorService.addOpendoorInfo(odi);
//                        response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1127));
//                        return ;
//                    }


                    //员工审批流程开启且访客未补充信息
//                    if (checkSigninAppointmentQrcode(app)) {
//                        response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1129));
//                        return;
//                    }

                    response.getWriter().write("code=0000&&desc=success");
                    //添加通行记录
                    odi.setOpenStatus("成功");
                    opendoorService.addOpendoorInfo(odi);

                    app.setOpenCount(app.getOpenCount() + 1);
                    appointmentService.updateOpenCount(app);


                    //如果是单进单出解除刷脸对应功能
//                    sendVisitorPassMsg(response, ui, eq, app.getvType(), app.getPhone(),"a"+app.getId());


                    //邀请码自动签到
//                    autoSigninAppointmentQrcode(app, eq);

                    //根据配置自动签出
//                    autoSignout(odi, v);
                    return;

                } else if (vtype.equals("25") || vtype.equals("40")) {
                    //预约/签到二维码
                    Visitor v = visitorService.getVisitorById(evid);
                    if (null == v || null == v.getAppointmentDate()) {
                        odi.setOpenStatus(ErrorEnum.E_1123.getMsg());
                        opendoorService.addOpendoorInfo(odi);
                        response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1123));
                        return;
                    }
                    odi.setUserid(v.getUserid());
                    odi.setMobile(v.getVphone());
                    odi.setVname(v.getVname());
                    odi.setCompany(v.getVcompany());
                    odi.setVid("v" + v.getVid());

                    UserInfo ui = userService.getUserInfo(v.getUserid());
                    //检查是否为授权通行门禁设备
                    Equipment eq = checkEquipmentAuth(response, devicenumber, ui.getUserid(),v.getGid());
                    if (eq == null) return;
                    odi.setDeviceCode(eq.getDeviceCode());
                    odi.setDeviceName(eq.getDeviceName());
                    if (eq.getEnterStatus() == 1) {
                        odi.setDirection("进门");
                    } else {
                        odi.setDirection("出门");
                    }


                    if (v.getAppid() == 0) {
                        //预约访客
                        /**
                         * 二维码有效期判断,fm检查代码感觉这段代码没什么用
                         */
//                        String date = sdf.format(v.getAppointmentDate());
//                        String printDate = "20" + expireddate.substring(0, 6);
//                        if (!date.equals(printDate)) {
//                            response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1113));
//                            return;
//                        }

//                        if (checkVisitorAccessTime(response, v.getAppointmentDate(), ui,odi)) return;
                    }else {

                        //邀请访客
                        Appointment app = appointmentService.getAppointmentbyId(v.getAppid());
                        //邀请函有效期限制
//                        if (checkAppointmentVisitorLimit(response, app, ui,odi)) return;
                    }




//                    if (checkTimesAndAccessType(response, ui, eq, v.getvType(), v.getVphone(),odi)) return;

                    //检查是否已签到
//                    if (checkSigninStatus(response, v,odi)) return;

                    //结束拜访后不允许再进
//                    if (checkLeaveStatus(response, v.getLeaveTime(),eq,odi)) return;

                    //检查是否允许一次签到多天进出
//                    if (checkMoreDays(response,ui,v.getvType(), expireddate,odi)) return;

                    //是否允许刷邀请码开门
                    if((Constant.AllowedAppCodeAccess == null
                            || Constant.AllowedAppCodeAccess.equals("0"))
                    && vtype.equals("25")){
                        odi.setOpenStatus(ErrorEnum.E_1127.getMsg());
                        opendoorService.addOpendoorInfo(odi);
                        response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1127));
                        return ;
                    }

                    //添加通行记录
                    odi.setOpenStatus("成功");
                    opendoorService.addOpendoorInfo(odi);

                    //自动签到
                    if(eq.getEnterStatus() == 1
                            && v.getVisitdate() == null
                            && Constant.AutoSignin != null
                            && Constant.AutoSignin.equals("1")) {
                        v.setVisitdate(new Date());
                        v.setSignInOpName(odi.getDeviceName());
                        v.setSignInGate(odi.getGname());
                        int updateRet = visitorService.updateSigninByAppClient(v);

                        //通知被访人
                        if (updateRet > 0) {
                            Employee emp = employeeService.getEmployee(v.getEmpid());
                            List<ExtendVisitor> evlist = extendVisitorService.getBaseExtendVisitor(userInfo.getUserid());
//                            SendNodifytoDefault sntd = new SendNodifytoDefault(userInfo, v, evlist, emp);
//                            Thread t1 = new Thread(sntd);
//                            t1.start();
                        } else {
                            SysLog.error("updateSigninByAppClient failed:"+v);
                        }
                    }

                    //单次进出情况通知senselink访客已进入
//                    if (v.getAppid() == 0){
//                        sendVisitorPassMsg(response, ui, eq, v.getvType(), v.getVphone(), "v" + v.getVid());
//                    }else{
//                        sendVisitorPassMsg(response, ui, eq, v.getvType(), v.getVphone(), "a" + v.getAppid());
//                    }

                    //根据配置自动签出
//                    autoSignout(odi, v);

                    response.getWriter().write("code=0000&&desc=success");

                    return;

                }
            } else if (etype.equals("03")) {
                //供应商
                ResidentVisitor rv = new ResidentVisitor();
                rv.setUserid(0);
                rv.setRid(strevid);
                rv = residentVisitorService.getResidentVisitorByRid(rv);
                if(rv.getRstatus()!=1) {
                    odi.setOpenStatus(ErrorEnum.E_1121.getMsg());
                    opendoorService.addOpendoorInfo(odi);
                	response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1121));
                    return;
                }

                //检查供应商授权日期
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                int startdate = Integer.parseInt(rv.getStartDate().replaceAll("-", ""));
                int now = Integer.parseInt(sdf2.format(new Date()));
                int enddate = Integer.parseInt(rv.getEndDate().replaceAll("-", ""));
                if (now < startdate || now > enddate) {
                    odi.setOpenStatus(ErrorEnum.E_1115.getMsg());
                    opendoorService.addOpendoorInfo(odi);
                    response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1115));
                    return;
                }
                odi.setUserid(rv.getUserid());
                odi.setVname(rv.getName());
                odi.setCompany(rv.getCompany());
                odi.setMobile(rv.getPhone());
                odi.setVtype("供应商");

//                EquipmentGroup eg = new EquipmentGroup();
//                eg.setUserid(rv.getUserid());
//                eg.setReqEtype("(0,2)");
//                eg.setEtype(2);
//                List<EquipmentGroup> eglist = equipmentGroupService.getEquipmentGroupByUserid(eg);
//                StringBuffer sb = new StringBuffer();
//                sb.append("");
//                for (int s = 0; s < eglist.size(); s++) {
//                    if (eglist.size() - 1 == s) {
//                        sb = sb.append(eglist.get(s).getEgid());
//                    } else {
//                        sb = sb.append(eglist.get(s).getEgid() + ",");
//                    }
//                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("deviceQrcode", devicenumber);
                if(StringUtils.isNotBlank(rv.getEgids())) {
                	map.put("egids", "(" + rv.getEgids() + ")");
                }else {
                	map.put("egids", "(-1)");
                }
                Equipment eq = eGRelationService.getEquipmentByDq(map);
                if(eq == null){
                    odi.setOpenStatus(ErrorEnum.E_1111.getMsg());
                    opendoorService.addOpendoorInfo(odi);
                    response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1111));
                    return;
                }
                odi.setDeviceCode(eq.getDeviceCode());
                odi.setDeviceName(eq.getDeviceName());
                if (eq.getEnterStatus() == 1) {
                    odi.setDirection("进门");
                } else {
                    odi.setDirection("出门");
                }


                odi.setOpenStatus("成功");
                opendoorService.addOpendoorInfo(odi);

                response.getWriter().write("code=0000&&desc=success");
                return;
            }

        } catch (IOException e) {
            //throw new ApplicationException("IOException in populateWithJSON", e);
            try {
                odi.setOpenStatus(ErrorEnum.E_1110.getMsg());
                opendoorService.addOpendoorInfo(odi);
                response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1110));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return;
        }
    }


    @ApiOperation(value = "/getAppointmnetByBusinessKey 根据审批流businessKey获取访客信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"businessKey\":\"a123456/v222\",\n"+
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAppointmnetByBusinessKey")
    @ResponseBody
    public RespInfo getAppointmnetByBusinessKey(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated Camunda req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        int empid = Integer.parseInt(authToken.getLoginAccountId());
        Employee employee = employeeService.getEmployee(empid);
        if(employee == null){
            return new RespInfo(ErrorEnum.E_608.getCode(), ErrorEnum.E_608.getMsg());
        }
        if ((!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != employee.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(req.getBusinessKey().startsWith("a")){
            Appointment app = new Appointment();
            app.setAgroup(req.getBusinessKey().substring(1));
            app.setUserid(authToken.getUserid());
            List<Appointment> appointmnetByAgroup = appointmentService.getAppointmnetByAgroup(app);
            List<Visitor> visitorList = new ArrayList<Visitor>();
            for(Appointment appointment:appointmnetByAgroup){
                Visitor visitor = BeanUtils.appointmentToVisitor(appointment);
                visitor.setEncryption(AESUtil.encode(visitor.getAppid() + "", Constant.AES_KEY));
                visitorList.add(visitor);
            }
            return new RespInfo(0, "success", visitorList);
        }else{
            Visitor app = new Visitor();
            app.setVgroup(Integer.parseInt(req.getBusinessKey().substring(1)));
            app.setUserid(authToken.getUserid());
            List<Visitor> appointmnetByAgroup = visitorService.getVisitorByVgroup(app.getVgroup()+"");
            for (int i = 0; i < appointmnetByAgroup.size(); i++) {
                appointmnetByAgroup.get(i).setEncryption(AESUtil.encode("v"+appointmnetByAgroup.get(i).getVid() + "", Constant.AES_KEY));
            }
            return new RespInfo(0, "success", appointmnetByAgroup);
        }
    }



    /**
     * 获取扫码设备信息
     * @param response
     * @param devicenumber
     * @param userid
     * @param gids 授权的gid数据
     * @return null 对应设备未授权，或者没有该设备码的设备
     * @throws IOException
     */
    private Equipment checkEquipmentAuth(HttpServletResponse response, String devicenumber, int userid,String gids) throws IOException {

        EquipmentGroup eg = new EquipmentGroup();
        eg.setUserid(userid);
        eg.setReqEtype("(0,2)");
        eg.setEtype(2);
        eg.setGids(gids);
        List<EquipmentGroup> eglist = equipmentGroupService.getEquipmentGroupByUserid(eg);
        StringBuffer sb = new StringBuffer();
        sb.append("");
        for (int s = 0; s < eglist.size(); s++) {
            if(eglist.get(s).getStatus() == 0){
                continue;
            }
            if (eglist.size() - 1 == s) {
                sb = sb.append(eglist.get(s).getEgid());
            } else {
                sb = sb.append(eglist.get(s).getEgid() + ",");
            }
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("deviceQrcode", devicenumber);
        if(StringUtils.isNotBlank(sb.toString())) {
            //授权的门岗
            map.put("egids", "(" + sb.toString() + ")");
        }else {
            map.put("egids", "(-1)");
        }

        Equipment eq = eGRelationService.getEquipmentByDq(map);
        if (null == eq) {
            response.getWriter().write(getQrcodeReaderErrorResp(ErrorEnum.E_1111));
            return null;
        }
        return eq;
    }

    public String getQrcodeReaderErrorResp(ErrorEnum errorEnum){
       return  "code="+errorEnum.getCode()+"&&desc="+errorEnum.getMsg();
    }

    @ApiOperation(value = "/getResidentVisitorByRid 根据rid获取常住访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rid\":\"R12345678910\",\n" +
                    "    \"userid\":\"12345678910\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getResidentVisitorByRid")
    @ResponseBody
    public RespInfo getResidentVisitorByRid(@RequestBody ResidentVisitor rv, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if(AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())){

        }
        else if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        //校验访客
        rv.setRid(rv.getRid().substring(1));
        rv = residentVisitorService.getResidentVisitorByRid(rv);
        if(null == rv){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())){
            Person personByOpenid = personInfoService.getVisitPersonByOpenid(authToken.getOpenid());
            if (null == personByOpenid) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            if(!personByOpenid.getPmobile().equals(rv.getPhone())){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

        }
        return new RespInfo(0, "success", rv);
    }

    @ApiOperation(value = "/getResidentVisitorBySecRid 根据SecRid获取常住访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getResidentVisitorBySecRid")
    @ResponseBody
    public RespInfo getResidentVisitorBySecRid(@RequestBody ResidentVisitor rv, HttpServletRequest request) {
        rv.setRid(AESUtil.decode(rv.getRid(), Constant.AES_KEY));

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        rv = residentVisitorService.getResidentVisitorByRid(rv);
        VisitorType visType = new VisitorType();
        visType.setCategory(-1);
        visType.setUserid(authToken.getUserid());

        //通过tid查询用户访客类型，以及有效期周期
        List<VisitorType> vtList = visitorTypeService.getVisitorType(visType);
        if (vtList.size() > 0) {
            rv.setQid(vtList.get(0).getQid());
            rv.setTid(vtList.get(0).getTid());
        }

        return new RespInfo(0, "success", rv);
    }

    @ApiOperation(value = "/show 邀请函", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"secid\":\"sadfasfasdfasfwzZfsaefsdgfdsgs\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/show")
    @ResponseBody
    public RespInfo show(
            @ApiParam(value = "Appointment 访客邀请Bean", required = true) @Validated @RequestBody  Appointment app, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        Map<String, Object> map = new HashMap<String, Object>();
        int aid = Integer.parseInt(AESUtil.decode(app.getSecid(), Constant.AES_KEY));
        app = appointmentService.getAppointmentbyId(aid);
        if (null == app) {
            return new RespInfo(57, "no records");
        }
        UserInfo userinfo = userService.getUserInfo(app.getUserid());
        map.put("face", userinfo.getFaceScaner());
        SimpleDateFormat time = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        String inviteContent = "";
        VisitorType visType = new VisitorType();
        visType.setTid(app.getTid());
        visType.setUserid(app.getUserid());
        visType = visitorTypeService.getVisitorTypeByTid(visType);
        if (userinfo.getSubAccount() == 1 && app.getSubaccountId() != 0) {
            SubAccount sa = subAccountService.getSubAccountById(app.getSubaccountId());
            inviteContent = app.getInviteContent().replaceAll("\\{visitor\\}", app.getName());
            map.put("companyProfile", sa.getCompanyProfile());
            map.put("meetingPoint", sa.getMeetingPoint());
            String[] companyNames = sa.getCompanyName().split("#");
            if (companyNames.length>1){
                map.put("companyName_zh", companyNames[0]);
                map.put("companyName_En",  companyNames[1]);
            }else {
                map.put("companyName_zh", companyNames[0]);
            }
        } else {
            inviteContent = app.getInviteContent().replaceAll("\\{visitor\\}", app.getName())
                    .replaceAll("\\{company\\}", userinfo.getCardText());
            String companyProfile = app.getCompanyProfile().replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt/;script");
            map.put("companyProfile", companyProfile);
        }

        inviteContent = inviteContent.replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt;/script");
        String address = app.getAddress().replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt;/script");
        String traffic = app.getTraffic().replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt;/script");
        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("02");
        sb.append("23");
        sb.append("19875485522");//可以填手机号，此处未使用
        //二维码有效期
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String date2 = sdf.format(new Date().getTime() + 120000L);
        sb.append(date2);
        sb.append(app.getId());
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);
        String secureProtocol = HtmlUtils.htmlUnescape(userinfo.getSecureProtocol());
        map.put("longitude", app.getLongitude());
        map.put("latitude", app.getLatitude());
        map.put("traffic", traffic);
        map.put("address", address);
        map.put("inviteContent", inviteContent);
        map.put("appointmentDate", time.format(app.getAppointmentDate()));
        map.put("status", app.getStatus());
        map.put("scaner", userinfo.getScaner());
        map.put("secureProtocol", secureProtocol);
        map.put("remark", app.getRemark());
        map.put("id", app.getId());
        map.put("qrcode", sb.toString());
        map.put("exam", userinfo.getQuestionnaireSwitch());
        map.put("appExtendCol", app.getAppExtendCol());
        map.put("photo", app.getPhotoUrl());
        map.put("userid", app.getUserid());
        map.put("visitType",app.getVisitType());
        map.put("qrcodeConf",app.getQrcodeConf());
        if (null != visType && org.apache.commons.lang3.StringUtils.isNotBlank(visType.getQid())) {
            map.put("question", 1);
        } else {
            map.put("question", 0);
        }
        return new RespInfo(0, "success", map);
    }

    @ApiOperation(value = "/bus 邀请函", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"secid\":\"sadfasfasdfasfwzZfsaefsdgfdsgs\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/bus")
    @ResponseBody
    public RespInfo bus(
            @ApiParam(value = "Appointment 访客邀请Bean", required = true) @Validated @RequestBody  Appointment app, BindingResult result) {
        Map<String, Object> model = new HashMap<String, Object>();
        String appid = AESUtil.decode(app.getSecid(), Constant.AES_KEY);
        if(appid.startsWith("v")){
            int aid = Integer.parseInt(appid.substring(1));
            Visitor v=visitorService.getVisitorById(aid);
            if(v == null){
                return new RespInfo(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
            }
            Employee emp = employeeService.getEmployee(v.getEmpid());
            if(emp == null){
                return new RespInfo(ErrorEnum.E_001.getCode(),ErrorEnum.E_001.getMsg());
            }
            Usertemplate ut=new Usertemplate();
            ut.setUserid(emp.getUserid());
            ut.setTemplateType(v.getVisitType());
            ut.setGid(Integer.parseInt(v.getGid()));
            //TODO 目前采用了默认模板，多企业服务情况下考虑使用企业模板
            Usertemplate usertemp=appointmentService.getUsertemplate(ut);
            if(null==usertemp){
                return new RespInfo(ErrorEnum.E_059.getCode(),ErrorEnum.E_059.getMsg());
            }
            app.setUserid(emp.getUserid());
            app.setEmpEmail(emp.getEmpEmail());
            app.setEmpName(emp.getEmpName());
            app.setEmpPhone(emp.getEmpPhone());
            app.setAddress(usertemp.getAddress());
            app.setCompanyProfile(usertemp.getCompanyProfile());
            app.setInviteContent(usertemp.getInviteContent());
            app.setLatitude(usertemp.getLatitude());
            app.setLongitude(usertemp.getLongitude());
            app.setTraffic(usertemp.getTraffic());
            app.setPhone(v.getVphone());
            app.setVisitType(v.getVisitType());
            app.setAppointmentDate(v.getAppointmentDate());
            app.setEmpid(emp.getEmpid());
            app.setCompany(v.getCompany());
            app.setvType(v.getvType());
            app.setTid(v.getTid());
            app.setPlateNum(v.getPlateNum());
            app.setCardId(v.getCardId());
            app.setVcompany(v.getVcompany());
            app.setQrcodeConf(1);
            app.setQrcodeType(0);
            app.setStatus(v.getVisitdate()==null?0:1);
            app.setRemark(v.getRemark());
            app.setAppExtendCol(v.getExtendCol());
            app.setName(v.getVname());
            app.setId(v.getVid());
            app.setPermission(v.getPermission());
            app.setStatus(v.getStatus());
            app.setHealthDeclaration(v.getHealthDeclaration());
        }else {
            int aid = Integer.parseInt(AESUtil.decode(app.getSecid(), Constant.AES_KEY));
            app = appointmentService.getAppointmentbyId(aid);
            if (null == app) {
                return new RespInfo(ErrorEnum.E_059.getCode(),ErrorEnum.E_059.getMsg());
            }
        }
        UserInfo userinfo = userService.getUserInfo(app.getUserid());
        model.put("face", userinfo.getFaceScaner());

        // SimpleDateFormat time=new SimpleDateFormat("yyyy年MM月dd日  h:mm a",Locale.US);

        SimpleDateFormat time = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");

        String inviteContent = "";

        if (userinfo.getSubAccount() == 1 && app.getSubaccountId() != 0 && app.getInviteContent() != null) {
            SubAccount sa = subAccountService.getSubAccountById(app.getSubaccountId());
            inviteContent = app.getInviteContent().replaceAll("\\{visitor\\}", app.getName())
//                    .replaceAll("\\{company\\}", sa.getCompanyName())
                    .replaceAll("\\{empid\\}", app.getEmpName());

            String[] companyNames = sa.getCompanyName().split("#");
            if (companyNames.length>1){
                model.put("companyName_zh", companyNames[0]);
                model.put("companyName_En",  companyNames[1]);
            }else {
                model.put("companyName_zh", companyNames[0]);
            }

            model.put("companyProfile", sa.getCompanyProfile());
            model.put("meetingPoint", sa.getMeetingPoint());
        } else if(app.getInviteContent() != null){
            inviteContent = app.getInviteContent().replaceAll("\\{visitor\\}", app.getName())
                    .replaceAll("\\{company\\}", userinfo.getCardText())
                    .replaceAll("\\{empid\\}", app.getEmpName());
            String companyProfile = app.getCompanyProfile().replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt/;script");
            model.put("companyProfile", companyProfile);
        }

        inviteContent = inviteContent.replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt;/script");

        String address = "";
        if(app.getAddress() != null) {
            address = app.getAddress().replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt;/script");
        }
        String traffic ="";
        if(traffic != null) {
            traffic = app.getTraffic().replaceAll("\\\\", "").replaceAll("\"", "\\\\\\\"").replaceAll("(?i)<script", "&lt;script").replaceAll("(?i)</script", "&lt;/script");
        }

        VisitorType visType = new VisitorType();
        visType.setTid(app.getTid());
        visType.setUserid(app.getUserid());

        visType = visitorTypeService.getVisitorTypeByTid(visType);

        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("02");
        if(appid.startsWith("v")){
            sb.append("25");
        }else {
            sb.append("23");
        }
        sb.append("19875485522");
        //二维码有效期
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        Long Expired = new Date().getTime() + 120000L;
        String date2 = sdf.format(Expired);
        sb.append(date2);
        sb.append(app.getId());
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);
        String secureProtocol = HtmlUtils.htmlUnescape(userinfo.getSecureProtocol());

        model.put("longitude", app.getLongitude());
        model.put("latitude", app.getLatitude());
        model.put("traffic", traffic);
        model.put("address", address);
        model.put("inviteContent", inviteContent);
        model.put("appointmentDate", time.format(app.getAppointmentDate()));
        model.put("status", app.getStatus());
        model.put("scaner", userinfo.getScaner());
        model.put("remark", app.getRemark());
        model.put("visitType", app.getVisitType());
        model.put("secureProtocol", secureProtocol);
        model.put("id", app.getId());
        model.put("qrcode", sb.toString());
        model.put("exam", userinfo.getQuestionnaireSwitch());
        model.put("appExtendCol", app.getAppExtendCol());
        model.put("photo", app.getPhotoUrl());
        model.put("userid", app.getUserid());
        model.put("visitType",app.getVisitType());
        model.put("qrcodeConf",app.getQrcodeConf());
        model.put("vType",app.getvType());
        model.put("permission",app.getPermission());
        model.put("appointmenProcessSwitch",userinfo.getAppointmenProcessSwitch());
        model.put("tid",app.getTid());
        model.put("gid",app.getGid());
        model.put("appointmentDateTamp",app.getAppointmentDate().getTime());
        model.put("signinType",appid.startsWith("v")?2:1);
        model.put("healthDeclaration",app.getHealthDeclaration());
        //二维码过期时间
        model.put("qrcodeExpired",Expired);
        if (null != visType && org.apache.commons.lang3.StringUtils.isNotBlank(visType.getQid())) {
            model.put("question", 1);
        } else {
            model.put("question", 0);
        }
        return new RespInfo(0, "success", model);
    }


    @ApiOperation(value = "/UpdatePhoto 现场签到", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"userid\",\n" +
                    "    \"photoUrl\":\"photoUrl\",\n" +
                    "    \"name\":\"name\",\n" +
                    "    \"email\":\"email\",\n" +
                    "    \"phone\":\"phone\",\n" +
                    "    \"visitType\":\"visitType\",\n" +
                    "    \"remark\":\"remark\",\n" +
                    "    \"vcompany\":\"vcompany\",\n" +
                    "    \"peopleCount\":\"peopleCount\",\n" +
                    "    \"empid\":\"empid\",\n" +
                    "    \"tid\":\"tid\",\n" +
                    "    \"vType\":\"vType\",\n" +
                    "    \"plateNum\":\"plateNum\",\n" +
                    "    \"meetingPoint\":\"meetingPoint\",\n" +
                    "    \"sex\":\"sex\",\n" +
                    "    \"floors\":\"floors\",\n" +
                    "    \"extendCol\":\"[\"name=jack\",\"access=1\"]\",\n" +
                    "    \"memberName\":\"memberName\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdatePhoto")
    @ResponseBody
    public RespInfo UpdatePhoto(@RequestBody RequestVisit req, HttpServletRequest request) throws JsonProcessingException {
        int empid = req.getEmpid();
        Employee emp = employeeService.getOpenid(empid);
        if (null == emp) {
            return new RespInfo(1, "no employee");
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (( !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != emp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        try {
            //检查员工状态
            visitorService.checkEmployeeTask(emp.getEmpid() + "");
        }catch (ErrorException e){
            return new RespInfo(e.getErrorEnum().getCode(), e.getErrorEnum().getMsg());
        }

        try {
            String photoUrl = req.getPhotoUrl();
            String name = req.getName();
            String email = req.getEmail();
            String phone = req.getPhone();
            String visitType = req.getVisitType();
            String remark = req.getRemark();
            String vcompany = req.getVcompany();
            int peopleCount = req.getPeopleCount();
            String company = "";
            int tid = req.getTid();
            String vType = req.getvType();
            String plateNum = req.getPlateNum();
            String meetingPoint = req.getMeetingPoint();
            String sex = req.getSex();
            String floors = req.getFloors();
            JsonNode rootNode = null;
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);

            UserInfo userinfo = userService.getUserInfo(emp.getUserid());


            List<String> extcol = req.getExtendCol();
            String strext = "";

            if (null != extcol && extcol.size() > 0) {
                Map<String, String> map = new HashMap<String, String>();
                for (int a = 0; a < extcol.size(); a++) {
                    String[] col = extcol.get(a).split("=");
                    map.put(col[0], col[1]);
                }
                if (StringUtils.isBlank(map.get("access"))) {
                    //获取默认门禁
                    String access = visitorService.getDefaultAccess(userinfo, req.getGid(), emp.getEmpid());
                    map.put("access", access);
                }
                strext = mapper.writeValueAsString(map);
            }


            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
            int d1 = Integer.parseInt(emp.getStartDate());
            int d2 = Integer.parseInt(emp.getEndDate());
            int today = Integer.parseInt(sd.format(new Date()));
            if (today < d1 || today > d2) {
                return new RespInfo(1113, "employee invalid date");
            }

            Person p = personInfoService.getInvitePersonByPhone(emp.getEmpPhone());
            if (null != p && null != p.getPopenid() && !"".equals(p.getPopenid())) {
                emp.setOpenid(p.getPopenid());
            }

            Map<String, String> bmap = new HashMap<String, String>();


            List<Department> deptByEmpid = departmentService.getDeptByEmpid(empid, userinfo.getUserid());
            int empdeptid = 0;
            if (!deptByEmpid.isEmpty()){
                empdeptid = deptByEmpid.get(0).getDeptid();
            }
            if (userinfo.getBlackListSwitch() == 1) {
                List<Blacklist> blList = new ArrayList<Blacklist>();
                Blacklist bl = new Blacklist();
                bl.setUserid(userinfo.getUserid());
                bl.setPhone(req.getPhone());
                if (req.getCard() != null) {
                    bl.setCredentialNo(req.getCard().getCardId());
                }

                String sid = emp.getSubaccountId() + "";

                if (StringUtils.isNotBlank(bl.getPhone()) || StringUtils.isNotBlank(req.getCard().getCardId())) {
                    blList = blacklistService.checkBlacklist(bl);

                    if (blList.size() > 0 && StringUtils.isNotBlank(blList.get(0).getSids())) {
                        String sids[] = blList.get(0).getSids().split(",");
                        boolean b = false;
                        for (int t = 0; t < sids.length; t++) {
                            if (sid.equals(sids[t])) {
                                b = true;
                            }
                        }

                        if (!b || sids.length > 1) {
                            bmap.put(req.getPhone(), blList.get(0).getSname());
                            if (!b) {
                                blList.clear();
                            }
                        }
                    }

                }

                if (StringUtils.isNotBlank(req.getMemberName())) {
                    try {
                        rootNode = mapper.readValue(req.getMemberName(), JsonNode.class);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Iterator<JsonNode> it = rootNode.iterator();
                    Blacklist bli = new Blacklist();
                    while (it.hasNext()) {
                        JsonNode jn = it.next();
                        bli.setUserid(userinfo.getUserid());
                        bli.setPhone(jn.path("mobile").asText());
                        bli.setCredentialNo(jn.path("cardId").asText());
                        if (emp.getSubaccountId() != 0) {
                            bli.setSids(emp.getSubaccountId() + "");
                        }
                        if (StringUtils.isNotBlank(bli.getPhone())) {
                            List<Blacklist> blist = blacklistService.checkBlacklist(bli);
                            if (blist.size() > 0 && StringUtils.isNotBlank(blist.get(0).getSids())) {
                                String sids[] = blist.get(0).getSids().split(",");
                                boolean b = false;
                                for (int t = 0; t < sids.length; t++) {
                                    if (sid.equals(sids[t])) {
                                        b = true;
                                    }
                                }

                                if (!b || sids.length > 1) {
                                    bmap.put(jn.path("mobile").asText(), blist.get(0).getSname());
                                }

                                if (b) {
                                    blList.add(blist.get(0));
                                }
                            } else if (blist.size() > 0) {
                                blList.add(blist.get(0));
                            }
                        }
                    }
                }

                if (blList.size() > 0) {
                    return new RespInfo(66, "user was added to the blacklist", blList);
                }
            }

            Date d = new Date();

            Visitor vt = new Visitor();
            vt.setEmpid(empid);
            vt.setEmpdeptid(empdeptid);
            vt.setEmpName(emp.getEmpName());
            vt.setUserid(userinfo.getUserid());
            vt.setVphoto(photoUrl);
            vt.setVname(name);
            vt.setVemail(email);
            vt.setExtendCol(strext);
            vt.setVphone(phone);
            vt.setRemark(remark);
            vt.setVcompany(vcompany);
            vt.setSignInOpName(req.getSignInOpName());
            vt.setSignInGate(req.getSignInGate());
            vt.setAppointmentDate(d);
            vt.setGid(req.getGid() + "");
            vt.setvType(vType);
            vt.setTid(tid);
            vt.setMeetingPoint(meetingPoint);
            vt.setPlateNum(plateNum);
            vt.setSex(sex);
            vt.setCardNo(req.getCardNo());
            vt.setClientNo(req.getClientNo());
            vt.setPermissionName(emp.getEmpName());
            vt.setFloors(floors);
            vt.setVisitdate(new Date());
            vt.setQrcodeConf(1);
            vt.setCardId(req.getCardId());
            vt.setPermission(1);

            if (bmap.containsKey(req.getPhone())) {
                vt.setbCompany(bmap.get(req.getPhone()));
            }

            if (null != emp.getEmpPhone() && !"".equals(emp.getEmpPhone()) && emp.getEmpPhone().length() == 11) {
                vt.setEmpPhone(emp.getEmpPhone());
            }
            vt.setVisitType(visitType);
            vt.setPeopleCount(peopleCount);
            vt.setSubaccountId(emp.getSubaccountId());
            if (userinfo.getSubAccount() == 0 || emp.getSubaccountId() == 0) {
                company = userinfo.getCompany();
            } else {
                SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
                company = sa.getCompanyName();
            }
            vt.setCompany(company);
            // collect id card info
//            if (req.getCard() != null) {
//                if (IDCardValidateUtil.verifyIDCard(req.getCard().getCardId())) {
//                    IDCard cardData = cardService.getById(req.getCard().getCardId());
//                    if (cardData == null) {
//                        cardService.add(req.getCard());
//                        if (null == photoUrl || "".equals(photoUrl)) {
//                            vt.setVphoto(req.getCard().getImage());
//                        }
//                    } else {
//                        if (null == photoUrl || "".equals(photoUrl)) {
//                            vt.setVphoto(cardData.getImage());
//                        }
//                    }
//                    vt.setCardId(req.getCard().getCardId());
//
//                }
//            }

            String mnames = "";
            if (StringUtils.isNotBlank(req.getMemberName())) {
                Iterator<JsonNode> it = rootNode.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    if ("".equals(mnames)) {
                        mnames = jn.path("name").asText();
                    } else {
                        mnames = mnames + "," + jn.path("name").asText();
                    }
                }
                vt.setMemberName(mnames);
            }

            vt.setSignOutOpName(req.getSignOutOpName());
            req.setSubaccountId(vt.getSubaccountId());
            VisitorChart vc = visitorService.getVisitSaCountByVphone(req);
            vt.setVisitorCount(Integer.parseInt(vc.getCount()));

            // TODO: 2020/7/2 判断是否周末、节假日、梯控、调休日时间拜访
            String weekendPass = "";
            String holidayPass = "";
            String scPass = "";
            String daysOffPass = "";
            if (StringUtils.isNotBlank(floors)) {
                String[] f = floors.split(",");
                int s = passRuleService.getSendCardStatus(userinfo.getUserid(), d, f);
                scPass = s == 1 ? "是" : "否";
            }
            if (StringUtils.isNotBlank(req.getGid())) {
                Boolean w = passRuleService.isWeekendPass(d, Integer.parseInt(req.getGid()), userinfo.getUserid());
                Boolean h = passRuleService.isHolidayPass(d, Integer.parseInt(req.getGid()), userinfo.getUserid());
                if (StringUtils.isNotBlank(floors)){
                    Boolean daysOffTranslation = passRuleService.isDaysOffTranslation(d, Integer.parseInt(req.getGid()),Arrays.asList(floors), userinfo.getUserid());
                    daysOffPass = daysOffTranslation ? "是" : "否";
                }
                weekendPass = w ? "是" : "否";
                holidayPass = h ? "是" : "否";
            }
            vt.setIsWeekendVisitor(weekendPass);
            vt.setIsHolidayVisitor(holidayPass);
            vt.setIsSCTimeVisitor(scPass);
            vt.setIsDaysOffVisitor(daysOffPass);

            int i = visitorService.addVisitor(vt);
            List<Visitor> passList = new ArrayList<Visitor>();
            passList.add(vt);
            passService.passAuth(passList, PassEvent.Pass_Add);

           logger.info("userid:" + userinfo.getUserid() + "empid:" + emp.getEmpid());
            if (i > 0) {
                List<ExtendVisitor> evlist = new ArrayList<ExtendVisitor>();
                if (peopleCount > 0) {
                    evlist = extendVisitorService.getTeamExtendVisitor(emp.getUserid());
                } else {
                    evlist = extendVisitorService.getBaseExtendVisitor(userinfo.getUserid());
                }

                messageService.sendCommonNotifyEvent(vt,NotifyEvent.EVENTTYPE_CHECK_IN);
            } else {
                return new RespInfo(1, "insert failed");
            }

            if (StringUtils.isNotBlank(req.getMemberName())) {
                List<Visitor> vlist = new ArrayList<Visitor>();
                try {
                    rootNode = mapper.readValue(req.getMemberName(), JsonNode.class);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Iterator<JsonNode> it = rootNode.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    Visitor v = new Visitor();
                    v.setUserid(userinfo.getUserid());
                    v.setVname(jn.path("name").asText());
                    v.setVphone(jn.path("mobile").asText());
                    v.setEmpName(emp.getEmpName());
                    v.setEmpPhone(emp.getEmpPhone());
                    v.setVisitdate(vt.getVisitdate());
                    v.setAppointmentDate(d);
                    v.setVisitType(visitType);
                    v.setVemail(jn.path("email").asText());
                    v.setCardId(jn.path("cardId").asText());
                    v.setEmpid(emp.getEmpid());
                    v.setEmpdeptid(vt.getEmpdeptid());
                    v.setPeopleCount(req.getPeopleCount());
                    v.setVgroup(vt.getVid());
                    v.setPermissionName(vt.getPermissionName());
                    v.setSubaccountId(0);
                    v.setCompany(company);
                    v.setvType(vType);
                    v.setTid(tid);
                    v.setExtendCol(vt.getExtendCol());
                    v.setGid(req.getGid() + "");
                    v.setVcompany(vcompany);
                    v.setSignInOpName(vt.getSignInOpName());
                    v.setSignInGate(vt.getSignInGate());
                    v.setPlateNum(plateNum);
                    v.setMeetingPoint(meetingPoint);
                    v.setCardNo(req.getCardNo());
                    v.setClientNo(req.getClientNo());
                    v.setFloors(floors);
                    v.setQrcodeConf(vt.getQrcodeConf());
                    v.setIsWeekendVisitor(weekendPass);
                    v.setIsHolidayVisitor(holidayPass);
                    v.setIsSCTimeVisitor(scPass);
                    v.setIsDaysOffVisitor(daysOffPass);
                    v.setPermission(1);

                    if (bmap.containsKey(jn.path("mobile").asText())) {
                        vt.setbCompany(jn.path("mobile").asText());
                    }
                    vc = visitorService.getVisitSaCountByVphone(req);
                    v.setVisitorCount(Integer.parseInt(vc.getCount()));
                    vlist.add(v);
                }

                if (vlist.size() > 0) {
                    visitorService.addGroupVisitor(vlist);
                }

            }

            int count = visitorService.getVisitorCount(userinfo.getUserid());
            List<Visitor> rvlist = new ArrayList<Visitor>();
            if (StringUtils.isNotBlank(req.getMemberName())) {
                rvlist = visitorService.getGroupVistorList(vt.getVid());
            }

            Map<String, Object> maps = new HashMap<String, Object>();
            maps.put("vcount", count);
            maps.put("vid", IvrData.NORMAL_VISIT_TYPE + vt.getVid());
            maps.put("glist", rvlist);

            return new RespInfo(0, "success", maps);
        } finally {
        }
    }

    @ApiOperation(value = "/VisitAppointmentSignin 预约签到", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":\"vid\",\n" +
                    "    \"cardId\":\"访客证件号\",\n" +
                    "    \"photoUrl\":\"访客照片\",\n" +
                    "    \"plateNum\":\"访客车牌\",\n" +
                    "    \"vcompany\":\"访客公司\",\n" +
                    "    \"gid\":\"签到门岗id（必填）\",\n" +
                    "    \"remark\":\"remark\",\n" +
                    "    \"signInGate\":\"signInGate\",\n" +
                    "    \"signInOpName\":\"signInOpName\",\n" +
                    "    \"extendCol\":\"[\"moreTimes=1\",\"access=1\"]\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/VisitAppointmentSignin")
    @ResponseBody
    public RespInfo VisitAppointmentSignin(@RequestBody RequestVisit req, HttpServletRequest request) throws JsonProcessingException {
        int vid = req.getVid();
        String cardId = req.getCardId();
        Visitor vt = visitorService.getVisitorById(vid);
        if(vt==null){
            return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (( !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != vt.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(StringUtils.isBlank(req.getGid())){
            return new RespInfo(ErrorEnum.E_400.getCode(), ErrorEnum.E_400.getMsg(),"gid is empty");
        }

        if(StringUtils.isEmpty(vt.getvType())){
            return new RespInfo(ErrorEnum.E_104.getCode(), ErrorEnum.E_104.getMsg());
        }

        UserInfo userinfo = userService.getUserInfo(vt.getUserid());

        //邀请被拒绝或取消
        if (vt.getPermission()!= 1) {
            return new RespInfo(ErrorEnum.E_067.getCode(), ErrorEnum.E_067.getMsg());
        }

        List<String> extcol = req.getExtendCol();
        if (null != extcol && extcol.size() > 0) {
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < extcol.size(); i++) {
                String[] col = extcol.get(i).split("=");
                map.put(col[0], col[1]);
            }
            if (StringUtils.isBlank(map.get("access"))){
                //获取默认门禁
                String access = visitorService.getDefaultAccess(userinfo, req.getGid(), vt.getEmpid());
                map.put("access",access);
            }
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
            String strext = mapper.writeValueAsString(map);
            vt.setExtendCol(strext);
        }else {
            Map<String, String> map = new HashMap<String, String>();

            //获取默认门禁
            String access = visitorService.getDefaultAccess(userinfo, req.getGid(), vt.getEmpid());
            map.put("access",access);
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
            String strext = mapper.writeValueAsString(map);
            vt.setExtendCol(strext);
        }


        long appdate = vt.getAppointmentDate().getTime();
        long time = new Date().getTime();

        Employee emp = employeeService.getOpenid(vt.getEmpid());
        vt.setVisitdate(new Date());
        vt.setVphoto(req.getPhotoUrl());
        vt.setCardId(cardId);
        vt.setRemark(req.getRemark());


        /**
         * 有效期检查
         */
        int m = UtilTools.differentDays(vt.getAppointmentDate(), new Date());
        if(m<0){
            //预约日期未到
            return new RespInfo(ErrorEnum.E_064.getCode(), ErrorEnum.E_064.getMsg());
        }

        //结束时间
        try {
            String endDate = vt.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
            if(StringUtils.isNotEmpty(endDate)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("gmt"));
                if (format.parse(endDate).getTime() < new Date().getTime()) {
                    //已过期
                    return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
                }
            }else if ( (m+1)> vt.getQrcodeConf()) {
                //已过期
                return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
            }
        }catch (Exception e){
            return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
        }



        //单天预约检查允许的来访时间偏差
        if(vt.getQrcodeConf()>1) {
            if (userinfo.getPreExtendTime() != 0) {
                long preExtendTime = userinfo.getPreExtendTime() * 60000L;
                if ((appdate - time) > preExtendTime) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("appointmentDate", appdate);
                    map.put("preExtendTime", userinfo.getPreExtendTime());
                    return new RespInfo(ErrorEnum.E_064.getCode(), ErrorEnum.E_064.getMsg(), map);
                }
            }

            if (userinfo.getLatExtendTime() != 0) {
                long latExtendTime = userinfo.getLatExtendTime() * 60000L;
                if ((time - appdate) > latExtendTime) {
                    return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
                }
            }
        }

        vt.setSignInOpName(req.getSignInOpName());
        vt.setSignInGate(req.getSignInGate());
        vt.setVcompany(req.getVcompany());
        vt.setPlateNum(req.getPlateNum());

        try {
            //检查员工状态
            visitorService.checkEmployeeTask(vt.getEmpid() + "");
            //检查答题状态
            visitorService.checkQuestionnaireTask(userinfo,vt);
        }catch (ErrorException e){
            return new RespInfo(e.getErrorEnum().getCode(), e.getErrorEnum().getMsg());
        }

        int i = 0;
//			if(vt.getVgroup()==1){
//				i=visitorService.batchUpdateVisitorAppointment(vt);
//			}else{
        i = visitorService.updateVisitorAppointment(vt);

        //下发人脸数据
        List<Visitor> passList = new ArrayList<Visitor>();
        passList.add(vt);
        passService.passAuth(passList, PassEvent.Pass_Add);
//			}

        //发送通知
        if (i > 0) {
            messageService.sendCommonNotifyEvent(vt,NotifyEvent.EVENTTYPE_CHECK_IN);
        } else {
            SysLog.error("updateVisitorAppointment failed:", req);
            return new RespInfo(ErrorEnum.E_063.getCode(), ErrorEnum.E_063.getMsg());
        }

        int count = visitorService.getVisitorCount(userinfo.getUserid());

//			List<RespVisitor>  glist=visitorService.getGroupVistorList(vid);
        List<RespVisitor> vlist = new ArrayList<RespVisitor>();
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("vcount", count);
        maps.put("glist", vlist);
        return new RespInfo(0, "success", maps);
    }

    @ApiOperation(value = "/batchSignIn 批量签到", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/batchSignIn")
    @ResponseBody
    public RespInfo batchSingin(@RequestBody BatchReqVisit brv, HttpServletRequest request) throws JsonProcessingException {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (( !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Visitor> vlist = new ArrayList<Visitor>();
        for (int i = 0; i < brv.getSignInfoList().size(); i++) {
            String photoUrl = brv.getSignInfoList().get(i).getPhotoUrl();
            String name = brv.getSignInfoList().get(i).getName();
            String email = brv.getSignInfoList().get(i).getEmail();
            String phone = brv.getSignInfoList().get(i).getPhone();
            String visitType = brv.getSignInfoList().get(i).getVisitType();
            String cardId = brv.getSignInfoList().get(i).getCardId();
            String sex = brv.getSignInfoList().get(i).getSex();
            int peopleCount = brv.getSignInfoList().get(i).getPeopleCount();
            int empid = brv.getSignInfoList().get(i).getEmpid();
            int clientNo = brv.getSignInfoList().get(i).getClientNo();
            if (null == brv.getSignInfoList().get(i).getDate() || "".equals(brv.getSignInfoList().get(i).getDate())) {
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            try {
                date = sdf.parse(brv.getSignInfoList().get(i).getDate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            List<String> extcol = brv.getSignInfoList().get(i).getExtendCol();
            String strext = "";
            if (null != extcol && extcol.size() > 0) {
                Map<String, String> map = new HashMap<String, String>();
                for (int a = 0; a < extcol.size(); a++) {
                    String[] col = extcol.get(a).split("=");
                    map.put(col[0], col[1]);
                }
                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
                strext = mapper.writeValueAsString(map);
            }

            Employee emp = employeeService.getOpenid(empid);
            if (null == emp) {
                continue;
            }
            if(authToken.getUserid() != emp.getUserid()){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            Visitor vt = new Visitor();
            vt.setEmpid(empid);
            vt.setEmpName(emp.getEmpName());
            vt.setUserid(emp.getUserid());
            vt.setEmpPhone(emp.getEmpPhone());
            vt.setVphoto(photoUrl);
            vt.setVname(name);
            vt.setVisitdate(date);
            vt.setVemail(email);
            vt.setExtendCol(strext);
            vt.setVphone(phone);
            vt.setVisitType(visitType);
            vt.setPeopleCount(peopleCount);
            vt.setSubaccountId(emp.getSubaccountId());
            vt.setCardId(cardId);
            vt.setSex(sex);
            vt.setClientNo(clientNo);

            vlist.add(vt);
        }

        if (vlist.size() > 0) {
            visitorService.batchSingin(vlist);
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/创建手写签名pdf", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/createPdfSign")
    @ResponseBody
    public RespInfo createPdfSign(@RequestBody RequestVisit req, HttpServletRequest request) throws ParseException {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        Visitor v = visitorService.getVisitorById(req.getVid());
        if (null == v) {
            return new RespInfo(ErrorEnum.E_2323.getCode(), ErrorEnum.E_2323.getMsg());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != v.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo ui = userService.getUserInfo(v.getUserid());
        String date = time.format(v.getVisitdate());
        boolean result = false;
        BASE64Decoder decoder = new BASE64Decoder();
        //Base64解码
        byte[] b;
        String filename="";
        try {
            b = decoder.decodeBuffer(req.getSignPic());

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }

//            File myPath = new File("/work/signpdf");
//            if (!myPath.exists()) {//若此目录不存在，则创建之// 这个东西只能建立一级文件夹，两级是无法建立的。。。。。
//                myPath.mkdir();
//            }
            filename=MD5.crypt("a3mj4"+v.getVid()) + ".pdf";
          //  result = PdfTools.createPdf3(date + "_" + filename, b, ui.getSecureProtocol(), v.getVname(), date);
            result = PdfTools.createPdfByte(filename, b, ui.getSecureProtocol(), v.getVname(), date); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (result) {
            v.setSignPdf(filename);
            visitorService.updateSignPdf(v);
        } else {
            return new RespInfo(2323, "create pdf failed");
        }


        return new RespInfo(0, "success", date + "_" + v.getVid() + ".pdf");

    }

    @ApiOperation(value = "/VisitorSignOut 访客签出,签离", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"phone\":\"13813946371\",\n" +
                    "    \"signOutGate\":\"1号楼\",\n" +
                    "    \"signOutOpName\":\"1\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/VisitorSignOut")
    @ResponseBody
    public RespInfo VisitorSignOut(@RequestBody RequestVisit req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = req.getUserid();
        String vphone = req.getPhone();

        Visitor vt = new Visitor();
        vt.setUserid(userid);
        vt.setVphone(vphone);
        vt.setSignOutDate(new Date());
        vt.setSignOutOpName(req.getSignOutOpName());
        vt.setSignOutGate(req.getSignOutGate());

        List<RespVisitor> list = visitorService.checkSignOutRecords(vt);
        for (int i = 0; i < list.size(); i++) {
            if (strRedisTemplate.hasKey("aid_" + list.get(i).getAppid())) {
                strRedisTemplate.delete("aid_" + list.get(i).getAppid());
            }
            if (strRedisTemplate.hasKey("vid_" + list.get(i).getVid())) {
                strRedisTemplate.delete("vid_" + list.get(i).getVid());
            }
        }
        visitorService.updateSignOut(vt);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/VisitorSignOutByVid 根据vid签出", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":430528,\n" +
                    "    \"signOutGate\":\"1号楼\",\n" +
                    "    \"signOutOpName\":\"Cindy\",\n" +
                    "    \"remark\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/VisitorSignOutByVid")
    @ResponseBody
    public RespInfo VisitorSignOutByVid(@RequestBody RequestVisit req, HttpServletRequest request) {
        Visitor vt = new Visitor();
        if (req.getVid() != 0) {
            vt = visitorService.getVisitorById(req.getVid());
            vt.setSignOutDate(new Date());
            vt.setSignOutOpName(req.getSignOutOpName());
            vt.setSignOutGate(req.getSignOutGate());
        } else {
            vt.setAppid(req.getAid());
            vt = visitorService.getVisitorByAppId(vt);
            vt.setSignOutDate(new Date());
            vt.setSignOutOpName(req.getSignOutOpName());
            vt.setSignOutGate(req.getSignOutGate());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != vt.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        visitorService.updateSignOutByVid(vt);

        if (vt.getAppid() != 0) {
            if (strRedisTemplate.hasKey("aid_" + vt.getAppid())) {
                strRedisTemplate.delete("aid_" + vt.getAppid());
            }
        } else {
            if (strRedisTemplate.hasKey("vid_" + req.getVid())) {
                strRedisTemplate.delete("vid_" + req.getVid());
            }
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/batchSignOut 批量签出", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[\n" +
                    "    {\n" +
                    "        \"userid\":\"2147483647\",\n" +
                    "        \"vid\":430528,\n" +
                    "        \"signOutGate\":\"1号楼\",\n" +
                    "        \"signOutOpName\":\"Cindy\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"userid\":\"2147483647\",\n" +
                    "        \"vid\":430529,\n" +
                    "        \"signOutGate\":\"1号楼\",\n" +
                    "        \"signOutOpName\":\"Cindy\"\n" +
                    "    }\n" +
                    "]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/batchSignOut")
    @ResponseBody
    public RespInfo batchSignOut(@RequestBody List<Visitor> vlist, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        //强制插入userid
        for(Visitor vt:vlist){
            vt.setUserid(authToken.getUserid());
        }
        visitorService.batchSignOut(vlist);
        return new RespInfo(0, "success");
    }

    public void sendNotify(Object ... o) {
    	 ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
    	 EmpVisitProxy vp =objectMapper.convertValue(o[0],EmpVisitProxy.class);
    	 Employee empProxy=objectMapper.convertValue(o[1],Employee.class);
    	 UserInfo userinfo=objectMapper.convertValue(o[2],UserInfo.class);
    	 JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ExtendVisitor.class);
    	 List<ExtendVisitor> evlist=objectMapper.convertValue(o[3], javaType);
    	 Visitor vt=objectMapper.convertValue(o[4],Visitor.class);
    	 Employee emp=objectMapper.convertValue(o[5],Employee.class);

         Date pdate = new Date();
         Date sdate = null;
         Date edate = null;
         if (null != vp) {
             empProxy = employeeService.getEmployee(vp.getProxyId());
             sdate = vp.getStartDate();
             edate = vp.getEndDate();
         }

    	 boolean signleNotify=false;

 	    if(StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getMsgNotify() == 1) {
 	    	visitorService.sendWeixin(userinfo, emp, vt);
 	    	if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                 visitorService.sendWeixin(userinfo, empProxy, vt);
             }

 	    	if(userinfo.getNotifyType()==1) {
 	    		signleNotify=true;
 	    	}
 	    }

 	    if(StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getWxBusNotify()==1) {
 	    	if(!signleNotify||userinfo.getNotifyType()==0) {
 	    		visitorService.sendNotifyByWXBus(emp, vt);

 	    		if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
 	                visitorService.sendNotifyByWXBus(empProxy, vt);
 	            }
 	    	}

 	    	if(userinfo.getNotifyType()==1) {
 	    		signleNotify=true;
 	    	}
 	    }

 	    if(StringUtils.isNotBlank(emp.getDdid())&&userinfo.getDdnotify() == 1) {
 	    	if(!signleNotify||userinfo.getNotifyType()==0) {
 	    		visitorService.sendNotifyByDD(emp, vt);

 	    		if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                     visitorService.sendNotifyByDD(empProxy, vt);
                 }
 	    	}

 	    	if(userinfo.getNotifyType()==1) {
 	    		signleNotify=true;
 	    	}
 	    }

 	   if(StringUtils.isNotBlank(emp.getOpenid())&&userinfo.getFsNotify() == 1) {
	    	if(!signleNotify||userinfo.getNotifyType()==0) {
	    		visitorService.sendNotifyByFeiShu(emp, vt);

	    		if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    visitorService.sendNotifyByFeiShu(empProxy, vt);
                }
	    	}

	    	if(userinfo.getNotifyType()==1) {
	    		signleNotify=true;
	    	}
	    }

 	    if (userinfo.getEmailType() != 0 && StringUtils.isNotBlank(emp.getEmpEmail())) {
             logger.info("sendMail:" + emp.getEmpEmail());
             try {
             	if(!signleNotify||userinfo.getNotifyType()==0) {
	                    visitorService.sendMail(userinfo, emp, vt, evlist);
	                    if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
	                        logger.info("sendMail:" + empProxy.getEmpEmail());
	                        visitorService.sendMail(userinfo, empProxy, vt, evlist);
	                    }
             	}

             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }

             if(userinfo.getNotifyType()==1) {
 	    		signleNotify=true;
 	    	}
         }

 	    if(userinfo.getSmsNotify() == 1&&emp.getEmpPhone().length()==11) {
 	    	 if (StringUtils.isNotBlank(emp.getEmpPhone())) {
 	    		 if(!signleNotify||userinfo.getNotifyType()==0) {
 	    			 visitorService.sendSMS(userinfo,emp,vt);
 	    			    if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
 	                        visitorService.sendSMS(userinfo, empProxy, vt);
 	                    }
 	    		 }
 	         }
 	    }

    }

    public class SendNodifytoDefault implements Runnable {
        private UserInfo userinfo;
        private Visitor vt;
        private List<ExtendVisitor> evlist;
        private Employee emp;

//        SendNodifytoDefault(UserInfo userinfo, Visitor vt, List<ExtendVisitor> evlist, Employee emp) {
//            this.userinfo = userinfo;
//            this.evlist = evlist;
//            this.vt = vt;
//            this.emp = emp;
//        }


        public void run() {
            EmpVisitProxy vp = new EmpVisitProxy();
            Employee empProxy = new Employee();
            vp.setEmpid(emp.getEmpid());
            vp.setUserid(userinfo.getUserid());
            vp = visitProxyService.getProxyInfoByEid(vp);
            sendNotify(vp,empProxy,userinfo,evlist,vt,emp);

        }

    }

    public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
//    	  String token=UtilTools.checkFeiShuConfigure("cli_a02189227978500b","8Nz4o1AFNTCsr3x39tkJ0dIVlZOKptU3");
//    	  System.out.println(token);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receive_id", "ou_68d11cec7734d1cdad314de4a415ba61");
        Map<String, String> tmap = new HashMap<String, String>();
        tmap.put("text","已在公司前台完成来访登记，请准备接待。 ");
        map.put("content", JsonUtil.stringify(tmap));
        map.put("msg_type", "text");
    	String url = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
        	String response = HttpClientUtil.postJsonBodyForFs(url, 3000, map, "utf-8","t-04ebdf5d237076587ebe5659fcf98c580c57bb05" );
          System.out.println(response);

    }

    @ApiOperation(value = "获取访客满意度调查表 /getVisitorQuestionnaire", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":123456\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorQuestionnaire")
    @ResponseBody
    public RespInfo getVisitorQuestionnaire(@RequestBody RequestVisit requestVisit, HttpServletRequest request) {
        int vid = requestVisit.getVid();
        Visitor visitor = visitorService.getVisitorById(vid);
        if (null != visitor) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                    ||authToken.getUserid() != visitor.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

            String encodeVid = AESUtil.encode(visitor.getVid() + "", Constant.AES_KEY);
            return new RespInfo(0, "success", encodeVid);
        } else {
            return new RespInfo(-1, "invalid vid");
        }
    }

    @ApiOperation(value = "获取满意度评分结果 /getScoreChartByVisitor", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"startDate\":\"2021-06-15\",\n" +
                    "    \"endDate\":\"2021-06-15\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getScoreChartByVisitor")
    @ResponseBody
    public RespInfo getScoreChartByVisitor(@RequestBody RequestVisit requestVisit, HttpServletRequest request) {
        if (StringUtils.isBlank(requestVisit.getStartDate()) || StringUtils.isBlank(requestVisit.getEndDate())) {
            return new RespInfo(-1, "Fail");
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        int userid = authToken.getUserid();
        requestVisit.setUserid(userid);

        if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
    		Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
    		String gids[]=requestVisit.getGid().split(",");
    		String mgids[]=mgr.getGid().split(",");
    	    boolean auth=UtilTools.arrayContain(gids, mgids);
    		if(auth) {
    			return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
    		}
        }

        List<Map<String, Object>> response = new ArrayList<>();
        List<Visitor> visitorList = visitorService.getScoreChartByVisitor(requestVisit);
        if (null != visitorList && visitorList.size() > 0) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<Integer, List<Visitor>> groupMap = visitorList.stream().collect(Collectors.groupingBy(visitor -> visitor.getOverallScore()));
            Set<Integer> keys = groupMap.keySet();
            for (Integer key : keys) {
                List<Visitor> visitors = groupMap.get(key);
                Map<String, Object> param = new HashMap<String, Object>() {
                    {
                        put("type", key);
                        put("count", visitors.size());
                    }
                };
                response.add(param);
            }
        }
        return new RespInfo(0, "sucess", response);
    }


    @ApiOperation(value = "/testOnVisitAppoinmentNotify", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":123456\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/testOnVisitAppoinmentNotify")
    @ResponseBody
    public RespInfo testOnVisitAppoinmentNotify(@RequestBody Map visitor, HttpServletRequest request) {
        SysLog.info(visitor);
        return new RespInfo(0, "success");
    }

}
