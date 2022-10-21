package com.event.listener.passListen;

import java.util.HashMap;
import java.util.Map;

import com.annotation.ProcessLogger;
import com.utils.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.client.bean.Visitor;
import com.config.activemq.MessageSender;
import com.event.event.PassEvent;
import com.web.bean.Appointment;
/**
 * 下发人脸
 */
@Component
public class FacePassListen {
	 @Autowired
     private MessageSender messageSender;

	 @EventListener(value = PassEvent.class,condition = "#passEvent.eqType.contains('0')")
	 public void listener(PassEvent passEvent) {
		 if(null!=passEvent.getVtList() && passEvent.getVtList().size()>0) {
			for(Visitor visitor:passEvent.getVtList()) {
				 if(passEvent.getOpType()==PassEvent.Pass_Add) {
				   Map<String, Object> map = new HashMap<String, Object>();
		           map.put("key", "visitor_update");
		           map.put("visit_id", "v" + visitor.getVid());
		           map.put("company_id", visitor.getUserid());
		           messageSender.updateFaceLib(map);
				 }else if(passEvent.getOpType()==PassEvent.Pass_Del) {
				 	Map<String, Object> map = new HashMap<String, Object>();
		            map.put("key", "visitor_delete");
		            map.put("company_id", visitor.getUserid());
		            map.put("visit_id", "v" + visitor.getVid());
		            messageSender.updateFaceLib(map);
				 }else {
					Map<String, Object> map = new HashMap<String, Object>();
			        map.put("key", "visitor_update");
		            map.put("visit_id", "v" + visitor.getVid());
			        map.put("company_id", visitor.getUserid());
			        messageSender.updateFaceLib(map);
				 }
			}
	 }else if(null!=passEvent.getAppList() && passEvent.getAppList().size()>0) {
			for(Appointment app:passEvent.getAppList()) {
			 if(passEvent.getOpType()==PassEvent.Pass_Add) {
			    Map<String, Object> map = new HashMap<String, Object>();
	            map.put("key", "visitor_update");
	            map.put("visit_id", "a" + app.getId());
	            map.put("company_id", app.getUserid());
	            messageSender.updateFaceLib(map);
			 }else if(passEvent.getOpType()==PassEvent.Pass_Del) {
			 	Map<String, Object> map = new HashMap<String, Object>();
	            map.put("key", "visitor_delete");
	            map.put("visit_id", "a" + app.getId());
	            map.put("company_id", app.getUserid());
	            messageSender.updateFaceLib(map);
			 }else {
				Map<String, Object> map = new HashMap<String, Object>();
	            map.put("key", "visitor_update");
	            map.put("visit_id", "a" + app.getId());
	            map.put("company_id", app.getUserid());
	            messageSender.updateFaceLib(map);
			 }
			}
		 }
}
	
}
