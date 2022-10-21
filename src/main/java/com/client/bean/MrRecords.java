package com.client.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MrRecords {
	private int id;
	private int userid;
	private int mrid;
	private String name;
	private String address;
	private String openkey;
	private String lockName;
	private String communityCode;
	private String title;
	private int peopleCount;
	private String empName;
	private String empPhone;
	private Date startDate;
	private Date endDate;
	private float cost;
	private int status;
}
