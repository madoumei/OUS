package com.client.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName(value ="qcv_logistics")
@Data
@ApiModel("ogistics 物流记录Bean")
public class Logistics {
    @ApiModelProperty("物流id")
    @TableId(type = IdType.AUTO)
    private int sid;
    private int userid;

    //private String sname;
    //private String smobile;
    //private String scardId;
    @ApiModelProperty("司机姓名")
    private String dname;
    @ApiModelProperty("司机手机号")
    private String dmobile;
    @ApiModelProperty("司机身份证号")
    private String dcardId;
    @ApiModelProperty("司机信息封装JSON")
    private String driverExtend;
    @ApiModelProperty("物流信息封装")
    private String logExtend;
    @ApiModelProperty("车牌号")
    private String plateNum;
    @ApiModelProperty("车牌号封装扩展字段")
    private String vehicleExtend;
    @ApiModelProperty("货物明细")
    private String goodsPhoto;
    @ApiModelProperty("健康码")
    private String healthPhoto;
    @ApiModelProperty("行程码")
    private String journPhoto;
    @ApiModelProperty("核酸报告")
    private String nucleicPhoto;
    @ApiModelProperty("供应商公司")
    private String company;
    @ApiModelProperty("物流类型")
    private String logType;
    @ApiModelProperty("道口")
    private int rid;
    @ApiModelProperty("预约时间")
    private Date appointmentDate;
    @ApiModelProperty("到达时间")
    private Date visitdate;
    @ApiModelProperty("离开时间")
    private Date leaveTime;
    @ApiModelProperty("结束时间")
    private Date finishTime;
    @ApiModelProperty("车辆状态：1已预约2已进场等待叫号3等待装卸4装卸完成5已离场6过号")
    private int pstatus;
    private String logNum;
    private String inspectExtend;
   // private String memberInfo;
    @ApiModelProperty("其他事宜")
    private String remark;

}
