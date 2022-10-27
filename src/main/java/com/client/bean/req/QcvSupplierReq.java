package com.client.bean.req;

import lombok.Data;

import java.util.List;


@Data
public class QcvSupplierReq {
    /**
     *
     */
    private Integer id;

    private List<Integer> ids;

    /**
     *
     */
    private String name;

    private Integer pageNum;

    private Integer pageSize;
}