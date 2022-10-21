package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.News;
import com.client.dao.NewsDao;
import com.client.service.NewsService;


@Service("newsService")
public class NewsServiceImpl implements NewsService{
	
	@Autowired
	private NewsDao newsDao;

	@Override
	public int addNews(News n) {
		// TODO Auto-generated method stub
		return newsDao.addNews(n);
	}


	@Override
	public List<News> getNewsByCid(News n) {
		// TODO Auto-generated method stub
		return newsDao.getNewsByCid(n);
	}

	@Override
	public List<News> getNewsByPhone(News n) {
		// TODO Auto-generated method stub
		return newsDao.getNewsByPhone(n);
	}

	@Override
	public int delNews(News n) {
		// TODO Auto-generated method stub
		return newsDao.delNews(n);
	}

	@Override
	public int updateNewsStatus(News n) {
		// TODO Auto-generated method stub
		return newsDao.updateNewsStatus(n);
	}
	
	@Override
	public List<News> getAllNews(News n) {
		// TODO Auto-generated method stub
		return newsDao.getAllNews(n);
	}

	@Override
	public News getNews(int id) {
		// TODO Auto-generated method stub
		return newsDao.getNews(id);
	}


	@Override
	public int updateBrcount(int id) {
		// TODO Auto-generated method stub
		return newsDao.updateBrcount(id);
	}


	@Override
	public int updateRecount(News n) {
		// TODO Auto-generated method stub
		return newsDao.updateRecount(n);
	}


	@Override
	public List<News> getHotNews(News n) {
		// TODO Auto-generated method stub
		return newsDao.getHotNews(n);
	}


	@Override
	public int updateNews(News n) {
		// TODO Auto-generated method stub
		return newsDao.updateNews(n);
	}


	@Override
	public List<News> getPropertyNews(News n) {
		// TODO Auto-generated method stub
		return newsDao.getPropertyNews(n);
	}
}
