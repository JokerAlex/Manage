package com.dzhy.manage.service.impl;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.repository.OutputRepository;
import com.dzhy.manage.repository.ProduceRepository;
import com.dzhy.manage.repository.ProductRepository;
import com.dzhy.manage.service.ProductService;
import com.dzhy.manage.util.ExcelUtils;
import com.dzhy.manage.util.FileUtil;
import com.dzhy.manage.util.FtpUtil;
import com.dzhy.manage.util.UpdateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
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

    @Value("${manage.img.filePath}")
    private String filePath;

    @Value("${manage.fileType}")
    private String[] fileTypes;

    private final ProductRepository productRepository;
    private final ProduceRepository produceRepository;
    private final OutputRepository outputRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProduceRepository produceRepository, OutputRepository outputRepository) {
        this.productRepository = productRepository;
        this.produceRepository = produceRepository;
        this.outputRepository = outputRepository;
    }

    @Override
    public ResponseDTO checkProductName(String productName) throws ParameterException {
        if (StringUtils.isBlank(productName)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage() + ":" + productName);
        }

        log.info("productName = {}", productName);
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
        Product insert = new Product();
        insert.setProductName(product.getProductName());
        insert.setProductPrice(product.getProductPrice());
        insert.setProductComment(product.getProductComment());
        insert.setCategoryId(product.getCategoryId());
        insert.setProductSize(product.getProductSize());
        try {
            productRepository.save(insert);
            log.info("add product success product = {}", insert);
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
        //update.setProductName(product.getProductName());
        update.setProductPrice(product.getProductPrice());
        update.setProductComment(product.getProductComment());
        update.setProductSize(product.getProductSize());
        update.setCategoryId(product.getCategoryId());
        Product source = productRepository.findByProductId(product.getProductId());
        boolean isPriceUpdate = true;
        if (source.getProductPrice().equals(update.getProductPrice())) {
            isPriceUpdate = false;
        }
        UpdateUtils.copyNullProperties(source, update);
        try {
            productRepository.save(update);
            log.info("update product success product = {}", update);
            //判断价格是否更新
            if (isPriceUpdate) {
                List<Output> outputList = outputRepository.findAllByOutputProductId(update.getProductId());
                outputList.forEach(output -> {
                    output.setOutputMugongTotalPrice(output.getOutputMugong() * update.getProductPrice());
                    output.setOutputYoufangTotalPrice(output.getOutputYoufang() * update.getProductPrice());
                    output.setOutputBaozhuangTotalPrice(output.getOutputBaozhuang() * update.getProductPrice());
                    output.setOutputTedingTotalPrice(output.getOutputTeding() * update.getProductPrice());
                    output.setOutputBeijingInputTotalPrice(output.getOutputBeijingInput() * update.getProductPrice());
                    output.setOutputBeijingtedingInputTotalPrice(output.getOutputBeijingtedingInput() * update.getProductPrice());
                    output.setOutputFactoryOutputTotalPrice(output.getOutputFactoryOutput() * update.getProductPrice());
                    output.setOutputTedingFactoryOutputTotalPrice(output.getOutputTedingFactoryOutput() * update.getProductPrice());
                    output.setOutputBeijingStockTotalPrice(output.getOutputBeijingStock() * update.getProductPrice());
                    output.setOutputBeijingtedingStockTotalPrice(output.getOutputBeijingtedingStock() * update.getProductPrice());
                });
                outputRepository.saveAll(outputList);
                List<Produce> produceList = produceRepository.findAllByProduceProductId(update.getProductId());
                produceList.forEach(produce -> produce.setProduceProductPrice(update.getProductPrice()));
                produceRepository.saveAll(produceList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess(update);
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO uploadPictures(Integer productId, List<MultipartFile> multipartFiles) throws ParameterException, GeneralException, IOException {
        if (productId == null || CollectionUtils.isEmpty(multipartFiles)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product product = productRepository.findByProductId(productId);
        if (product == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage());
        }
        List<String> pictureNameList = Lists.newArrayList();
        Map<String, MultipartFile> map = new LinkedHashMap<>(multipartFiles.size());
        //图片重命名
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                return ResponseDTO.isError("图片错误");
            }
            String originalFilename = multipartFile.getOriginalFilename();
            assert originalFilename != null;
            String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!isValid(fileType, fileTypes)) {
                return ResponseDTO.isError("图片格式错误");
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + fileType;
            map.put(fileName, multipartFile);
            pictureNameList.add(fileName);
        }
        FileUtil.upload(map, filePath);
        //产品保存图片信息
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(product.getProductImg())) {
            sb.append(product.getProductImg()).append(",");
        }
        sb.append(StringUtils.join(pictureNameList, ","));
        product.setProductImg(sb.toString());
        try {
            productRepository.save(product);
            log.info("upload pictures success product = {}", product);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deletePictures(Integer productId, List<String> fileNames) throws ParameterException, GeneralException, IOException {
        if (productId == null || CollectionUtils.isEmpty(fileNames)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        Product product = productRepository.findByProductId(productId);
        if (product == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage());
        }
        if (StringUtils.isBlank(product.getProductImg())) {
            return ResponseDTO.isError("该产品没有图片");
        }
        //删除文件
        FileUtil.del(fileNames, filePath);
        List<String> pictureList = Lists.newArrayList(product.getProductImg().split(","));
        boolean delImgStr = pictureList.removeAll(fileNames);
        //产品图片信息更新
        product.setProductImg(StringUtils.join(pictureList, ","));
        try {
            productRepository.save(product);
            log.info("delete pictures success productId : {}, delImgStr : {}", productId, delImgStr);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteProduct(Integer productId) throws ParameterException, GeneralException {
        if (productId == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        if (!productRepository.existsById(productId)) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + productId);
        }
        try {
            productRepository.deleteById(productId);
            log.info("delete product success, productId = {}", productId);
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
        try {
            //获取所有图片名称
            List<Product> productList = productRepository.findByProductIdIn(productIds);
            List<String> pictures = Lists.newArrayList();
            LocalDate date = LocalDate.now();
            for (Product product : productList) {
                if (produceRepository.existsByProduceYearAndProduceMonthAndProduceDayAndProduceProductId(
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth(), product.getProductId())) {
                    return ResponseDTO.isError("该产品在进度表中仍有使用记录，不能删除");
                }

                if (StringUtils.isNotBlank(product.getProductImg())) {
                    pictures.addAll(Lists.newArrayList(product.getProductImg().split(",")));
                }
            }
            if (CollectionUtils.isNotEmpty(pictures)) {
                FileUtil.del(pictures, filePath);
            }
            log.info("delete product batch pictures : {}", pictures);
            productRepository.deleteAllByProductIdIn(productIds);
            log.info("delete product batch productIds = {}", productIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO listAllProduct(String productName) {
        Sort sort = new Sort(Sort.Direction.ASC, "productName");
        List<Product> products = productRepository.findAll(MySpec.method(productName, null), sort);
        return ResponseDTO.isSuccess(products);
    }

    @Override
    public ResponseDTO listProduct(Integer pageNum, Integer pageSize, String productName, Integer categoryId) throws ParameterException {
        if (pageNum == null || pageSize == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "productName");
        Page<Product> productPage = productRepository.findAll(MySpec.method(productName, categoryId), pageable);
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

    private boolean isValid(String fileType, String[] allowTypes) {
        if (StringUtils.isBlank(fileType)) {
            return false;
        }
        for (String type : allowTypes) {
            if (type.equals(fileType)) {
                return true;
            }
        }
        return false;
    }

    private static class MySpec {
        /**
         * 动态查询语句
         *
         * @param productName
         * @param categoryId
         * @return
         */
        static Specification<Product> method(String productName, Integer categoryId) {
            return (Specification<Product>) (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(productName)) {
                    predicateList.add(criteriaBuilder.like(root.get("productName"), '%' + productName + '%'));
                }
                if (categoryId != null) {
                    predicateList.add((criteriaBuilder.equal(root.get("categoryId"), categoryId)));
                }
                log.info("productName : {}, categoryId : {}", productName, categoryId);
                Predicate[] predicates = new Predicate[predicateList.size()];
                return criteriaBuilder.and(predicateList.toArray(predicates));
            };
        }
    }
}
