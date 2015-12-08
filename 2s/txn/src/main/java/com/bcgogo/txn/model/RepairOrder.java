package com.bcgogo.txn.model;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.user.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-14
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "repair_order")
public class RepairOrder extends LongIdentifier {
  public RepairOrder() {
  }

  public RepairOrder fromDTO(RepairOrderDTO repairOrderDTO) {
    if(repairOrderDTO == null)
      return this;
    setId(repairOrderDTO.getId());
    this.shopId = repairOrderDTO.getShopId();
    this.date = repairOrderDTO.getDate();
    this.no = repairOrderDTO.getNo();
    this.deptId = repairOrderDTO.getDeptId();
    this.dept = repairOrderDTO.getDept();
    this.vechicleId = repairOrderDTO.getVechicleId();
    this.vechicle = repairOrderDTO.getVechicle();
    this.customerId = repairOrderDTO.getCustomerId();
    this.customer = repairOrderDTO.getCustomerName();
    this.customerCompany = repairOrderDTO.getCompany();
    this.customerContact = repairOrderDTO.getContact();
    this.customerMobile = repairOrderDTO.getMobile();
    this.customerLandline = repairOrderDTO.getLandLine();
    this.customerAddress = repairOrderDTO.getAddress();
    this.memberNo = repairOrderDTO.getCustomerMemberNo();
    this.memberType = repairOrderDTO.getCustomerMemberType();
    this.memberStatus = repairOrderDTO.getCustomerMemberStatus();
    this.startMileage = repairOrderDTO.getStartMileage();
    this.endMileage = repairOrderDTO.getEndMileage();
    this.fuelNumber = repairOrderDTO.getFuelNumber();
    this.startDate = repairOrderDTO.getStartDate();
    this.endDate = repairOrderDTO.getEndDate();
    this.executorId = repairOrderDTO.getExecutorId();
    this.executor = repairOrderDTO.getExecutor();
    this.total = repairOrderDTO.getTotal();
    this.editorId = repairOrderDTO.getEditorId();
    this.editor = repairOrderDTO.getEditor();
    this.editDate = repairOrderDTO.getEditDate();
    this.reviewerId = repairOrderDTO.getReviewerId();
    this.reviewer = repairOrderDTO.getReviewer();
    this.reviewDate = repairOrderDTO.getReviewDate();
    this.invalidatorId = repairOrderDTO.getInvalidatorId();
    this.invalidator = repairOrderDTO.getInvalidator();
    this.invalidateDate = repairOrderDTO.getInvalidateDate();
    this.statusEnum = repairOrderDTO.getStatus();
    this.serviceTypeEnum = repairOrderDTO.getServiceType();
    this.serviceWorker = repairOrderDTO.getServiceWorker();
    this.productSaler = repairOrderDTO.getProductSaler();
    this.memo = repairOrderDTO.getMemo();
    this.totalCostPrice = repairOrderDTO.getTotalCostPrice();
    this.vestDate = (repairOrderDTO.getVestDate() == null ? 0 : repairOrderDTO.getVestDate());
    this.settleDate = repairOrderDTO.getSettleDate();
    this.receiptNo = repairOrderDTO.getReceiptNo();
    this.vehicleHandover = repairOrderDTO.getVehicleHandover();
    this.vehicleHandoverId = repairOrderDTO.getVehicleHandoverId();
    if (null == repairOrderDTO.getAfterMemberDiscountTotal()) {
      this.afterMemberDiscountTotal = repairOrderDTO.getTotal();
    } else {
      this.afterMemberDiscountTotal = repairOrderDTO.getAfterMemberDiscountTotal();
    }
    this.vehicleEngine = repairOrderDTO.getEngine();
    this.vehicleEngineNo = repairOrderDTO.getVehicleEngineNo();
    this.vehicleModel = repairOrderDTO.getModel();
    this.vehicleBrand = repairOrderDTO.getBrand();
    this.vehicleColor = repairOrderDTO.getVehicleColor();
    this.vehicleBuyDate = repairOrderDTO.getVehicleBuyDate();
    this.vehicleChassisNo = repairOrderDTO.getVehicleChassisNo();
    this.setStorehouseId(repairOrderDTO.getStorehouseId());
    this.setStorehouseName(repairOrderDTO.getStorehouseName());

    this.description = repairOrderDTO.getDescription();
    this.setStatementAccountOrderId(getStatementAccountOrderId());
    this.vehicleContact = repairOrderDTO.getVehicleContact();
    this.vehicleMobile = repairOrderDTO.getVehicleMobile();

    this.productSalerIds = repairOrderDTO.getProductSalerIds();
    this.customerContactId = repairOrderDTO.getContactId();

    this.appUserNo = repairOrderDTO.getAppUserNo();
    this.appointOrderId = repairOrderDTO.getAppointOrderId();
    this.isSmsSend=repairOrderDTO.getIsSmsSend();
    this.setOtherTotalCostPrice(repairOrderDTO.getOtherTotalCostPrice());
    this.setOtherIncomeTotal(repairOrderDTO.getOtherIncomeTotal());
    this.setConsumingRecordId(repairOrderDTO.getConsumingRecordId());//

    return this;
  }

  public RepairOrderDTO toDTO() {
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    repairOrderDTO.setId(getId());
    if(null != repairOrderDTO.getId())
    {
      repairOrderDTO.setIdStr(repairOrderDTO.getId().toString());
    }

    repairOrderDTO.setShopId(getShopId());
    repairOrderDTO.setDate(getDate());
    repairOrderDTO.setNo(getNo());
    repairOrderDTO.setDeptId(getDeptId());
    repairOrderDTO.setDept(getDept());
    repairOrderDTO.setVechicleId(getVechicleId());
    repairOrderDTO.setVechicle(getVechicle());
    repairOrderDTO.setCustomerId(getCustomerId());
    repairOrderDTO.setCustomerName(getCustomer());
    repairOrderDTO.setContact(getCustomerContact());
    repairOrderDTO.setCompany(getCustomerCompany());
    repairOrderDTO.setMobile(getCustomerMobile());
    repairOrderDTO.setLandLine(getCustomerLandline());
    repairOrderDTO.setAddress(getCustomerAddress());
    repairOrderDTO.setCustomerMemberNo(getMemberNo());
    repairOrderDTO.setCustomerMemberStatus(getMemberStatus());
    repairOrderDTO.setCustomerMemberType(getMemberType());
    repairOrderDTO.setStartMileage(getStartMileage());
    repairOrderDTO.setEndMileage(getEndMileage());
    repairOrderDTO.setFuelNumber(getFuelNumber());
    repairOrderDTO.setStartDate(getStartDate());
    repairOrderDTO.setEndDate(getEndDate());
    repairOrderDTO.setExecutorId(getExecutorId());
    repairOrderDTO.setExecutor(getExecutor());
    repairOrderDTO.setTotal(getTotal());
    repairOrderDTO.setEditorId(getEditorId());
    repairOrderDTO.setEditor(getEditor());
    repairOrderDTO.setEditDate(getEditDate());
    repairOrderDTO.setReviewerId(getReviewerId());
    repairOrderDTO.setReviewer(getReviewer());
    repairOrderDTO.setReviewDate(getReviewDate());
    repairOrderDTO.setInvalidatorId(getInvalidatorId());
    repairOrderDTO.setInvalidator(getInvalidator());
    repairOrderDTO.setInvalidateDate(getInvalidateDate());
    repairOrderDTO.setStatus(getStatusEnum());
    repairOrderDTO.setServiceType(getServiceTypeEnum());
    repairOrderDTO.setServiceWorker(getServiceWorker());
    repairOrderDTO.setProductSaler(getProductSaler());
    repairOrderDTO.setMemo(getMemo());
    repairOrderDTO.setTotalCostPrice(getTotalCostPrice());
    repairOrderDTO.setVestDate( (getVestDate() == null ? 0 : getVestDate()) );
	  repairOrderDTO.setCreationDate(getCreationDate());
    repairOrderDTO.setSettleDate(getSettleDate());
    repairOrderDTO.setReceiptNo(getReceiptNo());
    if (null == getAfterMemberDiscountTotal()) {
      repairOrderDTO.setAfterMemberDiscountTotal(getTotal());
    } else {
      repairOrderDTO.setAfterMemberDiscountTotal(getAfterMemberDiscountTotal());
    }
    if ("未填写".equals(this.getProductSaler())) {
      repairOrderDTO.setProductSaler("");
    }
    if ("未填写".equals(this.getServiceWorker())) {
      repairOrderDTO.setServiceWorker("");
    }
    repairOrderDTO.setEngine(getVehicleEngine());
    repairOrderDTO.setVehicleEngineNo(getVehicleEngineNo());
    repairOrderDTO.setBrand(getVehicleBrand());
    repairOrderDTO.setModel(getVehicleModel());
    repairOrderDTO.setVehicleColor(getVehicleColor());
    repairOrderDTO.setVehicleBuyDate(getVehicleBuyDate());
    repairOrderDTO.setVehicleChassisNo(getVehicleChassisNo());
    repairOrderDTO.setStorehouseId(this.getStorehouseId());
    repairOrderDTO.setStorehouseName(this.getStorehouseName());
    repairOrderDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    repairOrderDTO.setDescription(this.getDescription());
    repairOrderDTO.setVehicleContact(this.getVehicleContact());
    repairOrderDTO.setVehicleMobile(this.getVehicleMobile());

    repairOrderDTO.setProductSalerIds(this.getProductSalerIds());
    repairOrderDTO.setContactId(this.getCustomerContactId());
    repairOrderDTO.setAppUserNo(this.getAppUserNo());
    repairOrderDTO.setAppointOrderId(getAppointOrderId());
    repairOrderDTO.setOtherTotalCostPrice(this.getOtherTotalCostPrice());
    repairOrderDTO.setOtherIncomeTotal(this.getOtherIncomeTotal());
    repairOrderDTO.setVehicleHandover(this.getVehicleHandover());
    repairOrderDTO.setVehicleHandoverId(this.getVehicleHandoverId());
    repairOrderDTO.setIsSmsSend(this.getIsSmsSend());
    repairOrderDTO.setConsumingRecordId(this.getConsumingRecordId());//
    return repairOrderDTO;
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

  @Column(name = "dept")
  public String getDept() {
    return dept;
  }

  private void setDept(String dept) {
    this.dept = dept;
  }

  @Column(name = "vechicle_id")
  public Long getVechicleId() {
    return vechicleId;
  }

  public void setVechicleId(Long vechicleId) {
    this.vechicleId = vechicleId;
  }

  @Column(name="vechicle")
  public String getVechicle() {
    return vechicle;
  }

  private void setVechicle(String vechicle) {
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

  @Column(name="customer")
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

  @Column(name = "executor")
  public String getExecutor() {
    return executor;
  }

  private void setExecutor(String executor) {
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

  @Column(name = "editor")
  public String getEditor() {
    return editor;
  }

  private void setEditor(String editor) {
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

  @Column(name = "reviewer")
  public String getReviewer() {
    return reviewer;
  }

  private void setReviewer(String reviewer) {
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

  @Column(name = "invalidator")
  public String getInvalidator() {
    return invalidator;
  }

  private void setInvalidator(String invalidator) {
    this.invalidator = invalidator;
  }

  @Column(name = "invalidate_date", length = 20)
  public String getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(String invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  @Column(name = "status")
  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  @Column(name = "status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatusEnum() {
    return statusEnum;
  }

  public void setStatusEnum(OrderStatus statusEnum) {
    this.statusEnum = statusEnum;
  }

  @Column(name = "service_type")
  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  @Column(name = "service_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getServiceTypeEnum() {
    return serviceTypeEnum;
  }

  public void setServiceTypeEnum(OrderTypes serviceTypeEnum) {
    this.serviceTypeEnum = serviceTypeEnum;
  }

  @Column(name = "service_worker")
  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  @Column(name = "product_saler")
  public String getProductSaler() {
    return productSaler;
  }

  public void setProductSaler(String productSaler) {
    this.productSaler = productSaler;
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

  @Column(name = "vest_date")
  public Long getVestDate() {
    return this.vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "settle_date")
  public Long getSettleDate() {
    return settleDate;
  }

  public void setSettleDate(Long settleDate) {
    this.settleDate = settleDate;
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
  @Column(name="storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
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

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  @Column(name="storehouse_name")
  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  @Column(name="description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name="statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  @Column(name="vehicle_mobile")
  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  @Column(name="vehicle_contact")
  public String getVehicleContact() {

    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  @Column(name = "product_saler_ids")
  public String getProductSalerIds() {
    return productSalerIds;
  }

  public void setProductSalerIds(String productSalerIds) {
    this.productSalerIds = productSalerIds;
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

  @Column(name="other_total_cost_price")
  public Double getOtherTotalCostPrice() {
    return otherTotalCostPrice;
  }

  public void setOtherTotalCostPrice(Double otherTotalCostPrice) {
    this.otherTotalCostPrice = otherTotalCostPrice;
  }

  @Column(name = "other_income_total")
  public Double getOtherIncomeTotal() {
    return otherIncomeTotal;
  }

  public void setOtherIncomeTotal(Double otherIncomeTotal) {
    this.otherIncomeTotal = otherIncomeTotal;
  }
  @Column(name = "vehicle_handover")
  public String getVehicleHandover() {
    return vehicleHandover;
  }

  public void setVehicleHandover(String vehicleHandover) {
    this.vehicleHandover = vehicleHandover;
  }
  @Column(name = "vehicle_handover_id")
  public Long getVehicleHandoverId() {
    return vehicleHandoverId;
  }

  public void setVehicleHandoverId(Long vehicleHandoverId) {
    this.vehicleHandoverId = vehicleHandoverId;
  }

  @Column(name = "is_sms_send")
  public String getIsSmsSend() {
    return isSmsSend;
  }

  public void setIsSmsSend(String send) {
    isSmsSend = send;
  }

  @Column(name = "consuming_record_id")
  public Long getConsumingRecordId() {
    return consumingRecordId;
  }

  public void setConsumingRecordId(Long consumingRecordId) {
    this.consumingRecordId = consumingRecordId;
  }

  private Long shopId;
  private Long date;
  private String no;
  private Long deptId;
  private String dept;                //瞬态字段
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
  private String customerContact;
  private String customerCompany;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private String memberNo;
  private String memberType;
  private MemberStatus memberStatus;
  private Double startMileage;
  private Double endMileage;
  private String vehicleHandover;
  private Long vehicleHandoverId;
  private String fuelNumber;
  private Long startDate;
  private Long endDate;
  private Long executorId;
  private String executor;
  private double total;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long reviewerId;
  private String reviewer;
  private Long reviewDate;
  private Long invalidatorId;
  private String invalidator;
  private String invalidateDate;
  private Long status;
  private OrderStatus statusEnum;
  private String serviceType;
  private OrderTypes serviceTypeEnum;
  private String serviceWorker;
  private String productSaler;
  private String memo;
  private Double totalCostPrice;
  private Long vestDate;
  private Long settleDate;

  private String receiptNo;
  private Double afterMemberDiscountTotal;

  private Long storehouseId;
  private String storehouseName;
  private Long statementAccountOrderId;//对账单id

  private String description;

  private String productSalerIds;
  private Long customerContactId;

  private String appUserNo;
  private Long appointOrderId;//预约单id

  private Double otherTotalCostPrice; //施工单其他费用成本总和
  private Double otherIncomeTotal;//其他费用总和

  //记录施工单是否发送过消息给用户
  private String isSmsSend;

  //记录施工单对应的代金券消费记录
  private Long consumingRecordId;
}
