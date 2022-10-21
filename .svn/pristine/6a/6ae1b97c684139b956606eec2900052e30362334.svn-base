package com.utils.setTokenUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class FeishuUtils {
    /**
     * 获取飞书token
     *
     * @param cm
     * @param userInfo
     */
    public static String setFsToken(CacheManager cm, UserInfo userInfo) {
        String result="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String access_token_url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/";

        String corpid = userInfo.getSecurityID();
        String corpsecret = userInfo.getSecurityKey();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("app_id", corpid);
        map.put("app_secret", corpsecret);

        String jsonBody = HttpClientUtil.postJsonBodyOther(access_token_url, 3000, map, "UTF-8");
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
            // 如果请求成功
            JsonNode errcode = jsonNode.get("code");
            if (null != errcode && "0".equals(errcode.asText())) {
                cm.putToken("FsToken_" + userInfo.getUserid(), jsonNode.get("tenant_access_token").asText());
            } else {
                SysLog.error(dateFormat.format(new Date()) + " 获取accessToken失败: " + jsonBody);
                result = jsonBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }
}
