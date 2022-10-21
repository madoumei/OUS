package com.client.service.Impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utils.Constant;
import com.web.bean.OperateLog;
import com.web.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.bean.Logistics;
import com.client.bean.ReqLogistics;
import com.client.dao.LogisticsDao;
import com.client.service.LogisticsService;
import com.utils.UtilTools;
import com.web.bean.Manager;
import com.web.bean.UserInfo;
import com.web.dao.ConfigureDao;
import com.web.dao.UserDao;


@Service("logisticsService")
public class LogisticsServiceImpl implements LogisticsService{
	@Autowired
	private LogisticsDao logisticsDao;
	
	@Autowired
	private ConfigureDao configureDao;
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private OperateLogService operateLogService;

	@Override
	public int addLogisticsInfo(Logistics lo) {
		// TODO Auto-generated method stub
		return logisticsDao.addLogisticsInfo(lo);
	}

	@Override
	public List<Logistics> getLogisticsInfo(ReqLogistics rl) {
		// TODO Auto-generated method stub
		return logisticsDao.getLogisticsInfo(rl);
	}

	@Override
	public int updateLogisticsInfo(Logistics lo) {
		// TODO Auto-generated method stub
		return logisticsDao.updateLogisticsInfo(lo);
	}

	@Override
	public Logistics getTodayLogisticsInfo(ReqLogistics rl) {
		// TODO Auto-generated method stub
		return logisticsDao.getTodayLogisticsInfo(rl);
	}

	@Override
	public String sendLogisticsSms(Logistics lo,UserInfo userinfo,Manager mgr) {
		// TODO Auto-generated method stub
		/**Map<String,String> params=new HashMap<String,String>();
		String response="";
		String objId,objName;
		int a=1;
		if(null!=mgr){
			SimpleDateFormat date=new SimpleDateFormat("MM月dd日 HH时mm分");
			params.put("message", lo.getSname()+"预约在"+date.format(lo.getAppointmentDate())+"前往提货，请在微信服务号中授权。 ");
			params.put("phone", mgr.getMoblie());
			OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,mgr.getMoblie(),mgr.getSname(),params.get("message"));
			 response=UtilTools.sendSmsByYiMei(params,configureDao,userinfo);
		}else{
			if(lo.getPstatus()==1){
				params.put("message", "您好，欢迎光临"+userinfo.getCardText()+"！请点击链接完成访客培训："+ Constant.FASTDFS_URL+"/Answer.html?id="+userinfo.getUserid());
				params.put("phone", lo.getSmobile());
				response=UtilTools.sendSmsByYiMei(params,configureDao,userinfo);
				OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,lo.getSmobile(),lo.getSname(),params.get("message"));

				params.put("phone", lo.getDmobile());
			    response=UtilTools.sendSmsByYiMei(params,configureDao,userinfo);
				OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,lo.getDmobile(),lo.getDname(),params.get("message"));
			    a=2;
			}else if(lo.getPstatus()==2){
				SimpleDateFormat date=new SimpleDateFormat("MM月dd日 HH时mm分");
				params.put("message","您发起的"+date.format(lo.getAppointmentDate())+"的提货申请未通过负责人审批，请联系负责人。 ");
				params.put("phone", lo.getSmobile());
				 response=UtilTools.sendSmsByYiMei(params,configureDao,userinfo);
				OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,lo.getSmobile(),lo.getSname(),params.get("message"));
			}else if(lo.getPstatus()==3){
				params.put("message", "您好，您的车辆已经装载完毕，可以离开了 ");
				params.put("phone", lo.getDmobile());
				response=UtilTools.sendSmsByYiMei(params,configureDao,userinfo);
				OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,lo.getDmobile(),lo.getDname(),params.get("message"));
			}
		}


		if("0".equals(response)){
			userinfo.setAppSmsCount(userinfo.getAppSmsCount()+a);
			userDao.updateAppSmsCount(userinfo);
		}*/


		return null;
	}



	@Override
	public Logistics getLogisticsById(Logistics lo) {
		// TODO Auto-generated method stub
		return logisticsDao.getLogisticsById(lo);
	}

}
