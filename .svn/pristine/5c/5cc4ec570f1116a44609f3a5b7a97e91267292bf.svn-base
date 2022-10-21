package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.Holiday;
import com.web.bean.ReqHd;
import com.web.dao.HolidayDao;
import com.web.service.HolidayService;


@Service("holidayService")
public class HolidayServiceImpl implements HolidayService{
	
	@Autowired
	private HolidayDao holidayDao;

	@Override
	public int addHoliday(Holiday hd) {
		// TODO Auto-generated method stub
		return holidayDao.addHoliday(hd);
	}

	@Override
	public int updateHoliday(Holiday hd) {
		// TODO Auto-generated method stub
		return holidayDao.updateHoliday(hd);
	}

	@Override
	public int delHoliday(Holiday hd) {
		// TODO Auto-generated method stub
		return holidayDao.delHoliday(hd);
	}

	@Override
	public List<Holiday> getHoliday(ReqHd hd) {
		// TODO Auto-generated method stub
		return holidayDao.getHoliday(hd);
	}

}
