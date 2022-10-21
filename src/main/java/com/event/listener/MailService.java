package com.event.listener;

import com.config.qicool.common.utils.StringUtils;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.utils.msgUtils.MsgTemplateUtils;
import com.web.bean.UserInfo;
import com.web.dao.ConfigureDao;
import com.web.dao.UserDao;
import com.web.service.MsgTemplateService;
import com.web.service.OperateLogService;
import com.web.service.UserService;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

@Component
public class MailService implements SmartApplicationListener {
    private final static String PLATEFORM = "email";

    @Autowired
    protected UserService userService;

    @Autowired
    protected ConfigureDao configureDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected OperateLogService operateLogService;

    @Autowired
    protected MsgTemplateService msgTemplateService;

    public MailService() {
        SysLog.info(this.getClass().getName() + " start");
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == NotifyEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof NotifyEvent) {
            NotifyEvent event = (NotifyEvent) applicationEvent;
            onNotifyEvent(event);
        }

    }

    /**
     * 预约访客事件
     *
     * @param event
     */
    private void onNotifyEvent(NotifyEvent event) {
        UserInfo userInfo = userService.getUserInfo(event.getUserId());
        if (null == userInfo) {
            SysLog.error("NotifyEvent can't find UserInfo id=" + event.getUserId());
            return;
        }
        if (userInfo.getSmsNotify() == 0) {
            return;
        }
        //是否为顺序发送
        if (userInfo.getNotifyType() == 1 && StringUtils.isNotEmpty(event.getSentWay())) {
            return;
        }

        //是否指定发送方式
        if (StringUtils.isNotEmpty(event.getSendWay()) && !event.getSendWay().contains(PLATEFORM)) {
            return;
        }

        com.web.bean.MsgTemplate msg = msgTemplateService.getTemplate(0, PLATEFORM + "_" + event.getEventType());
        if (msg == null || 0 == msg.getStatus()) {
            return;
        }

        String content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());
        if (StringUtils.isBlank(content)) {
            //未设置模板，不发送
            return;
        }
        String sendEmail = new String();
        List<Litigant> receivers = event.getReceivers();
        if (!receivers.isEmpty()) {
            for (Litigant receiver : receivers) {
                sendEmail = receiver.getEmail();
            }
        }
        content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());

        JavaMailSenderImpl mailSender = null;
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();

        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");

        ve.init(p);

        StringWriter writer = new StringWriter();

        String contextText = content;
        //获取JavaMailSender bean
        MimeMessage mailMessage;
        if (userInfo.getEmailType() == 3) {
            mailMessage = (MimeMessage) UtilTools.getEmailConf("smtp.ym.163.com", "994", "service@coolvisit.com", "ZoneZone8006");
            userInfo.setEmailAccount("service@coolvisit.com");
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(userInfo.getEmailPwd()) && userInfo.getSmtpPort() != 25) {
            mailMessage = (MimeMessage) UtilTools.getEmailConf(userInfo.getSmtp(), userInfo.getSmtpPort() + "", userInfo.getEmailAccount(), userInfo.getEmailPwd());
        } else if (org.apache.commons.lang3.StringUtils.isNotBlank(userInfo.getEmailPwd()) && userInfo.getSmtpPort() == 25) {
            mailSender = UtilTools.getEmailConf(userInfo.getSmtp(), userInfo.getSmtpPort(), userInfo.getEmailAccount(), userInfo.getEmailPwd());
            mailMessage = mailSender.createMimeMessage();
        } else {
            mailMessage = (MimeMessage) UtilTools.getNoAuthEmailConf(userInfo.getSmtp(), userInfo.getSmtpPort(), userInfo.getEmailAccount());
        }
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
            if (StringUtils.isNotBlank(sendEmail)) {
                messageHelper.setTo(sendEmail);//接受者
            } else {
                return;
            }

            messageHelper.setFrom(userInfo.getEmailAccount());//发送者,这里还可以另起Email别名，不用和xml里的username一致
            messageHelper.setSubject(msg.getTitle());//主题

            messageHelper.setText(contextText, true);//邮件内容
            if (userInfo.getSmtpPort() == 25 && org.apache.commons.lang3.StringUtils.isNotBlank(userInfo.getEmailPwd())) {
                mailSender.send(mailMessage);
            } else {
                Transport.send(mailMessage);
            }
            event.setSentWay(event.getSentWay() + ",email");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
