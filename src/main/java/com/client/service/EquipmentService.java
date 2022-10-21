package com.client.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.Equipment;

public interface EquipmentService extends IService<Equipment> {
	boolean isEnterRouter(Equipment eq);

	public int addEquipment(Equipment e);

	public List<Equipment> getEquipmentbyUserid(Equipment e);

	public List<Equipment> getEquipmentCountbyUserid(Equipment e);

	public int delEquipment(int eid);
	
	public int updateEquipment(Equipment e);
	
	public Equipment getEquipmentbyDeviceName(Equipment e);
	
	public Equipment getEquipmentbyDeviceCode(Equipment e);
	
	public Equipment getEquipmentbyExtendCode(Equipment e);
	
	public Equipment getEquipmentbyEid(Equipment e);
	
	public Equipment getEquipmentbyDcRn(Equipment e);
	
	public Equipment getEquipmentbyDeviceQrcode(Equipment e);

    int updateOnlineStatus(Equipment equipment);
}
