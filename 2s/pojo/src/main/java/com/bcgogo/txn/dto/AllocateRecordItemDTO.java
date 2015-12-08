package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class AllocateRecordItemDTO extends BcgogoOrderItemDto {
  private String idStr;
  private Long allocateRecordId;
  private Double costPrice;
  private Double totalCostPrice;
  private String inStorageBin;

  public AllocateRecordItemDTO() {
    this.amount = 0d;
    this.costPrice = 0d;
    this.totalCostPrice = 0d;
    inventoryAmount = 0d;
  }

  /**
   * 重写  不需要仓位字段
   * @param productHistoryDTO
   */
  public void setProductHistoryDTO(ProductHistoryDTO productHistoryDTO){
    if (productHistoryDTO == null) {
      return;
    }
    this.setProductId(productHistoryDTO.getProductLocalInfoId());
    this.productName = productHistoryDTO.getName();
    this.brand = productHistoryDTO.getBrand();
    this.model = productHistoryDTO.getModel();
    this.spec = productHistoryDTO.getSpec();
    this.vehicleBrand = productHistoryDTO.getProductVehicleBrand();
    this.vehicleModel = productHistoryDTO.getProductVehicleModel();
    this.vehicleYear = productHistoryDTO.getProductVehicleYear();
    this.vehicleEngine = productHistoryDTO.getProductVehicleEngine();
    this.sellUnit = productHistoryDTO.getSellUnit();
    this.storageUnit = productHistoryDTO.getStorageUnit();
    this.rate = productHistoryDTO.getRate();
    this.tradePrice = productHistoryDTO.getTradePrice();
    this.commodityCode = productHistoryDTO.getCommodityCode();
    this.productKind = productHistoryDTO.getKindName();
    this.productKindId = productHistoryDTO.getKindId();
    this.setProductHistoryId(productHistoryDTO.getId());
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null){
      this.idStr = id.toString();
    }
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getAllocateRecordId() {
    return allocateRecordId;
  }

  public void setAllocateRecordId(Long allocateRecordId) {
    this.allocateRecordId = allocateRecordId;
  }

  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = NumberUtil.round(costPrice, NumberUtil.MONEY_PRECISION);
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = NumberUtil.round(totalCostPrice, NumberUtil.MONEY_PRECISION);
  }

  public String getInStorageBin() {
    return inStorageBin;
  }

  public void setInStorageBin(String inStorageBin) {
    this.inStorageBin = inStorageBin;
  }

  public ItemIndexDTO toItemIndexDTO(AllocateRecordDTO allocateRecordDTO){
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(allocateRecordDTO.getShopId());
    itemIndexDTO.setOrderId(allocateRecordDTO.getId());
    itemIndexDTO.setOrderTimeCreated(allocateRecordDTO.getVestDate());
    itemIndexDTO.setOrderStatus(OrderStatus.SETTLED);
    itemIndexDTO.setOrderReceiptNo(allocateRecordDTO.getReceiptNo());

    itemIndexDTO.setOrderType(OrderTypes.ALLOCATE_RECORD);
    return itemIndexDTO;
  }

  public List<ItemIndexDTO> toInOutRecordDTO(AllocateRecordDTO allocateRecordDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(allocateRecordDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(allocateRecordDTO);
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setItemType(ItemTypes.OUT);
      itemIndexDTO.setStorehouseId(allocateRecordDTO.getOutStorehouseId());
      itemIndexDTO.setStorehouseName(allocateRecordDTO.getOutStorehouseName());
      itemIndexDTOList.add(itemIndexDTO);

      itemIndexDTO = toItemIndexDTO(allocateRecordDTO);
      itemIndexDTO.setItemType(ItemTypes.IN);
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setStorehouseId(allocateRecordDTO.getInStorehouseId());
      itemIndexDTO.setStorehouseName(allocateRecordDTO.getInStorehouseName());
      itemIndexDTOList.add(itemIndexDTO);

    }else{
      if(!ArrayUtils.isEmpty(this.getOutStorageRelationDTOs())){
        for(OutStorageRelationDTO outStorageRelationDTO : getOutStorageRelationDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(allocateRecordDTO);
          itemIndexDTO.setItemType(ItemTypes.OUT);
          itemIndexDTO.setStorehouseId(allocateRecordDTO.getOutStorehouseId());
          itemIndexDTO.setStorehouseName(allocateRecordDTO.getOutStorehouseName());

          itemIndexDTO.setInOutRecordId(outStorageRelationDTO.getId());
          itemIndexDTO.setRelatedSupplierId(outStorageRelationDTO.getRelatedSupplierId());
          itemIndexDTO.setRelatedSupplierName(outStorageRelationDTO.getRelatedSupplierName());
          itemIndexDTO.setItemCount(outStorageRelationDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(outStorageRelationDTO.getOutStorageUnit());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
      if(!ArrayUtils.isEmpty(this.getInStorageRecordDTOs())){
        for(InStorageRecordDTO inStorageRecordDTO : getInStorageRecordDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(allocateRecordDTO);
          itemIndexDTO.setItemType(ItemTypes.IN);
          itemIndexDTO.setStorehouseId(allocateRecordDTO.getInStorehouseId());
          itemIndexDTO.setStorehouseName(allocateRecordDTO.getInStorehouseName());
          itemIndexDTO.setInOutRecordId(inStorageRecordDTO.getId());
          itemIndexDTO.setRelatedSupplierId(inStorageRecordDTO.getSupplierId());
          itemIndexDTO.setRelatedSupplierName(inStorageRecordDTO.getSupplierName());
          itemIndexDTO.setItemCount(inStorageRecordDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(inStorageRecordDTO.getInStorageUnit());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;
  }
}
