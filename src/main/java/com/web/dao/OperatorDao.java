package com.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.web.bean.Operator;


@Mapper
public interface OperatorDao {
		public Operator getOperator(@Param("account") String account);
		
		public int addOperator(Operator op);
		
		public int updateLoginDate(Operator op);
}
