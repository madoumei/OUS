package com.web.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("qcv_eqpt_rule")
@ApiModel("设备潮汐策略 Bean")
public class EqptRule {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int userid;
    @ApiModelProperty("设备组")
    private String eids;
    private String rname;
    @ApiModelProperty("策略执行时间段")
    private String time;
    private int active;

    @TableField("startDate")
    @ApiModelProperty("开始日期，null表示长期有效")
    private Date startDate;

    @TableField("endDate")
    @ApiModelProperty("结束日期，null表示长期有效")
    private Date endDate;

}
