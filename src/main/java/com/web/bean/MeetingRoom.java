package com.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRoom {
	private int id;
	private int userid;
	private String name;
	private String address;
	private String size;
	private String deviceCode;
	private float price;
	private int prematureDays;
	private int latestTime;
	private String remark;
}
