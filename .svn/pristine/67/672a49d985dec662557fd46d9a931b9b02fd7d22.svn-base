package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.ReqVR;
import com.client.bean.VehicleRecord;
import com.client.dao.VehicleRecordDao;
import com.client.service.VehicleRecordService;


@Service("vehicleRecordService")
public class VehicleRecordServiceImpl implements VehicleRecordService{
	@Autowired
	private VehicleRecordDao vehicleRecordDao;

	@Override
	public int addVehicleRecord(VehicleRecord vr) {
		// TODO Auto-generated method stub
		return vehicleRecordDao.addVehicleRecord(vr);
	}

	@Override
	public List<VehicleRecord> getVehicleRecord(ReqVR vr) {
		// TODO Auto-generated method stub
		return vehicleRecordDao.getVehicleRecord(vr);
	}

	@Override
	public VehicleRecord getVehicleRecordByVsid(VehicleRecord vr) {
		// TODO Auto-generated method stub
		return vehicleRecordDao.getVehicleRecordByVsid(vr);
	}

}
