package com.annotation.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.annotation.ProcessLogger;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.config.qicool.common.utils.StringUtils;
import com.google.gson.Gson;
import com.utils.SysLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.logging.Logger;

@Aspect
@Component
public class ProcessLoggerAspect {

//    @Pointcut("@annotation(com.annotation.ProcessLogger)")
@Pointcut("execution(public * com.client.service.Impl..*.*Task(..)) || execution(public * com.client.service.Impl..*.*Router(..))" +
        "|| execution(public * com.web.service.impl..*.*Task(..)) || execution(public * com.web.service.impl..*.*Router(..))" +
        "|| execution(public * com.web.service.impl..*.*Event(..)) || @annotation(com.annotation.ProcessLogger)")
public void log() {
    }

//    @Around("log()")
//    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//
//        Object result = joinPoint.proceed();
//        SysLog.info("Response：" + new Gson().toJson(result));
//        SysLog.info("耗时：" + (System.currentTimeMillis() - startTime));
//
//        return result;
//    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        
        //SysLog.info("==================Task Start=================");
        if(getLogValue(joinPoint) != null) {

            SysLog.info("Description：" + getLogValue(joinPoint)+" "+ joinPoint.getSignature().getDeclaringTypeName() + "," + joinPoint.getSignature().getName());
        }else {
            //打印全路径及method
            SysLog.info("Class Method：" + joinPoint.getSignature().getDeclaringTypeName() + "," + joinPoint.getSignature().getName());
            //SysLog.info("请求参数：" + new Gson().toJson(joinPoint.getArgs()));
        }

    }

    private String getLogValue(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ProcessLogger annotation = method.getAnnotation(ProcessLogger.class);
        if(annotation == null){
            return null;
        }
        return annotation.value();
    }

    private boolean isRouter(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ProcessLogger annotation = method.getAnnotation(ProcessLogger.class);
        if(annotation == null){
            return false;
        }
        return annotation.isRouter();
    }

    @After("log()")
    public void doAfter() {
        //SysLog.info("==================Task End=================");
    }

    //@AfterRunning: 返回通知 rsult为返回内容
    @AfterReturning(value="log()",returning="result")
    public void afterReturningMethod(JoinPoint joinPoint,Object result){
        if(joinPoint.getSignature().getName().contains("Router") || isRouter(joinPoint)) {
            SysLog.info("调用了返回:" + new Gson().toJson(result));
        }
    }
    //@AfterThrowing: 异常通知
    @AfterThrowing(value="log()",throwing="e")
    public void afterReturningMethod(JoinPoint joinPoint, Exception e){
        if(e instanceof ErrorException){
            ErrorException errorException = (ErrorException)e;
            ErrorEnum errorEnum = errorException.getErrorEnum();
            SysLog.error("调用了异常通知",errorEnum.getCode()+":"+errorEnum.getMsg());
        }else {
            SysLog.error("调用了异常通知",e);
        }

    }
}
