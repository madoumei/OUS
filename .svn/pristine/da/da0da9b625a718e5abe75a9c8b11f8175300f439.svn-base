package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.SuperAccount;


@Mapper
public interface SuperAccountDao {
	public List<SuperAccount>  getAllSuperAccount();
	
	public SuperAccount getSuperAccount(String username);
	
	public int updateSupAccInfo(SuperAccount supacc);
	
	public int addSuperAccount(SuperAccount supacc);
	
	public int updateRelatedAccount(SuperAccount supacc);
	
	public int updateSuperAccountStatus(SuperAccount supacc);
	
	public int updateSupAccPassword(SuperAccount supacc);
}
