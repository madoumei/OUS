package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.ResidentProject;
import com.web.bean.ResidentVisitor;
import com.web.bean.RvReport;

@Mapper
public interface ResidentVisitorDao {
	public int addProject(ResidentProject rp);
	
	public int updateProject(ResidentProject rp);
	
	public List<ResidentProject> getProject(ResidentProject rp);
	
	public int delProject(ResidentProject rp);
	
	public int addResidentVisitor(ResidentVisitor rv);
	
	public int updateResidentVisitor(ResidentVisitor rv);
	
	public List<ResidentVisitor> getResidentVisitor(ResidentVisitor rv);
	
	public int delResidentVisitor(ResidentVisitor rv);
	
	public int delRVisitorByPid(ResidentVisitor rv);
	
	public List<ResidentVisitor> getResidentVisitorByName(ResidentVisitor rv);
	
	public List<ResidentVisitor> getAllResidentCompany(ResidentVisitor rv);
	
	public List<ResidentVisitor> getResidentVisitorByCompany(ResidentVisitor rv);
	
	public ResidentVisitor getResidentVisitorByRid(ResidentVisitor rv);
	
	public ResidentVisitor getResidentVisitorByPhone(ResidentVisitor rv);
	
	public int updateResidentFace(ResidentVisitor rv);
	
	public List<ResidentVisitor> getResidentVisitorByUserid(ResidentVisitor rv);
	
	public ResidentVisitor getResidentVisitorByCardId(ResidentVisitor rv);
	
	public List<ResidentProject> getProjectByName(ResidentProject rv);
	
	public int updateRvPermission(ResidentVisitor rv); 
	
	public int batchAddResidentVisitor(List<ResidentVisitor> rvList);
	
	public int batchUpdateRvAvatar(List<ResidentVisitor> rvList);
	
	public List<RvReport> getRvReport(RvReport rvr);

	public List<RvReport> getRvOpenInfo(RvReport rvr);

}
