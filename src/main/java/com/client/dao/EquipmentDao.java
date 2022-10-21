package com.client.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.client.bean.Equipment;
import org.apache.ibatis.annotations.Param;

public interface EquipmentDao extends BaseMapper<Equipment> {
	public int addEquipment(Equipment e);
	
	public List<Equipment> getEquipmentbyUserid(Equipment e);

	public List<Equipment> getEquipmentCountbyUserid(Equipment e);

	public int delEquipment(@Param("eid")int eid);
	
	public int updateEquipment(Equipment e);
	
	public Equipment getEquipmentbyDeviceName(Equipment e);
	
	public Equipment getEquipmentbyDeviceCode(Equipment e);
	
	public Equipment getEquipmentbyExtendCode(Equipment e);
	
	public Equipment getEquipmentbyDeviceQrcode(Equipment e);
	
	public Equipment getEquipmentbyDcRn(Equipment e);
	
	public Equipment getEquipmentbyEid(Equipment e);

	int updateOnlineStatus(Equipment equipment);
}
