package com.bcgogo.api;

import com.bcgogo.enums.app.AppointWay;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 手机端用户预约服务
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class AppServiceDTO implements Serializable {

  private Long serviceCategoryId;//服务类型二级分类Id
  private Long appointTime;//预约时间
  private String appointTimeStr;//预约时间
  private String mobile;//手机号
  private String vehicleNo;//车牌号
  private String vehicleBrand;//车辆品牌
  private Long vehicleBrandId;//车辆品牌ID
  private String vehicleModel;//车型
  private Long vehicleModelId;//车型ID
  private String vehicleVin;//车辆唯一标识号
  private String userNo;//用户账号
  private String contact;//联系人
  private String receiptNo;//联系人
  private String remark;//备注
  private Long shopId;//店铺id
  private AppointWay appointWay;//     预约方式（手机端预约或者店铺预约）

  private Long appUserId;
  private String coordinateLat;//预约单纬度信息
  private String coordinateLon;//预约单经度信息

  private String openId;

  private AppointOrderFaultInfoItemDTO[] faultInfoItems;

  /**
   * @param from 来源 app或wx
   * @return
   */
  public String validate(String from) {
    if ("wx".equals(from)) {
      if (StringUtil.isEmpty(openId)) {
        return "用户账号异常。";
      }
      if (StringUtil.isEmpty(appointTimeStr)) {
        return "请选择服务时间";
      }
    } else {
      if (StringUtil.isEmpty(userNo)) {
        return "用户账号不能为空";
      }
      if (appointTime == null) {
        return "请选择服务时间";
      }
    }

    if (serviceCategoryId == null) {
      return "请选择服务类型";
    }

    if (StringUtil.isEmpty(contact)) {
      return "请输入预约联系人";
    } else if (contact.length() > 20) {
      return "预约联系人最多20个字";
    }

    if (StringUtil.isEmpty(mobile)) {
      return "手机号不能为空";
    } else if (mobile.length() > 20) {
      return "手机号最多20个字";
    } else if (RegexUtils.isNotMobile(getMobile())) {
      return "手机号格式错误,请重新输入";
    }

    if (shopId == null) {
      return "请选择预约店铺";
    }
    if (StringUtil.isEmpty(vehicleNo)) {
      return "请输入车牌号";
    } else if (vehicleNo.length() > 20) {
      return "车牌号最多20个字";
    } else if (!RegexUtils.isVehicleNo(vehicleNo)) {
      return "车牌号格式错误，请重新输入";
    }
    if (StringUtil.isNotEmpty(remark) && remark.length() > 200) {
      return "备注最多200个字";
    }
    return "";
  }

  public Long getServiceCategoryId() {
    return serviceCategoryId;
  }

  public void setServiceCategoryId(Long serviceCategoryId) {
    this.serviceCategoryId = serviceCategoryId;
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAppointTime() {
    return appointTime;
  }

  public void setAppointTime(Long appointTime) {
    this.appointTime = appointTime;
  }

  public String getAppointTimeStr() {
    return appointTimeStr;
  }

  public void setAppointTimeStr(String appointTimeStr) {
    this.appointTimeStr = appointTimeStr;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public AppointWay getAppointWay() {
    return appointWay;
  }

  public void setAppointWay(AppointWay appointWay) {
    this.appointWay = appointWay;
  }

  public Long getAppUserId() {
    return appUserId;
  }

  public void setAppUserId(Long appUserId) {
    this.appUserId = appUserId;
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

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public AppointOrderFaultInfoItemDTO[] getFaultInfoItems() {
    return faultInfoItems;
  }

  public void setFaultInfoItems(AppointOrderFaultInfoItemDTO[] faultInfoItems) {
    this.faultInfoItems = faultInfoItems;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("AppServiceDTO{");
    sb.append("serviceCategoryId=").append(serviceCategoryId);
    sb.append(", appointTime=").append(appointTime);
    sb.append(", mobile='").append(mobile).append('\'');
    sb.append(", vehicleNo='").append(vehicleNo).append('\'');
    sb.append(", vehicleBrand='").append(vehicleBrand).append('\'');
    sb.append(", vehicleBrandId=").append(vehicleBrandId);
    sb.append(", vehicleModel='").append(vehicleModel).append('\'');
    sb.append(", vehicleModelId=").append(vehicleModelId);
    sb.append(", vehicleVin='").append(vehicleVin).append('\'');
    sb.append(", userNo='").append(userNo).append('\'');
    sb.append(", contact='").append(contact).append('\'');
    sb.append(", remark='").append(remark).append('\'');
    sb.append(", shopId=").append(shopId);
    sb.append(", appointWay=").append(appointWay);
    sb.append(", appUserId=").append(appUserId);
    sb.append(", coordinateLat='").append(coordinateLat).append('\'');
    sb.append(", coordinateLon='").append(coordinateLon).append('\'');
    sb.append(", faultInfoItems=").append(Arrays.toString(faultInfoItems));
    sb.append('}');
    return sb.toString();
  }
}
