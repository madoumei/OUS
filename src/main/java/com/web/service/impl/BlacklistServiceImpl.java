package com.web.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.Blacklist;
import com.web.bean.ReqBlacklist;
import com.web.dao.BlacklistDao;
import com.web.service.BlacklistService;

@Service("blacklistService")
public class BlacklistServiceImpl implements BlacklistService{
	@Autowired
	private BlacklistDao blacklistDao;

	@Override
	public int addBlacklist(Blacklist bl) {
		// TODO Auto-generated method stub
		return blacklistDao.addBlacklist(bl);
	}


	@Override
	public int updateBlacklist(Blacklist bl) {
		// TODO Auto-generated method stub
		return blacklistDao.updateBlacklist(bl);
	}

	@Override
	public int delBlacklist(Map<String,Object> map) {
		// TODO Auto-generated method stub
		return blacklistDao.delBlacklist(map);
	}

	@Override
	public List<Blacklist> getBlacklistBybids(ReqBlacklist rbl) {
		return blacklistDao.getBlacklistBybids(rbl);
	}


	@Override
	public List<Blacklist> checkBlacklist(Blacklist bl) {
		// TODO Auto-generated method stub
		return blacklistDao.checkBlacklist(bl);
	}



	@Override
	public List<Blacklist> getBlacklist(ReqBlacklist rbl) {
		// TODO Auto-generated method stub
		return blacklistDao.getBlacklist(rbl);
	}
 
}
