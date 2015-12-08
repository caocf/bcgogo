package com.bcgogo.config.dto;

/**
 * Created with IntelliJ IDEA.
 * User: jinyuan
 * Date: 13-8-6
 * Time: 下午11:37
 * To change this template use File | Settings | File Templates.
 */
public class ExportFileDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long exportRecordId;
  private String fileName;
  private String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = String.valueOf(id);
  }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getExportRecordId() {
    return exportRecordId;
  }

  public void setExportRecordId(Long exportRecordId) {
    this.exportRecordId = exportRecordId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
