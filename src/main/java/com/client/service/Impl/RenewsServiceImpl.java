package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.Renews;
import com.client.dao.RenewsDao;
import com.client.service.RenewsService;


@Service("renewsService")
public class RenewsServiceImpl implements RenewsService{
	
	@Autowired
	private RenewsDao renewsDao;

	@Override
	public int addRenews(Renews r) {
		// TODO Auto-generated method stub
		return renewsDao.addRenews(r);
	}

	@Override
	public List<Renews> getRenews(Renews r) {
		// TODO Auto-generated method stub
		return renewsDao.getRenews(r);
	}

	@Override
	public List<Renews> getRenewsByNid(Renews r) {
		// TODO Auto-generated method stub
		return renewsDao.getRenewsByNid(r);
	}

	@Override
	public List<Renews> getRenewsByPhone(Renews r) {
		// TODO Auto-generated method stub
		return renewsDao.getRenewsByPhone(r);
	}

	@Override
	public int delRenews(String phone) {
		// TODO Auto-generated method stub
		return renewsDao.delRenews(phone);
	}

	@Override
	public int updateRenewsStatus(Renews r) {
		// TODO Auto-generated method stub
		return renewsDao.updateRenewsStatus(r);
	}
	
	
}
