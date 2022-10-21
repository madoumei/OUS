package com.client.bean;


import com.config.qicool.common.persistence.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class ReqEqptMonitorLog extends BaseEntity<EqptMonitorLog> {
	private static final long serialVersionUID = 12345678910L;
	private String uid;
	private int userid;
	private String errorName;
	private String errorInFo;
	private Date time;
	private String startDate;
	private String endDate;
	private int startIndex;
	private int requestedCount;
}
