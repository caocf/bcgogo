package com.bcgogo.txn.model.app;

import com.bcgogo.api.AppOrderDTO;
import com.bcgogo.api.AppServiceDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.app.AppointWay;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AppointOrderDTO;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "appoint_order")
public class AppointOrder extends LongIdentifier {
  private String appUserNo;//app用户账号
  private Long shopId;// 店铺id
  private Long orderId;//预约单生成的洗车美容单或者施工单id
  private String orderType;//预约单生成的单据类型（洗车美容单或者施工单）  OrderTypes.WASH_BEAUTY| OrderTypes.REPAIR,
  private AppointOrderStatus status;//      预约单状态
  private AppointWay appointWay;//     预约方式（手机端预约或者店铺预约）
  private Long createTime;//      预约单创建时间
  private Long appointTime;//     预约时间
  private String remark;//      备注
  private String customer;//     客户
  private String customerMobile;//     客户手机号码
  private String customerLandLine;//     客户座机
  private String openId;
  private Long customerId;//       客户id
  private String vehicleNo;//          车牌号
  private Long vehicleModelId;//      车型id
  private Long vehicleBrandId;//     车辆品牌id
  private String engineNo;//      发动机编号
  private String vehicleVin;//     车辆vin码
  private String vehicleContact;//    车辆联系人
  private String vehicleMobile;//     车辆手机号码
  private Long assistantId;//      预约单接单人（店铺员工id)

  private String vehicleModel;//      车型
  private String vehicleBrand;//     车辆品牌
  private String appointCustomer;//预约人

  private String receiptNo;//单据号码

  private Long standardVehicleModelId;//      标准车型id  手机端传递过来的车型di
  private Long standardVehicleBrandId;//     标准车辆品牌id   手机端传递过来的车辆品牌di

  private String coordinateLat;//预约单纬度信息
  private String coordinateLon;//预约单经度信息
  private Double total;//预约单总额
  private Double currentMileage;//当前里程

  public AppointOrder() {
    super();
  }

  public AppointOrder(AppServiceDTO appServiceDTO) {

    this.setAppUserNo(appServiceDTO.getUserNo());
    this.setShopId(appServiceDTO.getShopId());
    this.setAppointWay(appServiceDTO.getAppointWay());
    this.setCreateTime(System.currentTimeMillis());
    this.setAppointTime(appServiceDTO.getAppointTime());
    this.setRemark(appServiceDTO.getRemark());
    this.setReceiptNo(appServiceDTO.getReceiptNo());
    this.setStatus(AppointOrderStatus.PENDING);
    this.setCustomer(appServiceDTO.getContact());
    this.setCustomerMobile(appServiceDTO.getMobile());
     this.setOpenId(appServiceDTO.getOpenId());
    if(StringUtils.isNotEmpty(appServiceDTO.getCoordinateLat()) && StringUtils.isNotEmpty(appServiceDTO.getCoordinateLon())){
      this.setCoordinateLat(appServiceDTO.getCoordinateLat());
      this.setCoordinateLon(appServiceDTO.getCoordinateLon());
    }
    if (!StringUtils.isEmpty(appServiceDTO.getVehicleNo())) {
      this.setVehicleNo(appServiceDTO.getVehicleNo());
      this.setStandardVehicleBrandId(appServiceDTO.getVehicleBrandId());
      this.setStandardVehicleModelId(appServiceDTO.getVehicleModelId());
      this.setVehicleBrand(appServiceDTO.getVehicleBrand());
      this.setVehicleModel(appServiceDTO.getVehicleModel());
      this.setVehicleContact(appServiceDTO.getContact());
      this.setVehicleMobile(appServiceDTO.getMobile());
      this.setVehicleVin(appServiceDTO.getVehicleVin());
    }
  }

  public AppOrderDTO toAppOrderDTO() {
     AppOrderDTO appOrderDTO = new AppOrderDTO();
     appOrderDTO.setId(getId());
     appOrderDTO.setShopId(getShopId());
     appOrderDTO.setReceiptNo(getReceiptNo());
     appOrderDTO.setStatus(getStatus().getName());
     appOrderDTO.setVehicleNo(getVehicleNo());
     appOrderDTO.setVehicleContact(getVehicleContact());
     appOrderDTO.setVehicleMobile(getVehicleMobile());
     appOrderDTO.setCustomerName(getCustomer());
     appOrderDTO.setRemark(getRemark());
     appOrderDTO.setShopId(getShopId());
     appOrderDTO.setOrderId(getId());
     appOrderDTO.setOrderType(OrderTypes.APPOINT_ORDER.getName());
     appOrderDTO.setOrderTime(getAppointTime());
     return appOrderDTO;
   }

  public AppointOrderDTO toDTO() {
    AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
    appointOrderDTO.setId(getId());
    appointOrderDTO.setAppUserNo(getAppUserNo());
    appointOrderDTO.setShopId(getShopId());
    appointOrderDTO.setOrderId(getOrderId());
    appointOrderDTO.setOrderType(getOrderType());
    appointOrderDTO.setStatus(getStatus());
    appointOrderDTO.setAppointWay(getAppointWay());
    appointOrderDTO.setAppointTime(getAppointTime());
    appointOrderDTO.setCreateTime(getCreateTime());
    appointOrderDTO.setRemark(getRemark());
    appointOrderDTO.setCustomer(getCustomer());
    appointOrderDTO.setCustomerMobile(getCustomerMobile());
    appointOrderDTO.setCustomerLandLine(getCustomerLandLine());
    appointOrderDTO.setCustomerId(getCustomerId());
    appointOrderDTO.setOpenId(getOpenId());
    appointOrderDTO.setVehicleNo(getVehicleNo());
    appointOrderDTO.setVehicleModel(getVehicleModel());
    appointOrderDTO.setVehicleBrand(getVehicleBrand());
    appointOrderDTO.setVehicleBrandId(getVehicleBrandId());
    appointOrderDTO.setVehicleModelId(getVehicleModelId());
    appointOrderDTO.setEngineNo(getEngineNo());
    appointOrderDTO.setVehicleVin(getVehicleVin());
    appointOrderDTO.setVehicleContact(getVehicleContact());
    appointOrderDTO.setVehicleMobile(getVehicleMobile());
    appointOrderDTO.setAssistantId(getAssistantId());
    appointOrderDTO.setAppointCustomer(getAppointCustomer());
    appointOrderDTO.setReceiptNo(getReceiptNo());
    appointOrderDTO.setCoordinateLat(getCoordinateLat());
    appointOrderDTO.setCoordinateLon(getCoordinateLon());
    appointOrderDTO.setTotal(getTotal());
    appointOrderDTO.setCurrentMileage(getCurrentMileage());
    return appointOrderDTO;
  }

  public void fromDTO(AppointOrderDTO appointOrderDTO) {
    if(appointOrderDTO != null){
       this.setId(appointOrderDTO.getId());
      this.setAppUserNo(appointOrderDTO.getAppUserNo());
      this.setShopId(appointOrderDTO.getShopId());
      this.setOrderId(appointOrderDTO.getOrderId());
      this.setOrderType(appointOrderDTO.getOrderType());
      this.setStatus(appointOrderDTO.getStatus());
      this.setAppointWay(appointOrderDTO.getAppointWay());
      this.setAppointTime(appointOrderDTO.getAppointTime());
      this.setCreateTime(appointOrderDTO.getCreateTime());
      this.setRemark(appointOrderDTO.getRemark());
      this.setCustomer(appointOrderDTO.getCustomer());
      this.setCustomerMobile(appointOrderDTO.getCustomerMobile());
      this.setCustomerLandLine(appointOrderDTO.getCustomerLandLine());
      this.setCustomerId(appointOrderDTO.getCustomerId());
      this.setOpenId(appointOrderDTO.getOpenId());
      this.setVehicleNo(appointOrderDTO.getVehicleNo());
      this.setVehicleModel(appointOrderDTO.getVehicleModel());
      this.setVehicleBrand(appointOrderDTO.getVehicleBrand());
      this.setVehicleBrandId(appointOrderDTO.getVehicleBrandId());
      this.setVehicleModelId(appointOrderDTO.getVehicleModelId());
      this.setEngineNo(appointOrderDTO.getEngineNo());
      this.setVehicleVin(appointOrderDTO.getVehicleVin());
      this.setVehicleContact(appointOrderDTO.getVehicleContact());
      this.setVehicleMobile(appointOrderDTO.getVehicleMobile());
      this.setAssistantId(appointOrderDTO.getAssistantId());
      this.setAppointCustomer(appointOrderDTO.getAppointCustomer());
      this.setReceiptNo(appointOrderDTO.getReceiptNo());
      this.setCoordinateLon(appointOrderDTO.getCoordinateLon());
      this.setCoordinateLat(appointOrderDTO.getCoordinateLat());
      this.setTotal(appointOrderDTO.getTotal());
      this.setCurrentMileage(appointOrderDTO.getCurrentMileage());
    }
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
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

  @Column(name = "order_type")
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public AppointOrderStatus getStatus() {
    return status;
  }

  public void setStatus(AppointOrderStatus status) {
    this.status = status;
  }

//  @Column(name = "service_type")
//  @Enumerated(EnumType.STRING)
//  public AppServiceType getServiceType() {
//    return serviceType;
//  }
//
//  public void setServiceType(AppServiceType serviceType) {
//    this.serviceType = serviceType;
//  }

  @Column(name = "appoint_way")
  @Enumerated(EnumType.STRING)
  public AppointWay getAppointWay() {
    return appointWay;
  }

  public void setAppointWay(AppointWay appointWay) {
    this.appointWay = appointWay;
  }

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name = "appoint_time")
  public Long getAppointTime() {
    return appointTime;
  }

  public void setAppointTime(Long appointTime) {
    this.appointTime = appointTime;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Column(name = "customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "customer_mobile")
  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  @Column(name = "customer_landline")
  public String getCustomerLandLine() {
    return customerLandLine;
  }

  public void setCustomerLandLine(String customerLandLine) {
    this.customerLandLine = customerLandLine;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "vehicle_model_id")
  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  @Column(name = "vehicle_brand_id")
  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  @Column(name = "engine_no")
  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name = "vehicle_vin")
  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
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

  @Column(name = "assistant_id")
  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  @Column(name = "vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "appoint_customer")
  public String getAppointCustomer() {
    return appointCustomer;
  }

  public void setAppointCustomer(String appointCustomer) {
    this.appointCustomer = appointCustomer;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "standard_vehicle_model_id")
  public Long getStandardVehicleModelId() {
    return standardVehicleModelId;
  }

  public void setStandardVehicleModelId(Long standardVehicleModelId) {
    this.standardVehicleModelId = standardVehicleModelId;
  }

  @Column(name = "standard_vehicle_brand_id")
  public Long getStandardVehicleBrandId() {
    return standardVehicleBrandId;
  }

  public void setStandardVehicleBrandId(Long standardVehicleBrandId) {
    this.standardVehicleBrandId = standardVehicleBrandId;
  }

  @Column(name = "coordinate_lat")
  public String getCoordinateLat() {
    return coordinateLat;
  }

  public void setCoordinateLat(String coordinateLat) {
    this.coordinateLat = coordinateLat;
  }

  @Column(name = "coordinate_lon")
  public String getCoordinateLon() {
    return coordinateLon;
  }

  public void setCoordinateLon(String coordinateLon) {
    this.coordinateLon = coordinateLon;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "current_mileage")
  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }
}
