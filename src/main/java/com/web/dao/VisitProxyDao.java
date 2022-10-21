package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.EmpVisitProxy;

@Mapper
public interface VisitProxyDao {
	public int addProxy(EmpVisitProxy vp);
	
	public EmpVisitProxy getProxyInfoByEid(EmpVisitProxy vp);
	
	public List<EmpVisitProxy> getProxyInfoByPId(EmpVisitProxy vp);
	
	public int updateProxy(EmpVisitProxy vp);
	
	public EmpVisitProxy  checkProxy(EmpVisitProxy vp);

}
