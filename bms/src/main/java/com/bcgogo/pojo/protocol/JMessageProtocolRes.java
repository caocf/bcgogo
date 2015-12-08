//package com.bcgogo.pojo.protocol;
//
///**
// * Created by IntelliJ IDEA.
// * Author: ndong
// * Date: 2015-6-4
// * Time: 11:11
// */
//
//import com.bcgogo.pojo.constants.Constant;
//import com.bcgogo.pojo.enums.ProtocolType;
//
///**
// * 消息协议-响应
// *
// * @author Simple
// */
//public class JMessageProtocolRes extends MinaProtocol {
//
//  private int code;// 结果码
//
//  private String msg;// 响应内容
//
//  @Override
//  public byte[] getProtocol() {
//    return null;
//  }
//
//  @Override
//  public ProtocolType getType() {
//    return ProtocolType.MIRROR;
//  }
//
//  public void setResultCode(byte resultCode) {
//    this.resultCode = resultCode;
//  }
//
//  public byte getResultCode() {
//    return resultCode;
//  }
//
//  public void setContent(String content) {
//    this.content = content;
//  }
//
//  public String getContent() {
//    return content;
//  }
//
//}
