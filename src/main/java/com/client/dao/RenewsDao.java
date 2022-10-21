package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.Renews;


@Mapper
public interface RenewsDao {
	
	public int addRenews(Renews r);
	
	public List<Renews> getRenews(Renews r);

	public List<Renews> getRenewsByNid(Renews r);
	
	public List<Renews> getRenewsByPhone(Renews r);
	
	public int delRenews(@Param("phone") String phone);
	
	public int updateRenewsStatus(Renews r);
	
}
