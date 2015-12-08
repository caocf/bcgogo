package com.bcgogo.product.model;

import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductDTO;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product")
public class Product extends LongIdentifier {
  private Long kindId;
  private String brand; // 品牌
  private String model; // 型号
  private String spec;  // 规格
  private String name;  // 品名
  private String nameEn;
  private String mfr;
  private String mfrEn;
  private Integer originNo;
  private String origin;
  private String unit;
  private Long parentId;
  private Integer checkStatus;
  private Long state;
  private String productVehicleBrand; // 车辆品牌
  private String productVehicleModel; // 车辆型号
  private String productVehicleYear;
  private String productVehicleEngine;
  private String memo;
  private String firstLetter;      //产品名称首字母
  private String firstLetterCombination;   //产品名称首字母组合
  private Long shopId;
  private Integer productVehicleStatus;
  private String barcode;
  private String commodityCode;//商品编码
  private ProductStatus status;   //商品状态
  private String description; // 商品描述
  private Long normalProductId;
  private ProductRelevanceStatus relevanceStatus;
  public Product() {
  }

  public Product fromDTO(ProductDTO productDTO){
    if(productDTO == null){
      return this;
    }
    setBrand(productDTO.getBrand());
    setKindId(productDTO.getKindId());
    setMemo(productDTO.getMemo());
    setMfr(productDTO.getMfr());
    setMfrEn(productDTO.getMfrEn());
    setModel(productDTO.getModel());
    setName(productDTO.getName());
    setNameEn(productDTO.getNameEn());
    setProductVehicleStatus(productDTO.getProductVehicleStatus());
    setProductVehicleBrand(productDTO.getProductVehicleBrand());
    setProductVehicleModel(productDTO.getProductVehicleModel());
    setProductVehicleEngine(productDTO.getProductVehicleEngine());
    setProductVehicleYear(productDTO.getProductVehicleYear());
    setOrigin(productDTO.getOrigin());
    setOriginNo(productDTO.getOriginNo());
    setShopId(productDTO.getShopId());
    setSpec(productDTO.getSpec());
    setCheckStatus(productDTO.getCheckStatus());
    setParentId(productDTO.getParentId());
    setState(productDTO.getState());
    setUnit(productDTO.getUnit());
    if (StringUtils.isNotEmpty(productDTO.getFirstLetter())) {
      setFirstLetter(productDTO.getFirstLetter().toLowerCase());
      setFirstLetterCombination(productDTO.getFirstLetterCombination().toLowerCase());
    }
    setBarcode(productDTO.getBarcode());
    setStatus(productDTO.getStatus());
    setId(productDTO.getId());
    setCommodityCode(productDTO.getCommodityCode());
    setNormalProductId(productDTO.getNormalProductId());
    setRelevanceStatus(productDTO.getRelevanceStatus());
    setDescription(productDTO.getDescription());
    return this;
  }

  public ProductDTO toDTO() {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setShopId(this.getShopId());
    productDTO.setId(this.getId());
    productDTO.setVersion(getVersion());
    productDTO.setName(this.getName());
    productDTO.setBrand(this.getBrand());
    productDTO.setModel(this.getModel());
    productDTO.setSpec(this.getSpec());
    productDTO.setFirstLetter(this.getFirstLetter());
    productDTO.setFirstLetterCombination(this.getFirstLetterCombination());
    productDTO.setProductVehicleBrand(this.getProductVehicleBrand());
    productDTO.setProductVehicleModel(this.getProductVehicleModel());
    productDTO.setProductVehicleYear(this.getProductVehicleYear());
    productDTO.setProductVehicleEngine(this.getProductVehicleEngine());
    productDTO.setProductVehicleStatus(this.getProductVehicleStatus());
    productDTO.setVehicleBrand(this.getProductVehicleBrand());
    productDTO.setVehicleEngine(this.getProductVehicleEngine());
    productDTO.setVehicleModel(this.getProductVehicleModel());
    productDTO.setVehicleYear(this.getProductVehicleYear());
    productDTO.setBarcode(this.getBarcode());
    productDTO.setCheckStatus(this.getCheckStatus());
    productDTO.setKindId(this.getKindId());
    productDTO.setMemo(this.getMemo());
    productDTO.setMfr(this.getMfr());
    productDTO.setMfrEn(this.getMfrEn());
    productDTO.setNameEn(this.getNameEn());
    productDTO.setOrigin(this.getOrigin());
    productDTO.setOriginNo(this.getOriginNo());
    productDTO.setParentId(this.getParentId());
    productDTO.setState(this.getState());
    productDTO.setUnit(this.getUnit());
    productDTO.setCommodityCode(this.getCommodityCode());
    productDTO.setStatus(this.getStatus());
    productDTO.setNormalProductId(this.getNormalProductId());
    productDTO.setRelevanceStatus(this.getRelevanceStatus());
    productDTO.setDescription(this.getDescription());
    productDTO.generateProductInfo();
    return productDTO;
  }

  @Column(name = "product_vehicle_year", length = 10)
  public String getProductVehicleYear() {
    return productVehicleYear;
  }

  public void setProductVehicleYear(String productVehicleYear) {
    this.productVehicleYear = productVehicleYear;
  }

  @Column(name = "product_vehicle_engine", length = 10)
  public String getProductVehicleEngine() {
    return productVehicleEngine;
  }

  public void setProductVehicleEngine(String productVehicleEngine) {
    this.productVehicleEngine = productVehicleEngine;
  }

  @Column(name = "product_vehicle_brand", length = 50)
  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  @Column(name = "product_vehicle_model", length = 50)
  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  @Column(name = "first_letter", length = 100)
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  @Column(name = "first_letter_combination", length = 200)
  public String getFirstLetterCombination() {
    return firstLetterCombination;
  }

  public void setFirstLetterCombination(String firstLetterCombination) {
    this.firstLetterCombination = firstLetterCombination;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "kind_id")
  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  @Column(name = "brand", length = 200)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model", length = 200)
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "spec", length = 2000)
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "name", length = 200)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "name_en", length = 200)
  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  @Column(name = "mfr", length = 200)
  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
  }

  @Column(name = "mfr_en", length = 200)
  public String getMfrEn() {
    return mfrEn;
  }

  public void setMfrEn(String mfrEn) {
    this.mfrEn = mfrEn;
  }

  @Column(name = "origin_no")
  public Integer getOriginNo() {
    return originNo;
  }

  public void setOriginNo(Integer originNo) {
    this.originNo = originNo;
  }

  @Column(name = "origin", length = 200)
  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  @Column(name = "unit", length = 200)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "memo", length = 2000)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "check_status")
  public Integer getCheckStatus() {
    return checkStatus;
  }

  public void setCheckStatus(Integer checkStatus) {
    this.checkStatus = checkStatus;
  }

  @Column(name = "product_vehicle_status")
  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  @Column(name = "barcode", length = 20)
  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

	@Column(name = "commodity_code" ,length = 50)
	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		if(StringUtils.isNotBlank(commodityCode)){
			this.commodityCode = commodityCode.trim().toUpperCase();
		}else {
			this.commodityCode = null;
		}
	}

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

  @Column(name="normal_product_id")
  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  @Column(name="description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name="relevance_status")
  @Enumerated(EnumType.STRING)
  public ProductRelevanceStatus getRelevanceStatus() {
    return relevanceStatus;
  }

  public void setRelevanceStatus(ProductRelevanceStatus relevanceStatus) {
    this.relevanceStatus = relevanceStatus;
  }
}
