package com.web.dao;

import java.util.List;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.web.bean.Manager;

@Mapper
public interface ManagerDao {

	public int addManager(Manager manager);
	
	public Manager getByAccount(String account);

	public Manager getPwdByAccount(String account);

	public Manager getByCompany(Manager manager);
	
	public Manager getByMobile(Manager manager);
	
	public List<Manager> getByUser(Manager manager);
	
	public int updateManager(Manager manager);
	
	public int deleteManager(Manager manager);

	List<Manager> getManagerListBySubAccountId(Manager manager);

	int updateServerManagerPwd(Manager manager);

    Manager getManagerList(Manager manager);
}
