package com.client.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VisitorAnswer {
	private String  identity;//主键
	private int userid;//公司id
	private String name;//访客名字
	private int correct;//得分
	private int rcount;//答对数量
	private Date passDate;//通过时间
	public int ifPass;//是否及格 1:及格；0：不及格
	public Date endDate;//有效期结束日期
}
