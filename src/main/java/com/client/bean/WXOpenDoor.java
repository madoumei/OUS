package com.client.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WXOpenDoor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8033470202582398267L;
	private String ksid;
	private String key_secret;
	private String device_id;
	private String lock_name;
}
