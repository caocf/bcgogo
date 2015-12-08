package com.bcgogo.search.dto;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXOrderDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/3/12
 * Time: 8:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderSearchResultDTO {
  private static final Logger LOG = LoggerFactory.getLogger(OrderSearchResultDTO.class);
  private String editor;
  private Long shopId;         //店面ID
  private String shopName;
  private String shopIdStr;         //店面ID
  private String shopAreaInfo;
  private Long orderId;        //单子ID
  private String orderIdStr;        //单子ID
  private String orderType;    // 单子类型
  private String orderTypeValue;    // 单子类型 Value
  private String orderStatus;     //  状态
  private String orderStatusValue;     //状态Value
  private String vehicle;      // 车牌号
  private String customerOrSupplierName;   // 客户或供应商名字
  private Long customerOrSupplierId;
  private Boolean notPaid; //欠款；
  private Long paymentTime;//还款时间
  private String contactNum;//联系方式
  private String contact;//联系ren
  private String address;
  private String[] serviceWorker; //维修美容单中的施工人 或者洗车单中的洗车人
  private String serviceWorkers; //以，分开
  private String[] salesman;
  private String salesMans;
  private String operator;
  private String[] products;
  private String vModel; // repair order's vehicle
  private String vBrand;
  private String[] payMethod;
  private Long createdTime;
  private String createdTimeStr;
  private Long vestDate;
  private String vestDateStr;
  private Long endDate;
  private String endDateStr;
  private String orderContent;
  private List<ItemIndexDTO> itemIndexDTOs;
  private String receiptNo; //单据号
  private String memberNo;    //会员号
  private String memberType;    //会员卡类型
  private String memo;    //会员号
  private String memberStatus;//会员状态
  private Double memberLastBuyTotal;//上次购卡金额
  private Long memberLastBuyDate;//上次购卡时间
  private String  memberLastBuyDateStr;
  private Double worth;        //储值新增金额
  private Double memberBalance;//储值余额
  private Double memberLastRecharge;//上次储值余额
  private CustomerStatus customerStatus;
  private Double amount; //单据总额
  private Double debt; //单据欠款
  private Double settled; //单据实收
  private Double totalCostPrice;//成本
  private Double discount;//折扣
  private Double grossProfit;//毛利
  private Double grossProfitRate;//毛利率

  private String inTimeStr; //进厂时间
  private String outTimeStr;//出厂时间

  //会员消费统计
  private String consumeType;
  private String orderContentShort;
  private String accountMemberNo;//结算时的会员号码
  private Double memberBalancePay;//结算时使用会员支付的金额

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;
  private String storehouseName;

  //销售退货统计
  private String originOrderIdStr;
  private String originOrderType;
  private String originReceiptNo;


  private String productNames;

  private String customerOrSupplierIdStr;

  private Long purchaseInventoryId;  //采购关联的入库单Id
  private String purchaseInventoryIdStr;
  private String purchaseInventoryReceiptNo; //采购关联的入库单单据号

  private Long customerOrSupplierShopId;
  private String customerOrSupplierShopIdStr;

  private String title;
  private String preBuyOrderStatusStr;

  private Double otherTotalCostPrice;//销售单 施工单 其他费用成本总和
  private Double otherIncomeTotal;//销售单 施工单 其他费用总和
  private Double productTotal;
  private Double productTotalCostPrice;

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getPreBuyOrderStatusStr() {
    return preBuyOrderStatusStr;
  }

  public void setPreBuyOrderStatusStr(String preBuyOrderStatusStr) {
    this.preBuyOrderStatusStr = preBuyOrderStatusStr;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public String getShopAreaInfo() {
    return shopAreaInfo;
  }

  public void setShopAreaInfo(String shopAreaInfo) {
    this.shopAreaInfo = shopAreaInfo;
  }

  public String getOriginReceiptNo() {
    return originReceiptNo;
  }

  public void setOriginReceiptNo(String originReceiptNo) {
    this.originReceiptNo = originReceiptNo;
  }

  public String getOriginOrderIdStr() {
    return originOrderIdStr;
  }

  public void setOriginOrderIdStr(String originOrderIdStr) {
    this.originOrderIdStr = originOrderIdStr;
  }

  public String getOriginOrderType() {
    return originOrderType;
  }

  public void setOriginOrderType(String originOrderType) {
    this.originOrderType = originOrderType;
  }

  public String getCustomerOrSupplierIdStr() {
    return customerOrSupplierIdStr;
  }

  public void setCustomerOrSupplierIdStr(String customerOrSupplierIdStr) {
    this.customerOrSupplierIdStr = customerOrSupplierIdStr;
  }

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  public Double getMemberBalancePay() {
    return memberBalancePay;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
    this.memberBalancePay = memberBalancePay;
  }

  public String getAccountMemberNo() {
    return accountMemberNo;
  }

  public void setAccountMemberNo(String accountMemberNo) {
    this.accountMemberNo = accountMemberNo;
  }

  public String getOrderContentShort() {
    return orderContentShort;
  }

  public void setOrderContentShort(String orderContentShort) {
    this.orderContentShort = StringUtil.getShortString(this.getOrderContent(),0,14);
  }


  public String getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(String consumeType) {
    this.consumeType = consumeType;
  }

  public Double getMemberLastRecharge() {
    return memberLastRecharge;
  }

  public void setMemberLastRecharge(Double memberLastRecharge) {
    this.memberLastRecharge = memberLastRecharge;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public Double getSettled() {
    return settled;
  }

  public void setSettled(Double settled) {
    this.settled = settled;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
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
    this.orderIdStr = String.valueOf(orderId);
    this.orderId = orderId;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    try {
      //转换 orderType
      orderTypeValue = OrderTypes.valueOf(orderType).getName();
      this.orderType = orderType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    if (orderStatus == null) return;
    try {
      //转换 orderStatus
      this.orderStatusValue = OrderStatus.valueOf(orderStatus).getName();
      this.orderStatus = orderStatus;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public Boolean getNotPaid() {
    return notPaid;
  }

  public void setNotPaid(Boolean notPaid) {
    this.notPaid = notPaid;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  public String getContactNum() {
    return contactNum;
  }

  public void setContactNum(String contactNum) {
    this.contactNum = contactNum;
  }

  public String[] getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String[] serviceWorker) {
    this.serviceWorker = serviceWorker;
    serviceWorkers = StringUtils.join(serviceWorker, ",");
  }

  public String getServiceWorkers() {
    return serviceWorkers;
  }

  public void setServiceWorkers(String serviceWorkers) {
    this.serviceWorkers = serviceWorkers;
  }

  public String[] getSalesman() {
    return salesman;
  }

  public void setSalesman(String[] salesman) {
    this.salesman = salesman;
    this.salesMans = StringUtils.join(salesman, "，");
  }

  public String getSalesMans() {
    return salesMans;
  }

  public void setSalesMans(String salesMans) {
    this.salesMans = salesMans;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String[] getProducts() {
    return products;
  }

  public void setProducts(String[] products) {
    this.products = products;
  }

  public String getvModel() {
    return vModel;
  }

  public void setvModel(String vModel) {
    this.vModel = vModel;
  }

  public String getvBrand() {
    return vBrand;
  }

  public void setvBrand(String vBrand) {
    this.vBrand = vBrand;
  }

  public String[] getPayMethod() {
    return payMethod;
  }

  public void setPayMethod(String[] payMethod) {
    this.payMethod = payMethod;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, createdTime);
    this.createdTime = createdTime;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, endDate);
    this.endDate = endDate;
    if(endDate!=null){
      try {
        if(endDate>=DateUtil.getTheDayTime()){
          this.preBuyOrderStatusStr="有效";
        }else{
          this.preBuyOrderStatusStr ="过期";
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, vestDate);
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getOrderContent() {
    return orderContent;
  }

  public void setOrderContent(String orderContent) {
    this.orderContentShort = StringUtil.getShortString(orderContent,0,20);
    this.orderContent = orderContent;
  }

  public String getCreatedTimeStr() {
    return createdTimeStr;
  }

  public void setCreatedTimeStr(String createdTimeStr) {
    this.createdTimeStr = createdTimeStr;
  }

  public String getOrderTypeValue() {
    return orderTypeValue;
  }

  public void setOrderTypeValue(String orderTypeValue) {
    this.orderTypeValue = orderTypeValue;
  }

  public String getOrderStatusValue() {
    return orderStatusValue;
  }

  public void setOrderStatusValue(String orderStatusValue) {
    this.orderStatusValue = orderStatusValue;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public List<ItemIndexDTO> getItemIndexDTOs() {
    return itemIndexDTOs;
  }

  public void setItemIndexDTOs(List<ItemIndexDTO> itemIndexDTOs) {
    this.itemIndexDTOs = itemIndexDTOs;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
    this.customerOrSupplierIdStr = customerOrSupplierId == null?"":customerOrSupplierId.toString();
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public void setCustomerStatus(CustomerStatus customerStatus) {
    this.customerStatus = customerStatus;
  }

  public String generateOrderContent() {
    if (CollectionUtils.isEmpty(this.getItemIndexDTOs())) {
      return null;
    }
    StringBuffer str = new StringBuffer();
    if (this.getOrderType().equals("REPAIR")) {
      str.append("施工内容：");
    } else if (this.getOrderType().equals("SALE")) {
      str.append("销售内容:");
    } else if (this.getOrderType().equals("INVENTORY")) {
      str.append("入库商品：");
    }
    if (this.getOrderType().equals("WASH_BEAUTY")) {
      for (ItemIndexDTO orderIndexDTO : this.getItemIndexDTOs()) {
        if (!StringUtil.isAllEmpty(orderIndexDTO.getServices())) {
          str.append(orderIndexDTO.getServices());
          str.append(";");
        }
      }
      return str.toString();
    }
    String itemBrand = "";
    String itemName = "";
    Double itemCostPrice = 0d;
    Double itemCount = 0d;
    for (ItemIndexDTO orderIndexDTO : this.getItemIndexDTOs()) {
      //过滤掉施工单中ItemType为空的记录，防止施工单中施工内容重复显示
      if("REPAIR".equals(this.getOrderType()) && orderIndexDTO.getServices() != null && orderIndexDTO.getItemType() == null) {
          continue;
      }
      itemName = orderIndexDTO.getItemName() == null ? "" : orderIndexDTO.getItemName();
      itemBrand = orderIndexDTO.getItemBrand() == null ? "" : orderIndexDTO.getItemBrand();
      itemCostPrice = orderIndexDTO.getItemCostPrice() == null ? 0d : orderIndexDTO.getItemCostPrice();
      itemCount = orderIndexDTO.getItemCount() == null ? 0d : orderIndexDTO.getItemCount();
      str.append("(品名:").append(itemName);
      if (!StringUtils.isBlank(itemBrand)) {
        str.append(",品牌:").append(itemBrand).append(",单价:").append(itemCostPrice).append("数量:").append(itemCount).append(");");
      } else {
        str.append("(单价:").append(itemCostPrice).append("数量:").append(itemCount).append(");");
      }
    }
    return str.toString();
  }


  public void setMemberStatus(String memberStatus) {
    this.memberStatus = memberStatus;
  }

  public String getMemberStatus() {
    return memberStatus;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  public Double getWorth() {
    return worth;
  }

  public Double getMemberLastBuyTotal() {
    return memberLastBuyTotal;
  }

  public void setMemberLastBuyTotal(Double memberLastBuyTotal) {
    this.memberLastBuyTotal = memberLastBuyTotal;
  }

  public Long getMemberLastBuyDate() {
    return memberLastBuyDate;
  }

  public void setMemberLastBuyDate(Long memberLastBuyDate) {
    this.memberLastBuyDate = memberLastBuyDate;
    memberLastBuyDateStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd",memberLastBuyDate);
  }

  public String getMemberLastBuyDateStr() {
    return memberLastBuyDateStr;
  }

  public void setMemberLastBuyDateStr(String memberLastBuyDateStr) {
    this.memberLastBuyDateStr = memberLastBuyDateStr;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public Double getGrossProfitRate() {
    return grossProfitRate;
  }

  public void setGrossProfitRate(Double grossProfitRate) {
    this.grossProfitRate = grossProfitRate;
  }

  public Double getGrossProfit() {
    return grossProfit;
  }

  public void setGrossProfit(Double grossProfit) {
    this.grossProfit = grossProfit;
  }

  public String getInTimeStr() {
    return inTimeStr;
  }

  public void setInTimeStr(String inTimeStr) {
    this.inTimeStr = inTimeStr;
  }

  public String getOutTimeStr() {
    return outTimeStr;
  }

  public void setOutTimeStr(String outTimeStr) {
    this.outTimeStr = outTimeStr;
  }


  public Double getMemberDiscountRatio() {
    return memberDiscountRatio;
  }

  public void setMemberDiscountRatio(Double memberDiscountRatio) {
    this.memberDiscountRatio = memberDiscountRatio;
  }

  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }

  public String getProductNames() {
    return productNames;
  }

  public void setProductNames(String productNames) {
    this.productNames = productNames;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
    this.setPurchaseInventoryIdStr(purchaseInventoryId == null ? null : purchaseInventoryId.toString());
  }

  public String getPurchaseInventoryIdStr() {
    return purchaseInventoryIdStr;
  }

  public void setPurchaseInventoryIdStr(String purchaseInventoryIdStr) {
    this.purchaseInventoryIdStr = purchaseInventoryIdStr;
  }

  public String getPurchaseInventoryReceiptNo() {
    return purchaseInventoryReceiptNo;
  }

  public void setPurchaseInventoryReceiptNo(String purchaseInventoryReceiptNo) {
    this.purchaseInventoryReceiptNo = purchaseInventoryReceiptNo;
  }

  public Long getCustomerOrSupplierShopId() {
    return customerOrSupplierShopId;
  }

  public void setCustomerOrSupplierShopId(Long customerOrSupplierShopId) {
    this.customerOrSupplierShopId = customerOrSupplierShopId;
    if(this.customerOrSupplierShopId != null){
      this.setCustomerOrSupplierShopIdStr(customerOrSupplierShopId.toString());
    }else {
      this.setCustomerOrSupplierShopIdStr("");
    }
  }

  public String getCustomerOrSupplierShopIdStr() {
    return customerOrSupplierShopIdStr;
  }

  public void setCustomerOrSupplierShopIdStr(String customerOrSupplierShopIdStr) {
    this.customerOrSupplierShopIdStr = customerOrSupplierShopIdStr;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Double getOtherTotalCostPrice() {
    return otherTotalCostPrice;
  }

  public void setOtherTotalCostPrice(Double otherTotalCostPrice) {
    this.otherTotalCostPrice = otherTotalCostPrice;
  }

  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
  }

  public Double getProductTotal() {
    return productTotal;
  }

  public void setProductTotal(Double productTotal) {
    this.productTotal = productTotal;
  }

  public Double getProductTotalCostPrice() {
    return productTotalCostPrice;
  }

  public void setProductTotalCostPrice(Double productTotalCostPrice) {
    this.productTotalCostPrice = productTotalCostPrice;
  }


}
