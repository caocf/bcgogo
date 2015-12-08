package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

public class PurchaseReturnItemDTO extends BcgogoOrderItemDto implements Serializable {
  private static final long serialVersionUID = 0L;
  private Long purchaseReturnId;
  private Double price;
  private Double total;
  private String memo;
  private Double returnAbleAmount;
  private Integer productVehicleStatus;
  private String barcode;
  private String vehicleInfo;

  private Long supplierId;
  private String supplierName;
  private String supplierIdStr;
  private String orderTimeCreatedStr;
  private ItemIndexDTO[] itemIndexDTOs;
  private String checkId;
  private Double purchasePrice;

  private Double purchaseAmount;//采购量
  private String purchaseUnit;//采购单位

  private Double iprice;  //入库价格
  private Double iamount;//入库数量

  public Double getIprice() {
    return iprice;
  }

  public void setIprice(Double iprice) {
    this.iprice = iprice;
  }

  public Double getIamount() {
    return iamount;
  }

  public void setIamount(Double iamount) {
    this.iamount = iamount;
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public String getCheckId() {
    return checkId;
  }

  public void setCheckId(String checkId) {
    this.checkId = checkId;
  }

  public String getOrderTimeCreatedStr() {
    return orderTimeCreatedStr;
  }

  public void setOrderTimeCreatedStr(String orderTimeCreatedStr) {
    this.orderTimeCreatedStr = orderTimeCreatedStr;
  }

  public ItemIndexDTO[] getItemIndexDTOs() {
    return itemIndexDTOs;
  }

  public void setItemIndexDTOs(ItemIndexDTO[] itemIndexDTOs) {
    this.itemIndexDTOs = itemIndexDTOs;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
    if(supplierId !=null){
     this.supplierIdStr = supplierId.toString();
    }

  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(StringUtil.valueOf(id));
  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  public Double getReturnAbleAmount() {
    return returnAbleAmount;
  }

  public void setReturnAbleAmount(Double returnAbleAmount) {
    this.returnAbleAmount = returnAbleAmount;
  }

  public Long getPurchaseReturnId() {
    return purchaseReturnId;
  }

  public void setPurchaseReturnId(Long purchaseReturnId) {
    this.purchaseReturnId = purchaseReturnId;
  }

  public Double getPrice() {
    if(price==null) return 0d;
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
    if(total==null) return 0d;
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

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

	public String getVehicleInfo() {
    vehicleInfo = "";
    if (null != vehicleBrand && !vehicleBrand.isEmpty()) vehicleInfo += vehicleBrand;
    if (null != vehicleModel && !vehicleModel.isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" + vehicleModel;
    } else if (null != vehicleModel && !vehicleModel.isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += vehicleModel;
    }

    if (null != vehicleYear && !vehicleYear.isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" + vehicleYear;
    } else if (null != vehicleYear && !vehicleYear.isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += vehicleYear;
    }

    if (null != vehicleEngine && !vehicleEngine.isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" + vehicleEngine;
    } else if (null != vehicleEngine && !vehicleEngine.isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += vehicleEngine;
    }
    return vehicleInfo;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PurchaseReturnItemDTO that = (PurchaseReturnItemDTO) o;

    if (Double.compare(that.amount, amount) != 0) return false;
    if (Double.compare(that.price, price) != 0) return false;
    if (Double.compare(that.total, total) != 0) return false;
    if (barcode != null ? !barcode.equals(that.barcode) : that.barcode != null) return false;
    if (brand != null ? !brand.equals(that.brand) : that.brand != null) return false;
    if (memo != null ? !memo.equals(that.memo) : that.memo != null) return false;
    if (model != null ? !model.equals(that.model) : that.model != null) return false;
    if (productName != null ? !productName.equals(that.productName) : that.productName != null) return false;
    if (productVehicleStatus != null ? !productVehicleStatus.equals(that.productVehicleStatus) : that.productVehicleStatus != null)
      return false;
    if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;
    if (returnAbleAmount != null ? !returnAbleAmount.equals(that.returnAbleAmount) : that.returnAbleAmount != null)
      return false;
    if (sellUnit != null ? !sellUnit.equals(that.sellUnit) : that.sellUnit != null) return false;
    if (spec != null ? !spec.equals(that.spec) : that.spec != null) return false;
    if (storageUnit != null ? !storageUnit.equals(that.storageUnit) : that.storageUnit != null) return false;
    if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;
    if (vehicleBrand != null ? !vehicleBrand.equals(that.vehicleBrand) : that.vehicleBrand != null) return false;
    if (vehicleEngine != null ? !vehicleEngine.equals(that.vehicleEngine) : that.vehicleEngine != null) return false;
    if (vehicleInfo != null ? !vehicleInfo.equals(that.vehicleInfo) : that.vehicleInfo != null) return false;
    if (vehicleModel != null ? !vehicleModel.equals(that.vehicleModel) : that.vehicleModel != null) return false;
    if (vehicleYear != null ? !vehicleYear.equals(that.vehicleYear) : that.vehicleYear != null) return false;

    return true;
  }


  public void setSupplier(ItemIndexDTO itemIndexDTO) {
    this.setSupplierId(itemIndexDTO.getCustomerId());
    this.supplierName = itemIndexDTO.getCustomerOrSupplierName();
  }

  public void setProduct(ProductDTO productDTO) {
	  if(productDTO == null){
		  return;
	  }
    this.setProductId(productDTO.getProductLocalInfoId());
    this.productName = productDTO.getName();
    this.brand = productDTO.getBrand();
    this.model = productDTO.getModel();
    this.spec = productDTO.getSpec();
    this.vehicleBrand = productDTO.getProductVehicleBrand();
    this.vehicleModel = productDTO.getProductVehicleModel();
    this.price = productDTO.getPurchasePrice() ==null?0:productDTO.getPurchasePrice();
    this.unit = productDTO.getSellUnit();
    this.sellUnit = productDTO.getSellUnit();
    this.storageUnit = productDTO.getStorageUnit();
    this.rate = productDTO.getRate();
	  this.tradePrice = productDTO.getTradePrice();
	  this.storageBin = productDTO.getStorageBin();
	  this.commodityCode = productDTO.getCommodityCode();
  }

  public ItemIndexDTO toItemIndexDTO(PurchaseReturnDTO purchaseReturnDTO){
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();
    itemIndexDTO.setShopId(purchaseReturnDTO.getShopId());
    itemIndexDTO.setCustomerId(purchaseReturnDTO.getSupplierId());
    itemIndexDTO.setCustomerOrSupplierName(purchaseReturnDTO.getSupplier());
    itemIndexDTO.setOrderId(purchaseReturnDTO.getId());
    itemIndexDTO.setOrderReceiptNo(purchaseReturnDTO.getReceiptNo());
    itemIndexDTO.setOrderStatus(purchaseReturnDTO.getStatus());
    itemIndexDTO.setOrderTimeCreated(purchaseReturnDTO.getVestDate() == null ? purchaseReturnDTO.getCreationDate() : purchaseReturnDTO.getVestDate());
    itemIndexDTO.setOrderTotalAmount(this.getTotal());
    itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemCostPrice());
    itemIndexDTO.setStorehouseId(purchaseReturnDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(purchaseReturnDTO.getStorehouseName());

    itemIndexDTO.setItemPrice(this.getPrice());
    itemIndexDTO.setItemMemo(this.getMemo());
    itemIndexDTO.setOrderType(OrderTypes.RETURN);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);

    return itemIndexDTO;
  }

  public PurchaseReturnItemDTO fromItemIndexDTO(ItemIndexDTO itemIndexDTO) {
    super.fromBcgogoItemIndexDTO(itemIndexDTO);
    setPrice(itemIndexDTO.getItemPrice());
    setMemo(itemIndexDTO.getItemMemo());
    if (getPrice() != null && getAmount() != null) {
      setTotal(getPrice() * getAmount());
    }
    return this;
  }

  public ItemIndexDTO toInOutRecordDTO(PurchaseReturnDTO purchaseReturnDTO) {
    ItemIndexDTO itemIndexDTO = toItemIndexDTO(purchaseReturnDTO);
    itemIndexDTO.setItemType(ItemTypes.OUT);
    itemIndexDTO.setInOutRecordId(this.getId());
    itemIndexDTO.setRelatedSupplierId(purchaseReturnDTO.getSupplierId());
    itemIndexDTO.setRelatedSupplierName(purchaseReturnDTO.getSupplier());
    itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemCostPrice());
    itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
    return itemIndexDTO;
  }

  @Override
  public String toString() {
    return "PurchaseReturnItemDTO{" +
        "purchaseReturnId=" + purchaseReturnId +
        ", price=" + price +
        ", total=" + total +
        ", memo='" + memo + '\'' +
        ", returnAbleAmount=" + returnAbleAmount +
        ", productVehicleStatus=" + productVehicleStatus +
        ", barcode='" + barcode + '\'' +
        ", vehicleInfo='" + vehicleInfo + '\'' +
        ", supplierId=" + supplierId +
        ", supplierName='" + supplierName + '\'' +
        ", supplierIdStr='" + supplierIdStr + '\'' +
        ", orderTimeCreatedStr='" + orderTimeCreatedStr + '\'' +
        ", itemIndexDTOs=" + (itemIndexDTOs == null ? null : Arrays.asList(itemIndexDTOs)) +
        ", checkId='" + checkId + '\'' +
        ", purchasePrice=" + purchasePrice +
        ", purchaseAmount=" + purchaseAmount +
        ", purchaseUnit='" + purchaseUnit + '\'' +
        ", productInfo='" + productInfo + '\'' +
        ", iprice=" + iprice +
        ", iamount=" + iamount +
        '}' + super.toString();
  }

  public Double getPurchaseAmount() {
    return purchaseAmount;
  }

  public void setPurchaseAmount(Double purchaseAmount) {
    this.purchaseAmount = purchaseAmount;
  }

  public String getPurchaseUnit() {
    return purchaseUnit;
  }

  public void setPurchaseUnit(String purchaseUnit) {
    this.purchaseUnit = purchaseUnit;
  }
}
