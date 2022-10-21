package com.web.service;

import com.web.bean.AuthToken;
import com.web.bean.RespInfo;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenServer {

    public int getTokenUserId(String tokenJsonStr);

    public String getTokenuserName(String tokenJsonStr);

    public AuthToken getTokenBean(String tokenJsonStr);

    public int getUserIdByRequestToken(HttpServletRequest request);

    public AuthToken getAuthTokenByRequest(HttpServletRequest request);

    /**
     * 从请求头获取签名相关参数，appCode,authid,timestamp,验证签名是否正确
     * @param request
     * @return null 验证通过，非null，错误信息
     */
    RespInfo checkSign(HttpServletRequest request);

    public String getTokenJsonString(AuthToken authToken);

    public String getAESEncoderTokenString(AuthToken authToken);

    public boolean checkUserAuthorityForExport(ManagerService mgrService, RedisTemplate redisTemplate, HttpServletResponse response, String ctoken, int userid, String gid) throws IOException;

    }
