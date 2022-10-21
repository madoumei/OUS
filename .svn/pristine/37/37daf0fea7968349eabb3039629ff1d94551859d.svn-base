package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.TbGoods;


@Mapper
public interface TbGoodsDao {
	
	public int addTbGoods(TbGoods tg);
	
	public List<TbGoods> getAllTbGoods(TbGoods tg);
	
	public List<TbGoods> getSaleTbGoods(TbGoods tg);
	
	public List<TbGoods> getSaleTbGoodsByCid(TbGoods tg);
	
	public TbGoods getTbGoodsByGid(@Param("gid") String gid);
	
	public int updateTbGoods(TbGoods tg);
	
	public int delTbGoods(@Param("gid")String gid);

}
