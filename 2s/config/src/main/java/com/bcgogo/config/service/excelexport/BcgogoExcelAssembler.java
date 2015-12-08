package com.bcgogo.config.service.excelexport;

import com.bcgogo.constant.ImportConstants;
import com.bcgogo.utils.CollectionUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 导出文件封装类，组件内部使用，每一次调用都会产生一个实例
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-5
 * Time: 上午10:32
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoExcelAssembler {

    /**
     * 数据迭代器负责提供导出数据的读取
     */
    private BcgogoExportDataIterator bcgogoExportDataIterator;

    /**
     * 导出excel的版本号
     */
    private String version;

    /**
     * 调用者使用本构造函数初始化BcgogoExcelAssembler实例
     * @param bcgogoExportDataIterator
     * @param version
     */
    public BcgogoExcelAssembler( BcgogoExportDataIterator bcgogoExportDataIterator, String version){
        setBcgogoExportDataIterator(bcgogoExportDataIterator);
        setVersion(version);
    }

    /**
     * 封装Workbook，即一个excel文件对象
     * @return
     * @throws ExcelExportException
     */
    public Workbook assembleWorkbook() throws ExcelExportException{
        Workbook workbook = null;
        if(ImportConstants.ExcelVersion.EXCEL_VERSION_2003.equals(this.version)){
            workbook = new HSSFWorkbook();
        }else{
            workbook = new XSSFWorkbook();
        }
        assembleSheet(workbook.createSheet());
        return workbook;

    }

    /**
     * 封装一个excel标签页，内部使用
     * 使用数据迭代器循环获取数据，并将每个元数据封装成一个excel行
     * 此处指要求数据迭代器统一提供List<List<String>>格式的数据
     * @param sheet
     * @throws ExcelExportException
     */
    private void assembleSheet(Sheet sheet) throws ExcelExportException{
        int i = 0;
        //如果在表头之上有显示信息，需要先显示
        if(CollectionUtil.isNotEmpty(bcgogoExportDataIterator.getHeadShowInfo())) {
          assembleRow(sheet.createRow(i), bcgogoExportDataIterator.getHeadShowInfo());
          i++;
        }
        //如果有表头，需要先封装表头
        if(bcgogoExportDataIterator.getHead() != null){
            assembleRow(sheet.createRow(i), bcgogoExportDataIterator.getHead());
            i ++;
        }
        List<List<String>> rows = null;
        //封装数据
        while(this.bcgogoExportDataIterator.hasNext()){
            //如果超过了允许的最大行数，则不再进行装配数据
            if(i >= bcgogoExportDataIterator.getTotalRowsPerExcel()) {
              break;
            }
            rows = (List<List<String>>) bcgogoExportDataIterator.next();
            if(rows == null) {
                continue;
            }
            for(List<String> row : rows){
                if(row == null){
                    continue;
                }
                assembleRow(sheet.createRow(i), row);
                i ++;
            }
        }
        if(CollectionUtil.isNotEmpty(bcgogoExportDataIterator.getTailShowInfo())) {
          assembleRow(sheet.createRow(i), bcgogoExportDataIterator.getTailShowInfo());
        }
    }

    /**
     * 封装一个excel行
     * @param row
     * @param values
     * @throws ExcelExportException
     */
    private void assembleRow( Row row, List<String> values) throws ExcelExportException{
        if(values == null || values.isEmpty()){
            return ;
        }
        int i = 0;
        for(String value : values){
            assembleCell(row.createCell(i), value);
            i ++;
        }
    }

    /**
     * 封装一个excel单元格
     * @param cell
     * @param value
     * @throws ExcelExportException
     */
    private void assembleCell(Cell cell, String value) throws ExcelExportException{
        //TODO 可修改单元格风格
        cell.setCellValue(value);

    }


    public BcgogoExportDataIterator getBcgogoExportDataIterator() {
        return bcgogoExportDataIterator;
    }

    public void setBcgogoExportDataIterator(BcgogoExportDataIterator bcgogoExportDataIterator) {
        this.bcgogoExportDataIterator = bcgogoExportDataIterator;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
