package com.web.service;

import com.web.bean.Operator;

public interface OperatorService {
	public Operator getOperator(String account);
	
	public int addOperator(Operator op);
	
	public int updateLoginDate(Operator op);

}
