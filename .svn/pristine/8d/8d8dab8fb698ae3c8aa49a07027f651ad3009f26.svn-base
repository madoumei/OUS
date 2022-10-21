package com.client.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.client.bean.*;
import com.web.bean.VisitorChart;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VisitorDao extends BaseMapper<Visitor> {
	public int addVisitor(Visitor vt);
	
	public int updateVisitor(Visitor vt);
	
	public int updatePermission(Visitor vt);
	
	public int updateVisitorSms(Visitor vt);
	
	public Visitor getVisitorById(@Param("vid") int vid);
	
	public Visitor getVisitor(Visitor v);
	
	public List<RespVisitor> getVisitorList(@Param("userid")int userid, @Param("visitDate")String visitDate);
	
	public List<RespVisitor>  searchVisitByCondition(Map<String,Object> conditions );
	
	public int getVisitorCount(@Param("userid")int userid);
	
	public List<RespVisitor>  getVisitorListByEmpid(@Param("empid")int empid);
	
	public int batchSingin(List<Visitor> vlist);
	
	public int addVisitorApponintmnet(Visitor vt);
	
	public List<RespVisitor> getVisitorAppointmentByPhone(Map<String,Object> conditions);
	
	public List<RespVisitor> getVisitorAppointmentList(Visitor vt);
	
	public int  addApponintmnetVisitor(Visitor vt);
	
	public int  updateVisitorAppointment(Visitor vt);
	
	public int  updateSignOut(Visitor vt);
	
	public List<RespVisitor>  checkSignOutRecords(Visitor vt);
	
	public List<RespVisitor> getVistorAppListByEmpPhone(RespVisitor vt);
	
	public List<RespVisitor>  getTempList(Map<String,String> conditions );
	
	public List<RespVisitor>  getAppointmentListByEmpPhone(RespVisitor vt);
	
	public int  updateSigninByAppClient(Visitor vt);
	
	public List<RespVisitor> getAppointmentListByVPhone(RespVisitor vt);
	
	public List<RespVisitor> searchVisitForApp(Map<String,String> conditions );
	
	public Visitor getTodayVisitorById(@Param("vid")int vid);
	
	public List<RespVisitor> getAppointmentListByEmpId(RespVisitor vt);
	
	public List<RespVisitor> getVisitorAppointmentByVname(Map<String,Object> conditions);
	
	public int updateVisitRemark(Visitor vt);
	
	public int addGroupVisitor(List<Visitor> vlist);
	
	public int updateGroupPermission(Visitor vt);
	
	public List<Visitor> getGroupVistorList(@Param("vid")int vid);
	
	public Visitor getTodayVisitorByPhone(@Param("vphone")String vphone,@Param("userid")int userid);
	
	public int  updateSignOutByVid(Visitor vt);
	
	public List<Visitor> getLastestVisitor(Map<String,Object> map);
	
	public List<RespVisitor> searchAppByCondition(Map<String,String> map);
	
	public int  batchSignOut(List<Visitor> vlist);
	
	public int  updateVisitInfo(Visitor vt);
	
	public Visitor getVisitorByCardID(@Param("cardId")String cardId,@Param("userid")int userid);
	
	public List<RespVisitor> SearchRVisitorByCondition(Map<String,String> map);
	
	public Visitor getTodayAppointmentByPhone(Visitor vt);
	
	public int  batchUpdateVisitorAppointment(Visitor vt);
	
	public List<RespVisitor> getVistorProxyListByEmpPhone(RespVisitor vt);
	
	public int  updateSignPdf(Visitor vt);
	
	public int  updateLeaveTime(Visitor vt);
	
	public Visitor getVisitorByAppId(Visitor vt);
	
	public List<RespVisitor>  getExamVisitList(Map<String,Object> conditions);
	
	public Visitor getVisitorByPlateNum(Visitor vt);

    List<Visitor> getVisitorByexpireDate(@Param("userid")int userid,@Param("expireDate")String expireDate);

	int deleteVisitByVids(List<Integer> expireVid);

    List<VisitorChart> getSignInVisitorByDept(RequestVisit visitor);
    
    public int updateCardNo(Visitor vt);

    List<VisitorChart> getArrivedVisitorChart(RequestVisit requestVisit);
    
    List<VisitorChart> getAllArrivedVisitorChart(RequestVisit requestVisit);

    List<VisitorChart> getAllArrivedVisitorChartSmart(RequestVisit requestVisit);

    public VisitorChart getNoArrivedVCount(RequestVisit requestVisit);
    
    public List<VisitorChart> getNoArrivedLineChart(RequestVisit requestVisit);

    List<VisitorRecord> newSearchVisitorByCondition1(Map<String, Object>  rv);
    
    public VisitorChart getVisitSaCountByVphone(RequestVisit requestVisit);
    
    public int updateVisitorExtendCol(Visitor vt);

    List<VisitorChart> getArrivedVCount(RequestVisit rv);

	List<Visitor> getVListBySubAccountId(@Param("empid") int empid, @Param("userid")int userid);
	
	public List<RespVisitor>  searchVisitByConditionPage(RequestVisit rv);

	public List<RespVisitor> searchAppByConditionPage(RequestVisit requestVisit);
	    
	public List<RespVisitor>  SearchRVisitorByConditionPage(RequestVisit requestVisit);
	
	public DataStatistics getSignedCount(RequestVisit rv);
	    
	public DataStatistics getAppointmentCount(RequestVisit rv);
	
	public DataStatistics getRvSignedCount(RequestVisit rv);
	
	public List<VisitorChart>  getAllVisitorLineChart(RequestVisit rv);

    List<VisitorChart> getDeptVisitByAppDate(RequestVisit requestVisit);

    List<VisitorRecord> newSearchVisitorByCondition1Page(ReqVisitorRecord rv);
    
    int finishQuestionnaire(Visitor v);
    
    List<Visitor> searchLeaveTimeVisitByCondition(Map<String, Object> reqMap);

    List<Visitor> getScoreChartByVisitor(RequestVisit requestVisit);

	/**
	 * 支持当天全部，当天未签到，当天已签到
	 * 支持多天，非当天已签到，其他情况下有重复统计的问题
	 * 非当天未签到，对于多天预约有错误
	 * @param requestVisit
	 * @return
	 */
	public List<RespVisitor>  searchVisitors(RequestVisit requestVisit);

	public int batchUpdateExtendCol(List<Visitor> atlist);

	public int  VisitorReply(Visitor visitor);
}
