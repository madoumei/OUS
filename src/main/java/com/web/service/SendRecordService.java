package com.web.service;

import java.util.List;

import com.web.bean.SendRecord;

public interface SendRecordService {
	
	public int addSendRecord(SendRecord sr);
	
	public List<SendRecord> getSendRecordList(int userid);
	
	public int updateSendRecord(SendRecord sr);
	
	public int delSendRecord(int id);
}
