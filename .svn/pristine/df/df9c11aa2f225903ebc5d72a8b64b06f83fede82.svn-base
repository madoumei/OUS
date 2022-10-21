package com;
import com.config.netty.NettyServerBootstrap;
import com.utils.SysLog;
import com.utils.licenseUtils.LicenseUtil;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.smartboot.license.client.License;
import org.smartboot.license.client.LicenseEntity;
import org.smartboot.license.client.LicenseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, GsonAutoConfiguration.class})
@EnableProcessApplication
@EnableScheduling
@ServletComponentScan()
@EnableAsync
//@MapperScan(basePackages = {"com.web.dao","com.client.dao"})
public class VisitorApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        try {
            SpringApplication.run(VisitorApplication.class, args);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new NettyServerBootstrap(10020);
                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        try {
            //检查license
            LicenseUtil licenseUtil = new LicenseUtil();
            licenseUtil.checkLicenseData();
        }catch (LicenseException e){
            SysLog.error("License error",e);
            return null;
        }
        return application.sources(VisitorApplication.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
