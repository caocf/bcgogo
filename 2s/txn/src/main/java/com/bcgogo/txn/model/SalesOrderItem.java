package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SalesOrderItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-13
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem extends LongIdentifier {
  public SalesOrderItem(){
  }

  @Column(name = "sales_order_id")
  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
  
  @Column(name="product_history_id")
  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }


  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "memo", length = 100)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "unit" , length = 20)
  public String getUnit(){
    return unit;
  }

  public void setUnit(String unit){
    this.unit = unit;
  }


  private Long salesOrderId;
  private Long productId;
  private Long productHistoryId;
  private double amount;
  private double price;
  private double total;
  private String memo;
  private Double costPrice;
  private Double totalCostPrice;
  private Double percentage;
  private Double percentageAmount;
  private Long businessCategoryId;
  private String businessCategoryName;
	private Double reserved; //预留，采购下单时减库存到预留上
  private Double quotedPrice;
  private Long quotedPreBuyOrderItemId;
  private String promotionsId;
  private Boolean customPriceFlag;

  @Column(name="quoted_pre_buy_order_item_id")
  public Long getQuotedPreBuyOrderItemId() {
    return quotedPreBuyOrderItemId;
  }

  public void setQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) {
    this.quotedPreBuyOrderItemId = quotedPreBuyOrderItemId;
  }

  @Column(name="percentage")
  public Double getPercentage() {
      return percentage;
  }
  @Column(name="percentage_amount")
  public Double getPercentageAmount() {
      return percentageAmount;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      this.percentageAmount = percentageAmount;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }
  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }
  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  private Long shopId;
  private String unit;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

	@Column(name="reserved")
	public Double getReserved() {
		return reserved;
	}

	public void setReserved(Double reserved) {
		this.reserved = reserved;
	}

	public SalesOrderItemDTO toDTO() {
    SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO();

    salesOrderItemDTO.setId(this.getId());
    salesOrderItemDTO.setShopId(this.getShopId());

    salesOrderItemDTO.setSalesOrderId(this.getSalesOrderId());
    salesOrderItemDTO.setProductId(this.getProductId());
    salesOrderItemDTO.setProductHistoryId(getProductHistoryId());
    salesOrderItemDTO.setAmount(this.getAmount());
    salesOrderItemDTO.setPrice(this.getPrice());
    salesOrderItemDTO.setTotal(this.getTotal());
    salesOrderItemDTO.setMemo(this.getMemo());
    salesOrderItemDTO.setCostPrice(this.getCostPrice());
    salesOrderItemDTO.setTotalCostPrice(this.getTotalCostPrice());
    salesOrderItemDTO.setUnit(this.getUnit());
    salesOrderItemDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    salesOrderItemDTO.setBusinessCategoryName(this.getBusinessCategoryName());
		salesOrderItemDTO.setReserved(this.getReserved());
    salesOrderItemDTO.setPromotionsIds(null);     //todo 改掉
    salesOrderItemDTO.setQuotedPrice(this.getQuotedPrice());
    salesOrderItemDTO.setQuotedPreBuyOrderItemId(this.getQuotedPreBuyOrderItemId());
    salesOrderItemDTO.setPromotionsId(getPromotionsId());
    salesOrderItemDTO.setCustomPriceFlag(getCustomPriceFlag());
    if(getCustomPriceFlag() == null){
      if(getQuotedPreBuyOrderItemId()!=null){
        salesOrderItemDTO.setCustomPriceFlag(false);
      }else if(getPromotionsId()!=null){
        salesOrderItemDTO.setCustomPriceFlag(false);
      }else if(!NumberUtil.isEqual(getQuotedPrice(), getPrice())){
        salesOrderItemDTO.setCustomPriceFlag(true);
      }else{
        salesOrderItemDTO.setCustomPriceFlag(false);
      }
    }
    return salesOrderItemDTO;
  }

  public SalesOrderItem fromDTO(SalesOrderItemDTO salesOrderItemDTO){
    if(salesOrderItemDTO == null)
      return this;
    setId(salesOrderItemDTO.getId());
    this.salesOrderId = salesOrderItemDTO.getSalesOrderId();
    this.productId = salesOrderItemDTO.getProductId();
    this.productHistoryId = salesOrderItemDTO.getProductHistoryId();
    this.amount = salesOrderItemDTO.getAmount();
    this.price = salesOrderItemDTO.getPrice();
    this.total = salesOrderItemDTO.getTotal();
    this.memo = salesOrderItemDTO.getMemo();
    this.costPrice = salesOrderItemDTO.getCostPrice();
    this.totalCostPrice = salesOrderItemDTO.getTotalCostPrice();
    this.shopId = salesOrderItemDTO.getShopId();
    this.unit = salesOrderItemDTO.getUnit();
    this.businessCategoryId = salesOrderItemDTO.getBusinessCategoryId();
    this.businessCategoryName = salesOrderItemDTO.getBusinessCategoryName();
	  this.reserved = salesOrderItemDTO.getReserved();
    this.quotedPrice = salesOrderItemDTO.getQuotedPrice();
    this.setQuotedPreBuyOrderItemId(salesOrderItemDTO.getQuotedPreBuyOrderItemId());
    this.setPromotionsId(salesOrderItemDTO.getPromotionsId());
    this.setCustomPriceFlag(salesOrderItemDTO.getCustomPriceFlag());
    return this;
  }






  @Column(name="quoted_price")
  public Double getQuotedPrice() {
    return quotedPrice;
  }

  public void setQuotedPrice(Double quotedPrice) {
    this.quotedPrice = quotedPrice;
  }

  @Column(name="promotions_id")
  public String getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(String promotionsId) {
    this.promotionsId = promotionsId;
  }

  @Column(name = "custom_price_flag")
  public Boolean getCustomPriceFlag() {
    return customPriceFlag;
  }

  public void setCustomPriceFlag(Boolean customPriceFlag) {
    this.customPriceFlag = customPriceFlag;
  }
}