package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.EqCardInfo;
import com.client.bean.Equipment;
import com.client.dao.EquipmentDao;
import com.client.service.EquipmentService;


@Service("equipmentService")
public class EquipmentServiceImpl extends ServiceImpl<EquipmentDao,Equipment> implements EquipmentService{

	@Autowired
	private EquipmentDao equipmentDao;

	/**
	 * 是不是进门设备
	 * @param eq
	 * @return
	 */
	@Override
	public boolean isEnterRouter(Equipment eq) {
		if(eq.getEnterStatus()==1){
			return true;
		}
		return false;
	}

	@Override
	public int addEquipment(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.addEquipment(e);
	}

	@Override
	public List<Equipment> getEquipmentbyUserid(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyUserid(e);
	}

	@Override
	public List<Equipment> getEquipmentCountbyUserid(Equipment e) {
		return equipmentDao.getEquipmentCountbyUserid(e);
	}

	@Override
	public int delEquipment(int eid) {
		// TODO Auto-generated method stub
		return equipmentDao.delEquipment(eid);
	}

	@Override
	public int updateEquipment(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.updateEquipment(e);
	}

	@Override
	public Equipment getEquipmentbyDeviceName(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyDeviceName(e);
	}

	@Override
	public Equipment getEquipmentbyDeviceCode(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyDeviceCode(e);
	}

	@Override
	public Equipment getEquipmentbyExtendCode(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyExtendCode(e);
	}

	@Override
	public Equipment getEquipmentbyEid(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyEid(e);
	}

	@Override
	public Equipment getEquipmentbyDeviceQrcode(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyDeviceQrcode(e);
	}

	@Override
	public Equipment getEquipmentbyDcRn(Equipment e) {
		// TODO Auto-generated method stub
		return equipmentDao.getEquipmentbyDcRn(e);
	}

	@Override
	public int updateOnlineStatus(Equipment equipment) {
		LambdaUpdateWrapper<Equipment> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Equipment::getDeviceCode,equipment.getDeviceCode())
				.set(Equipment::getOnlineStatus,equipment.getOnlineStatus());
		return update(null,updateWrapper)?1:0;
	}
}
