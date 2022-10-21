package com.utils.jsonUtils;

import com.web.bean.RespInfo;

/**
 * 响应工具类
 */
public class ResponseUtils {

    public static RespInfo success(Object object) {
        return new RespInfo(0, "success", object);
    }

    public static RespInfo success() {
        return success(null);
    }

    public static RespInfo error(Integer code, String msg) {
        return new RespInfo(0, msg);
    }
}
