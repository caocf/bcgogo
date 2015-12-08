package com.bcgogo.config.service;

import com.bcgogo.config.dto.ExportFileDTO;
import com.bcgogo.config.dto.ExportRecordDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ExportFile;
import com.bcgogo.config.model.ExportRecord;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.ShopConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-17
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ExportService implements IExportService {
    @Override
    public ExportRecordDTO createExportRecord(ExportRecordDTO exportRecordDTO) {
        if(exportRecordDTO == null){
            return exportRecordDTO;
        }
        ConfigWriter writer = configDaoManager.getWriter();
        ExportRecord exportRecord = new ExportRecord(exportRecordDTO);
        Object status = writer.begin();
        try{
            writer.save(exportRecord);
            writer.commit(status);
            exportRecordDTO.setId(exportRecord.getId());
        }finally {
            writer.rollback(status);
        }

        return exportRecordDTO;
    }

    @Override
    public String generateExportFileName(String scene) {
       return  scene + "-" + UUID.randomUUID().toString() + ".xls";
    }



  @Override
  public ExportFileDTO saveExportFile(ExportFileDTO exportFileDTO) {
    if(exportFileDTO == null) {
      return exportFileDTO;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    ExportFile exportFile = new ExportFile(exportFileDTO);
    Object status = writer.begin();
    try{
      writer.save(exportFile);
      writer.commit(status);
      exportFileDTO.setId(exportFile.getId());
    }finally {
      writer.rollback(status);
    }
    return exportFileDTO;
  }

    @Override
    public ExportFileDTO getExportFileDTOById(Long exportFileId) {
        if(exportFileId == null) {
            return null;
        }
        ConfigWriter writer = configDaoManager.getWriter();
        ExportFile exportFile = writer.getById(ExportFile.class,exportFileId);
        if(exportFile == null) {
            return null;
        }
        return  exportFile.toDTO();
    }

    @Override
    public void updateExportFileById(Long exportFileId) {
        if(exportFileId == null) {
            return;
        }
        ConfigWriter writer = configDaoManager.getWriter();
        ExportFile exportFile = writer.getById(ExportFile.class,exportFileId);
        exportFile.setStatus("DOWNLOAD");
        Object status = writer.begin();
        try{
            writer.update(exportFile);
            writer.commit(status);
        }finally {
            writer.rollback(status);
        }
    }

    @Autowired
    private ConfigDaoManager configDaoManager;
}
