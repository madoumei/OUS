package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("UpPassword 更新密码Bean")
public class UpPassword {
	@ApiModelProperty("userid")
	private int userid;
	@ApiModelProperty("账号")
	private String account;
	@ApiModelProperty("老密码")
	private String oldpwd;
	@ApiModelProperty("新密码")
	private String newpwd;
}
