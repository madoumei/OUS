package com.web.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import camundajar.impl.scala.App;
import com.annotation.ProcessLogger;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.*;
import com.client.dao.EquipmentDao;
import com.client.service.EquipmentGroupService;
import com.client.service.ExtendVisitorService;
import com.client.service.PassService;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.config.qicool.common.utils.StringUtils;
import com.event.event.NotifyEvent;
import com.event.event.PassEvent;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.*;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.yimei.JsonHelper;
import com.web.bean.*;
import com.web.bean.SubAccountTemplate;
import com.web.dao.ConfigureDao;
import com.web.dao.UserDao;
import com.web.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.AppointmentDao;


@Service("appointmentService")
public class AppointmentServiceImpl extends ServiceImpl<AppointmentDao,Appointment> implements  AppointmentService{
	@Autowired
	private AppointmentDao appointmentDao;

	@Autowired
	private UserService userService;
	@Autowired
	private VisitorService visitorService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ConfigureDao configureDao;
	@Autowired
	private OperateLogService operateLogService;

	@Autowired
	private SubAccountService subAccountService;

	@Autowired
	private ExtendVisitorService extendVisitorService;

	@Autowired
	private EquipmentGroupService equipmentGroupService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private PassService passService;

	@Autowired
	private CamundaProcessService camundaProcessService;


	/**
	 * 补填子流程，由授权触发
	 * @param appointment
	 */
	@ProcessLogger("补填子流程")
	@Override
	public void supplementSubBPM(Appointment appointment, UserInfo userinfo){
		int supplementType = visitorService.supplementTypeRouter(BeanUtils.appointmentToVisitor(appointment));
		if(supplementType == 2){
			//补填并必填，发邀请
			List<Appointment> vlist = getAppointmnetByAgroup(appointment);
			for(Appointment app:vlist){
				app.setStatus(6);
				updateAppointmentStatusByIdTask(app);
				messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(app),NotifyEvent.EVENTTYPE_SEND_INVITE);
			}
		}else{
			if(supplementType == 1) {
				//可补填非必填
				List<Appointment> vlist = getAppointmnetByAgroup(appointment);
				for(Appointment app:vlist){
					app.setStatus(6);
					updateAppointmentStatusByIdTask(app);
				}
			}
			//非必填，直接进入审批流程
			approvalSubBPM(appointment,true);

		}

	}

	/**
	 * 审批子流程
	 * @param appointment
     * @param group 是否为批处理
	 */
	@ProcessLogger("邀请审批子流程流程")
	@Override
	public void approvalSubBPM(Appointment appointment,boolean group) {
        UserInfo userinfo = userDao.getUserInfo(appointment.getUserid());
        if (group) {
            if (userinfo.getProcessSwitch() == 0) {
                //不要审批，自动完成审批
                List<Appointment> vlist = getAppointmnetByAgroup(appointment);
                for (Appointment app : vlist) {
                    app.setPermission(1);
                    updateAppointmentPermission(app);
                }
            } else {

				List<String> allVisitors = new ArrayList<>();
                List<Appointment> vlist = getAppointmnetByAgroup(appointment);
                for (Appointment app : vlist) {
                	if(app.getPermission() == 0) {
                    app.setPermission(4);
                    updateAppointmentPermission(app);
						allVisitors.add(app.getId() + "");
                }

				}
//
//				Employee employee = employeeService.getEmployee(appointment.getEmpid());
//				Camunda camunda = new Camunda();
//				camunda.setTitle("访客邀请审批");
//				camunda.setBusinessKey("a"+appointment.getAgroup());
//				//查找领导
//				camunda.setLeader(appointment.getExtendValue("leader"));
//				camunda.setAllVisitors(allVisitors);
//				camunda.setAppointmentDate(vlist.get(0).getAppointmentDate());
//				camunda.setQrcodeConf(vlist.get(0).getQrcodeConf());
//				String endDate = vlist.get(0).getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
//				if(org.apache.commons.lang.StringUtils.isNotEmpty(endDate)) {
//					try {
//						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//						format.setTimeZone(TimeZone.getTimeZone("gmt"));
//						Date date = format.parse(endDate);
//						camunda.setEndDate(date);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//				int ret = camundaProcessService.createApproveProcess(userinfo,employee,camunda);
//				if(ret != 0) {
//					throw new ErrorException(ErrorEnum.getByCode(ret));
//				}
            }
        } else {
            if (userinfo.getProcessSwitch() == 0) {
                //不要审批，自动完成审批
                appointment.setPermission(1);
                updateAppointmentPermission(appointment);
            } else {
                SysLog.info("等待被访人提交审批,vid：" + appointment.getId());
                appointment.setPermission(4);
                updateAppointmentPermission(appointment);

//				Employee employee = employeeService.getEmployee(appointment.getEmpid());
//				appointment = appointmentDao.getAppointmentbyId(appointment.getId());
//				Camunda camunda = new Camunda();
//				camunda.setTitle("访客邀请审批");
//				camunda.setBusinessKey("a"+appointment.getAgroup());
//				//查找领导
//				camunda.setLeader(appointment.getExtendValue("leader"));
//				List<String> allVisitors = new ArrayList<>();
//				allVisitors.add(appointment.getId()+"");
//				camunda.setAllVisitors(allVisitors);
//				camunda.setAppointmentDate(appointment.getAppointmentDate());
//				camunda.setQrcodeConf(appointment.getQrcodeConf());
//				String endDate = appointment.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
//				if(org.apache.commons.lang.StringUtils.isNotEmpty(endDate)) {
//					try {
//						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//						format.setTimeZone(TimeZone.getTimeZone("gmt"));
//						Date date = format.parse(endDate);
//						camunda.setEndDate(date);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//				int ret = camundaProcessService.createApproveProcess(userinfo,employee,camunda);
//				if(ret != 0) {
//					throw new ErrorException(ErrorEnum.getByCode(ret));
//				}
            }
        }
    }

	/**
	 * 补填授权子流程
	 * @param appointment
	 * @param userinfo
	 */
	@Override
	public void supplementPermissonSubBPM(Appointment appointment, UserInfo userinfo){
		if ("1".equalsIgnoreCase(userinfo.getAppointmenProcessSwitch())) {
			//需要二次授权

			//通知被访人
			messageService.sendSupplementPermissionNotifyEvent(BeanUtils.appointmentToVisitor(appointment));
		} else {
			//不需要二次授权,进入审批流程
			approvalSubBPM(appointment,false);
		}
	}

	/**
	 * 补填授权，1：同意，2：拒绝
	 * @param appointment
	 */
	@Override
	public void completeSupplementTask(Appointment appointment){
		int ret = updateAppointmentPermission(appointment);
		if(ret == 0){
			throw new ErrorException(ErrorEnum.E_057);
		}
	}


	/**
	 * 产生一条签到记录
	 * @param vtParam
	 * @param userinfo
	 * @return
	 */
	@Override
	public Visitor addSigninTask(Visitor vtParam,UserInfo userinfo){

		Visitor vt = new Visitor();//签到数据

		if(vtParam.getSigninType() == 1) {
			Appointment at = BeanUtils.VisitorToAppointment(vtParam);
			vt = addAppSigninTask(at,userinfo);
		}else{
			//预约签到
			vt = visitorService.getVisitorById(vtParam.getVid());
			vt.setVisitdate(new Date());
			visitorService.updateVisitorAppointment(vt);
		}

		return vt;
	}

	/**
	 * 产生邀请签到记录
	 * @param at
	 * @param userinfo
	 * @return
	 */
	public Visitor addAppSigninTask(Appointment at,UserInfo userinfo) {
		String phone = at.getPhone();
		String cardId = at.getCardId();
		String vemail = at.getVemail();
		String company = "";
		int i = 0;
		int aid = at.getId();

		Visitor vt = new Visitor();//签到数据
		Appointment ap = null;

		if (aid > 0) {
			ap = getAppointmentbyId(aid);
		} else {
			if (null != phone && !"".equals(phone)) {
				Appointment searchApp = new Appointment();
				searchApp.setUserid(at.getUserid());
				searchApp.setPhone(at.getPhotoUrl());
				searchApp.setSubaccountId(at.getSubaccountId());
				ap = getAppointmentByWPhone(searchApp);
			}
		}

		ap.setPhotoUrl(at.getPhotoUrl());
		ap.setVisitDate(new Date());
		ap.setCardId(cardId);
		ap.setRemark(at.getRemark());
		ap.setPlateNum(at.getPlateNum());

		if (ap.getSubaccountId() != 0) {
			i = updateSaAppointmentStatus(ap);
		} else {
			i = updateAppointmentStatus(ap);
		}

		if (i <= 0) {
			SysLog.error("updateAppointmentStatus failed:", ap);
			throw new ErrorException(ErrorEnum.E_063);
		}

		Employee emp = employeeService.getOpenid(ap.getEmpid());
		if (null == emp) {
			SysLog.error("no employee about empid:", ap.getEmpid());
			throw new ErrorException(ErrorEnum.E_001);
		}

		if (userinfo.getSubAccount() == 0 || ap.getSubaccountId() == 0) {
			company = userinfo.getCompany();
		} else {
			SubAccount sa = subAccountService.getSubAccountById(ap.getSubaccountId());
			company = sa.getCompanyName();
		}


		vt.setEmpid(ap.getEmpid());
		vt.setEmpdeptid(ap.getEmpdeptid());
		vt.setEmpName(emp.getEmpName());
		vt.setUserid(emp.getUserid());
		vt.setVphoto(at.getPhotoUrl());
		vt.setVname(ap.getName());
		vt.setVisitdate(new Date());
		vt.setVphone(ap.getPhone());
		vt.setVisitType(ap.getVisitType());
		vt.setEmpPhone(emp.getEmpPhone());
		//邀请时间不变
		vt.setAppointmentDate(ap.getAppointmentDate());

		vt.setPermission(1);
		vt.setSigninType(1);
		vt.setCardId(cardId);
		vt.setPeopleCount(1);
		vt.setRemark(ap.getRemark());
		vt.setVcompany(ap.getVcompany());
		vt.setSubaccountId(ap.getSubaccountId());
		vt.setCompany(company);
		vt.setSignInGate(at.getSignInGate());
		vt.setSignInOpName(at.getSignInOpName());
		vt.setvType(ap.getvType());
		vt.setAppid(ap.getId());
		vt.setTid(ap.getTid());
		vt.setGid(ap.getGid() + "");
		vt.setPlateNum(at.getPlateNum());
		vt.setSex(at.getSex());
		vt.setClientNo(ap.getClientNo());
		vt.setQrcodeConf(ap.getQrcodeConf());
		if (null != vemail && !"".equals(vemail)) {
			vt.setVemail(vemail);
		} else {
			vt.setVemail(ap.getVemail());
		}

		String meetingPoint = ap.getExtendValue("meetAddress");
		String access = ap.getExtendValue("access");
		if(StringUtils.isEmpty(access)){
			access = visitorService.getDefaultAccess(userinfo, vt.getGid(), emp.getEmpid());
		}
		if (StringUtils.isNotEmpty(at.getAppExtendCol())) {
			vt.setExtendCol(at.getAppExtendCol());
		}
		vt.setMeetingPoint(meetingPoint);

		vt.addExtendValue("access", access);
		vt.addExtendValue("meetAddress", meetingPoint);
		List<String> extcol = at.getExtendCol();
		if (null != extcol && extcol.size() > 0) {
			for (int a = 0; a < extcol.size(); a++) {
				String[] col = extcol.get(a).split("=");
				if (col.length > 1) {
					vt.addExtendValue(col[0], col[1]);
				}
			}
		}

		visitorService.addApponintmnetVisitor(vt);

		return vt;
	}


	/**
	 * 检查邀请访客授权及有效期
	 * @param aid
	 * @return
	 */
	@Override
	public Appointment checkAppointmentPermissionTask(String aid) {
		Appointment app = getAppointmentbyId(Integer.parseInt(aid));
		if(app == null){
			throw new ErrorException(ErrorEnum.E_703);
		}
		Visitor vt = BeanUtils.appointmentToVisitor(app);
		UserInfo ui = userDao.getUserInfo(app.getUserid());
		visitorService.checkAppointmentVisitorLimitTask(vt,ui);
		return app;
	}

	@Override
	public int addUsertemplate(Usertemplate ut) {
		// TODO Auto-generated method stub
		return appointmentDao.addUsertemplate(ut);
	}

	@Override
	public Usertemplate getUsertemplate(Usertemplate ut) {
		// TODO Auto-generated method stub
		return appointmentDao.getUsertemplate(ut);
	}

	@Override
	public int updateUsertemplate(Usertemplate ut) {
		// TODO Auto-generated method stub
		return appointmentDao.updateUsertemplate(ut);
	}

	@Override
	public int updateOldUsertemplate(Usertemplate ut) {
		return appointmentDao.updateOldUsertemplate(ut);
	}

	@Override
	public Emptemplate getEmptemplate(Emptemplate et) {
		// TODO Auto-generated method stub
		return appointmentDao.getEmptemplate(et);
	}

	@Override
	public int updateEmptemplate(Emptemplate et) {
		// TODO Auto-generated method stub
		return appointmentDao.updateEmptemplate(et);
	}

	@Override
	public int addAppointment(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.addAppointment(at);
	}

	@Override
	public Appointment getAppointmentbyId(int id) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentbyId(id);
	}

	@Override
	public List<Appointment> getAppointmentByUserid(int userid) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentByUserid(userid);
	}

	@Override
	public List<Appointment> getAppointments(String email,int status) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointments(email,status);
	}

	@Override
	public int updateAppointmentStatus(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateAppointmentStatus(at);
	}

	@Override
	public int addEmptemplate(Emptemplate et) {
		// TODO Auto-generated method stub
		return appointmentDao.addEmptemplate(et);
	}

	@Override
	public List<Appointment> getAppointmentByPhone(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentByPhone(at);
	}

	@Override
	public int delUsertemplate(Usertemplate ut) {
		// TODO Auto-generated method stub
		return appointmentDao.delUsertemplate(ut);
	}

	@Override
	public Appointment getAppointmentByWPhone(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentByWPhone(at);
	}

	@Override
	public Appointment getAppointmentOldRecords(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentOldRecords(at);
	}

	@Override
	public List<String> getAppExpiredRecords(String userid) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppExpiredRecords(userid);
	}

	@Override
	public int updatePhotoUrl(int id,String photoUrl) {
		// TODO Auto-generated method stub
		return appointmentDao.updatePhotoUrl(id,photoUrl);
	}

	@Override
	public Appointment getAppointmentPhotoUrl(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentPhotoUrl(at);
	}

	@Override
	public int updateAppointmentSms(int id) {
		// TODO Auto-generated method stub
		return appointmentDao.updateAppointmentSms(id);
	}

	@Override
	public int addSAtemplate(SubAccountTemplate sat) {
		// TODO Auto-generated method stub
		return appointmentDao.addSAtemplate(sat);
	}

	@Override
	public int updateSAtemplate(SubAccountTemplate sat) {
		// TODO Auto-generated method stub
		return appointmentDao.updateSAtemplate(sat);
	}

	@Override
	public SubAccountTemplate getSAtemplate(int subaccountId,String templateType,int gid) {
		// TODO Auto-generated method stub
		return appointmentDao.getSAtemplate(subaccountId, templateType,gid);
	}

	@Override
	public int delSAtemplate(SubAccountTemplate sat) {
		// TODO Auto-generated method stub
		return appointmentDao.delSAtemplate(sat);
	}

	@Override
	public int updateSaAppointmentStatus(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateSaAppointmentStatus(at);
	}

	@Override
	public List<Usertemplate> getUsertemplateByUserid(int userid) {
		// TODO Auto-generated method stub
		return appointmentDao.getUsertemplateByUserid(userid);
	}

	@Override
	public Appointment getAppointmentByHash(int qrContent) {
		return appointmentDao.getAppointmentByHash(qrContent);
	}

	@Override
	public int AppointmentReply(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.AppointmentReply(at);
	}

	@Override
	public int updateAppSendStatus(int id) {
		// TODO Auto-generated method stub
		return appointmentDao.updateAppSendStatus(id);
	}

	@Override
	public List<Appointment> getAppointmentList(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentList(at);
	}

	@Override
	public List<Appointment> getAppointmentByMid(int mid) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmentByMid(mid);
	}

	//发送状态不影响业务流程
	@Deprecated
	@Override
	public int batchUpdateAppSendStatus(List<Appointment> atlist) {
		// TODO Auto-generated method stub
		return appointmentDao.batchUpdateAppSendStatus(atlist);
	}

	@Override
	public int updateMeetingAppStatus(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateMeetingAppStatus(at);
	}

	@Override
	public Appointment getMeetingAppointment(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getMeetingAppointment(at);
	}

	@Override
	public Appointment getAMeetingRecord(int mid) {
		// TODO Auto-generated method stub
		return appointmentDao.getAMeetingRecord(mid);
	}

	@Override
	public List<RespVisitor> searchInviteByCondition(
			Map<String, String> conditions) {
		// TODO Auto-generated method stub
		return appointmentDao.searchInviteByCondition(conditions);
	}

	@Override
	public int updateOpenCount(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateOpenCount(at);
	}

	@Override
	public int updateLeaveTime(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateLeaveTime(at);
	}

	@Override
	public List<Appointment> getAppointmnetByAgroup(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.getAppointmnetByAgroup(at);
	}

	@Override
	public int updateAppAreaInfo(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateAppAreaInfo(at);
	}

	@Override
	public List<Usertemplate> getUserTemplateType(Usertemplate ut) {
		// TODO Auto-generated method stub
		return appointmentDao.getUserTemplateType(ut);
	}

	@Override
	public int updateAppPreview(Appointment at) {
		// TODO Auto-generated method stub
		return appointmentDao.updateAppPreview(at);
	}

	@Override
	public List<Appointment> getAppointmentByexpireDate(int userid, String expireDate) {
		return appointmentDao.getAppointmentByexpireDate(userid,expireDate);
	}

	@Override
	public int deleteAppointmentByids(List<Integer> appointmentIdList) {
		return appointmentDao.deleteAppointmentByids(appointmentIdList);
	}

	@Override
	public int updateCardNo(Appointment app) {
		// TODO Auto-generated method stub
		return appointmentDao.updateCardNo(app);
	}

	@Override
	public List<RespVisitor> searchNotSendCardVisitList(Map<String, String> map) {
		return appointmentDao.searchNotSendCardVisitList(map);
	}

	@Override
	public int updateAppExtendCol(Appointment app) {
		// TODO Auto-generated method stub
		return appointmentDao.updateAppExtendCol(app);
	}

	@Override
	public List<Appointment> getAppointmentbyAcode(int parseInt) {
		return appointmentDao.getAppointmentbyAcode(parseInt);
	}

	@Override
	public List<RespVisitor> searchInviteByConditionPage(RequestVisit rv) {
		// TODO Auto-generated method stub
		return appointmentDao.searchInviteByConditionPage(rv);
	}

	@Override
	public DataStatistics getInviteCount(RequestVisit rv) {
		// TODO Auto-generated method stub
		return appointmentDao.getInviteCount(rv);
	}

	@Override
	public Usertemplate getOldUsertemplate(Usertemplate ut) {
		return appointmentDao.getOldUsertemplate(ut);
	}

	@Override
	public int updateAppointmentPermission(Appointment appointment) {
		int ret = appointmentDao.updateAppointmentPermission(appointment);
		if(ret == 1){
			Visitor vt = BeanUtils.appointmentToVisitor(appointment);
			if( appointment.getPermission() == 1){
				//发送通知
				int supplementType = visitorService.supplementTypeRouter(vt);
				if(supplementType ==2){
					//必填的情况，说明是先发邀请函在授权/审批,邀请函已发过，通知授权成功
					messageService.sendCommonNotifyEvent(vt, NotifyEvent.EVENTTYPE_ACCEPT_APPOINTMENT);
				}else{
					//非必填的情况是先审批再发邀请函
					messageService.sendCommonNotifyEvent(vt,NotifyEvent.EVENTTYPE_SEND_INVITE);
				}
				if( Constant.AccessWithoutSignin.equals("1")) {
					//允许未签到开门，授权后直接下发通行权限
					List<Appointment> appointmentList = new ArrayList<Appointment>();
					appointmentList.add(appointment);
					passService.passAuth(appointmentList, PassEvent.Pass_Add);
				}
			}else if(vt.getPermission() == 2){
				messageService.sendCommonNotifyEvent(vt, NotifyEvent.EVENTTYPE_REJECT_APPOINTMENT);
			}else if(vt.getPermission() == 3){
				int supplementType = visitorService.supplementTypeRouter(vt);
				messageService.sendCommonNotifyEvent(vt, NotifyEvent.EVENTTYPE_VISIT_CANCEL);
			}
		}
		return ret;
	}

	@Override
	public List<VisitorChart> getDeptAppVisitByAppDate(RequestVisit requestVisit) {
		return appointmentDao.getDeptAppVisitByAppDate(requestVisit);
	}

	@Override
	public List<Appointment> getAppointmentByMidOrder(RequestVisit requestVisit) {
		return this.appointmentDao.searchAppointmentByMidOrder(requestVisit);
	}

	@Override
	public int updateAppointmentJoinMeetingSign(RequestVisit requestVisit) {
		return this.appointmentDao.updateAppointmentJoinMeetingSign(requestVisit);
	}

	@Override
	public int updateAppointmentFace(RequestVisit requestVisit) {
		int result = this.appointmentDao.updateAppointmentFaceById(requestVisit);
		if (result >0 ){
			return 0;
		}else{
			return -1;
		}
	}

	@Override
	public int addReserveMeeting(Appointment appointment) {
		return this.appointmentDao.addAppointmentReserve(appointment);
	}

	@Override
	public List<Appointment> getReviewMeeting(RespVisitor visitor) {
		return this.appointmentDao.getReviewMeeting(visitor);
	}

	@Override
	public int updateReviewMeetingStatus(Appointment appointment) {
		return this.appointmentDao.updateAppointmentMeetingStatus(appointment);
	}

	@Override
	public Appointment getAppointmentByPhoneAndMid(Appointment appointment) {
		return this.appointmentDao.getAppointmentByPhoneAndMid(appointment);
	}

	@Override
	public void updateSupplementAppointmentTask(Appointment app) {
		if(appointmentDao.updateSupplementAppointment(app)==0){
			throw new ErrorException(ErrorEnum.E_057);
		}
	}

	@Override
	public int sendSmsPermissionNotice(int i, Appointment app) {
		if (1 == i){
			int empid = app.getEmpid();
			Employee employee = employeeService.getEmployee(empid);
			Visitor visitor = new Visitor();
			visitor.setVid(app.getId());
			visitor.setVname(app.getName());
			visitor.setAppointmentDate(app.getAppointmentDate());
			visitor.setVphone(app.getPhone());
			visitor.setVisitType(app.getVisitType());
			UserInfo userInfo = this.userService.getUserInfo(app.getUserid());
			this.visitorService.sendAppointmentSMS(userInfo,employee,visitor);
		}else if (2 == i){
			int empid = app.getEmpid();
			Employee employee = employeeService.getEmployee(empid);
			UserInfo userInfo = this.userService.getUserInfo(app.getUserid());
			Visitor visitor = new Visitor();
			visitor.setVid(app.getId());
			visitor.setVname(app.getName());
			visitor.setAppointmentDate(app.getAppointmentDate());
			visitor.setVphone(app.getPhone());
			visitor.setVisitType(app.getVisitType());
			this.visitorService.sendCancelSupplementSMS(userInfo,employee,visitor);
		}
		return 0;
	}

	@Override
	public int updateAppointmentVgroup(Appointment appointment) {
		return this.appointmentDao.updateAppointmentVgroup(appointment);
	}

	@Override
	public List<Appointment> getAppointmentByIdAndAgroup(int id) {
		return this.appointmentDao.getAppointmentByIdAndAgroup(id);
	}

	@Override
	public int updateSupplementAppointmentDateById(Appointment appointment) {
		return this.appointmentDao.updateSupplementAppointmentDateById(appointment);
	}
	
	@Override
	public int batchUpdateAppExtendCol(List<Appointment> atlist) {
		// TODO Auto-generated method stub
		return appointmentDao.batchUpdateAppExtendCol(atlist);
	}


	@Override
	public String sendAppoinmentNotify(Appointment appointment) {
		if (ObjectUtils.isEmpty(appointment)){
			return "-1";
		}
		Employee employee = employeeService.getEmployee(appointment.getEmpid());
		if (ObjectUtils.isEmpty(employee)){
			return "-1";
		}
		boolean signleNotify=false;
		UserInfo userInfo = userService.getUserInfo(appointment.getUserid());
		if (userInfo.getPermissionSwitch() == 1 && userInfo.getProcessSwitch() == 0 &&userInfo.getWxBusNotify()==1){
			if (StringUtils.isNotBlank(employee.getOpenid())){
				if(!signleNotify||userInfo.getNotifyType()==0) {
					String response = this.sendAppoinmentNotifyByWXBus(userInfo,appointment,employee);
					if ("0".equalsIgnoreCase(response)){
						signleNotify = true;
						return "0";
					}
				}
			}
		}
		if (userInfo.getProcessSwitch() == 0 && userInfo.getSmsNotify() == 1){
			if (StringUtils.isNotBlank(employee.getEmpPhone())) {
				if(!signleNotify||userInfo.getNotifyType()==0) {
					String response = this.sendInviteSMS(userInfo, appointment, employee);
					if ("0".equalsIgnoreCase(response)){
						return "0";
					}
				}
			}
		}
		return "0";
	}

	@Override
	public void updateAppointmentStatusByIdTask(Appointment appointment) {
		int ret = appointmentDao.updateAppointmentStatusById(appointment);
		if(ret==0){
			throw new ErrorException(ErrorEnum.E_057);
		}
	}

	/**
	 * 员工接收访客补充信息短信通知
	 *
	 * @param userInfo
	 * @param appointment
	 * @param employee
	 * @return
	 */
	private String sendInviteSMS(UserInfo userInfo, Appointment appointment, Employee employee) {

		SimpleDateFormat time = new SimpleDateFormat("MM月dd日 HH时mm分");

		Map<String, String> params = new HashMap<String, String>();

		params.put("message", "您好，" + appointment.getName() + "的" + time.format(appointment.getAppointmentDate()) + "的来访信息已经提交，请在访客系统中完成授权。");

		params.put("phone", employee.getEmpPhone());
		String response = UtilTools.sendSmsByYiMei(params, configureDao, userInfo);

		if ("0".equals(response)) {
			userInfo.setAppSmsCount(userInfo.getAppSmsCount() + 1);
			userDao.updateAppSmsCount(userInfo);
		}

		//日志
		OperateLog.addSMSLog(operateLogService,userInfo.getUserid(), response, 1,appointment.getPhone(),appointment.getName(),params.get("message"));

		return response;
	}

	/**
	 * 员工接收访客访客补充信息企业微信通知
	 *
	 * @param userInfo
	 * @param appointment
	 * @param emp
	 * @return
	 */
	private String sendAppoinmentNotifyByWXBus(UserInfo userInfo,Appointment appointment,Employee emp) {
		SimpleDateFormat time = new SimpleDateFormat("MM月dd日 HH时mm分");
		String msgUrl = Constant.FASTDFS_URL + "empClient/?path=myVisitor&pc_slide=true";

		ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Map<String, Object> reqParam = new HashMap<>();
		reqParam.put("touser", emp.getOpenid());
		reqParam.put("agentid", userInfo.getAgentid());
		reqParam.put("msgtype", "text");
		Map<String, Object> markdown = new HashMap<>();
		StringBuffer sb = new StringBuffer();
		sb.append(" 访客授权提醒:  ").append("\r\n")
				.append(" 事　项：访客授权  ").append("\r\n")
				.append(" 描述信息：" + "你好" + emp.getEmpName() + "，" + appointment.getName() + "的" + time.format(appointment.getAppointmentDate()) + "的来访信息已经提交，请在企业微信中授权访问。" + "  ").append("\r\n")
				.append(" 查看详情，请点击：<a href=\"" + msgUrl + "\">查看审批详情</a>");
		markdown.put("content", sb.toString());
		reqParam.put("text", markdown);

		CacheManager cm = CacheManager.getInstance();
		if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
			settoken(cm, userInfo);
		}
		String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
		try {
			String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
			String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
			System.out.println(response);
			JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
			String errcode = jsonNode.get("errcode").asText();
			if (null != jsonNode && "0".equals(errcode)) {
				System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
				return "0";
			} else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
				checkresult(cm, userInfo, response, url, reqParam);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "-1";
	}

	public static void settoken(CacheManager cm, UserInfo userInfo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String corpid = userInfo.getCorpid();
		String corpsecret = userInfo.getCorpsecret();
		String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;

		String jsonBody = HttpClientUtil.postJsonBodyOther(access_token_url, 1000, null, "UTF-8");
		try {
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
			// 如果请求成功
			JsonNode errcode = jsonNode.get("errcode");
			if (null != errcode && "0".equals(errcode.asText())) {
				cm.putToken("WeChartToken_" + userInfo.getUserid(), jsonNode.get("access_token").asText());
			} else {
				System.out.println(dateFormat.format(new Date()) + " 获取accessToken失败: " + jsonBody);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkresult(CacheManager cm, UserInfo userInfo, String result, String url, Map<String, Object> map) {
		ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
		try {
			JsonNode rootNode = mapper.readValue(result, JsonNode.class);
			if (!"".equals(rootNode.path("errcode").asText())) {
				String errcode = rootNode.path("errcode").asText();
				if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
					settoken(cm, userInfo);
					url = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
					result = HttpClientUtil.postJsonBodyOther(url, 5000, map, "utf-8");
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
