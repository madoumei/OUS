package com.web.service;

import com.web.bean.IvrData;
import com.web.bean.RespInfo;

public interface IvrService {
	public RespInfo ivrCall(IvrData ivrData);
	public String ivrStartService(String callid);
	public String ivrGetTimeout(String callid);
	public String ivrDtmfReport(String callid, String digits);
	public String ivrStopservice(String callid);
	public void sendnotify(String alias, String alert);
	public String redistest(String test);
}
