package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.utils.*;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class SupplierInventoryDTO implements Serializable {
  private Long id;
  private Long productId;
  private Long[] productIds;
  private Long supplierId;
  private String supplierIdStr;
  private Long storehouseId;
  private Long shopId;
  private Double totalInStorageAmount;
  private Double remainAmount;
  private String unit;
  private Double maxStoragePrice;
  private Double minStoragePrice;
  private Double averageStoragePrice;
  private Long lastStorageTime;    //最后入库时间
  private String lastStorageTimeStr;
  private Double lastStoragePrice;
  private OutStorageSupplierType supplierType;
  private String supplierName;
  private String supplierContact;
  private String supplierMobile;
  private Double changeAmount;        // Service中专入值用作更新remainAmount
  private Double totalInStorageChangeAmount;  //service中传入值用作更新totalInStorageAmount
  private Long lastPurchaseInventoryOrderId;
  private Double lastStorageAmount;
  private OrderTypes orderType;
  private OrderStatus orderStatus;
  private YesNo disabled;
  private Double tempAmount; //记录在线采购打通中间状态的数量

  public SupplierInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO, PurchaseInventoryItemDTO itemDTO) {
    if(itemDTO != null){
      productId = itemDTO.getProductId();
    }
    if (purchaseInventoryDTO != null) {
      shopId = purchaseInventoryDTO.getShopId();
      supplierId = purchaseInventoryDTO.getSupplierId();
      supplierName = purchaseInventoryDTO.getSupplier();
      supplierContact = purchaseInventoryDTO.getContact();
      supplierMobile = purchaseInventoryDTO.getMobile();
      lastStorageTime = purchaseInventoryDTO.getVestDate();
      supplierType = OutStorageSupplierType.NATIVE_SUPPLIER;
      storehouseId = purchaseInventoryDTO.getStorehouseId();
      lastPurchaseInventoryOrderId = purchaseInventoryDTO.getId();
      lastStorageTime = purchaseInventoryDTO.getVestDate();
    }
    orderType = OrderTypes.INVENTORY;
  }

  public SupplierInventoryDTO(){

    }

  public SupplierInventoryDTO(String productSupplierDetailJson) {
       Map<String, String> map = JsonUtil.jsonToStringMap(productSupplierDetailJson);
       if (StringUtils.isNotBlank(map.get("supplier_id")))
         this.setSupplierId(Long.valueOf(map.get("supplier_id")));
       if (StringUtils.isNotBlank(map.get("product_id"))) this.setProductId(Long.valueOf(map.get("product_id")));
       if (StringUtils.isNotBlank(map.get("name"))) this.setSupplierName(map.get("name"));
       if (StringUtils.isNotBlank(map.get("mobile"))) this.setSupplierMobile(map.get("mobile"));
       if (StringUtils.isNotBlank(map.get("contact"))) this.setSupplierContact(map.get("contact"));
       if (StringUtils.isNotBlank(map.get("lastUsedTime"))) this.setLastStorageTime(Long.valueOf(map.get("lastUsedTime")));
  }

  public SupplierInventoryDTO(SupplierInventoryDTO supplierInventoryDTO) {
    if(supplierInventoryDTO != null){


    this.id = supplierInventoryDTO.getId();
    this.productId = supplierInventoryDTO.getProductId();
    this.productIds = supplierInventoryDTO.getProductIds();
    this.supplierId = supplierInventoryDTO.getSupplierId();
    this.supplierIdStr = supplierInventoryDTO.getSupplierIdStr();
    this.storehouseId = supplierInventoryDTO.getStorehouseId();
    this.shopId = supplierInventoryDTO.getShopId();
    this.totalInStorageAmount = supplierInventoryDTO.getTotalInStorageAmount();
    this.remainAmount = supplierInventoryDTO.getRemainAmount();
    this.unit = supplierInventoryDTO.getUnit();
    this.maxStoragePrice = supplierInventoryDTO.getMaxStoragePrice();
    this.minStoragePrice = supplierInventoryDTO.getMinStoragePrice();
    this.averageStoragePrice = supplierInventoryDTO.getAverageStoragePrice();
    this.lastStorageTime = supplierInventoryDTO.getLastStorageTime();
    this.lastStorageTimeStr = supplierInventoryDTO.getLastStorageTimeStr();
    this.lastStoragePrice = supplierInventoryDTO.getLastStoragePrice();
    this.supplierType = supplierInventoryDTO.getSupplierType();
    this.supplierName = supplierInventoryDTO.getSupplierName();
    this.supplierContact = supplierInventoryDTO.getSupplierContact();
    this.supplierMobile = supplierInventoryDTO.getSupplierMobile();
    this.changeAmount = supplierInventoryDTO.getChangeAmount();
    this.totalInStorageChangeAmount = supplierInventoryDTO.getTotalInStorageChangeAmount();
    this.lastPurchaseInventoryOrderId = supplierInventoryDTO.getLastPurchaseInventoryOrderId();
    this.lastStorageAmount = supplierInventoryDTO.getLastStorageAmount();
    this.orderType = supplierInventoryDTO.getOrderType();
    this.disabled = supplierInventoryDTO.getDisabled();
    }
  }

  //入库单转化成销售单位的数量，入库价，最后入库时间
  public void addStorageInventoryChange(String sellUnit, Double changeAmount, Double purchasePrice){
    this.setUnit(sellUnit);
    if (purchasePrice != null && purchasePrice > 0.0001) {
      this.setLastStoragePrice(purchasePrice);
    }
    this.setAverageStoragePrice(purchasePrice);
    this.setChangeAmount(changeAmount);
    this.setTotalInStorageChangeAmount(changeAmount);
    this.setLastStoragePrice(purchasePrice);
    if(changeAmount != null && changeAmount > 0.0001){
      this.setLastStorageAmount(changeAmount);
    }
  }

   //入库单作废专用
  public void reduceStorageInventoryChange(String sellUnit, Double changeAmount, Double purchasePrice) {
    this.setUnit(sellUnit);
    if (purchasePrice != null && purchasePrice > 0.0001) {
      this.setLastStoragePrice(purchasePrice);
    }
    this.setChangeAmount(-NumberUtil.doubleVal(changeAmount));
    this.setTotalInStorageChangeAmount(-NumberUtil.doubleVal(changeAmount));
    if (changeAmount != null && changeAmount > 0.0001) {
      this.setLastStorageAmount(changeAmount);
    }
  }


  public String toSupplierInfoStr() {
    StringBuffer sb = new StringBuffer();
//   		PingyinInfo pingyinInfo = null;
   		if (StringUtils.isNotBlank(supplierName)) {
   			sb.append(supplierName).append(" ");
//   			try {
//   				pingyinInfo = PinyinUtil.getPingyinInfo(this.supplierName);
//   				sb.append(pingyinInfo.pingyin).append(" ");
//   				sb.append(pingyinInfo.firstLetters).append(" ");
//   			} catch (Exception e) {
//   				e.printStackTrace();
//   			}
   		}
   		if (StringUtils.isNotBlank(supplierContact)) {
   			sb.append(supplierContact).append(" ");
//   			try {
//   				pingyinInfo = PinyinUtil.getPingyinInfo(this.supplierContact);
//   				sb.append(pingyinInfo.pingyin).append(" ");
//   				sb.append(pingyinInfo.firstLetters).append(" ");
//   			} catch (Exception e) {
//   				e.printStackTrace();
//   			}
   		}
   		if (StringUtils.isNotBlank(supplierMobile)) {
   			sb.append(supplierMobile).append(" ");
   		}
   		if (supplierId != null) {
   			sb.append(supplierId.toString()).append(" ");
   		}
   		if (sb.length() > 0) {
   			sb.substring(0, sb.length() - 1);
   		}
   		return sb.toString();
  }

  public String generateProductSupplierDetail() {
    Map<String, String> map = new HashMap<String, String>();
    if (StringUtils.isNotBlank(this.supplierName)) {
      map.put("name", this.supplierName);
    }
    if (this.supplierId != null) {
      map.put("supplier_id", this.supplierId.toString());
    }
    if (this.productId != null) {
      map.put("product_id", this.productId.toString());
    }

    if (StringUtils.isNotBlank(this.supplierMobile)) {
      map.put("mobile", this.supplierMobile);
    }

    if (StringUtils.isNotBlank(this.supplierContact)) {
      map.put("contact", this.supplierContact);
    }
    if (lastStorageTime != null) {
      map.put("lastUsedTime", this.lastStorageTime.toString());
    }
    return JsonUtil.mapToJson(map);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long[] getProductIds() {
    return productIds;
  }

  public void setProductIds(Long[] productIds) {
    this.productIds = productIds;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
    if(supplierId != null){
      this.setSupplierIdStr(String.valueOf(supplierId));
    }else{
      this.setSupplierIdStr("");
    }

  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  public Double getTotalInStorageAmount() {
    return totalInStorageAmount;
  }

  public void setTotalInStorageAmount(Double totalInStorageAmount) {
    this.totalInStorageAmount = totalInStorageAmount;
  }

  public Double getRemainAmount() {
    return remainAmount;
  }

  public void setRemainAmount(Double remainAmount) {
    this.remainAmount = remainAmount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Double getMaxStoragePrice() {
    return maxStoragePrice;
  }

  public void setMaxStoragePrice(Double maxStoragePrice) {
    this.maxStoragePrice = maxStoragePrice;
  }

  public Double getMinStoragePrice() {
    return minStoragePrice;
  }

  public void setMinStoragePrice(Double minStoragePrice) {
    this.minStoragePrice = minStoragePrice;
  }

  public Double getAverageStoragePrice() {
    return averageStoragePrice;
  }

  public void setAverageStoragePrice(Double averageStoragePrice) {
    this.averageStoragePrice = averageStoragePrice;
  }

  public Long getLastStorageTime() {
    return lastStorageTime;
  }

  public void setLastStorageTime(Long lastStorageTime) {
    this.lastStorageTime = lastStorageTime;
    if(lastStorageTime != null){
      this.setLastStorageTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,lastStorageTime));
    }else {
      this.setLastStorageTimeStr("");
    }
  }

  public String getLastStorageTimeStr() {
    return lastStorageTimeStr;
  }

  public void setLastStorageTimeStr(String lastStorageTimeStr) {
    this.lastStorageTimeStr = lastStorageTimeStr;
  }

  public Double getLastStoragePrice() {
    return lastStoragePrice;
  }

  public void setLastStoragePrice(Double lastStoragePrice) {
    this.lastStoragePrice = lastStoragePrice;
  }

  public OutStorageSupplierType getSupplierType() {
    return supplierType;
  }

  public void setSupplierType(OutStorageSupplierType supplierType) {
    this.supplierType = supplierType;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getSupplierContact() {
    return supplierContact;
  }

  public void setSupplierContact(String supplierContact) {
    this.supplierContact = supplierContact;
  }

  public String getSupplierMobile() {
    return supplierMobile;
  }

  public void setSupplierMobile(String supplierMobile) {
    this.supplierMobile = supplierMobile;
  }

  public Double getChangeAmount() {
    return changeAmount;
  }

  public void setChangeAmount(Double changeAmount) {
    this.changeAmount = changeAmount;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getLastPurchaseInventoryOrderId() {
    return lastPurchaseInventoryOrderId;
  }

  public void setLastPurchaseInventoryOrderId(Long lastPurchaseInventoryOrderId) {
    this.lastPurchaseInventoryOrderId = lastPurchaseInventoryOrderId;
  }

  public Double getLastStorageAmount() {
    return lastStorageAmount;
  }

  public void setLastStorageAmount(Double lastStorageAmount) {
    this.lastStorageAmount = lastStorageAmount;
  }

  public Double getTotalInStorageChangeAmount() {
    return totalInStorageChangeAmount;
  }

  public void setTotalInStorageChangeAmount(Double totalInStorageChangeAmount) {
    this.totalInStorageChangeAmount = totalInStorageChangeAmount;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public YesNo getDisabled() {
    return disabled;
  }

  public void setDisabled(YesNo disabled) {
    this.disabled = disabled;
  }

  public Double getTempAmount() {
    return tempAmount;
  }

  public void setTempAmount(Double tempAmount) {
    this.tempAmount = tempAmount;
  }

  //用supplierId,productId,storehouseId
  public String toMapKey(Long supplierId,Long productId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append(supplierId == null ? "null":supplierId.toString());
    sb.append("_");
    sb.append(productId == null ? "null":productId.toString());
    sb.append("_");
    sb.append(storehouseId == null ? "null":storehouseId.toString());
    return sb.toString();
  }

}
