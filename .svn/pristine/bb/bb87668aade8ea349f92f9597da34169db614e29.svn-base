package com.client.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@TableName("gis_track")
@ApiModel("Track 运动轨迹坐标Bean")
public class Track {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int userid;
    private String vid;
    @TableField("openid")
    @ApiModelProperty("小程序openid")
    private String openId;

    @ApiModelProperty("轨迹采集时间")
//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("createTime")
    private Date createTime;
    private BigDecimal longitude;
    private BigDecimal latitude;

    @ApiModelProperty("访客姓名")
    @TableField(exist = false)
    private String name;
}
