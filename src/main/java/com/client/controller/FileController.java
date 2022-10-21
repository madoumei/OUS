package com.client.controller;

import com.client.service.EqptMonitorService;
import com.client.service.FileUploadOrDownLoadServer;
import com.config.exception.ErrorEnum;
import com.utils.Constant;
import com.utils.UploadResp;
import com.utils.UtilTools;
import com.utils.FileUtils.MinioTools;
import com.web.bean.AuthToken;
import com.web.bean.ResidentVisitor;
import com.web.bean.RespInfo;
import com.web.service.ResidentVisitorService;

import com.web.service.TokenServer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "FileController", tags = "API_文件管理", hidden = true)
public class FileController {

    @Autowired
    private FileUploadOrDownLoadServer fileUploadOrDownLoadServer;
    
    @Autowired
    private  EqptMonitorService eqptMonitorService;
    
    @Autowired
	private ResidentVisitorService residentVisitorService;
    
//    @Autowired
//	private FileUtils fu;

    @Autowired
    private TokenServer tokenServer;

//    @ApiOperation("/Upload 图片上传")
//    @RequestMapping(value = "/Upload", method = RequestMethod.POST)
//    @ResponseBody
//    public RespInfo FastdfsUpload(@ApiParam(required = true) @RequestParam(value = "filename") MultipartFile file, HttpServletRequest request) {
//        //检查token
//        try {
//            tokenServer.getAuthTokenByRequest(request);
//        }catch (RuntimeException e){
//            //没有token就检查签名
//            RespInfo respInfo = tokenServer.checkSign(request);
//            if (null != respInfo) {
//                return respInfo;
//            }
//        }
//
//        String name = "test";
//        if (!file.isEmpty()) {
//            name = file.getOriginalFilename().trim();
//            name = name.substring(name.lastIndexOf("/") + 1);
//            String prefix = name.substring(name.lastIndexOf(".") + 1);
//            String suffixList = "jpg,gif,png,ico,bmp,jpeg";
//            String videoList = "mp4,flv,avi,rm,rmvb,wmv,ogv";
//            //获取文件后缀
//
//            if (suffixList.contains(prefix.trim().toLowerCase())) {
//                InputStream inputStream;
//                try {
//                    inputStream = file.getInputStream();
//                    BufferedImage bi = ImageIO.read(inputStream);
//                    if (bi == null) {
//                        return new RespInfo(24, "no image");
//                    }
//                } catch (IOException e1) {
//                    // TODO Auto-generated catch block
//                    return new RespInfo(24, "no image");
//                }
//            } else {
//                if (!videoList.contains(prefix.trim().toLowerCase())) {
//                    return new RespInfo(21, "invalid file type");
//                }
//            }
//
//            if (name == null) {
//                name = "test";
//            }
//
//            try {
//                // String classPath = new
//                // File(postController.class.getResource("/").getFile()).getCanonicalPath();
//                //
//                // String fdfsClientConfigFilePath = classPath + File.separator
//                // + CLIENT_CONFIG_FILE;
//                //
//                // logger.info("Fast DFS configuration file path:" +
//                // fdfsClientConfigFilePath);
//                // ClientGlobal.init(fdfsClientConfigFilePath);
//                String result = fu.uploadFile(file, name, file.getSize());
//                if (result.compareTo("") == 0) {
//                    return new RespInfo(21, "result is empty");
//                } else {
//                    return new RespInfo(0, "success", new UploadResp(Constant.FASTDFS_URL + result));
//                    // uploadFileByStream(file.getInputStream(), name,
//                    // file.getSize());
//                }
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return new RespInfo(21, "fail to upload");
//            }
//
//            /*
//             * try { byte[] bytes = file.getBytes(); BufferedOutputStream stream
//             * = new BufferedOutputStream(new FileOutputStream(new File(name +
//             * "-uploaded"))); stream.write(bytes); stream.close(); return
//             * "You successfully uploaded " + name + " into " + name +
//             * "-uploaded !"; } catch (Exception e) { return
//             * "You failed to upload " + name + " => " + e.getMessage(); }
//             */
//        } else {
//            return new RespInfo(21, "file is empty");
//        }
//    }
    
    @ApiOperation("/Upload  文件上传")
    @RequestMapping(value = "/Upload", method = RequestMethod.POST)
    @ResponseBody
    public RespInfo MinioUpload(@ApiParam(required = true) @RequestParam(value = "filename") MultipartFile file, HttpServletRequest request) {
        //检查token
        try {
            tokenServer.getAuthTokenByRequest(request);
        }catch (RuntimeException e){
            //没有token就检查签名
            RespInfo respInfo = tokenServer.checkSign(request);
            if (null != respInfo) {
                return respInfo;
            }
        }

        String name = "test";
        if (!file.isEmpty()) {
            name = file.getOriginalFilename().trim();
            name = name.substring(name.lastIndexOf("/") + 1);
            String prefix = name.substring(name.lastIndexOf(".") + 1);
            String suffixList = "jpg,gif,png,ico,bmp,jpeg";
            String videoList = "mp4,flv,avi,rm,rmvb,wmv,ogv";
            //获取文件后缀

            if (suffixList.contains(prefix.trim().toLowerCase())) {
                InputStream inputStream;
                try {
                    inputStream = file.getInputStream();
                    BufferedImage bi = ImageIO.read(inputStream);
                    if (bi == null) {
                        return new RespInfo(24, "no image");
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    return new RespInfo(24, "no image");
                }
            } else {
                if (!videoList.contains(prefix.trim().toLowerCase())) {
                    return new RespInfo(21, "invalid file type");
                }
            }

            try {
    	      String fileName=UtilTools.produceId(8)+System.currentTimeMillis()+"."+prefix;
      	      String result=MinioTools.uploadFile(file,Constant.BUCKET_NAME,"/pic/"+YearMonth.now()+"/"+fileName);
      	      // 使用putObject上传一个文件到存储桶中。
      	      return new RespInfo(0, "success", new UploadResp(result));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new RespInfo(21, "fail to upload");
            }

            /*
             * try { byte[] bytes = file.getBytes(); BufferedOutputStream stream
             * = new BufferedOutputStream(new FileOutputStream(new File(name +
             * "-uploaded"))); stream.write(bytes); stream.close(); return
             * "You successfully uploaded " + name + " into " + name +
             * "-uploaded !"; } catch (Exception e) { return
             * "You failed to upload " + name + " => " + e.getMessage(); }
             */
        } else {
            return new RespInfo(21, "file is empty");
        }
    }

    /**
     * 劳务公司上传劳务照片
     * 头像类型：常驻访客？员工
     */
    /**
     * @param zipFile     上传的文件
     * @param visitorType 访客类型：visitorType
     *                    1：员工；
     *                    2：常驻访客；
     * @return
     */
    @ApiOperation(value = "/UploadLaborFile 劳务公司上传劳务照片", httpMethod = "POST")
    @RequestMapping(value = "/UploadLaborFile", method = RequestMethod.POST)
    @ResponseBody
    public RespInfo UploadLaborFile(@RequestParam(value = "zipFile") MultipartFile zipFile,
                                    @RequestParam(value = "visitorType") Integer visitorType,
                                    HttpServletRequest request) {
        RespInfo respInfo = null;
        try {
            request.setCharacterEncoding("UTF-8");
            int type = visitorType.intValue();
            if (zipFile.isEmpty()) {
                return new RespInfo(620, "Empty File upload");
            }
            if (!zipFile.getOriginalFilename().endsWith(".zip")) {
                return new RespInfo(619, "File upload format error");
            }
            if (type != 1 && type != 2) {
                return new RespInfo(618, "Visitor type exception");
            }
            String path = "/opt/facePicture";
            StringBuffer stringBuffer = new StringBuffer(path);
            if (1 == type) {
                stringBuffer.append("/emp");
            }
            if (2 == type) {
                stringBuffer.append("/visitor");
            }
            String targetDir = stringBuffer.toString();
            long startTime = System.currentTimeMillis();
            respInfo = fileUploadOrDownLoadServer.unzip(zipFile, targetDir);
            long endTime = System.currentTimeMillis();
            System.out.println("解压缩共计耗时：" + (endTime - startTime) + "毫秒");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return respInfo;
    }
    
    @RequestMapping(value = "/UploadLog", method = RequestMethod.POST)
    @ResponseBody
    public  RespInfo UploadLog(
            @RequestParam(value = "filename") MultipartFile file, @RequestParam(value = "uuid") String uuid,
            HttpServletRequest request) {
    		if (!file.isEmpty()) {
            try {
                String path="/work/clientLog";
                File fileLog=new File(path);
        		if(!fileLog.exists()){//如果文件夹不存在
        			fileLog.mkdir();//创建文件夹
        		}

        		File log = new File(path+"/"+uuid+".log");
        		file.transferTo(log);
        		
                eqptMonitorService.updateLogDate(uuid);
        		
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new RespInfo(21, "fail to upload");
            }
        } else {
            return new RespInfo(21, "fail to upload");
        }
    		 return new RespInfo(0, "success");
    }
    
    /**
     * 常驻访客多文件上传
     *
     * @param rid
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/UploadResidentFile")
    @ResponseBody
    public RespInfo UploadResidentFile(@RequestParam(value = "rid") String rid, HttpServletRequest request) {

        ResidentVisitor residentVisitor = new ResidentVisitor();
        residentVisitor.setRid(rid.substring(1));
        ResidentVisitor rv = residentVisitorService.getResidentVisitorByRid(residentVisitor);
        if (null != rv) {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                    && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                    ||authToken.getUserid() != rv.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }

            List<Map<String, Object>> maps = fileUploadOrDownLoadServer.UploadEmpIdPhoto(rv, request);
            if (null != maps) {
                return new RespInfo(0, "success", maps);
            } else {
                return new RespInfo(ErrorEnum.E_620.getCode(), ErrorEnum.E_620.getMsg());
            }
        }
        return new RespInfo(622, "No Resident Info", rid);
    }
    


}
