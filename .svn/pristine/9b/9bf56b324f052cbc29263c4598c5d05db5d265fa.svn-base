package com.utils;

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public static String EMPCOUNT = "empcount";

    public static String REFRESHCOUNT = "refreshcount";

    public static String WATERMARK = "watermark";

    public static String SMSCOUNT = "smscount";

    public static String FACESCAN = "faceScan";

    public static String GATECOUNT = "gatecount";//门岗数量限制

    //员工同步
    public static String EMP_SYNC = "empSync";

    public static String BUCKET_NAME = "coolvisit";
    //优图
    public static final String APP_ID = "";
    public static final String SECRET_ID = "";
    public static final String SECRET_KEY = "";
    //二十四小时
    public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L; // milliseconds

    public static final long EXTEND_TIME = 30 * 24 * 60 * 60 * 1000L;

    public static final long LOCK_TIME = 5 * 60 * 288000L;

    public static final long EXPIRE_TIME_HOUR = 12;

    public static final long EXPIRE_TIME_MILLI = 43200000L;

    //妙兜开门
    public static final String APP_KEY = "";
    public static final String AGT_NUM = "";

    //极光推送
    public final static String OD_MASTER_SECRET = "";
    public final static String OD_APP_KEY = "";

    public final static String AES_KEY = "fangketong2016";

    public final static String SIGNIN_NOTIFY = "已在公司前台完成来访登记，请准备接待。";

    public final static String DDNOTIFY_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

    public final static String DDPAGELINK_URL = "dingtalk://dingtalkclient/action/openapp?";

    public static final String[] passUrl = {
            "/qcvisit/error",
            "/qcvisit/v2/**", "/qcvisit/swagger-ui.html", "/qcvisit/webjars/**", "/qcvisit/swagger-resources/**", "/qcvisit/doc.html", "/qcvisit/templates/websocket.jsp",
            "/qcvisit/getValidationCode",
            "/qcvisit/validationCode",
            "/qcvisit/Login",
            "/qcvisit/empLogin",
            "/qcvisit/empLoginByDDCode", "/qcvisit/empLoginByInternalDD",
            "/qcvisit/empLoginByWXChartCode", "/qcvisit/empLoginByFeiShuCode",
            "/qcvisit/empLoginByWXCode",
            "/qcvisit/LoginManager",
            "/qcvisit/ssoLogin",
            "/qcvisit/subAccountLogin",
            "/qcvisit/sendPicSmsCode",
            "/qcvisit/checkSmsCode",
            "/qcvisit/addPersonInfo",
            "/qcvisit/webActivateAccount",
            "/qcvisit/ExportVisitorList",
            "/qcvisit/ExportAbnormalEmployee",
            "/qcvisit/ExportBlacklist",
            "/qcvisit/ExportOpendoorInfoList",
            "/qcvisit/ExportLogisticsList",
            "/qcvisit/ExportLogList",
            "/qcvisit/ExportInterimCardList",
            "/qcvisit/ExportVehicleInfoList",
            "/qcvisit/ExportRvOdList",
            "/qcvisit/ExportgetRvrList",
            "/qcvisit/uploadQrcode",
            "/qcvisit/newUploadQrcode",
            "/qcvisit/show", "/qcvisit/bus",
            "/qcvisit/getPermissionRecordByLink", "/qcvisit/updatePermissionByLink", "/qcvisit/updateAppointmentReply",
            "/qcvisit/getEmployeeByEncoderVid", "/qcvisit/getVisitorTypeByEncodeVid", "/qcvisit/getTokenByEncodeVid",
            "/qcvisit/getAppointmentbySecId",
            "/qcvisit/uploadAppointmentPhoto",
            "/qcvisit/downloadResidentAllFile","/qcvisit/downloadResidentFile",
            "/qcvisit/RetrievePassword",//发送找回密码链接
            "/qcvisit/ModifyPassword",//修改密码
            "/qcvisit/getVisitorType",//短信授权使用
            "/qcvisit/getExtendTypeInfo",//独立预约页面使用
            "/qcvisit/getGate",//独立预约页面使用
            //"/qcvisit/getEmployeeByName",//独立预约页面使用
            "/qcvisit/addVisitorApponintmnet",//独立预约页面使用
            "/qcvisit/getEmployeeByPhone",//独立预约页面使用
            "/qcvisit/getTimestamp",//获取时间戳，签名时使用
            "/qcvisit/getVisitorBySecId",//问卷调查，低风险
            "/qcvisit/empLoginByWXAppletsCode",//小程序鉴权
            "/qcvisit/getAppletPics",//小程序轮播图
            "/qcvisit/GetPicUrl",//独立预约页面获取协议
            "/qcvisit/getDefaultReception",//获取默认接待人
            "/qcvisit/resetEmpPwd",//员工重置密码
            "/qcvisit/signCheck",//检查签名
            "/qcvisit/getAnswerResult",//邀请函获取答题结果
            "/qcvisit/getVisitorTypeByTid",//邀请函获取问卷信息
            "/qcvisit/uploadFace",//签名验证，邀请函用
            "/qcvisit/getFaceStatus",//签名验证，邀请函用
            "/qcvisit/Upload",//上传图片
            "/qcvisit/getMeetingById",//签名验证，根据Id获取会议
            "/qcvisit/addReserveMeeting",//签名验证，预约会议
            "/qcvisit/uploadFaceStatus",//更新人脸识别
            "/qcvisit/updateSupplementTask", //邀请函更新访客信息
            "/qcvisit/addAppointmentSupplement", //添加随访人员
            "/qcvisit/getAppointmentaGroupBySecid", //根据secid获取访客信息
            "/qcvisit/getEmployeeByEmail", //根据邮箱获取员工信息
            "/qcvisit/checkSystem", //系统检测
            "/qcvisit/testOnVisitAppoinmentNotify", //测试接口
            "/qcvisit/releaseLock", //解除锁定ip
            "/qcvisit/getRiskArea" //获取危险区域
    };

    public static String path = "/work/account.properties";
    public final static String FASTDFS_URL = ReadProperties.getProperties_2(path, "user.url");
    public final static String WECHAT_URL = ReadProperties.getProperties_2(path, "wechat.url");//微信部署路径
    public final static String CUSTOM_URL = ReadProperties.getProperties_2(path, "custom.url");//第三方服务器路径
    public final static String ACCOUNT = ReadProperties.getProperties_2(path, "user.account","visitor@coolvisit.com");
    public final static String APPID = ReadProperties.getProperties_2(path, "APPID");
    public final static String APP_SECRET = ReadProperties.getProperties_2(path, "APP_SECRET");
    public final static String AppointmentNotify = ReadProperties.getProperties_2(path, "AppointmentNotify");
    public final static String VisitNotify = ReadProperties.getProperties_2(path, "VisitNotify");
    public final static String AppointSuccess = ReadProperties.getProperties_2(path, "AppointSuccess");
    public final static String AppointFefuse = ReadProperties.getProperties_2(path, "AppointFefuse");
    public final static String ApplyProcess = ReadProperties.getProperties_2(path, "ApplyProcess");
    public final static String ProcessFinish = ReadProperties.getProperties_2(path, "ProcessFinish");
    public final static String InviteSuccess = ReadProperties.getProperties_2(path, "InviteSuccess");
    public final static String InviteFefuse = ReadProperties.getProperties_2(path, "InviteFefuse");
    public final static String LeaveNotify = ReadProperties.getProperties_2(path, "LeaveNotify");
    public final static String CcNotify = ReadProperties.getProperties_2(path, "CcNotify");
    public final static String AppointArrived = ReadProperties.getProperties_2(path, "AppointArrived");
    public final static String Visitcancel = ReadProperties.getProperties_2(path, "Visitcancel");

    public final static String AutoSignout = ReadProperties.getProperties_2(path, "AutoSignout");//自动签离
    public final static String AutoSignoutAfterLeave = ReadProperties.getProperties_2(path, "AutoSignoutAfterLeave");//结束拜访后自动签离
    public final static String AutoSignin = ReadProperties.getProperties_2(path, "AutoSignin");//开门自动签到
    public final static String AccessWithoutSignin = ReadProperties.getProperties_2(path, "AccessWithoutSignin");//允许未签到开门
    public final static String AllowedAppCodeAccess = ReadProperties.getProperties_2(path, "AllowedAppCodeAccess");//允许刷邀请码进门

    public final static String APPLETS_ID = ReadProperties.getProperties_2(path, "APPLETSID");
    public final static String APPLETS_SECRET = ReadProperties.getProperties_2(path, "APPLETSSECRET");
    public final static String H2_IP=ReadProperties.getProperties_2(path, "h2_ip");//红门服务器
    
    public final static String MINIO_IP=ReadProperties.getProperties_2(path, "minio_ip");//minio 服务器
    public final static String MINIO_ACCOUNT=ReadProperties.getProperties_2(path, "minio_account");//minio  帐号
    public final static String MINIO_PASSWORD=ReadProperties.getProperties_2(path, "minio_password");//minio 密码

    //主从设置 1 master 0 slave
    public final static String IS_MASTER = ReadProperties.getProperties_2(path, "is_master","1");

    /**
     * 自动审批的线程锁
     */
    public static String LOCK_AUTO_COMPLETE = "LOCK_AUTO_COMPLETE";

    public final static Map<String, String> WeiXin_Notify = WeiXinEnum();

    public static Map<String, String> WeiXinEnum() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("APPID", APPID);
        map.put("APP_SECRET", APP_SECRET);
        map.put("AppointmentNotify", AppointmentNotify);
        map.put("VisitNotify", VisitNotify);//来访提醒
        map.put("AppointSuccess", AppointSuccess);//预约成功通知
        map.put("AppointFefuse", AppointFefuse);//预约失败提醒
        map.put("ApplyProcess", ApplyProcess);//流程审批提醒
        map.put("ProcessFinish", ProcessFinish);//申请审批提醒
        map.put("InviteSuccess", InviteSuccess);//邀请已接受通知
        map.put("InviteFefuse", InviteFefuse);//拒绝邀请提醒
        map.put("LeaveNotify", LeaveNotify);//访客处理提醒
        map.put("CcNotify", CcNotify);//流程抄送提醒
        map.put("AppointArrived", AppointArrived);//邀请签到提醒
        map.put("Visitcancel", Visitcancel);//邀请签到提醒
        return map;
    }

    //签名授权app
    public final static Map<String, String> APP_CODES = appCodes();

    public static Map<String, String> appCodes() {
        //在此增加签名授权appcode，secret
        Map<String, String> map = new HashMap<String, String>();
        map.put("q0001", "asdfkxlcY2ewleml9209Klsd");//前端
        return map;
    }


    //小程序
    public final static Map<String, String> WeiXin_Applets_Notify = WeiXinAppletsEnum();

    public static Map<String, String> WeiXinAppletsEnum() {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put("APPLETSID", APPLETS_ID);
        map.put("APPLETSSECRET", APPLETS_SECRET);
        return map;
    }

}
	