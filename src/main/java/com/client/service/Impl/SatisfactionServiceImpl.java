package com.client.service.Impl;

import com.client.bean.Satisfaction;
import com.client.bean.SatisfactionQuestionnaire;
import com.client.dao.SatisfactionDao;
import com.client.service.SatisfactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("SatisfactionService")
public class SatisfactionServiceImpl implements SatisfactionService {

    @Autowired
    private SatisfactionDao satisfactionDao;

    @Override
    public int addSatisfaction(Satisfaction satisfaction) {
        return satisfactionDao.addSatisfaction(satisfaction);
    }

    @Override
    public int addSatisfactionQuestionnaire(SatisfactionQuestionnaire sq) {
        return satisfactionDao.addSatisfactionQuestionnaire(sq);
    }

    @Override
    public int updateSatisfaction(Satisfaction sa) {
        return satisfactionDao.updateSatisfaction(sa);
    }

    @Override
    public Satisfaction getSatisfactionByUserid(Satisfaction sa) {
        return satisfactionDao.getSatisfactionByUserid(sa);
    }

    @Override
    public int deleteSatisfactionQuestionnaire(SatisfactionQuestionnaire sq) {
        return satisfactionDao.deleteSatisfactionQuestionnaire(sq);
    }

    @Override
    public SatisfactionQuestionnaire getSatisfactionQuestionnaire(SatisfactionQuestionnaire sq) {
        return satisfactionDao.getSatisfactionQuestionnaire(sq);
    }

    @Override
    public List<SatisfactionQuestionnaire> getSatisfactionQuestionnaireByUserid(SatisfactionQuestionnaire sq) {
        return satisfactionDao.getSatisfactionQuestionnaireByUserid(sq);
    }

    @Override
    public int updateSatisfactionQuestionnaire(SatisfactionQuestionnaire sq) {
        return satisfactionDao.updateSatisfactionQuestionnaire(sq);
    }
}
