package com.bcgogo.txn.dto;


import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 8/28/11
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseOrderItemDTO extends BcgogoOrderItemDto {
  private ProductDTO productDTO = new ProductDTO();
  private Double price;
  private Double total;
  private String memo;
  private Long purchaseOrderId;

  private String productType;

//  private String vehicleBrand;
//  private String vehicleModel;
//  private String vehicleYear;
//  private String vehicleEngine;

  private Long vehicleBrandId;
  private Long vehicleModelId;
  private Long vehicleYearId;
  private Long vehicleEngineId;

  private Integer productVehicleStatus;

  private Double lowerLimit;
  private Double upperLimit;

  private Long supplierProductId;  //批发商productLocalInfoId
	private String supplierProductIdStr;

  private ProductStatus supplierProductSalesStatus;//批发商商品上下架状态
  private String promotionsInfoJson;
  private String promotionsId;
  private Set<Long> promotionsIds;
  private List<PromotionsDTO> promotionsDTOs;
  private List<PromotionOrderRecordDTO> promotionOrderRecordDTOs;
  private Double quotedPrice;
  private Boolean customPriceFlag;
  private Double inSalesAmount;

  private Long shoppingCartItemId;
  private Long quotedPreBuyOrderItemId;
  private Double shortage;  //缺料量

  public Double getShortage() {
    return shortage;
  }

  public void setShortage(Double shortage) {
    this.shortage = shortage;
  }

  public PurchaseOrderItemDTO() {
  }

	//set productDTO时确认是否需要给item的单位重新赋值
    public void setWholesalerProductDTO(ProductDTO productDTO) {
      if(productDTO == null){
        return;
      }
      this.setSupplierProductId(productDTO.getProductLocalInfoId());
      this.setProductName(productDTO.getName());
      this.setBrand(productDTO.getBrand());
      this.setModel(productDTO.getModel());
      this.setSpec(productDTO.getSpec());
      this.setVehicleModel(productDTO.getProductVehicleModel());
      this.setVehicleBrand(productDTO.getProductVehicleBrand());
      this.setCommodityCode(productDTO.getCommodityCode());
      this.setSupplierProductSalesStatus(productDTO.getSalesStatus());
    }

  public void setWholesalerProductHistoryDTO(ProductHistoryDTO productHistoryDTO){
    if (productHistoryDTO == null) {
      return;
    }
    this.setSupplierProductId(productHistoryDTO.getProductLocalInfoId());
    this.setProductName(productHistoryDTO.getName());
    this.setBrand(productHistoryDTO.getBrand());
    this.setModel(productHistoryDTO.getModel());
    this.setSpec(productHistoryDTO.getSpec());
    this.setVehicleModel(productHistoryDTO.getProductVehicleModel());
    this.setVehicleBrand(productHistoryDTO.getProductVehicleBrand());
    this.setCommodityCode(productHistoryDTO.getCommodityCode());
    this.setProductHistoryId(productHistoryDTO.getId());
  }
  public Long getShoppingCartItemId() {
    return shoppingCartItemId;
  }

  public void setShoppingCartItemId(Long shoppingCartItemId) {
    this.shoppingCartItemId = shoppingCartItemId;
  }

  public Boolean getCustomPriceFlag() {
    return customPriceFlag;
  }

  public void setCustomPriceFlag(Boolean customPriceFlag) {
    this.customPriceFlag = customPriceFlag;
  }

  public Double getInSalesAmount() {
    return inSalesAmount;
  }

  public void setInSalesAmount(Double inSalesAmount) {
    this.inSalesAmount = inSalesAmount;
  }

  public List<PromotionsDTO> getPromotionsDTOs() {
    return promotionsDTOs;
  }

  public void setPromotionsDTOs(List<PromotionsDTO> promotionsDTOs) {
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      for(PromotionsDTO promotionsDTO:promotionsDTOs){
        if(promotionsDTO!=null&&CollectionUtil.isNotEmpty(promotionsDTO.getPromotionsRuleDTOList())){
          Collections.sort(promotionsDTO.getPromotionsRuleDTOList(),PromotionsRuleDTO.SORT_BY_LEVEL);
        }
      }
    }
    this.promotionsDTOs = promotionsDTOs;
  }

  public List<PromotionOrderRecordDTO> getPromotionOrderRecordDTOs() {
    return promotionOrderRecordDTOs;
  }

  public void setPromotionOrderRecordDTOs(List<PromotionOrderRecordDTO> promotionOrderRecordDTOs) {
    this.promotionOrderRecordDTOs = promotionOrderRecordDTOs;
  }

  public String getPromotionsInfoJson() {
    return promotionsInfoJson;
  }

  public void setPromotionsInfoJson(String promotionsInfoJson) {
    this.promotionsInfoJson = promotionsInfoJson;
  }

  public Double getPrice() {
    if(price==null){
      return 0D;
    }
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
    if(total==null){
      return 0D;
    }
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

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(StringUtil.valueOf(id));
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
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

  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  //modify by liuWei
  public String getVehicleInfo() {
    vehicleInfo = "";
    if (null != this.getVehicleBrand() && !this.getVehicleBrand().isEmpty()) vehicleInfo += this.getVehicleBrand();
    if (null != this.getVehicleModel() && ! this.getVehicleModel().isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" +  this.getVehicleModel();
    } else if (null !=   this.getVehicleModel() && ! this.getVehicleModel().isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += this.getVehicleModel();
    }

    if (null !=  this.getVehicleYear() && !this.getVehicleYear().isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" + this.getVehicleYear();
    } else if (null != this.getVehicleYear() && !this.getVehicleYear().isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += this.getVehicleYear();
    }

    if (null != this.getVehicleEngine() && !this.getVehicleEngine().isEmpty() && !"".equals(vehicleInfo)) {
      vehicleInfo += "/" + this.getVehicleEngine();
    } else if (null != this.getVehicleEngine() && !this.getVehicleEngine().isEmpty() && "".equals(vehicleInfo)) {
      vehicleInfo += this.getVehicleEngine();
    }
    return vehicleInfo;
  }

  public void setVehicleInfo(String vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }

  private String vehicleInfo;

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

	public Long getSupplierProductId() {
		return supplierProductId;
	}

	public void setSupplierProductId(Long supplierProductId) {
		this.supplierProductId = supplierProductId;
    if(supplierProductId != null){
      setSupplierProductIdStr(supplierProductId.toString());
    }
	}

  public Double getQuotedPrice() {
    return quotedPrice;
  }

  public void setQuotedPrice(Double quotedPrice) {
    this.quotedPrice = quotedPrice;
  }

  @Override
  public String toString() {
    return "PurchaseOrderItemDTO{" +
        "id=" + id +
        ", productDTO=" + productDTO +
        ", price=" + price +
        ", total=" + total +
        ", memo='" + memo + '\'' +
        ", purchaseOrderId=" + purchaseOrderId +
        ", productType='" + productType + '\'' +
        ", vehicleBrandId=" + vehicleBrandId +
        ", vehicleModelId=" + vehicleModelId +
        ", vehicleYearId=" + vehicleYearId +
        ", vehicleEngineId=" + vehicleEngineId +
        ", quotedPrice=" + quotedPrice +
        ", productVehicleStatus=" + productVehicleStatus +
        ", lowerLimit=" + lowerLimit +
        ", upperLimit=" + upperLimit +
        ", supplierProductId=" + supplierProductId +
        ", vehicleInfo='" + vehicleInfo + '\'' +
        " " + super.toString();
  }

  public ItemIndexDTO toItemIndexDTO(PurchaseOrderDTO purchaseOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(purchaseOrderDTO.getShopId());
    itemIndexDTO.setOrderId(purchaseOrderDTO.getId());
    itemIndexDTO.setCustomerId(purchaseOrderDTO.getSupplierId());
    itemIndexDTO.setCustomerOrSupplierName(purchaseOrderDTO.getSupplier());
    itemIndexDTO.setCustomerOrSupplierStatus(purchaseOrderDTO.getSupplierStatus() == null ? CustomerStatus.ENABLED.toString() : purchaseOrderDTO.getSupplierStatus().toString());

    itemIndexDTO.setPaymentTime(purchaseOrderDTO.getDeliveryDate());
    itemIndexDTO.setOrderTimeCreated(purchaseOrderDTO.getVestDate() == null ? purchaseOrderDTO.getCreationDate() : purchaseOrderDTO.getVestDate());
    itemIndexDTO.setOrderStatus(purchaseOrderDTO.getStatus());
    itemIndexDTO.setOrderReceiptNo(purchaseOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderTotalAmount(purchaseOrderDTO.getTotal());

    itemIndexDTO.setOrderType(OrderTypes.PURCHASE);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setItemTotalAmount(this.getTotal());
    itemIndexDTO.setItemMemo(this.getMemo());
    itemIndexDTO.setItemPrice(this.getPrice());
    itemIndexDTO.setSupplierProductId(this.getSupplierProductId());

    return itemIndexDTO;
  }

  public String getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(String promotionsId) {
    this.promotionsId = promotionsId;
  }

  public Set<Long> getPromotionsIds() {
    return promotionsIds;
  }

  public void setPromotionsIds(Set<Long> promotionsIds) {
    this.promotionsIds = promotionsIds;
  }

  public ProductStatus getSupplierProductSalesStatus() {
    return supplierProductSalesStatus;
  }

  public void setSupplierProductSalesStatus(ProductStatus supplierProductSalesStatus) {
    this.supplierProductSalesStatus = supplierProductSalesStatus;
  }

  public Long getQuotedPreBuyOrderItemId() {
    return quotedPreBuyOrderItemId;
  }

  public void setQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) {
    this.quotedPreBuyOrderItemId = quotedPreBuyOrderItemId;
  }

  public String getSupplierProductIdStr() {
    return supplierProductIdStr;
  }

  public void setSupplierProductIdStr(String supplierProductIdStr) {
    this.supplierProductIdStr = supplierProductIdStr;
  }
}
