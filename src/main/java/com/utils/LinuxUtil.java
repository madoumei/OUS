package com.utils;

import com.config.qicool.common.utils.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class LinuxUtil {
    public static List<Desk> getDeskUsage() {
        List<Desk> desks = new ArrayList<>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("df -hl");// df -hl 查看硬盘空间
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String str = null;
                String[] strArray = null;
                int line = 0;
                while ((str = in.readLine()) != null) {
                    line++;
                    if (line == 1) {
                        continue;
                    }
                    Desk desk = new Desk();
                    int m = 0;
                    strArray = str.split(" ");
                    for (String para : strArray) {
                        if (para.trim().length() == 0)
                            continue;
                        ++m;

                        if(m == 1){
                            if(!para.startsWith("/")){
                                break;
                            }
                            desk.setFilesystem(para);
                            continue;
                        }
                        else if (para.endsWith("G") || para.endsWith("Gi")) {
                            // 目前的服务器
                            if (m == 2) {
                                String totle = para.replace("G","");
                                totle = totle.replace("i","");
                                if(Float.parseFloat(totle)<30){
                                    //太小的磁盘忽略
                                    break;
                                }
                                desk.setTotal(para);
                            }
                            if (m == 3) {
                                desk.setUsed(para);
                            }
                        }
                        else if (para.endsWith("%")) {
                            if (m == 5) {
                                desk.setUse_rate(para);
                            }
                        }
                    }

                    if(StringUtils.isNotEmpty(desk.getUse_rate())) {
                        desks.add(desk);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desks;
    }

    public static String checkUrl(String url) {
        String result="";
        try {
            URL httpUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) httpUrl.openConnection();
            connection.connect();
            for (Certificate certificate : connection.getServerCertificates()) {
                //第一个就是服务器本身证书，后续的是证书链上的其他证书
                X509Certificate x509Certificate = (X509Certificate) certificate;
//                System.out.println(x509Certificate.getSubjectDN());
//                System.out.println(x509Certificate.getNotBefore());//有效期开始时间
//                System.out.println(x509Certificate.getNotAfter());//有效期结束时间
                result = x509Certificate.getNotAfter().getTime()+"";
                break;
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String execCmdParts(String url) {
        String[] cmdParts1 = {"curl", "-vvI", url};

        ProcessBuilder process = new ProcessBuilder(cmdParts1);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            return builder.toString();
        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }

        return null;
    }


    public static class Desk {
        private String filesystem;
        private String total;
        private String used;
        private String use_rate;

        public String toString(){
            return "磁盘名称："+filesystem+"，总磁盘空间："+total+"，已使用："+used+"，使用率达："+use_rate;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getUsed() {
            return used;
        }

        public void setUsed(String used) {
            this.used = used;
        }

        public String getUse_rate() {
            return use_rate;
        }

        public void setUse_rate(String use_rate) {
            this.use_rate = use_rate;
        }

        public String getFilesystem() {
            return filesystem;
        }

        public void setFilesystem(String filesystem) {
            this.filesystem = filesystem;
        }
    }
}
