package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.NewsCategory;
import com.client.dao.NewsCategoryDao;
import com.client.service.NewsCategoryService;


@Service("newsCategoryService")
public class NewsCategoryServiceImpl implements NewsCategoryService{
	
	@Autowired
	private NewsCategoryDao newsCategoryDao;

	@Override
	public int addNewsCategory(NewsCategory nc) {
		// TODO Auto-generated method stub
		return newsCategoryDao.addNewsCategory(nc);
	}

	@Override
	public List<NewsCategory> getNewsCategory() {
		// TODO Auto-generated method stub
		return newsCategoryDao.getNewsCategory();
	}

	@Override
	public int delNewsCategory(int cid) {
		// TODO Auto-generated method stub
		return newsCategoryDao.delNewsCategory(cid);
	}

	@Override
	public List<NewsCategory> getCategoryByCtype(int ctype) {
		// TODO Auto-generated method stub
		return newsCategoryDao.getCategoryByCtype(ctype);
	}


}
