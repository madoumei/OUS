package com.utils.httpUtils;

import com.config.exception.ErrorEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.AESUtil;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

public class HttpClientUtil {
    
    private final static Logger logger = Logger.getLogger("mylogger1");
     
    public static CloseableHttpClient init(){
        CloseableHttpClient httpclient = null;
    	try {	
			SSLContext SSLContext = 
			        new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
			        {
			            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
			            {
			                return true;
			            }
			        }).build();
	        	
			 SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContext, new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"}, null,  
					 SSLConnectionSocketFactory.getDefaultHostnameVerifier());  
			 
			 httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();  
	        } catch (KeyManagementException e) {
	            logger.error("KeyManagementException", e);
	        } catch (NoSuchAlgorithmException e) {
	            logger.error("NoSuchAlgorithmException", e);
	        } catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	return httpclient;
    }
    
    
    public static String postJsonBody(String url, int timeout, Map<String, Object> map, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);
            
            ObjectMapper objectMapper=new ObjectMapper();
            
            String str1 = objectMapper.writeValueAsString(map).replace("\\", "");
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return "";
    }

    public static String postJsonBodySource(String url, int timeout, String content, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            String str1 = content.replace("\\", "");
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
                httpclient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String postJsonBodySourceNoReplace(String url, int timeout, Map<String,Object> map, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            ObjectMapper objectMapper=new ObjectMapper();

            String str1 = objectMapper.writeValueAsString(map);
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
                httpclient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String postJsonBodySourceWechat(String url, int timeout, String content, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            String str1 = content;
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
                httpclient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String postTextBody(String url, int timeout, Map<String, Object> map, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "text/plain");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            ObjectMapper objectMapper=new ObjectMapper();

            String str1 = objectMapper.writeValueAsString(map).replace("\\", "");
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
                httpclient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }
    
    public static String postVnetJsonBody(String url, int timeout, Map<String, Object> map, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);
            
            ObjectMapper objectMapper=new ObjectMapper();
            
            String str1 = objectMapper.writeValueAsString(map);
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return "";
    }
    
    public static String postJsonBodyOther(String url, int timeout, Map<String, Object> map, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);
            
            ObjectMapper objectMapper=new ObjectMapper();
            
            String str1 = objectMapper.writeValueAsString(map);
            post.setEntity(new StringEntity(str1, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return "";
    }

    public static String postJsonBodyOtherSource(String url, int timeout, String content, String encoding){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            post.setEntity(new StringEntity(content, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+content);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
                httpclient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }
     
    @SuppressWarnings("deprecation")
    public static String invokeGet(String url, Map<String, String> params, String encode, int connectTimeout) {
        String responseString = null;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(connectTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout).build();
         
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        for (Entry<String, String> entry : params.entrySet()) {
            if (i == 0 && !url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            String value = entry.getValue();
            try {
                sb.append(URLEncoder.encode(value, encode));
            } catch (UnsupportedEncodingException e) {
                logger.warn("encode http get params error, value is "+value, e);
                sb.append(URLEncoder.encode(value));
            }
            i++;
        }
        logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        get.setConfig(requestConfig);
        CloseableHttpClient httpclient =init();
        try {
            CloseableHttpResponse response =httpclient.execute(get);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        responseString = EntityUtils.toString(entity, encode);
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
                return responseString;
            } finally {
                if(response != null){
                    response.close();
                }
            }
            logger.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
        } catch (SocketTimeoutException e) {
            logger.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
            return responseString;
        } catch (Exception e) {
            logger.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
        } finally {
            get.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return responseString;
    }
    
    @SuppressWarnings("deprecation")
    public static String invokeGetNoEnCode(String url, Map<String, String> params, String encode, int connectTimeout) {
        String responseString = null;
    
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(connectTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout).build();
         
        StringBuilder sb = new StringBuilder();
        sb.append(url);
 
        int i = 0;
        for (Entry<String, String> entry : params.entrySet()) {
            if (i == 0 && !url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            String value = entry.getValue();
            sb.append(value);
            i++;
        }
//        logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
    
        HttpGet get = new HttpGet(sb.toString());
        get.setConfig(requestConfig);
        CloseableHttpClient httpclient =init();
        try{
            CloseableHttpResponse response =httpclient.execute(get);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        responseString = EntityUtils.toString(entity, encode);
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                logger.info(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
                e.printStackTrace();
                return responseString;
            } finally {
                if(response != null){
                    response.close();
                }
            }
            logger.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
        } catch (SocketTimeoutException e) {
            logger.info(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
            e.printStackTrace();
            return responseString;
        } catch (Exception e) {
            logger.info(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
            e.printStackTrace();
            return responseString;
        } finally {
            get.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return responseString;
    }
    
    public static String invokeGetForFs(String url, Map<String, String> params, String encode, int connectTimeout,String accessToken) {
        String responseString = null;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(connectTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout).build();
         
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        for (Entry<String, String> entry : params.entrySet()) {
            if (i == 0 && !url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            String value = entry.getValue();
            try {
                sb.append(URLEncoder.encode(value, encode));
            } catch (UnsupportedEncodingException e) {
                logger.warn("encode http get params error, value is "+value, e);
                sb.append(URLEncoder.encode(value));
            }
            i++;
        }
        logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        get.addHeader("Authorization", "Bearer "+accessToken);
        get.addHeader("Content-Type", "application/json; charset=utf-8");
        get.setConfig(requestConfig);
        CloseableHttpClient httpclient =init();
        try {
            CloseableHttpResponse response =httpclient.execute(get);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        responseString = EntityUtils.toString(entity, encode);
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
                return responseString;
            } finally {
                if(response != null){
                    response.close();
                }
            }
            logger.info(String.format("[HttpUtils Get]Debug url:%s , response string %s:", sb.toString(), responseString));
        } catch (SocketTimeoutException e) {
            logger.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
            return responseString;
        } catch (Exception e) {
            logger.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
        } finally {
            get.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return responseString;
    }
    
    public static String postJsonBodyForFs(String url, int timeout, Map<String, Object> map, String encoding,String accessToken){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", "Bearer "+accessToken);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);
            
            ObjectMapper objectMapper=new ObjectMapper();
            
            String str1 = objectMapper.writeValueAsString(map);
            post.setEntity(new StringEntity(str1, encoding));
            System.out.println(str1);
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+str1);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return "";
    }

    public static String postJsonBodyForFsSource(String url, int timeout, String content, String encoding,String accessToken){
        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient =init();
        try {
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", "Bearer "+accessToken);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            post.setEntity(new StringEntity(content, encoding));
            logger.info("[HttpUtils Post] begin invoke url:" + url + " , params:"+content);
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        logger.info("[HttpUtils Post]Debug response, url :" + url + " , response string :"+str);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            post.releaseConnection();
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
     
    public final static int connectTimeout = 5000;
    /**
     * HTTPS请求，默认超时为5S
     * @param reqURL
     * @param params
     * @return
     */
    public static String connectPostHttps(String reqURL, Map<String, String> params) {
        String responseContent = null;
        HttpPost httpPost = new HttpPost(reqURL); 
        CloseableHttpClient httpclient =init();
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(connectTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectTimeout).build();
             
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
            for (Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            
            // 绑定到请求 Entry
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
            httpPost.setConfig(requestConfig);
            
            CloseableHttpResponse response = httpclient.execute(httpPost);
             try {
                // 执行POST请求
                HttpEntity entity = response.getEntity(); // 获取响应实体
                try {
                    if (null != entity) {
                        responseContent = EntityUtils.toString(entity, Consts.UTF_8);
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
            logger.info("requestURI : "+httpPost.getURI()+", responseContent: " + responseContent);
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            httpPost.releaseConnection();
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return responseContent;
         
    }


    public static String getHttpErrorStr(ErrorEnum errorEnum){
        String ret =  "{\"status\":"+errorEnum.getCode()+",\"reason\":"+errorEnum.getMsg()+"}";
        return ret;
    }

    public static String getHttpErrorStr(ErrorEnum errorEnum,String msg){
        String ret =  "{\"status\":"+errorEnum.getCode()+",\"reason\":"+errorEnum.getMsg()+","+msg+"}";
        return ret;
    }
}
