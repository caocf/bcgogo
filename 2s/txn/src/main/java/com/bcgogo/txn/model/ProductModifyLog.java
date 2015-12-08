package com.bcgogo.txn.model;

import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductModifyFields;
import com.bcgogo.enums.ProductModifyOperations;
import com.bcgogo.enums.ProductModifyTables;
import com.bcgogo.enums.StatProcessStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ProductModifyLogDTO;

import javax.persistence.*;

/**
 * 新的商品修改记录表，记录商品修改时所有的变动
 * User: Jimuchen
 * Date: 12-11-1
 * Time: 上午11:24
 */
@Entity
@Table(name = "product_modify_log")
public class ProductModifyLog extends LongIdentifier {
  private Long productId;
  private Long shopId;
  private Long userId;
  private Long operationId;
  private ProductModifyOperations operationType;
  private ProductModifyFields fieldName;
  private ProductModifyTables tableName;
  private String oldValue;
  private String newValue;
  private StatProcessStatus statProcessStatus;
  private ProductRelevanceStatus relevanceStatus; //商品标准化那边专用

  public ProductModifyLog(){}

  public ProductModifyLog(ProductModifyLogDTO productModifyLogDTO, boolean setId){
    if(productModifyLogDTO == null){
      return;
    }
    if(setId){
      setId(productModifyLogDTO.getId());
    }
    setProductId(productModifyLogDTO.getProductId());
    setShopId(productModifyLogDTO.getShopId());
    setUserId(productModifyLogDTO.getUserId());
    setOperationId(productModifyLogDTO.getOperationId());
    setOperationType(productModifyLogDTO.getOperationType());
    setFieldName(productModifyLogDTO.getFieldName());
    setTableName(productModifyLogDTO.getTableName());
    setOldValue(productModifyLogDTO.getOldValue());
    setNewValue(productModifyLogDTO.getNewValue());
    setStatProcessStatus(productModifyLogDTO.getStatProcessStatus());
    setRelevanceStatus(productModifyLogDTO.getRelevanceStatus());
  }

  public ProductModifyLogDTO toDTO(){
    ProductModifyLogDTO dto = new ProductModifyLogDTO();
    dto.setId(getId());
    dto.setProductId(getProductId());
    dto.setShopId(getShopId());
    dto.setUserId(getUserId());
    dto.setOperationId(getOperationId());
    dto.setOperationType(getOperationType());
    dto.setFieldName(getFieldName());
    dto.setTableName(getTableName());
    dto.setOldValue(getOldValue());
    dto.setNewValue(getNewValue());
    dto.setStatProcessStatus(getStatProcessStatus());
    dto.setCreationDate(getCreationDate());
    return dto;
  }

  @Column(name="product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name="operation_id")
  public Long getOperationId() {
    return operationId;
  }

  public void setOperationId(Long operationId) {
    this.operationId = operationId;
  }

  @Column(name="operation_type")
  @Enumerated(EnumType.STRING)
  public ProductModifyOperations getOperationType() {
    return operationType;
  }

  public void setOperationType(ProductModifyOperations operationType) {
    this.operationType = operationType;
  }

  @Column(name="field_name")
  @Enumerated(EnumType.STRING)
  public ProductModifyFields getFieldName() {
    return fieldName;
  }

  public void setFieldName(ProductModifyFields fieldName) {
    this.fieldName = fieldName;
  }

  @Column(name="table_name")
  @Enumerated(EnumType.STRING)
  public ProductModifyTables getTableName() {
    return tableName;
  }

  public void setTableName(ProductModifyTables tableName) {
    this.tableName = tableName;
  }

  @Column(name="old_value")
  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  @Column(name="new_value")
  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  @Column(name="stat_process_status")
  @Enumerated(EnumType.STRING)
  public StatProcessStatus getStatProcessStatus() {
    return statProcessStatus;
  }

  public void setStatProcessStatus(StatProcessStatus statProcessStatus) {
    this.statProcessStatus = statProcessStatus;
  }

  @Column(name="relevance_status")
  @Enumerated(EnumType.STRING)
  public ProductRelevanceStatus getRelevanceStatus() {
    return relevanceStatus;
  }

  public void setRelevanceStatus(ProductRelevanceStatus relevanceStatus) {
    this.relevanceStatus = relevanceStatus;
  }
}
