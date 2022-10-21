package com.web.service;

import java.util.List;

import com.web.bean.SuperAccount;

public interface SuperAccountService {
	public List<SuperAccount>  getAllSuperAccount();
	
	public SuperAccount getSuperAccount(String username);
	
	public int updateSupAccInfo(SuperAccount supacc);
	
	public int addSuperAccount(SuperAccount supacc);
	
	public int updateRelatedAccount(SuperAccount supacc);
	
	public int updateSuperAccountStatus(SuperAccount supacc);
	
	public int updateSupAccPassword(SuperAccount supacc);

}
