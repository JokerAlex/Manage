package com.dzhy.manage.service;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @ClassName ProduceService
 * @Description produce 管理
 * @Author alex
 * @Date 2018/10/30
 **/
public interface ProduceService {

    ResponseDTO addProduce(Produce produce) throws ParameterException, GeneralException;

    ResponseDTO importFromExcel(MultipartFile multipartFile) throws ParameterException, GeneralException, IOException;

    ResponseDTO importFromDB(Integer year, Integer month, Integer day) throws ParameterException, GeneralException;

    ResponseDTO exportExcel(Integer year, Integer month, Integer day, OutputStream outputStream) throws ParameterException, GeneralException;

    ResponseDTO updateProduce(Produce produce, int flag) throws ParameterException, GeneralException;

    ResponseDTO changeProduce(Produce produce) throws ParameterException, GeneralException;

    ResponseDTO deleteProduceBatch(List<Integer> produceIds) throws ParameterException, GeneralException;

    ResponseDTO deleteAllByDate(Integer year, Integer month, Integer day) throws ParameterException, GeneralException;

    ResponseDTO listProduce(Integer pageNum, Integer pageSize, Integer year, Integer month, Integer day, String productName) throws ParameterException;

    ResponseDTO getDetails(Integer produceId) throws ParameterException;
}
