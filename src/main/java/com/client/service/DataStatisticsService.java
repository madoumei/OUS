package com.client.service;

import java.util.List;
import java.util.Map;

import com.client.bean.DataStatistics;
import com.client.bean.VisitStatistics;

public interface DataStatisticsService {
	public int addDataStatistics(DataStatistics ds);
	
	public DataStatistics getDataStatisticsByUserid(int userid);
	
	public DataStatistics getSumDataStatistics();
	
	public int updateWeiXinData(int userid);
	
	public int updateDingDingData(int userid);
	
	public int updateRtxData(int userid);
	
	public int updateEmailData(int userid);
	
	public int updateIvrData(int userid);

	public int getEmpCount();
	
	public int getUserCount();
	
	public int getCompanyCount();
	
	public int getVisitorCount();
	
	public int getSevenDayVisitorCount();
	
	public int getThirtyDayVisitorCount();
	
	public int getSmsCount();
	
	public List<VisitStatistics> getDaysVisitorCount(Map<String,String> map);
	
	public List<VisitStatistics> getSupAccVCount(Map<String,String> map );
	
	public List<VisitStatistics> getSupAccVCountByUserid(Map<String,Object> map );
	
	public int getAllSupAccVCount(Map<String,String> map );
	
	public List<VisitStatistics>  getAllSupAccVCountByUserid(Map<String,String> map );
}
