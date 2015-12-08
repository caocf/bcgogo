package com.bcgogo.pojo.util.excel;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-7-22
 * Time: 上午9:04
 */

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @author Hongten</br>
 *         <p/>
 *         参考地址：http://hao0610.iteye.com/blog/1160678
 */
public class ExcelParser {

  public static void main(String[] args) throws IOException {
    String path = "C:\\Users\\Administrator\\Desktop\\UserList_STAT.xls";
    readExcel(path);
  }

  /**
   * 读取xls文件内容
   *
   * @return List<XlsDto>对象
   * @throws IOException 输入/输出(i/o)异常
   */
  public static List<String> readExcel03(String path) throws IOException {
    InputStream is = new FileInputStream(path);
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
    List<String> list = new ArrayList<String>();
    // 循环工作表Sheet
    for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
      if (hssfSheet == null) {
        continue;
      }
      // 循环行Row
      for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
        HSSFRow hssfRow = hssfSheet.getRow(rowNum);
        if (hssfRow == null) {
          continue;
        }
        // 循环列Cell
        // 0学号 1姓名 2学院 3课程名 4 成绩
        // for (int cellNum = 0; cellNum <=4; cellNum++) {
        HSSFCell cell = hssfRow.getCell(0);
        if (cell == null) {
          continue;
        }
        list.add(getValue(cell));
      }
    }
    return list;
  }

  public static List<String> readExcel(String path) throws IOException {
     InputStream inputStream = new FileInputStream(path);
    // 构造 XSSFWorkbook 对象，strPath 传入文件路径
    XSSFWorkbook xwb = new XSSFWorkbook(inputStream);
// 读取第一章表格内容
    XSSFSheet sheet = xwb.getSheetAt(0);
// 定义 row、cell
    XSSFRow row;
    String cell;
    List<String> list = new ArrayList<String>();
// 循环输出表格中的内容
    for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
      row = sheet.getRow(i);
      for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {
        // 通过 row.getCell(j).toString() 获取单元格内容，
        cell = row.getCell(j).toString();
        System.out.print(cell + "\t");
      }
      System.out.println("");
    }
    return list;
  }

  /**
   * 得到Excel表中的值
   *
   * @param hssfCell Excel中的每一个格子
   * @return Excel中每一个格子中的值
   */
  @SuppressWarnings("static-access")
  private static String getValue(HSSFCell hssfCell) {
    if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
      // 返回布尔类型的值
      return String.valueOf(hssfCell.getBooleanCellValue());
    } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
      // 返回数值类型的值
      return String.valueOf(hssfCell.getNumericCellValue());
    } else {
      // 返回字符串类型的值
      return String.valueOf(hssfCell.getStringCellValue());
    }
  }

}