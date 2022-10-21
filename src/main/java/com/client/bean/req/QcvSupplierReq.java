package com.client.bean.req;

import lombok.Data;


@Data
public class QcvSupplierReq {
    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private String name;

    private Integer pageNum;

    private Integer pageSize;
}