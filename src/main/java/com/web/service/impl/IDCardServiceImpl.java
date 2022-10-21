package com.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.IDCard;
import com.web.dao.CardDao;
import com.web.service.IDCardService;

@Service("cardService")
public class IDCardServiceImpl implements IDCardService {

	@Autowired 
	private CardDao cardDao;
	
	@Override
	public IDCard getById(String cardId) {
		return cardDao.getById(cardId);
	}

	@Override
	public int add(IDCard cardData) {
		
		cardDao.add(cardData);
		
		return 0;
	}

	@Override
	public int delete(String cardId) {
		cardDao.delete(cardId);
		return 0;
	}

}
