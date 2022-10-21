package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.EquipmentGroup;
import com.client.dao.EquipmentGroupDao;
import com.client.service.EquipmentGroupService;


@Service("equipmentGroupService")
public class EquipmentGroupServiceImpl implements EquipmentGroupService{
	
	@Autowired
	private EquipmentGroupDao equipmentGroupDao;

	@Override
	public int addEquipmentGroup(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.addEquipmentGroup(eg);
	}

	@Override
	public List<EquipmentGroup> getEquipmentGroupByUserid(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.getEquipmentGroupByUserid(eg);
	}

	@Override
	public int updateEquipmentGroup(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.updateEquipmentGroup(eg);
	}

	@Override
	public int delEquipmentGroup(int egid) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.delEquipmentGroup(egid);
	}


	@Override
	public List<EquipmentGroup> getEquipmentGroupByGname(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.getEquipmentGroupByGname(eg);
	}

	@Override
	public EquipmentGroup getEquipmentGroupByEgid(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.getEquipmentGroupByEgid(eg);
	}

	@Override
	public List<EquipmentGroup> getEquipmentGroupByEgidSmart(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.getEquipmentGroupByEgidSmart(eg);
	}

	@Override
	public EquipmentGroup getEgidByName(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return equipmentGroupDao.getEgidByName(eg);
	}

	@Override
	public List<EquipmentGroup> getEquipmentGroupByEgidArray(String[] accessId) {
		return equipmentGroupDao.getEquipmentGroupByEgidArray(accessId);
	}

	@Override
	public List<EquipmentGroup> getEquipmentGroupListByGid(int userid,int gid) {
		return equipmentGroupDao.getEquipmentGroupListByGid(userid,gid);
	}
}
