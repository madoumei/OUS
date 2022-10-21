package com.config.exception;

import lombok.Getter;


@Getter
public class ErrorException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2883756934282599356L;
	private ErrorEnum errorEnum ;
    /*无参构造函数*/
    public ErrorException(){
        super();
    }

    //用详细信息指定一个异常
    public ErrorException(ErrorEnum errorEnum){
        super(errorEnum.getMsg());
        this.errorEnum = errorEnum;
    }

    public void throwErrorException(ErrorEnum errorEnum){
        throw new ErrorException(errorEnum);
    }

}
