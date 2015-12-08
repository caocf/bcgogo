package com.bcgogo.product.dto;

import com.bcgogo.enums.ProductAdStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.ProductLocalInfoRequest;
import com.bcgogo.txn.dto.PromotionsDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:33
 * To change this template use File | Settings | File Templates.
 */
public class ProductLocalInfoDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Long productId;
  private Double price;
  private Double purchasePrice;

  private String storageUnit;
  private String sellUnit;
  private Long rate;
  private Double percentage;
  private Double percentageAmount;
  private String pointsExchangeable;
	private Double tradePrice;   //批发价
	private String storageBin;// 仓位
  private Long businessCategoryId;
  private String businessCategoryName;
  private Long version;

  private ProductStatus salesStatus;//上架下架状态
  private Long lastInSalesTime;//最后上架时间
  private Long lastOffSalesTime;//最后下架时间
  private Double inSalesAmount;//上架量  //上架量    -1 代表 有货   -2  无货
  private Double inSalesPrice;
  private String inSalesUnit;
   private String guaranteePeriod;
  private List<PromotionsDTO> promotionsDTOs;

  private ProductAdStatus adStatus;

  public ProductLocalInfoDTO() {
  }

  public ProductLocalInfoDTO(ProductLocalInfoRequest request) {
    setShopId(request.getShopId());
    setProductId(request.getProductId());
    setPrice(request.getPrice());
    setPurchasePrice(request.getPurchasePrice());
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Long getRate(){
    return rate;
  }

  public void setRate(Long rate){
    this.rate = rate;
  }

  public Double getPercentage() {
      return percentage;
  }

  public Double getPercentageAmount() {
      return percentageAmount;
  }

  public String getPointsExchangeable() {
      return pointsExchangeable;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      this.percentageAmount = percentageAmount;
  }

  public void setPointsExchangeable(String pointsExchangeable) {
      this.pointsExchangeable = pointsExchangeable;
  }

	public Double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(Double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public String getStorageBin() {
		return storageBin;
	}

	public void setStorageBin(String storageBin) {
		this.storageBin = storageBin;
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

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
  }

  public Long getLastInSalesTime() {
    return lastInSalesTime;
  }

  public void setLastInSalesTime(Long lastInSalesTime) {
    this.lastInSalesTime = lastInSalesTime;
  }

  public Long getLastOffSalesTime() {
    return lastOffSalesTime;
  }

  public void setLastOffSalesTime(Long lastOffSalesTime) {
    this.lastOffSalesTime = lastOffSalesTime;
  }

  public Double getInSalesAmount() {
    return inSalesAmount;
  }

  public void setInSalesAmount(Double inSalesAmount) {
    this.inSalesAmount = inSalesAmount;
  }

  public Double getInSalesPrice() {
    return inSalesPrice;
  }

  public void setInSalesPrice(Double inSalesPrice) {
    this.inSalesPrice = inSalesPrice;
  }

  public String getInSalesUnit() {
    return inSalesUnit;
  }

  public void setInSalesUnit(String inSalesUnit) {
    this.inSalesUnit = inSalesUnit;
  }

  public String getGuaranteePeriod() {
    return guaranteePeriod;
  }

  public void setGuaranteePeriod(String guaranteePeriod) {
    this.guaranteePeriod = guaranteePeriod;
  }

  @Override
  public ProductLocalInfoDTO clone() throws CloneNotSupportedException {
    ProductLocalInfoDTO newInfoDTO = new ProductLocalInfoDTO();
    newInfoDTO.setId(id);
    newInfoDTO.setShopId(shopId);
    newInfoDTO.setProductId(productId);
    newInfoDTO.setPrice(price);
    newInfoDTO.setPurchasePrice(purchasePrice);
    newInfoDTO.setStorageUnit(storageUnit);
    newInfoDTO.setSellUnit(sellUnit);
    newInfoDTO.setRate(rate);
    newInfoDTO.setBusinessCategoryId(businessCategoryId);
    newInfoDTO.setBusinessCategoryName(businessCategoryName);
    newInfoDTO.setTradePrice(tradePrice);
    newInfoDTO.setStorageBin(storageBin);
    newInfoDTO.setInSalesPrice(inSalesPrice);
    newInfoDTO.setInSalesAmount(inSalesAmount);
    newInfoDTO.setGuaranteePeriod(guaranteePeriod);
    return newInfoDTO;
  }

  public ProductLocalInfoDTO fromProductForGoodsInSales(ProductDTO productDTO){
    if(productDTO.getPrice()!=null)
      this.setPrice(productDTO.getPrice());
    if(productDTO.getTradePrice()!=null)
      this.setTradePrice(productDTO.getTradePrice());
    if(productDTO.getInSalesAmount()!=null)
      this.setInSalesAmount(productDTO.getInSalesAmount());
    if(productDTO.getInSalesPrice()!=null)
      this.setInSalesPrice(productDTO.getInSalesPrice());
    if(productDTO.getGuaranteePeriod()!=null)
      this.setGuaranteePeriod(productDTO.getGuaranteePeriod());
    if(productDTO.getStorageUnit()!=null)
      this.setStorageUnit(productDTO.getStorageUnit());
    if(productDTO.getSellUnit()!=null)
      this.setSellUnit(productDTO.getSellUnit());
    if(productDTO.getRate()!=null)
      this.setRate(productDTO.getRate());
    if(productDTO.getUnit()!=null)
      this.setInSalesUnit(productDTO.getUnit());
    return this;
  }

  public List<PromotionsDTO> getPromotionsDTOs() {
    return promotionsDTOs;
  }

  public void setPromotionsDTOs(List<PromotionsDTO> promotionsDTOs) {
    this.promotionsDTOs = promotionsDTOs;
  }

  public ProductAdStatus getAdStatus() {
    return adStatus;
  }

  public void setAdStatus(ProductAdStatus adStatus) {
    this.adStatus = adStatus;
  }
}
