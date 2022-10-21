package com.web.service.impl;

import com.config.qicool.common.utils.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.Configures;
import com.web.dao.ConfigureDao;
import com.web.service.ConfigureService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service("configureService")
public class ConfigureServiceImpl implements ConfigureService{
	
	@Autowired
	private ConfigureDao configureDao;

	@Override
	public Configures getConfigure(int userid,String name) {
		// TODO Auto-generated method stub
		
		Configures conf=configureDao.getConfigure(userid,name);
		if(null==conf){
			conf=configureDao.getDefaultConfigure(name);
		}
		
		return conf;
	}

	@Override
	public int addConfigure(Configures conf) {
		// TODO Auto-generated method stub
		return configureDao.addConfigure(conf);
	}

	@Override
	public int updateConfigure(Configures conf) {
		// TODO Auto-generated method stub
		return configureDao.updateConfigure(conf);
	}

	@Override
	public int delConfigure(Configures conf) {
		// TODO Auto-generated method stub
		return configureDao.delConfigure(conf);
	}

	@Override
	public Configures getDefaultConfigure(String name) {
		// TODO Auto-generated method stub
		return configureDao.getDefaultConfigure(name);
	}


	@Override
	public Configures checkConfigure(int userid, String name) {
		// TODO Auto-generated method stub
		return configureDao.getConfigure(userid, name);
	}

	/**
	 * 获取小程序二维码
	 * @param scene 小程序访问参数，可以是gid
	 * @return
	 */
	@Override
	public String createwxaqrcode(String scene) {
		CacheManager cm = CacheManager.getInstance();
		if (null == cm.getToken(CacheManager.TOKEN_WXAPP) || "".equals(cm.getToken(CacheManager.TOKEN_WXAPP))) {
			setWXAppToken(cm);
		}

		//1.获取请求的url
		String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";
		try {
			String requrl = url
					.replace("ACCESS_TOKEN", cm.getToken(CacheManager.TOKEN_WXAPP));
			ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
			Map<String, Object> params = new HashMap<>();
			params.put("scene", scene);
			//2.调用接口，发送请求，获取成员
			String response = HttpClientUtil.postTextBody(requrl, 5000, params, "utf-8");;
			try {
				JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
				//可能是token问题
				setWXAppToken(cm);
				SysLog.error("获取小程序二维码：" + response);
				return "";
			}catch (JsonParseException e){
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 获取小程序token
	 * @param cm
	 */
	public static void setWXAppToken(CacheManager cm) {
		if(Constant.WeiXin_Applets_Notify.get("APPLETSID")==null
		||Constant.WeiXin_Applets_Notify.get("APPLETSSECRET")==null){
			throw new NullPointerException("account.properties中APPLETSID/APPLETSSECRET 未配置");
		}
		ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "client_credential");
		params.put("appid", Constant.WeiXin_Applets_Notify.get("APPLETSID"));
		params.put("secret", Constant.WeiXin_Applets_Notify.get("APPLETSSECRET"));
		String response = HttpClientUtil.invokeGet("https://api.weixin.qq.com/cgi-bin/token", params, "utf-8", 5000);
		try {
			JsonNode rootNode = mapper.readValue(response, JsonNode.class);
			if ("".equals(rootNode.path("errcode").asText())) {
				JsonNode token = rootNode.path("access_token");
				cm.putToken(CacheManager.TOKEN_WXAPP, token.asText());
				System.out.println(cm.getToken("WXAppToken"));
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

}
