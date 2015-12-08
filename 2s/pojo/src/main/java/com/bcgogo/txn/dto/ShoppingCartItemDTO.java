package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import sun.rmi.runtime.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
public class ShoppingCartItemDTO implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId;
  private Double amount;
  private Long supplierShopId;
  private String supplierShopIdStr;
  private Long productLocalInfoId;
  private String productLocalInfoIdStr;
  private Long userId;
  private Long editDate;

  //产品本身带的信息
  private String commodityCode;//商品编码
  private String productBrand;
  private String productModel;
  private String productSpec;
  private String productName;
  private String productVehicleBrand;
  private String productVehicleModel;
  private String sellUnit;//批发商销售单位
  private Double price;//实际优惠后的单价
  private String promotionsInfoJson;
  private Long promotionsId;
  private ImageCenterDTO imageCenterDTO;
   private Double inSalesPrice;

  @Deprecated
  private PromotionsDTO promotionsDTO;
  private Double quotedPrice;
  private ProductStatus salesStatus;//上架下架状态
  private Double total;

  /* add by zhuj */
  private String productInfoStr; //  6字段拼接信息 对应productDTO里面的字段
  private String promotionTypesShortStr; // 页面显示用的 促销短信息拼接的str 对应枚举为 PromotionsTypesShort
  private List<PromotionsDTO> promotionsDTOList; //  促销列表
  private Double inSalesPriceAfterCal; // 计算促销以后的价格
  private String promotionTypesStr; // 页面显示用的 促销短信息拼接的str 对应枚举为 PromotionsTypesShort
  private String[] promotionsTitle;

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public Double getInSalesPrice() {
    return inSalesPrice;
  }

  public void setInSalesPrice(Double inSalesPrice) {
    this.inSalesPrice = inSalesPrice;
  }

  public String getPromotionTypesShortStr() {
    return promotionTypesShortStr;
  }

  public void setPromotionTypesShortStr(String promotionTypesShortStr) {
    this.promotionTypesShortStr = promotionTypesShortStr;
  }

  public Double getInSalesPriceAfterCal() {
    return inSalesPriceAfterCal;
  }

  public void setInSalesPriceAfterCal(Double inSalesPriceAfterCal) {
    this.inSalesPriceAfterCal = inSalesPriceAfterCal;
  }

  public String getPromotionTypesStr() {
    return promotionTypesStr;
  }

  public void setPromotionTypesStr(String promotionTypesStr) {
    this.promotionTypesStr = promotionTypesStr;
  }

  /* add end */

  public List<PromotionsDTO> getPromotionsDTOList() {
    return promotionsDTOList;
  }

  public void setPromotionsDTOList(List<PromotionsDTO> promotionsDTOList) {
    this.promotionsDTOList = promotionsDTOList;
  }

  public String getProductInfoStr() {
    return productInfoStr;
  }

  public void setProductInfoStr(String productInfoStr) {
    this.productInfoStr = productInfoStr;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
    if (supplierShopId != null) {
      this.supplierShopIdStr = supplierShopId.toString();
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null){
      idStr = id.toString();
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
    this.setProductLocalInfoIdStr(StringUtil.valueOf(this.productLocalInfoId));
  }

  public String getProductLocalInfoIdStr() {
    return productLocalInfoIdStr;
  }

  public void setProductLocalInfoIdStr(String productLocalInfoIdStr) {
    this.productLocalInfoIdStr = productLocalInfoIdStr;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public void setProductDTO(ProductDTO productDTO){
    if(productDTO==null) {
      return;
    }
    this.setCommodityCode(productDTO.getCommodityCode());
    this.setProductName(productDTO.getName());
    this.setProductBrand(productDTO.getBrand());
    this.setProductSpec(productDTO.getSpec());
    this.setProductModel(productDTO.getModel());
    this.setProductVehicleBrand(productDTO.getProductVehicleBrand());
    this.setProductVehicleModel(productDTO.getProductVehicleBrand());
    this.setSalesStatus(productDTO.getSalesStatus());
    this.setPrice(NumberUtil.round(productDTO.getInSalesPrice()));
    this.setQuotedPrice(NumberUtil.round(productDTO.getInSalesPrice()));
    this.setSellUnit(productDTO.getInSalesUnit());
    this.setProductInfoStr(productDTO.generateProductInfo()); // add by zhuj
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

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
  }

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  public PromotionsDTO getPromotionsDTO() {
    return promotionsDTO;
  }

  public void setPromotionsDTO(PromotionsDTO promotionsDTO) {
    this.promotionsDTO = promotionsDTO;
  }

  public Double getQuotedPrice() {
    return quotedPrice;
  }

  public void setQuotedPrice(Double quotedPrice) {
    this.quotedPrice = quotedPrice;
  }

  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String[] getPromotionsTitle() {
    return promotionsTitle;
  }

  public void setPromotionsTitle(String[] promotionsTitle) {
    this.promotionsTitle = promotionsTitle;
  }

  public String getSupplierShopIdStr() {
    return supplierShopIdStr;
  }

  public void setSupplierShopIdStr(String supplierShopIdStr) {
    this.supplierShopIdStr = supplierShopIdStr;
  }
}
