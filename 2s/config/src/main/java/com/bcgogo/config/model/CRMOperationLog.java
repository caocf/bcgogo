package com.bcgogo.config.model;

import com.bcgogo.config.dto.CRMOperationLogDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午8:28
 */
@Entity
@Table(name = "crm_operation_log")
public class CRMOperationLog extends LongIdentifier {
  private Long shopId;
  private String module;//操作模块
  private String type; //操作类型
  private String content;   //操作内容
  private Long operateTime;  //操作时间
  private String ipAddress;
  private String userNo;

  public CRMOperationLog() {
  }

  public CRMOperationLog(CRMOperationLogDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setContent(dto.getContent());
    this.setUserNo(dto.getUserNo());
    this.setType(dto.getType());
    this.setModule(dto.getModule());
    this.setIpAddress(dto.getIpAddress());
    this.setOperateTime(dto.getOperateTime());
  }

  public void fromDTO(CRMOperationLogDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setContent(dto.getContent());
    this.setUserNo(dto.getUserNo());
    this.setType(dto.getType());
    this.setModule(dto.getModule());
    this.setIpAddress(dto.getIpAddress());
    this.setOperateTime(dto.getOperateTime());
  }

  public CRMOperationLogDTO toDTO() {
    CRMOperationLogDTO dto = new CRMOperationLogDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setContent(this.getContent());
    dto.setUserNo(this.getUserNo());
    dto.setType(this.getType());
    dto.setModule(this.getModule());
    dto.setIpAddress(this.getIpAddress());
    dto.setOperateTime(this.getOperateTime());
    return dto;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "module")
  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  @Column(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "operate_time")
  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }


  @Column(name = "ip_address")
  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  @Column(name = "user_no")
  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }
}
