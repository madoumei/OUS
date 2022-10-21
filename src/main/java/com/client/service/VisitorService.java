package com.client.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.api.push.PushResult;

import com.annotation.ProcessLogger;
import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.utils.PoolHttpClientUtil;
import com.web.bean.*;

public interface VisitorService extends IService<Visitor> {

	public static final int PERMISSION_FIRST = 0;//待一次授权
	public static final int PERMISSION_ACCEPT = 1;//通过
	public static final int PERMISSION_REJECT = 2;//拒绝
	public static final int PERMISSION_CANCEL = 3;//取消
	public static final int PERMISSION_APPROVE = 4;//待审批
	public static final int PERMISSION_SEC = 5;//完成一次授权

	/**
	 * 扩展字段关键字
	 */
	public static final String EXTEND_KEY_ACCESS = "access";//授权门禁组
	public static final String EXTEND_KEY_MOREDAYS = "moreDays";//多天访问
	public static final String EXTEND_KEY_MORETIMES = "moreTimes";//当天多次进出
	public static final String EXTEND_KEY_ACCESSTYPE = "accessType";//允许的开门方式
	public static final String EXTEND_KEY_ROUTE = "route";//行驶路径id
	public static final String EXTEND_KEY_GEOFENCE = "gf";//电子围栏id
	public static final String EXTEND_KEY_ARRIVETIME = "arriveTime";//到达电子围栏时间
	public static final String EXTEND_KEY_ENDDATE = "visitorEndDate";//拜访结束时间

	/**
	 * 自动授权
	 * @param vid id
	 * @param tid
	 * @param vType 访客类型
	 */
	public void autoPermission(int vid,int tid,String vType);

	/**
	 * 自动审批
	 */
	public void autopproval(int vid);

	/**
	 * 二维码人员类型
	 * @param qrcode
	 * @return 01 员工  02 访客  03 常驻访客
	 */
    String getEType(String qrcode);

	/**
	 * 二维码类型
	 * @param qrcode
	 * @return 23 邀请二维码  25 预约二维码 40 打印贴纸二维码
	 */
	String getQrcodeType(String qrcode);

	/**
	 * 是否允许刷邀约码开门
	 * @param qrcodeType 访客二维码类型
	 */
	void checkAppCodeAccessTask(String qrcodeType);

	void checkQrcodeExpiredDateTask(String expireddate);

	Visitor checkVisitorPermissionTask(String vid);

	void checkSigninAppointmentQrcodeTask(Appointment app);

	void checkLeaveStatusTask(Visitor vt);

    void checkSignoutStatusTask(Visitor visitor);

    void checkSigninStatusTask(Visitor vt);

    int autoSigninRouter(Visitor vt);

    Employee checkEmployeeTask(String empid);

    Employee checkEmployeeByCardNoTask( String cardNo);

    Equipment checkEquipmentTask(String egids, String devicenumber);

	@ProcessLogger("检查门禁潮汐属性")
	void checkEquipmentRuleTask(Equipment eqpt);

	@ProcessLogger("检查通行策略")
	void checkEquipmentPassTimeTask(Equipment eqpt, Employee employee);

	int autoSignoutRouter(Visitor vt);

	void autoSignoutTask(Visitor vt, Equipment equipment);

	boolean checkVisitorAccessTimeTask(UserInfo ui);

	boolean checkAppointmentVisitorLimitTask(Visitor vt, UserInfo ui);

	void checkTimesTask(UserInfo ui, String vType, String vphone) throws IOException;

	void checkAccessTypeTask(UserInfo ui, String vType, String accType) throws IOException;

    void checkQuestionnaireTask(UserInfo userinfo, Visitor vt);

    void sendVisitorPassMsgTask(UserInfo ui, Equipment eq, Visitor visitor) throws IOException;

	void addExtendSetting(UserInfo userinfo, Visitor vt);

	public int addVisitor(Visitor vt);
	
	public int updateVisitor(Visitor vt);
	
	public Visitor getVisitorById(int vid);

	public int updateVisitorSms(Visitor vt);
	
	public int updatePermission(Visitor vt);

	int updateVGroup(Visitor vt);

	public boolean sendMail(UserInfo userinfo, Employee emp, Visitor vt, List<ExtendVisitor> evlist) throws JsonParseException, JsonMappingException, IOException;

	/**
	 * 邮件通知访客已完成签到
	 * @param userinfo
	 * @param emp 被通知人
	 * @param vt  签到数据
	 * @return
	 */
	public boolean sendAppointmentMail(UserInfo userinfo,Employee emp,Visitor vt) throws JsonParseException, JsonMappingException, IOException;
	
	public Visitor getVisitor(Visitor v);
	
	public List<RespVisitor> getVisitorList(int userid, String visitDate);

	/**
	 * 微信通知访客已完成签到
	 * @param userinfo
	 * @param emp 被通知人
	 * @param vt  签到数据
	 * @return
	 */
	public String sendWeixin(UserInfo userinfo,Employee emp,Visitor vt);
	
	public String sendWeixinByBus(Visitor vt,String type,String permission);
	
	public String sendAppReplyByDD(UserInfo userinfo, Employee emp, Visitor vt, int type);

	/**
	 * 短信通知访客已完成签到
	 * @param userinfo
	 * @param emp 被通知人员
	 * @param vt 签到数据
	 * @return
	 */
	public String sendSMS(UserInfo userinfo,Employee emp,Visitor vt);

	/**
	 * 通过短信发送邀请函
	 * @param userinfo
	 * @param emp 邀请人
	 * @param vt
	 * @return
	 */
	public String sendAppointmentSMS(UserInfo userinfo,Employee emp,Visitor vt);

	public String sendAppointmentMeetingSMS(UserInfo userinfo,Visitor vt,Integer type);

	public String sendAppointmentReplySMS(UserInfo userinfo,Employee emp,Visitor vt,int status);
	
	public List<RespVisitor>  searchVisitByCondition(Map<String,Object> conditions );
	
	public String privateSendWeiXin(String vname,String visitType,String vphone,String visitDate,String openid,String photo);
	
	public int getVisitorCount(int userid);
	
	public List<RespVisitor>  getVisitorListByEmpid(int empid);
	
	public int batchSingin(List<Visitor> vlist);

	public String sendIvrSMS(UserInfo userinfo, Employee emp, Visitor vt);
	
	public int addVisitorApponintmnet(Visitor vt);
	
	public List<RespVisitor> getVisitorAppointmentByPhone(Map<String,Object> conditions);

	public List<RespVisitor> getVisitorAppointmentList(Visitor vt);
	
	public String sendInviteSMS(UserInfo userinfo, Visitor vt,Employee emp); 
	
	public String sendInviteReplySMS(UserInfo userinfo, Visitor vt,String empName,String type); 
	
	public int  addApponintmnetVisitor(Visitor vt);
	
	public int  updateVisitorAppointment(Visitor vt);
	
	public int  updateSignOut(Visitor vt);
	
	public List<RespVisitor>  checkSignOutRecords(Visitor vt);
	
	public List<RespVisitor> getVistorAppListByEmpPhone(RespVisitor vt);
	
	public List<RespVisitor>  getTempList(Map<String,String> conditions );
	
	public List<RespVisitor>  getAppointmentListByEmpPhone(RespVisitor vt);
	
	public String sendVerifyCodeByWeixin(UserInfo userinfo, Visitor vt,Person p);
	
	public String sendRefuseVisitByWeixin(ProcessRecord pr, Visitor vt,Person p);
	
	public int  updateSigninByAppClient(Visitor vt);
	
	public String sendAppoinmentNotifyByWeixin(UserInfo userinfo,Visitor vt,Employee emp,Person visitor,int proxy); 
	
	public List<RespVisitor> getAppointmentListByVPhone(RespVisitor vt);
	
	public List<RespVisitor> searchVisitForApp(Map<String,String> conditions );
	
	public Visitor getTodayVisitorById(int vid);
	
	public List<RespVisitor> getAppointmentListByEmpId(RespVisitor vt);
	
	public List<RespVisitor> getVisitorAppointmentByVname(Map<String,Object> conditions);
	
	public String sendVNet(UserInfo userinfo,Visitor vt);
	
	public int updateVisitRemark(Visitor vt);
	
	public int addGroupVisitor(List<Visitor> vlist);
	
	public int updateGroupPermission(Visitor vt);
	
	public List<Visitor> getGroupVistorList(int vid);
	
	public Visitor getTodayVisitorByPhone(String vphone,int userid);
	
	public int  updateSignOutByVid(Visitor vt);
	
	public List<Visitor> getLastestVisitor(Map<String,Object> map);
	
	public List<RespVisitor> searchAppByCondition(Map<String,String> map);
	
	public int  batchSignOut(List<Visitor> vlist);
	
	public String  sendVisitorKeyByWX(Visitor vt,Person p);
	
	public  boolean sendVisitorKey(Person p, List<Equipment> elist,Visitor vt,int time);
	
	public PushResult  sendJpushNotify(Visitor vt,String content,String type);
	
	public int  updateVisitInfo(Visitor vt);
	
	public Visitor getVisitorByCardID(String cardId,int userid);
	
	public List<RespVisitor> SearchRVisitorByCondition(Map<String,String> map);
	
	public Visitor getTodayAppointmentByPhone(Visitor vt);
	
	public String sendVoiceSMS(UserInfo userinfo,Employee emp, Visitor vt);
	
	public String sendCancelSMS(UserInfo userinfo, Employee emp, Visitor vt);
	
	public String sendSmsReply(Map<String, String> params,UserInfo userinfo,int count);
	
	public String sendSmsCodeByYiMei(Map<String, String> params,UserInfo userinfo);
	
	public String sendNotifyByWXBus(Employee emp,Visitor vt); 
	
	public String sendNotifyByDD(Employee emp,Visitor vt); 
	
	public String sendAppoinmentNotifyByWXBus(Employee emp,Visitor vt,int proxy); 
	
	public String sendAppoinmentNotifyByDD(Employee emp,Visitor vt,int proxy); 
	
	public String sendAppointmentReplyWXBus(Employee emp,Visitor vt, int type);
	
	public int  batchUpdateVisitorAppointment(Visitor vt);
	
	public String sendAppointmentReplyWX(Employee emp,Visitor vt, int type);
	
	public List<RespVisitor> getVistorProxyListByEmpPhone(RespVisitor vt);
	
	public int  updateSignPdf(Visitor vt);
	
	public int  updateLeaveTime(Visitor vt);

	int checkTrack(String key);

    void checkTrackTimeout(String key);

    public void sendLeaveMessage(String key);
	
	public Visitor getVisitorByAppId(Visitor vt);
	
	public List<RespVisitor>  getExamVisitList(Map<String,Object> conditions);
	
	public Visitor getVisitorByPlateNum(Visitor vt);

	public String sendSmsToResidentVisitor(UserInfo userinfo,ResidentVisitor rv);
	
	public void sendAutoNotifyMessage(String key);
	
	public String sendDefaultNotifyByWeixin(Visitor vt,Employee emp);

    public List<Visitor> getVisitorByexpireDate(int userid,String expireDate);

	int deleteVisitByVids(List<Integer> expireVid);

    List<VisitorChart> getSignInVisitorByDept(RequestVisit visit);
    
    public int updateCardNo(Visitor vt);

    List<VisitorChart> getArrivedVisitorChart(RequestVisit requestVisit);
    
    List<VisitorChart> getAllArrivedVisitorChart(RequestVisit requestVisit);

    List<VisitorChart> getAllArrivedVisitorChartSmart(RequestVisit requestVisit);

	public VisitorChart getNoArrivedVCount(RequestVisit requestVisit);
	
	public List<VisitorChart> getNoArrivedLineChart(RequestVisit requestVisit);

    public List<VisitorRecord> newSearchVisitorByCondition1(Map<String, Object> map);

    
    public VisitorChart getVisitSaCountByVphone(RequestVisit requestVisit);
    
    public int updateVisitorExtendCol(Visitor vt);

    List<VisitorChart> getArrivedVCount(RequestVisit rv);

	String sendCancelVisitByWeixin(Visitor v,Person visitor);

	String sendArrivedWXNotify(Employee employee, Visitor vt);

	List<Visitor> getVListBySubAccountId(int empid,int userid);
	
	public List<RespVisitor> searchVisitByConditionPage(RequestVisit rv);
	    
    public List<RespVisitor> searchAppByConditionPage(RequestVisit requestVisit);
	    
	public List<RespVisitor>  SearchRVisitorByConditionPage(RequestVisit requestVisit);
	
    public DataStatistics getSignedCount(RequestVisit rv);
    
    public DataStatistics getAppointmentCount(RequestVisit rv);
    
    public DataStatistics getRvSignedCount(RequestVisit rv);
    
    public List<VisitorChart>  getAllVisitorLineChart(RequestVisit rv);

    List<VisitorChart> getDeptVisitByAppDate(RequestVisit requestVisit);

    String sendDMettingAppointmentSMS(UserInfo userInfo, Visitor vt);

	public String sendTextNotifyByWXBus(Employee emp, String msg);

	/**
	 * 企业微信消息通知访客已完成签到
	 * @param emp 被通知人
	 * @param vt
	 * @return
	 */
	public String sendMarkdownNotifyByWXBus(Employee emp, Visitor vt);

	public String sendProcessMarkdownNotifyByWXBus(Employee candidateUser, List<Appointment> appointmentList);

	public String sendProcessMarkdownNotifyBydingtalk(Employee employee, List<Appointment> appointmentList,UserInfo userInfo);

	public String sendProcessMarkdownFinishNotifyByWXBus(Employee toEmployee, ProcessRecord pr);

    List<VisitorRecord> newSearchVisitorByCondition1Page(ReqVisitorRecord rv);

	void sendProcessNotify(Object ... objects);

	void sendProcessFinishNotify(Object ... objects);
	
	public String sendTextNotifyByFeiShu(Employee emp, String msg);
	 
	public String sendAppReplyByFeiShu(UserInfo userinfo, Employee emp, Visitor vt, int type);
	
	public String sendNotifyByFeiShu(Employee emp, Visitor vt);
	
	public String sendAppoinmentNotifyByFeiShu(Employee emp, Visitor vt,int proxy);
	
	public int finishQuestionnaire(Visitor v);
	
	 List<Visitor> searchLeaveTimeVisitByCondition(Map<String, Object> reqMap);

    List<Visitor> getScoreChartByVisitor(RequestVisit requestVisit);

	public String sendCancelSupplementSMS(UserInfo userinfo,Employee emp,Visitor vt);

	/**
	 * 更新补填信息
	 * @param visitor
	 */
	public void updateSupplementTask(Visitor visitor);

	public void updateStatusByVidTask(Visitor visitor);

	/**
	 * 获取访客默认授权门禁
	 * 权限获取顺序：被访人访客门禁权限；被访人所在企业访客门禁权限；对应门岗（gids）所有访客类型的门禁
	 * @param userinfo
	 * @param gids 授权的门岗
	 * @param empid
	 * @return access
	 */
    String getDefaultAccess(UserInfo userinfo, String gids, int empid);

    void firstPermissionSubBPM(Visitor visitor, UserInfo userinfo);

	void supplementSubBPM(Visitor visitor, UserInfo userinfo);

	void supplementPermissonSubBPM(Visitor visitor, UserInfo userinfo);

	void completeSupplementTask(Visitor visitor);

	void approvalSubBPM(Visitor visitor,boolean group);

    int supplementTypeRouter(Visitor visitor);

	/**
	 * 通过vgroup获取访客信息
	 * @param vGroup
	 * @return
	 */
	public List<Visitor> getVisitorByVgroup(String vGroup);

    List<RespVisitor> searchVisitors(RequestVisit rv);

	int batchUpdateExtendCol(List<Visitor> atlist);

	/**
	 * 更新状态
	 * @param vt
	 * @return
	 */
	public int VisitorReply(Visitor vt);
}
