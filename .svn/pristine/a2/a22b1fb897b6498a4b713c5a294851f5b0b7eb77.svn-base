package com.utils.cacheUtils;

import com.client.bean.RespVisitor;

import javax.websocket.Session;
import java.util.List;

public class CacheManager {          

    public static final String TOKEN_WEIXIN = "token";//微信
    public static final String TOKEN_WXAPP = "WXAppToken";//小程序

    private BaseCache newsCache;
  
    private static CacheManager instance;      
  
    private static Object lock = new Object();             
  
    private CacheManager() {      
        newsCache = new BaseCache("access",7200);           
    }              
  
    public static CacheManager getInstance(){      
        if (instance == null){      
            synchronized( lock ){      
                if (instance == null){      
                    instance = new CacheManager();      
                }      
            }      
        }      
  
        return instance;      
  
    }         
  
  
    public void removeAllNews() {      
        newsCache.removeAll();      
    } 
    
    public void removeCache(String key) {      
        newsCache.remove(key);    
    } 
    
	public List<RespVisitor> getVisitorList(String newsID) {
    	  
        try {      
  
            @SuppressWarnings("unchecked")
			List<RespVisitor> list = (List<RespVisitor>) newsCache.get(newsID);
			return  list;      
  
        } catch (Exception e) {      
  
            System.out.println("getNews>>newsID["+newsID+"]>>"+e.getMessage());      
        }
		return null;      
  
    } 
    
    public String getToken(String newsID) {      
  	  
        try {      
  
            return (String) newsCache.get(newsID);      
  
        } catch (Exception e) {      
  
            System.out.println("getNews>>newsID["+newsID+"]>>"+e.getMessage());      
        }
		return null;      
  
    } 
    
    public void putToken(String newsid,String news)
    {
    	newsCache.put(newsid,news);      
    }  
    
    public void putVisitorList(String newsid,List<RespVisitor> news)
    {
    	newsCache.put(newsid,news);      
    }  
    
    public void putSession(String userid,Session session)
    {
    	 newsCache.put(userid,session);     
    }
    
    public Session getSession(String userid) {      
        try {      
  
            return (Session) newsCache.get(userid);      
  
        } catch (Exception e) {      
            System.out.println("getNews>>newsID["+userid+"]>>"+e.getMessage());      
        }
		return null;      
  
    } 

}
