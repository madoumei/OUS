package com.client.bean;


import com.config.qicool.common.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqEM extends BaseEntity<EqptMonitor> {

	private static final long serialVersionUID = 8424195758027517641L;
	
	private int startIndex;
	private int requestedCount;
	private int userid;
	private String deviceName;
	private String eType;
	private int eStatus;
}
