package com.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.EquipmentPersonGroup;


@Mapper
public interface EquipmentPersonGroupDao {
	
	public int addEPG(EquipmentPersonGroup epg);
	
	public int addEpgRelation(List<EquipmentPersonGroup> epglist);
	
	public EquipmentPersonGroup getEpgByGname(EquipmentPersonGroup epg);
	
	public List<EquipmentPersonGroup> getEpgByUserid(@Param("userid") int userid);

	public List<EquipmentPersonGroup> getEpgByEgid(EquipmentPersonGroup epg);
	
	public List<EquipmentPersonGroup> getEpgByMobile(EquipmentPersonGroup epg);
	
	public int updateEpgStatus(EquipmentPersonGroup epg);
	
	public int updateEpgInfo(EquipmentPersonGroup epg);
	
	public int updateEquipmentGroupStatus(EquipmentPersonGroup epg);
	
	public int delEPG(@Param("pgid")int pgid);
	
	public int delEquipmentGroup(@Param("egid")int egid);
	
	public int delEpgRelation(@Param("pgid")int pgid);
	
	public List<EquipmentPersonGroup> getEpgRelation(@Param("pgid")int pgid);
	
	public int  delEpgByMobile(Map<String,Object> conditions);
	
	public List<EquipmentPersonGroup>  getEpgRelationByPgid(int pgid);
}
