package com.client.bean;


import com.config.qicool.common.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Renews extends BaseEntity<Renews> {
	private static final long serialVersionUID = 5354068157242390206L;
	
	private int rid;
	private int nid;
	private String content;
	private Date rdate;
	private int empid;
	private String empPhone;
	private int status;
	private int startIndex;
	private int requestedCount;
	private String pcompany;
	private String nickname;
	private String pname;
	private String avatar;
	

}
