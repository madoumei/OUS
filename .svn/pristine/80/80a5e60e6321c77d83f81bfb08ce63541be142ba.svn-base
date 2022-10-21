package com.web.service;

import java.util.List;

import com.web.bean.Manager;

public interface ManagerService {

	int addManager(Manager manager);

	int updateManager(Manager manager);

	int deleteManager(Manager manager);

	Manager getManagerByAccount(String account);

	//返回了密码
	Manager getManagerPwdByAccount(String account);

	Manager getManagerByCompany(Manager manager);
	
	Manager getManagerByMobile(Manager manager);

	List<Manager> getManagerList(Manager manager);

	List<Manager> getManagerListBySubAccountId(Manager manager);

	int updateServerManagerPwd(Manager oldManager);

}
