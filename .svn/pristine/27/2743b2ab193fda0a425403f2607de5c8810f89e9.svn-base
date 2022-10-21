package com.client.service.Impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.annotation.ProcessLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.client.bean.ExtendVisitor;
import com.client.bean.Visitor;
import com.client.dao.ExtendVisitorDao;
import com.client.service.PassService;
import com.event.event.PassEvent;
import com.web.bean.Appointment;
import com.web.bean.UserInfo;
import com.web.dao.UserDao;

@Service("passService")
public class PassSeviceImpl implements PassService{
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
    private UserDao userDao;
	
	@Autowired
    private ExtendVisitorDao extendVisitorDao;
	
	@Value("${eqbrand}")
	private String eqbrand;


	/**
	 * 下发权限
	 * @param paList
	 * @param userinfo
	 * @param opType
	 * @param eqType 0 人脸,1 二维码,2 车辆,3 ic卡
	 * @param <T>
	 * @return
	 */
	@Async
	@ProcessLogger("下发访客权限")
	@Override
	public <T> String passAuth(List<T> paList, UserInfo userinfo, int opType,String eqType) {
		// TODO Auto-generated method stub
		PassEvent pe=new PassEvent();
		pe.setUserinfo(userinfo);
		pe.setOpType(opType);
		pe.setEqType(eqType);
		pe.setEqbrand(eqbrand);
		if(null!=paList&&paList.size()>0) {
			if(paList.get(0) instanceof Visitor) {
				pe.setVtList(Arrays.asList(paList.toArray(new Visitor[paList.size()])));
				applicationEventPublisher.publishEvent(pe);
			}else if(paList.get(0) instanceof Appointment) {
				pe.setAppList(Arrays.asList(paList.toArray(new Appointment[paList.size()])));
				applicationEventPublisher.publishEvent(pe);
			}
		}
		return "0";
	}

	@Async
	@ProcessLogger("下发访客权限")
	@Override
	public <T> String passAuth(List<T> paList, int opType) {
		// TODO Auto-generated method stub
		PassEvent pe=new PassEvent();
		pe.setOpType(opType);
		pe.setEqbrand(eqbrand);
		if(null!=paList&&paList.size()>0) {
			if(paList.get(0) instanceof Visitor) {
				Visitor v=(Visitor) paList.get(0);
				UserInfo userinfo =userDao.getUserInfo(v.getUserid());
				pe.setUserinfo(userinfo);
				pe.setVtList(Arrays.asList(paList.toArray(new Visitor[paList.size()])));
		        ExtendVisitor ev = new ExtendVisitor();
		        ev.seteType(v.getvType());
		        ev.setUserid(userinfo.getUserid());
		        List<ExtendVisitor> extendVisitors = extendVisitorDao.getExtendVisitorByType(ev);
		        String accessType = "";
		        for (int i = 0; i < extendVisitors.size(); i++) {
		            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
		                accessType += extendVisitors.get(i).getInputValue();
		            }
		        }
				pe.setEqType(accessType);
				applicationEventPublisher.publishEvent(pe);
			}else if(paList.get(0) instanceof Appointment) {
				Appointment app=(Appointment) paList.get(0);
				UserInfo userinfo =userDao.getUserInfo(app.getUserid());
				pe.setUserinfo(userinfo);
		        ExtendVisitor ev = new ExtendVisitor();
		        ev.seteType(app.getvType());
		        ev.setUserid(userinfo.getUserid());
		        List<ExtendVisitor> extendVisitors = extendVisitorDao.getExtendVisitorByType(ev);
		        String accessType = "";
		        for (int i = 0; i < extendVisitors.size(); i++) {
		            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
		                accessType += extendVisitors.get(i).getInputValue();
		            }
		        }
		    	pe.setEqType(accessType);
				pe.setAppList(Arrays.asList(paList.toArray(new Appointment[paList.size()])));
				applicationEventPublisher.publishEvent(pe);
			}
		}
		return "0";
	}
	
	@Override
	public <T> String passAuth(Object obj,int opType) {
		// TODO Auto-generated method stub
		PassEvent pe=new PassEvent();
		pe.setOpType(opType);
		pe.setEqbrand(eqbrand);
		if(null!=obj) {
			if(obj instanceof Visitor) {
				Visitor v=(Visitor) obj;
				UserInfo userinfo =userDao.getUserInfo(v.getUserid());
				pe.setUserinfo(userinfo);
				
		        ExtendVisitor ev = new ExtendVisitor();
		        ev.seteType(v.getvType());
		        ev.setUserid(userinfo.getUserid());
		        List<ExtendVisitor> extendVisitors = extendVisitorDao.getExtendVisitorByType(ev);
		        String accessType = "";
		        for (int i = 0; i < extendVisitors.size(); i++) {
		            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
		                accessType += extendVisitors.get(i).getInputValue();
		            }
		        }
		    	pe.setEqType(accessType);
		    	
		    	List<Visitor> vtList=new ArrayList<Visitor>();
		    	vtList.add(v);
		    	pe.setVtList(vtList);
				applicationEventPublisher.publishEvent(pe);
			}else if(obj instanceof Appointment) {
				Appointment app=(Appointment) obj;
				UserInfo userinfo =userDao.getUserInfo(app.getUserid());
				pe.setUserinfo(userinfo);
				
		        ExtendVisitor ev = new ExtendVisitor();
		        ev.seteType(app.getvType());
		        ev.setUserid(userinfo.getUserid());
		        List<ExtendVisitor> extendVisitors = extendVisitorDao.getExtendVisitorByType(ev);
		        String accessType = "";
		        for (int i = 0; i < extendVisitors.size(); i++) {
		            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
		                accessType += extendVisitors.get(i).getInputValue();
		            }
		        }
		    	pe.setEqType(accessType);
		    	
		    	List<Appointment> appList=new ArrayList<Appointment>();
		    	appList.add(app);
		    	pe.setAppList(appList);
				applicationEventPublisher.publishEvent(pe);
			}
		}
		return "0";
	}
	
	@Override
	public <T> String passAuth(Object obj,int opType,UserInfo userinfo) {
		// TODO Auto-generated method stub
		PassEvent pe=new PassEvent();
		pe.setOpType(opType);
		pe.setEqbrand(eqbrand);
		if(null!=obj) {
			if(obj instanceof Visitor) {
				Visitor v=(Visitor) obj;
				pe.setUserinfo(userinfo);
				
		        ExtendVisitor ev = new ExtendVisitor();
		        ev.seteType(v.getvType());
		        ev.setUserid(userinfo.getUserid());
		        List<ExtendVisitor> extendVisitors = extendVisitorDao.getExtendVisitorByType(ev);
		        String accessType = "";
		        for (int i = 0; i < extendVisitors.size(); i++) {
		            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
		                accessType += extendVisitors.get(i).getInputValue();
		            }
		        }
		    	pe.setEqType(accessType);
		    	
		    	List<Visitor> vtList=new ArrayList<Visitor>();
		    	vtList.add(v);
		    	pe.setVtList(vtList);
				applicationEventPublisher.publishEvent(pe);
			}else if(obj instanceof Appointment) {
				Appointment app=(Appointment) obj;
				pe.setUserinfo(userinfo);
				
		        ExtendVisitor ev = new ExtendVisitor();
		        ev.seteType(app.getvType());
		        ev.setUserid(userinfo.getUserid());
		        List<ExtendVisitor> extendVisitors = extendVisitorDao.getExtendVisitorByType(ev);
		        String accessType = "";
		        for (int i = 0; i < extendVisitors.size(); i++) {
		            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
		                accessType += extendVisitors.get(i).getInputValue();
		            }
		        }
		    	pe.setEqType(accessType);
		    	
		    	List<Appointment> appList=new ArrayList<Appointment>();
		    	appList.add(app);
		    	pe.setAppList(appList);
				applicationEventPublisher.publishEvent(pe);
			}
		}
		return "0";
	}
}
