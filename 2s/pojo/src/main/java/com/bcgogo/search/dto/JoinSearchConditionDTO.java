package com.bcgogo.search.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * User: xzhu
 * Date: 12-5-11
 * Time: 上午9:15
 * 搜索条件DTO
 */
public class JoinSearchConditionDTO {
  private Long shopId;
  private String fromColumn;
  private String toColumn;
  private String fromIndex;

  // join  客户供应商
  private String customerOrSupplierName;
  private String customerOrSupplierInfo;
  private String[] customerOrSupplier;
  private Long areaId;
  private boolean includeBasic;

  //客户供应商 join  orderItem

  private String productSearchWord;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String productVehicleModel;
  private String productVehicleBrand;
  private String commodityCode;//商品编码
  private String[] orderTypes;
  private ItemTypes itemTypes;
  private String[] orderStatus;

  public String getCustomerOrSupplierInfo() {
    return customerOrSupplierInfo;
  }

  public void setCustomerOrSupplierInfo(String customerOrSupplierInfo) {
    this.customerOrSupplierInfo = customerOrSupplierInfo;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  public boolean isEmptyOfProductInfo() {
    return StringUtil.isAllEmpty(productSearchWord,productName, productBrand, productSpec, productModel, productVehicleBrand, productVehicleModel,commodityCode);
  }
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getFromColumn() {
    return fromColumn;
  }

  public void setFromColumn(String fromColumn) {
    this.fromColumn = fromColumn;
  }

  public String getToColumn() {
    return toColumn;
  }

  public void setToColumn(String toColumn) {
    this.toColumn = toColumn;
  }

  public String getFromIndex() {
    return fromIndex;
  }

  public void setFromIndex(String fromIndex) {
    this.fromIndex = fromIndex;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public String[] getCustomerOrSupplier() {
    return customerOrSupplier;
  }

  public void setCustomerOrSupplier(String[] customerOrSupplier) {
    this.customerOrSupplier = customerOrSupplier;
  }

  public boolean getIncludeBasic() {
    return includeBasic;
  }

  public boolean isIncludeBasic() {
    return includeBasic;
  }

  public void setIncludeBasic(boolean includeBasic) {
    this.includeBasic = includeBasic;
  }

  public String getProductSearchWord() {
    return productSearchWord;
  }

  public void setProductSearchWord(String productSearchWord) {
    this.productSearchWord = productSearchWord;
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

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public ItemTypes getItemTypes() {
    return itemTypes;
  }

  public void setItemTypes(ItemTypes itemTypes) {
    this.itemTypes = itemTypes;
  }

  public String[] getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(String[] orderTypes) {
    this.orderTypes = orderTypes;
  }

  public String[] getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String[] orderStatus) {
    this.orderStatus = orderStatus;
  }
}
