package com.bcgogo.txn.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.app.AppointWay;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.notification.velocity.AppointVelocityContext;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.SysAppointParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-5
 * Time: 上午10:36
 */
public class AppointOrderDTO extends BaseDTO {
  private String appUserNo;//app用户账号
  private Long shopId;// 店铺id
  private Long userId;//操作单据登录人Id
  private Long orderId;//预约单生成的洗车美容单或者施工单id
  private String orderIdStr;
  private String orderType;//预约单生成的单据类型（洗车美容单或者施工单）
  private AppointOrderStatus status;//      预约单状态
  private String statusStr;//单据状态文案
  private String appointServiceType;//预约服务内容（从item上组装的数据）
  private AppointWay appointWay;//     预约方式（在线预约或者电话预约或者现场预约）
  private String appointWayStr;//预约方式枚举值对应的文案
  private Long createTime;//      预约单创建时间
  private String createTimeStr;
  private Long appointTime;//     预约时间
  private String appointTimeStr;
  private String remark;//      备注
  private String customer;//     客户
  private String customerMobile;//     客户手机号码
  private String customerLandLine;//     客户座机
  private String openId;
  private Long customerId;//       客户id
  private Long vehicleId;//车辆Id（店铺下的车辆）
  private String vehicleNo;//          车牌号
  private Long vehicleModelId;//      车型id 本地库的vehicleModelId
  private Long vehicleBrandId;//     车辆品牌id 本地库的vehicleBrandId
  private String engineNo;//      发动机编号
  private String vehicleVin;//     车辆vin码
  private String vehicleContact;//    车辆联系人
  private String vehicleMobile;//     车辆手机号码
  private Long assistantId;//      预约单接单人（店铺员工id)
  private String  assistantMan;//      预约单接单人

  private String vehicleModel;//      车型
  private String vehicleBrand;//     车辆品牌
  private String appointCustomer;//预约人
  private String memberNo;//会员卡号
  private String memberType;//会员类型
  private String memberBalance; //会员卡余额0元
  private String memberStatus; //会员卡状态 有效 失效 部分有效
  private String receiptNo; //单据号
  private boolean isAddVehicleToSolr = false;//是否将新车型添加到solr
  private boolean isAddVehicleLicenceNoToSolr = false;//是否将新车辆信息添加到solr
  private AppointOrderServiceItemDTO[] serviceItemDTOs;
  private String refuseMsg;//拒绝理由，保存在 config的 operation_log 的content上
  private String cancelMsg;//取消理由，保存在 config的 operation_log 的content上

  private String coordinateLat;//预约单纬度信息
  private String coordinateLon;//预约单经度信息
  private AppointOrderMaterialDTO[] itemDTOs;
  private AppointOrderServiceDetailDTO[] serviceDTOs;
  private Double serviceTotal;
  private Double itemTotal;
  private Double total;
  private Double currentMileage;
  private FaultInfoToShopDTO[] faultInfoToShopDTOs;
  private String shopAddress;
  private String shopName;
  private String shopMobile;

  public String getShopAddress() {
    return shopAddress;
  }

  public void setShopAddress(String shopAddress) {
    this.shopAddress = shopAddress;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopMobile() {
    return shopMobile;
  }

  public void setShopMobile(String shopMobile) {
    this.shopMobile = shopMobile;
  }

  public boolean validateOverdueAppointRemindParams() {
    return !StringUtil.isEmpty(vehicleNo) && !ArrayUtils.isEmpty(serviceItemDTOs)
        && appointTime != null;
  }


  public AppointVelocityContext toAppointVelocityContext() {
    AppointVelocityContext context = new AppointVelocityContext();
    context.setVehicleNo(getVehicleNo());
    context.setAppointTime(getAppointTime());
    String services = "";
    int i = 0;
    for (AppointOrderServiceItemDTO itemDTO : serviceItemDTOs) {
      if (i++ != 0) {
        services += ",";
      }
      services += itemDTO.getServiceName();
    }
    context.setServices(services);
    return context;
  }

  public void setVehicleDTO(VehicleDTO vehicleDTO) {
    if(vehicleDTO != null){
      setVehicleId(vehicleDTO.getId());
      setVehicleNo(vehicleDTO.getLicenceNo());
      setVehicleModel(vehicleDTO.getModel());
      setVehicleModelId(vehicleDTO.getModelId());
      setVehicleBrand(vehicleDTO.getBrand());
      setVehicleBrandId(vehicleDTO.getBrandId());
      setVehicleContact(vehicleDTO.getContact());
      setVehicleMobile(vehicleDTO.getMobile());
      setCurrentMileage(vehicleDTO.getObdMileage());
    }
  }

public void setCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO != null) {
      setCustomerId(customerDTO.getId());
      setCustomer(customerDTO.getName());
      setCustomerLandLine(customerDTO.getLandLine());
      if(!ArrayUtils.isEmpty(customerDTO.getContacts())){
        ContactDTO contactDTO = customerDTO.getContacts()[0];
        if(contactDTO != null){
          setCustomerMobile(contactDTO.getMobile());
//          setContactId(contactDTO.getId());
        }
      }
    }
  }

  //设置预约单需要更新的数据
  public void setUpdateInfo(AppointOrderDTO appointOrderDTO) throws ParseException {
    if(appointOrderDTO != null){
      //车辆品牌
      if(StringUtils.isNotEmpty(appointOrderDTO.getVehicleBrand()) && StringUtils.isEmpty(this.getVehicleBrand())){
        this.setVehicleBrand(appointOrderDTO.getVehicleBrand());
      }
      //车型
      if(StringUtils.isNotEmpty(appointOrderDTO.getVehicleModel()) && StringUtils.isEmpty(this.getVehicleModel())){
        this.setVehicleModel(appointOrderDTO.getVehicleModel());
      }
      //车辆联系人
      if (StringUtils.isNotEmpty(appointOrderDTO.getVehicleContact()) && StringUtils.isEmpty(this.getVehicleContact())) {
        this.setVehicleContact(appointOrderDTO.getVehicleContact());
      }
      //车辆联系人电话
      if (StringUtils.isNotEmpty(appointOrderDTO.getVehicleMobile()) && StringUtils.isEmpty(this.getVehicleMobile())) {
        this.setVehicleMobile(appointOrderDTO.getVehicleMobile());
      }
         //客户手机
      if (StringUtils.isNotEmpty(appointOrderDTO.getCustomerMobile()) && StringUtils.isEmpty(this.getCustomerMobile())) {
        this.setCustomerMobile(appointOrderDTO.getCustomerMobile());
      }
             //客户座机
      if (StringUtils.isNotEmpty(appointOrderDTO.getCustomerLandLine()) && StringUtils.isEmpty(this.getCustomerLandLine())) {
        this.setCustomerLandLine(appointOrderDTO.getCustomerLandLine());
      }
      //预约时间
      if(StringUtil.isNotEmpty(appointOrderDTO.getAppointTimeStr())){
        setAppointTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm",appointOrderDTO.getAppointTimeStr()));
      }
       //预约人
      setAppointCustomer(appointOrderDTO.getAppointCustomer());
      //接待人
      setAssistantMan(appointOrderDTO.getAssistantMan());
      //预约方式
      setAppointWay(appointOrderDTO.getAppointWay());
      //备注
      setRemark(appointOrderDTO.getRemark());
      //预约服务项目
      setServiceItemDTOs(appointOrderDTO.getServiceItemDTOs());
      setUserId(appointOrderDTO.getUserId());
      //更新材料项目
      setItemDTOs(appointOrderDTO.getItemDTOs());
      //更新服务项目
      setServiceDTOs(appointOrderDTO.getServiceDTOs());
      //更新总价
      setTotal(appointOrderDTO.getTotal());
    }
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
    if(orderId != null) {
      setOrderIdStr(String.valueOf(orderId));
    }
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public AppointOrderStatus getStatus() {
    return status;
  }

  public void setStatus(AppointOrderStatus status) {
    this.status = status;
    if(status != null){
      setStatusStr(status.getName());
    }else {
      setStatusStr("");
    }
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public String getAppointServiceType() {
    return appointServiceType;
  }

  public void setAppointServiceType(String appointServiceType) {
    this.appointServiceType = appointServiceType;
  }

  public AppointWay getAppointWay() {
    return appointWay;
  }

  public void setAppointWay(AppointWay appointWay) {
    this.appointWay = appointWay;
    if(appointWay != null){
      setAppointWayStr(appointWay.getName());
    }else {
      setAppointWayStr("");
    }
  }

  public String getAppointWayStr() {
    return appointWayStr;
  }

  public void setAppointWayStr(String appointWayStr) {
    this.appointWayStr = appointWayStr;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
    if(createTime != null) {
      this.setCreateTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, createTime));
    }
  }

  public Long getAppointTime() {
    return appointTime;
  }

  public void setAppointTime(Long appointTime) {
    this.appointTime = appointTime;
    if(appointTime != null) {
      this.setAppointTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT,appointTime));
    }
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public String getCustomerLandLine() {
    return customerLandLine;
  }

  public void setCustomerLandLine(String customerLandLine) {
    this.customerLandLine = customerLandLine;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getAppointCustomer() {
    return appointCustomer;
  }

  public void setAppointCustomer(String appointCustomer) {
    this.appointCustomer = appointCustomer;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(String memberBalance) {
    this.memberBalance = memberBalance;
  }

  public String getAssistantMan() {
    return assistantMan;
  }

  public void setAssistantMan(String assistantMan) {
    this.assistantMan = assistantMan;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getMemberStatus() {
    return memberStatus;
  }

  public void setMemberStatus(String memberStatus) {
    this.memberStatus = memberStatus;
  }

  public AppointOrderServiceItemDTO[] getServiceItemDTOs() {
    return serviceItemDTOs;
  }

  public void setServiceItemDTOs(AppointOrderServiceItemDTO[] serviceItemDTOs) {
    this.serviceItemDTOs = serviceItemDTOs;

    //预约服务类型，目前只做一条数据，以后需求可能会变动
    if (!ArrayUtils.isEmpty(serviceItemDTOs)) {
      StringBuilder sb = new StringBuilder();
      for (AppointOrderServiceItemDTO appointOrderServiceItemDTO : serviceItemDTOs) {
        if (appointOrderServiceItemDTO != null && StringUtils.isNotBlank(appointOrderServiceItemDTO.getServiceName())
            && ObjectStatus.ENABLED.equals(appointOrderServiceItemDTO.getStatus())) {
          sb.append(appointOrderServiceItemDTO.getServiceName()).append(",");
        }
      }
      setAppointServiceType(sb.toString().replaceFirst(",$", ""));
    }
  }

  public void setMemberDTO(MemberDTO memberDTO) {
     if(memberDTO == null) {
       return;
     }
    setMemberNo(memberDTO.getMemberNo());
    setMemberBalance(memberDTO.getBalance() == null ? "0元": NumberUtil.round(memberDTO.getBalance(),2)+"元");
    setMemberType(memberDTO.getType());
  }

  public String getAppointTimeStr() {
    return appointTimeStr;
  }

  public void setAppointTimeStr(String appointTimeStr) {
    this.appointTimeStr = appointTimeStr;
  }

  public String getCreateTimeStr() {

    return createTimeStr;
  }

  public void setCreateTimeStr(String createTimeStr) {
    this.createTimeStr = createTimeStr;
  }

  public boolean isAddVehicleToSolr() {
    return isAddVehicleToSolr;
  }

  public void setAddVehicleToSolr(boolean addVehicleToSolr) {
    isAddVehicleToSolr = addVehicleToSolr;
  }

  public boolean isAddVehicleLicenceNoToSolr() {
    return isAddVehicleLicenceNoToSolr;
  }

  public void setAddVehicleLicenceNoToSolr(boolean addVehicleLicenceNoToSolr) {
    isAddVehicleLicenceNoToSolr = addVehicleLicenceNoToSolr;
  }

  public String getRefuseMsg() {
    return refuseMsg;
  }

  public void setRefuseMsg(String refuseMsg) {
    this.refuseMsg = refuseMsg;
  }

  public String getCancelMsg() {
    return cancelMsg;
  }

  public void setCancelMsg(String cancelMsg) {
    this.cancelMsg = cancelMsg;
  }

  public String getCoordinateLat() {
    return coordinateLat;
  }

  public void setCoordinateLat(String coordinateLat) {
    this.coordinateLat = coordinateLat;
  }

  public String getCoordinateLon() {
    return coordinateLon;
  }

  public void setCoordinateLon(String coordinateLon) {
    this.coordinateLon = coordinateLon;
  }

  public AppointOrderMaterialDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(AppointOrderMaterialDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
    double total = 0;
    if (!ArrayUtils.isEmpty(itemDTOs)) {
      for (AppointOrderMaterialDTO materialDTO : itemDTOs) {
        if (StringUtils.isNotBlank(materialDTO.getProductName())) {
          total += materialDTO.getTotal();
        }
      }
      total = NumberUtil.round(total, 2);
    }
    setItemTotal(total);
  }

  public AppointOrderServiceDetailDTO[] getServiceDTOs() {
    return serviceDTOs;
  }

  public void setServiceDTOs(AppointOrderServiceDetailDTO[] serviceDTOs) {
    this.serviceDTOs = serviceDTOs;
    double total = 0;
    if (!ArrayUtils.isEmpty(serviceDTOs)) {
      for (AppointOrderServiceDetailDTO serviceDTO : serviceDTOs) {
        if (StringUtils.isNotBlank(serviceDTO.getService())) {
          total += serviceDTO.getTotal();
        }
      }
      total = NumberUtil.round(total, 2);
    }
    setServiceTotal(total);
  }

  public Double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(Double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  public Double getItemTotal() {
    return itemTotal;
  }

  public void setItemTotal(Double itemTotal) {
    this.itemTotal = itemTotal;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  public FaultInfoToShopDTO[] getFaultInfoToShopDTOs() {
    return faultInfoToShopDTOs;
  }

  public void setFaultInfoToShopDTOs(FaultInfoToShopDTO[] faultInfoToShopDTOs) {
    this.faultInfoToShopDTOs = faultInfoToShopDTOs;
  }

  @Override
  public String toString() {
    return "AppointOrderDTO{" +
        "appUserNo='" + appUserNo + '\'' +
        ", shopId=" + shopId +
        ", userId=" + userId +
        ", orderId=" + orderId +
        ", orderIdStr='" + orderIdStr + '\'' +
        ", orderType='" + orderType + '\'' +
        ", status=" + status +
        ", statusStr='" + statusStr + '\'' +
        ", appointServiceType='" + appointServiceType + '\'' +
        ", appointWay=" + appointWay +
        ", appointWayStr='" + appointWayStr + '\'' +
        ", createTime=" + createTime +
        ", createTimeStr='" + createTimeStr + '\'' +
        ", appointTime=" + appointTime +
        ", appointTimeStr='" + appointTimeStr + '\'' +
        ", remark='" + remark + '\'' +
        ", customer='" + customer + '\'' +
        ", customerMobile='" + customerMobile + '\'' +
        ", customerLandLine='" + customerLandLine + '\'' +
        ", customerId=" + customerId +
        ", vehicleId=" + vehicleId +
        ", vehicleNo='" + vehicleNo + '\'' +
        ", vehicleModelId=" + vehicleModelId +
        ", vehicleBrandId=" + vehicleBrandId +
        ", engineNo='" + engineNo + '\'' +
        ", vehicleVin='" + vehicleVin + '\'' +
        ", vehicleContact='" + vehicleContact + '\'' +
        ", vehicleMobile='" + vehicleMobile + '\'' +
        ", assistantId=" + assistantId +
        ", assistantMan='" + assistantMan + '\'' +
        ", vehicleModel='" + vehicleModel + '\'' +
        ", vehicleBrand='" + vehicleBrand + '\'' +
        ", appointCustomer='" + appointCustomer + '\'' +
        ", memberNo='" + memberNo + '\'' +
        ", memberType='" + memberType + '\'' +
        ", memberBalance='" + memberBalance + '\'' +
        ", memberStatus='" + memberStatus + '\'' +
        ", receiptNo='" + receiptNo + '\'' +
        ", isAddVehicleToSolr=" + isAddVehicleToSolr +
        ", isAddVehicleLicenceNoToSolr=" + isAddVehicleLicenceNoToSolr +
        ", serviceItemDTOs=" + Arrays.toString(serviceItemDTOs) +
        '}';
  }

  public void setRefuseOrCancelMsg(List<OperationLogDTO> operationLogDTOList) {
     if(CollectionUtil.isEmpty(operationLogDTOList)) {
       return;
     }
     for(OperationLogDTO operationLogDTO : operationLogDTOList) {
        if(OperationTypes.REFUSE.equals(operationLogDTO.getOperationType())) {
           setRefuseMsg(operationLogDTO.getContent());
        } else if(OperationTypes.CANCEL.equals(operationLogDTO.getOperationType())) {
           setCancelMsg(operationLogDTO.getContent());
        }
     }
  }

  public AppAppointParameter toAppAppointParameter() {
    AppAppointParameter appAppointParameter = new AppAppointParameter();
    appAppointParameter.setAppUserNo(this.getAppUserNo());
    appAppointParameter.setVehicleNo(this.getVehicleNo());
    appAppointParameter.setShopId(this.getShopId());
    appAppointParameter.setAppointOrderId(this.getId());
    appAppointParameter.setApplyTime(this.getAppointTime());
    appAppointParameter.setServices(this.getAppointServiceType());
    return appAppointParameter;
  }

  public SysAppointParameter toSysAppointParameter() {
    SysAppointParameter appAppointParameter = new SysAppointParameter();
    appAppointParameter.setAppUserNo(this.getAppUserNo());
    appAppointParameter.setVehicleNo(this.getVehicleNo());
    appAppointParameter.setShopId(this.getShopId());
    appAppointParameter.setAppointOrderId(this.getId());
    appAppointParameter.setApplyTime(this.getAppointTime());
    appAppointParameter.setServices(this.getAppointServiceType());
    return appAppointParameter;
  }

}
