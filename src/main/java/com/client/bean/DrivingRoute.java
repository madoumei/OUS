package com.client.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@TableName("gis_route")
@ApiModel("DrivingRoute 规划路径Bean")
public class DrivingRoute {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int userid;

    @ApiModelProperty("路径名称")
    private String name;
    @ApiModelProperty("路径备注信息，比如：限高3米，最长9米车辆，步行")
    private String remark;

    @ApiModelProperty("路径数据点 '0 0,0 10,10 0'")
    private String points;

}
