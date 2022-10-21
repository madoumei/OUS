package com.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.HoneywellLft;


@Mapper
public interface HoneywellLftDao {
	public int addHoneywellLft(HoneywellLft hft);
	
	public int updateHoneywellLft(HoneywellLft hft);
	
	public  HoneywellLft getHoneywellLft(HoneywellLft hft);

}
