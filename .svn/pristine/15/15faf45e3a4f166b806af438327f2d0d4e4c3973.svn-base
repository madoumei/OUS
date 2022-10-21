package com.web.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@TableName("qcv_eqptgroup_passtime_relation")
@ApiModel("设备组和通行时间表之间规则 Bean")
public class EqptGroupPassTimeRlt {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int userid;
    @ApiModelProperty("设备组")
    private String egids;
    @ApiModelProperty("通行时刻表")
    private String rids;
    private int status=1;
}
