package com.client.service;

import java.util.List;
import java.util.Map;

import com.client.bean.Questionnaire;
import com.client.bean.QuestionnaireDetail;
import com.client.bean.ReqQuestion;
import com.client.bean.ReqQuestionDetail;
import com.client.bean.VisitorAnswer;

/**
 * 试卷管理接口
 * 
 * @author dingzhenzhen
 * @Date 2019-05-22 14:18
 */
public interface QuestionnaireService {
	/**
	 * 设置试卷标题
	 * 
	 * @param que
	 * @return
	 */
	public int addQuestionnaire(Questionnaire que);

	/**
	 * 更新试卷
	 * 
	 * @param que
	 * @return
	 */
	public int updateQuestionnaire(Questionnaire que);

	/**
	 * 删除试卷
	 * 
	 * @param que
	 * @return
	 */
	public int delQuestionnaire(Questionnaire que);

	/**
	 * 查询试卷列表
	 * 
	 * @param rque
	 * @return
	 */
	public List<Questionnaire> getQuestionnaire(ReqQuestion rque);

	/**
	 * 获取访客试卷答案
	 * 
	 * @param va
	 * @return
	 */
	public VisitorAnswer getAnswerByIdentity(VisitorAnswer va);

	/**
	 * 新增试卷答案
	 * 
	 * @param va
	 * @return
	 */
	public int addAnswer(VisitorAnswer va);

	/**
	 * 新增试卷题目
	 * 
	 * @param queDetailBean
	 * @return
	 */
	public int addQuestionnaireDetail(QuestionnaireDetail queDetailBean);

	/**
	 * 更新试卷题目
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
	 * 删除试卷
	 * 
	 * @param que
	 * @return
	 */
	public int delQuestionnaireDetail(QuestionnaireDetail que);

	/**
	 * 查询试卷列表
	 * 
	 * @param rque
	 * @return
	 */
	public List<QuestionnaireDetail> getQuestionnaireDetail(ReqQuestionDetail rque);
	
	/**
	 * 通过主键查询试卷设置明细
	 * @param qid
	 * @return
	 */
	public Questionnaire getQueByQid(ReqQuestion rq);
	
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
