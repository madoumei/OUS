package com.client.service;

import java.util.List;
import java.util.Map;

import com.client.bean.EquipmentGroup;
import com.client.bean.EquipmentPersonGroup;

public interface EquipmentPersonGroupService {
	
	public int addEPG(EquipmentPersonGroup epg);
	
	public int addEpgRelation(List<EquipmentPersonGroup> epglist);
	
	public EquipmentPersonGroup getEpgByGname(EquipmentPersonGroup epg);
	
	public List<EquipmentPersonGroup> getEpgByUserid(int userid);

	public List<EquipmentPersonGroup> getEpgByEgid(EquipmentPersonGroup epg);
	
	public List<EquipmentPersonGroup> getEpgByMobile(EquipmentPersonGroup epg);
	
	public int updateEpgStatus(EquipmentPersonGroup epg);
	
	public int updateEpgInfo(EquipmentPersonGroup epg);
	
	public int updateEquipmentGroupStatus(EquipmentPersonGroup epg);
	
	public int delEPG(int pgid);
	
	public int delEquipmentGroup(int egid);
	
	public int delEpgRelation(int pgid);
	
	public List<EquipmentPersonGroup> getEpgRelation(int pgid);
	
	public int  delEpgByMobile(Map<String,Object> conditions);
	
	public List<EquipmentPersonGroup>  getEpgRelationByPgid(int pgid);
	
}
