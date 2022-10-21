package com.web.dao;

import com.web.bean.DaysOffTranslation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface DaysOffTranslationDao {
    public int addDaysOffTranslation(DaysOffTranslation daysOffTranslation);

    int delDaysOffTranslation(DaysOffTranslation daysOffTranslation);

    List<DaysOffTranslation> getDaysOffTranslation(DaysOffTranslation dot);
}
