package com.client.bean;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.utils.jsonUtils.JacksonJsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@TableName("qcv_visitor")
@ApiModel("Visitor 访客Bean")
public class Visitor {
	@TableId(value = "vid", type = IdType.AUTO)
	private int vid;
	private int empid;
	private int empdeptid;
	@TableField(exist = false)
	private String deptName;
	@TableField("empName")
	private String empName;
	@TableField("empPhone")
	private String empPhone;
	private int userid;
	private String company;
	private String vname;
	private String vemail;
	private String vphoto;
	private String vphone;
	@TableField("visitType")
	private String visitType;
	private Date visitdate;
	@TableField("extendCol")
	private String extendCol;
	@TableField("peopleCount")
	private int peopleCount;
	@TableField("smsStatus")
	private int smsStatus;
	@TableField("subaccountId")
	private int subaccountId;
	@ApiModelProperty(value = "授权状态 0 未授权 1 已授权 2拒绝 3已取消  4==审批中，不允许再修改 5一次授权")
	private int permission;
	@TableField("appointmentDate")
	private Date appointmentDate;
	@TableField("signinType")
	@ApiModelProperty(value = "邀约类型,1=邀请,2=预约",allowableValues = "1=邀请,2=预约",example = "1")
	private int signinType;
	@TableField("cardId")
	private String cardId;
	@TableField("signOutDate")
	private Date signOutDate;
	@TableField(exist = false)
	private int mid;
	private String remark;
	private int vgroup;
	private String vcompany;
	@TableField("visitReason")
	private String visitReason;
	private int aid;
	private String area;
	@TableField("memberName")
	private String memberName;
	@TableField("signInOpName")
	private String signInOpName;
	@TableField("signOutOpName")
	private String signOutOpName;
	@TableField("signInGate")
	private String signInGate;
	@TableField("signOutGate")
	private String signOutGate;
	@TableField(exist = false)
	private int qrcodeType;
	@TableField("qrcodeConf")
	private int qrcodeConf;
	@TableField("signPdf")
	@ApiModelProperty(value = "签名文件名")
	private String signPdf;
	@TableField("leaveTime")
	private Date leaveTime;
	private int tid;
	@TableField("vType")
	private String vType;
	private int appid;
	@TableField("plateNum")
	private String plateNum;
	@TableField("meetingPoint")
	private String meetingPoint;
	private String gid;
	private String sex;
	@TableField("cardNo")
	private String cardNo;
	@TableField("clientNo")
	private Integer clientNo;//0-前台 1-小程序 2-邀请函 3-前台 4-访客机
	@TableField("cardOpName")
	private String cardOpName;
	@Deprecated
	@TableField(exist = false)
	private String access;//没用到
	@TableField("bCompany")
	private String bCompany;
	@TableField("visitorCount")
	private int visitorCount;
	@TableField("permissionName")
	private String permissionName;
	private String floors;
	@TableField("isWeekendVisitor")
	private String isWeekendVisitor;
	@TableField("isHolidayVisitor")
	private String isHolidayVisitor;
	@TableField("isSCTimeVisitor")
	private String isSCTimeVisitor;
	@TableField("isDaysOffVisitor")
	private String isDaysOffVisitor;
	@TableField("waitPermissionSeconds")
	private String waitPermissionSeconds;
	@TableField("createTime")
	private Timestamp createTime;
	@ApiModelProperty("邀请函状态 0 未签到  1已签到 2 已打开 3 接受 4 拒绝 6待补填")
	private int status;
	@TableField(exist = false)
	private String acode;
	@TableField(exist = false)
	@ApiModelProperty("会议名称")
	private String mName;
	@TableField(exist = false)
	private String token;
	@TableField("overallScore")
	@ApiModelProperty("调查问卷分数")
	private int overallScore;
	@TableField("scoreDetail")
	@ApiModelProperty("调查问卷详细信息")
	private String scoreDetail;
	@TableField("isBlackList")
	@ApiModelProperty("是否黑名单0=否 1=是")
	private int isBlackList;

	@TableField("healthDeclaration")
	@ApiModelProperty("健康申报信息")
	private String healthDeclaration;
	@TableField(exist = false)
	@ApiModelProperty("邀请函id加密字段")
	private String encryption;
	@TableField(exist = false)
	@ApiModelProperty("访客邀请码")
	private int areaid;

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
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

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
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

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVemail() {
		return vemail;
	}

	public void setVemail(String vemail) {
		this.vemail = vemail;
	}

	public String getVphoto() {
		return vphoto;
	}

	public void setVphoto(String vphoto) {
		this.vphoto = vphoto;
	}

	public String getVphone() {
		return vphone;
	}

	public void setVphone(String vphone) {
		this.vphone = vphone;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public Date getVisitdate() {
		return visitdate;
	}

	public void setVisitdate(Date visitdate) {
		this.visitdate = visitdate;
	}

	public String getExtendCol() {
		return extendCol;
	}

	public void setExtendCol(String extendCol) {
		this.extendCol = extendCol;
	}

	public int getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
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

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public int getSigninType() {
		return signinType;
	}

	public void setSigninType(int signinType) {
		this.signinType = signinType;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public Date getSignOutDate() {
		return signOutDate;
	}

	public void setSignOutDate(Date signOutDate) {
		this.signOutDate = signOutDate;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getVgroup() {
		return vgroup;
	}

	public void setVgroup(int vgroup) {
		this.vgroup = vgroup;
	}

	public String getVcompany() {
		return vcompany;
	}

	public void setVcompany(String vcompany) {
		this.vcompany = vcompany;
	}

	public String getVisitReason() {
		return visitReason;
	}

	public void setVisitReason(String visitReason) {
		this.visitReason = visitReason;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getSignInOpName() {
		return signInOpName;
	}

	public void setSignInOpName(String signInOpName) {
		this.signInOpName = signInOpName;
	}

	public String getSignOutOpName() {
		return signOutOpName;
	}

	public void setSignOutOpName(String signOutOpName) {
		this.signOutOpName = signOutOpName;
	}

	public String getSignInGate() {
		return signInGate;
	}

	public void setSignInGate(String signInGate) {
		this.signInGate = signInGate;
	}

	public String getSignOutGate() {
		return signOutGate;
	}

	public void setSignOutGate(String signOutGate) {
		this.signOutGate = signOutGate;
	}

	public int getQrcodeType() {
		return qrcodeType;
	}

	public void setQrcodeType(int qrcodeType) {
		this.qrcodeType = qrcodeType;
	}

	public int getQrcodeConf() {
		return qrcodeConf;
	}

	public void setQrcodeConf(int qrcodeConf) {
		this.qrcodeConf = qrcodeConf;
	}

	public String getSignPdf() {
		return signPdf;
	}

	public void setSignPdf(String signPdf) {
		this.signPdf = signPdf;
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

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getMeetingPoint() {
		return meetingPoint;
	}

	public void setMeetingPoint(String meetingPoint) {
		this.meetingPoint = meetingPoint;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Integer getClientNo() {
		return clientNo;
	}

	public void setClientNo(Integer clientNo) {
		this.clientNo = clientNo;
	}

	public String getCardOpName() {
		return cardOpName;
	}

	public void setCardOpName(String cardOpName) {
		this.cardOpName = cardOpName;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
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

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
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

	public String getWaitPermissionSeconds() {
		return waitPermissionSeconds;
	}

	public void setWaitPermissionSeconds(String waitPermissionSeconds) {
		this.waitPermissionSeconds = waitPermissionSeconds;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAcode() {
		return acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(int overallScore) {
		this.overallScore = overallScore;
	}

	public String getScoreDetail() {
		return scoreDetail;
	}

	public void setScoreDetail(String scoreDetail) {
		this.scoreDetail = scoreDetail;
	}
	
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getExtendValue(String key) {
		if (extendCol == null) {
			return null;
		}
		ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
		try {
			JsonNode jsonNode = instance.readValue(extendCol, JsonNode.class);
			if (null != jsonNode
					&& (jsonNode.get(key) != null)) {
				return jsonNode.get(key).asText();
			}
		} catch (Exception e) {

		}
		return null;
	}

	public boolean addExtendValue(String key,String value) {
		if (StringUtils.isBlank(extendCol)) {
			extendCol="{}";
		}
		ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
		try {
			ObjectNode jsonNode = instance.readValue(extendCol, ObjectNode.class);
			jsonNode.put(key, value);
			extendCol = jsonNode.toString();
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	public String getEgids() {
		return getExtendValue("access");
	}
}
