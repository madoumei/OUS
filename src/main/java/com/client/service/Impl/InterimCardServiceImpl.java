package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.InterimCard;
import com.client.bean.ReqInterimCard;
import com.client.dao.InterimCardDao;
import com.client.service.InterimCardService;


@Service("interimCardService")
public class InterimCardServiceImpl implements InterimCardService{
	@Autowired
	private InterimCardDao interimCardDao;

	@Override
	public int addInterimCard(InterimCard ic) {
		// TODO Auto-generated method stub
		return interimCardDao.addInterimCard(ic);
	}

	@Override
	public List<InterimCard> getInterimCard(ReqInterimCard ric) {
		// TODO Auto-generated method stub
		return interimCardDao.getInterimCard(ric);
	}

	@Override
	public int delInterimCard(InterimCard ic) {
		// TODO Auto-generated method stub
		return interimCardDao.delInterimCard(ic);
	}

	@Override
	public int updateInterimCard(InterimCard ic) {
		// TODO Auto-generated method stub
		return interimCardDao.updateInterimCard(ic);
	}

	@Override
	public int returnInterimCard(InterimCard ic) {
		// TODO Auto-generated method stub
		return interimCardDao.returnInterimCard(ic);
	}

}
