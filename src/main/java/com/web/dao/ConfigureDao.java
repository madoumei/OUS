package com.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.web.bean.Configures;

@Mapper
public interface ConfigureDao {
	
	public Configures getConfigure(@Param("usreid") int userid, @Param("name")String name);
	
	public int addConfigure(Configures conf);
	
	public int updateConfigure(Configures conf);
	
	public int delConfigure(Configures conf);
	
	public Configures getDefaultConfigure(@Param("name") String name);

}
