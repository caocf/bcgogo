package com.bcgogo.txn.model;

import com.bcgogo.BooleanEnum;
import com.bcgogo.enums.OtherIncomeCalculateWay;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SalesOrderOtherIncomeItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-11
 * Time: 下午1:46
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="sales_order_other_income_item")
public class SalesOrderOtherIncomeItem extends LongIdentifier{
  private Long shopId;
  private Long orderId;
  private String name;
  private String memo;
  private Double price;

  private Double costPrice;
  private BooleanEnum calculateCostPrice;

  private OtherIncomeCalculateWay otherIncomeCalculateWay;//施工单其他费用 材料管理费 计算方式
  private Double otherIncomePriceRate;//施工单其他费用 材料管理费 计算比率

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name="price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "calculate_cost_price")
  @Enumerated(EnumType.STRING)
  public BooleanEnum getCalculateCostPrice() {
    return calculateCostPrice;
  }

  public void setCalculateCostPrice(BooleanEnum calculateCostPrice) {
    this.calculateCostPrice = calculateCostPrice;
  }

  @Column(name = "other_income_calculate_way")
  @Enumerated(EnumType.STRING)
  public OtherIncomeCalculateWay getOtherIncomeCalculateWay() {
    return otherIncomeCalculateWay;
  }

  public void setOtherIncomeCalculateWay(OtherIncomeCalculateWay otherIncomeCalculateWay) {
    this.otherIncomeCalculateWay = otherIncomeCalculateWay;
  }

  @Column(name = "other_income_price_rate")
  public Double getOtherIncomePriceRate() {
    return otherIncomePriceRate;
  }

  public void setOtherIncomePriceRate(Double otherIncomePriceRate) {
    this.otherIncomePriceRate = otherIncomePriceRate;
  }

  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
     this.costPrice = costPrice;
  }



  public SalesOrderOtherIncomeItem(){
  }

  public SalesOrderOtherIncomeItem(SalesOrderOtherIncomeItemDTO salesOrderOherIncomeItemDTO)
  {
    if(null == salesOrderOherIncomeItemDTO)
    {
      return;
    }

    this.setMemo(salesOrderOherIncomeItemDTO.getMemo());
    this.setId(salesOrderOherIncomeItemDTO.getId());
    this.setName(salesOrderOherIncomeItemDTO.getName());
    this.setOrderId(salesOrderOherIncomeItemDTO.getOrderId());
    this.setShopId(salesOrderOherIncomeItemDTO.getShopId());
    this.setPrice(salesOrderOherIncomeItemDTO.getPrice());

    this.setOtherIncomeCalculateWay(salesOrderOherIncomeItemDTO.getOtherIncomeCalculateWay());
    this.setOtherIncomePriceRate(NumberUtil.doubleVal(salesOrderOherIncomeItemDTO.getOtherIncomeRate()));

    if (salesOrderOherIncomeItemDTO.getOtherIncomeCostPrice() == null) {
      this.setCalculateCostPrice(BooleanEnum.FALSE);
    } else {
      this.setCalculateCostPrice(BooleanEnum.TRUE);
    }
    this.setCostPrice(NumberUtil.doubleVal(salesOrderOherIncomeItemDTO.getOtherIncomeCostPrice()));

  }

  public SalesOrderOtherIncomeItemDTO toDTO()
  {
    SalesOrderOtherIncomeItemDTO salesOrderOtherIncomeItemDTO = new SalesOrderOtherIncomeItemDTO();
    salesOrderOtherIncomeItemDTO.setId(this.getId());
    salesOrderOtherIncomeItemDTO.setOrderId(this.getOrderId());
    salesOrderOtherIncomeItemDTO.setName(this.getName());
    salesOrderOtherIncomeItemDTO.setMemo(this.getMemo());
    salesOrderOtherIncomeItemDTO.setPrice(this.getPrice());
    salesOrderOtherIncomeItemDTO.setShopId(this.getShopId());
    salesOrderOtherIncomeItemDTO.setCalculateCostPrice(getCalculateCostPrice() == null?null:getCalculateCostPrice().name());
    salesOrderOtherIncomeItemDTO.setOtherIncomeCostPrice(getCostPrice());
    salesOrderOtherIncomeItemDTO.setOtherIncomeCalculateWay(getOtherIncomeCalculateWay());
    salesOrderOtherIncomeItemDTO.setOtherIncomeRate(getOtherIncomePriceRate());
    return salesOrderOtherIncomeItemDTO;
  }

  public void fromDTO(SalesOrderOtherIncomeItemDTO salesOrderOherIncomeItemDTO)
  {
    if(null != salesOrderOherIncomeItemDTO)
    {
      this.setMemo(salesOrderOherIncomeItemDTO.getMemo());
      this.setId(salesOrderOherIncomeItemDTO.getId());
      this.setName(salesOrderOherIncomeItemDTO.getName());
      this.setOrderId(salesOrderOherIncomeItemDTO.getOrderId());
      this.setShopId(salesOrderOherIncomeItemDTO.getShopId());
      this.setPrice(salesOrderOherIncomeItemDTO.getPrice());
      this.setOtherIncomeCalculateWay(salesOrderOherIncomeItemDTO.getOtherIncomeCalculateWay());
      this.setOtherIncomePriceRate(NumberUtil.doubleVal(salesOrderOherIncomeItemDTO.getOtherIncomeRate()));

      if (salesOrderOherIncomeItemDTO.getOtherIncomeCostPrice() == null) {
        this.setCalculateCostPrice(BooleanEnum.FALSE);
      } else {
        this.setCalculateCostPrice(BooleanEnum.TRUE);
      }
      this.setCostPrice(NumberUtil.doubleVal(salesOrderOherIncomeItemDTO.getOtherIncomeCostPrice()));
    }
  }
}
