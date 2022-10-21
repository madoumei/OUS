package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "响应信息")
public class RespInfoT<T> implements Serializable {
    //	private static final long serialVersionUID = -1840369347913160900L;
    @ApiModelProperty(value = "响应状态码")
    private int status;//0:成功
    @ApiModelProperty(value = "响应描述")
    private String reason;//success
    @ApiModelProperty(value = "响应参数")
    private T result;

    public RespInfoT(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }
}
