package com.client.bean;

import com.config.qicool.common.persistence.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@ApiModel("ReqLogistics 请求物流Bean")
public class ReqLogistics extends BaseEntity<Logistics> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int userid;
	private String startDate;
	private String endDate;
	private List<Integer> psList;
	private String sname;
	private String logType;
	private String cardid;
	private String mobile;
	private int startIndex;
	private int requestedCount;
	private String plateNum;
	private String openid;
	private String memberInfo;

}
