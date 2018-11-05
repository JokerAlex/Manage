package com.dzhy.manage.service.impl;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Year;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.repository.YearRepository;
import com.dzhy.manage.service.YearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName YearServiceImpl
 * @Description 年 管理
 * @Author alex
 * @Date 2018/10/30
 **/
@Service("iYearService")
@Slf4j
public class YearServiceImpl implements YearService {

    private final YearRepository yearRepository;

    public YearServiceImpl(YearRepository yearRepository) {
        this.yearRepository = yearRepository;
    }

    @Override
    public ResponseDTO listAll() {
        List<Year> years = yearRepository.findAll();
        return ResponseDTO.isSuccess(years);
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO addYear(Integer year) throws ParameterException, GeneralException {
        if (year == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        if (yearRepository.existsById(year)) {
            ResponseDTO.isError(ResultEnum.IS_EXIST.getMessage());
        }
        Year insert = new Year();
        insert.setYearId(year);
        try {
            yearRepository.save(insert);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }
}
