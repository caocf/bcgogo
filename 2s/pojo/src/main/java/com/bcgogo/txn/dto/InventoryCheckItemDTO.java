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
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-19
 * Time: 下午5:00
 * To change this template use File | Settings | File Templates.
 */
public class InventoryCheckItemDTO extends BcgogoOrderItemDto{

    //库存盘点单表ID
  private Long inventoryCheckId;


  //新在库存量
  private Double actualInventoryAmount;

  //库存调整量
  private Double inventoryAmountAdjustment;

  //盘点金额
  private Double inventoryAdjustmentPrice;

  //当前入库价平均价

    //现在入库价平均价
  private Double actualInventoryAveragePrice;


  // 入库平均价的调整量
  private Double inventoryAveragePriceAdjustment;

  private String inventoryAmountUnit;


  public Long getInventoryCheckId() {
    return inventoryCheckId;
  }

  public void setInventoryCheckId(Long inventoryCheckId) {
    this.inventoryCheckId = inventoryCheckId;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }


  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Double getActualInventoryAmount() {
    return actualInventoryAmount;
  }

  public void setActualInventoryAmount(Double actualInventoryAmount) {
    this.actualInventoryAmount = actualInventoryAmount;
  }


  public Double getInventoryAmountAdjustment() {
    return inventoryAmountAdjustment;
  }

  public void setInventoryAmountAdjustment(Double inventoryAmountAdjustment) {
    this.inventoryAmountAdjustment = inventoryAmountAdjustment;
  }

  public Double getInventoryAdjustmentPrice() {
    return inventoryAdjustmentPrice;
  }

  public void setInventoryAdjustmentPrice(Double inventoryAdjustmentPrice) {
    this.inventoryAdjustmentPrice = inventoryAdjustmentPrice;
  }


  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }


  public Double getActualInventoryAveragePrice() {
    return actualInventoryAveragePrice;
  }

  public void setActualInventoryAveragePrice(Double actualInventoryAveragePrice) {
    this.actualInventoryAveragePrice = actualInventoryAveragePrice;
  }


  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getInventoryAmountUnit() {
    return inventoryAmountUnit;
  }

  public void setInventoryAmountUnit(String inventoryAmountUnit) {
    this.inventoryAmountUnit = inventoryAmountUnit;
  }

  public Double getInventoryAveragePriceAdjustment() {
    return inventoryAveragePriceAdjustment;
  }

  public void setInventoryAveragePriceAdjustment(Double inventoryAveragePriceAdjustment) {
    this.inventoryAveragePriceAdjustment = inventoryAveragePriceAdjustment;
  }

  public ItemIndexDTO toItemIndexDTO(InventoryCheckDTO inventoryCheckDTO){
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(inventoryCheckDTO.getShopId());
    itemIndexDTO.setOrderId(inventoryCheckDTO.getId());
    itemIndexDTO.setOrderTimeCreated(inventoryCheckDTO.getEditDate());
    itemIndexDTO.setOrderStatus(OrderStatus.SETTLED);
    itemIndexDTO.setOrderReceiptNo(inventoryCheckDTO.getReceiptNo());
    itemIndexDTO.setStorehouseId(inventoryCheckDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(inventoryCheckDTO.getStorehouseName());

    itemIndexDTO.setOrderType(OrderTypes.INVENTORY_CHECK);
    return itemIndexDTO;
  }
  public List<ItemIndexDTO> toInOutRecordDTO(InventoryCheckDTO inventoryCheckDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();

    if(inventoryCheckDTO.getMergeInOutRecordFlag()){
      if(NumberUtil.subtraction(this.getInventoryAmount(),this.getActualInventoryAmount())>0){
        ItemIndexDTO itemIndexDTO = toItemIndexDTO(inventoryCheckDTO);
        itemIndexDTO.setItemType(ItemTypes.OUT);
        itemIndexDTO.setInOutRecordId(this.getId());
        itemIndexDTO.setItemCount(NumberUtil.subtraction(this.getInventoryAmount(),this.getActualInventoryAmount()));
        itemIndexDTO.setUnit(this.getSellUnit());
        itemIndexDTOList.add(itemIndexDTO);
      }else if(NumberUtil.subtraction(this.getInventoryAmount(),this.getActualInventoryAmount())<0){
        ItemIndexDTO itemIndexDTO = toItemIndexDTO(inventoryCheckDTO);
        itemIndexDTO.setItemType(ItemTypes.IN);
        itemIndexDTO.setInOutRecordId(this.getId());
        itemIndexDTO.setItemCount(NumberUtil.round(this.getActualInventoryAmount()-this.getInventoryAmount(),1));
        itemIndexDTO.setUnit(this.getSellUnit());
        itemIndexDTOList.add(itemIndexDTO);
      }
    }else{
      if(!ArrayUtils.isEmpty(this.getOutStorageRelationDTOs())){
        for(OutStorageRelationDTO outStorageRelationDTO : getOutStorageRelationDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(inventoryCheckDTO);
          itemIndexDTO.setItemType(ItemTypes.OUT);
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
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(inventoryCheckDTO);
          itemIndexDTO.setItemType(ItemTypes.IN);
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
