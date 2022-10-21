package com.web.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.bean.*;
import org.apache.ibatis.annotations.Mapper;
import com.client.bean.DataStatistics;
import com.client.bean.RequestVisit;
import com.client.bean.RespVisitor;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AppointmentDao extends BaseMapper<Appointment> {

	public int addUsertemplate(Usertemplate ut);

	public Usertemplate getUsertemplate(Usertemplate ut);

	public int updateUsertemplate(Usertemplate ut);

	int updateOldUsertemplate(Usertemplate ut);

	public int addEmptemplate(Emptemplate et);

	public Emptemplate getEmptemplate(Emptemplate et);

	public int updateEmptemplate(Emptemplate et);

	public int addAppointment(Appointment at);

	public Appointment getAppointmentbyId(@Param("id") int id);

	public List<Appointment> getAppointmentByUserid(@Param("userid")int userid);

	public List<Appointment> getAppointments(@Param("email")String email,@Param("status")int status);

	public int updateAppointmentStatus(Appointment at);

	public int updateSaAppointmentStatus(Appointment at);

	public List<Appointment> getAppointmentByPhone(Appointment at);

	public int delUsertemplate(Usertemplate ut);

	public Appointment getAppointmentByWPhone(Appointment at);

	public Appointment getAppointmentOldRecords(Appointment at);

	public List<String> getAppExpiredRecords(String userid);

	public int updatePhotoUrl(@Param("id") int id,@Param("photoUrl") String photoUrl);

	public Appointment getAppointmentPhotoUrl(Appointment at);

	public List<Appointment> getAppointmentList(Appointment at);

	public int updateAppointmentSms(@Param("id")int id);

	public int addSAtemplate(SubAccountTemplate sat);

	public int updateSAtemplate(SubAccountTemplate sat);

	public SubAccountTemplate getSAtemplate(@Param("subaccountId")int subaccountId,@Param("templateType")String templateType,@Param("gid")int gid);

	public int delSAtemplate(SubAccountTemplate sat);

	public List<Usertemplate> getUsertemplateByUserid(@Param("userid")int userid);

	public Appointment getAppointmentByHash(@Param("qrContent")int qrContent);

	public int AppointmentReply(Appointment at);

	public int updateAppSendStatus(@Param("id")int id);

	public List<Appointment> getAppointmentByMid(@Param("mid")int mid);

	public int batchUpdateAppSendStatus(@Param("list") List<Appointment> atlist);

	public int  updateMeetingAppStatus(Appointment at);

	public Appointment getMeetingAppointment(Appointment at);

	public Appointment getAMeetingRecord(@Param("mid")int mid);

	public List<RespVisitor>  searchInviteByCondition(Map<String,String> conditions);

	public int  updateOpenCount(Appointment at);

	public int  updateLeaveTime(Appointment at);

	public List<Appointment> getAppointmnetByAgroup(Appointment at);

	public int  updateAppAreaInfo(Appointment at);

	public List<Usertemplate> getUserTemplateType(Usertemplate ut);

	public int updateAppPreview(Appointment at);

	List<Appointment> getAppointmentByexpireDate(@Param("userid")int userid, @Param("expireDate")String expireDate);

	int deleteAppointmentByids(List<Integer> appointmentIdList);

	public int updateCardNo(Appointment app);

	List<RespVisitor> searchNotSendCardVisitList(Map<String, String> map);

	public int updateAppExtendCol(Appointment app);

	List<Appointment> getAppointmentbyAcode(@Param("acode")int acode);

	public List<RespVisitor> searchInviteByConditionPage(RequestVisit rv);

	public DataStatistics getInviteCount(RequestVisit rv);

	Usertemplate getOldUsertemplate(Usertemplate ut);

	int updateAppointmentPermission(Appointment appointment);

	List<VisitorChart> getDeptAppVisitByAppDate(RequestVisit requestVisit);

	List<Appointment> searchAppointmentByMidOrder(RequestVisit requestVisit);

	int updateAppointmentJoinMeetingSign(RequestVisit requestVisit);

	int updateAppointmentFaceById(RequestVisit requestVisit);

	Appointment getAppointmentByPhoneAndMid(Appointment appointment);

	List<Appointment> getReviewMeeting(RespVisitor visitor);

	int updateAppointmentMeetingStatus(Appointment appointment);

	int addAppointmentReserve(Appointment appointment);

	int updateSupplementAppointment(Appointment app);

	int updateAppointmentVgroup(Appointment appointment);

	List<Appointment> getAppointmentByIdAndAgroup(int id);

	int updateSupplementAppointmentDateById(Appointment appointment);

	int updateAppointmentStatusById(Appointment appointment);

	public int batchUpdateAppExtendCol(@Param("list") List<Appointment> atlist);
}
