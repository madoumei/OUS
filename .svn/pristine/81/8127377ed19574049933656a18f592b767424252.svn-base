package com.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.Blacklist;
import com.web.bean.ReqBlacklist;


@Mapper
public interface BlacklistDao {
	public  int addBlacklist(Blacklist bl);
	
	public  List<Blacklist> checkBlacklist(Blacklist bl);
	
	public  List<Blacklist> getBlacklist(ReqBlacklist rbl);
	
	public  int updateBlacklist(Blacklist bl);
	
	public  int delBlacklist(Map<String,Object> map);

	List<Blacklist> getBlacklistBybids(ReqBlacklist rbl);
}
