package com.client.dao;

import com.client.bean.Satisfaction;
import com.client.bean.SatisfactionQuestionnaire;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SatisfactionDao {

    int addSatisfaction(Satisfaction satisfaction);

    int updateSatisfaction(Satisfaction sa);

    Satisfaction getSatisfactionByUserid(Satisfaction sa);

    int addSatisfactionQuestionnaire(SatisfactionQuestionnaire sq);

    int deleteSatisfactionQuestionnaire(SatisfactionQuestionnaire sq);

    SatisfactionQuestionnaire getSatisfactionQuestionnaire(SatisfactionQuestionnaire sq);

    List<SatisfactionQuestionnaire> getSatisfactionQuestionnaireByUserid(SatisfactionQuestionnaire sq);

    int updateSatisfactionQuestionnaire(SatisfactionQuestionnaire sq);
}
