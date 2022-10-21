package com.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class PoolHttpClientUtil {
    
    private final static Logger logger = LoggerFactory.getLogger(PoolHttpClientUtil.class);
     
    private  PoolingHttpClientConnectionManager connManager = null;
    private  CloseableHttpClient httpclient;
    
    public  void init(){
    	try {	
			SSLContext SSLContext = 
			        new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
			        {
			            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
			            {
			                return true;
			            }
			        }).build();
	        	
	            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	                    .register("http", PlainConnectionSocketFactory.INSTANCE)
	                    .register("https", new SSLConnectionSocketFactory(SSLContext))
	                    .build();
	             
	            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
	            // Create socket configuration
	            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
	            connManager.setDefaultSocketConfig(socketConfig);
	            // Create message constraints
	            MessageConstraints messageConstraints = MessageConstraints.custom()
	                .setMaxHeaderCount(200)
	                .setMaxLineLength(2000)
	                .build();
	            // Create connection configuration
	            ConnectionConfig connectionConfig = ConnectionConfig.custom()
	                .setMalformedInputAction(CodingErrorAction.IGNORE)
	                .setUnmappableInputAction(CodingErrorAction.IGNORE)
	                .setCharset(Consts.UTF_8)
	                .setMessageConstraints(messageConstraints)
	                .build();
	            connManager.setDefaultConnectionConfig(connectionConfig);
	            connManager.setMaxTotal(150);
	            connManager.setDefaultMaxPerRoute(20);
	            httpclient = HttpClients.custom().setSSLHostnameVerifier(SSLConnectionSocketFactory.getDefaultHostnameVerifier()).setConnectionManager(connManager).build();
	        } catch (KeyManagementException e) {
	            logger.error("KeyManagementException", e);
	        } catch (NoSuchAlgorithmException e) {
	            logger.error("NoSuchAlgorithmException", e);
	        } catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    public  String postJsonBody(String url, int timeout, Map<String, Object> map, String encoding){
        HttpPost post = new HttpPost(url);
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
        }
        return "";
    }
    
    public  String postJsonBodyOther(String url, int timeout, Map<String, Object> map, String encoding){
        HttpPost post = new HttpPost(url);
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
        }
        return "";
    }
     
    @SuppressWarnings("deprecation")
    public  String invokeGet(String url, Map<String, String> params, String encode, int connectTimeout) {
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
        }
        return responseString;
    }
    
    public  void  closeConnect(){
	 try {
			httpclient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         connManager.shutdown();
    }
     
    public final static int connectTimeout = 5000;
    /**
     * HTTPS请求，默认超时为5S
     * @param reqURL
     * @param params
     * @return
     */
    public  String connectPostHttps(String reqURL, Map<String, String> params) {
        String responseContent = null;
        HttpPost httpPost = new HttpPost(reqURL); 
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(connectTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectTimeout).build();
             
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); 
            // 绑定到请求 Entry
            for (Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            
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
        }
        return responseContent;
         
    }
 
}
