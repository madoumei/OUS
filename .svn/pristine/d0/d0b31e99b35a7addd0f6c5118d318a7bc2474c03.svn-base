package com.config.intercept;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.AuthToken;
import com.web.bean.UserInfo;
import com.web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Service
public class TokenVerifyInterceptor implements HandlerInterceptor {

    public static final String INVALID_TOKEN_JSON = "{\"status\":27,\"reason\":\"invalid token\"}";
    public static final String NO_TOKEN_JSON = "{\"status\":28,\"reason\":\"no token\"}";
    public static final String EXPIRE_TOKEN_JSON = "{\"status\":29,\"reason\":\"token expired\"}";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        //20210806fm新加，尝试解决返回错误码不能为中文的问题
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        //查看是否包含放行接口
        for (int i = 0; i < Constant.passUrl.length; i++) {
            if (url.equals(Constant.passUrl[i])) {
                return true;
            }
        }


        boolean isValid = true;
        String ctoken = request.getHeader("X-COOLVISIT-TOKEN");
        if (StringUtils.isBlank(ctoken)) {
            //token为空
            isValid = false;
            response.getWriter().write(NO_TOKEN_JSON);
        } else {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            if (hashOperations.hasKey(ctoken, "id")) {
                Long expireDate = (long) hashOperations.get(ctoken, "id");
                Date currentDate = new Date();
                if (expireDate < currentDate.getTime()) {
                    isValid = false;
                    response.getWriter().write(EXPIRE_TOKEN_JSON);
                }
            } else {
                isValid = false;
                response.getWriter().write(INVALID_TOKEN_JSON);
            }

        }

        return isValid;

    }
}
