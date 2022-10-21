package com.client.service;

import java.util.List;

import com.client.bean.News;


public interface NewsService {
	public int addNews(News n);
	
	public List<News> getAllNews(News n);
	
	public News getNews(int id);

	public List<News> getNewsByCid(News n);
	
	public List<News> getNewsByPhone(News n);
	
	public int delNews(News n);
	
	public int updateNewsStatus(News n);
	
	public int updateBrcount(int id);
	
	public int updateRecount(News n);
	
	public List<News> getHotNews(News n);
	
	public int updateNews(News n);
	
	public List<News> getPropertyNews(News n);
}
