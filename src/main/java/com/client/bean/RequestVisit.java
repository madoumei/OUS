package com.client.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.config.qicool.common.persistence.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.IDCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@ApiModel("RequestVisit 请求访客Bean")
@Data
public class RequestVisit extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7278411937096286305L;
	@ApiModelProperty(value = "用户id",example ="123456")
	private int userid;
	@ApiModelProperty(value = "员工id",example ="123456")
	private int empid;
	@ApiModelProperty(value = "访客id",example ="123456")
	private int vid;
	private String name;
	private String empName;
	private String empPhone;
	private String empdeptid;
	@ApiModelProperty(value = "员工工号",example ="E2131")
	private String empNo;
	@ApiModelProperty(value = "员工邮箱",example ="E2131")
	private String email;
	@ApiModelProperty(value = "访客头像url",example ="http://xxxx.jpg")
	private String photoUrl;
	@ApiModelProperty(value = "访客手机号",example ="139139xxx")
	private String phone;
	@ApiModelProperty(value = "拜访类型",example = "商务#Business")
	private String visitType;
	private String date;
	private String startDate;
	private String endDate;
	private List<String> extendCol;
	private int peopleCount;
	private String team;
	@ApiModelProperty(value = "微信openid",notes ="oHriHwZNEkGDAov-Rphjv9GyQ1xg")
	private String openId;
	@ApiModelProperty(value = "微信openid",allowableValues = "用于统计：sex=性别,vType=访客类型,visitType=拜访类型,clientNo=预约渠道；用于访客：a=邀请，v=预约",example ="")
	private String type;
	private int subaccountId;
	private int ivrPrint;
	private String appointmentDate;
	private String cardId;
	private IDCard card;
	private String verifyCode;
	private String company;
	private int startIndex;
	private int requestedCount;
	private String remark;
	private int vgroup;
	private String memberName;
	@ApiModelProperty(value = "s=入驻企业")
	private String ctype;
	private String tag;
	private String visitReason;
	private int aid;
	private String area;
	private String vcompany;
	private String signInOpName;
	private String signOutOpName;
	private String signInGate;
	private String signOutGate;
	private String vcode;
	private String digest;
	private String signPic;
	private Date leaveTime;
	@ApiModelProperty(value = "访客类型id")
	private int tid;
	@ApiModelProperty(value = "访客类型",example ="普通访客#Normal Visitor")
	private String vType;
	private String plateNum;
	private String vtExtendCol;
	private String meetingPoint;
	@ApiModelProperty(value = "门岗id")
	private String gid;
	private String sex;
	private String chartStatus;
	@ApiModelProperty(value = "邀约类型,1=邀请,2=预约",allowableValues = "1=邀请,2=预约",example = "1")
	private String signinType;
	private String cardNo;
	private String floors;
	@ApiModelProperty(value = "预约终端,1=小程序,2-PC端,3-前台,4-访客机,5-pad,6-第三方",allowableValues = "1=小程序,2-PC端,3-前台,4-访客机,5-pad,6-第三方",example = "1")
	private Integer clientNo;//1-小程序 2-PC端 3-前台 4-访客机 5-pad
	private String[] gNames;
	private Date reqDate;
	private int qrcodeConf;
	@ApiModelProperty(value="记录范围，0-默认全部访客记录，1-已签到访客记录，2-未签到访客记录",example = "0")
	private int svcType; //0-默认全部访客记录，1-已签到访客记录，2-未签到访客记录
	private String reception;//前台搜索条件
	@ApiModelProperty(value = "邀约类型,0=全部数据,1=正在拜访,2=未签到,3=离开列表,4=已签到",allowableValues = "0=全部数据,1=正在拜访,2=未签到,3=离开列表,4=已签到",example = "0")
	private int searchType; //0 全部数据 1 正在拜访 2 未签到 3 离开列表 4 已签到
	@ApiModelProperty(value = "审批流中的会话id")
	private int taskid;
	@ApiModelProperty("安全id")
	private String secid;
	@ApiModelProperty("调查问卷分数")
	private int overallScore;
	@ApiModelProperty("调查问卷详细信息")
	private String scoreDetail;
	@ApiModelProperty("会议人员签到时间")
	private String signDate;
	@ApiModelProperty("头像同步失败状态")
	private Integer face;
	@ApiModelProperty(value = "预约人会议人员状态 0-未授权默认状态,1-已授权,2-拒绝")
	private Integer meetingStatus;
	@ApiModelProperty(value = "会议id")
	private int mid;//对应qcv_meeting表
	@ApiModelProperty("参会人员ID")
	private int appid;
	@ApiModelProperty("健康申报信息")
	private String healthDeclaration;

	@ApiModelProperty("访客类别 1 物流 2访客")
	private String category;

	@ApiModelProperty("门岗名")
	private String gateName;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getEmpid() {
		return empid;
	}

	public void setEmpid(int empid) {
		this.empid = empid;
	}

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<String> getExtendCol() {
		return extendCol;
	}

	public void setExtendCol(List<String> extendCol) {
		this.extendCol = extendCol;
	}

	public int getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSubaccountId() {
		return subaccountId;
	}

	public void setSubaccountId(int subaccountId) {
		this.subaccountId = subaccountId;
	}

	public int getIvrPrint() {
		return ivrPrint;
	}

	public void setIvrPrint(int ivrPrint) {
		this.ivrPrint = ivrPrint;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public IDCard getCard() {
		return card;
	}

	public void setCard(IDCard card) {
		this.card = card;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getRequestedCount() {
		return requestedCount;
	}

	public void setRequestedCount(int requestedCount) {
		this.requestedCount = requestedCount;
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

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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

	public String getVcompany() {
		return vcompany;
	}

	public void setVcompany(String vcompany) {
		this.vcompany = vcompany;
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

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getSignPic() {
		return signPic;
	}

	public void setSignPic(String signPic) {
		this.signPic = signPic;
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

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getVtExtendCol() {
		return vtExtendCol;
	}

	public void setVtExtendCol(String vtExtendCol) {
		this.vtExtendCol = vtExtendCol;
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

	public String getChartStatus() {
		return chartStatus;
	}

	public void setChartStatus(String chartStatus) {
		this.chartStatus = chartStatus;
	}

	public String getSigninType() {
		return signinType;
	}

	public void setSigninType(String signinType) {
		this.signinType = signinType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getFloors() {
		return floors;
	}

	public void setFloors(String floors) {
		this.floors = floors;
	}

	public Integer getClientNo() {
		return clientNo;
	}

	public void setClientNo(Integer clientNo) {
		this.clientNo = clientNo;
	}

	public String[] getgNames() {
		return gNames;
	}

	public void setgNames(String[] gNames) {
		this.gNames = gNames;
	}

	public Date getReqDate() {
		return reqDate;
	}

	public void setReqDate(Date reqDate) {
		this.reqDate = reqDate;
	}

	public int getQrcodeConf() {
		return qrcodeConf;
	}

	public void setQrcodeConf(int qrcodeConf) {
		this.qrcodeConf = qrcodeConf;
	}

	public int getSvcType() {
		return svcType;
	}

	public void setSvcType(int svcType) {
		this.svcType = svcType;
	}

	public String getReception() {
		return reception;
	}

	public void setReception(String reception) {
		this.reception = reception;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public String getSecid() {
		return secid;
	}

	public void setSecid(String secid) {
		this.secid = secid;
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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
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

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getEmpdeptid() {
		return empdeptid;
	}

	public void setEmpdeptid(String empdeptid) {
		this.empdeptid = empdeptid;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}


	public String getExtendValue(String key) {
		if (vtExtendCol == null) {
			return null;
		}
		ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
		try {
			JsonNode jsonNode = instance.readValue(vtExtendCol, JsonNode.class);
			if (null != jsonNode
					&& (jsonNode.get(key) != null)) {
				return jsonNode.get(key).asText();
			}
		} catch (Exception e) {

		}
		return null;
	}
}
