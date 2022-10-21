package com.utils.setTokenUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.Constant;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WechatAppletUtils {

    public static void settokenApplet(CacheManager cm) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credential");
        //params.put("appid", "wx00346e6aa62dc909");
        params.put("appid", Constant.WeiXinAppletsEnum().get("APPLETSID"));
        //params.put("secret", "feca1ef36e377caf64e6ab6f74e49c97");
        params.put("secret", Constant.WeiXinAppletsEnum().get("APPLETSSECRET"));
        String response = HttpClientUtil.invokeGet("https://api.weixin.qq.com/cgi-bin/token", params, "utf-8", 5000);
        try {
            JsonNode rootNode = mapper.readValue(response, JsonNode.class);
            if ("".equals(rootNode.path("errcode").asText())) {
                JsonNode token = rootNode.path("access_token");
                cm.putToken("token_applet", token.asText());
                System.out.println(cm.getToken("token_applet"));
            }

        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 校验微信小程序推送
     *
     * @param result
     */
    public static String checkresultApplet(String result) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        try {
            JsonNode rootNode = mapper.readValue(result, JsonNode.class);
            if (!"".equals(rootNode.path("errcode").asText())) {
                String errcode = rootNode.path("errcode").asText();
                if ("43101".equalsIgnoreCase(errcode)) {
                    return "-1";
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
