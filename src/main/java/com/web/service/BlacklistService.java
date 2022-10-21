package com.web.service;

import java.util.List;
import java.util.Map;

import com.web.bean.Blacklist;
import com.web.bean.ReqBlacklist;


public interface BlacklistService {
	public  int addBlacklist(Blacklist bl);
	
	public  List<Blacklist> checkBlacklist(Blacklist bl);
	
	public  List<Blacklist> getBlacklist(ReqBlacklist rbl);
	
	public  int updateBlacklist(Blacklist bl);
	
	public  int delBlacklist(Map<String,Object> map);

	List<Blacklist> getBlacklistBybids(ReqBlacklist rbl);
}
