package com.bcgogo.product.productManage;

import com.bcgogo.common.Sort;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.Product.SearchInputType;
import com.bcgogo.enums.ProductRecommendType;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import sun.rmi.server.InactiveGroupException;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-27
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public class ProductSearchCondition {
  // product
  private Long productId;
  private Long shopId;
  private Long[] shopIds;
  private Long[] productIds;
  private String productName;
  private String brand;
  private String spec;
  private String model;
  private String vehicleModel;
  private String vehicleBrand;
  private String commodityCode;//商品编码
  private String productKind;//商品分类
  private ProductStatus productStatus;
  private ProductStatus salesStatus;

  private ProductRecommendType productRecommendType;
  //online
  private Long wholesalerShopId;//批发商shopId

  private Long[] wholesalerShopIds;//批发商shopId
  private String inputName;
  private Long firstCategoryId;

  private Long secondCategoryId;
  private Long thirdCategoryId;
  private Sort sortCondition;
  private String sortFiled;
  private String sort;//排序规则
  private Integer start=0;
  private Integer limit;
  private Integer page;

  private Long normalProductId;
  //crm 采购统计相关
  private ProductRelevanceStatus status;
  private String productInfo;
  private NormalProductStatType normalProductStatType;

  private String normalProductIdStr;
  private Long provinceId;
  private Long cityId;
  private Long regionId;
  private String shopName;
  private String shopVersion;

  public void clearByInputType() {
    if (SearchInputType.BRAND.toString().equals(this.getBrand())) {
      this.setModel(null);
      this.setSpec(null);
      this.setVehicleBrand(null);
      this.setVehicleModel(null);
      this.setCommodityCode(null);
    } else if (SearchInputType.SPEC.toString().equals(this.getSpec())) {
      this.setModel(null);
      this.setVehicleBrand(null);
      this.setVehicleModel(null);
      this.setCommodityCode(null);

    } else if (SearchInputType.MODEL.toString().equals(this.getModel())) {
      this.setVehicleBrand(null);
      this.setVehicleModel(null);
      this.setCommodityCode(null);

    } else if (SearchInputType.VEHICLE_BRAND.toString().equals(this.getVehicleBrand())) {
      this.setVehicleModel(null);
      this.setCommodityCode(null);

    } else if (SearchInputType.VEHICLE_MODEL.toString().equals(this.getVehicleModel())) {
      this.setCommodityCode(null);
    }
  }

  public Sort getSortCondition() {
    return sortCondition;
  }

  public void setSortCondition(Sort sortCondition) {
    this.sortCondition = sortCondition;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long[] getShopIds() {
    return shopIds;
  }

  public void setShopIds(Long[] shopIds) {
    this.shopIds = shopIds;
  }

  public Long[] getProductIds() {
    return productIds;
  }

  public void setProductIds(Long[] productIds) {
    this.productIds = productIds;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  public ProductRecommendType getProductRecommendType() {
    return productRecommendType;
  }

  public void setProductRecommendType(ProductRecommendType productRecommendType) {
    this.productRecommendType = productRecommendType;
  }

  public ProductStatus getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }

  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  public Long[] getWholesalerShopIds() {
    return wholesalerShopIds;
  }

  public void setWholesalerShopIds(Long[] wholesalerShopIds) {
    this.wholesalerShopIds = wholesalerShopIds;
  }

  public String getSortFiled() {
    return sortFiled;
  }

  public void setSortFiled(String sortFiled) {
    this.sortFiled = sortFiled;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getInputName() {
    return inputName;
  }

  public void setInputName(String inputName) {
    this.inputName = inputName;
  }

  public Long getFirstCategoryId() {
    return firstCategoryId;
  }

  public void setFirstCategoryId(Long firstCategoryId) {
    this.firstCategoryId = firstCategoryId;
  }

  public Long getSecondCategoryId() {
    return secondCategoryId;
  }

  public void setSecondCategoryId(Long secondCategoryId) {
    this.secondCategoryId = secondCategoryId;
  }

  public Long getThirdCategoryId() {
    return thirdCategoryId;
  }

  public void setThirdCategoryId(Long thirdCategoryId) {
    this.thirdCategoryId = thirdCategoryId;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  public ProductRelevanceStatus getStatus() {
    return status;
  }

  public void setStatus(ProductRelevanceStatus status) {
    this.status = status;
  }

  public String getProductInfo() {
    return productInfo;
  }

  public void setProductInfo(String productInfo) {
    this.productInfo = productInfo;
  }

  public NormalProductStatType getNormalProductStatType() {
    return normalProductStatType;
  }

  public void setNormalProductStatType(NormalProductStatType normalProductStatType) {
    this.normalProductStatType = normalProductStatType;
  }

  public String getNormalProductIdStr() {
    return normalProductIdStr;
  }

  public void setNormalProductIdStr(String normalProductIdStr) {
    this.normalProductIdStr = normalProductIdStr;
  }

  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public Long getCityId() {

    return cityId;
  }

  public void setCityId(Long cityId) {
    this.cityId = cityId;
  }

  public Long getProvinceId() {

    return provinceId;
  }

  public void setProvinceId(Long provinceId) {
    this.provinceId = provinceId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopVersion() {
    return shopVersion;
  }

  public void setShopVersion(String shopVersion) {
    this.shopVersion = shopVersion;
  }
}
