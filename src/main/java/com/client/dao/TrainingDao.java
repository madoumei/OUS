package com.client.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.client.bean.Training;


@Mapper
public interface TrainingDao {
    public int addTrainingVideo(Training t);

    public int updateTrainingVideo(Training t);

    public List<Training> getTrainingVideo(Training t);

    public int delTrainingVideo(Training t);
}
