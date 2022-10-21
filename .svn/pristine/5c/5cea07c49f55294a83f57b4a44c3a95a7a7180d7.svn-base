package com.client.service;

import com.web.bean.ResidentVisitor;
import com.web.bean.RespInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author changmeidong
 * @date 2020/2/26 15:48
 * @Version 1.0
 */
public interface FileUploadOrDownLoadServer {

    public Map<String, List<String>> showAllFileByEmpId(ResidentVisitor residentVisitor);

    public Map<String, List<String>> showAllFilePathByEmpId(ResidentVisitor residentVisitor,HttpServletRequest request);

    public boolean validateFileType(String fileName, String FileType);

    public String validateFilePath(String filePath, String subPath);

    public List<Map<String,Object>> UploadEmpIdPhoto(ResidentVisitor residentVisitor, HttpServletRequest request);

    public void downloadAllFile(HttpServletResponse response, String empids);

    void downLoadFile(HttpServletResponse response, ResidentVisitor residentVisitor, String fileNames);

    RespInfo unzip(MultipartFile file, String targetDir);
    
}
