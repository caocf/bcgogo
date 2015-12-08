package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.ClientBindingLogDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-19
 * Time: 下午3:46
 * 客户端绑定日志
 */
@Entity
@Table(name = "client_binding_log")
public class ClientBindingLog extends LongIdentifier {
  private Long shopId;
  private String userNo;
  private String mac;//登录mac 地址
  private Long bindingTime;

  public ClientBindingLog() {
    super();
  }

  public ClientBindingLog(Long shopId, String userNo, String mac, Long bindingTime) {
    this.shopId = shopId;
    this.userNo = userNo;
    this.mac = mac;
    this.bindingTime = bindingTime;
  }

  public ClientBindingLog(ClientBindingLogDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setUserNo(dto.getUserNo());
    this.setMac(dto.getMac());
    this.setBindingTime(dto.getBindingTime());
  }

  public ClientBindingLogDTO toDTO() {
    ClientBindingLogDTO dto = new ClientBindingLogDTO();
    dto.setId(getId());
    dto.setShopId(getShopId());
    dto.setUserNo(getUserNo());
    dto.setMac(getMac());
    dto.setBindingTime(getBindingTime());
    return dto;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_no")
  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  @Column(name = "mac")
  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  @Column(name = "binding_time")
  public Long getBindingTime() {
    return bindingTime;
  }

  public void setBindingTime(Long bindingTime) {
    this.bindingTime = bindingTime;
  }
}
