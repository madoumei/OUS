package com.utils;

import com.config.exception.ErrorEnum;
import com.dbay.apns4j.IApnsService;
import com.dbay.apns4j.impl.ApnsServiceImpl;
import com.dbay.apns4j.model.ApnsConfig;
import com.dbay.apns4j.model.Payload;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.utils.FileUtils.MinioTools;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.yimei.AES;
import com.utils.yimei.GZIPUtils;
import com.utils.yimei.JsonHelper;
import com.utils.yimei.ResultModel;
import com.utils.yimei.http.*;
import com.web.bean.*;
import com.web.dao.ConfigureDao;
import com.web.service.TokenServer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class UtilTools {

    private static IApnsService apnsService;

    // 加密算法
    private final static String algorithm = "AES/ECB/PKCS5Padding";
    // 编码
    private final static String encode = "UTF-8";

    private final static String APPID = "EUCP-EMY-SMS1-4HOT6";

    private final static String SECRETKEY = "508763302D4C9CBB";

    public static JavaMailSenderImpl getEmailConf(String host, int port, String email, String password) {
        JavaMailSenderImpl ms = new JavaMailSenderImpl();

        ms.setHost(host);

        ms.setPort(port);

        ms.setUsername(email);//这里是自己的邮箱用户名，注意不用加@和后面的内容

        ms.setPassword(password);//这里要换成自己的密码呀

        Properties pt = new Properties();

        pt.setProperty("mail.smtp.auth", "true");
        pt.setProperty("mail.smtp.timeout", "25000");

        ms.setJavaMailProperties(pt);

        return ms;
    }

    public static Message getNoAuthEmailConf(String host, int port, String email) {
        // Get a Properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", false);

        Session session = Session.getDefaultInstance(props, null);

        Message message = new MimeMessage(session);

        return message;
    }

    public static Message getEmailConf(String host, String port, String email, String pwd) {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        // Get a Properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.ssl.enable", true);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", true);

        String username = email;
        String password = pwd;
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);

        return message;
    }

    public static boolean getSSLEmailConf(String host, String port, String email, String pwd, String to, String digest) {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        // Get a Properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.ssl.enable", true);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", true);
        try {
            String username = email;
            String password = pwd;
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });


            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress("service@coolvisit.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("找回密码！");//主题
            message.setText("您好!请点击链接进行密码重置：" + Constant.FASTDFS_URL + "reset?email=" + to + "&digest=" + digest);//邮件内容

            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取钉钉token
     * @param userid
     * @param appid
     * @param appSecret
     * @return
     */
    public static String getDDAccToken(int userid, String appid, String appSecret) {
        CacheManager cm = CacheManager.getInstance();
        String acctoken = cm.getToken("token_" + userid);
        try {
            if (null == acctoken || "".equals(acctoken)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appkey", appid);
                params.put("appsecret", appSecret);
                ObjectMapper obj = JacksonJsonUtil.getMapperInstance(false);
                String token1 = obj.writeValueAsString(params);
                SysLog.info("token req=" +token1);
                String result = HttpClientUtil.invokeGet("https://oapi.dingtalk.com/gettoken", params, "UTF-8", 30000);
                SysLog.info("response="+ result);
                JsonNode rootNode = obj.readValue(result, JsonNode.class);

                if (rootNode.path("errcode").asInt() == 0) {
                    JsonNode token = rootNode.path("access_token");
                    acctoken = token.asText();
                    cm.putToken("token_" + userid, acctoken);
                }else{
                    SysLog.error("get token response=" +result);
                }
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

        return acctoken;


    }

    public static String checkDDAccToken(int userid, String corpid, String corpsecret) {
        String error="";
        CacheManager cm = CacheManager.getInstance();
        String acctoken = cm.getToken("token_" + userid);
        try {

            Map<String, String> params = new HashMap<String, String>();
            params.put("appkey", corpid);
            params.put("appsecret", corpsecret);

            String result = HttpClientUtil.invokeGet("https://oapi.dingtalk.com/gettoken", params, "UTF-8", 30000);
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode rootNode = mapper.readValue(result, JsonNode.class);

            if (rootNode.path("errcode").asInt() == 0) {
                JsonNode token = rootNode.path("access_token");
                acctoken = token.asText();
                cm.putToken("token_" + userid, acctoken);
            } else {
                SysLog.error("check token response=" +result);
                error = result;
            }


        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        }

        return error;


    }

    public static String checkWeChartAccess_token(String appid, String appsecret) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpId}&corpsecret={corpsecret}";
        String accessToken = "";

        String requestUrl = access_token_url.replace("{corpId}", appid).replace("{corpsecret}", appsecret);
        String jsonBody = HttpClientUtil.postJsonBody(requestUrl, 1000, null, "UTF-8");
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
            // 如果请求成功
            if (jsonNode.has("errcode") && "0".equals(jsonNode.get("errcode").asText())) {
                accessToken = jsonNode.get("access_token").asText();
            }else {
                SysLog.error(dateFormat.format(new Date())+" 获取accessToken失败: "+jsonBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }
    
    public static String checkFeiShuConfigure(String appid, String appsecret) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String access_token_url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/";
        String accessToken = "";
        
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("app_id", appid);
        map.put("app_secret", appsecret);

        String jsonBody = HttpClientUtil.postJsonBodyOther(access_token_url, 3000, map, "UTF-8");
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
            // 如果请求成功
            if (jsonNode.has("code") && "0".equals(jsonNode.get("code").asText())) {
                accessToken = jsonNode.get("tenant_access_token").asText();
            }else {
                SysLog.error(dateFormat.format(new Date())+" 获取accessToken失败: "+jsonBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public static IApnsService getApnsService(String certificate) {
        if (apnsService == null) {
            ApnsConfig config = new ApnsConfig();
            InputStream is = UtilTools.class.getResourceAsStream(certificate);
            config.setKeyStore(is);
            config.setDevEnv(true);
            config.setPassword("1234");
            config.setPoolSize(3);
            config.setCacheLength(100);
            apnsService = ApnsServiceImpl.createInstance(config);
        }
        return apnsService;
    }

    public static void apns(String deviceToken, String msg, IApnsService service) {

        Payload payload = new Payload();
        payload.setAlert(msg);
        // If this property is absent, the badge is not changed. To remove the badge, set the value of this property to 0
        payload.setBadge(1);
        payload.setSound("default");

        service.sendNotification(deviceToken, payload);

        // get feedback
//  	    List<Feedback> list = service.getFeedbacks();
//  	    if (list != null && list.size() > 0) {
//  	      for (Feedback feedback : list) {
//  	        System.out.println(feedback.getDate() + " " + feedback.getToken());
//  	      }
//  	    }
//
        // It's a good habit to shutdown what you never use


//  			System.exit(0);
    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static boolean validatePhone(String phone) {
        Pattern pattern = Pattern.compile("[1][358]\\d{9}");//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位);

        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }


    public static int getYearMonthDay(Date dt) {
        //传入日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);//设置时间
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日期
        int date = Integer.parseInt(year + "" + month + "" + day);
        return date;
    }


    public static String miaodouService(Map<String, String> params, String method) {
        params.put("app_key", Constant.APP_KEY);
        params.put("agt_num", Constant.AGT_NUM);
        String response = HttpClientUtil.connectPostHttps("http://121.40.204.191:18080/mdserver/service/" + method, params);
        return response;
    }

    public static String miaodouWXService(Map<String, String> params, String method) {
        params.put("app_key", Constant.APP_KEY);
        params.put("agt_num", Constant.AGT_NUM);
        String response = HttpClientUtil.connectPostHttps("http://develop.wx.imiaodou.com/pservice/" + method, params);
        return response;
    }

    public static String listToString(List<?> a) {
        StringBuilder sb = new StringBuilder();
        if (a != null && a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                if (i < a.size() - 1) {
                    sb.append(a.get(i) + ",");
                } else {
                    sb.append(a.get(i));
                }
            }
        }
        return sb.toString();
    }

//	  public static String shortUrl(String url){
//		  Map<String,String> params =new HashMap<String,String>();
//		  params.put("source", "3271760578");
//		  params.put("url_long", url);
//		  String response=HttpClientUtil.invokeGet("http://api.t.sina.com.cn/short_url/shorten.json", params, "utf-8", 5000);
//		  ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
//			JsonNode rootNode= null;
//			List<ShortURL> shorturl =null;
//			try {
//		     shorturl = mapper.readValue(response,new TypeReference<List<ShortURL>>(){});
//			} catch (JsonParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return shorturl.get(0).getUrl_short().substring(7);
//	  }

    public static String sendSmsByYiMei(Map<String, String> params, ConfigureDao configureDao, UserInfo userinfo) {
        if (checkSMSCount(configureDao, userinfo)) return "43";

        SysLog.info("=============begin setSingleSms==================");
        SmsSingleRequest pamars = new SmsSingleRequest();
        pamars.setContent("【来访通】" + params.get("message"));
        pamars.setCustomSmsId(null);
        pamars.setExtendedCode(null);
        pamars.setMobile(params.get("phone"));
        ResultModel result = request(APPID, SECRETKEY, algorithm, pamars, "http://shmtn.b2m.cn/inter/sendSingleSMS", false, encode);
        if ("SUCCESS".equals(result.getCode())) {
            SmsResponse response = JsonHelper.fromJson(SmsResponse.class, result.getResult());
            SysLog.info("result code :" + result.getCode());
            return "0";
        }else{
            SysLog.error("发送短信 result="+result);
        }
        SysLog.info("=============end setSingleSms==================");

        return "-1";
    }

    /**
     * 检查短信数量
     * @param configureDao
     * @param userinfo
     * @return
     */
    protected static boolean checkSMSCount(ConfigureDao configureDao, UserInfo userinfo) {
        int smscount = userinfo.getSmsCount() + userinfo.getWxSmsCount() + userinfo.getAppSmsCount();
        Configures conf = configureDao.getConfigure(userinfo.getUserid(), Constant.SMSCOUNT);
        if (null == conf) {
            conf = configureDao.getDefaultConfigure(Constant.SMSCOUNT);
        }

        if (smscount > Integer.parseInt(conf.getValue())) {
            SysLog.error("短信已用完:"+userinfo.getUserPrintInfo());
            return true;
        }

        if ((Integer.parseInt(conf.getValue())-smscount)==100
        ||(Integer.parseInt(conf.getValue())-smscount)==50
        ||(Integer.parseInt(conf.getValue())-smscount)==30) {
            SysLog.warn("短信快用完:"+userinfo.getUserPrintInfo()+" 剩余短信数量:"+(Integer.parseInt(conf.getValue())-smscount));
            sendSMSWarning("尊敬的客户，您的短信余额已不足"+(Integer.parseInt(conf.getValue())-smscount)+"条，为了保证您的业务正常使用，请尽快充值。",userinfo);
            return true;
        }
        return false;
    }

    /**
     * 发送短信条数告警通知给管理员
     * @param warningMsg
     * @param userinfo
     * @return
     */
    public static String sendSMSWarning(String warningMsg,UserInfo userinfo) {
        Map<String,String> params=new HashMap<String,String>();
        params.put("message", warningMsg);
        params.put("phone", userinfo.getPhone());
        return sendSmsCodeByYiMei(params,userinfo);
    }

    public static String sendSmsCodeByYiMei(Map<String, String> params, ConfigureDao configureDao, UserInfo userinfo) {
        if (checkSMSCount(configureDao, userinfo)) return "43";
        return sendSmsCodeByYiMei(params,userinfo);
    }

    private static String sendSmsCodeByYiMei(Map<String, String> params, UserInfo userinfo) {
        SysLog.info("=============begin setSingleSms==================");
        SmsSingleRequest pamars = new SmsSingleRequest();
        pamars.setContent(params.get("message"));
        pamars.setCustomSmsId(null);
        pamars.setExtendedCode(null);
        pamars.setMobile(params.get("phone"));
        ResultModel result = request(APPID, SECRETKEY, algorithm, pamars, "http://shmtn.b2m.cn/inter/sendSingleSMS", false, encode);
        if ("SUCCESS".equals(result.getCode())) {
            SysLog.info("result code :" + result.getCode());
            SmsResponse response = JsonHelper.fromJson(SmsResponse.class, result.getResult());
            return "0";
        }else{
            SysLog.error("发送短信 result="+result+userinfo.getUserPrintInfo());
        }
        SysLog.info("=============end setSingleSms==================");
        return "-1";
    }

    public static ResultModel request(String appId, String secretKey, String algorithm, Object content, String url, final boolean isGzip, String encode) {
        Map<String, String> headers = new HashMap<String, String>();
        EmayHttpRequestBytes request = null;
        try {
            headers.put("appId", appId);
            headers.put("encode", encode);
            String requestJson = JsonHelper.toJsonString(content);
            SysLog.info("result json: " + requestJson);
            byte[] bytes = requestJson.getBytes(encode);
            SysLog.info("request data size : " + bytes.length);
            if (isGzip) {
                headers.put("gzip", "on");
                bytes = GZIPUtils.compress(bytes);
                SysLog.info("request data size [com]: " + bytes.length);
            }
            byte[] parambytes = AES.encrypt(bytes, secretKey.getBytes(), algorithm);
            SysLog.info("request data size [en] : " + parambytes.length);
            request = new EmayHttpRequestBytes(url, encode, "POST", headers, null, parambytes);
        } catch (Exception e) {
            SysLog.error("短信加密异常");
            e.printStackTrace();
        }
        EmayHttpClient client = new EmayHttpClient();
        String code = null;
        String result = null;
        try {
            EmayHttpResponseBytes res = client.service(request, new EmayHttpResponseBytesPraser());
            if (res == null) {
                SysLog.error("请求接口异常");
                return new ResultModel(code, result);
            }
            if (res.getResultCode().equals(EmayHttpResultCode.SUCCESS)) {
                if (res.getHttpCode() == 200) {
                    code = res.getHeaders().get("result");
                    if (code.equals("SUCCESS")) {
                        byte[] data = res.getResultBytes();
                        SysLog.info("response data size [en and com] : " + data.length);
                        data = AES.decrypt(data, secretKey.getBytes(), algorithm);
                        if (isGzip) {
                            data = GZIPUtils.decompress(data);
                        }
                        SysLog.info("response data size : " + data.length);
                        result = new String(data, encode);
                        SysLog.info("response json: " + result);
                    }
                } else {
                    SysLog.info("请求接口异常,请求码:" + res.getHttpCode());
                }
            } else {
                SysLog.info("请求接口网络异常:" + res.getResultCode().getCode());
            }
        } catch (Exception e) {
            SysLog.error("解析失败");
            e.printStackTrace();
        }
        ResultModel re = new ResultModel(code, result);
        return re;
    }

    public static String encodeImgageToBase64(URL imageUrl) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }    // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
    }

    public static String produceId(int c) {
        String a = "abcdefghjkmnopqrstuvwxyz1234567890ABCDEFGHJKMNOPQRSTUVWXYZ1234567890";
        char[] rands = new char[c];
        for (int i = 0; i < rands.length; i++) {
            int rand = (int) (Math.random() * a.length());
            rands[i] = a.charAt(rand);
        }

        return String.valueOf(rands);
    }

    public static String produceId() {
        String a = "1234567890";
        char[] rands = new char[6];
        for (int i = 0; i < rands.length; i++) {
            int rand = (int) (Math.random() * a.length());
            rands[i] = a.charAt(rand);
        }

        return String.valueOf(rands);
    }

    public static boolean validateUser(String token, String id) {
        String tokenuserid = token.substring(0, token.indexOf("-"));
        if (tokenuserid.equals(id)) {
            return true;
        }

        return false;
    }

    public static String intToHex(int n) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            sb = sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a;
    }

    public static String toChineseHex(String s) {
        String ss = s;
        byte[] bt = new byte[0];

        try {
            bt = ss.getBytes("GB2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s1 = "";
        for (int i = 0; i < bt.length; i++) {
            String tempStr = Integer.toHexString(bt[i]);
            if (tempStr.length() > 2)
                tempStr = tempStr.substring(tempStr.length() - 2);
            s1 = s1 + tempStr + "";
        }
        return s1.toUpperCase();
    }

    public static String SerialData(String content) {
        String header = "FE98";
        String dataLength = "006C";  //更改
        String data1 = "9754000000000000000000010101";
        String sheader = "FE5C4B89";
        String sdataLength = "59000000";//更改
        String data2 = "3100006CB9";
        String sdataLength2 = "46000000";//更改
        String material = "303030303030303032";
        String data3 = "2C0104FF303130313031393931323331";
        String mLength = "13000000";//暂时不更改
        String data4 = "55AA0000373232313231000008001000011100";
        String clength = "12000000";//更改
        content = toChineseHex(content) + "FF00010001000100677C";
        String end = "FFFFF66CF6";

        String intclength = intToHex(content.length() / 2);
        clength = strLength(intclength);

        String strsdataLength2 = material + data3 + mLength + data4 + clength + content;
        String intsdataLength2 = intToHex(strsdataLength2.length() / 2);
        sdataLength2 = strLength(intsdataLength2);


        String result = dataLength + data1 + sheader + sdataLength + data2 + sdataLength2 + material + data3 + mLength + data4 + clength + content + end;
        String resultLength = intToHex(result.length() / 2);
        dataLength = dataLength(resultLength);
        result = header + dataLength + data1 + sheader + sdataLength + data2 + sdataLength2 + material + data3 + mLength + data4 + clength + content + end;

        return result;
    }

    public static String dataLength(String str) {
        int length = 4 - str.length();
        for (int i = 0; i < length; i++) {
            str = "0" + str;
        }
        return str;
    }

    public static String strLength(String str) {
        int length = 8 - str.length();
        for (int i = 0; i < length; i++) {
            str += "0";
        }
        return str;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param byteArray 要转码的字节数组
     * @return 返回转码后的16进制字符串
     */
    public static String byteArrayToHexStr(byte byteArray[]) {
        StringBuffer buffer = new StringBuffer(byteArray.length * 2);
        int i;
        for (i = 0; i < byteArray.length; i++) {
            if (((int) byteArray[i] & 0xff) < 0x10)//小于十前面补零
                buffer.append("0");
            buffer.append(Long.toString((int) byteArray[i] & 0xff, 16));
        }
        return buffer.toString();
    }

    public static String Base64ToImgage(String base64string,FastFileStorageClient storageClient) {
    	StorePath results = null;
        String url = "";
        try {
            BASE64Decoder decoder = new BASE64Decoder();

            byte[] bytes1 = decoder.decodeBuffer(base64string);
            String fileExtName = "jpg";
            InputStream inStream =new ByteArrayInputStream(bytes1);
            // 上传文件
            try {
            	results = storageClient.uploadFile(inStream, bytes1.length, fileExtName, null);
                url = Constant.FASTDFS_URL + results.getFullPath();
            } catch (Exception e) {
                SysLog.error("Upload file \"" + results.getFullPath() + "\"fails",SysLog.HTTP);
            }

        } catch (Exception e) {

            // TODO: handle exception

        }

        return url;
    }
    
    public static String Base64ToImgage(String base64string) {
//    	StorePath results = null;
        String url = "";
        try {
            BASE64Decoder decoder = new BASE64Decoder();

            byte[] bytes1 = decoder.decodeBuffer(base64string);
            String fileExtName = "png";
            InputStream inStream =new ByteArrayInputStream(bytes1);
            // 上传文件
            try {
//            	results = storageClient.uploadFile(inStream, bytes1.length, fileExtName, null);
//                url = Constant.FASTDFS_URL + results.getFullPath();
            	String fileName=UtilTools.produceId(8)+System.currentTimeMillis()+"."+fileExtName;
            	url = MinioTools.uploadFile(inStream, Constant.BUCKET_NAME, "image/png", "/base64/"+fileName);
            } catch (Exception e) {
//                SysLog.error("Upload file \"" + results.getFullPath() + "\"fails",SysLog.HTTP);
            	SysLog.error("Upload file \"" + url + "\"fails",SysLog.HTTP);
            }

        } catch (Exception e) {

            // TODO: handle exception

        }

        return url;
    }

    /**
     * 文件上传到fdfs
     */
    public static String fileToImage(File file,FastFileStorageClient storageClient) {
    	StorePath results = null;
        String url = "";
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            //获取输入流
            String name = file.getName();
            String prefix = name.substring(name.lastIndexOf(".") + 1);
            String suffixList = "jpg,gif,png,ico,bmp,jpeg";
            if (!suffixList.contains(prefix.trim().toLowerCase())) {
                return "";
            }

            //新的 byte 数组输出流，缓冲区容量1024byte
            bos = new ByteArrayOutputStream(2048);
            //缓存
            byte[] b = new byte[2048];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }


            byte[] fileBuff = bos.toByteArray();
            String fileId = "";

            // 上传文件
            try {
            	results = storageClient.uploadFile(fis, file.length(), prefix, null);
                url = Constant.FASTDFS_URL + results.getFullPath();
            } catch (Exception e) {
                SysLog.error("Upload file \"" + fileId + "\"fails",SysLog.HTTP);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return url;
    }

    //minio
    public static String fileToImage(File file,String path) {
        String url = "";
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            //获取输入流
            String name = file.getName();
            String prefix = name.substring(name.lastIndexOf(".") + 1);
            String suffixList = "jpg,gif,png,ico,bmp,jpeg";
            if (!suffixList.contains(prefix.trim().toLowerCase())) {
                return "";
            }

            //新的 byte 数组输出流，缓冲区容量1024byte
//            bos = new ByteArrayOutputStream(2048);
//            //缓存
//            byte[] b = new byte[2048];
//            int n;
//            while ((n = fis.read(b)) != -1) {
//                bos.write(b, 0, n);
//            }


            // 上传文件
            try {
                String fileName=UtilTools.produceId(8)+System.currentTimeMillis()+"."+prefix;
            	url = MinioTools.uploadFile(fis, Constant.BUCKET_NAME, "image/png", path+fileName);
            } catch (Exception e) {
                SysLog.error("Upload file \"" + url + "\"fails",SysLog.HTTP);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return url;
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hexStr 要转换的16进制字符串
     * @return 返回转码后的字节数组
     */

    public static byte[] hexStrToByteArray(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] encrypted = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);//取高位字节
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);//取低位字节
            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }

    public static int binarysearchKey(Object[] array, int targetNum) {

        //Object[] array = temp.clone();
        Arrays.sort(array);
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
        int left = 0, right = 0;
        for (right = array.length - 1; left != right; ) {
            int midIndex = (right + left) / 2;
            int mid = (right - left);
            int midValue = (Integer) array[midIndex];
            if (targetNum == midValue) {
                return midIndex;
            }

            if (targetNum > midValue) {
                left = midIndex;
            } else {
                right = midIndex;
            }

            if (mid <= 1) {
                break;
            }
        }
        int rightnum = ((Integer) array[right]).intValue();
        int leftnum = ((Integer) array[left]).intValue();
        int ret = Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? rightnum : leftnum;
        System.out.println("和要查找的数：" + targetNum + "最接近的数：" + ret);
        return ret;
    }

    /**
     * date2 和 date1 相差几天
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //不同年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //同一年
        {
            return day2 - day1;
        }
    }
    
    public static boolean arrayContain(String gids[],String mgids[]) {
    	boolean auth=false;
		for(String gid : gids){
			 for(String mgid :mgids) {
				   if(gid.equals(mgid)) {
					   auth=false;
					   break;
				   }else {
					   auth=true;
				   }
				 }
			 
				 if(auth){
					   break;
				 }
		}
		
		return auth;
    }

    /**
     * 优先验证token，没有token的情况验证签名
     * @param redisTemplate
     * @param tokenServer
     * @param request
     * @param userid
     * @return null 验证通过，非null 返回错误码
     */
    public static RespInfo checkTokenAndSign(RedisTemplate redisTemplate, TokenServer tokenServer, HttpServletRequest request, int userid){
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

            if (authToken != null && authToken.getUserid() != userid) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

            if(authToken != null){
                //token 验证
                HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

                if (hashOperations.hasKey(authToken.getToken(), "id")) {
                    Long expireDate = (long) hashOperations.get(authToken.getToken(), "id");
                    Date currentDate = new Date();
                    if (expireDate < currentDate.getTime()) {
                        return new RespInfo(ErrorEnum.E_608.getCode(), ErrorEnum.E_608.getMsg());
                    }
                }else {
                    return new RespInfo(ErrorEnum.E_608.getCode(), ErrorEnum.E_608.getMsg());
                }
            }else {
                return new RespInfo(ErrorEnum.E_608.getCode(), ErrorEnum.E_608.getMsg());
            }
        }catch (RuntimeException e){
            //没有token就检查签名
            RespInfo respInfo = tokenServer.checkSign(request);
            if(respInfo != null){
                return respInfo;
            }
        }
        return null;
    }
    
    
    public static String getTokenByPcode(RedisTemplate redisTemplate,String key,String hashkey) {
    	  HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
          if(hashOperations.hasKey(key, hashkey)&&null!=hashOperations.get(key, hashkey)){
          	String value=(String) hashOperations.get(key, hashkey);
          	hashOperations.delete(key, hashkey);
          	return value;
          }
          
          return null;
    }
    
    public static void main(String args[]) {
//		  getSSLEmailConf("smtp.ym.163.com","994","service@coolvisit.com","ZoneZone8006","273261987@qq.com","222");
    	String avatar="https://dev.coolvisit.top/group1/M00/00/01/rBEABGEBFoeAcYU2AAUcSMY1kZY313.jpg";
    	 String[] url= avatar.substring("https://dev.coolvisit.top/".length()).split("/", 2);
    }

}
