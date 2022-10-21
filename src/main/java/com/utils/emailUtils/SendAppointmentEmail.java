package com.utils.emailUtils;

import com.client.bean.Visitor;
import com.utils.MD5;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendAppointmentEmail {
	public boolean send(UserInfo userinfo,Employee emp,Visitor vt) {
		 SimpleDateFormat time=new SimpleDateFormat("yyyy年MM月dd日 HH:mm"); 
		
		 StringBuffer content=new StringBuffer();
		 
		 String photourl="https://www.coolvisit.com/assets/img/avatar.jpg";
		 
		 if(!"".equals(vt.getVphoto())&&null!=vt.getVphoto()){
			 photourl=vt.getVphoto();
		 }
		 
		 content.append("<TABLE cellSpacing=0 cellPadding=0 width='580px' align=center bgColor=#F5F5F5 border=0>"+
		 "<TR><TD><TABLE cellSpacing=0 cellPadding=0 width='580px' align=center bgColor=#F5F5F5 border=0>"+
		 "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 30px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 0px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=30 width='100%'></TD></TR>"+
		 "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 30px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 0px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=30 align=center>"+
		 "<IMG style='BORDER-TOP: #fff 3px solid; BORDER-RIGHT: #fff 3px solid; BORDER-BOTTOM: #fff 3px solid; BORDER-LEFT: #fff 3px solid; border-radius: 150px' src='"+photourl+"' width=150 height=150></TD></TR></TABLE></TD></TR>"+
		 "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 30px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 0px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=30 width='100%'></TD></TR></TABLE>"+
		 "<TABLE cellSpacing=0 cellPadding=0 width='580px' align=center bgColor=#F5F5F5 border=0><TR>"+
		 "<TD><TABLE cellSpacing=0 cellPadding=0 align=center width='100%' border=0><TR>"+
		 "<TD height=50></TD></TR>"+
		 "<TR><TD align='center' colspan='2'><p><font size='6'><span style='background-color: transparent ;'>"+vt.getVname()+"正在等候见您</span></font></p></TD></TR><TR>"+
		 "<TD height=17></TD></TR>"+
		 "<TR><TD align='center' colspan='2'><p><font size='5'><span style='background-color: transparent ;'>"+vt.getVname()+"于"+time.format(vt.getVisitdate())+"到达公司</span></font></p></TD></TR>"+
		 "<TR><TD colspan='2' style='FONT-SIZE: 1px; HEIGHT: 50px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=50></TD></TR>"+
		 "<TR><TD colspan='2' style='FONT-SIZE: 1px; HEIGHT: 1px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' bgColor=#e0e0e0 height=1></TD></TR>"+
		 "<TR><TD colspan='2' style='FONT-SIZE: 1px; HEIGHT: 50px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=50></TD></TR>");

		 content.append( "<TR><TD align='right'><p><font size='5'><span style='background-color: transparent ;'>来访者姓名：</span></font></p></TD>"+
					 "<TD><font size='5'><span style='background-color: transparent ;'>"+vt.getVname()+"</span></font></TD></TR>"+
					 "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 22px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=22></TD></TR>");
		 
		 content.append( "<TR><TD align='right'><p><font size='5'><span style='background-color: transparent ;'>手机号：</span></font></p></TD>"+
				 "<TD><font size='5'><span style='background-color: transparent ;'>"+vt.getVphone()+"</span></font></TD></TR>"+
				 "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 22px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=22></TD></TR>");
		 
		 content.append( "<TR><TD align='right'><p><font size='5'><span style='background-color: transparent ;'>拜访事由：</span></font></p></TD>"+
				 "<TD><font size='5'><span style='background-color: transparent ;'>"+vt.getVisitType()+"</span></font></TD></TR>"+
				 "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 22px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=22></TD></TR>");
		  
		 content.append("</TABLE></TD></TR></TABLE><TABLE cellSpacing=0 cellPadding=0 width='580px' align=center border=0><TR>"+
		"<TD style='FONT-SIZE: 1px; HEIGHT: 5px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=5 width='100%'></TD></TR></TABLE>");
	 
//		 content.append("<TABLE width='580px' align=center border=0><TR>"+
//					"<TD align='center'><IMG src='https://www.coolvisit.com/assets/img/qrcode_for_gh_c43286c7c025_258.jpg' width=100 height=100></TD></TR></TABLE>");
//		 content.append("<TABLE width='580px' align=center border=0><TR><TD align='center'><p>扫描二维码，通知方式随心设</p></TD></TR></TABLE>");
//		 if(userinfo.getPreRegisterSwitch()==1){
//			 content.append("<TABLE width='580px' align=center border=0><TR><TD align='center'><p><a href ='https://www.coolvisit.com/order.html?empid="+emp.getEmpid()+"&digest="+MD5.crypt(emp.getEmpEmail())+"'>30秒填写邀请函，两步发送预约短信</a></p></TD></TR></TABLE>");
//		 }
	 try { 
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials;
        
    	if(null==userinfo.getDomain()||"".equals(userinfo.getDomain())){
    	  credentials = new WebCredentials(userinfo.getEmailAccount(), userinfo.getEmailPwd());
    	  service.setUrl(new URI(userinfo.getExchange()));
    	}else{
    	  credentials = new WebCredentials(userinfo.getEmailAccount(), userinfo.getEmailPwd(),userinfo.getDomain());
    	  service.setUrl(new URI(userinfo.getExchange()));
    	}
        
        service.setCredentials(credentials);
        service.setPreAuthenticate(true);
		Date d=new Date();
        
      //实例化一个Appointment
        Appointment appointment = new Appointment(service);
        //约会主题
        appointment.setSubject(vt.getVname()+"来访提醒！");
        //约会内容
        appointment.setBody(MessageBody.getMessageBodyFromText(content.toString()));
        
        //约会开始时间2010-6-1 12:30：00
        appointment.setStart(new Date(d.getTime()+60*1000)); 
        //约会结束
        appointment.setEnd(new Date(d.getTime()+10*60*1000));
        //约会的位置
        appointment.setLocation("公司前台");
        
        //添加与会者
        appointment.getRequiredAttendees().add(emp.getEmpEmail());
        // 从2010-6-1 12:30：00开始每周举行一次
//        appointment.Recurrence = new Recurrence.WeeklyPattern(
//        appointment.Start,
//        1,/*每一周一次*/
//        DayOfWeek.Saturday
//        );
                  //可以设置发送通知的方式，如：
                  //Appointment.Save(SendInvitationsMode.SendOnlyToAll)
       
 //       appointment.update(ConflictResolutionMode.AutoResolve);
        appointment.save();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
    }
	
}
