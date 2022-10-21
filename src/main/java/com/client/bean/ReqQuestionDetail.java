package com.client.bean;


import com.config.qicool.common.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqQuestionDetail extends BaseEntity<QuestionnaireDetail> {
	private static final long serialVersionUID = -6821204711675340376L;
	private int startIndex;// 开始行数
	private int requestedCount;// 请求条数
	private String qid;//试卷主键
	private int userid;
	private String question;
}
