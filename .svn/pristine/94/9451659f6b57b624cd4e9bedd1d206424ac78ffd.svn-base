package com.event.listener.passListen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.client.bean.Visitor;
import com.event.event.PassEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.Appointment;
/**
 * 
 * 下发车辆
 */
@Component
public class VehiclePassListen {
	@EventListener(value = PassEvent.class,condition = "#passEvent.eqType.contains('2')")
	 public void listener(PassEvent passEvent) {
		 if(StringUtils.isNotEmpty(passEvent.getEqbrand()) && passEvent.getEqbrand().indexOf("H2")!=-1) {
		 	if(null!= passEvent.getVtList()) {
				 Visitor v=passEvent.getVtList().get(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
	    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	    	 Calendar calendar = Calendar.getInstance();
		     calendar.setTime(v.getAppointmentDate());
	         calendar.add(calendar.DATE, v.getQrcodeConf() - 1);//把日期往后增加一天.整数往后推,负数往前移动
			
	    	Map<String,String> map=new HashMap<String,String>();
	    	map.put("car_plate_no", v.getPlateNum());
	    	map.put("area_name", "");
	    	if(v.getQrcodeConf()==0) {
	    		map.put("auth_times", "30");
	    	}else {
	    		map.put("auth_times", String.valueOf(v.getQrcodeConf()*30));
	    	}
	    	map.put("allow_enter_time_begin", sdf.format(v.getAppointmentDate()));
	    	map.put("allow_enter_time_end", sdf2.format(v.getAppointmentDate())+"+23:59:00");
	    	map.put("visitor_company", v.getVcompany());
	    	map.put("visitor_id", "v"+v.getVid());
	    	map.put("visitor_name", v.getVname());
	    	map.put("visitor_phone", v.getVphone());
	    	if(null!=v.getVisitType()) {
		    	if(v.getVisitType().indexOf("#")==-1) {
		    		map.put("visit_reason", v.getVisitType());
		    	}else {
		    		map.put("visit_reason", v.getVisitType().substring(0,v.getVisitType().indexOf("#")));
		    	}
			}else {
				map.put("visit_reason", "");
			}
	    	map.put("recep_depart", v.getDeptName());
	    	map.put("recep_name", v.getEmpName());
	    	map.put("recep_phone", v.getEmpPhone());
	    	map.put("remark", v.getRemark());
	    	
	    	String response = HttpClientUtil.invokeGetNoEnCode("http://"+Constant.H2_IP+"/plugin/visitor.action?do=visitorAuth", map,"utf-8",5000);
	    	  try {
	              ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
	              JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
	              // 如果请求成功
	              if (jsonNode.has("result_code") && "0".equals(jsonNode.get("result_code").asText())) {
	              }
	          } catch (IOException e) {
//	              e.printStackTrace();
	          }
			}else {
			    List<Appointment> aList=passEvent.getAppList();
			    if(aList.size()>0) {
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
			    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			    	int size=aList.size();
			    	 Calendar calendar = Calendar.getInstance();
				     calendar.setTime(aList.get(0).getAppointmentDate());
			         calendar.add(calendar.DATE, aList.get(0).getQrcodeConf() - 1);//把日期往后增加一天.整数往后推,负数往前移动
			         
			    	for(int a=0;a<size;a++) {
			    		if(StringUtils.isNotBlank(aList.get(a).getPlateNum())) {
					    	Map<String,String> map=new HashMap<String,String>();
					    	map.put("car_plate_no", aList.get(a).getPlateNum());
					    	map.put("area_name", "");
					    	map.put("auth_times", String.valueOf(aList.get(a).getQrcodeConf()*30));
					    	map.put("allow_enter_time_begin", sdf.format(aList.get(a).getAppointmentDate()));
					    	map.put("allow_enter_time_end", sdf2.format(calendar.getTime())+"+23:59:59");
					    	map.put("visitor_company", aList.get(a).getVcompany());
					    	map.put("visitor_id", "a"+aList.get(a).getId());
					    	map.put("visitor_name", aList.get(a).getName());
					    	map.put("visitor_phone", aList.get(a).getPhone());
					    	if(null!=aList.get(a).getVisitType()) {
						    	if(aList.get(a).getVisitType().indexOf("#")==-1) {
						    		map.put("visit_reason", aList.get(a).getVisitType());
						    	}else {
						    		map.put("visit_reason", aList.get(a).getVisitType().substring(0, aList.get(a).getVisitType().indexOf("#")));
						    	}
			    			}else {
			    				map.put("visit_reason", "");
			    			}
					    	map.put("recep_depart", aList.get(a).getDeptName());
					    	map.put("recep_name", aList.get(a).getEmpName());
					    	map.put("recep_phone", aList.get(a).getEmpPhone());
					    	map.put("remark", aList.get(a).getRemark());
					    	
					    	String response = HttpClientUtil.invokeGetNoEnCode("http://"+Constant.H2_IP+"/plugin/visitor.action?do=visitorAuth", map,"utf-8",5000);
					    	  try {
					              ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
					              JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
					              // 如果请求成功
					              if (jsonNode.has("result_code") && "0".equals(jsonNode.get("result_code").asText())) {
					            	  SysLog.info("车辆下发成功:"+sdf.format(LocalDate.now()));
					              }
					          } catch (IOException e) {
//					              e.printStackTrace();
					          }
			    		}
			    	}
			    }
			}
		 }else if(StringUtils.isNotEmpty(passEvent.getEqbrand()) && passEvent.getEqbrand().indexOf("HK")!=-1) {
			 
		 }
	 
	}
}
