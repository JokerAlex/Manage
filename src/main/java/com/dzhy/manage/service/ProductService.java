package com.dzhy.manage.service;

import com.dzhy.manage.entity.Product;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.dto.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName ProductService
 * @Description product 管理
 * @Author alex
 * @Date 2018/10/30
 **/
public interface ProductService {

    ResponseDTO checkProductName(String productName) throws ParameterException;

    ResponseDTO addProduct(Product product) throws ParameterException, GeneralException;

    ResponseDTO importProduct(MultipartFile multipartFile) throws ParameterException, GeneralException, IOException;

    ResponseDTO updateProduct(Product product) throws ParameterException, GeneralException;

    ResponseDTO uploadPictures(Integer productId, List<MultipartFile> multipartFiles) throws ParameterException, GeneralException, IOException;

    ResponseDTO deleteProduct(Integer productId) throws ParameterException, GeneralException;

    ResponseDTO deleteProductBatch(List<Integer> productIds) throws ParameterException, GeneralException;

    ResponseDTO listAllProduct(String productName)throws ParameterException;

    ResponseDTO listProduct(Integer pageNum, Integer pageSize, String productName) throws ParameterException;

    ResponseDTO getDetails(Integer productId) throws ParameterException, GeneralException;
}
