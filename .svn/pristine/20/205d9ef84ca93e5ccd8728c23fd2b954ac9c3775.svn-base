package com.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.Operator;
import com.web.dao.OperatorDao;
import com.web.service.OperatorService;


@Service("operatorService")
public class OperatorServiceImpl implements OperatorService{
	
	@Autowired
	private OperatorDao operatorDao;

	@Override
	public Operator getOperator(String account) {
		// TODO Auto-generated method stub
		return operatorDao.getOperator(account);
	}

	@Override
	public int addOperator(Operator op) {
		// TODO Auto-generated method stub
		return operatorDao.addOperator(op);
	}

	@Override
	public int updateLoginDate(Operator op) {
		// TODO Auto-generated method stub
		return operatorDao.updateLoginDate(op);
	}

}
