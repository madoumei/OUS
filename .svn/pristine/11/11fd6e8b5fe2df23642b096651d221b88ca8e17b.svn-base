package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value = "Token 令牌token")
public class AuthToken {
    public static final String ROLE_HSE = "0";//HSE
    public static final String ROLE_SUPP_COMPANY = "1";//劳务公司
    public static final String ROLE_GATE = "2";//前台
    public static final String ROLE_LOGISTICS = "3";//物流管理员
    public static final String ROLE_MACHINE = "4";//访客机
    public static final String ROLE_SUBADMIN = "5";//子管理员
    public static final String ROLE_ADMIN = "6";//管理员
    public static final String ROLE_EMPLOYEE = "7";//员工
    public static final String ROLE_VISITOR = "8";//访客
    public static final String ROLE_SUPPLIER = "9";//供应商
    public static final String ROLE_SUB_COMPANY = "10";//入驻企业

    @ApiModelProperty(value = "认证账号id，员工=empid,供应商=rid,访客=vid,管理员=账号",example = "jake@360.com")
    private String loginAccountId;

    //0-hse     1-劳务公司      2-前台    3-物管    4-访客机账号     5-子账号
    @ApiModelProperty(value = "认证账号角色，  0-hse     1-劳务公司      2-前台    3-物管    4-访客机账号     5-子账号     6-管理员    7-员工   8-访客  9-常驻访客 10-入驻企业",example = "0")
    private String accountRole;

    @ApiModelProperty(value = "用户id",example = "214836")
    private int userid;

    @ApiModelProperty(value = "登录员工/访客的openid",example = "ov3v64uYWoMWo7OwrB2NFE")
    private String openid;

    @ApiModelProperty(value = "token生成时间",example = "1615537455853")
    private Long dateTime;

    private String token;

    public AuthToken(String loginAccountId, String accountRole, int userid, String openid,Long dateTime) {
        this.loginAccountId = loginAccountId;
        this.accountRole = accountRole;
        this.userid = userid;
        this.openid = openid;
        this.dateTime = dateTime;
    }

    public AuthToken() {
    }

}
