package com.client.bean;

import com.config.qicool.common.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TbGoods extends BaseEntity<TbGoods> {
	private static final long serialVersionUID = 7798372360112454481L;
	
	private String gid; 
	private String title;
	private BigDecimal price;
	private BigDecimal origPrice;
	private String tag;
	private int sales;
	private String imageUrl;
	private int cid;
	private int status;
	private int startIndex;
	private int requestedCount;
	private Date onsaleDate;

}
