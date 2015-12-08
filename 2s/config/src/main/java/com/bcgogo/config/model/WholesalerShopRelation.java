package com.bcgogo.config.model;

import com.bcgogo.config.dto.WholesalerShopRelationDTO;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.ShopRelationStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: wei lingfeng
 * Date: 12-11-13
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wholesaler_shop_relation")
public class WholesalerShopRelation extends LongIdentifier {
  private Long shopId;
  private Long wholesalerShopId;
  private ShopRelationStatus status;
  private Long operationShopId;
  private Long operationManId;
  private String cancelMsg;
  private RelationTypes relationType;//关联关系，目前有三种，RELATED，CUSTOMER_COLLECTION，SUPPLIER_COLLECTION


  public WholesalerShopRelation(Long customerShopId, Long wholesalerShopId, ShopRelationStatus status, RelationTypes relationTypes) {
    this.shopId = customerShopId;
    this.wholesalerShopId = wholesalerShopId;
    this.status = status;
    this.relationType = relationTypes;
  }

  public WholesalerShopRelation() {
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "wholesaler_shop_id")
  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ShopRelationStatus getStatus() {
    return status;
  }

  public void setStatus(ShopRelationStatus status) {
    this.status = status;
  }

  @Column(name = "operation_shop_id")
  public Long getOperationShopId() {
    return operationShopId;
  }

  public void setOperationShopId(Long operationShopId) {
    this.operationShopId = operationShopId;
  }

  @Column(name = "operation_man_id")
  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  @Column(name = "cancel_msg")
  public String getCancelMsg() {
    return cancelMsg;
  }

  public void setCancelMsg(String cancelMsg) {
    this.cancelMsg = cancelMsg;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "relation_type")
  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public WholesalerShopRelationDTO toDTO(){
    WholesalerShopRelationDTO dto = new WholesalerShopRelationDTO();
    dto.setId(this.getShopId());
    dto.setShopId(this.getShopId());
    dto.setWholesalerShopId(this.getWholesalerShopId());
    dto.setStatus(this.getStatus());
    dto.setOperationShopId(this.getOperationShopId());
    dto.setOperationManId(this.getOperationManId());
    dto.setCancelMsg(this.getCancelMsg());
    dto.setRelationType(this.getRelationType());
    return dto;
  }
}
