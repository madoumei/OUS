package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import com.client.bean.EquipmentGroupResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.EGRelation;
import com.client.bean.Equipment;
import com.client.bean.EquipmentGroup;
import com.client.dao.EGRelationDao;
import com.client.service.EGRelationService;


@Service("eGRelationService")
public class EGRelationServiceImpl implements EGRelationService{
	@Autowired
	private EGRelationDao eGRelationDao;

	@Override
	public int addEGRelation(List<EGRelation> rlist) {
		// TODO Auto-generated method stub
		return eGRelationDao.addEGRelation(rlist);
	}

	@Override
	public List<Equipment> getEquipmentByEgid(EquipmentGroup eg) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEquipmentByEgid(eg);
	}

	@Override
	public List<EquipmentGroup> getEGroupByEid(Equipment e) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEGroupByEid(e);
	}

	@Override
	public int updateRelationByEgid(EGRelation egr) {
		// TODO Auto-generated method stub
		return eGRelationDao.updateRelationByEgid(egr);
	}

	@Override
	public int updateRelationByEid(EGRelation egr) {
		// TODO Auto-generated method stub
		return eGRelationDao.updateRelationByEid(egr);
	}

	@Override
	public int delRelationByEgid(int egid) {
		// TODO Auto-generated method stub
		return eGRelationDao.delRelationByEgid(egid);
	}

	@Override
	public int delRelationByEid(int eid) {
		// TODO Auto-generated method stub
		return eGRelationDao.delRelationByEid(eid);
	}

	@Override
	public List<String> getEquipmentByEgids(List<Integer> egids) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEquipmentByEgids(egids);
	}

	@Override
	public List<Equipment> getVisitorEquipment(int userid,int status){
		// TODO Auto-generated method stub
		return eGRelationDao.getVisitorEquipment(userid,status);
	}
	
	@Override
	public Equipment getEquipmentByDq(Map<String, String> map) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEquipmentByDq(map);
	}

	@Override
	public List<Equipment> getEquipmentsByEgids(Map<String, String> map) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEquipmentsByEgids(map);
	}

	@Override
	public Equipment getEquipmentByDc(Map<String, String> map) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEquipmentByDc(map);
	}

	@Override
	public List<Equipment> getEquipmentByEtype(Map<String, String> map) {
		// TODO Auto-generated method stub
		return eGRelationDao.getEquipmentByEtype(map);
	}

	@Override
	public List<EquipmentGroupResp> getEGroupList(Equipment e) {
		return eGRelationDao.getEGroupList(e);
	}

	@Override
	public Equipment getEquipmentGroupByGid(Map<String, String> map) {
		return eGRelationDao.getEquipmentGroupByGid(map);
	}

	@Override
	public List<EGRelation> getEGRelationList(EGRelation eg) {
		return eGRelationDao.getEGRelationList(eg);
	}

	@Override
	public EGRelation getEGRelationByEgid(EGRelation eg) {
		return eGRelationDao.getEGRelationByEgid(eg);
	}

	@Override
	public EquipmentGroup getEGroupByEgid(EquipmentGroup e) {
		return eGRelationDao.getEGroupByEgid(e);
	}
}
