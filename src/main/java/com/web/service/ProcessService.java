package com.web.service;

import java.util.List;

import com.client.bean.Visitor;
import com.web.bean.Appointment;
import com.web.bean.Employee;
import com.web.bean.ProcessArea;
import com.web.bean.ProcessRecord;
import com.web.bean.ProcessRule;
import com.web.bean.ReqProcess;

public interface ProcessService {
	public int addProcessArea(ProcessArea pa);
	
	public int updateProcessArea(ProcessArea pa);
	
	public int deleteProcessArea(ProcessArea pa);
	
	public ProcessArea getProcessAreaByName(ProcessArea pa);
	
	public List<ProcessArea> getProcessArea(ProcessArea pa);
	
	public int addProcessRule(List<ProcessRule> prlist);
	
	public int deleteProcessRule(ProcessRule pr);
	
	public List<ProcessRule> getProcessRule(ProcessRule pr);
	
	public int addProcessRecord(ProcessRecord pr);
	
	public List<ProcessRecord> getProcessRecord(ProcessRecord pr);
	
	public List<ProcessRule> getCurrentProcess(ProcessRule pr);
	
	public int updateProcessRecord(ProcessRecord pr);
	
	public ProcessRecord getProcessRecordByRid(ProcessRecord pr);
	
	public List<ProcessRecord> getNoApproveRecords(ReqProcess rp);
	
	public List<ProcessRecord> getApprovedRecords(ReqProcess rp);
	
	public String sendNewRequestWeiXin(ProcessRecord pr,Visitor v,Employee emp);
	
	public String sendNewRequestWeiXinToCC(ProcessRecord pr,Visitor v,Employee emp,Appointment app);
	
	public String sendAppNewRequestWeiXin(ProcessRecord pr,List<Appointment> applist,Employee emp);
	
	public String sendFinishWeiXin(ProcessRecord pr,Visitor v,Employee emp);
	
	public ProcessRecord getFirstSubmitRecords(ProcessRecord pr);
	
	public List<ProcessRecord> getProcessRecordBySubName(ProcessRecord pr);
	
	public List<ProcessRule> getProcessRuleByRole(int role);
	
	public int cancelProcess(ProcessRecord pr);
	
	public List<ProcessRecord> getFirstSubRecordsByEmpid(ReqProcess rp);
}
