package com.client.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VisitorRecord {
    private String appDate;                //预约日期
    private String appVisitTime;           //预约拜访时间
    private String waitPermissionSeconds;  //授权等待时长(秒)
    private String empName;  //授权等待时长(秒)
    private String floors;              //楼层
    private String company;             //大楼租房名称(公司名称)
    private String vName;               //访客姓名
    private String vPhone;              //访客手机号
    private String visitType;           //访问目的
    private String isBlackList;         //是否黑名单
    private String bCompany;            //黑名单公司
    private String visitdate;           //签到时间
    private String  clientNo;           //预约途径
    private String sex;                 //访客性别
    private int memberCount;            //团队人数
    private int visitorCount;           //拜访总次数
    private String isTakeCard;          //是否取卡
    private String permissionName;      //授权人姓名
    private String cardNo;              //访客门禁卡号
    private String isWeekendVisitor;    //是否周末时间拜访
    private String isHolidayVisitor;    //是否节假日时间拜访
    private String isSCTimeVisitor;     //是否梯控时间拜访
    private String isDaysOffVisitor;     //是否调休日时间拜访
    private String peopleCount;         //访问人数
    private String gid;                 //门岗/塔楼
    private Date createTime;
}
