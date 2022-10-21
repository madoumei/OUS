package com.client.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("图像验证码")
public class ValCode {
    @ApiModelProperty("base64字符串")
    private String base64Str;
	@ApiModelProperty("code")
    private String vcode;
    private String digest;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
}
