package com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.AuthToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@SpringBootTest
public class TokenDemo {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void getToken() throws JsonProcessingException {
        /**
         * 生成新token
         */
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        //AuthToken(loginAccountId=ceshi, accountRole=2, userid=2147483647, openid=null, dateTime=1618472785488)
        String account = "1402";
        AuthToken authToken = new AuthToken();
        authToken.setLoginAccountId(account);
        authToken.setUserid(2147483647);
        authToken.setAccountRole("2");
        authToken.setDateTime(new Date().getTime());
        String encoderToken = AESUtil.encode(mapperInstance.writeValueAsString(authToken), Constant.AES_KEY);
        Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put(encoderToken, "id", exprieDate.getTime());
        System.out.println(encoderToken);
    }

    @Test
    public void addToken() throws JsonProcessingException {
        String encoderToken = "A6AD2867EAF04815FE72770B441B78F760CA48F56280D2F5E666137976F006486FCDB00F3ADA89356E2021593957219AE6E8A9CC7A4AE28CDA5813BFC016321331657DFE6EAB78C3F0471836F53D009A330197CFC8986D30A4653D7AE048C5FA280DCF355D4D1AEEEE69463C2B648BB7";
        Date exprieDate = new Date(new Date().getTime() + Constant.EXPIRE_TIME);
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put(encoderToken, "id", exprieDate.getTime());
        System.out.println(encoderToken);
    }

    /**
     * 解析token
     * @throws JsonProcessingException
     */
    @Test
    public void ResolveToken() throws JsonProcessingException {
//        String token = "A6AD2867EAF04815FE72770B441B78F73C892E4764FCCA4F8C8E795DB66793866FCDB00F3ADA89356E2021593957219AE6E8A9CC7A4AE28CDA5813BFC0163213B2B32E6A376C9A77756FFE0A7E07741A964E7D260E20A19A4F3CBCC7CABE4FA90612ACE8010CAF4C65FC35C09168224A8A86BEE2595F02A0F0D83DFCB35F51AF";
        String token = "A6AD2867EAF04815FE72770B441B78F760CA48F56280D2F5E666137976F006486FCDB00F3ADA89356E2021593957219AE6E8A9CC7A4AE28CDA5813BFC016321331657DFE6EAB78C3F0471836F53D009A330197CFC8986D30A4653D7AE048C5FA280DCF355D4D1AEEEE69463C2B648BB7";
        String decode = AESUtil.decode(token, Constant.AES_KEY);
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        AuthToken authToken = null;
        try {
            authToken = mapperInstance.readValue(decode, AuthToken.class);
            System.out.println(authToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
