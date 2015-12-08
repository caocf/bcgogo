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
 * Date: 12-12-28
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class InnerPickingItemDTO extends BcgogoOrderItemDto {
  private Long innerPickingId;
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

  public Long getInnerPickingId() {
    return innerPickingId;
  }

  public void setInnerPickingId(Long innerPickingId) {
    this.innerPickingId = innerPickingId;
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


  public ItemIndexDTO toItemIndexDTO(InnerPickingDTO innerPickingDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(innerPickingDTO.getShopId());
    itemIndexDTO.setOrderId(innerPickingDTO.getId());
    itemIndexDTO.setOrderTimeCreated(innerPickingDTO.getVestDate());
    itemIndexDTO.setOrderStatus(innerPickingDTO.getStatus());
    itemIndexDTO.setOrderReceiptNo(innerPickingDTO.getReceiptNo());
    itemIndexDTO.setStorehouseId(innerPickingDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(innerPickingDTO.getStorehouseName());
    itemIndexDTO.setOrderType(OrderTypes.INNER_PICKING);

    return itemIndexDTO;
  }
  public List<ItemIndexDTO> toInOutRecordDTO(InnerPickingDTO innerPickingDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(innerPickingDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(innerPickingDTO);
      itemIndexDTO.setItemType(ItemTypes.OUT);
      itemIndexDTO.setInOutRecordId(this.getId());
//      itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
      itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotal());
      itemIndexDTOList.add(itemIndexDTO);
    }else{
      if(!ArrayUtils.isEmpty(this.getOutStorageRelationDTOs())){
        for(OutStorageRelationDTO outStorageRelationDTO : getOutStorageRelationDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(innerPickingDTO);
          itemIndexDTO.setItemType(ItemTypes.OUT);
          itemIndexDTO.setInOutRecordId(outStorageRelationDTO.getId());
          itemIndexDTO.setRelatedSupplierId(outStorageRelationDTO.getRelatedSupplierId());
          itemIndexDTO.setRelatedSupplierName(outStorageRelationDTO.getRelatedSupplierName());
          itemIndexDTO.setItemCount(outStorageRelationDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(outStorageRelationDTO.getOutStorageUnit());
//          itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
          itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotal());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;
  }
}
