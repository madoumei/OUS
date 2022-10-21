package com.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.EquipmentGroup;

@Mapper
public interface EquipmentGroupDao {
	public int addEquipmentGroup(EquipmentGroup eg);
	
	public List<EquipmentGroup> getEquipmentGroupByUserid(EquipmentGroup eg);
	
	public EquipmentGroup getEquipmentGroupByEgid(EquipmentGroup eg);

	public List<EquipmentGroup> getEquipmentGroupByEgidSmart(EquipmentGroup eg);

	public int updateEquipmentGroup(EquipmentGroup eg);
	
	public int delEquipmentGroup(@Param("egid")int egid);
	
	public List<EquipmentGroup> getEquipmentGroupByGname(EquipmentGroup eg);

	public EquipmentGroup getEgidByName(EquipmentGroup eg);

	List<EquipmentGroup> getEquipmentGroupByEgidArray(String[] accessId);

    List<EquipmentGroup> getEquipmentGroupListByGid(@Param("userid")int userid,@Param("gid")int gid);
}
