package com.dzhy.manage.service;

import com.dzhy.manage.entity.Output;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.dto.ResponseDTO;

import java.io.OutputStream;

/**
 * @ClassName OutputService
 * @Description output 管理
 * @Author alex
 * @Date 2018/10/30
 **/
public interface OutputService {

    ResponseDTO listOutput(Integer pageNum, Integer pageSize, Integer year, Integer month, String productName) throws ParameterException, GeneralException;

    ResponseDTO changeOutput(Output output) throws ParameterException, GeneralException;

    ResponseDTO exportExcel(Integer year, Integer month, OutputStream outputStream) throws ParameterException, GeneralException;
}
