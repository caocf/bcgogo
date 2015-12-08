package com.bcgogo.config.service.excelexport;


import com.bcgogo.config.dto.ExportFileDTO;
import com.bcgogo.config.dto.ExportRecordDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IExportService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 导出组件调用入口
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-5
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */

@Component
public class BcgogoExcelDataExporter {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoExcelDataExporter.class);
    /**
     * 导出方法，对外提供调用
     * @param bcgogoExportDataIterator  数据迭代器实例，由调用者指定
     * @param version   导出的excel文件版本，2003代表2003及以前的版本；2007代表2007及以后的版本
                * @throws com.bcgogo.config.service.excelexport.ExcelExportException
                */
     public ExportResult export(BcgogoExportDataIterator bcgogoExportDataIterator, String version, ExportRecordDTO exportRecordDTO) throws ExcelExportException{
            ExportResult exportResult = new ExportResult();
            //用于存放文件的路径
            String path = ServiceManager.getService(IConfigService.class).getConfig("ExportFileDir", ShopConstant.BC_SHOP_ID);
            BcgogoExcelAssembler bcgogoExcelAssembler = new BcgogoExcelAssembler(bcgogoExportDataIterator, version);
            List<ExportFileDTO> exportFileDTOList = new ArrayList<ExportFileDTO>();
            for(int i = 0 ; i < bcgogoExportDataIterator.getNumOfExportExcel() ; i++) {
              Workbook workbook =  bcgogoExcelAssembler.assembleWorkbook();
              //创建导出文件对象
              ExportFileDTO exportFileDTO = new ExportFileDTO();
              exportFileDTO.setShopId(exportRecordDTO.getShopId());
              exportFileDTO.setFileName(ServiceManager.getService(IExportService.class).generateExportFileName(exportRecordDTO.getScene()));
              exportFileDTO.setStatus("WAITTING");
              if(workbook == null){
                throw new ExcelExportException("封装workbook失败！");
              }
              BufferedOutputStream  bos = null;
              try {
                //将excel文件保存到磁盘上
                bos = new BufferedOutputStream(new FileOutputStream(path + exportFileDTO.getFileName()));
                workbook.write(bos);
                if(i == 0) {
                  //保存导出记录
                  ServiceManager.getService(IExportService.class).createExportRecord(exportRecordDTO);
                }
                //保存export_file
                exportFileDTO.setExportRecordId(exportRecordDTO.getId());
                ServiceManager.getService(IExportService.class).saveExportFile(exportFileDTO);
                exportFileDTOList.add(exportFileDTO);

              } catch (IOException e) {
                throw new ExcelExportException("封装workbook失败！", e);
              } finally {
                try {
                  if(bos != null){
                    bos.flush();
                    bos.close();
                  }
                } catch (Exception e) {
                   LOG.error(e.getMessage(),e);
                }

              }
            }
         //返回导出结果
         exportResult.setExportFileDTOList(exportFileDTOList);
         exportResult.setExportScene(exportRecordDTO.getScene());
         exportResult.setFileNum(bcgogoExportDataIterator.getNumOfExportExcel());
         exportResult.setTotalNum(bcgogoExportDataIterator.getTotalRows());
         exportResult.setTotalRowsPerExcel(bcgogoExportDataIterator.getTotalRowsPerExcel());
        return exportResult;
    }

}
