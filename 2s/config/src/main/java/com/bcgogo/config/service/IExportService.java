package com.bcgogo.config.service;

import com.bcgogo.config.dto.ExportFileDTO;
import com.bcgogo.config.dto.ExportRecordDTO;

/**
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-17
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public interface IExportService {

    public ExportRecordDTO createExportRecord(ExportRecordDTO exportRecordDTO);

    public ExportFileDTO saveExportFile(ExportFileDTO exportFileDTO);

    public String generateExportFileName(String scene);   //自动生成导出时的文件名，scene为导出的场景

    public ExportFileDTO getExportFileDTOById(Long exportFileId);

    public void updateExportFileById(Long exportFileId);
}
