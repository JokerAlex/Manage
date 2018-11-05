package com.dzhy.manage.service.impl;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.repository.OutputRepository;
import com.dzhy.manage.repository.ProductRepository;
import com.dzhy.manage.service.ProductService;
import com.dzhy.manage.util.ExcelUtils;
import com.dzhy.manage.util.UpdateUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ProductServiceImpl
 * @Description 产品管理
 * @Author alex
 * @Date 2018/10/30
 **/
@Service("iProductService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OutputRepository outputRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, OutputRepository outputRepository) {
        this.productRepository = productRepository;
        this.outputRepository = outputRepository;
    }

    @Override
    public ResponseDTO checkProductName(String productName) throws ParameterException {
        if (StringUtils.isBlank(productName)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage() + ":" + productName);
        }

        log.info("[checkProductName] productName = {}", productName);
        boolean isExist = productRepository.existsByProductName(productName);
        if (isExist) {
            return ResponseDTO.isError(ResultEnum.UNUSABLE_NAME.getMessage() + " 名称:" + productName);
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO addProduct(Product product) throws ParameterException, GeneralException {
        if (product == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        ResponseDTO r = this.checkProductName(product.getProductName());
        if (!r.isOk()) {
            return r;
        }
        log.info("[addProduct] product = {}", product.toString());
        Product insert = new Product();
        insert.setProductName(product.getProductName());
        insert.setProductPrice(product.getProductPrice());
        insert.setProductComment(product.getProductComment());
        try {
            productRepository.save(insert);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess(insert);
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO importProduct(MultipartFile multipartFile) throws ParameterException, GeneralException, IOException {
        if (multipartFile == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        String fileName = multipartFile.getOriginalFilename();
        log.info("fileName = {}", fileName);
        //判断文件类型
        //读取文件内容并存储
        if (!fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2003L)
                && !fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2007U)) {
            return ResponseDTO.isError(ResultEnum.ILLEGAL_FILE_TYPE.getMessage());
        }
        //excel文件读取，写入数据库
        List<Map<String, String>> readResult = ExcelUtils.readToMapList(multipartFile.getInputStream());
        List<Product> productList = readResult.stream()
                .map(row -> {
                    if (productRepository.existsByProductName(row.get(Constants.PRODUCT_NAME))) {
                        throw new GeneralException(ResultEnum.IS_EXIST.getMessage() + "-名称:" + row.get(Constants.PRODUCT_NAME));
                    }

                    Product product = new Product();
                    product.setProductName(row.get(Constants.PRODUCT_NAME));
                    product.setProductPrice(Float.valueOf(row.get(Constants.PRICE)));
                    product.setProductComment(row.get(Constants.COMMENT));
                    return product;
                })
                .collect(Collectors.toList());
        try {
            productRepository.saveAll(productList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO updateProduct(Product product) throws ParameterException, GeneralException {
        if (product == null || product.getProductId() == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        if (product.getProductName() != null) {
            Product temp = productRepository.findByProductName(product.getProductName());
            if (temp != null && !temp.getProductId().equals(product.getProductId())) {
                return ResponseDTO.isError(ResultEnum.IS_EXIST.getMessage() + "-名称:" + product.getProductName());
            }
        }
        Product update = new Product();
        update.setProductId(product.getProductId());
        update.setProductName(product.getProductName());
        update.setProductPrice(product.getProductPrice());
        update.setProductComment(product.getProductComment());
        log.info("[addProduct] product = {}", product.toString());
        Product source = productRepository.findByProductId(product.getProductId());
        UpdateUtils.copyNullProperties(source, update);
        try {
            productRepository.save(update);
            //判断价格是否更新
            if (product.getProductPrice() != null) {
                List<Output> outputList = outputRepository.findAllByOutputProductId(update.getProductId());
                outputList.forEach(output -> {
                    output.setOutputBaozhuangTotalPrice(output.getOutputBaozhuang() * update.getProductPrice());
                    output.setOutputTedingTotalPrice(output.getOutputTeding() * update.getProductPrice());
                });
                outputRepository.saveAll(outputList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess(update);
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteProduct(Integer productId) throws ParameterException, GeneralException {
        if (productId == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        if (!productRepository.existsById(productId)) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" +productId);
        }
        log.info("[deleteProduct] productId = {}", productId);
        try {
            productRepository.deleteById(productId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }

        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteProductBatch(List<Integer> productIds) throws ParameterException, GeneralException {
        if (CollectionUtils.isEmpty(productIds)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        log.info("[deleteProductBatch] productIds = {}", productIds.toString());
        try {
            productRepository.deleteAllByProductIdIn(productIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO listAllProduct(String productName) {
        List<Product> products;
        if (StringUtils.isBlank(productName)) {
            products = productRepository.findAll();
        } else {
            Sort sort = new Sort(Sort.Direction.ASC, "productName");
            products = productRepository.findAllByProductNameContaining(productName, sort);
        }
        return ResponseDTO.isSuccess(products);
    }

    @Override
    public ResponseDTO listProduct(Integer pageNum, Integer pageSize, String productName) throws ParameterException {
        if (pageNum == null || pageSize == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "productName");
        Page<Product> productPage;
        if (StringUtils.isBlank(productName)) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findAllByProductNameContaining(productName, pageable);
        }
        return ResponseDTO.isSuccess(productPage);
    }

    @Override
    public ResponseDTO getDetails(Integer productId) throws ParameterException {
        if (productId == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product product = productRepository.findByProductId(productId);
        if (product == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + " ID:" + productId);
        }
        return ResponseDTO.isSuccess(product);
    }
}
