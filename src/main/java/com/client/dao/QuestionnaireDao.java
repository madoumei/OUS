package com.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.client.bean.Questionnaire;
import com.client.bean.QuestionnaireDetail;
import com.client.bean.ReqQuestion;
import com.client.bean.ReqQuestionDetail;
import com.client.bean.VisitorAnswer;

/**
 * 
 * @author dingzhenzhen
 * @date 2019.5.22
 */
@Mapper
public interface QuestionnaireDao {
	/**
	 * 添加题库
	 * 
	 * @param que
	 * @return
	 */
	public int addQuestionnaire(Questionnaire que);

	/**
	 * 更新题库
	 * 
	 * @param que
	 * @return
	 */
	public int updateQuestionnaire(Questionnaire que);

	/**
	 * 删除题库
	 * 
	 * @param que
	 * @return
	 */
	public int delQuestionnaire(Questionnaire que);

	/**
	 * 查询题库列表
	 * 
	 * @param rque
	 * @return
	 */
	public List<Questionnaire> getQuestionnaire(ReqQuestion rque);
	
	/**
	 * 通过主键查询试卷设置明细
	 * @param qid
	 * @return
	 */
	public Questionnaire getQueByQid(ReqQuestion rq);

	/**
	 * 获得指定试卷的答案列表
	 * 
	 * @param va
	 * @return
	 */
	public VisitorAnswer getAnswerByIdentity(VisitorAnswer va);

	/**
	 * 根据客户的回答新增答案
	 * 
	 * @param va
	 * @return
	 */
	public int addAnswer(VisitorAnswer va);

	/**
	 * 添加指定题库下的题目
	 * 
	 * @param queDetailBean
	 * @return
	 */
	public int addQuestionnaireDetail(QuestionnaireDetail queDetailBean);

	/**
	 * 更新题目
	 * 
	 * @param que
	 * @return
	 */
	public int updateQuestionnaireDetail(QuestionnaireDetail que);
	
	
	/**
	 * 删除所属类型题目
	 * 
	 * @param que
	 * @return
	 */
	
	public int delQuestionnaireByQid(Questionnaire que);

	/**
	 * 删除题目
	 * 
	 * @param que
	 * @return
	 */
	public int delQuestionnaireDetail(QuestionnaireDetail que);

	/**
	 * 题目列表
	 * 
	 * @param rque
	 * @return
	 */
	public List<QuestionnaireDetail> getQuestionnaireDetail(ReqQuestionDetail rque);
	
	/**
	 * 提交试卷，将考试结果入库
	 * @param que
	 * @return
	 */
	public int addExamResult(VisitorAnswer visAnswer);
	
	/**
	 * 批量插入题目
	 * @param List
	 * @return
	 */
	public int batchAddQuestionnaireDetail(List<QuestionnaireDetail> qlist);
	
	/**
	 * 更新考试结果
	 * @param List
	 * @return
	 */
	public int updateExamResult(VisitorAnswer visAnswer);
	/**
	 * 随机获取题目
	 * @param List
	 * @return
	 */
	public List<QuestionnaireDetail> getRandomTopic(Map<String,Object> conditions);

}
