package com.bcgogo.config.dto;

import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.RegisterType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-5
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class RegisterInfoDTO implements Serializable {
  private Long id;
  private Long registerShopId;     //注册的店铺id
  private RegisterType registerType;//注册类型
  private OperatorType inviterType;  //邀请人类型
  private Long inviterShopId;      //邀请人店铺id
  private Long inviteeId;          //被邀请人Id，customerId,或者supplierId
  private OperatorType inviteeType;  //被邀请人类型
  private String invitationCode;    //邀请码
  private Long registerTime;//注册时间

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRegisterShopId() {
    return registerShopId;
  }

  public void setRegisterShopId(Long registerShopId) {
    this.registerShopId = registerShopId;
  }

  public RegisterType getRegisterType() {
    return registerType;
  }

  public void setRegisterType(RegisterType registerType) {
    this.registerType = registerType;
  }

  public OperatorType getInviterType() {
    return inviterType;
  }

  public void setInviterType(OperatorType inviterType) {
    this.inviterType = inviterType;
  }

  public Long getInviterShopId() {
    return inviterShopId;
  }

  public void setInviterShopId(Long inviterShopId) {
    this.inviterShopId = inviterShopId;
  }

  public Long getInviteeId() {
    return inviteeId;
  }

  public void setInviteeId(Long inviteeId) {
    this.inviteeId = inviteeId;
  }

  public OperatorType getInviteeType() {
    return inviteeType;
  }

  public void setInviteeType(OperatorType inviteeType) {
    this.inviteeType = inviteeType;
  }

  public String getInvitationCode() {
    return invitationCode;
  }

  public void setInvitationCode(String invitationCode) {
    this.invitationCode = invitationCode;
  }

  public Long getRegisterTime() {
    return registerTime;
  }

  public void setRegisterTime(Long registerTime) {
    this.registerTime = registerTime;
  }
}
