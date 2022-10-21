package com.utils.emailUtils;

import com.client.bean.Visitor;
import com.utils.UtilTools;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class SendTextEmail {
	public boolean send(UserInfo userinfo,Employee emp,Visitor vt) {  
	   JavaMailSenderImpl mailSender=UtilTools.getEmailConf(userinfo.getSmtp(),userinfo.getSmtpPort(),userinfo.getEmailAccount(),userinfo.getEmailPwd());
	    //获取JavaMailSender bean  
	   SimpleMailMessage mail = new SimpleMailMessage(); //<span style="color: #ff0000;">注意SimpleMailMessage只能用来发送text格式的邮件</span>  
	   try {  
	    mail.setTo(emp.getEmpEmail());//接受者  
	    mail.setFrom(userinfo.getEmailAccount());//发送者,这里还可以另起Email别名，不用和xml里的username一致  
	    mail.setSubject("来访提醒！");//主题  
	    mail.setText("您好!有姓名叫做"+vt.getVname()+"的人来拜访您！请您速到前台处接待。");//邮件内容  
	    mailSender.send(mail);  
	   } catch (Exception e) {  
		   e.printStackTrace();  
		   return false;
	   }  
	   return true;
	}
	
	
	public boolean send(String email,String digest) {  
		 return UtilTools.getSSLEmailConf("smtp.ym.163.com","994","service@coolvisit.com","ZoneZone8006",email,digest);
		}
	
	public boolean sendCheckCode(String email,String checkcode) {  
		JavaMailSenderImpl mailSender=UtilTools.getEmailConf("smtp.ym.163.com",25,"service@coolvisit.com","ZoneZone8006");
	    //获取JavaMailSender bean  
	   SimpleMailMessage mail = new SimpleMailMessage(); //<span style="color: #ff0000;">注意SimpleMailMessage只能用来发送text格式的邮件</span>  
	   try {  
	    String postfix=email.substring(email.indexOf("@")+1,email.indexOf("."));
	    if("uuzu".equals(postfix)){
	    	String uuzuemail=email.substring(0,email.indexOf("@"))+"@youzu.com";
	    	mail.setTo(uuzuemail);//接受者  
	   	    mail.setFrom("service@coolvisit.com");//发送者
	   	    mail.setSubject("来访通微信验证码");//主题  
	   	    mail.setText("您的微信验证码为："+checkcode);//邮件内容
	   	   
	   	    mailSender.send(mail);  
	    }
		  
	    mail.setTo(email);//接受者  
	    mail.setFrom("service@coolvisit.com");//发送者
	    mail.setSubject("来访通微信验证码");//主题  
	    mail.setText("您的微信验证码为："+checkcode);//邮件内容
	   
	    mailSender.send(mail);  
	   } catch (Exception e) {  
		   e.printStackTrace();  
		   return false;
	   }  
	   return true;
	}
}
