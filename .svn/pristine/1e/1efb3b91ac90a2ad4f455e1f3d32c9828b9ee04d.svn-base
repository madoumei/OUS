package com.event.listener;

import com.client.bean.Visitor;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.utils.StringUtils;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.msgUtils.MsgTemplateUtils;
import com.utils.yimei.AES;
import com.utils.yimei.GZIPUtils;
import com.utils.yimei.JsonHelper;
import com.utils.yimei.ResultModel;
import com.utils.yimei.http.*;
import com.web.bean.Configures;
import com.web.bean.Employee;
import com.web.bean.OperateLog;
import com.web.bean.UserInfo;
import com.web.dao.ConfigureDao;
import com.web.dao.MsgTemplateDao;
import com.web.dao.UserDao;
import com.web.service.MsgTemplateService;
import com.web.service.OperateLogService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Component
@Service
public class SMSYimeiService implements SmartApplicationListener {

    private final static String PLATEFORM = "sms";

    // 加密算法
    private final static String algorithm = "AES/ECB/PKCS5Padding";

    // 编码
    private final static String encode = "UTF-8";

    private final static String APPID = "EUCP-EMY-SMS1-4HOT6";

    private final static String SECRETKEY = "508763302D4C9CBB";

    @Autowired
    protected UserService userService;

    @Autowired
    protected ConfigureDao configureDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected OperateLogService operateLogService;

    @Autowired
    protected MsgTemplateService msgTemplateService;

    public SMSYimeiService() {
        SysLog.info(this.getClass().getName()+" start");
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == NotifyEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public int getOrder() {
        return 8;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            if (applicationEvent instanceof NotifyEvent) {
                NotifyEvent event = (NotifyEvent) applicationEvent;
                onNotifyEvent(event);
            }
        }catch (Exception e){
            SysLog.error(e);
        }

    }

    /**
     * 预约访客事件
     * @param event
     */
    private void onNotifyEvent(NotifyEvent event){
        UserInfo userInfo = userService.getUserInfo(event.getUserId());
        if(null == userInfo){
            SysLog.error("NotifyEvent can't find UserInfo id="+event.getUserId());
            return;
        }
        if(userInfo.getSmsNotify() == 0){
            return;
        }
        //是否为顺序发送
        if(userInfo.getNotifyType()==1 && StringUtils.isNotEmpty(event.getSentWay())) {
            return;
        }

        //是否指定发送方式
        if(StringUtils.isNotEmpty(event.getSendWay()) && !event.getSendWay().contains(PLATEFORM)){
            return;
        }

        com.web.bean.MsgTemplate msg = msgTemplateService.getTemplate(0, PLATEFORM+"_"+event.getEventType());
        if (msg == null || 0 == msg.getStatus()){
            SysLog.warn("未设置模板："+PLATEFORM+"_"+event.getEventType());
            return;
        }
        String content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());
        if(StringUtils.isBlank(content)){
            //未设置模板，不发送
            return;
        }

        int smsCount = 0;
        for(Litigant litigant:event.getReceivers()) {
            if(StringUtils.isBlank(litigant.getPhone())|| litigant.getPhone().startsWith("11")){
                continue;
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("message", content);
            params.put("phone", litigant.getPhone());
            String response = sendSms(params, configureDao, userInfo);
            //计数
            if ("0".equals(response)) {
                int length = params.get("message").length();
                smsCount = (length/70) + (length%70==0?0:1);
                //发送成功之后才会标记
                event.setSentWay(event.getSentWay()+",sms");
            }

            //日志
            OperateLog.addSMSLog(operateLogService, userInfo.getUserid(), response, smsCount, litigant.getPhone(), litigant.getName(), params.get("message"));
        }
    }

//    /**
//     * 发送预约授权
//     * @param userinfo
//     * @param vt
//     * @param emp
//     * @return
//     */
//    protected String sendInviteSMS(UserInfo userinfo, String msg,String phone) {
//        String vid = AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
//        SimpleDateFormat time = new SimpleDateFormat("MM月dd日");
//
//        Map<String, String> params = new HashMap<String, String>();
//        int smsCount = 0;
//        if (userinfo.getPermissionSwitch() == 1 && vt.getPermission() == 0) {
//            params.put("message", "您好，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，点击链接授权：" + Constant.FASTDFS_URL + "e.html?v=" + vid);
//        } else {
//            params.put("message", "您好，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，请准时接待。");
//        }
//        params.put("phone", phone);
//        String response = sendSms(params, configureDao, userinfo);
//        if ("0".equals(response)) {
//            int length = params.get("message").length();
//        int smsCount = (length/70) + (length%70==0?0:1);
//        }
//
//        //日志
//        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, smsCount,emp.getEmpPhone(),emp.getEmpName(),params.get("message"));
//
//        return response;
//    }

    public String sendSms(Map<String, String> params, ConfigureDao configureDao, UserInfo userinfo) {
        if (checkSMSCount(configureDao, userinfo)) return ErrorEnum.E_043.getCode()+"";
        String response = send(params,userinfo);
        int length = params.get("message").length();
        int smsCount = (length/70) + (length%70==0?0:1);
        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + smsCount);
            userDao.updateAppSmsCount(userinfo);
        }
        return response;
    }

    /**
     * 检查短信数量
     * @param configureDao
     * @param userinfo
     * @return
     */
    protected static boolean checkSMSCount(ConfigureDao configureDao, UserInfo userinfo) {
        int smscount = userinfo.getSmsCount() + userinfo.getWxSmsCount() + userinfo.getAppSmsCount();
        Configures conf = configureDao.getConfigure(userinfo.getUserid(), Constant.SMSCOUNT);
        if (null == conf) {
            conf = configureDao.getDefaultConfigure(Constant.SMSCOUNT);
        }

        if (smscount > Integer.parseInt(conf.getValue())) {
            SysLog.error("短信已用完:"+userinfo.getUserPrintInfo());
            return true;
        }

        if ((Integer.parseInt(conf.getValue())-smscount)==100
                ||(Integer.parseInt(conf.getValue())-smscount)==50
                ||(Integer.parseInt(conf.getValue())-smscount)==30) {
            SysLog.warn("短信快用完:"+userinfo.getUserPrintInfo()+" 剩余短信数量:"+(Integer.parseInt(conf.getValue())-smscount));
            sendSMSWarning("尊敬的客户，您的短信余额已不足"+(Integer.parseInt(conf.getValue())-smscount)+"条，为了保证您的业务正常使用，请尽快充值。",userinfo);
            return true;
        }
        return false;
    }


    /**
     * 发送短信条数告警通知给管理员
     * @param warningMsg
     * @param userinfo
     * @return
     */
    public static String sendSMSWarning(String warningMsg,UserInfo userinfo) {
        Map<String,String> params=new HashMap<String,String>();
        params.put("message", warningMsg);
        params.put("phone", userinfo.getPhone());
        return send(params,userinfo);
    }

    private static String send(Map<String, String> params, UserInfo userinfo) {
        SysLog.info("=============begin setSingleSms==================");
        SmsSingleRequest pamars = new SmsSingleRequest();
        pamars.setContent(params.get("message"));
        pamars.setCustomSmsId(null);
        pamars.setExtendedCode(null);
        pamars.setMobile(params.get("phone"));
        ResultModel result = request(APPID, SECRETKEY, algorithm, pamars, "http://shmtn.b2m.cn/inter/sendSingleSMS", false, encode);
        if ("SUCCESS".equals(result.getCode())) {
            SysLog.info("result code :" + result.getCode());
            SmsResponse response = JsonHelper.fromJson(SmsResponse.class, result.getResult());
            SysLog.info("=============end setSingleSms==================");
            return "0";
        }else{
            SysLog.error("发送短信 result="+result+userinfo.getUserPrintInfo());
        }
        SysLog.info("=============end setSingleSms==================");


        return "-1";
    }

    public static ResultModel request(String appId, String secretKey, String algorithm, Object content, String url, final boolean isGzip, String encode) {
        Map<String, String> headers = new HashMap<String, String>();
        EmayHttpRequestBytes request = null;
        try {
            headers.put("appId", appId);
            headers.put("encode", encode);
            String requestJson = JsonHelper.toJsonString(content);
            SysLog.info("result json: " + requestJson);
            byte[] bytes = requestJson.getBytes(encode);
            if (isGzip) {
                headers.put("gzip", "on");
                bytes = GZIPUtils.compress(bytes);
            }
            byte[] parambytes = AES.encrypt(bytes, secretKey.getBytes(), algorithm);
            request = new EmayHttpRequestBytes(url, encode, "POST", headers, null, parambytes);
        } catch (Exception e) {
            SysLog.error("短信加密异常");
            e.printStackTrace();
        }
        EmayHttpClient client = new EmayHttpClient();
        String code = null;
        String result = null;
        try {
            EmayHttpResponseBytes res = client.service(request, new EmayHttpResponseBytesPraser());
            if (res == null) {
                SysLog.error("请求接口异常");
                return new ResultModel(code, result);
            }
            if (res.getResultCode().equals(EmayHttpResultCode.SUCCESS)) {
                if (res.getHttpCode() == 200) {
                    code = res.getHeaders().get("result");
                    if (code.equals("SUCCESS")) {
                        byte[] data = res.getResultBytes();
                        data = AES.decrypt(data, secretKey.getBytes(), algorithm);
                        if (isGzip) {
                            data = GZIPUtils.decompress(data);
                        }
                        result = new String(data, encode);
                        SysLog.info("response json: " + result);
                    }
                } else {
                    SysLog.error("请求接口异常,请求码:" + res.getHttpCode());
                }
            } else {
                SysLog.error("请求接口网络异常:" + res.getResultCode().getCode());
            }
        } catch (Exception e) {
            SysLog.error("解析失败");
            e.printStackTrace();
        }
        ResultModel re = new ResultModel(code, result);
        return re;
    }
}
