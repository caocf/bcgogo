package com.bcgogo.notification.service;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.SalesRemindEventDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.UserDTO;
import org.apache.velocity.VelocityContext;

import java.util.List;

public interface ISmsService {

  //发送短信给后台审核人员
  void sendRegistrationReminderForBackgroundAuditStaff(String shopName) throws SmsException;

  void registerMsgSendToCustomer(ShopDTO shopDTO, UserDTO userDTO, String password) throws SmsException;

  /**
   * 试用版 发送注册短信
   *
   * @param shopDTO
   * @param userDTO
   * @param password
   * @throws SmsException
   */
  void trialRegisterSmsSendToCustomer(ShopDTO shopDTO, UserDTO userDTO, String password) throws SmsException;

  void sendResetPasswordSMS(Long shopId, UserDTO userDTO) throws SmsException;

  /**
   * app user 发送短信
   * @throws SmsException
   */
  boolean sendResetAppUserPasswordSMS(String appUserNo, String password, String mobile, String name,AppUserType appUserType) throws SmsException;

  void sendChangeUserNoSMS(Long shopId, UserDTO userDTO) throws SmsException;

  void changePassword(Long shopId, UserDTO userDTO) throws SmsException;

  void changeMemberPassword(Long shopId, UserDTO userDTO) throws SmsException;

  void verificationCode(UserDTO userDTO, String vercode) throws SmsException;

  /**
   * 给客户发送邀请码
   *
   * @param sendDTO InvitationCodeSendDTO (发送方shopId  邀请码  手机号)
   * @throws SmsException
   */
  boolean sendInvitationCode(InvitationCodeSendDTO sendDTO) throws SmsException;

  /**
   * 批量发送邀请码
   *
   * @param invitationCodeSendDTOList InvitationCodeSendDTO List
   * @throws SmsException
   */
  void sendInvitationCodeByShop(SmsDTO smsDTO,SmsSendScene smsSendScene,List<InvitationCodeSendDTO> invitationCodeSendDTOList) throws SmsException;

  void sendMsgAllocatedAccount(Long shopId, UserDTO userDTO, String password) throws BcgogoException;

  void sendFinishMsgToCustomer(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO) throws SmsException;

  void sendDebtMsgToBoss(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO, String time) throws SmsException;

  void sendDebtMsgToBoss(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO, SalesOrderDTO salesOrderDTO, List<SalesRemindEventDTO> salesRemindEventDTOs, String time) throws SmsException;

  void sendSalesOrderCustomerCheapMsgToBoss(SalesOrderDTO salesOrderDTO, Long shopId, ShopDTO shopDTO, String time) throws Exception;

  void sendSalesOrderCustomerDebtMsgToBoss(SalesOrderDTO salesOrderDTO, Long shopId, ShopDTO shopDTO, String time, Long payTime) throws SmsException;

  void sendCheapMsgToBoss(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO, String time) throws SmsException;

  void sendOwedInfoMessageToBoss(double totalAmount, double owedAmount, String name, String phone, Long shopId, ShopDTO shopDTO, String[] licenseNosArray, double payedAmountOld, String huankuanTime, String dateFormat);

  void sendCheapInfoMessageToBoss(double totalAmount, double payedAmount, double owedAmount, String name, String phone, Long shopId, ShopDTO shopDTO, String[] licenseNosArray, double payedAmountOld, String dateFormat) throws BcgogoException;

  String sendMemberMsgToCardOwner(CustomerDTO customerDTO, ShopDTO shopDTO, VelocityContext context) throws Exception;

  String sendMemberCardBuyMsg(MemberCardOrderDTO memberCardOrderDTO, ShopDTO shopDTO, VelocityContext context) throws Exception;

  CustomerRemindSms sendCustomerServiceRemindMessage(CustomerRemindSms customerRemindSms) throws BcgogoException;

  List<MessageTemplateDTO> searchMessageTemplate(String type, Long shopId, Pager pager);

  int countMessageTemplate(String type, Long shopId, int pageNo);

  void saveMsgTemplate(MessageTemplateDTO msgTemplateDTO);

  void updateMsgTemplate(MessageTemplateDTO msgTemplateDTO);

  /**
   * 销售单拒绝短信
   * 您的采购订单{orderNumber}已经被供应商接受，正在备货中！
   *
   * @param receiverId  接受方shopId
   * @param senderId    发送方shopId
   * @param orderNumber 单据号
   * @throws SmsException 短信异常
   */
  void salesAcceptedSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException;

  /**
   * 销售单拒绝短信
   * 您的采购订单{采购单据号}已经被供应商拒绝，如有问题请与供应商联系！
   *
   * @param receiverId  接受方shopId
   * @param senderId    发送方shopId
   * @param orderNumber 单据号
   * @throws SmsException 短信异常
   */
  void salesRefuseSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException;

  /**
   * 备货中作废短信
   * 您的采购订单{采购单据号}已经被供应商销售终止，如有问题请与供应商联系！
   *
   * @param receiverId  接受方shopId
   * @param senderId    发送方shopId
   * @param orderNumber 单据号
   * @throws SmsException 短信异常
   */
  void stockingCancelSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException;

  /**
   * 已发货作废短信
   * 您的采购订单{采购单据号}已经被供应商销售终止，如有问题请与供应商联系！
   *
   * @param receiverId  接受方shopId
   * @param senderId    发送方shopId
   * @param orderNumber 单据号
   * @throws SmsException 短信异常
   */
  void shippedCancelSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException;

  /**
   * 退货单接受短信
   * 您的入库退货单{入库退货单据号}已经被供应商接受，可以继续做结算操作！
   *
   * @param receiverId  接受方shopId
   * @param senderId    发送方shopId
   * @param orderNumber 单据号
   * @throws SmsException 短信异常
   */
  void returnsAcceptedSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException;

  /**
   * 退货单拒绝短信
   * 您的入库退货单{入库退货单据号}已经被供应商拒绝，如有问题请与供应商联系！
   *
   * @param receiverId  接受方shopId
   * @param senderId    发送发shopId
   * @param orderNumber 单据号
   * @throws SmsException 短信异常
   */
  void returnsRefuseSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException;

  String sendMemberCardRenewMsg(MemberCardOrderDTO memberCardOrderDTO, ShopDTO shopDTO, VelocityContext context) throws Exception;

  public void sendBcgogoOrderSms(ShopDTO shopDTO, BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO) throws SmsException;

}
