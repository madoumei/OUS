package com.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.web.bean.Community;


@Mapper
public interface CommunityDao {
	public Community getCommunity(@Param("userid") int userid);
	
	public int addCommunity(Community c);
	
	public int updateCommunity(Community c);

}
