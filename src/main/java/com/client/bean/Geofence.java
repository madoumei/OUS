package com.client.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Setter
@Getter
@TableName("gis_geofence")
@ApiModel("Geofence 电子围栏Bean")
public class Geofence {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int userid;

    @ApiModelProperty("围栏名称")
    private String name;

    @ApiModelProperty("围栏数据点 Polyline'0 0,0 10,10 0'")
    private String points;

    @ApiModelProperty("围栏类型,polyline")
    private String type;

    @ApiModelProperty("父电子围栏id")
    private int parentid;
}
