package com.bcgogo.api;

import com.bcgogo.enums.app.ObdSimOwnerType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XinyuQiu on 14-7-11.
 */
public class ObdSimOutStorageDTO {

  private static final Logger LOG = LoggerFactory.getLogger(ObdSimOutStorageDTO.class);

  private ObdSimOwnerType outStorageType;

  private String outStorageTargetIdStr;
  private Long outStorageTargetId;
  private String outStorageTargetName;

  private String[] outStorageImeis;

  private String outStorageDateStr;
  private Long outStorageDate;

  private String operationName;
  private Long operationUserId;
  private Long operationShopId;

  public void  generateInfo(){
    if(StringUtils.isNotBlank(outStorageDateStr)){
      try{
        setOutStorageDate(DateUtil.convertDateStringToDateLong(DateUtil.DEFAULT,outStorageDateStr));
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }
    if(StringUtils.isNotBlank(outStorageTargetIdStr) && StringUtils.isNumeric(outStorageTargetIdStr) ){
      setOutStorageTargetId(NumberUtil.longValue(outStorageTargetIdStr));
    }
  }

  public ObdSimOwnerType getOutStorageType() {
    return outStorageType;
  }

  public void setOutStorageType(ObdSimOwnerType outStorageType) {
    this.outStorageType = outStorageType;
  }

  public String getOutStorageTargetIdStr() {
    return outStorageTargetIdStr;
  }

  public void setOutStorageTargetIdStr(String outStorageTargetIdStr) {
    this.outStorageTargetIdStr = outStorageTargetIdStr;
  }

  public Long getOutStorageTargetId() {
    return outStorageTargetId;
  }

  public void setOutStorageTargetId(Long outStorageTargetId) {
    this.outStorageTargetId = outStorageTargetId;
  }

  public String getOutStorageTargetName() {
    return outStorageTargetName;
  }

  public void setOutStorageTargetName(String outStorageTargetName) {
    this.outStorageTargetName = outStorageTargetName;
  }

  public String[] getOutStorageImeis() {
    return outStorageImeis;
  }

  public void setOutStorageImeis(String[] outStorageImeis) {
    this.outStorageImeis = outStorageImeis;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public Long getOperationUserId() {
    return operationUserId;
  }

  public void setOperationUserId(Long operationUserId) {
    this.operationUserId = operationUserId;
  }

  public Long getOperationShopId() {
    return operationShopId;
  }

  public void setOperationShopId(Long operationShopId) {
    this.operationShopId = operationShopId;
  }

  public String getOutStorageDateStr() {
    return outStorageDateStr;
  }

  public void setOutStorageDateStr(String outStorageDateStr) {
    this.outStorageDateStr = outStorageDateStr;
  }

  public Long getOutStorageDate() {
    return outStorageDate;
  }

  public void setOutStorageDate(Long outStorageDate) {
    this.outStorageDate = outStorageDate;
  }
}
