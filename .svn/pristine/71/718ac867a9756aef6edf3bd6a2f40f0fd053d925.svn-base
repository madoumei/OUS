package com.client.service;


import java.util.List;
import java.util.Map;

import com.client.bean.Gate;
import com.client.bean.Opendoor;
import com.client.bean.OpendoorInfo;
import com.client.bean.ReqODI;
import com.client.bean.RequestVisit;
import com.web.bean.VisitorChart;


public interface OpendoorService {
	
	public int addKeys(List<Opendoor> odlist);
	
	public int  delkeysByPgid(int pgid);
	
	public int  delkeysByMobile(Map<String,Object> conditions);
	
	public List<Opendoor> getKeysByMobile(Opendoor od);
	
	public int  updateOpenDoorStatus(Opendoor od);
	
	public int  delkeysByCode(String code);
	
	public int  addOpendoorInfo(OpendoorInfo odi);
	
	public List<OpendoorInfo> getOpendoorInfo(ReqODI reqodi);

	public int  BatchAddOpendoorInfo(List<ReqODI> odlist);
	
	public List<VisitorChart> getArrivedVCount(RequestVisit rv);
	
	public List<VisitorChart> getArrivedLineChart(RequestVisit rv);
	
	public OpendoorInfo getLastRecords(OpendoorInfo odi);

}
