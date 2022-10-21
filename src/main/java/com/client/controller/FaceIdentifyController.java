package com.client.controller;

import com.client.bean.RequestFI;
import com.config.activemq.MessageSender;
import com.utils.Constant;
import com.utils.imageUtils.ImageChange;
import com.utils.imageUtils.ImgCompress;
import com.web.bean.*;
import com.web.service.EmployeeService;
import com.web.service.PersonInfoService;
import com.web.service.TokenServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "FaceIdentifyController", tags = "API_人脸管理", hidden = true)
public class FaceIdentifyController {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private TokenServer tokenServer;

    private String photoUrl= Constant.FASTDFS_URL;

    @ApiOperation(value = "/uploadFace 上传人脸", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"photoUrl\":\"https://.............\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/uploadFace")
    @ResponseBody
    public RespInfo uploadFace(@RequestBody RequestFI rfi, HttpServletRequest request) {

        //检查token
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        }catch (RuntimeException e){
            //没有token就检查签名
            RespInfo respInfo = tokenServer.checkSign(request);
            if (null != respInfo) {
                return respInfo;
            }
        }

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(rfi.getPhotoUrl(), "faceScan", "-1");
        redisTemplate.expire(rfi.getPhotoUrl(), 5, TimeUnit.MINUTES);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "avatar_uploaded");
        map.put("image_url", rfi.getPhotoUrl());
        messageSender.updateFaceLib(map);

        return new RespInfo(0, "success");
    }


    @ApiOperation(value = "/getFaceStatus 获取上传人脸状态", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"photoUrl\":\"https://.............\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getFaceStatus")
    @ResponseBody
    public RespInfo getFaceStatus(@RequestBody RequestFI rfi, HttpServletRequest request) {

        //检查token
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        }catch (RuntimeException e){
            //没有token就检查签名
            RespInfo respInfo = tokenServer.checkSign(request);
            if (null != respInfo) {
                return respInfo;
            }
        }

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String a = "-1";
        if (hashOperations.hasKey(rfi.getPhotoUrl(), "faceScan")) {
            a = (String) hashOperations.get(rfi.getPhotoUrl(), "faceScan");
        }

        if (!"-1".equals(a)) {
            hashOperations.delete(rfi.getPhotoUrl(), "faceScan");
        }
        return new RespInfo(0, "success", a);
    }

    @ApiOperation(value = "/updateAllFace 下发全部人脸", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"userid\":\"123456\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateAllFace")
    @ResponseBody
    public RespInfo updateAllFace(
            @ApiParam(value = "RequestEmp 请求员工Bean", required = true) @Validated @RequestBody RequestEmp rep,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        List<Employee> emplist = employeeService.getOldEmployeeList(rep.getUserid());

        for (int i = 0; i < emplist.size(); i++) {
            Employee emp = emplist.get(i);
            if (null == emp.getOpenid() || "".equals(emp.getOpenid()) || null == emp.getAvatar() || "".equals(emp.getAvatar())) {
                continue;
            }

            if (!"".equals(emp.getOpenid()) && null != emp.getOpenid()) {
                Person p = new Person();
                p.setPmobile(emp.getEmpPhone());
                p.setAvatar(emp.getAvatar());
                personInfoService.updateInviteAvatar(p);
                employeeService.updateEmpAvatar(emp);
            }

        }

        Map<String, Object> empmap = new HashMap<String, Object>();
        empmap.put("key", "employee_batch");
        empmap.put("company_id", rep.getUserid());
        messageSender.updateFaceLib(empmap);

        return new RespInfo(0, "success");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/Rotateface")
    @ResponseBody
    public RespInfo Rotateface(@RequestBody  RequestFI rfi) {
        String imagePath=rfi.getPhotoUrl().replace(photoUrl+"group1/M00", "/qicool/data");
        //人脸检测调用示例
        try {

            File file1=new File(imagePath);             //用file1取得图片名字
            BufferedImage bi=null;
            Image image = ImageIO.read(file1);
            ImgCompress imgCom = new ImgCompress(image);
            bi=imgCom.resizeFix(250,250);
            bi= ImageChange.rotate90SX(bi);
            ImageIO.write(bi, "jpg", new File(imagePath));

            //get respose
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new RespInfo(0, "success");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/Compressface")
    @ResponseBody
    public RespInfo Compressface(@RequestBody  RequestFI rfi) {
        String imagePath=rfi.getPhotoUrl().replace(photoUrl+"group1/M00", "/qicool/data");
        //人脸检测调用示例
        try {

            File file1=new File(imagePath);             //用file1取得图片名字
            BufferedImage bi=null;
            Image image = ImageIO.read(file1);
            ImgCompress imgCom = new ImgCompress(image);
            bi=imgCom.resizeFix(250,250);
            ImageIO.write(bi, "jpg", new File(imagePath));
            //get respose
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new RespInfo(0, "success");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/uploadFaceStatus")
    @ResponseBody
    public RespInfo uploadFaceStatus(@RequestBody RequestFI rfi){
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(rfi.getPhotoUrl(),"faceScan",rfi.getResult());

        return new RespInfo(0,"success");
    }
}
