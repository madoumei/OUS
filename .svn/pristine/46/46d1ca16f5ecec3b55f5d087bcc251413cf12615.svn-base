package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.MrRecords;
import com.client.bean.ReqMrRecords;
import com.client.dao.MrRecordsDao;
import com.client.service.MrRecordsService;


@Service("mrRecordsService")
public class MrRecordsServiceImpl implements MrRecordsService{
	
	@Autowired
	private MrRecordsDao mrRecordsDao;

	@Override
	public int addMrRecords(ReqMrRecords reqmr) {
		// TODO Auto-generated method stub
		return mrRecordsDao.addMrRecords(reqmr);
	}

	@Override
	public List<MrRecords> getMrRecordsList(ReqMrRecords reqmr) {
		// TODO Auto-generated method stub
		return mrRecordsDao.getMrRecordsList(reqmr);
	}

	@Override
	public List<MrRecords> getMeetingRoomKeys(ReqMrRecords reqmr) {
		// TODO Auto-generated method stub
		return mrRecordsDao.getMeetingRoomKeys(reqmr);
	}

	@Override
	public int updateMeetingRoom(ReqMrRecords reqmr) {
		// TODO Auto-generated method stub
		return mrRecordsDao.updateMeetingRoom(reqmr);
	}

	@Override
	public int deleteMeeting(int id) {
		// TODO Auto-generated method stub
		return mrRecordsDao.deleteMeeting(id);
	}

	@Override
	public List<MrRecords> checkMrRecords(ReqMrRecords reqmr) {
		// TODO Auto-generated method stub
		return mrRecordsDao.checkMrRecords(reqmr);
	}

	@Override
	public List<MrRecords> getMrRecordsById(ReqMrRecords reqmr) {
		// TODO Auto-generated method stub
		return mrRecordsDao.getMrRecordsById(reqmr);
	}

	@Override
	public MrRecords getMrRecord(int id) {
		// TODO Auto-generated method stub
		return mrRecordsDao.getMrRecord(id);
	}

}
