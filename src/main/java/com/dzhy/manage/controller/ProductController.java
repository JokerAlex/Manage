package com.dzhy.manage.controller;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.service.ProductService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName ProductController
 * @Description 产品管理 controller
 * @Author alex
 * @Date 2018/10/31
 **/
@RestController
@RequestMapping("/product")
@Api(value = "产品", description = "产品管理")
public class ProductController {

    private final ProductService iProductService;

    public ProductController(ProductService iProductService) {
        this.iProductService = iProductService;
    }

    @ApiOperation(value = "检查产品名称", notes = "添加新的产品前检查产品名称是否在数据库中已有记录")
    @ApiImplicitParam(name = "productName", value = "产品名称", required = true, dataTypeClass = String.class)
    @GetMapping("/check")
    public ResponseDTO checkProductName(@RequestParam(value = "productName") String productName) {
        return iProductService.checkProductName(productName);
    }

    @ApiOperation(value = "添加产品", notes = "添加新的产品")
    @ApiImplicitParam(name = "product", value = "产品实体类", required = true, dataTypeClass = Product.class)
    @PostMapping()
    public ResponseDTO addProduct(@RequestBody Product product) {
        return iProductService.addProduct(product);
    }

    @ApiOperation(value = "添加产品", notes = "通过 Excel 文件导入，添加新的产品")
    @PostMapping(value = "/import", headers = "content-type=multipart/form-data")
    public ResponseDTO importProduct(@ApiParam(value = "文件", required = true) MultipartFile multipartFile) throws Exception {
        return iProductService.importProduct(multipartFile);
    }

    @ApiOperation(value = "更新产品", notes = "更新产品信息")
    @PutMapping()
    public ResponseDTO updateProduct(@RequestBody Product product) {
        return iProductService.updateProduct(product);
    }

    @ApiOperation(value = "删除产品", notes = "删除产品，单个删除，批量删除")
    @ApiImplicitParam(name = "productIds", value = "要删除的产品ID集合", required = true, dataTypeClass = List.class)
    @DeleteMapping()
    public ResponseDTO deleteProductBatch(@RequestBody List<Integer> productIds) {
        return iProductService.deleteProductBatch(productIds);
    }

    @ApiOperation(value = "获取所有产品", notes = "获取所有产品, 添加新的生产进度使用该接口")
    @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class)
    @GetMapping("/list")
    public ResponseDTO listAllProduct(@RequestParam(value = "productName") String productName) {
        return iProductService.listAllProduct(productName);
    }

    @ApiOperation(value = "获取所有产品", notes = "获取所有产品, 产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class)
    })
    @GetMapping()
    public ResponseDTO listProduct(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "productName")String productName) {
        return iProductService.listProduct(pageNum, pageSize, productName);
    }

    @ApiOperation(value = "获取产品详情", notes = "获取产品详情")
    @GetMapping("/{productId}")
    public ResponseDTO getDetails(@PathVariable("productId") Integer productId) {
        return iProductService.getDetails(productId);
    }
}