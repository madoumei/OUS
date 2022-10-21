package com.client.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@ApiModel("RespPicUrl")
public class RespPicUrl {
	private String logourl;
	private String bgurl;
	private String themecolor;
	private String defaultphoto;
	private String watermark;
	private int wmstatus;
	private String cardtext;
	private String cardPic;
	private String qrcode;
	private int qrcodeSwitch;
	private int qrcodeType;
	private int teamVisitSwitch;
	private int comeAgain;
	private int cardType;
	private int cardSize;
	private int cardLogo;
	private int printType;
	private int userType;
	private int preRegisterSwitch;
	private String faceScanThreshold;
	private int ivrPrint;
	private Date expireDate;
	private int signOutSwitch;
	private int badgeMode;
	private String badgeCustom;
	private int brandType;
	private int brandPosition;
	private int showAvatar;
	private int avatarType;
	private String customText;
	private int questionnaireSwitch;
	private int permissionSwitch;
	private String secureProtocol;
	@ApiModelProperty("调查问卷开关")
	private int scoreDetail;
	@ApiModelProperty("疫情开关")
	private int epidemic;
	@ApiModelProperty(value = "健康信息采集开关 0:关 1:开")
	private int infoCollectionSwitch;
	private int processSwitch;
}
