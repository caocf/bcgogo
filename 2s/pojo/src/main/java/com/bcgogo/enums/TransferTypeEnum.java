package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 下午1:23
 * To change this template use File | Settings | File Templates.
 */
public enum TransferTypeEnum {
  BORROW_FROM_OTHER("从他人那借来"),
  BORROW_TO_OTHER("借给他人"),
  RETURN_FROM_OTHER("他人还给自己"),
  RETURN_TO_OTHER("自己还给他人");

  String transferType;

  TransferTypeEnum(String transferType){
    this.transferType = transferType;
  }

  public String getTransferType(){
    return this.transferType;
  }
}
