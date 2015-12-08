package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;

/**
 * Created with IntelliJ IDEA.
 * User: Zhangjuntao
 * Date: 13-11-15
 * Time: 上午11:06
 */
public interface IEnquiryPushMessageService {
  /**
   * 店铺报价
   *
   * @param parameter {
   *                  appUserNo
   *                  shopId
   *                  enquiryId
   *                  enquiryTime
   *                  }
   */
  boolean createShopQuoteEnquiryMessageToApp(ShopQuoteEnquiryParameter parameter) throws Exception;

  /**
   * app询价
   *
   * @param parameter {
   *                  vehicleNo
   *                  appUserNo
   *                  shopId
   *                  enquiryId
   *                  }
   */
  boolean createAppSubmitEnquiryMessageToShop(AppEnquiryParameter parameter) throws Exception;
}
