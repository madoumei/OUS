package com.client.controller;

import com.client.bean.*;
import com.client.dao.EqptRuleDao;
import com.client.service.*;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.utils.*;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/*")
@Api(value = "EquipmentController", tags = "API_门禁设备管理", hidden = true)
public class EquipmentController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private EGRelationService eGRelationService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VehicleRecordService vehicleRecordService;

    @Autowired
    private ManagerService managerService;
    
    @Autowired
    private OpendoorService opendoorService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private EquipmentPersonGroupService equipmentPersonGroupService;
    
    @Autowired
    private VisitorService visitorService;
    
    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private EqptRuleService eqptRuleService;

    private static  EqptMonitorService eqptMonitorService;

	@Autowired(required = true)
	public void setRfidRecordsService(EqptMonitorService eqptMonitorService) {
		EquipmentController.eqptMonitorService = eqptMonitorService;
	}


    public static int updateHeartbeat(String uid) {
        int i = eqptMonitorService.updateHeartbeat(uid);
        return i;
    }

    @ApiOperation(value = "/addEquipment 添加设备", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEquipment")
    @ResponseBody
    public RespInfo addEquipment(@RequestBody Equipment e, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        e.setUserid(authToken.getUserid());
        Equipment eqpt = equipmentService.getEquipmentbyDeviceName(e);
        if (null != eqpt) {
            return new RespInfo(70, "DeviceName already  Exist");
        }

        if (e.getDeviceQrcode() != null && !"".equals(e.getDeviceQrcode())) {
            eqpt = equipmentService.getEquipmentbyDeviceQrcode(e);
            if (null != eqpt) {
                return new RespInfo(71, "DeviceQrcode already  Exist");
            }
        }

        if (!e.geteType().startsWith("CAHK") //海康设备时不检查
                && null != e.getExtendCode() && !"".equals(e.getExtendCode())) {
            eqpt = equipmentService.getEquipmentbyExtendCode(e);
            if (null != eqpt) {
                return new RespInfo(72, "ExtendCode already  Exist");
            }
        }

        equipmentService.addEquipment(e);
        List<String> deviceNameList = new ArrayList<>();
        if (e.getEid() != 0 && e.getEgids().size() > 0) {
            List<EGRelation> rlist = new ArrayList<EGRelation>();
            for (int a = 0; a < e.getEgids().size(); a++) {
                EGRelation egr = new EGRelation();
                egr.setEid(e.getEid());
                egr.setUserid(e.getUserid());
                egr.setEgid(e.getEgids().get(a));
                egr.setStatus(1);
                rlist.add(egr);
                // TODO: 2020/4/3 根据id查询门岗名称
                Equipment equipment = equipmentService.getEquipmentbyEid(e);
                String deviceName = equipment.getDeviceName();
                deviceNameList.add(deviceName);
            }
            eGRelationService.addEGRelation(rlist);
        }
        // TODO: 2020/4/14 门禁设备添加操作记录
        UserInfo userInfo = null;
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(e.getLoginAccount())) {
            Manager manager = managerService.getManagerByAccount(e.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                userInfo = userService.getUserInfo(e.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(e.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setoTime(new Date());
            log.setOptEvent("增加");
            log.setObjName("门禁设备: " + e.getDeviceName());
            log.setOptClient("0");
            log.setOptModule("5");
            log.setOptDesc("成功,添加门禁设备: " + e.getDeviceName());
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateEquipment 更新设备", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateEquipment")
    @ResponseBody
    public RespInfo updateEquipment(@RequestBody Equipment e, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        e.setUserid(authToken.getUserid());
        Equipment eqpt = equipmentService.getEquipmentbyEid(e);

        if (!eqpt.getDeviceName().equals(e.getDeviceName())) {
            Equipment neweqpt = equipmentService.getEquipmentbyDeviceName(e);
            if (null != neweqpt) {
                return new RespInfo(70, "DeviceName already  Exist");
            }
        }

        if(StringUtils.isNotBlank(eqpt.getDeviceQrcode()) && StringUtils.isNotBlank(e.getDeviceQrcode())){
            if (!eqpt.getDeviceQrcode().equals(e.getDeviceQrcode())) {
                Equipment neweqpt = equipmentService.getEquipmentbyDeviceQrcode(e);
                if (null != neweqpt) {
                    return new RespInfo(71, "DeviceQrcode already  Exist");
                }
            }
        }

        if (StringUtils.isNotBlank(eqpt.getExtendCode()) && StringUtils.isNotBlank(e.getExtendCode())){
            if (!eqpt.getExtendCode().equals(e.getExtendCode())) {
                if (null != e.getExtendCode() && !"".equals(e.getExtendCode())) {
                    Equipment neweqpt = equipmentService.getEquipmentbyExtendCode(e);
                    if (null != neweqpt) {
                        return new RespInfo(72, "ExtendCode already  Exist");
                    }
                }
            }
        }

        int s = equipmentService.updateEquipment(e);

        List<EGRelation> rlist = new ArrayList<EGRelation>();
        List<String> deviceNameList = new ArrayList<>();
        eGRelationService.delRelationByEid(e.getEid());
        if (e.getEgids().size() > 0) {
            for (int a = 0; a < e.getEgids().size(); a++) {
                EGRelation egr = new EGRelation();
                egr.setEid(e.getEid());
                egr.setUserid(e.getUserid());
                egr.setEgid(e.getEgids().get(a));
                egr.setStatus(e.getStatus());
                rlist.add(egr);
                // TODO: 2020/4/3 根据id查询门岗名称
                Equipment equipment = equipmentService.getEquipmentbyEid(e);
                String deviceName = equipment.getDeviceName();
                deviceNameList.add(deviceName);
            }
            eGRelationService.addEGRelation(rlist);
        }
        // TODO: 2020/4/14 门禁设备更新操作日志记录
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(e.getLoginAccount())) {
            Manager manager = managerService.getManagerByAccount(e.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(e.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(e.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setoTime(new Date());
            log.setOptEvent("修改");
            log.setObjName("门禁设备: " + e.getDeviceName());
            log.setOptClient("0");
            log.setOptModule("5");
            log.setOptDesc("成功,修改门禁设备:" + e.getDeviceName());
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delEquipment 删除设备", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delEquipment")
    @ResponseBody
    public RespInfo delEquipment(@RequestBody Equipment e, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        e = equipmentService.getEquipmentbyEid(e);
        int i = equipmentService.delEquipment(e.getEid());
        if (i > 0) {
            eGRelationService.delRelationByEid(e.getEid());
        } else {
            return new RespInfo(1, "delete failed");
        }
        // TODO: 2020/4/14 门禁设备删除操作日志
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(e.getLoginAccount())) {
            Manager manager = managerService.getManagerByAccount(e.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(e.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(e.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setoTime(new Date());
            log.setOptEvent("删除");
            log.setObjName("门禁设备:" + e.getDeviceName());
            log.setOptClient("0");
            log.setOptModule("5");
            log.setOptDesc("成功,删除门禁设备: " + e.getDeviceName());
            operateLogService.addLog(log);
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getEquipmentbyUserid 根据Userid获取设备", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEquipmentbyUserid")
    @ResponseBody
    public RespInfo getEquipmentbyUserid(HttpServletRequest request) {
        List<Equipment> elist = new ArrayList<>();
        String ctoken = request.getHeader("X-COOLVISIT-TOKEN");
        String decode = AESUtil.decode(ctoken, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            AuthToken authToken = mapperInstance.readValue(decode, AuthToken.class);
            Equipment equipment = new Equipment();
            equipment.setUserid(authToken.getUserid());
            elist = equipmentService.getEquipmentbyUserid(equipment);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
        return new RespInfo(0, "success", elist);
    }

    @ApiOperation(value = "/getEquipmentGroupByUserid 根据Userid获取设备组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\": 123\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEquipmentGroupByUserid")
    @ResponseBody
    public RespInfo getEquipmentGroupByUserid(
            @ApiParam(value = "EquipmentGroup 门禁组Bean", required = true) @Validated @RequestBody EquipmentGroup eg,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //验证userid
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (eg.getUserid() != authToken.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<EquipmentGroup> eglist = equipmentGroupService.getEquipmentGroupByUserid(eg);
        return new RespInfo(0, "success", eglist);
    }

    @ApiOperation(value = "/addEquipmentGroup 添加设备组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEquipmentGroup")
    @ResponseBody
    public RespInfo addEquipmentGroup(@RequestBody EquipmentGroup eg, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != eg.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<EquipmentGroup> list = equipmentGroupService.getEquipmentGroupByGname(eg);
        if (list.size() > 0) {
            return new RespInfo(73, "GroupName already  Exist");
        }

        equipmentGroupService.addEquipmentGroup(eg);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "group_update");
        map.put("group_id", eg.getEgid());
        //map.put("group_name", eg.getEgname());
        map.put("company_id", eg.getUserid());

        messageSender.updateFaceLib(map);

        //添加日志
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(eg.getLoginAccount())) {
            Manager manager = managerService.getManagerByAccount(eg.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(eg.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(eg.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setoTime(new Date());
            log.setOptEvent("增加");
            log.setObjName("门禁: " + eg.getEgname());
            log.setOptClient("0");
            log.setOptModule("5");
            log.setOptDesc("成功,添加门禁组: " + eg.getEgname());
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success", eg);
    }

    @ApiOperation(value = "/updateEquipmentGroup 更新设备组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateEquipmentGroup")
    @ResponseBody
    public RespInfo updateEquipmentGroup(@RequestBody EquipmentGroup eg, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != eg.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<EquipmentGroup> eglist = equipmentGroupService.getEquipmentGroupByGname(eg);
        if (eglist.size() > 0 && eglist.get(0).getEgid() != eg.getEgid()) {
            return new RespInfo(70, "GroupName already Exist");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "group_update");
        map.put("group_id", eg.getEgid());
        //map.put("group_name", eg.getEgname());
        map.put("company_id", eg.getUserid());

        messageSender.updateFaceLib(map);

        equipmentGroupService.updateEquipmentGroup(eg);

        // TODO: 2020/4/14 添加门禁组更新操作记录
        UserInfo userInfo = null;
        String optId = "";
        String optName = "";
        String optRole = "";
        if (StringUtils.isNotEmpty(eg.getLoginAccount())) {
            Manager manager = managerService.getManagerByAccount(eg.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                userInfo = userService.getUserInfo(eg.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(eg.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setoTime(new Date());
            log.setOptEvent("修改");
            log.setObjName("门禁:" + eg.getEgname());
            log.setOptClient("0");
            log.setOptModule("5");
            log.setOptDesc("成功,更新门禁组: " + eg.getEgname());
            operateLogService.addLog(log);
        }

        return new RespInfo(0, "success");
    }

    /**
     * 批量更新员工的门禁组
     *
     * @param repEmp
     * @return
     */
    @ApiOperation(value = "/batchUpdateEmpEgids 批量更新员工设备组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/batchUpdateEmpEgids")
    @ResponseBody
    public RespInfo batchUpdateEmpEgids(@RequestBody List<Employee> repEmp, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != repEmp.get(0).getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        employeeService.batchUpdateEmpEgids(repEmp);
        int userid = repEmp.get(0).getUserid();
        UserInfo userInfo = userService.getUserInfo(userid);
        OperateLog log = new OperateLog();
        log.setUserid(userid);
        log.setOptName(userInfo.getUsername());
        log.setOptId(userInfo.getEmail());
        log.setObjId("");
        log.setOptRole("0");
        log.setIpAddr(request.getHeader("X-Forwarded-For"));
        log.setOptModule("1");
        log.setOptClient("0");
        log.setOptEvent("修改");
        List<String> collect = repEmp.stream().map(Employee::getEmpName).collect(Collectors.toList());
        log.setObjName(String.join(",", collect));
        log.setOptDesc("成功，批量更新员工：" + String.join(",", collect) + " 门禁");
        operateLogService.addLog(log);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delEquipmentGroup 删除设备组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delEquipmentGroup")
    @ResponseBody
    public RespInfo delEquipmentGroup(@RequestBody EquipmentGroup eg, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != eg.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        // TODO: 2020/4/10 门岗删除日志操作记录
        EquipmentGroup byEgid = equipmentGroupService.getEquipmentGroupByEgid(eg);
        int i = equipmentGroupService.delEquipmentGroup(eg.getEgid());
        if (i > 0) {
            Employee emp = new Employee();
            emp.setEgids("," + eg.getEgid() + ",");
            emp.setUserid(eg.getUserid());
            employeeService.updateEmpEgids(emp);

            //添加日志
            UserInfo userInfo = null;
            String optId = "";
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(eg.getLoginAccount())) {
                Manager manager = managerService.getManagerByAccount(eg.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    userInfo = userService.getUserInfo(eg.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(eg.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setoTime(new Date());
                log.setOptEvent("删除");
                log.setObjName("门禁" + byEgid.getEgname());
                log.setOptClient("0");
                log.setOptModule("5");
                log.setOptDesc("成功,删除门禁组: " + byEgid.getEgname());
                operateLogService.addLog(log);
            }
            eGRelationService.delRelationByEgid(eg.getEgid());
            equipmentPersonGroupService.delEquipmentGroup(eg.getEgid());
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "group_delete");
        map.put("group_id", eg.getEgid());
        map.put("company_id", eg.getUserid());

        messageSender.updateFaceLib(map);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getEGroupByEid 根据eid获取门禁组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEGroupByEid")
    @ResponseBody
    public RespInfo getEGroupByEid(@RequestBody Equipment e) {
        List<EquipmentGroup> eglist = eGRelationService.getEGroupByEid(e);
        return new RespInfo(0, "success", eglist);
    }

    @ApiOperation(value = "添加设备监听 /addEqptMonitor", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":-1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEqptMonitor")
    @ResponseBody
    public RespInfo addEqptMonitor(@RequestBody EqptMonitor em) {
        int i=eqptMonitorService.updateEqptMonitor(em);
        if(i==0){
            eqptMonitorService.addEqptMonitor(em);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "添加设备监听错误日志 /addEqptMonitorErrorLog", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addEqptMonitorErrorLog")
    @ResponseBody
    public RespInfo addEqptMonitorErrorLog(@RequestBody EqptMonitorLog eqptMonitorLog){
        int i = eqptMonitorService.addEqptMonitorErrorLog(eqptMonitorLog);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "获取设备监听 /getEqptMonitor", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":-1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEqptMonitor")
    @ResponseBody
    public RespInfo getEqptMonitor(@RequestBody ReqEM rem,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rem.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        Page<EqptMonitor> rpage = new Page<EqptMonitor>(rem.getStartIndex() / rem.getRequestedCount() + 1, rem.getRequestedCount(), 0);
        rem.setPage(rpage);
        List<EqptMonitor> emList = eqptMonitorService.getEqptMonitor(rem);
        Date date = new Date();
        for (int i = 0; i < emList.size(); i++) {
            if (date.getTime() - emList.get(i).getLastOnline().getTime() < 50000) {
                emList.get(i).seteStatus(1);
            }
        }
        rpage.setList(emList);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "删除设备监听 /delEqptMonitor", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delEqptMonitor")
    @ResponseBody
    public RespInfo delEqptMonitor(@RequestBody EqptMonitor em) {
        eqptMonitorService.delEqptMonitor(em);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "更新设备监听 /updateEqptMonitor", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateEqptMonitor")
    @ResponseBody
    public RespInfo updateEqptMonitor(@RequestBody EqptMonitor em) {
        eqptMonitorService.updateEqptMonitor(em);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "客户端上传日志 /clientUploadLog", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/clientUploadLog")
    @ResponseBody
    public RespInfo clientUploadLog(@RequestBody EqptMonitor em) {
        ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
        valueOperations.set("uuid_"+em.getUid(), em.getUid(), 5, TimeUnit.MINUTES);
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "门禁管理_分页获取门禁组列表 /getEquipmentGroupByPage", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEquipmentGroupByPage")
    @ResponseBody
    public RespInfo getEquipmentGroupByPage(
            @ApiParam(value = "EquipmentGroup 门禁组Bean", required = true) @Validated @RequestBody EquipmentGroup eg,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != eg.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        Page<EquipmentGroup> rpage = new Page<EquipmentGroup>(eg.getStartIndex() / eg.getRequestedCount() + 1, eg.getRequestedCount(), 0);
        eg.setPage(rpage);
        List<EquipmentGroup> eglist = equipmentGroupService.getEquipmentGroupByUserid(eg);
        rpage.setList(eglist);
        return new RespInfo(0, "success", rpage);
    }

    /**
     * 门禁搜索接口
     *
     * @param repEmp
     * @return
     */
    @ApiOperation(value = "门禁管理_条件查询员工门禁组 /searchEmpEgroupByCondition", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"empName\":\"\",\n" +
                    "    \"egids\":\"\",\n" +
                    "    \"gid\":\"\",\n" +
                    "    \"deptid\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/searchEmpEgroupByCondition")
    @ResponseBody
    public RespInfo searchEgroupByCondition(@ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp repEmp,
                                            HttpServletRequest request, BindingResult result) {

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != repEmp.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = repEmp.getUserid();
        String empName = repEmp.getEmpName();
        int gid = repEmp.getGid();
        String egids = repEmp.getEgids();
        int deptid = repEmp.getDeptid();

        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("userid", String.valueOf(userid));
        searchMap.put("empName", empName);
        searchMap.put("egids", egids);
        if (deptid > 0) {
            searchMap.put("deptIdStr", String.valueOf(deptid));
        }
        //根据条件查询出所有符合要求的员工信息
        List<Employee> employees = employeeService.searchEmpByCondition(searchMap);
        //根据门岗条件过滤出符合要求的员工
        List<Employee> respEmp = new ArrayList<>();

        //过滤出符合门岗条件的员工
        if (gid > 0) {
            List<EquipmentGroup> groups = equipmentGroupService.getEquipmentGroupListByGid(userid, gid);
            List<Integer> egidList = groups.stream().map(EquipmentGroup::getEgid).collect(Collectors.toList());
            if (null != groups && groups.size() > 0) {

                for (Employee employee : employees) {
                    if (StringUtils.isNotBlank(employee.getEgids())) {
                        ArrayList<String> empEgidList = Lists.newArrayList(employee.getEgids().split(","));
                        for (String s : empEgidList) {
                            if (egidList.contains(Integer.parseInt(s))) {
                                if (respEmp.contains(employee)) {
                                    break;
                                }
                                respEmp.add(employee);
                            }
                        }
                    }
                }
            }
            //门禁组条件
        } else {
            respEmp.addAll(employees);
        }
        return new RespInfo(0, "success", respEmp);
    }

    @ApiOperation(value = "门禁管理_获取车辆通行记录 /getVehicleRecord", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startDate\":\"2021-03-19\",\n" +
                    "    \"endDate\":\"2021-03-19\",\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"deviceCode\":\"\",\n" +
                    "    \"deviceName\":\"\",\n" +
                    "    \"name\":\"\",\n" +
                    "    \"plateNum\":\"\",\n" +
                    "    \"vehList\":[\n" +
                    "        1,\n" +
                    "        2,\n" +
                    "        3,\n" +
                    "        -1\n" +
                    "    ]\n" +
                    "    \"gid\":\"13\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVehicleRecord")
    @ResponseBody
    public RespInfo getVehicleRecord(@RequestBody ReqVR rvr,HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != rvr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){
            UserInfo userInfo = new UserInfo();
            userInfo.setUserid(rvr.getUserid());
            userInfo.setEmail(authToken.getLoginAccountId());
            userInfo= userService.getUserByAccount(userInfo);
            if (null == userInfo){
                return new RespInfo(1, "invalid user");
            }
        }
        else if (AuthToken.ROLE_GATE.equals(authToken.getAccountRole())||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager mgr = managerService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[]=rvr.getGid().split(",");
            String mgids[]=mgr.getGid().split(",");
            boolean auth=UtilTools.arrayContain(gids, mgids);
            if(auth) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
    	
        Page<VehicleRecord> rpage = new Page<VehicleRecord>(rvr.getStartIndex() / rvr.getRequestedCount() + 1, rvr.getRequestedCount(), 0);
        rvr.setPage(rpage);
        List<VehicleRecord> vrlist = vehicleRecordService.getVehicleRecord(rvr);
        rpage.setList(vrlist);

        return new RespInfo(0, "success", rpage);
    }
    @ApiOperation(value = "门禁管理_根据访客id获取车辆通行记录 /getVehicleRecordByVid", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"vehType\":1,\n" +
                    "    \"sType\":1,\n" +
                    "    \"vsid\":343,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVehicleRecordByVid")
    @ResponseBody
    public RespInfo getVehicleRecordByVid(@RequestBody VehicleRecord vr,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != vr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){
            UserInfo userInfo = new UserInfo();
            userInfo.setUserid(vr.getUserid());
            userInfo.setEmail(authToken.getLoginAccountId());
            userInfo= userService.getUserByAccount(userInfo);
            if (null == userInfo){
                return new RespInfo(1, "invalid user");
            }
        }
        else if (AuthToken.ROLE_GATE.equals(authToken.getAccountRole())||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager mgr = managerService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[]=vr.getGid().split(",");
            String mgids[]=mgr.getGid().split(",");
            boolean auth=UtilTools.arrayContain(gids, mgids);
            if(auth) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        vr = vehicleRecordService.getVehicleRecordByVsid(vr);
        return new RespInfo(0, "success", vr);
    }

    @ApiOperation(value = "门禁管理_获取开门记录 /getOpendoorInfo", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startDate\":\"2021-03-31\",\n" +
                    "    \"endDate\":\"2021-03-31\",\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"mobile\":\"\",\n" +
                    "    \"deviceCode\":\"\",\n" +
                    "    \"vtype\":\"\",\n" +
                    "    \"vname\":\"\",\n" +
                    "    \"company\":\"\"\n" +
                    "    \"gids\":\"12\"\n" +
                    "    \"direction\":\"出门/进门\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getOpendoorInfo")
    @ResponseBody
    public RespInfo getOpendoorInfo(@RequestBody ReqODI reqodi,
                                    HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != reqodi.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){
            UserInfo userInfo = new UserInfo();
            userInfo.setUserid(reqodi.getUserid());
            userInfo.setEmail(authToken.getLoginAccountId());
            userInfo= userService.getUserByAccount(userInfo);
            if (null == userInfo){
                return new RespInfo(1, "invalid user");
            }
        }
        else if (AuthToken.ROLE_GATE.equals(authToken.getAccountRole())||AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager mgr = managerService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[]=reqodi.getGids().split(",");
    		String mgids[]=mgr.getGid().split(",");
    	    boolean auth=UtilTools.arrayContain(gids, mgids);
    		if(auth) {
    			return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
    		}
        }else{
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }


        Page<OpendoorInfo> rpage = new Page<OpendoorInfo>(reqodi.getStartIndex() / reqodi.getRequestedCount() + 1, reqodi.getRequestedCount(), 0);
        reqodi.setPage(rpage);
        List<OpendoorInfo> odilist = opendoorService.getOpendoorInfo(reqodi);
        rpage.setList(odilist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "门禁管理_条件搜索错误日志 /searchEqptMonitorErrorLogByCondition", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @PostMapping("/searchEqptMonitorErrorLogByCondition")
    @ResponseBody
    public RespInfo getEqptMonitorErrorLog(@RequestBody ReqEqptMonitorLog eqptMonitorLog,HttpServletRequest request){

        Page<EqptMonitorLog> eqptMonitorLogPage = new Page<EqptMonitorLog>(eqptMonitorLog.getStartIndex() / eqptMonitorLog.getRequestedCount() + 1, eqptMonitorLog.getRequestedCount(), 0);
        eqptMonitorLog.setPage(eqptMonitorLogPage);
        List<EqptMonitorLog> list = this.eqptMonitorService.searchEqptMonitorErrorLogByCondition(eqptMonitorLog);
        eqptMonitorLogPage.setList(list);
        return new RespInfo(0,"success",eqptMonitorLogPage);
    }

    @ApiOperation(value = "门禁管理_批量添加开门日志 /batchAddOpendoorInfo", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/batchAddOpendoorInfo")
    @ResponseBody
    public RespInfo batchAddOpendoorInfo(@RequestBody List<ReqODI> odlist) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> list=new ArrayList<String>();
        for(int i=0;i<odlist.size();i++){
            if(StringUtils.isNotBlank(odlist.get(i).getVid())){
                if("a".equals(odlist.get(i).getVid().substring(0, 1))){
                    Appointment ap=appointmentService.getAppointmentbyId(Integer.parseInt(odlist.get(i).getVid().substring(1)));

                    Date visitorDate=null;
                    try {
                        visitorDate = timeFormat.parse(odlist.get(i).getOpenDate());
                    } catch (ParseException e) {
                        visitorDate = new Date();
                    }

                    int d2=1;//0 当天，1 第二天
                    if(null!=ap&&null!=ap.getVisitDate()){
                        d2=DateUtils.truncatedCompareTo(visitorDate, ap.getVisitDate(), Calendar.DATE);
                    }

                    //自动签到
                    if(d2!=0 && Constant.AutoSignin != null && Constant.AutoSignin.equals("1")
                            && odlist.get(i).getDirection() != null
                            && odlist.get(i).getDirection().equals("进门")){
                        Visitor vt=new Visitor();
                        vt.setEmpid(ap.getEmpid());
                        vt.setEmpName(ap.getEmpName());
                        vt.setUserid(ap.getUserid());
                        vt.setVphoto(ap.getPhotoUrl());
                        vt.setVname(ap.getName());
                        try {
                            vt.setVisitdate(timeFormat.parse(odlist.get(i).getOpenDate()));
                        } catch (ParseException e) {
                            vt.setVisitdate(new Date());
                        }
                        vt.setVphone(ap.getPhone());
                        vt.setVisitType(ap.getVisitType());
                        vt.setEmpPhone(ap.getEmpPhone());
                        vt.setAppointmentDate(ap.getAppointmentDate());
                        vt.setPermission(1);
                        vt.setSigninType(1);
                        vt.setCardId(ap.getCardId());
                        vt.setPeopleCount(0);
                        vt.setRemark(ap.getRemark());
                        vt.setVcompany(ap.getVcompany());
                        vt.setSubaccountId(ap.getSubaccountId());
                        vt.setCompany(ap.getCompany());
                        vt.setSignInGate(odlist.get(i).getGname());
                        vt.setSignInOpName(odlist.get(i).getDeviceName());
                        vt.setvType(ap.getvType());
                        vt.setAppid(ap.getId());
                        vt.setTid(ap.getTid());
                        vt.setGid(ap.getGid()+"");
                        vt.setPlateNum(ap.getPlateNum());
                        vt.setSex(ap.getSex());
                        vt.setVemail(ap.getVemail());
                        vt.setVisitorCount(ap.getVisitorCount()+1);
                        vt.setPermissionName(ap.getEmpName());
                        vt.setCardNo(ap.getCardNo());
                        vt.setExtendCol(ap.getAppExtendCol());
                        vt.setClientNo(ap.getClientNo());
                        vt.setQrcodeConf(ap.getQrcodeConf());
                        vt.setFloors(ap.getFloors());
                        vt.setIsDaysOffVisitor(ap.getIsDaysOffVisitor());
                        vt.setIsHolidayVisitor(ap.getIsHolidayVisitor());
                        vt.setIsSCTimeVisitor(ap.getIsSCTimeVisitor());
                        vt.setIsWeekendVisitor(ap.getIsWeekendVisitor());

                        ap.setVisitDate(vt.getVisitdate());
                        ap.setVisitorCount(ap.getVisitorCount()+1);
                        int updateRest = appointmentService.updateSaAppointmentStatus(ap);
                        if(updateRest == 0){
                            //更新失败
                            SysLog.warn("进门自动签到失败，"+odlist.get(i).getVname()+" "+odlist.get(i).getOpenDate()+" aid="+ap.getId());
                            continue;
                        }

                        visitorService.addApponintmnetVisitor(vt);
                        // TODO: 2020/7/9 发送邀请用户到访微信通知
                        UserInfo userInfo = userService.getUserInfo(ap.getUserid());
                        if(userInfo.getMsgNotify()==1){
                            List<Employee> empInfo = employeeService.getEmpInfo(vt.getUserid(), ap.getEmpPhone());
                            if (empInfo.size()>0){
                                for (Employee employee : empInfo) {
                                    if (StringUtils.isNotEmpty(employee.getOpenid())){
                                        visitorService.sendArrivedWXNotify(employee,vt);
                                    }
                                }
                            }
                        }
                    }

                    //自动签出
                    Visitor v=new Visitor();
                    v.setAppid(ap.getId());
                    v = visitorService.getVisitorByAppId(v);
                    autoSignout(odlist.get(i),v);

                }else if("v".equals(odlist.get(i).getVid().substring(0, 1))){
                    Visitor v =visitorService.getVisitorById(Integer.parseInt(odlist.get(i).getVid().substring(1)));
                    //自动签到
                    ReqODI odi = odlist.get(i);
                    if(null!=v&&null==v.getVisitdate()
                            && Constant.AutoSignin != null && Constant.AutoSignin.equals("1")
                            && odi.getDirection() != null
                            && odi.getDirection().equals("进门")){
                        v.setVisitdate(new Date());
                        v.setSignInOpName(odi.getDeviceName());
                        v.setSignInGate(odi.getGname());
                        visitorService.updateSigninByAppClient(v);
                    }

                    //自动签出
                    autoSignout(odlist.get(i),v);
                }
            }
        }
        opendoorService.BatchAddOpendoorInfo(odlist);

        return new RespInfo(0, "success");
    }


    /**
     * 自动签离
     * @param odi
     * @param v
     */
    protected void autoSignout(ReqODI odi, Visitor v) {
        if(null!=v&& odi.getDirection() != null
                && odi.getDirection().equals("出门")){
            v.setSignOutDate(new Date());
            v.setSignOutOpName(odi.getDeviceName());
            v.setSignOutGate(odi.getGname());
            if(Constant.AutoSignoutAfterLeave != null && Constant.AutoSignoutAfterLeave.equals("1")
                    //如果已经结束拜访，自动签出
                    &&null!=v.getLeaveTime()){
                visitorService.updateSignOutByVid(v);
            }
            else if(Constant.AutoSignout != null && Constant.AutoSignout.equals("1")){
                //离开自动签出
                visitorService.updateSignOutByVid(v);
            }
        }
    }

    @ApiOperation(value = "门禁管理_导出开门记录 /ExportOpendoorInfoList", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportOpendoorInfoList")
    public void ExportOpendoorInfoList(HttpServletRequest req, HttpServletResponse response) {
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

            String startDate = req.getParameter("startDate");
            String endDate = req.getParameter("endDate");
            String mobile = req.getParameter("mobile");
            String deviceCode = req.getParameter("deviceCode");
            String vtype = req.getParameter("vtype");
            String vname = req.getParameter("vname");
            String company = req.getParameter("company");
            String exportCols = req.getParameter("exportCols");
            String gids = req.getParameter("gids");

            //检查权限
            if(tokenServer.checkUserAuthorityForExport(managerService,redisTemplate,response, ctoken, userid, gids)) return;

            ReqODI ro = new ReqODI();
            ro.setUserid(userid);
            ro.setStartDate(startDate);
            ro.setEndDate(endDate);
            ro.setMobile(mobile);
            ro.setDeviceCode(deviceCode);
            ro.setVname(vname);
            ro.setVtype(vtype);
            ro.setCompany(company);
            ro.setGids(gids);

            List<OpendoorInfo> odilist = opendoorService.getOpendoorInfo(ro);

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "人员进出记录_" + startDate + "-" + endDate;
            HSSFWorkbook wb = new HSSFWorkbook();
            ExcelDownLoad download = new AgentInfoExcelDownLoad();

            wb = download.createDownLoadExcel(odilist, wb,exportCols);

            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
            wb.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_ACCESS, odilist.size(), "导出"+filename+":"+ro.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "门禁管理_导出车辆通行记录 /ExportVehicleInfoList", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportVehicleInfoList")
    public void ExportVehicleInfoList(HttpServletRequest req, HttpServletResponse response) {
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
            String startDate = req.getParameter("startDate");
            String endDate = req.getParameter("endDate");
            String plateNum = req.getParameter("plateNum");
            String deviceCode = req.getParameter("deviceCode");
            String deviceName = req.getParameter("deviceName");
            String vehType = req.getParameter("vehType");
            String name = req.getParameter("name");
            String gid=req.getParameter("gid");

            //检查权限
            if(tokenServer.checkUserAuthorityForExport(managerService,redisTemplate,response, ctoken, userid, gid)) return;

            ReqVR rl = new ReqVR();
            rl.setUserid(userid);
            rl.setStartDate(startDate);
            rl.setEndDate(endDate);
            rl.setPlateNum(plateNum);
            rl.setDeviceCode(deviceCode);
            rl.setDeviceName(deviceName);
            rl.setName(name);
            rl.setGid(gid);
            List<Integer> vehList = new ArrayList<Integer>();
            for (int i = 0; i < vehType.length(); i++) {
                vehList.add(Integer.parseInt(vehType.substring(i, i + 1)));
            }
            rl.setVehList(vehList);

            List<VehicleRecord> vrlist = vehicleRecordService.getVehicleRecord(rl);
            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "车辆进出记录_" + startDate + "-" + endDate;
            HSSFWorkbook wb = new HSSFWorkbook();
            ExcelDownLoad download = new AgentInfoExcelDownLoad();

            wb = download.createDownLoadVRExcel(vrlist, wb);

            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
            wb.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_ACCESS, vrlist.size(), "导出"+filename+":"+rl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value = "/uploadVehicleInfo")
    @ResponseBody
    public Map<String, Object> uploadVehicleInfo(HttpServletRequest request) throws Exception {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(body);
        ALarmPlateNum ar = (ALarmPlateNum) JacksonJsonUtil.jsonToBean(body, ALarmPlateNum.class);
        Date date = new Date();
        String strDateFormat = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        String plateNum = ar.getAlarmInfoPlate().getResult().getPlateResult().getLicense();
        //第一行字
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> line1 = new HashMap<String, Object>();
        line1.put("data", lx.getData(1, "测试公司"));
        line1.put("delay_time", 0);
        list.add(line1);

        //第二行字
        Map<String, Object> line2 = new HashMap<String, Object>();
        line2.put("data", lx.getData(2, plateNum));
        line2.put("delay_time", 200);
        list.add(line2);


        Equipment e = new Equipment();
        e.setDeviceCode(ar.getAlarmInfoPlate().getSn());
        Equipment eqpt = equipmentService.getEquipmentbyDeviceCode(e);
        List<EquipmentGroup> eglist = eGRelationService.getEGroupByEid(e);
        String gid="";
        if(eglist.size()>0) {
        	gid=eglist.get(0).getGids();
        }
        int userid = 0;
        Employee emp = new Employee();
        if (null != eqpt) {
            userid = eqpt.getUserid();
        }

        emp.setUserid(userid);
        emp.setPlateNum(plateNum);
        emp = employeeService.getEmployeebyPlateNum(emp);

        VehicleRecord vr = new VehicleRecord();
        vr.setDeviceCode(ar.getAlarmInfoPlate().getSn());
        vr.setDeviceName(ar.getAlarmInfoPlate().getDeviceName());
        if (ar.getAlarmInfoPlate().getChannel() == 0) {
            vr.setsType(1);
        } else {
            vr.setsType(2);
        }
        vr.setPlateNum(plateNum);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vr.setsTime(time.parse(ar.getAlarmInfoPlate().getResult().getPlateResult().getRecotime()));
        //fastdfs
        // vr.setPhotoUrl(UtilTools.Base64ToImgage(ar.getAlarmInfoPlate().getResult().getPlateResult().getImageFile(),storageClient));
        //fastdfs
        
        //minio
        vr.setPhotoUrl(UtilTools.Base64ToImgage(ar.getAlarmInfoPlate().getResult().getPlateResult().getImageFile()));
        //minio
        if (null != emp) {
            vr.setEmpNo(emp.getEmpNo());
            vr.setName(emp.getEmpName());
            vr.setMoblie(emp.getEmpPhone());
            vr.setVehType(1);
            vr.setUserid(userid);
            vr.setGid(gid);
            vehicleRecordService.addVehicleRecord(vr);

            //第三行字
            Map<String, Object> line3 = new HashMap<String, Object>();
            line3.put("data", lx.getData(3, "员工车辆"));
            line3.put("delay_time", 400);
            list.add(line3);
        } else {
            ReqLogistics rl = new ReqLogistics();
            rl.setUserid(userid);
            rl.setPlateNum(plateNum);
            Logistics log = logisticsService.getTodayLogisticsInfo(rl);
            if (null != log) {
                vr.setName(log.getDname());
                vr.setMoblie(log.getDmobile());
                vr.setVehType(3);
                vr.setUserid(userid);
                vr.setVsid(log.getLogNum());
                vr.setGid(gid);
                vehicleRecordService.addVehicleRecord(vr);
                //第三行字
                Map<String, Object> line3 = new HashMap<String, Object>();
                line3.put("data", lx.getData(3, "访客车辆"));
                line3.put("delay_time", 400);
                list.add(line3);
            } else {
                Visitor v = new Visitor();
                v.setUserid(userid);
                v.setPlateNum(plateNum);
                v = visitorService.getVisitorByPlateNum(v);
                if (null != v) {
                    vr.setName(v.getVname());
                    vr.setMoblie(v.getVphone());
                    vr.setVehType(2);
                    vr.setUserid(userid);
                    vr.setVsid(String.valueOf(v.getVid()));
                    vr.setGid(gid);
                    vehicleRecordService.addVehicleRecord(vr);
                    //第三行字
                    Map<String, Object> line3 = new HashMap<String, Object>();
                    line3.put("data", lx.getData(3, "访客车辆"));
                    line3.put("delay_time", 400);
                    list.add(line3);
                } else {
                    vr.setVehType(-1);
                    vr.setUserid(userid);
                    vr.setGid(gid);
                    vehicleRecordService.addVehicleRecord(vr);
                    //第三行字
                    Map<String, Object> line3 = new HashMap<String, Object>();
                    line3.put("data", lx.getData(3, "未知车辆"));
                    line3.put("delay_time", 400);
                    list.add(line3);
                }
            }
        }


        //第四行字
        Map<String, Object> line4 = new HashMap<String, Object>();
        line4.put("data", lx.getData(4, sdf.format(date)));
        line4.put("delay_time", 600);
        list.add(line4);

        //
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map21 = new HashMap<String, Object>();
        map21.put("action", "open");
        map2.put("barrier_control", map21);


        map2.put("rs485_data", list);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Response", map2);


        return map;
    }
    
    @ApiOperation(value = "获取设备组和设备绑定关系 /getEGroupList", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEGroupList")
    @ResponseBody
    public RespInfo getEGroupList(@RequestBody Equipment e) {
        List<EquipmentGroupResp> eglist = eGRelationService.getEGroupList(e);
        return new RespInfo(0, "success", eglist);
    }


    @ApiOperation(value = "/getEquipmentByEgid 通过egid获取门禁组包含的门禁设备", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEquipmentByEgid")
    @ResponseBody
    public RespInfo getEquipmentByEgid(@RequestBody EquipmentGroup group) {
        List<Equipment> list = eGRelationService.getEquipmentByEgid(group);
        return new RespInfo(0, "success", list);
    }

    @ApiOperation(value = "更新设备在线状态 /updateOnlineStatus", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8"
    )
    @PostMapping(value = "/updateOnlineStatus")
    @ResponseBody
    public RespInfo updateOnlineStatus(@RequestBody List<Equipment> equipment){
        int i = 0;
        if (!equipment.isEmpty()){
            for (Equipment equipment1 : equipment) {
                if(StringUtils.isEmpty(equipment1.getDeviceName())){
                    equipment1.setDeviceName(equipment1.getDeviceCode());
                }
                i += this.equipmentService.updateOnlineStatus(equipment1);
            }
        }

        return new RespInfo(0,"success",i);
    }



    @ApiOperation(value = "/addEqptRule 添加/修改设备潮汐策略",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/addEqptRule")
    @ResponseBody
    public RespInfo addEqptRule(@ApiParam(value = "EqptRule 设备策略Bean", required = true)  @RequestBody EqptRule rlt,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        rlt.setUserid(authToken.getUserid());
        boolean ret = eqptRuleService.saveOrUpdate(rlt);
        if(ret) {
            //通知潮汐变更
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "tide_batch");
            map.put("company_id", authToken.getUserid());
            messageSender.updateFaceLib(map);

            return new RespInfo(0, "success");
        }

        return new RespInfo(ErrorEnum.E_2000.getCode(), ErrorEnum.E_2000.getMsg());
    }


    @ApiOperation(value = "/getEqptRule 获取设备潮汐策略",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/getEqptRule")
    @ResponseBody
    public RespInfo getEqptRule(@ApiParam(value = "EqptRule 设备策略Bean", required = true)  @RequestBody EqptRule rlt,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<EqptRule> list = eqptRuleService.getList(authToken.getUserid());
        return new RespInfo(0, "success",list);
    }

    @ApiOperation(value = "/delEqptRule 删除设备潮汐策略",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/delEqptRule")
    @ResponseBody
    public RespInfo delEqptRule(@ApiParam(value = "EqptRule 设备策略Bean", required = true)  @RequestBody EqptRule rlt,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        rlt.setUserid(authToken.getUserid());
        boolean ret = eqptRuleService.remove(rlt);
        if(ret) {
            //通知潮汐变更
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "tide_batch");
            map.put("company_id", authToken.getUserid());
            messageSender.updateFaceLib(map);
            return new RespInfo(0, "success");
        }

        return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
    }

    @ApiOperation(value = "/getEGroupByEgid 根据egid获取门禁组", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getEGroupByEgid")
    @ResponseBody
    public RespInfo getEGroupByEgid(@RequestBody EquipmentGroup e) {
        EquipmentGroup eglist = eGRelationService.getEGroupByEgid(e);
        return new RespInfo(0, "success", eglist);
    }
}
