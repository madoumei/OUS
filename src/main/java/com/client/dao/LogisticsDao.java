package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.client.bean.Logistics;
import com.client.bean.ReqLogistics;


@Mapper
public interface LogisticsDao {
		public int addLogisticsInfo(Logistics lo);
		
		public List<Logistics> getLogisticsInfo(ReqLogistics rl);
		
		public Logistics getTodayLogisticsInfo(ReqLogistics rl);
		
		public int updateLogisticsInfo(Logistics lo);
		
		public Logistics getLogisticsById(Logistics lo);
}
