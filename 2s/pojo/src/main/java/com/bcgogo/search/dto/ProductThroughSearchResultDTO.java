package com.bcgogo.search.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ProductThroughSearchResultDTO {
  private static final Logger LOG = LoggerFactory.getLogger(ProductThroughSearchResultDTO.class);
  private Long shopId;         //店面ID
  private Long orderId;        //单子ID
  private String orderIdStr;        //单子ID
  private String orderType;    // 单子类型
  private String orderTypeValue;    // 单子类型 Value
  private String relatedSupplierName;
  private String relatedCustomerName;
  private Long createdTime;
  private String createdTimeStr;
  private String orderReceiptNo; //单据号

  private ItemTypes itemType;
  private Long itemId;
  private Double itemCount;

    private Double itemPrice;
    private Double itemTotalCostPrice;
    private Double itemTotal;

  private Long productId;
  private String productName;
  private String productBrand;
  private String productModel;
  private String productSpec;
  private String productVehicleBrand;          //产品本身带的车辆信息
  private String productVehicleModel;
  private String commodityCode;
  private String unit;
  private String storehouseName;

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
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
    this.orderIdStr = String.valueOf(orderId);
    this.orderId = orderId;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    try {
      //转换 orderType
      orderTypeValue = OrderTypes.valueOf(orderType).getName();
      this.orderType = orderType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTimeStr = DateUtil.dateLongToStr(createdTime);
    this.createdTime = createdTime;
  }

  public String getCreatedTimeStr() {
    return createdTimeStr;
  }

  public void setCreatedTimeStr(String createdTimeStr) {
    this.createdTimeStr = createdTimeStr;
  }

  public String getOrderTypeValue() {
    return orderTypeValue;
  }

  public void setOrderTypeValue(String orderTypeValue) {
    this.orderTypeValue = orderTypeValue;
  }


  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }


  public String getOrderReceiptNo() {
    return orderReceiptNo;
  }

  public void setOrderReceiptNo(String orderReceiptNo) {
    this.orderReceiptNo = orderReceiptNo;
  }

  public ItemTypes getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    if (StringUtils.isEmpty(itemType)) return;
    try {
      this.itemType = ItemTypes.valueOf(itemType);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public Double getItemCount() {
    return itemCount;
  }

  public void setItemCount(Double itemCount) {
    this.itemCount = itemCount;
  }


    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Double getItemTotalCostPrice() {
        return itemTotalCostPrice;
    }

    public void setItemTotalCostPrice(Double itemTotalCostPrice) {
        this.itemTotalCostPrice = itemTotalCostPrice;
    }

    public Double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(Double itemTotal) {
        this.itemTotal = itemTotal;
    }


    public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public String getRelatedSupplierName() {
    return relatedSupplierName;
  }

  public void setRelatedSupplierName(String relatedSupplierName) {
    this.relatedSupplierName = relatedSupplierName;
  }

  public String getRelatedCustomerName() {
    return relatedCustomerName;
  }

  public void setRelatedCustomerName(String relatedCustomerName) {
    this.relatedCustomerName = relatedCustomerName;
  }

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }
}
