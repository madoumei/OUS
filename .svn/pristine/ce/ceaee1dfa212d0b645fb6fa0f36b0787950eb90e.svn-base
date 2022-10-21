package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.ProcessArea;
import com.web.bean.ProcessRecord;
import com.web.bean.ProcessRule;
import com.web.bean.ReqProcess;


@Mapper
public interface ProcessDao {
	public int addProcessArea(ProcessArea pa);
	
	public int updateProcessArea(ProcessArea pa);
	
	public int deleteProcessArea(ProcessArea pa);
	
	public List<ProcessArea> getProcessArea(ProcessArea pa);
	
	public ProcessArea getProcessAreaByName(ProcessArea pa);
	
	public int addProcessRule(List<ProcessRule> rlist);
	
	public int deleteProcessRule(ProcessRule pr);
	
	public List<ProcessRule> getProcessRule(ProcessRule pr);
	
	public int addProcessRecord(ProcessRecord pr);
	
	public List<ProcessRecord> getProcessRecord(ProcessRecord pr);
	
	public List<ProcessRule> getCurrentProcess(ProcessRule pr);
	
	public int updateProcessRecord(ProcessRecord pr);
	
	public ProcessRecord getProcessRecordByRid(ProcessRecord pr);
	
	public List<ProcessRecord> getNoApproveRecords(ReqProcess rp);
	
	public List<ProcessRecord> getApprovedRecords(ReqProcess rp);
	
	public ProcessRecord getFirstSubmitRecords(ProcessRecord pr);
	
	public List<ProcessRecord> getProcessRecordBySubName(ProcessRecord pr);
	
	public List<ProcessRule> getProcessRuleByRole(int role);
	
	public int cancelProcess(ProcessRecord pr);
	
	public List<ProcessRecord> getFirstSubRecordsByEmpid(ReqProcess rp);
	
}
