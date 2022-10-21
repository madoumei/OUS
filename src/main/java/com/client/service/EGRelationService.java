package com.client.service;

import java.util.List;
import java.util.Map;

import com.client.bean.EGRelation;
import com.client.bean.Equipment;
import com.client.bean.EquipmentGroup;
import com.client.bean.EquipmentGroupResp;

public interface EGRelationService {
	
	public int addEGRelation(List<EGRelation> rlist);
	
	public List<Equipment> getEquipmentByEgid(EquipmentGroup eg);
	
	public List<EquipmentGroup> getEGroupByEid(Equipment e);
	
	public int  updateRelationByEgid(EGRelation egr);
	
	public int  updateRelationByEid(EGRelation egr);

	public int  delRelationByEgid(int egid);
	
	public int  delRelationByEid(int eid);
	
	public List<String>  getEquipmentByEgids(List<Integer> egids);
	
	public List<Equipment> getVisitorEquipment(int userid,int status);
	
	public Equipment getEquipmentByDq(Map<String,String> map);
	
	public List<Equipment> getEquipmentsByEgids(Map<String,String> map);
	
	public Equipment getEquipmentByDc(Map<String,String> map);
	
	public List<Equipment>  getEquipmentByEtype(Map<String,String> map);

    List<EquipmentGroupResp> getEGroupList(Equipment e);

    Equipment getEquipmentGroupByGid(Map<String, String> map);

	List<EGRelation> getEGRelationList(EGRelation eg);

	EGRelation getEGRelationByEgid(EGRelation eg);

    EquipmentGroup getEGroupByEgid(EquipmentGroup e);
}
