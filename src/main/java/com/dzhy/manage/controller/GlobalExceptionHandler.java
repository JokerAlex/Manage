package com.dzhy.manage.controller;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName GlobalExceptionHandler
 * @Description exception handler
 * @Author alex
 * @Date 2018/10/30
 **/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseDTO handlerException(Exception e) {
        log.error(e.getMessage());
        return ResponseDTO.isError(e.getMessage());
    }

    @ExceptionHandler(GeneralException.class)
    @ResponseBody
    public ResponseDTO handlerGeneralException(GeneralException e) {
        log.error(e.getMessage());
        return ResponseDTO.isError(e.getMessage());
    }

    @ExceptionHandler(ParameterException.class)
    @ResponseBody
    public ResponseDTO handlerParameterErrorException(ParameterException e) {
        log.error(e.getMessage());
        return ResponseDTO.isError(e.getMessage());
    }
}
