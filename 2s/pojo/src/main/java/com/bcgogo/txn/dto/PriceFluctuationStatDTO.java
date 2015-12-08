package com.bcgogo.txn.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 12-11-5
 * Time: 上午9:24
 * To change this template use File | Settings | File Templates.
 */
public class PriceFluctuationStatDTO {
  private Long id;
  private Long shopId;
  private double amount;            //总数量
  private double total;             //总金额
  private double bcgogoPrice;      //Bcgogo单价
  private int times;                //采购次数
  private Long productId;           //产品表ID
  private Long productLocalInfoId;//产品本地信息ID
  private Long statTime;            //统计时间

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

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public double getBcgogoPrice() {
    return bcgogoPrice;
  }

  public void setBcgogoPrice(double bcgogoPrice) {
    this.bcgogoPrice = bcgogoPrice;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }
}
