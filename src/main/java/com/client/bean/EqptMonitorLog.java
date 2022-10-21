package com.client.bean;


import lombok.Data;

import java.util.Date;
@Data
public class EqptMonitorLog {
	private String uid;
	private int userid;
	private String errorName;
	private String errorInFo;
	private Date time;
}
