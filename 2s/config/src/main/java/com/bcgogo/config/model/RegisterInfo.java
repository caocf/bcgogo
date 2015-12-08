package com.bcgogo.config.model;

import com.bcgogo.config.dto.RegisterInfoDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.InvitationCodeDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-4
 * Time: 上午11:05
 */
@Entity
@Table(name = "register_info")
public class RegisterInfo extends LongIdentifier {
  private Long registerShopId;     //注册的店铺id
  private RegisterType registerType;//注册类型
  private OperatorType inviterType;  //邀请人类型
  private Long inviterShopId;      //邀请人店铺id
  private Long inviteeId;          //被邀请人Id，customerId,或者supplierId
  private OperatorType inviteeType;  //被邀请人类型
  private String invitationCode;    //邀请码
  private Long registerTime;//注册时间

  @Column(name = "register_shop_id")
  public Long getRegisterShopId() {
    return registerShopId;
  }

  public void setRegisterShopId(Long registerShopId) {
    this.registerShopId = registerShopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "inviter_type")
  public OperatorType getInviterType() {
    return inviterType;
  }

  public void setInviterType(OperatorType inviterType) {
    this.inviterType = inviterType;
  }

  @Column(name = "inviter_shop_id")
  public Long getInviterShopId() {
    return inviterShopId;
  }

  public void setInviterShopId(Long inviterShopId) {
    this.inviterShopId = inviterShopId;
  }

  @Column(name = "invitee_id")
  public Long getInviteeId() {
    return inviteeId;
  }

  public void setInviteeId(Long inviteeId) {
    this.inviteeId = inviteeId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "invitee_type")
  public OperatorType getInviteeType() {
    return inviteeType;
  }

  public void setInviteeType(OperatorType inviteeType) {
    this.inviteeType = inviteeType;
  }

  @Column(name = "invitation_code")
  public String getInvitationCode() {
    return invitationCode;
  }

  public void setInvitationCode(String invitationCode) {
    this.invitationCode = invitationCode;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "register_type")
  public RegisterType getRegisterType() {
    return registerType;
  }

  public void setRegisterType(RegisterType registerType) {
    this.registerType = registerType;
  }

  @Column(name = "register_time")
  public Long getRegisterTime() {
    return registerTime;
  }

  public void setRegisterTime(Long registerTime) {
    this.registerTime = registerTime;
  }

  public void from(ShopDTO shopDTO) {
    this.setRegisterTime(System.currentTimeMillis());
    if (shopDTO != null) {
      this.setRegisterShopId(shopDTO.getId());
      this.setRegisterType(shopDTO.getRegisterType());
      InvitationCodeDTO invitationCodeDTO = shopDTO.getInvitationCodeDTO();
      if (invitationCodeDTO != null) {
        this.setInviteeType(invitationCodeDTO.getInviteeType());
        this.setInvitationCode(invitationCodeDTO.getCode());
        this.setInviterType(invitationCodeDTO.getInviterType());
        this.setInviterShopId(invitationCodeDTO.getInviterId());
        this.setInviteeId(invitationCodeDTO.getInviteeId());
      }
    }
  }

  public RegisterInfoDTO toDTO() {
    RegisterInfoDTO registerInfoDTO = new RegisterInfoDTO();
    registerInfoDTO.setId(this.getId());
    registerInfoDTO.setInvitationCode(this.getInvitationCode());
    registerInfoDTO.setInviteeId(this.getInviteeId());
    registerInfoDTO.setInviteeType(this.getInviteeType());
    registerInfoDTO.setInviterType(this.getInviterType());
    registerInfoDTO.setInviterShopId(this.getInviterShopId());
    registerInfoDTO.setRegisterShopId(this.getRegisterShopId());
    registerInfoDTO.setRegisterTime(this.getRegisterTime());
    registerInfoDTO.setRegisterType(this.getRegisterType());
    return registerInfoDTO;
  }
}
