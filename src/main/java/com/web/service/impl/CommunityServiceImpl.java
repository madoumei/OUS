package com.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.Community;
import com.web.dao.CommunityDao;
import com.web.service.CommunityService;


@Service("communityService")
public class CommunityServiceImpl implements CommunityService{
	
	@Autowired
	private CommunityDao communityDao;

	@Override
	public Community getCommunity(int userid) {
		// TODO Auto-generated method stub
		return communityDao.getCommunity(userid);
	}

	@Override
	public int addCommunity(Community c) {
		// TODO Auto-generated method stub
		return communityDao.addCommunity(c);
	}

	@Override
	public int updateCommunity(Community c) {
		// TODO Auto-generated method stub
		return communityDao.updateCommunity(c);
	}


}
