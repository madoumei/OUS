package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel("EmpVisitProxy 员工访客代理Bean")
public class EmpVisitProxy {
	@ApiModelProperty("userid")
	private int userid;
	@ApiModelProperty("员工id")
	private int empid;
	@ApiModelProperty("员工姓名")
	private String empName;
	@ApiModelProperty("代理人员工id")
	private int proxyId;
	@ApiModelProperty("代理人姓名")
	private String proxyName;
	@ApiModelProperty("代理人类型")
	private int proxyType;
	@ApiModelProperty("代理状态，0-停用；1-启用 ")
	private int proxyStatus;
	@ApiModelProperty("代理起始时间")
	private Date startDate;
	@ApiModelProperty("代理结束时间")
	private Date endDate;
}
