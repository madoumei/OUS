package com.client.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.client.bean.Training;
import com.client.dao.TrainingDao;
import com.client.service.TrainingService;

@Service("trainingService")
public class TrainingServiceImpl implements TrainingService{
	@Autowired
	private TrainingDao trainingDao;

	@Override
	public int addTrainingVideo(Training t) {
		// TODO Auto-generated method stub
		return trainingDao.addTrainingVideo(t);
	}

	@Override
	public int updateTrainingVideo(Training t) {
		// TODO Auto-generated method stub
		return trainingDao.updateTrainingVideo(t);
	}

	@Override
	public List<Training> getTrainingVideo(Training t) {
		// TODO Auto-generated method stub
		return trainingDao.getTrainingVideo(t);
	}

	@Override
	public int delTrainingVideo(Training t) {
		// TODO Auto-generated method stub
		return trainingDao.delTrainingVideo(t);
	}

}
