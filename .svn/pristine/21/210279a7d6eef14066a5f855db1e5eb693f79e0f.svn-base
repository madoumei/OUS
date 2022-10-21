package com.utils.FileUtils;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Component
public class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class);
    
    @Autowired
    private FastFileStorageClient storageClient;
    
    //private static final String FILE_SERVER = "http://139.196.213.49/";
    //private static final String FILE_SERVER = "http://www.coolvisit.com/";

    /**
     * Upload File to DFS, directly transferring java.io.InputStream to
     * java.io.OutStream
     *
     * @param inStream       , file to be uploaded.
     * @param uploadFileName , the name of the file.
     * @param fileLength     , the length of the file.
     * @return the file ID in DFS.
     * @throws IOException
     * @author Poechant
     * @email zhongchao.ustc@gmail.com
     */
    public String uploadFileByStream(InputStream inStream, String uploadFileName, long fileLength) throws IOException {
        StorePath results = null;
        String fileExtName = "";
        if (uploadFileName.contains(".")) {
            fileExtName = uploadFileName.substring(uploadFileName
                    .lastIndexOf(".") + 1);
        } else {
            logger.warn("Fail to upload file, because the format of filename is illegal.");
            return null;
        }

        try {
            // results[0]: groupName, results[1]: remoteFilename.
        	results = storageClient.uploadFile(inStream, fileLength, fileExtName, null);
        } catch (Exception e) {
            logger.warn("Upload file \"" + uploadFileName + "\"fails");
        }finally
        {
            try
            {
                if(inStream != null)
                {
                    // 关闭流资源
                	inStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return results.getFullPath();
    }

    /**
     * Upload File to DFS.
     *
     * @param file           , file to be uploaded.
     * @param uploadFileName , the name of the file.
     * @param fileLength     , the length of the file.
     * @return the file ID in DFS.
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String uploadFileName,long fileLength) throws IOException {
    	InputStream fileBuff = file.getInputStream();
        return this.uploadFile(fileBuff, uploadFileName,fileLength);
    }

    public  String uploadFile(InputStream fileBuff, String uploadFileName,long fileLength) throws IOException {
        StorePath fileId = null;
        String result="";
        String fileExtName = "";

        if (uploadFileName.contains(".")) {
            fileExtName = uploadFileName.substring(uploadFileName
                    .lastIndexOf(".") + 1);
        } else {
            logger.warn("Fail to upload file, because the format of filename is illegal.");
            return null;
        }

        // 建立连接
//        TrackerClient tracker = new TrackerClient();
//        TrackerServer trackerServer = tracker.getConnection();
//        StorageServer storageServer = null;
//        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
//
//        // 设置元信�?
//        NameValuePair[] metaList = new NameValuePair[3];
//        metaList[0] = new NameValuePair("fileName", uploadFileName);
//        metaList[1] = new NameValuePair("fileExtName", fileExtName);
//        metaList[2] = new NameValuePair("fileLength",
//                String.valueOf(fileLength));

        // 上传文件
        try {
            fileId = storageClient.uploadFile(fileBuff, fileLength,fileExtName ,null);
            if(null!=fileId) {
            	result=fileId.getFullPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Upload file \"" + uploadFileName + "\"fails");
            return null;
        }
        return result;
    }

    /**
     * 删除文件
     * @param group 组名 如：group1
     * @param storagePath 不带组名的路径名称 如：M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return -1失败,0成功
     */
    public void delete_file(String group ,String storagePath) throws IOException{
        if(StringUtils.isNotBlank(group)&&StringUtils.isNotBlank(storagePath)){
//            TrackerClient tracker = new TrackerClient();
//            TrackerServer trackerServer = tracker.getConnection();
//            	StorageServer storageServer = null;
//            	StorageClient1 client = new StorageClient1(trackerServer, storageServer);
               storageClient.deleteFile(group, storagePath);

        }
    }

}
