package com.bcgogo.config.service.excelimport;

import com.bcgogo.constant.ImportConstants;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * excel解析器，用来解析excel文件中的内容，并转换成java数据对象
 * 同时支持2003和2007版本
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-31
 * Time: 上午11:31
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BcgogoExcelParser {

  private static final Logger LOG = LoggerFactory.getLogger(BcgogoExcelParser.class);

  /**
   * 从excel文件的输入流中读取数据并解析成List<Map<String, String>>类型
   *
   * @param data
   * @return
   */
  public List<Map<String, Object>> parseData(byte[] data, String version) {
    InputStream inputStream = new ByteArrayInputStream(data);
    List<String> head = parseHead(data, version);
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    try {
      if (inputStream == null) {
        return null;
      }
      Workbook workbook = null;
      if (ImportConstants.ExcelVersion.EXCEL_VERSION_2007.equals(version)) {
        workbook = new XSSFWorkbook(inputStream);
      } else {
        workbook = new HSSFWorkbook(inputStream);
      }
      int sheetCount = workbook.getNumberOfSheets();
      if (sheetCount <= 0) {
        return dataList;
      }
      Sheet sheet = null;
      List<Map<String, Object>> sheetDataList = null;
      for (int index = 0; index < sheetCount; index++) {
        sheet = workbook.getSheetAt(index);
        sheetDataList = parseDataFromSheet(sheet, head);
        if (sheetDataList != null && !sheetDataList.isEmpty()) {
          for(Map record : sheetDataList){
            //过滤掉全部为空的数据行
            boolean allEmpty = true;
            Iterator iter = record.keySet().iterator();
            inner : while(iter.hasNext()){
              Object value = record.get(iter.next());
              if(value != null && StringUtils.isNotBlank(value.toString())){
                allEmpty = false;
                break inner;
              }
            }
            if(!allEmpty){
              dataList.add(record);
            }
          }
        }
      }
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return dataList;
  }

  /**
   * 解析一个sheet页面的数据
   *
   * @param sheet
   * @param head
   * @return
   */
  private List<Map<String, Object>> parseDataFromSheet(Sheet sheet, List<String> head) {
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    Iterator<Row> rowIterator = sheet.iterator();
    //跳过表头
    if(!rowIterator.hasNext()){
      return result;
    }
    rowIterator.next();
    Map<String, Object> rowData = null;
    while (rowIterator.hasNext()) {
      rowData = parseDataFromRow(rowIterator.next(), head);
      if (rowData != null) {
        result.add(rowData);
      }
    }
    return result;
  }

  /**
   * 解析一行数据
   *
   * @param row
   * @param head
   * @return
   */
  private Map<String, Object> parseDataFromRow(Row row, List<String> head) {

    for(int i = 0;i< row.getPhysicalNumberOfCells();i++){
      if(row.getCell(i) == null){
        row.createCell(i).setCellValue("");
      }
    }

    Iterator<String> fieldIterator = head.iterator();
    Map<String, Object> rowData = new HashMap<String, Object>();
    Iterator<Cell> cellIterator = row.iterator();
    Cell cell = null;
    while (cellIterator.hasNext() && fieldIterator.hasNext()) {
      cell = cellIterator.next();
      rowData.put(fieldIterator.next(), parseDataFromCell(cell));
    }
    return rowData;
  }

  /**
   * 解析一个单元格的内容
   *
   * @param cell
   * @return
   */
  private Object parseDataFromCell(Cell cell) {
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_BLANK:
        return StringUtil.EMPTY_STRING;
      case Cell.CELL_TYPE_BOOLEAN:
        return StringUtil.EMPTY_STRING + cell.getBooleanCellValue();
      case Cell.CELL_TYPE_ERROR:
        return StringUtil.EMPTY_STRING + cell.getErrorCellValue();
      case Cell.CELL_TYPE_FORMULA:
        return cell.getCellFormula();
      case Cell.CELL_TYPE_NUMERIC:
        return cell.getNumericCellValue();
      case Cell.CELL_TYPE_STRING:
        return cell.getStringCellValue();
      default:
        return StringUtil.EMPTY_STRING;
    }
  }

  /**
   * 从excel文件的输入流中读取头部信息并解析成List<String>类型
   *
   * @param data
   * @return
   */
  public List<String> parseHead(byte[] data, String version) {
    InputStream inputStream = new ByteArrayInputStream(data);
    List<String> result = new ArrayList<String>();
    try {
      if (inputStream == null) {
        return null;
      }
      Workbook workbook = null;
      if (ImportConstants.ExcelVersion.EXCEL_VERSION_2007.equals(version)) {
        workbook = new XSSFWorkbook(inputStream);
      } else {
        workbook = new HSSFWorkbook(inputStream);
      }
      int sheetCount = workbook.getNumberOfSheets();

      if (sheetCount <= 0) {
        return null;
      }
      Sheet sheet = workbook.getSheetAt(0);
      if (sheet == null) {
        return null;
      }
      Iterator<Row> rowIterator = sheet.iterator();
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.iterator();
      Cell cell = null;
      while (cellIterator.hasNext()) {
        cell = cellIterator.next();
        result.add(String.valueOf(parseDataFromCell(cell)));
      }

    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

}
