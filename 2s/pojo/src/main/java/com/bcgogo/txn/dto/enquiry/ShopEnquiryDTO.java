package com.bcgogo.txn.dto.enquiry;

import com.bcgogo.api.AppUserImageDTO;
import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.api.EnquiryShopResponseDTO;
import com.bcgogo.api.EnquiryTargetShopDTO;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-24
 * Time: 下午1:59
 * 店铺查看
 */
public class ShopEnquiryDTO implements Serializable {
  private String receiptNo;
  private Long id;  //询价单id
  private String idStr;
  private String appUserNo;
  private String description; //询价单描述
  private AppUserImageDTO[] enquiryImages; //询价单图片信息
  private EnquiryShopResponseDTO[] enquiryShopResponses; //询价单店铺回复信息
  private EnquiryShopResponseStatus responseStatus;  //询价单店铺操作的状态状态  <-EnquiryTargetShop.shopResponseStatus
  private EnquiryTargetShopStatus  appEnquiryTargetStatus;//用户操作询价单发送给当前店铺的状态 <-EnquiryTargetShop.status
  private EnquiryStatus  enquiryStatus;//用户操作询价单的状态  <-Enquiry.status
  private String responseStatusStr;//询价单店铺操作的状态状态的描述
  private String appUserName;
  private String vehicleNo;
  private String appUserMobile;
  private Long lastResponseTime; //最后报价时间
  private String lastResponseTimeStr;
  private Long sendTime;//询价时间
  private String sendTimeStr;//询价时间

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }else {
      setIdStr("");
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AppUserImageDTO[] getEnquiryImages() {
    return enquiryImages;
  }

  public void setEnquiryImages(AppUserImageDTO[] enquiryImages) {
    this.enquiryImages = enquiryImages;
  }

  public EnquiryShopResponseDTO[] getEnquiryShopResponses() {
    return enquiryShopResponses;
  }

  public void setEnquiryShopResponses(EnquiryShopResponseDTO[] enquiryShopResponses) {
    this.enquiryShopResponses = enquiryShopResponses;
  }


  public EnquiryShopResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(EnquiryShopResponseStatus responseStatus) {
    this.responseStatus = responseStatus;
    if(responseStatus != null){
      setResponseStatusStr(responseStatus.getName());
    }else {
      setResponseStatusStr("");
    }
  }

  public String getResponseStatusStr() {
    return responseStatusStr;
  }

  public void setResponseStatusStr(String responseStatusStr) {
    this.responseStatusStr = responseStatusStr;
  }

  public String getLastResponseTimeStr() {
    return lastResponseTimeStr;
  }

  public void setLastResponseTimeStr(String lastResponseTimeStr) {
    this.lastResponseTimeStr = lastResponseTimeStr;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
    if (sendTime != null) {
      setSendTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, sendTime));
    } else {
      setSendTimeStr("");
    }
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }

  public String getAppUserName() {
    return appUserName;
  }

  public void setAppUserName(String appUserName) {
    this.appUserName = appUserName;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getAppUserMobile() {
    return appUserMobile;
  }

  public void setAppUserMobile(String appUserMobile) {
    this.appUserMobile = appUserMobile;
  }

  public Long getLastResponseTime() {
    return lastResponseTime;
  }

  public void setLastResponseTime(Long lastResponseTime) {
    this.lastResponseTime = lastResponseTime;
    if(lastResponseTime != null){
      setLastResponseTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,lastResponseTime));
    }else {
      setLastResponseTimeStr("");
    }
  }

  public EnquiryTargetShopStatus getAppEnquiryTargetStatus() {
    return appEnquiryTargetStatus;
  }

  public void setAppEnquiryTargetStatus(EnquiryTargetShopStatus appEnquiryTargetStatus) {
    this.appEnquiryTargetStatus = appEnquiryTargetStatus;
  }

  public EnquiryStatus getEnquiryStatus() {
    return enquiryStatus;
  }

  public void setEnquiryStatus(EnquiryStatus enquiryStatus) {
    this.enquiryStatus = enquiryStatus;
  }

  public void setEnquiryDTO(EnquiryDTO enquiryDTO) {
    if (enquiryDTO != null) {
      setId(enquiryDTO.getId());
      setAppUserNo(enquiryDTO.getAppUserNo());
      setAppUserName(enquiryDTO.getAppUserName());
      setAppUserMobile(enquiryDTO.getAppUserMobile());
      setVehicleNo(enquiryDTO.getVehicleNo());
      setDescription(enquiryDTO.getDescription());
      setEnquiryStatus(enquiryDTO.getStatus());
    }
  }

  public void setEnquiryTargetShopDTO(EnquiryTargetShopDTO enquiryTargetShopDTO) {
    if (enquiryTargetShopDTO != null) {
      setReceiptNo(enquiryTargetShopDTO.getReceiptNo());
      setSendTime(enquiryTargetShopDTO.getSendTime());
      setLastResponseTime(enquiryTargetShopDTO.getLastResponseTime());
      setResponseStatus(enquiryTargetShopDTO.getShopResponseStatus());
      setAppEnquiryTargetStatus(enquiryTargetShopDTO.getStatus());
    }
  }
}
