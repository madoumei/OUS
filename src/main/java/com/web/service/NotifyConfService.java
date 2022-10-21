package com.web.service;

import com.web.bean.NotifyConf;


public interface NotifyConfService {
		public NotifyConf getNotifyConf(String openid);
		
		public int  addNotifyConf(NotifyConf nc);
		
		public int NotifyConfigure(NotifyConf nc);
		
		public int delNotifyConf(String openid);
}
