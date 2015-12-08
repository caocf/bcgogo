package com.bcgogo.api;

import com.bcgogo.enums.app.OBDStatus;

/**
 * Created by XinyuQiu on 14-6-16.
 */
public class ObdSimHistoryDTO {
  private Long id;
  private Long obdSimId;
  private String simNo;
  private String mobile;
  private Long useDate;//开通日期
  private Integer usePeriod;//服务期
  private OBDStatus status;
  private Long storageId;//仓管员ID
  private Long sellerId;//统购销售员Id
  private Long agentId;//代理商Id

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getObdSimId() {
    return obdSimId;
  }

  public void setObdSimId(Long obdSimId) {
    this.obdSimId = obdSimId;
  }

  public String getSimNo() {
    return simNo;
  }

  public void setSimNo(String simNo) {
    this.simNo = simNo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Long getUseDate() {
    return useDate;
  }

  public void setUseDate(Long useDate) {
    this.useDate = useDate;
  }

  public Integer getUsePeriod() {
    return usePeriod;
  }

  public void setUsePeriod(Integer usePeriod) {
    this.usePeriod = usePeriod;
  }

  public OBDStatus getStatus() {
    return status;
  }

  public void setStatus(OBDStatus status) {
    this.status = status;
  }

  public Long getStorageId() {
    return storageId;
  }

  public void setStorageId(Long storageId) {
    this.storageId = storageId;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }
}
