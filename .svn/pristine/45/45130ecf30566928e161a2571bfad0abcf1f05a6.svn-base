package com.utils.emailUtils;

import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.UtilTools;
import com.web.bean.Appointment;
import com.web.bean.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class SendInviteEmail {
    private String realpath;

    public String getRealpath() {
        return realpath;
    }

    public void setRealpath(String realpath) {
        this.realpath = realpath;
    }

    public boolean send(UserInfo userinfo, Appointment a) {
        JavaMailSenderImpl mailSender = null;
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();
        SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, this.getRealpath() + "WEB-INF/classes/velocity");

        ve.init(p);
        Template template = ve.getTemplate("invite.vm");
        VelocityContext context = new VelocityContext();
        context.put("name", a.getName());
        context.put("time", time.format(a.getAppointmentDate()));
        context.put("address", a.getAddress());
        context.put("company", userinfo.getCardText());
        context.put("invite_icon", Constant.FASTDFS_URL + "static/img/email/invite_icon.png");
        context.put("date_icon", Constant.FASTDFS_URL + "static/img/email/date_icon.png");
        context.put("location_icon", Constant.FASTDFS_URL + "static/img/email/location_icon.png");
        String url = "";
        if ("面试".equals(a.getVisitType())) {
            url = Constant.FASTDFS_URL + "show.html?id=" + AESUtil.encode(a.getId() + "", Constant.AES_KEY);
        } else if ("商务".equals(a.getVisitType())) {
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(a.getId() + "", Constant.AES_KEY);
        } else {
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(a.getId() + "", Constant.AES_KEY);
        }
        context.put("link", url);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        String result = writer.toString();

        //获取JavaMailSender bean
        MimeMessage mailMessage;
        if (userinfo.getEmailType() == 3) {
            mailMessage = (MimeMessage) UtilTools.getEmailConf("smtp.ym.163.com", "994", "service@coolvisit.com", "ZoneZone8006");
            userinfo.setEmailAccount("service@coolvisit.com");
        } else if (StringUtils.isNotBlank(userinfo.getEmailPwd()) && userinfo.getSmtpPort() != 25) {
            mailMessage = (MimeMessage) UtilTools.getEmailConf(userinfo.getSmtp(), userinfo.getSmtpPort() + "", userinfo.getEmailAccount(), userinfo.getEmailPwd());
        } else if (StringUtils.isNotBlank(userinfo.getEmailPwd()) && userinfo.getSmtpPort() == 25) {
            mailSender = UtilTools.getEmailConf(userinfo.getSmtp(), userinfo.getSmtpPort(), userinfo.getEmailAccount(), userinfo.getEmailPwd());
            mailMessage = mailSender.createMimeMessage();
        } else {
            mailMessage = (MimeMessage) UtilTools.getNoAuthEmailConf(userinfo.getSmtp(), userinfo.getSmtpPort(), userinfo.getEmailAccount());
        }

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
            messageHelper.setTo(a.getVemail());//接受者
            messageHelper.setFrom(userinfo.getEmailAccount());//发送者,这里还可以另起Email别名，不用和xml里的username一致
            messageHelper.setSubject(a.getEmpName() + " invited you to " + userinfo.getCardText());//主题

            messageHelper.setText(result, true);//邮件内容
            if (userinfo.getSmtpPort() == 25 && StringUtils.isNotBlank(userinfo.getEmailPwd())) {
                mailSender.send(mailMessage);
            } else {
                Transport.send(mailMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean sendNoAuth(UserInfo userinfo, Appointment a) {
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();
        SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, this.getRealpath() + "WEB-INF/velocity");

        ve.init(p);
        Template template = ve.getTemplate("invite.vm");
        VelocityContext context = new VelocityContext();
        context.put("name", a.getName());
        context.put("time", time.format(a.getAppointmentDate()));
        context.put("address", a.getAddress());
        context.put("company", userinfo.getCardText());
        String url = "";
        if ("面试".equals(a.getVisitType())) {
            url = Constant.FASTDFS_URL + "show?id=" + AESUtil.encode(a.getId() + "", Constant.AES_KEY);
        } else if ("商务".equals(a.getVisitType())) {
            url = Constant.FASTDFS_URL + "bus?id=" + AESUtil.encode(a.getId() + "", Constant.AES_KEY);
        } else {
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(a.getId() + "", Constant.AES_KEY);
        }
        context.put("link", url);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        String result = writer.toString();

        MimeMessage mailMessage = (MimeMessage) UtilTools.getNoAuthEmailConf("", 25, "");
        //获取JavaMailSender bean
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
            messageHelper.setTo(a.getVemail());//接受者
            messageHelper.setFrom("");//发送者,这里还可以另起Email别名，不用和xml里的username一致
            messageHelper.setSubject(a.getEmpName() + " invited you to " + userinfo.getCardText());//主题
            messageHelper.setText(result, true);//邮件内容
            Transport.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
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
