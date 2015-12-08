package com.bcgogo.config.model;

import com.bcgogo.config.dto.ExportFileDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: jinyuan
 * Date: 13-8-6
 * Time: 下午11:30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "export_file")
public class ExportFile extends LongIdentifier {
  private Long shopId;
  private Long exportRecordId;
  private String fileName;
  private String status;
  public ExportFile() {

  }
  public ExportFile(ExportFileDTO exportFileDTO) {
      setShopId(exportFileDTO.getShopId());
      setExportRecordId(exportFileDTO.getExportRecordId());
      setFileName(exportFileDTO.getFileName());
      setStatus(exportFileDTO.getStatus());
  }

  public ExportFileDTO toDTO() {
      ExportFileDTO exportFileDTO = new ExportFileDTO();
      exportFileDTO.setId(this.getId());
      exportFileDTO.setShopId(this.shopId);
      exportFileDTO.setExportRecordId(this.exportRecordId);
      exportFileDTO.setFileName(this.fileName);
      exportFileDTO.setStatus(this.status);
      return exportFileDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "export_record_id")

  public Long getExportRecordId() {
    return exportRecordId;
  }

  public void setExportRecordId(Long exportRecordId) {
    this.exportRecordId = exportRecordId;
  }

  @Column(name = "file_name", length = 255)

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Column(name = "status", length = 20)

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
