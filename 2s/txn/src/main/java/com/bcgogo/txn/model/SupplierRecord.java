package com.bcgogo.txn.model;

import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.SupplierRecordDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-9-11
 * Time: 上午11:26
 */
@Entity
@Table(name = "supplier_record")
public class SupplierRecord extends LongIdentifier {
  private Long shopId;
  private Long supplierId;
  private Double creditAmount;
  private ObjectStatus status;
  private Double debt;

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name="credit_amount")
  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  @Column(name="debt")
  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  @Override
  public String toString() {
    return "SupplierRecord{" +
        "shopId=" + shopId +
        ", supplierId=" + supplierId +
        ", creditAmount=" + creditAmount +
        '}';
  }

  public void fromDTO(SupplierRecordDTO supplierRecordDTO) {
    setId(supplierRecordDTO.getId());
    setShopId(supplierRecordDTO.getShopId());
    setSupplierId(supplierRecordDTO.getSupplierId());
    setCreditAmount(supplierRecordDTO.getCreditAmount());
    setDebt(supplierRecordDTO.getDebt());
  }

  public SupplierRecordDTO toDTO() {
    SupplierRecordDTO supplierRecordDTO = new SupplierRecordDTO();
    supplierRecordDTO.setId(getId());
    supplierRecordDTO.setShopId(getShopId());
    supplierRecordDTO.setSupplierId(getSupplierId());
    supplierRecordDTO.setCreditAmount(getCreditAmount());
    supplierRecordDTO.setDebt(getDebt());
    return supplierRecordDTO;
  }

  public SupplierRecord(SupplierRecordDTO supplierRecordDTO)
  {
    if(null == supplierRecordDTO)
    {
      return;
    }

    this.creditAmount = supplierRecordDTO.getCreditAmount();
    this.shopId = supplierRecordDTO.getShopId();
    this.setId(supplierRecordDTO.getId());
    this.supplierId = supplierRecordDTO.getSupplierId();
    this.debt = supplierRecordDTO.getDebt();
  }

  public SupplierRecord()
  {

  }
}
