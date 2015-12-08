package com.bcgogo.product.model;

import com.bcgogo.enums.ProductAdStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_local_info")
public class ProductLocalInfo extends LongIdentifier {
  private Double price; // 价格（销售单）
  private Double purchasePrice; // 采购价
  private Long productId;
  private Long shopId;
  private String storageUnit; // 大单位
  private String sellUnit;  // 小单位
  private Long rate;
  private Double percentage;
  private Double percentageAmount;
  private String pointsExchangeable;
  private Double tradePrice;   //批发价
  private String storageBin;// 仓位
  private Long businessCategoryId;
  private ProductStatus salesStatus = ProductStatus.NotInSales;//上架 下架  默认下架状态
  private Double inSalesAmount;//上架量    -1 代表 有货   -2  无货
  private Double inSalesPrice;//上架销售价
  private String inSalesUnit;//上架单位
  private String guaranteePeriod;
  private Long lastInSalesTime;//最后上架时间
  private Long lastOffSalesTime;//最后下架时间
  private ProductAdStatus adStatus;//默认广告商品状态

  public ProductLocalInfo() {
  }

  public ProductLocalInfo fromProductLocalInfoDTO(ProductLocalInfoDTO productLocalInfoDTO) {
    if (productLocalInfoDTO == null) {
      return this;
    }
    this.setShopId(productLocalInfoDTO.getShopId());
    this.setPrice(productLocalInfoDTO.getPrice());
    this.setProductId(productLocalInfoDTO.getProductId());
    this.setPurchasePrice(productLocalInfoDTO.getPurchasePrice());
    this.setId(productLocalInfoDTO.getId());
    this.setRate(productLocalInfoDTO.getRate());
    this.setSellUnit(productLocalInfoDTO.getSellUnit());
    this.setShopId(productLocalInfoDTO.getShopId());
    this.setStorageUnit(productLocalInfoDTO.getStorageUnit());
    this.setStorageBin(productLocalInfoDTO.getStorageBin());
    this.setTradePrice(productLocalInfoDTO.getTradePrice());
    this.setBusinessCategoryId(productLocalInfoDTO.getBusinessCategoryId());
    this.setStorageBin(productLocalInfoDTO.getStorageBin());
    this.setTradePrice(productLocalInfoDTO.getTradePrice());

    return this;
  }

  @Column(name = "percentage")
  public Double getPercentage() {
    return percentage;
  }

  @Column(name = "percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
  }

  @Column(name = "points_exchangeable")
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

  @Column(name = "purchase_price")
  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "storage_unit", length = 20)
  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    if(StringUtils.isNotBlank(storageUnit)){
      this.storageUnit = storageUnit;
    }else {
      this.storageUnit = null;
    }
  }

  @Column(name = "sell_unit", length = 20)
  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    if (StringUtils.isNotBlank(sellUnit)) {
      this.sellUnit = sellUnit;
    } else {
      this.sellUnit = null;
    }
  }

  @Column(name = "rate")
  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }

  @Column(name = "trade_price")
  public Double getTradePrice() {
    return tradePrice;
  }

  public void setTradePrice(Double tradePrice) {
    this.tradePrice = tradePrice;
  }

  @Column(name = "storage_bin" , length = 20)
  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }



  @Column(name = "sales_status")
  @Enumerated(EnumType.STRING)
  public ProductStatus getSalesStatus() {
    return salesStatus;
  }

  public void setSalesStatus(ProductStatus salesStatus) {
    this.salesStatus = salesStatus;
  }

  @Column(name="in_sales_amount")
  public Double getInSalesAmount() {
    return inSalesAmount;
  }

  public void setInSalesAmount(Double inSalesAmount) {
    this.inSalesAmount = inSalesAmount;
  }

  @Column(name="in_sales_price")
  public Double getInSalesPrice() {
    return inSalesPrice;
  }

  public void setInSalesPrice(Double inSalesPrice) {
    this.inSalesPrice = inSalesPrice;
  }

    @Column(name="in_sales_unit")
  public String getInSalesUnit() {
    return inSalesUnit;
  }

  public void setInSalesUnit(String inSalesUnit) {
    this.inSalesUnit = inSalesUnit;
  }

  @Column(name="last_in_sales_time")
  public Long getLastInSalesTime() {
    return lastInSalesTime;
  }

  public void setLastInSalesTime(Long lastInSalesTime) {
    this.lastInSalesTime = lastInSalesTime;
  }

  @Column(name="last_off_sales_time")
  public Long getLastOffSalesTime() {
    return lastOffSalesTime;
  }

  public void setLastOffSalesTime(Long lastOffSalesTime) {
    this.lastOffSalesTime = lastOffSalesTime;
  }

  @Column(name="guarantee_period")
  public String getGuaranteePeriod() {
    return guaranteePeriod;
  }

  public void setGuaranteePeriod(String guaranteePeriod) {
    this.guaranteePeriod = guaranteePeriod;
  }

  @Column(name="ad_status")
  @Enumerated(EnumType.STRING)
  public ProductAdStatus getAdStatus() {
    return adStatus;
  }

  public void setAdStatus(ProductAdStatus adStatus) {
    this.adStatus = adStatus;
  }

   public ProductLocalInfoDTO toDTO() {
    ProductLocalInfoDTO dto = new ProductLocalInfoDTO();
    dto.setId(getId());
    dto.setPrice(getPrice());
    dto.setProductId(getProductId());
    dto.setPurchasePrice(getPurchasePrice());
    dto.setRate(getRate());
    dto.setSellUnit(getSellUnit());
    dto.setShopId(getShopId());
    dto.setStorageUnit(getStorageUnit());
    dto.setStorageBin(getStorageBin());
    dto.setTradePrice(getTradePrice());
    dto.setStorageBin(getStorageBin());
    dto.setPercentage(getPercentage());
    dto.setPercentageAmount(getPercentageAmount());
    dto.setPointsExchangeable(getPointsExchangeable());
    dto.setBusinessCategoryId(getBusinessCategoryId());
    dto.setVersion(getVersion());
    dto.setLastInSalesTime(getLastInSalesTime());
    dto.setLastOffSalesTime(getLastOffSalesTime());
    dto.setInSalesAmount(getInSalesAmount());
    dto.setInSalesPrice(getInSalesPrice());
    dto.setInSalesUnit(getInSalesUnit());
    dto.setGuaranteePeriod(getGuaranteePeriod());
    dto.setSalesStatus(getSalesStatus());
    dto.setAdStatus(getAdStatus());
    return dto;
  }

  public void fromDTO(ProductLocalInfoDTO localInfoDTO) {
    this.setId(localInfoDTO.getId());
    this.setPrice(localInfoDTO.getPrice());
    this.setProductId(localInfoDTO.getProductId());
    this.setPurchasePrice(localInfoDTO.getPurchasePrice());
    this.setRate(localInfoDTO.getRate());
    this.setSellUnit(localInfoDTO.getSellUnit());
    this.setShopId(localInfoDTO.getShopId());
    this.setStorageUnit(localInfoDTO.getStorageUnit());
    this.setStorageBin(localInfoDTO.getStorageBin());
    this.setTradePrice(localInfoDTO.getTradePrice());
    this.setStorageBin(localInfoDTO.getStorageBin());
    this.setPercentage(localInfoDTO.getPercentage());
    this.setPercentageAmount(localInfoDTO.getPercentageAmount());
    this.setPointsExchangeable(localInfoDTO.getPointsExchangeable());
    this.setBusinessCategoryId(localInfoDTO.getBusinessCategoryId());
    this.setLastInSalesTime(localInfoDTO.getLastInSalesTime());
    this.setLastOffSalesTime(localInfoDTO.getLastOffSalesTime());
    this.setInSalesAmount(localInfoDTO.getInSalesAmount());
    this.setInSalesPrice(localInfoDTO.getInSalesPrice());
    this.setInSalesUnit(localInfoDTO.getInSalesUnit());
    this.setGuaranteePeriod(localInfoDTO.getGuaranteePeriod());
    this.setSalesStatus(localInfoDTO.getSalesStatus());
    this.setAdStatus(localInfoDTO.getAdStatus());
  }

  /**
   * product的 id key   不是productlocalid
   * @param productLocalInfoList
   * @return
   */
  public static Map<Long,ProductLocalInfo> listToMap(List<ProductLocalInfo> productLocalInfoList) {
    Map<Long, ProductLocalInfo> map = new HashMap<Long, ProductLocalInfo>();

    if (CollectionUtils.isEmpty(productLocalInfoList)) {
      return null;
    }

    for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
      map.put(productLocalInfo.getProductId(), productLocalInfo);
    }

    return map;
  }

  /**
   * product的 id key   不是productlocalid
   * @param productLocalInfoList
   * @return
   */
  public static Map<Long,ProductLocalInfo> listToProductLocalInfoIdKeyMap(List<ProductLocalInfo> productLocalInfoList) {
    Map<Long, ProductLocalInfo> map = new HashMap<Long, ProductLocalInfo>();

    if (CollectionUtils.isEmpty(productLocalInfoList)) {
      return null;
    }

    for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
      map.put(productLocalInfo.getId(), productLocalInfo);
    }

    return map;
  }

  //2014-07-30 16:55:50        1406710550000
  //2014-07-25 16:55:50        1406278550000
  public static void main(String[]args){
    try {
      Long res=DateUtil.convertDateStringToDateLong(DateUtil.ALL,"2014-07-25 16:55:50");
      System.out.println(res);
    } catch (ParseException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }
}
