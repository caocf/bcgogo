package com.bcgogo.search.model;

import com.bcgogo.enums.ProductStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;

import javax.persistence.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 12/31/11
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inventory_search_index")
public class InventorySearchIndex extends LongIdentifier {
  public InventorySearchIndex() {
  }

  public void createInventorySearchIndex(InventoryDTO inventoryDTO,ProductDTO productDTO){
    if(inventoryDTO!=null){
      this.setAmount(inventoryDTO.getAmount());
      this.setUnit(inventoryDTO.getUnit());
      this.setPurchasePrice(inventoryDTO.getLatestInventoryPrice());
      this.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
      this.setUpperLimit(inventoryDTO.getUpperLimit());
      this.setLowerLimit(inventoryDTO.getLowerLimit());
      this.setRecommendedPrice(inventoryDTO.getSalesPrice());
    }
    if(productDTO != null){
      this.setParentProductId(productDTO.getId());
      this.setProductId(productDTO.getProductLocalInfoId());
      this.setCommodityCode(productDTO.getCommodityCode());
      this.setModel(productDTO.getProductVehicleModel());
      this.setEngine(productDTO.getProductVehicleEngine());
      this.setBrand(productDTO.getProductVehicleBrand());
      this.setYear(productDTO.getProductVehicleYear());
      this.setProductModel(productDTO.getModel());
      this.setProductName(productDTO.getName());
      this.setProductSpec(productDTO.getSpec());
      this.setProductBrand(productDTO.getBrand());
      this.setProductVehicleStatus(productDTO.getProductVehicleStatus());
      this.setShopId(productDTO.getShopId());
      this.setBarcode(productDTO.getBarcode());
      this.setEditDate(System.currentTimeMillis());
    }
  }
  public void setId(Long id) {
    super.setId(id);
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "product_name", length = 200)
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "product_brand", length = 200)
  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  @Column(name = "product_spec", length = 30)
  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  @Column(name = "product_model", length = 30)
  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  @Column(name = "brand", length = 30)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model", length = 30)
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "year", length = 30)
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  @Column(name = "engine", length = 30)
  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "product_vehicle_status")
  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }

  @Column(name = "parent_product_id")
  public Long getParentProductId() {
    return parentProductId;
  }

  public void setParentProductId(Long parentProductId) {
    this.parentProductId = parentProductId;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "purchase_price")
  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  @Column(name = "barcode")
  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  @Column(name = "recommended_price")
  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }


  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

   @Column(name = "lower_limit")
  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  @Column(name = "upper_limit")
  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

		@Column(name = "commodity_code" ,length = 50)
	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		if(StringUtils.isNotBlank(commodityCode)){
			this.commodityCode = commodityCode;
		}else {
			this.commodityCode = null;
		}

	}

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	public ProductStatus getStatus() {
		return status;
	}

  @Column(name = "kind_name")
  public String getkindName(){
    return kindName;
  }

  public void setKindName(String kindName){
    this.kindName = kindName;
  }

	public void setStatus(ProductStatus status) {
		this.status = status;
	}
  @Column(name = "inventory_average_price")
  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }


  private Long shopId;         //店面ID
  private Long productId;
  private Long editDate; // 最新入库时间
  private Integer productVehicleStatus;//产品适用车型。0：专车专用；1：通用车款；2多款
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String brand;
  private String model;
  private String year;
  private String engine;
  private Double amount;//库存量
  private Long parentProductId;
  private Double price;
  private Double purchasePrice;
  private Double recommendedPrice;
  private String barcode;
  private String unit;
  private Double lowerLimit;
  private Double upperLimit;
	private String commodityCode;//商品编码
	private ProductStatus status;   //商品状态
  private String kindName;     //商品分类名称，用于库存查询页面的显示
  private Double inventoryAveragePrice;

  public SolrDocument toSolrDocument() throws IOException {
    SolrDocument doc = new SolrDocument();
    doc.addField("id", parentProductId);
    doc.addField("product_id", productId);  //to add productLocalInfoId
    doc.addField("shop_id", shopId == null ? 1L : shopId);
    doc.addField("isBasicData", (shopId != null && shopId.longValue() == 1L));
    doc.addField("inventory_amount", amount);
    doc.addField("purchase_price", purchasePrice);
    doc.addField("inventoryAveragePrice", inventoryAveragePrice);
    if (purchasePrice != null && amount != null) {
      doc.addField("inventory_price", purchasePrice * amount);
    } else {
      doc.addField("inventory_price", 0);
    }
    doc.addField("lastmodified", this.getLastModified());
    doc.addField("recommendedprice", recommendedPrice);
    doc.addField("product_name", productName);
    PingyinInfo pingyinInfo = null;
    if (!StringUtils.isBlank(productName)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(productName);
      doc.addField("product_name_fl", pingyinInfo.firstLetters);
      doc.addField("product_name_py", pingyinInfo.pingyin);
    }
    doc.addField("product_brand", productBrand);
    if (!StringUtils.isBlank(productBrand)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(productBrand);
      doc.addField("product_brand_fl", pingyinInfo.firstLetters);
      doc.addField("product_brand_py", pingyinInfo.pingyin);
    }
    doc.addField("product_model", productModel);
    if (!StringUtils.isBlank(productModel)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(productModel);
      doc.addField("product_model_fl", pingyinInfo.firstLetters);
      doc.addField("product_model_py", pingyinInfo.pingyin);
    }
    doc.addField("product_spec", productSpec);
    if (!StringUtils.isBlank(productSpec)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(productSpec);
      doc.addField("product_spec_fl", pingyinInfo.firstLetters);
      doc.addField("product_spec_py", pingyinInfo.pingyin);
    }
    doc.addField("product_vehicle_brand", brand);
    if (!StringUtils.isBlank(brand)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(brand);
      doc.addField("product_vehicle_brand_fl", pingyinInfo.firstLetters);
      doc.addField("product_vehicle_brand_py", pingyinInfo.pingyin);
    }
    doc.addField("product_vehicle_model", model);
    if (!StringUtils.isBlank(model)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(model);
      doc.addField("product_vehicle_model_fl", pingyinInfo.firstLetters);
      doc.addField("product_vehicle_model_py", pingyinInfo.pingyin);
    }
    doc.addField("product_vehicle_year", year);
    doc.addField("product_vehicle_engine", engine);
    doc.addField("product_vehicle_status", productVehicleStatus);
    return doc;
  }


  public InventorySearchIndexDTO toDTO() {
    InventorySearchIndexDTO inventorySearchIndexDTO = new InventorySearchIndexDTO();
    inventorySearchIndexDTO.setId(this.getId());
    inventorySearchIndexDTO.setAmount(this.getAmount());
    inventorySearchIndexDTO.setBarcode(this.getBarcode());
    inventorySearchIndexDTO.setBrand(this.getBrand());
    inventorySearchIndexDTO.setEditDate(this.getEditDate());
    if (this.getEditDate() != null && this.getEditDate() > 0L)
    inventorySearchIndexDTO.setEditDateStr(DateUtil.convertDateLongToDateString(
          DateUtil.DATE_STRING_FORMAT_DEFAULT, this.getEditDate()));
    inventorySearchIndexDTO.setEngine(this.getEngine());
    inventorySearchIndexDTO.setModel(this.getModel());
    inventorySearchIndexDTO.setParentProductId(this.getParentProductId());
    inventorySearchIndexDTO.setPrice(this.getPrice());
    inventorySearchIndexDTO.setProductBrand(this.getProductBrand());
    inventorySearchIndexDTO.setProductId(this.getProductId());
    inventorySearchIndexDTO.setProductModel(this.getProductModel());
    inventorySearchIndexDTO.setProductName(this.getProductName());
    inventorySearchIndexDTO.setProductSpec(this.getProductSpec());
    inventorySearchIndexDTO.setProductVehicleStatus(this.getProductVehicleStatus());
    inventorySearchIndexDTO.setPurchasePrice(this.getPurchasePrice());
    inventorySearchIndexDTO.setShopId(this.getShopId());
    inventorySearchIndexDTO.setYear(this.getYear());
    inventorySearchIndexDTO.setRecommendedPrice(this.getRecommendedPrice());
    inventorySearchIndexDTO.setUnit(this.getUnit());
    inventorySearchIndexDTO.setSellUnit(this.getUnit());
    inventorySearchIndexDTO.setLastModified(this.getLastModified());
    inventorySearchIndexDTO.setUpperLimit(this.getUpperLimit());
    inventorySearchIndexDTO.setLowerLimit(this.getLowerLimit());
	  inventorySearchIndexDTO.setCommodityCode(this.getCommodityCode());
	  inventorySearchIndexDTO.setStatus(this.getStatus());
    inventorySearchIndexDTO.setInventoryAveragePrice(this.getInventoryAveragePrice());
    inventorySearchIndexDTO.setKindName(this.getkindName());
    return  inventorySearchIndexDTO;
  }

  public InventorySearchIndex(InventorySearchIndexDTO inventorySearchIndexDTO) {
    if (inventorySearchIndexDTO == null) {
      return;
    }
    this.setBrand(inventorySearchIndexDTO.getBrand());
    this.setAmount(inventorySearchIndexDTO.getAmount());
    this.setBarcode(inventorySearchIndexDTO.getBarcode());
    this.setEditDate(inventorySearchIndexDTO.getEditDate());
    this.setEngine(inventorySearchIndexDTO.getEngine());
    this.setModel(inventorySearchIndexDTO.getModel());
    this.setParentProductId(inventorySearchIndexDTO.getParentProductId());
    this.setPrice(inventorySearchIndexDTO.getPrice());
    this.setProductBrand(inventorySearchIndexDTO.getProductBrand());
    this.setProductId(inventorySearchIndexDTO.getProductId());
    this.setProductModel(inventorySearchIndexDTO.getProductModel());
    this.setProductName(inventorySearchIndexDTO.getProductName());
    this.setProductSpec(inventorySearchIndexDTO.getProductSpec());
    this.setProductVehicleStatus(inventorySearchIndexDTO.getProductVehicleStatus());
    this.setPurchasePrice(inventorySearchIndexDTO.getPurchasePrice());
    this.setRecommendedPrice(inventorySearchIndexDTO.getRecommendedPrice());
    this.setShopId(inventorySearchIndexDTO.getShopId());
    this.setYear(inventorySearchIndexDTO.getYear());
	  this.setCommodityCode(inventorySearchIndexDTO.getCommodityCode());
    this.setInventoryAveragePrice(inventorySearchIndexDTO.getInventoryAveragePrice());
    this.setUnit(inventorySearchIndexDTO.getUnit());
  }

}
