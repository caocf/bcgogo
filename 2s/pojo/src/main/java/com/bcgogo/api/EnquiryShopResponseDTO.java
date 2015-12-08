package com.bcgogo.api;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 上午11:18
 */
public class EnquiryShopResponseDTO implements Serializable {
  private Long id;
  private Long enquiryId;
  private Long shopId;
  private String shopName;
  private String responseMsg;
  private Long responseTime;
  private String responseTimeStr;
  private EnquiryShopResponseStatus status; //shop处理的状态
  private String idStr;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(String.valueOf(id));
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

  public Long getEnquiryId() {
    return enquiryId;
  }

  public void setEnquiryId(Long enquiryId) {
    this.enquiryId = enquiryId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getResponseMsg() {
    return responseMsg;
  }

  public void setResponseMsg(String responseMsg) {
    this.responseMsg = responseMsg;
  }

  public Long getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(Long responseTime) {
    this.responseTime = responseTime;
    if(responseTime != null){
      setResponseTimeStr(DateUtil.dateLongToStr(responseTime,DateUtil.DATE_STRING_FORMAT_DEFAULT));
    }else {
      setResponseTimeStr("");
    }
  }

  public String getResponseTimeStr() {
    return responseTimeStr;
  }

  public void setResponseTimeStr(String responseTimeStr) {
    this.responseTimeStr = responseTimeStr;
  }

  public EnquiryShopResponseStatus getStatus() {
    return status;
  }

  public void setStatus(EnquiryShopResponseStatus status) {
    this.status = status;
  }
}
