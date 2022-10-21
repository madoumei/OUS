package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.MrRecords;
import com.client.bean.ReqMrRecords;

@Mapper
public interface MrRecordsDao {
	
	public  int addMrRecords(ReqMrRecords reqmr);
	
	public  List<MrRecords> getMrRecordsList(ReqMrRecords reqmr);
	
	public  List<MrRecords> getMeetingRoomKeys(ReqMrRecords reqmr);
	
	public  int updateMeetingRoom(ReqMrRecords reqmr);
	
	public  int deleteMeeting(@Param("id") int id);
	
	public  List<MrRecords> checkMrRecords(ReqMrRecords reqmr);
	
	public  List<MrRecords> getMrRecordsById(ReqMrRecords reqmr);
	
	public MrRecords getMrRecord(@Param("id") int id);

}
