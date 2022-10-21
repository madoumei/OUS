package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.EquipmentGroup;
import com.client.bean.EquipmentPersonGroup;
import com.client.dao.EquipmentPersonGroupDao;
import com.client.service.EquipmentPersonGroupService;


@Service("equipmentPersonGroupService")
public class EquipmentPersonGroupServiceImpl implements EquipmentPersonGroupService{
	
	@Autowired
	private EquipmentPersonGroupDao equipmentPersonGroupDao;

	@Override
	public int addEPG(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.addEPG(epg);
	}

	@Override
	public List<EquipmentPersonGroup> getEpgByUserid(int userid) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.getEpgByUserid(userid);
	}

	@Override
	public int updateEpgStatus(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.updateEpgStatus(epg);
	}

	@Override
	public int delEPG(int pgid) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.delEPG(pgid);
	}

	@Override
	public int updateEpgInfo(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.updateEpgInfo(epg);
	}

	@Override
	public int updateEquipmentGroupStatus(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.updateEquipmentGroupStatus(epg);
	}

	@Override
	public int delEquipmentGroup(int egid) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.delEquipmentGroup(egid);
	}

	@Override
    public EquipmentPersonGroup getEpgByGname(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.getEpgByGname(epg);
	}

	@Override
	public List<EquipmentPersonGroup> getEpgByEgid(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.getEpgByEgid(epg);
	}

	@Override
	public List<EquipmentPersonGroup> getEpgByMobile(EquipmentPersonGroup epg) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.getEpgByMobile(epg);
	}

	@Override
	public int delEpgRelation(int pgid) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.delEpgRelation(pgid);
	}

	@Override
	public int addEpgRelation(List<EquipmentPersonGroup> epglist) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.addEpgRelation(epglist);
	}

	@Override
	public List<EquipmentPersonGroup> getEpgRelation(int pgid) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.getEpgRelation(pgid);
	}

	@Override
	public int delEpgByMobile(Map<String, Object> conditions) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.delEpgByMobile(conditions);
	}

	@Override
	public List<EquipmentPersonGroup>  getEpgRelationByPgid(int pgid) {
		// TODO Auto-generated method stub
		return equipmentPersonGroupDao.getEpgRelationByPgid(pgid);
	}

}
