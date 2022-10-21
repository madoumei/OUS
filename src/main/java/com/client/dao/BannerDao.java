package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.client.bean.Banner;


@Mapper
public interface BannerDao {
	
	public int addBanner(Banner b);
	
	public List<Banner> getBanners();
	 
	public int updateBanner(Banner b);
	
	public int delBanner(int bid);

}
