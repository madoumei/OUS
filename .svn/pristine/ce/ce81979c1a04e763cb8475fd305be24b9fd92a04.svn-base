package com.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.NotifyConf;
import com.web.dao.NotifyConfDao;
import com.web.service.NotifyConfService;


@Service("notifyConfService")
public class NotifyConfServiceImpl implements NotifyConfService{
	
		@Autowired
		private NotifyConfDao notifyConfDao;
	
		public NotifyConf getNotifyConf(String openid) {
			return notifyConfDao.getNotifyConf(openid);
		}
		
		public int  addNotifyConf(NotifyConf nc) {
			return notifyConfDao.addNotifyConf(nc);
		}
		
		public int NotifyConfigure(NotifyConf nc) {
			return notifyConfDao.NotifyConfigure(nc);
		}
		
		public int delNotifyConf(String openid) {
			return notifyConfDao.delNotifyConf(openid);
		}
}
