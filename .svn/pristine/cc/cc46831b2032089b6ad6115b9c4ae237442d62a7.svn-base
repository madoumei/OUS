package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.SendRecord;
import com.web.dao.SendRecordDao;
import com.web.service.SendRecordService;


@Service("sendRecordService")
public class SendRecordServiceImpl implements SendRecordService{
	
	@Autowired
	private SendRecordDao sendRecordDao;

	@Override
	public int addSendRecord(SendRecord sr) {
		// TODO Auto-generated method stub
		return sendRecordDao.addSendRecord(sr);
	}

	@Override
	public List<SendRecord> getSendRecordList(int userid) {
		// TODO Auto-generated method stub
		return sendRecordDao.getSendRecordList(userid);
	}

	@Override
	public int updateSendRecord(SendRecord sr) {
		// TODO Auto-generated method stub
		return sendRecordDao.updateSendRecord(sr);
	}

	@Override
	public int delSendRecord(int id) {
		// TODO Auto-generated method stub
		return sendRecordDao.delSendRecord(id);
	}

}
