package com.bcgogo.txn.dto;

import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户或者供应商对账单封装类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午1:35
 * To change this template use File | Settings | File Templates.
 */
public class StatementAccountOrderDTO implements Serializable,Comparable<StatementAccountOrderDTO>  {
  private Long id;
  private String idStr;
  private Long shopId;     //店铺id
  private Long startDate; //对账日期 开始时间
  private Long endDate;   //对账日期 结束时间
  private String receiptNo; //对账单据号
  private Long vestDate; //结算时间 默认为系统时间
  private Long salesManId; //结算人id
  private String salesMan; //结算人名字
  private Long customerOrSupplierId;//客户或者供应商id
  private String customerOrSupplierIdStr;//客户或者供应商id
  private String customerOrSupplier; //客户或者供应商名字
  private double total;   //应收 应付总和
  private OrderTypes orderType;//单据类型 默认为客户对账单 或者供应商对账单
  private OrderStatus orderStatus;//单据状态

  private String orderTypeStr;//对账单类型
  private String vestDateStr; //对账日期
  private double settledAmount; //实收
  private double discount; //折扣
  private double debt; //欠款
  private Long orderId;//单据id

  private String orderIdStr;

  private String startDateStr; //对账日期 开始时间
  private String endDateStr;   //对账日期 结束时间

  private String orderTotalStr;//对账总额字符串

  private String settledAmountStr;//实收实付 字符串


  private OrderDebtType orderDebtType;//单据欠款类型

  private String memberNumber;//客户本身会员号码

  private String mobile;//手机号码

  //对账单结算相关信息
  private String accountMemberNo; //结算时填的会员号码
  private String accountMemberPassword;  //结算时填的会员密码
  private Long accountMemberId;//结算时会员id
  private String memberPassword;    //会员密码
  private Double memberAmount;  //储值支付
  private Double cashAmount;        //支付方式:现金
  private Double bankAmount;      //支付方式 银行卡
  private Double bankCheckAmount;       //支付方式 支票
  private String bankCheckNo;          //支付方式 支票号码
  private String paymentTimeStr;//还款时间
  private Long paymentTime;//还款时间
  private Double depositAmount;//定金支付金额
  private Double statementAmount;//对账支付

  private boolean sendMemberSms;//是否发送短信
  private String print;//是否打印

  private String jsonStr;//json数据

  private Long receivableId;//每个欠款单据的receivableId;
  private Long debtId;//每个欠款单据的debtId;

  private Long payableId;//每个欠款单据的payableId


  public List<StatementAccountOrderDTO> orderDTOList;//对账单所对的单据列表

  private Long statementAccountOrderId;//对账单id

  public MemberDTO memberDTO;//会员信息

  //客户或者供应商信息
  private String contact;
  private String address;

  private double totalReceivable;
  private double totalPayable;

  public Long creationDate;//创建时间

  private String receptNoListStr;
  private String identity;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getReceptNoListStr() {
    return receptNoListStr;
  }

  public void setReceptNoListStr(String receptNoListStr) {
    this.receptNoListStr = receptNoListStr;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
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

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getCustomerOrSupplierIdStr() {
    return customerOrSupplierIdStr;
  }

  public void setCustomerOrSupplierIdStr(String customerOrSupplierIdStr) {
    this.customerOrSupplierIdStr = customerOrSupplierIdStr;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(Double depositAmount) {
    this.depositAmount = depositAmount;
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  public List<StatementAccountOrderDTO> getOrderDTOList() {
    return orderDTOList;
  }

  public void setOrderDTOList(List<StatementAccountOrderDTO> orderDTOList) {
    this.orderDTOList = orderDTOList;
  }

  public Long getPayableId() {
    return payableId;
  }

  public void setPayableId(Long payableId) {
    this.payableId = payableId;
  }

  public String getOrderTotalStr() {
    return orderTotalStr;
  }

  public void setOrderTotalStr(String orderTotalStr) {
    this.orderTotalStr = orderTotalStr;
  }

  public Long getReceivableId() {
    return receivableId;
  }

  public void setReceivableId(Long receivableId) {
    this.receivableId = receivableId;
  }

  public Long getDebtId() {
    return debtId;
  }

  public void setDebtId(Long debtId) {
    this.debtId = debtId;
  }

  public StatementAccountOrderDTO() {

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = (id == null ? "" : id.toString());
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
    this.vestDateStr = DateUtil.dateLongToStr(vestDate, DateUtil.DATE_STRING_FORMAT_DAY);
  }

  public Long getSalesManId() {
    return salesManId;
  }

  public void setSalesManId(Long salesManId) {
    this.salesManId = salesManId;
  }

  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
    this.customerOrSupplierIdStr = (customerOrSupplierId == null ? "" : customerOrSupplierId.toString());
  }

  public String getCustomerOrSupplier() {
    return customerOrSupplier;
  }

  public void setCustomerOrSupplier(String customerOrSupplier) {
    this.customerOrSupplier = customerOrSupplier;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
    if(orderType!=null)
    this.orderTypeStr = orderType.getName();
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = debt;
  }

  public OrderDebtType getOrderDebtType() {
    return orderDebtType;
  }

  public void setOrderDebtType(OrderDebtType orderDebtType) {
    this.orderDebtType = orderDebtType;
  }

  public String getMemberNumber() {
    return memberNumber;
  }

  public void setMemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  public String getAccountMemberNo() {
    return accountMemberNo;
  }

  public void setAccountMemberNo(String accountMemberNo) {
    this.accountMemberNo = accountMemberNo;
  }

  public String getAccountMemberPassword() {
    return accountMemberPassword;
  }

  public void setAccountMemberPassword(String accountMemberPassword) {
    this.accountMemberPassword = accountMemberPassword;
  }

  public Long getAccountMemberId() {
    return accountMemberId;
  }

  public void setAccountMemberId(Long accountMemberId) {
    this.accountMemberId = accountMemberId;
  }

  public String getMemberPassword() {
    return memberPassword;
  }

  public void setMemberPassword(String memberPassword) {
    this.memberPassword = memberPassword;
  }

  public Double getMemberAmount() {
    return memberAmount;
  }

  public void setMemberAmount(Double memberAmount) {
    this.memberAmount = memberAmount;
  }

  public Double getCashAmount() {
    return cashAmount;
  }

  public void setCashAmount(Double cashAmount) {
    this.cashAmount = cashAmount;
  }

  public Double getBankAmount() {
    return bankAmount;
  }

  public void setBankAmount(Double bankAmount) {
    this.bankAmount = bankAmount;
  }

  public Double getBankCheckAmount() {
    return bankCheckAmount;
  }

  public void setBankCheckAmount(Double bankCheckAmount) {
    this.bankCheckAmount = bankCheckAmount;
  }

  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
  }

  public String getPaymentTimeStr() {
    return paymentTimeStr;
  }

  public String getSettledAmountStr() {
    return settledAmountStr;
  }

  public void setSettledAmountStr(String settledAmountStr) {
    this.settledAmountStr = settledAmountStr;
  }

  public void setPaymentTimeStr(String paymentTimeStr) {
    this.paymentTimeStr = paymentTimeStr;
    if (StringUtil.isNotEmpty(paymentTimeStr)) {
      try {
        this.setPaymentTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, paymentTimeStr));
      } catch (ParseException e) {
        this.setPaymentTime(null);
      }
    }
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  public boolean isSendMemberSms() {
    return sendMemberSms;
  }

  public void setSendMemberSms(boolean sendMemberSms) {
    this.sendMemberSms = sendMemberSms;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }

  public String getJsonStr() {
    return jsonStr;
  }

  public void setJsonStr(String jsonStr) {
    this.jsonStr = jsonStr;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = totalReceivable;
  }

  public double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(double totalPayable) {
    this.totalPayable = totalPayable;
  }

  public String getOrderIdStr() {
    return orderId==null?"":orderId.toString();
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public ReceivableDTO toReceivableDTO() {
    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setShopId(getShopId());
    receivableDTO.setOrderType(getOrderType());
    receivableDTO.setOrderId(getId());
    receivableDTO.setStatus(ReceivableStatus.FINISH);
    receivableDTO.setCustomerId(getCustomerOrSupplierId());
    receivableDTO.setReceiptNo(getReceiptNo());
    receivableDTO.setVestDate(getVestDate());
    receivableDTO.setOrderDebtType(getOrderDebtType());
    receivableDTO.setStatementAmount(0D);
    receivableDTO.setLastPayee(getSalesMan());
    receivableDTO.setLastPayeeId(getSalesManId());
    receivableDTO.setRemindTime(getPaymentTime());
    if(NumberUtil.doubleVal(getMemberAmount()) > 0) {
      receivableDTO.setMemberId(this.getMemberDTO().getId());
      receivableDTO.setMemberNo(this.getAccountMemberNo());
    }

    if (getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_PAYABLE) {
      receivableDTO.setSettledAmount(0 - getSettledAmount());
      receivableDTO.setDebt(0 - getDebt());
      receivableDTO.setDiscount(0 - getDiscount());
      receivableDTO.setTotal(0 - getTotal());
      receivableDTO.setMemberBalancePay(0 - getMemberAmount());
      receivableDTO.setCash(0 - getCashAmount());
      receivableDTO.setBankCard(0 - getBankAmount());
      receivableDTO.setCheque(0 - getBankCheckAmount());
      receivableDTO.setDeposit(0 - getDepositAmount()); // add by zhuj
      receivableDTO.setAfterMemberDiscountTotal(0 - getTotal());

    } else if (getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE) {
      receivableDTO.setSettledAmount(getSettledAmount());
      receivableDTO.setDebt(getDebt());
      receivableDTO.setDiscount(getDiscount());
      receivableDTO.setTotal(getTotal());
      receivableDTO.setMemberBalancePay(getMemberAmount());
      receivableDTO.setCash(getCashAmount());
      receivableDTO.setBankCard(getBankAmount());
      receivableDTO.setCheque(getBankCheckAmount());
      receivableDTO.setDeposit(getDepositAmount()); // add by zhuj
      receivableDTO.setAfterMemberDiscountTotal(getTotal());
    }
    return receivableDTO;
  }

  public PayableDTO toPayableDTO() {
    PayableDTO payableDTO = new PayableDTO();
    payableDTO.setShopId(getShopId());
    payableDTO.setPayTime(getVestDate());
    payableDTO.setSupplierId(getCustomerOrSupplierId());
    payableDTO.setPurchaseInventoryId(getId());
    payableDTO.setStatus(PayStatus.USE);
    payableDTO.setReceiptNo(getReceiptNo());

    if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
      payableDTO.setAmount(0 - NumberUtil.doubleVal(getTotal()));
      payableDTO.setPaidAmount(0 - NumberUtil.doubleVal(getSettledAmount()));
      payableDTO.setCreditAmount(0 - NumberUtil.doubleVal(getDebt()));
      payableDTO.setDeduction(0 - NumberUtil.doubleVal(getDiscount()));
      payableDTO.setCash(0 - NumberUtil.doubleVal(getCashAmount()));
      payableDTO.setBankCard(0 - NumberUtil.doubleVal(getBankAmount()));
      payableDTO.setCheque(0 - NumberUtil.doubleVal(getBankCheckAmount()));
    } else if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
      payableDTO.setAmount(getTotal());
      payableDTO.setPaidAmount(getSettledAmount());
      payableDTO.setCreditAmount(getDebt());
      payableDTO.setDeduction(getDiscount());
      payableDTO.setCash(getCashAmount());
      payableDTO.setBankCard(getBankAmount());
      payableDTO.setCheque(getBankCheckAmount());
      payableDTO.setDeposit(getDepositAmount());
    }

    payableDTO.setLastPayer(getSalesMan());
    payableDTO.setLastPayerId(getSalesManId());
    payableDTO.setOrderType(getOrderType());
    payableDTO.setOrderDebtType(getOrderDebtType());
    payableDTO.setStrikeAmount(0D);
    payableDTO.setMaterialName(getOrderType().getName());
    return payableDTO;
  }


  public PayableHistoryDTO toPayableHistoryDTO() {
    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();
    payableHistoryDTO.setShopId(getShopId());

    payableHistoryDTO.setCheckNo(getBankCheckNo());

    payableHistoryDTO.setSupplierId(getCustomerOrSupplierId());
    payableHistoryDTO.setPayer(getSalesMan());
    payableHistoryDTO.setPayerId(getSalesManId());
    payableHistoryDTO.setPayTime(getVestDate());

    if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
      payableHistoryDTO.setDepositAmount(-getDepositAmount());
      payableHistoryDTO.setActuallyPaid(-getSettledAmount());
      payableHistoryDTO.setDeduction(-getDiscount());
      payableHistoryDTO.setCreditAmount(-getDebt());
      payableHistoryDTO.setCash(-getCashAmount());
      payableHistoryDTO.setBankCardAmount(-getBankAmount());
      payableHistoryDTO.setCheckAmount(-getBankCheckAmount());
    } else if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
      payableHistoryDTO.setDepositAmount(getDepositAmount());
      payableHistoryDTO.setActuallyPaid(getSettledAmount());
      payableHistoryDTO.setDeduction(getDiscount());
      payableHistoryDTO.setCreditAmount(getDebt());
      payableHistoryDTO.setCash(getCashAmount());
      payableHistoryDTO.setBankCardAmount(getBankAmount());
      payableHistoryDTO.setCheckAmount(getBankCheckAmount());
    }

    return payableHistoryDTO;
  }

  public PayableHistoryRecordDTO toPayableHistoryRecordDTO(PayableDTO payableDTO, PayableHistoryDTO payableHistoryDTO) {
    PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
    payableHistoryRecordDTO.setShopId(getShopId());

    payableHistoryRecordDTO.setCheckNo(getBankCheckNo());

    payableHistoryRecordDTO.setPurchaseInventoryId(getId());
    payableHistoryRecordDTO.setPayableHistoryId(payableHistoryDTO.getId());
    payableHistoryRecordDTO.setSupplierId(getCustomerOrSupplierId());
    payableHistoryRecordDTO.setPayableId(payableDTO.getId());
    payableHistoryRecordDTO.setStatus(PayStatus.USE);
    payableHistoryRecordDTO.setPaidTime(System.currentTimeMillis());
    payableHistoryRecordDTO.setStrikeAmount(0D);
    payableHistoryRecordDTO.setDayType(DayType.STATEMENT_ORDER);
    payableHistoryRecordDTO.setPayer(getSalesMan());
    payableHistoryRecordDTO.setPayerId(getSalesManId());
    payableHistoryRecordDTO.setPaymentType(PaymentTypes.STATEMENT_ORDER);

    //正值
    if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
      payableHistoryRecordDTO.setDeduction(getDiscount());
      payableHistoryRecordDTO.setCreditAmount(getDebt());
      payableHistoryRecordDTO.setCash(getCashAmount());
      payableHistoryRecordDTO.setBankCardAmount(getBankAmount());
      payableHistoryRecordDTO.setCheckAmount(getBankCheckAmount());
      payableHistoryRecordDTO.setAmount(getTotal());
      payableHistoryRecordDTO.setPaidAmount(getSettledAmount());
      payableHistoryRecordDTO.setDepositAmount(getDepositAmount());
      payableHistoryRecordDTO.setActuallyPaid(getSettledAmount());
      payableHistoryRecordDTO.setMaterialName(getOrderType().getName() + "付款");

    } else if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
      payableHistoryRecordDTO.setDeduction(-getDiscount());
      payableHistoryRecordDTO.setCreditAmount(-getDebt());
      payableHistoryRecordDTO.setCash(-getCashAmount());
      payableHistoryRecordDTO.setBankCardAmount(-getBankAmount());
      payableHistoryRecordDTO.setCheckAmount(-getBankCheckAmount());
      payableHistoryRecordDTO.setAmount(-getTotal());
      payableHistoryRecordDTO.setPaidAmount(-getSettledAmount());
      payableHistoryRecordDTO.setDepositAmount(0D);
      payableHistoryRecordDTO.setActuallyPaid(-getSettledAmount());
      payableHistoryRecordDTO.setMaterialName(getOrderType().getName() + "收款");

    }

    return payableHistoryRecordDTO;
  }

  public ReceptionRecordDTO toReceptionRecordDTO(ReceivableDTO receivableDTO) {
    ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
    receptionRecordDTO.setReceivableId(getReceivableId());
    receptionRecordDTO.setPayeeId(getSalesManId());
    receptionRecordDTO.setPayee(getSalesMan());
    receptionRecordDTO.setChequeNo(getBankCheckNo());
    receptionRecordDTO.setRecordNum(0);
    receptionRecordDTO.setShopId(getShopId());
    receptionRecordDTO.setOrderId(getId());
    receptionRecordDTO.setReceptionDate(System.currentTimeMillis());
    receptionRecordDTO.setStrike(0D);
    receptionRecordDTO.setOrderTypeEnum(getOrderType());
    receptionRecordDTO.setOrderStatusEnum(getOrderStatus());
    receptionRecordDTO.setDayType(DayType.STATEMENT_ORDER);
    receptionRecordDTO.setOriginDebt(0D);

    if(NumberUtil.doubleVal(getMemberAmount()) > 0) {
      receptionRecordDTO.setMemberId(this.getMemberDTO().getId());
      receptionRecordDTO.setMemberNo(this.getAccountMemberNo());
    }

    if (getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE) {
      receptionRecordDTO.setAmount(getSettledAmount());
      receptionRecordDTO.setMemberBalancePay(NumberUtil.doubleVal(getMemberAmount()));
      receptionRecordDTO.setCash(NumberUtil.doubleVal(getCashAmount()));
      receptionRecordDTO.setBankCard(NumberUtil.doubleVal(getBankAmount()));
      receptionRecordDTO.setCheque(NumberUtil.doubleVal(getBankCheckAmount()));
      receptionRecordDTO.setRemainDebt(getDebt());
      receptionRecordDTO.setOrderTotal(getTotal());
      receptionRecordDTO.setDiscount(NumberUtil.doubleVal(getDiscount()));
      receptionRecordDTO.setAfterMemberDiscountTotal(getTotal());

    } else if (getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_PAYABLE) {
      receptionRecordDTO.setAmount(-getSettledAmount());
      receptionRecordDTO.setMemberBalancePay(-NumberUtil.doubleVal(getMemberAmount()));
      receptionRecordDTO.setCash(-NumberUtil.doubleVal(getCashAmount()));
      receptionRecordDTO.setBankCard(-NumberUtil.doubleVal(getBankAmount()));
      receptionRecordDTO.setCheque(-NumberUtil.doubleVal(getBankCheckAmount()));
      receptionRecordDTO.setRemainDebt(-getDebt());
      receptionRecordDTO.setOrderTotal(-getTotal());
      receptionRecordDTO.setAfterMemberDiscountTotal(-getTotal());
      receptionRecordDTO.setDiscount(-NumberUtil.doubleVal(getDiscount()));
    }

    return receptionRecordDTO;
  }

  public DebtDTO toDebtDTO(ReceivableDTO receivableDTO) {
    DebtDTO debtDTO = new DebtDTO();
    debtDTO.setShopId(shopId);
    debtDTO.setCustomerId(getCustomerOrSupplierId());
    debtDTO.setOrderId(getId());
    debtDTO.setRecievableId(receivableDTO.getId());
    debtDTO.setOrderType(getOrderType());
    debtDTO.setOrderTime(getVestDate());
    debtDTO.setVehicleNumber(null);
    debtDTO.setContent(getOrderType().getName());
    debtDTO.setService(null);
    debtDTO.setMaterial(null);
    debtDTO.setTotalAmount(getTotal());
    debtDTO.setSettledAmount(getSettledAmount());
    debtDTO.setDebt(getDebt());
    debtDTO.setPayTime(getVestDate());
    debtDTO.setRemindTime(getPaymentTime());
    debtDTO.setStatus(DebtStatus.ARREARS);
    debtDTO.setReceiptNo(getReceiptNo());
    debtDTO.setRemindStatus(UserConstant.Status.ACTIVITY);
    return debtDTO;
  }


  public RunningStatDTO toRunningStatDTO() {
    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(getShopId());
    runningStatDTO.setStatYear((long) DateUtil.getYear(getVestDate()));
    runningStatDTO.setStatMonth((long) DateUtil.getMonth(getVestDate()));
    runningStatDTO.setStatDay((long) DateUtil.getDay(getVestDate()));
    runningStatDTO.setStatDate(getVestDate());

    //客户对账单
    if (getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
      if (getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE) {

        runningStatDTO.setCashIncome(NumberUtil.doubleVal(getCashAmount()));
        runningStatDTO.setChequeIncome(NumberUtil.doubleVal(getBankCheckAmount()));
        runningStatDTO.setUnionPayIncome(NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setMemberPayIncome(NumberUtil.doubleVal(getMemberAmount()));
        runningStatDTO.setDebtWithdrawalIncome(NumberUtil.doubleVal(getTotalReceivable()));
        runningStatDTO.setIncomeSum(NumberUtil.doubleVal(getCashAmount()) + NumberUtil.doubleVal(getBankCheckAmount()) + NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setRunningSum(runningStatDTO.getIncomeSum());

      } else if (getOrderDebtType() == OrderDebtType.CUSTOMER_DEBT_PAYABLE) {
        runningStatDTO.setCashIncome(-NumberUtil.doubleVal(getCashAmount()));
        runningStatDTO.setChequeIncome(-NumberUtil.doubleVal(getBankCheckAmount()));
        runningStatDTO.setUnionPayIncome(-NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setDepositPayExpenditure(-NumberUtil.doubleVal(getDepositAmount())); // add by zhuj
        runningStatDTO.setDebtWithdrawalIncome(NumberUtil.doubleVal(getTotalReceivable()));
        runningStatDTO.setIncomeSum(NumberUtil.doubleVal(getCashAmount()) + NumberUtil.doubleVal(getBankCheckAmount()) + NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setIncomeSum(-runningStatDTO.getIncomeSum());
        runningStatDTO.setRunningSum(runningStatDTO.getIncomeSum());
      }
    } else if (getOrderType() == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
        runningStatDTO.setCashExpenditure(NumberUtil.doubleVal(getCashAmount()));
        runningStatDTO.setChequeExpenditure(NumberUtil.doubleVal(getBankCheckAmount()));
        runningStatDTO.setUnionPayExpenditure(NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setExpenditureSum(NumberUtil.doubleVal(getCashAmount()) + NumberUtil.doubleVal(getBankCheckAmount()) + NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setRunningSum(0 - runningStatDTO.getExpenditureSum());
        runningStatDTO.setDebtWithdrawalExpenditure(NumberUtil.doubleVal(getTotalPayable()));
        runningStatDTO.setDepositPayExpenditure(NumberUtil.doubleVal(getDepositAmount()));

      } else if (getOrderDebtType() == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
        runningStatDTO.setCashExpenditure(-NumberUtil.doubleVal(getCashAmount()));
        runningStatDTO.setChequeExpenditure(-NumberUtil.doubleVal(getBankCheckAmount()));
        runningStatDTO.setUnionPayExpenditure(-NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setExpenditureSum(NumberUtil.doubleVal(getCashAmount()) + NumberUtil.doubleVal(getBankCheckAmount()) + NumberUtil.doubleVal(getBankAmount()));
        runningStatDTO.setExpenditureSum(-runningStatDTO.getExpenditureSum());
        runningStatDTO.setDebtWithdrawalExpenditure(NumberUtil.doubleVal(getTotalPayable()));
        runningStatDTO.setRunningSum(-runningStatDTO.getExpenditureSum());
      }
    }

    return runningStatDTO;
  }


  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(getOrderType());
    orderIndexDTO.setCreationDate(this.getCreationDate());
    //orderIndex中的状态和salesOrderDTO的状态保持一致。
    orderIndexDTO.setOrderStatus(this.getOrderStatus());
    orderIndexDTO.setVestDate(this.getVestDate());

    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerOrSupplierId());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomerOrSupplier());
    orderIndexDTO.setCustomerStatus(CustomerStatus.ENABLED);
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setAddress(this.getAddress());
    orderIndexDTO.setContact(this.getContact());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());

    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setOrderTotalCostPrice(0D);
    orderIndexDTO.setArrears(this.getDebt());
    orderIndexDTO.setOrderDebt(this.getDebt());
    orderIndexDTO.setOrderSettled(this.getSettledAmount());
    orderIndexDTO.setDiscount(this.getDiscount());
    orderIndexDTO.setMemberBalancePay(this.getMemberAmount());
    orderIndexDTO.setAccountMemberId(this.getAccountMemberId());
    orderIndexDTO.setAccountMemberNo(this.getAccountMemberNo());

    orderIndexDTO.setOrderContent(getOrderType().getName());

    orderIndexDTO.setAfterMemberDiscountTotal(this.getTotal());

    if (this.getPaymentTime() != null && this.getPaymentTime() != 0) {
      orderIndexDTO.setPaymentTime(this.getPaymentTime());
    }

    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (NumberUtil.doubleVal(this.getCashAmount()) != 0) { //现金
      payMethods.add(PayMethod.CASH);
    }
    if (NumberUtil.doubleVal(this.getBankAmount()) != 0) { //银行卡
      payMethods.add(PayMethod.BANK_CARD);
    }
    if (NumberUtil.doubleVal(this.getBankCheckAmount()) != 0) {// 支票
      payMethods.add(PayMethod.CHEQUE);
    }
    if (this.getAccountMemberId() != null && this.getAccountMemberId() != 0) {   //会员支付
      payMethods.add(PayMethod.MEMBER_BALANCE_PAY);
    }
    if (this.getStatementAccountOrderId() != null) {//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }

    orderIndexDTO.setPayMethods(payMethods);
    return orderIndexDTO;
  }

  @Override
  public int compareTo(StatementAccountOrderDTO statementAccountOrderDTO) {

    OrderTypes thisOrderType = this.getOrderType();
    OrderTypes compareOrderType = statementAccountOrderDTO.getOrderType();
    if (thisOrderType == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT || thisOrderType == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      return -1;
    } else if (compareOrderType == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT || compareOrderType == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      return 1;
    }
    return 0;
  }

  @Override
  public String toString() {
    return "StatementAccountOrderDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", receiptNo='" + receiptNo + '\'' +
        ", vestDate=" + vestDate +
        ", salesManId=" + salesManId +
        ", salesMan='" + salesMan + '\'' +
        ", customerOrSupplierId=" + customerOrSupplierId +
        ", customerOrSupplier=" + customerOrSupplier +
        ", total=" + total +
        ", orderType=" + orderType +
        ", orderTypeStr='" + orderTypeStr + '\'' +
        ", vestDateStr='" + vestDateStr + '\'' +
        ", settledAmount=" + settledAmount +
        ", discount=" + discount +
        ", debt=" + debt +
        ", orderId=" + orderId +
        ", orderDebtType=" + orderDebtType +
        ", memberNumber='" + memberNumber + '\'' +
        ", accountMemberNo='" + accountMemberNo + '\'' +
        ", accountMemberPassword='" + accountMemberPassword + '\'' +
        ", accountMemberId=" + accountMemberId +
        ", memberPassword='" + memberPassword + '\'' +
        ", memberAmount=" + memberAmount +
        ", cashAmount=" + cashAmount +
        ", bankAmount=" + bankAmount +
        ", bankCheckAmount=" + bankCheckAmount +
        ", bankCheckNo='" + bankCheckNo + '\'' +
        ", depositAmount='" + depositAmount + '\'' +
        ", paymentTimeStr='" + paymentTimeStr + '\'' +
        ", sendMemberSms=" + sendMemberSms +
        ", print='" + print + '\'' +
        ", jsonStr='" + jsonStr + '\'' +
        '}';
  }
}
