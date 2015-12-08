package com.bcgogo.txn.dto;

import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.RfTxnConstant;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午9:58
 * To change this template use File | Settings | File Templates.
 */
public class WashBeautyOrderDTO extends BcgogoOrderDto{
  public static final Logger LOG = LoggerFactory.getLogger(WashBeautyOrderDTO.class);
  private Long date;
  private String no;
  private Long deptId;
  private String dept;
  private Long vechicleId;
  private String vechicle;
  private String vehicleEngine;
  private String vehicleEngineNo;
  private String vehicleColor;
  private Long vehicleBuyDate;
  private String vehicleChassisNo;
  private String vehicleContact;
  private String vehicleMobile;
  private Long customerId;
  private String customerIdStr;
  private String customer;
  private String company;
  private String address;
  private CustomerStatus customerStatus;
  private Double startMileage;
  private Double endMileage;
  private String fuelNumber;
  private Long startDate;
  private Long endDate;
  private Long executorId;
  private String executor;
  private Double total;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;
  private String invalidateDate;
  private OrderStatus status;
  private String memo;
  private Double totalCostPrice;
  private Long vestDate;
  private WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs;
  private List<WashBeautyOrderItemDTO>  washBeautyOrderItemDTOList;
  private MemberDTO memberDTO;
  private String licenceNo;
  private String brand;
  private Long brandId;
  private String model;
  private Long modelId;
  private String contact;
  private String mobile;
  private String landLine;
  private ServiceDTO[] serviceDTOs;
   private double serviceTotal;   //施工单中的施工总费用

  private String memberType;//会员类型
	private String memberNo;  //会员号码
	private String memberStatus;       //会员状态
	private String memberPassword;    //会员密码
	private Double memberAmount;  //储值支付
	private Double cashAmount;        //支付方式:现金
	private Double bankAmount;      //支付方式 银行卡
	private Double bankCheckAmount;       //支付方式 支票
	private String bankCheckNo;          //支付方式 支票号码
  private Double strike;          //支付方式 冲账
  private String payee;     //结算人

  private String accountMemberNo; //结算时填的会员号码
  private String accountMemberPassword;  //结算时填的会员密码
  private Long accountMemberId;//结算时填的会员id

  private double settledAmount;    //实收
  private double debt;
  private double orderDiscount;
  private double discount;
  private String huankuanTime;  //还款时间
  private Long repaymentTime;//  还款时间

  private List<PayMethod> payMethods;

  private String vestDateStr;

  private String print;

  private SalesManDTO[] salesManDTOs;

  private String serviceWorker;//施工人
  private String orderContent;//施工内容
  private Long memberId;     //结算时使用的memberId

  private boolean sendMemberSms;
  

  private WashBeautyOrderItemDTO[] itemDTOs;

  private Long creationDate;

  private String receiptNo;

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;
  private double totalReturnDebt; //洗车单页面:应付
  private double totalReceivable;// 洗车单页面:应收
  private double totalConsume;//洗车单页面:累计消费

  private boolean isAddVehicleInfoToSolr = false; //是否添加车辆品牌信息到solr  只有vehicle的基本信息
  private Long contactId;   //联系人id
  private String contactIdStr;
  private String qq;
  private String email;
  private Long appointOrderId;  //相关联的预约单Id

  private Long consumingRecordId;  //记录施工单对应的代金券消费记录id add by LiTao 2015-11-18
  private Double couponAmount;  //代金券金额 add by LiTao 2015-12-04

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  private String appUserNo;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Double getStrike() {
    return strike;
  }

  public void setStrike(Double strike) {
    this.strike = strike;
  }

  public double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public List<WashBeautyOrderItemDTO> getWashBeautyOrderItemDTOList() {
    return washBeautyOrderItemDTOList;
  }

  public void setWashBeautyOrderItemDTOList(List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOList) {
    this.washBeautyOrderItemDTOList = washBeautyOrderItemDTOList;
  }

  public WashBeautyOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(WashBeautyOrderItemDTO[] itemDTO) {
    this.itemDTOs = itemDTO;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }


  public String getOrderContent() {
    return orderContent;
  }

  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public void setCustomerStatus(CustomerStatus customerStatus) {
    this.customerStatus = customerStatus;
  }

  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  public SalesManDTO[] getSalesManDTOs() {
    return salesManDTOs;
  }

  public void setSalesManDTOs(SalesManDTO[] salesManDTOs) {
    this.salesManDTOs = salesManDTOs;
  }

  public String getHuankuanTime() {
    return huankuanTime;
  }

  public void setHuankuanTime(String huankuanTime) {
    this.huankuanTime = huankuanTime;
  }

  public Long getRepaymentTime() {
    return repaymentTime;
  }

  public void setRepaymentTime(Long repaymentTime) {
    this.repaymentTime = repaymentTime;
  }

  public double getOrderDiscount() {
    return orderDiscount;
  }

  public void setOrderDiscount(double orderDiscount) {
    this.orderDiscount = orderDiscount;
    this.discount=orderDiscount;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public ServiceDTO[] getServiceDTOs() {
    return serviceDTOs;
  }

  public void setServiceDTOs(ServiceDTO[] serviceDTOs) {
    this.serviceDTOs = ServiceDTO.toSort(serviceDTOs);
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public String getVehicleContact() {

    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  public Long getVechicleId() {
    return vechicleId;
  }

  public void setVechicleId(Long vechicleId) {
    this.vechicleId = vechicleId;
  }

  public String getVechicle() {
    return vechicle;
  }

  public void setVechicle(String vechicle) {
    this.vechicle = vechicle;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
    this.customerIdStr=StringUtil.valueOf(customerId);
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public Double getEndMileage() {
    return endMileage;
  }

  public void setEndMileage(Double endMileage) {
    this.endMileage = endMileage;
  }

  public String getFuelNumber() {
    return fuelNumber;
  }

  public void setFuelNumber(String fuelNumber) {
    this.fuelNumber = fuelNumber;
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

  public Long getExecutorId() {
    return executorId;
  }

  public void setExecutorId(Long executorId) {
    this.executorId = executorId;
  }

  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Long getReviewerId() {
    return reviewerId;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  public String getReviewer() {
    return reviewer;
  }

  public void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  public String getInvalidator() {
    return invalidator;
  }

  public void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  public String getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(String invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
    this.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD, vestDate));
  }

  public WashBeautyOrderItemDTO[] getWashBeautyOrderItemDTOs() {
    return washBeautyOrderItemDTOs;
  }

  public void setWashBeautyOrderItemDTOs(WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs) {
    this.washBeautyOrderItemDTOs = washBeautyOrderItemDTOs;
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getMemberStatus() {
    return memberStatus;
  }

  public void setMemberStatus(String memberStatus) {
    this.memberStatus = memberStatus;
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

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = debt;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }

  public List<PayMethod> getPayMethods() {
    return payMethods;
  }

  public void setPayMethods(List<PayMethod> payMethods) {
    this.payMethods = payMethods;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public boolean isSendMemberSms() {
    return sendMemberSms;
  }

  public void setSendMemberSms(boolean sendMemberSms) {
    this.sendMemberSms = sendMemberSms;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getAccountMemberId() {
    return accountMemberId;
  }

  public void setAccountMemberId(Long accountMemberId) {
    this.accountMemberId = accountMemberId;
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

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  public String getVehicleEngineNo() {
    return vehicleEngineNo;
  }

  public void setVehicleEngineNo(String vehicleEngineNo) {
    this.vehicleEngineNo = vehicleEngineNo;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  public Long getVehicleBuyDate() {
    return vehicleBuyDate;
  }

  public void setVehicleBuyDate(Long vehicleBuyDate) {
    this.vehicleBuyDate = vehicleBuyDate;
  }

  public String getVehicleChassisNo() {
    return vehicleChassisNo;
  }

  public void setVehicleChassisNo(String vehicleChassisNo) {
    this.vehicleChassisNo = vehicleChassisNo;
  }

  public double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }

  public boolean isAddVehicleInfoToSolr() {
    return isAddVehicleInfoToSolr;
  }

  public void setAddVehicleInfoToSolr(boolean addVehicleInfoToSolr) {
    isAddVehicleInfoToSolr = addVehicleInfoToSolr;
  }

  public Long getConsumingRecordId() {
    return consumingRecordId;
  }

  public void setConsumingRecordId(Long consumingRecordId) {
    this.consumingRecordId = consumingRecordId;
  }

  public Double getCouponAmount() {
    if(couponAmount!=null) {
      return couponAmount;
    }
    return 0D;
  }

  public void setCouponAmount(Double couponAmount) {
    this.couponAmount = couponAmount;
  }

  public Set<Long> getServiceIds() {
    Set<Long> serviceIds = new HashSet<Long>();
    if(!ArrayUtils.isEmpty(this.getWashBeautyOrderItemDTOs())){
      for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : this.getWashBeautyOrderItemDTOs()){
        if(washBeautyOrderItemDTO!=null && washBeautyOrderItemDTO.getServiceId() != null){
          serviceIds.add(washBeautyOrderItemDTO.getServiceId());
        }
      }
    }
    return serviceIds;
  }

  public Set<String> getCategoryNames() {
    Set<String> categoryNames = new HashSet<String>();
    if(!ArrayUtils.isEmpty(this.getWashBeautyOrderItemDTOs())){
      for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : this.getWashBeautyOrderItemDTOs()){
        if(StringUtils.isNotBlank(washBeautyOrderItemDTO.getBusinessCategoryName())){
          categoryNames.add(washBeautyOrderItemDTO.getBusinessCategoryName());
        }
      }
    }
    return categoryNames;
  }





  @Override
  public String toString() {
    return "WashBeautyOrderDTO{" +
        "date=" + date +
        ", no='" + no + '\'' +
        ", deptId=" + deptId +
        ", dept='" + dept + '\'' +
        ", vechicleId=" + vechicleId +
        ", vechicle='" + vechicle + '\'' +
        ", customerId=" + customerId +
        ", customer='" + customer + '\'' +
        ", startMileage=" + startMileage +
        ", endMileage=" + endMileage +
        ", fuelNumber='" + fuelNumber + '\'' +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", executorId=" + executorId +
        ", executor='" + executor + '\'' +
        ", total=" + total +
        ", editorId=" + editorId +
        ", editor='" + editor + '\'' +
        ", editDate=" + editDate +
        ", reviewerId=" + reviewerId +
        ", reviewer='" + reviewer + '\'' +
        ", reviewDate=" + reviewDate +
        ", invalidatorId=" + invalidatorId +
        ", invalidator='" + invalidator + '\'' +
        ", invalidateDate='" + invalidateDate + '\'' +
        ", status=" + status +
        ", memo='" + memo + '\'' +
        ", totalCostPrice=" + totalCostPrice +
        ", vestDate=" + vestDate +
        ", washBeautyOrderItemDTOs=" + (washBeautyOrderItemDTOs == null ? null : Arrays.asList(washBeautyOrderItemDTOs)) +
        ", memberDTO=" + memberDTO +
        ", licenceNo='" + licenceNo + '\'' +
        ", brand='" + brand + '\'' +
        ", model='" + model + '\'' +
        ", contact='" + contact + '\'' +
        ", mobile='" + mobile + '\'' +
        ", landLine='" + landLine + '\'' +
        ", serviceDTOs=" + (serviceDTOs == null ? null : Arrays.asList(serviceDTOs)) +
        ", memberType='" + memberType + '\'' +
        ", memberNo='" + memberNo + '\'' +
        ", memberStatus='" + memberStatus + '\'' +
        ", memberPassword='" + memberPassword + '\'' +
        ", memberAmount=" + memberAmount +
        ", cashAmount=" + cashAmount +
        ", bankAmount=" + bankAmount +
        ", bankCheckAmount=" + bankCheckAmount +
        ", bankCheckNo='" + bankCheckNo + '\'' +
        ", accountMemberNo='" + accountMemberNo + '\'' +
        ", accountMemberPassword='" + accountMemberPassword + '\'' +
        ",accountMemberId:" + accountMemberId + '\'' +
        ", settledAmount=" + settledAmount +
        ", debt=" + debt +
        ", orderDiscount=" + orderDiscount +
        ", huankuanTime='" + huankuanTime + '\'' +
        ", vestDateStr='" + vestDateStr + '\'' +
        ", print='" + print + '\'' +
        ", salesManDTOs=" + (salesManDTOs == null ? null : Arrays.asList(salesManDTOs)) +
        ", serviceWorker='" + serviceWorker + '\'' +
        ", orderContent='" + orderContent + '\'' +
        ", memberId=" + memberId +
        ", appUserNo=" + appUserNo +
        ", sendMemberSms=" + sendMemberSms +
        '}';
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setCustomerStatus(this.getCustomerStatus());
    //支付方式 存入solr
    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (this.getCashAmount() != null && this.getCashAmount() > 0) { //现金
      payMethods.add(PayMethod.CASH);
    }
    if (this.getBankAmount() != null && this.getBankAmount() > 0) { //银行卡
      payMethods.add(PayMethod.BANK_CARD);
    }
    if (this.getBankCheckAmount() != null && this.getBankCheckAmount() > 0) {// 支票
      payMethods.add(PayMethod.CHEQUE);
    }
    if (this.getAccountMemberId() != null && this.getAccountMemberId() > 0) {// 会员
      payMethods.add(PayMethod.MEMBER_BALANCE_PAY);
    }
    if(this.getStatementAccountOrderId() != null){//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }
    orderIndexDTO.setPayMethods(payMethods);

    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setOrderTotalCostPrice(this.getTotalCostPrice());
    orderIndexDTO.setArrears(this.getDebt());
    orderIndexDTO.setOrderDebt(this.getDebt());
    orderIndexDTO.setOrderSettled(this.getSettledAmount());
    orderIndexDTO.setDiscount(this.getOrderDiscount());
    orderIndexDTO.setMemberBalancePay(this.getMemberAmount());
    orderIndexDTO.setAccountMemberId(this.getAccountMemberId());
    orderIndexDTO.setAccountMemberNo(this.getAccountMemberNo());
    orderIndexDTO.setMemberDiscountRatio(this.getMemberDiscountRatio());
    if(null != this.afterMemberDiscountTotal) {
      orderIndexDTO.setAfterMemberDiscountTotal(this.getAfterMemberDiscountTotal());
    }
    else {
      orderIndexDTO.setAfterMemberDiscountTotal(this.getTotal());
    }
    //归属时间
    orderIndexDTO.setVestDate(this.getVestDate());
    if (StringUtils.isNotBlank(this.getBrand())) {
      orderIndexDTO.setVehicleBrand(this.getBrand());
    }
    if (StringUtils.isNotBlank(this.getModel())) {
      orderIndexDTO.setVehicleModel(this.getModel());
    }
    if(StringUtils.isNotBlank(this.getVehicleColor())){
      orderIndexDTO.setVehicleColor(this.getVehicleColor());
    }
    orderIndexDTO.setOrderType(OrderTypes.WASH_BEAUTY);
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setOrderContent(this.getOrderContent());
    try {
      orderIndexDTO.setPaymentTime(StringUtils.isBlank(this.getHuankuanTime())? null : DateUtil.convertDateStringToDateLong("yyyy-MM-dd", this.getHuankuanTime()));
    } catch (ParseException e) {
      LOG.error("/WashBeautyOrderDTO");
      LOG.error("method=toOrderIndexDTO");
      LOG.error("shopId:" + this.getShopId());
      LOG.error(e.getMessage(), e);
    }
    orderIndexDTO.setServiceWorker(StringUtil.isEmpty(this.getServiceWorker()) ? RfTxnConstant.ASSISTANT_NAME : this.getServiceWorker());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerId());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomer());
    orderIndexDTO.setContact(getContact());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setVehicle(this.getVechicle());

    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setShopId(this.getShopId());
    if(getMemberDTO()!=null){
      orderIndexDTO.setMemberNo(this.getMemberDTO().getMemberNo());
      orderIndexDTO.setMemberType(this.getMemberDTO().getType());
    }else{
      orderIndexDTO.setMemberNo(this.getMemberNo());
      orderIndexDTO.setMemberType(this.getMemberType());
    }

    ////计次收费项目
    if (CollectionUtils.isNotEmpty(this.getWashBeautyOrderItemDTOList())) {
      StringBuilder serviceWorker = new StringBuilder();
      StringBuilder orderContent = new StringBuilder();
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : this.getWashBeautyOrderItemDTOList()) {
        itemIndexDTOList.add(washBeautyOrderItemDTO.toItemIndexDTO(this));

        if (StringUtils.isNotBlank(washBeautyOrderItemDTO.getSalesMan())) {
          serviceWorker.append(washBeautyOrderItemDTO.getSalesMan()).append(",");
        }
        orderContent.append(washBeautyOrderItemDTO.getServiceName()).append(";");

        if (washBeautyOrderItemDTO.getPayType().equals(ConsumeType.TIMES)){
          orderIndexDTO.getPayPerProjects().add(washBeautyOrderItemDTO.getServiceName());
        }
        if(washBeautyOrderItemDTO.getPayType().equals(ConsumeType.COUPON)){
          if(CollectionUtils.isEmpty(orderIndexDTO.getPayMethods()) || !orderIndexDTO.getPayMethods().contains(PayMethod.COUPON)){
            orderIndexDTO.getPayMethods().add(PayMethod.COUPON);
          }
          if(StringUtils.isNotBlank(washBeautyOrderItemDTO.getCouponType())){
            orderIndexDTO.getCouponTypes().add(washBeautyOrderItemDTO.getCouponType());
          }
        }
      }
      orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
      //施工内容
      orderIndexDTO.setOrderContent(orderContent.toString());
      //施工人
      orderIndexDTO.setServiceWorker(StringUtils.isBlank(serviceWorker.toString()) ? TxnConstant.ASSISTANT_NAME : serviceWorker.toString());
      orderIndexDTO.setVehicleContact(this.getVehicleContact());
      orderIndexDTO.setVehicleMobile(this.getVehicleMobile());
    }

    return  orderIndexDTO;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    setCustomer(customerDTO.getName());
    setCustomerId(customerDTO.getId());
    setLandLine(customerDTO.getLandLine());
    setAddress(customerDTO.getAddress());
    setCompany(customerDTO.getCompany());
    if(getContactId() == null && !ArrayUtils.isEmpty(customerDTO.getContacts())){
      ContactDTO contactDTO = customerDTO.getContacts()[0];
      if(contactDTO != null){
        setContact(contactDTO.getName());
        setMobile(contactDTO.getMobile());
        setContactId(contactDTO.getId());
        setQq(contactDTO.getQq());
        setEmail(contactDTO.getEmail());
      }
    }
  }

  public void setVehicleDTO(VehicleDTO vehicleDTO) {
    setVechicle(vehicleDTO.getLicenceNo());
    setVechicleId(vehicleDTO.getId());
    setLicenceNo(vehicleDTO.getLicenceNo());
    setBrand(vehicleDTO.getBrand());
    setModel(vehicleDTO.getModel());
    setVehicleEngine(vehicleDTO.getEngine());
    setVehicleEngineNo(vehicleDTO.getEngineNo());
    setVehicleColor(vehicleDTO.getColor());
    setVehicleBuyDate(vehicleDTO.getCarDate());
    setVehicleChassisNo(vehicleDTO.getChassisNumber());
    setVehicleContact(vehicleDTO.getContact());
    setVehicleMobile(vehicleDTO.getMobile());
  }

  public CustomerDTO generateCustomerDTO() {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setName(getCustomer());
    customerDTO.setId(getCustomerId());
    customerDTO.setContact(getContact());
    customerDTO.setMobile(getMobile());
    customerDTO.setLandLine(getLandLine());
    customerDTO.setAddress(getAddress());
    customerDTO.setCompany(getCompany());
    return customerDTO;
  }

  public void clearCustomerInfo() {
    setCustomer(null);
    setCustomerId(null);
    setContact(null);
    setMobile(null);
    setLandLine(null);
    setAddress(null);
    setCompany(null);
  }

  public VehicleDTO generateVehicleDTO() {
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setId(getVechicleId());
    vehicleDTO.setLicenceNo(StringUtils.isBlank(getVechicle())?getLicenceNo():getVechicle());
    vehicleDTO.setBrand(getBrand());
    vehicleDTO.setModel(getModel());
    vehicleDTO.setEngine(getVehicleEngine());
    vehicleDTO.setEngineNo(getVehicleEngineNo());
    vehicleDTO.setColor(getVehicleColor());
    vehicleDTO.setCarDate(getVehicleBuyDate());
    vehicleDTO.setChassisNumber(getVehicleChassisNo());
    vehicleDTO.setContact(getVehicleContact());
    vehicleDTO.setMobile(getVehicleMobile());
    return vehicleDTO;
  }

  public void clearVehicleInfo(){
    setVechicleId(null);
    setVechicle(null);
    setLicenceNo(null);
    setBrand(null);
    setModel(null);
    setVehicleEngine(null);
    setVehicleEngineNo(null);
    setVehicleColor(null);
    setVehicleBuyDate(null);
    setVehicleChassisNo(null);
    setVehicleContact(null);
    setVehicleMobile(null);
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    if(contactId != null){
      contactIdStr = contactId.toString();
    }
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setReceivableDTO(ReceivableDTO receivableDTO) {
    setSettledAmount(receivableDTO.getSettledAmount());
    setOrderDiscount(receivableDTO.getDiscount());
    setDebt(receivableDTO.getDebt());
    setBankAmount(receivableDTO.getBankCard());
    setBankCheckNo(StringUtil.valueOf(receivableDTO.getCheque()));
    setBankCheckAmount(receivableDTO.getCheque());
    setCashAmount(receivableDTO.getCash());
    setMemberAmount(receivableDTO.getMemberBalancePay());
  }

  public void setDefaultMemberService() {
    ServiceDTO[] serviceDTOs = new ServiceDTO[1];
    serviceDTOs[0] = new ServiceDTO();
    serviceDTOs[0].setName("无服务");
    this.setServiceDTOs(serviceDTOs);
  }

  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = totalReceivable;
  }

  public double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(double totalConsume) {
    this.totalConsume = totalConsume;
  }
}
