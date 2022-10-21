package com.utils.emailUtils;

import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.web.bean.Appointment;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import com.web.bean.Usertemplate;
import com.web.service.AppointmentService;
import com.web.service.EmployeeService;
import com.web.service.UserService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadEmail {
	   private MimeMessage mimeMessage = null;    
	   private String saveAttachPath = ""; //附件下载后的存放目录    
	   private StringBuffer bodytext = new StringBuffer();//存放邮件内容    
	   private String dateformat = "yy-MM-dd HH:mm"; //默认的日前显示格式    
	  
	   public ReadEmail(MimeMessage mimeMessage) {    
	       this.mimeMessage = mimeMessage;    
	   }    
	  
	   public void setMimeMessage(MimeMessage mimeMessage) {    
	       this.mimeMessage = mimeMessage;    
	   }    
	  
	   /**   
	    * 获得发件人的地址和姓名   
	    */   
	   public String getFrom() throws Exception {    
	       InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();    
	       String from = address[0].getAddress();    
	       if (from == null)    
	           from = "";    
	       String personal = address[0].getPersonal();    
	       if (personal == null)    
	           personal = "";    
	       String fromaddr = personal + "<" + from + ">";    
	       return fromaddr;    
	   }    
	  
	   /**   
	    * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址   
	    */   
	   public String getMailAddress(String type) throws Exception {    
	       String mailaddr = "";    
	       String addtype = type.toUpperCase();    
	       InternetAddress[] address = null;    
	       if (addtype.equals("TO") || addtype.equals("CC")|| addtype.equals("BCC")) {    
	           if (addtype.equals("TO")) {    
	               address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);    
	           } else if (addtype.equals("CC")) {    
	               address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);    
	           } else {    
	               address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);    
	           }    
	           if (address != null) {    
	               for (int i = 0; i < address.length; i++) {    
	                   String email = address[i].getAddress();    
	                   if (email == null)    
	                       email = "";    
	                   else {    
	                       email = MimeUtility.decodeText(email);    
	                   }    
	                   String personal = address[i].getPersonal();    
	                   if (personal == null)    
	                       personal = "";    
	                   else {    
	                       personal = MimeUtility.decodeText(personal);    
	                   }    
	                   String compositeto = personal + "<" + email + ">";    
	                   mailaddr += "," + compositeto;    
	               }    
	               mailaddr = mailaddr.substring(1);    
	           }    
	       } else {    
	           throw new Exception("Error emailaddr type!");    
	       }    
	       return mailaddr;    
	   }    
	  
	   /**   
	    * 获得邮件主题   
	    */   
	   public String getSubject() throws MessagingException {    
	       String subject = "";    
	       try {    
	           subject = MimeUtility.decodeText(mimeMessage.getSubject());    
	           if (subject == null)    
	               subject = "";    
	       } catch (Exception exce) {}    
	       return subject;    
	   }    
	  
	   /**   
	    * 获得邮件发送日期   
	    */   
	   public String getSentDate() throws Exception {    
	       Date sentdate = mimeMessage.getSentDate();    
	       SimpleDateFormat format = new SimpleDateFormat(dateformat);    
	       return format.format(sentdate);    
	   }    
	   
	    /**   
	     * 获得邮件正文内容   
	     */   
	    public String getBodyText() {    
	        return bodytext.toString();    
	    }    
	   
	    /**   
	     * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析   
	     */   
	    public void getMailContent(Part part) throws Exception {    
	        String contenttype = part.getContentType();    
	        int nameindex = contenttype.indexOf("name");    
	        boolean conname = false;    
	        if (nameindex != -1)    
	            conname = true;    
	        System.out.println("CONTENTTYPE: " + contenttype);    
	        if (part.isMimeType("text/plain") && !conname) {    
	            bodytext.append((String) part.getContent());    
	        } else if (part.isMimeType("text/html") && !conname) {    
	            bodytext.append((String) part.getContent());    
	        } else if (part.isMimeType("multipart/*")) {    
	            Multipart multipart = (Multipart) part.getContent();    
	            int counts = multipart.getCount();    
	            for (int i = 0; i < counts; i++) {    
	                getMailContent(multipart.getBodyPart(i));    
	            }    
	        } else if (part.isMimeType("message/rfc822")) {    
	            getMailContent((Part) part.getContent());    
	        } else {}    
	    }    
	   
	    /**    
	     * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"   
	     */    
	    public boolean getReplySign() throws MessagingException {    
	        boolean replysign = false;    
	        String needreply[] = mimeMessage    
	                .getHeader("Disposition-Notification-To");    
	        if (needreply != null) {    
	            replysign = true;    
	        }    
	        return replysign;    
	    }    
	   
	    /**   
	     * 获得此邮件的Message-ID   
	     */   
	    public String getMessageId() throws MessagingException {    
	        return mimeMessage.getMessageID();    
	    }    
	   
	    /**   
	     * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】   
	     */   
	    public boolean isNew() throws MessagingException {    
	        boolean isnew = false;    
	        Flags flags = ((Message) mimeMessage).getFlags();    
	        Flags.Flag[] flag = flags.getSystemFlags();    
	        System.out.println("flags's length: " + flag.length);    
	        for (int i = 0; i < flag.length; i++) {    
	            if (flag[i] == Flags.Flag.SEEN) {    
	                isnew = true;    
	                System.out.println("seen Message.......");    
	                break;    
	            }    
	        }    
	        return isnew;    
	    }    
	   
	    /**   
	     * 判断此邮件是否包含附件   
	     */   
	    public boolean isContainAttach(Part part) throws Exception {    
	        boolean attachflag = false;    
	        String contentType = part.getContentType();    
	        if (part.isMimeType("multipart/*")) {    
	            Multipart mp = (Multipart) part.getContent();    
	            for (int i = 0; i < mp.getCount(); i++) {    
	                BodyPart mpart = mp.getBodyPart(i);    
	                String disposition = mpart.getDisposition();    
	                if ((disposition != null)    
	                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition    
	                                .equals(Part.INLINE))))    
	                    attachflag = true;    
	                else if (mpart.isMimeType("multipart/*")) {    
	                    attachflag = isContainAttach((Part) mpart);    
	                } else {    
	                    String contype = mpart.getContentType();    
	                    if (contype.toLowerCase().indexOf("application") != -1)    
	                        attachflag = true;    
	                    if (contype.toLowerCase().indexOf("name") != -1)    
	                        attachflag = true;    
	                }    
	            }    
	        } else if (part.isMimeType("message/rfc822")) {    
	            attachflag = isContainAttach((Part) part.getContent());    
	        }    
	        return attachflag;    
	    }    
	   
	    /**    
	     * 【保存附件】    
	     */    
	    public void saveAttachMent(Part part) throws Exception {    
	        String fileName = "";    
	        if (part.isMimeType("multipart/*")) {    
	            Multipart mp = (Multipart) part.getContent();    
	            for (int i = 0; i < mp.getCount(); i++) {    
	                BodyPart mpart = mp.getBodyPart(i);    
	                String disposition = mpart.getDisposition();    
	                if ((disposition != null)    
	                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition    
	                                .equals(Part.INLINE)))) {    
	                    fileName = mpart.getFileName();    
	                    if (fileName.toLowerCase().indexOf("gb2312") != -1) {    
	                        fileName = MimeUtility.decodeText(fileName);    
	                    }    
	                    saveFile(fileName, mpart.getInputStream());    
	                } else if (mpart.isMimeType("multipart/*")) {    
	                    saveAttachMent(mpart);    
	                } else {    
	                    fileName = mpart.getFileName();    
	                    if ((fileName != null)    
	                            && (fileName.toLowerCase().indexOf("GB2312") != -1)) {    
	                        fileName = MimeUtility.decodeText(fileName);    
	                        saveFile(fileName, mpart.getInputStream());    
	                    }    
	                }    
	            }    
	        } else if (part.isMimeType("message/rfc822")) {    
	            saveAttachMent((Part) part.getContent());    
	        }    
	    }    
	   
	    /**    
	     * 【设置附件存放路径】    
	     */    
	   
	    public void setAttachPath(String attachpath) {    
	        this.saveAttachPath = attachpath;    
	    }    
	   
	    /**   
	     * 【设置日期显示格式】   
	     */   
	    public void setDateFormat(String format) throws Exception {    
	        this.dateformat = format;    
	    }    
	   
	    /**   
	     * 【获得附件存放路径】   
	     */   
	    public String getAttachPath() {    
	        return saveAttachPath;    
	    }    
	   
	    /**   
	     * 【真正的保存附件到指定目录里】   
	     */   
	    private void saveFile(String fileName, InputStream in) throws Exception {    
	        String osName = System.getProperty("os.name");    
	        String storedir = getAttachPath();    
	        String separator = "";    
	        if (osName == null)    
	            osName = "";    
	        if (osName.toLowerCase().indexOf("win") != -1) {    
	            separator = "\\";   
	            if (storedir == null || storedir.equals(""))   
	                storedir = "c:\\tmp";   
	        } else {   
	            separator = "/";   
	            storedir = "/tmp";   
	        }   
	        File storefile = new File(storedir + separator + fileName);   
	        System.out.println("storefile's path: " + storefile.toString());   
	        // for(int i=0;storefile.exists();i++){   
	        // storefile = new File(storedir+separator+fileName+i);   
	        // }   
	        BufferedOutputStream bos = null;   
	        BufferedInputStream bis = null;   
	        try {   
	            bos = new BufferedOutputStream(new FileOutputStream(storefile));   
	            bis = new BufferedInputStream(in);   
	            int c;   
	            while ((c = bis.read()) != -1) {   
	                bos.write(c);   
	                bos.flush();   
	            }   
	        } catch (Exception exception) {   
	            exception.printStackTrace();   
	            throw new Exception("文件保存失败!");   
	        } finally {   
	            bos.close();   
	            bis.close();   
	        }   
	    }   
	    
	    public static void addAppointment(List<Map<String,Object>> mlist,UserService userService,VisitorService visitorService,
	    		AppointmentService appointmentService,EmployeeService employeeService) {
	    	List<Appointment> atlist=new ArrayList<Appointment>();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm" );
	    	UserInfo userinfo =new UserInfo();
	    	try{
		    	for(int i=0;i<mlist.size();i++){
		    		 String[] sinfo=String.valueOf(mlist.get(i).get("sender")).split(",");
		    		 userinfo = userService.selectBycompany(sinfo[0]);
		    		if(userinfo==null){
		    			System.out.print("invalid user");
		    			return;
		    		}
		    		List<Employee> emplist=employeeService.checkEmployeeExists(userinfo.getUserid(), sinfo[2]);
		    		if(emplist.size()==0){
		    			System.out.print("no employee");
		    			continue;
		    		}
		    		
		    		Usertemplate ut=new Usertemplate();
		    		ut.setUserid(userinfo.getUserid());
		    		ut.setTemplateType("商务");
		    		ut.setGid(0);
		    		
		    		ut=appointmentService.getUsertemplate(ut);
		    		
		    		String[] rinfo=String.valueOf(mlist.get(i).get("receiver")).split(";");
		    		Date date=sdf.parse(String.valueOf(mlist.get(i).get("appdate")));
		    		String remark=String.valueOf(mlist.get(i).get("belongings"));
		    		String vcompany=String.valueOf(mlist.get(i).get("company"));
		    	   for(int s=0;s<rinfo.length;s++){
		    			Appointment app=new Appointment();
		    			String[] reveiver=rinfo[s].split(",");
		    			app.setUserid(userinfo.getUserid());
		    			app.setEmpid(emplist.get(0).getEmpid());
		    			app.setEmpName(emplist.get(0).getEmpName());
		    			app.setEmpPhone(emplist.get(0).getEmpPhone());
		    			app.setCompany(userinfo.getCardText());
		    			app.setName(reveiver[0]);
		    			app.setPhone(reveiver[1]);
		    			app.setVisitType("商务");
		    			app.setAddress(ut.getAddress());
		    			app.setInviteContent(ut.getInviteContent());
		    			app.setLatitude(ut.getLatitude());
		    			app.setLongitude(ut.getLongitude());
		    			app.setCompanyProfile(ut.getCompanyProfile());
		    			app.setTraffic(ut.getTraffic());
		    			app.setAppointmentDate(date);
		    			app.setRemark(remark);
		    			app.setVcompany(vcompany);
		    			
		    			atlist.add(app);
		    	
		    		}
		    	   
		    	   for(int p=0;p<atlist.size();p++){
					    Visitor vt=new Visitor();
						vt.setVname(atlist.get(p).getName());
						vt.setVphone(atlist.get(p).getPhone());
						vt.setVisitType(atlist.get(p).getVisitType());
						vt.setMid(0);
			 			if(atlist.get(p).getPhone().length()==11){
			 				appointmentService.addAppointment(atlist.get(p));
			 				vt.setVid(atlist.get(p).getId());
			 				String code=visitorService.sendAppointmentSMS(userinfo, emplist.get(0), vt);
			 				atlist.get(p).setSendStatus(2);
			 				
			 				if("0".equals(code)){
			 					atlist.remove(p);  
			 				    p--;
			 				}else{
								continue;
							}
			 			}else{
			 				continue;
			 			}
				 	}
				 
				 if(atlist.size()!=0){
					 appointmentService.batchUpdateAppSendStatus(atlist);
				 }
		    	}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
			}
		}
	    
	  
	    /**   
	     * PraseMimeMessage类测试   
	     */   
	    public static void main(String args[]) throws Exception {   
	    	Properties props = System.getProperties();   
	        props.put("mail.pop3.ssl.enable", true);  
	        props.put("mail.imap.host", "imap.163.com");   
	        props.put("mail.imap.port", "143");   
	        Session session = Session.getDefaultInstance(props, null);       

	        IMAPStore store;
	        IMAPFolder folder;
	        Message[] message = null;
			
			store = (IMAPStore) session.getStore("imap");
			store.connect("qicoolvisit@163.com","rpdxyzhkujcblfbq"); 
			folder = (IMAPFolder) store.getFolder("INBOX");   
		    folder.open(Folder.READ_WRITE);  
        
		    int total = folder.getMessageCount();      
        
		    message =  folder.getMessages(folder.getMessageCount()-folder.getUnreadMessageCount()+1,folder.getMessageCount());   
//	        System.out.println("Messages's length: " + total);   
	        ReadEmail pmm = null;   
	        for (int i = 0; i < message.length; i++) {   
	        	StringBuffer bodytext = new StringBuffer();
//	            Flags flags = message[i].getFlags();      
	            pmm = new ReadEmail((MimeMessage) message[i]);   
//	            System.out.println("Message " + i + " subject: " + pmm.getSubject());   
//	            System.out.println("Message " + i + " sentdate: "+ pmm.getSentDate());   
//	            System.out.println("Message " + i + " replysign: "+ pmm.getReplySign());   
//	            System.out.println("Message " + i + " hasRead: " + pmm.isNew());   
//	            System.out.println("Message " + i + "  containAttachment: "+ pmm.isContainAttach((Part) message[i]));   
	            System.out.println("Message " + i + " from: " + pmm.getFrom());   
//	            System.out.println("Message " + i + " to: "+ pmm.getMailAddress("to"));   
//	            System.out.println("Message " + i + " cc: "+ pmm.getMailAddress("cc"));   
//	            System.out.println("Message " + i + " bcc: "+ pmm.getMailAddress("bcc"));   
//	            pmm.setDateFormat("yy年MM月dd日 HH:mm");   
//	            System.out.println("Message " + i + " sentdate: "+ pmm.getSentDate());   
//	            System.out.println("Message " + i + " Message-ID: "+ pmm.getMessageId());   
//	            // 获得邮件内容===============   
//	            pmm.getMailContent((Part) message[i]);   
//	            System.out.println("Message " + i + " bodycontent: \r\n"   
//	                    + pmm.getBodyText()); 
//	            pmm.setAttachPath("c:\\");    
//	            pmm.saveAttachMent((Part) message[i]);  
	            	Multipart multipart;
	            try{
	            	 multipart = (Multipart) message[i].getContent();   
	            }catch(Exception e){
	            	message[i].setFlag(Flags.Flag.SEEN, true);
	            	continue;
	            }
//	            int counts = multipart.getCount();    
	            String str= String.valueOf(multipart.getBodyPart(0).getContent());
	            bodytext.append(str); 
	           
	            String content=bodytext.toString();
	            if(content.indexOf("{")==-1){
	            	message[i].setFlag(Flags.Flag.SEEN, true);
	            	continue;
	            }
	            
	            Map<String,Object> map=new HashMap<String,Object>();
	        	String[] regs = {"：","，","；",":",",",";"}; 
	            String[] ss = content.split("\n"); 
	            
	            for(int c=0;c<ss.length;c++){
	            	if(c<3){
						for ( int s = 0; s< regs.length / 2; s++ ) 
						{ 
							ss[c] = ss[c].replaceAll (regs[s], regs[s + regs.length / 2]); 
						} 
	            	}
					
					if(c==0){
						map.put("sender", ss[c].substring(ss[c].indexOf("{")+1, ss[c].indexOf("}")));
					}
					else if(c==1){
						map.put("receiver", ss[c].substring(ss[c].indexOf("{")+1, ss[c].indexOf("}")));
					}
					else if(c==2){
						map.put("appdate", ss[c].substring(ss[c].indexOf("{")+1, ss[c].indexOf("}")));
					}
					else if(c==3){
						map.put("cause", ss[c].substring(ss[c].indexOf("{")+1, ss[c].indexOf("}")));
					}
					else if(c==4){
						map.put("company", ss[c].substring(ss[c].indexOf("{")+1, ss[c].indexOf("}")));
					}else if(c==5){
						map.put("belongings", ss[c].substring(ss[c].indexOf("{")+1, ss[c].indexOf("}")));
					}
	            }
  
	            message[i].setFlag(Flags.Flag.SEEN, true);
	        }    
	        
	        if (folder != null)      
             folder.close(true);       
            if (store != null)      
             store.close();   
	    }    
}
