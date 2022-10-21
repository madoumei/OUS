package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.Banner;
import com.client.dao.BannerDao;
import com.client.service.BannerService;


@Service("bannerService")
public class BannerServiceImpl implements BannerService{
	
	@Autowired
	private BannerDao bannerDao;

	@Override
	public int addBanner(Banner b) {
		// TODO Auto-generated method stub
		return bannerDao.addBanner(b);
	}

	@Override
	public List<Banner> getBanners() {
		// TODO Auto-generated method stub
		return bannerDao.getBanners();
	}

	public int updateBanner(Banner b) {
		// TODO Auto-generated method stub
		return bannerDao.updateBanner(b);
	}

	@Override
	public int delBanner(int bid) {
		// TODO Auto-generated method stub
		return bannerDao.delBanner(bid);
	}
	


}
