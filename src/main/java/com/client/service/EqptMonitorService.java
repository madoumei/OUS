package com.client.service;

import java.util.List;
import java.util.Map;

import com.client.bean.EqptMonitor;
import com.client.bean.EqptMonitorLog;
import com.client.bean.ReqEM;
import com.client.bean.ReqEqptMonitorLog;


public interface EqptMonitorService {
	public int addEqptMonitor(EqptMonitor em);
	
	public List<EqptMonitor> getEqptMonitor(ReqEM rem);
	
	public int updateEqptMonitor(EqptMonitor em);
	
	public int updateHeartbeat(String uid);
	
	public int updateLogDate(String uid);
	
	public int delEqptMonitor(EqptMonitor em);

    int addEqptMonitorErrorLog(EqptMonitorLog eqptMonitorLog);

    List<EqptMonitorLog> searchEqptMonitorErrorLogByCondition(ReqEqptMonitorLog eqptMonitorLog);
}
