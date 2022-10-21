package com.web.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.TokenManage;
import com.web.dao.TokenManageDao;
import com.web.service.TokenManageService;


@Service("tokenManageService")
public class TokenManageServiceImpl implements TokenManageService{
	
	@Autowired
	private TokenManageDao tokenManageDao;

	@Override
	public int addToken(TokenManage tm) {
		// TODO Auto-generated method stub
		return tokenManageDao.addToken(tm);
	}

	@Override
	public TokenManage getToken(Map<String,String> map) {
		// TODO Auto-generated method stub
		return tokenManageDao.getToken(map);
	}

	@Override
	public int updateTokenManage(TokenManage tm) {
		// TODO Auto-generated method stub
		return tokenManageDao.updateTokenManage(tm);
	}

	@Override
	public int delTokenManage(int id) {
		// TODO Auto-generated method stub
		return tokenManageDao.delTokenManage(id);
	}

	@Override
	public TokenManage checkToken(int userid,String token) {
		// TODO Auto-generated method stub
		return tokenManageDao.checkToken(userid,token);
	}

	@Override
	public TokenManage getClient(Map<String, String> map) {
		// TODO Auto-generated method stub
		return tokenManageDao.getClient(map);
	}

	@Override
	public List<TokenManage> getClients(int userid) {
		// TODO Auto-generated method stub
		return tokenManageDao.getClients(userid);
	}

}
