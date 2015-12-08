package com.bcgogo.user.model;

import com.bcgogo.enums.IncomeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CouponConsumeRecordDTO;


import javax.persistence.*;

/**
 * 商城现场消费记录
 * Created by IntelliJ IDEA.
 * Author : ztyu
 * Date: 2015/11/2
 * Time: 16:54
 */

@Entity
@Table(name = "consuming_record")
public class ConsumingRecord extends LongIdentifier {

  private String appUserNo;          //用户ID
  private String receiptNo;         //订单号
  private Double coupon;             //使用代金券金额
  private Long shopId;              //消费店铺
  private Long orderId;             //单据ID
  private OrderTypes orderTypes;    //单据类型
  private Long consumerTime;        //消费时间
  private long productId;           //商品的id
  private String product;           //购买的商品
  private Integer productNum;       //商品数量
  private IncomeType incomeType;    //收入支出类型
  private OrderStatus orderStatus;  //消费状态
  private double sumMoney;           //总金额
  private OrderStatus adminStatus;  //后台订单状态
  private String customerInfo;      //客户信息，包括客户名字和车牌号
  private String remark;            //app端附加信息


  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "coupon")
  public Double getCoupon() {
    return coupon;
  }

  public void setCoupon(Double coupon) {
    this.coupon = coupon;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_types")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(OrderTypes orderTypes) {
    this.orderTypes = orderTypes;
  }

  @Column(name = "order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Column(name = "consumer_time")
  public Long getConsumerTime() {
    return consumerTime;
  }

  public void setConsumerTime(Long consumerTime) {
    this.consumerTime = consumerTime;
  }

  @Column(name = "product_id")
  public long getProductId() {
    return productId;
  }

  public void setProductId(long productId) {
    this.productId = productId;
  }

  @Column(name = "product")
  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  @Column(name = "product_num")
  public Integer getProductNum() {
    return productNum;
  }

  public void setProductNum(Integer productNum) {
    this.productNum = productNum;
  }

  @Column(name = "income_type")
  @Enumerated(EnumType.STRING)
  public IncomeType getIncomeType() {
    return incomeType;
  }

  public void setIncomeType(IncomeType incomeType) {
    this.incomeType = incomeType;
  }

  @Column(name = "sum_money")
  public double getSumMoney() {
    return sumMoney;
  }

  public void setSumMoney(double sumMoney) {
    this.sumMoney = sumMoney;
  }

  @Column(name = "admin_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getAdminStatus() {
    return adminStatus;
  }

  public void setAdminStatus(OrderStatus adminStatus) {
    this.adminStatus = adminStatus;
  }

  @Column(name = "customer_info")
  public String getCustomerInfo() {
    return customerInfo;
  }

  public void setCustomerInfo(String customerInfo) {
    this.customerInfo = customerInfo;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public CouponConsumeRecordDTO toCouponConsumeRecordDTO() {
    CouponConsumeRecordDTO dto = new CouponConsumeRecordDTO();
    dto.setId(this.getId());
    dto.setAppUserNo(this.getAppUserNo());
    dto.setReceiptNo(this.getReceiptNo());
    dto.setCoupon(this.getCoupon());
    dto.setShopId(this.getShopId());
    dto.setOrderId(this.getOrderId());
    dto.setOrderTypes(this.getOrderTypes());
    dto.setConsumerTime(this.getConsumerTime());
    dto.setProduct(this.getProduct());
    dto.setProductNum(this.getProductNum());
    dto.setIncomeType(this.getIncomeType());
    dto.setSumMoney(this.getSumMoney());
    dto.setOrderStatus(this.getOrderStatus());
    return dto;
  }

  public ConsumingRecord() {

  }

  public ConsumingRecord(ConsumingRecord consumingRecord) {
    this.appUserNo = consumingRecord.getAppUserNo();
    this.receiptNo = consumingRecord.getReceiptNo();
    this.coupon = consumingRecord.getCoupon();
    this.shopId = consumingRecord.getShopId();
    this.orderId = consumingRecord.getOrderId();
    this.orderTypes = consumingRecord.getOrderTypes();
    this.consumerTime = consumingRecord.getConsumerTime();
    this.productId = consumingRecord.getProductId();
    this.product = consumingRecord.getProduct();
    this.productNum = consumingRecord.getProductNum();
    this.incomeType = consumingRecord.getIncomeType();
    this.orderStatus = consumingRecord.getOrderStatus();
    this.sumMoney = consumingRecord.getSumMoney();
    this.adminStatus = consumingRecord.getAdminStatus();
    this.customerInfo = consumingRecord.getCustomerInfo();
    this.remark = consumingRecord.getRemark();
  }
}
