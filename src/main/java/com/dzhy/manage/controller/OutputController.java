package com.dzhy.manage.controller;

import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.exception.ParameterException;
import com.dzhy.manage.service.OutputService;
import com.dzhy.manage.util.ExcelUtils;
import com.dzhy.manage.dto.ResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * @ClassName OutputController
 * @Description 产值 controller
 * @Author alex
 * @Date 2018/11/2
 **/
@RestController
@RequestMapping("/output")
@Api(value = "产值", description = "产值管理")
@Slf4j
public class OutputController {
    private final OutputService iOutputService;

    public OutputController(OutputService iOutputService) {
        this.iOutputService = iOutputService;
    }

    @ApiOperation(value = "错误修正", notes = "错误修正，修正后数据库里的值改变为输入值")
    @ApiImplicitParam(name = "output", value = "产值实体类", required = true, dataTypeClass = Output.class)
    @PutMapping()
    public ResponseDTO changeOutput(@RequestBody Output output) {
        return iOutputService.changeOutput(output);
    }

    @ApiOperation(value = "列表", notes = "获取产值列表，分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class)
    })
    @GetMapping()
    public ResponseDTO listOutput(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "year") Integer year,
                                  @RequestParam(value = "month") Integer month,
                                  @RequestParam(value = "productName") String productName) {
        return iOutputService.listOutput(pageNum, pageSize, year, month, productName);
    }

    @ApiOperation(value = "导出", notes = "手动导出 Excel 文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class)
    })
    @GetMapping("/export")
    public void exportExcel(@RequestParam(value = "year") Integer year,
                            @RequestParam(value = "month") Integer month,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        if (year == null || month == null) {
            throw new ParameterException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        LocalDate date = LocalDate.of(year, month, 1);
        String fileName = date.toString().replace("-", "").substring(0, 6) + Constants.OUTPUT_TITLE + ExcelUtils.EXCEL_2007U;
        String encoderFileName;
        encoderFileName = getEncoderFileName(request, fileName);

        response.setContentType("multipart/form-data;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; fileName=" + encoderFileName);

        iOutputService.exportExcel(year, month, response.getOutputStream());
    }

    static String getEncoderFileName(HttpServletRequest request, String fileName) {
        String encoderFileName;
        try {
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                encoderFileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                encoderFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new GeneralException("编码错误");
        }
        return encoderFileName;
    }
}
