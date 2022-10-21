package com.web.service;

import java.util.List;

import com.web.bean.EmpVisitProxy;

public interface VisitProxyService {
	public int addProxy(EmpVisitProxy vp);
	
	public EmpVisitProxy getProxyInfoByEid(EmpVisitProxy vp);
	
	public List<EmpVisitProxy> getProxyInfoByPId(EmpVisitProxy vp);
	
	public int updateProxy(EmpVisitProxy vp);
	
	public EmpVisitProxy  checkProxy(EmpVisitProxy vp);
}
