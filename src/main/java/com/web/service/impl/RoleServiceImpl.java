package com.web.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.web.bean.Role;
import com.web.dao.RoleDao;
import com.web.service.RoleService;


@Service("roleService")
public class RoleServiceImpl implements RoleService{
	@Autowired
	private RoleDao roldDao;
	

	@Override
	public int addRole(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.addRole(ro);
	}

	@Override
	public int updateRole(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.updateRole(ro);
	}

	@Override
	public int deleteRole(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.deleteRole(ro);
	}

	@Override
	public List<Role> getRoleList(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.getRoleList(ro);
	}

	@Override
	public List<Role> getRoleGroupList(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.getRoleGroupList(ro);
	}

	@Override
	public int addEmpRole(List<Role> erlist) {
		// TODO Auto-generated method stub
		return roldDao.addEmpRole(erlist);
	}

	@Override
	public int deleteRoleEmp(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return roldDao.deleteRoleEmp(map);
	}

	@Override
	public Role getRole(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.getRole(ro);
	}

	@Override
	public int delRoleEmp(Role ro) {
		// TODO Auto-generated method stub
		return roldDao.delRoleEmp(ro);
	}

}
