package com.web.service;

import java.util.List;

import com.web.bean.OperateLog;
import com.web.bean.ReqOperateLog;


public interface OperateLogService {
	public int addLog(OperateLog ol);
	
	public List<OperateLog> getLogList(ReqOperateLog  rol);
}
