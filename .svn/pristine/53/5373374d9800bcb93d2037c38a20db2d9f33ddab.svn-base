package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.VisitorType;
import com.web.dao.VisitorTypeDao;
import com.web.service.VisitorTypeService;

@Service("visitorTypeService")
public class VisitorTypeServiceImpl implements VisitorTypeService{
	@Autowired
	private VisitorTypeDao visitorTypeDao;
	

	@Override
	public int addVisitorType(VisitorType vt) {
		// TODO Auto-generated method stub
		return visitorTypeDao.addVisitorType(vt);
	}

	@Override
	public List<VisitorType> getVisitorType(VisitorType vt) {
		// TODO Auto-generated method stub
		return visitorTypeDao.getVisitorType(vt);
	}

	@Override
	public int updateVisitorType(VisitorType vt) {
		// TODO Auto-generated method stub
		return visitorTypeDao.updateVisitorType(vt);
	}

	@Override
	public int delVisitorType(VisitorType vt) {
		// TODO Auto-generated method stub
		return visitorTypeDao.delVisitorType(vt);
	}

	@Override
	public VisitorType getVisitorTypeByTid(VisitorType vt) {
		// TODO Auto-generated method stub
		return visitorTypeDao.getVisitorTypeByTid(vt); 
	}

	@Override
	public int updateVisitorTypeQid(VisitorType vt) {
		// TODO Auto-generated method stub
		return visitorTypeDao.updateVisitorTypeQid(vt);
	}

}
