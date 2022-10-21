package com.client.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class EqptMonitor {
	private String uid;
	private int userid;
	private String deviceName;
	private String devicePlace;
	private int eType;
	private int eStatus;
	private Date lastOnline;
	private String ipAdd;
	private String extendInfo;
	private String sysVer;
	private String appVer;
	private Date logDate;


	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDevicePlace() {
		return devicePlace;
	}

	public void setDevicePlace(String devicePlace) {
		this.devicePlace = devicePlace;
	}

	public int geteType() {
		return eType;
	}

	public void seteType(int eType) {
		this.eType = eType;
	}

	public int geteStatus() {
		return eStatus;
	}

	public void seteStatus(int eStatus) {
		this.eStatus = eStatus;
	}

	public Date getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(Date lastOnline) {
		this.lastOnline = lastOnline;
	}

	public String getIpAdd() {
		return ipAdd;
	}

	public void setIpAdd(String ipAdd) {
		this.ipAdd = ipAdd;
	}

	public String getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(String extendInfo) {
		this.extendInfo = extendInfo;
	}

	public String getSysVer() {
		return sysVer;
	}

	public void setSysVer(String sysVer) {
		this.sysVer = sysVer;
	}

	public String getAppVer() {
		return appVer;
	}

	public void setAppVer(String appVer) {
		this.appVer = appVer;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
}
