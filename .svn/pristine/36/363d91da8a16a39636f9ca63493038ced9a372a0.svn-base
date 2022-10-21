package com.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.annotation.ProcessLogger;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.ResidentProject;
import com.web.bean.ResidentVisitor;
import com.web.bean.RvReport;
import com.web.dao.ResidentVisitorDao;
import com.web.service.ResidentVisitorService;


@Service("residentVisitorService")
public class ResidentVisitorServiceImpl implements ResidentVisitorService{
	@Autowired
	private ResidentVisitorDao residentVisitorDao;

	/**
	 * 检查供应商有效性，并返回数据结构
	 * @param rid
	 * @return
	 */
	@ProcessLogger("检查供应商有效性，并返回数据结构")
	@Override
	public ResidentVisitor checkResidentVisitorTask(String rid){
		if (rid.startsWith("C")){
			rid = rid.substring(1);
		}
		ResidentVisitor rv = new ResidentVisitor();
		rv.setUserid(0);
		rv.setRid(rid);
		rv = getResidentVisitorByRid(rv);
		if(rv.getRstatus()!=1) {
			throw new ErrorException(ErrorEnum.E_1121);
		}

		//检查供应商授权日期
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		int startdate = Integer.parseInt(rv.getStartDate().replaceAll("-", ""));
		int now = Integer.parseInt(sdf2.format(new Date()));
		int enddate = Integer.parseInt(rv.getEndDate().replaceAll("-", ""));
		if (now < startdate || now > enddate) {
			throw new ErrorException(ErrorEnum.E_1115);
		}
		return rv;
	}

	@Override
	public int addProject(ResidentProject rp) {
		// TODO Auto-generated method stub
		return residentVisitorDao.addProject(rp);
	}

	@Override
	public int updateProject(ResidentProject rp) {
		// TODO Auto-generated method stub
		return residentVisitorDao.updateProject(rp);
	}

	@Override
	public List<ResidentProject> getProject(ResidentProject rp) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getProject(rp);
	}

	@Override
	public int delProject(ResidentProject rp) {
		// TODO Auto-generated method stub
		return residentVisitorDao.delProject(rp);
	}

	@Override
	public int addResidentVisitor(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.addResidentVisitor(rv);
	}

	@Override
	public int updateResidentVisitor(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.updateResidentVisitor(rv);
	}

	@Override
	public List<ResidentVisitor> getResidentVisitor(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitor(rv);
	}

	@Override
	public int delResidentVisitor(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.delResidentVisitor(rv);
	}

	@Override
	public int delRVisitorByPid(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.delRVisitorByPid(rv);
	}

	@Override
	public List<ResidentVisitor> getResidentVisitorByName(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitorByName(rv);
	}

	@Override
	public List<ResidentVisitor> getAllResidentCompany(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getAllResidentCompany(rv);
	}

	@Override
	public List<ResidentVisitor> getResidentVisitorByCompany(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitorByCompany(rv);
	}

	@Override
	public ResidentVisitor getResidentVisitorByRid(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitorByRid(rv);
	}

	@Override
	public int updateResidentFace(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.updateResidentFace(rv);
	}

	@Override
	public List<ResidentVisitor> getResidentVisitorByUserid(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitorByUserid(rv);
	}

	@Override
	public ResidentVisitor getResidentVisitorByCardId(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitorByCardId(rv);
	}

	@Override
	public ResidentVisitor getResidentVisitorByPhone(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getResidentVisitorByPhone(rv);
	}

	@Override
	public List<ResidentProject> getProjectByName(ResidentProject rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getProjectByName(rv);
	}

	@Override
	public int updateRvPermission(ResidentVisitor rv) {
		// TODO Auto-generated method stub
		return residentVisitorDao.updateRvPermission(rv);
	}

	@Override
	public int batchAddResidentVisitor(List<ResidentVisitor> rvList) {
		// TODO Auto-generated method stub
		return residentVisitorDao.batchAddResidentVisitor(rvList);
	}

	@Override
	public int batchUpdateRvAvatar(List<ResidentVisitor> rvList) {
		// TODO Auto-generated method stub
		return residentVisitorDao.batchUpdateRvAvatar(rvList);
	}

	@Override
	public List<RvReport> getRvReport(RvReport rvr) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getRvReport(rvr);
	}

	@Override
	public List<RvReport> getRvOpenInfo(RvReport rvr) {
		// TODO Auto-generated method stub
		return residentVisitorDao.getRvOpenInfo(rvr);
	}
}
