package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-22
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_relation_invite")
public class ShopRelationInvite extends LongIdentifier {
  private Long originShopId;     //邀请人shopId
  private Long originUserId;     //邀请人userId
  private Long inviteTime;      //邀请时间
  private Long invitedShopId;   //被邀请shopId
  private InviteType inviteType;  //被邀请类型
  private InviteStatus status;    //被邀请状态
  private String operationMan;   //处理邀请人
  private Long operationManId;  //处理邀请人Id
  private Long operationTime;     //处理时间
  private String refuseMsg;       //拒绝理由
  private Long customerId;
  private Long supplierId;

  public ShopRelationInvite(Long shopId, Long userId, Long supplerShopIds) {
    originShopId = shopId;
    originUserId = userId;
    invitedShopId = supplerShopIds;
    inviteTime = System.currentTimeMillis();
  }

  public ShopRelationInvite() {
  }

  public ShopRelationInviteDTO toDTO() {
    ShopRelationInviteDTO shopRelationInviteDTO = new ShopRelationInviteDTO();
    shopRelationInviteDTO.setId(this.getId());
    shopRelationInviteDTO.setInviteType(this.getInviteType());
    shopRelationInviteDTO.setOriginShopId(this.getOriginShopId());
    shopRelationInviteDTO.setOriginUserId(this.getOriginUserId());
    shopRelationInviteDTO.setInviteTime(this.getInviteTime());
    shopRelationInviteDTO.setInvitedShopId(this.getInvitedShopId());
    shopRelationInviteDTO.setStatus(this.getStatus());
    shopRelationInviteDTO.setOperationMan(this.getOperationMan());
    shopRelationInviteDTO.setOperationManId(this.getOperationManId());
    shopRelationInviteDTO.setOperationTime(this.getOperationTime());
    shopRelationInviteDTO.setRefuseMsg(this.getRefuseMsg());
    shopRelationInviteDTO.setCustomerId(this.getCustomerId());
    shopRelationInviteDTO.setSupplierId(this.getSupplierId());
    return shopRelationInviteDTO;
  }

  public void fromDTO(ShopRelationInviteDTO shopRelationInviteDTO) {
    this.setId(shopRelationInviteDTO.getId());
    this.setOriginShopId(shopRelationInviteDTO.getOriginShopId());
    this.setOriginUserId(shopRelationInviteDTO.getOriginUserId());
    this.setInviteTime(shopRelationInviteDTO.getInviteTime());
    this.setInvitedShopId(shopRelationInviteDTO.getInvitedShopId());
    this.setStatus(shopRelationInviteDTO.getStatus());
    this.setOperationMan(shopRelationInviteDTO.getOperationMan());
    this.setOperationManId(shopRelationInviteDTO.getOperationManId());
    this.setOperationTime(shopRelationInviteDTO.getOperationTime());
    this.setRefuseMsg(shopRelationInviteDTO.getRefuseMsg());
    this.setCustomerId(shopRelationInviteDTO.getCustomerId());
    this.setSupplierId(shopRelationInviteDTO.getSupplierId());
  }

  public void setOperationInfo(ShopRelationInviteDTO shopRelationInviteDTO) {
    this.setOperationMan(shopRelationInviteDTO.getOperationMan());
    this.setOperationManId(shopRelationInviteDTO.getOperationManId());
    this.setOperationTime(shopRelationInviteDTO.getOperationTime());
    this.setStatus(shopRelationInviteDTO.getStatus());
    this.setRefuseMsg(shopRelationInviteDTO.getRefuseMsg());
    this.setCustomerId(shopRelationInviteDTO.getCustomerId());
    this.setSupplierId(shopRelationInviteDTO.getSupplierId());
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "origin_shop_id")
  public Long getOriginShopId() {
    return originShopId;
  }

  public void setOriginShopId(Long originShopId) {
    this.originShopId = originShopId;
  }

  @Column(name = "origin_user_id")
  public Long getOriginUserId() {
    return originUserId;
  }

  public void setOriginUserId(Long originUserId) {
    this.originUserId = originUserId;
  }

  @Column(name = "invite_time")
  public Long getInviteTime() {
    return inviteTime;
  }

  public void setInviteTime(Long inviteTime) {
    this.inviteTime = inviteTime;
  }

  @Column(name = "invited_shop_id")
  public Long getInvitedShopId() {
    return invitedShopId;
  }

  public void setInvitedShopId(Long invitedShopId) {
    this.invitedShopId = invitedShopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "invite_type")
  public InviteType getInviteType() {
    return inviteType;
  }

  public void setInviteType(InviteType inviteType) {
    this.inviteType = inviteType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public InviteStatus getStatus() {
    return status;
  }

  public void setStatus(InviteStatus status) {
    this.status = status;
  }

  @Column(name = "operation_man")
  public String getOperationMan() {
    return operationMan;
  }

  public void setOperationMan(String operationMan) {
    this.operationMan = operationMan;
  }

  @Column(name = "operation_man_id")
  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  @Column(name = "operation_time")
  public Long getOperationTime() {
    return operationTime;
  }

  public void setOperationTime(Long operationTime) {
    this.operationTime = operationTime;
  }

  @Column(name = "refuse_msg")
  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    this.refuseMsg = refuseMsg;
  }
}
