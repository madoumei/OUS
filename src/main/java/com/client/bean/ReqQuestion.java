package com.client.bean;

import com.config.qicool.common.persistence.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("ReqQuestion 请求问卷Bean")
public class ReqQuestion extends BaseEntity<Questionnaire> {
    private static final long serialVersionUID = -8420498298266384010L;
    @ApiModelProperty("开始行数")
    private int startIndex;
    @ApiModelProperty("请求条数")
    private int requestedCount;
    @ApiModelProperty("公司Id")
    private int userid;
    @ApiModelProperty("问卷id")
    private String qid;

}
