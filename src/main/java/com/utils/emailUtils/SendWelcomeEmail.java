package com.utils.emailUtils;

import com.utils.UtilTools;
import com.web.bean.UserInfo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class SendWelcomeEmail {
	private String realpath;
	public String getRealpath() {
		return realpath;
	}

	public void setRealpath(String realpath) {
		this.realpath = realpath;
	}


	public boolean sendRegisterNotify(UserInfo userinfo) {  
		JavaMailSenderImpl mailSender=UtilTools.getEmailConf("smtp.163.com",25,"qicoolvisit@163.com","rpdxyzhkujcblfbq");
	    //获取JavaMailSender bean  
	   SimpleMailMessage mail = new SimpleMailMessage(); //<span style="color: #ff0000;">注意SimpleMailMessage只能用来发送text格式的邮件</span>  
	   SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	   try {  
	    mail.setTo("service@coolvisit.com");//接受者  
	    mail.setFrom("qicoolvisit@163.com");//发送者,这里还可以另起Email别名，不用和xml里的username一致  
	    mail.setSubject("新用户注册通知！");//主题  
	    mail.setText("用户公司名："+userinfo.getCompany()+"   注册时间："+time.format(userinfo.getRegDate())+" 姓名： "+userinfo.getUsername()+" 手机号： "+userinfo.getPhone());//邮件内容  
	    mailSender.send(mail);  
	   } catch (Exception e) {  
		   e.printStackTrace();  
		   return false;
	   }  
	   return true;
	}
	
	public boolean send(UserInfo userinfo) {  
		  VelocityEngine ve = new VelocityEngine();  
		  Properties p = new Properties();  
		  p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");  
		  p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");  
		  p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,this.getRealpath()+"WEB-INF/velocity");  

		  ve.init(p);  
		  Template template = ve.getTemplate("welcome.vm");  
		  VelocityContext context = new VelocityContext();  
//		  context.put("name","菜乒乓");  
		  StringWriter writer = new StringWriter();  
		  template.merge(context, writer);  
		  String result = writer.toString();  
		
		    //获取JavaMailSender bean  
		   MimeMessage mailMessage =(MimeMessage) UtilTools.getEmailConf("smtp.ym.163.com","994","service@coolvisit.com","ZoneZone8006");
		   try {  
			   MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage,true,"utf-8"); 
			   messageHelper.setTo(userinfo.getEmail());//接受者  
			   messageHelper.setFrom("service@coolvisit.com");//发送者,这里还可以另起Email别名，不用和xml里的username一致  
			   messageHelper.setSubject("欢迎使用来访通！");//主题  

	 
			   messageHelper.setText(result,true);//邮件内容  
			   Transport.send(mailMessage);  
		   } catch (Exception e) {  
			   e.printStackTrace();  
			   return false;
		   }finally{
			   try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		   }
		   return true;
		}
	
	}
