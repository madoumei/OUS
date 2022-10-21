package com.web.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@TableName("qcv_msg_template")
@ApiModel("消息模板")
public class MsgTemplate {
	@ApiModelProperty("消息id")
	private String id;
	private int userid;
	@ApiModelProperty("消息类型：SMS,WX,CAMPS,EMAIL")
	private String type;
	@ApiModelProperty(value = "消息模板id",required = false)
	private String templateid;
	@ApiModelProperty(value = "消息模板标题",required = false)
	private String title;
	@ApiModelProperty("消息模板内容")
	private String content;
	@ApiModelProperty("0 停用 1 启用")
	private int status = 1;
	@ApiModelProperty("消息用处说明")
	private String remark;
}
