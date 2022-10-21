package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.Manager;
import com.web.dao.ManagerDao;
import com.web.service.ManagerService;

@Service("managerService")
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	ManagerDao managerDao;

	@Override
	public int addManager(Manager manager) {
		return managerDao.addManager(manager);
	}

	@Override
	public int updateManager(Manager manager) {
		return managerDao.updateManager(manager);
	}

	@Override
	public int deleteManager(Manager manager) {
		return managerDao.deleteManager(manager);
	}

	@Override
	public Manager getManagerByAccount(String account) {
		return managerDao.getByAccount(account);
	}

	@Override
	public Manager getManagerPwdByAccount(String account) {
		return managerDao.getPwdByAccount(account);
	}


	@Override
	public List<Manager> getManagerList(Manager manager) {
		return managerDao.getByUser(manager);
	}

	@Override
	public Manager getManagerByCompany(Manager manager) {
		// TODO Auto-generated method stub
		return managerDao.getByCompany(manager);
	}

	@Override
	public Manager getManagerByMobile(Manager manager) {
		// TODO Auto-generated method stub
		return managerDao.getByMobile(manager);
	}

	@Override
	public List<Manager> getManagerListBySubAccountId(Manager manager) {
		return managerDao.getManagerListBySubAccountId(manager);
	}

	@Override
	public int updateServerManagerPwd(Manager manager) {
		return managerDao.updateServerManagerPwd(manager);
	}

}
