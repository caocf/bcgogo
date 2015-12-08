package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-9
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventoryItemDTO extends BcgogoOrderItemDto {


  private Long purchaseInventoryId;
  private Long purchaseItemId;
  private Double price;
  private Double total;
  private String memo;
  private String productType;

  private String vehicleInfo;
  private Long vehicleBrandId;
  private Long vehicleModelId;
  private Long vehicleYearId;
  private Long vehicleEngineId;
  private Double purchasePrice;   //采购价格
  private Integer productVehicleStatus;
  private Double recommendedPrice;

  private String barcode;

  private Double lowerLimit;
  private Double upperLimit;

  private Long vestDate;//入库单结算时间

  private Long supplierProductId;

  public Long getSupplierProductId() {
    return supplierProductId;
  }

  public void setSupplierProductId(Long supplierProductId) {
    this.supplierProductId = supplierProductId;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public PurchaseInventoryItemDTO() {
  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public Long getVehicleYearId() {
    return vehicleYearId;
  }

  public void setVehicleYearId(Long vehicleYearId) {
    this.vehicleYearId = vehicleYearId;
  }

  public Long getVehicleEngineId() {
    return vehicleEngineId;
  }

  public void setVehicleEngineId(Long vehicleEngineId) {
    this.vehicleEngineId = vehicleEngineId;
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  //modify by liuWei
  public String getVehicleInfo() {
    vehicleInfo = "";
    if (!this.getVehicleBrand().isEmpty()) vehicleInfo += this.getVehicleBrand();
    if (null != this.getVehicleModel() && !this.getVehicleModel().isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" + this.getVehicleModel();
    } else if (null != this.getVehicleModel() && !this.getVehicleModel().isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += this.getVehicleModel();
    }

//    if (null != this.getVehicleYear() && !this.getVehicleYear().isEmpty() && !"".equals(vehicleInfo)) {
//      vehicleInfo += "/" + this.getVehicleYear();
//    } else if (null != this.getVehicleYear() && !this.getVehicleYear().isEmpty() && "".equals(vehicleInfo)) {
//      vehicleInfo += this.getVehicleYear();
//    }
//
//    if (null != this.getVehicleEngine() && !this.getVehicleEngine().isEmpty() && !"".equals(vehicleInfo)) {
//      vehicleInfo += "/" + this.getVehicleEngine();
//    } else if (null != this.getVehicleEngine() && !this.getVehicleEngine().isEmpty() && "".equals(vehicleInfo)) {
//      vehicleInfo += this.getVehicleEngine();
//    }
    return vehicleInfo;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getPurchaseItemId() {
    return purchaseItemId;
  }

  public void setPurchaseItemId(Long purchaseItemId) {
    this.purchaseItemId = purchaseItemId;
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

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

  public ItemIndexDTO toItemIndexDTO(PurchaseInventoryDTO purchaseInventoryDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();
    itemIndexDTO.setOrderId(this.getPurchaseInventoryId());
    itemIndexDTO.setItemId(this.getId());
    itemIndexDTO.setOrderType(OrderTypes.INVENTORY);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setItemPrice(NumberUtil.doubleVal(this.getPurchasePrice()));
    itemIndexDTO.setInoutRecordTotalCostPrice(NumberUtil.doubleVal(this.getTotal()));

    itemIndexDTO.setShopId(purchaseInventoryDTO.getShopId());
    itemIndexDTO.setCustomerId(purchaseInventoryDTO.getSupplierId());
    itemIndexDTO.setCustomerOrSupplierName(purchaseInventoryDTO.getSupplier());
    itemIndexDTO.setOrderReceiptNo(purchaseInventoryDTO.getReceiptNo());
    itemIndexDTO.setOrderStatus(purchaseInventoryDTO.getStatus());
    itemIndexDTO.setOrderTotalAmount(purchaseInventoryDTO.getTotal());
    itemIndexDTO.setOrderTimeCreated(purchaseInventoryDTO.getVestDate() == null ? purchaseInventoryDTO.getCreationDate() : purchaseInventoryDTO.getVestDate());
    itemIndexDTO.setStorehouseId(purchaseInventoryDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(purchaseInventoryDTO.getStorehouseName());
    return itemIndexDTO;
  }

  public ItemIndexDTO toInOutRecordDTO(PurchaseInventoryDTO purchaseInventoryDTO) {
    ItemIndexDTO itemIndexDTO = toItemIndexDTO(purchaseInventoryDTO);
    itemIndexDTO.setItemType(ItemTypes.IN);
    itemIndexDTO.setInOutRecordId(this.getId());
    itemIndexDTO.setRelatedSupplierId(purchaseInventoryDTO.getSupplierId());
    itemIndexDTO.setRelatedSupplierName(purchaseInventoryDTO.getSupplier());
//    itemIndexDTO.setInoutRecordTotalCostPrice(NumberUtil.doubleVal(purchaseInventoryDTO.getCash())+NumberUtil.doubleVal(purchaseInventoryDTO.getBankCardAmount())+NumberUtil.doubleVal(purchaseInventoryDTO.getCheckAmount())+NumberUtil.doubleVal(purchaseInventoryDTO.getDepositAmount()));
    itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotal());
    itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
    return itemIndexDTO;
  }
}
