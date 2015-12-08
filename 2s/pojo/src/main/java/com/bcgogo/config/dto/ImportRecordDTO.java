package com.bcgogo.config.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */
public class ImportRecordDTO {

  private Long id;
  private Long shopId;
  private String status;
  private String fileName;
  private byte[] fileContent;
  private String type;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public String getStatus() {
    return status;
  }

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

  public byte[] getFileContent() {
    return fileContent;
  }

  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
