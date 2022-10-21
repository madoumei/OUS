package com.client.service;

import java.util.List;

import com.client.bean.TbGoods;

public interface TbGoodsService {
	
	public int addTbGoods(TbGoods tg);
	
	public List<TbGoods> getAllTbGoods(TbGoods tg);
	
	public List<TbGoods> getSaleTbGoods(TbGoods tg);
	
	public List<TbGoods> getSaleTbGoodsByCid(TbGoods tg);
	
	public TbGoods getTbGoodsByGid(String gid);
	
	public int updateTbGoods(TbGoods tg);
	
	public int delTbGoods(String gid);

}
