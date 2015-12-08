package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class BorrowOrderItemDTO extends BcgogoOrderItemDto{
  private String idStr;
  private Long shopId;
  private Long orderId;
  private Double price;   //库存均价
  private Double total;
  private Double returnAmount;
  private Double unReturnAmount;

//  public void setProductDTO(ProductDTO productDTO) {
//      if(productDTO == null){
//        return;
//      }
//    this.setProductId(productDTO.getProductLocalInfoId());
//    this.setCommodityCode(productDTO.getCommodityCode());
//    this.setProductName(productDTO.getName());
//    this.setBrand(productDTO.getBrand());
//    this.setModel(productDTO.getModel());
//    this.setSpec(productDTO.getSpec());
//    this.setVehicleBrand(productDTO.getProductVehicleBrand());
//    this.setVehicleModel(productDTO.getProductVehicleModel());
//    this.setSellUnit(productDTO.getSellUnit());
//    this.setStorageUnit(productDTO.getStorageUnit());
//    this.setRate(productDTO.getRate());
//   }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null){
      this.idStr=id.toString();
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(Double returnAmount) {
    this.returnAmount = returnAmount;
  }

  public Double getUnReturnAmount() {
    return unReturnAmount;
  }

  public void setUnReturnAmount(Double unReturnAmount) {
    this.unReturnAmount = unReturnAmount;
  }

  public ItemIndexDTO toItemIndexDTO(BorrowOrderDTO borrowOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(borrowOrderDTO.getShopId());
    itemIndexDTO.setOrderId(borrowOrderDTO.getId());
    itemIndexDTO.setOrderTimeCreated(borrowOrderDTO.getVestDate());
    itemIndexDTO.setOrderStatus(borrowOrderDTO.getStatus());
    itemIndexDTO.setOrderReceiptNo(borrowOrderDTO.getReceiptNo());
    itemIndexDTO.setStorehouseId(borrowOrderDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(borrowOrderDTO.getStorehouseName());
    itemIndexDTO.setOrderType(OrderTypes.BORROW_ORDER);

    return itemIndexDTO;
  }
  public List<ItemIndexDTO> toInOutRecordDTO(BorrowOrderDTO borrowOrderDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(borrowOrderDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(borrowOrderDTO);
      itemIndexDTO.setItemType(ItemTypes.OUT);
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setRelatedCustomerName(borrowOrderDTO.getBorrower());
      itemIndexDTO.setRelatedCustomerId(borrowOrderDTO.getBorrowerId());
      itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemTotalCostPrice());
      itemIndexDTOList.add(itemIndexDTO);
    }else {
      if(!ArrayUtils.isEmpty(this.getOutStorageRelationDTOs())){
        for(OutStorageRelationDTO outStorageRelationDTO : getOutStorageRelationDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(borrowOrderDTO);
          itemIndexDTO.setItemType(ItemTypes.OUT);
          itemIndexDTO.setInOutRecordId(outStorageRelationDTO.getId());
          itemIndexDTO.setRelatedSupplierId(outStorageRelationDTO.getRelatedSupplierId());
          itemIndexDTO.setRelatedSupplierName(outStorageRelationDTO.getRelatedSupplierName());
          itemIndexDTO.setItemCount(outStorageRelationDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(outStorageRelationDTO.getOutStorageUnit());
          itemIndexDTO.setRelatedCustomerName(borrowOrderDTO.getBorrower());   //todo qxyflg1
          itemIndexDTO.setRelatedCustomerId(borrowOrderDTO.getBorrowerId());
            itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemTotalCostPrice());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;

  }
}
