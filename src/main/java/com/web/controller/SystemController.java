package com.web.controller;


import com.client.bean.EquipmentGroup;
import com.client.bean.ExtendVisitor;
import com.client.bean.Gate;
import com.client.service.EquipmentGroupService;
import com.client.service.ExtendVisitorService;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.intercept.IpUrlLimitInterceptor;
import com.config.qicool.common.persistence.Page;
import com.config.qicool.common.utils.SpringContextHolder;
import com.event.listener.DingtalkService;
import com.event.listener.FeishuService;
import com.event.listener.WechatBusService;
import com.event.listener.WechatService;
import com.utils.*;
import com.utils.FileUtils.MinioTools;
import com.web.bean.*;
import com.web.dao.ConfigureDao;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.YearMonth;
import java.util.*;


/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "SystemController", tags = "API_系统设置", hidden = true)
public class SystemController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ManagerService mgrService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private MsgTemplateService msgTemplateService;

    @Autowired
    private ConfigureDao configureDao;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private ExtendVisitorService extendVisitorService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WechatBusService wechatBusService;
    @Autowired
    private FeishuService feishuService;
    @Autowired
    private DingtalkService dingtalkService;

    @ApiOperation(value = "基本设置_预约授权开关 /updatePermissionSwitch", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"permissionSwitch\":0" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updatePermissionSwitch")
    @ResponseBody
    public RespInfo updatePermissionSwitch(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo requser,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(requser.getUserid());
        userinfo.setPermissionSwitch(requser.getPermissionSwitch());

        userService.updatePermissionSwitch(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_获取所有模板类型 /GetAllTemplateType ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"gid\":\"1\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetAllTemplateType")
    @ResponseBody
    public RespInfo GetVisitType(
            @ApiParam(value = "Usertemplate 用户模板Bean", required = true) @Validated @RequestBody Usertemplate ut,
            HttpServletRequest request, BindingResult result) {
        List<Usertemplate> utlist = appointmentService.getUserTemplateType(ut);

        return new RespInfo(0, "success", utlist);
    }

    @ApiOperation(value = "基本设置_模板限制员工修改开关 /updateTempEditSwitch", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"tempEditSwitch\":1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateTempEditSwitch")
    @ResponseBody
    public RespInfo updateTempEditSwitch(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        int tempEditSwitch = reqinfo.getTempEditSwitch();
        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setTempEditSwitch(tempEditSwitch);

        userService.updateTempEditSwitch(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_前台验证有效期配置 /updateExtendTime ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"preExtendTime\":0,\n" +
                    "    \"latExtendTime\":0,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateExtendTime")
    @ResponseBody
    public RespInfo updateExtendTime(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        int preExtendTime = reqinfo.getPreExtendTime();
        int latExtendTime = reqinfo.getLatExtendTime();
        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setPreExtendTime(preExtendTime);
        userinfo.setLatExtendTime(latExtendTime);
        userService.updateExtendTime(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_更新前台迁离时间 /updateLeaveExpiryTime ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"leaveExpiryTime\":0,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateLeaveExpiryTime")
    @ResponseBody
    public RespInfo updateLeaveExpiryTime(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateLeaveExpiryTime(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_更新员工工作时间设置 /updateOffDutyTime ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"upDuty\":\"09:00\",\n" +
                    "    \"offDuty\":\"19:30\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateOffDutyTime")
    @ResponseBody
    public RespInfo updateOffDutyTime(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateOffDutyTime(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_更新员工邀请函二维码有效期 /updateQrcodeConf", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"qrMaxCount\":2,\n" +
                    "    \"qrMaxDuration\":10,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateQrcodeConf")
    @ResponseBody
    public RespInfo updateQrcodeConf(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateQrcodeConf(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_更新访客数据保留时长 /updateDataKeepTime", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"dataKeepTime\":0~99,0 永久保留\n" +
                    "    \"userid\":2147483641\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateDataKeepTime")
    @ResponseBody
    public RespInfo updateDataKeepTime(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(userinfo.getDataKeepTime()<0 || userinfo.getDataKeepTime()>99){
            return new RespInfo(ErrorEnum.E_604.getCode(), "dataKeepTime:0~99,0 永久保留");
        }

        userService.updateDataKeepTime(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_更新二维码签到开关 /updateScanerSwitch", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"scaner\":0\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateScanerSwitch")
    @ResponseBody
    public RespInfo updateScanerSwitch(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo requser,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = requser.getUserid();
        int scaner = requser.getScaner();

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setScaner(scaner);

        userService.updateScanerSwitch(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "通知设置_微信通知开关配置 /UpdateWxConf", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"msgNotify\":0\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateWxConf")
    @ResponseBody
    public RespInfo UpdateWxConf(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = reqinfo.getUserid();
        int msgn = reqinfo.getMsgNotify();
        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setMsgNotify(msgn);
        userService.updateWxConf(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "通知设置_邮件通知配置 /ConfigureEmail", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"type\":3,\n" +
                    "    \"account\":null,\n" +
                    "    \"pwd\":null,\n" +
                    "    \"smtp\":null,\n" +
                    "    \"smtp_port\":0,\n" +
                    "    \"exchange\":null,\n" +
                    "    \"domain\":null,\n" +
                    "    \"ssl\":0\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/ConfigureEmail")
    @ResponseBody
    public RespInfo ConfigureEmail(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userinfo = userService.getUserInfo(reqinfo.getUserid());

        String emailAccount = reqinfo.getAccount();
        String emailPwd = reqinfo.getPwd();
        int type = reqinfo.getType();
        String smtp = reqinfo.getSmtp();
        int smtpPort = reqinfo.getSmtp_port();
        String exchange = reqinfo.getExchange();
        String domain = reqinfo.getDomain();

        userinfo.setEmailType(type);
        userinfo.setEmailAccount(emailAccount);
        userinfo.setEmailPwd(emailPwd);
        userinfo.setSmtp(smtp);
        userinfo.setSmtpPort(smtpPort);
        userinfo.setExchange(exchange);
        userinfo.setDomain(domain);


        userService.upEmailConf(userinfo);

        return new RespInfo(0, "success", userinfo);
    }

    @ApiOperation(value = "通知设置_短信通知开关配置 /updateSMSConf", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"smsNotify\":1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSMSConf")
    @ResponseBody
    public RespInfo updateSMSConf(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        int smsNotify = reqinfo.getSmsNotify();

        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setSmsNotify(smsNotify);
        userService.updateSMSConf(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "通知设置_叮叮通知配置 /updateDDNotify", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"smsNotify\":1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateDDNotify")
    @ResponseBody
    public RespInfo UpdateDDNotify(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo req,
            HttpServletRequest request, BindingResult bindingResult) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = req.getUserid();
        int ddnotify = req.getDdnotify();
        int ddautosync = req.getDdautosync();

        String ddagentid = req.getDdagentid();
        String ddcorpid = req.getDdcorpid();
        String ddcorpsecret = req.getDdcorpsecret();


        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setDdnotify(ddnotify);
        userinfo.setDdautosync(ddautosync);

        userinfo.setDdagentid(ddagentid);
        userinfo.setDdAppid(ddcorpid);
        userinfo.setDdAppSccessSecret(ddcorpsecret);

        if (ddnotify != 0) {
            String result = UtilTools.checkDDAccToken(userid, ddcorpid, ddcorpsecret);
            if (StringUtils.isNotEmpty(result)) {
                return new RespInfo(51, "DD configure failed");
            }
        }

        userService.updateDDNotify(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "通知设置_腾讯通通知配置 /updateRtxConf ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rtxIp\":\"\",\n" +
                    "    \"rtxPort\":\"\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateRtxConf")
    @ResponseBody
    public RespInfo updateRtxConf(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo req,
            HttpServletRequest request, BindingResult bindingResult) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = req.getUserid();
        String rtxip = req.getRtxIp();
        int rtxport = req.getRtxPort();

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setRtxip(rtxip);
        userinfo.setRtxport(rtxport);

        userService.updateRtxConf(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "账号设置_获取管理员 /GetManagerByUser ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"stList\":[\n" +
                    "        1\n" +
                    "    ]\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetManagerByUser")
    @ResponseBody
    public RespInfo getManagerByUser(
            @ApiParam(value = "Manager 管理员Bean", required = true) @Validated @RequestBody Manager manager,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != manager.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (manager.getStList().size() > 0) {
            List<Manager> mgrList = mgrService.getManagerList(manager);

            for (Manager m : mgrList) {
                List<Permission> allPermission = permissionService.getPermissionByaccount(m.getAccount());
                if (!allPermission.isEmpty() && allPermission.size() > 0) {
                    List<Permission> list = permissionService.getPermissionTree(allPermission);
                    m.setModule(list);
                }
            }

            //劳务公司数据
            if (AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())) {
                List<Manager> resultList = new ArrayList<>();
                for (Manager m : mgrList) {
                    if(m.getAccount().equals(authToken.getLoginAccountId())) {
                        resultList.add(m);
                    }
                }
                return new RespInfo(0, "success", resultList);
            }

            return new RespInfo(0, "success", mgrList);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "多企业服务_根据UserId分页获取入驻企业 /getSubAccountPageByUserid ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"gids\":\"\",\n" +
                    "    \"companyName\":\"\",\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"isUse\":-1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getSubAccountPageByUserid")
    @ResponseBody
    public RespInfo getSubAccountPageByUserid(
            @ApiParam(value = "eqSubAccount 请求入驻企业Bean", required = true) @Validated @RequestBody ReqSubAccount reqSubAccount,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqSubAccount.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
            Manager manager = managerService.getManagerByAccount(authToken.getLoginAccountId());
            String AuthGids = manager.getGid();
            if(StringUtils.isBlank(reqSubAccount.getGids())){
                reqSubAccount.setGids(AuthGids);
            }else {
                String reqGids = reqSubAccount.getGids();
                String[] gidList = reqGids.split(",");
                for(String gid:gidList){
                    if(!(","+AuthGids+",").contains(","+gid+",")){
                        return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                    }
                }
            }

        }

        Page rpage = new Page(reqSubAccount.getStartIndex() / reqSubAccount.getRequestedCount() + 1, reqSubAccount.getRequestedCount(), 0);
        reqSubAccount.setPage(rpage);
        List<SubAccount> listsa = subAccountService.getSubAccountByUseridPage(reqSubAccount);

        if (listsa.size() > 0) {
            for (SubAccount subAccount : listsa) {
                List<Permission> permissions = permissionService.getPermissionByaccount(subAccount.getEmail());
                if (permissions.size() > 0) {
                    List<Permission> permissionTree = permissionService.getPermissionTree(permissions);
                    subAccount.setModule(permissionTree);
                }
            }
        }
        rpage.setList(listsa);
        return new RespInfo(0, "success", rpage);

    }

    @ApiOperation(value = "多企业服务_启用/停用多企业服务模式 /updateSASwitch ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"subAccount\":1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSASwitch")
    @ResponseBody
    public RespInfo updateSASwitch(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo requser,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = requser.getUserid();
        int saswitch = requser.getSubAccount();

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setSubAccount(saswitch);

        userService.updateSubAccountSwitch(userinfo);

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "培训问卷_启用/停用访客签到填写培训问卷 /updateQuestionnaireSwitch ", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"questionnaireSwitch\":1\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateQuestionnaireSwitch")
    @ResponseBody
    public RespInfo updateQuestionnaireSwitch(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateQuestionnaireSwitch(userinfo);
        return new RespInfo(0, "success");
    }



    @ApiOperation(value = "/getTimestamp 获取系统时间戳，用于签名算法", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getTimestamp")
    @ResponseBody
    public RespInfo getTimestamp(
            @ApiParam(value = "UserInfo 用户信息Bean", required = false) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {

        return new RespInfo(0, "success",Long.toString(System.currentTimeMillis()));
    }
    
    @ApiOperation(value = "停用/启用满意度调查问卷 /updateSatisfactionSwitch", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSatisfactionSwitch")
    @ResponseBody
    public RespInfo updateSatisfactionSwitch(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        userinfo.setUserid(authToken.getUserid());
        userService.updateSatisfactionSwitch(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "基本设置_邀请授权开关 /updateAppointmenProcessSwitch", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"appointmenProcessSwitch\":0" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateAppointmenProcessSwitch")
    @ResponseBody
    public RespInfo updateAppointmenProcessSwitch(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo requser,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(requser.getUserid());
        userinfo.setAppointmenProcessSwitch(requser.getAppointmenProcessSwitch());

        userService.updateAppointmenProcessSwitch(userinfo);

        return new RespInfo(0, "success");
    }



    @ApiOperation(value = "/getMsgTemplateList 获取消息模板列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"id\":模板id,\n" +
                    "\"type\":\"消息类型：SMS,WX,CAMPS,EMAIL\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getMsgTemplateList")
    @ResponseBody
    public RespInfoT<List<MsgTemplate>> getMsgTemplateList(@RequestBody MsgTemplate conf, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        return new RespInfoT(0, "success",msgTemplateService.getTemplateList(conf));
    }

    @ApiOperation(value = "/addMsgTemplateList 增加消息模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"id\":模板id,\n" +
                    "\"type\":\"消息类型：SMS,WX,CAMPS,EMAIL\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addMsgTemplateList")
    @ResponseBody
    public RespInfo addMsgTemplateList(@RequestBody MsgTemplate conf, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        conf.setUserid(0);
        MsgTemplate template = msgTemplateService.getTemplate(0, conf.getId());
        if(template!=null){
            int ret = msgTemplateService.updateMsgTemplate(conf);
            if (ret == 0) {
                return new RespInfo(ErrorEnum.E_063.getCode(), ErrorEnum.E_063.getMsg());
            }
        }else {
            int ret = msgTemplateService.addTemplate(conf);
            if (ret == 0) {
                return new RespInfo(ErrorEnum.E_063.getCode(), ErrorEnum.E_063.getMsg());
            }
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateMsgTemplate 修改 消息模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"id\":模板id,\n" +
                    "\"type\":\"消息类型：SMS,WX,CAMPS,EMAIL\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateMsgTemplate")
    @ResponseBody
    public RespInfo updateMsgTemplate(@RequestBody MsgTemplate conf, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        conf.setUserid(0);
        int ret = msgTemplateService.updateMsgTemplate(conf);
        if(ret == 0){
            return new RespInfo(ErrorEnum.E_063.getCode(), ErrorEnum.E_063.getMsg());
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delMsgTemplate 删除消息模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"id\":模板id,\n" +
                    "\"type\":\"消息类型：SMS,WX,CAMPS,EMAIL\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delMsgTemplate")
    @ResponseBody
    public RespInfo delMsgTemplate(@RequestBody MsgTemplate conf, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        conf.setUserid(0);
        int ret = msgTemplateService.delMsgTemplate(conf);
        if(ret == 0){
            return new RespInfo(ErrorEnum.E_063.getCode(), ErrorEnum.E_063.getMsg());
        }
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/checkSystem 系统检测", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"email\":账号,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/checkSystem")
    @ResponseBody
    public RespInfoT<List<RespSystemCheck>> checkSystem(@RequestBody UserInfo req, HttpServletRequest request) {

        UserInfo userinfo = userService.getUserInfoWithExt(req.getEmail());
        if(userinfo == null){
            return new RespInfoT(ErrorEnum.E_005.getCode(), ErrorEnum.E_005.getMsg());
        }
        List<RespSystemCheck> result = new ArrayList<>();

        //域名检测
        result.addAll(checkUrl());

        //短信检测
        result.addAll(checkSMSCount(userinfo));

        //磁盘使用情况
        result.addAll(checkDeskUsage());
        result.addAll(checkRedis(userinfo));
        result.addAll(checkMQ(userinfo));

        //传文件
        result.addAll(checkUploadFile());

        //配置
        result.addAll(checkInviteTemp(userinfo));

        //门禁组
        result.addAll(checkEquipmentGroup(userinfo));

        //账号
        result.addAll(checkAccounts(userinfo));

        result.addAll(checkWechat(userinfo));

        Collections.sort(result, new Comparator<RespSystemCheck>() {
            @Override
            public int compare(RespSystemCheck arg0, RespSystemCheck arg1) {
                return arg1.getError()-(arg0.getError());
            }
        });

        return new RespInfoT(0, "success",result);
    }


    /**
     * 账号MQ
     * @return
     */
    protected List<RespSystemCheck> checkMQ(UserInfo userInfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        try {
            result.add( new RespSystemCheck(messageSender.sendMessage("test msg")==0?RespSystemCheck.ERROR_CHECK_PASS:RespSystemCheck.ERROR_CHECK_UNPASS, "MQ检查", ""));
        }catch (Exception e){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "MQ检查", ""));
        }
        return result;
    }

    /**
     * 账号Redis
     * @return
     */
    protected List<RespSystemCheck> checkRedis(UserInfo userInfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("test", "key", "8888");
            if(hashOperations.get("test", "key")!=null){
                hashOperations.delete("test", "key");
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_PASS, "Redis检查", ""));
            }
        }catch (Exception e){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "Redis检查", ""));

        }
        return result;
    }

    /**
     * 账号检查
     * @return
     */
    protected List<RespSystemCheck> checkAccounts(UserInfo userInfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        Gate search = new Gate();
        search.setUserid(userInfo.getUserid());
        //获取门岗列表
        List<Gate> gateList = addressService.getGateList(search);
        if(gateList.size()==0){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "账号检查", "未配置门岗"));
            return result;
        }

        for(Gate gate:gateList) {
            Manager sManager = new Manager();
            List<Integer> stList = new ArrayList<>();
            stList.add(4);
            sManager.setStList(stList);
            sManager.setUserid(userInfo.getUserid());
            sManager.setGid(gate.getGid()+"");
            List<Manager> fkjs = managerService.getManagerList(sManager);
            if(fkjs.size()==0){
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "访客机账号", gate.getGname()+":未配置"));
            }

            stList.clear();
            stList.add(2);
            sManager.setStList(stList);
             fkjs = managerService.getManagerList(sManager);
            if(fkjs.size()==0){
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "前台账号", gate.getGname()+":未配置"));
            }
        }
        if(result.size()==0){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_PASS, "账号检查", ""));
        }
        return result;
    }

    /**
     * 磁盘检查
     * @return
     */
    protected List<RespSystemCheck> checkDeskUsage() {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        List<LinuxUtil.Desk> deskUsages = LinuxUtil.getDeskUsage();
        for(LinuxUtil.Desk desk:deskUsages) {
            RespSystemCheck diskCheck = new RespSystemCheck(RespSystemCheck.ERROR_CHECK_NOTHING, "磁盘检查", desk.toString());
            String rate = desk.getUse_rate();
            if (Float.parseFloat(rate.replace("%", "")) > 85) {
                diskCheck.setError(RespSystemCheck.ERROR_CHECK_UNPASS);
            } else {
                diskCheck.setError(RespSystemCheck.ERROR_CHECK_PASS);
            }
            result.add(diskCheck);
        }
        return result;
    }

    /**
     * 磁盘检查
     * @return
     */
    protected List<RespSystemCheck> checkUploadFile() {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();

        File f = new File("/work/account.properties");
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , f.getName());
        try (InputStream input = new FileInputStream(f);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile file =  new CommonsMultipartFile(item);

        String name = file.getOriginalFilename().trim();
        name = name.substring(name.lastIndexOf("/") + 1);
        String prefix = name.substring(name.lastIndexOf(".") + 1);

        String fileName=UtilTools.produceId(8)+System.currentTimeMillis()+"."+prefix;
        String url= MinioTools.uploadFile(file,Constant.BUCKET_NAME,"/pic/"+ YearMonth.now()+"/"+fileName);
        if(url != null){
            List<String> sList=new ArrayList<String>();
            sList.add(url);
            MinioTools.removeObjects(Constant.BUCKET_NAME,sList);
            result.add(new RespSystemCheck(RespSystemCheck.ERROR_CHECK_PASS, "上传文件检查成功",url ));
        }else{
            result.add(new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "上传文件检查失败", ""));

        }

        return result;
    }

    /**
     * 磁盘检查
     * @return
     */
    protected List<RespSystemCheck> checkSMSCount(UserInfo userinfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        int smscount = userinfo.getSmsCount() + userinfo.getWxSmsCount() + userinfo.getAppSmsCount();
        Configures conf = configureDao.getConfigure(userinfo.getUserid(), Constant.SMSCOUNT);
        if (null == conf) {
            conf = configureDao.getDefaultConfigure(Constant.SMSCOUNT);
        }

        if ((Integer.parseInt(conf.getValue()) - smscount) == 100
                || (Integer.parseInt(conf.getValue()) - smscount) == 50
                || (Integer.parseInt(conf.getValue()) - smscount) == 30
                || smscount > Integer.parseInt(conf.getValue())) {
            result.add(new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "短信检查", smscount + "/" + conf.getValue()));
        } else {
            result.add(new RespSystemCheck(RespSystemCheck.ERROR_CHECK_MANUAL, "短信检查", smscount + "/" + conf.getValue()));
        }
        return result;
    }

    /**
     * 域名检查
     * @return
     */
    protected List<RespSystemCheck> checkUrl() {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        result.add(new RespSystemCheck(RespSystemCheck.ERROR_CHECK_MANUAL,"域名检查", Constant.FASTDFS_URL));
        if(StringUtils.isNotEmpty(Constant.FASTDFS_URL)&&Constant.FASTDFS_URL.startsWith("https")) {
            String sslResult = LinuxUtil.checkUrl(Constant.FASTDFS_URL);
            if (StringUtils.isNotEmpty(sslResult)) {
                Date expireDate = new Date(Long.parseLong(sslResult));
                Date today = new Date();
                SysLog.info("expireDate.getTime() "+expireDate.getTime()+"-  today.getTime()="+today.getTime()+" ="+(expireDate.getTime() - today.getTime()));
                SysLog.info("expireDate.getTime() "+expireDate.getTime()+"-  today.getTime()="+today.getTime()+" ="+((expireDate.getTime() - today.getTime()) > 60 * 24 * 3600 * 1000));
                result.add(new RespSystemCheck((expireDate.getTime() - today.getTime()) >( 60 * 24 * 3600 * 1000 )? RespSystemCheck.ERROR_CHECK_PASS : RespSystemCheck.ERROR_CHECK_UNPASS, "SSL检查", "过期时间："+expireDate.toLocaleString()));
            }
        }
        return result;
    }

    /**
     * 检查邀请函模板
     * @return
     */
    protected List<RespSystemCheck> checkInviteTemp(UserInfo userInfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        Gate search = new Gate();
        search.setUserid(userInfo.getUserid());
        //获取门岗列表
        List<Gate> gateList = addressService.getGateList(search);
        if(gateList.size()==0){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "门岗", "未添加"));
            return result;
        }
        //获取访客类型
        VisitorType vt = new VisitorType();
        vt.setUserid(userInfo.getUserid());
        vt.setCategory(2);
        List<VisitorType> vtlist = visitorTypeService.getVisitorType(vt);
        if(vtlist.size()==0){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "访客类型", "未配置"));
            return result;
        }

        List<String> visitTypeList = new ArrayList<>();//拜访事由
        for(VisitorType visitorType:vtlist){
            //访客类型详情
            ExtendVisitor ev = new ExtendVisitor();
            ev.seteType(visitorType.getvType());
            ev.setUserid(userInfo.getUserid());
            List<ExtendVisitor> visitorTypeInfos = extendVisitorService.getExtendVisitorByType(ev);
            if(visitorTypeInfos.size()==0){
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "访客类型", visitorType.getvType()+":未配置"));
                break;
            }


            for(ExtendVisitor extendVisitor:visitorTypeInfos){
                //有效期检查
                if(extendVisitor.getFieldName().equals("qrcodeConf")){
                    if(StringUtils.isEmpty(extendVisitor.getInputValue())){
                        result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "访客类型.有效期", visitorType.getvType()+":未配置"));
                    }
                }

                //拜访事由
                if(extendVisitor.getFieldName().equals("visitType")){
                    if(StringUtils.isEmpty(extendVisitor.getInputValue())){
                        result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "访客类型.拜访事由", visitorType.getvType()+":未配置"));
                    }else {
                        String[] vts = extendVisitor.getInputValue().split(",");
                        for (int i = 0; i < vts.length; i++) {
                            if (!visitTypeList.contains(vts[i])) {
                                visitTypeList.add(vts[i]);
                            }
                        }
                    }
                }


            }
        }
        for(Gate gate:gateList){
            Usertemplate ut = new Usertemplate();
            ut.setGid(gate.getGid());
            ut.setUserid(userInfo.getUserid());
            List<Usertemplate> utlist = appointmentService.getUserTemplateType(ut);
            if(utlist.size()==0){
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "邀请函模板", gate.getGname()+":未配置"));
                continue;
            }

            Map<String,Usertemplate> tmpMap = new HashMap<String,Usertemplate>();//邀请函模板列表
            for(Usertemplate usertemplate:utlist){
                tmpMap.put(usertemplate.getTemplateType(),usertemplate);
            }

            for(String visitType:visitTypeList){
                if(tmpMap.get(visitType)==null){
                    result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "邀请函模板", gate.getGname()+"."+visitType+":未配置"));
                }
            }
        }
        if(result.size()==0){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_PASS, "访客类型设置", ""));
        }
        return result;
    }

    /**
     * 检查门禁组
     * @param userInfo
     * @return
     */
    protected List<RespSystemCheck> checkEquipmentGroup(UserInfo userInfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();

        Gate search = new Gate();
        search.setUserid(userInfo.getUserid());
        //获取门岗列表
        List<Gate> gateList = addressService.getGateList(search);
        if(gateList.size()==0){
            return result;
        }
        for(Gate gate:gateList){
            //访客门禁组
            EquipmentGroup seg=new EquipmentGroup();
            seg.setUserid(userInfo.getUserid());
            seg.setStatus(1);
            seg.setGids(gate.getGid()+"");
            seg.setReqEtype("(0,2)");
            List<EquipmentGroup> eglist = equipmentGroupService.getEquipmentGroupByUserid(seg);
            if(eglist.size()==0){
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "访客门禁组", gate.getGname()+":未配置"));
            }

            //员工门禁组
            seg.setUserid(userInfo.getUserid());
            seg.setReqEtype("(0,1)");
            eglist = equipmentGroupService.getEquipmentGroupByUserid(seg);
            if(eglist.size()==0){
                result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_UNPASS, "员工门禁组", gate.getGname()+":未配置"));
            }
        }

        if(result.size()==0){
            result.add( new RespSystemCheck(RespSystemCheck.ERROR_CHECK_PASS, "门禁组设置", ""));
        }
        return result;
    }


    /**
     * 组件检查
     * @return
     */
    protected List<RespSystemCheck> checkWechat(UserInfo userInfo) {
        List<RespSystemCheck> result =new  ArrayList<RespSystemCheck>();
        String ret = wechatService.test(userInfo);
        result.add( new RespSystemCheck(StringUtils.isEmpty(ret)?RespSystemCheck.ERROR_CHECK_PASS:RespSystemCheck.ERROR_CHECK_UNPASS, "微信服务号设置", ret));

        ret = wechatBusService.test(userInfo);
        result.add( new RespSystemCheck(StringUtils.isEmpty(ret)?RespSystemCheck.ERROR_CHECK_PASS:RespSystemCheck.ERROR_CHECK_UNPASS, "企业微信服务号设置", ret));

        ret = dingtalkService.test(userInfo);
        result.add( new RespSystemCheck(StringUtils.isEmpty(ret)?RespSystemCheck.ERROR_CHECK_PASS:RespSystemCheck.ERROR_CHECK_UNPASS, "钉钉设置", ret));

        ret = feishuService.test(userInfo);
        result.add( new RespSystemCheck(StringUtils.isEmpty(ret)?RespSystemCheck.ERROR_CHECK_PASS:RespSystemCheck.ERROR_CHECK_UNPASS, "飞书设置", ret));

        return result;
    }

    @ApiOperation(value = "更新风险区域 /updateRiskArea", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateRiskArea")
    @ResponseBody
    public RespInfo updateRiskArea(@ApiParam(value = "UserInfo 用户信息Bean", required = true) @RequestBody UserInfo userinfo,
                                   HttpServletRequest request){
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        userinfo.setUserid(authToken.getUserid());
        //userService.updateSatisfactionSwitch(userinfo);
        int count = userService.updateRiskArea(userinfo);
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/getRiskArea", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"id\":模板id,\n" +
                    "\"type\":\"消息类型：SMS,WX,CAMPS,EMAIL\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRiskArea")
    @ResponseBody
    public RespInfoT<List<MsgTemplate>> getRiskArea(@RequestBody UserInfo userInfo) {
        return new RespInfoT(0, "success",userService.getRiskArea(userInfo));
    }


    @ApiOperation(value = "/releaseLock 解除ip地址锁定", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"rtxIp\":192.0.0.1 \n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/releaseLock")
    @ResponseBody
    public RespInfo releaseLock(
            @ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo requser,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        RedisUtil redisUtil = SpringContextHolder.getBean(RedisUtil.class);
        Boolean ret = redisUtil.releaseLock(IpUrlLimitInterceptor.LOCK_IP_URL_KEY+requser.getRtxIp(),requser.getRtxIp());
        if(ret) {
            return new RespInfo(0, "success");
        }

        return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
    }

}
