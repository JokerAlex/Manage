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
        Product product = productRepository.findByProductId(outputSource.getOutputProductId());
        if (product == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + output.getOutputProductName());
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
            update.setOutputBaozhuangTotalPrice(update.getOutputBaozhuang() * product.getProductPrice());
        } else if (output.getOutputTeding() != null) {
            update.setOutputTeding(output.getOutputTeding());
            update.setOutputTedingTotalPrice(update.getOutputTeding() * product.getProductPrice());
        } else if (output.getOutputBeijingInput() != null) {
            update.setOutputBeijingInput(output.getOutputBeijingInput());
            update.setOutputBeijingInputTotalPrice(update.getOutputBeijingInput() * product.getProductPrice());
        } else if (output.getOutputBeijingtedingInput() != null) {
            update.setOutputBeijingtedingInput(output.getOutputBeijingtedingInput());
            update.setOutputBeijingtedingInputTotalPrice(update.getOutputBeijingtedingInput() * product.getProductPrice());
        } else if (output.getOutputFactoryOutput() != null) {
            update.setOutputFactoryOutput(output.getOutputFactoryOutput());
            update.setOutputFactoryOutputTotalPrice(update.getOutputFactoryOutput() * product.getProductPrice());
        } else if (output.getOutputTedingFactoryOutput() != null) {
            update.setOutputTedingFactoryOutput(output.getOutputTedingFactoryOutput());
            update.setOutputTedingFactoryOutputTotalPrice(update.getOutputTedingFactoryOutput() * product.getProductPrice());
        } else if (output.getOutputBeijingStock() != null) {
            update.setOutputBeijingStock(output.getOutputBeijingStock());
            update.setOutputBeijingStockTotalPrice(update.getOutputBeijingStock() * product.getProductPrice());
        } else if (output.getOutputBeijingtedingStock() != null) {
            update.setOutputBeijingtedingStock(output.getOutputBeijingtedingStock());
            update.setOutputBeijingtedingStockTotalPrice(update.getOutputBeijingtedingStock() * product.getProductPrice());
        }
        UpdateUtils.copyNullProperties(outputSource, update);
        try {
            outputRepository.save(update);
            log.info("update output success output = {}", update);
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
                            String.valueOf(output.getOutputMugongTotalPrice()),
                            String.valueOf(output.getOutputYoufang()),
                            String.valueOf(output.getOutputYoufangTotalPrice()),
                            String.valueOf(output.getOutputBaozhuang()),
                            String.valueOf(output.getOutputBaozhuangTotalPrice()),
                            String.valueOf(output.getOutputTeding()),
                            String.valueOf(output.getOutputTedingTotalPrice()),
                            String.valueOf(output.getOutputBeijingInput()),
                            String.valueOf(output.getOutputBeijingInputTotalPrice()),
                            String.valueOf(output.getOutputBeijingtedingInput()),
                            String.valueOf(output.getOutputBeijingtedingInputTotalPrice()),
                            String.valueOf(output.getOutputFactoryOutput()),
                            String.valueOf(output.getOutputFactoryOutputTotalPrice()),
                            String.valueOf(output.getOutputTedingFactoryOutput()),
                            String.valueOf(output.getOutputTedingFactoryOutputTotalPrice()),
                            String.valueOf(output.getOutputBeijingStock()),
                            String.valueOf(output.getOutputBeijingStockTotalPrice()),
                            String.valueOf(output.getOutputBeijingtedingStock()),
                            String.valueOf(output.getOutputBeijingtedingStockTotalPrice())
                    );
                })
                .collect(Collectors.toList());
        String title = year + "-" + month + "\t" + Constants.OUTPUT_TITLE;
        List<String> headers = Arrays.asList(Constants.PRODUCT_NAME, Constants.XIA_DAN, Constants.MU_GONG, Constants.MU_GONG_TOTAL_PRICE,
                Constants.YOU_FANG, Constants.YOU_FANG_TOTAL_PRICE, Constants.BAO_ZHUANG, Constants.BAOZHUNAG_TOTAL_PRICE, Constants.TE_DING,
                Constants.TEDING_TOTAL_PRICE, Constants.BEI_JING_INPUT, Constants.BEI_JING_INPUT_TOTAL_PRICE, Constants.BEI_JING_TEDING_INPUT,
                Constants.BEI_JING_TEDING_INPUT_TOTAL_PRICE, Constants.FACTORY_OUTPUT, Constants.FACTORY_OUTPUT_TOTAL_PRICE,
                Constants.TEDING_FACTORY_OUTPUT, Constants.TEDING_FACTORY_OUTPUT_TOTAL_PRICE, Constants.BEIJING_STOCK,
                Constants.BEIJING_STOCK_TOTAL_PRICE, Constants.BEIJINGTEDING_STOCK, Constants.BEIJINGTEDING_STOCK_TOTAL_PRICE);
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
        if (outputList.size() == 1) {
            Output output = new Output();
            UpdateUtils.copyNullProperties(outputList.get(0), output);
            output.setOutputProductName("合计");
            return output;
        } else {
            return outputList.stream()
                    .reduce((x, y) -> new Output()
                            .setOutputXiadan(x.getOutputXiadan() + y.getOutputXiadan())
                            .setOutputMugong(x.getOutputMugong() + y.getOutputMugong())
                            .setOutputMugongTotalPrice(x.getOutputMugongTotalPrice() + y.getOutputMugongTotalPrice())
                            .setOutputYoufang(x.getOutputYoufang() + y.getOutputYoufang())
                            .setOutputYoufangTotalPrice(x.getOutputYoufangTotalPrice() + y.getOutputYoufangTotalPrice())
                            .setOutputBaozhuang(x.getOutputBaozhuang() + y.getOutputBaozhuang())
                            .setOutputBaozhuangTotalPrice(x.getOutputBaozhuangTotalPrice() + y.getOutputBaozhuangTotalPrice())
                            .setOutputTeding(x.getOutputTeding() + y.getOutputTeding())
                            .setOutputTedingTotalPrice(x.getOutputTedingTotalPrice() + y.getOutputTedingTotalPrice())
                            .setOutputBeijingInput(x.getOutputBeijingInput() + y.getOutputBeijingInput())
                            .setOutputBeijingInputTotalPrice(x.getOutputBeijingInputTotalPrice() + y.getOutputBeijingInputTotalPrice())
                            .setOutputBeijingtedingInput(x.getOutputBeijingtedingInput() + y.getOutputBeijingtedingInput())
                            .setOutputBeijingtedingInputTotalPrice(x.getOutputBeijingtedingInputTotalPrice() + y.getOutputBeijingtedingInputTotalPrice())
                            .setOutputFactoryOutput(x.getOutputFactoryOutput() + y.getOutputFactoryOutput())
                            .setOutputFactoryOutputTotalPrice(x.getOutputFactoryOutputTotalPrice() + y.getOutputFactoryOutputTotalPrice())
                            .setOutputTedingFactoryOutput(x.getOutputTedingFactoryOutput() + y.getOutputTedingFactoryOutput())
                            .setOutputTedingFactoryOutputTotalPrice(x.getOutputTedingFactoryOutputTotalPrice() + y.getOutputTedingFactoryOutputTotalPrice())
                            .setOutputBeijingStock(x.getOutputBeijingStock() + y.getOutputBeijingStock())
                            .setOutputBeijingStockTotalPrice(x.getOutputBeijingStockTotalPrice() + y.getOutputBeijingStockTotalPrice())
                            .setOutputBeijingtedingStock(x.getOutputBeijingtedingStock() + y.getOutputBeijingtedingStock())
                            .setOutputBeijingtedingStockTotalPrice(x.getOutputBeijingtedingStockTotalPrice() + y.getOutputBeijingtedingStockTotalPrice())
                    )
                    .orElse(new Output()
                            .setOutputXiadan(0)
                            .setOutputMugong(0)
                            .setOutputMugongTotalPrice(0.0F)
                            .setOutputYoufang(0)
                            .setOutputYoufangTotalPrice(0.0F)
                            .setOutputBaozhuang(0)
                            .setOutputBaozhuangTotalPrice(0.0F)
                            .setOutputTeding(0)
                            .setOutputTedingTotalPrice(0.0F)
                            .setOutputBeijingInput(0)
                            .setOutputBeijingInputTotalPrice(0.0F)
                            .setOutputBeijingtedingInput(0)
                            .setOutputBeijingtedingInputTotalPrice(0.0F)
                            .setOutputFactoryOutput(0)
                            .setOutputFactoryOutputTotalPrice(0.0F)
                            .setOutputTedingFactoryOutput(0)
                            .setOutputTedingFactoryOutputTotalPrice(0.0F)
                            .setOutputBeijingStock(0)
                            .setOutputBeijingStockTotalPrice(0.0F)
                            .setOutputBeijingtedingStock(0)
                            .setOutputBeijingtedingStockTotalPrice(0.0F))
                    .setOutputProductName("合计");
        }
    }
}
