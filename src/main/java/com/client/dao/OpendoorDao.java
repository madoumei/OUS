package com.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.client.bean.Gate;
import com.client.bean.Opendoor;
import com.client.bean.OpendoorInfo;
import com.client.bean.ReqODI;
import com.client.bean.RequestVisit;
import com.web.bean.VisitorChart;


@Mapper
public interface OpendoorDao {

	public int addKeys(List<Opendoor> odlist);

	public int  delkeysByPgid(@Param("pgid") int pgid);

	public int  delkeysByMobile(Map<String,Object> conditions);

	public List<Opendoor> getKeysByMobile(Opendoor od);

	public int  updateOpenDoorStatus(Opendoor od);

	public int  delkeysByCode(@Param("deviceCode")String deviceCode);

	public int  addOpendoorInfo(OpendoorInfo odi);

	public List<OpendoorInfo> getOpendoorInfo(ReqODI reqodi);

	public int  BatchAddOpendoorInfo(List<ReqODI> odlist);

	public List<VisitorChart> getArrivedVCount(RequestVisit rv);

	public List<VisitorChart> getArrivedLineChart(RequestVisit rv);

	public OpendoorInfo getLastRecords(OpendoorInfo odi);

}
