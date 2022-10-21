package com.web.service.impl;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.web.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.web.service.AppointmentService;
import com.web.service.EmployeeService;
import com.web.service.IvrService;
import com.web.service.UserService;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;

@Service("ivrService")
public class IvrServiceImpl implements IvrService {
	
	protected static final Logger LOG = LoggerFactory.getLogger(IvrServiceImpl.class);

	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private VisitorService visitorService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmployeeService employeeService;

	private static final String REDIS_HASH_FIELD_VID = "vid";
	private static final String REDIS_HASH_FIELD_TIME= "time";
	private static final String REDIS_HASH_FIELD_ALIAS = "alias";
	private static final String REDIS_HASH_FIELD_ENAME = "ename";
	private static final String REDIS_HASH_FIELD_VNAME = "vname";
	private static final String REDIS_HASH_FIELD_PHONE = "phone";
	private static final String REDIS_HASH_FIELD_STATUS = "status";
	private static final String REDIS_HASH_FIELD_TYPE = "type";
	
	public static enum IVR_PROC_STATUS
	{
		IVR_PROC_STATUS_INIT,
		IVR_PROC_STATUS_PENDING,
		IVR_PROC_STATUS_FINISH
	}
	
	private RedisTemplate redisTemplate;
	
	private ScheduledExecutorService service = null;

	@Inject
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	@Override
	public RespInfo ivrCall(IvrData ivrData) {
		Visitor  vt  = null;
		if(ivrData.getVid() == null)
		{
			return new RespInfo(90, "no such visit");
		}
		String visitType = ivrData.getVid().substring(0, 1);
		if(ivrData.getIvrType() == null)
		{
			ivrData.setIvrType(IvrData.IVR_TYPE_CONFIRM);
		}
		if(visitType.compareToIgnoreCase(IvrData.APPOINTMENT_VISIT_TYPE) == 0)
		{
			Appointment am = appointmentService.getAppointmentbyId(Integer.parseInt(ivrData.getVid().substring(1)));
			if(am != null)
			{
				vt = new Visitor();
			    vt.setVid(am.getId());
				vt.setEmpid(am.getEmpid());
				vt.setEmpName(am.getEmpName());
				vt.setUserid(am.getUserid());
				vt.setVphoto(am.getPhotoUrl());
				vt.setVname(am.getName());
				vt.setVisitdate(new Date());
				vt.setVphone(am.getPhone());
	 			vt.setVisitType(am.getVisitType());
			}			
		}
		else 
		{
			vt = visitorService.getVisitorById(Integer.parseInt(ivrData.getVid().substring(1)));
		}
		if (vt == null) {
			return new RespInfo(90, "no such visit");
		}

		Employee emp = employeeService.getEmployee(vt.getEmpid());

		String phone = emp.getEmpPhone();

		HashMap<String, Object> result = null;

		CCPRestSDK restAPI = new CCPRestSDK();
/*		restAPI.init("sandboxapp.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8a48b5515350d1e2015378288a0c3f3e", "2dbceb08f9a14c0694cc31b77f86e077");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("aaf98f895376c1960153782f9f09013a");// 初始化应用ID
*/		
		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8aaf070857418a5801574621efea0306", "4ef1e2e0c8824a5b98b27ea0f4ff44d6");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8a216da85741a1b901574fdf92fc09f1");// 初始化应用ID
		result = restAPI.ivrDial(phone, ivrData.getVid(), false);// true是录音

		System.out.println("SDKTestIvrDial result=" + result);

		if ("000000".equals(result.get("statusCode"))) {
			// 正常返回输出data包体信息（map）
			HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
			for (Map.Entry<String, Object> value : data.entrySet()) {
				System.out.println(value.getKey() + " = " + value.getValue());
			}

			String callid = (String) data.get("callSid");
			HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
			hashOperations.put(callid, REDIS_HASH_FIELD_VID,  ivrData.getVid());
			hashOperations.put(callid, REDIS_HASH_FIELD_TIME, new Date());
			hashOperations.put(callid, REDIS_HASH_FIELD_PHONE, vt.getVphone());
			hashOperations.put(callid, REDIS_HASH_FIELD_ENAME, vt.getEmpName());
			hashOperations.put(callid, REDIS_HASH_FIELD_VNAME, vt.getVname());
			hashOperations.put(callid, REDIS_HASH_FIELD_ALIAS, ivrData.getAlias());
			hashOperations.put(callid, REDIS_HASH_FIELD_STATUS, IVR_PROC_STATUS.IVR_PROC_STATUS_INIT);
			hashOperations.put(callid, REDIS_HASH_FIELD_TYPE, ivrData.getIvrType());
			try
			{
				service = Executors.newScheduledThreadPool(1);
				service.schedule(new IvrTimeoutProc(callid), 2, TimeUnit.MINUTES);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			//BoundValueOperations<String, Integer> countVal = redisTemplate.boundValueOps(callid); 
			return new RespInfo(0, "success");
		} else {
			// 异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));

			return new RespInfo(91, "ivr call failure",
					result.get("statusCode") + ":" + (String) result.get("statusMsg"));
		}
	}
	
	class IvrTimeoutProc implements Runnable {
		private String callid;
		
		
		public IvrTimeoutProc(String callid) {
			this.callid = callid;
		}


		@Override
		public void run() {
			try {
				HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
				
				String vid = (String)hashOperations.get(callid, REDIS_HASH_FIELD_VID);
				if(vid != null)
				{
					IVR_PROC_STATUS status = (IVR_PROC_STATUS)hashOperations.get(callid, REDIS_HASH_FIELD_STATUS);
					if(status == IVR_PROC_STATUS.IVR_PROC_STATUS_INIT)
					{
						sendsmsnotify(vid);
						
						hashOperations.delete(callid, REDIS_HASH_FIELD_VID);
						hashOperations.delete(callid, REDIS_HASH_FIELD_TIME);
						hashOperations.delete(callid, REDIS_HASH_FIELD_PHONE);
						hashOperations.delete(callid, REDIS_HASH_FIELD_VNAME);
						hashOperations.delete(callid, REDIS_HASH_FIELD_ENAME);
						hashOperations.delete(callid, REDIS_HASH_FIELD_ALIAS);
						hashOperations.delete(callid, REDIS_HASH_FIELD_STATUS);
						hashOperations.delete(callid, REDIS_HASH_FIELD_TYPE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public String ivrStartService(String callid) {	
		HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
		
		String employer_name = (String)hashOperations.get(callid, REDIS_HASH_FIELD_ENAME);
		String visitor_name = (String)hashOperations.get(callid, REDIS_HASH_FIELD_VNAME);
		String type = (String)hashOperations.get(callid, REDIS_HASH_FIELD_TYPE);
		String vid = (String)hashOperations.get(callid,  REDIS_HASH_FIELD_VID);
		if(vid != null)
			hashOperations.put(callid, REDIS_HASH_FIELD_STATUS, IVR_PROC_STATUS.IVR_PROC_STATUS_PENDING);
		
		String result = "<?xml version='1.0' encoding='UTF-8'?>";
		if(type != null && type.compareToIgnoreCase(IvrData.IVR_TYPE_NOTIFY) == 0)
		{
			result += "<Response><PlayTTS loop='3'>";
			if(employer_name != null)
				result += employer_name;
			result += "您好，";
			if(visitor_name != null)
				result += visitor_name;
			else
				result += "有人";

			result +=  "已在前台等候，请接待。谢谢</PlayTTS><Hangup/></Response>";
		}
		else
		{
			result += "<Response><Get action='dtmfreport' numdigits='1' timeout='20'><PlayTTS loop='-1'>";
			if(employer_name != null)
				result += employer_name;
			result += "您好，";
			if(visitor_name != null)
				result += visitor_name;
			else
				result += "有人";
			result +=  "已在前台等候，选择接待请按1，选择暂不接待请按2";
			result += "</PlayTTS></Get><Redirect>gettimeout</Redirect></Response>";
		}
		return result;
	}
	
	public void sendsmsnotify(String vid) 
	{
		Visitor vt = null;
		String visitType = vid.substring(0, 1);
		if(visitType.compareToIgnoreCase(IvrData.NORMAL_VISIT_TYPE) == 0)
		{
			vt = visitorService.getVisitorById(Integer.parseInt(vid.substring(1)));
		}
		else if(visitType.compareToIgnoreCase(IvrData.APPOINTMENT_VISIT_TYPE) == 0)
		{
			Appointment am = appointmentService.getAppointmentbyId(Integer.parseInt(vid.substring(1)));
			if(am != null)
			{
				vt = new Visitor();
			    vt.setVid(am.getId());
				vt.setEmpid(am.getEmpid());
				vt.setEmpName(am.getEmpName());
				vt.setUserid(am.getUserid());
				vt.setVphoto(am.getPhotoUrl());
				vt.setVname(am.getName());
				vt.setVisitdate(new Date());
				vt.setVphone(am.getPhone());
	 			vt.setVisitType(am.getVisitType());
			}			
		}
		
		if(vt != null)
		{
			Employee emp = employeeService.getEmployee(vt.getEmpid());
			UserInfo userinfo=userService.getUserInfoByUserId(emp.getUserid());
			visitorService.sendIvrSMS(userinfo, emp, vt);
		}
	}

	
	public String ivrGetTimeout(String callid) {
		HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
		String result = "<?xml version='1.0' encoding='UTF-8' ?><Response><PlayTTS>感谢您的使用。</PlayTTS><Hangup/></Response>";

		return result;
	}
		
	public String ivrDtmfReport(String callid, String digits) {
		HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
		
		String vid = (String)hashOperations.get(callid, REDIS_HASH_FIELD_VID);
		String alias = (String)hashOperations.get(callid, REDIS_HASH_FIELD_ALIAS);
//		String phone = (String)hashOperations.get(callid, REDIS_HASH_FIELD_PHONE);

		
		//对云通讯平台回调请求的响应包体，响应的是放音提示后挂断用户电话
		if(digits.compareToIgnoreCase("1") == 0)
		{
			hashOperations.put(callid, REDIS_HASH_FIELD_STATUS, IVR_PROC_STATUS.IVR_PROC_STATUS_FINISH);
			if(alias != null)
				sendnotify(alias, "vid:"+vid);
			String result = "<?xml version='1.0' encoding='UTF-8' ?><Response><PlayTTS>同意接待消息已发送成功，祝您会面愉快。</PlayTTS><Hangup/></Response>";
	
			return result;
		}
		else
		{
			String result = "<?xml version='1.0' encoding='UTF-8' ?><Response><PlayTTS>感谢您的使用。</PlayTTS><Hangup/></Response>";
			sendsmsnotify(vid);
			return result;			
		}
	}
	
	public String ivrStopservice(String callid) {
		HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
		
		hashOperations.delete(callid, REDIS_HASH_FIELD_VID);
		hashOperations.delete(callid, REDIS_HASH_FIELD_TIME);
		hashOperations.delete(callid, REDIS_HASH_FIELD_PHONE);
		hashOperations.delete(callid, REDIS_HASH_FIELD_VNAME);
		hashOperations.delete(callid, REDIS_HASH_FIELD_ENAME);
		hashOperations.delete(callid, REDIS_HASH_FIELD_ALIAS);
		hashOperations.delete(callid, REDIS_HASH_FIELD_STATUS);
		hashOperations.delete(callid, REDIS_HASH_FIELD_TYPE);
		String result = "<?xml version='1.0' encoding='UTF-8' ?><Response><CmdNone/></Response>";
		return result;
	}
	
	private final static String JPUSH_MASTER_SECRET = "8fac55a17e6c3643411dd934";
	private final static String JPUSH_APP_KEY = "61466f6a310571060af61a13";
	
	
	public class SendPushNotify implements Runnable {

		private String alias;
		private String alert;

		SendPushNotify(String alias, String alert) {
			this.alias = alias;
			this.alert = alert;
		}

		public void run() {

			// HttpProxy proxy = new HttpProxy("localhost", 3128);
			// Can use this https proxy:
			// https://github.com/Exa-Networks/exaproxy
			JPushClient jpushClient = new JPushClient(JPUSH_MASTER_SECRET, JPUSH_APP_KEY, 3);

			// For push, all you need do is to build PushPayload object.
			/*
			 * PushPayload payload = PushPayload.newBuilder()
			 * .setPlatform(Platform.all()) .setAudience(Audience.alias(alias))
			 * .setNotification(Notification.alert(alert)) .build();
			 */

			PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all())
					.setAudience(Audience.alias(alias)).setMessage(Message.newBuilder().setMsgContent(alert).build())
					.build();

			try {
				PushResult result = jpushClient.sendPush(payload);
				LOG.info("Got result - " + result);
			} catch (APIConnectionException e) {
				LOG.error("Connection error. Should retry later. ", e);
			} catch (APIRequestException e) {
				LOG.error("Error response from JPush server. Should review and fix it. ", e);
				LOG.info("HTTP Status: " + e.getStatus());
				LOG.info("Error Code: " + e.getErrorCode());
				LOG.info("Error Message: " + e.getErrorMessage());
				LOG.info("Msg ID: " + e.getMsgId());
			}
		}

	}
	
	public void sendnotify(String alias, String alert)
	{
		Thread task = new Thread(new SendPushNotify(alias, alert));
		task.start();
	}

	@Override
	public String redistest(String test) {
		// TODO Auto-generated method stub
//		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//		valueOperations.set(test, test+"123123");
//		redisTemplate.expire(test, 24, TimeUnit.HOURS);
		
	
		List<String> wlist=new ArrayList<String>();
		wlist.add("a");
		wlist.add("b");
		wlist.add("c");
		
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		redisTemplate.delete(test);
		listOperations.rightPushAll(test, wlist);
		redisTemplate.expire(test, 15, TimeUnit.MINUTES);
//		
//		
		List<String> ss=new ArrayList<String>();
		Long size = listOperations.size(test);
		ss=listOperations.range(test, 0, size);
		
		return ss.toString();
		}

}
