package com.client.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 试卷实体类
 * 
 * @author dingzhenzhen
 * @date 2019-05-23 9:58
 */
@Getter
@Setter
@ApiModel("Questionnaire 调查问卷Bean")
public class Questionnaire {
	@ApiModelProperty("题库id")
	private String qid;
	@ApiModelProperty("userid")
	private int userid;
	@ApiModelProperty("标题")
	private String title;
	@ApiModelProperty("描述")
	private String fdesc;
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("答题时间")
	private int ansTime;
	@ApiModelProperty("题目数")
	private String  topicNum;
	@ApiModelProperty("及格分数")
	private int topicRigNum;
}