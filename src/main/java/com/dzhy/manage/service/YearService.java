package com.dzhy.manage.service;

import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.dto.ResponseDTO;

/**
 * @ClassName YearService
 * @Description year 管理
 * @Author alex
 * @Date 2018/10/30
 **/
public interface YearService {

    ResponseDTO listAll();

    ResponseDTO addYear(Integer year) throws ParameterException, GeneralException;


}
