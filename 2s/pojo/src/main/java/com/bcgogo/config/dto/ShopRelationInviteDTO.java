package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-22
 * Time: 下午1:08
 * To change this template use File | Settings | File Templates.
 */
public class ShopRelationInviteDTO implements Serializable {

  private Long id;
  private String idStr;
  private Long originShopId;     //邀请人shopId
  private String originShopIdStr;     //邀请人shopId
  private Long originUserId;     //邀请人userId
  private String originUserIdStr;     //邀请人userId
  private Long inviteTime;      //邀请时间
  private Long invitedShopId;   //被邀请shopId
  private String invitedShopIdStr;   //被邀请shopId
  private InviteType inviteType;  //被邀请类型
  private InviteStatus status;    //被邀请状态
  private String operationMan;   //处理邀请人
  private Long operationManId;  //处理邀请人Id
  private String operationManIdStr;  //处理邀请人Id
  private Long operationTime;     //处理时间
  private String refuseMsg;       //拒绝理由

  private String originShopName;
  private String originAddress;
  private String originBusinessScope;
  private Long customerId,supplierId;
  private String customerIdStr,supplierIdStr;

  public void setOriginShopInfo(ShopDTO shopDTO) {
    if (shopDTO != null) {
      this.setOriginShopName(shopDTO.getName());
      this.setOriginAddress(shopDTO.getAddress());
      this.setOriginBusinessScope(shopDTO.fromBusinessScopes());
    }
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    if(customerId!=null)this.setCustomerIdStr(customerId.toString());
    this.customerId = customerId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    if(supplierId!=null)this.setSupplierIdStr(supplierId.toString());
    this.supplierId = supplierId;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) this.setIdStr(id.toString());
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getOriginShopId() {
    return originShopId;
  }

  public void setOriginShopId(Long originShopId) {
    if (originShopId != null) this.setOriginShopIdStr(originShopId.toString());
    this.originShopId = originShopId;
  }

  public Long getOriginUserId() {
    return originUserId;
  }

  public void setOriginUserId(Long originUserId) {
    if (originUserId != null) this.setOriginUserIdStr(originUserId.toString());
    this.originUserId = originUserId;
  }

  public Long getInviteTime() {
    return inviteTime;
  }

  public void setInviteTime(Long inviteTime) {
    this.inviteTime = inviteTime;
  }

  public Long getInvitedShopId() {
    return invitedShopId;
  }

  public void setInvitedShopId(Long invitedShopId) {
    if (invitedShopId != null) this.setInvitedShopIdStr(invitedShopId.toString());
    this.invitedShopId = invitedShopId;
  }

  public InviteType getInviteType() {
    return inviteType;
  }

  public void setInviteType(InviteType inviteType) {
    this.inviteType = inviteType;
  }

  public InviteStatus getStatus() {
    return status;
  }

  public void setStatus(InviteStatus status) {
    this.status = status;
  }

  public String getOperationMan() {
    return operationMan;
  }

  public void setOperationMan(String operationMan) {
    this.operationMan = operationMan;
  }

  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    if (operationManId != null) this.setOperationManIdStr(operationManId.toString());
    this.operationManId = operationManId;
  }

  public Long getOperationTime() {
    return operationTime;
  }

  public void setOperationTime(Long operationTime) {
    this.operationTime = operationTime;
  }

  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    this.refuseMsg = refuseMsg;
  }

  public String getOriginShopIdStr() {
    return originShopIdStr;
  }

  public void setOriginShopIdStr(String originShopIdStr) {
    this.originShopIdStr = originShopIdStr;
  }

  public String getOriginUserIdStr() {
    return originUserIdStr;
  }

  public void setOriginUserIdStr(String originUserIdStr) {
    this.originUserIdStr = originUserIdStr;
  }

  public String getInvitedShopIdStr() {
    return invitedShopIdStr;
  }

  public void setInvitedShopIdStr(String invitedShopIdStr) {
    this.invitedShopIdStr = invitedShopIdStr;
  }

  public String getOperationManIdStr() {
    return operationManIdStr;
  }

  public void setOperationManIdStr(String operationManIdStr) {
    this.operationManIdStr = operationManIdStr;
  }

  public String getOriginShopName() {
    return originShopName;
  }

  public void setOriginShopName(String originShopName) {
    this.originShopName = originShopName;
  }

  public String getOriginAddress() {
    return originAddress;
  }

  public void setOriginAddress(String originAddress) {
    this.originAddress = originAddress;
  }

  public String getOriginBusinessScope() {
    return originBusinessScope;
  }

  public void setOriginBusinessScope(String originBusinessScope) {
    this.originBusinessScope = originBusinessScope;
  }
}
