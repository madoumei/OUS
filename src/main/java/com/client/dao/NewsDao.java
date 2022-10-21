package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.News;
import com.client.bean.Renews;


@Mapper
public interface NewsDao {
	public int addNews(News n);
	
	public List<News> getAllNews(News n);
	
	public News getNews(@Param("nid") int nid);
	
	public List<News> getNewsByCid(News n);
	
	public List<News> getNewsByPhone(News n);
	
	public List<News> getHotNews(News n);
	
	public int delNews(News n);
	
	public int updateNewsStatus(News n);
	
	public int updateBrcount(@Param("nid") int nid);
	
	public int updateRecount(News n);
	
	public int updateNews(News n);

	public List<News> getPropertyNews(News n);
	
}
