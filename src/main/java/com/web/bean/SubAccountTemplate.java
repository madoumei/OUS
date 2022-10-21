package com.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubAccountTemplate {
	private int subaccountId;
	private int userid;
	private String templateType;
	private String inviteContent;
	private String address;
	private String longitude;
	private String latitude;
	private String companyProfile;
	private String traffic;
	private int gid;
}
