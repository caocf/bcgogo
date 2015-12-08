package com.bcgogo.config.service.excelimport;

import com.bcgogo.config.dto.ImportRecordDTO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-10
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
public class ImportContext {

  /**
   * 待导入的文件记录id列表 *
   */
  private List<Long> importRecordIdList;

  /**
   * 字段映射关系 *
   */
  private Map<String, String> fieldMapping;

  private Map<Long,String> fileNameMapping;
  private byte[] fileContent;

  private Long shopId;

  private List<Map<String, Object>> dataList;

  private InputStream inputStream;

  private String fileName;

  private String version;

  private String type;



  private List<ImportRecordDTO> importRecordDTOList;

  private boolean importToDefault;

  private Long userId;//操作人

  private String userName;

  public boolean isImportToDefault() {
    return importToDefault;
  }

  public void setImportToDefault(boolean importToDefault) {
    this.importToDefault = importToDefault;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public byte[] getFileContent() {
    return fileContent;
  }

  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  public String getFileName() {
    return fileName;
  }

  public String getVersion() {
    return version;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
    setVersion(ExcelImportUtil.getVersion(this.fileName));
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public List<Long> getImportRecordIdList() {
    return importRecordIdList;
  }

  public void setImportRecordIdList(List<Long> importRecordIdList) {
    this.importRecordIdList = importRecordIdList;
  }

  public Map<String, String> getFieldMapping() {
    return fieldMapping;
  }

  public void setFieldMapping(Map<String, String> fieldMapping) {
    this.fieldMapping = fieldMapping;
  }

  public Map<Long, String> getFileNameMapping() {
    return fileNameMapping;
  }

  public void setFileNameMapping(Map<Long, String> fileNameMapping) {
    this.fileNameMapping = fileNameMapping;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public List<Map<String, Object>> getDataList() {
    return dataList;
  }

  public void setDataList(List<Map<String, Object>> dataList) {
    this.dataList = dataList;
  }

  public List<ImportRecordDTO> getImportRecordDTOList() {
    return importRecordDTOList;
  }

  public void setImportRecordDTOList(List<ImportRecordDTO> importRecordDTOList) {
    this.importRecordDTOList = importRecordDTOList;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }
}
