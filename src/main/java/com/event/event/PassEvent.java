package com.event.event;

import java.util.List;

import com.client.bean.Visitor;
import com.web.bean.Appointment;
import com.web.bean.Employee;
import com.web.bean.Person;
import com.web.bean.UserInfo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("通行事件")
public class PassEvent {
	/**
	 * 操作类型 
	 */
	 public final static int Pass_Add=1;
	 public final static int Pass_Mod=2;
	 public final static int Pass_Del=3;
	
     private List<Visitor> vtList;
     private List<Appointment> appList;
     private UserInfo userinfo;
     private String eqType;//设备类型 0 人脸,1 二维码,2 车辆,3 ic卡
     private int opType;//操作类型
     private String eqbrand;//设备品牌
}
