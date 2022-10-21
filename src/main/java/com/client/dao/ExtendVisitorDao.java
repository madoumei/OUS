package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.client.bean.ExtendVisitor;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ExtendVisitorDao {
	
	public int addExtendVisitor(ExtendVisitor ev);
	
	public List<ExtendVisitor> getExtendVisitor(@Param("userid")int userid);
	
	public int delExtendVisitor(@Param("userid") int userid);
	
	public ExtendVisitor getVisitType(@Param("userid")int userid,@Param("eType")String eType);
	
	public List<ExtendVisitor> getTeamExtendVisitor(@Param("userid")int userid);
	
	public List<ExtendVisitor> getAllExtendVisitor(@Param("userid")int userid);
	
	public List<ExtendVisitor> getBaseExtendVisitor(@Param("userid")int userid);
	
	public int delTeamExtendVisitor(@Param("userid")int userid);
	
	public List<ExtendVisitor> getExtendVisitorByType(ExtendVisitor ev);
	
	public int delExtendVisitorByType(ExtendVisitor ev);
	
	public List<String> getExtendTypeList(@Param("userid")int userid);
	
	public int updateExtendVisitor(ExtendVisitor ev);

}
