package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-8
 * Time: 上午7:03
 * To change this template use File | Settings | File Templates.
 */
public class ReturnOrderItemDTO extends BcgogoOrderItemDto{

  private Long orderId;
  private Double borrowAmount;
  private Double returnAmount;
  private String unit;
  private String borrowUnit;
  private String returner;
  private Long productHistoryId;
  private Double price;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Double getBorrowAmount() {
    return borrowAmount;
  }

  public void setBorrowAmount(Double borrowAmount) {
    this.borrowAmount = borrowAmount;
  }

  public Double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(Double returnAmount) {
    this.returnAmount = returnAmount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getBorrowUnit() {
    return borrowUnit;
  }

  public void setBorrowUnit(String borrowUnit) {
    this.borrowUnit = borrowUnit;
  }

  public String getReturner() {
    return returner;
  }

  public void setReturner(String returner) {
    this.returner = returner;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }

  public ItemIndexDTO toItemIndexDTO(ReturnOrderDTO returnOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(returnOrderDTO.getShopId());
    itemIndexDTO.setOrderId(returnOrderDTO.getId());
    itemIndexDTO.setOrderTimeCreated(returnOrderDTO.getVestDate());
    itemIndexDTO.setOrderStatus(returnOrderDTO.getStatus());
    itemIndexDTO.setOrderReceiptNo(returnOrderDTO.getReceiptNo());
    itemIndexDTO.setStorehouseId(returnOrderDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(returnOrderDTO.getStorehouseName());
    itemIndexDTO.setOrderType(OrderTypes.RETURN_ORDER);

    return itemIndexDTO;
  }
  public List<ItemIndexDTO> toInOutRecordDTO(ReturnOrderDTO returnOrderDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(returnOrderDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(returnOrderDTO);
      //出入库记录 特别处理  借料归还单  并入 借料
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setOrderReceiptNo(returnOrderDTO.getBorrowOrderReceiptNo());
      itemIndexDTO.setOrderId(NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
      itemIndexDTO.setOrderType(OrderTypes.BORROW_ORDER);
      itemIndexDTO.setItemType(ItemTypes.IN);
      itemIndexDTO.setItemCount(this.getReturnAmount());
      itemIndexDTO.setRelatedCustomerId(returnOrderDTO.getReturnId());
      itemIndexDTO.setRelatedCustomerName(returnOrderDTO.getReturner());
      itemIndexDTOList.add(itemIndexDTO);
    }else {
      if(!ArrayUtils.isEmpty(this.getInStorageRecordDTOs())){
        for(InStorageRecordDTO inStorageRecordDTO : getInStorageRecordDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(returnOrderDTO);
          //出入库记录 特别处理  借料归还单  并入 借料

          itemIndexDTO.setOrderReceiptNo(returnOrderDTO.getBorrowOrderReceiptNo());
          itemIndexDTO.setOrderId(NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
          itemIndexDTO.setOrderType(OrderTypes.BORROW_ORDER);


          itemIndexDTO.setInOutRecordId(inStorageRecordDTO.getId());
          itemIndexDTO.setItemType(ItemTypes.IN);
          itemIndexDTO.setRelatedSupplierId(inStorageRecordDTO.getSupplierId());
          itemIndexDTO.setRelatedSupplierName(inStorageRecordDTO.getSupplierName());
          itemIndexDTO.setRelatedCustomerId(returnOrderDTO.getReturnId());
          itemIndexDTO.setRelatedCustomerName(returnOrderDTO.getReturner());
          itemIndexDTO.setItemCount(inStorageRecordDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(inStorageRecordDTO.getInStorageUnit());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;

  }
}
