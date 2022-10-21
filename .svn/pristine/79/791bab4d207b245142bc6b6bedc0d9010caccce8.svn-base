package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import com.client.bean.EqptMonitorLog;
import com.client.bean.ReqEqptMonitorLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.EqptMonitor;
import com.client.bean.ReqEM;
import com.client.dao.EqptMonitorDao;
import com.client.service.EqptMonitorService;


@Service("eqptMonitorService")
public class EqptMonitorServiceImpl implements EqptMonitorService{
	
	@Autowired
	private EqptMonitorDao eqptMonitorDao;
	
	@Override
	public int addEqptMonitor(EqptMonitor em) {
		// TODO Auto-generated method stub
		return eqptMonitorDao.addEqptMonitor(em);
	}

	@Override
	public List<EqptMonitor> getEqptMonitor(ReqEM rem) {
		// TODO Auto-generated method stub
		return eqptMonitorDao.getEqptMonitor(rem);
	}

	@Override
	public int updateEqptMonitor(EqptMonitor em) {
		// TODO Auto-generated method stub
		return eqptMonitorDao.updateEqptMonitor(em);
	}

	@Override
	public int delEqptMonitor(EqptMonitor em) {
		// TODO Auto-generated method stub
		return eqptMonitorDao.delEqptMonitor(em);
	}

	@Override
	public int updateHeartbeat(String uid) {
		// TODO Auto-generated method stub
		return eqptMonitorDao.updateHeartbeat(uid);
	}

	@Override
	public int updateLogDate(String uid) {
		// TODO Auto-generated method stub
		return eqptMonitorDao.updateLogDate(uid);
	}

	@Override
	public int addEqptMonitorErrorLog(EqptMonitorLog eqptMonitorLog) {
		return eqptMonitorDao.addEqptMonitorErrorLog(eqptMonitorLog);
	}

	@Override
	public List<EqptMonitorLog> searchEqptMonitorErrorLogByCondition(ReqEqptMonitorLog eqptMonitorLog) {
		return eqptMonitorDao.searchEqptMonitorErrorLogByCondition(eqptMonitorLog);
	}
}
