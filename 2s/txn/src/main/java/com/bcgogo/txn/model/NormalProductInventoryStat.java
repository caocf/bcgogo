package com.bcgogo.txn.model;

import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.NormalProductInventoryStatDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * 后台CRM标准产品采购分析统计（只统计入库单）
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-2
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "normal_product_inventory_stat")
public class NormalProductInventoryStat extends LongIdentifier {

  private Long shopId;
  private Long normalProductId; //标准产品库id
  private NormalProductStatType normalProductStatType;
  private double amount; //入库数量
  private double total;  //入库总额
  private double topPrice; //最高价
  private double bottomPrice; //最低价
  private double times;//采购次数
  private Long productLocalInfoId; //统计到具体某个店的某个商品的采购

  public NormalProductInventoryStat() {

  }

  public NormalProductInventoryStat(NormalProductInventoryStatDTO normalProductInventoryStatDTO) {
    this.setId(normalProductInventoryStatDTO.getId());
    this.setShopId(normalProductInventoryStatDTO.getShopId());
    this.setNormalProductId(normalProductInventoryStatDTO.getNormalProductId());

    this.setNormalProductStatType(normalProductInventoryStatDTO.getNormalProductStatType());

    this.setAmount(normalProductInventoryStatDTO.getAmount());
    this.setTotal(normalProductInventoryStatDTO.getTotal());
    this.setTopPrice(normalProductInventoryStatDTO.getTopPrice());
    this.setBottomPrice(normalProductInventoryStatDTO.getBottomPrice());
    this.setTimes(normalProductInventoryStatDTO.getTimes());
    this.setProductLocalInfoId(normalProductInventoryStatDTO.getProductLocalInfoId());
  }

  public NormalProductInventoryStat fromDTO(NormalProductInventoryStatDTO normalProductInventoryStatDTO, boolean setId) {
    if (setId) {
      this.setId(normalProductInventoryStatDTO.getId());
    }
    this.setShopId(normalProductInventoryStatDTO.getShopId());
    this.setNormalProductId(normalProductInventoryStatDTO.getNormalProductId());

    this.setNormalProductStatType(normalProductInventoryStatDTO.getNormalProductStatType());

    this.setAmount(normalProductInventoryStatDTO.getAmount());
    this.setTotal(normalProductInventoryStatDTO.getTotal());
    this.setTopPrice(normalProductInventoryStatDTO.getTopPrice());
    this.setBottomPrice(normalProductInventoryStatDTO.getBottomPrice());
    this.setTimes(normalProductInventoryStatDTO.getTimes());
    this.setProductLocalInfoId(normalProductInventoryStatDTO.getProductLocalInfoId());

    return this;
  }

  public NormalProductInventoryStatDTO toDTO() {
    NormalProductInventoryStatDTO normalProductInventoryStatDTO = new NormalProductInventoryStatDTO();

    normalProductInventoryStatDTO.setId(this.getId());
    normalProductInventoryStatDTO.setShopId(this.getShopId());
    normalProductInventoryStatDTO.setNormalProductId(this.getNormalProductId());

    normalProductInventoryStatDTO.setNormalProductStatType(this.getNormalProductStatType());

    normalProductInventoryStatDTO.setAmount(this.getAmount());
    normalProductInventoryStatDTO.setTotal(this.getTotal());
    normalProductInventoryStatDTO.setTopPrice(this.getTopPrice());
    normalProductInventoryStatDTO.setBottomPrice(this.getBottomPrice());
    normalProductInventoryStatDTO.setTimes(this.getTimes());
    normalProductInventoryStatDTO.setProductLocalInfoId(this.getProductLocalInfoId());

    if (normalProductInventoryStatDTO.getAmount() > 0 && normalProductInventoryStatDTO.getTotal() >= 0) {
      normalProductInventoryStatDTO.setAveragePrice(NumberUtil.toReserve(normalProductInventoryStatDTO.getTotal() / normalProductInventoryStatDTO.getAmount(), NumberUtil.MONEY_PRECISION));
    } else {
      normalProductInventoryStatDTO.setAveragePrice(0D);
    }

    return normalProductInventoryStatDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }


  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "normal_product_id")
  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = NumberUtil.toReserve(amount,1);
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = NumberUtil.toReserve(total,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "top_price")
  public double getTopPrice() {
    return topPrice;
  }

  public void setTopPrice(double topPrice) {
    this.topPrice = NumberUtil.toReserve(topPrice,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "bottom_price")
  public double getBottomPrice() {
    return bottomPrice;
  }

  public void setBottomPrice(double bottomPrice) {
    this.bottomPrice = NumberUtil.toReserve(bottomPrice,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "stat_type")
  @Enumerated(EnumType.STRING)
  public NormalProductStatType getNormalProductStatType() {
    return normalProductStatType;
  }

  public void setNormalProductStatType(NormalProductStatType normalProductStatType) {
    this.normalProductStatType = normalProductStatType;
  }

  @Column(name = "times")
  public double getTimes() {
    return times;
  }

  public void setTimes(double times) {
    this.times = NumberUtil.toReserve(times,1);
  }

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }
}
