package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.client.bean.ReqVR;
import com.client.bean.VehicleRecord;


@Mapper
public interface VehicleRecordDao {
	public int addVehicleRecord(VehicleRecord vr);
	
	public List<VehicleRecord> getVehicleRecord(ReqVR vr);
	
	public VehicleRecord getVehicleRecordByVsid(VehicleRecord vr);
}
