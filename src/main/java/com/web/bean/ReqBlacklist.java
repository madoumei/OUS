package com.web.bean;

import com.config.qicool.common.persistence.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@ApiModel("ReqBlacklist 请求黑名单Bean")
public class ReqBlacklist extends BaseEntity {
    /**
     *
     */
    private static final long serialVersionUID = 6113049080635244421L;
    @ApiModelProperty("userid")
    private int userid;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("手机号")
    private String phone;
    private String credentialNo;
    private List<String> bids;
    @ApiModelProperty("开始索引")
    private int startIndex;
    @ApiModelProperty("请求总数")
    private int requestedCount;
    private String account;
    private String gids;
    private String sids;
    private String gname;
    private String sname;
    @ApiModelProperty("搜索条件")
    private String condition;
    private String loginAccount;

}
