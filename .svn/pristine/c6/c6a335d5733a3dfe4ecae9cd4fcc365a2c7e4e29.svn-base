package com.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.client.service.Impl.VisitorServiceImpl;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.client.bean.Visitor;
import com.client.service.Impl.VisitorServiceImpl;
import com.utils.Constant;
import com.web.bean.Appointment;
import com.web.bean.Employee;
import com.web.bean.ProcessArea;
import com.web.bean.ProcessRecord;
import com.web.bean.ProcessRule;
import com.web.bean.ReqProcess;
import com.web.dao.ProcessDao;
import com.web.service.ProcessService;


@Service("processService")
public class ProcessServiceImpl implements ProcessService{
	@Autowired
	private ProcessDao processDao;

	@Override
	public int addProcessArea(ProcessArea pa) {
		// TODO Auto-generated method stub
		return processDao.addProcessArea(pa);
	}

	@Override
	public int updateProcessArea(ProcessArea pa) {
		// TODO Auto-generated method stub
		return processDao.updateProcessArea(pa);
	}

	@Override
	public int deleteProcessArea(ProcessArea pa) {
		// TODO Auto-generated method stub
		return processDao.deleteProcessArea(pa);
	}

	@Override
	public int addProcessRule(List<ProcessRule> prlist) {
		// TODO Auto-generated method stub
		return processDao.addProcessRule(prlist);
	}

	@Override
	public int deleteProcessRule(ProcessRule pr) {
		// TODO Auto-generated method stub
		return processDao.deleteProcessRule(pr);
	}

	@Override
	public List<ProcessArea> getProcessArea(ProcessArea pa) {
		// TODO Auto-generated method stub
		return processDao.getProcessArea(pa);
	}

	@Override
	public List<ProcessRule> getProcessRule(ProcessRule pr) {
		// TODO Auto-generated method stub
		return processDao.getProcessRule(pr);
	}

	@Override
	public int addProcessRecord(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.addProcessRecord(pr);
	}

	@Override
	public List<ProcessRecord> getProcessRecord(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.getProcessRecord(pr);
	}

	@Override
	public List<ProcessRule> getCurrentProcess(ProcessRule pr) {
		// TODO Auto-generated method stub
		return processDao.getCurrentProcess(pr);
	}

	@Override
	public ProcessArea getProcessAreaByName(ProcessArea pa) {
		// TODO Auto-generated method stub
		return processDao.getProcessAreaByName(pa);
	}

	@Override
	public int updateProcessRecord(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.updateProcessRecord(pr);
	}

	@Override
	public ProcessRecord getProcessRecordByRid(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.getProcessRecordByRid(pr);
	}

	@Override
	public List<ProcessRecord> getNoApproveRecords(ReqProcess rp) {
		// TODO Auto-generated method stub
		return processDao.getNoApproveRecords(rp);
	}

	@Override
	public List<ProcessRecord> getApprovedRecords(ReqProcess rp) {
		// TODO Auto-generated method stub
		return processDao.getApprovedRecords(rp);
	}

	@Override
	public String sendNewRequestWeiXin(ProcessRecord pr,Visitor v,Employee emp) {
		// TODO Auto-generated method stub
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String,Map<String,String>> maps=new HashMap<String,Map<String,String>>();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		String[]  str=new String[]{"first","keyword1","keyword2","keyword3","keyword4","remark"};
		
		for(int i=0;i<str.length;i++){
			if(i==0){
				Map<String,String> map1=new HashMap<String,String>();
				map1.put("value", "您有新的流程审批任务,请及时处理");
				map1.put("color", "#173177");
				maps.put(str[i], map1);
			}else if(i==1){
				Map<String,String> map2=new HashMap<String,String>();
				map2.put("value", "预约审批");
				map2.put("color", "#173177");
				maps.put(str[i], map2);
			}else if(i==2){
				Map<String,String> map3=new HashMap<String,String>();
				map3.put("value", "访客"+v.getVname()+"于"+time.format(v.getAppointmentDate())+"预约拜访"+v.getEmpName());
				map3.put("color", "#173177");
				maps.put(str[i], map3);
			}else if(i==3){
				Map<String,String> map4=new HashMap<String,String>();
				map4.put("value", v.getEmpName());
				map4.put("color", "#173177");
				maps.put(str[i], map4);
			}else if(i==4){
				Map<String,String> map5=new HashMap<String,String>();
				map5.put("value", time.format(new Date()));
				map5.put("color", "#173177");
				maps.put(str[i], map5);
			}else if(i==5){
				Map<String,String> map6=new HashMap<String,String>();
				map6.put("value","点击查看审批详情" );
				map6.put("color", "#173177");
				maps.put(str[i], map6);
			}
			
		}
		
		map.put("touser", emp.getOpenid());
		map.put("url", Constant.FASTDFS_URL+"empClient/?path=approve");
		map.put("template_id", Constant.WeiXin_Notify.get("ApplyProcess"));
		map.put("topcolor", "#FF0000");
		map.put("data", maps);
		
		CacheManager cm=CacheManager.getInstance();
		if(null==cm.getToken("token")||"".equals(cm.getToken("token"))){
			VisitorServiceImpl.settoken(cm);
		}
		
		String result= HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+cm.getToken("token"), 5000, map, "utf-8");
		VisitorServiceImpl.checkresult(cm, result, map);
		
		return result;
	}
	
	@Override
	public String sendNewRequestWeiXinToCC(ProcessRecord pr,Visitor v,Employee emp,Appointment app) {
		// TODO Auto-generated method stub
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String,Map<String,String>> maps=new HashMap<String,Map<String,String>>();
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		String[]  str=new String[]{"first","keyword1","keyword2","keyword3","remark"};
		
		for(int i=0;i<str.length;i++){
			if(i==0){
				Map<String,String> map1=new HashMap<String,String>();
				map1.put("value", "流程抄送提醒");
				map1.put("color", "#173177");
				maps.put(str[i], map1);
			}else if(i==1){
				Map<String,String> map2=new HashMap<String,String>();
				if(null==v){
					map2.put("value", "邀请审批");
				}else{
					map2.put("value", "预约审批");
				}
				map2.put("color", "#173177");
				maps.put(str[i], map2);
			}else if(i==2){
				Map<String,String> map3=new HashMap<String,String>();
				if(v==null){
					map3.put("value", app.getEmpName());
				}else{
					map3.put("value", v.getEmpName());
				}
				
				map3.put("color", "#173177");
				maps.put(str[i], map3);
			}else if(i==3){
				Map<String,String> map4=new HashMap<String,String>();
				map4.put("value", time.format(pr.getSubmitTime()));
				map4.put("color", "#173177");
				maps.put(str[i], map4);
			}else if(i==4){
				Map<String,String> map6=new HashMap<String,String>();
				map6.put("value","请您关注！" );
				map6.put("color", "#173177");
				maps.put(str[i], map6);
			}
			
		}
		
		map.put("touser", emp.getOpenid());
		map.put("template_id", Constant.WeiXin_Notify.get("CcNotify"));
		map.put("topcolor", "#FF0000");
		map.put("data", maps);
		
		CacheManager cm=CacheManager.getInstance();
		if(null==cm.getToken("token")||"".equals(cm.getToken("token"))){
			VisitorServiceImpl.settoken(cm);
		}
		
		String result=HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+cm.getToken("token"), 5000, map, "utf-8");
		VisitorServiceImpl.checkresult(cm, result, map);
		
		return result;
	}

	@Override
	public String sendFinishWeiXin(ProcessRecord pr, Visitor v, Employee emp) {
		// TODO Auto-generated method stub
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String,Map<String,String>> maps=new HashMap<String,Map<String,String>>();
		String[]  str=new String[]{"first","keyword1","keyword2","keyword3","keyword4","remark"};
		String pType="";
		for(int i=0;i<str.length;i++){
			if(i==0){
				Map<String,String> map1=new HashMap<String,String>();
				map1.put("value", "您的审批已经处理完毕！");
				map1.put("color", "#173177");
				maps.put(str[i], map1);
			}else if(i==1){
				Map<String,String> map2=new HashMap<String,String>();
				if(1==v.getSigninType()){
					map2.put("value", "邀请申请");
					pType="1";
				}else{
					map2.put("value", "预约申请");
					pType="2";
				}
				map2.put("color", "#173177");
				maps.put(str[i], map2);
			}else if(i==2){
				Map<String,String> map3=new HashMap<String,String>();
				map3.put("value", pr.getSubEmpName());
				map3.put("color", "#173177");
				maps.put(str[i], map3);
			}else if(i==3){
				Map<String,String> map4=new HashMap<String,String>();
				map4.put("value", pr.getRemark());
				map4.put("color", "#173177");
				maps.put(str[i], map4);
			}else if(i==4){
				Map<String,String> map5=new HashMap<String,String>();
				if(pr.getStatus()==1){
					map5.put("value", "同意");
				}else{
					map5.put("value", "不同意");
				}
				map5.put("color", "#173177");
				maps.put(str[i], map5);
			}else if(i==5){
				Map<String,String> map6=new HashMap<String,String>();
				map6.put("value","点击查看审批详情" );
				map6.put("color", "#173177");
				maps.put(str[i], map6);
			}
			
		}
		
		map.put("touser", emp.getOpenid());
		map.put("url", Constant.FASTDFS_URL+"empClient/?path=approve");
		map.put("template_id", Constant.WeiXin_Notify.get("ProcessFinish"));
		map.put("topcolor", "#FF0000");
		map.put("data", maps);
		
		CacheManager cm=CacheManager.getInstance();
		if(null==cm.getToken("token")||"".equals(cm.getToken("token"))){
			VisitorServiceImpl.settoken(cm);
		}
		
		String result=HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+cm.getToken("token"), 5000, map, "utf-8");
		VisitorServiceImpl.checkresult(cm, result, map);
		
		return result;
	}

	@Override
	public ProcessRecord getFirstSubmitRecords(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.getFirstSubmitRecords(pr);
	}
	@Override
	public List<ProcessRecord> getProcessRecordBySubName(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.getProcessRecordBySubName(pr);
	}

	@Override
	public List<ProcessRule> getProcessRuleByRole(int role) {
		// TODO Auto-generated method stub
		return processDao.getProcessRuleByRole(role);
	}

	@Override
	public int cancelProcess(ProcessRecord pr) {
		// TODO Auto-generated method stub
		return processDao.cancelProcess(pr);
	}

	@Override
	public List<ProcessRecord> getFirstSubRecordsByEmpid(ReqProcess rp) {
		// TODO Auto-generated method stub
		return processDao.getFirstSubRecordsByEmpid(rp);
	}

	@Override
	public String sendAppNewRequestWeiXin(ProcessRecord pr,
			List<Appointment> applist, Employee emp) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				Map<String, Object> map=new HashMap<String, Object>();
				Map<String,Map<String,String>> maps=new HashMap<String,Map<String,String>>();
				SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
				String[]  str=new String[]{"first","keyword1","keyword2","keyword3","keyword4","remark"};
				
				for(int i=0;i<str.length;i++){
					if(i==0){
						Map<String,String> map1=new HashMap<String,String>();
						map1.put("value", "您有新的流程审批任务,请及时处理");
						map1.put("color", "#173177");
						maps.put(str[i], map1);
					}else if(i==1){
						Map<String,String> map2=new HashMap<String,String>();
						map2.put("value", "邀请审批");
						map2.put("color", "#173177");
						maps.put(str[i], map2);
					}else if(i==2){
						Map<String,String> map3=new HashMap<String,String>();
						if(applist.size()==1){
							map3.put("value", applist.get(0).getEmpName()+"邀请访客"+applist.get(0).getName()+"于"+time.format(applist.get(0).getAppointmentDate())+"来访");
						}else if(applist.size()>1){
							map3.put("value", applist.get(0).getEmpName()+"邀请访客"+applist.get(0).getName()+"，"+applist.get(1).getName()+"等，于"+time.format(applist.get(0).getAppointmentDate())+"来访");
						}
						map3.put("color", "#173177");
						maps.put(str[i], map3);
					}else if(i==3){
						Map<String,String> map4=new HashMap<String,String>();
						map4.put("value", pr.getSubEmpName());
						map4.put("color", "#173177");
						maps.put(str[i], map4);
					}else if(i==4){
						Map<String,String> map5=new HashMap<String,String>();
						map5.put("value", time.format(new Date()));
						map5.put("color", "#173177");
						maps.put(str[i], map5);
					}else if(i==5){
						Map<String,String> map6=new HashMap<String,String>();
						map6.put("value","点击查看审批详情" );
						map6.put("color", "#173177");
						maps.put(str[i], map6);
					}
					
				}
				
				map.put("touser", emp.getOpenid());
				map.put("url", Constant.FASTDFS_URL+"empClient/?path=approve");
				map.put("template_id", Constant.WeiXin_Notify.get("ApplyProcess"));
				map.put("topcolor", "#FF0000");
				map.put("data", maps);
				
				CacheManager cm=CacheManager.getInstance();
				if(null==cm.getToken("token")||"".equals(cm.getToken("token"))){
					VisitorServiceImpl.settoken(cm);
				}
				
				String result=HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+cm.getToken("token"), 5000, map, "utf-8");
				VisitorServiceImpl.checkresult(cm, result, map);
				
				return result;
	}
}
