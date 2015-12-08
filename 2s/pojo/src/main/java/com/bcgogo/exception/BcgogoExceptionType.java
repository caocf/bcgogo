package com.bcgogo.exception;

/**
 * Created by IntelliJ IDEA.
 * User: XJ
 * Date: 9/27/11
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public enum BcgogoExceptionType {
  configNotFound(10000L, "config Not Found."),
  ShopNotFound(10001L, "Shop not found."),
  UserNotFound(10002L, "User not found."),
  UserGroupNotFound(10003L, "User group not found."),

  InvalidMobileNumber(10091L, "Invalid mobile number."),
  InvalidEmailAddress(10092L, "Invalid email address."),

  DuplicateUserNameFound(20001L, "Duplicate user name found."),
  //DuplicateUserNoFound(20002L, "Duplicate user no found."),
  DuplicateMobileFound(20011L, "Duplicate mobile Number found."),
  DuplicateEmailFound(20012L, "Duplicate email address found."),
  InvalidUserName(20013L, "User name is invalid."),
  EmptyPasswordNotAllowed(20014L, "Empty password is not allowed."),

  DuplicateUserGroupNameFound(20021L, "Duplicate user group name found."),
  InvalidUserGroupName(20022L, "User group name is invalid."),
  UserGroupNameExisted(20023L, "User group Existed"),

  InvalidShopName(20031L, "Shop name is invalid."),
  DuplicateShopNameFound(20032L, "Duplicate shop name found."),

  VehicleNotFound(20041L, "Vehicle Not Found."),

  CustomerNotFound(20051L, "Customer Not Found."),

  SupplierNotFound(20061L, "Supplier Not Found."),

  CustomerRecordNotFound(20071L, "Customer Record Not Found."),

   OBDNotUnique(20151L, "obd info error,no unique."),

  CustomerServiceJobNotFound(20081L, "Customer Service Job Not Found."),

  CustomerCardNotFound(20091L, "Customer Card Not Found."),

  MsgUnsupportedEncoding(30001L, "Message Unsupported Encoding"),
  MsgIOException(30002L, "Message IO Exception"),
  InterruptedException(30003L, "Interrupted Exception"),
  DocumentException(30004L, "Document Exception"),

  WholeSalerShopIdNotFound(40001L, "WholeSaler Shop Not Found."),
  //会员相关
  MemberNoNotFound(30005L,"MemberNo is empty "),

  NullException(1L, "Null pointer found."),
  EmptyException(2L, "Somewhere is empty."),
  IllegalArgument(3L, "parameter is illegal."),

  NotEnoughInventory(50001L, "Inventory Not Enough");


  private long code;
  private String message;

  BcgogoExceptionType(long code, String message) {
    this.code = code;
    this.message = message;
  }

  public long getCode() {
    return code;
  }

  public void setCode(long code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
