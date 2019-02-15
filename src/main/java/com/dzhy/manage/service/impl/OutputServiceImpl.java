package com.dzhy.manage.service.impl;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.repository.OutputRepository;
import com.dzhy.manage.repository.ProductRepository;
import com.dzhy.manage.service.OutputService;
import com.dzhy.manage.util.ExcelUtils;
import com.dzhy.manage.util.UpdateUtils;
import com.dzhy.manage.dto.ResponseDTO;
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

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @ClassName OutputServiceImpl
 * @Description 产值管理
 * @Author alex
 * @Date 2018/10/30
 **/
@Service("iOutputService")
@Slf4j
public class OutputServiceImpl implements OutputService {

    private final OutputRepository outputRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OutputServiceImpl(OutputRepository outputRepository, ProductRepository productRepository) {
        this.outputRepository = outputRepository;
        this.productRepository = productRepository;
    }


    @Override
    public ResponseDTO listOutput(Integer pageNum, Integer pageSize, Integer year, Integer month, String productName) throws ParameterException, GeneralException {
        if (year == null || month == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "outputProductName");
        Page<Output> outputPage;
        if (StringUtils.isBlank(productName)) {
            outputPage = outputRepository.findAllByOutputYearAndAndOutputMonth(year, month, pageable);
        } else {
            outputPage = outputRepository.findAllByOutputYearAndAndOutputMonthAndOutputProductNameContaining(year, month, productName, pageable);
        }
        return ResponseDTO.isSuccess(outputPage);
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO changeOutput(Output output) throws ParameterException, GeneralException {
        if (output == null || output.getOutputId() == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        //获取 output source
        Output outputSource = outputRepository.findByOutputId(output.getOutputId());
        if (outputSource == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + output.getOutputId());
        }
        Output update = new Output();
        if (output.getOutputXiadan() != null) {
            update.setOutputXiadan(output.getOutputXiadan());
        } else if (output.getOutputMugong() != null) {
            update.setOutputMugong(output.getOutputMugong());
        } else if (output.getOutputYoufang() != null) {
            update.setOutputYoufang(output.getOutputYoufang());
        } else if (output.getOutputBaozhuang() != null) {
            update.setOutputBaozhuang(output.getOutputBaozhuang());
            Product product = productRepository.findByProductId(outputSource.getOutputProductId());
            if (product == null) {
                return ResponseDTO.isError();
            }
            update.setOutputBaozhuangTotalPrice(update.getOutputBaozhuang() * product.getProductPrice());
        } else if (output.getOutputTeding() != null) {
            update.setOutputTeding(output.getOutputTeding());
            Product product = productRepository.findByProductId(outputSource.getOutputProductId());
            if (product == null) {
                return ResponseDTO.isError();
            }
            update.setOutputTedingTotalPrice(update.getOutputTeding() * product.getProductPrice());
        } else if (output.getOutputBeijing() != null) {
            update.setOutputBeijing(output.getOutputBeijing());
        } else if (output.getOutputBeijingteding() != null) {
            update.setOutputBeijingteding(output.getOutputBeijingteding());
        }
        UpdateUtils.copyNullProperties(outputSource, update);
        try {
            log.info("[changeOutput] update = {}", update.toString());
            outputRepository.save(update);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO exportExcel(Integer year, Integer month, OutputStream outputStream) throws ParameterException, GeneralException {
        if (year == null || month == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<Output> outputList = outputRepository.findAllByOutputYearAndAndOutputMonth(year, month);
        if (CollectionUtils.isEmpty(outputList)) {
            return ResponseDTO.isError("选定的月份没有数据");
        }
        Output total = getTotal(outputList);
        outputList.add(total);
        List<List<String>> list = outputList.stream()
                .map(output -> {
                    return Arrays.asList(
                            output.getOutputProductName(),
                            String.valueOf(output.getOutputXiadan()),
                            String.valueOf(output.getOutputMugong()),
                            String.valueOf(output.getOutputYoufang()),
                            String.valueOf(output.getOutputBaozhuang()),
                            String.valueOf(output.getOutputTeding()),
                            String.valueOf(output.getOutputBaozhuangTotalPrice()),
                            String.valueOf(output.getOutputTedingTotalPrice()),
                            String.valueOf(output.getOutputBeijing()),
                            String.valueOf(output.getOutputBeijingteding())
                    );
                })
                .collect(Collectors.toList());
        String title = year + "-" + month + "\t" + Constants.OUTPUT_TITLE;
        List<String> headers = Arrays.asList(Constants.PRODUCT_NAME, Constants.XIA_DAN, Constants.MU_GONG,
                Constants.YOU_FANG, Constants.BAO_ZHUANG, Constants.TE_DING, Constants.BAOZHUNAG_TOTAL_PRICE,
                Constants.TEDING_TOTAL_PRICE, Constants.BEI_JING, Constants.BEI_JING_TE_DING);
        try {
            ExcelUtils.exportData(title, headers, list, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.EXPORT_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO getOutputTotal(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<Output> outputList = outputRepository.findAllByOutputYearAndAndOutputMonth(year, month);
        Output total = getTotal(outputList);
        return ResponseDTO.isSuccess(total);
    }

    private Output getTotal(List<Output> outputList) {
        return outputList.stream()
                .reduce((x, y) -> new Output()
                        .setOutputXiadan(x.getOutputXiadan() + y.getOutputXiadan())
                        .setOutputMugong(x.getOutputMugong() + y.getOutputMugong())
                        .setOutputYoufang(x.getOutputYoufang() + y.getOutputYoufang())
                        .setOutputBaozhuang(x.getOutputBaozhuang() + y.getOutputBaozhuang())
                        .setOutputBaozhuangTotalPrice(x.getOutputBaozhuangTotalPrice() + y.getOutputBaozhuangTotalPrice())
                        .setOutputTeding(x.getOutputTeding() + y.getOutputTeding())
                        .setOutputTedingTotalPrice(x.getOutputTedingTotalPrice() + y.getOutputTedingTotalPrice())
                        .setOutputBeijing(x.getOutputBeijing() + y.getOutputBeijing())
                        .setOutputBeijingteding(x.getOutputBeijingteding() + y.getOutputBeijingteding())
                )
                .orElse(new Output()
                        .setOutputXiadan(0)
                        .setOutputMugong(0)
                        .setOutputYoufang(0)
                        .setOutputBaozhuang(0)
                        .setOutputBaozhuangTotalPrice(0.0F)
                        .setOutputTeding(0)
                        .setOutputTedingTotalPrice(0.0F)
                        .setOutputBeijing(0)
                        .setOutputBeijingteding(0))
                .setOutputProductName("合计");
    }
}
