package com.bcgogo.product.dto;

import com.bcgogo.enums.config.VehicleSelectBrandModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-24
 * Time: 下午3:19
 * To change this template use File | Settings | File Templates.
 */
public class NormalProductDTO {
  private Long id;
  private Long productCategoryId;
  private String commodityCode;
  private String unit;
  private String brand;
  private String model;
  private String spec;
  private String vehicleModel;
  private String vehicleBrand;
  private String productName;
  private Long vehicleModelId;
  private Long vehicleBrandId;
  private String vehicleModelIds;
  private String vehicleBrandModelInfo;
  private String productCategoryName;
  private Long productFirstCategoryId;
  private String productFirstCategoryName;
  private Long productSecondCategoryId;
  private String productSecondCategoryName;
  private VehicleSelectBrandModel selectBrandModel; //注册时选择车型时 是否是全部车型
  private Boolean selectAllBrandModel;
  private Double price;
  private Integer bindingShopProductCount;

  public Integer getBindingShopProductCount() {
    return bindingShopProductCount;
  }

  public void setBindingShopProductCount(Integer bindingShopProductCount) {
    this.bindingShopProductCount = bindingShopProductCount;
  }

  public Boolean getSelectAllBrandModel() {
    return selectAllBrandModel;
  }

  public void setSelectAllBrandModel(Boolean selectAllBrandModel) {
    this.selectAllBrandModel = selectAllBrandModel;
  }

  public String getVehicleModelIds() {
    return vehicleModelIds;
  }

  public void setVehicleModelIds(String vehicleModelIds) {
    this.vehicleModelIds = vehicleModelIds;
  }

  public String getVehicleBrandModelInfo() {
    return vehicleBrandModelInfo;
  }

  public void setVehicleBrandModelInfo(String vehicleBrandModelInfo) {
    this.vehicleBrandModelInfo = vehicleBrandModelInfo;
  }

  public VehicleSelectBrandModel getSelectBrandModel() {
    return selectBrandModel;
  }

  public void setSelectBrandModel(VehicleSelectBrandModel selectBrandModel) {
    this.selectBrandModel = selectBrandModel;
  }

  public Long getProductFirstCategoryId() {
    return productFirstCategoryId;
  }

  public void setProductFirstCategoryId(Long productFirstCategoryId) {
    this.productFirstCategoryId = productFirstCategoryId;
  }

  public Long getProductSecondCategoryId() {
    return productSecondCategoryId;
  }

  public void setProductSecondCategoryId(Long productSecondCategoryId) {
    this.productSecondCategoryId = productSecondCategoryId;
  }

  public String getProductFirstCategoryName() {
    return productFirstCategoryName;
  }

  public void setProductFirstCategoryName(String productFirstCategoryName) {
    this.productFirstCategoryName = productFirstCategoryName;
  }

  public String getProductSecondCategoryName() {
    return productSecondCategoryName;
  }

  public void setProductSecondCategoryName(String productSecondCategoryName) {
    this.productSecondCategoryName = productSecondCategoryName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
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

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductCategoryName() {
    return productCategoryName;
  }

  public void setProductCategoryName(String productCategoryName) {
    this.productCategoryName = productCategoryName;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public static Map<Long,NormalProductDTO> listToMap(List<NormalProductDTO> list)
  {
    Map<Long,NormalProductDTO> map = new HashMap<Long, NormalProductDTO>();

    if(CollectionUtils.isEmpty(list))
    {
      return map;
    }

    for(NormalProductDTO normalProductDTO : list)
    {
      map.put(normalProductDTO.getId(),normalProductDTO);
    }

    return map;
  }
}
