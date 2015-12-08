package com.bcgogo.txn.dto;


import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-16
 * To change this template use File | Settings | File Templates.
 */
public class SalesOrderItemDTO extends BcgogoOrderItemDto {
  private Long salesOrderId;
  private Double price;
  private Double total;
  private String memo;
  private String productType;

//  private String vehicleYear;
//  private String vehicleEngine;
//  private String vehicleBrand;
//  private String vehicleModel;

  private String productVehicleStatus;
  private Long vehicleBrandId;
  private Long vehicleModelId;
  private Long vehicleYearId;
  private Long vehicleEngineId;
  private Double purchasePrice;
  private Double costPrice;
  private Double totalCostPrice;
  private Double percentage;
  private Double percentageAmount;
  private String priceStr;
  private String totalStr;
  private String draftOrderItemIdStr;
  private Long businessCategoryId;
  private String businessCategoryName;
  private Double shortage;  //缺料量
  private String promotionsId;
  private Set<Long> promotionsIds;
  private List<PromotionsDTO> promotionsDTOs;
  private Double quotedPrice;
  private Double salesPrice; //打印用 勿删
  private Double purchaseAmount;
  private Long quotedPreBuyOrderItemId;
  private boolean isAddVehicleLicenceNoToSolr = false;
  private Boolean customPriceFlag;

  public SalesOrderItemDTO() {
	}

  public SalesOrderItemDTO(PurchaseOrderItemDTO purchaseOrderItemDTO) {
    setAmount(purchaseOrderItemDTO.getAmount());
    setPurchaseAmount(purchaseOrderItemDTO.getAmount());
    setProductId(purchaseOrderItemDTO.getSupplierProductId());
    setPrice(purchaseOrderItemDTO.getPrice());
    setQuotedPrice(purchaseOrderItemDTO.getQuotedPrice());
    setTotal(purchaseOrderItemDTO.getTotal());
    setPromotionsIds(purchaseOrderItemDTO.getPromotionsIds());
    setUnit(purchaseOrderItemDTO.getUnit());
     Set<Long> promotionsIdList=purchaseOrderItemDTO.getPromotionsIds();
    StringBuffer sb=new StringBuffer();
    if(CollectionUtil.isNotEmpty(promotionsIdList)){
      for(Long promotionId:promotionsIdList){
        if(sb.length()==0){
          sb.append(promotionId);
        }else{
          sb.append(",");
          sb.append(promotionId);
        }
      }
    }
    this.setPromotionsId(sb.toString());
    this.setPromotionsId(purchaseOrderItemDTO.getPromotionsId());
    setQuotedPreBuyOrderItemId(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId());
    setCustomPriceFlag(purchaseOrderItemDTO.getCustomPriceFlag());
  }
  public Long getQuotedPreBuyOrderItemId() {
    return quotedPreBuyOrderItemId;
  }

  public void setQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) {
    this.quotedPreBuyOrderItemId = quotedPreBuyOrderItemId;
  }
  public String getProductInfo(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getProductName())){
      sb.append(this.getProductName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getBrand())){
      sb.append(this.getBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getSpec())){
      sb.append(this.getSpec()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getModel())){
      sb.append(this.getModel()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getVehicleBrand())){
      sb.append(this.getVehicleBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getVehicleModel())){
      sb.append(this.getVehicleModel()).append(" ");
    }
    this.productInfo = sb.toString().trim();
    return this.productInfo;
  }
  public Set<Long> getPromotionsIds() {
    return promotionsIds;
  }

  public void setPromotionsIds(Set<Long> promotionsIds) {
    this.promotionsIds = promotionsIds;
  }

  public List<PromotionsDTO> getPromotionsDTOs() {
    return promotionsDTOs;
  }

  public void setPromotionsDTOs(List<PromotionsDTO> promotionsDTOs) {
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      for(PromotionsDTO promotionsDTO:promotionsDTOs){
        if(promotionsDTO!=null){
          Collections.sort(promotionsDTO.getPromotionsRuleDTOList(),PromotionsRuleDTO.SORT_BY_LEVEL);
        }
      }
    }
    this.promotionsDTOs = promotionsDTOs;
  }

  public Double getPercentage() {
      return percentage;
  }

  public Double getPercentageAmount() {
      return percentageAmount;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      this.percentageAmount = percentageAmount;
  }

  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  public Double getPrice() {
      if(price == null){
      return 0D;
    }
    return price;

  }

  public void setPrice(Double price) {
    this.price = price;
    if(price==null){
      this.price=0D;
      this.priceStr="0";
      return;
    }
    this.priceStr = NumberUtil.roundToString(this.price,2);
    this.setSalesPrice(price);
  }

  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
  }

  public Double getPurchaseAmount() {
    return purchaseAmount;
  }

  public void setPurchaseAmount(Double purchaseAmount) {
    this.purchaseAmount = purchaseAmount;
  }

  public Double getTotal() {
    if(total==null)
      return 0D;
    return NumberUtil.round(total, NumberUtil.MONEY_PRECISION);
  }

  public void setTotal(Double total) {
    this.total = total;
    if(total==null) this.total=0D;
    this.totalStr = NumberUtil.roundToString(this.total,2);
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(String productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }
  private String vehicleInfo;

  public Double getCostPrice() {
    return costPrice;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  //add by liuWei
  private Long shopId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getPurchasePrice() {
	  if(purchasePrice == null){
		  return  0D;
	  }
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
	  if(purchasePrice == null){
		  this.purchasePrice = 0d;
	  }
    this.purchasePrice = purchasePrice;
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

  public String getDraftOrderItemIdStr() {
    return draftOrderItemIdStr;
  }

  public void setDraftOrderItemIdStr(String draftOrderItemIdStr) {
    this.draftOrderItemIdStr = draftOrderItemIdStr;
  }

  //modify  by liuWei
  public String getVehicleInfo() {
    vehicleInfo="";
//   if(!this.getVehicleBrand().isEmpty()) vehicleInfo +=this.getVehicleBrand();

    if(StringUtils.isNotBlank(this.getVehicleBrand()))
    {
      vehicleInfo += this.getVehicleBrand();
    }

    if(null!=this.getVehicleModel()&&!this.getVehicleModel().isEmpty()&&!"".equals(vehicleInfo)){
      vehicleInfo +="/"+this.getVehicleModel();
    } else if(null!=this.getVehicleModel()&&!this.getVehicleModel().isEmpty()&&"".equals(vehicleInfo)){
      vehicleInfo +=this.getVehicleModel();
    }

//     if(null!=this.getVehicleYear()&&!this.getVehicleYear().isEmpty()&&!"".equals(vehicleInfo)){
//      vehicleInfo +="/"+this.getVehicleYear();
//    } else if(null!=this.getVehicleYear()&&!this.getVehicleYear().isEmpty()&&"".equals(vehicleInfo)){
//      vehicleInfo +=this.getVehicleYear();
//    }
//
//     if(null!=this.getVehicleEngine()&&!this.getVehicleEngine().isEmpty()&&!"".equals(vehicleInfo)){
//      vehicleInfo +="/"+this.getVehicleEngine();
//    } else if(null!=this.getVehicleEngine()&&!this.getVehicleEngine().isEmpty()&&"".equals(vehicleInfo)){
//      vehicleInfo +=this.getVehicleEngine();
//    }
    return vehicleInfo;
  }

  public void setVehicleInfo(String vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }

  public void setUnitAndRate(ProductLocalInfoDTO productLocalInfoDTO) {
    this.storageUnit = productLocalInfoDTO.getStorageUnit();
    this.sellUnit = productLocalInfoDTO.getSellUnit();
    this.rate = productLocalInfoDTO.getRate();
    if (this.unit == null) {
      if (!StringUtils.isBlank(this.sellUnit)) {
        this.unit = productLocalInfoDTO.getSellUnit();
      } else if (!StringUtils.isBlank(this.storageUnit)) {
        this.unit = productLocalInfoDTO.getStorageUnit();
      }
    }
  }

  public void setUnitAndRate(ProductDTO productDTO) {
    this.storageUnit = productDTO.getStorageUnit();
    this.sellUnit = productDTO.getSellUnit();
    this.rate = productDTO.getRate();
    if (this.unit == null) {
      if (!StringUtils.isBlank(this.sellUnit)) {
        this.unit = productDTO.getSellUnit();
      } else if (!StringUtils.isBlank(this.storageUnit)) {
        this.unit = productDTO.getStorageUnit();
      }
    }
  }

  public String getPriceStr() {
    return priceStr;
  }

  public void setPriceStr(String priceStr) {
    this.priceStr = priceStr;
  }

  public String getTotalStr() {
    return totalStr;
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public Double getShortage() {
    return shortage;
  }

  public void setShortage(Double shortage) {
    this.shortage = shortage;
  }

  public Boolean getCustomPriceFlag() {
    return customPriceFlag;
  }

  public void setCustomPriceFlag(Boolean customPriceFlag) {
    this.customPriceFlag = customPriceFlag;
  }

  @Override
  public void setProductHistoryDTO(ProductHistoryDTO productHistoryDTO) {
    super.setProductHistoryDTO(productHistoryDTO);
    setBusinessCategoryId(productHistoryDTO.getBusinessCategoryId());
    setBusinessCategoryName(productHistoryDTO.getBusinessCategoryName());
  }

  @Override
  public String toString() {
    return "SalesOrderItemDTO{" +
        "id=" + id +
        ", salesOrderId=" + salesOrderId +
        ", price=" + price +
        ", total=" + total +
        ", memo='" + memo + '\'' +
        ", productType='" + productType + '\'' +
        ", productVehicleStatus='" + productVehicleStatus + '\'' +
        ", vehicleBrandId=" + vehicleBrandId +
        ", vehicleModelId=" + vehicleModelId +
        ", vehicleYearId=" + vehicleYearId +
        ", vehicleEngineId=" + vehicleEngineId +
        ", purchasePrice=" + purchasePrice +
        ", costPrice=" + costPrice +
        ", totalCostPrice=" + totalCostPrice +
        ", percentage=" + percentage +
        ", percentageAmount=" + percentageAmount +
        ", priceStr='" + priceStr + '\'' +
        ", totalStr='" + totalStr + '\'' +
        ", draftOrderItemIdStr='" + draftOrderItemIdStr + '\'' +
        ", businessCategoryId=" + businessCategoryId +
        ", businessCategoryName='" + businessCategoryName + '\'' +
        ", shortage=" + shortage +
        ", vehicleInfo='" + vehicleInfo + '\'' +
        ", shopId=" + shopId +
        ", " + super.toString();
  }

  public ItemIndexDTO toItemIndexDTO(SalesOrderDTO salesOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(salesOrderDTO.getShopId());
    itemIndexDTO.setVehicle(salesOrderDTO.getLicenceNo());
    itemIndexDTO.setOrderId(salesOrderDTO.getId());
    itemIndexDTO.setPaymentTime(salesOrderDTO.getPaymentTime());
    itemIndexDTO.setOrderTimeCreated(salesOrderDTO.getVestDate() == null ? salesOrderDTO.getCreationDate() : salesOrderDTO.getVestDate());
    itemIndexDTO.setOrderStatus(salesOrderDTO.getStatus());
    itemIndexDTO.setCustomerId(salesOrderDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(salesOrderDTO.getCustomer());
    itemIndexDTO.setCustomerOrSupplierStatus(salesOrderDTO.getCustomerStatus() == null ? CustomerStatus.ENABLED.toString() : salesOrderDTO.getCustomerStatus().toString());
    itemIndexDTO.setOrderReceiptNo(salesOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderTotalAmount(salesOrderDTO.getTotal());
    itemIndexDTO.setArrears(salesOrderDTO.getDebt());
    itemIndexDTO.setStorehouseId(salesOrderDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(salesOrderDTO.getStorehouseName());

    itemIndexDTO.setOrderType(OrderTypes.SALE);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setItemMemo(this.getMemo());
    itemIndexDTO.setItemCostPrice(NumberUtil.doubleVal(this.getCostPrice()));
    itemIndexDTO.setTotalCostPrice(NumberUtil.doubleVal(this.getTotalCostPrice()));
    itemIndexDTO.setItemPrice(NumberUtil.doubleVal(this.getPrice()));
    itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
    itemIndexDTO.setItemTotalAmount(this.getTotal());

    itemIndexDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    itemIndexDTO.setBusinessCategoryName(this.getBusinessCategoryName());

    return itemIndexDTO;
  }
  public List<ItemIndexDTO> toInOutRecordDTO(SalesOrderDTO salesOrderDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(salesOrderDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(salesOrderDTO);
      itemIndexDTO.setItemType(ItemTypes.OUT);
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setRelatedCustomerId(salesOrderDTO.getCustomerId());
      itemIndexDTO.setRelatedCustomerName(salesOrderDTO.getCustomer());
      itemIndexDTO.setItemCount(this.getAmount());
      itemIndexDTO.setUnit(this.getUnit());
        itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
        itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotalCostPrice());
      itemIndexDTOList.add(itemIndexDTO);
    }else {
      if(!ArrayUtils.isEmpty(this.getOutStorageRelationDTOs())){
        for(OutStorageRelationDTO outStorageRelationDTO : getOutStorageRelationDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(salesOrderDTO);
          itemIndexDTO.setItemType(ItemTypes.OUT);
          itemIndexDTO.setInOutRecordId(outStorageRelationDTO.getId());
          itemIndexDTO.setRelatedSupplierId(outStorageRelationDTO.getRelatedSupplierId());
          itemIndexDTO.setRelatedSupplierName(outStorageRelationDTO.getRelatedSupplierName());
          itemIndexDTO.setRelatedCustomerId(salesOrderDTO.getCustomerId());
          itemIndexDTO.setRelatedCustomerName(salesOrderDTO.getCustomer());
          itemIndexDTO.setItemCount(outStorageRelationDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(outStorageRelationDTO.getOutStorageUnit());
            itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
            itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotalCostPrice());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;

  }
  public Double getQuotedPrice() {
    return quotedPrice;
  }

  public void setQuotedPrice(Double quotedPrice) {
    this.quotedPrice = quotedPrice;
  }

  public void setAddVehicleLicenceNoToSolr(boolean addVehicleLicenceNoToSolr) {
    this.isAddVehicleLicenceNoToSolr = addVehicleLicenceNoToSolr;
  }

  public boolean isAddVehicleLicenceNoToSolr() {
    return isAddVehicleLicenceNoToSolr;
  }

  public String getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(String promotionsId) {
    this.promotionsId = promotionsId;
  }
}
