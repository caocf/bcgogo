package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.WashOrderDTO;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-6
 * Time: 下午8:56
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "wash_order")
public class WashOrder  extends LongIdentifier {
  public WashOrder(){}

  public WashOrder fromDTO(WashOrderDTO washOrderDTO) {
    if(washOrderDTO==null)
      return this;
    this.setId(washOrderDTO.getId());
    this.setShopId(washOrderDTO.getShopId());
    this.setCustomerId(washOrderDTO.getCustomerId());
    this.setCardId(washOrderDTO.getCardId());
    this.setOrderTypeEnum(washOrderDTO.getOrderType());
    this.setCashNum(washOrderDTO.getCashNum());
    this.setState(washOrderDTO.getState());
    this.setWashWorker(washOrderDTO.getWashWorker());
    this.setCostPrice(washOrderDTO.getCostPrice());
    this.setVestDate(washOrderDTO.getVestDate());

    return this;
  }

  public WashOrderDTO toDTO() {
    WashOrderDTO washOrderDTO = new WashOrderDTO();

    washOrderDTO.setId(this.getId());
    washOrderDTO.setShopId(this.getShopId());
    washOrderDTO.setCustomerId(this.getCustomerId());
    washOrderDTO.setCardId(this.getCardId());
    washOrderDTO.setOrderType(getOrderTypeEnum());
    washOrderDTO.setCashNum(this.getCashNum());
    washOrderDTO.setState(this.getState());
    washOrderDTO.setWashWorker(this.getWashWorker());
    Date date = new Date(this.getCreationDate());
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd");
    washOrderDTO.setCreationDate(simpleDateFormat.format(date));
    washOrderDTO.setVestDate(this.getVestDate());
    washOrderDTO.setVestDateStr(simpleDateFormat.format(new Date(this.getVestDate())));
    washOrderDTO.setCostPrice(this.costPrice);
    return washOrderDTO;
  }

  private Long shopId;
  private Long customerId;
  /**
   * 0:会员卡充值
   * 1:会员卡洗车
   * 2:现金洗车
   */
  private Long orderType;
  private OrderTypes orderTypeEnum;

  private Long cardId;
  private double cashNum;
  private Long state;
  private Long washTimes;
  private String washWorker;
  private Double costPrice;
  private Long vestDate;
  private Double percentage;
  private Double PercentageAmount;

  @Column(name="percentage")
  public Double getPercentage() {
      return percentage;
  }
  @Column(name="percentage_amount")
  public Double getPercentageAmount() {
      return PercentageAmount;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      PercentageAmount = percentageAmount;
  }

  @Column(name="vest_date")
  public Long getVestDate()
  {
      return this.vestDate;
  }

  public void setVestDate(Long vestDate)
  {
      this.vestDate =vestDate;
  }

  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  @Column(name = "wash_worker")
  public String getWashWorker() {
        return washWorker;
  }
  public void setWashWorker(String washWorker) {
        this.washWorker = washWorker;
  }
  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "order_type")
  public Long getOrderType() {
    return orderType;
  }

  public void setOrderType(Long orderType) {
    this.orderType = orderType;
  }

  @Column(name = "card_id")
  public Long getCardId() {
    return cardId;
  }

  public void setCardId(Long cardId) {
    this.cardId = cardId;
  }

  @Column(name = "cash_num")
  public double getCashNum() {
    return cashNum;
  }

  public void setCashNum(double cashNum) {
    this.cashNum = cashNum;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "wash_times")
  public Long getWashTimes() {
    return washTimes;
  }

  public void setWashTimes(Long washTimes) {
    this.washTimes = washTimes;
  }

  @Column(name = "order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

}
