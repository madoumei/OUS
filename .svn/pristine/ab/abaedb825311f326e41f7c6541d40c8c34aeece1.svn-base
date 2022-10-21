package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.Gate;
import com.client.bean.Opendoor;
import com.client.bean.OpendoorInfo;
import com.client.bean.ReqODI;
import com.client.bean.RequestVisit;
import com.client.dao.OpendoorDao;
import com.client.service.OpendoorService;
import com.web.bean.VisitorChart;


@Service("opendoorService")
public class OpendoorServiceImpl implements OpendoorService{
	
	@Autowired
	private OpendoorDao opendoorDao;

	@Override
	public int addKeys(List<Opendoor> odlist) {
		// TODO Auto-generated method stub
		return opendoorDao.addKeys(odlist);
	}

	@Override
	public int delkeysByPgid(int pgid) {
		// TODO Auto-generated method stub
		return opendoorDao.delkeysByPgid(pgid);
	}

	@Override
	public int delkeysByMobile(Map<String,Object> conditions) {
		// TODO Auto-generated method stub
		return opendoorDao.delkeysByMobile(conditions);
	}

	@Override
	public List<Opendoor> getKeysByMobile(Opendoor od) {
		// TODO Auto-generated method stub
		return opendoorDao.getKeysByMobile(od);
	}

	@Override
	public int updateOpenDoorStatus(Opendoor od) {
		// TODO Auto-generated method stub
		return opendoorDao.updateOpenDoorStatus(od);
	}

	@Override
	public int delkeysByCode(String code) {
		// TODO Auto-generated method stub
		return opendoorDao.delkeysByCode(code);
	}

	@Override
	public int addOpendoorInfo(OpendoorInfo odi) {
		// TODO Auto-generated method stub
		return opendoorDao.addOpendoorInfo(odi);
	}

	@Override
	public List<OpendoorInfo> getOpendoorInfo(ReqODI reqodi) {
		// TODO Auto-generated method stub
		return opendoorDao.getOpendoorInfo(reqodi);
	}

	@Override
	public int BatchAddOpendoorInfo(List<ReqODI> odlist) {
		// TODO Auto-generated method stub
		return opendoorDao.BatchAddOpendoorInfo(odlist);
	}

	@Override
	public List<VisitorChart> getArrivedVCount(RequestVisit rv) {
		// TODO Auto-generated method stub
		return opendoorDao.getArrivedVCount(rv);
	}

	@Override
	public List<VisitorChart> getArrivedLineChart(RequestVisit rv) {
		// TODO Auto-generated method stub
		return opendoorDao.getArrivedLineChart(rv);
	}

	@Override
	public OpendoorInfo getLastRecords(OpendoorInfo odi) {
		// TODO Auto-generated method stub
		return opendoorDao.getLastRecords(odi);
	}

}
