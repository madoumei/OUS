package com.web.bean.VO;

import com.config.qicool.common.persistence.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
@ApiModel("MeetingVO 会议VO")
@Data
public class MeetingVO extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "会议ID")
    private int mid;
    @ApiModelProperty(value = "员工编号")
    private int empid;
    @ApiModelProperty(value = "创建者名称")
    private String name;
    @ApiModelProperty(value = "创建者手机号")
    private String phone;
    @ApiModelProperty(value = "创建者邮箱")
    private String email;
    @ApiModelProperty(value = "企业ID")
    private int userid;
    @ApiModelProperty(value = "会议名称")
    private String subject;
    @ApiModelProperty(value = "日期")
    private Date mdate;
    @ApiModelProperty(value = "发起方")
    private String sponsor;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "会议状态:0取消1正常2开始3结束")
    private int status;
    @ApiModelProperty(value = "会议邀请函详细内容")
    private String inviteContent;
    @ApiModelProperty(value = "会议地址")
    private String address;
    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "维度")
    private String latitude;
    @ApiModelProperty(value = "公司简介")
    private String companyProfile;
    @ApiModelProperty(value = "交通信息")
    private String traffic;
    @ApiModelProperty(value = "会议开始时间")
    private String startTime;
    @ApiModelProperty(value = "会议结束时间")
    private String endTime;
    private int startIndex;
    private int requestedCount;
    @ApiModelProperty(value = "会议预约截止时间")
    private String deadlineTime;
    @ApiModelProperty(value = "会议室编号")
    private int mrid;
    @ApiModelProperty(value = "会议室开始使用时间")
    private String roomStartTime;
    @ApiModelProperty(value = "会议室使用结束时间")
    private String roomEndTime;
}
