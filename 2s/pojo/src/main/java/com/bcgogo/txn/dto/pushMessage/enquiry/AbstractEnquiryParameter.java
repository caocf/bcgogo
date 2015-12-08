package com.bcgogo.txn.dto.pushMessage.enquiry;

/**
 * User: ZhangJuntao
 * Date: 13-11-13
 * Time: 下午1:55
 */
public abstract class AbstractEnquiryParameter {
  protected Long enquiryId;
  protected Long shopId;
  protected String appUserNo;

  public AbstractEnquiryParameter() {

  }

  public AbstractEnquiryParameter(Long shopId, String appUserNo, Long enquiryId) {
    this.setShopId(shopId);
    this.setAppUserNo(appUserNo);
    this.setEnquiryId(enquiryId);
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

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }
}
