package com.bcgogo.config.dto;

import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.ShopRelationStatus;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-13
 * Time: 下午5:25
 * To change this template use File | Settings | File Templates.
 */
public class WholesalerShopRelationDTO {
  private Long id;
  private Long shopId;
  private Long wholesalerShopId;
  private ShopRelationStatus status;
  private Long operationShopId;
  private Long operationManId;
  private String cancelMsg;
  private RelationTypes relationType;//关联关系，目前有三种，RELATED，CUSTOMER_COLLECTION，SUPPLIER_COLLECTION


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  public ShopRelationStatus getStatus() {
    return status;
  }

  public Long getOperationShopId() {
    return operationShopId;
  }

  public void setOperationShopId(Long operationShopId) {
    this.operationShopId = operationShopId;
  }

  public void setStatus(ShopRelationStatus status) {
    this.status = status;
  }

  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  public String getCancelMsg() {
    return cancelMsg;
  }

  public void setCancelMsg(String cancelMsg) {
    this.cancelMsg = cancelMsg;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }
}
