package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.InstalmentPlanDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午9:01
 * 分期付款
 */
@Entity
@Table(name = "instalment_plan")
public class InstalmentPlan extends LongIdentifier {
  private final static Long MONTH_TIME_MILLIS = 30 * 24 * 60 * 60 * 1000L;
  private static final Double ZERO = 0.0001D;
  private Long shopId;
  private Double totalAmount;        //总金额
  private Double payableAmount;      //应付金额
  private Double paidAmount;         //已付金额
  private Long startTime;             //总的开始时间
  private Long endTime;               //总的截止时间
  private Integer periods;            //期数
  private PaymentStatus status;    //状态
  private Long currentItemId;        //当前处于第几期Id
  private Long currentItemEndTime;  //当前分期阶段结束日期
  private String memo;

  public InstalmentPlan() {
    super();
  }

  public Map<Integer, InstalmentPlanItem> firstInstalmentPlan(BcgogoReceivableOrder order, InstalmentPlanAlgorithm algorithm, PaymentMethod paymentMethod) throws BcgogoException {
    this.setShopId(order.getShopId());
    this.setTotalAmount(order.getTotalAmount());
    this.setPaidAmount(order.getReceivedAmount());
    this.setPayableAmount(order.getReceivableAmount());
    this.setStartTime(order.getStartTime());
    if (algorithm == null) throw new BcgogoException("InstalmentPlanAlgorithm is null");
    if (StringUtils.isBlank(algorithm.getTerminallyRatio()))
      throw new BcgogoException("InstalmentPlanAlgorithm terminally ratio is null");
    String[] radios = algorithm.getTerminallyRatio().split(",");
    Map<Integer, InstalmentPlanItem> items = new HashMap<Integer, InstalmentPlanItem>();
    InstalmentPlanItem item, oldBrotherItem = null;
    Double paidAmount = this.getPaidAmount();
    int periodNumber = 1;
    this.setPeriods(radios.length);
    double total = 0.0d;
    for (String radio : radios) {
      item = new InstalmentPlanItem();
      item.setShopId(order.getShopId());
      item.setProportion(Double.valueOf(radio));
      item.setPaidAmount(0d);
      if (periodNumber == 1) {
        item.setEndTime(order.getStartTime());
      } else {
        item.setEndTime(DateUtil.getNextMonthTime(oldBrotherItem.getEndTime()));
        if (periodNumber - this.getPeriods() == 0) {
          this.setEndTime(item.getEndTime());
        }
      }
      if (periodNumber - this.getPeriods() == 0) {
        item.setCurrentAmount(NumberUtil.round(order.getTotalAmount() - total, 0));
      } else {
        item.setCurrentAmount(NumberUtil.round(this.getTotalAmount() * item.getProportion(), 0));
        total += item.getCurrentAmount();
      }
      item.setStatus(PaymentStatus.NON_PAYMENT);
      item.setPeriodNumber(periodNumber++);
      if (paidAmount > ZERO) {
        item.setPaymentMethod(paymentMethod);
        if (paidAmount - item.getCurrentAmount() > ZERO) {
          paidAmount = paidAmount - item.getCurrentAmount();
          item.setPaidAmount(item.getCurrentAmount());
          item.setPayableAmount(0d);
          item.setStatus(PaymentStatus.FULL_PAYMENT);
        } else {
          item.setPaidAmount(NumberUtil.round(paidAmount,2));
          item.setPayableAmount(NumberUtil.round(item.getCurrentAmount() - item.getPaidAmount(),2));
          paidAmount = 0d;
          if (item.getPayableAmount() > ZERO) {
            item.setStatus(PaymentStatus.PARTIAL_PAYMENT);
          } else {
            item.setStatus(PaymentStatus.FULL_PAYMENT);
          }
        }
      }else{
        item.setPayableAmount(item.getCurrentAmount());
      }
      items.put(item.getPeriodNumber(), item);
      oldBrotherItem = item;
    }
    return items;
  }

  public InstalmentPlanDTO toDTO() {
    InstalmentPlanDTO dto = new InstalmentPlanDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setTotalAmount(this.getTotalAmount());
    dto.setPayableAmount(this.getPayableAmount());
    dto.setPaidAmount(this.getPaidAmount());
    dto.setStartTime(this.getStartTime());
    dto.setEndTime(this.getEndTime());
    dto.setPeriods(this.getPeriods());
    dto.setStatus(this.getStatus());
    dto.setCurrentItemId(this.getCurrentItemId());
    dto.setCurrentItemEndTime(this.getCurrentItemEndTime());
    dto.setMemo(this.getMemo());
    return dto;
  }


  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "total_amount")
  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Column(name = "payable_amount")
  public Double getPayableAmount() {
    return payableAmount;
  }

  public void setPayableAmount(Double payableAmount) {
    this.payableAmount = payableAmount;
  }

  @Column(name = "paid_amount")
  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  @Column(name = "start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Column(name = "end_time")
  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @Column(name = "periods")
  public Integer getPeriods() {
    return periods;
  }

  public void setPeriods(Integer periods) {
    this.periods = periods;
  }

  @Column(name = "current_item_id")
  public Long getCurrentItemId() {
    return currentItemId;
  }

  public void setCurrentItemId(Long currentItemId) {
    this.currentItemId = currentItemId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  @Column(name = "current_item_end_time")
  public Long getCurrentItemEndTime() {
    return currentItemEndTime;
  }

  public void setCurrentItemEndTime(Long currentItemEndTime) {
    this.currentItemEndTime = currentItemEndTime;
  }
}
