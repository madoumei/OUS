package com.web.service;

import java.util.List;
import java.util.Map;

import com.web.bean.TokenManage;

public interface TokenManageService {
	
	public int addToken(TokenManage tm);
	
	public TokenManage getToken(Map<String,String> map);
	
	public int updateTokenManage(TokenManage tm);
	
	public int delTokenManage(int id);
	
	public TokenManage checkToken(int userid,String token);
	
	public TokenManage getClient(Map<String,String> map);
	
	public List<TokenManage> getClients(int userid);

}
