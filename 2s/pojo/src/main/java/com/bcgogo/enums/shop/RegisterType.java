package com.bcgogo.enums.shop;

import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-1
 * Time: 上午10:49
 */
//与 RegisterType.js一致
public enum RegisterType {             //客户来源
  SALESMAN_REGISTER,           //销售注册店铺
  SERVICE_REGISTER,           //客服CRM注册店铺
  SYSTEM_INVITE_CUSTOMER,
  SYSTEM_INVITE_SUPPLIER,
  CUSTOMER_INVITE, //500 配置
  SUPPLIER_INVITE, //200 配置
  SUPPLIER_REGISTER,
  SELF_REGISTER;   //无邀请码，直接注册

  //用户自己注册
  public static boolean isRegisterByCustomerSelf(RegisterType type) {
    return SYSTEM_INVITE_CUSTOMER == type || SYSTEM_INVITE_SUPPLIER == type ||
        CUSTOMER_INVITE == type || SUPPLIER_INVITE == type;
  }

  //非bcgogo注册 需要走试用逻辑
  public static boolean isRegisterNotByBcgogo(RegisterType type) {
    return SYSTEM_INVITE_CUSTOMER == type || SYSTEM_INVITE_SUPPLIER == type ||
        CUSTOMER_INVITE == type || SUPPLIER_INVITE == type || SUPPLIER_REGISTER == type || SELF_REGISTER == type;
  }

  public static boolean isRegisterByShopSuggestion(RegisterType type) {
    return CUSTOMER_INVITE == type || SUPPLIER_INVITE == type;
  }


  public static RegisterType getRegisterTypeByInviteType(InvitationCodeType invitationCodeType, OperatorType inviteeType) {
    RegisterType registerType = null;
    if (InvitationCodeType.SYSTEM.equals(invitationCodeType)) {
      if (OperatorType.CUSTOMER.equals(inviteeType)) {
        registerType = SYSTEM_INVITE_CUSTOMER;
      } else if (OperatorType.SUPPLIER.equals(inviteeType)) {
        registerType = SYSTEM_INVITE_SUPPLIER;
      }
    } else if (OperatorType.CUSTOMER.equals(inviteeType)) {
      registerType = SUPPLIER_INVITE;
    } else if (OperatorType.SUPPLIER.equals(inviteeType)) {
      registerType = CUSTOMER_INVITE;
    }
    return registerType;
  }
}
