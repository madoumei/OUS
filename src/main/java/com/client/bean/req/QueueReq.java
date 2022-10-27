package com.client.bean.req;

import lombok.Data;

@Data
public class QueueReq {

    private Integer pageSize;

    private Integer pageNum;

    private Integer userid;

    private String queueName;

}
