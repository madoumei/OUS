package com.web.service.impl;

import com.web.bean.DaysOffTranslation;
import com.web.dao.DaysOffTranslationDao;
import com.web.service.DaysOffTranslationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DaysOffTranslationServerImpl implements DaysOffTranslationServer {

    @Autowired
    private DaysOffTranslationDao daysOffTranslationDao;

    @Override
    public int addDaysOffTranslation(DaysOffTranslation daysOffTranslation) {
        return daysOffTranslationDao.addDaysOffTranslation(daysOffTranslation);
    }

    @Override
    public int delDaysOffTranslation(DaysOffTranslation daysOffTranslation) {
        return daysOffTranslationDao.delDaysOffTranslation(daysOffTranslation);
    }

    @Override
    public List<DaysOffTranslation> getDaysOffTranslation(DaysOffTranslation dot) {
        return daysOffTranslationDao.getDaysOffTranslation(dot);
    }
}
