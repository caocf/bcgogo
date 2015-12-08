package com.bcgogo.txn.dto;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OtherIncomeCalculateWay;
import com.bcgogo.search.dto.ItemIndexDTO;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-11
 * Time: 下午1:52
 * To change this template use File | Settings | File Templates.
 */
public class SalesOrderOtherIncomeItemDTO extends OrderOtherIncomeItemDTO{


  public ItemIndexDTO toItemIndexDTO(SalesOrderDTO salesOrderDTO){
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setShopId(salesOrderDTO.getShopId());
    itemIndexDTO.setVehicle(salesOrderDTO.getLicenceNo());
    itemIndexDTO.setOrderId(salesOrderDTO.getId());
    itemIndexDTO.setPaymentTime(salesOrderDTO.getPaymentTime());
    itemIndexDTO.setOrderTimeCreated(salesOrderDTO.getVestDate() == null ? salesOrderDTO.getCreationDate() : salesOrderDTO.getVestDate());
    itemIndexDTO.setOrderType(OrderTypes.SALE);
    itemIndexDTO.setOrderStatus(salesOrderDTO.getStatus());
    itemIndexDTO.setItemType(ItemTypes.OTHER_INCOME);
    itemIndexDTO.setCustomerId(salesOrderDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(salesOrderDTO.getCustomer());
    itemIndexDTO.setCustomerOrSupplierStatus(salesOrderDTO.getCustomerStatus() == null ? CustomerStatus.ENABLED.toString() : salesOrderDTO.getCustomerStatus().toString());
    itemIndexDTO.setOrderReceiptNo(salesOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderTotalAmount(salesOrderDTO.getTotal());
    itemIndexDTO.setArrears(salesOrderDTO.getDebt());

    itemIndexDTO.setItemName(this.getName());
    itemIndexDTO.setItemId(this.getId());
    itemIndexDTO.setItemPrice(this.getPrice());
    itemIndexDTO.setItemTotalAmount(this.getPrice());
    itemIndexDTO.setItemMemo(this.getMemo());
    return itemIndexDTO;
  }
}
