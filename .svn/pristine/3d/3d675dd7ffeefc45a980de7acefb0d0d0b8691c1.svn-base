package com.utils.emailUtils;

import com.client.bean.ExtendVisitor;
import com.client.bean.Visitor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.MD5;
import com.utils.UtilTools;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SendHtmlEmail {
    public boolean send(UserInfo userinfo, Employee emp, Visitor vt, List<ExtendVisitor> evlist) throws JsonParseException, JsonMappingException, IOException {
        JavaMailSenderImpl mailSender = null;
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
        //获取JavaMailSender bean


        String extendfileds = vt.getExtendCol();
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        JsonNode rootNode = mapper.readValue(extendfileds, JsonNode.class);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < evlist.size(); i++) {
            JsonNode jn = rootNode.path(evlist.get(i).getFieldName());
            if (null != jn) {
                map.put(evlist.get(i).getFieldName(), jn.asText());
            }
        }

        Collections.sort(evlist, new Comparator<ExtendVisitor>() {
            public int compare(ExtendVisitor arg0, ExtendVisitor arg1) {
                return arg0.getInputOrder().compareTo(arg1.getInputOrder());
            }
        });

        SimpleDateFormat time = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        StringBuffer content = new StringBuffer();

        String photourl = "https://www.coolvisit.com/assets/img/avatar.jpg";

        if (!"".equals(vt.getVphoto()) && null != vt.getVphoto()) {
            photourl = vt.getVphoto();
        }

        content.append("<TABLE cellSpacing=0 cellPadding=0 width='400px' align=center bgColor=#F5F5F5 border=0>" +
                "<TR><TD><TABLE cellSpacing=0 cellPadding=0 width='400px' align=center bgColor=#F5F5F5 border=0>" +
                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 30px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 0px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=30 width='100%'></TD></TR>" +
                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 30px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 0px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=30 align=center>" +
                "<IMG style='BORDER-TOP: #fff 3px solid; BORDER-RIGHT: #fff 3px solid; BORDER-BOTTOM: #fff 3px solid; BORDER-LEFT: #fff 3px solid; border-radius: 150px' src='" + photourl + "' width=150 height=150></TD></TR></TABLE></TD></TR>" +
                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 30px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 0px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=30 width='100%'></TD></TR></TABLE>" +
                "<TABLE cellSpacing=0 cellPadding=0 width='400px' align=center bgColor=#F5F5F5 border=0><TR>" +
                "<TD><TABLE cellSpacing=0 cellPadding=0 align=center width='100%' border=0><TR>" +
                "<TD height=50></TD></TR>" +
                "<TR><TD align='center'><p><font size='6'><span style='background-color: transparent ;'>" + vt.getVname() + "正在等候见您</span></font></p></TD></TR><TR>" +
                "<TD height=17></TD></TR>" +
                "<TR><TD align='center'><p><font size='5'><span style='background-color: transparent ;'>" + vt.getVname() + "于" + time.format(vt.getVisitdate()) + "到达公司</span></font></p></TD></TR>" +
                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 50px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=50></TD></TR>" +
                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 1px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' bgColor=#e0e0e0 height=1></TD></TR>" +
                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 50px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=50></TD></TR>");

        for (int j = 0; j < evlist.size(); j++) {
            String value = evlist.get(j).getDisplayName();
            if (null != map.get(evlist.get(j).getFieldName()) && !"".equals(map.get(evlist.get(j).getFieldName())) && !"name".equals(evlist.get(j).getFieldName()) && !"empid".equals(evlist.get(j).getFieldName())) {
                if ("phone".equals(value)) {
                    value = value.substring(0, value.indexOf("#"));
                    content.append("<TR><TD style='text-align:center;'><font size='5'><span style='background-color: transparent ;'>Mobile Phone：</span></font>" +
                            "<font size='5'><span style='background-color: transparent ;'>" + map.get(evlist.get(j).getFieldName()) + "</span></font></TD></TR>" +
                            "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 22px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=22></TD></TR>");
                } else {
                    if (value.indexOf("#") != -1) {
                        value = value.substring(0, value.indexOf("#"));
                        content.append("<TR><TD style='text-align:center;'><font size='5'><span style='background-color: transparent ;'>" + value + "：</span></font>" +
                                "<font size='5'><span style='background-color: transparent ;'>" + map.get(evlist.get(j).getFieldName()) + "</span></font></TD></TR>" +
                                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 22px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=22></TD></TR>");
                    } else {
                        content.append("<TR><TD style='text-align:center;'><font size='5'><span style='background-color: transparent ;'>" + value + "：</span></font>" +
                                "<font size='5'><span style='background-color: transparent ;'>" + map.get(evlist.get(j).getFieldName()) + "</span></font></TD></TR>" +
                                "<TR><TD style='FONT-SIZE: 1px; HEIGHT: 22px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' height=22></TD></TR>");
                    }
                }
            }
        }

        content.append("</TABLE></TD></TR></TABLE><TABLE cellSpacing=0 cellPadding=0 width='400px' align=center border=0><TR>" +
                "<TD style='FONT-SIZE: 1px; HEIGHT: 5px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px; PADDING-LEFT: 0px; MARGIN: 0px; LINE-HEIGHT: 1px; PADDING-RIGHT: 0px' bgColor=#F5F5F5 height=5 width='100%'></TD></TR></TABLE>");

//        content.append("<TABLE align=center border=0><TR>" +
//                "<TD align=center><IMG src='https://www.coolvisit.com/assets/img/qrcode_for_gh_c43286c7c025_258.jpg' width=100 height=100></TD></TR></TABLE>");
//        content.append("<TABLE align=center border=0><TR><TD align=center><p>扫描二维码，通知方式随心设</p></TD></TR></TABLE>");
//        if (userinfo.getPreRegisterSwitch() == 1) {
//            content.append("<TABLE width='580px' align=center border=0><TR><TD align='center'><p><a href ='https://www.coolvisit.com/order.html?empid=" + emp.getEmpid() + "&digest=" + MD5.crypt(emp.getEmpEmail()) + "'>30秒填写邀请函，两步发送预约短信</a></p></TD></TR></TABLE>");
//        }


        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");

            messageHelper.setTo(emp.getEmpEmail());//接受者
            messageHelper.setFrom(userinfo.getEmailAccount());//发送者,这里还可以另起Email别名，不用和xml里的username一致
            messageHelper.setSubject(vt.getVname() + "来访提醒！");//主题
            messageHelper.setText(content.toString(), true);//邮件内容

            if (userinfo.getSmtpPort() == 25 && StringUtils.isNotBlank(userinfo.getEmailPwd())) {
                mailSender.send(mailMessage);
            } else {
                Transport.send(mailMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
