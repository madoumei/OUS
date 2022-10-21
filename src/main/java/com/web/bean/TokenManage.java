package com.web.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TokenManage {
	private int id;
	private int userid;
	private String email;
	private String token;
	private Date expireDate;
	private int clientType;
	private String uuid;
	private String license;
	
}
