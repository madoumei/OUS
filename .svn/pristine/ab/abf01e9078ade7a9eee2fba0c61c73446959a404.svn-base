package com.utils.msgUtils;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取模板消息
 */
public class MsgTemplateUtils {

    /**
     * 通过模板名字获取模板消息
     * @param templateStr
     * @param data
     * @param defaultNullReplaceVals
     * @return
     */
    public static String getTemplateMsg(String templateStr, Map<String, ?> data, String... defaultNullReplaceVals) {
        if(templateStr == null) return null;

        if(data == null) data = Collections.EMPTY_MAP;

        String nullReplaceVal = defaultNullReplaceVals.length > 0 ? defaultNullReplaceVals[0] : "";
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");

        StringBuffer newValue = new StringBuffer(templateStr.length());

        Matcher matcher = pattern.matcher(templateStr);

        while (matcher.find()) {
            String key = matcher.group(1);
            String r = data.get(key) != null ? data.get(key).toString() : nullReplaceVal;
            matcher.appendReplacement(newValue, r.replaceAll("\\\\", "\\\\\\\\")); //这个是为了替换windows下的文件目录在java里用\\表示
        }

        matcher.appendTail(newValue);

        return newValue.toString();
    }
}
