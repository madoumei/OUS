package com.client.service;

import java.util.List;

import com.client.bean.MrRecords;
import com.client.bean.ReqMrRecords;

public interface MrRecordsService {

	public  int addMrRecords(ReqMrRecords reqmr);
	
	public  List<MrRecords> getMrRecordsList(ReqMrRecords reqmr);
	
	public  List<MrRecords> getMeetingRoomKeys(ReqMrRecords reqmr);
	
	public  int updateMeetingRoom(ReqMrRecords reqmr);
	
	public  int deleteMeeting(int id);
	
	public  List<MrRecords> checkMrRecords(ReqMrRecords reqmr);
	
	public  List<MrRecords> getMrRecordsById(ReqMrRecords reqmr);
	
	public MrRecords getMrRecord(int id);
}
