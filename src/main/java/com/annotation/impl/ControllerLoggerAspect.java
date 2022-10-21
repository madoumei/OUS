package com.annotation.impl;


import com.annotation.ProcessLogger;
import com.google.gson.Gson;
import com.utils.SysLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component

public class ControllerLoggerAspect {

    @Pointcut("@annotation(com.annotation.ControllerLogger)")
//    @Pointcut("execution(public * com.client.controller..*.*(..)),execution(public * com.web.controller..*.*(..))")
    public void log() {
    }

    @Around("log()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();
        SysLog.info("Response：" + new Gson().toJson(result));
        SysLog.info("耗时：" + (System.currentTimeMillis() - startTime));

        return result;
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        SysLog.info("==================Start=================");
        SysLog.info("URL：" + request.getRequestURL().toString());
//        SysLog.info("Description：" + getLogValue(joinPoint));
        SysLog.info("Method：" + request.getMethod().toString());

        //打印controller全路径及method
        SysLog.info("Class Method：" + joinPoint.getSignature().getDeclaringTypeName() + "," + joinPoint.getSignature().getName());
        SysLog.info("客户端IP：" + request.getRemoteAddr());

//        SysLog.info("请求参数：" + new Gson().toJson(joinPoint.getArgs()));

    }

    private String getLogValue(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        ProcessLogger logger = method.getAnnotation(ProcessLogger.class);

        return logger.value();
    }



    //@AfterRunning: 返回通知 rsult为返回内容
    @AfterReturning(value="log()",returning="result")
    public void afterReturningMethod(JoinPoint joinPoint,Object result){
        SysLog.info("调用了返回:"+new Gson().toJson(result));
    }
    //@AfterThrowing: 异常通知
    @AfterThrowing(value="log()",throwing="e")
    public void afterReturningMethod(JoinPoint joinPoint, Exception e){
        SysLog.error("调用了异常通知",e);
    }
}
