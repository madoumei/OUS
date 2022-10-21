package com.client.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataStatistics {
	private int userid;
	private int weixin;
	private int dingding;
	private int rtx;
	private int email;
	private int sms;
	private int ivr;
	private int totalVisitor;
	private int totalUser;
	private int totalCompany;
	private int totalEmp;
	private int sevenDayVcount;
	private int thirtyDayVcount;
	private int nowVCount; //正在拜访人数
	private int signOutCount; // 签离人数
	private int arrivedCount; //签到人数
	private int noArrivedCount;//未到人数
}
