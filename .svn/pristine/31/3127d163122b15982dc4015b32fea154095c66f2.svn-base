package com.client.dao;

import java.util.List;
import java.util.Map;

import com.client.bean.EqptMonitorLog;
import com.client.bean.ReqEqptMonitorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.EqptMonitor;
import com.client.bean.ReqEM;


@Mapper
public interface EqptMonitorDao {
	public int addEqptMonitor(EqptMonitor em);
	
	public List<EqptMonitor> getEqptMonitor(ReqEM rem);
	
	public int updateEqptMonitor(EqptMonitor em);
	
	public int updateHeartbeat(@Param("uid") String uid);
	
	public int updateLogDate(@Param("uid")String uid);
	
	public int delEqptMonitor(EqptMonitor em);

    int addEqptMonitorErrorLog(EqptMonitorLog eqptMonitorLog);

    List<EqptMonitorLog> searchEqptMonitorErrorLogByCondition(ReqEqptMonitorLog eqptMonitorLog);
}
