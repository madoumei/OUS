package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel("Holiday 节假日Bean")
public class Holiday {
	@ApiModelProperty("假日id")
	private String hid;
	@ApiModelProperty("userid")
	private int userid;
	@ApiModelProperty("日期")
	private Date hdate;
	@ApiModelProperty("备注")
	private String remark;
	@ApiModelProperty("0 休息日 1 工作日")
	private int passFlag;
	@ApiModelProperty("规则id")
	private int rid;

}
