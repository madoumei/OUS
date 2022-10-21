package com.client.service.Impl;

import com.client.service.FileUploadOrDownLoadServer;
import com.utils.Constant;
import com.utils.UtilTools;
import com.utils.FileUtils.MinioTools;
import com.web.bean.ResidentVisitor;
import com.web.bean.RespInfo;
import com.web.service.ResidentVisitorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * @author changmeidong
 * @date 2020/2/26 15:54
 * @Version 1.0
 */
@Service
public class FileUploadOrDownLoadServerImpl implements FileUploadOrDownLoadServer {

    public static final String rootPath = "/qicool/residentVisitor_file";

    @Autowired
    private ResidentVisitorService residentVisitorService;

    /**
     * 根据文件名称下载用户文件
     *
     * @param response
     * @param residentVisitor
     * @param fileNames
     */
    @Override
    public void downLoadFile(HttpServletResponse response, ResidentVisitor residentVisitor, String fileNames) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String downloadName = dateFormat.format(new Date()) + "附件.zip";
        try {
            response.setContentType("multipart/from-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(downloadName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("下载文件时编码出现错误");
            e.printStackTrace();
        }
        ZipOutputStream zops = null;
        OutputStream ops = null;
        try {
            ops = response.getOutputStream();
            zops = new ZipOutputStream(ops, Charset.forName("UTF-8"));
            //执行写入流程
            //downloadTolocal(zops, residentVisitor, fileNames);
            downloadTolocalFromMinio(zops, residentVisitor, fileNames);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流流程
            try {
                zops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载用户的指定的文件
     *
     * @param zops
     * @param residentVisitor
     * @param fileNames
     */
    private void downloadTolocal(ZipOutputStream zops, ResidentVisitor residentVisitor, String fileNames) throws IOException {
        //获取该用户所有的文件绝对路径
        File file = new File(rootPath + "/" + residentVisitor.getRid().substring(1));
        String[] downLoadFileNames = fileNames.split(",");
        FileInputStream fis = null;
        byte[] buf = new byte[1024 * 2];
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                //获取所有需要下载的文件名称
                for (String downLoadFileName : downLoadFileNames) {
                    if (listFile.getName().equals(downLoadFileName)) {
                        //执行下载的操作逻辑
                        fis = new FileInputStream(listFile);
                        zops.putNextEntry(new ZipEntry(residentVisitor.getName() + "/" + downLoadFileName));
                        //写文件
                        int len;
                        while ((len = fis.read(buf)) != -1) {
                            zops.write(buf, 0, len);
                        }
                        zops.closeEntry();
                    }
                }
            }
            if (null != fis) {
                fis.close();
            }
        }
    }
    
    //minio
    private void downloadTolocalFromMinio(ZipOutputStream zos,  ResidentVisitor residentVisitor, String fileNames) throws IOException {
    	InputStream in = null;
    	byte[] buf = new byte[1024 * 2];
        ResidentVisitor rv = new ResidentVisitor();
        String[] downLoadFileNames = fileNames.split(",");
            List<String> filenames =MinioTools.getObjectList(Constant.BUCKET_NAME, "/residentVisitor/"+ residentVisitor.getRid().substring(1));
            for (String name : filenames) {
            			String objectName=name.substring(name.lastIndexOf("/")+1);
            			if(Arrays.asList(downLoadFileNames).contains(objectName)) {
	                        in = MinioTools.getObject(Constant.BUCKET_NAME, name);
	                        // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
	                        zos.putNextEntry(new ZipEntry(residentVisitor.getName() + objectName));
	                        // copy文件到zip输出流中
	                        int len;
	                        while ((len = in.read(buf)) != -1) {
	                            zos.write(buf, 0, len);
	                        }
	                        zos.closeEntry();
	                        in.close();
            			}
            }
    }

    /**
     * 展示指定用户文件夹下的所有文件名称
     *
     * @param v
     * @return
     */
    @Override
    public Map<String, List<String>> showAllFileByEmpId(ResidentVisitor v) {
        String dirName = rootPath + "/"+ v.getRid().substring(1);
        File deskFile = new File(dirName);
        String[] fileNames = deskFile.list();
        Map<String, List<String>> result = new HashMap<>();
        if (null != fileNames && fileNames.length > 0) {
            List<String> fileNameList = new ArrayList();
            for (String fileName : fileNames) {
                fileNameList.add(Constant.FASTDFS_URL + "residentVisitor/" + v.getRid().substring(1) + "/" +fileName);
            }
            result.put(v.getName(), fileNameList);
        }
        return result;
    }

    /**
     * 展示指定用户文件夹下的所有文件详细名称
     *
     * @param v
     * @return
     */
    @Override
    public Map<String, List<String>> showAllFilePathByEmpId(ResidentVisitor v, HttpServletRequest request) {
        Map<String, List<String>> result = new HashMap<>();
        String dirName = rootPath + "/" + v.getRid().substring(1);
        File deskFile = new File(dirName);
        if (deskFile.exists()) {
            File[] files = deskFile.listFiles();
            List<String> fileNameList = new ArrayList();
            for (File file : files) {
                fileNameList.add(Constant.FASTDFS_URL + "residentVisitor/" + v.getRid().substring(1) + "/" + file.getName());
            }
            result.put(v.getName(), fileNameList);
        }
        return result;
    }

    /**
     * 验证上传的文件类型 是否在指定的文件类型列表中
     *
     * @param fileName 传入的文件名称
     * @param FileType 指定文件类型
     * @return
     */
    @Override
    public boolean validateFileType(String fileName, String FileType) {
        //创建上传文件类型
        HashMap<String, String> extMap = new HashMap<>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar,gz,bz2,pdf");

        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (FileType != null) {
            if (extMap.containsKey(FileType)) {
                String extType = extMap.get(FileType);
                String extTypes[] = extType.split(",");
                return Arrays.asList(extTypes).contains(fileType);
            } else {
                return false;
            }
        } else {
            String extType = "";
            for (String value : extMap.values()) {
                extType = extType + "," + value;
            }
            String extTypes[] = extType.split(",");
            return Arrays.asList(extTypes).contains(fileType);
        }
    }

    /**
     * 验证文件路径是否存在,不存在则创建
     *
     * @param rootPath
     * @return
     */
    @Override
    public String validateFilePath(String rootPath, String subPath) {
        File file = new File(rootPath + "/" + subPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.toString();
    }

    /**
     * 根据员工姓名上传证书
     *
     * @param residentVisitor
     * @param request
     * @return
     */
    @Override
    public List<Map<String, Object>> UploadEmpIdPhoto(ResidentVisitor residentVisitor, HttpServletRequest request) {
        List<Map<String, Object>> fileList = new ArrayList();
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
        Iterator<String> iterator = fileMap.keySet().iterator();
        Iterator<String> it = fileMap.keySet().iterator();

        //验证文件类型
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            MultipartFile multipartFile = (MultipartFile) fileMap.get(key);
            String fileRealName = multipartFile.getOriginalFilename();// 原始名称
            // 验证上传文件类型
            if (!validateFileType(fileRealName, null)) {
                return null;
            }
        }

        //验证文件的路径
      //  String destFilePath = validateFilePath(rootPath, residentVisitor.getName() + "_" + residentVisitor.getRid().substring(1));

        //上传操作
        while (it.hasNext()) {
            String key = (String) it.next();
            MultipartFile multipartFile = (MultipartFile) fileMap.get(key);
            String fileRealName = multipartFile.getOriginalFilename();// 原始名称
            String[] str = fileRealName.split("\\.");
            //minio
            String fileName=UtilTools.produceId(8)+System.currentTimeMillis()+"."+str[str.length - 1];
    	    String result=MinioTools.uploadFile(multipartFile,Constant.BUCKET_NAME,
    	    		"/residentVisitor/" + residentVisitor.getRid().substring(1)+"/"+ fileName);
    	    //minio
    	    
    	    
//            File uploadedFile = new File(destFilePath, str[0] + "." + str[str.length - 1]);
//            try {
//                multipartFile.transferTo(uploadedFile);
                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("filePath", Constant.FASTDFS_URL +"images/"+residentVisitor.getName() + "_" + residentVisitor.getRid().substring(1)+"/"+ uploadedFile.getName());
                map.put("filePath",result);
                fileList.add(map);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return fileList;
    }

    /**
     * 下载指定员工id的所有文件
     *
     * @param response
     * @param rids
     */
    @Override
    public void downloadAllFile(HttpServletResponse response, String rids) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String downloadName = dateFormat.format(new Date()) + "附件.zip";
        try {
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(downloadName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("下载文件名编码时出现错误:");
            e.printStackTrace();
        }
        OutputStream outputStream = null;
        ZipOutputStream zos = null;

        try {
            outputStream = response.getOutputStream();
            zos = new ZipOutputStream(outputStream, Charset.forName("UTF-8"));
            // 将文件流写入zip中
          //  downloadTolocal(zos, rids);
            downloadTolocalFromMinio(zos, rids);
            zos.finish();
        } catch (IOException e) {
            System.out.println("downloadAllFile下载全部附件失败");
            e.printStackTrace();
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (Exception e2) {
                    System.out.println("关闭输入流时出现错误");
                    e2.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e2) {
                    System.out.println("关闭输入流时出现错误");
                    e2.printStackTrace();
                }
            }
        }
    }

    private void downloadTolocal(ZipOutputStream zos, String rids) throws IOException {
        //获取目标文件夹下的所有子文件
        Map<String, List<File>> files = getDirFileNames(rootPath, rids.split(","));
        Set<String> empName = files.keySet();
        FileInputStream in = null;
        byte[] buf = new byte[1024 * 2];
        for (String name : empName) {
            //下载文件
            List<File> filePaths = files.get(name);
            for (File file : filePaths) {
                if (file.isFile()) {
                    in = new FileInputStream(file);
                    // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                    zos.putNextEntry(new ZipEntry(name + "/" + file.getName()));
                    // copy文件到zip输出流中
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    zos.closeEntry();
                }
            }
            in.close();
        }

    }
    
    //minio
    private void downloadTolocalFromMinio(ZipOutputStream zos, String rids) throws IOException {
    	InputStream in = null;
    	byte[] buf = new byte[1024 * 2];
        String[] strRids=rids.split(",");
        ResidentVisitor rv = new ResidentVisitor();
        for (String rid : strRids) {
            rv.setRid(rid.substring(1));
            ResidentVisitor residentVisitor = residentVisitorService.getResidentVisitorByRid(rv);
            List<String> filenames =MinioTools.getObjectList(Constant.BUCKET_NAME, "/residentVisitor/"+residentVisitor.getRid().substring(1));
            for (String name : filenames) {
                //下载文件
                        in = MinioTools.getObject(Constant.BUCKET_NAME, name);
                        // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                        String objectName=name.substring(name.lastIndexOf("/"));
                        zos.putNextEntry(new ZipEntry(residentVisitor.getName() +  objectName));
                        // copy文件到zip输出流中
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            zos.write(buf, 0, len);
                        }
                        zos.closeEntry();
                        in.close();
            }
        }
    }

    /**
     * 获取文件夹下所有文件绝对路径
     *
     * @param rootPath
     * @param rids
     * @return
     */
    private Map<String, List<File>> getDirFileNames(String rootPath, String[] rids) {
        Map<String, List<File>> fileNames = new HashMap<>();
        ResidentVisitor rv = new ResidentVisitor();
        for (String rid : rids) {
            rv.setRid(rid.substring(1));
            ResidentVisitor residentVisitor = residentVisitorService.getResidentVisitorByRid(rv);
            File destFile = new File(rootPath + "/" + residentVisitor.getRid().substring(1));
            File[] files = destFile.listFiles();
            List<File> list = new ArrayList<>();
            for (File fileName : files) {
                list.add(fileName);
            }
            fileNames.put(residentVisitor.getName(), list);
        }
        return fileNames;
    }

    /**
     * 上传的文件进行解压缩
     *
     * @param multipartFile 上传的文件对象
     * @param destDirPath   解压的目标文件夹
     */
    @Override
    public RespInfo unzip(MultipartFile multipartFile, String destDirPath) {
        //判断目标文件是否存在
        File destDirPathFile = new File(destDirPath);
        if (!destDirPathFile.exists()) {
            //不存在就创建
            destDirPathFile.mkdirs();
        }
        ZipInputStream zipInputStream = null;
        FileOutputStream fos = null;
        InputStream multipartFileInputStream = null;
        try {
            //被解压缩文件
            multipartFileInputStream = multipartFile.getInputStream();
            zipInputStream = new ZipInputStream(multipartFileInputStream, Charset.forName("GBK"));
            ZipEntry zipEntry = null;
            boolean macos=false;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            	if (zipEntry.isDirectory()&&zipEntry.getName().startsWith("__MACOSX/")) {
            		 multipartFileInputStream = multipartFile.getInputStream();
            		 zipInputStream = new ZipInputStream(multipartFileInputStream, Charset.forName("utf-8"));
            		 macos=true;
            		 break;
            	}
            }
            
            if(!macos) {
	            multipartFileInputStream = multipartFile.getInputStream();
	            zipInputStream = new ZipInputStream(multipartFileInputStream, Charset.forName("GBK"));
            }
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                // 如果不是文件夹，就在服务器创建个文件夹
                if (!zipEntry.isDirectory()  && !zipEntry.getName().startsWith("__MACOSX/")) {
                    String FilePath = null;
                    File targetFile =null;
                    if (zipEntry.getName().contains("/")) {
                        FilePath = zipEntry.getName().split("/")[1];
                        targetFile=new File(destDirPath + "/" + FilePath);
                    }else {
                        targetFile = new File(destDirPath+ "/"+zipEntry.getName());
                    }

                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    // 将压缩文件内容写入到这个文件中
                    fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024 * 1024 * 2];
                    while ((len = zipInputStream.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    fos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new RespInfo(617, "Unzip failed");
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != multipartFileInputStream) {
                try {
                    multipartFileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != zipInputStream) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new RespInfo(0, "success");
    }
    
}
