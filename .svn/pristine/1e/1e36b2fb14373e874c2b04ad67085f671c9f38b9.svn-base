package com.client.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.Questionnaire;
import com.client.bean.QuestionnaireDetail;
import com.client.bean.ReqQuestion;
import com.client.bean.ReqQuestionDetail;
import com.client.bean.VisitorAnswer;
import com.client.dao.QuestionnaireDao;
import com.client.service.QuestionnaireService;

@Service("questionnaireService")
public class QuestionnaireServiceImpl implements QuestionnaireService {
	@Autowired
	private QuestionnaireDao questionnaireDao;

	@Override
	public int addQuestionnaire(Questionnaire que) {
		// TODO Auto-generated method stub
		return questionnaireDao.addQuestionnaire(que);
	}

	@Override
	public int updateQuestionnaire(Questionnaire que) {
		// TODO Auto-generated method stub
		return questionnaireDao.updateQuestionnaire(que);
	}

	@Override
	public int delQuestionnaire(Questionnaire que) {
		// TODO Auto-generated method stub
		return questionnaireDao.delQuestionnaire(que);
	}

	@Override
	public List<Questionnaire> getQuestionnaire(ReqQuestion rque) {
		// TODO Auto-generated method stub
		return questionnaireDao.getQuestionnaire(rque);
	}

	@Override
	public VisitorAnswer getAnswerByIdentity(VisitorAnswer va) {
		// TODO Auto-generated method stub
		return questionnaireDao.getAnswerByIdentity(va);
	}

	@Override
	public int addAnswer(VisitorAnswer va) {
		// TODO Auto-generated method stub
		return questionnaireDao.addAnswer(va);
	}

	@Override
	public int addQuestionnaireDetail(QuestionnaireDetail queDetailBean) {
		// TODO Auto-generated method stub
		return questionnaireDao.addQuestionnaireDetail(queDetailBean);
	}

	@Override
	public List<QuestionnaireDetail> getQuestionnaireDetail(ReqQuestionDetail rque) {
		// TODO Auto-generated method stub
		return questionnaireDao.getQuestionnaireDetail(rque);
	}

	@Override
	public int updateQuestionnaireDetail(QuestionnaireDetail que) {
		// TODO Auto-generated method stub
		return questionnaireDao.updateQuestionnaireDetail(que);
	}

	@Override
	public int delQuestionnaireDetail(QuestionnaireDetail que) {
		// TODO Auto-generated method stub
		return questionnaireDao.delQuestionnaireDetail(que);
	}

	@Override
	public int addExamResult(VisitorAnswer visAnswer) {
		// TODO Auto-generated method stub
		return questionnaireDao.addExamResult(visAnswer);  
	}

	@Override
	public Questionnaire getQueByQid(ReqQuestion rq) {
		// TODO Auto-generated method stub
		return questionnaireDao.getQueByQid(rq); 
	}

	@Override
	public int batchAddQuestionnaireDetail(List<QuestionnaireDetail> qlist) {
		// TODO Auto-generated method stub
		return questionnaireDao.batchAddQuestionnaireDetail(qlist);
	}

	@Override
	public int updateExamResult(VisitorAnswer visAnswer) {
		// TODO Auto-generated method stub
		return questionnaireDao.updateExamResult(visAnswer);
	}

	@Override
	public List<QuestionnaireDetail> getRandomTopic(
			Map<String, Object> conditions) {
		// TODO Auto-generated method stub
		return questionnaireDao.getRandomTopic(conditions);
	}

	@Override
	public int delQuestionnaireByQid(Questionnaire que) {
		// TODO Auto-generated method stub
		return questionnaireDao.delQuestionnaireByQid(que);
	}

}
