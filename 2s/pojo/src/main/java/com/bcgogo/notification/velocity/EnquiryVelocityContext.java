package com.bcgogo.notification.velocity;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.utils.DateUtil;

/**
 * User: ZhangJuntao
 * Date: 13-11-13
 * Time: 下午2:42
 */
public class EnquiryVelocityContext {
  private ShopDTO shopDTO;
  private String appUserNo;
  private String vehicleNo;
  private Long enquiryTime;
  private String enquiryTimeStr;

  public void from(ShopQuoteEnquiryParameter parameter) {
    setAppUserNo(parameter.getAppUserNo());
    setEnquiryTime(parameter.getEnquiryTime());
  }

  public void from(AppEnquiryParameter parameter) {
    setAppUserNo(parameter.getAppUserNo());
    setVehicleNo(parameter.getVehicleNo());
  }


  public ShopDTO getShopDTO() {
    return shopDTO;
  }

  public void setShopDTO(ShopDTO shopDTO) {
    this.shopDTO = shopDTO;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getEnquiryTime() {
    return enquiryTime;
  }

  public void setEnquiryTime(Long enquiryTime) {
    this.enquiryTime = enquiryTime;
    if (enquiryTime != null) {
      setEnquiryTimeStr(DateUtil.convertDateLongToString(enquiryTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getEnquiryTimeStr() {
    return enquiryTimeStr;
  }

  public void setEnquiryTimeStr(String enquiryTimeStr) {
    this.enquiryTimeStr = enquiryTimeStr;
  }


}
