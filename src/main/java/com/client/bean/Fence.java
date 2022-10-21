package com.client.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.awt.geom.Point2D;

@Setter
@Getter
@TableName("qcv_fence")
@ApiModel("Fence 电子围栏Bean")
public class Fence {
    private int id;
    private String name;
    private int userid;
    @ApiModelProperty("围栏坐标点，json数组格式的，[{\"longitude\":\"\",\"latitude\":\"\"}]")
    private String range;
}
