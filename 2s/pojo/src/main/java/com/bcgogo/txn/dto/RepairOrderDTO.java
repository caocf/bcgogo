package com.bcgogo.txn.dto;

import com.bcgogo.api.AppOrderDTO;
import com.bcgogo.api.AppOrderItemDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-18
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderDTO extends BcgogoOrderDto {
  private static final Logger LOG = LoggerFactory.getLogger(RepairOrderDTO.class);
  private Long vehicleHandoverId;

  public RepairOrderDTO() {
  }

  private String shopName;
  private String shopAddress;
  private String shopLandLine;
  private String shopMobile;
  private Long date;
  private String no;
  private Long deptId;
  private String dept;
  private Long vechicleId;
  private String vechicleIdStr;
  private String vechicle;
  private String vehicleEngineNo;
  private String vehicleColor;
  private String vehicleChassisNo;
  private Long vehicleBuyDate;
  private String vehicleBuyDateStr;
  private String vehicleContact;
  private String vehicleMobile;
  private Long customerId;
  private String customerIdStr;
  private String customerName;
  private String customerMemberNo;      //静态信息使用
  private String customerMemberType;    //静态信息使用
  private MemberStatus customerMemberStatus;    //静态信息使用
  private String company;
  private String address;
  private CustomerStatus customerStatus;
  private String contact;
  private Double startMileage;
  private double endMileage;
  private String fuelNumber;
  private Long startDate;
  private String startDateStr;
  private String startDateString;
  private Long endDate;
  private String endDateStr;
  private Long executorId;
  private String executor;
  private Double total, totalApprox;//近似值
  private String totalStr;
  private double totalHid;
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private String editDateStr;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;
  private String invalidateDate;
  private OrderStatus status;
  private OrderTypes serviceType;
  private String[] serviceTypeArr;
  private RepairOrderItemDTO[] itemDTOs;
  private RepairOrderServiceDTO[] serviceDTOs;
  private RepairRemindEventDTO[] remindEventDTOs;  //提醒事件
  private Long orderStatus;

  private String licenceNo;
  private String brand;
  private Long brandId;
  private String model;
  private Long modelId;
  private String year;
  private Long yearId;
  private Long engineId;
  private String engine;
  private Long receivableId;
  private double settledAmount;    //实收
  private double settledAmountHid;
  private double debt;              //欠款
  private double debtHid;
  private String huankuanTime; //还款时间

  private String statusStr; //维修单状态字符串虚拟字段
  private boolean customerCard;      //是否有洗车卡
  private String lastWashTime;        //上次洗车时间
  private Long remainWashTimes;     //剩余洗车次数
  private Integer todayWashTimes;      //今天洗车次数

  private String serviceWorker;
  private String productSaler;
  private String productSalerIds;


  private String maintainTimeStr;   //预约保养时间
  private String insureTimeStr;      //预约保险时间
  private String examineTimeStr;    //预约验车时间
  private Long maintainMileage;//保养里程
  private String settledAmountStr;

  //  private boolean lack; //判断是否有缺料待修记录
  private ShopUnitDTO[] shopUnits;//单据上使用过的单位顺序

  //add by liuWei 营业统计 2012.5.21
  private String serviceContent; //施工单中的施工内容
  private String serviceContentStr; //施工单中的施工内容简写
  private String salesContent;  //施工单中的销售内容
  private String salesContentStr; //施工单中的销售内容简写
  private double serviceTotal;   //施工单中的施工总费用
  private String serviceTotalStr;   //施工单中的施工总费用
  private double serviceTotalCost; //施工单中的施工 总工时成本
  private double productTotal;
  private String productTotalStr;
  private Double actualHoursTotal;//实际工时总和
  private double salesTotal;  //施工单中的销售总费用
  private double salesTotalCost;//施工单中的销售总成本
  private double orderTotalCost;    //施工单中的总成本
  private double orderProfit; //施工单毛利
  private String orderProfitPercent; //施工单毛利率
  private Long vestDate;      //结算时间
  private String vestDateStr;
  private Long settleDate;  //归属时间 （todo 需要和vestDate 换一下）
  private String settleDateStr;
  private String settlement; //结算方式
  private String accountDateStr; //结算时间，区别于vestDate
  private AppointServiceDTO[] appointServiceDTOs;

  private InventoryLimitDTO inventoryLimitDTO;
  private Long creationDate;

  private List<MemberServiceDTO> memberServiceDTOs;
  private String memberServiceDeadLineStr;
  private String memberJoinDateStr;
  private String memberType;//会员类型
  private String memberNo;  //会员号码
  private Long memberId;
  private Double memberRemainAmount;  //卡内余额
  private String memberStatus;       //会员状态
  private String memberPassword;    //会员密码
  private Double memberAmount;  //储值支付
  private Double cashAmount;        //支付方式:现金
  private Double bankAmount;      //支付方式 银行卡
  private Double bankCheckAmount;       //支付方式 支票
  private String bankCheckNo;          //支付方式 支票号码
  private Double strikeAmount;    //支付方式：冲账
  private String payee;   //结算人
  private CustomerDTO customer;
  private String finishOrderDownType;

  public String getFinishOrderDownType() {
    return finishOrderDownType;
  }

  public void setFinishOrderDownType(String finishOrderDownType) {
    this.finishOrderDownType = finishOrderDownType;
  }

  //结算信息
  private String accountMemberNo; //结算时填的会员号码
  private String accountMemberPassword;  //结算时填的会员密码
  private Long accountMemberId;//结算时会员id

  private String print;
  private String mobile;
  private String landLine;
  private double discount;
  private double orderDiscount;//单据折扣
  private Double totalCostPrice;

  private boolean sendMemberSms;

  private String draftOrderIdStr;

  private String repairOrderTemplateName;

  private Long repairOrderTemplateId;

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;

  private RepairOrderSubmitType repairOrderSubmitType;

  private Boolean containMaterial;//判断施工单是否有材料内容

  private String productNames;

  private boolean isSaveBtnShow = true;     //派单改单按钮
  private boolean isFinishBtnShow = true;   //完工按钮
  private boolean isAccountBtnShow = true;   //结算按钮

  private String description;       //故障说明
  private Long insuranceOrderId;    //保险单号
  private String qualifiedNo;    //合格证号
  private String repairContractNo;  //维修合同编号
  private InsuranceOrderDTO insuranceOrderDTO;
  //记录施工单是否发送过消息给用户
  private String isSmsSend;
  private String insuranceOrderIdStr;
  private Long repairPickingId;
  private String repairPickingIdStr;
  private String repairPickingReceiptNo;

  private double totalReturnDebt;
  private VelocityContext memberSmsVelocityContext;
  private boolean isAddVehicleInfoToSolr = false; //是否添加车辆品牌信息到solr  只有vehicle的基本信息
  private Long contactId; //施工单联系人Id
  private String contactIdStr;
  private String qq;
  private String email;
  private Long appointOrderId;  //相关联的预约单Id
  private String appointOrderIdStr;
  private String appUserNo;


  private String vehicleHandover;//接车人

  private Double otherTotalCostPrice; //施工单其他费用成本总和

  private Long consumingRecordId;  //记录施工单对应的代金券消费记录id add by LiTao 2015-11-13
  private CouponConsumeRecordDTO couponConsumeRecordDTO;//记录施工单对应的代金券消费记录DTO add by LiTao 2015-11-13
  private Double couponAmount;  //代金券金额 add by LiTao 2015-12-04

  public String getVehicleHandover() {
    return vehicleHandover;
  }

  public void setVehicleHandover(String vehicleHandover) {
    this.vehicleHandover = vehicleHandover;
  }

  public Double getOtherTotalCostPrice() {
    return otherTotalCostPrice;
  }

  public void setOtherTotalCostPrice(Double otherTotalCostPrice) {
    this.otherTotalCostPrice = otherTotalCostPrice;
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
    if(appointOrderId != null){
      setAppointOrderIdStr(appointOrderId.toString());
    }else {
      setAppointOrderIdStr("");
    }
  }

  public String getAppointOrderIdStr() {
    return appointOrderIdStr;
  }

  public void setAppointOrderIdStr(String appointOrderIdStr) {
    this.appointOrderIdStr = appointOrderIdStr;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
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

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Boolean getContainMaterial() {
    return containMaterial;
  }

  public void setContainMaterial(Boolean containMaterial) {
    this.containMaterial = containMaterial;
  }

  public CustomerDTO getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerDTO customer) {
    this.customer = customer;
  }

  public List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList;
  private Double otherIncomeTotal;

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Long getRepairOrderTemplateId() {
    return repairOrderTemplateId;
  }

  public void setRepairOrderTemplateId(Long repairOrderTemplateId) {
    this.repairOrderTemplateId = repairOrderTemplateId;
  }

  public String getRepairOrderTemplateName() {
    return repairOrderTemplateName;
  }

  public void setRepairOrderTemplateName(String repairOrderTemplateName) {
    this.repairOrderTemplateName = repairOrderTemplateName;
  }

  public Double getMemberRemainAmount() {
    return memberRemainAmount;
  }

  public void setMemberRemainAmount(Double memberRemainAmount) {
    this.memberRemainAmount = memberRemainAmount;
  }

  public Long getSettleDate() {
    return settleDate;
  }

  public void setSettleDate(Long settleDate) {
    if (StringUtils.isBlank(settleDateStr)) {
      settleDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, settleDate);
    }
    this.settleDate = settleDate;
  }

  public String getSettleDateStr() {
    return settleDateStr;
  }

  public void setSettleDateStr(String settleDateStr) {
    this.settleDateStr = settleDateStr;
  }

  public String getAccountDateStr() {
    return accountDateStr;
  }

  public void setAccountDateStr(String accountDateStr) {
    this.accountDateStr = accountDateStr;
  }

  public String getSettlement() {
    return settlement;
  }

  public void setSettlement(String settlement) {
    this.settlement = settlement;
  }

  public AppointServiceDTO[] getAppointServiceDTOs() {
    return appointServiceDTOs;
  }

  public void setAppointServiceDTOs(AppointServiceDTO[] appointServiceDTOs) {
    this.appointServiceDTOs = appointServiceDTOs;
  }

  private String receiptNo;

  public OrderTypes getServiceType() {
    return serviceType;
  }

  public void setServiceType(OrderTypes serviceType) {
    this.serviceType = serviceType;
  }

  public String getDraftOrderIdStr() {
    return draftOrderIdStr;
  }

  public void setDraftOrderIdStr(String draftOrderIdStr) {
    this.draftOrderIdStr = draftOrderIdStr;
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

  public Double getMemberAmount() {
    return memberAmount;
  }

  public void setMemberAmount(Double memberAmount) {
    this.memberAmount = memberAmount;
  }

  public String getMemberPassword() {
    return memberPassword;
  }

  public void setMemberPassword(String memberPassword) {
    this.memberPassword = memberPassword;
  }


  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
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

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = NumberUtil.toReserve(settledAmount, 2);
    this.settledAmountStr = MoneyUtil.toBigType(String.valueOf(settledAmount));
  }

  public double getSettledAmountHid() {
    return settledAmountHid;
  }

  public void setSettledAmountHid(double settledAmountHid) {
    this.settledAmountHid = settledAmountHid;
  }

  public String getSettledAmountStr() {
    return MoneyUtil.toBigType(String.valueOf(settledAmount));
  }

  public void setSettledAmountStr(String settledAmountStr) {
    this.settledAmountStr = settledAmountStr;
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

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
    this.vechicle = licenceNo;
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


  public String[] getServiceTypeArr() {
    return serviceTypeArr;
  }

  public void setServiceTypeArr(String[] serviceTypeArr) {
    this.serviceTypeArr = serviceTypeArr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public RepairOrderServiceDTO[] getServiceDTOs() {
    return serviceDTOs;
  }

  public void setServiceDTOs(RepairOrderServiceDTO[] serviceDTOs) {
    this.serviceDTOs = serviceDTOs;
  }

  public RepairOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(RepairOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

//  public Long getId() {
//    return id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public Long getShopId() {
//    return shopId;
//  }
//
//  public void setShopId(Long shopId) {
//    this.shopId = shopId;
//  }

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
    if (vechicleId != null) vechicleIdStr = vechicleId.toString();
  }

  public String getVechicle() {
    return vechicle;
  }

  public void setVechicle(String vechicle) {
    this.vechicle = vechicle;
    this.licenceNo = vechicle;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
    if (customerId != null) customerIdStr = customerId.toString();
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public double getEndMileage() {
    return endMileage;
  }

  public void setEndMileage(double endMileage) {
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
    if (StringUtils.isBlank(startDateStr)) {
      this.startDateStr = DateUtil.convertDateLongToString(startDate, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN);
      this.startDateString = DateUtil.convertDateLongToString(startDate, DateUtil.DATE_STRING_FORMAT_DAY);

    }
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    if (StringUtils.isBlank(endDateStr)) {
      this.endDateStr = DateUtil.convertDateLongToString(endDate, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN);
    }
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
    if (total == null) {
      return 0D;
    }
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public double getTotalApprox() {
    totalApprox = NumberUtil.round(total, NumberUtil.MONEY_PRECISION);
    return totalApprox;
  }

  public void setTotalApprox(double totalApprox) {
    this.totalApprox = totalApprox;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
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

  public String getEditDateStr() {
    return DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, editDate);
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
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


  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  public String getProductSaler() {
    return productSaler;
  }

  public void setProductSaler(String productSaler) {
    this.productSaler = productSaler;
  }

  public ShopUnitDTO[] getShopUnits() {
    return shopUnits;
  }

  public void setShopUnits(ShopUnitDTO[] shopUnits) {
    this.shopUnits = shopUnits;
  }

  public InventoryLimitDTO getInventoryLimitDTO() {
    return inventoryLimitDTO;
  }

  public void setInventoryLimitDTO(InventoryLimitDTO inventoryLimitDTO) {
    this.inventoryLimitDTO = inventoryLimitDTO;
  }

  public Long getVestDate() {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return this.vestDateStr;
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

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public double getOrderDiscount() {
    return orderDiscount;
  }

  public Long getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(Long orderStatus) {
    this.orderStatus = orderStatus;
  }

  public void setOrderDiscount(double orderDiscount) {
    this.orderDiscount = NumberUtil.toReserve(orderDiscount, 2);
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = NumberUtil.toReserve(totalCostPrice, 2);
  }

  public double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(double serviceTotal) {
    this.serviceTotal = NumberUtil.toReserve(serviceTotal, 2);
  }

  public String getServiceTotalStr() {
    return serviceTotalStr;
  }

  public void setServiceTotalStr(String serviceTotalStr) {
    this.serviceTotalStr = serviceTotalStr;
  }

  public double getProductTotal() {
    return productTotal;
  }

  public void setProductTotal(double productTotal) {
    this.productTotal = productTotal;
    this.setProductTotalStr(MoneyUtil.toBigType(StringUtil.valueOf(this.productTotal)));
  }

  public String getProductTotalStr() {
    return productTotalStr;
  }

  public void setProductTotalStr(String productTotalStr) {
    this.productTotalStr = productTotalStr;
  }

  public double getServiceTotalCost() {
    return serviceTotalCost;
  }

  public void setServiceTotalCost(double serviceTotalCost) {
    this.serviceTotalCost = NumberUtil.toReserve(serviceTotalCost, 2);
  }

  public double getSalesTotal() {
    return salesTotal;
  }

  public void setSalesTotal(double salesTotal) {
    this.salesTotal = NumberUtil.toReserve(salesTotal, 2);
  }

  public double getSalesTotalCost() {
    return salesTotalCost;
  }

  public void setSalesTotalCost(double salesTotalCost) {
    this.salesTotalCost = NumberUtil.toReserve(salesTotalCost, 2);
  }

  public double getOrderTotalCost() {
    return orderTotalCost;
  }

  public void setOrderTotalCost(double orderTotalCost) {
    this.orderTotalCost = NumberUtil.toReserve(orderTotalCost, 2);
  }

  public double getOrderProfit() {
    return orderProfit;
  }

  public void setOrderProfit(double orderProfit) {
    this.orderProfit = NumberUtil.toReserve(orderProfit, 2);
  }

  public String getOrderProfitPercent() {
    return orderProfitPercent;
  }

  public void setOrderProfitPercent(String orderProfitPercent) {
    this.orderProfitPercent = orderProfitPercent;
  }

  public String getServiceContent() {
    return serviceContent;
  }

  public void setServiceContent(String serviceContent) {
    this.serviceContent = serviceContent;
  }

  public String getServiceContentStr() {
    return serviceContentStr;
  }

  public void setServiceContentStr(String serviceContentStr) {
    this.serviceContentStr = serviceContentStr;
  }

  public String getSalesContent() {
    return salesContent;
  }

  public void setSalesContent(String salesContent) {
    this.salesContent = salesContent;
  }

  public String getSalesContentStr() {
    return salesContentStr;
  }

  public void setSalesContentStr(String salesContentStr) {
    this.salesContentStr = salesContentStr;
  }

  public VelocityContext getMemberSmsVelocityContext() {
    return memberSmsVelocityContext;
  }

  public void setMemberSmsVelocityContext(VelocityContext memberSmsVelocityContext) {
    this.memberSmsVelocityContext = memberSmsVelocityContext;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    if (contactId != null) {
      contactIdStr = contactId.toString();
    }
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  public String getStatusStr() {
    StringBuffer sb = new StringBuffer();
    boolean isLack = false;//缺料标识
    boolean isCome = false;//来料标识
    boolean isFinish = false;//完工标识
    boolean isDebt = false;//欠款标识

    if (remindEventDTOs != null && remindEventDTOs.length > 0) {
      for (int i = 0; i < remindEventDTOs.length; i++) {
        RepairRemindEventDTO remind = remindEventDTOs[i];
        RepairRemindEventTypes type = remind.getEventType();

        if (type.equals(RepairRemindEventTypes.LACK))
          isLack = true;
        else if (type.equals(RepairRemindEventTypes.DEBT))
          isDebt = true;
        else if (type.equals(RepairRemindEventTypes.FINISH))
          isFinish = true;
        else if (type.equals(RepairRemindEventTypes.INCOMING))
          isCome = true;
      }
    }

    if (status != null && status.equals(OrderStatus.REPAIR_DONE)) {
      sb.append("待交付 ");
    } else if (status != null && status.equals(OrderStatus.REPAIR_SETTLED)) {
      if (!isDebt)
        sb.append("已完成 ");
      else
        sb.append("欠款 ");
    } else {
      sb.append("待交付 ");
      if (isLack)
        sb.append("缺料待修 ");
      else if (isCome && !isFinish)
        sb.append("来料待修 ");
    }

    return sb.toString();
  }

  public String getMaintainTimeStr() {
    return maintainTimeStr;
  }

  public void setMaintainTimeStr(String maintainTimeStr) {
    this.maintainTimeStr = maintainTimeStr;
  }

  public String getInsureTimeStr() {
    return insureTimeStr;
  }

  public void setInsureTimeStr(String insureTimeStr) {
    this.insureTimeStr = insureTimeStr;
  }

  public String getExamineTimeStr() {
    return examineTimeStr;
  }

  public void setExamineTimeStr(String examineTimeStr) {
    this.examineTimeStr = examineTimeStr;
  }

  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public String getLastWashTime() {
    return lastWashTime;
  }

  public void setLastWashTime(String lastWashTime) {
    this.lastWashTime = lastWashTime;
  }

  public boolean isCustomerCard() {
    return customerCard;
  }

  public void setCustomerCard(boolean customerCard) {
    this.customerCard = customerCard;
  }

  public Long getRemainWashTimes() {
    return remainWashTimes;
  }

  public void setRemainWashTimes(Long remainWashTimes) {
    this.remainWashTimes = remainWashTimes;
  }

  public Integer getTodayWashTimes() {
    return todayWashTimes;
  }

  public void setTodayWashTimes(Integer todayWashTimes) {
    this.todayWashTimes = todayWashTimes;
  }

  public String getShopAddress() {
    return shopAddress;
  }

  public void setShopAddress(String shopAddress) {
    this.shopAddress = shopAddress;
  }

  public String getShopLandLine() {
    return shopLandLine;
  }

  public void setShopLandLine(String shopLandLine) {
    this.shopLandLine = shopLandLine;
  }

  public String getShopMobile() {
    return shopMobile;
  }

  public void setShopMobile(String shopMobile) {
    this.shopMobile = shopMobile;
  }

  public String getHuankuanTime() {
    return huankuanTime;
  }

  public void setHuankuanTime(String huankuanTime) {
    this.huankuanTime = huankuanTime;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public double getDebtHid() {
    return debtHid;
  }

  public void setDebtHid(double debtHid) {
    this.debtHid = debtHid;
  }

  public double getTotalHid() {
    return totalHid;
  }

  public void setTotalHid(double totalHid) {
    this.totalHid = totalHid;
  }

  public String getTotalStr() {
    return totalStr;
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }

  public RepairRemindEventDTO[] getRemindEventDTOs() {
    return remindEventDTOs;
  }

  public void setRemindEventDTOs(RepairRemindEventDTO[] remindEventDTOs) {
    this.remindEventDTOs = remindEventDTOs;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public Long getReceivableId() {
    return receivableId;
  }

  public void setReceivableId(Long receivableId) {
    this.receivableId = receivableId;
  }

  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = debt;
  }

  public double getSettledAmount() {
    return settledAmount;
  }

//  public Long getId() {
//    return id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public Long getShopId() {
//    return shopId;
//  }
//
//  public void setShopId(Long shopId) {
//    this.shopId = shopId;
//  }


  public String getVechicleIdStr() {
    return vechicleIdStr;
  }

  public void setVechicleIdStr(String vechicleIdStr) {
    this.vechicleIdStr = vechicleIdStr;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public boolean isSendMemberSms() {
    return sendMemberSms;
  }

  public void setSendMemberSms(boolean sendMemberSms) {
    this.sendMemberSms = sendMemberSms;
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

  public String getProductNames() {
    return productNames;
  }

  public void setProductNames(String productNames) {
    this.productNames = productNames;
  }

  public List<RepairOrderOtherIncomeItemDTO> getOtherIncomeItemDTOList() {
    return otherIncomeItemDTOList;
  }

  public void setOtherIncomeItemDTOList(List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList) {
    this.otherIncomeItemDTOList = otherIncomeItemDTOList;
  }

  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
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
    if(vehicleBuyDate != null){
      this.vehicleBuyDateStr = DateUtil.dateLongToStr(vehicleBuyDate, DateUtil.DATE_STRING_FORMAT_DAY);
    }
    this.vehicleBuyDate = vehicleBuyDate;
  }

  public String getVehicleBuyDateStr() {
    return vehicleBuyDateStr;
  }

  public void setVehicleBuyDateStr(String vehicleBuyDateStr) {
    this.vehicleBuyDateStr = vehicleBuyDateStr;
  }

  public String getVehicleChassisNo() {
    return vehicleChassisNo;
  }

  public void setVehicleChassisNo(String vehicleChassisNo) {
    this.vehicleChassisNo = vehicleChassisNo;
  }

  public String getCustomerMemberNo() {
    return customerMemberNo;
  }

  public void setCustomerMemberNo(String customerMemberNo) {
    this.customerMemberNo = customerMemberNo;
  }

  public String getCustomerMemberType() {
    return customerMemberType;
  }

  public void setCustomerMemberType(String customerMemberType) {
    this.customerMemberType = customerMemberType;
  }

  public MemberStatus getCustomerMemberStatus() {
    return customerMemberStatus;
  }

  public void setCustomerMemberStatus(MemberStatus customerMemberStatus) {
    this.customerMemberStatus = customerMemberStatus;
  }

  public boolean getIsSaveBtnShow() {
    return isSaveBtnShow;
  }

  public void setIsSaveBtnShow(boolean saveBtnShow) {
    isSaveBtnShow = saveBtnShow;
  }

  public boolean getIsFinishBtnShow() {
    return isFinishBtnShow;
  }

  public void setIsFinishBtnShow(boolean finishBtnShow) {
    isFinishBtnShow = finishBtnShow;
  }

  public boolean getIsAccountBtnShow() {
    return isAccountBtnShow;
  }

  public void setIsAccountBtnShow(boolean accountBtnShow) {
    isAccountBtnShow = accountBtnShow;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getInsuranceOrderId() {
    return insuranceOrderId;
  }

  public void setInsuranceOrderId(Long insuranceOrderId) {
    this.insuranceOrderId = insuranceOrderId;
    if (insuranceOrderId != null) {
      this.setInsuranceOrderIdStr(insuranceOrderId.toString());
    } else {
      this.setInsuranceOrderIdStr("");
    }
  }

  public String getInsuranceOrderIdStr() {
    return insuranceOrderIdStr;
  }

  public void setInsuranceOrderIdStr(String insuranceOrderIdStr) {
    this.insuranceOrderIdStr = insuranceOrderIdStr;
  }


  public Long getRepairPickingId() {
    return repairPickingId;
  }

  public void setRepairPickingId(Long repairPickingId) {
    this.repairPickingId = repairPickingId;
    setRepairPickingIdStr(repairPickingId == null ? "" : repairPickingId.toString());
  }

  public String getRepairPickingIdStr() {
    return repairPickingIdStr;
  }

  public void setRepairPickingIdStr(String repairPickingIdStr) {
    this.repairPickingIdStr = repairPickingIdStr;
  }

  public String getRepairPickingReceiptNo() {
    return repairPickingReceiptNo;
  }

  public void setRepairPickingReceiptNo(String repairPickingReceiptNo) {
    this.repairPickingReceiptNo = repairPickingReceiptNo;
  }

  public String getQualifiedNo() {
    return qualifiedNo;
  }

  public void setQualifiedNo(String qualifiedNo) {
    this.qualifiedNo = qualifiedNo;
  }

  public String getRepairContractNo() {
    return repairContractNo;
  }

  public void setRepairContractNo(String repairContractNo) {
    this.repairContractNo = repairContractNo;
  }

  public double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }

  public String getProductSalerIds() {
    return productSalerIds;
  }

  public void setProductSalerIds(String productSalerIds) {
    this.productSalerIds = productSalerIds;
  }

  public Double getActualHoursTotal() {
    return actualHoursTotal;
  }

  public void setActualHoursTotal(Double actualHoursTotal) {
    this.actualHoursTotal = actualHoursTotal;
  }

  public boolean isAddVehicleInfoToSolr() {
    return isAddVehicleInfoToSolr;
  }

  public void setAddVehicleInfoToSolr(boolean addVehicleInfoToSolr) {
    isAddVehicleInfoToSolr = addVehicleInfoToSolr;
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

  /**
   * 代金券交易记录id的getter/setter
   * @return
   * add by LiTao 2015-11-13
   */
  public Long getConsumingRecordId() {
    return consumingRecordId;
  }

  public void setConsumingRecordId(Long consumingRecordId) {
    this.consumingRecordId = consumingRecordId;
  }
  public void setConsumingRecordId(String consumingRecordId) {
    this.consumingRecordId=new Long(consumingRecordId);
  }

  /**
   * 代金券交易记录DTO的getter/setter
   * @return
   */
  public CouponConsumeRecordDTO getCouponConsumeRecordDTO() {
    return couponConsumeRecordDTO;
  }

  public void setCouponConsumeRecordDTO(CouponConsumeRecordDTO couponConsumeRecordDTO) {
    this.couponConsumeRecordDTO = couponConsumeRecordDTO;
    this.couponAmount = couponConsumeRecordDTO.getCoupon();
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

  @Override
  public String toString() {
    return JsonUtil.objectToJson(this);
//    final StringBuilder sb = new StringBuilder();
//    sb.append("RepairOrderDTO");
//    sb.append("{id=").append(this.getId() == null ? "" : this.getId());
//    sb.append(", shopId=").append(this.getShopId() == null ? "" : this.getShopId());
//    sb.append(", memberType=").append(this.getMemberType() == null ? "" : this.getMemberType());
//    sb.append(", memberNo=").append(this.getMemberNo() == null ? "" : this.getMemberNo());
//    sb.append(", accountMemberNo=").append(this.getAccountMemberNo() == null ? "" : this.getAccountMemberNo());
//    sb.append(", accountMemberPassword=").append(this.getAccountMemberPassword() == null ? "" : this.getAccountMemberPassword());
//    sb.append(", accountMemberId=").append(this.getAccountMemberId() == null ? "" : this.getAccountMemberId());
//    sb.append(", memberStatus=").append(this.getMemberStatus() == null ? "" : this.getMemberStatus());
//    sb.append(", memberPassword=").append(this.getMemberPassword() == null ? "" : this.getMemberPassword());
//    sb.append(", memberAmount=").append(this.getMemberAmount() == null ? "" : this.getMemberAmount());
//    sb.append(", cashAmount=").append(this.getCashAmount() == null ? "" : this.getCashAmount());
//    sb.append(", bankAmount=").append(this.getBankAmount() == null ? "" : this.getBankAmount());
//    sb.append(", bankCheckAmount=").append(this.getBankCheckAmount() == null ? "" : this.getBankCheckAmount());
//    sb.append(", bankCheckNo=").append(this.getBankCheckNo() == null ? "" : this.getBankCheckNo());
//    sb.append(", shopName='").append(StringUtil.truncValue(shopName)).append('\'');
//    sb.append(", shopAddress='").append(StringUtil.truncValue(shopAddress)).append('\'');
//    sb.append(", shopLandLine='").append(StringUtil.truncValue(shopLandLine)).append('\'');
//    sb.append(", date=").append(date == null ? "" : date);
//    sb.append(", no='").append(StringUtil.truncValue(no)).append('\'');
//    sb.append(", deptId=").append(deptId == null ? "" : deptId);
//    sb.append(", dept='").append(StringUtil.truncValue(dept)).append('\'');
//    sb.append(", vechicleId=").append(vechicleId == null ? "" : vechicleId);
//    sb.append(", vechicle='").append(StringUtil.truncValue(vechicle)).append('\'');
//    sb.append(", customerId=").append(customerId == null ? "" : customerId);
//    sb.append(", customer='").append(StringUtil.truncValue(customerName)).append('\'');
//    sb.append(", contact='").append(StringUtil.truncValue(contact)).append('\'');
//    sb.append(", startMileage=").append(startMileage);
//    sb.append(", endMileage=").append(endMileage);
//    sb.append(", fuelNumber='").append(StringUtil.truncValue(fuelNumber)).append('\'');
//    sb.append(", startDate=").append(startDate == null ? "" : startDate);
//    sb.append(", startDateStr='").append(StringUtil.truncValue(startDateStr)).append('\'');
//    sb.append(", endDate=").append(endDate == null ? "" : endDate);
//    sb.append(", endDateStr='").append(StringUtil.truncValue(endDateStr)).append('\'');
//    sb.append(", executorId=").append(executorId == null ? "" : executorId);
//    sb.append(", executor='").append(StringUtil.truncValue(executor)).append('\'');
//    sb.append(", total=").append(total);
//    sb.append(", totalHid=").append(totalHid);
//    sb.append(", memo='").append(StringUtil.truncValue(memo)).append('\'');
//    sb.append(", editorId=").append(editorId == null ? "" : editorId);
//    sb.append(", editor='").append(StringUtil.truncValue(editor)).append('\'');
//    sb.append(", editDate=").append(editDate == null ? "" : editDate);
//    sb.append(", reviewerId=").append(reviewerId == null ? "" : reviewerId);
//    sb.append(", reviewer='").append(StringUtil.truncValue(reviewer)).append('\'');
//    sb.append(", reviewDate=").append(reviewDate == null ? "" : reviewDate);
//    sb.append(", invalidatorId=").append(invalidatorId == null ? "" : invalidatorId);
//    sb.append(", invalidator='").append(StringUtil.truncValue(invalidator)).append('\'');
//    sb.append(", invalidateDate='").append(StringUtil.truncValue(invalidateDate)).append('\'');
//    sb.append(", status=").append(status == null ? "" : status);
//    sb.append(", serviceType='").append(serviceType == null ? "" : serviceType.getName()).append('\'');
//    sb.append(", serviceTypeArr=").append(serviceTypeArr == null ? "null" : Arrays.asList(serviceTypeArr).toString());
//    if (itemDTOs != null) {
//      for (RepairOrderItemDTO itemDTO : itemDTOs) {
//        sb.append(itemDTO.toString());
//      }
//    }
//    sb.append(", serviceDTOs=").append(serviceDTOs == null ? "null" : Arrays.asList(serviceDTOs).toString());
//    sb.append(", remindEventDTOs=").append(remindEventDTOs == null ? "null" : Arrays.asList(remindEventDTOs).toString());
//    sb.append(", licenceNo='").append(StringUtil.truncValue(licenceNo)).append('\'');
//    sb.append(", brand='").append(StringUtil.truncValue(brand)).append('\'');
//    sb.append(", brandId=").append(brandId == null ? "" : brandId);
//    sb.append(", model='").append(StringUtil.truncValue(model)).append('\'');
//    sb.append(", modelId=").append(modelId == null ? "" : modelId);
//    sb.append(", year='").append(StringUtil.truncValue(year)).append('\'');
//    sb.append(", yearId=").append(yearId == null ? "" : yearId);
//    sb.append(", engineId=").append(engineId == null ? "" : engineId);
//    sb.append(", engine='").append(StringUtil.truncValue(engine)).append('\'');
//    sb.append(", receivableId=").append(receivableId == null ? "" : receivableId);
//    sb.append(", settledAmount=").append(settledAmount);
//    sb.append(", settledAmountHid=").append(settledAmountHid);
//    sb.append(", debt=").append(debt);
//    sb.append(", debtHid=").append(debtHid);
//    sb.append(", huankuanTime='").append(StringUtil.truncValue(huankuanTime)).append('\'');
//    sb.append(", statusStr='").append(StringUtil.truncValue(statusStr)).append('\'');
//    sb.append(", customerCard=").append(customerCard);
//    sb.append(", lastWashTime='").append(StringUtil.truncValue(lastWashTime)).append('\'');
//    sb.append(", remainWashTimes=").append(remainWashTimes == null ? "" : remainWashTimes);
//    sb.append(", todayWashTimes=").append(todayWashTimes == null ? "" : todayWashTimes);
//    sb.append(", serviceWorker='").append(StringUtil.truncValue(serviceWorker)).append('\'');
//    sb.append(", productSaler='").append(StringUtil.truncValue(productSaler)).append('\'');
//    sb.append(", mobile='").append(StringUtil.truncValue(mobile)).append('\'');
//    sb.append(", landLine='").append(StringUtil.truncValue(landLine)).append('\'');
//    sb.append('}');
//    return sb.toString();
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setMemberNo(this.getCustomerMemberNo());
    orderIndexDTO.setMemberType(this.getCustomerMemberType());
    orderIndexDTO.setMemberStatus(this.getCustomerMemberStatus() == null ? "" : getCustomerMemberStatus().toString());
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.REPAIR);
    orderIndexDTO.setOrderStatus(this.getStatus());
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerId());
    orderIndexDTO.setCustomerStatus(this.getCustomerStatus());
    orderIndexDTO.setVestDate(this.getSettleDate());  //zjt:施工单使用的是 settleDate 做为归属时间
    orderIndexDTO.setCreationDate(this.getCreationDate() == null ? System.currentTimeMillis() : this.getCreationDate());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    if (!StringUtils.isBlank(this.getCustomerName())) {
      orderIndexDTO.setCustomerOrSupplierName(this.getCustomerName());
    } else if (!StringUtils.isBlank(this.getLicenceNo())) {
      orderIndexDTO.setCustomerOrSupplierName(this.getLicenceNo());
    } else if (!StringUtils.isBlank(this.getVechicle())) {
      orderIndexDTO.setCustomerOrSupplierName(this.getVechicle());
    }
    if (!StringUtils.isBlank(this.getVechicle())) {
      orderIndexDTO.setVehicle(this.getVechicle());
    } else if (!StringUtils.isBlank(this.getLicenceNo())) {
      orderIndexDTO.setVehicle(this.getLicenceNo());
    }
    if (StringUtils.isNotBlank(this.getBrand())) {
      orderIndexDTO.setVehicleBrand(this.getBrand());
    }
    if (StringUtils.isNotBlank(this.getModel())) {
      orderIndexDTO.setVehicleModel(this.getModel());
    }
    if(StringUtils.isNotBlank(this.getVehicleColor())){
      orderIndexDTO.setVehicleColor(this.getVehicleColor());
    }
    if (!StringUtils.isBlank(this.getMobile())) {
      orderIndexDTO.setContactNum(this.getMobile());
    } else if (!StringUtils.isBlank(this.getLandLine())) {
      orderIndexDTO.setContactNum(this.getLandLine());
    }
    if (StringUtils.isNotBlank(getContact())) {
      orderIndexDTO.setContact(getContact());
    }
    if (StringUtils.isNotBlank(getVehicleContact())) {
      orderIndexDTO.setVehicleContact(getVehicleContact());
    }
    if (StringUtils.isNotBlank(getVehicleMobile())) {
      orderIndexDTO.setVehicleMobile(getVehicleMobile());
    }
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
    if (null != this.getAfterMemberDiscountTotal()) {
      orderIndexDTO.setAfterMemberDiscountTotal(this.getAfterMemberDiscountTotal());
    } else {
      orderIndexDTO.setAfterMemberDiscountTotal(this.getTotal());
    }
    //支付方式 存入solr
    List<PayMethod> payMethods = new ArrayList<PayMethod>();
    if (NumberUtil.doubleVal(this.getCashAmount()) > 0) { //现金
      payMethods.add(PayMethod.CASH);
    }
    if (NumberUtil.doubleVal(this.getBankAmount()) > 0) { //银行卡
      payMethods.add(PayMethod.BANK_CARD);
    }
    if (NumberUtil.doubleVal(this.getBankCheckAmount()) > 0) {// 支票
      payMethods.add(PayMethod.CHEQUE);
    }
    if (this.getAccountMemberId() != null && this.getAccountMemberId() != 0) {   //会员支付
      payMethods.add(PayMethod.MEMBER_BALANCE_PAY);
    }

    if (this.getStatementAccountOrderId() != null) {//对账支付
      payMethods.add(PayMethod.STATEMENT_ACCOUNT);
    }
    orderIndexDTO.setPayMethods(payMethods);

    String endDateStr = this.getHuankuanTime() != null ? this.getHuankuanTime() : "";
    if (!StringUtils.isBlank(endDateStr)) {
      try {
        Long endDateLong = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endDateStr);
        orderIndexDTO.setPaymentTime(endDateLong);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    StringBuffer str = new StringBuffer();
    if (this.getServiceDTOs() != null && this.getServiceDTOs().length > 0) {
      str.append("施工内容:");
      for (RepairOrderServiceDTO repairOrderServiceDTO : this.getServiceDTOs()) {
        if (StringUtils.isBlank(repairOrderServiceDTO.getService()) && (repairOrderServiceDTO.getTotal() < 0)) {
          continue;
        }
        //判断计次收费项目
        if (repairOrderServiceDTO.getConsumeType() != null && repairOrderServiceDTO.getConsumeType().equals(ConsumeType.TIMES)) {
          orderIndexDTO.getPayPerProjects().add(repairOrderServiceDTO.getService());
        }
        itemIndexDTOList.add(repairOrderServiceDTO.toItemIndexDTO(this));

        if (!StringUtils.isBlank(repairOrderServiceDTO.getService())) {
          str.append("(");
          str.append(repairOrderServiceDTO.getService());
        }
        if (repairOrderServiceDTO.getTotal() > 0.0) {
          str.append(",").append(repairOrderServiceDTO.getTotal());
          str.append("元);");
        } else {
          str.append(");");
        }
      }
    }
    if (this.getItemDTOs() != null && this.getItemDTOs().length > 0) {
      str.append("销售内容:");
      for (RepairOrderItemDTO repairOrderItemDTO : this.getItemDTOs()) {
        if (repairOrderItemDTO == null) continue;
        if (StringUtils.isBlank(repairOrderItemDTO.getProductName()) || repairOrderItemDTO.getProductId() == null) {
          continue;
        }
        //添加每个单据的产品信息
        itemIndexDTOList.add(repairOrderItemDTO.toItemIndexDTO(this));
        inOutRecordDTOList.addAll(repairOrderItemDTO.toInOutRecordDTO(this));
        //保存销售内容
        str.append("(");
        if (!StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          str.append("品名:").append(repairOrderItemDTO.getProductName());
        }
        if (!StringUtils.isBlank(repairOrderItemDTO.getBrand())) {
          str.append(",品牌:").append(repairOrderItemDTO.getBrand());
        }
        if (repairOrderItemDTO.getAmount() != null) {
          str.append(",数量:").append(repairOrderItemDTO.getAmount());
        }
        str.append(",总价").append(String.valueOf(repairOrderItemDTO.getTotal()));
        str.append("元);");
      }
    }


    if (CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      str.append("其他费用:");
      for (RepairOrderOtherIncomeItemDTO otherIncomeItemDTO : this.getOtherIncomeItemDTOList()) {
        if (otherIncomeItemDTO == null) continue;
        if (StringUtils.isBlank(otherIncomeItemDTO.getName())) {
          continue;
        }
        str.append("(").append(otherIncomeItemDTO.getName()).append(":").append(NumberUtil.doubleVal(otherIncomeItemDTO.getPrice()));
        str.append("元);");
      }
    }



    if (CollectionUtils.isNotEmpty(this.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO itemDTO : this.getOtherIncomeItemDTOList()) {
        if (itemDTO == null) continue;
        if (StringUtils.isBlank(itemDTO.getName())) {
          continue;
        }
        //添加每个单据的产品信息
        itemIndexDTOList.add(itemDTO.toItemIndexDTO(this));
      }
    }

    orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
    orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);

    if (StringUtils.isNotBlank(str.toString()) && str.length() > 1) {
      String tmp = str.substring(0, str.length() - 1);
      if (tmp.length() > 450) {
        tmp = tmp.substring(0, 450);
        tmp = tmp + "等";
      }
      orderIndexDTO.setOrderContent(tmp);
    } else {
      LOG.warn("施工单repair order[orderId:{}]内容为空", this.getId());
    }
    //施工人
    orderIndexDTO.setServiceWorker(StringUtils.isBlank(this.getServiceWorker()) ? TxnConstant.ASSISTANT_NAME : this.getServiceWorker());
    //销售人
    orderIndexDTO.setSalesMans(StringUtils.isBlank(this.getProductSaler()) ? TxnConstant.ASSISTANT_NAME : this.getProductSaler());
    return orderIndexDTO;
  }

  public boolean isValidateSuccess() {
    if (StringUtils.isBlank(licenceNo)) return false;
    return true;
  }

  @Override
  public RepairOrderDTO clone() throws CloneNotSupportedException {
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setId(getId());
    repairOrderDTO.shopName = shopName;
    repairOrderDTO.shopAddress = shopAddress;
    repairOrderDTO.shopLandLine = shopLandLine;
    repairOrderDTO.date = date;
    repairOrderDTO.no = no;
    repairOrderDTO.deptId = deptId;
    repairOrderDTO.dept = dept;
    repairOrderDTO.vechicleId = vechicleId;
    repairOrderDTO.vechicleIdStr = vechicleIdStr;
    repairOrderDTO.vechicle = vechicle;
    repairOrderDTO.customerId = customerId;
    repairOrderDTO.customerIdStr = customerIdStr;
    repairOrderDTO.customerName = customerName;
    repairOrderDTO.customerStatus = customerStatus;
    repairOrderDTO.contact = contact;
    repairOrderDTO.startMileage = startMileage;
    repairOrderDTO.endMileage = endMileage;
    repairOrderDTO.fuelNumber = fuelNumber;
    repairOrderDTO.startDate = startDate;
    repairOrderDTO.startDateStr = startDateStr;
    repairOrderDTO.endDate = endDate;
    repairOrderDTO.endDateStr = endDateStr;
    repairOrderDTO.executorId = executorId;
    repairOrderDTO.executor = executor;
    repairOrderDTO.total = total;
    repairOrderDTO.totalApprox = totalApprox;
    repairOrderDTO.totalHid = totalHid;
    repairOrderDTO.memo = memo;
    repairOrderDTO.editorId = editorId;
    repairOrderDTO.editor = editor;
    repairOrderDTO.editDate = editDate;
    repairOrderDTO.editDateStr = editDateStr;
    repairOrderDTO.reviewerId = reviewerId;
    repairOrderDTO.reviewer = reviewer;
    repairOrderDTO.reviewDate = reviewDate;
    repairOrderDTO.invalidatorId = invalidatorId;
    repairOrderDTO.invalidator = invalidator;
    repairOrderDTO.invalidateDate = invalidateDate;
    repairOrderDTO.status = status;
    repairOrderDTO.serviceType = serviceType;
    repairOrderDTO.serviceTypeArr = serviceTypeArr;
    repairOrderDTO.itemDTOs = itemDTOs;
    repairOrderDTO.serviceDTOs = serviceDTOs;
    repairOrderDTO.remindEventDTOs = remindEventDTOs;
    repairOrderDTO.orderStatus = orderStatus;
    repairOrderDTO.licenceNo = licenceNo;
    repairOrderDTO.brand = brand;
    repairOrderDTO.brandId = brandId;
    repairOrderDTO.model = model;
    repairOrderDTO.modelId = modelId;
    repairOrderDTO.year = year;
    repairOrderDTO.yearId = yearId;
    repairOrderDTO.engineId = engineId;
    repairOrderDTO.engine = engine;
    repairOrderDTO.receivableId = receivableId;
    repairOrderDTO.vehicleHandover = vehicleHandover;
    repairOrderDTO.vehicleHandoverId = vehicleHandoverId;
    repairOrderDTO.settledAmount = settledAmount;
    repairOrderDTO.settledAmountHid = settledAmountHid;
    repairOrderDTO.debt = debt;
    repairOrderDTO.debtHid = debtHid;
    repairOrderDTO.huankuanTime = huankuanTime;
    repairOrderDTO.statusStr = statusStr;
    repairOrderDTO.customerCard = customerCard;
    repairOrderDTO.lastWashTime = lastWashTime;
    repairOrderDTO.remainWashTimes = remainWashTimes;
    repairOrderDTO.todayWashTimes = todayWashTimes;
    repairOrderDTO.serviceWorker = serviceWorker;
    repairOrderDTO.productSaler = productSaler;
    repairOrderDTO.maintainTimeStr = maintainTimeStr;
    repairOrderDTO.insureTimeStr = insureTimeStr;
    repairOrderDTO.examineTimeStr = examineTimeStr;
    repairOrderDTO.settledAmountStr = settledAmountStr;
    repairOrderDTO.shopUnits = shopUnits;
    repairOrderDTO.serviceContent = serviceContent;
    repairOrderDTO.serviceContentStr = serviceContentStr;
    repairOrderDTO.salesContent = salesContent;
    repairOrderDTO.salesContentStr = salesContentStr;
    repairOrderDTO.serviceTotal = serviceTotal;
    repairOrderDTO.serviceTotalCost = serviceTotalCost;
    repairOrderDTO.salesTotal = salesTotal;
    repairOrderDTO.salesTotalCost = salesTotalCost;
    repairOrderDTO.orderTotalCost = orderTotalCost;
    repairOrderDTO.orderProfit = orderProfit;
    repairOrderDTO.orderProfitPercent = orderProfitPercent;
    repairOrderDTO.vestDate = vestDate;
    repairOrderDTO.vestDateStr = vestDateStr;
    repairOrderDTO.settleDate = settleDate;
    repairOrderDTO.settleDateStr = settleDateStr;
    repairOrderDTO.inventoryLimitDTO = inventoryLimitDTO;
    repairOrderDTO.creationDate = creationDate;
    repairOrderDTO.memberType = memberType;
    repairOrderDTO.memberNo = memberNo;
    repairOrderDTO.memberRemainAmount = memberRemainAmount;
    repairOrderDTO.memberStatus = memberStatus;
    repairOrderDTO.memberPassword = memberPassword;
    repairOrderDTO.memberAmount = memberAmount;
    repairOrderDTO.cashAmount = cashAmount;
    repairOrderDTO.bankAmount = bankAmount;
    repairOrderDTO.bankCheckAmount = bankCheckAmount;
    repairOrderDTO.bankCheckNo = bankCheckNo;
    repairOrderDTO.accountMemberNo = accountMemberNo;
    repairOrderDTO.accountMemberPassword = accountMemberPassword;
    repairOrderDTO.accountMemberId = accountMemberId;
    repairOrderDTO.print = print;
    repairOrderDTO.mobile = mobile;
    repairOrderDTO.landLine = landLine;
    repairOrderDTO.discount = discount;
    repairOrderDTO.orderDiscount = orderDiscount;
    repairOrderDTO.totalCostPrice = totalCostPrice;
    repairOrderDTO.sendMemberSms = sendMemberSms;
    repairOrderDTO.draftOrderIdStr = draftOrderIdStr;
    repairOrderDTO.setShopVersionId(getShopVersionId());
    repairOrderDTO.repairOrderTemplateName = repairOrderTemplateName;
    repairOrderDTO.repairOrderTemplateId = repairOrderTemplateId;
    repairOrderDTO.receiptNo = receiptNo;
    repairOrderDTO.otherIncomeItemDTOList = otherIncomeItemDTOList;
    repairOrderDTO.otherIncomeTotal = otherIncomeTotal;
    repairOrderDTO.setStorehouseName(this.getStorehouseName());
    repairOrderDTO.setStorehouseId(this.getStorehouseId());
    repairOrderDTO.description = description;
    repairOrderDTO.setAppointServiceDTOs(this.getAppointServiceDTOs());
    repairOrderDTO.address = address;
    repairOrderDTO.contactId = contactId;
    repairOrderDTO.qq = qq;
    repairOrderDTO.email = email;
    repairOrderDTO.setMaintainMileage(this.getMaintainMileage());
    repairOrderDTO.otherTotalCostPrice = this.getOtherTotalCostPrice();
    repairOrderDTO.isSmsSend= isSmsSend;
    return repairOrderDTO;
  }

  public String getServiceStr() {
    if (ArrayUtil.isEmpty(getServiceDTOs())) {
      return null;
    }
    StringBuffer stringBuffer = new StringBuffer();
    for (RepairOrderServiceDTO repairOrderServiceDTO : getServiceDTOs()) {
      if (StringUtils.isNotBlank(repairOrderServiceDTO.getService())) {
        stringBuffer.append(repairOrderServiceDTO.getService() + ";");
      }
    }
    if (stringBuffer.length() > 0) {
      return stringBuffer.substring(0, stringBuffer.length() - 1);
    } else {
      return null;
    }
  }

  public String getProductStr() {
    if (ArrayUtil.isEmpty(getItemDTOs())) {
      return null;
    }
    StringBuffer stringBuffer = new StringBuffer();
    for (RepairOrderItemDTO repairOrderItemDTO : getItemDTOs()) {
      if (StringUtils.isNotBlank(repairOrderItemDTO.getProductName())) {
        stringBuffer.append(repairOrderItemDTO.getProductName() + ";");
      }
    }
    if (stringBuffer.length() > 0) {
      return stringBuffer.substring(0, stringBuffer.length() - 1);
    } else {
      return null;
    }
  }

  public void setVehicleDTO(VehicleDTO vehicleDTO) {
    setVechicleId(vehicleDTO.getId());
    setVechicle(vehicleDTO.getLicenceNo());
    setLicenceNo(vehicleDTO.getLicenceNo());
    if(getStartMileage() == null){
      setStartMileage(vehicleDTO.getObdMileage());
    }
    setEngine(vehicleDTO.getEngine());
    setYear(vehicleDTO.getYear());
    setVehicleEngineNo(vehicleDTO.getEngineNo());
    setModel(vehicleDTO.getModel());
    setModelId(vehicleDTO.getModelId());
    setBrand(vehicleDTO.getBrand());
    setBrandId(vehicleDTO.getBrandId());
    setVehicleColor(vehicleDTO.getColor());
    setVehicleBuyDate(vehicleDTO.getCarDate());
    setVehicleChassisNo(vehicleDTO.getChassisNumber());
    setYear(vehicleDTO.getYear());
    setYearId(vehicleDTO.getYearId());
    setEngineId(vehicleDTO.getEngineId());
    setVehicleContact(vehicleDTO.getContact());
    setVehicleMobile(vehicleDTO.getMobile());
    setVehicleEngineNo(vehicleDTO.getEngineNo());
  }

  public void setCustomerQqEmailDTO(CustomerDTO customerDTO){
    setQq(customerDTO.getQq());
    setEmail(customerDTO.getEmail());
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO != null) {
      setCustomerId(customerDTO.getId());
      setCustomerName(customerDTO.getName());
      setCompany(customerDTO.getCompany());
      setLandLine(customerDTO.getLandLine());
      setAddress(customerDTO.getAddress());
      if (getContactId() == null && !ArrayUtils.isEmpty(customerDTO.getContacts())) {
        ContactDTO contactDTO = customerDTO.getContacts()[0];
        if (contactDTO != null) {
          setContact(contactDTO.getName());
          setMobile(contactDTO.getMobile());
          setContactId(contactDTO.getId());
          setQq(contactDTO.getQq());
          setEmail(contactDTO.getEmail());
        }
      }
    }
  }

  public CustomerDTO generateCustomerDTO() {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setId(getCustomerId());
    customerDTO.setName(getCustomerName());
    customerDTO.setCompany(getCompany());
    customerDTO.setContact(getContact());
    customerDTO.setMobile(getMobile());
    customerDTO.setLandLine(getLandLine());
    customerDTO.setAddress(getAddress());
    return customerDTO;
  }

  public VehicleDTO generateVehicleDTO() {
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setId(getVechicleId());
    vehicleDTO.setLicenceNo(getVechicle() == null ? getLicenceNo() : getVechicle());
    vehicleDTO.setEngine(getEngine());
    vehicleDTO.setEngineNo(getVehicleEngineNo());
    vehicleDTO.setModel(getModel());
    vehicleDTO.setModelId(getModelId());
    vehicleDTO.setBrand(getBrand());
    vehicleDTO.setBrandId(getBrandId());
    vehicleDTO.setColor(getVehicleColor());
    vehicleDTO.setCarDate(getVehicleBuyDate());
    vehicleDTO.setChassisNumber(getVehicleChassisNo());
    vehicleDTO.setContact(getVehicleContact());
    vehicleDTO.setMobile(getVehicleMobile());
    return vehicleDTO;
  }

  public void clearCustomerInfo() {
    setCustomerId(null);
    setCustomerName(null);
    setCompany(null);
    setContact(null);
    setMobile(null);
    setLandLine(null);
    setAddress(null);
  }

  public void clearVehicleInfo() {
    setVechicleId(null);
    setVechicle(null);
    setLicenceNo(null);
    setEngine(null);
    setVehicleEngineNo(null);
    setModel(null);
    setModelId(null);
    setBrand(null);
    setBrandId(null);
    setVehicleColor(null);
    setVehicleBuyDate(null);
    setVehicleChassisNo(null);
    setVehicleContact(null);
    setVehicleMobile(null);
  }

  public void setShopDTO(ShopDTO shopDTO) {
    if (shopDTO != null) {
      this.setShopId(shopDTO.getId());
      this.setShopName(shopDTO.getName());
      this.setShopAddress(shopDTO.getAddress());
      this.setShopLandLine(shopDTO.getLandline());
      this.setShopVersionId(shopDTO.getShopVersionId());
    }
  }

  public String getMemberJoinDateStr() {
    return memberJoinDateStr;
  }

  public void setMemberJoinDateStr(String memberJoinDateStr) {
    this.memberJoinDateStr = memberJoinDateStr;
  }

  public String getMemberServiceDeadLineStr() {
    return memberServiceDeadLineStr;
  }

  public void setMemberServiceDeadLineStr(String memberServiceDeadLineStr) {
    this.memberServiceDeadLineStr = memberServiceDeadLineStr;
  }

  public List<MemberServiceDTO> getMemberServiceDTOs() {
    return memberServiceDTOs;
  }

  public void setMemberServiceDTOs(List<MemberServiceDTO> memberServiceDTOs) {
    this.memberServiceDTOs = memberServiceDTOs;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    if (memberDTO != null) {
      this.setMemberNo(memberDTO.getMemberNo());
      this.setMemberType(memberDTO.getType());
      this.setMemberRemainAmount(memberDTO.getBalance());
      this.setMemberJoinDateStr(memberDTO.getJoinDateStr());
      this.setMemberServiceDeadLineStr(memberDTO.getServiceDeadLineStr());
      this.setMemberServiceDTOs(memberDTO.getMemberServiceDTOs());
    }else {
      this.setMemberNo(null);
      this.setMemberType(null);
      this.setMemberRemainAmount(null);
      this.setMemberJoinDateStr(null);
      this.setMemberServiceDeadLineStr(null);
      this.setMemberServiceDTOs(null);
    }
  }

  public void setCustomerVehicleDTO(CustomerVehicleDTO customerVehicleDTO) {
    if (customerVehicleDTO == null) return;
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(new Date());
    calendar.add(calendar.DATE, -1);
    Date nowTime = calendar.getTime();

    Long maintainTime = customerVehicleDTO.getMaintainTime();
    Long insureTimeStr = customerVehicleDTO.getInsureTime();
    Long examineTime = customerVehicleDTO.getExamineTime();
    if (null != maintainTime && nowTime.getTime() < maintainTime)
      this.setMaintainTimeStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", maintainTime));
    if (null != insureTimeStr && nowTime.getTime() < insureTimeStr)
      this.setInsureTimeStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", insureTimeStr));
    if (null != examineTime && nowTime.getTime() < examineTime)
      this.setExamineTimeStr(DateUtil.convertDateLongToDateString("yyyy-MM-dd", examineTime));
  }

  public boolean isHaveItem() {
    boolean isHaveItem = false;
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : this.getItemDTOs()) {
        if (StringUtils.isNotBlank(repairOrderItemDTO.getProductName())) {
          isHaveItem = true;
          break;
        }
      }
    }
    return isHaveItem;
  }

  public Set<Long> getServiceIds() {
    Set<Long> serviceIds = new HashSet<Long>();
    if (!ArrayUtils.isEmpty(this.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : this.getServiceDTOs()) {
        if (repairOrderServiceDTO != null && repairOrderServiceDTO.getServiceId() != null) {
          serviceIds.add(repairOrderServiceDTO.getServiceId());
        }
      }
    }
    return serviceIds;
  }

  public void calculateTotal() {
    double result = 0;
    double service = 0;
    double sales = 0;
    double otherIncome = 0;
    if (!ArrayUtils.isEmpty(this.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : this.getServiceDTOs()) {
        result += NumberUtil.doubleVal(repairOrderServiceDTO.getTotal());
        service += NumberUtil.doubleVal(repairOrderServiceDTO.getTotal());
      }
    }
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : this.getItemDTOs()) {
        result += NumberUtil.doubleVal(repairOrderItemDTO.getTotal());
        sales += NumberUtil.doubleVal(repairOrderItemDTO.getTotal());
      }
    }

    if(CollectionUtils.isNotEmpty(getOtherIncomeItemDTOList())){
      for(RepairOrderOtherIncomeItemDTO orderOtherIncomeItemDTO : getOtherIncomeItemDTOList()){
		    result += NumberUtil.doubleVal(orderOtherIncomeItemDTO.getPrice());
        otherIncome += NumberUtil.doubleVal(orderOtherIncomeItemDTO.getPrice());
      }
    }
    this.setTotal(NumberUtil.toReserve(result, NumberUtil.PRECISION));
    this.setServiceTotal(NumberUtil.toReserve(service, NumberUtil.PRECISION));
    this.setSalesTotal(NumberUtil.toReserve(sales, NumberUtil.PRECISION));
    this.setOtherIncomeTotal(NumberUtil.toReserve(otherIncome, NumberUtil.PRECISION));
  }

  public InsuranceOrderDTO getInsuranceOrderDTO() {
    return insuranceOrderDTO;
  }

  public void setInsuranceOrderDTO(InsuranceOrderDTO insuranceOrderDTO) {
    if (insuranceOrderDTO != null) {
      this.setInsuranceOrderId(insuranceOrderDTO.getId());
    }
    this.insuranceOrderDTO = insuranceOrderDTO;
  }

  public String getIsSmsSend() {
    return isSmsSend;
  }

  public void setIsSmsSend(String send) {
    isSmsSend = send;
  }

  public AppOrderDTO toAppOrderDTO() {
    AppOrderDTO appOrderDTO = new AppOrderDTO();
    appOrderDTO.setId(getId());
    appOrderDTO.setReceiptNo(getReceiptNo());
    appOrderDTO.setStatus(getStatus().getName());
    appOrderDTO.setVehicleNo(getVechicle());
    appOrderDTO.setCustomerName(getCustomerName());
    appOrderDTO.setShopId(getShopId());
    appOrderDTO.setOrderId(getId());
    appOrderDTO.setOrderType(OrderTypes.REPAIR.getName());
    appOrderDTO.setOrderTime(getVestDate());
    appOrderDTO.setServiceType("机修保养");
    appOrderDTO.setVehicleContact(getVehicleContact());
    appOrderDTO.setVehicleMobile(getVehicleMobile());


    if (ArrayUtil.isNotEmpty(getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : getItemDTOs()) {
        AppOrderItemDTO appOrderItemDTO = new AppOrderItemDTO();
        appOrderItemDTO.setContent(repairOrderItemDTO.getProductName() + "*" + NumberUtil.doubleVal(repairOrderItemDTO.getAmount()));
        appOrderItemDTO.setType(AppOrderItemDTO.itemTypeProduct);
        appOrderItemDTO.setAmount(repairOrderItemDTO.getTotal());
        appOrderDTO.getOrderItems().add(appOrderItemDTO);
      }
    }

    if (ArrayUtil.isNotEmpty(getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : getServiceDTOs()) {
        AppOrderItemDTO appOrderItemDTO = new AppOrderItemDTO();
        appOrderItemDTO.setContent(repairOrderServiceDTO.getService());
        appOrderItemDTO.setType(AppOrderItemDTO.itemTypeService);

        if (repairOrderServiceDTO.getConsumeType() == ConsumeType.TIMES) {
          appOrderItemDTO.setAmount(0D);
        } else {
          appOrderItemDTO.setAmount(repairOrderServiceDTO.getTotal());
        }
        appOrderDTO.getOrderItems().add(appOrderItemDTO);
      }
    }

    if (CollectionUtils.isNotEmpty(getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO repairOrderOtherIncomeItemDTO : getOtherIncomeItemDTOList()) {
        AppOrderItemDTO appOrderItemDTO = new AppOrderItemDTO();
        appOrderItemDTO.setContent(repairOrderOtherIncomeItemDTO.getName());
        appOrderItemDTO.setType(AppOrderItemDTO.itemTypeService);
        appOrderItemDTO.setAmount(NumberUtil.doubleVal(repairOrderOtherIncomeItemDTO.getPrice()));
        appOrderDTO.getOrderItems().add(appOrderItemDTO);
      }
    }

    return appOrderDTO;
  }

  public void setVehicleHandoverId(Long vehicleHandoverId) {
    this.vehicleHandoverId = vehicleHandoverId;
  }

  public Long getVehicleHandoverId() {
    return vehicleHandoverId;
  }

  public String getStartDateString() {
    return startDateString;
  }

  public void setStartDateString(String startDateString) {
    this.startDateString = startDateString;
  }
}
