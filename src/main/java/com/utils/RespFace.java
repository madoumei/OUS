package com.utils;

import java.io.Serializable;

public class RespFace implements Serializable {
	private String result;
	private String result_details;
	
	public RespFace(String result, String result_details)
	{
		this.result = result;
		this.result_details = result_details;
	}
	
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResult_details() {
		return result_details;
	}
	public void setResult_details(String result_details) {
		this.result_details = result_details;
	}

}
