package com.bcgogo.config.model;

import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "import_record")
public class ImportRecord extends LongIdentifier {

  private Long shopId;
  private String status;
  private String fileName;
  private byte[] fileContent;
  private String type;

  public ImportRecord() {
  }

  public ImportRecord(ImportRecordDTO importRecordDTO) {
    if (importRecordDTO == null) {
      return;
    }
    this.setId(importRecordDTO.getId());
    this.shopId = importRecordDTO.getShopId();
    this.type = importRecordDTO.getType();
    this.fileContent = importRecordDTO.getFileContent();
    this.fileName = importRecordDTO.getFileName();
    this.status = importRecordDTO.getStatus();
  }

  @Column(name = "file_content")
  public byte[] getFileContent() {
    return fileContent;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Column(name = "status", length = 20)
  public String getStatus() {
    return status;
  }

  @Column(name = "file_name", length = 500)
  public String getFileName() {
    return fileName;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  @Column(name = "type", length = 20)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ImportRecordDTO toDTO() {
    ImportRecordDTO importRecordDTO = new ImportRecordDTO();
    importRecordDTO.setId(this.getId());
    importRecordDTO.setShopId(this.shopId);
    importRecordDTO.setFileName(this.fileName);
    importRecordDTO.setStatus(this.status);
    importRecordDTO.setFileContent(this.fileContent);
    importRecordDTO.setType(this.type);
    return importRecordDTO;
  }
}
