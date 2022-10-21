package com.web.service;

import java.util.List;

import com.web.bean.VisitorType;

public interface VisitorTypeService {
	public int addVisitorType(VisitorType vt);
	
	public List<VisitorType> getVisitorType(VisitorType vt);
	
	public int updateVisitorType(VisitorType vt);
	
	public int delVisitorType(VisitorType vt);
	
	public VisitorType getVisitorTypeByTid(VisitorType vt);
	
	public int updateVisitorTypeQid(VisitorType vt);
}
