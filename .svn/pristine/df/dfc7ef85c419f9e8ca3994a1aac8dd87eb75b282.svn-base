package com.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.TokenManage;


@Mapper
public interface TokenManageDao {
	public int addToken(TokenManage tm);
	
	public TokenManage getToken(Map<String,String> map);
	
	public int updateTokenManage(TokenManage tm);
	
	public int delTokenManage(int id);
	
	public TokenManage checkToken(int userid,String token);
	
	public TokenManage getClient(Map<String,String> map);
	
	public List<TokenManage> getClients(int userid);

}
