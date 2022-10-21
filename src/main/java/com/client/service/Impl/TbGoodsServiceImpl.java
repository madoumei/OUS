package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.TbGoods;
import com.client.dao.TbGoodsDao;
import com.client.service.TbGoodsService;


@Service("tbGoodsService")
public class TbGoodsServiceImpl implements TbGoodsService{
	
	@Autowired
	private TbGoodsDao tbGoodsDao;

	@Override
	public int addTbGoods(TbGoods tg) {
		// TODO Auto-generated method stub
		return tbGoodsDao.addTbGoods(tg);
	}

	@Override
	public List<TbGoods> getAllTbGoods(TbGoods tg) {
		// TODO Auto-generated method stub
		return tbGoodsDao.getAllTbGoods(tg);
	}

	@Override
	public List<TbGoods> getSaleTbGoods(TbGoods tg) {
		// TODO Auto-generated method stub
		return tbGoodsDao.getSaleTbGoods(tg);
	}

	@Override
	public List<TbGoods> getSaleTbGoodsByCid(TbGoods tg) {
		// TODO Auto-generated method stub
		return tbGoodsDao.getSaleTbGoodsByCid(tg);
	}

	@Override
	public int updateTbGoods(TbGoods tg) {
		// TODO Auto-generated method stub
		return tbGoodsDao.updateTbGoods(tg);
	}

	@Override
	public int delTbGoods(String gid) {
		// TODO Auto-generated method stub
		return tbGoodsDao.delTbGoods(gid);
	}

	@Override
	public TbGoods getTbGoodsByGid(String gid) {
		// TODO Auto-generated method stub
		return tbGoodsDao.getTbGoodsByGid(gid);
	}
	


}
