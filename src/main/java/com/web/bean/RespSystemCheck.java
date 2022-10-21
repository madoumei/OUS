package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统自检信息
 */
@ApiModel("RespSystemCheck 自检结果")
@Data
public class RespSystemCheck {
    public static int ERROR_CHECK_UNPASS = 4;
    public static int ERROR_CHECK_FAILED = 3;
    public static int ERROR_CHECK_MANUAL = 2;
    public static int ERROR_CHECK_NOTHING = 1;
    public static int ERROR_CHECK_PASS = 0;

    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("错误状态 4=检查失败，3=检查未通过，2=人工检测项目 1=未检测， 0=检查通过")
    private int error;
    @ApiModelProperty("描述")
    private String description;

    public RespSystemCheck(int error, String title, String description){
        this.error = error;
        this.title = title;
        this.description = description;
    }
}
