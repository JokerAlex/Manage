package com.dzhy.manage.controller;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Category;
import com.dzhy.manage.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CategoryController
 * @Description 产品类别管理
 * @Author alex
 * @Date 2018/11/13
 **/
@RestController
@RequestMapping("/category")
@Api(value = "类别", description = "产品类别管理")
public class CategoryController {

    private final CategoryService iCategoryService;

    @Autowired
    public CategoryController(CategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    @ApiOperation(value = "检查类别名称", notes = "检查类别名称是否可用")
    @ApiImplicitParam(name = "categoryName", value = "类别名称", required = true, dataTypeClass = String.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @GetMapping("/check")
    public ResponseDTO checkCategoryName(@RequestParam(value = "categoryName") String categoryName) {
        return iCategoryService.checkCategoryName(categoryName);
    }
    @ApiOperation(value = "添加类别", notes = "添加新的类别")
    @ApiImplicitParam(name = "category", value = "类别实体", required = true, dataTypeClass = Category.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PostMapping()
    public ResponseDTO addCategory(@RequestBody Category category) {
        return iCategoryService.addCategory(category);
    }

    @ApiOperation(value = "更新类别", notes = "更新类别")
    @ApiImplicitParam(name = "category", value = "类别实体", required = true, dataTypeClass = Category.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PutMapping()
    public ResponseDTO updateCategory(@RequestBody Category category) {
        return iCategoryService.updateCategory(category);
    }

    @ApiOperation(value = "删除类别", notes = "单个/批量删除类别")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @DeleteMapping()
    public ResponseDTO deleteCategoryBatch(@RequestParam(value = "categoryIds[]") List<Integer> categoryIds) {
        return iCategoryService.deleteCategoryBatch(categoryIds);
    }

    @ApiOperation(value = "列表", notes = "获取类别列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "categoryName", value = "类别名称，模糊查询使用", dataTypeClass = String.class)
    })
    @GetMapping()
    public ResponseDTO listCategory(@RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize")Integer pageSize,
                                    @RequestParam(value = "categoryName")String categoryName) {
        return iCategoryService.listCategory(pageNum, pageSize, categoryName);
    }
}
