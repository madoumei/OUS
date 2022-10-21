package com.web.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@ApiModel("UserInfo 用户信息Bean")
public class UserInfo {
	@ApiModelProperty("企业userid")
	private	int userid;
	@ApiModelProperty("企业用户名")
	private String username;
	@ApiModelProperty("密码")
	private String password;
	@ApiModelProperty("邮箱")
	private String email;
	@ApiModelProperty("手机号")
	private String phone;
	@ApiModelProperty("公司名")
	private String company;
	@ApiModelProperty("注册日期")
	private Date  regDate;
	@ApiModelProperty("登录日期")
	private Date  loginDate;
	@ApiModelProperty("最近一次同步通讯录时间")
	private Date refreshDate;
	@ApiModelProperty("企业logo")
	private String logo;
	@ApiModelProperty("微信提醒（0:关闭,1:启用）")
	private int msgNotify;
	@ApiModelProperty("企业邮箱类型(0:关闭,1:smtp,2:exchange,3:default)")
	private int emailType;
	@ApiModelProperty("邮箱账号")
	private String emailAccount;
	@ApiModelProperty("邮箱密码")
	private String emailPwd;
	@ApiModelProperty("邮箱服务器ip")
	private String smtp;
	@ApiModelProperty("邮箱服务器端口")
	private int smtpPort;
	@ApiModelProperty("短信通知（0:关闭,1:启用）")
	private int smsNotify;
	@ApiModelProperty("Rtx服务器端webservice地址")
	private String exchange;
	@ApiModelProperty("域名")
	private String domain;
	@ApiModelProperty("Rtx自动刷新（0:关闭,1:启用）")
	private int rtxAuto;
	@ApiModelProperty("Rtx服务器ip")
	private String rtxip;
	@ApiModelProperty("Rtx服务器端口号")
	private int rtxport;
	@ApiModelProperty("账户类型（0-免费版，1-高级版，2-个人定制）")
	private int  userType;
	@ApiModelProperty("背景图")
	private String backgroundPic;
	@ApiModelProperty("找回密码验证码")
	private String digest;
	@ApiModelProperty("客户端主题颜色")
	private String themecolor;
	@ApiModelProperty("默认头像")
	private String defaultPhoto;
	@ApiModelProperty("每天同步rtx次数")
	private int refreshCount;
	@ApiModelProperty("名片展示的文字")
	private String cardText;
	@ApiModelProperty("logo图片链接")
	private String cardPic;

	@ApiModelProperty("丁丁corpid")
	private String ddcorpid;
	@ApiModelProperty("丁丁ddcorpsecret")
	private String ddcorpsecret;
	@ApiModelProperty("丁丁agentid")
	private String ddagentid;

	@ApiModelProperty("丁丁自动同步开关（0:关闭,1:启用）")
	private int ddautosync;

	@ApiModelProperty("微信企业号corpid")
	private String corpid;
	@ApiModelProperty("微信企业号secret")
	private String corpsecret;
	@ApiModelProperty("微信企业号agentid")
	private String agentid;
	@ApiModelProperty("叮叮应用id")
	private String ddAppid;
	@ApiModelProperty("叮叮应用秘钥")
	private String ddAppSccessSecret;

	@ApiModelProperty("二维码连接")
	private String qrcode;
	@ApiModelProperty("二维码开关")
	private int qrcodeSwitch;
	@ApiModelProperty("二维码类型")
	private int qrcodeType;
	@ApiModelProperty("token")
	private String token;
	@ApiModelProperty("token 失效时间")
	private int tokenExpire;
	@ApiModelProperty("token 失效时间")
	private Date expireDate;
	@ApiModelProperty("私有云服务器域名")
	private String custReqUrl;
	@ApiModelProperty("微信绑定方式 1-公司加姓名 2-邮箱  3-手机")
	private int bindingType;
	@ApiModelProperty("曾经来过开关 0：关 1：开")
	private int comeAgain;
	@ApiModelProperty("1 黑白打印  2 彩色代印")
	private int printType;
	@ApiModelProperty("名片样式")
	private int cardType;
	private int cardSize;
	private int cardLogo;
	private int custSource;
	private String custWeb;
	private String custAddress;
	private int serverLocation;
	private String remark;
	private int smsCount;
	private int wxSmsCount;
	private int appSmsCount;
	private int unsubscribe;
	private int smsTotal;
	private int subAccount;
	private int preRegisterSwitch;
	private int scaner;
	private int ivrNotify;
	private int ivrPrint;
	private int permissionSwitch;
	private int idCardSwitch;
	private int signOutSwitch;
	private String permanentCode;
	private String serviceID;
	@ApiModelProperty(value = "飞书AppID")
	private String securityID;
	@ApiModelProperty(value = "飞书AppSecret")
	private String securityKey;
	private int faceScaner;
	private int webwalkins;
	private int preExtendTime;
	private int latExtendTime;
	private int tempEditSwitch;
	private int keyExpireTime;
	private int blackListSwitch;
	@ApiModelProperty(value = "审批开关 0=不需要审批 1=需要审批")
	private int processSwitch;
	private String  secureProtocol;
	private String upDuty;
	private String offDuty;
	private int qrMaxCount;
	private int qrMaxDuration;
	private int badgeMode;
	private String badgeCustom;
	private int brandType;
	private int brandPosition;
	private int showAvatar;
	private int avatarType;
	private String customText;
	private int leaveExpiryTime;
	private int ssl;
	private String version="3.0.0";
	private String passableSTime;
	private String passableETime;
	private int questionnaireSwitch;
	private int gid;
	private String gname;
	private int sType;
	private String sName;
	@ApiModelProperty("免责条款")
	private String escapeClause;
	@ApiModelProperty("疫情开关，0-关闭，1-开启")
	private int epidemic;

	@ApiModelProperty("0-全发 1-只发一个")
	private int notifyType;
	@ApiModelProperty("企业微信通知 0-全发 1-只发一个")
	private int wxBusNotify;
	@ApiModelProperty("飞书通知（0:关闭,1:启用）")
	private int fsNotify;
	@ApiModelProperty("丁丁通知开关（0:关闭,1:启用）")
	private int ddnotify;
	@ApiModelProperty(value = "满意度调查开关，0-关,1-开")
	private String  satisfactionQuestionnaire;

	@ApiModelProperty(value = "访客数据保留时长，0~99，0表示永久保留")
	private int dataKeepTime = 6;
	@ApiModelProperty(value = "小程序背景图")
	private String appletCarouselPic;
	@Deprecated
	@ApiModelProperty(value = "邀请流程访客更新信息后是否要二次确认开关，0=不需要再确认，1=需要二次确认")
	private String appointmenProcessSwitch;
	@ApiModelProperty(value = "访客机轮播图")
	private String carouselPic;
	@ApiModelProperty(value = "风险区域")
	private String riskArea;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Date getRefreshDate() {
		return refreshDate;
	}

	public void setRefreshDate(Date refreshDate) {
		this.refreshDate = refreshDate;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getMsgNotify() {
		return msgNotify;
	}

	public void setMsgNotify(int msgNotify) {
		this.msgNotify = msgNotify;
	}

	public int getEmailType() {
		return emailType;
	}

	public void setEmailType(int emailType) {
		this.emailType = emailType;
	}

	public String getEmailAccount() {
		return emailAccount;
	}

	public void setEmailAccount(String emailAccount) {
		this.emailAccount = emailAccount;
	}

	public String getEmailPwd() {
		return emailPwd;
	}

	public void setEmailPwd(String emailPwd) {
		this.emailPwd = emailPwd;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public int getSmsNotify() {
		return smsNotify;
	}

	public void setSmsNotify(int smsNotify) {
		this.smsNotify = smsNotify;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getRtxAuto() {
		return rtxAuto;
	}

	public void setRtxAuto(int rtxAuto) {
		this.rtxAuto = rtxAuto;
	}

	public String getRtxip() {
		return rtxip;
	}

	public void setRtxip(String rtxip) {
		this.rtxip = rtxip;
	}

	public int getRtxport() {
		return rtxport;
	}

	public void setRtxport(int rtxport) {
		this.rtxport = rtxport;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getBackgroundPic() {
		return backgroundPic;
	}

	public void setBackgroundPic(String backgroundPic) {
		this.backgroundPic = backgroundPic;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getThemecolor() {
		return themecolor;
	}

	public void setThemecolor(String themecolor) {
		this.themecolor = themecolor;
	}

	public String getDefaultPhoto() {
		return defaultPhoto;
	}

	public void setDefaultPhoto(String defaultPhoto) {
		this.defaultPhoto = defaultPhoto;
	}

	public int getRefreshCount() {
		return refreshCount;
	}

	public void setRefreshCount(int refreshCount) {
		this.refreshCount = refreshCount;
	}

	public String getCardText() {
		return cardText;
	}

	public void setCardText(String cardText) {
		this.cardText = cardText;
	}

	public String getCardPic() {
		return cardPic;
	}

	public void setCardPic(String cardPic) {
		this.cardPic = cardPic;
	}

	public int getDdnotify() {
		return ddnotify;
	}

	public void setDdnotify(int ddnotify) {
		this.ddnotify = ddnotify;
	}

	public int getDdautosync() {
		return ddautosync;
	}

	public void setDdautosync(int ddautosync) {
		this.ddautosync = ddautosync;
	}

	public String getDdcorpid() {
		return ddcorpid;
	}

	public void setDdcorpid(String ddcorpid) {
		this.ddcorpid = ddcorpid;
	}

	public String getDdcorpsecret() {
		return ddcorpsecret;
	}

	public void setDdcorpsecret(String ddcorpsecret) {
		this.ddcorpsecret = ddcorpsecret;
	}

	public String getDdagentid() {
		return ddagentid;
	}

	public void setDdagentid(String ddagentid) {
		this.ddagentid = ddagentid;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public int getQrcodeSwitch() {
		return qrcodeSwitch;
	}

	public void setQrcodeSwitch(int qrcodeSwitch) {
		this.qrcodeSwitch = qrcodeSwitch;
	}

	public int getQrcodeType() {
		return qrcodeType;
	}

	public void setQrcodeType(int qrcodeType) {
		this.qrcodeType = qrcodeType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getCustReqUrl() {
		return custReqUrl;
	}

	public void setCustReqUrl(String custReqUrl) {
		this.custReqUrl = custReqUrl;
	}

	public int getBindingType() {
		return bindingType;
	}

	public void setBindingType(int bindingType) {
		this.bindingType = bindingType;
	}

	public int getTokenExpire() {
		return tokenExpire;
	}

	public void setTokenExpire(int tokenExpire) {
		this.tokenExpire = tokenExpire;
	}

	public int getComeAgain() {
		return comeAgain;
	}

	public void setComeAgain(int comeAgain) {
		this.comeAgain = comeAgain;
	}

	public int getPrintType() {
		return printType;
	}

	public void setPrintType(int printType) {
		this.printType = printType;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public int getCardSize() {
		return cardSize;
	}

	public void setCardSize(int cardSize) {
		this.cardSize = cardSize;
	}

	public int getCardLogo() {
		return cardLogo;
	}

	public void setCardLogo(int cardLogo) {
		this.cardLogo = cardLogo;
	}

	public int getCustSource() {
		return custSource;
	}

	public void setCustSource(int custSource) {
		this.custSource = custSource;
	}

	public String getCustWeb() {
		return custWeb;
	}

	public void setCustWeb(String custWeb) {
		this.custWeb = custWeb;
	}

	public String getCustAddress() {
		return custAddress;
	}

	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}

	public int getServerLocation() {
		return serverLocation;
	}

	public void setServerLocation(int serverLocation) {
		this.serverLocation = serverLocation;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(int smsCount) {
		this.smsCount = smsCount;
	}

	public int getWxSmsCount() {
		return wxSmsCount;
	}

	public void setWxSmsCount(int wxSmsCount) {
		this.wxSmsCount = wxSmsCount;
	}

	public int getAppSmsCount() {
		return appSmsCount;
	}

	public void setAppSmsCount(int appSmsCount) {
		this.appSmsCount = appSmsCount;
	}

	public int getUnsubscribe() {
		return unsubscribe;
	}

	public void setUnsubscribe(int unsubscribe) {
		this.unsubscribe = unsubscribe;
	}

	public int getSmsTotal() {
		return smsTotal;
	}

	public void setSmsTotal(int smsTotal) {
		this.smsTotal = smsTotal;
	}

	public int getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(int subAccount) {
		this.subAccount = subAccount;
	}

	public int getPreRegisterSwitch() {
		return preRegisterSwitch;
	}

	public void setPreRegisterSwitch(int preRegisterSwitch) {
		this.preRegisterSwitch = preRegisterSwitch;
	}

	public int getScaner() {
		return scaner;
	}

	public void setScaner(int scaner) {
		this.scaner = scaner;
	}

	public int getIvrNotify() {
		return ivrNotify;
	}

	public void setIvrNotify(int ivrNotify) {
		this.ivrNotify = ivrNotify;
	}

	public int getIvrPrint() {
		return ivrPrint;
	}

	public void setIvrPrint(int ivrPrint) {
		this.ivrPrint = ivrPrint;
	}

	public int getPermissionSwitch() {
		return permissionSwitch;
	}

	public void setPermissionSwitch(int permissionSwitch) {
		this.permissionSwitch = permissionSwitch;
	}

	public int getIdCardSwitch() {
		return idCardSwitch;
	}

	public void setIdCardSwitch(int idCardSwitch) {
		this.idCardSwitch = idCardSwitch;
	}

	public int getSignOutSwitch() {
		return signOutSwitch;
	}

	public void setSignOutSwitch(int signOutSwitch) {
		this.signOutSwitch = signOutSwitch;
	}

	public String getPermanentCode() {
		return permanentCode;
	}

	public void setPermanentCode(String permanentCode) {
		this.permanentCode = permanentCode;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getCorpsecret() {
		return corpsecret;
	}

	public void setCorpsecret(String corpsecret) {
		this.corpsecret = corpsecret;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getSecurityID() {
		return securityID;
	}

	public void setSecurityID(String securityID) {
		this.securityID = securityID;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public int getFaceScaner() {
		return faceScaner;
	}

	public void setFaceScaner(int faceScaner) {
		this.faceScaner = faceScaner;
	}

	public int getWebwalkins() {
		return webwalkins;
	}

	public void setWebwalkins(int webwalkins) {
		this.webwalkins = webwalkins;
	}

	public int getPreExtendTime() {
		return preExtendTime;
	}

	public void setPreExtendTime(int preExtendTime) {
		this.preExtendTime = preExtendTime;
	}

	public int getLatExtendTime() {
		return latExtendTime;
	}

	public void setLatExtendTime(int latExtendTime) {
		this.latExtendTime = latExtendTime;
	}

	public int getTempEditSwitch() {
		return tempEditSwitch;
	}

	public void setTempEditSwitch(int tempEditSwitch) {
		this.tempEditSwitch = tempEditSwitch;
	}

	public int getKeyExpireTime() {
		return keyExpireTime;
	}

	public void setKeyExpireTime(int keyExpireTime) {
		this.keyExpireTime = keyExpireTime;
	}

	public int getBlackListSwitch() {
		return blackListSwitch;
	}

	public void setBlackListSwitch(int blackListSwitch) {
		this.blackListSwitch = blackListSwitch;
	}

	public int getProcessSwitch() {
		return processSwitch;
	}

	public void setProcessSwitch(int processSwitch) {
		this.processSwitch = processSwitch;
	}

	public String getSecureProtocol() {
		return secureProtocol;
	}

	public void setSecureProtocol(String secureProtocol) {
		this.secureProtocol = secureProtocol;
	}

	public String getUpDuty() {
		return upDuty;
	}

	public void setUpDuty(String upDuty) {
		this.upDuty = upDuty;
	}

	public String getOffDuty() {
		return offDuty;
	}

	public void setOffDuty(String offDuty) {
		this.offDuty = offDuty;
	}

	public int getQrMaxCount() {
		return qrMaxCount;
	}

	public void setQrMaxCount(int qrMaxCount) {
		this.qrMaxCount = qrMaxCount;
	}

	public int getQrMaxDuration() {
		return qrMaxDuration;
	}

	public void setQrMaxDuration(int qrMaxDuration) {
		this.qrMaxDuration = qrMaxDuration;
	}

	public int getBadgeMode() {
		return badgeMode;
	}

	public void setBadgeMode(int badgeMode) {
		this.badgeMode = badgeMode;
	}

	public String getBadgeCustom() {
		return badgeCustom;
	}

	public void setBadgeCustom(String badgeCustom) {
		this.badgeCustom = badgeCustom;
	}

	public int getBrandType() {
		return brandType;
	}

	public void setBrandType(int brandType) {
		this.brandType = brandType;
	}

	public int getBrandPosition() {
		return brandPosition;
	}

	public void setBrandPosition(int brandPosition) {
		this.brandPosition = brandPosition;
	}

	public int getShowAvatar() {
		return showAvatar;
	}

	public void setShowAvatar(int showAvatar) {
		this.showAvatar = showAvatar;
	}

	public int getAvatarType() {
		return avatarType;
	}

	public void setAvatarType(int avatarType) {
		this.avatarType = avatarType;
	}

	public String getCustomText() {
		return customText;
	}

	public void setCustomText(String customText) {
		this.customText = customText;
	}

	public int getLeaveExpiryTime() {
		return leaveExpiryTime;
	}

	public void setLeaveExpiryTime(int leaveExpiryTime) {
		this.leaveExpiryTime = leaveExpiryTime;
	}

	public int getSsl() {
		return ssl;
	}

	public void setSsl(int ssl) {
		this.ssl = ssl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPassableSTime() {
		return passableSTime;
	}

	public void setPassableSTime(String passableSTime) {
		this.passableSTime = passableSTime;
	}

	public String getPassableETime() {
		return passableETime;
	}

	public void setPassableETime(String passableETime) {
		this.passableETime = passableETime;
	}

	public int getQuestionnaireSwitch() {
		return questionnaireSwitch;
	}

	public void setQuestionnaireSwitch(int questionnaireSwitch) {
		this.questionnaireSwitch = questionnaireSwitch;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public int getsType() {
		return sType;
	}

	public void setsType(int sType) {
		this.sType = sType;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getEscapeClause() {
		return escapeClause;
	}

	public void setEscapeClause(String escapeClause) {
		this.escapeClause = escapeClause;
	}

	public int getEpidemic() {
		return epidemic;
	}

	public void setEpidemic(int epidemic) {
		this.epidemic = epidemic;
	}

	public String getDdAppid() {
		return ddAppid;
	}

	public void setDdAppid(String ddAppid) {
		this.ddAppid = ddAppid;
	}

	public String getDdAppSccessSecret() {
		return ddAppSccessSecret;
	}

	public void setDdAppSccessSecret(String ddAppSccessSecret) {
		this.ddAppSccessSecret = ddAppSccessSecret;
	}

	public int getWxBusNotify() {
		return wxBusNotify;
	}

	public void setWxBusNotify(int wxBusNotify) {
		this.wxBusNotify = wxBusNotify;
	}

	public int getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(int notifyType) {
		this.notifyType = notifyType;
	}

	public int getFsNotify() {
		return fsNotify;
	}

	public void setFsNotify(int fsNotify) {
		this.fsNotify = fsNotify;
	}

	public String getSatisfactionQuestionnaire() {
		return satisfactionQuestionnaire;
	}

	public void setSatisfactionQuestionnaire(String satisfactionQuestionnaire) {
		this.satisfactionQuestionnaire = satisfactionQuestionnaire;
	}

	public String getAppletCarouselPic() {
		return appletCarouselPic;
	}

	public void setAppletCarouselPic(String appletCarouselPic) {
		this.appletCarouselPic = appletCarouselPic;
	}

	@JsonIgnore
	public String getUserPrintInfo() {
		return " "+username+" "+userid+" ";
	}

	public int getDataKeepTime() {
		return dataKeepTime;
	}

	public void setDataKeepTime(int dataKeepTime) {
		this.dataKeepTime = dataKeepTime;
	}

	public String getAppointmenProcessSwitch() {
		return appointmenProcessSwitch;
	}

	public void setAppointmenProcessSwitch(String appointmenProcessSwitch) {
		this.appointmenProcessSwitch = appointmenProcessSwitch;
	}
	public String getCarouselPic() {
		return carouselPic;
	}

	public void setCarouselPic(String carouselPic) {
		this.carouselPic = carouselPic;
	}

	public String getRiskArea() {
		return riskArea;
	}

	public void setRiskArea(String riskArea) {
		this.riskArea = riskArea;
	}
}
