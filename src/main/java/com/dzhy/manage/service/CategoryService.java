package com.dzhy.manage.service;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Category;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description 产品分类服务
 * @Author alex
 * @Date 2018/11/13
 **/
public interface CategoryService {

    ResponseDTO checkCategoryName(String categoryName) throws ParameterException;

    ResponseDTO addCategory(Category category) throws ParameterException, GeneralException;

    ResponseDTO updateCategory(Category category) throws ParameterException, GeneralException;

    ResponseDTO deleteCategoryBatch(List<Integer> categoryIds) throws ParameterException, GeneralException;

    ResponseDTO listCategory(Integer pageNum, Integer pageSize, String categoryName) throws ParameterException;
}
