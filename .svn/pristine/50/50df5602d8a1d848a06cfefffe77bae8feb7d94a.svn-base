package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.EmpVisitProxy;
import com.web.dao.VisitProxyDao;
import com.web.service.VisitProxyService;

@Service("visitProxyService")
public class VisitProxyServiceImpl implements VisitProxyService{
	@Autowired
	private VisitProxyDao visitProxyDao;

	@Override
	public int addProxy(EmpVisitProxy vp) {
		// TODO Auto-generated method stub
		return visitProxyDao.addProxy(vp);
	}

	@Override
	public EmpVisitProxy getProxyInfoByEid(EmpVisitProxy vp) {
		// TODO Auto-generated method stub
		return visitProxyDao.getProxyInfoByEid(vp);
	}

	@Override
	public List<EmpVisitProxy> getProxyInfoByPId(EmpVisitProxy vp) {
		// TODO Auto-generated method stub
		return visitProxyDao.getProxyInfoByPId(vp);
	}

	@Override
	public int updateProxy(EmpVisitProxy vp) {
		// TODO Auto-generated method stub
		return visitProxyDao.updateProxy(vp);
	}

	@Override
	public EmpVisitProxy checkProxy(EmpVisitProxy vp) {
		// TODO Auto-generated method stub
		return visitProxyDao.checkProxy(vp);
	}


	
	
	
}
