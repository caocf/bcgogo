package com.bcgogo.txn.model;

import com.bcgogo.api.AppOrderDTO;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.user.dto.MemberDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wash_beauty_order")
public class WashBeautyOrder extends LongIdentifier {
  private Long shopId;
  private Long date;
  private String no;
  private Long deptId;
  private String dept;
  private Long vechicleId;
  private String vechicle;
  private String vehicleEngineNo;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleColor;
  private Long vehicleBuyDate;
  private String vehicleEngine;
  private String vehicleChassisNo;
  private String vehicleContact;
  private String vehicleMobile;
  private Long customerId;
  private String customer;
  private String customerCompany;
  private String customerContact;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private String memberNo;
  private String memberType;
  private MemberStatus memberStatus;
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
  private String receiptNo;
  private Double afterMemberDiscountTotal;

  private Long statementAccountOrderId;//对账单id

  private Long customerContactId;

  private String appUserNo;
  private Long appointOrderId;

  private Long consumingRecordId;  //记录施工单对应的代金券消费记录id add by LiTao 2015-11-18

  public WashBeautyOrder() {
  }



  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "date")
  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Column(name = "no", length = 20)
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Column(name = "dept_id")
  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  @Column(name = "dept", length = 20)
  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  @Column(name = "vechicle_id")
  public Long getVechicleId() {
    return vechicleId;
  }

  public void setVechicleId(Long vechicleId) {
    this.vechicleId = vechicleId;
  }

  @Column(name = "vechicle", length = 20)
  public String getVechicle() {
    return vechicle;
  }

  public void setVechicle(String vechicle) {
    this.vechicle = vechicle;
  }

  @Column(name="vehicle_engine_no")
  public String getVehicleEngineNo() {
    return vehicleEngineNo;
  }

  public void setVehicleEngineNo(String vehicleEngineNo) {
    this.vehicleEngineNo = vehicleEngineNo;
  }

  @Column(name="vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name="vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name="vehicle_color")
  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  @Column(name="vehicle_buy_date")
  public Long getVehicleBuyDate() {
    return vehicleBuyDate;
  }

  public void setVehicleBuyDate(Long vehicleBuyDate) {
    this.vehicleBuyDate = vehicleBuyDate;
  }

  @Column(name="vehicle_engine")
  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  @Column(name="vehicle_chassis_no")
  public String getVehicleChassisNo() {
    return vehicleChassisNo;
  }

  public void setVehicleChassisNo(String vehicleChassisNo) {
    this.vehicleChassisNo = vehicleChassisNo;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer", length = 100)
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "start_mileage")
  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  @Column(name = "end_mileage", length = 20)
  public Double getEndMileage() {
    return endMileage;
  }

  public void setEndMileage(Double endMileage) {
    this.endMileage = endMileage;
  }

  @Column(name = "fuel_number")
  public String getFuelNumber() {
    return fuelNumber;
  }

  public void setFuelNumber(String fuelNumber) {
    this.fuelNumber = fuelNumber;
  }

  @Column(name = "start_date", length = 200)
  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }


  @Column(name = "end_date")
  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  @Column(name = "executor_id")
  public Long getExecutorId() {
    return executorId;
  }

  public void setExecutorId(Long executorId) {
    this.executorId = executorId;
  }

  @Column(name = "executor", length = 20)
  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  @Column(name = "editor", length = 20)
  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "reviewer_id")
  public Long getReviewerId() {
    return reviewerId;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  @Column(name = "reviewer", length = 20)
  public String getReviewer() {
    return reviewer;
  }

  public void setReviewer(String reviewer) {
    this.reviewer = reviewer;
  }

  @Column(name = "review_date")
  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  @Column(name = "invalidator_id")
  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  @Column(name = "invalidator", length = 20)
  public String getInvalidator() {
    return invalidator;
  }

  public void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  @Column(name = "invalidate_date", length = 20)
  public String getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(String invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  @Column(name="vest_date")
  public Long getVestDate(){
      return this.vestDate;
  }

  public void setVestDate(Long vestDate){
      this.vestDate = vestDate;
  }

  @Column(name="receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name="after_member_discount_total")
  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }

  @Column(name="customer_company")
  public String getCustomerCompany() {
    return customerCompany;
  }

  public void setCustomerCompany(String customerCompany) {
    this.customerCompany = customerCompany;
  }

  @Column(name="customer_mobile")
  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  @Column(name="customer_landline")
  public String getCustomerLandline() {
    return customerLandline;
  }

  public void setCustomerLandline(String customerLandline) {
    this.customerLandline = customerLandline;
  }

  @Column(name="customer_address")
  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  @Column(name="member_no")
  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  @Column(name="member_type")
  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  @Column(name="member_status")
  @Enumerated(EnumType.STRING)
  public MemberStatus getMemberStatus() {
    return memberStatus;
  }

  public void setMemberStatus(MemberStatus memberStatus) {
    this.memberStatus = memberStatus;
  }

  @Column(name="customer_contact")
  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  @Column(name="statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  @Column(name = "vehicle_contact")
  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  @Column(name = "vehicle_mobile")
  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  @Column(name="customer_contact_id")
  public Long getCustomerContactId() {
    return customerContactId;
  }

  public void setCustomerContactId(Long customerContactId) {
    this.customerContactId = customerContactId;
  }

  @Column(name="app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name="appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name="consuming_record_id")
  public Long getConsumingRecordId() {
    return consumingRecordId;
  }

  public void setConsumingRecordId(Long consumingRecordId) {
    this.consumingRecordId = consumingRecordId;
  }

  public WashBeautyOrderDTO toDTO(){
    WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
    washBeautyOrderDTO.setCustomer(this.getCustomer());
    washBeautyOrderDTO.setCustomerId(this.getCustomerId());
    washBeautyOrderDTO.setDate(this.getDate());
    washBeautyOrderDTO.setDept(this.getDept());
    washBeautyOrderDTO.setDeptId(this.getDeptId());
    washBeautyOrderDTO.setEditDate(this.getEditDate());
    washBeautyOrderDTO.setEditor(this.getEditor());
    washBeautyOrderDTO.setEditorId(this.getEditorId());
    washBeautyOrderDTO.setEndDate(this.getEndDate());
    washBeautyOrderDTO.setEndMileage(this.getEndMileage());
    washBeautyOrderDTO.setExecutor(this.getExecutor());
    washBeautyOrderDTO.setExecutorId(this.getExecutorId());
    washBeautyOrderDTO.setFuelNumber(this.getFuelNumber());
    washBeautyOrderDTO.setId(this.getId());
    washBeautyOrderDTO.setInvalidateDate(this.getInvalidateDate());
    washBeautyOrderDTO.setInvalidator(this.getInvalidator());
    washBeautyOrderDTO.setInvalidatorId(this.getInvalidatorId());
    washBeautyOrderDTO.setMemo(this.getMemo());
    washBeautyOrderDTO.setNo(this.getNo());
    washBeautyOrderDTO.setReviewDate(this.getReviewDate());
    washBeautyOrderDTO.setReviewer(this.getReviewer());
    washBeautyOrderDTO.setReviewerId(this.getReviewerId());
    washBeautyOrderDTO.setShopId(this.getShopId());
    washBeautyOrderDTO.setStartDate(this.getStartDate());
    washBeautyOrderDTO.setStartMileage(this.getStartMileage());
    washBeautyOrderDTO.setStatus(this.getStatus());
    washBeautyOrderDTO.setTotal(this.getTotal());
    washBeautyOrderDTO.setTotalCostPrice(this.getTotalCostPrice());
    washBeautyOrderDTO.setVechicle(this.getVechicle());
    washBeautyOrderDTO.setVechicleId(this.getVechicleId());
    washBeautyOrderDTO.setVestDate(this.getVestDate());
    washBeautyOrderDTO.setCreationDate(this.getCreationDate());
    washBeautyOrderDTO.setReceiptNo(this.getReceiptNo());

    if (null == this.getAfterMemberDiscountTotal()) {
      washBeautyOrderDTO.setAfterMemberDiscountTotal(this.getTotal());
    } else {
      washBeautyOrderDTO.setAfterMemberDiscountTotal(this.getAfterMemberDiscountTotal());
    }
    washBeautyOrderDTO.setCompany(getCustomerCompany());
    washBeautyOrderDTO.setContact(getCustomerContact());
    washBeautyOrderDTO.setMobile(getCustomerMobile());
    washBeautyOrderDTO.setLandLine(getCustomerLandline());
    washBeautyOrderDTO.setAddress(getCustomerAddress());
    MemberDTO memberDTO = new MemberDTO();
    memberDTO.setMemberNo(getMemberNo());
    memberDTO.setType(getMemberType());
    memberDTO.setStatus(getMemberStatus());
    washBeautyOrderDTO.setMemberDTO(memberDTO);
    washBeautyOrderDTO.setVehicleEngine(getVehicleEngine());
    washBeautyOrderDTO.setVehicleEngineNo(getVehicleEngineNo());
    washBeautyOrderDTO.setBrand(getVehicleBrand());
    washBeautyOrderDTO.setModel(getVehicleModel());
    washBeautyOrderDTO.setVehicleColor(getVehicleColor());
    washBeautyOrderDTO.setVehicleBuyDate(getVehicleBuyDate());
    washBeautyOrderDTO.setVehicleChassisNo(getVehicleChassisNo());
    washBeautyOrderDTO.setVehicleContact(getVehicleContact());
    washBeautyOrderDTO.setVehicleMobile(getVehicleMobile());
    washBeautyOrderDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    washBeautyOrderDTO.setContactId(getCustomerContactId());
    washBeautyOrderDTO.setAppUserNo(getAppUserNo());
    washBeautyOrderDTO.setAppointOrderId(getAppointOrderId());
    washBeautyOrderDTO.setConsumingRecordId(getConsumingRecordId());
    return washBeautyOrderDTO;
  }

  public void fromDTO(WashBeautyOrderDTO washBeautyOrderDTO){
    if(washBeautyOrderDTO != null){
      this.setCustomer(washBeautyOrderDTO.getCustomer());
      this.setCustomerId(washBeautyOrderDTO.getCustomerId());
      this.setDate(washBeautyOrderDTO.getDate());
      this.setDept(washBeautyOrderDTO.getDept());
      this.setDeptId(washBeautyOrderDTO.getDeptId());
      this.setEditDate(washBeautyOrderDTO.getEditDate());
      this.setEditor(washBeautyOrderDTO.getEditor());
      this.setEditorId(washBeautyOrderDTO.getEditorId());
      this.setEndDate(washBeautyOrderDTO.getEndDate());
      this.setEndMileage(washBeautyOrderDTO.getEndMileage());
      this.setExecutor(washBeautyOrderDTO.getExecutor());
      this.setExecutorId(washBeautyOrderDTO.getExecutorId());
      this.setFuelNumber(washBeautyOrderDTO.getFuelNumber());
      if(washBeautyOrderDTO.getId() != null){
        this.setId(washBeautyOrderDTO.getId());
      }
      this.setInvalidateDate(washBeautyOrderDTO.getInvalidateDate());
      this.setInvalidator(washBeautyOrderDTO.getInvalidator());
      this.setInvalidatorId(washBeautyOrderDTO.getInvalidatorId());
      this.setMemo(washBeautyOrderDTO.getMemo());
      this.setNo(washBeautyOrderDTO.getNo());
      this.setReviewDate(washBeautyOrderDTO.getReviewDate());
      this.setReviewer(washBeautyOrderDTO.getReviewer());
      this.setReviewerId(washBeautyOrderDTO.getReviewerId());
      this.setShopId(washBeautyOrderDTO.getShopId());
      this.setStartDate(washBeautyOrderDTO.getStartDate());
      this.setStartMileage(washBeautyOrderDTO.getStartMileage());
      this.setStatus(washBeautyOrderDTO.getStatus());
      this.setTotal(washBeautyOrderDTO.getTotal());
      this.setTotalCostPrice(washBeautyOrderDTO.getTotalCostPrice());
      this.setVechicle(washBeautyOrderDTO.getVechicle());
      this.setVechicleId(washBeautyOrderDTO.getVechicleId());
      this.setVestDate(washBeautyOrderDTO.getVestDate());
      this.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
      if (null == washBeautyOrderDTO.getAfterMemberDiscountTotal()) {
        this.setAfterMemberDiscountTotal(washBeautyOrderDTO.getTotal());
      } else {
        this.setAfterMemberDiscountTotal(washBeautyOrderDTO.getAfterMemberDiscountTotal());
      }
      this.setCustomerCompany(washBeautyOrderDTO.getCompany());
      this.setCustomerContact(washBeautyOrderDTO.getContact());
      this.setCustomerMobile(washBeautyOrderDTO.getMobile());
      this.setCustomerLandline(washBeautyOrderDTO.getLandLine());
      this.setCustomerAddress(washBeautyOrderDTO.getAddress());
      this.setVehicleEngine(washBeautyOrderDTO.getVehicleEngine());
      this.setVehicleEngineNo(washBeautyOrderDTO.getVehicleEngineNo());
      this.setVehicleModel(washBeautyOrderDTO.getModel());
      this.setVehicleBrand(washBeautyOrderDTO.getBrand());
      this.setVehicleColor(washBeautyOrderDTO.getVehicleColor());
      this.setVehicleBuyDate(washBeautyOrderDTO.getVehicleBuyDate());
      this.setVehicleChassisNo(washBeautyOrderDTO.getVehicleChassisNo());
      this.setStatementAccountOrderId(getStatementAccountOrderId());
      MemberDTO memberDTO = washBeautyOrderDTO.getMemberDTO();
      if(memberDTO!=null){
        setMemberNo(memberDTO.getMemberNo());
        setMemberType(memberDTO.getType());
        setMemberStatus(memberDTO.getStatus());
      }
      this.setVehicleContact(washBeautyOrderDTO.getVehicleContact());
      this.setVehicleMobile(washBeautyOrderDTO.getVehicleMobile());
      this.setCustomerContactId(washBeautyOrderDTO.getContactId());
      this.setAppUserNo(washBeautyOrderDTO.getAppUserNo());
      this.setAppointOrderId(washBeautyOrderDTO.getAppointOrderId());
      this.setConsumingRecordId(washBeautyOrderDTO.getConsumingRecordId());
    }
  }

  public WashBeautyOrder(WashBeautyOrderDTO washBeautyOrderDTO){
    if(washBeautyOrderDTO != null){
      this.setCustomer(washBeautyOrderDTO.getCustomer());
      this.setCustomerId(washBeautyOrderDTO.getCustomerId());
      this.setDate(washBeautyOrderDTO.getDate());
      this.setDept(washBeautyOrderDTO.getDept());
      this.setDeptId(washBeautyOrderDTO.getDeptId());
      this.setEditDate(washBeautyOrderDTO.getEditDate());
      this.setEditor(washBeautyOrderDTO.getEditor());
      this.setEditorId(washBeautyOrderDTO.getEditorId());
      this.setEndDate(washBeautyOrderDTO.getEndDate());
      this.setEndMileage(washBeautyOrderDTO.getEndMileage());
      this.setExecutor(washBeautyOrderDTO.getExecutor());
      this.setExecutorId(washBeautyOrderDTO.getExecutorId());
      this.setFuelNumber(washBeautyOrderDTO.getFuelNumber());
      if(washBeautyOrderDTO.getId() != null){
        this.setId(washBeautyOrderDTO.getId());
      }
      this.setInvalidateDate(washBeautyOrderDTO.getInvalidateDate());
      this.setInvalidator(washBeautyOrderDTO.getInvalidator());
      this.setInvalidatorId(washBeautyOrderDTO.getInvalidatorId());
      this.setMemo(washBeautyOrderDTO.getMemo());
      this.setNo(washBeautyOrderDTO.getNo());
      this.setReviewDate(washBeautyOrderDTO.getReviewDate());
      this.setReviewer(washBeautyOrderDTO.getReviewer());
      this.setReviewerId(washBeautyOrderDTO.getReviewerId());
      this.setShopId(washBeautyOrderDTO.getShopId());
      this.setStartDate(washBeautyOrderDTO.getStartDate());
      this.setStartMileage(washBeautyOrderDTO.getStartMileage());
      this.setStatus(washBeautyOrderDTO.getStatus());
      this.setTotal(washBeautyOrderDTO.getTotal());
      this.setTotalCostPrice(washBeautyOrderDTO.getTotalCostPrice());
      this.setVechicle(washBeautyOrderDTO.getVechicle());
      this.setVechicleId(washBeautyOrderDTO.getVechicleId());
      this.setVestDate(washBeautyOrderDTO.getVestDate());
      this.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
      if(null == washBeautyOrderDTO.getAfterMemberDiscountTotal()) {
        this.setAfterMemberDiscountTotal(washBeautyOrderDTO.getTotal());
      } else {
        this.setAfterMemberDiscountTotal(washBeautyOrderDTO.getAfterMemberDiscountTotal());
      }
      this.setCustomerCompany(washBeautyOrderDTO.getCompany());
      this.setCustomerContact(washBeautyOrderDTO.getContact());
      this.setCustomerMobile(washBeautyOrderDTO.getMobile());
      this.setCustomerLandline(washBeautyOrderDTO.getLandLine());
      this.setCustomerAddress(washBeautyOrderDTO.getAddress());
      this.setVehicleEngine(washBeautyOrderDTO.getVehicleEngine());
      this.setVehicleEngineNo(washBeautyOrderDTO.getVehicleEngineNo());
      this.setVehicleModel(washBeautyOrderDTO.getModel());
      this.setVehicleBrand(washBeautyOrderDTO.getBrand());
      this.setVehicleColor(washBeautyOrderDTO.getVehicleColor());
      this.setVehicleBuyDate(washBeautyOrderDTO.getVehicleBuyDate());
      this.setVehicleChassisNo(washBeautyOrderDTO.getVehicleChassisNo());
      MemberDTO memberDTO = washBeautyOrderDTO.getMemberDTO();
      if(memberDTO!=null){
        setMemberNo(memberDTO.getMemberNo());
        setMemberType(memberDTO.getType());
        setMemberStatus(memberDTO.getStatus());
      }
      this.setVehicleContact(washBeautyOrderDTO.getVehicleContact());
      this.setVehicleMobile(washBeautyOrderDTO.getVehicleMobile());
      this.setCustomerContactId(washBeautyOrderDTO.getContactId());
      this.setAppUserNo(washBeautyOrderDTO.getAppUserNo());
      this.setAppointOrderId(washBeautyOrderDTO.getAppointOrderId());
      this.setConsumingRecordId(washBeautyOrderDTO.getConsumingRecordId());
    }
  }
}
