package com.client.service;

import java.util.List;

import com.client.bean.NewsCategory;


public interface NewsCategoryService {
	public int addNewsCategory(NewsCategory nc);
	
	public List<NewsCategory> getNewsCategory();
	
	public int delNewsCategory(int cid);
	
	public List<NewsCategory> getCategoryByCtype(int ctype);

}
