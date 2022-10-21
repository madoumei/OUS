package com.web.bean;


import com.config.qicool.common.persistence.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@ApiModel("ReqOperateLog 日志请求类Bean")
public class ReqOperateLog extends BaseEntity<OperateLog> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6480558394575685046L;
	@ApiModelProperty("用户id")
	private int userid;
	@ApiModelProperty("起始")
	private int startIndex;
	@ApiModelProperty("请求总数")
	private int requestedCount;
	@ApiModelProperty("操作对象名称")
	private String optName;
	@ApiModelProperty("操作模块 0=登录模块，1=员工，2=账号，6=黑名单，7=访客，8=消息，20=其他")
	private String optModule;
	@ApiModelProperty("开始时间")
	private String startDate;
	@ApiModelProperty("结束时间")
	private String endDate;
}
