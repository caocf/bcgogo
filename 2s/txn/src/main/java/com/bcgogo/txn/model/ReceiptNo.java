package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ReceiptNoDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-20
 * Time: 下午2:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="receipt_no")
public class ReceiptNo extends LongIdentifier {

  private Long shopId;

  private OrderTypes types;

  private String receiptNo;

  @Enumerated(EnumType.STRING)
  @Column(name="type")
  public OrderTypes getTypes() {
    return types;
  }

  public void setTypes(OrderTypes types) {
    this.types = types;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public ReceiptNo(){

  }

  public ReceiptNo(ReceiptNoDTO receiptNoDTO)
  {
    if(null == receiptNoDTO)
    {
      return;
    }

    this.setId(receiptNoDTO.getId());
    this.setShopId(receiptNoDTO.getShopId());
    this.setTypes(receiptNoDTO.getTypes());
    this.setReceiptNo(receiptNoDTO.getReceiptNo());
  }

  public ReceiptNoDTO toDTO()
  {
    ReceiptNoDTO receiptNoDTO = new ReceiptNoDTO();

    receiptNoDTO.setId(this.getId());
    receiptNoDTO.setReceiptNo(this.getReceiptNo());
    receiptNoDTO.setShopId(this.getShopId());
    receiptNoDTO.setTypes(this.getTypes());
    return receiptNoDTO;
  }
}
