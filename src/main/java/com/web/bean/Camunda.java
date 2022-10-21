package com.web.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ApiModel("Camunda 工作流Bean")
public class Camunda implements Serializable {
    private static final long serialVersionUID = 6016454117239214709L;

    @ApiModelProperty("审批标题")
    private String title;
    private int userid;
    private String taskid;
    private String processInstanceId;
    @ApiModelProperty("审批状态 0：审批中，1：同意，2：拒绝，3：退回，4：撤回，5：取消")
    private int status;    //0：审批中，1：同意，2：拒绝，3：撤回
    private String notifyType;
    @ApiModelProperty("提交人id")
    private String empid;
    private String openid;
    @ApiModelProperty("评论，原因")
    private String remark;
    @JsonProperty("pType")
    private int pType;
    private String businessKey;
    @ApiModelProperty("提交人id")
    private int subEmpId;
    @ApiModelProperty("提交人姓名")
    private String subEmpName;
    private Date submitTime;
    private String vid;
    @ApiModelProperty("部门领导empid")
    private String leader;
    @ApiModelProperty("所有审批访客数据,id列表")
    private List<String> allVisitors;
    @ApiModelProperty("新增审批访客数据")
    private List<String> addVisitors;
    @ApiModelProperty("删除审批访客数据")
    private List<String> delVisitors;
//    @ApiModelProperty("预约来访时间")
//    private Date appointmentDate;
//    @ApiModelProperty("结束来访时间")
//    private Date endDate;
//    @ApiModelProperty("来访有效期")
//    private int qrcodeConf;
//    @ApiModelProperty("区域")
//    private String area;
}
