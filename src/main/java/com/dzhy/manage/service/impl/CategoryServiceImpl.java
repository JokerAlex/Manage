package com.dzhy.manage.service.impl;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Category;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.repository.CategoryRepository;
import com.dzhy.manage.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CategoryServiceImpl
 * @Description 产品分类管理
 * @Author alex
 * @Date 2018/11/13
 **/
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ResponseDTO checkCategoryName(String categoryName) throws ParameterException {
        if (StringUtils.isBlank(categoryName)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        if (categoryRepository.existsByCategoryName(categoryName)) {
            return ResponseDTO.isError(ResultEnum.IS_EXIST.getMessage());
        }
        return ResponseDTO.isSuccess();

    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO addCategory(Category category) throws ParameterException, GeneralException {
        if (category == null || StringUtils.isBlank(category.getCategoryName())) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        ResponseDTO checkName = this.checkCategoryName(category.getCategoryName());
        if (!checkName.isOk()) {
            return checkName;
        }
        Category add = new Category().setCategoryName(category.getCategoryName());
        try {
            categoryRepository.save(add);
            log.info("[addCategory] add success");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO updateCategory(Category category) throws ParameterException, GeneralException {
        if (category == null || category.getCategoryId() == null || StringUtils.isBlank(category.getCategoryName())) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        ResponseDTO checkName = this.checkCategoryName(category.getCategoryName());
        if (!checkName.isOk()) {
            return checkName;
        }
        Category update = categoryRepository.findByCategoryId(category.getCategoryId());
        update.setCategoryName(category.getCategoryName());
        try {
            categoryRepository.save(update);
            log.info("[updateCategory] update success");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteCategoryBatch(List<Integer> categoryIds) throws ParameterException, GeneralException {
        if (CollectionUtils.isEmpty(categoryIds)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        try {
            log.info("[deleteCategoryBatch] categoryIds : {}", categoryIds.toString());
            categoryRepository.deleteByCategoryIdIn(categoryIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO listCategory(Integer pageNum, Integer pageSize, String categoryName) throws ParameterException {
        if (pageNum == null || pageSize == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "categoryName");
        Page<Category> categoryPage;
        if (StringUtils.isBlank(categoryName)) {
            categoryPage = categoryRepository.findAll(pageable);
        } else {
            categoryPage = categoryRepository.findAllByCategoryNameContaining(categoryName, pageable);
        }
        return ResponseDTO.isSuccess(categoryPage);
    }
}
