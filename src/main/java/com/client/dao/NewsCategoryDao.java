package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.NewsCategory;


@Mapper
public interface NewsCategoryDao {
	public int addNewsCategory(NewsCategory nc);
	
	public List<NewsCategory> getNewsCategory();
	
	public int delNewsCategory(@Param("cid") int cid);
	
	public List<NewsCategory> getCategoryByCtype(@Param("ctype")int ctype);

}
