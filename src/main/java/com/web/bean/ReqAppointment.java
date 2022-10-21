package com.web.bean;

public class ReqAppointment {
	private String id;
	private int status;
	private Integer clientNo;//1-小程序 2-PC端 3-前台 4-访客机 5-pad

	public Integer getClientNo() {
		return clientNo;
	}

	public void setClientNo(Integer clientNo) {
		this.clientNo = clientNo;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	

}
