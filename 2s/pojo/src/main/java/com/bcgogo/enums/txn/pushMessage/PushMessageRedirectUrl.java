package com.bcgogo.enums.txn.pushMessage;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public enum PushMessageRedirectUrl {
  RedirectShopUrl("_RedirectShopUrl_","shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=%s"),
  RedirectShopProductDetailUrl("_RedirectShopProductDetailUrl_","shopProductDetail.do?method=toShopProductDetail&paramShopId=%s&productLocalId=%s&quotedPreBuyOrderItemId=%s"),
  RedirectBuyInformationDetailUrl("_RedirectBuyInformationDetailUrl_","preBuyOrder.do?method=showBuyInformationDetailByPreBuyOrderItemId&preBuyOrderItemId=%s"),
  RedirectPreBuyOrderUrl("_RedirectPreBuyOrderUrl_","preBuyOrder.do?method=showPreBuyOrderById&preBuyOrderId=%s"),
  RedirectIgnoredQuotedPreBuyOrderUrl("_RedirectIgnoredQuotedPreBuyOrderUrl_","preBuyOrder.do?method=showBuyInformationDetailByQuotedPreBuyOrderItemId&quotedPreBuyOrderItemId=%s"),
  RedirectAppointOrderDetailUrl("_RedirectAppointOrderDetailUrl_","appoint.do?method=showAppointOrderDetail&appointOrderId=%s"),
  RedirectCustomerDetailUrl("_RedirectCustomerDetailUrl_","unitlink.do?method=customer&customerId=%s"),
  RedirectSupplierDetailUrl("_RedirectSupplierDetailUrl_","unitlink.do?method=supplier&supplierId=%s"),
  RedirectAnnouncementUrl("_RedirectAnnouncementUrl_","sysReminder.do?method=toSysAnnouncement"),
  RedirectSMSWriteUrl("_RedirectSMSWriteUrl_","sms.do?method=smswrite"),
  RedirectRelatedApplyPushMessageUrl("_RedirectRelatedApplyPushMessageUrl_","pushMessage.do?method=receiverPushMessageList&category=RelatedApplyMessage&relatedObjectId=%s"),
  RedirectEnquiryPushMessageUrl("_RedirectEnquiryPushMessageUrl_","enquiry.do?method=showEnquiryDetail&enquiryId=%s"),
  SendSms2ShopUrl("_SendSms2ShopUrl_","sms.do?method=smswrite&mobile=%s&contactName=%s"),
  ShopFaultInfoUrl("_ShopFaultInfoUrl_","shopFaultInfo.do?method=showShopFaultInfoList&shopFaultInfoId=%s"),
  BackToPushMessageCenterPage("_BackToPushMessageCenterPage_","pushMessage.do?method=receiverPushMessageList&category=%s"),
  ;
  private final String key;
  private final String url;

  private PushMessageRedirectUrl(String key, String url) {
    this.key = key;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
  public String getKey() {
    return key;
  }
}