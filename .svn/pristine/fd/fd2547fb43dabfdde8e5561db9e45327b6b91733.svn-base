package com.web.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.annotation.ProcessLogger;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@TableName("qcv_appointment")
@ApiModel("Appointment 访客邀请Bean")
public class Appointment {
	@TableId(value = "id", type = IdType.AUTO)
	private int id;
	private String secid;
	@ApiModelProperty(value = "企业ID")
	private int userid;
	@ApiModelProperty(value = "企业名称")
	private String company;
	@ApiModelProperty(value = "员工ID")
	private int empid;
	@ApiModelProperty(value = "部门ID")
	private int empdeptid;
	@ApiModelProperty(value = "部门名称")
	private String deptName;
	@ApiModelProperty(value = "员工openid")
	private String openid;

	@TableField("empName")
	@ApiModelProperty(value = "员工名")
	private String empName;

	@TableField("empEmail")
	@ApiModelProperty(value = "员工邮箱")
	private String empEmail;

	@TableField("empPhone")
	@ApiModelProperty(value = "员工电话")
	private String empPhone;
	@ApiModelProperty(value = "预约人姓名")
	private String name;
	@ApiModelProperty(value = "预约人电话")
	private String phone;
	@ApiModelProperty(value = "预约人邮箱")
	private String vemail;

	@TableField("appointmentDate")
	@ApiModelProperty(value = "预约日期")
	private Date appointmentDate;
	@TableField("visitDate")
	@ApiModelProperty(value = "来访日期")
	private Date visitDate;
	@TableField("visitType")
	@ApiModelProperty(value = "预约类型")
	private String visitType;
	@TableField("inviteContent")
	@ApiModelProperty(value = "预约正文")
	private String inviteContent;
	@ApiModelProperty(value = "地址")
	private String address;
	@ApiModelProperty(value = "经度")
	private String longitude;
	@ApiModelProperty(value = "纬度")
	private String latitude;
	@TableField("companyProfile")
	@ApiModelProperty(value = "公司简介")
	private String companyProfile;
	@ApiModelProperty(value = "交通信息")
	private String traffic;
	@ApiModelProperty(value = "邀请函状态 0 未签到  1已签到 2 已打开 3 接受 4 拒绝 6 待补填")
	private int status;
	@TableField("photoUrl")
	@ApiModelProperty(value = "照片")
	private String photoUrl;
	@TableField("wxDate")
	private String wxDate;
	@TableField("sendType")
	private String sendType;
	@TableField("smsStatus")
	@ApiModelProperty(value = "快捷回复标志")
	private int smsStatus;
	@TableField("subaccountId")
	private int subaccountId;
	@TableField("qrContent")
	private String qrContent;
	@TableField("sendStatus")
	private int sendStatus;
	@TableField("cardId")
	private String cardId;
	private String vcompany;
	private String remark;
	private int mid;
	private String ddid;
	@TableField(exist = false)
	private List<String> extendCol;
	@TableField("signInGate")
	private String signInGate;
	@TableField("signInOpName")
	private String signInOpName;
	@TableField("qrcodeConf")
	private int qrcodeConf;
	@TableField("qrcodeType")
	private int qrcodeType;
	@TableField("openCount")
	private int openCount;
	@TableField("leaveTime")
	private Date leaveTime;
	private int tid;
	private int peopleCount;

	@ApiModelProperty(value = "访客类型",example ="普通访客#Normal Visitor")
	@TableField("vType")
	private String vType;

	@ApiModelProperty(value = "批量邀请组id，用于标识同一批邀请的人员")
	private String agroup;
	private int areaid;
	private String area;
	@TableField("visitReason")
	private String visitReason;
	@TableField("plateNum")
	private String plateNum;
	@TableField("appExtendCol")
	private String appExtendCol;
	private int gid;
	@TableField("cardNo")
	private String cardNo;
	private String sex;
	@ApiModelProperty("访客预约途径，常驻供应商签到不填，0-前台 1-小程序/微信 2-PC端 3-前台 4-访客机 5-pad")
	@TableField("clientNo")
	private Integer clientNo;
	@TableField("bCompany")
	private String bCompany;
	@TableField("visitorCount")
	private int visitorCount;
	private String floors;
	@TableField("isWeekendVisitor")
	private String isWeekendVisitor;
	@TableField("isHolidayVisitor")
	private String isHolidayVisitor;
	@TableField("isSCTimeVisitor")
	private String isSCTimeVisitor;
	@TableField("isDaysOffVisitor")
	private String isDaysOffVisitor;
	private String acode;
	@TableField("meetAddress")
	private String meetAddress;
	@TableField("meetingId")
	private String meetingId;
	@TableField("uniqueChar")
	private String uniqueChar;
	private int permission;
	@TableField(exist = false)
	private String token;
	@TableField("signDate")
	@ApiModelProperty(value = "参会人员签到时间")
	private String signDate;
	@ApiModelProperty(value = "参会人员人脸识别状态")
	private Integer face;
	@TableField("meetingStatus")
	@ApiModelProperty(value = "预约参会状态:0未授权默认状态1已授权2拒绝")
	private Integer meetingStatus;

	@TableField("healthDeclaration")
	@ApiModelProperty("健康申报信息")
	private String healthDeclaration;

	@TableField(exist = false)
	@ApiModelProperty("会议名称")
	private String mName;
	@TableField(exist = false)
	@ApiModelProperty("门岗名")
	private String gateName;

	@TableField(exist = false)
	@ApiModelProperty("访客类别 1 物流 2访客")
	private String category;

	@TableField("createTime")
	private Timestamp createTime;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSecid() {
		return secid;
	}

	public void setSecid(String secid) {
		this.secid = secid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getEmpid() {
		return empid;
	}

	public void setEmpid(int empid) {
		this.empid = empid;
	}

	public int getEmpdeptid() {
		return empdeptid;
	}

	public void setEmpdeptid(int empdeptid) {
		this.empdeptid = empdeptid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVemail() {
		return vemail;
	}

	public void setVemail(String vemail) {
		this.vemail = vemail;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public String getInviteContent() {
		return inviteContent;
	}

	public void setInviteContent(String inviteContent) {
		this.inviteContent = inviteContent;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(String companyProfile) {
		this.companyProfile = companyProfile;
	}

	public String getTraffic() {
		return traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getWxDate() {
		return wxDate;
	}

	public void setWxDate(String wxDate) {
		this.wxDate = wxDate;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public int getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(int smsStatus) {
		this.smsStatus = smsStatus;
	}

	public int getSubaccountId() {
		return subaccountId;
	}

	public void setSubaccountId(int subaccountId) {
		this.subaccountId = subaccountId;
	}

	public String getQrContent() {
		return qrContent;
	}

	public void setQrContent(String qrContent) {
		this.qrContent = qrContent;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getVcompany() {
		return vcompany;
	}

	public void setVcompany(String vcompany) {
		this.vcompany = vcompany;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getDdid() {
		return ddid;
	}

	public void setDdid(String ddid) {
		this.ddid = ddid;
	}

	public List<String> getExtendCol() {
		return extendCol;
	}

	public void setExtendCol(List<String> extendCol) {
		this.extendCol = extendCol;
	}

	public String getSignInGate() {
		return signInGate;
	}

	public void setSignInGate(String signInGate) {
		this.signInGate = signInGate;
	}

	public String getSignInOpName() {
		return signInOpName;
	}

	public void setSignInOpName(String signInOpName) {
		this.signInOpName = signInOpName;
	}

	public int getQrcodeConf() {
		return qrcodeConf;
	}

	public void setQrcodeConf(int qrcodeConf) {
		this.qrcodeConf = qrcodeConf;
	}

	@ProcessLogger(value = "二维码类型",isRouter = true)
	public int getQrcodeType() {
		return qrcodeType;
	}

	public void setQrcodeType(int qrcodeType) {
		this.qrcodeType = qrcodeType;
	}

	public int getOpenCount() {
		return openCount;
	}

	public void setOpenCount(int openCount) {
		this.openCount = openCount;
	}

	public Date getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(Date leaveTime) {
		this.leaveTime = leaveTime;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getvType() {
		return vType;
	}

	public void setvType(String vType) {
		this.vType = vType;
	}

	public String getAgroup() {
		return agroup;
	}

	public void setAgroup(String agroup) {
		this.agroup = agroup;
	}

	public int getAreaid() {
		return areaid;
	}

	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getVisitReason() {
		return visitReason;
	}

	public void setVisitReason(String visitReason) {
		this.visitReason = visitReason;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getAppExtendCol() {
		return appExtendCol;
	}

	public void setAppExtendCol(String appExtendCol) {
		this.appExtendCol = appExtendCol;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getClientNo() {
		return clientNo;
	}

	public void setClientNo(Integer clientNo) {
		this.clientNo = clientNo;
	}

	public String getbCompany() {
		return bCompany;
	}

	public void setbCompany(String bCompany) {
		this.bCompany = bCompany;
	}

	public int getVisitorCount() {
		return visitorCount;
	}

	public void setVisitorCount(int visitorCount) {
		this.visitorCount = visitorCount;
	}

	public String getFloors() {
		return floors;
	}

	public void setFloors(String floors) {
		this.floors = floors;
	}

	public String getIsWeekendVisitor() {
		return isWeekendVisitor;
	}

	public void setIsWeekendVisitor(String isWeekendVisitor) {
		this.isWeekendVisitor = isWeekendVisitor;
	}

	public String getIsHolidayVisitor() {
		return isHolidayVisitor;
	}

	public void setIsHolidayVisitor(String isHolidayVisitor) {
		this.isHolidayVisitor = isHolidayVisitor;
	}

	public String getIsSCTimeVisitor() {
		return isSCTimeVisitor;
	}

	public void setIsSCTimeVisitor(String isSCTimeVisitor) {
		this.isSCTimeVisitor = isSCTimeVisitor;
	}

	public String getIsDaysOffVisitor() {
		return isDaysOffVisitor;
	}

	public void setIsDaysOffVisitor(String isDaysOffVisitor) {
		this.isDaysOffVisitor = isDaysOffVisitor;
	}

	public String getAcode() {
		return acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	public String getMeetAddress() {
		return meetAddress;
	}

	public void setMeetAddress(String meetAddress) {
		this.meetAddress = meetAddress;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getUniqueChar() {
		return uniqueChar;
	}

	public void setUniqueChar(String uniqueChar) {
		this.uniqueChar = uniqueChar;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getSignDate() {
		return signDate;
	}

	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}

	public Integer getFace() {
		return face;
	}

	public void setFace(Integer face) {
		this.face = face;
	}

	public Integer getMeetingStatus() {
		return meetingStatus;
	}

	public void setMeetingStatus(Integer meetingStatus) {
		this.meetingStatus = meetingStatus;
	}
	
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getExtendValue(String key) {
		if (appExtendCol == null) {
			return null;
		}
		return JSON.parseObject(appExtendCol).getString(key);
	}

	public boolean addExtendValue(String key,String value) {
		if (StringUtils.isBlank(appExtendCol)) {
			appExtendCol="{}";
		}
		JSONObject jsonObject = JSON.parseObject(appExtendCol);
		jsonObject.put(key, value);
		appExtendCol = jsonObject.toString();
		return true;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}
}
