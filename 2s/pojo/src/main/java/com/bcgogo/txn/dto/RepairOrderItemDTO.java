package com.bcgogo.txn.dto;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.UnitUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-18
 * Time: 上午11:44
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderItemDTO extends BcgogoOrderItemDto {
  private Long repairOrderId;
  //  private Long productId;
  private Double price;
  private Double total, totalApprox; //采购价格
  private String memo;
  //  private String brand;
  private Integer productType;
  private Double purchasePrice;   //采购价格
  private boolean lack; //判断是否有缺料待修记录
  private Long shopId;
  private Double costPrice;
  private Double totalCostPrice;
  private Double percentage;
  private Double percentageAmount;
  private Long templateItemId;
  private String templateItemIdStr;
  private Long businessCategoryId;
  private String businessCategoryName;
  private String businessCategoryIdStr;
  private String activeRecommendSupplierHtml;
  private RepairRemindEventDTO repairRemindEventDTO;

  public Long getTemplateItemId() {
    return templateItemId;
  }

  public void setTemplateItemId(Long templateItemId) {
    this.templateItemId = templateItemId;
    if(templateItemId != null)
    {
      this.templateItemIdStr = String.valueOf(templateItemId);
    }
  }

  public String getTemplateItemIdStr() {
    return templateItemIdStr;
  }

  public void setTemplateItemIdStr(String templateItemIdStr) {
    this.templateItemIdStr = templateItemIdStr;
    if(StringUtils.isNotBlank(templateItemIdStr))
    {
      templateItemId = NumberUtil.longValue(templateItemIdStr);
    }

  }

  public RepairOrderItemDTO() {
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

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public Double getCostPrice() {
    return costPrice;
  }


  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public boolean isLack() {
    return lack;
  }

  public void setLack(boolean lack) {
    this.lack = lack;
  }

  public Double getPurchasePrice() {
    if(purchasePrice==null) return 0d;
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Integer getProductType() {
    return productType;
  }

  public void setProductType(Integer productType) {
    this.productType = productType;
  }

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  public Double getPrice() {
    if(price==null) return 0d;
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
    // this.purchasePrice = price;
  }

  public Double getTotal() {
    if(total==null) return 0d;
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public double getTotalApprox() {
    totalApprox = NumberUtil.round(total, NumberUtil.MONEY_PRECISION);
    return totalApprox;
  }

  public void setTotalApprox(double totalApprox) {
    this.totalApprox = totalApprox;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
    this.businessCategoryIdStr = null==businessCategoryId?"":businessCategoryId.toString();
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public String getBusinessCategoryIdStr() {
    return businessCategoryIdStr;
  }

  public void setBusinessCategoryIdStr(String businessCategoryIdStr) {
    this.businessCategoryIdStr = businessCategoryIdStr;
  }

  public ItemIndexDTO toItemIndexDTO(RepairOrderDTO repairOrderDTO){
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();
    itemIndexDTO.setShopId(this.getShopId());
    itemIndexDTO.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndexDTO.setCustomerOrSupplierStatus(repairOrderDTO.getCustomerStatus()==null? CustomerStatus.ENABLED.toString():repairOrderDTO.getCustomerStatus().toString());
    itemIndexDTO.setOrderReceiptNo(repairOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderStatus(repairOrderDTO.getStatus());
    itemIndexDTO.setOrderTimeCreated(repairOrderDTO.getSettleDate() == null ? repairOrderDTO.getCreationDate() : repairOrderDTO.getSettleDate());
    itemIndexDTO.setVehicle(repairOrderDTO.getVechicle());
    itemIndexDTO.setOrderId(repairOrderDTO.getId());
    itemIndexDTO.setStorehouseId(repairOrderDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(repairOrderDTO.getStorehouseName());

    itemIndexDTO.setOrderType(OrderTypes.REPAIR);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setItemPrice(this.getPrice());
    itemIndexDTO.setItemTotalAmount(this.getTotal());
    itemIndexDTO.setTotalCostPrice(this.getTotalCostPrice());
    itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
    itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotalCostPrice());
    itemIndexDTO.setItemCostPrice(NumberUtil.doubleVal(this.getCostPrice()));
    itemIndexDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    itemIndexDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    return itemIndexDTO;
  }

  public List<ItemIndexDTO> toInOutRecordDTO(RepairOrderDTO repairOrderDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(repairOrderDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(repairOrderDTO);
      itemIndexDTO.setItemType(ItemTypes.OUT);
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setRelatedCustomerId(repairOrderDTO.getCustomerId());
      itemIndexDTO.setRelatedCustomerName(repairOrderDTO.getCustomerName());
      itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
      itemIndexDTO.setInoutRecordTotalCostPrice(this.getTotalCostPrice());
      itemIndexDTOList.add(itemIndexDTO);
    }else{
      if(!ArrayUtils.isEmpty(this.getOutStorageRelationDTOs())){
        for(OutStorageRelationDTO outStorageRelationDTO : getOutStorageRelationDTOs()){
          if(outStorageRelationDTO==null) continue;
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(repairOrderDTO);
          itemIndexDTO.setItemType(ItemTypes.OUT);
          itemIndexDTO.setInOutRecordId(outStorageRelationDTO.getId());
          itemIndexDTO.setRelatedSupplierId(outStorageRelationDTO.getRelatedSupplierId());
          itemIndexDTO.setRelatedSupplierName(outStorageRelationDTO.getRelatedSupplierName());
          itemIndexDTO.setRelatedCustomerId(repairOrderDTO.getCustomerId());
          itemIndexDTO.setRelatedCustomerName(repairOrderDTO.getCustomerName());
          itemIndexDTO.setItemCount(outStorageRelationDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(outStorageRelationDTO.getOutStorageUnit());
          itemIndexDTO.setInoutRecordItemTotalAmount(this.getTotal());
          itemIndexDTO.setInoutRecordTotalCostPrice(this.getItemTotalCostPrice());
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }

    return itemIndexDTOList;
  }

  @Override
  public String toString() {
    return "RepairOrderItemDTO{" +
        "id=" + id +
        ", idStr='" + idStr + '\'' +
        ", repairOrderId=" + repairOrderId +
        ", price=" + price +
        ", total=" + total +
        ", totalApprox=" + totalApprox +
        ", memo='" + memo + '\'' +
        ", productType=" + productType +
        ", purchasePrice=" + purchasePrice +
        ", lack=" + lack +
        ", shopId=" + shopId +
        ", costPrice=" + costPrice +
        ", totalCostPrice=" + totalCostPrice +
        ", percentage=" + percentage +
        ", percentageAmount=" + percentageAmount +
        ", templateItemId=" + templateItemId +
        ", templateItemIdStr='" + templateItemIdStr + '\'' +
        ", businessCategoryId=" + businessCategoryId +
        ", businessCategoryName='" + businessCategoryName + '\'' +
        ", businessCategoryIdStr='" + businessCategoryIdStr + '\'' +
        ", " + super.toString();
  }

  public void fromInsuranceItemDTO(InsuranceOrderItemDTO insuranceOrderItemDTO) {
    this.setAmount(insuranceOrderItemDTO.getAmount());
    this.setPrice(insuranceOrderItemDTO.getPrice());
    this.setTotal(NumberUtil.doubleVal(this.getAmount()) * NumberUtil.doubleVal(this.getPrice()));
    if(UnitUtil.isStorageUnit(insuranceOrderItemDTO.getUnit(),this)){
      this.setUnit(insuranceOrderItemDTO.getUnit());
    }else {
      if(this.getProductId()!=null&&StringUtils.isNotBlank(this.getSellUnit())){
        this.setUnit(this.getSellUnit());
      }else {
        this.setUnit(insuranceOrderItemDTO.getUnit());
      }
    }
  }

  public void setInventoryDTO(InventoryDTO inventoryDTO) {
    if (inventoryDTO != null) {
      double inventoryAmountWithItemUnit = inventoryDTO.getAmount();
      if(UnitUtil.isStorageUnit(this.getUnit(),this)){
        inventoryAmountWithItemUnit = inventoryAmountWithItemUnit * this.getRate();
      }
      this.setInventoryAmount(inventoryAmountWithItemUnit);
    }
  }

  public String getActiveRecommendSupplierHtml() {
    return activeRecommendSupplierHtml;
  }

  public void setActiveRecommendSupplierHtml(String activeRecommendSupplierHtml) {
    this.activeRecommendSupplierHtml = activeRecommendSupplierHtml;
  }

  public RepairRemindEventDTO getRepairRemindEventDTO() {
    return repairRemindEventDTO;
  }

  public void setRepairRemindEventDTO(RepairRemindEventDTO repairRemindEventDTO) {
    this.repairRemindEventDTO = repairRemindEventDTO;
  }

  public void fromAppointOrderMaterialWithoutProductId(AppointOrderMaterialDTO appointOrderMaterialDTO) {
    if (appointOrderMaterialDTO != null) {
      this.setProductName(appointOrderMaterialDTO.getProductName());
      this.setBrand(appointOrderMaterialDTO.getBrand());
      this.setModel(appointOrderMaterialDTO.getModel());
      this.setSpec(appointOrderMaterialDTO.getSpec());
      this.setVehicleBrand(appointOrderMaterialDTO.getVehicleBrand());
      this.setVehicleModel(appointOrderMaterialDTO.getVehicleModel());
      this.setUnit(appointOrderMaterialDTO.getUnit());
      this.setCommodityCode(appointOrderMaterialDTO.getCommodityCode());
      double amount = NumberUtil.round(appointOrderMaterialDTO.getAmount(), 2);
      double price = NumberUtil.round(appointOrderMaterialDTO.getPrice(), 2);
      this.setAmount(amount);
      this.setPrice(price);
      this.setTotal(NumberUtil.round(amount * price, 2));
    }
  }
}
