package com.client.bean;

import java.util.Date;

import com.config.qicool.common.persistence.BaseEntity;
import com.config.qicool.common.persistence.Page;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class News extends BaseEntity<News> {
	private static final long serialVersionUID = 2602865493301889562L;
	
	private int nid;
	private String title;
	private String content;
	private String picture;
	private Date ndate;
	private Date rdate;
	private int empid;
	private String empPhone;
	private int brcount;
	private int recount;
	private int cid;
	private int status;
	private int startIndex;
	private int requestedCount;
	private Page<Renews> rpage;
	private String pcompany;
	private String nickname;
	private String pname;
	private String avatar;
	private int userid;
	

}
