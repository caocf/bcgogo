package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-4
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class InnerReturnItemDTO extends BcgogoOrderItemDto {
   private Long innerReturnId;
   private Double price;   //库存均价
   private Double total;

   public void setProductDTO(ProductDTO productDTO) {
       if(productDTO == null){
         return;
       }
     this.setProductId(productDTO.getProductLocalInfoId());
     this.setCommodityCode(productDTO.getCommodityCode());
     this.setProductName(productDTO.getName());
     this.setBrand(productDTO.getBrand());
     this.setModel(productDTO.getModel());
     this.setSpec(productDTO.getSpec());
     this.setVehicleBrand(productDTO.getProductVehicleBrand());
     this.setVehicleModel(productDTO.getProductVehicleModel());
     this.setSellUnit(productDTO.getSellUnit());
     this.setStorageUnit(productDTO.getStorageUnit());
     this.setRate(productDTO.getRate());
    }

   public Long getId() {
     return id;
   }

   public void setId(Long id) {
     this.id = id;
   }

  public Long getInnerReturnId() {
    return innerReturnId;
  }

  public void setInnerReturnId(Long innerReturnId) {
    this.innerReturnId = innerReturnId;
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


  public ItemIndexDTO toItemIndexDTO(InnerReturnDTO innerReturnDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(innerReturnDTO.getShopId());
    itemIndexDTO.setOrderId(innerReturnDTO.getId());
    itemIndexDTO.setOrderTimeCreated(innerReturnDTO.getVestDate());
    itemIndexDTO.setOrderStatus(innerReturnDTO.getStatus());
    itemIndexDTO.setOrderReceiptNo(innerReturnDTO.getReceiptNo());
    itemIndexDTO.setStorehouseId(innerReturnDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(innerReturnDTO.getStorehouseName());
    itemIndexDTO.setOrderType(OrderTypes.INNER_RETURN);

    return itemIndexDTO;
  }
  public List<ItemIndexDTO> toInOutRecordDTO(InnerReturnDTO innerReturnDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(innerReturnDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(innerReturnDTO);
      itemIndexDTO.setItemType(ItemTypes.IN);
      itemIndexDTO.setInOutRecordId(this.getId());
       itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
//      itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemTotalCostPrice());
      itemIndexDTOList.add(itemIndexDTO);
    }else{
      if(!ArrayUtils.isEmpty(this.getInStorageRecordDTOs())){
        for(InStorageRecordDTO inStorageRecordDTO : getInStorageRecordDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(innerReturnDTO);
          itemIndexDTO.setItemType(ItemTypes.IN);
          itemIndexDTO.setInOutRecordId(inStorageRecordDTO.getId());
          itemIndexDTO.setRelatedSupplierId(inStorageRecordDTO.getSupplierId());
          itemIndexDTO.setRelatedSupplierName(inStorageRecordDTO.getSupplierName());
          itemIndexDTO.setItemCount(inStorageRecordDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(inStorageRecordDTO.getInStorageUnit());
           itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
//          itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemTotalCostPrice());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;
  }
}
