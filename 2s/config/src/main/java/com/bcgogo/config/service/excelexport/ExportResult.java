package com.bcgogo.config.service.excelexport;

import com.bcgogo.config.dto.ExportFileDTO;

import javax.persistence.metamodel.ListAttribute;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-1
 * Time: 上午9:49
 * To change this template use File | Settings | File Templates.
 */
public class ExportResult {
    int totalNum;
    int totalRowsPerExcel;
    int fileNum;
    List<ExportFileDTO> exportFileDTOList;
    String exportScene;
    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalRowsPerExcel() {
        return totalRowsPerExcel;
    }

    public void setTotalRowsPerExcel(int totalRowsPerExcel) {
        this.totalRowsPerExcel = totalRowsPerExcel;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public String getExportScene() {
        return exportScene;
    }

    public void setExportScene(String exportScene) {
        this.exportScene = exportScene;
    }

    public List<ExportFileDTO> getExportFileDTOList() {
        return exportFileDTOList;
    }

    public void setExportFileDTOList(List<ExportFileDTO> exportFileDTOList) {
        this.exportFileDTOList = exportFileDTOList;
    }
}
