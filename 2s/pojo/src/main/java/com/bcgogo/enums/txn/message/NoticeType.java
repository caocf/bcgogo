package com.bcgogo.enums.txn.message;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午2:40                  关联客户成功通知  提示客户合并供应商
 */
public enum NoticeType {
  CANCEL_ASSOCIATION_NOTICE("取消关联通知"),
  ASSOCIATION_REJECT_NOTICE("关联拒绝通知"),
  CUSTOMER_ACCEPT_TO_SUPPLIER("关联客户成功通知"),    //供应商申请添加客户店，客户接受后给供应商的通知              //客户id
  SUPPLIER_ACCEPT_TO_CUSTOMER("关联供应商成功通知"),   //客户申请添加供应商，供应商接受后给客户的通知              //供应商id
  SUPPLIER_ACCEPT_TO_SUPPLIER("供应商接受客户申请通知"),        //客户申请添加供应商，供应商接受后给供应商的通知   //客户id
  CUSTOMER_ACCEPT_TO_CUSTOMER("客户接受供应商申请通知");    //供应商申请添加客户店，客户接受后给客户的通知            //供应商id



  private final String value;

  public String getValue() {
    return value;
  }

  NoticeType(String value) {
    this.value = value;
  }
}
