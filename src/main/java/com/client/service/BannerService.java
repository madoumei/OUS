package com.client.service;

import java.util.List;

import com.client.bean.Banner;

public interface BannerService {
	
	public int addBanner(Banner b);
	
	public List<Banner> getBanners();
	 
	public int updateBanner(Banner b);
	
	public int delBanner(int bid);

}
