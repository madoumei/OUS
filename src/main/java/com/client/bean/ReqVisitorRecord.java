package com.client.bean;

import com.config.qicool.common.persistence.BaseEntity;
import com.config.qicool.common.persistence.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class ReqVisitorRecord extends BaseEntity<VisitorRecord> {
    private int startIndex;
    private int requestedCount;
    private Page<Renews> rpage;
    private int userid;
    private String date;
    private String endDate;
    private int subaccountId;               //子公司id
    private String appDate;                //预约日期
    private String appVisitTime;           //预约拜访时间
    private String waitPermissionSeconds;  //授权等待时长(秒)
    private String floors;              //楼层
    private String company;             //大楼租房名称(公司名称)
    private String name;               //访客姓名
    private String phone;              //访客手机号
    private String visitType;           //访问目的
    private String isBlackList;         //是否黑名单
    private String bCompany;            //黑名单公司
    private String  clientNo;           //预约途径
    private String sex;                 //访客性别
    private String peopleCount;         //访问人数
    private int visitorCount;           //拜访总次数
    private String permissionName;      //授权人姓名
    private String isTakeCard;          //是否取卡
    private String cardNo;              //访客门禁卡号
    private String isWeekendVisitor;    //是否周末时间拜访
    private String isHolidayVisitor;    //是否节假日时间拜访
    private String isSCTimeVisitor;     //是否梯控时间拜访
    private String isDaysOffVisitor;     //是否调休日时间拜访
    private String empName;             //员工姓名
    private String empPhone;            //员工手机号
    private String email;               //访客邮箱
    private String vcompany;            //访客公司
    private String signInGate;          //签到门岗
    private String vType;               //访客类型
    private String gid;                 //门禁id
    private Integer category;           //请求类别：0 全部；1 已到
    private String[] gateName;
    private String[] floor;             //用于搜索多楼层
    private List<Integer> gidlist;

}
