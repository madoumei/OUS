package com.config.intercept;

import com.alibaba.fastjson.JSON;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.utils.SpringContextHolder;
import com.config.qicool.common.utils.StringUtils;
import com.utils.RedisUtil;
import com.utils.SysLog;
import com.web.bean.RespInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @package: com.technicalinterest.group.interceptor
 * @className: IpUrlLimitInterceptor
 * @description: ip+url重复请求现在拦截器
 * @since: 0.1
 * 同一个ip地址1s内访问5次就会被锁1小时
 **/
@Slf4j
@Service
public class IpUrlLimitInterceptor implements HandlerInterceptor {


    private RedisUtil getRedisUtil() {
        return  SpringContextHolder.getBean(RedisUtil.class);
    }

    public static final String LOCK_IP_URL_KEY="lock_ip_";

    private static final String IP_URL_REQ_TIME="ip_url_times_";

    private static final long LIMIT_TIMES=300;//1s内最大访问次数

    private static final int IP_LOCK_TIME=3600;//锁定时间
    private static final int IP_EXPIRATION_TIME=60;//周期时间

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
       if(httpServletRequest.getRequestURI().contains("releaseLock")){
            return true;
        }
        if (ipIsLock(StringUtils.getRemoteAddr(httpServletRequest))){
            SysLog.error("ip访问被禁止={}",StringUtils.getRemoteAddr(httpServletRequest));
            RespInfo respInfo = new RespInfo();
            respInfo.setStatus(ErrorEnum.E_LOCK_IP.getCode());
            respInfo.setReason(ErrorEnum.E_LOCK_IP.getMsg()+":"+StringUtils.getRemoteAddr(httpServletRequest));
            returnJson(httpServletResponse, JSON.toJSONString(respInfo));
            return false;
        }
        if(!addRequestTime(StringUtils.getRemoteAddr(httpServletRequest),httpServletRequest.getRequestURI())){
            RespInfo respInfo = new RespInfo();
            respInfo.setStatus(ErrorEnum.E_LOCK_IP.getCode());
            respInfo.setReason(ErrorEnum.E_LOCK_IP.getMsg()+":"+StringUtils.getRemoteAddr(httpServletRequest));
            returnJson(httpServletResponse, JSON.toJSONString(respInfo));
            return false;
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * @Description: 判断ip是否被禁用
     * @author: shuyu.wang
     * @date: 2019-10-12 13:08
     * @param ip
     * @return java.lang.Boolean
     */
    private Boolean ipIsLock(String ip){
        RedisUtil redisUtil=getRedisUtil();
        if(redisUtil.hasKey(LOCK_IP_URL_KEY+ip)){
            return true;
        }
        return false;
    }
    /**
     * @Description: 记录请求次数
     * @author: shuyu.wang
     * @date: 2019-10-12 17:18
     * @param ip
     * @param uri
     * @return java.lang.Boolean
     */
    private Boolean addRequestTime(String ip,String uri){
        String key=IP_URL_REQ_TIME+ip+uri;
        RedisUtil redisUtil=getRedisUtil();
        if (redisUtil.hasKey(key)){
            long time=redisUtil.incr(key,(long)IP_EXPIRATION_TIME);
            if (time>=LIMIT_TIMES){
                redisUtil.getLock(LOCK_IP_URL_KEY+ip,ip,IP_LOCK_TIME);
                return false;
            }
        }else {
            redisUtil.getLock(key,(long)1,IP_EXPIRATION_TIME);
        }
        return true;
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
            log.error("LoginInterceptor response error ---> {}", e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


}
