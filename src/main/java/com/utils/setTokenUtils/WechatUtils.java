package com.utils.setTokenUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.UserInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WechatUtils {

    /**
     * 获取微信token
     *
     * @param cm
     */
    public static String settokenNotify(CacheManager cm) {
        String result="";
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credential");
        params.put("appid", Constant.WeiXin_Notify.get("APPID"));
        params.put("secret", Constant.WeiXin_Notify.get("APP_SECRET"));
        String response = HttpClientUtil.invokeGet("https://api.weixin.qq.com/cgi-bin/token", params, "utf-8", 5000);
        try {
            JsonNode rootNode = mapper.readValue(response, JsonNode.class);
            if ("".equals(rootNode.path("errcode").asText())) {
                JsonNode token = rootNode.path("access_token");
                cm.putToken(CacheManager.TOKEN_WEIXIN, token.asText());
                System.out.println(cm.getToken("token"));
                return result;
            }
            SysLog.error("微信 gettoken response="+response);
        } catch (JsonParseException e) {
            e.printStackTrace();
            result = e.getMessage();
        } catch (JsonMappingException e) {
            e.printStackTrace();
            result = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    /**
     * 校验微信推送
     *
     * @param cm
     * @param result
     * @param content
     */
    public static String checkresultWeixin(CacheManager cm, String result, String content) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        try {
            JsonNode rootNode = mapper.readValue(result, JsonNode.class);
            if (!"".equals(rootNode.path("errcode").asText())) {
                String errcode = rootNode.path("errcode").asText();
                if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                    settokenNotify(cm);
                    result = HttpClientUtil.postJsonBodySource("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, content, "utf-8");
                    return "-1";
                } else if ("0".equals(errcode)) {
                    return "0";
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    //企业微信

    /**
     * 获取企业微信token
     *
     * @param cm
     * @param userInfo
     */
    public static String settokenBus(CacheManager cm, UserInfo userInfo) {
        String result="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String corpid = userInfo.getCorpid();
        String corpsecret = userInfo.getCorpsecret();
        String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;

        String jsonBody = HttpClientUtil.postJsonBodyOther(access_token_url, 1000, null, "UTF-8");
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
            // 如果请求成功
            JsonNode errcode = jsonNode.get("errcode");
            if (null != errcode && "0".equals(errcode.asText())) {
                cm.putToken("wechatBusToken_" + userInfo.getUserid(), jsonNode.get("access_token").asText());
            } else {
                SysLog.error(dateFormat.format(new Date()) + " 获取accessToken失败: " + jsonBody);
                result=jsonBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result=e.getMessage();
        }

        return result;
    }

    public static String checkresultWeixinBus(CacheManager cm, UserInfo userInfo, String result, String url, String content) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        try {
            JsonNode rootNode = mapper.readValue(result, JsonNode.class);
            if (!"".equals(rootNode.path("errcode").asText())) {
                String errcode = rootNode.path("errcode").asText();
                if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                    settokenBus(cm, userInfo);
                    url = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
                    result = HttpClientUtil.postJsonBodySourceWechat(url, 5000, content, "utf-8");
                    return "-1";
                } else if ("0".equals(errcode)) {
                    return "0";
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }
}
