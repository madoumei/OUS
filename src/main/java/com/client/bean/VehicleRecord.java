package com.client.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class VehicleRecord {
	private int userid;
	private String deviceCode;
	private String deviceName;
	private String plateNum;
	private String name;
	private String moblie;
	private String empNo;
	private int vehType;
	private Date sTime;
	private int sType;
	private String photoUrl;
	private String vsid;
	private String gid;


	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoblie() {
		return moblie;
	}

	public void setMoblie(String moblie) {
		this.moblie = moblie;
	}

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public int getVehType() {
		return vehType;
	}

	public void setVehType(int vehType) {
		this.vehType = vehType;
	}

	public Date getsTime() {
		return sTime;
	}

	public void setsTime(Date sTime) {
		this.sTime = sTime;
	}

	public int getsType() {
		return sType;
	}

	public void setsType(int sType) {
		this.sType = sType;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getVsid() {
		return vsid;
	}

	public void setVsid(String vsid) {
		this.vsid = vsid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}
	
}