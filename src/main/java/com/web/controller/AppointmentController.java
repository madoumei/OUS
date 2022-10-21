package com.web.controller;

import com.client.bean.*;
import com.client.service.*;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.config.qicool.common.persistence.Page;
import com.event.event.NotifyEvent;
import com.event.event.PassEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.utils.*;
import com.utils.emailUtils.SendInviteEmail;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.yimei.JsonHelper;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "AppointmentController", tags = "API_访客邀请管理", hidden = true)
public class AppointmentController {
    private static Logger logger = Logger.getLogger("mylogger2");

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PassRuleService passRuleService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AddressService addressService;

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private VisitProxyService visitProxyService;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private ExtendVisitorService extendVisitorService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private PassService passService;

    @ApiOperation(value = "/addAppointment 批量访客邀请", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[\n" +
                    "    {\n" +
                    "        \"name\":\"测试\",\n" +
                    "        \"phone\":\"15251805548\",\n" +
                    "        \"visitType\":\"商务#Business\",\n" +
                    "        \"appointmentDate\":\"1615877866000\",\n" +
                    "        \"openid\":\"oHriHwe_sYbEctEbQ8QDm7bLkOmA\",\n" +
                    "        \"tid\":181,\n" +
                    "        \"gid\":1,\n" +
                    "        \"vtype\":\"普通访客#Normal Visitor\",\n" +
                    "        \"qrcodeType\":0,\n" +
                    "        \"qrcodeConf\":1,\n" +
                    "        \"remark\":\"\",\n" +
                    "        \"clientNo\":1,\n" +
                    "        \"appExtendCol\":\"{\\\"name\\\":\\\"测试\\\",\\\"phone\\\":\\\"15251805548\\\",\\\"appointmentDate\\\":\\\"2020-12-04 15:00\\\",\\\"email\\\":\\\"\\\",\\\"visitType\\\":\\\"商务\\\",\\\"qrcodeConf\\\":1,\\\"remark\\\":\\\"\\\",\\\"gateType\\\":\\\"1号楼\\\",\\\"secret\\\":\\\"n\\\"}\"\n" +
                    "    }\n" +
                    "]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addAppointment")
    @ResponseBody
    public RespInfo addAppointment(
            @ApiParam(value = "Appointment 访客邀请Bean", required = true) @Validated @RequestBody List<Appointment> atlist,
            HttpServletRequest req, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //子账号用于第三方调用
        AuthToken authToken = tokenServer.getAuthTokenByRequest(req);
        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int empId = 0;
        if (Integer.parseInt(authToken.getLoginAccountId()) > 0) {
            empId = Integer.parseInt(authToken.getLoginAccountId());
        } else {
            List<Employee> byOpenid = employeeService.getEmpListByOpenid(authToken.getOpenid());
            if (null != byOpenid && byOpenid.size() > 0) {
                empId = byOpenid.get(0).getEmpid();
            }
        }

        Employee emp = employeeService.getEmployee(empId);
        String company = "";
        int mid = atlist.get(0).getMid();
        String agroup = String.valueOf(new Date().getTime());
        List<Blacklist> blList = new ArrayList<Blacklist>();

        if (null == emp) {
            return new RespInfo(55, "no employee");
        }

        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(emp.getStartDate());
        int d2 = Integer.parseInt(emp.getEndDate());
        int today = Integer.parseInt(sd.format(new Date()));
        if (today < d1 || today > d2) {
            return new RespInfo(ErrorEnum.E_1113.getCode(), ErrorEnum.E_1113.getMsg());
        }

        UserInfo userinfo = userService.getUserInfo(authToken.getUserid());
        if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
            SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
            company = sa.getCompanyName();
            int purviewValue = 1;
            if ((sa.getVaPerm() & purviewValue) != purviewValue) {
                return new RespInfo(ErrorEnum.E_116.getCode(), ErrorEnum.E_116.getMsg());
            }

        } else {
            company = userinfo.getCompany();
        }
        List<Appointment> processAppointmentList = new ArrayList<>();

        //黑名单判断
        Blacklist bl = new Blacklist();
        if (userinfo.getBlackListSwitch() == 1) {
            for (int i = 0; i < atlist.size(); i++) {
                bl.setUserid(userinfo.getUserid());
                bl.setPhone(atlist.get(i).getPhone());
                bl.setCredentialNo(atlist.get(i).getCardId());
                if (atlist.get(i).getGid() != 0) {
                    bl.setGids(atlist.get(i).getGid() + "");
                }
                if (emp.getSubaccountId() != 0) {
                    bl.setSids(emp.getSubaccountId() + "");
                }

                if ((StringUtils.isNotBlank(atlist.get(i).getPhone())) || (StringUtils.isNotBlank(atlist.get(i).getCardId()))) {
                    List<Blacklist> bli = blacklistService.checkBlacklist(bl);
                    if (blList.size() > 0 || bli.size() > 0) {
                        if (bli.size() > 0) {
                            blList.add(bli.get(0));
                        }
                        continue;
                    }
                }
            }
        }

        if (blList.size() > 0) {
            return new RespInfo(66, "user was added to the blacklist", blList);
        }

        /**
         * 检查是否有模板
         */
        for (int i = 0; i < atlist.size(); i++) {
            Emptemplate et = new Emptemplate();
            et.setUserid(emp.getUserid());
            et.setEmpPhone(emp.getEmpPhone());
            String temptype = atlist.get(i).getVisitType();
            et.setTemplateType(temptype);
            et.setGid(atlist.get(i).getGid());
            Emptemplate emptemp = appointmentService.getEmptemplate(et);
            if (null == emptemp || userinfo.getTempEditSwitch() == 1) {
                Usertemplate ut = new Usertemplate();
                ut.setUserid(emp.getUserid());
                ut.setTemplateType(temptype);
                ut.setGid(atlist.get(i).getGid());
                if (emp.getSubaccountId() == 0) {
                    Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                    if (null == usertemp) {
                        return new RespInfo(59, "no template is found");
                    }
                }
            }
        }

        //获取扩展信息
        String moreTimes = "0";
        String accessType = "";
        String moreDays = "1";
        ExtendVisitor ev = new ExtendVisitor();
        ev.seteType(atlist.get(0).getvType());
        ev.setUserid(emp.getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        for (int i = 0; i < extendVisitors.size(); i++) {
            if ("moreTimes".equals(extendVisitors.get(i).getFieldName())) {
                moreTimes = extendVisitors.get(i).getInputValue();
            }
            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
                accessType = extendVisitors.get(i).getInputValue();
            }
            if ("moreDays".equals(extendVisitors.get(i).getFieldName())) {
                moreDays = extendVisitors.get(i).getInputValue();
            }
        }

        for (int i = 0; i < atlist.size(); i++) {
            Emptemplate et = new Emptemplate();
            et.setUserid(emp.getUserid());
            et.setEmpPhone(emp.getEmpPhone());
            String temptype = atlist.get(i).getVisitType();
            et.setTemplateType(temptype);
            et.setGid(atlist.get(i).getGid());
            Emptemplate emptemp = appointmentService.getEmptemplate(et);
            if (null == emptemp || userinfo.getTempEditSwitch() == 1) {
                Usertemplate ut = new Usertemplate();
                ut.setUserid(emp.getUserid());
                ut.setTemplateType(temptype);
                ut.setGid(atlist.get(i).getGid());
                if (emp.getSubaccountId() == 0) {
                    Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                    if (null == usertemp) {
                        return new RespInfo(59, "no template is found");
                    }
                    atlist.get(i).setInviteContent(usertemp.getInviteContent());
                    atlist.get(i).setCompanyProfile(usertemp.getCompanyProfile());
                    atlist.get(i).setAddress(usertemp.getAddress());
                    atlist.get(i).setTraffic(usertemp.getTraffic());
                    atlist.get(i).setLatitude(usertemp.getLatitude());
                    atlist.get(i).setLongitude(usertemp.getLongitude());

                } else {
                    SubAccountTemplate subacctemp = appointmentService.getSAtemplate(emp.getSubaccountId(), temptype, atlist.get(i).getGid());
                    if (null == subacctemp) {
                        Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                        atlist.get(i).setInviteContent(usertemp.getInviteContent());
                        atlist.get(i).setCompanyProfile(usertemp.getCompanyProfile());
                        atlist.get(i).setAddress(usertemp.getAddress());
                        atlist.get(i).setTraffic(usertemp.getTraffic());
                        atlist.get(i).setLatitude(usertemp.getLatitude());
                        atlist.get(i).setLongitude(usertemp.getLongitude());
                    } else {
                        atlist.get(i).setInviteContent(subacctemp.getInviteContent());
                        atlist.get(i).setCompanyProfile(subacctemp.getCompanyProfile());
                        atlist.get(i).setAddress(subacctemp.getAddress());
                        atlist.get(i).setTraffic(subacctemp.getTraffic());
                        atlist.get(i).setLatitude(subacctemp.getLatitude());
                        atlist.get(i).setLongitude(subacctemp.getLongitude());
                    }
                }
            } else {
                atlist.get(i).setInviteContent(emptemp.getInviteContent());
                atlist.get(i).setCompanyProfile(emptemp.getCompanyProfile());
                atlist.get(i).setAddress(emptemp.getAddress());
                atlist.get(i).setTraffic(emptemp.getTraffic());
                atlist.get(i).setLatitude(emptemp.getLatitude());
                atlist.get(i).setLongitude(emptemp.getLongitude());
            }
            atlist.get(i).setEmpEmail(emp.getEmpEmail());
            atlist.get(i).setEmpName(emp.getEmpName());
            atlist.get(i).setEmpPhone(emp.getEmpPhone());
            atlist.get(i).setSubaccountId(emp.getSubaccountId());
            atlist.get(i).setCompany(company);
            atlist.get(i).setAgroup(agroup);
            atlist.get(i).setUserid(authToken.getUserid());
            atlist.get(i).setEmpid(empId);
            String endDate = atlist.get(i).getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
            if(StringUtils.isNotEmpty(endDate)) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    format.setTimeZone(TimeZone.getTimeZone("gmt"));
                    Date date = format.parse(endDate);
                    int conf= UtilTools.differentDays(atlist.get(i).getAppointmentDate(),date);
                    atlist.get(i).setQrcodeConf(conf+1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            List<Department> dept = departmentService.getDeptByEmpid(empId, userinfo.getUserid());
            if (null == dept || dept.size() == 0) {
                atlist.get(i).setEmpdeptid(0);
                atlist.get(i).setDeptName("");
            } else {
                atlist.get(i).setEmpdeptid(dept.get(0).getDeptid());
                atlist.get(i).setDeptName(dept.get(0).getDeptName());
            }

            String floors = atlist.get(i).getFloors();
            RequestVisit rv = new RequestVisit();
            rv.setUserid(userinfo.getUserid());
            rv.setPhone(atlist.get(i).getPhone());
            rv.setSubaccountId(emp.getSubaccountId());

            VisitorChart vc = visitorService.getVisitSaCountByVphone(rv);
            atlist.get(i).setVisitorCount(Integer.parseInt(vc.getCount()));

            //access字段
            String access = atlist.get(i).getExtendValue("access");
            if (StringUtils.isEmpty(access)) {
                //未设置access,插入默认门禁值

                //获取默认门禁
                Appointment appointment = atlist.get(i);
                access = visitorService.getDefaultAccess(userinfo, appointment.getGid() + "", appointment.getEmpid());
                appointment.addExtendValue("access", access);
            }
            atlist.get(i).addExtendValue("moreTimes", moreTimes);
            atlist.get(i).addExtendValue("moreDays", moreDays);
            atlist.get(i).addExtendValue("accessType", accessType);

            /**
             * 随机验证码
             */
            String checkcode = RandomStringUtils.random(6, false, true);
            atlist.get(i).setAcode(checkcode);

            atlist.get(i).setPermission(VisitorService.PERMISSION_SEC);

            appointmentService.addAppointment(atlist.get(i));

        }


//        if (mid != 0) {
//            //会议直接发邀请函
//            Meeting m = new Meeting();
//            m.setMid(mid);
//            m.setStatus(1);
//            meetingService.updateMeetingStatus(m);
//            for(){
//
//            }
//        }else{
        /**
         * 上面保存完数据后开始走流程
         */
        appointmentService.supplementSubBPM(atlist.get(0), userinfo);

//        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addAppointmentExternal 第三方接口，批量访客邀请", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[\n" +
                    "    {\n" +
                    "        \"name\":\"测试\",\n" +
                    "        \"phone\":\"15251805548\",\n" +
                    "        \"visitType\":\"商务#Business\",\n" +
                    "        \"appointmentDate\":\"1615877866000\",\n" +
                    "        \"openid\":\"oHriHwe_sYbEctEbQ8QDm7bLkOmA\",\n" +
                    "        \"category\":1：物流2：访客,\n" +
                    "        \"gateName\":门岗名称,\n" +
                    "        \"vtype\":\"普通访客#Normal Visitor\",\n" +
                    "        \"qrcodeType\":0,\n" +
                    "        \"qrcodeConf\":1,\n" +
                    "        \"remark\":\"\",\n" +
                    "        \"clientNo\":1,\n" +
                    "        \"appExtendCol\":\"{\\\"name\\\":\\\"测试\\\",\\\"phone\\\":\\\"15251805548\\\",\\\"appointmentDate\\\":\\\"2020-12-04 15:00\\\",\\\"email\\\":\\\"\\\",\\\"visitType\\\":\\\"商务\\\",\\\"qrcodeConf\\\":1,\\\"remark\\\":\\\"\\\",\\\"gateType\\\":\\\"1号楼\\\",\\\"secret\\\":\\\"n\\\"}\"\n" +
                    "    }\n" +
                    "]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addAppointmentExternal")
    @ResponseBody
    public RespInfo addAppointmentExternal(
            @ApiParam(value = "Appointment 访客邀请Bean", required = true) @Validated @RequestBody List<Appointment> atlist,
            HttpServletRequest req, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //子账号用于第三方调用
        AuthToken authToken = tokenServer.getAuthTokenByRequest(req);
        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (atlist.isEmpty()){
            return new RespInfo(ErrorEnum.E_505.getCode(), ErrorEnum.E_505.getMsg());
        }

        /**int empId = 0;
        if (Integer.parseInt(authToken.getLoginAccountId()) > 0) {
            empId = Integer.parseInt(authToken.getLoginAccountId());
        } else {
            List<Employee> byOpenid = employeeService.getEmpListByOpenid(authToken.getOpenid());
            if (null != byOpenid && byOpenid.size() > 0) {
                empId = byOpenid.get(0).getEmpid();
            }
        }*/



        //Employee emp = employeeService.getEmployee(empId);
        List<Employee> emps = employeeService.getEmpInfo(authToken.getUserid(),atlist.get(0).getEmpPhone());
        Employee emp = new Employee();
        if (emps.isEmpty()){
            return new RespInfo(ErrorEnum.E_055.getCode(), ErrorEnum.E_055.getMsg());
        }else {
            emp = emps.get(0);
        }
        String company = "";
        int mid = atlist.get(0).getMid();
        String agroup = String.valueOf(new Date().getTime());
        List<Blacklist> blList = new ArrayList<Blacklist>();

        if (null == emp) {
            return new RespInfo(55, "no employee");
        }

        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(emp.getStartDate());
        int d2 = Integer.parseInt(emp.getEndDate());
        int today = Integer.parseInt(sd.format(new Date()));
        if (today < d1 || today > d2) {
            return new RespInfo(ErrorEnum.E_1113.getCode(), ErrorEnum.E_1113.getMsg());
        }

        UserInfo userinfo = userService.getUserInfo(authToken.getUserid());
        if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
            SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
            company = sa.getCompanyName();
            int purviewValue = 1;
            if ((sa.getVaPerm() & purviewValue) != purviewValue) {
                return new RespInfo(ErrorEnum.E_116.getCode(), ErrorEnum.E_116.getMsg());
            }

        } else {
            company = userinfo.getCompany();
        }
        List<Appointment> processAppointmentList = new ArrayList<>();

        //黑名单判断
        Blacklist bl = new Blacklist();
        if (userinfo.getBlackListSwitch() == 1) {
            for (int i = 0; i < atlist.size(); i++) {
                bl.setUserid(userinfo.getUserid());
                bl.setPhone(atlist.get(i).getPhone());
                bl.setCredentialNo(atlist.get(i).getCardId());
                if (atlist.get(i).getGid() != 0) {
                    bl.setGids(atlist.get(i).getGid() + "");
                }
                if (emp.getSubaccountId() != 0) {
                    bl.setSids(emp.getSubaccountId() + "");
                }

                if ((StringUtils.isNotBlank(atlist.get(i).getPhone())) || (StringUtils.isNotBlank(atlist.get(i).getCardId()))) {
                    List<Blacklist> bli = blacklistService.checkBlacklist(bl);
                    if (blList.size() > 0 || bli.size() > 0) {
                        if (bli.size() > 0) {
                            blList.add(bli.get(0));
                        }
                        continue;
                    }
                }
            }
        }

        if (blList.size() > 0) {
            return new RespInfo(66, "user was added to the blacklist", blList);
        }


        //获取扩展信息
        String moreTimes = "0";
        String accessType = "";
        String moreDays = "1";
        ExtendVisitor ev = new ExtendVisitor();
        ev.seteType(atlist.get(0).getvType());
        ev.setUserid(atlist.get(0).getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        for (int i = 0; i < extendVisitors.size(); i++) {
            if ("moreTimes".equals(extendVisitors.get(i).getFieldName())) {
                moreTimes = extendVisitors.get(i).getInputValue();
            }
            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
                accessType = extendVisitors.get(i).getInputValue();
            }
            if ("moreDays".equals(extendVisitors.get(i).getFieldName())) {
                moreDays = extendVisitors.get(i).getInputValue();
            }
        }

        for (int i = 0; i < atlist.size(); i++) {

            if (StringUtils.isBlank(atlist.get(i).getPhone()) && StringUtils.isBlank(atlist.get(i).getVemail())){
                return new RespInfo(-1,"visitor phone or email not null");
            }

            //根据门岗名查询门岗详情
            String gateName = atlist.get(i).getGateName();
            Gate gate = new Gate();
            gate.setGname(gateName);
            gate.setUserid(emp.getUserid());
            Gate gateByName = addressService.getGateByName(gate);
            if (ObjectUtils.isNotEmpty(gateByName)) {
                atlist.get(i).setGid(gateByName.getGid());
            }else{
                return new RespInfo(125,"gata is null");
            }
            //获取访客类型TID
            VisitorType visitorType = new VisitorType();
            visitorType.setUserid(emp.getUserid());
            visitorType.setvType(atlist.get(i).getvType());
            if (StringUtils.isNotBlank(atlist.get(i).getCategory())) {
                if ("物流".equalsIgnoreCase(atlist.get(i).getCategory())) {
                    visitorType.setCategory(1);
                } else if ("访客".equalsIgnoreCase(atlist.get(i).getCategory())) {
                    visitorType.setCategory(2);
                }
            }
            List<VisitorType> visitorTypeList = visitorTypeService.getVisitorType(visitorType);
            if (!visitorTypeList.isEmpty()) {
                atlist.get(i).setTid(visitorTypeList.get(0).getTid());
            }else{
                return new RespInfo(104,"visitor type is null");
            }
            Emptemplate et = new Emptemplate();
            et.setUserid(emp.getUserid());
            et.setEmpPhone(emp.getEmpPhone());
            String temptype = atlist.get(i).getVisitType();
            et.setTemplateType(temptype);
            et.setGid(atlist.get(i).getGid());
            Emptemplate emptemp = appointmentService.getEmptemplate(et);
            if (null == emptemp || userinfo.getTempEditSwitch() == 1) {
                Usertemplate ut = new Usertemplate();
                ut.setUserid(emp.getUserid());
                ut.setTemplateType(temptype);
                ut.setGid(atlist.get(i).getGid());
                if (emp.getSubaccountId() == 0) {
                    Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                    if (null == usertemp) {
                        return new RespInfo(68,"visitorType is fail");
                    }
                    atlist.get(i).setInviteContent(usertemp.getInviteContent());
                    atlist.get(i).setCompanyProfile(usertemp.getCompanyProfile());
                    atlist.get(i).setAddress(usertemp.getAddress());
                    atlist.get(i).setTraffic(usertemp.getTraffic());
                    atlist.get(i).setLatitude(usertemp.getLatitude());
                    atlist.get(i).setLongitude(usertemp.getLongitude());

                } else {
                    SubAccountTemplate subacctemp = appointmentService.getSAtemplate(emp.getSubaccountId(), temptype, atlist.get(i).getGid());
                    if (null == subacctemp) {
                        Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                        atlist.get(i).setInviteContent(usertemp.getInviteContent());
                        atlist.get(i).setCompanyProfile(usertemp.getCompanyProfile());
                        atlist.get(i).setAddress(usertemp.getAddress());
                        atlist.get(i).setTraffic(usertemp.getTraffic());
                        atlist.get(i).setLatitude(usertemp.getLatitude());
                        atlist.get(i).setLongitude(usertemp.getLongitude());
                    } else {
                        atlist.get(i).setInviteContent(subacctemp.getInviteContent());
                        atlist.get(i).setCompanyProfile(subacctemp.getCompanyProfile());
                        atlist.get(i).setAddress(subacctemp.getAddress());
                        atlist.get(i).setTraffic(subacctemp.getTraffic());
                        atlist.get(i).setLatitude(subacctemp.getLatitude());
                        atlist.get(i).setLongitude(subacctemp.getLongitude());
                    }
                }
            } else {
                atlist.get(i).setInviteContent(emptemp.getInviteContent());
                atlist.get(i).setCompanyProfile(emptemp.getCompanyProfile());
                atlist.get(i).setAddress(emptemp.getAddress());
                atlist.get(i).setTraffic(emptemp.getTraffic());
                atlist.get(i).setLatitude(emptemp.getLatitude());
                atlist.get(i).setLongitude(emptemp.getLongitude());
            }
            atlist.get(i).setEmpEmail(emp.getEmpEmail());
            atlist.get(i).setEmpName(emp.getEmpName());
            atlist.get(i).setEmpPhone(emp.getEmpPhone());
            atlist.get(i).setSubaccountId(emp.getSubaccountId());
            atlist.get(i).setCompany(company);
            atlist.get(i).setAgroup(agroup);
            atlist.get(i).setUserid(authToken.getUserid());
            atlist.get(i).setEmpid(emp.getEmpid());
            List<Department> dept = departmentService.getDeptByEmpid(emp.getEmpid(), userinfo.getUserid());
            if (null == dept || dept.size() == 0) {
                atlist.get(i).setEmpdeptid(0);
                atlist.get(i).setDeptName("");
            } else {
                atlist.get(i).setEmpdeptid(dept.get(0).getDeptid());
                atlist.get(i).setDeptName(dept.get(0).getDeptName());
            }

            String floors = atlist.get(i).getFloors();
            RequestVisit rv = new RequestVisit();
            rv.setUserid(userinfo.getUserid());
            rv.setPhone(atlist.get(i).getPhone());
            rv.setSubaccountId(emp.getSubaccountId());

            VisitorChart vc = visitorService.getVisitSaCountByVphone(rv);
            atlist.get(i).setVisitorCount(Integer.parseInt(vc.getCount()));

            //access字段
            String access = atlist.get(i).getExtendValue("access");
            if (StringUtils.isEmpty(access)) {
                //未设置access,插入默认门禁值

                //获取默认门禁
                Appointment appointment = atlist.get(i);
                access = visitorService.getDefaultAccess(userinfo, appointment.getGid() + "", appointment.getEmpid());
                appointment.addExtendValue("access", access);
            }
            atlist.get(i).addExtendValue("moreTimes", moreTimes);
            atlist.get(i).addExtendValue("moreDays", moreDays);
            atlist.get(i).addExtendValue("accessType", accessType);
            atlist.get(i).addExtendValue("tid", atlist.get(i).getTid()+"");
            atlist.get(i).setClientNo(2);

            /**
             * 随机验证码
             */
            String checkcode = RandomStringUtils.random(6, false, true);
            atlist.get(i).setAcode(checkcode);

            atlist.get(i).setPermission(VisitorService.PERMISSION_ACCEPT);

            appointmentService.addAppointment(atlist.get(i));
            messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(atlist.get(i)),NotifyEvent.EVENTTYPE_SEND_INVITE);
        }


        return new RespInfo(0, "success");
    }


    @Deprecated
    @ApiOperation(value = "/addAppointmentByWX 微信访客邀请", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "3.0以后已废弃"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addAppointmentByWX")
    @ResponseBody
    public RespInfo addAppointmentByWX(
            @ApiParam(value = "Appointment 访客邀请Bean", required = true) @Validated @RequestBody Appointment app,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        Employee emp = null;
        String openid = app.getOpenid();
        String temptype = app.getVisitType();
        String company = "";
        String agroup = String.valueOf(new Date().getTime());
        int gid = app.getGid();

        if (null == openid || "".equals(openid)) {
            return new RespInfo(0, "failed");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Employee> emplist = employeeService.getEmpListByOpenid(openid);
        if (emplist.size() > 0) {
            emp = emplist.get(0);
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if (!authToken.getOpenid().equals(emp.getOpenid())) {
                return new RespInfo(1, "invalid user");
            }
        } else {
            return new RespInfo(55, "no employee");
        }

        Emptemplate et = new Emptemplate();
        et.setUserid(emp.getUserid());
        et.setEmpPhone(emp.getEmpPhone());
        et.setTemplateType(temptype);
        et.setGid(gid);
        UserInfo userinfo = userService.getUserInfo(emp.getUserid());
        Blacklist bl = new Blacklist();
        if (userinfo.getBlackListSwitch() == 1) {
            bl.setUserid(userinfo.getUserid());
            bl.setPhone(app.getPhone());
            bl.setCredentialNo(app.getCardId());
            if ((StringUtils.isNotBlank(app.getPhone())) || (StringUtils.isNotBlank(app.getCardId()))) {
                String sid = String.valueOf(emp.getSubaccountId());
                List<Blacklist> bli = blacklistService.checkBlacklist(bl);
                if (bli.size() > 0 && StringUtils.isNotBlank(bli.get(0).getSids())) {
                    String sids[] = bli.get(0).getSids().split(",");
                    boolean b = false;
                    for (int t = 0; t < sids.length; t++) {
                        if (sid.equals(sids[t])) {
                            b = true;
                        }
                    }

                    if (!b || sids.length > 1) {
                        app.setbCompany(bli.get(0).getSname());
                    }

                    if (b) {
                        return new RespInfo(66, "user was added to the blacklist", bli);
                    }
                } else if (bli.size() > 0) {
                    return new RespInfo(66, "user was added to the blacklist", bli);
                }
            }
        }

        Emptemplate emptemp = appointmentService.getEmptemplate(et);
        if (null == emptemp || userinfo.getTempEditSwitch() == 1) {
            Usertemplate ut = new Usertemplate();
            ut.setUserid(emp.getUserid());
            ut.setTemplateType(temptype);
            ut.setGid(gid);

            if (emp.getSubaccountId() == 0) {
                Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                if (null == usertemp) {
                    return new RespInfo(59, "no template is found");
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
            } else {
                SubAccountTemplate subacctemp = appointmentService.getSAtemplate(emp.getSubaccountId(), temptype, gid);
                if (null == subacctemp) {
                    Usertemplate usertemp = appointmentService.getUsertemplate(ut);
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
                } else {
                    app.setUserid(emp.getUserid());
                    app.setEmpEmail(emp.getEmpEmail());
                    app.setEmpName(emp.getEmpName());
                    app.setEmpPhone(emp.getEmpPhone());
                    app.setAddress(subacctemp.getAddress());
                    app.setCompanyProfile(subacctemp.getCompanyProfile());
                    app.setInviteContent(subacctemp.getInviteContent());
                    app.setLatitude(subacctemp.getLatitude());
                    app.setLongitude(subacctemp.getLongitude());
                    app.setTraffic(subacctemp.getTraffic());
                }
            }
        } else {
            app.setUserid(emp.getUserid());
            app.setEmpEmail(emp.getEmpEmail());
            app.setEmpName(emp.getEmpName());
            app.setEmpPhone(emp.getEmpPhone());
            app.setLatitude(emptemp.getLatitude());
            app.setLongitude(emptemp.getLongitude());
            app.setAddress(emptemp.getAddress());
            app.setTraffic(emptemp.getTraffic());
            app.setInviteContent(emptemp.getInviteContent());
            app.setCompanyProfile(emptemp.getCompanyProfile());
        }
        try {
            Date appDate = sdf.parse(app.getWxDate());
            app.setAppointmentDate(appDate);
            app.setEmpid(emp.getEmpid());
            app.setSubaccountId(emp.getSubaccountId());

            if (userinfo.getSubAccount() == 0 || emp.getSubaccountId() == 0) {
                company = userinfo.getCompany();
            } else {
                SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
                company = sa.getCompanyName();
            }
            app.setCompany(company);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return new RespInfo(0, "date error");
        }
        app.setAgroup(agroup);

        RequestVisit rv = new RequestVisit();
        rv.setUserid(app.getUserid());
        rv.setPhone(app.getPhone());
        rv.setSubaccountId(emp.getSubaccountId());

        VisitorChart vc = visitorService.getVisitSaCountByVphone(rv);
        app.setVisitorCount(Integer.parseInt(vc.getCount()));
        /**
         * 随机验证码
         */
        String checkcode = RandomStringUtils.random(6, false, true);
        app.setAcode(checkcode);

        //access字段
        String appExtendCol = app.getAppExtendCol();
        ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
        try {
            JsonNode jsonNode = instance.readValue(appExtendCol, JsonNode.class);
            if (null != jsonNode
                    && (jsonNode.get("access") == null
                    || StringUtils.isBlank(jsonNode.get("access").asText()))) {
                //未设置access,插入默认门禁值

                //获取默认门禁
                String access = visitorService.getDefaultAccess(userinfo, app.getGid() + "", app.getEmpid());
                if (StringUtils.isNotBlank(access)) {
                    ObjectNode rootNode = instance.readValue(appExtendCol, ObjectNode.class);
                    rootNode.put("access", access);
                    app.setAppExtendCol(rootNode.toString());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (userinfo.getProcessSwitch() == 0) {
            app.setPermission(1);
        }
        appointmentService.addAppointment(app);

        if (userinfo.getProcessSwitch() == 1) {
            ProcessRecord pr = new ProcessRecord();
            pr.setLevel(0);
            pr.setSubEmpId(0);
            pr.setSubEmpName(emp.getEmpName());
            pr.setRvwEmpId(emp.getEmpid() + "");
            pr.setRvwEmpName(emp.getEmpName());
            pr.setVid(agroup);
            pr.setUserid(userinfo.getUserid());
            pr.setPType(1);
            int i = processService.addProcessRecord(pr);
            if (i > 0) {
                if (null != emp && null != emp.getOpenid() && !"".equals(emp.getOpenid())) {
                    List<Appointment> atlist = new ArrayList<Appointment>();
                    atlist.add(app);
                    processService.sendAppNewRequestWeiXin(pr, atlist, emp);
                }
            }
        }

        return new RespInfo(0, "success", AESUtil.encode(app.getId() + "", Constant.AES_KEY));
    }


    @ApiOperation(value = "/addAppointmentListByWX 微信访客邀请", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "3.0以后已废弃"
    )
    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "/addAppointmentListByWX")
    @ResponseBody
    public RespInfo addAppointmentListByWX(@RequestBody List<Appointment> appList, HttpServletRequest request) {
        List<Blacklist> blList = new ArrayList<Blacklist>();
        Blacklist bl = new Blacklist();
        Employee emp = null;
        UserInfo userinfo = null;
        List<Appointment> processAppointmentList = new ArrayList<>();
        String agroup = String.valueOf(new Date().getTime());
        for (int a = 0; a < appList.size(); a++) {
            String openid = appList.get(a).getOpenid();
            String temptype = appList.get(a).getVisitType();
            String company = "";
            int gid = appList.get(0).getGid();
            String floors = appList.get(a).getFloors();
            if (StringUtils.isNotEmpty(floors)) {
                floors = floors.substring(0, floors.length() - 1);
            }
            if (null == openid || "".equals(openid)) {
                return new RespInfo(0, "failed");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Employee> emplist = employeeService.getEmpListByOpenid(openid);
            if (emplist.size() > 0) {
                emp = emplist.get(0);
                String token = request.getHeader("X-COOLVISIT-TOKEN");
                token = token.substring(token.indexOf("-") + 1);
                if (!token.equals(emp.getOpenid())) {
                    return new RespInfo(1, "invalid user");
                }
            } else {
                return new RespInfo(55, "no employee");
            }

            Emptemplate et = new Emptemplate();
            et.setUserid(emp.getUserid());
            et.setEmpPhone(emp.getEmpPhone());
            et.setTemplateType(temptype);
            et.setGid(gid);
            userinfo = userService.getUserInfo(emp.getUserid());

            if (userinfo.getBlackListSwitch() == 1) {
                bl.setUserid(userinfo.getUserid());
                bl.setPhone(appList.get(a).getPhone());
                bl.setCredentialNo(appList.get(a).getCardId());
                if (appList.get(a).getGid() != 0) {
                    bl.setGids(appList.get(a).getGid() + "");
                }
                if (emp.getSubaccountId() != 0) {
                    bl.setSids(emp.getSubaccountId() + "");
                }
                if ((StringUtils.isNotBlank(appList.get(a).getPhone()))
                        || (StringUtils.isNotBlank(appList.get(a).getCardId()))) {
                    String sid = String.valueOf(emp.getSubaccountId());
                    List<Blacklist> bli = blacklistService.checkBlacklist(bl);
                    if (bli.size() > 0 && StringUtils.isNotBlank(bli.get(0).getSids())) {
                        String sids[] = bli.get(0).getSids().split(",");
                        boolean b = false;
                        for (int t = 0; t < sids.length; t++) {
                            if (sid.equals(sids[t])) {
                                b = true;
                            }
                        }

                        if (!b || sids.length > 1) {
                            appList.get(a).setbCompany(bli.get(0).getSname());
                        }

                        if (b) {
                            blList.add(bli.get(0));
                        }
                    } else if (bli.size() > 0) {
                        blList.add(bli.get(0));
                    }
                }
            }

            // TODO: 2020/7/2 判断是否周末、节假日、梯控、调休日时间拜访
            String weekendPass = "";
            String holidayPass = "";
            String scPass = "";
            String daysOffPass = "";
            String wxDate = appList.get(0).getWxDate();
            Date appdate = null;
            try {
                appdate = sdf.parse(wxDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (gid > 0 && StringUtils.isNotBlank(floors)) {
                Boolean w = passRuleService.isWeekendPass(appdate, gid, userinfo.getUserid());
                Boolean h = passRuleService.isHolidayPass(appdate, gid, userinfo.getUserid());
                String[] f = floors.split(",");
                int status = passRuleService.getSendCardStatus(userinfo.getUserid(), appdate, f);
                weekendPass = w ? "是" : "否";
                holidayPass = h ? "是" : "否";
                scPass = status == 1 ? "是" : "否";
            }

            Emptemplate emptemp = appointmentService.getEmptemplate(et);
            List<Department> dept = departmentService.getDeptByEmpid(emp.getEmpid(), userinfo.getUserid());
            if (null == emptemp || userinfo.getTempEditSwitch() == 1) {
                Usertemplate ut = new Usertemplate();
                ut.setUserid(emp.getUserid());
                ut.setTemplateType(temptype);
                ut.setGid(gid);

                if (emp.getSubaccountId() == 0) {
                    Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                    if (null == usertemp) {
                        return new RespInfo(59, "no template is found");
                    }
                    appList.get(a).setUserid(emp.getUserid());
                    appList.get(a).setEmpEmail(emp.getEmpEmail());
                    appList.get(a).setEmpName(emp.getEmpName());
                    appList.get(a).setEmpPhone(emp.getEmpPhone());
                    appList.get(a).setAddress(usertemp.getAddress());
                    appList.get(a).setCompanyProfile(usertemp.getCompanyProfile());
                    appList.get(a).setInviteContent(usertemp.getInviteContent());
                    appList.get(a).setLatitude(usertemp.getLatitude());
                    appList.get(a).setLongitude(usertemp.getLongitude());
                    appList.get(a).setTraffic(usertemp.getTraffic());
                    if (null == dept || dept.size() == 0) {
                        appList.get(a).setEmpdeptid(0);
                    } else {
                        appList.get(a).setEmpdeptid(dept.get(0).getDeptid());
                    }
                } else {
                    SubAccountTemplate subacctemp = appointmentService.getSAtemplate(emp.getSubaccountId(), temptype, gid);
                    if (null == subacctemp) {
                        Usertemplate usertemp = appointmentService.getUsertemplate(ut);
                        appList.get(a).setUserid(emp.getUserid());
                        appList.get(a).setEmpEmail(emp.getEmpEmail());
                        appList.get(a).setEmpName(emp.getEmpName());
                        appList.get(a).setEmpPhone(emp.getEmpPhone());
                        appList.get(a).setAddress(usertemp.getAddress());
                        appList.get(a).setCompanyProfile(usertemp.getCompanyProfile());
                        appList.get(a).setInviteContent(usertemp.getInviteContent());
                        appList.get(a).setLatitude(usertemp.getLatitude());
                        appList.get(a).setLongitude(usertemp.getLongitude());
                        appList.get(a).setTraffic(usertemp.getTraffic());
                    } else {
                        appList.get(a).setUserid(emp.getUserid());
                        appList.get(a).setEmpEmail(emp.getEmpEmail());
                        appList.get(a).setEmpName(emp.getEmpName());
                        appList.get(a).setEmpPhone(emp.getEmpPhone());
                        appList.get(a).setAddress(subacctemp.getAddress());
                        appList.get(a).setCompanyProfile(subacctemp.getCompanyProfile());
                        appList.get(a).setInviteContent(subacctemp.getInviteContent());
                        appList.get(a).setLatitude(subacctemp.getLatitude());
                        appList.get(a).setLongitude(subacctemp.getLongitude());
                        appList.get(a).setTraffic(subacctemp.getTraffic());
                    }
                }
            } else {
                appList.get(a).setUserid(emp.getUserid());
                appList.get(a).setEmpEmail(emp.getEmpEmail());
                appList.get(a).setEmpName(emp.getEmpName());
                appList.get(a).setEmpPhone(emp.getEmpPhone());
                appList.get(a).setLatitude(emptemp.getLatitude());
                appList.get(a).setLongitude(emptemp.getLongitude());
                appList.get(a).setAddress(emptemp.getAddress());
                appList.get(a).setTraffic(emptemp.getTraffic());
                appList.get(a).setInviteContent(emptemp.getInviteContent());
                appList.get(a).setCompanyProfile(emptemp.getCompanyProfile());
                if (null == dept || dept.size() == 0) {
                    appList.get(a).setEmpdeptid(0);
                } else {
                    appList.get(a).setEmpdeptid(dept.get(0).getDeptid());
                }
            }
            try {
                Date appDate = sdf.parse(appList.get(a).getWxDate());
                appList.get(a).setAppointmentDate(appDate);
                appList.get(a).setEmpid(emp.getEmpid());
                appList.get(a).setSubaccountId(emp.getSubaccountId());

                if (userinfo.getSubAccount() == 0 || emp.getSubaccountId() == 0) {
                    company = userinfo.getCompany();
                } else {
                    SubAccount sa = subAccountService.getSubAccountById(emp.getSubaccountId());
                    company = sa.getCompanyName();
                }
                appList.get(a).setCompany(company);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                return new RespInfo(0, "date error");
            }
            appList.get(a).setAgroup(agroup);
            RequestVisit rv = new RequestVisit();
            rv.setUserid(appList.get(a).getUserid());
            rv.setPhone(appList.get(a).getPhone());
            rv.setSubaccountId(emp.getSubaccountId());

            VisitorChart vc = visitorService.getVisitSaCountByVphone(rv);
            appList.get(a).setVisitorCount(Integer.parseInt(vc.getCount()));

            appList.get(a).setIsWeekendVisitor(weekendPass);
            appList.get(a).setIsHolidayVisitor(holidayPass);
            appList.get(a).setIsSCTimeVisitor(scPass);
            appList.get(a).setIsDaysOffVisitor(daysOffPass);
            appList.get(a).setFloors(floors);

            //access字段
            String appExtendCol = appList.get(a).getAppExtendCol();
            ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
            try {
                JsonNode jsonNode = instance.readValue(appExtendCol, JsonNode.class);
                if (null != jsonNode
                        && (jsonNode.get("access") == null
                        || StringUtils.isBlank(jsonNode.get("access").asText()))) {
                    //未设置access,插入默认门禁值

                    //获取默认门禁
                    String access = visitorService.getDefaultAccess(userinfo, appList.get(a).getGid() + "", appList.get(a).getEmpid());
                    if (StringUtils.isNotBlank(access)) {
                        ObjectNode rootNode = instance.readValue(appExtendCol, ObjectNode.class);
                        rootNode.put("access", access);
                        appList.get(a).setAppExtendCol(rootNode.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            appointmentService.addAppointment(appList.get(a));

            /**
             * 流程审批判断
             */
            if (userinfo.getProcessSwitch() == 0) {
                if (null != appList.get(a).getPhone() && appList.get(a).getPhone().trim().length() == 11) {
                    Visitor vt = new Visitor();
                    vt.setVid(appList.get(a).getId());
                    vt.setVname(appList.get(a).getName());
                    vt.setVphone(appList.get(a).getPhone());
                    vt.setVisitType(appList.get(a).getVisitType());
                    try {
                        String code = visitorService.sendAppointmentSMS(userinfo, emp, vt);
                        appList.get(a).setSendStatus(2);

                        if ("0".equals(code)) {
                            appList.get(a).setSendStatus(1);
                        } else if ("43".equals(code)) {
                            return new RespInfo(43, "exceed the maximum limit");
                        } else {
                            appList.get(a).setSendStatus(2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (null != appList.get(a).getVemail() && !"".equals(appList.get(a).getVemail().trim())) {
                    SendInviteEmail si = new SendInviteEmail();
                    si.setRealpath(request.getSession().getServletContext().getRealPath("/"));

                    if (si.send(userinfo, appList.get(a))) {
                        appList.get(a).setSendStatus(1);
                    } else {
                        appList.get(a).setSendStatus(2);
                    }
                }

            } else {
                processAppointmentList.add(appList.get(a));
            }
        }

        if (processAppointmentList.size() > 0 && userinfo.getProcessSwitch() == 1) {
            /**
             * 开始邀请工作流
             */
            Map<String, Object> camundaParam = new HashMap<>();
            camundaParam.put("userid", userinfo.getUserid() + "");
            camundaParam.put("employee", emp.getEmpid() + "");
            camundaParam.put("realPath", request.getSession().getServletContext().getRealPath("/"));
            camundaParam.put("epidemic", userinfo.getEpidemic());
            int gid = processAppointmentList.get(0).getGid();
            Gate gate = new Gate();
            gate.setGids(gid + "");
            gate.setUserid(userinfo.getUserid());
            List<Gate> gates = addressService.getGateById(gate);
            if (gates != null && gates.size() > 0) {
                String gname = gates.get(0).getGname();
                camundaParam.put("gname", gname);
            }
            RuntimeService runtimeService = processEngine.getRuntimeService();
            try {
                String leaderEmpId = "";
                System.out.println("businessKey: " + "a" + agroup);
                List<Department> deptByEmpid = departmentService.getDeptByEmpid(emp.getEmpid(), userinfo.getUserid());
                if (!deptByEmpid.isEmpty()) {
                    String deptManagerEmpid = deptByEmpid.get(0).getDeptManagerEmpid();
                    if (StringUtils.isNotBlank(deptManagerEmpid)) {
                        leaderEmpId = deptManagerEmpid;
                    } else {
                        System.out.println("员工：" + emp.getEmpName() + " deptManager is null");
                        return new RespInfo(-1, "deptManager is null");
                    }
                }
                ProcessInstance processInstance = runtimeService
                        .startProcessInstanceByKey("visitorApprove", "a" + agroup, camundaParam);
                /**
                 * 员工完成员工节点的节点任务
                 */
                TaskService taskService = processEngine.getTaskService();
                Task visittask = taskService.createTaskQuery()
                        .processInstanceBusinessKey("a" + agroup)
                        .processInstanceId(processInstance.getId())         //流程定义的id
                        .taskAssignee(emp.getEmpid() + "")                   //只查询该任务负责人的任务
                        .singleResult();
                taskService.setVariable(visittask.getId(), "leader", leaderEmpId);
                Map<String, Object> param = new HashMap<>();
                param.put("status", 1);
                taskService.createComment(visittask.getId(), visittask.getProcessInstanceId(), "员工发起邀请审批");
                taskService.complete(visittask.getId(), param);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ProcessEngineException(e.getMessage());
            }
        }

        if (blList.size() > 0) {
            return new RespInfo(66, "user was added to the blacklist", blList);
        }

        if (appList.size() != 0) {
            appointmentService.batchUpdateAppSendStatus(appList);
        }

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/updateAppointmentReply 更新邀请答复", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"id\":\"83E544945C14082D08620AA5BFBDC26B\",\n" +
                    "    \"status\":3\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateAppointmentReply")
    @ResponseBody
    public RespInfo updateAppointmentReply(@RequestBody ReqAppointment ra) {
        String id = AESUtil.decode(ra.getId(), Constant.AES_KEY);
        if(id.startsWith("v")){
            id = id.substring(1);
            Visitor visitor = visitorService.getVisitorById(Integer.parseInt(id));
            if(visitor == null){
                return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
            }
            visitor.setStatus(ra.getStatus());
            visitorService.VisitorReply(visitor);
            if (visitor.getStatus() == 3
                    && visitor.getPermission() == VisitorService.PERMISSION_ACCEPT
                    && Constant.AccessWithoutSignin.equals("1")) {
                List<Visitor> visitorList = new ArrayList<Visitor>();
                visitorList.add(visitor);
                passService.passAuth(visitorList,  PassEvent.Pass_Add);
            }
            if (visitor.getMid() == 0) {
                if (visitor.getStatus() == 3) {
                    messageService.sendCommonNotifyEvent(visitor, NotifyEvent.EVENTTYPE_VISITOR_ACCEPT);
                } else if (visitor.getStatus() == 4) {
                    messageService.sendCommonNotifyEvent(visitor, NotifyEvent.EVENTTYPE_VISITOR_REJECT);
                }

            }
            return new RespInfo(0, "success");
        }else if(id.startsWith("a")){
            id = id.substring(1);
        }
        int aid = Integer.parseInt(id);
        Appointment at = new Appointment();

        at.setId(aid);
        at.setStatus(ra.getStatus());
        Appointment appointmentbyId = appointmentService.getAppointmentbyId(aid);
        if (ObjectUtils.isNotEmpty(appointmentbyId)) {
            int status = appointmentbyId.getStatus();
            if (5 == status) {
                return new RespInfo(-1, "fail");
            }
        }
        if ((at.getStatus() == 3 || at.getStatus() == 4) && (appointmentbyId.getStatus() == 0 || appointmentbyId.getStatus() == 6)) {
            appointmentService.AppointmentReply(at);
            Appointment a = appointmentService.getAppointmentbyId(at.getId());
            if (at.getStatus() == 3
                    && at.getPermission() == VisitorService.PERMISSION_ACCEPT
                    && Constant.AccessWithoutSignin.equals("1")) {
                List<Appointment> appointmentList = new ArrayList<Appointment>();
                appointmentList.add(a);
                passService.passAuth(appointmentList,  PassEvent.Pass_Add);
            }
            if (a.getMid() == 0) {
                if (a.getStatus() == 3) {
                    messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(a), NotifyEvent.EVENTTYPE_VISITOR_ACCEPT);
                } else if (a.getStatus() == 4) {
                    messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(a), NotifyEvent.EVENTTYPE_VISITOR_REJECT);
                }

            }
        }

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/appointmentSignin 邀请签到", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"userid\",\n" +
                    "    \"id\":\"id\",\n" +
                    "    \"phone\":\"phone\",\n" +
                    "    \"cardId\":\"cardId\",\n" +
                    "    \"vemail\":\"vemail\",\n" +
                    "    \"photoUrl\":\"photoUrl\",\n" +
                    "    \"remark\":\"remark\",\n" +
                    "    \"plateNum\":\"plateNum\",\n" +
                    "    \"signInGate\":\"signInGate\",\n" +
                    "    \"signInOpName\":\"signInOpName\",\n" +
                    "    \"sex\":\"sex\",\n" +
                    "    \"extendCol\":\"[\"moreTimes=1\",\"access=1\"]\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/appointmentSignin")
    @ResponseBody
    public RespInfo appointmentSignin(@RequestBody Appointment at, HttpServletRequest request) throws JsonProcessingException, ParseException {


        String company = "";
        String phone = at.getPhone();
        String cardId = at.getCardId();
        String vemail = at.getVemail();
        int userid = at.getUserid();
        int i = 0;
        int aid = at.getId();

        Appointment ap = null;
        if (aid > 0) {
            ap = appointmentService.getAppointmentbyId(aid);
        } else {
            if (null != phone && !"".equals(phone)) {
                ap = appointmentService.getAppointmentByWPhone(at);
            }
        }

        if (null == ap) {
            SysLog.error("not find this record:", at);
            return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole()))
                || authToken.getUserid() != ap.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userinfo = userService.getUserInfo(ap.getUserid());

        if (userinfo.getBlackListSwitch() == 1) {
            Blacklist bl = new Blacklist();
            bl.setUserid(userinfo.getUserid());
            bl.setPhone(phone);
            bl.setCredentialNo(cardId);
            if (ap.getSubaccountId() != 0) {
                bl.setSids(ap.getSubaccountId() + "");
            }

            if ((null != phone && !"".equals(phone)) || (null != cardId && !"".equals(cardId))) {
                List<Blacklist> blList = blacklistService.checkBlacklist(bl);
                if (blList.size() > 0) {
                    return new RespInfo(ErrorEnum.E_066.getCode(), ErrorEnum.E_066.getMsg());
                }
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = dateFormat.format(new Date());
        String appTime = dateFormat.format(ap.getAppointmentDate());

        Date et = dateFormat.parse(endTime);
        Date att = dateFormat.parse(appTime);

        long as = (et.getTime() - att.getTime()) / (24 * 60 * 60 * 1000L);
        int day = Integer.parseInt(String.valueOf(as));
        if (day < 0) {
            day = 0;
        }

        Calendar calendar = Calendar.getInstance();

        /**
         * 有效期检查
         */
        int m = UtilTools.differentDays(ap.getAppointmentDate(), new Date());
        if (m < 0) {
            //预约日期未到
            return new RespInfo(ErrorEnum.E_064.getCode(), ErrorEnum.E_064.getMsg());
        }

        //结束时间
        try {
            String endDate = ap.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
            if(StringUtils.isNotEmpty(endDate)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("gmt"));
                if (format.parse(endDate).getTime() < new Date().getTime()) {
                    //已过期
                    return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
                }
            }else if ((m + 1) > ap.getQrcodeConf()) {
                //已过期
                return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
            }
        }catch (Exception e){
            return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg());
        }


        //非会议，非多天情况,检查时间
        long now = new Date().getTime();
        if (userinfo.getPreExtendTime() != 0 && ap.getMid() == 0 && ap.getQrcodeConf() <= 1) {
            long preExtendTime = userinfo.getPreExtendTime() * 60000L;
            calendar.setTime(ap.getAppointmentDate());
            long appdate = calendar.getTimeInMillis();


            if ((appdate - now) > preExtendTime) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("appointmentDate", appdate);
                map.put("preExtendTime", userinfo.getPreExtendTime());
                return new RespInfo(ErrorEnum.E_064.getCode(), ErrorEnum.E_064.getMsg(), map);
            }
        }

        if (userinfo.getLatExtendTime() != 0 && ap.getMid() == 0 && ap.getQrcodeConf() <= 1) {
            long latExtendTime = userinfo.getLatExtendTime() * 60000L;
            calendar.setTime(ap.getAppointmentDate());
            long appdate = calendar.getTimeInMillis();

            if ((now - appdate) > latExtendTime) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("appointmentDate", appdate);
                map.put("latExtendTime", userinfo.getLatExtendTime());
                return new RespInfo(ErrorEnum.E_065.getCode(), ErrorEnum.E_065.getMsg(), map);
            }
        }


        //邀请被拒绝或取消
        if (ap.getStatus() == 4 || ap.getStatus() == 5
                || ap.getPermission() != 1) {
            return new RespInfo(ErrorEnum.E_067.getCode(), ErrorEnum.E_067.getMsg());
        }

        if (ap.getPermission() == 0) {
            return new RespInfo(ErrorEnum.E_1121.getCode(), ErrorEnum.E_1121.getMsg());
        }

        //TODO 已签到情况是否要禁止重复签到


        Visitor vt = null;
        try {
            //检查员工状态
            Employee emp = visitorService.checkEmployeeTask(ap.getEmpid() + "");
            //检查答题状态
            visitorService.checkQuestionnaireTask(userinfo, BeanUtils.appointmentToVisitor(ap));
            vt = appointmentService.addAppSigninTask(at, userinfo);
        } catch (ErrorException e) {
            return new RespInfo(e.getErrorEnum().getCode(), e.getErrorEnum().getMsg());
        }

        //下发权限
        List<Appointment> appointmentList = new ArrayList<Appointment>();
        appointmentList.add(ap);
        passService.passAuth(appointmentList, PassEvent.Pass_Add);

        //发送通知
        messageService.sendCommonNotifyEvent(vt, NotifyEvent.EVENTTYPE_CHECK_IN);

        aid = vt.getVid();
        int count = visitorService.getVisitorCount(userid);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("vcount", count);
        maps.put("photoUrl", ap.getPhotoUrl());
        maps.put("name", ap.getName());
        maps.put("visitType", ap.getVisitType());
        maps.put("empName", ap.getEmpName());
        maps.put("phone", ap.getPhone());
        maps.put("company", company);
        maps.put("peopleNum", 1);
        maps.put("vcompany", ap.getVcompany());
        maps.put("remark", ap.getRemark());
        maps.put("vid", aid);
        maps.put("aid", IvrData.APPOINTMENT_VISIT_TYPE + ap.getId());

        return new RespInfo(0, "success", maps);
    }

    @ApiOperation(value = "/updateAppPreview 更新邀请函预览", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateAppPreview")
    @ResponseBody
    public RespInfo updateAppPreview(@RequestBody Appointment app) {
        appointmentService.updateAppPreview(app);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getTodayAppointmentByPhone 根据手机号获取当天邀请记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getTodayAppointmentByPhone")
    @ResponseBody
    public RespInfo getTodayAppointmentByPhone(@RequestBody Visitor vt) {
        Visitor v = visitorService.getTodayAppointmentByPhone(vt);
        return new RespInfo(0, "success", v);
    }

    @ApiOperation(value = "/getAppointmentbySecId 通过SecId获取邀请记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAppointmentbySecId")
    @ResponseBody
    public RespInfo getAppointmentbySecId(@RequestBody ReqAppointment ra) {
        Appointment a = null;
        String appid = AESUtil.decode(ra.getId(), Constant.AES_KEY);
        if (appid.startsWith("v")) {
            int vid = Integer.parseInt(appid.substring(1));
            Visitor visitor = visitorService.getVisitorById(vid);
            a = BeanUtils.VisitorToAppointment(visitor);
        } else {
            int aid = Integer.parseInt(AESUtil.decode(ra.getId(), Constant.AES_KEY));
            a = appointmentService.getAppointmentbyId(aid);
        }
        if (null != a) {
            AuthToken authToken = new AuthToken();
            authToken.setAccountRole(AuthToken.ROLE_VISITOR);
            authToken.setLoginAccountId(authToken.getLoginAccountId());
            authToken.setUserid(a.getUserid());
            authToken.setDateTime(new Date().getTime());
            String token = tokenServer.getAESEncoderTokenString(authToken);
            HashOperations hashOperations = redisTemplate.opsForHash();
            Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
            hashOperations.put(token, "id", exprieDate.getTime());
            redisTemplate.expire(token, 12, TimeUnit.HOURS);
            a.setToken(token);
        }
        return new RespInfo(0, "success", a);
    }


    @Deprecated
    @ApiOperation(value = "/uploadAppointmentPhoto 在邀请函中上传照片,3.7版本后改为updateSupplementAppointment", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = ""
    )
    @RequestMapping(method = RequestMethod.POST, value = "/uploadAppointmentPhoto")
    @ResponseBody
    public RespInfo uploadAppointmentPhoto(@RequestBody RequestFI rfi) {
        String id = AESUtil.decode(rfi.getId(), Constant.AES_KEY);
        Appointment app = appointmentService.getAppointmentbyId(Integer.parseInt(id));
        if (null != app) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "invitation_avatar_uploaded");
            map.put("visit_id", "a" + app.getId());
            map.put("company_id", app.getUserid());
            String appExtendCol = app.getAppExtendCol();
            ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
            try {
                JsonNode jsonNode = instance.readValue(appExtendCol, JsonNode.class);
                if (null != jsonNode && jsonNode.get("access") != null && StringUtils.isNotBlank(jsonNode.get("access").asText())) {
                    String access = jsonNode.get("access").asText();
                    map.put("group_ids", access);
                } else {
                    //未设置access

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            messageSender.updateFaceLib(map);

            appointmentService.updatePhotoUrl(app.getId(), rfi.getPhotoUrl());
        }
        return new RespInfo(0, "success");
    }

    public void sendNotify(Object... o) {
        ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
        EmpVisitProxy vp = objectMapper.convertValue(o[0], EmpVisitProxy.class);
        Employee empProxy = objectMapper.convertValue(o[1], Employee.class);
        UserInfo userinfo = objectMapper.convertValue(o[2], UserInfo.class);
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ExtendVisitor.class);
        List<ExtendVisitor> evlist = objectMapper.convertValue(o[3], javaType);
        Visitor vt = objectMapper.convertValue(o[4], Visitor.class);
        Employee emp = objectMapper.convertValue(o[5], Employee.class);

        Date pdate = new Date();
        Date sdate = null;
        Date edate = null;
        if (null != vp) {
            empProxy = employeeService.getEmployee(vp.getProxyId());
            sdate = vp.getStartDate();
            edate = vp.getEndDate();
        }

        boolean signleNotify = false;

        if (StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getMsgNotify() == 1) {
            visitorService.sendWeixin(userinfo, emp, vt);
            if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                visitorService.sendWeixin(userinfo, empProxy, vt);
            }

            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }

        if (StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getWxBusNotify() == 1) {
            if (!signleNotify || userinfo.getNotifyType() == 0) {
                visitorService.sendNotifyByWXBus(emp, vt);

                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    visitorService.sendNotifyByWXBus(empProxy, vt);
                }
            }

            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }

        if (StringUtils.isNotBlank(emp.getDdid()) && userinfo.getDdnotify() == 1) {
            if (!signleNotify || userinfo.getNotifyType() == 0) {
                visitorService.sendNotifyByDD(emp, vt);

                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    visitorService.sendNotifyByDD(empProxy, vt);
                }
            }

            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }

        if (StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getFsNotify() == 1) {
            if (!signleNotify || userinfo.getNotifyType() == 0) {
                visitorService.sendNotifyByFeiShu(emp, vt);

                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    visitorService.sendNotifyByFeiShu(empProxy, vt);
                }
            }

            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }

        if (userinfo.getEmailType() != 0 && StringUtils.isNotBlank(emp.getEmpEmail())) {
            logger.info("sendMail:" + emp.getEmpEmail());
            try {
                if (!signleNotify || userinfo.getNotifyType() == 0) {
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

            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }

        if (userinfo.getSmsNotify() == 1 && emp.getEmpPhone().length() == 11) {
            if (StringUtils.isNotBlank(emp.getEmpPhone())) {
                if (!signleNotify || userinfo.getNotifyType() == 0) {
                    visitorService.sendSMS(userinfo, emp, vt);
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
        private Employee emp;

        SendNodifytoDefault(UserInfo userinfo, Visitor vt, Employee emp) {
            this.userinfo = userinfo;
            this.vt = vt;
            this.emp = emp;
        }

        public void run() {
            EmpVisitProxy vp = new EmpVisitProxy();
            Employee empProxy = new Employee();
            vp.setEmpid(emp.getEmpid());
            vp.setUserid(userinfo.getUserid());
            vp = visitProxyService.getProxyInfoByEid(vp);
            List<ExtendVisitor> evlist = new ArrayList<ExtendVisitor>();
            sendNotify(vp, empProxy, userinfo, evlist, vt, emp);
        }
    }

    @ApiOperation(value = "/getVisitorJoinMeeting 分页获取参会人员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/getVisitorJoinMeeting")
    @ResponseBody
    public RespInfo getJoinMeeting(@RequestBody RequestVisit requestVisit, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page<Appointment> page = new Page<Appointment>(requestVisit.getStartIndex() / requestVisit.getRequestedCount() + 1, requestVisit.getRequestedCount(), 0);
        requestVisit.setPage(page);
        List<Appointment> list = this.appointmentService.getAppointmentByMidOrder(requestVisit);
        page.setList(list);
        return new RespInfo(0, "success", page);
    }

    @ApiOperation(value = "/updateJoinMeetingSign 星云修改会议人员签到状态", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/updateJoinMeetingSign")
    @ResponseBody
    public RespInfo updateJoinMeetingSignStatus(@RequestBody RequestVisit requestVisit, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != requestVisit.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int result = this.appointmentService.updateAppointmentJoinMeetingSign(requestVisit);
        return new RespInfo(result, "success", result);
    }

    @ApiOperation(value = "/uploadAppointmentFaceResult 适配层通知来访通，某个访客头像同步失败", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/uploadAppointmentFaceResult")
    @ResponseBody
    public RespInfo uploadAppointmentFaceResult(@RequestBody RequestVisit requestVisit, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) || authToken.getUserid() != requestVisit.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int result = this.appointmentService.updateAppointmentFace(requestVisit);
        return new RespInfo(result, "success");
    }

    @ApiOperation(value = "/addReserveMeeting 预约会议", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/addReserveMeeting")
    @ResponseBody
    public RespInfo addReserveMeeting(@RequestBody Appointment appointment, HttpServletRequest request) {
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if ((!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                    && AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                    && AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                    && AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))
                    || authToken.getUserid() != appointment.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        } catch (RuntimeException e) {
            //没有token就检查签名
            RespInfo respInfo = tokenServer.checkSign(request);
            if (null != respInfo) {
                return respInfo;
            }
        }



        /*int result = this.appointmentService.addReserveMeeting(appointment);
        if (0 == result){
            return new RespInfo(0,"success");
        }else{
            return new RespInfo(-1,"fail add reserveMeeting");
        }*/


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Meeting meetingById = this.meetingService.getMeetingById(appointment.getMid());
        if (ObjectUtils.isEmpty(meetingById)) {
            return new RespInfo(ErrorEnum.E_110.getCode(), ErrorEnum.E_110.getMsg());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(meetingById.getDeadlineTime())) {
            try {
                //预约不可以超过截止时间
                Date ddl = dateFormat.parse(meetingById.getDeadlineTime());
                if (new Date().getTime() > ddl.getTime()) {
                    return new RespInfo(ErrorEnum.E_111.getCode(), ErrorEnum.E_111.getMsg());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //取消状态不可以预约
        if (0 == meetingById.getStatus()) {
            return new RespInfo(ErrorEnum.E_109.getCode(), ErrorEnum.E_109.getMsg());
        }
        //同一手机号不可以重复预约相同会议
        Appointment appointment1 = this.appointmentService.getAppointmentByPhoneAndMid(appointment);
        if (ObjectUtils.isNotEmpty(appointment1)) {
            return new RespInfo(ErrorEnum.E_112.getCode(), ErrorEnum.E_112.getMsg());
        }
        UserInfo userInfo = this.userService.getUserInfo(appointment.getUserid());
        appointment.setCompany(userInfo.getCompany());
        appointment.setMeetingStatus(0);
        appointment.setAppointmentDate(new Date());
        int i = this.appointmentService.addReserveMeeting(appointment);
        if (i > 0) {
            return new RespInfo(0, "success", i);
        } else {
            return new RespInfo(ErrorEnum.E_113.getCode(), ErrorEnum.E_113.getMsg());
        }

    }

    /*@PostMapping("/getReviewMeeting")
    @ResponseBody
    public RespInfo getReviewMeeting(@RequestBody RespVisitor visitor){
        Page<Appointment> page = new Page<Appointment>(visitor.getStartIndex() / visitor.getRequestedCount() + 1, visitor.getRequestedCount(), 0);
        visitor.setPage(page);
        List<Appointment> appointments = this.appointmentService.getReviewMeeting(visitor);
        page.setList(appointments);
        return new RespInfo(0,"success",page);
    }*/

    @ApiOperation(value = "/updateReviewMeeting 审批预约会议人员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/updateReviewMeeting")
    @ResponseBody
    public RespInfo updateReviewMeetingNotify(@RequestBody Appointment appointment, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()) && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        /*int result = this.appointmentService.updateReviewMeetingStatus(appointment);
        if (result > 0){
            return new RespInfo(0,"success");
        }else{
            return new RespInfo(-1,"success");
        }*/

        Appointment appointmentbyId = this.appointmentService.getAppointmentbyId(appointment.getId());
        if (ObjectUtils.isEmpty(appointmentbyId)) {
            return new RespInfo(ErrorEnum.E_114.getCode(), ErrorEnum.E_114.getMsg());
        }
        if (appointment.getMeetingStatus() == 0 && appointmentbyId.getMeetingStatus() == 1) {
            return new RespInfo(115, "Approved");
        }
        if (appointment.getMeetingStatus() == 1 && appointmentbyId.getMeetingStatus() == 2) {
            return new RespInfo(115, "Has been canceled");
        }
        int mid = appointmentbyId.getMid();
        Meeting meetingById = this.meetingService.getMeetingById(mid);

        UserInfo userInfo = userService.getUserInfo(appointmentbyId.getUserid());
        Visitor visitor = new Visitor();
        visitor.setVname(appointmentbyId.getName());
        visitor.setVid(appointmentbyId.getId());
        visitor.setVphone(appointmentbyId.getPhone());
        visitor.setRemark(meetingById.getSubject());
        visitor.setExtendCol(meetingById.getStartTime() + "" + meetingById.getEndTime());


        Usertemplate ut = new Usertemplate();
        ut.setGid(appointmentbyId.getGid());
        ut.setUserid(appointmentbyId.getUserid());
        ut.setTemplateType(appointmentbyId.getVisitType());
        Usertemplate usertemplate = appointmentService.getUsertemplate(ut);

        appointment.setInviteContent(usertemplate.getInviteContent());
        appointment.setAddress(usertemplate.getAddress());
        appointment.setLongitude(usertemplate.getLongitude());
        appointment.setLatitude(usertemplate.getLatitude());
        appointment.setCompanyProfile(usertemplate.getCompanyProfile());
        appointment.setTraffic(usertemplate.getTraffic());

        Map<String, Object> params = new HashMap<>();
        params.put("name", appointmentbyId.getName());
        params.put("phone", appointmentbyId.getPhone());
        params.put("visitType", appointmentbyId.getVisitType());
        params.put("vcompany", appointmentbyId.getVcompany());

        //转换json
        String requestJson = JsonHelper.toJsonString(params);

        appointment.setAppExtendCol(requestJson);

        //同意预约
        if (1 == appointment.getMeetingStatus()) {
            try {
                visitorService.sendAppointmentMeetingSMS(userInfo, visitor, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (2 == appointment.getMeetingStatus()) {
            try {
                visitorService.sendAppointmentMeetingSMS(userInfo, visitor, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int result = 0;
        try {
            result = this.appointmentService.updateReviewMeetingStatus(appointment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result > 0) {
            return new RespInfo(0, "sucess");
        } else {
            return new RespInfo(115, "Update failed");
        }
    }

    @ApiOperation(value = "/updateSupplementAppointment 更新邀请函访客数据,补填", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/updateSupplementAppointment")
    @ResponseBody
    public RespInfo updateSupplementAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {

        String business_id = AESUtil.decode(appointment.getSecid(), Constant.AES_KEY);
        try {
            if (business_id.startsWith("v")) {
                //预约访客
                int vid = Integer.parseInt(business_id.substring(1));
                Visitor vt = visitorService.getVisitorById(vid);
                if (vt == null) {
                    return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
                }
                UserInfo userinfo = userService.getUserInfo(vt.getUserid());
                if (userinfo == null) {
                    return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
                }
                Visitor visitor = BeanUtils.appointmentToVisitor(appointment);
                visitor.setVid(vid);
                visitor.setUserid(vt.getUserid());
                visitor.setStatus(vt.getStatus());
                if (VisitorService.PERMISSION_FIRST != vt.getPermission()
                        && VisitorService.PERMISSION_SEC != vt.getPermission()) {
                    return new RespInfo(ErrorEnum.E_058.getCode(), ErrorEnum.E_058.getMsg());
                }

                visitorService.updateSupplementTask(visitor);
                //非必填的情况可以继续补填
                int supplementType = visitorService.supplementTypeRouter(vt);
                if (supplementType == 2) {
                    visitor.setStatus(3);
                    visitorService.updateStatusByVidTask(visitor);
                }

                //二次补填授权流程
                vt = visitorService.getVisitorById(vid);
                visitorService.supplementPermissonSubBPM(vt, userinfo);
            } else {
                //邀请访客
                int aid = Integer.parseInt(business_id);
                Appointment app = appointmentService.getAppointmentbyId(aid);
                if (null == app) {
                    return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
                }

                UserInfo userinfo = userService.getUserInfo(app.getUserid());
                if (userinfo == null) {
                    return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
                }

                appointment.setId(app.getId());
                appointment.setUserid(app.getUserid());
                appointment.setStatus(app.getStatus());

                if (VisitorService.PERMISSION_FIRST != app.getPermission()
                        && VisitorService.PERMISSION_SEC != app.getPermission()) {
                    return new RespInfo(ErrorEnum.E_058.getCode(), ErrorEnum.E_058.getMsg());
                }

                //修改补填数据
                appointmentService.updateSupplementAppointmentTask(appointment);
                //非必填的情况可以继续补填
                int supplementType = visitorService.supplementTypeRouter(BeanUtils.appointmentToVisitor(app));
                if (supplementType == 2) {
                    appointment.setStatus(3);
                    appointmentService.updateAppointmentStatusByIdTask(appointment);
                }

                //二次补填授权流程
                app = appointmentService.getAppointmentbyId(aid);
                appointmentService.supplementPermissonSubBPM(app, userinfo);

            }

        } catch (ProcessEngineException e) {
            return new RespInfo(ErrorEnum.E_2000.getCode(), e.getMessage());
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException errorException = (ErrorException) e;
                return new RespInfo(errorException.getErrorEnum().getCode(), errorException.getErrorEnum().getMsg());
            }
            e.printStackTrace();
            SysLog.error("更新邀请函失败！" + appointment.getId());
            return new RespInfo(59, "The update failed");
        }

        return new RespInfo(0, "success");

    }

    @ApiOperation(value = "/empReconfirm 员工确认访客补填写信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "    [{\n" +
                    "        \"vid\":\"0\",\n" +
                    "        \"appid\":\"1234\",\n" +
                    "        \"permission\":\"商务#Business\",\n" +
                    "        \"remark\":\"1615877866000\",\n" +
                    "    }]\n")
    @PostMapping("/empReconfirm")
    @ResponseBody
    public RespInfo empReconfirm(@RequestBody List<Visitor> vlist, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        String permissionName = "manager";
        for (Visitor visitor : vlist) {
            int vid = visitor.getVid();
            int aid = visitor.getAppid();
            Visitor vt = null;

            if (aid != 0) {
                Appointment app = appointmentService.getAppointmentbyId(aid);
                if (null == app) {
                    return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
                }

                if (authToken.getUserid() != app.getUserid()) {
                    return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }

                if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
                    Employee employee = employeeService.getEmployee(app.getEmpid());
                    if (employee != null) {
                        permissionName = employee.getEmpName();
                    }

                    //访客记录的员工id跟请求的token中的empid不一致：查询自己的代理人
                    if (employee.getEmpid() != Integer.parseInt(authToken.getLoginAccountId())) {
                        //判断是不是自己代理人
                        EmpVisitProxy empVisitProxy = new EmpVisitProxy();
                        empVisitProxy.setEmpid(app.getEmpid());
                        empVisitProxy.setUserid(authToken.getUserid());
                        EmpVisitProxy proxyInfoByEid = visitProxyService.getProxyInfoByEid(empVisitProxy);
                        //如果既不是自己的访客记录，也不是自己的代理人的，直接返回非法请求
                        if (null == proxyInfoByEid || proxyInfoByEid.getProxyId() != Integer.parseInt(authToken.getLoginAccountId())) {
                            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                        }
                    }
                }

                if (VisitorService.PERMISSION_FIRST != app.getPermission()
                        && VisitorService.PERMISSION_SEC != app.getPermission()) {
                    return new RespInfo(ErrorEnum.E_070.getCode(), ErrorEnum.E_070.getMsg());
                }

                app.setPermission(visitor.getPermission());
                app.setRemark(visitor.getRemark());
                app.setAppExtendCol(visitor.getExtendCol());
                if (1 != app.getPermission() && 2 != app.getPermission()) {
                    return new RespInfo(ErrorEnum.E_058.getCode(), ErrorEnum.E_058.getMsg());
                }

                if (visitor.getPermission() == 1) {
                    //同意
                    appointmentService.approvalSubBPM(app, false);
                } else {
                    //拒绝
                    appointmentService.completeSupplementTask(app);
                }

            } else if (vid != 0) {
                vt = visitorService.getVisitorById(vid);
                if (null == vt) {
                    return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
                }

                if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
                    Employee employee = employeeService.getEmployee(vt.getEmpid());
                    if (employee != null) {
                        permissionName = employee.getEmpName();
                    }

                    //访客记录的员工id跟请求的token中的empid不一致：查询自己的代理人
                    if (employee.getEmpid() != Integer.parseInt(authToken.getLoginAccountId())) {
                        //判断是不是自己代理人
                        EmpVisitProxy empVisitProxy = new EmpVisitProxy();
                        empVisitProxy.setEmpid(vt.getEmpid());
                        empVisitProxy.setUserid(authToken.getUserid());
                        EmpVisitProxy proxyInfoByEid = visitProxyService.getProxyInfoByEid(empVisitProxy);
                        //如果既不是自己的访客记录，也不是自己的代理人的，直接返回非法请求
                        if (null == proxyInfoByEid || proxyInfoByEid.getProxyId() != Integer.parseInt(authToken.getLoginAccountId())) {
                            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                        }
                    }
                }

                if (authToken.getUserid() != vt.getUserid()) {
                    return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }
                if (VisitorService.PERMISSION_FIRST != vt.getPermission()
                        && VisitorService.PERMISSION_SEC != vt.getPermission()) {
                    return new RespInfo(ErrorEnum.E_070.getCode(), ErrorEnum.E_070.getMsg());
                }

                vt.setPermissionName(permissionName);
                vt.setPermission(visitor.getPermission());
                vt.setExtendCol(visitor.getExtendCol());
                vt.setRemark(visitor.getRemark());
                if (1 != vt.getPermission() && 2 != vt.getPermission()) {
                    return new RespInfo(ErrorEnum.E_058.getCode(), ErrorEnum.E_058.getMsg());
                }


                if (visitor.getPermission() == 1) {
                    //同意
                    visitorService.approvalSubBPM(vt, false);
                } else {
                    //拒绝
                    visitorService.completeSupplementTask(vt);
                }

            } else {
                return new RespInfo(60, "update fail");
            }
        }

        return new RespInfo(ErrorEnum.E_0.getCode(), ErrorEnum.E_0.getMsg());

    }

    @Deprecated
    @ApiOperation(value = "/getAppointmentaGroupBySecid 根据secid获取同批邀请人员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[\n" +
                    "    {\n" +
                    "        \"name\":\"测试\",\n" +
                    "        \"phone\":\"15251805548\",\n" +
                    "        \"visitType\":\"商务#Business\",\n" +
                    "        \"appointmentDate\":\"1615877866000\",\n" +
                    "        \"openid\":\"oHriHwe_sYbEctEbQ8QDm7bLkOmA\",\n" +
                    "        \"tid\":181,\n" +
                    "        \"gid\":1,\n" +
                    "        \"vtype\":\"普通访客#Normal Visitor\",\n" +
                    "        \"qrcodeType\":0,\n" +
                    "        \"qrcodeConf\":1,\n" +
                    "        \"remark\":\"\",\n" +
                    "        \"clientNo\":1,\n" +
                    "        \"appExtendCol\":\"{\\\"name\\\":\\\"测试\\\",\\\"phone\\\":\\\"15251805548\\\",\\\"appointmentDate\\\":\\\"2020-12-04 15:00\\\",\\\"email\\\":\\\"\\\",\\\"visitType\\\":\\\"商务\\\",\\\"qrcodeConf\\\":1,\\\"remark\\\":\\\"\\\",\\\"gateType\\\":\\\"1号楼\\\",\\\"secret\\\":\\\"n\\\"}\"\n" +
                    "    }\n" +
                    "]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAppointmentaGroupBySecid")
    @ResponseBody
    public RespInfo getAppointmentaGroupBySecid(@RequestBody Appointment appointment,
                                                HttpServletRequest req, BindingResult result) {
        if (StringUtils.isNotBlank(appointment.getSecid())) {
            return new RespInfo(57, "no records");
        }
        String appid = AESUtil.decode(appointment.getSecid(), Constant.AES_KEY);
        if (appid.startsWith("v")) {
            int vid = Integer.parseInt(appid.substring(1));

            return new RespInfo(57, "no records");
        } else {
            int aid = Integer.parseInt(AESUtil.decode(appointment.getSecid(), Constant.AES_KEY));
            Appointment app = appointmentService.getAppointmentbyId(aid);
            if (null == app) {
                return new RespInfo(57, "no records");
            }
            List<Appointment> appointments = new ArrayList<>();
            appointments.add(app);
            return new RespInfo(0, "success", appointments);
        }
    }

    /**
     * 根据邀请随机码获取邀请记录
     *
     * @param at
     * @return
     */
    @Deprecated
    @ApiOperation(value = "/getAppointmentByAcode 通过邀请码获取邀请记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[\n" +
                    "    {\n" +
                    "        \"acode\":\"邀请码\",\n" +
                    "    }\n" +
                    "]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAppointmentByAcode")
    @ResponseBody
    public RespInfo getAppointmentByAcode(@RequestBody Appointment at, HttpServletRequest request, BindingResult result) {

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        String acode = at.getAcode();
        Visitor v = new Visitor();
        if (StringUtils.isNotBlank(acode)) {
            List<Appointment> appointments = appointmentService.getAppointmentbyAcode(Integer.parseInt(acode));
            if (null != appointments && appointments.size() > 0) {
                at = appointments.get(0);
                v = new Visitor();
                v.setVid(at.getId());
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
                v.setAreaid(at.getAreaid());
                v.setArea(at.getArea());
                v.setvType(at.getvType());
                v.setVisitReason(at.getVisitReason());
                v.setAppid(at.getId());
                v.setRemark(at.getRemark());
                v.setPlateNum(at.getPlateNum());
                v.setTid(at.getTid());
                v.setCardId(at.getCardId());
                v.setGid(at.getGid() + "");
                v.setExtendCol(at.getAppExtendCol());
                v.setAcode(at.getAcode());
                v.setStatus(at.getStatus());

                return new RespInfo(0, "success", v);
            }
        }
        return new RespInfo(0, "success");
    }
}
