package com.web.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel("Employee 员工Bean")
public class Employee {
    private int empid;
    private int userid;
    private String pemail;
    @NotBlank(message = "openid不能为空")
    @ApiModelProperty("外部系统唯一标识")
    private String openid;
    private String ddid;

    @ApiModelProperty("姓名")
    @NotBlank(message = "empName不能为空")
    @Size(max = 80,message = "empName最大长度80")
    private String empName;

    @ApiModelProperty("工号")
    @Size(max = 50,message = "empNo最大长度50")
    private String empNo;

    @ApiModelProperty("邮箱")
    private String empEmail;

    @ApiModelProperty("手机号")
    @NotBlank(message = "empPhone不能为空")
    private String empPhone;
    private String empPwd;
    @ApiModelProperty("empVisitdate")
    private Date empVisitdate;
    private String empTeam;
    @ApiModelProperty("昵称")
    private String empNickname;
    private String empRtxAccount;
    @ApiModelProperty("性别 女,男")
    private String empSex;
    @ApiModelProperty("职务")
    private String empPosition;

    @Min(0)
    @Max(4)
    @ApiModelProperty("0：非默认,1：默认接待人 2:web端添加 3 pad端不展示，禁止预约 4停用")
    private int empType;
    @ApiModelProperty("默认接待的访客类型，empType=1时有效")
    private String visitType;
    @ApiModelProperty("默认接收人 1 开启，0 关闭")
    private int defaultNotify;

    @ApiModelProperty("入驻企业账号id")
    private int subaccountId;
    @ApiModelProperty("备注信息")
    @Size(max = 255,message = "remark最大长度255")
    private String remark;
    private String token;
    private int deptid;
    @ApiModelProperty("外部部门id列表")
    @JsonProperty
    @NotNull(message = "oDeptidList不能为空")
    private List<String> oDeptidList;
    private String deptName;
    @ApiModelProperty("工位")
    private String workbay;
    @ApiModelProperty("电话")
    private String telephone;
    private List<Integer> deptids;
    @ApiModelProperty("门禁卡号")
    private String cardNo;
    @ApiModelProperty("员工被授权的设备组")
    private String egids;
    @ApiModelProperty("开始日期 yyyyMMdd")
    @Pattern(regexp = "((\\d{3}[1-9]|\\d{2}[1-9]\\d|\\d[1-9]\\d{2}|[1-9]\\d{3})(((0[13578]|1[02])(0[1-9]|[12]\\d|3[01]))|((0[469]|11)(0[1-9]|[12]\\d|30))|(02(0[1-9]|[1]\\d|2[0-8]))))|(((\\d{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)",
            message = "时间格式应为：yyyyMMdd")
    private String startDate;
    @ApiModelProperty("结束日期 yyyyMMdd")
    @Pattern(regexp = "((\\d{3}[1-9]|\\d{2}[1-9]\\d|\\d[1-9]\\d{2}|[1-9]\\d{3})(((0[13578]|1[02])(0[1-9]|[12]\\d|3[01]))|((0[469]|11)(0[1-9]|[12]\\d|30))|(02(0[1-9]|[1]\\d|2[0-8]))))|(((\\d{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)",
            message = "时间格式应为：yyyyMMdd")
    private String endDate;

    @ApiModelProperty("头像链接")
    @Size(max = 512,message = "avatar最大长度512")
    private String avatar;

    @ApiModelProperty("0 头像识别成功 -9 头像识别失败")
    private int face;

    @ApiModelProperty("车牌号")
    @Size(max = 150,message = "plateNum最大长度150")
    private String plateNum;
    @ApiModelProperty("员工可以授权的访客设备组")
    private String vegids;
    @ApiModelProperty("绑定门岗")
    private int gid;
    @ApiModelProperty("免责条款 0不同意  1同意")
    private int ecStatus;
    private String companyName;
    @ApiModelProperty("是否可邀请，0-可以，1-拒绝")
    private int appType;
    @ApiModelProperty("是否为领导")
    private Boolean isLeader;
    public Employee() {
    }


    /**
     * 如果对象类型是Employee 的话 则返回true 去比较hashCode值
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof Employee) {
            Employee emp = (Employee) obj;
            if (null == emp.getOpenid() || "".equals(emp.getOpenid())) {
                if (emp.empid == this.empid) return true; // 只比较id
            } else {
                if (emp.openid.equals(this.openid)) return true;
            }
            // 比较id和username 一致时才返回true 之后再去比较 hashCode
            // if(emp.ddid == this.ddid && emp.empName.equals(this.empName)) return true;
        }
        return false;
    }


    /**
     * 重写hashcode 方法，返回的hashCode 不一样才认定为不同的对象
     */
    @Override
    public int hashCode() {
        if (null == openid || "".equals(openid)) {
            return empid;
        } else {
            return openid.hashCode(); // 只比较id，id一样就不添加进集合
        }
        //   return ddid.hashCode() * empName.hashCode();
    }


}
