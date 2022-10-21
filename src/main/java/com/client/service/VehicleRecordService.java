package com.client.service;

import java.util.List;

import com.client.bean.ReqVR;
import com.client.bean.VehicleRecord;

public interface VehicleRecordService {
	public int addVehicleRecord(VehicleRecord vr);
	
	public List<VehicleRecord> getVehicleRecord(ReqVR vr);
	
	public VehicleRecord getVehicleRecordByVsid(VehicleRecord vr);
}
