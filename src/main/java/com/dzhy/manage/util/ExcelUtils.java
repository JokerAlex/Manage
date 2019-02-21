package com.dzhy.manage.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @ClassName ExcelUtils
 * @Description excel 读取，导出
 * @Author alex
 * @Date 2018/10/30
 **/
public class ExcelUtils {
    /**
     * 点
     */
    public static final String POINT = ".";

    /**
     * 2003- 版本的excel
     */
    public static final String EXCEL_2003L = ".xls";
    /**
     * 2007+ 版本的excel
     */
    public static final String EXCEL_2007U = ".xlsx";

    private Workbook getWorkbook(String fileName) throws Exception {
        Workbook wb = null;
        InputStream inStr = new FileInputStream(fileName);
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (EXCEL_2003L.equals(fileType)) {
            wb = new HSSFWorkbook(inStr);
        } else if (EXCEL_2007U.equals(fileType)) {
            wb = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    private static List<List<String>> read(Sheet sheet) {
        return StreamSupport.stream(sheet.spliterator(), true)
                .filter(Objects::nonNull)
                .filter(row -> row.getLastCellNum() != 1)
                .map(row -> StreamSupport.stream(row.spliterator(), false)
                        .map(cell -> {
                            String cellValue;
                            if (cell == null) {
                                return "";
                            } else {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        cellValue = cell.getStringCellValue().trim();
                                        cellValue = StringUtils.isBlank(cellValue) ? "" : cellValue;
                                        break;
                                    case BOOLEAN:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        cellValue = String.valueOf(cell.getCellFormula().trim());
                                        break;
                                    case NUMERIC:
                                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                            cellValue = DateUtil.parseYYYYMMDDDate(cell.getDateCellValue().toString()).toString();
                                        } else {
                                            cellValue = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                                        }
                                        break;
                                    case BLANK:
                                        cellValue = "";
                                        break;
                                    case ERROR:
                                        cellValue = "";
                                        break;
                                    default:
                                        cellValue = cell.toString().trim();
                                        break;
                                }
                            }
                            return cellValue.trim();
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public static List<Map<String, String>> readToMapList(InputStream inputStream) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        List<List<String>> listList = read(sheet);
        List<String> colName = listList.get(0);
        listList.remove(0);

        List<Map<String, String>> mapList = new ArrayList<>(listList.size());
        for (List<String> aListList : listList) {
            Map<String, String> row = new HashMap<>(colName.size());
            for (int j = 0; j < colName.size(); j++) {
                row.put(colName.get(j), aListList.get(j));
            }
            mapList.add(row);
        }
        return mapList;
    }

    public static List<List<String>> readExcelByInputStream(InputStream inputStream) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        return read(sheet);
    }

    public static void exportData(String title, List<String> headers, List<List<String>> list, OutputStream outputStream) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();

        //标题样式
        CellStyle cellStyleTitle = wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        Font font = wb.createFont();
        font.setFontName("黑体");
        //字号
        font.setFontHeightInPoints((short) 20);
        cellStyleTitle.setFont(font);
        //第一行合并
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, headers.size() - 1);
        sheet.addMergedRegion(region);
        Row row;
        Cell cell;
        //填充第0行数据标题
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleTitle);
        row.setHeightInPoints(40);

        // 开始填充表头和数据
        // 此处设置数据格式
        DataFormat df = wb.createDataFormat();
        //单元格样式-上下左右边框
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        //自动换行
        //cellStyle.setWrapText(true);
        //填充第1行表头
        row = sheet.createRow(1);
        for (int i = 0; i < headers.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(cellStyle);
        }
        //填充数据,从第2行开始
        for (int i = 0; i < list.size(); i++) {
            int rowSize = list.get(i).size();
            row = sheet.createRow(i + 2);
            sheet.setColumnWidth(i,11*256);
            for (int j = 0; j < rowSize; j++) {
                cell = row.createCell(j);
                String data = list.get(i).get(j);
                boolean isNum = false;
                boolean isInteger=false;
                boolean isPercent=false;
                if (!StringUtils.isBlank(data)) {
                    //判断data是否为数值型
                    isNum = data.matches("^(-?\\d+)(\\.\\d+)?$");
                    //判断data是否为整数（小数部分是否为0）
                    isInteger=data.matches("^[-\\+]?[\\d]*$");
                    //判断data是否为百分数（是否包含“%”）
                    isPercent=data.contains("%");
                }
                //如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
                if (isNum && !isPercent) {

                    if (isInteger) {
                        //数据格式只显示整数
                        cellStyle.setDataFormat(df.getFormat("#,#0"));
                    }else{
                        //保留两位小数点
                        cellStyle.setDataFormat(df.getFormat("#,##0.00"));
                    }
                    // 设置单元格格式
                    cell.setCellStyle(cellStyle);
                    // 设置单元格内容为double类型
                    cell.setCellValue(Double.parseDouble(data));
                } else {
                    cell.setCellStyle(cellStyle);
                    // 设置单元格内容为字符型
                    cell.setCellValue(data);
                }
            }
        }
        wb.write(outputStream);
    }
}
