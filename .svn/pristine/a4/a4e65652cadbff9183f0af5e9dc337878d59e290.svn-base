package com.config.exception;

import com.google.common.base.VerifyException;
import com.utils.ExceptionUtils.RequestUtils;
import com.utils.SysLog;
import com.web.bean.RespInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLSyntaxErrorException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常拦截配置类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // 专门用来捕获和处理Controller层的校验时异常
    @ExceptionHandler(VerifyException.class)
    public ModelAndView verifyExceptionHandler(VerifyException e, HttpServletRequest request) {
        try {
            SysLog.error(request.getRequestURI()+RequestUtils.getRequestJsonString(request));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        mv.addObject("code", "999");
        mv.addObject("msg",e.getMessage());
        mv.addObject("data",null);
        return mv;
    }

    /**
     * 异常解析
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RespInfo resolveException(Exception exception, HttpServletRequest request) {
//    	StringWriter stringWriter = new StringWriter();
//    	exception.printStackTrace(new PrintWriter(stringWriter));
//        logger.error("ExceptionInfo:", stringWriter.toString());
        //exception.printStackTrace();
        SysLog.exceptionLogs(exception,request);

        Throwable cause = exception.getCause();
        //入参不可读异常
        if (exception instanceof HttpMessageNotReadableException) {
            String msg = cause.toString();
            if (msg.indexOf("\"") > 0) {
                msg = msg.split("\"")[1];
            }
            return new RespInfo(ErrorEnum.E_400.getCode(),ErrorEnum.E_400.getMsg(), "未知字段名：" + msg);
        }

        if (exception instanceof MissingServletRequestParameterException){
            return new RespInfo(ErrorEnum.E_400.getCode(),ErrorEnum.E_400.getMsg(), exception.getMessage());
        }


        //数组越界异常
        if (exception instanceof IndexOutOfBoundsException) {
            //Index: 1, Size: 0
            return new RespInfo(ErrorEnum.E_607.getCode(), ErrorEnum.E_607.getMsg(), exception.getMessage());
        }

        //无效参数
        if (exception instanceof IllegalArgumentException) {
            return new RespInfo(ErrorEnum.E_604.getCode(),ErrorEnum.E_604.getMsg(),exception.getMessage());
        }

        //空指针异常
//        if (exception instanceof NullPointerException) {
//            return new RespInfo(ErrorEnum.E_605.getCode(),ErrorEnum.E_605.getMsg(),exception.getMessage());
//        }

        //数据格式转换异常
        if (exception instanceof NumberFormatException) {
            return new RespInfo(ErrorEnum.E_606.getCode(), ErrorEnum.E_606.getMsg(),exception.getMessage());
        }

        // 数据库异常不返回详情
        if (exception instanceof BadSqlGrammarException
        || exception instanceof SQLDataException) {
            return new RespInfo(ErrorEnum.E_500.getCode(),ErrorEnum.E_500.getMsg());
        }


        //参数校验异常
        if (exception instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
            return new RespInfo(ErrorEnum.E_604.getCode(),ErrorEnum.E_604.getMsg(),bindingResult.getFieldError().getField()+":"
                    +bindingResult.getFieldError().getDefaultMessage());
        }

        // 业务异常
        if (exception instanceof RuntimeException) {
            if(exception.getMessage() != null && exception.getMessage().length()<30){
                return new RespInfo(ErrorEnum.E_500.getCode(),ErrorEnum.E_500.getMsg(),exception.getMessage());
            }
            return new RespInfo(ErrorEnum.E_500.getCode(),ErrorEnum.E_500.getMsg());
        }

        // 其他异常
        return new RespInfo(ErrorEnum.E_500.getCode(),ErrorEnum.E_500.getMsg());

    }
}
