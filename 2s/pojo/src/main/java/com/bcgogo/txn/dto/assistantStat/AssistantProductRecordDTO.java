package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.NumberUtil;

/**
 * 员工商品销售记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午1:28
 * To change this template use File | Settings | File Templates.
 */
public class AssistantProductRecordDTO extends AssistantRecordDTO{
  private Long itemId;

  private Long productId;
  private String productName;

  private Double amount;
  private Double price;
  private String unit;
  private Double total;


  private Long productAchievementHistoryId;

  public AssistantProductRecordDTO(){}

  public AssistantProductRecordDTO(BcgogoOrderDto bcgogoOrderDto, BcgogoOrderItemDto bcgogoOrderItemDto,OrderTypes orderType) {
    this.setShopId(bcgogoOrderDto.getShopId());
    this.setOrderId(bcgogoOrderDto.getId());

    this.setItemId(bcgogoOrderItemDto.getId());
    this.setProductId(bcgogoOrderItemDto.getProductId());
    this.setProductName(bcgogoOrderItemDto.getProductName());
    this.setAmount(bcgogoOrderItemDto.getAmount());
    this.setUnit(bcgogoOrderItemDto.getUnit());
    this.setOrderType(orderType);

    if (orderType == OrderTypes.REPAIR) {
      RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;
      RepairOrderItemDTO repairOrderItemDTO = (RepairOrderItemDTO) bcgogoOrderItemDto;
      this.setPrice(repairOrderItemDTO.getPrice());
      this.setTotal(repairOrderItemDTO.getTotal());

      this.setReceiptNo(repairOrderDTO.getReceiptNo());
      this.setVestDate(repairOrderDTO.getVestDate());

      this.setCustomer(repairOrderDTO.getCustomerName());
      this.setCustomerId(repairOrderDTO.getCustomerId());
    } else if (orderType == OrderTypes.SALE) {
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) bcgogoOrderDto;
      SalesOrderItemDTO salesOrderItemDTO = (SalesOrderItemDTO) bcgogoOrderItemDto;
      this.setPrice(salesOrderItemDTO.getPrice());
      this.setTotal(salesOrderItemDTO.getTotal());

      this.setReceiptNo(salesOrderDTO.getReceiptNo());
      this.setVestDate(salesOrderDTO.getVestDate());

      this.setCustomer(salesOrderDTO.getCustomer());
      this.setCustomerId(salesOrderDTO.getCustomerId());
    } else if (orderType == OrderTypes.SALE_RETURN) {
      SalesReturnDTO salesReturnDTO = (SalesReturnDTO) bcgogoOrderDto;
      SalesReturnItemDTO salesReturnItemDTO = (SalesReturnItemDTO) bcgogoOrderItemDto;
      this.setPrice(salesReturnItemDTO.getPrice());
      this.setTotal(-salesReturnItemDTO.getTotal());

      this.setReceiptNo(salesReturnDTO.getReceiptNo());
      this.setVestDate(salesReturnDTO.getVestDate());

      this.setCustomer(salesReturnDTO.getCustomer());
      this.setCustomerId(salesReturnDTO.getCustomerId());
    }


  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = NumberUtil.toReserve(amount, NumberUtil.PRECISION);;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = NumberUtil.toReserve(price,NumberUtil.PRECISION);
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = NumberUtil.toReserve(total,NumberUtil.PRECISION);
  }

  public Long getProductAchievementHistoryId() {
    return productAchievementHistoryId;
  }

  public void setProductAchievementHistoryId(Long productAchievementHistoryId) {
    this.productAchievementHistoryId = productAchievementHistoryId;
  }
}
