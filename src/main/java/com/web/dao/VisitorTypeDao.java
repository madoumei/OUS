package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.VisitorType;


@Mapper
public interface VisitorTypeDao {
		public int addVisitorType(VisitorType vt);
		
		public List<VisitorType> getVisitorType(VisitorType vt);
		
		public int updateVisitorType(VisitorType vt);
		
		public int delVisitorType(VisitorType vt);
		
		public VisitorType getVisitorTypeByTid(VisitorType vt);
		
		public int updateVisitorTypeQid(VisitorType vt);
}
