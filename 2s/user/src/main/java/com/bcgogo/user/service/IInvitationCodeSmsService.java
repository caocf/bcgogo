package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.dto.SmsDTO;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-21
 * Time: 上午10:28
 */
public interface IInvitationCodeSmsService {
  /**
   * bcgogo 邀请码促销短信
   * 为客户和供应商批量发送邀请码
   *
   * @param invitationCodeSendDTO {
   *                              sendTime;发送时间                            非必须
   *                              pageSize：每页大小                           非必须
   *                              }
   */
  void sendInvitationSmsForCustomersAndSuppliers(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception;

  /**
   * 为客户批量发送邀请码
   *
   * @param invitationCodeSendDTO {
   *                              shopId:店铺Id                                必须
   *                              senderType:短信发送类型(扣款方式)            必须
   *                              checkingDuplicated:是否需要检查邀请码重复    非必须 默认true
   *                              sendTime;发送时间                            非必须
   *                              pageSize：每页大小                           非必须
   *                              }
   * @throws Exception
   */
  void sendInvitationCodeSmsForCustomers(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception;

  /**
   * 为供应商批量发送邀请码
   *
   * @param invitationCodeSendDTO {
   *                              shopId:店铺Id                                必须
   *                              senderType:短信发送类型(扣款方式)            必须
   *                              checkingDuplicated:是否需要检查邀请码重复    非必须 默认true
   *                              sendTime;发送时间                            非必须
   *                              pageSize：每页大小                           非必须
   *                              }
   * @throws Exception
   */
  void sendInvitationCodeSmsForSuppliers(SmsDTO smsDTO,InvitationCodeSendDTO invitationCodeSendDTO) throws Exception;

  boolean reSendInvitationCodeSms(InvitationCodeDTO dto) throws Exception;

  /**
   * 为指定客户发送邀请码
   *
   * @param shopId     店铺Id
   * @param customerId 客户Id
   * @param senderType  发送短信方
   * @param smsSendScene
   */
  Result sendCustomerInvitationCodeSms(Long shopId, Long customerId, SenderType senderType, SmsSendScene smsSendScene) throws Exception;

  /**
   * @param shopId     店铺Id
   * @param supplierId 供应商Id
   * @param senderType 发送短信方
   * @param smsSendScene
   */
  Result sendSupplierInvitationCodeSms(Long shopId, Long supplierId, SenderType senderType, SmsSendScene smsSendScene) throws Exception;

  Long checkCustomerOrSupplierWithoutSendInvitationCodeSms(String customerOrSupplier,Long shopId);
}
