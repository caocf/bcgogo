package com.bcgogo.search.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.dto.MemberCardOrderServiceDTO;
import com.bcgogo.txn.dto.MemberCardReturnDTO;
import com.bcgogo.txn.dto.MemberCardReturnServiceDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 04/09/12
 * Time: 15:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "order_index")
public class OrderIndex extends LongIdentifier {
  public OrderIndex() {
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


  @Column(name = "vehicle", length = 20)
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_type", length = 20)
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }


  @Column(name = "order_status", length = 20)
  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Column(name = "order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name = "order_status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatusEnum() {
    return orderStatusEnum;
  }

  public void setOrderStatusEnum(OrderStatus orderStatusEnum) {
    this.orderStatusEnum = orderStatusEnum;
  }

  //zhouxiaochen 2012-1-6
  @Column(name = "customer_or_supplier_name", length = 100)
  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  @Column(name = "order_total_amount")
  public Double getOrderTotalAmount() {
    return orderTotalAmount;
  }

  public void setOrderTotalAmount(Double orderTotalAmount) {
    this.orderTotalAmount = orderTotalAmount;
  }

  @Column(name = "arrears")
  public Double getArrears() {
    return arrears;
  }

  public void setArrears(Double arrears) {
    this.arrears = arrears;
  }

  @Column(name = "payment_time")
  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }


  @Column(name = "service_worker")
  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  @Column(name = "order_content")
  public String getOrderContent() {
    return orderContent;
  }

  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;

  }

  @Column(name = "customer_or_supplier_id")
  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  @Column(name = "contact_num")
  public String getContactNum() {
    return contactNum;
  }

  public void setContactNum(String contactNum) {
    this.contactNum = contactNum;
  }

  @Column(name = "worth")
  public Double getWorth() {
    return worth;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  @Column(name = "member_balance")
  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  private Long shopId;         //店面ID
  private Long orderId;        //单子ID
  private String orderType;    // 单子类型
  private OrderTypes orderTypeEnum;

  private String orderContent;      //单子内容
  private String orderStatus;     //  状态
  private OrderStatus orderStatusEnum;

  private Double orderTotalAmount; //单子总额
  private String vehicle;      // 车牌号
  private Long customerOrSupplierId;    // 客户或供应商ID
  private String customerOrSupplierName;   // 客户或供应商名字  private Double orderTotalAmount; //单据金额总计
  private Double arrears; //欠款；
  private Long paymentTime;//还款时间
  private String contactNum;//联系方式
  private String serviceWorker; //维修美容单中的施工人 或者洗车单中的洗车人
  private Double worth;
  private Double memberBalance;
  private Long vestDate; //归属时间
  private String receiptNo;

  @Column(name = "vest_date")
  public Long getVestDate() {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }


  public OrderIndex fromDTO(OrderIndexDTO orderIndexDTO, boolean setId) {
    if (setId) {
      this.setId(orderIndexDTO.getId());
    }
    this.setShopId(orderIndexDTO.getShopId());
    this.setOrderId(orderIndexDTO.getOrderId());
    this.setOrderTypeEnum(orderIndexDTO.getOrderType());
    this.setOrderContent(orderIndexDTO.getOrderContent());
    this.setOrderStatusEnum(orderIndexDTO.getOrderStatus());
    this.setOrderStatus(orderStatus == null ? null : orderStatus.toString());
    this.setOrderTotalAmount(orderIndexDTO.getOrderTotalAmount());
    this.setVehicle(orderIndexDTO.getVehicle());
    this.setCustomerOrSupplierId(orderIndexDTO.getCustomerOrSupplierId());
    this.setCustomerOrSupplierName(orderIndexDTO.getCustomerOrSupplierName());
    this.setArrears(orderIndexDTO.getArrears());
    this.setPaymentTime(orderIndexDTO.getPaymentTime());
    this.setContactNum(orderIndexDTO.getContactNum());
    this.setServiceWorker(orderIndexDTO.getServiceWorker());
    this.setWorth(orderIndexDTO.getWorth());
    this.setMemberBalance(orderIndexDTO.getMemberBalance());
    this.setVestDate(orderIndexDTO.getVestDate());
    this.setReceiptNo(orderIndexDTO.getReceiptNo());
    return this;
  }

  public OrderIndexDTO toDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setId(this.getId());
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getOrderId());
    orderIndexDTO.setOrderType(getOrderTypeEnum());
    orderIndexDTO.setOrderContent(this.getOrderContent());
    orderIndexDTO.setOrderStatus(getOrderStatusEnum());
    orderIndexDTO.setOrderTotalAmount(this.getOrderTotalAmount());
    orderIndexDTO.setVehicle(this.getVehicle());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerOrSupplierId());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomerOrSupplierName());
    orderIndexDTO.setArrears(this.getArrears());
    orderIndexDTO.setPaymentTime(this.getPaymentTime());
    orderIndexDTO.setContactNum(this.getContactNum());
    orderIndexDTO.setServiceWorker(this.getServiceWorker());
    orderIndexDTO.setCreationDate(this.getCreationDate());
    orderIndexDTO.setWorth(this.getWorth());
    orderIndexDTO.setMemberBalance(this.getMemberBalance());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    return orderIndexDTO;
  }

  public OrderIndex(MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    if (null == memberCardOrderDTO) {
      return;
    }
    this.setShopId(memberCardOrderDTO.getShopId());
    this.setOrderId(memberCardOrderDTO.getId());
    this.setOrderType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);

    List<MemberCardOrderServiceDTO> memberCardServiceDTOs = memberCardOrderDTO.getNewMemberCardOrderServiceDTOs();
    if (CollectionUtils.isEmpty(memberCardServiceDTOs)) {
      if (StringUtils.isNotBlank(memberCardOrderDTO.getMemberCardName())) {
        this.setOrderContent("会员卡(" + memberCardOrderDTO.getMemberCardName() + ")");
      } else {
        this.setOrderContent("会员卡");
      }
    } else {
      StringBuffer orderContent = new StringBuffer();
      Integer balanceTimes = 0;
      List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
      ItemIndexDTO itemIndexDTO;
      for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardServiceDTOs) {
        if (memberCardOrderServiceDTO == null || null == memberCardOrderServiceDTO.getId()) {
          continue;
        }
        itemIndexDTO= new ItemIndexDTO();
        balanceTimes = memberCardOrderServiceDTO.getBalanceTimes();
        if (balanceTimes == null) {
          orderContent.append("(").append(memberCardOrderServiceDTO.getServiceName()).append(",").append(0).append("次)");
        } else if (balanceTimes == -1) {
          orderContent.append("(").append(memberCardOrderServiceDTO.getServiceName()).append(",").append("无限次").append(")");
        } else {
          orderContent.append("(").append(memberCardOrderServiceDTO.getServiceName()).append(",").append(balanceTimes).append("次)");
        }
        itemIndexDTOs.add(itemIndexDTO);
      }
      this.setOrderContent(orderContent.toString());
    }
    this.setOrderTotalAmount(memberCardOrderDTO.getTotal());
    this.setCustomerOrSupplierId(memberCardOrderDTO.getCustomerId());
    this.setCustomerOrSupplierName(memberCardOrderDTO.getCustomerName());
    this.setContactNum(memberCardOrderDTO.getMobile());
    if (memberCardOrderDTO.getReceivableDTO() != null) {
      this.setArrears(memberCardOrderDTO.getReceivableDTO().getDebt());
    }
    this.setMemberBalance(memberCardOrderDTO.getMemberBalance());
    this.setWorth(memberCardOrderDTO.getWorth());
    if (null != this.getArrears() && this.getArrears() > 0) {
      this.setPaymentTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberCardOrderDTO.getRepayTime()));
    }
  }

  public OrderIndex(MemberCardReturnDTO memberCardReturnDTO) throws Exception {
    if (null == memberCardReturnDTO) {
      return;
    }
    this.setShopId(memberCardReturnDTO.getShopId());
    this.setOrderId(memberCardReturnDTO.getId());
    this.setOrderType(TxnConstant.OrderType.ORDER_TYPE_RETURN_MEMBER_CARD);
    this.setOrderTypeEnum(OrderTypes.MEMBER_RETURN_CARD);
    this.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
    List<MemberCardReturnServiceDTO> memberCardServiceDTOs = memberCardReturnDTO.getMemberCardReturnServiceDTOs();
    if (CollectionUtils.isEmpty(memberCardServiceDTOs)) {
      if (StringUtils.isNotBlank(memberCardReturnDTO.getMemberCardName())) {
        this.setOrderContent("会员卡(" + memberCardReturnDTO.getMemberCardName() + ")");
      } else {
        this.setOrderContent("会员卡");
      }
    } else {
      StringBuffer orderContent = new StringBuffer();
      Integer balanceTimes = 0;
      for (MemberCardReturnServiceDTO memberCardReturnServiceDTO : memberCardServiceDTOs) {
        balanceTimes = memberCardReturnServiceDTO.getRemainTimes();
        if (balanceTimes == null) {
          orderContent.append("(").append(memberCardReturnServiceDTO.getServiceName()).append(",").append(0).append("次)");
        } else if (balanceTimes == -1) {
          orderContent.append("(").append(memberCardReturnServiceDTO.getServiceName()).append(",").append("无限次").append(")");
        } else {
          orderContent.append("(").append(memberCardReturnServiceDTO.getServiceName()).append(",").append(balanceTimes).append("次)");
        }
      }
      this.setOrderContent(orderContent.toString());
    }
    this.setOrderTotalAmount(memberCardReturnDTO.getTotal());
    this.setCustomerOrSupplierId(memberCardReturnDTO.getCustomerId());
    this.setCustomerOrSupplierName(memberCardReturnDTO.getCustomerName());
    this.setContactNum(memberCardReturnDTO.getMobile());
    this.setMemberBalance(memberCardReturnDTO.getMemberBalance());
    this.setWorth(memberCardReturnDTO.getLastRecharge());   //上次储值金额
  }
}