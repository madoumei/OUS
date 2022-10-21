package com.web.bean;

import com.config.exception.ErrorEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.service.ManagerService;
import com.web.service.OperateLogService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@ApiModel("操作日志")
@AllArgsConstructor
@NoArgsConstructor
public class OperateLog {
	public static final String ROLE_ADMIN = "0";//管理员
	public static final String ROLE_HSE = "1";//HSE
	public static final String ROLE_EMPLOYEE = "2";//员工
	public static final String ROLE_GATE = "3";//前台
	public static final String ROLE_MACHINE = "4";//访客机
	public static final String ROLE_SUBADMIN = "5";//子管理员
	public static final String ROLE_SUB_COMPANY = "6";//入驻企业
	public static final String ROLE_SUPPLIER_COMPANY = "7";//劳务公司
	public static final String ROLE_LOGISTICS_MANAGER = "8";//物管
	public static final String ROLE_AUTO = "AUTO";//自动运行

	public static final String MODULE_LOGIN = "0";//登录
	public static final String MODULE_EMPLOYEE = "1";//员工
	public static final String MODULE_ACCOUNT = "2";//账号
	public static final String MODULE_RESIDENT = "3";//供应商
	public static final String MODULE_SUB_COMPANY = "4";//入驻企业
	public static final String MODULE_ACCESS = "5";//门禁
	public static final String MODULE_BLACKLIST = "6";//黑名单
	public static final String MODULE_VISITOR = "7";//访客
	public static final String MODULE_MSG = "8";//消息
	public static final String MODULE_OTHER = "20";//其他

	public static final String CLIENT_PC = "0";//PC
	public static final String CLIENT_MOBILE = "1";//移动

	@ApiModelProperty("用户id")
	private int userid;
	@ApiModelProperty("操作账号")
	private String optId;
	@ApiModelProperty("操作姓名")
	private String optName;
	@ApiModelProperty("操作人角色，操作角色 ；0-管理员 1-hse ；2-员工；3-前台；4-访客机；5-子管理员账号；6-入驻企业\n")
	private String optRole;
	@ApiModelProperty("ip地址")
	private String ipAddr;
	@ApiModelProperty("操作对象id")
	private String objId;
	@ApiModelProperty("操作对象项目")
	private String objName;
	@ApiModelProperty("操作事件")
	private String optEvent;
	@ApiModelProperty("操作客户端，0-PC ；1-移动")
	private String optClient;
	@ApiModelProperty("操作模块，0-登录 1-员工 2-账号 3-供应商 4-入驻企业 5-门禁 6-黑名单")
	private String optModule;
	@ApiModelProperty("操作时间")
	private Date oTime = new Date();
	@ApiModelProperty("操作结果")
	private String optDesc;

	public int getUserid() {
		return userid;
	}

	public OperateLog setUserid(int userid) {
		this.userid = userid;
		return this;
	}

	public String getOptId() {
		return optId;
	}

	public OperateLog setOptId(String optId) {
		this.optId = optId;
		return this;
	}

	public String getOptName() {
		return optName;
	}

	public OperateLog setOptName(String optName) {
		this.optName = optName;
		return this;
	}

	public String getOptRole() {
		return optRole;
	}

	public OperateLog setOptRole(String optRole) {
		this.optRole = optRole;
		return this;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public OperateLog setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
		return this;
	}

	public String getObjId() {
		return objId;
	}

	public OperateLog setObjId(String objId) {
		this.objId = objId;
		return this;
	}

	public String getObjName() {
		return objName;
	}

	public OperateLog setObjName(String objName) {
		this.objName = objName;
		return this;
	}

	public String getOptEvent() {
		return optEvent;
	}

	public OperateLog setOptEvent(String optEvent) {
		this.optEvent = optEvent;
		return this;
	}

	public String getOptClient() {
		return optClient;
	}

	public OperateLog setOptClient(String optClient) {
		this.optClient = optClient;
		return this;
	}

	public String getOptModule() {
		return optModule;
	}

	public OperateLog setOptModule(String optModule) {
		this.optModule = optModule;
		return this;
	}

	public Date getoTime() {
		return oTime;
	}

	public OperateLog setoTime(Date oTime) {
		this.oTime = oTime;
		return this;
	}

	public String getOptDesc() {
		return optDesc;
	}

	public OperateLog setOptDesc(String optDesc) {
		this.optDesc = optDesc;
		return this;
	}

	/**
	 * 添加导出操作日志
	 * @param operateLogService
	 * @param mgrService
	 * @param req
	 * @param ctoken
	 * @param userInfo
	 * @param module
	 * @param size
	 * @param optDesc 导出数据描述，包括参数
	 */
	public static void addExLog(OperateLogService operateLogService, ManagerService mgrService,
								HttpServletRequest req, String ctoken, UserInfo userInfo, String module, int size, String optDesc) {
		try {
			String optId = "";
			String optName = "";
			String optRole = "";
			String decode = AESUtil.decode(ctoken, Constant.AES_KEY);
			ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);

			//检查权限
			AuthToken authToken = null;
			try {
				authToken = mapperInstance.readValue(decode, AuthToken.class);
				if (org.apache.commons.lang.StringUtils.isNotEmpty(authToken.getLoginAccountId())) {
					Manager manager = mgrService.getManagerByAccount(authToken.getLoginAccountId());
					if (null != manager) {
						optId = manager.getAccount();
						optName = manager.getSname();
						optRole = String.valueOf(manager.getsType());
					}else {
						//主管理员
						optId = userInfo.getEmail();
						optName = userInfo.getUsername();
						optRole = OperateLog.ROLE_ADMIN;
					}
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			String ipAddr = req.getHeader("X-Forwarded-For");
			OperateLog log = new OperateLog();
			log.setUserid(userInfo.getUserid())
					.setOptRole(optRole)
					.setOptId(optId)
					.setOptName(optName)
					.setIpAddr(ipAddr)
					.setOptEvent("导出数据")
					.setOptModule(module)
					.setOptDesc(optDesc);
			operateLogService.addLog(log);
		}catch (Exception e){
			SysLog.error(e);
		}
	}


	/**
	 * 短信发送日志
	 * @param userId
	 * @param response
	 * @param a 短信条数
	 * @param phone 接收人手机号
	 * @param name 接受人姓名
	 * @param content 消息内容
	 */
	public static void addSMSLog(OperateLogService operateLogService,int userId, String response, int a,String phone,String name,String content) {
		try {
			String des = "";
			if ("0".equals(response)) {
				des = "成功,计数:" + a+" 内容："+content;
			}else if(response.equals(ErrorEnum.E_043.getCode())){
				des = "失败 error:短信已用完";
			} else {
				des = "失败 error:" + response;
			}

			//日志
			OperateLog log = new OperateLog();
			log.setUserid(userId)
					.setObjId(phone)
					.setObjName(name)
					.setOptEvent("短信")
					.setOptModule(OperateLog.MODULE_MSG)
					.setOptDesc(des);
			operateLogService.addLog(log);
		}catch (Exception e){

		}
	}
}
