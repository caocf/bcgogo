package com.bcgogo.txn.dto.pushMessage.enquiry;

import com.bcgogo.api.EnquiryShopResponseDTO;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;

/**
 * User: ZhangJuntao
 * Date: 13-11-13
 * Time: 下午1:37
 */
public class ShopQuoteEnquiryParameter extends AbstractEnquiryParameter {
  protected Long enquiryTime;

  public ShopQuoteEnquiryParameter() {
  }

  public ShopQuoteEnquiryParameter(Long shopId, String appUserNo, Long enquiryId, Long enquiryTime) {
    super(shopId, appUserNo, enquiryId);
    this.setEnquiryTime(enquiryTime);
  }

  public ShopQuoteEnquiryParameter(ShopEnquiryDTO shopEnquiryDTO, EnquiryShopResponseDTO enquiryShopResponseDTO) {
    super(enquiryShopResponseDTO.getShopId(), shopEnquiryDTO.getAppUserNo(), shopEnquiryDTO.getId());
    this.setEnquiryTime(enquiryShopResponseDTO.getResponseTime());
  }

  public String validate() {
    if (appUserNo == null) return "app userno is null";
    if (shopId == null) return "shop id is null";
    if (enquiryId == null) return "enquiry order id is null";
    if (enquiryTime == null) return "enquiry time id is null";
    return "";
  }

  public Long getEnquiryTime() {
    return enquiryTime;
  }

  public void setEnquiryTime(Long enquiryTime) {
    this.enquiryTime = enquiryTime;
  }
}
