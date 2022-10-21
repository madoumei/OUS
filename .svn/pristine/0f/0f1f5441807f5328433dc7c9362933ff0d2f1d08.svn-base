package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.DataStatistics;
import com.client.bean.VisitStatistics;
import com.client.dao.DataStatisticsDao;
import com.client.service.DataStatisticsService;


@Service("dataStatisticsService")
public class DataStatisticsServiceImpl implements DataStatisticsService{
	
	@Autowired
	private DataStatisticsDao dataStatisticsDao;

	@Override
	public int addDataStatistics(DataStatistics ds) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.addDataStatistics(ds);
	}

	@Override
	public DataStatistics getDataStatisticsByUserid(int userid) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getDataStatisticsByUserid(userid);
	}

	@Override
	public DataStatistics getSumDataStatistics() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getSumDataStatistics();
	}

	@Override
	public int updateWeiXinData(int userid) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.updateWeiXinData(userid);
	}

	@Override
	public int updateDingDingData(int userid) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.updateDingDingData(userid);
	}

	@Override
	public int updateRtxData(int userid) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.updateRtxData(userid);
	}

	@Override
	public int updateEmailData(int userid) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.updateEmailData(userid);
	}

	@Override
	public int getEmpCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getEmpCount();
	}

	@Override
	public int getUserCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getUserCount();
	}

	@Override
	public int getCompanyCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getCompanyCount();
	}

	@Override
	public int getVisitorCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getVisitorCount();
	}

	@Override
	public int getSevenDayVisitorCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getSevenDayVisitorCount();
	}

	@Override
	public int getThirtyDayVisitorCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getThirtyDayVisitorCount();
	}

	@Override
	public int getSmsCount() {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getSmsCount();
	}

	@Override
	public List<VisitStatistics> getDaysVisitorCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getDaysVisitorCount(map);
	}

	@Override
	public List<VisitStatistics> getSupAccVCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getSupAccVCount(map);
	}

	@Override
	public List<VisitStatistics> getSupAccVCountByUserid(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getSupAccVCountByUserid(map);
	}

	@Override
	public int getAllSupAccVCount(Map<String,String> map) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getAllSupAccVCount(map );
	}

	@Override
	public List<VisitStatistics>  getAllSupAccVCountByUserid(Map<String,String> map ) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.getAllSupAccVCountByUserid(map);
	}

	@Override
	public int updateIvrData(int userid) {
		// TODO Auto-generated method stub
		return dataStatisticsDao.updateIvrData(userid);
	}

}
