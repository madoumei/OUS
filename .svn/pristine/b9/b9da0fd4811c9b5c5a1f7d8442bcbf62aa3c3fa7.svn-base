package com.utils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.client.bean.Visitor;
import com.utils.ExceptionUtils.RequestUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 日志记录类
 * @author panteng
 * @version V0.0.1
 * @date 2016-09-08
 */
public class SysLog {
    public static Logger logDefault = Logger.getLogger(SysLog.class);

    public final static Logger HTTP = Logger.getLogger("mylogger1");
    public final static Logger logger2 = Logger.getLogger("mylogger2");

    /**
     * 打印警告
     *
     * @param obj
     */
    public static void warn(Object obj,Logger log) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                log.warn(location + str);
            } else {
                log.warn(location + obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void warn(Object obj) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                logDefault.warn(location + str);
            } else {
                logDefault.warn(location + obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 打印信息
     *
     * @param obj
     */
    public static void info(Object obj,Logger log) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                log.info(location + str);
            } else {
                log.info(location + obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void info(Object msg,Object obj) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                logDefault.info(location + str);
            } else {
                logDefault.info(location +msg.toString()+" "+obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void info(Object obj) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                logDefault.info(location + str);
            } else {
                logDefault.info(location + obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 打印错误
     *
     * @param obj
     */
    public static void error(Object obj,Logger log) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";

            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                log.error(location + str);
            } else {
                log.error(location + obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void error(Object obj) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                logDefault.error("[ERROR]"+location + str);
            } else {
                logDefault.error("[ERROR]"+location + obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void error(Object msg,Object obj) {
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                logDefault.error("[ERROR]"+location + msg.toString()+" "+str);
            } else {
                logDefault.error("[ERROR]"+location + msg.toString()+" "+obj.toString());
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 向数据库告警表中插入信息
     * @param obj
     */
    public static void dbWarn(Object obj,Logger log) {
        try{
            String printInfo = "";
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";

            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                printInfo = location + e.getMessage();
                log.fatal(printInfo.substring(0, printInfo.length() > 512?512:printInfo.length()));
            } else {
                printInfo = location + obj.toString();
                log.fatal(printInfo.substring(0, printInfo.length() > 512?512:printInfo.length()));
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void dbWarn(Object obj) {
        try{
            String printInfo = "";
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";

            /*** 是否是异常  ***/
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                printInfo = location + e.getMessage();
                logDefault.fatal(printInfo.substring(0, printInfo.length() > 512?512:printInfo.length()));
            } else {
                printInfo = location + obj.toString();
                logDefault.fatal(printInfo.substring(0, printInfo.length() > 512?512:printInfo.length()));
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 获取调用此函数的代码的位置
     * @return 包名.类名.方法名(行数)
     */
    public static String getCodeLocation(){
        try{
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";
            return location;
        }catch (Exception e) {
            // TODO: handle exception
            SysLog.error(e);
            return "";
        }
    }

    public static void exceptionLogs(Exception e, HttpServletRequest request) {
        try{
            String printInfo = "";
            /*** 获取输出信息的代码的位置 ***/
            String location = "";
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            location = stacks[2].getClassName() + "." + stacks[2].getMethodName()
                    + "(" + stacks[2].getLineNumber() + ")";

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logDefault.error("[ERROR]"+location+request.getRequestURI()+"\n"+RequestUtils.getRequestJsonString(request)+"\n"+str);
//            logDefault.error(location+request.getRequestURI()+"\n"+str);

        } catch (Exception ex) {

        }
    }

    public static void readTxtFile(String filePath) {
        File file = new File(filePath);
        String encoding = "utf-8";
        try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
             BufferedReader bufferedReader = new BufferedReader(read)) {
            //判断文件是否存在
            if (file.isFile() && file.exists()) {
                String lineTxt;
                Map<String,String> map = new HashMap<>();
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if(lineTxt.startsWith("result json:")){
                        String jsonStr = lineTxt.substring("result json: ".length());
                        JSONObject jb = JSONObject.parseObject(jsonStr);
                        try {
                            Visitor visitor = new Visitor();
                            if (jb.getString("content").contains("完成来访登记")) {
                                Date date = new Date();
                                date.setTime(jb.getLong("requestTime"));
                                if(jb.getLong("requestTime")<1640966400000l){
                                    continue;
                                }
                                visitor.setAppointmentDate(date);
                                visitor.setVisitdate(date);
                                String content = jb.getString("content");
                                content = content.substring("【丹纳赫】".length());
                                if (content.contains("（")) {
                                    visitor.setVname(content.substring(0, content.indexOf("（")));
                                    visitor.setVcompany(content.substring(content.indexOf("（")+1, content.indexOf("）")));
                                } else {
                                    visitor.setVname(content.substring(0, content.indexOf("已在公司")));
                                }
                                visitor.setVphone(map.get(visitor.getVname()));
                                visitor.setEmpPhone(jb.getString("mobile"));
                                SysLog.info(visitor.getAppointmentDate()+" "+visitor.getVname()+" "+visitor.getVphone()+" "+visitor.getVcompany()+" "+visitor.getEmpPhone()+" ");
                            } else if (jb.getString("content").contains("接受您的拜访预约")) {
                                String content = jb.getString("content");
                                content = content.substring("【丹纳赫】".length());
                                String name = content.substring(0, content.indexOf("您好"));
                                map.put(name,jb.getString("mobile"));
                            }
                        }catch (Exception e){

                        }
                    }
//                    System.out.println(lineTxt);
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
        }
    }


}