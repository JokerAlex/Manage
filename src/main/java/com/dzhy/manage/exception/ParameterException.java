package com.dzhy.manage.exception;

/**
 * @ClassName ParameterException
 * @Description 参数异常
 * @Author alex
 * @Date 2018/10/30
 **/
public class ParameterException extends RuntimeException {
    public ParameterException(String msg) {
        super(msg);
    }
}
