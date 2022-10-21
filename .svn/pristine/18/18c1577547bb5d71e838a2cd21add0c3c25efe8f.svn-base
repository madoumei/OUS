package com.utils.FileUtils;

import com.client.bean.Visitor;
import com.utils.Constant;
import com.utils.MD5;
import com.utils.SysLog;
import com.web.bean.Person;
import com.web.service.PersonInfoService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DelFileThread implements Runnable {
	@Autowired
	private FileUtils fu;
	
    private List<String> filePaths;
    private PersonInfoService personInfoService;
    private List<Visitor> vList;
    private List<String> rvList;
    
    public DelFileThread() {
    }

    public DelFileThread(List<String> filePaths, PersonInfoService personInfoService) {
        this.filePaths = filePaths;
        this.personInfoService = personInfoService;
    }
    
    public DelFileThread(List<String> filePaths, PersonInfoService personInfoService,List<Visitor> vList) {
        this.filePaths = filePaths;
        this.personInfoService = personInfoService;
        this.vList=vList;
    }
    
    public DelFileThread(List<String> filePaths, List<String> rvList,PersonInfoService personInfoService) {
        this.filePaths = filePaths;
        this.personInfoService = personInfoService;
        this.rvList=rvList;
    }

    @Override
    public void run() {
            List<String> sList=new ArrayList<String>();
            if(null!=filePaths&&filePaths.size()>0) {
	            for(String avatar:filePaths) {
	                try {
	                    if(StringUtils.isBlank(avatar)){
	                        continue;
	                    }
	
	                    // TODO: 2020/4/24 删除文件操作
	                    String fastdfsUrl = Constant.FASTDFS_URL;
	                    if (personInfoService != null) {
	                        //如果有注册信息，就跳出
	                        List<Person> person = personInfoService.getVisitPersonByAvatar(avatar);
	                        if (null != person && person.size() > 0) {
	                            continue;
	                        }
	                        List<Person> invitePersons = personInfoService.getInvitePersonByAvatar(avatar);
	                        if (null != invitePersons && invitePersons.size() > 0) {
	                            continue;
	                        }
	                    }
	                   //String[] url= avatar.substring(fastdfsUrl.length()).split("/", 2);
	                   //String group = url[0];
	                   //String storagePath = url[1];
	                   //fu.delete_file(group, storagePath);
	                    
	                   //minio
	                    String objectName=fastdfsUrl+Constant.BUCKET_NAME+"/";
	                    sList.add(avatar.substring(objectName.length()));
	                	MinioTools.removeObjects(Constant.BUCKET_NAME,sList);
	                 	//minio 
	                	SysLog.info(LocalDateTime.now()+":图片删除完成");
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    SysLog.warn(e);
	                }
	            }
            }
            
//            DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            for(Visitor v:vList) {
//            	  LocalDateTime localDateTime = v.getVisitdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//            	  String filename=localDateTime.format(dateTimeFormatter)+ "_" + MD5.crypt("a3mj4"+v.getVid()) + ".pdf";
//            	  File myPath = new File("/work/signpdf/"+filename);
//            	  if(myPath.exists()) {
//            		  myPath.delete();
//            	  }
//            }
            
              //minio  
              if(null!=vList&&vList.size()>0) {
            	  sList.clear();
            	  DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		          for(Visitor v:vList) {
		        	  if(StringUtils.isNotBlank(v.getSignPdf())) {
		        		  LocalDateTime localDateTime = v.getVisitdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		        		  sList.add("/signpdf/"+localDateTime.format(dateTimeFormatter)+ "_" +v.getSignPdf());
		        	  }
		          }
	              MinioTools.removeObjects(Constant.BUCKET_NAME,sList);
	              SysLog.info(LocalDateTime.now()+":PDF删除完成");
              }
              //minio
              
              if(null!=rvList&&rvList.size()>0) {
            	  sList.clear();
	              MinioTools.removeObjects(Constant.BUCKET_NAME,rvList);
	              SysLog.info(LocalDateTime.now()+":常驻访客附件删除完成");
              }
            
    }
}
