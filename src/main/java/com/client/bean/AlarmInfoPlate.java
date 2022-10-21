package com.client.bean;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AlarmInfoPlate {
	@JsonProperty(value = "resultType")
	private int resultType;
	@JsonProperty(value = "channel")
	private int channel;
	@JsonProperty(value = "deviceName")
	private String deviceName;
	@JsonProperty(value = "ipaddr")
	private String ipaddr;
	@JsonProperty(value = "seriaIno")
	private String seriaIno;
	@JsonProperty(value = "nParkID")
	private String nParkID;
	@JsonProperty(value = "ParkID")
	private String ParkID;
	@JsonProperty(value = "ParkName")
	private String ParkName;
	@JsonProperty(value = "ParkDoor")
	private String ParkDoor;
	@JsonProperty(value = "romid")
	private String romid;
	@JsonProperty(value = "sn")
	private String sn;
	@JsonProperty(value = "result")
	private AlarmResult result;
	
	public int getResultType() {
		return resultType;
	}
	public void setResultType(int resultType) {
		this.resultType = resultType;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getIpaddr() {
		return ipaddr;
	}
	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	public String getSeriaIno() {
		return seriaIno;
	}
	public void setSeriaIno(String seriaIno) {
		this.seriaIno = seriaIno;
	}
	public String getnParkID() {
		return nParkID;
	}
	public void setnParkID(String nParkID) {
		this.nParkID = nParkID;
	}
	public String getParkID() {
		return ParkID;
	}
	public void setParkID(String parkID) {
		ParkID = parkID;
	}
	public String getParkName() {
		return ParkName;
	}
	public void setParkName(String parkName) {
		ParkName = parkName;
	}
	public String getParkDoor() {
		return ParkDoor;
	}
	public void setParkDoor(String parkDoor) {
		ParkDoor = parkDoor;
	}
	public AlarmResult getResult() {
		return result;
	}
	public void setResult(AlarmResult result) {
		this.result = result;
	}
	public String getRomid() {
		return romid;
	}
	public void setRomid(String romid) {
		this.romid = romid;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}

}
