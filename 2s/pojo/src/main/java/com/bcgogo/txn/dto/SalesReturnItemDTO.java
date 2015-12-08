package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-13
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class SalesReturnItemDTO extends BcgogoOrderItemDto implements Serializable {
  public SalesReturnItemDTO() {
  }

  private Long salesReturnId;
  private Long customerOrderItemId;
  private Double price;
  private Double total;
  private String memo;
  private Double costPrice;
  private Double totalCostPrice;

  private PurchaseReturnItemDTO purchaseReturnItemDTO;//关联客户的退货条目

  private Boolean sameFlag;
  private Boolean replaceFlag;

  private double storageAmount;     // 批发商结算时自定义入库量
  private String wholesalerUnit;    //批发商结算时使用的单位
  private Long customerProductId;
  private String businessCategoryName;
  private Long businessCategoryId;
  private Double purchasePrice;


  private Double originSalesPrice;//销售单 或施工单 原来的销售价
  public Double originSaleAmount;//销售单 或施工单 原来的销售量
  public String originSaleAmountStr;//销售单 或施工单 原来的销售量
  public Double originSaleTotal;//销售单 或施工单 原来的销售总额


  public Double getOriginSaleTotal() {
    return originSaleTotal;
  }

  public void setOriginSaleTotal(Double originSaleTotal) {
    this.originSaleTotal = originSaleTotal;
  }

  public String getOriginSaleAmountStr() {
    return originSaleAmountStr;
  }

  public void setOriginSaleAmountStr(String originSaleAmountStr) {
    this.originSaleAmountStr = originSaleAmountStr;
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

  public Double getOriginSaleAmount() {
    return originSaleAmount;
  }

  public void setOriginSaleAmount(Double originSaleAmount) {
    this.originSaleAmount = originSaleAmount;
  }

  public Double getOriginSalesPrice() {
    return originSalesPrice;
  }

  public void setOriginSalesPrice(Double originSalesPrice) {
    this.originSalesPrice = NumberUtil.toReserve(originSalesPrice, NumberUtil.MONEY_PRECISION);
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }
  }

  public Long getSalesReturnId() {
    return salesReturnId;
  }

  public void setSalesReturnId(Long salesReturnId) {
    this.salesReturnId = salesReturnId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
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

  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public ItemIndexDTO toItemIndexDTO(SalesReturnDTO salesReturnDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(salesReturnDTO.getShopId());
    itemIndexDTO.setOrderStatus(salesReturnDTO.getStatus());
    itemIndexDTO.setOrderReceiptNo(salesReturnDTO.getReceiptNo());
    itemIndexDTO.setOrderTimeCreated(salesReturnDTO.getVestDate() == null ? salesReturnDTO.getCreationDate() : salesReturnDTO.getVestDate());
    itemIndexDTO.setOrderTotalAmount(salesReturnDTO.getTotal());
    itemIndexDTO.setCustomerId(salesReturnDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(salesReturnDTO.getCustomer());
    itemIndexDTO.setOrderId(salesReturnDTO.getId());
    itemIndexDTO.setStorehouseId(salesReturnDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(salesReturnDTO.getStorehouseName());

    itemIndexDTO.setItemPrice(NumberUtil.doubleVal(this.getPrice()));
    itemIndexDTO.setItemMemo(this.getMemo());
    itemIndexDTO.setOrderType(OrderTypes.SALE_RETURN);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setTotalCostPrice(NumberUtil.doubleVal(getTotalCostPrice()));
    itemIndexDTO.setItemCostPrice(NumberUtil.doubleVal(getCostPrice()));
    return itemIndexDTO;
  }

  public SalesReturnItemDTO fromItemIndexDTO(ItemIndexDTO itemIndexDTO) {
    super.fromBcgogoItemIndexDTO(itemIndexDTO);
    setPrice(itemIndexDTO.getItemPrice());
    setMemo(itemIndexDTO.getItemMemo());
    setTotalCostPrice(itemIndexDTO.getTotalCostPrice());
    setCostPrice(itemIndexDTO.getItemCostPrice());
    if (getPrice() != null && getAmount() != null) {
      setTotal(getPrice() * getAmount());
    }
    return this;
  }

  public List<ItemIndexDTO> toInOutRecordDTO(SalesReturnDTO salesReturnDTO) {
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if(salesReturnDTO.getMergeInOutRecordFlag()){
      ItemIndexDTO itemIndexDTO = toItemIndexDTO(salesReturnDTO);
      itemIndexDTO.setInOutRecordId(this.getId());
      itemIndexDTO.setItemType(ItemTypes.IN);
      itemIndexDTO.setRelatedCustomerId(salesReturnDTO.getCustomerId());
      itemIndexDTO.setRelatedCustomerName(salesReturnDTO.getCustomer());
      itemIndexDTO.setItemCount(NumberUtil.doubleVal(this.getAmount()));
      itemIndexDTO.setUnit(this.getUnit());
       itemIndexDTO.setInoutRecordItemTotalAmount(NumberUtil.doubleVal(this.getInventoryAveragePrice())*NumberUtil.doubleVal(this.getAmount()));
      itemIndexDTO.setInoutRecordTotalCostPrice(NumberUtil.doubleVal(this.getTotal()));
      itemIndexDTOList.add(itemIndexDTO);
    }else{
      if(!ArrayUtils.isEmpty(this.getInStorageRecordDTOs())){
        for(InStorageRecordDTO inStorageRecordDTO : getInStorageRecordDTOs()){
          ItemIndexDTO itemIndexDTO = toItemIndexDTO(salesReturnDTO);
          itemIndexDTO.setInOutRecordId(inStorageRecordDTO.getId());
          itemIndexDTO.setItemType(ItemTypes.IN);
          itemIndexDTO.setRelatedSupplierId(inStorageRecordDTO.getSupplierId());
          itemIndexDTO.setRelatedSupplierName(inStorageRecordDTO.getSupplierName());
          itemIndexDTO.setRelatedCustomerId(salesReturnDTO.getCustomerId());
          itemIndexDTO.setRelatedCustomerName(salesReturnDTO.getCustomer());
          itemIndexDTO.setItemCount(inStorageRecordDTO.getSupplierRelatedAmount());
          itemIndexDTO.setUnit(inStorageRecordDTO.getInStorageUnit());
            itemIndexDTO.setInoutRecordItemTotalAmount(NumberUtil.doubleVal(this.getInventoryAveragePrice())*NumberUtil.doubleVal(this.getAmount()));
      itemIndexDTO.setInoutRecordTotalCostPrice(NumberUtil.doubleVal(this.getTotal()));
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
    }
    return itemIndexDTOList;
  }

  public Long getCustomerOrderItemId() {
    return customerOrderItemId;
  }

  public void setCustomerOrderItemId(Long customerOrderItemId) {
    this.customerOrderItemId = customerOrderItemId;
  }

  public PurchaseReturnItemDTO getPurchaseReturnItemDTO() {
    return purchaseReturnItemDTO;
  }

  public void setPurchaseReturnItemDTO(PurchaseReturnItemDTO purchaseReturnItemDTO) {
    this.purchaseReturnItemDTO = purchaseReturnItemDTO;
//    if (purchaseReturnItemDTO == null) {
//      this.sameFlag = false;
//      return;
//    } else {
//      String thisKey = StringUtil.truncValue(this.getProductName())+"_" + StringUtil.truncValue(this.getModel())+"_"
//          + StringUtil.truncValue(this.getSpec())+"_" + StringUtil.truncValue(this.getBrand())+"_"
//          + StringUtil.truncValue(this.getVehicleBrand())+"_" + StringUtil.truncValue(this.getVehicleModel())+"_" + StringUtil.truncValue(this.getUnit());
//
//      String otherKey = StringUtil.truncValue(purchaseReturnItemDTO.getProductName())+"_" + StringUtil.truncValue(purchaseReturnItemDTO.getModel())+"_"
//          + StringUtil.truncValue(purchaseReturnItemDTO.getSpec())+"_" + StringUtil.truncValue(purchaseReturnItemDTO.getBrand())+"_"
//          + StringUtil.truncValue(purchaseReturnItemDTO.getVehicleBrand())+"_" + StringUtil.truncValue(purchaseReturnItemDTO.getVehicleModel())+"_" + StringUtil.truncValue(purchaseReturnItemDTO.getUnit());
//      this.sameFlag = StringUtil.isEqual(thisKey, otherKey);
//      if(this.sameFlag){
//        this.replaceFlag = false;
//      }
//    }
  }


  public Boolean getSameFlag() {
    return sameFlag;
  }

  public void setSameFlag(Boolean sameFlag) {
    this.sameFlag = sameFlag;
  }

  public Boolean getReplaceFlag() {
    return replaceFlag;
  }

  public void setReplaceFlag(Boolean replaceFlag) {
    this.replaceFlag = replaceFlag;
  }

  public double getStorageAmount() {
    return storageAmount;
  }

  public void setStorageAmount(double storageAmount) {
    this.storageAmount = storageAmount;
  }

  public String getWholesalerUnit() {
    return wholesalerUnit;
  }

  public void setWholesalerUnit(String wholesalerUnit) {
    this.wholesalerUnit = wholesalerUnit;
  }

  public Long getCustomerProductId() {
    return customerProductId;
  }

  public void setCustomerProductId(Long customerProductId) {
    this.customerProductId = customerProductId;
  }

  @Override
  public String toString() {
    return "SalesReturnItemDTO{" +
        "id=" + id +
        ", salesReturnId=" + salesReturnId +
        ", customerOrderItemId=" + customerOrderItemId +
        ", price=" + price +
        ", total=" + total +
        ", memo='" + memo + '\'' +
        ", costPrice=" + costPrice +
        ", totalCostPrice=" + totalCostPrice +
        ", purchaseReturnItemDTO=" + purchaseReturnItemDTO +
        ", sameFlag=" + sameFlag +
        ", replaceFlag=" + replaceFlag +
        ", storageAmount=" + storageAmount +
        ", wholesalerUnit='" + wholesalerUnit + '\'' +
        ", customerProductId=" + customerProductId +
        ", businessCategoryName='" + businessCategoryName + '\'' +
        ", businessCategoryId=" + businessCategoryId +
        ", purchasePrice=" + purchasePrice +
        ", originSalesPrice=" + originSalesPrice +
        ", originSaleAmount=" + originSaleAmount +
        ", originSaleAmountStr='" + originSaleAmountStr + '\'' +
        ", originSaleTotal=" + originSaleTotal +
        ", " + super.toString();
  }
}
