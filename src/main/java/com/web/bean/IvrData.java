package com.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IvrData {
	private String vid;
	private String ivrType;
	private String alias;
	
	public static final String NORMAL_VISIT_TYPE = "v";
	public static final String APPOINTMENT_VISIT_TYPE = "a";
	public static final String IVR_TYPE_NOTIFY = "n";
	public static final String IVR_TYPE_CONFIRM = "c";
	public IvrData() {
		super();
	}
}
