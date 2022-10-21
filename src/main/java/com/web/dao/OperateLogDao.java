package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.OperateLog;
import com.web.bean.ReqOperateLog;


@Mapper
public interface OperateLogDao {
	public int addLog(OperateLog ol);
	
	public List<OperateLog> getLogList(ReqOperateLog  rol);
}
