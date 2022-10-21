package com.web.service.impl;

import com.config.exception.ErrorEnum;
import com.config.qicool.common.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.MD5;
import com.utils.UtilTools;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.AuthToken;
import com.web.bean.Manager;
import com.web.bean.RespInfo;
import com.web.service.ManagerService;
import com.web.service.TokenServer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
public class TokenServerImpl implements TokenServer {
    @Override
    public int getTokenUserId(String tokenJsonStr) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            AuthToken token = mapperInstance.readValue(tokenJsonStr, AuthToken.class);
            return token.getUserid();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getTokenuserName(String tokenJsonStr) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            AuthToken token = mapperInstance.readValue(tokenJsonStr, AuthToken.class);
            return token.getLoginAccountId();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AuthToken getTokenBean(String tokenJsonStr) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            tokenJsonStr = AESUtil.decode(tokenJsonStr, Constant.AES_KEY);
            AuthToken token = mapperInstance.readValue(tokenJsonStr, AuthToken.class);
            return token;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getUserIdByRequestToken(HttpServletRequest request) {
        AuthToken authToken = null;
        String ctoken = request.getHeader("X-COOLVISIT-TOKEN");
        String decode = AESUtil.decode(ctoken, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        try {
            authToken = mapperInstance.readValue(decode, AuthToken.class);
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
        return authToken.getUserid();
    }


    @Override
    public AuthToken getAuthTokenByRequest(HttpServletRequest request) {
        String token = request.getHeader("X-COOLVISIT-TOKEN");
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException(ErrorEnum.E_027.getMsg());
        }
        String decode = AESUtil.decode(token, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        AuthToken authToken = null;
        try {
            authToken = mapperInstance.readValue(decode, AuthToken.class);
            authToken.setToken(token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return authToken;
    }

    /**
     * 检查签名
     * @param request
     * @return
     */
    @Override
    public RespInfo checkSign(HttpServletRequest request) {
        //验证时间戳的偏差
        Date current = new Date();
        String timestamp = request.getHeader("timestamp");
        if(timestamp == null){
            return new RespInfo(ErrorEnum.E_604.getCode(), ErrorEnum.E_604.getMsg());
        }
        long timestampL = Long.parseLong(timestamp);
        if(Math.abs(current.getTime()-timestampL)>1000*60*1){
            return new RespInfo(ErrorEnum.E_706.getCode(), ErrorEnum.E_706.getMsg());
        }

        //签名有效性
        String authid = request.getHeader("authid");
        String appCode = request.getHeader("appCode");
        String secret = Constant.APP_CODES.get(appCode);
        if(secret==null){
            return new RespInfo(ErrorEnum.E_705.getCode(), ErrorEnum.E_705.getMsg());
        }
        String digest = MD5.crypt(appCode + timestamp + secret);
        if (!digest.equalsIgnoreCase(authid)) {
            return new RespInfo(ErrorEnum.E_704.getCode(), ErrorEnum.E_704.getMsg());
        }

        return null;
    }

    @Override
    public String getTokenJsonString(AuthToken authToken) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        String token = "";
        try {
            token = mapperInstance.writeValueAsString(authToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public String getAESEncoderTokenString(AuthToken authToken) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        String token = "";
        try {
            token = mapperInstance.writeValueAsString(authToken);
            token = AESUtil.encode(token, Constant.AES_KEY);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return token;
    }


    /**
     * 仅用于导出数据检查用户权限
     * 检查用户权限，校验token和userid，管理员可以导出全部数据，子管理员可以导出授权范围gid的数据，其他token禁止访问
     * @param response
     * @param ctoken
     * @param userid
     * @param gid 如果设为-1，子管理员不检查gid
     * @return
     * @throws IOException
     */
    @Override
    public boolean checkUserAuthorityForExport(ManagerService mgrService, RedisTemplate redisTemplate, HttpServletResponse response, String ctoken, int userid, String gid) throws IOException {

        response.setCharacterEncoding("utf-8");   //设置 HttpServletResponse使用utf-8编码
        response.setHeader("Content-Type","text/html;charset=utf-8"); //设置响应头的编码

        //            ctoken = AESUtil.decode(ctoken, Constant.AES_KEY);
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        if (hashOperations.hasKey(ctoken, "id")) {
            Long expireDate = (long) hashOperations.get(ctoken, "id");
            Date currentDate = new Date();
            if (expireDate < currentDate.getTime()) {
                try {
                    response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_029));
                    return true;
                } catch (IOException e) {
                    //throw new ApplicationException("IOException in populateWithJSON", e);
                }
            }
        } else {
            try {
                response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_027));
                return true;
            } catch (IOException e) {
                //throw new ApplicationException("IOException in populateWithJSON", e);
            }
        }

        String decode = AESUtil.decode(ctoken, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);

        //检查权限
        AuthToken authToken = null;
        try {
            authToken = mapperInstance.readValue(decode, AuthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){
            //管理员
            if(authToken.getUserid() != userid) {
                response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_610));
                return true;
            }
        }else if(AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_HSE.equals(authToken.getAccountRole())){
            //子管理员
            if(authToken.getUserid() != userid) {
                response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_610));
                return true;
            }
            if(StringUtils.isBlank(gid)){
                response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_604,"子管理员gid不能为空"));
                return true;

            }
            if(!gid.equals("-1")) {
                Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
                if (mgr == null || org.apache.commons.lang3.StringUtils.isBlank(mgr.getGid())) {
                    response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_604, "未获得授权gid信息"));
                    return true;

                }
                String gids[] = gid.split(",");
                String mgids[] = mgr.getGid().split(",");
                boolean auth = UtilTools.arrayContain(gids, mgids);
                if (auth) {
                    response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_610));
                    return true;
                }
            }
        }
        else{
            //其他类型人员禁止导出
            response.getWriter().write(HttpClientUtil.getHttpErrorStr(ErrorEnum.E_610));
            return true;
        }
        return false;
    }
}
