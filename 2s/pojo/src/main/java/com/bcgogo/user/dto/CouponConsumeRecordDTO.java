package com.bcgogo.user.dto;

import com.bcgogo.enums.IncomeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LiTao on 2015/11/6.
 * 用以接收代金券消费信息的类
 * 用于txn.consuming_record、bcuser.app_user、bcuser.app_vehicle的联合查询
 */
public class CouponConsumeRecordDTO {
    private String appUserNo;          //用户ID
    private String receiptNo;         //订单号
    private Double coupon;             //使用代金券金额
    private Long shopId;              //消费店铺
    private Long orderId;             //单据ID
    private String orderIdStr;             //单据ID字符串
    private String orderTypes;    //单据类型
    private String consumerTime;        //消费时间
    private String product;           //购买的商品
    private Integer productNum;       //商品数量
    private String incomeType;    //收入支出类型
    private Float sumMoney;           //总金额
    private String appUserName;       //客户名
    private String appVehicleNo;      //车牌号
    private Long id;      //代金券消费记录id
    private String idStr;      //代金券消费记录id
    private String customerInfo;      //客户信息，包括客户名字和车牌号
    private OrderStatus orderStatus;         //消费状态

    public CouponConsumeRecordDTO(String appUserNo, String receiptNo, Double coupon, Long shopId, Long orderId, String orderTypes, String consumerTime, String product, Integer productNum, String incomeType, Float sumMoney, String appUserName, String appVehicleNo, Long id, String customerInfo, OrderStatus orderStatus) {
        this.appUserNo = appUserNo;
        this.receiptNo = receiptNo;
        this.coupon = coupon;
        this.shopId = shopId;
        this.orderId = orderId;
        this.orderTypes = orderTypes;
        this.consumerTime = consumerTime;
        this.product = product;
        this.productNum = productNum;
        this.incomeType = incomeType;
        this.sumMoney = sumMoney;
        this.appUserName = appUserName;
        this.appVehicleNo = appVehicleNo;
        this.id = id;
        this.customerInfo = customerInfo;
        this.orderStatus = orderStatus;
    }

    public String getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CouponConsumeRecordDTO() {
        super();
    }

    public String getAppUserNo() {
        return appUserNo;
    }

    public void setAppUserNo(String appUserNo) {
        this.appUserNo = appUserNo;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

  public Double getCoupon() {
    return coupon;
  }

  public void setCoupon(Double coupon) {
    this.coupon = coupon;
  }

  public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
        if(orderId!=null) {
            this.orderIdStr = orderId.toString();
        }
    }

    public String getOrderIdStr() {
        if(orderIdStr==null||"".equals(orderIdStr)){
            if(orderId!=null) {
                return orderId.toString();
            }
            return null;
        }
        return orderIdStr;
    }

    public void setOrderIdStr(String orderIdStr) {
        this.orderIdStr = orderIdStr;
    }

    public String getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypesToChinese(OrderTypes type){
        if (type!=null){
            this.orderTypes = type.getName();
        }
        else{
            this.orderTypes = "";
        }
    }

    public void setOrderTypesToChinese(String orderTypes) {
        if (StringUtil.isEmpty(orderTypes)){
            this.orderTypes="";
            return;
        }
        OrderTypes types=OrderTypes.valueOf(orderTypes);
        this.orderTypes = types.getName();
    }

    public void setOrderTypes(OrderTypes type){
        if (type!=null){
            this.orderTypes = type.name();
        }
        else{
            this.orderTypes = "";
        }
    }
    public void setOrderTypes(String orderTypes) {
        this.orderTypes = orderTypes;
    }

    public String getConsumerTime() {
        return consumerTime;
    }

    public Long getConsumerTimeStamp() throws ParseException {
        Long timeStamp=null;
        Date date = new SimpleDateFormat().parse(consumerTime);
        return date.getTime();
    }
    public void setConsumerTime(String consumerTime) {
        this.consumerTime = consumerTime;
    }

    public void setConsumerTime(Long consumerTime) {
        Date date=new Date(consumerTime);
        this.consumerTime = new SimpleDateFormat().format(date);
//        this.consumerTime = consumerTime;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getProductNum() {
        return productNum;
    }

    public void setProductNum(Integer productNum) {
        this.productNum = productNum;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeTypeToChinese(IncomeType type){
        if (type!=null){
            this.incomeType = type.getName();
        }
        else{
            this.incomeType = "";
        }
    }

    public void setIncomeTypeToChinese(String incomeType) {
        if(StringUtil.isEmpty(incomeType)){
            this.incomeType = "";
            return;
        }
        IncomeType type=IncomeType.valueOf(incomeType);
        this.incomeType = type.getName();
    }
    public void setIncomeType(IncomeType type){
        if (type!=null){
            this.incomeType = type.name();
        }
        else{
            this.incomeType = "";
        }
    }
    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public Float getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(Float sumMoney) {
        this.sumMoney = sumMoney;
    }

    public void setSumMoney(Double total) {
        this.sumMoney=total.floatValue();
    }

    public String getAppUserName() {
        return appUserName;
    }

    public void setAppUserName(String appUserName) {
        this.appUserName = appUserName;
    }

    public String getAppVehicleNo() {
        return appVehicleNo;
    }

    public void setAppVehicleNo(String appVehicleNo) {
        this.appVehicleNo = appVehicleNo;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    public void setOrderStatus(String orderStatusName) {
        this.orderStatus =OrderStatus.parseEnum(orderStatusName);
    }
    public String getIdStr() {
        return id.toString();
    }
}
