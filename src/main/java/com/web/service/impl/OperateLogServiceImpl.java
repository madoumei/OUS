package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.OperateLog;
import com.web.bean.ReqOperateLog;
import com.web.dao.OperateLogDao;
import com.web.service.OperateLogService;


@Service("operateLogService")
public class OperateLogServiceImpl implements OperateLogService{
	@Autowired
	private OperateLogDao operateLogDao;
	

	@Override
	public int addLog(OperateLog ol) {
		// TODO Auto-generated method stub
		return operateLogDao.addLog(ol);
	}

	@Override
	public List<OperateLog> getLogList(ReqOperateLog rol) {
		// TODO Auto-generated method stub
		return operateLogDao.getLogList(rol);
	}

}
