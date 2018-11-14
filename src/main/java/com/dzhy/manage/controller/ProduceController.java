package com.dzhy.manage.controller;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.service.ProduceService;
import com.dzhy.manage.util.ExcelUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName ProduceController
 * @Description 生产进度 controller
 * @Author alex
 * @Date 2018/10/31
 **/
@RestController
@RequestMapping("/produce")
@Api(value = "生产进度", description = "生产进度管理")
@Slf4j
public class ProduceController {

    private final ProduceService iProduceService;

    @Autowired
    public ProduceController(ProduceService iProduceService) {
        this.iProduceService = iProduceService;
    }

    @ApiOperation(value = "添加", notes = "添加新的生产进度")
    @ApiImplicitParam(name = "produce", value = "生产进度实体类", required = true, dataTypeClass = Produce.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PostMapping()
    public ResponseDTO addProduce(@RequestBody Produce produce) {
        return iProduceService.addProduce(produce);
    }

    @ApiOperation(value = "导入", notes = "数据源：Excel 文件。以导出的 Excel 文件为模版，导入生产进度")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PostMapping(value = "/import/excel", headers = "content-type=multipart/form-data")
    public ResponseDTO importFromExcel(@ApiParam(value = "文件", required = true) MultipartFile multipartFile) throws Exception {
        return iProduceService.importFromExcel(multipartFile);
    }

    @ApiOperation(value = "导入", notes = "数据源：数据库。选择要导入的日期，将选择日期的内容，导入为今天的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PostMapping("/import/db")
    public ResponseDTO importFromDB(@RequestParam(value = "year") Integer year,
                                    @RequestParam(value = "month") Integer month,
                                    @RequestParam(value = "day") Integer day) {
        return iProduceService.importFromDB(year, month, day);
    }

    @ApiOperation(value = "导出", notes = "手动导出 Excel 文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/export")
    public void exportExcel(@RequestParam(value = "year") Integer year,
                            @RequestParam(value = "month") Integer month,
                            @RequestParam(value = "day") Integer day,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        if (year == null || month == null || day == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        LocalDate date = LocalDate.of(year, month, day);
        String fileName = date.toString().replace("-", "") + Constants.PRODUCE_TITLE + ExcelUtils.EXCEL_2007U;
        String encoderFileName = OutputController.getEncoderFileName(request, fileName);

        response.setContentType("multipart/form-data;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; fileName=" + encoderFileName);

        iProduceService.exportExcel(year, month, day, response.getOutputStream());
    }

    @ApiOperation(value = "更新", notes = "生产进度更新，更新后数据库新值为 原始数据库值 + 输入的值")
    @ApiImplicitParam(name = "produce", value = "生产进度实体类", required = true, dataTypeClass = Produce.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PutMapping()
    public ResponseDTO updateProduce(@RequestBody Produce produce) {
        return iProduceService.updateProduce(produce, Constants.NOT_OUTPUT);
    }

    @ApiOperation(value = "出货", notes = "北京和北京特定出货")
    @ApiImplicitParam(name = "produce", value = "生产进度实体类", required = true, dataTypeClass = Produce.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PutMapping("/output")
    public ResponseDTO isOutput(@RequestBody Produce produce) {
        return iProduceService.updateProduce(produce, Constants.IS_OUTPUT);
    }

    @ApiOperation(value = "错误修正", notes = "错误修正，修正后数据库里的值改变为输入值")
    @ApiImplicitParam(name = "produce", value = "生产进度实体类", required = true, dataTypeClass = Produce.class)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @PutMapping("/change")
    public ResponseDTO changeProduce(@RequestBody Produce produce) {
        return iProduceService.changeProduce(produce);
    }

    @ApiOperation(value = "删除", notes = "单个删除、批量删除")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @DeleteMapping()
    public ResponseDTO deleteProduceBatch(@RequestParam("produceIds[]") List<Integer> produceIds) {
        return iProduceService.deleteProduceBatch(produceIds);
    }

    @ApiOperation(value = "删除", notes = "清空选定日期的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR')")
    @DeleteMapping("/all")
    public ResponseDTO deleteAllByDate(@RequestParam(value = "year") Integer year,
                                       @RequestParam(value = "month") Integer month,
                                       @RequestParam(value = "day") Integer day) {
        return iProduceService.deleteAllByDate(year, month, day);
    }

    @ApiOperation(value = "列表", notes = "获取生产进度列表，分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'USER')")
    @GetMapping()
    public ResponseDTO listProduce(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "year") Integer year,
                                   @RequestParam(value = "month") Integer month,
                                   @RequestParam(value = "day") Integer day,
                                   @RequestParam(value = "productName") String productName) {
        return iProduceService.listProduce(pageNum, pageSize, year, month, day, productName);
    }

    @ApiOperation(value = "详情", notes = "获取生产进度详情")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/{produceId}")
    public ResponseDTO getDetails(@PathVariable("produceId") Integer produceId) {
        return iProduceService.getDetails(produceId);
    }
}
