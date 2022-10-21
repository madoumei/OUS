package com.web.controller;

import com.client.bean.*;
import com.client.service.DataStatisticsService;
import com.client.service.ExtendVisitorService;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.utils.*;
import com.utils.emailUtils.SendTextEmail;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "UserController", tags = "API_用户管理", hidden = true)
public class UserController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ConfigureService configureService;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private ExtendVisitorService extendVisitorService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ManagerService mgrService;

    @Autowired
    private DataStatisticsService dataStatisticsService;

    public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L; // milliseconds

    @ApiOperation(value = "/GetPicUrl 获取配置信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetPicUrl")
    @ResponseBody
    public RespInfo GetPicUrl(@RequestBody RequestVisit req, HttpServletRequest request) {
        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, req.getUserid());
        if (respInfo != null) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }

        UserInfo userinfo = userService.getUserInfoByUserId(req.getUserid());
        if (userinfo == null) {
            throw new RuntimeException(ErrorEnum.E_609.getMsg());
        }
        Configures conf = configureService.getConfigure(req.getUserid(), Constant.WATERMARK);
        Configures face = configureService.getDefaultConfigure(Constant.FACESCAN);


        RespPicUrl rpu = new RespPicUrl();
        rpu.setBgurl(userinfo.getBackgroundPic());
        rpu.setLogourl(userinfo.getLogo());
        rpu.setThemecolor(userinfo.getThemecolor());
        rpu.setDefaultphoto(userinfo.getDefaultPhoto());
        rpu.setWatermark(conf.getValue());
        rpu.setWmstatus(conf.getStatus());
        rpu.setCardtext(userinfo.getCardText());
        rpu.setCardPic(userinfo.getCardPic());
        rpu.setQuestionnaireSwitch(userinfo.getQuestionnaireSwitch());

        rpu.setQrcode(userinfo.getQrcode());
        if (userinfo.getCardType() == 4 || userinfo.getQrcodeSwitch() == 1) {
            rpu.setQrcodeSwitch(1);
        } else {
            rpu.setQrcodeSwitch(0);
        }
        rpu.setQrcodeType(userinfo.getQrcodeType());
        rpu.setTeamVisitSwitch(0);
        rpu.setComeAgain(userinfo.getComeAgain());
        rpu.setCardType(userinfo.getCardType());
        rpu.setCardSize(userinfo.getCardSize());
        rpu.setCardLogo(userinfo.getCardLogo());
        rpu.setPrintType(userinfo.getPrintType());
        rpu.setUserType(userinfo.getUserType());
        rpu.setFaceScanThreshold(face.getValue());
        rpu.setPreRegisterSwitch(userinfo.getPreRegisterSwitch());
        rpu.setIvrPrint(userinfo.getIvrPrint());
        rpu.setExpireDate(userinfo.getExpireDate());
        rpu.setSignOutSwitch(userinfo.getSignOutSwitch());
        rpu.setBadgeMode(userinfo.getBadgeMode());
        rpu.setBadgeCustom(userinfo.getBadgeCustom());
        rpu.setBrandType(userinfo.getBrandType());
        rpu.setBrandPosition(userinfo.getBrandPosition());
        rpu.setShowAvatar(userinfo.getShowAvatar());
        rpu.setAvatarType(userinfo.getAvatarType());
        rpu.setCustomText(userinfo.getCustomText());
        rpu.setPermissionSwitch(userinfo.getPermissionSwitch());
        rpu.setSecureProtocol(userinfo.getSecureProtocol());
        rpu.setEpidemic(userinfo.getEpidemic());
        rpu.setProcessSwitch(userinfo.getProcessSwitch());

        List<ExtendVisitor> list = extendVisitorService.getTeamExtendVisitor(req.getUserid());
        if (userinfo.getUserType() != 0) {
            for (int i = 0; i < list.size(); i++) {
                if (null != list.get(i).getPlaceholder() && !"".equals(list.get(i).getPlaceholder())) {
                    rpu.setTeamVisitSwitch(1);
                    break;
                }
            }
        }

        return new RespInfo(0, "success", rpu);
    }

    @ApiOperation(value = "/addExtendType 添加拓展类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addExtendType")
    @ResponseBody
    public RespInfo addExtendType(@RequestBody List<ExtendVisitor> listev, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (!listev.isEmpty()) {
            extendVisitorService.delExtendVisitorByType(listev.get(0));

            for (int i = 0; i < listev.size(); i++) {
                ExtendVisitor ev = new ExtendVisitor();
                ev.setDisplayName(listev.get(i).getDisplayName());
                ev.setFieldName(listev.get(i).getFieldName());
                ev.setInputType(listev.get(i).getInputType());
                ev.setInputValue(listev.get(i).getInputValue());
                ev.setInputOrder(listev.get(i).getInputOrder());
                ev.setRequired(listev.get(i).getRequired());
                ev.setUserid(listev.get(i).getUserid());
                ev.setPlaceholder(listev.get(i).getPlaceholder());
                ev.setIsDisplay(listev.get(i).getIsDisplay());
                ev.seteType(listev.get(i).geteType());

                extendVisitorService.addExtendVisitor(ev);

                if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                        && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                        ||authToken.getUserid() != ev.getUserid()){
                    return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }
            }
        }


        return new RespInfo(0, "success", listev);
    }

    @ApiOperation(value = "/getExtendTypeInfo 获取拓展类型信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"123456\",\n" +
                    "    \"eType\":\"0\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getExtendTypeInfo")
    @ResponseBody
    public RespInfo getExtendTypeInfo(
            @ApiParam(value = "ExtendVisitor 拓展类型访客Bean", required = true) @RequestBody @Validated ExtendVisitor ev,
            BindingResult result, HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        List<ExtendVisitor> evlist = extendVisitorService.getExtendVisitorByType(ev);
        return new RespInfo(0, "success", evlist);
    }

    @ApiOperation(value = "/getExtendType 获取拓展类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getExtendType")
    @ResponseBody
    public RespInfo getExtendType(@RequestBody ExtendVisitor ev) {
        List<String> eList = extendVisitorService.getExtendTypeList(ev.getUserid());
        return new RespInfo(0, "success", eList);
    }

    @ApiOperation(value = "/delExtendType 删除拓展类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delExtendType")
    @ResponseBody
    public RespInfo delExtendType(@RequestBody ExtendVisitor ev, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ev.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        extendVisitorService.delExtendVisitorByType(ev);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getVisitorTypeByTid 获取访客类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"123456\",\n" +
                    "    \"eType\":\"0\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorTypeByTid")
    @ResponseBody
    public RespInfo getVisitorTypeByTid(@RequestBody VisitorType vt,HttpServletRequest request, BindingResult result) {

        //token和签名检查
        RespInfo respInfo = UtilTools.checkTokenAndSign(redisTemplate, tokenServer, request, vt.getUserid());
        if (respInfo != null) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            //访客token没有userid
            if (!(respInfo.getStatus() == ErrorEnum.E_610.getCode() && authToken.getUserid() == 0)) {
                return respInfo;
            }
        }

        vt = visitorTypeService.getVisitorTypeByTid(vt);

        return new RespInfo(0, "success", vt);
    }

    @ApiOperation(value = "/updateDDScannerCodeConfig 叮叮扫码登录配置", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\":2147483647,\n" +
                    "\t\"ddAppid\":\"dingoaiqxjyzuncdj6uggl\",\n" +
                    "\t\"ddAppSccessSecret\":\"F58vASjxwCJRrxmuYZtGw-9wZHfEuu0SMKQNjMFkzupDjHpm-WVT75vl26KlEnyz\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateDDScannerCodeConfig")
    @ResponseBody
    public RespInfo updateDDScannerCodeConfig(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userInfo,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userInfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = userInfo.getUserid();
        String ddAppid = userInfo.getDdAppid();
        String ddAppSccessSecret = userInfo.getDdAppSccessSecret();
        userInfo = userService.getUserInfoByUserId(userid);
        if (null != userInfo) {
            userInfo.setDdAppid(ddAppid);
            userInfo.setDdAppSccessSecret(ddAppSccessSecret);
            int i = userService.updateDDScannerConf(userInfo);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/GetUserInfo 获取用户信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647" +
                    "\"email\":email" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/GetUserInfo")
    @ResponseBody
    public RespInfo GetUserInfo(
            @ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo reqinfo,
            BindingResult result, HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        reqinfo.setUserid(authToken.getUserid());
        UserInfo userinfo = userService.getUserByAccount(reqinfo);
        Configures conf = configureService.getConfigure(userinfo.getUserid(), Constant.SMSCOUNT);
        userinfo.setSmsTotal(Integer.parseInt(conf.getValue()));

        return new RespInfo(0, "success", userinfo);
    }

    @ApiOperation(value = "/addUserTemplate 添加用户模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1,\n" +
                    "    \"inviteContent\":\"思维针对于实体店铺，多样化的支付方式、简单的操作流程、和强大的商业管理系统能够很好提高买单效率\",\n" +
                    "    \"templateType\":\"面试\",\n" +
                    "    \"address\":\"北京市东城区西四\",\n" +
                    "    \"longitude\":\"94.52\",\n" +
                    "    \"latitude\":\"125.26\",\n" +
                    "    \"companyProfile\":\"思维针对于实体店铺，多样化的支付方式、简单的操作流程、和强大的商业管理系统能够很好提高买单效率，提升用户体验。\",\n" +
                    "    \"traffic\":\"乘坐地铁十三号线\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addUserTemplate")
    @ResponseBody
    public RespInfo addUserTemplate(
            @ApiParam(value = "Usertemplate 用户模板Bean", required = true) @RequestBody @Validated Usertemplate ut,
            BindingResult result, HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ut.getUserid() && authToken.getAccountRole().equals("6")) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int a = appointmentService.addUsertemplate(ut);
        if (a > 0) {
            UserInfo userinfo = new UserInfo();
            userinfo.setUserid(ut.getUserid());
            userinfo.setPreRegisterSwitch(1);

            userService.updatePreRegisterSwitch(userinfo);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateUserTemplate 更新用户模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1,\n" +
                    "    \"inviteContent\":\"思维针对于实体店铺，多样化的支付方式、简单的操作流程、和强大的商业管理系统能够很好提高买单效率\",\n" +
                    "    \"templateType\":\"面试\",\n" +
                    "    \"address\":\"北京市东城区西四\",\n" +
                    "    \"longitude\":\"94.52\",\n" +
                    "    \"latitude\":\"125.26\",\n" +
                    "    \"companyProfile\":\"思维针对于实体店铺，多样化的支付方式、简单的操作流程、和强大的商业管理系统能够很好提高买单效率，提升用户体验。\",\n" +
                    "    \"traffic\":\"乘坐地铁十三号线\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateUserTemplate")
    @ResponseBody
    public RespInfo updateUserTemplate(
            @ApiParam(value = "Usertemplate 用户模板Bean", required = true) @RequestBody @Validated Usertemplate ut,
            BindingResult result, HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ut.getUserid() && authToken.getAccountRole().equals("6")) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        //获取老记录
        Usertemplate usertemp = appointmentService.getOldUsertemplate(ut);
        if (null != usertemp) {
            appointmentService.updateOldUsertemplate(ut);
        }

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/getUsertemplate 获取用户模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1,\n" +
                    "    \"templateType\":面试#Interview,\n" +
                    "    \"gid\":1,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getUsertemplate")
    @ResponseBody
    public RespInfo getUsertemplate(
            @ApiParam(value = "Usertemplate 用户模板Bean", required = true) @RequestBody @Validated Usertemplate ut,
            BindingResult result, HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ut.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        ut = appointmentService.getUsertemplate(ut);
        return new RespInfo(0, "success", ut);
    }


    @ApiOperation(value = "/delUsertemplate 删除用户模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1,\n" +
                    "    \"templateType\":面试#Interview,\n" +
                    "    \"gid\":1,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delUsertemplate")
    @ResponseBody
    public RespInfo delUsertemplate(
            @ApiParam(value = "Usertemplate 用户模板Bean", required = true) @RequestBody @Validated Usertemplate ut,
            BindingResult result, HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != ut.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        appointmentService.delUsertemplate(ut);
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/UpdateLogo 上传logo", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1,\n" +
                    "    \"logoUrl\":logoUrl\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateLogo")
    @ResponseBody
    public RespInfo UploadLogo(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        String logo = reqinfo.getLogoUrl();
        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setLogo(logo);
        userService.updateLogo(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateCardStyle 更新贴纸类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateCardStyle")
    @ResponseBody
    public RespInfo updateCardStyle(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        int printType = reqinfo.getPrintType();
        int cardType = reqinfo.getCardType();
        int cardSize = reqinfo.getCardSize();
        int cardLogo = reqinfo.getCardLogo();
        String cardtext = reqinfo.getCardText();
        String cardPic = reqinfo.getCardPic();
        int badgeMode = reqinfo.getBadgeMode();
        String badgeCustom = reqinfo.getBadgeCustom();
        int brandType = reqinfo.getBrandType();
        int brandPosition = reqinfo.getBrandPosition();
        int showAvatar = reqinfo.getShowAvatar();
        int avatarType = reqinfo.getAvatarType();
        String customText = reqinfo.getCustomText();


        UserInfo userinfo = userService.getUserInfo(userid);
        userinfo.setPrintType(printType);
        userinfo.setCardType(cardType);
        userinfo.setCardSize(cardSize);
        userinfo.setCardLogo(cardLogo);
        userinfo.setBadgeMode(badgeMode);
        userinfo.setBadgeCustom(badgeCustom);
        userinfo.setBrandType(brandType);
        userinfo.setBrandPosition(brandPosition);
        userinfo.setShowAvatar(showAvatar);
        userinfo.setAvatarType(avatarType);
        userinfo.setCustomText(customText);
        userService.updateCardStyle(userinfo);
        if ((null != cardtext && !"".equals(cardtext)) || (null != cardPic && !"".equals(cardPic))) {
            userinfo.setCardText(cardtext);
            userinfo.setCardPic(cardPic);
            userService.updateCardText(userinfo);
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateQRcode 更新code", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateQRcode")
    @ResponseBody
    public RespInfo updateQRcode(@RequestBody UserInfo userinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateQRcode(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/UploadBackgroundPic 更新背景图", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UploadBackgroundPic")
    @ResponseBody
    public RespInfo UploadBackgroundPic(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = reqinfo.getUserid();
        String bgurl = reqinfo.getBgPicUrl();
        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setBackgroundPic(bgurl);
        userService.updatebgPicUrl(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addVisitorType 添加访客类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647" +
                    "\"category\":category" +
                    "\"vType\":vType(访客类别 1 物流 2访客)" +
                    "\"povDays\":povDays" +
                    "\"qid\":qid" +
                    "\"gateType\":gateType" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addVisitorType")
    @ResponseBody
    public RespInfo addVisitorType(@RequestBody VisitorType vt,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != vt.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<VisitorType> vtList = visitorTypeService.getVisitorType(vt);
        if (vtList.size() > 0) {
            return new RespInfo(78, "vType already  Exist");
        }

        visitorTypeService.addVisitorType(vt);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delVisitorType 删除访客类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delVisitorType")
    @ResponseBody
    public RespInfo delVisitorType(@RequestBody VisitorType vt,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != vt.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        vt = visitorTypeService.getVisitorTypeByTid(vt);
        ExtendVisitor ev = new ExtendVisitor();
        ev.setUserid(vt.getUserid());
        ev.seteType(vt.getvType());
        extendVisitorService.delExtendVisitorByType(ev);

        visitorTypeService.delVisitorType(vt);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getVisitorType 根据userid获取访客类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"category\":\"category\",\n" +
                    "    \"vType\":\"vType\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorType")
    @ResponseBody
    public RespInfo getVisitorType(@RequestBody VisitorType vt) {
        List<VisitorType> vtlist = visitorTypeService.getVisitorType(vt);
        return new RespInfo(0, "success", vtlist);
    }

    @ApiOperation(value = "/updateVisitorType 更新访客类型", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateVisitorType")
    @ResponseBody
    public RespInfo updateVisitorType(@RequestBody VisitorType vt,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != vt.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        VisitorType oldvt = visitorTypeService.getVisitorTypeByTid(vt);
        vt.setCategory(oldvt.getCategory());
        List<VisitorType> vtList = visitorTypeService.getVisitorType(vt);
        if (vtList.size() > 0 && vtList.get(0).getTid() != vt.getTid()) {
            return new RespInfo(78, "vType already  Exist");
        }
        visitorTypeService.updateVisitorType(vt);
        ExtendVisitor ev = new ExtendVisitor();
        ev.setOldeType(vt.getOldvType());
        ev.seteType(vt.getvType());
        ev.setUserid(vt.getUserid());
        extendVisitorService.updateExtendVisitor(ev);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateSecureProtocol 更新人脸采集协议", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSecureProtocol")
    @ResponseBody
    public RespInfo updateSecureProtocol(@RequestBody UserInfo userinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateSecureProtocol(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/UpdateThemeColor 更新主题颜色", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateThemeColor")
    @ResponseBody
    public RespInfo UpdateThemeColor(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        String themecolor = reqinfo.getThemecolor();
        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setThemecolor(themecolor);
        userService.updateThemeColor(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/UpdateDefaultPhoto 更新默认照片", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateDefaultPhoto")
    @ResponseBody
    public RespInfo UpdateDefaultPhoto(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = reqinfo.getUserid();
        String defaultPhoto = reqinfo.getDefaultPhoto();

        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setDefaultPhoto(defaultPhoto);
        userService.updateDefaultPhoto(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateFaceScaner 刷脸签到开关", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateFaceScaner")
    @ResponseBody
    public RespInfo updateFaceScaner(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = reqinfo.getUserid();
        int faceScaner = reqinfo.getFaceScaner();
        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setFaceScaner(faceScaner);
        userService.updateFaceScaner(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addGate 添加门岗", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addGate")
    @ResponseBody
    public RespInfo addGate(@RequestBody List<Gate> glist,HttpServletRequest request) {
//        ListOperations<String, Gate> listOperations = redisTemplate.opsForList();
//        redisTemplate.delete("gatemanage");
//        for(int i=0;i<glist.size();i++) {
//        	glist.get(i).setGid("");
//        }
//        listOperations.rightPushAll("gatemanage", glist.toArray(new Gate[glist.size()]));
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (glist.size() == 0) {
            return new RespInfo(0, "success");
        }
        int userId = glist.get(0).getUserid();
        Configures conf = configureService.getConfigure(userId, Constant.GATECOUNT);
        int confstatus = conf.getStatus();
        Gate gate = new Gate();
        gate.setUserid(userId);
        List<Gate> gateList = addressService.getGateList(gate);

        int ecc = Integer.parseInt(conf.getValue());
        if (confstatus == 1 && (gateList.size() + glist.size()) > ecc) {
            return new RespInfo(43, "gate exceed the maximum limit " + ecc);
        }

        for (int i = 0; i < glist.size(); i++) {
            if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                    ||authToken.getUserid() != glist.get(i).getUserid()){
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            addressService.addGate(glist.get(i));
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateGate 更新门岗", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateGate")
    @ResponseBody
    public RespInfo updateGate(@RequestBody Gate gate,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != gate.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        addressService.updateGate(gate);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delGate 删除门岗", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delGate")
    @ResponseBody
    public RespInfo delGate(@RequestBody Gate gate,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != gate.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        addressService.delGate(gate);

        Usertemplate ut = new Usertemplate();
        ut.setUserid(gate.getUserid());
        ut.setGid(gate.getGid());

        appointmentService.delUsertemplate(ut);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getGateById 获取门岗信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"gids\":\"gids\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getGateById")
    @ResponseBody
    public RespInfo getGateById(
            @ApiParam(value = "Gate 门岗Bean", required = true) @Validated @RequestBody Gate gate,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != gate.getUserid()) {
            throw new RuntimeException(ErrorEnum.E_610.getMsg());
        }

        gate.setUserid(authToken.getUserid());
        List<Gate> glist = addressService.getGateById(gate);
        return new RespInfo(0, "success", glist);
    }

    @ApiOperation(value = "/getGate 获取所有门岗", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getGate")
    @ResponseBody
    public RespInfo getGate(@RequestBody Gate gate,HttpServletRequest request) {
        if(gate.getUserid()==0) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            gate.setUserid(authToken.getUserid());
        }
        List<Gate> glist = addressService.getGateList(gate);
        return new RespInfo(0, "success", glist);
    }

    @ApiOperation(value = "/ModifyUserInfo 账号信息修改", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2,\n" +
                    "    \"name\":\"mld\",\n" +
                    "    \"phone\":321232123,\n" +
                    "    \"company\":\"访客通\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/ModifyUserInfo")
    @ResponseBody
    public RespInfo ModifyUserInfo(@ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
                                   HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != reqinfo.getUserid() ||
                (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                        && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        String username = reqinfo.getName();
        String phone = reqinfo.getPhone();
        String company = reqinfo.getCompany();
        UserInfo userinfo = userService.getUserInfo(userid);
        userinfo.setUsername(username);
        userinfo.setPhone(phone);


        UserInfo user = userService.selectBycompany(company);
        if (user == null || user.getUserid() == userinfo.getUserid()) {
            userinfo.setCompany(company);
        } else {
            return new RespInfo(46, "company name already exist");
        }

        //为saas添加
        if (reqinfo.getCustAddress() != null) {
            userinfo.setCustAddress(reqinfo.getCustAddress());
            userService.updateCustInfo(userinfo);
        }

        userService.updateUserInfo(userinfo);
        return new RespInfo(0, "success", userinfo);
    }



    @ApiOperation(value = "/updatePassword 更新账号密码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1,\n" +
                    "    \"oldpwd\":\"xxxxx\",\n" +
                    "    \"newpwd\":\"xxxxx\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updatePassword")
    @ResponseBody
    public RespInfo updatePassword(@ApiParam(value = "UpPassword 更新密码Bean", required = true) @Validated @RequestBody UpPassword up,
                                   HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != up.getUserid() || !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        String account = up.getAccount();
        int userid = up.getUserid();
        String op = up.getOldpwd();
        String np = up.getNewpwd();
        int failcount = 0;
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

        UserInfo userinfo = new UserInfo();
        if (userid == 0) {
            userinfo = userService.getUserInfoWithExt(account);
        } else {
            userinfo = userService.getUserInfo(userid);
            userinfo = userService.getUserInfoWithExt(userinfo.getEmail());
        }

        if (hashOperations.hasKey("fail_login" + account, "locktime")) {
            Long locktime = (long) hashOperations.get("fail_login" + account, "locktime");
            if (new Date().getTime() - locktime > Constant.LOCK_TIME) {
                hashOperations.delete("fail_login" + account, "locktime");
                hashOperations.delete("fail_login" + account, "failcount");
            } else {
                return new RespInfo(301, "please retry  after 5 minutes");
            }
        }

        if (hashOperations.hasKey("fail_login" + account, "failcount")) {
            failcount = (int) hashOperations.get("fail_login" + account, "failcount");
        }

        if (failcount > 3) {
            hashOperations.put("fail_login" + account, "locktime", new Date().getTime());
            return new RespInfo(301, "please retry  after 5 minutes");
        }


        if (userinfo.getPassword().compareTo(MD5.crypt(op)) == 0) {
            userinfo.setPassword(MD5.crypt(np));
            userService.updatePwd(userinfo);
            hashOperations.put(userinfo.getEmail() + "_mfpwd", "adminUser", System.currentTimeMillis());
        } else {
            hashOperations.put("fail_login" + account, "failcount", failcount + 1);
            if (failcount == 0) {
                redisTemplate.expire("fail_login" + account, 10, TimeUnit.MINUTES);
            }
            return new RespInfo(75, "Invalid username or password");
        }

        if (hashOperations.hasKey("fail_login" + account, "failcount")) {
            hashOperations.delete("fail_login" + account, "failcount");
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/UpdateCardText 设置名片显示文字", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"cardText\":\"上海汽车\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateCardText")
    @ResponseBody
    public RespInfo UpdateCardText(@ApiParam(value = "ReqUserInfo 请求_用户详情", required = true) @Validated @RequestBody ReqUserInfo reqinfo,
                                   HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != reqinfo.getUserid() || !"6".equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int userid = reqinfo.getUserid();
        String cardtext = reqinfo.getCardText();
        String cardPic = reqinfo.getCardPic();

        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setCardText(cardtext);
        ui.setCardPic(cardPic);
        userService.updateCardText(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateWxBusNotify 企业微信提醒开关 0 关 1开", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"wxBusNotify\":1" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateWxBusNotify")
    @ResponseBody
    public RespInfo updateWxBusNotify(@RequestBody UserInfo ui,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ui.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateWxBusNotify(ui);
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/updateFsNotify 飞书提醒开关 0 关 1 开", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"fsNotify\":1" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateFsNotify")
    @ResponseBody
    public RespInfo updateFsNotify(@RequestBody UserInfo ui,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ui.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateFsNotify(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateNotifyType 消息提醒类型 0 全发 1 只发一个", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"notifyType\":1" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateNotifyType")
    @ResponseBody
    public RespInfo updateNotifyType(@RequestBody UserInfo ui,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != ui.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateNotifyType(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateProcessSwitch 设置流程开关", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"processSwitch\":\"0\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateProcessSwitch")
    @ResponseBody
    public RespInfo updateProcessSwitch(@ApiParam(value = "UserInfo 用户信息Bean", required = true) @Validated @RequestBody UserInfo userinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateProcessSwitch(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateblackListSwitch 更新黑名单开关", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateblackListSwitch")
    @ResponseBody
    public RespInfo updateblackListSwitch(@RequestBody UserInfo userinfo, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != userinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        userService.updateblackListSwitch(userinfo);

        UserInfo userInfo = userService.getUserInfo(userinfo.getUserid());
        String optId = userInfo.getEmail();
        String optName = userInfo.getUsername();
        String optRole = "0";
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(userInfo.getUserid());
        log.setOptId(optId);
        log.setOptName(optName);
        log.setOptRole(optRole);
        log.setIpAddr(ipAddr);
        log.setObjId("");
        log.setObjName("黑名单");
        log.setoTime(new Date());
        log.setOptEvent("修改");
        log.setOptClient("0");
        log.setOptModule("6");
        if (userInfo.getBlackListSwitch() == 0) {
            log.setOptDesc("成功,停用黑名单功能");
        } else {
            log.setOptDesc("成功,启用黑名单功能");
        }
        operateLogService.addLog(log);
        return new RespInfo(0, "success");

    }

    @ApiOperation(value = "/updateComeAgain 曾经来过", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateComeAgain")
    @ResponseBody
    public RespInfo updateComeAgain(@RequestBody ReqUserInfo requser,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = requser.getUserid();
        int ca = requser.getComeAgain();
        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setComeAgain(ca);
        userService.updateComeAgain(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateSignOutSwitch 登出开关", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateSignOutSwitch")
    @ResponseBody
    public RespInfo updateSignOutSwitch(@RequestBody ReqUserInfo requser,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(requser.getUserid());
        userinfo.setSignOutSwitch(requser.getSignOutSwitch());
        userService.updateSignOutSwitch(userinfo);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateIdCardSwitch 证件扫描", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateIdCardSwitch")
    @ResponseBody
    public RespInfo updateIdCardSwitch(@RequestBody ReqUserInfo requser,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != requser.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(requser.getUserid());
        userinfo.setIdCardSwitch(requser.getIdCardSwitch());

        userService.updateIdCardSwitch(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateWechatConf 配置同步企业微信", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647," +
                    "\"corpid\":\"xxxxxxx\"" +
                    "\"corpsecret\":\"xxxxxxx\"" +
                    "\"agentid\":\"xxxxxxx\"" +
                    "\"permanentCode\":\"起始部门id\"" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateWechatConf")
    @ResponseBody
    public RespInfo updateWechatConf(@RequestBody ReqUserInfo req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = req.getUserid();

        String corpid = req.getCorpid();
        String corpsecret = req.getCorpsecret();
        String agentid = req.getAgentid();
        String permanentCode = req.getPermanentCode();

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setCorpid(corpid);
        userinfo.setCorpsecret(corpsecret);
        userinfo.setAgentid(agentid);
        userinfo.setPermanentCode(permanentCode);


        String result = UtilTools.checkWeChartAccess_token(corpid, corpsecret);

        if (StringUtils.isEmpty(result)) {
            return new RespInfo(669, "wecahrt configure failed");
        }

        userService.updateWechatConf(userinfo);

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/updateFsConf 配置飞书", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647,\n" +
                    "\"securityID\":\"AppId\",\n" +
                    "\"securityKey\":\"AppSecret\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateFsConf")
    @ResponseBody
    public RespInfo updateFsConf(@RequestBody ReqUserInfo req,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != req.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = req.getUserid();

        String appid = req.getSecurityID();
        String appsecret = req.getSecurityKey();

        UserInfo userinfo = new UserInfo();
        userinfo.setUserid(userid);
        userinfo.setSecurityID(appid);
        userinfo.setSecurityKey(appsecret);


        String result = UtilTools.checkFeiShuConfigure(appid, appsecret);

        if (StringUtils.isEmpty(result)) {
            return new RespInfo(666, "feishu configure failed");
        }

        userService.updateVNetConf(userinfo);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getConfigure 获取配置信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":userid,\n" +
                    "\"name\":\"短信数量:smscount，门岗数量:gatecount:\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getConfigure")
    @ResponseBody
    public RespInfo getConfigure(@RequestBody Configures conf, HttpServletRequest request) {

        Configures c = configureService.getConfigure(conf.getUserid(), conf.getName());
        if (null != c) {
            return new RespInfo(0, "success", c);
        }
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/addConfigure 设置短信", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647,\n" +
                    "\"desc\":\"xx数量\",\n" +
                    "\"name\":\"短信数量:smscount，门岗数量:gatecount:\",\n" +
                    "\"value\":\"27000\",\n" +
                    "\"status\":1,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addConfigure")
    @ResponseBody
    public RespInfo addConfigure(@RequestBody Configures conf, HttpServletRequest request) {

        //权限判断，只有超级管理员才能有的权限
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != conf.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Configures c = configureService.checkConfigure(conf.getUserid(), conf.getName());
        if (null != c) {
            c.setDesc(conf.getDesc());
            c.setName(conf.getName());
            c.setValue(conf.getValue());
            c.setStatus(conf.getStatus());
            configureService.updateConfigure(c);
        } else {
            c = new Configures();
            c.setName(conf.getName());
            c.setValue(conf.getValue());
            c.setUserid(conf.getUserid());
            c.setDesc(conf.getDesc());
            c.setStatus(1);
            configureService.addConfigure(c);
        }

        String optId = "";
        String optName = "";
        String optRole = "";
        UserInfo userInfo = userService.getUserInfo(conf.getUserid());
        if (userInfo != null) {
            optId = userInfo.getEmail();
            optName = userInfo.getUsername();
            optRole = "0";
        }
        OperateLog log = new OperateLog();
        String ipAddr = request.getHeader("X-Forwarded-For");
        log.setUserid(conf.getUserid());
        log.setOptId(optId);
        log.setOptName(optName);
        log.setOptRole(optRole);
        log.setIpAddr(ipAddr);
        log.setObjId("");
        log.setoTime(new Date());
        log.setOptEvent("增加");
        log.setObjName("修改设置:" + conf.getName());
        log.setOptClient("0");
        log.setOptModule("2");
        log.setOptDesc("成功,value: " + conf.getValue());
        operateLogService.addLog(log);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/createwxaqrcode 批量获取获取小程序二维码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[{" +
                    "\"userid\":\"userid\",\n" +
                    "\"gid\":\"gid\",\n" +
                    "}]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/createwxaqrcode")
    @ResponseBody
    public RespInfo createwxaqrcode(@RequestBody List<Gate> glist, HttpServletRequest request) {

        for (Gate gate : glist) {
            StringBuilder scene = new StringBuilder();
            scene.append("userid=" + gate.getUserid());
            scene.append("&");
            scene.append("gid=" + gate.getGid());
            String buffe = configureService.createwxaqrcode(scene.toString());
            gate.setQrcode(buffe);
        }
        return new RespInfo(0, "success", glist);
    }


    @ApiOperation(value = "/Register 新加用户", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/Register", consumes = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public RespInfo register(@RequestBody ReqUserInfo reqinfo, HttpServletRequest request) {
        try {
            String email = reqinfo.getEmail();
            String company = reqinfo.getCompany();
            if (email == null || email.length() == 0 || !UtilTools.validateEmail(email)) {
                return new RespInfo(1, "invalid user");
            }

            UserInfo userinfo = userService.selectByName(email);

            if (userinfo != null) {
                return new RespInfo(3, "user already exist");
            }

            if (reqinfo.getPassword() == null || reqinfo.getPassword().length() == 0) {
                return new RespInfo(2, "invalid password");
            }

            UserInfo user = userService.selectBycompany(company);

            if (null != user) {
                return new RespInfo(46, "company name already exist");
            }


            userinfo = new UserInfo();

            String defaultPhoto = Constant.FASTDFS_URL + "group1/M00/00/3D/Cv7fv1aUay2AMMBSAAJxxMJj124287.png,"
                    + Constant.FASTDFS_URL + "/group1/M00/00/3D/Cv7fv1aUa7yADG32AAI_PZSKDIk170.png,"
                    + Constant.FASTDFS_URL + "group1/M00/00/3D/Cv7fv1aUbBSAGqZXAAOuhcLLS5M998.png,"
                    + Constant.FASTDFS_URL + "group1/M00/00/3D/Cv7fv1aUbG2AUFEvAAN5192X5Fs058.png";

            userinfo.setUsername(reqinfo.getName());
            userinfo.setPassword(MD5.crypt(reqinfo.getPassword()));
            //userinfo.setPassword(PrivateSecurityRealm.fromPlainPassword(reqinfo.getPassword()));
            userinfo.setPhone(reqinfo.getPhone());
            userinfo.setEmail(reqinfo.getEmail());
            userinfo.setCompany(reqinfo.getCompany());
            userinfo.setUserType(3);
            userinfo.setMsgNotify(0);
            userinfo.setEmailType(0);
            userinfo.setRegDate(new Date());
            userinfo.setDefaultPhoto(defaultPhoto);
            userinfo.setMsgNotify(1);
            Calendar calendar = Calendar.getInstance();
            calendar.set(2315, 10, 12);  //年月日  也可以具体到时分秒如calendar.set(2015, 10, 12,11,32,52);
            userinfo.setExpireDate(calendar.getTime());

            Random random = new Random();
            int randNum = random.nextInt(Integer.MAX_VALUE - 10000000 + 1) + 10000000;
            userinfo.setUserid(randNum);

            int a = userService.register(userinfo);
            if (a > 0) {
                for (int i = 0; i < 4; i++) {
                    ExtendVisitor ev = new ExtendVisitor();
                    if (i == 0) {
                        ev.setDisplayName("您的姓名");
                        ev.setFieldName("name");
                        ev.setInputType("text");
                        ev.setInputValue("");
                        ev.setInputOrder(1);
                        ev.setRequired(1);
                        ev.setUserid(userinfo.getUserid());
                        ev.setPlaceholder("");
                    }
                    if (i == 1) {
                        ev.setDisplayName("拜访事由");
                        ev.setFieldName("visitType");
                        ev.setInputType("button");
                        ev.setInputValue("面试,商务,私人,其他");
                        ev.setInputOrder(2);
                        ev.setRequired(1);
                        ev.setUserid(userinfo.getUserid());
                        ev.setPlaceholder("");
                    }
                    if (i == 2) {
                        ev.setDisplayName("您要拜访的人");
                        ev.setFieldName("empid");
                        ev.setInputType("text");
                        ev.setInputValue("");
                        ev.setInputOrder(3);
                        ev.setRequired(1);
                        ev.setUserid(userinfo.getUserid());
                        ev.setPlaceholder("");
                    }
                    if (i == 3) {
                        ev.setDisplayName("电话");
                        ev.setFieldName("phone");
                        ev.setInputType("text");
                        ev.setInputValue("");
                        ev.setInputOrder(4);
                        ev.setRequired(1);
                        ev.setUserid(userinfo.getUserid());
                        ev.setPlaceholder("");
                    }

                    extendVisitorService.addExtendVisitor(ev);
                }


                UserInfo ui = new UserInfo();
                ui.setUserid(userinfo.getUserid());
                userService.addUserinfoExtend(ui);

                DataStatistics ds = new DataStatistics();
                ds.setUserid(userinfo.getUserid());
                dataStatisticsService.addDataStatistics(ds);
            }

            String userToken = CommonUtils.generateSessionId(16);
            Date date = new Date();
            Date exprieDate = new Date(date.getTime() + EXPIRE_TIME);

            userinfo.setPassword("");
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(userinfo.getUserid() + "-" + userToken, "id", exprieDate.getTime());
            redisTemplate.expire(userinfo.getUserid() + "-" + userToken, 24, TimeUnit.HOURS);
            userinfo.setToken(userToken);

            return new RespInfo(0, "success", userinfo);

        } catch (Exception e) {
            e.printStackTrace();
            return new RespInfo(3, "user already exist");
        }
    }

    @ApiOperation(value = "/uploadAppleCarouselPic 更新小程序轮播图", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )

    @RequestMapping(method = RequestMethod.POST, value = "/uploadAppleCarouselPic")
    @ResponseBody
    public RespInfo uploadAppleCarouselPic(@RequestBody ReqUserInfo reqinfo,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != reqinfo.getUserid()){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = reqinfo.getUserid();
        String bgurl = reqinfo.getAppletCarouselPicUrl();
        UserInfo ui = new UserInfo();
        ui.setUserid(userid);
        ui.setAppletCarouselPic(bgurl);
        userService.updateAppletCarouselPicUrl(ui);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getAppletPics 获取小程序轮播图", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "userid:" + "11111" +
                    "}"
    )
    @PostMapping("/getAppletPics")
    @ResponseBody
    public RespInfo getAppletPicsByUserid(@RequestBody ReqUserInfo reqUserInfo,HttpServletRequest request) {
        int userid = reqUserInfo.getUserid();
        List<String> pics = this.userService.getAppletPicsByUserid(userid);
        return new RespInfo(0, "success", pics);
    }


    @ApiOperation(value = "/ModifyPassword 重置管理员密码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "email:" + "1123@qq.com" +
                    "digest:" + "XXXXXXX" +
                    "new_pwd:" + "XXXXXXX" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/ModifyPassword")
    @ResponseBody
    public RespInfo ModifyPassword(@RequestBody ReqUserInfo reqinfo, HttpServletRequest request) {
        String email = reqinfo.getEmail();
        String password = MD5.crypt(reqinfo.getNew_pwd());
        String digest = reqinfo.getDigest();
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        
        if(hashOperations.hasKey(email+"_digest", "digest")&&null!=hashOperations.get(email+"_digest", "digest")) {
        	 String rdigest = (String) hashOperations.get(email+"_digest", "digest");
        	 if (!digest.equals(rdigest)){
        		 return new RespInfo(39, "invalid link");
        	 }
        }else{
            return new RespInfo(39, "invalid link");
        }
       
        Manager manager = mgrService.getManagerByAccount(email);
        if (null != manager){
            manager.setPassword(password);
            mgrService.updateServerManagerPwd(manager);
            hashOperations.delete(email+"_digest", "digest");
            //删除加锁账号
            if (hashOperations.hasKey("fail_login" + email, "locktime")){
                hashOperations.delete("fail_login" + email, "locktime");
                hashOperations.delete("fail_login" + email, "failcount");
            }

            //添加操作日志
            if (org.apache.commons.lang.StringUtils.isNotEmpty(manager.getLoginAccount())) {
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
        }else {
            UserInfo userinfo = userService.selectByName(email);
            if(null!=userinfo) {
                UserInfo ui = new UserInfo();
                ui.setEmail(email);
                ui.setPassword(password);
                ui.setDigest("");
                userService.updatePwd(ui);
                hashOperations.delete(email + "_digest", "digest");
                //删除加锁账号
                if (hashOperations.hasKey("fail_login" + email, "locktime")){
                    hashOperations.delete("fail_login" + email, "locktime");
                    hashOperations.delete("fail_login" + email, "failcount");
                }

                //添加操作日志
                String optName = "";
                String optId = "";
                String optRole = "";
                optId = userinfo.getEmail();
                optName = userinfo.getUsername();
                optRole = "0";
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(ui.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);//角色 操作角色  0管理员 1hse 2员工 3前台
                log.setIpAddr(ipAddr);
                log.setObjId(email);
                log.setObjName(email);
                log.setoTime(new Date());
                log.setOptEvent("修改密码");//事件，修改、增加、删除、登录
                log.setOptClient("0");//操作端 0 PC  1 移动
                log.setOptModule("2");//操作模块 0登录 1员工 2账号
                log.setOptDesc("成功,修改密码: " + optId);
                operateLogService.addLog(log);

                return new RespInfo(0, "success");
            }else{
            	 return new RespInfo(1, "invalid user");
            }
        }


    }

    @ApiOperation(value = "/RetrievePassword 发送重置管理员密码邮件", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "email:" + "1123@qq.com" +
                    "digest:" + "XXXXXXX" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/RetrievePassword")
    @ResponseBody
    public RespInfo RetrievePassword(@RequestBody ReqUserInfo reqinfo) {
        String account = reqinfo.getEmail();

        String vcode = AESUtil.decode(reqinfo.getDigest(), Constant.AES_KEY);
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(account + "_vcode", "ValidationCode")) {
            String pcode = (String) hashOperations.get(account + "_vcode", "ValidationCode");
            if (!pcode.equals(vcode)) {
                return new RespInfo(119, "validationCode failed");
            }
            hashOperations.delete(account + "_vcode", "ValidationCode");
        } else {
            return new RespInfo(119, "validationCode failed");
        }
        
        String digest=MD5.crypt(account+new Date());
        String response="-1";
        /**
         * 劳务公司账号查询
         */
        Manager manager = mgrService.getManagerByAccount(account);
        if (null != manager){
            UserInfo userinfo = userService.getUserInfo(manager.getUserid());
            if(userinfo == null){
                return new RespInfo(1, "invalid user");
            }
            if(StringUtils.isNotBlank(manager.getMoblie())) {
            	 //发送短信
	            Map<String, String> params = new HashMap<String, String>();
	            params.put("message","您好!请点击链接进行密码重置：" + Constant.FASTDFS_URL + "reset?email=" + account + "&digest=" + digest);
	            params.put("phone", manager.getMoblie());
	            response = visitorService.sendSmsReply(params, userinfo,2);
	
	            OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,manager.getMoblie(),manager.getSname(),params.get("message"));
            }

        }else {
            UserInfo userinfo = userService.selectByName(account);
            if(userinfo == null) {
                return new RespInfo(1, "invalid user");
            }

            if(StringUtils.isNotBlank(userinfo.getPhone())) {
            	 //发送短信
                Map<String, String> params = new HashMap<String, String>();
                params.put("message","您好!请点击链接进行密码重置：" + Constant.FASTDFS_URL + "reset?email=" + account + "&digest=" + digest);
                params.put("phone", userinfo.getPhone());
	            response = visitorService.sendSmsReply(params, userinfo,2);
	
	            OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,userinfo.getPhone(),userinfo.getUsername(),params.get("message"));
            }
        
        }
        
        SendTextEmail ste=new SendTextEmail();
        if(ste.send(account, digest)||"0".equals(response)){
        	 hashOperations.put(account+"_digest","digest",digest);
             redisTemplate.expire(account+"_digest", 20, TimeUnit.MINUTES);
        }else{
            return new RespInfo(40, "failed to send");
        }

        return new RespInfo(0, "success");
    }
    
    @ApiOperation(value = "/updateCarouselPic 设置轮播图", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\"userid\":2147483647,\n" +
                    "\"carouselPic\":\"xxx,xxx,xxx,xxx,xxx\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateCarouselPic")
    @ResponseBody
    public RespInfo updateCarouselPic(@RequestBody UserInfo userinfo) {
        userService.updateCarouselPic(userinfo);
        return new RespInfo(0, "success");
    }

}
