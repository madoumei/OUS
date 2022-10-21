package com.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.Role;


@Mapper
public interface RoleDao {
	public int addRole(Role ro);
	
	public int updateRole(Role ro);
	
	public int deleteRole(Role ro);
	
	public List<Role> getRoleList(Role ro);
	
	public List<Role> getRoleGroupList(Role ro);
	
	public Role getRole(Role ro); 
	
	public int addEmpRole(List<Role> erlist);
	
	public int deleteRoleEmp(Map<String,Object> map);
	
	public int delRoleEmp(Role ro);
	
}
