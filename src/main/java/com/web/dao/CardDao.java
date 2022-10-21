package com.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.IDCard;

@Mapper
public interface CardDao {

	public IDCard getById(String cardId);

	public void add(IDCard cardData);

	public void delete(String cardId);

}
