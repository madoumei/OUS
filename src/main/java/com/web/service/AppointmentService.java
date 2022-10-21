package com.web.service;

import java.util.List;
import java.util.Map;

import com.annotation.ProcessLogger;
import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.DataStatistics;
import com.client.bean.RequestVisit;
import com.client.bean.RespVisitor;
import com.client.bean.Visitor;
import com.web.bean.*;
import com.web.bean.SubAccountTemplate;

public interface AppointmentService extends IService<Appointment> {

    @ProcessLogger("补填子流程")
    void supplementSubBPM(Appointment appointment, UserInfo userinfo);

	@ProcessLogger("邀请审批子流程流程")
	void approvalSubBPM(Appointment appointment,boolean group);

	void supplementPermissonSubBPM(Appointment appointment, UserInfo userinfo);

	void completeSupplementTask(Appointment appointment);

	Visitor addSigninTask(Visitor vtParam, UserInfo userinfo);

    Visitor addAppSigninTask(Appointment at, UserInfo userinfo);

    Appointment checkAppointmentPermissionTask(String aid);

    public int addUsertemplate(Usertemplate ut);

	public Usertemplate getUsertemplate(Usertemplate ut);

	public int updateUsertemplate(Usertemplate ut);

	public int updateOldUsertemplate(Usertemplate ut);

	public int addEmptemplate(Emptemplate et);

	public Emptemplate getEmptemplate(Emptemplate et);

	public int updateEmptemplate(Emptemplate et);

	public int addAppointment(Appointment at);

	public Appointment getAppointmentbyId(int id);

	public List<Appointment> getAppointmentByUserid(int userid);

	public List<Appointment> getAppointments(String email,int status);

	public int updateAppointmentStatus(Appointment at);

	public int updateSaAppointmentStatus(Appointment at);

	public List<Appointment> getAppointmentByPhone(Appointment at);

	public int delUsertemplate(Usertemplate ut);

	public Appointment getAppointmentByWPhone(Appointment at);

	public Appointment getAppointmentOldRecords(Appointment at);

	public List<String> getAppExpiredRecords(String userid);

	public int updatePhotoUrl(int id,String photoUrl);

	public Appointment getAppointmentPhotoUrl(Appointment at);

	public List<Appointment> getAppointmentList(Appointment at);

	public int updateAppointmentSms(int id);

	public int addSAtemplate(SubAccountTemplate sat);

	public int updateSAtemplate(SubAccountTemplate sat);

	public SubAccountTemplate getSAtemplate(int subaccountId,String templateType,int gid);

	public int delSAtemplate(SubAccountTemplate sat);

	public List<Usertemplate> getUsertemplateByUserid(int userid);

	public Appointment getAppointmentByHash(int qrContent);

	public int AppointmentReply(Appointment at);

	public int updateAppSendStatus(int id);

	public List<Appointment> getAppointmentByMid(int mid);

	public int batchUpdateAppSendStatus(List<Appointment> atlist);

	public int  updateMeetingAppStatus(Appointment at);

	public Appointment getMeetingAppointment(Appointment at);

	public Appointment getAMeetingRecord(int mid);

	public List<RespVisitor>  searchInviteByCondition(Map<String,String> conditions);

	public int  updateOpenCount(Appointment at);

	public int  updateLeaveTime(Appointment at);

	public List<Appointment> getAppointmnetByAgroup(Appointment at);

	public int  updateAppAreaInfo(Appointment at);

	public List<Usertemplate> getUserTemplateType(Usertemplate ut);

	public int updateAppPreview(Appointment at);

	List<Appointment> getAppointmentByexpireDate(int userid, String expireDate);

	int deleteAppointmentByids(List<Integer> appointmentIdList);

	public int updateCardNo(Appointment app);

	List<RespVisitor> searchNotSendCardVisitList(Map<String, String> map);

	public int updateAppExtendCol(Appointment app);

	List<Appointment> getAppointmentbyAcode(int parseInt);

	public List<RespVisitor> searchInviteByConditionPage(RequestVisit rv);

	public DataStatistics getInviteCount(RequestVisit rv);

	Usertemplate getOldUsertemplate(Usertemplate ut);

	int updateAppointmentPermission(Appointment appointment);

	List<VisitorChart> getDeptAppVisitByAppDate(RequestVisit requestVisit);

	List<Appointment> getAppointmentByMidOrder(RequestVisit requestVisit);

	int updateAppointmentJoinMeetingSign(RequestVisit requestVisit);

	int updateAppointmentFace(RequestVisit requestVisit);

	int addReserveMeeting(Appointment appointment);

	List<Appointment> getReviewMeeting(RespVisitor visitor);

	int updateReviewMeetingStatus(Appointment appointment);

	Appointment getAppointmentByPhoneAndMid(Appointment appointment);

	void updateSupplementAppointmentTask(Appointment app);

	int sendSmsPermissionNotice(int i, Appointment app);

	int updateAppointmentVgroup(Appointment appointment);

	List<Appointment> getAppointmentByIdAndAgroup(int id);

	int updateSupplementAppointmentDateById(Appointment appointment);

	int batchUpdateAppExtendCol(List<Appointment> atlist);

	String sendAppoinmentNotify(Appointment appointment);

	void updateAppointmentStatusByIdTask(Appointment appointment);
}
