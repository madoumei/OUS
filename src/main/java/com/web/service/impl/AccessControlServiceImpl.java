package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.AccessControl;
import com.web.bean.ReqAC;
import com.web.dao.AccessControlDao;
import com.web.service.AccessControlService;

@Service("accessControlService")
public class AccessControlServiceImpl implements AccessControlService{
	@Autowired
	private AccessControlDao accessControlDao;

	@Override
	public int addAccessControl(List<AccessControl> acList) {
		// TODO Auto-generated method stub
		return accessControlDao.addAccessControl(acList);
	}

	@Override
	public int updateAccessControl(AccessControl ac) {
		// TODO Auto-generated method stub
		return accessControlDao.updateAccessControl(ac);
	}

	@Override
	public List<AccessControl> getAcList(ReqAC rac) {
		// TODO Auto-generated method stub
		return accessControlDao.getAcList(rac);
	}

	@Override
	public List<AccessControl> getLeaderAcList(ReqAC rac) {
		// TODO Auto-generated method stub
		return accessControlDao.getLeaderAcList(rac);
	}

	@Override
	public int updateLmAccessControl(AccessControl ac) {
		// TODO Auto-generated method stub
		return accessControlDao.updateLmAccessControl(ac);
	}

	@Override
	public AccessControl getAcListByEmpNo(ReqAC rac) {
		// TODO Auto-generated method stub
		return accessControlDao.getAcListByEmpNo(rac);
	}

	@Override
	public List<AccessControl> getAcListByCid(ReqAC rac) {
		// TODO Auto-generated method stub
		return accessControlDao.getAcListByCid(rac);
	}

}
