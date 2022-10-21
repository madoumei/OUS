package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.ExtendVisitor;
import com.client.dao.ExtendVisitorDao;
import com.client.service.ExtendVisitorService;


@Service("extendVisitorService")
public class ExtendVisitorServiceImpl implements ExtendVisitorService{
	
	@Autowired
	private ExtendVisitorDao extendVisitorDao;

	@Override
	public List<ExtendVisitor> getExtendVisitor(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getExtendVisitor(userid);
	}

	@Override
	public int addExtendVisitor(ExtendVisitor ev) {
		// TODO Auto-generated method stub
		return extendVisitorDao.addExtendVisitor(ev);
	}

	@Override
	public int delExtendVisitor(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.delExtendVisitor(userid);
	}

	@Override
	public ExtendVisitor getVisitType(int userid,String eType) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getVisitType(userid,eType);
	}

	@Override
	public List<ExtendVisitor> getTeamExtendVisitor(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getTeamExtendVisitor(userid);
	}

	@Override
	public List<ExtendVisitor> getAllExtendVisitor(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getAllExtendVisitor(userid);
	}

	@Override
	public int delTeamExtendVisitor(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.delTeamExtendVisitor(userid);
	}

	@Override
	public List<ExtendVisitor> getExtendVisitorByType(ExtendVisitor ev) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getExtendVisitorByType(ev);
	}

	@Override
	public int delExtendVisitorByType(ExtendVisitor ev) {
		// TODO Auto-generated method stub
		return extendVisitorDao.delExtendVisitorByType(ev);
	}

	@Override
	public List<String> getExtendTypeList(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getExtendTypeList(userid);
	}

	@Override
	public List<ExtendVisitor> getBaseExtendVisitor(int userid) {
		// TODO Auto-generated method stub
		return extendVisitorDao.getBaseExtendVisitor(userid);
	}

	@Override
	public int updateExtendVisitor(ExtendVisitor ev) {
		// TODO Auto-generated method stub
		return extendVisitorDao.updateExtendVisitor(ev);
	}


}
