package com.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.HoneywellLft;
import com.web.dao.HoneywellLftDao;
import com.web.service.HoneywellLftService;


@Service("honeywellLftService")
public class HoneywellLftServiceImpl implements HoneywellLftService{
	@Autowired
	private HoneywellLftDao honeywellLftDao;

	@Override
	public int addHoneywellLft(HoneywellLft hft) {
		// TODO Auto-generated method stub
		return honeywellLftDao.addHoneywellLft(hft);
	}

	@Override
	public int updateHoneywellLft(HoneywellLft hft) {
		// TODO Auto-generated method stub
		return honeywellLftDao.updateHoneywellLft(hft);
	}

	@Override
	public HoneywellLft getHoneywellLft(HoneywellLft hft) {
		// TODO Auto-generated method stub
		return honeywellLftDao.getHoneywellLft(hft);
	}

}
