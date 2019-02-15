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
import com.dzhy.manage.service.ProduceService;
import com.dzhy.manage.util.ExcelUtils;
import com.dzhy.manage.util.UpdateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName ProduceServiceImpl
 * @Description 进度管理
 * @Author alex
 * @Date 2018/10/30
 **/
@Service("iProduceService")
@Slf4j
public class ProduceServiceImpl implements ProduceService {

    private final ProduceRepository produceRepository;
    private final OutputRepository outputRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProduceServiceImpl(ProduceRepository produceRepository, OutputRepository outputRepository, ProductRepository productRepository) {
        this.produceRepository = produceRepository;
        this.outputRepository = outputRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO addProduce(Produce produce) throws ParameterException, GeneralException {
        if (produce == null || produce.getProduceProductId() == null
                || StringUtils.isBlank(produce.getProduceProductName()) || produce.getProduceXiadan() <= 0) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        LocalDate date = LocalDate.now();
        boolean isExist = produceRepository.existsByProduceYearAndProduceMonthAndProduceDayAndProduceProductId(date.getYear(),
                date.getMonthValue(), date.getDayOfMonth(), produce.getProduceProductId());
        log.info("[addProduce] isExist = {}", isExist);
        if (isExist) {
            return ResponseDTO.isError(ResultEnum.IS_EXIST.getMessage());
        }
        //添加进度，一般只设置下单的值，其他阶段均为0
        Produce insert = new Produce();
        insert.setProduceYear(date.getYear());
        insert.setProduceMonth(date.getMonthValue());
        insert.setProduceDay(date.getDayOfMonth());
        insert.setProduceProductId(produce.getProduceProductId());
        insert.setProduceProductName(produce.getProduceProductName());
        insert.setProduceXiadan(produce.getProduceXiadan());
        insert.setProduceXiadanComment(produce.getProduceXiadanComment());

        try {
            produceRepository.save(insert);
            log.info("[addProduce] produce = {}", insert.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess(insert);
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO importFromExcel(MultipartFile multipartFile) throws ParameterException, GeneralException, IOException {
        if (multipartFile == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        LocalDate date = LocalDate.now();
        if (produceRepository.existsByProduceYearAndProduceMonthAndProduceDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth())) {
            log.info("请先将今天的数据清空");
            return ResponseDTO.isError("请先将今天的数据清空");
        }

        String fileName = multipartFile.getOriginalFilename();
        log.info("fileName = {}", fileName);
        //判断文件类型
        //读取文件内容并存储
        assert fileName != null;
        if (!fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2003L)
                && !fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2007U)) {
            return ResponseDTO.isError(ResultEnum.ILLEGAL_FILE_TYPE.getMessage());
        }
        //excel文件读取，写入数据库
        List<Map<String, String>> readResult = ExcelUtils.readToMapList(multipartFile.getInputStream());
        List<Produce> produceList = readResult.stream()
                .map(row -> {
                    Product product = productRepository.findByProductName(row.get(Constants.PRODUCT_NAME));
                    if (product == null) {
                        throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + row.get(Constants.PRODUCT_NAME));
                    }
                    return new Produce(
                            date.getYear(),
                            date.getMonthValue(),
                            date.getDayOfMonth(),
                            product.getProductId(),
                            product.getProductName(),
                            Integer.valueOf(row.get(Constants.XIA_DAN)),
                            row.get(Constants.XIA_DAN_COMMENT),
                            Integer.valueOf(row.get(Constants.MU_GONG)),
                            row.get(Constants.MU_GONG_COMMENT),
                            Integer.valueOf(row.get(Constants.YOU_FANG)),
                            row.get(Constants.YOU_FANG_COMMENT),
                            Integer.valueOf(row.get(Constants.BAO_ZHUANG)),
                            row.get(Constants.BAO_ZHUANG_COMMENT),
                            Integer.valueOf(row.get(Constants.TE_DING)),
                            row.get(Constants.TE_DING_COMMENT),
                            Integer.valueOf(row.get(Constants.BEI_JING)),
                            row.get(Constants.BEI_JING_COMMENT),
                            Integer.valueOf(row.get(Constants.BEI_JING_TE_DING)),
                            row.get(Constants.BEI_JING_TE_DING_COMMENT),
                            Integer.valueOf(row.get(Constants.BEN_DI_HE_TONG)),
                            row.get(Constants.BEN_DI_HE_TONG_COMMENT),
                            Integer.valueOf(row.get(Constants.WAI_DI_HE_TONG)),
                            row.get(Constants.WAI_DI_HE_TONG_COMMENT),
                            Integer.valueOf(row.get(Constants.DENG)),
                            row.get(Constants.DENG_COMMENT)
                    );
                })
                .collect(Collectors.toList());
        try {
            produceRepository.saveAll(produceList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO importFromDB(Integer year, Integer month, Integer day) throws ParameterException, GeneralException {
        if (year == null || month == null || day == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        LocalDate date = LocalDate.now();
        if (produceRepository.existsByProduceYearAndProduceMonthAndProduceDay(date.getYear(), date.getMonthValue(), date.getDayOfMonth())) {
            return ResponseDTO.isError("请先将今天的数据清空");
        }
        List<Produce> produceList = produceRepository.findAllByProduceYearAndProduceMonthAndProduceDay(year, month, day);
        if (CollectionUtils.isEmpty(produceList)) {
            return ResponseDTO.isError("选定的日期没有数据");
        }
        List<Produce> insertList = produceList.stream()
                .map(produce -> {
                    Produce p = new Produce();
                    BeanUtils.copyProperties(produce, p);
                    p.setProduceId(null);
                    p.setProduceYear(date.getYear());
                    p.setProduceMonth(date.getMonthValue());
                    p.setProduceDay(date.getDayOfMonth());
                    p.setProduceCreateTime(null);
                    p.setProduceUpdateTime(null);
                    //每月1号的数据不导入备注信息
                    if (date.getDayOfMonth() == 1) {
                        p.setProduceXiadanComment(null);
                        p.setProduceMugongComment(null);
                        p.setProduceYoufangComment(null);
                        p.setProduceBaozhuangComment(null);
                        p.setProduceTedingComment(null);
                        p.setProduceBeijingComment(null);
                        p.setProduceBeijingtedingComment(null);
                        p.setProduceBendihetongComment(null);
                        p.setProduceWaidihetongComment(null);
                        p.setProduceDengComment(null);
                    }
                    return p;
                })
                .collect(Collectors.toList());
        try {
            produceRepository.saveAll(insertList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO exportExcel(Integer year, Integer month, Integer day, OutputStream outputStream) throws ParameterException, GeneralException {
        if (year == null || month == null || day == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<Produce> produceList = produceRepository.findAllByProduceYearAndProduceMonthAndProduceDay(year, month, day);
        if (CollectionUtils.isEmpty(produceList)) {
            return ResponseDTO.isError("选定的日期没有数据");
        }
        //计算属性值合计
        Produce total = getTotal(produceList);
        produceList.add(total);
        List<List<String>> list = produceList.stream()
                .map(produce -> {
                    return Lists.newArrayList(
                            produce.getProduceProductName(),
                            String.valueOf(produce.getProduceXiadan()),
                            produce.getProduceXiadanComment(),
                            String.valueOf(produce.getProduceMugong()),
                            produce.getProduceMugongComment(),
                            String.valueOf(produce.getProduceYoufang()),
                            produce.getProduceYoufangComment(),
                            String.valueOf(produce.getProduceBaozhuang()),
                            produce.getProduceBaozhuangComment(),
                            String.valueOf(produce.getProduceTeding()),
                            produce.getProduceTedingComment(),
                            String.valueOf(produce.getProduceBeijing()),
                            produce.getProduceBeijingComment(),
                            String.valueOf(produce.getProduceBeijingteding()),
                            produce.getProduceBeijingtedingComment(),
                            String.valueOf(produce.getProduceBendihetong()),
                            produce.getProduceBendihetongComment(),
                            String.valueOf(produce.getProduceWaidihetong()),
                            produce.getProduceWaidihetongComment(),
                            String.valueOf(produce.getProduceDeng()),
                            produce.getProduceDengComment()
                    );
                })
                .collect(Collectors.toList());
        List<String> headers = Arrays.asList(Constants.PRODUCT_NAME, Constants.XIA_DAN, Constants.XIA_DAN_COMMENT,
                Constants.MU_GONG, Constants.MU_GONG_COMMENT, Constants.YOU_FANG, Constants.YOU_FANG_COMMENT,
                Constants.BAO_ZHUANG, Constants.BAO_ZHUANG_COMMENT, Constants.TE_DING, Constants.TE_DING_COMMENT,
                Constants.BEI_JING, Constants.BEI_JING_COMMENT, Constants.BEI_JING_TE_DING, Constants.BEI_JING_TE_DING_COMMENT,
                Constants.BEN_DI_HE_TONG, Constants.BEN_DI_HE_TONG_COMMENT, Constants.WAI_DI_HE_TONG, Constants.WAI_DI_HE_TONG_COMMENT,
                Constants.DENG, Constants.DENG_COMMENT);
        String title = year + "-" + month + "-" + day + "\t" + Constants.PRODUCE_TITLE;
        try {
            ExcelUtils.exportData(title, headers, list, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.EXPORT_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO updateProduce(Produce produce, int flag) throws ParameterException, GeneralException {
        if (produce == null || produce.getProduceId() == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        LocalDate date = LocalDate.now();
        //获取 produce source
        Produce produceSource = produceRepository.findByProduceId(produce.getProduceId());
        if (produceSource == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produce.getProduceId());
        }
        //获取 output source
        Output outputSource = getOutputSource(date.getYear(), date.getMonthValue(),
                produceSource.getProduceProductId(), produceSource.getProduceProductName());

        Produce update = new Produce();
        //判断更新是否可行，正值为生产进度，负值为退单
        if (produce.getProduceXiadan() != null && !updateXiaDan(produce, produceSource, update).isOk()) {
            //下单
            return updateXiaDan(produce, produceSource, update);
        } else if (produce.getProduceMugong() != null && !updateMuGong(produce, produceSource, update, outputSource).isOk()) {
            //木工
            return updateMuGong(produce, produceSource, update, outputSource);
        } else if (produce.getProduceYoufang() != null && !updateYouFang(produce, produceSource, update, outputSource).isOk()) {
            //油房
            return updateYouFang(produce, produceSource, update, outputSource);
        } else if (produce.getProduceBaozhuang() != null && !updateBaoZhuang(produce, produceSource, update, outputSource).isOk()) {
            //包装
            return updateBaoZhuang(produce, produceSource, update, outputSource);
        } else if (produce.getProduceTeding() != null && !updateTeDing(produce, produceSource, update, outputSource).isOk()) {
            //特定
            return updateTeDing(produce, produceSource, update, outputSource);
        } else if (produce.getProduceBeijing() != null && !updateBeiJing(produce, produceSource, update, outputSource, flag).isOk()) {
            //北京
            return updateBeiJing(produce, produceSource, update, outputSource, flag);
        } else if (produce.getProduceBeijingteding() != null && !updateBeiJingTeDing(produce, produceSource, update, outputSource, flag).isOk()) {
            //北京特定
            return updateBeiJingTeDing(produce, produceSource, update, outputSource, flag);
        } else if (produce.getProduceBendihetong() != null && !updateBenDiHeTong(produce, produceSource, update).isOk()) {
            //本地合同
            return updateBenDiHeTong(produce, produceSource, update);
        } else if (produce.getProduceWaidihetong() != null && !updateWaiDiHeTong(produce, produceSource, update).isOk()) {
            //外地合同
            return updateWaiDiHeTong(produce, produceSource, update);
        } else if (produce.getProduceDeng() != null && !updateDeng(produce, produceSource, update).isOk()) {
            //等待
            return updateDeng(produce, produceSource, update);
        }
        //更新到数据库
        try {
            //本地合同、外地合同、等待不影响产值
            if (produce.getProduceBendihetong() == null && produce.getProduceWaidihetong() == null && produce.getProduceDeng() == null) {
                outputRepository.save(outputSource);
            }
            UpdateUtils.copyNullProperties(produceSource, update);
            log.info("[updateProduce] update = {}", update);
            produceRepository.save(update);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO changeProduce(Produce produce) throws ParameterException, GeneralException {
        if (produce == null || produce.getProduceId() == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        //获取 produce source
        Produce produceSource = produceRepository.findByProduceId(produce.getProduceId());
        if (produceSource == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produce.getProduceId());
        }
        Produce update = new Produce();
        if (produce.getProduceXiadan() != null && produce.getProduceXiadan() >= 0) {
            //修正下单
            update.setProduceXiadan(produce.getProduceXiadan());
            update.setProduceXiadanComment(commentAppend(produceSource.getProduceXiadanComment(), "修改为",
                    produce.getProduceXiadan(), produce.getProduceXiadanComment()));
        } else if (produce.getProduceMugong() != null && produce.getProduceMugong() >= 0) {
            //修正木工
            update.setProduceMugong(produce.getProduceMugong());
            update.setProduceMugongComment(commentAppend(produceSource.getProduceMugongComment(), "修改为",
                    produce.getProduceMugong(), produce.getProduceMugongComment()));
        } else if (produce.getProduceYoufang() != null && produce.getProduceYoufang() >= 0) {
            //修正油房
            update.setProduceYoufang(produce.getProduceYoufang());
            update.setProduceYoufangComment(commentAppend(produceSource.getProduceYoufangComment(), "修改为",
                    produce.getProduceYoufang(), produce.getProduceYoufangComment()));
        } else if (produce.getProduceBaozhuang() != null && produce.getProduceBaozhuang() >= 0) {
            //修正包装
            update.setProduceBaozhuang(produce.getProduceBaozhuang());
            update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "修改为",
                    produce.getProduceBaozhuang(), produce.getProduceBaozhuangComment()));
        } else if (produce.getProduceTeding() != null && produce.getProduceTeding() >= 0) {
            //修正特定
            update.setProduceTeding(produce.getProduceTeding());
            update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "修改为",
                    produce.getProduceTeding(), produce.getProduceTedingComment()));
        } else if (produce.getProduceBeijing() != null && produce.getProduceBeijing() >= 0) {
            //修正北京
            update.setProduceBeijing(produce.getProduceBeijing());
            update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "修改为",
                    produce.getProduceBeijing(), produce.getProduceBeijingComment()));
        } else if (produce.getProduceBeijingteding() != null && produce.getProduceBeijingteding() >= 0) {
            //修正北京特定
            update.setProduceBeijingteding(produce.getProduceBeijingteding());
            update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "修改为",
                    produce.getProduceBeijingteding(), produce.getProduceBeijingtedingComment()));
        } else if (produce.getProduceBendihetong() != null && produce.getProduceBendihetong() >= 0) {
            //修正本地合同
            update.setProduceBendihetong(produce.getProduceBendihetong());
            update.setProduceBendihetongComment(commentAppend(produceSource.getProduceBendihetongComment(), "修改为",
                    produce.getProduceBendihetong(), produce.getProduceBendihetongComment()));
        } else if (produce.getProduceWaidihetong() != null && produce.getProduceWaidihetong() >= 0) {
            //修正外地合同
            update.setProduceWaidihetong(produce.getProduceWaidihetong());
            update.setProduceWaidihetongComment(commentAppend(produceSource.getProduceWaidihetongComment(), "修改为",
                    produce.getProduceWaidihetong(), produce.getProduceWaidihetongComment()));
        } else if (produce.getProduceDeng() != null && produce.getProduceDeng() >= 0) {
            //修正等待
            update.setProduceDeng(produce.getProduceDeng());
            update.setProduceDengComment(commentAppend(produceSource.getProduceDengComment(), "修改为",
                    produce.getProduceDeng(), produce.getProduceDengComment()));
        } else {
            return ResponseDTO.isError("参数值错误");
        }
        //更新到数据库
        try {
            UpdateUtils.copyNullProperties(produceSource, update);
            log.info("[updateProduce] update = {}", update);
            produceRepository.save(update);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteProduceBatch(List<Integer> produceIds) throws ParameterException, GeneralException {
        if (CollectionUtils.isEmpty(produceIds)) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage() + "-produceIds: is empty");
        }
        Produce temp = produceRepository.findByProduceId(produceIds.get(0));
        if (temp != null) {
            LocalDate now = LocalDate.now();
            LocalDate tempDate = LocalDate.of(temp.getProduceYear(), temp.getProduceMonth(), temp.getProduceDay());
            if (tempDate.isBefore(now)) {
                return ResponseDTO.isError("不能删除过去日期的数据");
            }
        }
        try {
            log.info("[deleteProduceBatch] produceIds = {}", produceIds.toString());
            produceRepository.deleteAllByProduceIdIn(produceIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage() + "-ID:" + produceIds.toString());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = GeneralException.class)
    public ResponseDTO deleteAllByDate(Integer year, Integer month, Integer day) throws ParameterException, GeneralException {
        if (year == null || month == null || day == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        LocalDate now = LocalDate.now();
        LocalDate temp = LocalDate.of(year, month, day);
        if (temp.isBefore(now)) {
            return ResponseDTO.isError("不能删除过去日期的数据");
        }
        try {
            produceRepository.deleteAllByProduceYearAndAndProduceMonthAndProduceDay(year, month, day);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return ResponseDTO.isSuccess();
    }

    @Override
    public ResponseDTO listProduce(Integer pageNum, Integer pageSize, Integer year, Integer month, Integer day, String productName) throws ParameterException {
        if (pageNum == null || pageSize == null || year == null || month == null || day == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.ASC, "produceProductName");
        Page<Produce> producePage;
        if (StringUtils.isBlank(productName)) {
            producePage = produceRepository.findAllByProduceYearAndProduceMonthAndProduceDay(year, month, day, pageable);
        } else {
            producePage = produceRepository.findAllByProduceYearAndProduceMonthAndProduceDayAndProduceProductNameContaining(year, month, day, productName, pageable);
        }
        return ResponseDTO.isSuccess(producePage);
    }

    @Override
    public ResponseDTO getDetails(Integer produceId) throws ParameterException {
        if (produceId == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Produce produce = produceRepository.findByProduceId(produceId);
        if (produce == null) {
            return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produceId);
        }
        return ResponseDTO.isSuccess(produce);
    }

    @Override
    public ResponseDTO getProduceTotal(Integer year, Integer month, Integer day) {
        if (year == null || month == null || day == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<Produce> produceList = produceRepository.findAllByProduceYearAndProduceMonthAndProduceDay(year, month, day);
        Produce total = getTotal(produceList);
        return ResponseDTO.isSuccess(total);
    }

    private Produce getTotal(List<Produce> produceList) {
        //计算属性值合计
        return produceList.stream()
                .reduce((x, y) ->
                        new Produce()
                                .setProduceXiadan(x.getProduceXiadan() + y.getProduceXiadan())
                                .setProduceMugong(x.getProduceMugong() + y.getProduceMugong())
                                .setProduceYoufang(x.getProduceYoufang() + y.getProduceYoufang())
                                .setProduceBaozhuang(x.getProduceBaozhuang() + y.getProduceBaozhuang())
                                .setProduceTeding(x.getProduceTeding() + y.getProduceTeding())
                                .setProduceBeijing(x.getProduceBeijing() + y.getProduceBeijing())
                                .setProduceBeijingteding(x.getProduceBeijingteding() + y.getProduceBeijingteding())
                                .setProduceBendihetong(x.getProduceBendihetong() + y.getProduceBendihetong())
                                .setProduceWaidihetong(x.getProduceWaidihetong() + y.getProduceWaidihetong())
                                .setProduceDeng(x.getProduceDeng() + y.getProduceDeng())
                )
                .orElse(new Produce()
                        .setProduceXiadan(0)
                        .setProduceMugong(0)
                        .setProduceYoufang(0)
                        .setProduceBaozhuang(0)
                        .setProduceTeding(0)
                        .setProduceBeijing(0)
                        .setProduceBeijingteding(0)
                        .setProduceBendihetong(0)
                        .setProduceWaidihetong(0)
                        .setProduceDeng(0))
                .setProduceProductName("合计");
    }

    private String commentAppend(String origin, String str, Integer num, String newComment) {
        if (StringUtils.isBlank(newComment)) {
            return origin;
        }
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(time.format(formatter))
                .append("#")
                .append(str)
                .append(num)
                .append(",")
                .append(newComment);
        if (StringUtils.isNotBlank(origin)) {
            return sb.append("###").append(origin).toString();
        }
        return sb.toString();
    }

    private Output getOutputSource(Integer year, Integer month, Integer productId, String productName) {
        boolean isOutputExist = outputRepository.existsByOutputYearAndOutputMonthAndOutputProductId(year, month, productId);
        if (!isOutputExist) {
            Output insert = new Output();
            insert.setOutputYear(year);
            insert.setOutputMonth(month);
            insert.setOutputProductId(productId);
            insert.setOutputProductName(productName);
            try {
                outputRepository.save(insert);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new GeneralException(ResultEnum.ADD_ERROR.getMessage() + "-产值:" + productName);
            }
        }
        return outputRepository.findByOutputYearAndOutputMonthAndOutputProductId(year, month, productId);
    }

    private ResponseDTO updateXiaDan(Produce param, Produce produceSource, Produce update) {
        //下单增加
        if (param.getProduceXiadan() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        }
        if (param.getProduceXiadan() + produceSource.getProduceXiadan() < 0) {
            return ResponseDTO.isError("更新后，下单值为负数");
        }
        update.setProduceXiadan(param.getProduceXiadan() + produceSource.getProduceXiadan());
        update.setProduceXiadanComment(commentAppend(produceSource.getProduceXiadanComment(), "",
                param.getProduceXiadan(), param.getProduceXiadanComment()));
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateMuGong(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：木工增加，下单减少
        //产值：下单增加
        if (param.getProduceMugong() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceMugong() > produceSource.getProduceXiadan()) {
            return ResponseDTO.isError("下单库存不足");
        } else if (param.getProduceMugong() + produceSource.getProduceMugong() < 0) {
            return ResponseDTO.isError("退单超过木工库存");
        }
        update.setProduceMugong(param.getProduceMugong() + produceSource.getProduceMugong());
        update.setProduceMugongComment(commentAppend(produceSource.getProduceMugongComment(), "",
                param.getProduceMugong(), param.getProduceMugongComment()));
        produceSource.setProduceXiadan(produceSource.getProduceXiadan() - param.getProduceMugong());
        outputSource.setOutputXiadan(outputSource.getOutputXiadan() + param.getProduceMugong());
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateYouFang(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：油房增加，木工减少
        //产值：木工增加
        if (param.getProduceYoufang() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceYoufang() > produceSource.getProduceMugong()) {
            return ResponseDTO.isError("木工库存不足");
        } else if (param.getProduceYoufang() + produceSource.getProduceYoufang() < 0) {
            return ResponseDTO.isError("退单超过油房库存");
        }
        update.setProduceYoufang(param.getProduceYoufang() + produceSource.getProduceYoufang());
        update.setProduceYoufangComment(commentAppend(produceSource.getProduceYoufangComment(), "",
                param.getProduceYoufang(), param.getProduceYoufangComment()));
        produceSource.setProduceMugong(produceSource.getProduceMugong() - param.getProduceYoufang());
        outputSource.setOutputMugong(outputSource.getOutputMugong() + param.getProduceYoufang());
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateBaoZhuang(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：包装增加，油房减少
        //产值：油房增加
        if (param.getProduceBaozhuang() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceBaozhuang() > produceSource.getProduceYoufang()) {
            return ResponseDTO.isError("油房库存不足");
        } else if (param.getProduceBaozhuang() + produceSource.getProduceBaozhuang() < 0) {
            return ResponseDTO.isError("退单量超过包装库存");
        }
        update.setProduceBaozhuang(param.getProduceBaozhuang() + produceSource.getProduceBaozhuang());
        update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "",
                param.getProduceBaozhuang(), param.getProduceBaozhuangComment()));
        produceSource.setProduceYoufang(produceSource.getProduceYoufang() - param.getProduceBaozhuang());
        outputSource.setOutputYoufang(outputSource.getOutputYoufang() + param.getProduceBaozhuang());
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateTeDing(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：特定增加，油房减少
        //产值：油房增加
        if (param.getProduceTeding() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceTeding() > produceSource.getProduceYoufang()) {
            return ResponseDTO.isError("油房库存不足");
        } else if (param.getProduceTeding() + produceSource.getProduceTeding() < 0) {
            return ResponseDTO.isError("退单量超过特定库存");
        }
        update.setProduceTeding(param.getProduceTeding() + produceSource.getProduceTeding());
        update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "",
                param.getProduceTeding(), param.getProduceTedingComment()));
        produceSource.setProduceYoufang(produceSource.getProduceYoufang() - param.getProduceTeding());
        outputSource.setOutputYoufang(outputSource.getOutputYoufang() + param.getProduceTeding());
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateBeiJing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        //进度：北京增加，包装减少
        //产值：包装增加
        if (param.getProduceBeijing() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        }

        // flag 判断更新是否为北京出货
        if (flag == Constants.NOT_OUTPUT) {
            if (param.getProduceBeijing() > produceSource.getProduceBaozhuang()) {
                return ResponseDTO.isError("包装库存不足");
            } else if (param.getProduceBeijing() + produceSource.getProduceBeijing() < 0) {
                return ResponseDTO.isError("退单量超过包装库存");
            }
            update.setProduceBeijing(param.getProduceBeijing() + produceSource.getProduceBeijing());
            produceSource.setProduceBaozhuang(produceSource.getProduceBaozhuang() - param.getProduceBeijing());
            outputSource.setOutputBaozhuang(outputSource.getOutputBaozhuang() + param.getProduceBeijing());

            Product product = productRepository.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produceSource.getProduceProductId());
            }
            outputSource.setOutputBaozhuangTotalPrice(outputSource.getOutputBaozhuang() * product.getProductPrice());
        } else {
            //出货->进度：北京减少。产值：北京增加
            if (param.getProduceBeijing() > produceSource.getProduceBeijing()) {
                return ResponseDTO.isError("北京库存不足");
            }
            update.setProduceBeijing(produceSource.getProduceBeijing() - param.getProduceBeijing());
            outputSource.setOutputBeijing(outputSource.getOutputBeijing() + param.getProduceBeijing());
        }
        update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "",
                produceSource.getProduceBeijing(), param.getProduceBeijingComment()));

        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateBeiJingTeDing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        //进度：北京特定增加，特定减少
        //产值：特定增加
        if (param.getProduceBeijingteding() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        }
        //判断是否为北京特定出货
        if (flag == Constants.NOT_OUTPUT) {
            if (param.getProduceBeijingteding() > produceSource.getProduceTeding()) {
                return ResponseDTO.isError("特定库存不足");
            } else if (param.getProduceBeijingteding() + produceSource.getProduceBeijingteding() < 0) {
                return ResponseDTO.isError("退单量超过北京特定库存");
            }
            update.setProduceBeijingteding(param.getProduceBeijingteding() + produceSource.getProduceBeijingteding());
            produceSource.setProduceTeding(produceSource.getProduceTeding() - param.getProduceBeijingteding());
            outputSource.setOutputTeding(outputSource.getOutputTeding() + param.getProduceBeijingteding());

            Product product = productRepository.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return ResponseDTO.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produceSource.getProduceProductId());
            }
            outputSource.setOutputTedingTotalPrice(outputSource.getOutputTeding() * product.getProductPrice());
        } else {
            //出货->进度：北京特定减少。产值：北京特定增加
            if (param.getProduceBeijingteding() > produceSource.getProduceBeijingteding()) {
                return ResponseDTO.isError("北京特定库存不足");
            } else {
                update.setProduceBeijingteding(produceSource.getProduceBeijingteding() - param.getProduceBeijingteding());
                outputSource.setOutputBeijingteding(outputSource.getOutputBeijingteding() + param.getProduceBeijingteding());
            }
        }
        update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "",
                produceSource.getProduceBeijingteding(), param.getProduceBeijingtedingComment()));

        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateBenDiHeTong(Produce param, Produce produceSource, Produce update) {
        //进度：本地合同自己增加，减少
        //产值：没有变化
        if (param.getProduceBendihetong() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceBendihetong() + produceSource.getProduceBendihetong() < 0) {
            return ResponseDTO.isError("退单量超过已有本地合同量");
        }
        update.setProduceBendihetong(param.getProduceBendihetong() + produceSource.getProduceBendihetong());
        update.setProduceBendihetongComment(commentAppend(produceSource.getProduceBendihetongComment(), "",
                param.getProduceBendihetong(), param.getProduceBendihetongComment()));
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateWaiDiHeTong(Produce param, Produce produceSource, Produce update) {
        //进度：外地合同自己增加，减少
        //产值：没有变化
        if (param.getProduceWaidihetong() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceWaidihetong() + produceSource.getProduceWaidihetong() < 0) {
            return ResponseDTO.isError("退单量超过已有外地合同量");
        }
        update.setProduceWaidihetong(param.getProduceWaidihetong() + produceSource.getProduceWaidihetong());
        update.setProduceWaidihetongComment(commentAppend(produceSource.getProduceWaidihetongComment(), "",
                param.getProduceWaidihetong(), param.getProduceWaidihetongComment()));
        return ResponseDTO.isSuccess();
    }

    private ResponseDTO updateDeng(Produce param, Produce produceSource, Produce update) {
        //进度：等待增加，减少
        //产值：没有变化
        if (param.getProduceDeng() == 0) {
            return ResponseDTO.isError("更新值不能为 0 ");
        } else if (param.getProduceDeng() + produceSource.getProduceDeng() < 0) {
            return ResponseDTO.isError("退单量超过已有等待量");
        }
        update.setProduceDeng(param.getProduceDeng() + produceSource.getProduceDeng());
        update.setProduceDengComment(commentAppend(produceSource.getProduceDengComment(), "",
                param.getProduceDeng(), param.getProduceDengComment()));
        return ResponseDTO.isSuccess();
    }
}
