package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PriceFluctuationStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: WeiLingfeng
 * Date: 12-11-5
 * Time: 上午9:25
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name="price_fluctuation_stat")
public class PriceFluctuationStat extends LongIdentifier {

  private Long shopId;
  private Long productId;           //product_local_info的ID
  private double amount;            //总数量
  private double total;             //总金额
  private int times;                //总次数
  private Long statTime;            //统计时间

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name="total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name="times")
  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  @Column(name="product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name="stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  public PriceFluctuationStatDTO toDTO(){
    PriceFluctuationStatDTO dto = new PriceFluctuationStatDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setAmount(this.getAmount());
    dto.setTotal(this.getTotal());
    dto.setTimes(this.getTimes());
    dto.setStatTime(this.getStatTime());
    dto.setProductId(this.getProductId());
    return dto;
  }
}
