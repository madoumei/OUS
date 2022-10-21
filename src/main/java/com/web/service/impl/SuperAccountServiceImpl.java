package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.SuperAccount;
import com.web.dao.SuperAccountDao;
import com.web.service.SuperAccountService;


@Service("superAccountService")
public class SuperAccountServiceImpl implements SuperAccountService{
	
	@Autowired
	private SuperAccountDao superAccountDao;

	@Override
	public SuperAccount getSuperAccount(String username) {
		// TODO Auto-generated method stub
		return superAccountDao.getSuperAccount(username);
	}

	@Override
	public int updateSupAccInfo(SuperAccount supacc) {
		// TODO Auto-generated method stub
		return superAccountDao.updateSupAccInfo(supacc);
	}

	@Override
	public int addSuperAccount(SuperAccount supacc) {
		// TODO Auto-generated method stub
		return superAccountDao.addSuperAccount(supacc);
	}

	@Override
	public int updateRelatedAccount(SuperAccount supacc) {
		// TODO Auto-generated method stub
		return superAccountDao.updateRelatedAccount(supacc);
	}

	@Override
	public List<SuperAccount> getAllSuperAccount() {
		// TODO Auto-generated method stub
		return superAccountDao.getAllSuperAccount();
	}

	@Override
	public int updateSuperAccountStatus(SuperAccount supacc) {
		// TODO Auto-generated method stub
		return superAccountDao.updateSuperAccountStatus(supacc);
	}

	@Override
	public int updateSupAccPassword(SuperAccount supacc) {
		// TODO Auto-generated method stub
		return superAccountDao.updateSupAccPassword(supacc);
	}

}
