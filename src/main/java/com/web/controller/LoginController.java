package com.web.controller;


import com.client.bean.Gate;
import com.client.bean.RequestVisit;
import com.client.bean.ValCode;
import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiUserGetbyunionidRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiUserGetbyunionidResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.event.event.NotifyEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;
import com.utils.*;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.msgUtils.MsgTemplateUtils;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.*;
import org.apache.commons.lang3.ObjectUtils;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "LoginController", tags = "API_登录管理", hidden = true)
public class LoginController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ConfigureService configureService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private ManagerService mgrService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ResidentVisitorService residentVisitorService;

    @Autowired
    private TokenManageService tokenManageService;

    @Autowired
    private MsgTemplateService msgTemplateService;



    @ApiOperation(value = "/getValidationCode 获取图形验证码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8",
            produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/getValidationCode")
    @ResponseBody
    public RespInfo getValidationCode() {
        String base64Str = "";
        try {
            base64Str = ValidationCode.getValidationCode();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new RespInfo(111, "validationCode produce failed");
        }

        String vcode = ValidationCode.getValidationCodeStr();
        System.out.println(vcode);
        vcode = AESUtil.encode(vcode, Constant.AES_KEY);
        ValCode vc = new ValCode();
        vc.setBase64Str(base64Str);
        vc.setDigest(vcode);

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(vcode + "_vdigest", "ValidationDigest", vcode);
        redisTemplate.expire(vcode + "_vdigest", 5, TimeUnit.MINUTES);

        return new RespInfo(0, "success", vc);
    }

    @ApiOperation(value = "/validationCode 验证图像验证码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"email\":\"接收者邮箱\",\n" +
                    "    \"phone\":\"phone\",\n" +
                    "    \"digest\":\"3AD00AA03E8D431221587191D671947A\",\n" +
                    "    \"vcode\":\"3sEA\"\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/validationCode", consumes = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public RespInfo validationCode(@RequestBody @Validated ValCode vc, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        String vcode = vc.getVcode();
        String code = AESUtil.decode(vc.getDigest(), Constant.AES_KEY);
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (null != vc.getPhone() && !"".equals(vc.getPhone())) {
            if (vcode.equalsIgnoreCase(code)) {
                if (!hashOperations.hasKey(vc.getDigest() + "_vdigest", "ValidationDigest")) {
                    return new RespInfo(119, "validationCode failed");
                }
                hashOperations.put(vc.getPhone() + "_vcode", "ValidationCode", code);
                hashOperations.delete(vc.getDigest() + "_vdigest", "ValidationDigest");
                redisTemplate.expire(vc.getPhone() + "_vcode", 5, TimeUnit.MINUTES);
                return new RespInfo(0, "success");
            } else {
                return new RespInfo(119, "validationCode failed");
            }
        } else {
            if (vcode.equalsIgnoreCase(code)) {
                if (!hashOperations.hasKey(vc.getDigest() + "_vdigest", "ValidationDigest")) {
                    return new RespInfo(119, "validationCode failed");
                }
                hashOperations.put(vc.getEmail() + "_vcode", "ValidationCode", code);
                hashOperations.delete(vc.getDigest() + "_vdigest", "ValidationDigest");
                redisTemplate.expire(vc.getEmail() + "_vcode", 1, TimeUnit.MINUTES);
                return new RespInfo(0, "success");
            } else {
                return new RespInfo(119, "validationCode failed");
            }
        }

    }

    @ApiOperation(value = "/checkSmsCode 验证短信验证码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":\"15251805548\",\n" +
                    "    \"verifyCode\":\"verifyCode\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/checkSmsCode")
    @ResponseBody
    public RespInfo checkSmsCode(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        String phone = req.getPhone();
        String smscode = req.getVerifyCode();
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(phone + "-_-" + smscode, "smscode")) {
            Long expireDate = (long) hashOperations.get(phone + "-_-" + smscode, "smscode");
            Date currentDate = new Date();
            if (expireDate < currentDate.getTime()) {
                hashOperations.delete(phone + "-_-" + smscode, "smscode");
                return new RespInfo(144, "smscode expired");
            }
        } else {
            return new RespInfo(145, "error smscode");
        }

        hashOperations.delete(phone + "-_-" + smscode, "smscode");
        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/sendPicSmsCode 发送图形短信验证码", httpMethod = "POST",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":\"15251805548\",\n" +
                    "    \"digest\":\"012FC1A2333ED7616A6BF6DF72501C84\",\n" +
                    "}" +
                    "获取图形验证码接口：getValidationCode" +
                    "验证短信验证码接口：checkSmsCode"

    )
    @RequestMapping(method = RequestMethod.POST, value = "/sendPicSmsCode")
    @ResponseBody
    public RespInfo sendPicSmsCode(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        String phone = req.getPhone();
        String vcode = AESUtil.decode(req.getDigest(), Constant.AES_KEY);
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(phone + "_vcode", "ValidationCode")) {
            String pcode = (String) hashOperations.get(phone + "_vcode", "ValidationCode");
            if (pcode.equals(vcode)) {
                String code = UtilTools.produceId();
                com.web.bean.MsgTemplate msg = msgTemplateService.getTemplate(0, "sms_"+ NotifyEvent.EVENTTYPE_CODE);
                if (msg == null || 0 == msg.getStatus()){
                    SysLog.warn("未设置模板："+"sms_"+NotifyEvent.EVENTTYPE_CODE);
                    return new RespInfo(119, "validationCode failed");

                }
                Map<String, String> params = new HashMap<>();
                params.put("code",code);
                String content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), params);

                Map<String, String> map = new HashMap<String, String>();
                map.put("phone", phone);
                map.put("message", content);

                UserInfo ui = userService.getUserInfoWithExt(Constant.ACCOUNT);
                if(ui == null){
                    ReqUserInfo reqinfo = new ReqUserInfo();
                    List<UserInfo> userInfoList = userService.getAllUserinfo(reqinfo);
                    if(userInfoList.size()>0){
                        ui = userInfoList.get(0);
                    }
                }

                if(ui != null) {
                    String response = visitorService.sendSmsCodeByYiMei(map, ui);
                    if ("0".equals(response)) {
                        Date date = new Date();
                        hashOperations.put(phone + "-_-" + code, "smscode", date.getTime() + 600000L);
                        hashOperations.delete(phone + "_vcode", "ValidationCode");
                        redisTemplate.expire(phone + "-_-" + code, 5, TimeUnit.MINUTES);
                    }
                    
                    return new RespInfo(0, "success", response);
                }

                return new RespInfo(ErrorEnum.E_005.getCode(), ErrorEnum.E_005.getMsg());

            } else {
                return new RespInfo(119, "validationCode failed");
            }
        } else {
            return new RespInfo(119, "validationCode failed");
        }

    }


    @ApiOperation(value = "/sendSmsCode 发送短信验证码", httpMethod = "POST",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":\"15251805548\",\n" +
                    "}" +
                    "验证短信验证码接口：checkSmsCode"

    )
    @RequestMapping(method = RequestMethod.POST, value = "/sendSmsCode")
    @ResponseBody
    public RespInfo sendSmsCode(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @RequestBody @Validated RequestVisit req,
            BindingResult result,HttpServletRequest request) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        String phone = req.getPhone();
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String code = UtilTools.produceId();
        com.web.bean.MsgTemplate msg = msgTemplateService.getTemplate(0, "sms_"+ NotifyEvent.EVENTTYPE_CODE);
        if (msg == null || 0 == msg.getStatus()){
            SysLog.warn("未设置模板："+"sms_"+NotifyEvent.EVENTTYPE_CODE);
            return new RespInfo(119, "validationCode failed");

        }
        Map<String, String> params = new HashMap<>();
        params.put("code",code);
        String content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), params);

        Map<String, String> map = new HashMap<String, String>();
        map.put("phone", phone);
        map.put("message", content);

        UserInfo ui = userService.getUserInfoWithExt(Constant.ACCOUNT);
        if(ui == null){
            ReqUserInfo reqinfo = new ReqUserInfo();
            List<UserInfo> userInfoList = userService.getAllUserinfo(reqinfo);
            if(userInfoList.size()>0){
                ui = userInfoList.get(0);
            }
        }

        if(ui != null) {
            String response = visitorService.sendSmsCodeByYiMei(map, ui);
            if ("0".equals(response)) {
                Date date = new Date();
                hashOperations.put(phone + "-_-" + code, "smscode", date.getTime() + 600000L);
                hashOperations.delete(phone + "_vcode", "ValidationCode");
                redisTemplate.expire(phone + "-_-" + code, 5, TimeUnit.MINUTES);
            }

            return new RespInfo(0, "success", response);
        }

        return new RespInfo(ErrorEnum.E_005.getCode(), ErrorEnum.E_005.getMsg());

    }


    @ApiOperation(value = "/Login 用户登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"email\":\"用户账号\",\n" +
                    "    \"password\":\"密码\",\n" +
                    "    \"digest\":\"3AD00AA03E8D431221587191D671947A\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/Login", consumes = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public RespInfo login(
            @ApiParam(value = "请求_用户详情", required = true) @RequestBody @Validated ReqUserInfo req,
            BindingResult result, HttpServletRequest request) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
//        request.getRemoteAddr()
                System.out.println("ip1 "+request.getRemoteAddr());
                System.out.println("ip2 "+request.getHeader("X-Forwarded-For"));
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.indexOf("iPad") != -1 || userAgent.indexOf("iPhone") != -1 || userAgent.indexOf("android") != -1 || userAgent.indexOf("Android") != -1) {
        } else {
            String vcode = AESUtil.decode(req.getDigest(), Constant.AES_KEY);
            if (hashOperations.hasKey(req.getEmail() + "_vcode", "ValidationCode")) {
                String pcode = (String) hashOperations.get(req.getEmail() + "_vcode", "ValidationCode");
                if (!pcode.equals(vcode)) {
                    return new RespInfo(119, "validationCode failed");
                }
                hashOperations.delete(req.getEmail() + "_vcode", "ValidationCode");
            } else {
                return new RespInfo(119, "validationCode failed");
            }
        }

        if (hashOperations.hasKey("fail_login" + req.getEmail(), "locktime")) {
            Long locktime = (long) hashOperations.get("fail_login" + req.getEmail(), "locktime");
            if (System.currentTimeMillis() - locktime > Constant.LOCK_TIME) {
                hashOperations.delete("fail_login" + req.getEmail(), "locktime");
                hashOperations.delete("fail_login" + req.getEmail(), "failcount");
            } else {
                SysLog.warn("fail_login " + req.getEmail() + "失败次数超过8次已锁定");
                return new RespInfo(ErrorEnum.E_301.getCode(), ErrorEnum.E_301.getMsg());
            }
        }

        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);

        UserInfo userinfo = userService.getUserInfoWithExt(req.getEmail());
        if (userinfo == null) {
            //非管理员账号
            Manager mgr = managerService.getManagerPwdByAccount(req.getEmail());
            if (mgr == null) {
                return new RespInfo(ErrorEnum.E_075.getCode(), ErrorEnum.E_075.getMsg());
            }

            Date date = new Date();
            if (null == mgr) {
                return new RespInfo(ErrorEnum.E_075.getCode(), ErrorEnum.E_075.getMsg());
            } else if (mgr.getEndDate().getTime() < date.getTime() || mgr.getStartDate().getTime() > date.getTime()) {
                return new RespInfo(42, "exceed the time limit");
            }

            //校验密码
            RespInfo respInfo = checkPassWord(req, hashOperations, mgr.getPassword());
            if (respInfo != null){
                addLoginFailedLog(request, mgr.getUserid(), req.getEmail(), req.getEmail(), "0","失败 错误码："+respInfo.getStatus());
                return respInfo;
            }


                Map<String, Object> maps = new HashMap<String, Object>();
                maps.put("userid", mgr.getUserid());
                UserInfo ui = userService.getUserInfoByUserId(mgr.getUserid());
                maps.put("pemail", ui.getEmail());
                maps.put("account", mgr.getAccount());

                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME_MILLI);

                /**
                 * 生成新token
                 */
                AuthToken authToken = new AuthToken(mgr.getAccount(), String.valueOf(mgr.getsType()), mgr.getUserid(), null, System.currentTimeMillis());
                String encoderToken = tokenServer.getAESEncoderTokenString(authToken);

                if (mgr.getsType() == 0 || mgr.getsType() == 4 || mgr.getsType() == 5 || mgr.getsType() == 6) {

                    /**
                     * hse登录
                     */
                    if (mgr.getsType() == 0) {
                        hashOperations.put(encoderToken, "id", exprieDate.getTime());
                        redisTemplate.expire(encoderToken, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);
                        maps.put("sName", mgr.getSname());
                        maps.put("sType", mgr.getsType());
                        maps.put("company", mgr.getCompany());
                        maps.put("token", encoderToken);
                        maps.put("gid", mgr.getGid());

                    addLoginLog(request, mgr.getUserid(), mgr.getAccount(), mgr.getSname(), OperateLog.ROLE_HSE);
                        return new RespInfo(0, "success", maps);
                    }

                    /**
                     * 访客机账号登录
                     */
                    if (mgr.getsType() == 4) {
                        hashOperations.put(encoderToken, "id", exprieDate.getTime());
                        redisTemplate.expire(encoderToken, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);
                        ui.setToken(encoderToken);
                        ui.setTokenExpire(new Long(12 * 60 * 60 * 1000L).intValue() / 1000);
                        ui.setGid(Integer.parseInt(mgr.getGid()));
                        ui.setsName(mgr.getSname());
                        ui.setsType(mgr.getsType());
                        if (ui.getGid() > 0) {
                            Gate gate = new Gate();
                            gate.setGids(String.valueOf(mgr.getGid()));
                            gate.setUserid(ui.getUserid());
                            List<Gate> glist = addressService.getGateById(gate);

                            if (glist.size() > 0) {
                                ui.setGname(glist.get(0).getGname());
                            }
                        }
                        ui.setLoginDate(new Date());

                        addLoginLog(request, mgr.getUserid(), mgr.getAccount(), mgr.getSname(), OperateLog.ROLE_MACHINE);

                        return new RespInfo(0, "success", ui);

                    }

                    /**
                     * 子账号登录
                     */
                    if (mgr.getsType() == 5) {
                        hashOperations.put(encoderToken, "id", exprieDate.getTime());
                        redisTemplate.expire(encoderToken, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);

                        maps.put("sName", mgr.getSname());
                        maps.put("sType", mgr.getsType());
                        maps.put("company", mgr.getCompany());
                        maps.put("token", encoderToken);
                        maps.put("gid", mgr.getGid());

                        // TODO: 2020/4/23 changmeidong 封装权限菜单
                        List<Permission> permissionList = permissionService.getPermissionByaccount(mgr.getAccount());
                        if (null != permissionList && permissionList.size() > 0) {
                            List<Permission> permissionTree = permissionService.getPermissionTree(permissionList);
                            maps.put("module", permissionTree);
                        }

                    addLoginLog(request, mgr.getUserid(), mgr.getAccount(), mgr.getSname(), OperateLog.ROLE_SUBADMIN);
                        return new RespInfo(0, "success", maps);
                    }
                    if (mgr.getsType() == 6) {
                    return new RespInfo(ErrorEnum.E_075.getCode(), ErrorEnum.E_075.getMsg());
                    }


                } else {
                    hashOperations.put(encoderToken, "id", exprieDate.getTime());
                    redisTemplate.expire(encoderToken, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);
                    if (StringUtils.isNotBlank(mgr.getGid())) {
                        Gate gate = new Gate();
                        gate.setGids(mgr.getGid());
                        gate.setUserid(ui.getUserid());
                        List<Gate> glist = addressService.getGateById(gate);

                        if (glist.size() > 0) {
                            maps.put("gname", glist.get(0).getGname());
                        }
                    }

                    maps.put("sName", mgr.getSname());
                    maps.put("sType", mgr.getsType());
                    maps.put("company", mgr.getCompany());
                    maps.put("token", encoderToken);
                    maps.put("gid", mgr.getGid());

                    String stype = "";
                    if(mgr.getsType() == 1){
                        stype = OperateLog.ROLE_SUPPLIER_COMPANY;
                    }else if (mgr.getsType() == 3){
                        //物管
                        stype = OperateLog.ROLE_LOGISTICS_MANAGER;
                    }
                addLoginLog(request, mgr.getUserid(), mgr.getAccount(), mgr.getSname(), stype);
                    return new RespInfo(0, "success", maps);
                }


        }

        //管理员账号
        if (userinfo.getUserType() != 0) {
            long expirdate = userinfo.getExpireDate().getTime() + Constant.EXPIRE_TIME_MILLI;
            Date date = new Date();
            if (expirdate < date.getTime()) {
                return new RespInfo(42, "exceed the time limit", userinfo.getUserid());
            }
        }

        RespInfo respInfo = checkPassWord(req, hashOperations, userinfo.getPassword());
        if (respInfo != null){
            addLoginFailedLog(request, userinfo.getUserid(), req.getEmail(), req.getEmail(), "0","失败 错误码："+respInfo.getStatus());
            return respInfo;
        }

        /**
         * 生成超级管理员token
         */
        AuthToken authToken = new AuthToken(userinfo.getEmail(), AuthToken.ROLE_ADMIN, userinfo.getUserid(), null, System.currentTimeMillis());
        String userToken = "";
        try {
            userToken = mapperInstance.writeValueAsString(authToken);
            userToken = AESUtil.encode(userToken, Constant.AES_KEY);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(userinfo.getUserid()));

        if (userinfo.getUserType() == 0) {
            if (userAgent.indexOf("iPad") != -1 || userAgent.indexOf("android") != -1) {
                return new RespInfo(1, "invalid user");
            }
        }
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("userid", userinfo.getUserid());
        m.put("username", userinfo.getUsername());

        userinfo.setLoginDate(new Date());
        userinfo.setTokenExpire(new Long(Constant.EXPIRE_TIME).intValue() / 1000);
        userService.updateLoginDate(userinfo);
        Configures conf = configureService.getConfigure(userinfo.getUserid(), Constant.SMSCOUNT);
        userinfo.setSmsTotal(Integer.parseInt(conf.getValue()));
        userinfo.setPassword("");
        hashOperations.put(userToken, "id", exprieDate.getTime());
        redisTemplate.expire(userToken, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);
        userinfo.setToken(userToken);


        addLoginLog(request, userinfo.getUserid(), userinfo.getEmail(), userinfo.getUsername(), "0");
        userinfo.setsType(-1);
        userinfo.setSecurityKey("");
        return new RespInfo(0, "success", userinfo);
    }

    /**
     * 检查密码正确性，多次错误锁定账号
     * @param req 请求信息
     * @param hashOperations
     * @param userPwd 正确的密码
     * @return
     */
    private RespInfo checkPassWord(@Validated @RequestBody @ApiParam(value = "请求_用户详情", required = true) ReqUserInfo req, HashOperations<String, String, Object> hashOperations, String userPwd) {
        //密码解码
        try {
            String base64 = req.getPassword();
            String front = base64.substring(0, 5);
            base64 = front + base64.substring(10);
            String pwd = Encodes.decodeBase64String(base64);
            pwd = new StringBuilder(pwd).reverse().toString();
            req.setPassword(pwd.substring(0, 3) + pwd.substring(8));
        } catch (Exception e) {
            return new RespInfo(999, "encrypt pwd");
        }

        if (userPwd.compareTo(MD5.crypt(req.getPassword())) != 0) {
            int failcount = 0;
            if (hashOperations.hasKey("fail_login" + req.getEmail(), "failcount")) {
                failcount = (int) hashOperations.get("fail_login" + req.getEmail(), "failcount");
            }
            hashOperations.put("fail_login" + req.getEmail(), "failcount", ++failcount);

            //第一次时开始计时，10分钟后计数清0
            if (failcount == 1) {
                redisTemplate.expire("fail_login" + req.getEmail(), 24, TimeUnit.HOURS);
            }

            if (failcount >= 8) {
                hashOperations.put("fail_login" + req.getEmail(), "locktime", System.currentTimeMillis());
                SysLog.warn("fail_login "+req.getEmail()+"登录失败次数超过8次锁定24小时");
                return new RespInfo(301, "密码错误，账号被锁定24小时");
            }

            if (failcount >= 6) {
                return new RespInfo(302, "密码错误，再错误"+(8-failcount)+"次后账号将被锁定");
            }


            return new RespInfo(ErrorEnum.E_075.getCode(), ErrorEnum.E_075.getMsg());
        }

        if (hashOperations.hasKey("fail_login" + req.getEmail(), "failcount")) {
            hashOperations.delete("fail_login" + req.getEmail(), "failcount");
        }
        return null;
    }

    private void addLoginLog(HttpServletRequest request, int userid, String account, String sname, String s) {
        OperateLog ol = new OperateLog();
        ol.setUserid(userid);
        ol.setOptId(account);
        ol.setOptName(sname);
        ol.setObjName("");
        ol.setOptClient("0");
        ol.setOptModule("0");
        ol.setOptRole(s);
        ol.setOptEvent("登录");
        ol.setOptDesc("成功");
        String ipAddr = request.getHeader("X-Forwarded-For");
        ol.setIpAddr(ipAddr);
        operateLogService.addLog(ol);
    }

    private void addLoginFailedLog(HttpServletRequest request, int userid, String account, String sname, String s,String status) {
        OperateLog ol = new OperateLog();
        ol.setUserid(userid);
        ol.setOptId(account);
        ol.setOptName(sname);
        ol.setObjName("");
        ol.setOptClient("0");
        ol.setOptModule("0");
        ol.setOptRole(s);
        ol.setOptEvent("登录");
        ol.setOptDesc(status);
        String ipAddr = request.getHeader("X-Forwarded-For");
        ol.setIpAddr(ipAddr);
        operateLogService.addLog(ol);
    }

    @ApiOperation(value = "/empLogin 员工登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":\"15251800000\",\n" +
                    "    \"empPwd\":\"MzIxdsmPnpGlzaXZsQ0pqa1Jvb2M=\",\n" +
                    "    \"digest\":\"3AD00AA03E8D431221587191D671947A\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLogin")
    @ResponseBody
    public RespInfo empLogin(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @RequestBody @Validated RequestEmp reqemp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.indexOf("iPad") != -1 || userAgent.indexOf("iPhone") != -1 || userAgent.indexOf("android") != -1 || userAgent.indexOf("Android") != -1) {
        } else {
            String vcode = AESUtil.decode(reqemp.getDigest(), Constant.AES_KEY);
            if (hashOperations.hasKey(reqemp.getPhone() + "_vcode", "ValidationCode")) {
                String pcode = (String) hashOperations.get(reqemp.getPhone() + "_vcode", "ValidationCode");
                if (!pcode.equals(vcode)) {
                    return new RespInfo(119, "validationCode failed");
                }
                hashOperations.delete(reqemp.getPhone() + "_vcode", "ValidationCode");
            } else {
                return new RespInfo(119, "validationCode failed");
            }
        }

        //校验登陆账号
        if (hashOperations.hasKey("fail_login" + reqemp.getPhone(), "locktime")) {
            Long locktime = (long) hashOperations.get("fail_login" + reqemp.getPhone(), "locktime");
            if (System.currentTimeMillis() - locktime > Constant.LOCK_TIME) {
                hashOperations.delete("fail_login" + reqemp.getPhone(), "locktime");
                hashOperations.delete("fail_login" + reqemp.getPhone(), "failcount");
            } else {
                SysLog.warn("fail_login " + reqemp.getPhone() + "失败次数超过8次已锁定");
                return new RespInfo(ErrorEnum.E_301.getCode(), ErrorEnum.E_301.getMsg());
            }
        }

        List<Employee> emplist = new ArrayList<>();
        UserInfo userInfo = null;
        int userid = 0;
        //当有userid时，直接查员工列表
        if (reqemp.getUserid() > 0) {
            userInfo = userService.getUserInfoByUserId(reqemp.getUserid());
            emplist = employeeService.getEmpInfo(reqemp.getUserid(), reqemp.getPhone());
        } else {
            //当没有userid时，调用checkEmpInfo逻辑，判断是否是多公司；是返回公司列表
            emplist = employeeService.getEmployeePassword(reqemp.getPhone());
            if (emplist.size() == 0) {
                return new RespInfo(1, "invalid user");
            }
            if (emplist.size() > 1) {
                List<Integer> list = new ArrayList<Integer>();
                for (int i = 0; i < emplist.size(); i++) {
                    if (!list.contains(emplist.get(i).getUserid())) {
                        list.add(emplist.get(i).getUserid());
                    }
                }
                List<UserInfo> userlist = userService.getUsers(list);
                return new RespInfo(668, "company not unique", userlist);
            }
            userInfo = userService.getUserInfoByUserId(emplist.get(0).getUserid());
        }


        if (emplist.size() > 0) {
            Person p = personInfoService.getVisitPersonByPhone(reqemp.getPhone());
            if (null == p || null == p.getPassword()) {
                return new RespInfo(135, "please activate account");
            }
            if (1 == userInfo.getSubAccount()) {
                if(emplist.get(0).getSubaccountId()==0){
                    //主账号员工

                }else {
                    SubAccount accountById = subAccountService.getSubAccountById(emplist.get(0).getSubaccountId());
                    if (accountById == null || accountById.getIsUse() == 0) {
                        return new RespInfo(ErrorEnum.E_094.getCode(), ErrorEnum.E_094.getMsg());
                    }
                }
            }

            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
            int d1 = Integer.parseInt(emplist.get(0).getStartDate());
            int d2 = Integer.parseInt(emplist.get(0).getEndDate());
            int today = Integer.parseInt(sd.format(new Date()));
            if (today < d1 || today > d2) {
                return new RespInfo(1113, "employee invalid date");
            }

            ReqUserInfo req = new ReqUserInfo();
            req.setPassword(reqemp.getEmpPwd());
            req.setEmail(reqemp.getPhone());
            //校验密码
            RespInfo respInfo = checkPassWord(req, hashOperations, p.getPassword());
            if (respInfo != null){
                addLoginFailedLog(request, userInfo.getUserid(), req.getEmail(), req.getEmail(), "0","失败 错误码："+respInfo.getStatus());
                return respInfo;
            }

            String base64 = reqemp.getEmpPwd();
            String front = base64.substring(0, 5);
            base64 = front + base64.substring(10);
            String pwd = Encodes.decodeBase64String(base64);
            pwd = new StringBuilder(pwd).reverse().toString();
            reqemp.setEmpPwd(pwd.substring(0, 3) + pwd.substring(8));
            if (p.getPassword().compareTo(MD5.crypt(reqemp.getEmpPwd())) == 0) {
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME_MILLI);

                AuthToken authToken = new AuthToken(String.valueOf(emplist.get(0).getEmpid()), "7",
                        emplist.get(0).getUserid(), emplist.get(0).getOpenid(), System.currentTimeMillis());
                String tokenJsonString = tokenServer.getTokenJsonString(authToken);
                String token = AESUtil.encode(tokenJsonString, Constant.AES_KEY);

                if (null != hashOperations.get(emplist.get(0).getEmpPhone(), "token")) {
                    hashOperations.delete(emplist.get(0).getEmpPhone(), "token");
                }

                hashOperations.put(token, "id", exprieDate.getTime());
                redisTemplate.expire(token, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);
                hashOperations.put(emplist.get(0).getEmpPhone(), "token", emplist.get(0).getEmpid() + "-" + token);
                redisTemplate.expire(emplist.get(0).getEmpPhone(), Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);

                emplist.get(0).setToken(token);
                UserInfo ui = userService.getUserInfoByUserId(emplist.get(0).getUserid());
                emplist.get(0).setPemail(ui.getEmail());

                addLoginLog(request, emplist.get(0).getUserid(), emplist.get(0).getEmpName(), emplist.get(0).getEmpName(), "2");
                return new RespInfo(0, "success", emplist.get(0));
            }
        }
        return new RespInfo(75, "Invalid username or password");
    }

    @ApiOperation(value = "/subAccountLogin 入驻企业登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/subAccountLogin")
    @ResponseBody
    public RespInfo subAccountLogin(@RequestBody SubAccount reqsa, HttpServletRequest request) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.indexOf("iPad") != -1 || userAgent.indexOf("iPhone") != -1 || userAgent.indexOf("android") != -1 || userAgent.indexOf("Android") != -1) {
        } else {
            String vcode = AESUtil.decode(reqsa.getDigest(), Constant.AES_KEY);
            if (hashOperations.hasKey(reqsa.getEmail() + "_vcode", "ValidationCode")) {
                String pcode = (String) hashOperations.get(reqsa.getEmail() + "_vcode", "ValidationCode");
                if (!pcode.equals(vcode)) {
                    return new RespInfo(119, "validationCode failed");
                }
                hashOperations.delete(reqsa.getEmail() + "_vcode", "ValidationCode");
            } else {
                return new RespInfo(119, "validationCode failed");
            }
        }

        SubAccount subAccount = subAccountService.getSubAccountByEmail(reqsa.getEmail());
        SubAccount sa = new SubAccount();
        if (subAccount == null) {
            Manager manager = managerService.getManagerByAccount(reqsa.getEmail());
            if (manager == null) {
                return new RespInfo(75, "Invalid username or password");
            } else {
                // TODO: 2020/5/14 子公司子管理员数据封装
                sa = subAccountService.getSubAccountById(manager.getSubAccountId());
                if (sa.getIsUse() == 0) {
                    return new RespInfo(94, "Account is disabled");
                }
                sa.setEmail(manager.getAccount());
                sa.setPassword(manager.getPassword());
                sa.setAdminType(1);
            }
        } else {
            if (subAccount.getIsUse() == 0) {
                return new RespInfo(94, "Account is disabled");
            }
            sa = subAccount;
        }

        String base64 = reqsa.getPassword();

        String front = base64.substring(0, 5);
        base64 = front + base64.substring(10);
        String pwd = Encodes.decodeBase64String(base64);
        pwd = new StringBuilder(pwd).reverse().toString();
        reqsa.setPassword(pwd.substring(0, 3) + pwd.substring(8));

        //校验登陆账号
        if (hashOperations.hasKey("fail_login" + reqsa.getEmail(), "locktime")) {
            Long locktime = (long) hashOperations.get("fail_login" + reqsa.getEmail(), "locktime");
            if (System.currentTimeMillis() - locktime > Constant.LOCK_TIME) {
                hashOperations.delete("fail_login" + reqsa.getEmail(), "locktime");
                hashOperations.delete("fail_login" + reqsa.getEmail(), "failcount");
            } else {
                SysLog.warn("fail_login " + reqsa.getEmail() + "失败次数超过8次已锁定");
                return new RespInfo(ErrorEnum.E_301.getCode(), ErrorEnum.E_301.getMsg());
            }
        }

        ReqUserInfo req = new ReqUserInfo();
        req.setPassword(reqsa.getPassword());
        req.setEmail(reqsa.getEmail());

        //校验密码
        RespInfo respInfo = checkPassWord(req, hashOperations, sa.getPassword());
        if (respInfo != null){
            addLoginFailedLog(request, sa.getUserid(), reqsa.getEmail(),reqsa.getEmail(), "0","失败 错误码："+respInfo.getStatus());
            return respInfo;
        }

        if (sa.getPassword().compareTo(MD5.crypt(reqsa.getPassword())) != 0) {
            return new RespInfo(75, "Invalid username or password");
        }

        sa.setPassword("");

        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(sa.getId()));
        map.put("clientType", "4");
        Date date = new Date();
        TokenManage tm = tokenManageService.getToken(map);
        Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME_MILLI);
//        String token = CommonUtils.generateSessionId(16);
        AuthToken authToken = new AuthToken();
        authToken.setUserid(sa.getUserid());
        authToken.setAccountRole(AuthToken.ROLE_SUB_COMPANY);
        authToken.setLoginAccountId(sa.getEmail());
        authToken.setDateTime(System.currentTimeMillis());
        String token = tokenServer.getAESEncoderTokenString(authToken);

        if (null == tm) {
            tm = new TokenManage();
            tm.setUserid(sa.getId());
            tm.setEmail(sa.getEmail());
            tm.setExpireDate(exprieDate);
            tm.setToken(token);
            tm.setClientType(4);
            tokenManageService.addToken(tm);
        } else {
            tm.setExpireDate(exprieDate);
            tm.setToken(token);
            tokenManageService.updateTokenManage(tm);
        }

        hashOperations.put(token, "id", exprieDate.getTime());
        redisTemplate.expire(token, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);

        sa.setToken(token);

        UserInfo ui = userService.getUserInfo(sa.getUserid());
        sa.setPemail(ui.getEmail());

        // TODO: 2020/4/28 查询入驻企业权限菜单
        if (StringUtils.isNotEmpty(reqsa.getEmail())) {
            List<Permission> permissionList = permissionService.getPermissionByaccount(reqsa.getEmail());
            if (permissionList.size() > 0 && null != permissionList) {
                List<Permission> permissionTree = permissionService.getPermissionTree(permissionList);
                sa.setModule(permissionTree);
            }
        }

        OperateLog ol = new OperateLog();
        ol.setUserid(ui.getUserid());
        ol.setOptId(sa.getEmail());
        String companyName = sa.getCompanyName();
        if (companyName.indexOf("#") > 0) {
            companyName = sa.getCompanyName().split("#")[0];
        }
        ol.setOptName(companyName);
        ol.setObjName("");
        ol.setOptClient("0");
        ol.setOptModule("0");
        ol.setOptRole("6");
        ol.setOptEvent("登录");
        ol.setOptDesc("成功");
        String ipAddr = request.getHeader("X-Forwarded-For");
        ol.setIpAddr(ipAddr);
        operateLogService.addLog(ol);

        return new RespInfo(0, "success", sa);

    }

    @ApiOperation(value = "/getPersonByOpenid 根据openid获取名片信息",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"type\": 0(必填)\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getPersonByOpenid")
    @ResponseBody
    public RespInfo getPersonByOpenid(
            @ApiParam(value = "电子名片表", required = true) @Validated @RequestBody Person p,
            BindingResult result, HttpServletRequest request) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (null == authToken) {
            throw new RuntimeException(ErrorEnum.E_608.getMsg());
        }

        p.setPopenid(authToken.getOpenid());
        if ("v".equals(p.getType())) {
            Person ps = personInfoService.getVisitPersonByOpenid(authToken.getOpenid());
            if (null != ps) {
                ps.setPassword("");
            }
            return new RespInfo(0, "success", ps);
        } else {
            Person ps = personInfoService.getInvitePersonByOpenid(authToken.getOpenid());
            List<Employee> elist = new ArrayList<Employee>();
            if (null != ps) {
                elist = employeeService.checkEmployeeExists(authToken.getUserid(), ps.getPmobile());
            }

            if (elist.size() == 0) {
                //openid检查
                elist = employeeService.getEmpListByOpenid(p.getPopenid());
                if (elist.size() == 0) {
                    return new RespInfo(1, "no employee");
                }
            }

            if (null != ps) {
                if (!ps.getPmobile().equals(elist.get(0).getEmpPhone())) {
                    personInfoService.delInvitePersonByOpenid(ps.getPopenid());
                    ps.setPmobile(elist.get(0).getEmpPhone());
                    personInfoService.addInvitePerson(ps);
                }
            } else {
                Person person = new Person();
                person.setPname(elist.get(0).getEmpName());
                person.setAvatar(elist.get(0).getAvatar());
                person.setPmobile(elist.get(0).getEmpPhone().replace("+86", ""));
                person.setPemail(elist.get(0).getEmpEmail());
                person.setPopenid(elist.get(0).getOpenid());
                person.setPcompany(elist.get(0).getCompanyName());
                person.setUserid(elist.get(0).getUserid());
                if ("男".equals(elist.get(0).getEmpSex())) {
                    person.setSex(1);
                } else if ("女".equals(elist.get(0).getEmpSex())) {
                    person.setSex(0);
                } else {
                    person.setSex(-1);
                }
                person.setFace(elist.get(0).getFace());
                personInfoService.addInvitePerson(person);
            }

            return new RespInfo(0, "success", ps);
        }
    }


    @ApiOperation(value = "/checkOpenid 校验openid", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/checkOpenid")
    @ResponseBody
    public RespInfo checkOpenid(HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (null == authToken) {
            throw new RuntimeException(ErrorEnum.E_608.getMsg());
        }
        String stype = authToken.getAccountRole();
        String openid = authToken.getOpenid();
        if (stype.equals("7") && StringUtils.isNotBlank(openid)) {
            List<Employee> emp = employeeService.getEmpListByOpenid(openid);
            if (emp.size() > 0) {
                HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
                hashOperations.put("wx-" + openid, "wxdd", emp.get(0).getEmpPhone());
                redisTemplate.expire("wx-" + openid, 12, TimeUnit.HOURS);

                return new RespInfo(0, "success", emp);
            } else {
                return new RespInfo(47, "no bind");
            }
        } else {
            throw new IllegalArgumentException("无效参数：" + authToken);
        }
    }

    @ApiOperation(value = "/empLoginByDDCode 第三方叮叮扫码登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\":\"1234324\"\n" +
                    "\t\"tmp_auth_code\":\"b3c36d1877703d9d8704e35625522f21\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLoginByDDCode")
    @ResponseBody
    public RespInfo empLoginByDD(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp requestEmp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        UserInfo userInfo = userService.getUserInfo(requestEmp.getUserid());
        String ddAppid = userInfo.getDdAppid();
        String ddAppSccessSecret = userInfo.getDdAppSccessSecret();
        String code = requestEmp.getTmp_auth_code();

        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
            OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
            req.setTmpAuthCode(code);
            OapiSnsGetuserinfoBycodeResponse response = client.execute(req, ddAppid, ddAppSccessSecret);
            System.out.println("扫码响应：" + mapperInstance.writeValueAsString(response));
            if (String.valueOf(response.getErrcode()).equals("0")) {
                String access_token = UtilTools.getDDAccToken(userInfo.getUserid(), ddAppid, ddAppSccessSecret);
                // 根据unionid获取userid
                DingTalkClient clientDingTalkClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/getbyunionid");
                OapiUserGetbyunionidRequest reqGetbyunionidRequest = new OapiUserGetbyunionidRequest();
                reqGetbyunionidRequest.setUnionid(response.getUserInfo().getUnionid());
                OapiUserGetbyunionidResponse oapiUserGetbyunionidResponse = clientDingTalkClient.execute(reqGetbyunionidRequest, access_token);
                System.out.println("获取userid响应：" + mapperInstance.writeValueAsString(oapiUserGetbyunionidResponse));
                if (String.valueOf(oapiUserGetbyunionidResponse.getErrcode()).equals("0")) {
                    String ddid = oapiUserGetbyunionidResponse.getResult().getUserid();
                    // 根据userId获取用户信息
                    Employee employee = employeeService.getEmpInfoByDDid(ddid, userInfo.getUserid());
                    if (null != employee) {
                        /**
                         * 公司存在判断
                         */
                        if (1 == userInfo.getSubAccount()) {
                            SubAccount accountById = subAccountService.getSubAccountById(employee.getSubaccountId());
                            if (accountById.getIsUse() == 0) {
                                return new RespInfo(94, "Account is disabled");
                            }
                        }
                        /**
                         * 有效期判断
                         */
                        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                        int d1 = Integer.parseInt(employee.getStartDate());
                        int d2 = Integer.parseInt(employee.getEndDate());
                        int today = Integer.parseInt(sd.format(new Date()));
                        if (today < d1 || today > d2) {
                            return new RespInfo(1113, "employee invalid date");
                        }

                    } else {
                        return new RespInfo(1, "invalid user");
                    }
                    /**
                     * 删除旧的token,生成新token
                     */
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    Date date = new Date();
                    Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
//                    String token = CommonUtils.generateSessionId(16);
                    AuthToken authToken = new AuthToken(employee.getEmpid() + "", "7", userInfo.getUserid(), employee.getOpenid(), System.currentTimeMillis());
                    String token = mapperInstance.writeValueAsString(authToken);
                    token = AESUtil.encode(token, Constant.AES_KEY);
                    hashOperations.put(token, "id", exprieDate.getTime());
                    hashOperations.put(employee.getEmpPhone(), "token", token);
                    redisTemplate.expire(token, 15, TimeUnit.HOURS);
                    redisTemplate.expire(employee.getEmpPhone(), 15, TimeUnit.HOURS);
                    employee.setToken(token);

                    /**
                     * 登录日志
                     */
                    UserInfo ui = userService.getUserInfoByUserId(employee.getUserid());
                    employee.setPemail(ui.getEmail());
                    addLoginLog(request, employee.getUserid(), employee.getEmpName(), employee.getEmpName(), "2");
                    return new RespInfo(0, "success", employee);
                } else {
                    return new RespInfo(673, oapiUserGetbyunionidResponse.getErrmsg());
                }
            } else {
                System.out.println("扫码鉴权失败:" + response.getErrmsg());
                return new RespInfo(672, "invalid dingtalk Qrcode");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/empLoginByInternalDD 企业内部叮叮扫码登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\":\"1234324\"\n" +
                    "\t\"tmp_auth_code\":\"b3c36d1877703d9d8704e35625522f21\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLoginByInternalDD")
    @ResponseBody
    public RespInfo empLoginByInternalDD(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp requestEmp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        UserInfo userInfo = userService.getUserInfo(requestEmp.getUserid());
        String code = requestEmp.getTmp_auth_code();

        String access_token = UtilTools.getDDAccToken(userInfo.getUserid(), userInfo.getDdAppid(), userInfo.getDdAppSccessSecret());

        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
            OapiUserGetuserinfoRequest req = new OapiUserGetuserinfoRequest();
            req.setCode(code);
            req.setHttpMethod("GET");
            OapiUserGetuserinfoResponse response;
            try {
                response = client.execute(req, access_token);
            } catch (ApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            System.out.println("扫码响应：" + mapperInstance.writeValueAsString(response));
            if (String.valueOf(response.getErrcode()).equals("0")) {
                // 根据unionid获取userid
                String ddid = response.getUserid();
                // 根据userId获取用户信息
                Employee employee = employeeService.getEmpInfoByDDid(ddid, userInfo.getUserid());
                if (null != employee) {
                    /**
                     * 公司存在判断
                     */
                    if (1 == userInfo.getSubAccount()) {
                        SubAccount accountById = subAccountService.getSubAccountById(employee.getSubaccountId());
                        if (accountById.getIsUse() == 0) {
                            return new RespInfo(94, "Account is disabled");
                        }
                    }
                    /**
                     * 有效期判断
                     */
                    SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                    int d1 = Integer.parseInt(employee.getStartDate());
                    int d2 = Integer.parseInt(employee.getEndDate());
                    int today = Integer.parseInt(sd.format(new Date()));
                    if (today < d1 || today > d2) {
                        return new RespInfo(1113, "employee invalid date");
                    }

                } else {
                    return new RespInfo(1, "invalid user");
                }
                /**
                 * 删除旧的token,生成新token
                 */
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
//                    String token = CommonUtils.generateSessionId(16);
                AuthToken authToken = new AuthToken(employee.getEmpid() + "", "7", userInfo.getUserid(), employee.getOpenid(), System.currentTimeMillis());
                String token = mapperInstance.writeValueAsString(authToken);
                token = AESUtil.encode(token, Constant.AES_KEY);
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(employee.getEmpPhone(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(employee.getEmpPhone(), 15, TimeUnit.HOURS);
                employee.setToken(token);

                /**
                 * 登录日志
                 */
                UserInfo ui = userService.getUserInfoByUserId(employee.getUserid());
                employee.setPemail(ui.getEmail());
                addLoginLog(request, employee.getUserid(), employee.getEmpName(), employee.getEmpName(), "2");
                return new RespInfo(0, "success", employee);

            } else {
                System.out.println("扫码鉴权失败:" + response.getErrmsg());
                return new RespInfo(672, "invalid dingtalk Qrcode");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/empLoginByWXChartCode 企业微信扫码登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\": \"43125135\"\n" +
                    "\t\"code\": \"zIHOJmbYt1R_VCaiiIO3n9Vyz7sem-fuspiLRLShxhI\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLoginByWXChartCode")
    @ResponseBody
    public RespInfo empLoginByWXChartCode(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp requestEmp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        try {
            String wchartUserId = employeeService.getWchartUserIdByCode(requestEmp.getUserid(), requestEmp.getCode());
            if (!"-1".equals(wchartUserId)) {
                UserInfo userInfo = userService.getUserInfo(requestEmp.getUserid());
                Employee emp = employeeService.getEmployeeByempRtxAccount(wchartUserId, userInfo.getUserid());
                if (null != emp) {

                    if(StringUtils.isNotBlank(emp.getEmpPhone())) {
                        Person p = personInfoService.getInvitePersonByPhone(emp.getEmpPhone());
                        /**
                         * 账号激活判断
                         */
                        if (null == p) {
                            p = new Person();
                            p.setPmobile(emp.getEmpPhone());
                            p.setAvatar(emp.getAvatar());
                            p.setUserid(emp.getUserid());
                            p.setPname(emp.getEmpName());
                            if ("男".equals(emp.getEmpSex())) {
                                p.setSex(1);
                            } else {
                                p.setSex(0);
                            }
                            p.setPemail(emp.getPemail());
                            p.setPopenid(emp.getOpenid());
                            //手机号变更,但openid已存在，删除原有重新注册
                            personInfoService.delInvitePersonByOpenid(emp.getOpenid());
                            personInfoService.addInvitePerson(p);
                        } else {
                            p.setPmobile(emp.getEmpPhone());
                            p.setAvatar(emp.getAvatar());
                            p.setUserid(emp.getUserid());
                            p.setPname(emp.getEmpName());
                            if ("男".equals(emp.getEmpSex())) {
                                p.setSex(1);
                            } else {
                                p.setSex(0);
                            }
                            p.setPemail(emp.getPemail());
                            p.setPopenid(emp.getOpenid());

                            personInfoService.updateInvitePerson(p);
                        }
                    }
                    /**
                     * 公司存在判断
                     */
                    if (1 == userInfo.getSubAccount()) {
                        SubAccount accountById = subAccountService.getSubAccountById(emp.getSubaccountId());
                        if (accountById.getIsUse() == 0) {
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
                } else {
                    System.out.println("获取成员失败:");
                    return new RespInfo(1, "invalid user");
                }
                /**
                 * 删除旧的token,生成新token
                 */
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken vt = new AuthToken(String.valueOf(emp.getEmpid()), "7",
                        userInfo.getUserid(), emp.getOpenid(), System.currentTimeMillis());
                ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
                String token = mapperInstance.writeValueAsString(vt);
                token = AESUtil.encode(token, Constant.AES_KEY);
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(emp.getEmpPhone(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(emp.getEmpPhone(), 15, TimeUnit.HOURS);
                emp.setToken(token);

                /**
                 * 登录日志
                 */
                UserInfo ui = userService.getUserInfoByUserId(emp.getUserid());
                addLoginLog(request, emp.getUserid(), emp.getEmpName(), emp.getEmpName(), "2");
                return new RespInfo(0, "success", emp);
            } else {
                System.out.println(wchartUserId);
                return new RespInfo(1, "invalid user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RespInfo(1, "invalid user");
        }
    }


    @ApiOperation(value = "/empLoginByWXCode 普通微信扫码登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\": \"43125135\"\n" +
                    "\t\"code\": \"zIHOJmbYt1R_VCaiiIO3n9Vyz7sem-fuspiLRLShxhI\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLoginByWXCode")
    @ResponseBody
    public RespInfo empLoginByWXCode(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp requestEmp,
            HttpServletRequest request, BindingResult result) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        try {
            UserInfo userInfo = userService.getUserInfo(requestEmp.getUserid());
            String openid = employeeService.getWxOpenIdByCode(requestEmp.getCode());
            if (StringUtils.isBlank(openid)) {
                return new RespInfo(ErrorEnum.E_702.getCode(), ErrorEnum.E_702.getMsg());
            }

            //openid查询预约的访客信息表
            Person invitePerson = personInfoService.getInvitePersonByOpenid(openid);
            Person visitPerson = personInfoService.getVisitPersonByOpenid(openid);

            //openid查询员工
            List<Employee> employees = employeeService.getEmpListByOpenid(openid);
            if (null != employees && employees.size() > 0 ) {
                //员工
                if (1 == userInfo.getSubAccount()) {
                    SubAccount accountById = subAccountService.getSubAccountById(employees.get(0).getSubaccountId());
                    if (accountById.getIsUse() == 0) {
                        return new RespInfo(94, "Account is disabled");
                    }
                }
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                int d1 = Integer.parseInt(employees.get(0).getStartDate());
                int d2 = Integer.parseInt(employees.get(0).getEndDate());
                int today = Integer.parseInt(sd.format(new Date()));
                if (today < d1 || today > d2) {
                    return new RespInfo(1113, "employee invalid date");
                }
                /**
                 * 删除旧的token,生成新token
                 */
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken empAuthToken = new AuthToken(String.valueOf(employees.get(0).getEmpid()), "7",
                        userInfo.getUserid(), employees.get(0).getOpenid(), System.currentTimeMillis());
                String token = tokenServer.getAESEncoderTokenString(empAuthToken);
                if (null != hashOperations.get(token, "token")) {
                    hashOperations.delete(token, "token");
                }
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(employees.get(0).getEmpPhone(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(employees.get(0).getEmpPhone(), 15, TimeUnit.HOURS);
                employees.get(0).setToken(token);


                return new RespInfo(0, "success", employees.get(0));
            }
            else if (null != invitePerson) {
                //员工的情况
                //检查员工是否存在
                employees = employeeService.getEmployeebyPhone(invitePerson.getPmobile(),null,invitePerson.getUserid());
                if(employees.size()==0){
                    return new RespInfo(ErrorEnum.E_701.getCode(), ErrorEnum.E_701.getMsg(), openid);
                }

                if (1 == userInfo.getSubAccount()) {
                    SubAccount accountById = subAccountService.getSubAccountById(employees.get(0).getSubaccountId());
                    if (accountById.getIsUse() == 0) {
                        return new RespInfo(94, "Account is disabled");
                    }
                }
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                int d1 = Integer.parseInt(employees.get(0).getStartDate());
                int d2 = Integer.parseInt(employees.get(0).getEndDate());
                int today = Integer.parseInt(sd.format(new Date()));
                if (today < d1 || today > d2) {
                    return new RespInfo(1113, "employee invalid date");
                }
                //创建token
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken vAuthToken = new AuthToken(employees.get(0).getEmpid()+"", "7", invitePerson.getUserid(), invitePerson.getPopenid(), System.currentTimeMillis());
                String token = tokenServer.getAESEncoderTokenString(vAuthToken);
                if (null != hashOperations.get(token, "token")) {
                    hashOperations.delete(token, "token");
                }
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(invitePerson.getPmobile(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(invitePerson.getPmobile(), 15, TimeUnit.HOURS);
                invitePerson.setAuthToken(token);
                invitePerson.setType("i");
                return new RespInfo(0, "success", invitePerson);
            }
            else if (null != visitPerson) {
                //创建token
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken vAuthToken = new AuthToken(visitPerson.getPmobile(), "8", visitPerson.getUserid(), visitPerson.getPopenid(), System.currentTimeMillis());
                String token = tokenServer.getAESEncoderTokenString(vAuthToken);
                if (null != hashOperations.get(token, "token")) {
                    hashOperations.delete(token, "token");
                }
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(visitPerson.getPmobile(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(visitPerson.getPmobile(), 15, TimeUnit.HOURS);
                visitPerson.setAuthToken(token);
                visitPerson.setType("v");
                return new RespInfo(0, "success", visitPerson);
            } else {
                System.out.println(dateFormat.format(System.currentTimeMillis()) + "根据openid未查询到系统用户");
                AuthToken at = new AuthToken();
                at.setOpenid(openid);
                return new RespInfo(ErrorEnum.E_701.getCode(), ErrorEnum.E_701.getMsg(), openid);
            }
        } catch (Exception e) {
            System.out.println("获取成员失败:");
            return new RespInfo(1, "invalid user");
        }
    }

    @ApiOperation(value = "/empLoginByFeiShuCode 飞书扫码登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\": \"43125135\"\n" +
                    "\t\"code\": \"XXXXXXXXXXX\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLoginByFeiShuCode")
    @ResponseBody
    public RespInfo empLoginByFeiShuCode(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp requestEmp,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        try {
            UserInfo userInfo = userService.getUserInfo(requestEmp.getUserid());
            String openId = employeeService.getFeiShuOpenIdByCode(userInfo.getUserid(), requestEmp.getCode());
            if (!"-1".equals(openId)) {
                List<Employee> empList = employeeService.getEmpListByOpenid(openId);
                Employee emp = new Employee();
                if (empList.size() > 0) {
                    emp = empList.get(0);
                    Person p = personInfoService.getInvitePersonByPhone(emp.getEmpPhone());
                    /**
                     * 账号激活判断
                     */
                    if (null == p) {
                        p = new Person();
                        p.setPmobile(emp.getEmpPhone());
                        p.setAvatar(emp.getAvatar());
                        p.setUserid(emp.getUserid());
                        p.setPname(emp.getEmpName());
                        if ("男".equals(emp.getEmpSex())) {
                            p.setSex(1);
                        } else {
                            p.setSex(0);
                        }
                        p.setPemail(emp.getPemail());
                        p.setPopenid(emp.getOpenid());

                        personInfoService.addInvitePerson(p);
                    } else {
                        p.setPmobile(emp.getEmpPhone());
                        p.setAvatar(emp.getAvatar());
                        p.setUserid(emp.getUserid());
                        p.setPname(emp.getEmpName());
                        if ("男".equals(emp.getEmpSex())) {
                            p.setSex(1);
                        } else {
                            p.setSex(0);
                        }
                        p.setPemail(emp.getPemail());
                        p.setPopenid(emp.getOpenid());

                        personInfoService.updateInvitePerson(p);
                    }
                    /**
                     * 公司存在判断
                     */
                    if (1 == userInfo.getSubAccount()) {
                        SubAccount accountById = subAccountService.getSubAccountById(emp.getSubaccountId());
                        if (accountById.getIsUse() == 0) {
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
                } else {
                    System.out.println("获取成员失败:");
                    return new RespInfo(1, "invalid user");
                }
                /**
                 * 删除旧的token,生成新token
                 */
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken vt = new AuthToken(String.valueOf(emp.getEmpid()), "7",
                        userInfo.getUserid(), emp.getOpenid(), System.currentTimeMillis());
                ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
                String token = mapperInstance.writeValueAsString(vt);
                token = AESUtil.encode(token, Constant.AES_KEY);
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(emp.getOpenid(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(emp.getOpenid(), 15, TimeUnit.HOURS);
                emp.setToken(token);

                /**
                 * 登录日志
                 */
                UserInfo ui = userService.getUserInfoByUserId(emp.getUserid());
                addLoginLog(request, emp.getUserid(), emp.getEmpName(), emp.getEmpName(), "2");
                return new RespInfo(0, "success", emp);
            } else {
                System.out.println(openId);
                return new RespInfo(1, "invalid user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RespInfo(1, "invalid user");
        }
    }

    @ApiOperation(value = "/logout 登出操作", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{ }"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/logout")
    @ResponseBody
    public RespInfo logout(HttpServletRequest request) {
        String token = request.getHeader("X-COOLVISIT-TOKEN");
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(token, "id")) {
            hashOperations.delete(token, "id");
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/LoginManager 前台登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{ }"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/LoginManager")
    @ResponseBody
    public RespInfo LoginManager(@ApiParam(value = "Manager 管理员Bean", required = true) @Validated @RequestBody Manager manager,
                                 HttpServletRequest request, BindingResult result) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.indexOf("iPad") != -1 || userAgent.indexOf("iPhone") != -1 || userAgent.indexOf("android") != -1 || userAgent.indexOf("Android") != -1) {
        } else {
            //登录验证码校验
            String vcode = AESUtil.decode(manager.getDigest(), Constant.AES_KEY);
            if (hashOperations.hasKey(manager.getAccount() + "_vcode", "ValidationCode")) {
                String pcode = (String) hashOperations.get(manager.getAccount() + "_vcode", "ValidationCode");
                if (!pcode.equals(vcode)) {
                    return new RespInfo(119, "validationCode failed");
                }
                hashOperations.delete(manager.getAccount() + "_vcode", "ValidationCode");
            } else {
                return new RespInfo(119, "validationCode failed");
            }
        }

        //查询账号
        Manager mgr = mgrService.getManagerPwdByAccount(manager.getAccount());
        if (null == mgr || mgr.getsType() != 2) {
            return new RespInfo(75, "Invalid username or password");
        }
        //校验锁定账号
        if (hashOperations.hasKey("fail_login" + manager.getAccount(), "locktime")) {
            Long locktime = (long) hashOperations.get("fail_login" + manager.getAccount(), "locktime");
            if (System.currentTimeMillis() - locktime > Constant.LOCK_TIME) {
                hashOperations.delete("fail_login" + manager.getAccount(), "locktime");
                hashOperations.delete("fail_login" + manager.getAccount(), "failcount");
            } else {
                SysLog.warn("fail_login " + manager.getAccount() + "失败次数超过8次已锁定");
                return new RespInfo(ErrorEnum.E_301.getCode(), ErrorEnum.E_301.getMsg());
            }
        }

        ReqUserInfo req = new ReqUserInfo();
        req.setPassword(manager.getPassword());
        req.setEmail(manager.getAccount());
        //校验密码
        RespInfo respInfo = checkPassWord(req, hashOperations, mgr.getPassword());
        if (respInfo != null){
            addLoginFailedLog(request, mgr.getUserid(), req.getEmail(), req.getEmail(), "0","失败 错误码："+respInfo.getStatus());
            return respInfo;
        }

        String base64 = manager.getPassword();
        String front = base64.substring(0, 5);
        base64 = front + base64.substring(10);
        String pwd = Encodes.decodeBase64String(base64);
        pwd = new StringBuilder(pwd).reverse().toString();
        manager.setPassword(pwd.substring(0, 3) + pwd.substring(8));

        //校验密码
        if (null != mgr && mgr.getPassword().compareToIgnoreCase(MD5.crypt(manager.getPassword())) == 0) {
            Date date = new Date();
            if (mgr.getEndDate().getTime() < date.getTime() || mgr.getStartDate().getTime() > date.getTime()) {
                return new RespInfo(42, "exceed the time limit");
            }

            Map<String, Object> maps = new HashMap<String, Object>();
            maps.put("userid", mgr.getUserid());
            UserInfo ui = userService.getUserInfo(mgr.getUserid());
            maps.put("pemail", ui.getEmail());
            Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME_MILLI);

            AuthToken authToken = new AuthToken();
            authToken.setUserid(mgr.getUserid());
            authToken.setAccountRole("2");
            authToken.setLoginAccountId(mgr.getAccount());
            authToken.setDateTime(System.currentTimeMillis());
            String token = tokenServer.getAESEncoderTokenString(authToken);

            hashOperations.put(token, "id", exprieDate.getTime());
            redisTemplate.expire(token, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);

            //封装放回数据
            Gate gate = new Gate();
            gate.setGids(String.valueOf(mgr.getGid()));
            gate.setUserid(ui.getUserid());
            List<Gate> glist = addressService.getGateById(gate);
            maps.put("sName", mgr.getSname());
            maps.put("sType", mgr.getsType());
            maps.put("token", token);
            maps.put("gid", mgr.getGid());
            if (glist.size() > 0) {
                maps.put("gname", glist.get(0).getGname());
            }

            //添加登录日志
            addLoginLog(request, mgr.getUserid(), mgr.getAccount(), mgr.getSname(), "3");
            return new RespInfo(0, "success", maps);
        } else {
            return new RespInfo(75, "Invalid username or password");
        }
    }

    @ApiOperation(value = "/getTokenByEncodeVid 根据加密vid获取token", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{ }"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getTokenByEncodeVid")
    @ResponseBody
    public RespInfo getTokenByEncodeVid(@RequestBody IvrData req) {
        String vid = AESUtil.decode(req.getVid(), Constant.AES_KEY);
        if (StringUtils.isNotBlank(req.getIvrType()) && "R".equals(req.getIvrType())) {
            ResidentVisitor residentVisitor = new ResidentVisitor();
            residentVisitor.setRid(vid);
            residentVisitor = residentVisitorService.getResidentVisitorByRid(residentVisitor);
            if (null != residentVisitor) {
                AuthToken authToken = new AuthToken();
                authToken.setLoginAccountId(vid);
                authToken.setUserid(residentVisitor.getUserid());
                authToken.setDateTime(new Date().getTime());
                authToken.setAccountRole(AuthToken.ROLE_SUPPLIER);
                String token = tokenServer.getAESEncoderTokenString(authToken);
                Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
                HashOperations hashOperations = redisTemplate.opsForHash();
                hashOperations.put(token, "id", exprieDate.getTime());
                Employee e = new Employee();
                e.setToken(token);
                return new RespInfo(0, "success", e);
            } else {
                return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
            }
        } else {
            Visitor visitor = visitorService.getVisitorById(Integer.parseInt(vid));
            if (null != visitor) {
                AuthToken authToken = new AuthToken();
                authToken.setLoginAccountId(vid);
                authToken.setUserid(visitor.getUserid());
                authToken.setDateTime(new Date().getTime());
                authToken.setAccountRole(AuthToken.ROLE_VISITOR);
                String token = tokenServer.getAESEncoderTokenString(authToken);
                Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
                HashOperations hashOperations = redisTemplate.opsForHash();
                hashOperations.put(token, "id", exprieDate.getTime());
                Employee e = new Employee();
                e.setToken(token);
                return new RespInfo(0, "success", e);
            } else {
                Appointment appointment = appointmentService.getAppointmentbyId(Integer.parseInt(vid));
                if (null != appointment) {
                    AuthToken authToken = new AuthToken();
                    authToken.setLoginAccountId(vid);
                    authToken.setUserid(visitor.getUserid());
                    authToken.setDateTime(new Date().getTime());
                    authToken.setAccountRole(AuthToken.ROLE_VISITOR);
                    String token = tokenServer.getAESEncoderTokenString(authToken);
                    Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    hashOperations.put(token, "id", exprieDate.getTime());
                    Employee e = new Employee();
                    e.setToken(token);
                    return new RespInfo(0, "success", e);
                } else {
                    return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
                }
            }
        }
    }

    @ApiOperation(value = "/refreshToken 重置token", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"email\":\"需要重置的账号\",\n" +
                    "    \"oldToken\":\"token\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/refreshToken")
    @ResponseBody
    public RespInfo refreshToken(@RequestBody ReqUserInfo userinfo, HttpServletRequest request) {
        //老token参数校验
        AuthToken oldAuthToken = tokenServer.getTokenBean(userinfo.getOldToken());
        if (null != oldAuthToken) {
            AuthToken authToken = new AuthToken(oldAuthToken.getLoginAccountId(), oldAuthToken.getAccountRole(),
                    oldAuthToken.getUserid(), oldAuthToken.getOpenid(), System.currentTimeMillis());
            String aesEncoderToken = tokenServer.getAESEncoderTokenString(authToken);
            int userid = oldAuthToken.getUserid();
            UserInfo ui = userService.getUserInfoByUserId(userid);
            if (ui.getUserType() == 1) {
                long expirdate = ui.getExpireDate().getTime() + Constant.EXTEND_TIME;
                Date date = new Date();
                if (expirdate < date.getTime()) {
                    return new RespInfo(42, "exceed the time limit", ui.getUserid());
                }
            }

            Date date = new Date();
            Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            if (hashOperations.hasKey(aesEncoderToken, "id")) {
                hashOperations.delete(aesEncoderToken, "id");
            }
            hashOperations.put(aesEncoderToken, "id", exprieDate.getTime());
            redisTemplate.expire(aesEncoderToken, 48, TimeUnit.HOURS);
            ui.setToken(aesEncoderToken);
            ui.setTokenExpire(new Long(Constant.EXPIRE_TIME).intValue() / 1000);
            return new RespInfo(0, "success", ui);
        } else {
            return new RespInfo(27, "invalid token");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ssoLogin")
    @ResponseBody
    public RespInfo ssoLogin(@RequestBody ReqUserInfo userinfo, HttpServletRequest request) {
        String account = userinfo.getAccount();
        String authid = userinfo.getAuthid();
        String timestamp = userinfo.getTimeStamp();
        if (timestamp == null || timestamp.isEmpty()) {
            return new RespInfo(ErrorEnum.E_604.getCode(), "timestamp is empty");
        }
        //验证时间戳的偏差
        Date current = new Date();
        long timestampL = Long.parseLong(timestamp);
        if (Math.abs(current.getTime() - timestampL) > 1000 * 60 * 5) {
            return new RespInfo(ErrorEnum.E_604.getCode(), "timestamp is expired");
        }

        int type = userinfo.getType();
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String digest = MD5.crypt(account + timestamp + "c2tea164e635e15a483963873661645d");
        if (!digest.equalsIgnoreCase(authid)) {
            return new RespInfo(ErrorEnum.E_002.getCode(), ErrorEnum.E_002.getMsg());
        }


//        String userToken = CommonUtils.generateSessionId(16);
        if (type == 1) {
            UserInfo ui = userService.selectByName(account);
            if(null==ui) {
          	  return new RespInfo(1, "invalid user");
            }
            Date date = new Date();
            Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
            AuthToken authToken = new AuthToken(ui.getEmail(), "6", ui.getUserid(),
                    null, System.currentTimeMillis());
            String token = tokenServer.getAESEncoderTokenString(authToken);
            hashOperations.put(token, "id", exprieDate.getTime());
            redisTemplate.expire(token, 24, TimeUnit.HOURS);
            ui.setToken(token);
            return new RespInfo(0, "success", ui);
        } else if (type == 2) {
            List<Employee> emplist = employeeService.checkEmployeeExists(userinfo.getUserid(), account);
            if (emplist.size() == 0) {
                return new RespInfo(1, "invalid user");
            }
            Date date = new Date();
            Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
//            String token = CommonUtils.generateSessionId(16);
            AuthToken authToken = new AuthToken(String.valueOf(emplist.get(0).getEmpid()), "7",
                    emplist.get(0).getUserid(), emplist.get(0).getOpenid(), System.currentTimeMillis());
            String token = tokenServer.getAESEncoderTokenString(authToken);
            if (null != hashOperations.get(token, "token")) {
                hashOperations.delete(token, "token");
            }

            hashOperations.put(token, "id", exprieDate.getTime());
            hashOperations.put(emplist.get(0).getEmpPhone(), "token", token);
            redisTemplate.expire(token, 15, TimeUnit.HOURS);

            emplist.get(0).setToken(token);
            UserInfo ui = userService.getUserInfo(emplist.get(0).getUserid());
            emplist.get(0).setPemail(ui.getEmail());
            return new RespInfo(0, "success", emplist.get(0));
        } else if (type == 3) {
            Manager mgr = mgrService.getManagerByAccount(account);
            if(null==mgr) {
            	return new RespInfo(1, "invalid user");
            }
            Map<String, Object> maps = new HashMap<String, Object>();
            maps.put("userid", mgr.getUserid());
            UserInfo ui = userService.getUserInfo(mgr.getUserid());
            maps.put("pemail", ui.getEmail());

            Date date = new Date();
            Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
//            String token = CommonUtils.generateSessionId(16);
            AuthToken authToken = new AuthToken(mgr.getAccount(), String.valueOf(mgr.getsType()), mgr.getUserid(), null, System.currentTimeMillis());
            String token = tokenServer.getAESEncoderTokenString(authToken);

            hashOperations.put(token, "id", exprieDate.getTime());
            redisTemplate.expire(token, 12, TimeUnit.HOURS);

            maps.put("token", token);
            return new RespInfo(0, "success", maps);
        } else {
            return new RespInfo(1010, "type error");
        }
    }


    @ApiOperation(value = "/empLoginByWXAppletsCode 小程序登录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"userid\": \"43125135\"\n" +
                    "\t\"code\": \"zIHOJmbYt1R_VCaiiIO3n9Vyz7sem-fuspiLRLShxhI\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/empLoginByWXAppletsCode")
    @ResponseBody
    public RespInfo empLoginByWXAppletsCode(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp requestEmp,
            HttpServletRequest request, BindingResult result) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        try {
            UserInfo userInfo = userService.getUserInfo(requestEmp.getUserid());
            String openid = employeeService.getWxAppletsOpenIdByCode(requestEmp.getCode());
            if (StringUtils.isBlank(openid)) {
                return new RespInfo(ErrorEnum.E_702.getCode(), ErrorEnum.E_702.getMsg());
            }

            //openid查询预约的访客信息表

            Person visitPerson = personInfoService.getVisitPersonByOpenid(openid);
            //openid查询员工
            List<Employee> employees = employeeService.getEmpListByOpenid(openid);
            if (null != employees && employees.size() > 0) {
                //员工
                if (1 == userInfo.getSubAccount()) {
                    SubAccount accountById = subAccountService.getSubAccountById(employees.get(0).getSubaccountId());
                    if (accountById.getIsUse() == 0) {
                        return new RespInfo(94, "Account is disabled");
                    }
                }
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                int d1 = Integer.parseInt(employees.get(0).getStartDate());
                int d2 = Integer.parseInt(employees.get(0).getEndDate());
                int today = Integer.parseInt(sd.format(new Date()));
                if (today < d1 || today > d2) {
                    return new RespInfo(1113, "employee invalid date");
                }
                /**
                 * 删除旧的token,生成新token
                 */
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken empAuthToken = new AuthToken(String.valueOf(employees.get(0).getEmpid()), AuthToken.ROLE_EMPLOYEE,
                        userInfo.getUserid(), employees.get(0).getOpenid(), System.currentTimeMillis());
                String token = tokenServer.getAESEncoderTokenString(empAuthToken);
                if (null != hashOperations.get(token, "token")) {
                    hashOperations.delete(token, "token");
                }
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(employees.get(0).getEmpPhone(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(employees.get(0).getEmpPhone(), 15, TimeUnit.HOURS);

                Employee emp = employees.get(0);
                Person invitePerson = new Person();
                invitePerson.setType("i");
                invitePerson.setAuthToken(token);
                invitePerson.setAvatar(emp.getAvatar());
                invitePerson.setPcompany(emp.getCompanyName());
                invitePerson.setPmobile(emp.getEmpPhone());
                invitePerson.setPopenid(emp.getOpenid());
                invitePerson.setPname(emp.getEmpName());
                invitePerson.setUserid(emp.getUserid());

                return new RespInfo(0, "success", invitePerson);
            }
//            if (null != invitePerson) {
//                //创建token
//                HashOperations hashOperations = redisTemplate.opsForHash();
//                Date date = new Date();
//                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
//                AuthToken vAuthToken = new AuthToken(invitePerson.getPmobile(), "7", invitePerson.getUserid(), invitePerson.getPopenid(), System.currentTimeMillis());
//                String token = tokenServer.getAESEncoderTokenString(vAuthToken);
//                if (null != hashOperations.get(token, "token")) {
//                    hashOperations.delete(token, "token");
//                }
//                hashOperations.put(token, "id", exprieDate.getTime());
//                hashOperations.put(invitePerson.getPmobile(), "token", token);
//                redisTemplate.expire(token, 15, TimeUnit.HOURS);
//                redisTemplate.expire(invitePerson.getPmobile(), 15, TimeUnit.HOURS);
//                invitePerson.setAuthToken(token);
//                invitePerson.setType("i");
//                return new RespInfo(0, "success", invitePerson);
//            }
            else if (null != visitPerson) {
                //创建token
                HashOperations hashOperations = redisTemplate.opsForHash();
                Date date = new Date();
                Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
                AuthToken vAuthToken = new AuthToken(visitPerson.getPmobile(), AuthToken.ROLE_VISITOR, visitPerson.getUserid(), visitPerson.getPopenid(), System.currentTimeMillis());
                String token = tokenServer.getAESEncoderTokenString(vAuthToken);
                if (null != hashOperations.get(token, "token")) {
                    hashOperations.delete(token, "token");
                }
                hashOperations.put(token, "id", exprieDate.getTime());
                hashOperations.put(visitPerson.getPmobile(), "token", token);
                redisTemplate.expire(token, 15, TimeUnit.HOURS);
                redisTemplate.expire(visitPerson.getPmobile(), 15, TimeUnit.HOURS);
                visitPerson.setAuthToken(token);
                visitPerson.setType("v");
                return new RespInfo(0, "success", visitPerson);
            } else {
                System.out.println(dateFormat.format(System.currentTimeMillis()) + "根据openid未查询到系统用户");
                AuthToken at = new AuthToken();
                at.setOpenid(openid);
                return new RespInfo(ErrorEnum.E_701.getCode(), ErrorEnum.E_701.getMsg(), openid);
            }
        } catch (Exception e) {
            System.out.println("获取成员失败:");
            return new RespInfo(1, "invalid user");
        }
    }

    @ApiOperation(value = "/signCheck 签名认证方式说明，用于检查签名是否正确", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\t\"appCode\": \"系统分配\"\n" +
                    "\t\"authid\": \"MD5(appCode+timestamp+appSecret)\"\n" +
                    "\t\"timestamp\": \"当前时间，可以通过/getTimestamp接口获取\"\n" +
                    "}"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="appCode",value="系统分配",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="authid",value="MD5(appCode+timestamp+appSecret),appSecret有系统分配",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="timestamp",value="当前时间，可以通过/getTimestamp接口获取",dataType="long", paramType = "query")
    })
    @RequestMapping(method = RequestMethod.POST, value = "/signCheck")
    @ResponseBody
    public RespInfo signCheck(RequestEmp req,HttpServletRequest request, BindingResult result) {
        //token和签名检查
        RespInfo respInfo = tokenServer.checkSign(request);
        if(respInfo != null){
            return respInfo;
        }
        return new RespInfo(0, "success");
    }

    /**
     * 员工Token换取管理员Token
     * @param request
     * @param requestEmp
     * @return
     */
    @PostMapping("/getAdminToken")
    public RespInfo exChangeToken(HttpServletRequest request,RequestEmp requestEmp){

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        //权限检查
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        String phone = requestEmp.getPhone();
        List<Employee> empInfo = employeeService.getEmpInfo(authToken.getUserid(), phone);
        if (empInfo.isEmpty()){
            return new RespInfo(-1,"fail");
        }
        Employee employee = empInfo.get(0);
        int userid = employee.getUserid();
        UserInfo userInfoByUserId = userService.getUserInfoByUserId(userid);
        if (ObjectUtils.isEmpty(userInfoByUserId)){
            return new RespInfo(-1,"fail");
        }
        AuthToken authTokenAdmin = new AuthToken(userInfoByUserId.getEmail(), AuthToken.ROLE_ADMIN, userInfoByUserId.getUserid(), null, System.currentTimeMillis());
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        String userToken = "";
        try {
            userToken = mapperInstance.writeValueAsString(authTokenAdmin);
            userToken = AESUtil.encode(userToken, Constant.AES_KEY);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        Date exprieDate = new Date(date.getTime() + Constant.EXPIRE_TIME);
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(userInfoByUserId.getUserid()));


        Map<String, Object> m = new HashMap<String, Object>();
        m.put("userid", userInfoByUserId.getUserid());
        m.put("username", userInfoByUserId.getUsername());

        userInfoByUserId.setLoginDate(new Date());
        userInfoByUserId.setTokenExpire(new Long(Constant.EXPIRE_TIME).intValue() / 1000);
        userService.updateLoginDate(userInfoByUserId);
        Configures conf = configureService.getConfigure(userInfoByUserId.getUserid(), Constant.SMSCOUNT);
        userInfoByUserId.setSmsTotal(Integer.parseInt(conf.getValue()));
        userInfoByUserId.setPassword("");
        hashOperations.put(userToken, "id", exprieDate.getTime());
        redisTemplate.expire(userToken, Constant.EXPIRE_TIME_HOUR, TimeUnit.HOURS);
        userInfoByUserId.setToken(userToken);


        addLoginLog(request, userInfoByUserId.getUserid(), userInfoByUserId.getEmail(), userInfoByUserId.getUsername(), "0");
        userInfoByUserId.setsType(-1);
        return new RespInfo(0,"success",userInfoByUserId);

        //UserInfo userinfo = userService.getUserInfoWithExt(req.getEmail());
    }

}
