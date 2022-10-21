package com.web.service;

import com.web.bean.DaysOffTranslation;

import java.util.List;

public interface DaysOffTranslationServer {
    public int addDaysOffTranslation(DaysOffTranslation daysOffTranslation);

    int delDaysOffTranslation(DaysOffTranslation daysOffTranslation);

    List<DaysOffTranslation> getDaysOffTranslation(DaysOffTranslation dot);
}
