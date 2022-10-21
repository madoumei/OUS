package com.event.listener;

import com.alibaba.fastjson.JSON;

import java.util.Map;

public class JsonParsTest {
    public static void main(String[] args) {
        String content = "{'thing4':'123456','phone_number5':'123123','time2':'123123','thing3':'访客'}";
        Map<String, String> parseContext = (Map<String, String>) JSON.parse(content);
        System.out.println(12);
        System.out.println(12);
        System.out.println(12);
    }
}
