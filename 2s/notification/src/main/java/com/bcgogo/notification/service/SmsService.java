package com.bcgogo.notification.service;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.enums.PasswordValidateStatus;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.*;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.Sms;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderDTO;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringWriter;
import java.util.*;

/**
 * 短信 内容模板
 * author：zhangjuntao
 */
@Component
public class SmsService implements ISmsService {
  private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);
  private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

  @Autowired
  private NotificationDaoManager notificationDaoManager;

  static {
    DOCUMENT_BUILDER_FACTORY.setIgnoringElementContentWhitespace(true);
    DOCUMENT_BUILDER_FACTORY.setValidating(true);
  }

  private INotificationService notificationService;

  public INotificationService getNotificationService() {
    if (notificationService == null) notificationService = ServiceManager.getService(INotificationService.class);
    return notificationService;
  }

  @Override
  public void sendRegistrationReminderForBackgroundAuditStaff(String shopName) throws SmsException {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    String mobiles = getConfigService().getConfig("BACKGROUND_AUDIT_STAFF_MOBILES", ShopConstant.BC_SHOP_ID);
    MessageTemplate msgTemplate;
    String content = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.registrationReminderForBackgroundAuditStaff);
    //有新的客户{shopName}待审核，请尽快处理！
    if (msgTemplate == null) {
      throw new SmsException("注册短信:registrationReminderForBackgroundAuditStaff is null,please import data of notification_message_template！");
    }
    content = msgTemplate.getContent();
    if (StringUtils.isNotBlank(content)) {
      content = msgTemplate.getContent();
      content = replace(content, SmsConstant.MsgTemplateContentConstant.shopName, shopName);
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", mobiles, content);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setSender(SenderType.bcgogo);  //实时发送  不需要 验证金额
      smsJobDTO.setContent(content);
      smsJobDTO.setReceiveMobile(mobiles);
      smsJobDTO.setShopId(ShopConstant.BC_SHOP_ID);
      notificationService.sendSmsSync(smsJobDTO);
    }
  }

  public void registerMsgSendToCustomer(ShopDTO shopDTO, UserDTO userDTO, String password) throws SmsException {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.registerMsgSendToCustomer);
    //<{shortName}><{storeManager}>您好!感谢使用苏州统购信息科技有限公司的一发软件,为您开通的用户名：{userNo}和密码{password},祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。
    if (msgTemplate == null) {
      throw new SmsException("注册短信:registerMsgSendToCustomer is null,please import data of notification_message_template！");
    }
    sendStr = msgTemplate.getContent();
    if (sendStr != null && !"".equals(sendStr)) {
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shortName, StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.storeManager, shopDTO.getStoreManager());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.password, password);
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, userDTO.getUserNo());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setSender(SenderType.bcgogo);  //实时发送  不需要 验证金额
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setName(shopDTO.getName());
      smsJobDTO.setReceiveMobile(shopDTO.getMobile());
      smsJobDTO.setShopId(ShopConstant.BC_SHOP_ID);
      notificationService.sendSmsSync(smsJobDTO);
    }
  }

  @Override
  public void trialRegisterSmsSendToCustomer(ShopDTO shopDTO, UserDTO userDTO, String password) throws SmsException {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.trialRegisterSmsSendToCustomer);
    //<{shortName}><{storeManager}>您好!感谢使用一发软件,您的用户名{userNo}密码为{password},免费试用期为30天，祝您使用愉快! 如有疑问请拨打客服电话:0512-66733331
    if (msgTemplate == null) {
      throw new SmsException("注册短信:trialRegisterSmsSendToCustomer is null,please import data of notification_message_template！");
    }
    sendStr = msgTemplate.getContent();
    if (sendStr != null && !"".equals(sendStr)) {
      String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shortName, shopName);
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.storeManager, shopDTO.getStoreManager());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.password, password);
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, userDTO.getUserNo());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setSender(SenderType.bcgogo);  //实时发送  不需要 验证金额
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setName(shopName);
      smsJobDTO.setReceiveMobile(shopDTO.getMobile());
      smsJobDTO.setShopId(ShopConstant.BC_SHOP_ID);
      notificationService.sendSmsSync(smsJobDTO);
    }
  }


  @Override
  public void sendResetPasswordSMS(Long shopId, UserDTO userDTO) throws SmsException {
    if (userDTO == null || StringUtils.isBlank(userDTO.getMobile())) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.resetPassword);
//    您好!一发软件用户名{userNo}对应密码修改为{newpassword},如有问题请拨打客服电话:0512-66733331与我们联系。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "resetPassword is null,please import data of notification_message_template");
      return;
    }
    sendStr = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendStr)) {
      ShopDTO shopDTO = getConfigService().getShopById(shopId);
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.newpassword, userDTO.getPasswordWithoutEncrypt());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, userDTO.getUserNo());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(shopId);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(shopDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_CHANGE_PWD);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setName(userDTO.getName());
      if(ServiceManager.getService(IShopBalanceService.class).isSMSArrearage(shopId)) {
        smsJobDTO.setSender(SenderType.bcgogo);
      } else {
        smsJobDTO.setSender(SenderType.Shop);
      }
      notificationService.sendSmsSync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    }
  }

  @Override
  public boolean sendResetAppUserPasswordSMS(String appUserNo, String password, String mobile, String name,AppUserType appUserType) throws SmsException {
    if (StringUtils.isBlank(password) || StringUtils.isBlank(appUserNo) || !RegexUtils.isMobile(mobile))
      return false;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.appResetPassword);
    //您好!行车一键通用户名{userNo}对应密码修改为{newpassword},如有问题请拨打客服电话:0512-66733331与我们联系。
    if (msgTemplate == null || StringUtils.isBlank(msgTemplate.getContent())) {
      LOG.warn("查询短信: " + "resetPassword is null,please import data of notification_message_template");
      return false;
    }
    String sendStr = msgTemplate.getContent();
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.newpassword, password);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, appUserType == AppUserType.BLUE_TOOTH ? appUserNo : mobile);
    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", mobile, sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(ShopConstant.BC_SHOP_ID);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setReceiveMobile(mobile);
    smsJobDTO.setType(SmsConstant.SMS_TYPE_CHANGE_PWD);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setName(name);
    smsJobDTO.setSender(SenderType.bcgogo);
    notificationService.sendSmsSync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", mobile, sendStr);
    return true;
  }

  @Override
  public void sendChangeUserNoSMS(Long shopId, UserDTO userDTO) throws SmsException {
    if (userDTO == null || StringUtils.isBlank(userDTO.getMobile())) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.changeUserNo);
//    您好!一发软件用户名{userNo}对应密码修改为{newpassword},如有问题请拨打客服电话:0512-66733331与我们联系。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "changeUserNo is null,please import data of notification_message_template");
      return;
    }
    sendStr = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendStr)) {
      ShopDTO shopDTO = getConfigService().getShopById(shopId);
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.newpassword, userDTO.getPasswordWithoutEncrypt());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, userDTO.getUserNo());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(shopId);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(shopDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_CHANGE_USER_NO);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setName(userDTO.getName());
      if(ServiceManager.getService(IShopBalanceService.class).isSMSArrearage(shopId)) {
        smsJobDTO.setSender(SenderType.bcgogo);
      } else {
        smsJobDTO.setSender(SenderType.Shop);
      }
      notificationService.sendSmsSync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    }
  }


  //修改密码
  public void changePassword(Long shopId, UserDTO userDTO) throws SmsException {
    if (userDTO == null || StringUtils.isBlank(userDTO.getMobile())) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.changePassword);
    //{name}：您好！您的密码已经修改：用户名：<{userNo}>，密码:<{newpassword}>。祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "changePassword is null,please import data of notification_message_template");
      return;
    }
    sendStr = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendStr)) {
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.newpassword, userDTO.getPasswordWithoutEncrypt());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.name, userDTO.getName());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, userDTO.getUserNo());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(shopId);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(userDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_CHANGE_PWD);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setName(userDTO.getName());
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsAsync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
    }
  }

  //修改密码
  public void changeMemberPassword(Long shopId, UserDTO userDTO) throws SmsException {
    if (userDTO == null || StringUtils.isBlank(userDTO.getMobile())) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.changeMemberPassword);
    //{name}会员：您好！您的会员卡密码已经修改：新密码:<{newpassword}>。如有任何问题请与我们的工作人员联系。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "changeMemberPassword is null,please import data of notification_message_template");
      return;
    }
    sendStr = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendStr)) {
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.newpassword, userDTO.getPasswordWithoutEncrypt());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.name, userDTO.getName());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(shopId);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(userDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_CHANGE_PWD);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setName(userDTO.getName());
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsAsync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
    }
  }

  //验证码短信 verification code
  public void verificationCode(UserDTO userDTO, String vercode) throws SmsException {
    if (userDTO == null || StringUtils.isBlank(userDTO.getMobile())) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.verificationCode);
    //您好!您的验证码为:<{vercode}>。祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "verificationCode is null,please import data of notification_message_template");
      return;
    }
    sendStr = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendStr)) {
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.vercode, vercode);
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(userDTO.getShopId());
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(userDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_VERIFICATION);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsSync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
    }
  }

  @Override
  public boolean sendInvitationCode(InvitationCodeSendDTO sendDTO) throws SmsException {
    ISmsSendingTimesService smsSendingTimesService = ServiceManager.getService(ISmsSendingTimesService.class);
    if (sendDTO.getShopId() == null || StringUtils.isBlank(sendDTO.getCode())) {
      LOG.warn("send invitation code failed,because of shop:{} or invitation code:{} is null.", sendDTO.getShopId(), sendDTO.getTransformedCode());
      return false;
    }
    String customerInviteCustomerRegUrl = getConfigService().getConfig("CustomerRegister", ShopConstant.BC_SHOP_ID);
    String customerInviteSupplierRegUrl = getConfigService().getConfig("SupplierRegister", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(customerInviteCustomerRegUrl) || StringUtils.isBlank(customerInviteSupplierRegUrl)) {
      LOG.error("发送邀请短信错误。无法取得系统配置的注册URL!");
      return false;
    }
    MessageTemplate msgTemplate;
    String sendContent = "";
    String regUrl = "";
    if (sendDTO.isCustomerSmsInvitationCodeTemplate()) {
      //我在用一发软件,这是汽车行业最好的管理软件,了解视频:XXX.bcgogo.com注册网址:regc.bcgogo.com邀请码(有效期10天):{invitationCode}
      msgTemplate = getNotificationService().getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.invitationCodeToCustomer);
      regUrl = customerInviteCustomerRegUrl;
    } else {
      //我在用一发软件,这是汽车行业最好的管理软件,了解视频:XXX.bcgogo.com注册网址:regs.bcgogo.com邀请码(有效期10天):{invitationCode}
      msgTemplate = getNotificationService().getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.invitationCodeToSupplier);
      regUrl = customerInviteSupplierRegUrl;
    }
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "invitation code is null,please import data of notification_message_template");
      return false;
    }

    sendContent = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendContent)) {
      ShopDTO shopDTO = getConfigService().getShopById(sendDTO.getShopId());
      if (shopDTO == null) {
        LOG.warn("get shop by id:{} is null.", sendDTO.getShopId());
        return false;
      }
      String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
      sendContent = msgTemplate.getContent();
      sendContent = replace(sendContent, SmsConstant.MsgTemplateContentConstant.invitationCode, sendDTO.getTransformedCode());
      sendContent = replace(sendContent, SmsConstant.MsgTemplateContentConstant.regUrl, regUrl);
//      sendContent = replace(sendContent, SmsConstant.MsgTemplateContentConstant.shopName, shopName);
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", sendDTO.getMobile(), sendContent);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      //todo  :需要写入模板中 做成配置
      smsJobDTO.setSmsChannel(SmsChannel.MARKETING);
      smsJobDTO.setSmsId(System.nanoTime());
      smsJobDTO.setShopId(sendDTO.getShopId());
      smsJobDTO.setContent(sendContent);
      smsJobDTO.setReceiveMobile(sendDTO.getMobile());
      smsJobDTO.setStartTime(sendDTO.getSendTime() != null ? sendDTO.getSendTime() : System.currentTimeMillis());
      smsJobDTO.setSender(sendDTO.getSender());
      smsJobDTO.setShopName(shopName);
      getNotificationService().sendSmsAsync(smsJobDTO);
      Set<String> mobiles = new HashSet<String>();
      mobiles.add(sendDTO.getMobile().trim());
      smsSendingTimesService.updateInvitationCodeSendingTimes(mobiles);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", sendDTO.getMobile(), sendContent);
    }
    return true;
  }

  public void sendInvitationCodeByShop(SmsDTO smsDTO,SmsSendScene smsSendScene,List<InvitationCodeSendDTO> invitationCodeSendDTOList) throws SmsException {
    String urlHeader = "http://";
    if (CollectionUtil.isEmpty(invitationCodeSendDTOList)) return;
    ISmsSendingTimesService smsSendingTimesService = ServiceManager.getService(ISmsSendingTimesService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    MessageTemplate customerSmsTemplate, supplierSmsTemplate;
    String content = "";
    //我在用一发软件,这是汽车行业最好的管理软件,了解视频:XXX.bcgogo.com注册网址:regc.bcgogo.com邀请码(有效期10天):{invitationCode}
    customerSmsTemplate = getNotificationService().getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.invitationCodeToCustomer);
    //我在用一发软件,这是汽车行业最好的管理软件,了解视频:XXX.bcgogo.com注册网址:regs.bcgogo.com邀请码(有效期10天):{invitationCode}
    supplierSmsTemplate = getNotificationService().getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.invitationCodeToSupplier);
    if (customerSmsTemplate == null || supplierSmsTemplate == null) {
      LOG.warn("查询短信: " + "invitation code is null,please import data of notification_message_template");
      return;
    }
    String customerInviteCustomerRegUrl = configService.getConfig("CustomerRegister", ShopConstant.BC_SHOP_ID);
    String customerInviteSupplierRegUrl = configService.getConfig("SupplierRegister", ShopConstant.BC_SHOP_ID);
    String systemInviteCustomerRegUrl = configService.getConfig("SystemCustomerRegister", ShopConstant.BC_SHOP_ID);
    String systemInviteSupplierRegUrl = configService.getConfig("SystemSupplierRegister", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(customerInviteCustomerRegUrl) || StringUtils.isBlank(customerInviteSupplierRegUrl) || StringUtils.isBlank(systemInviteCustomerRegUrl)
        || StringUtils.isBlank(systemInviteSupplierRegUrl)) {
      LOG.error("发送邀请短信错误。无法取得系统配置的注册URL!");
      return;
    }
    List<SmsJobDTO> smsJobDTOs = new ArrayList<SmsJobDTO>();
    SmsJobDTO smsJobDTO;
    Set<Long> shopIds = new HashSet<Long>();
    for (InvitationCodeSendDTO sendDTO : invitationCodeSendDTOList) {
      if (sendDTO.getShopId() == null || StringUtils.isBlank(sendDTO.getMobile()) || sendDTO.getSender() == null)
        continue;
      shopIds.add(sendDTO.getShopId());
    }
    if (CollectionUtil.isEmpty(shopIds)) return;
    Map<Long, ShopDTO> shopDTOMap = getShopService().getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    Set<String> mobiles = new HashSet<String>();
    ShopDTO shopDTO = null;
    String shopName = "";
    for (InvitationCodeSendDTO sendDTO : invitationCodeSendDTOList) {
      shopDTO = shopDTOMap.get(sendDTO.getShopId());
      if (sendDTO.getShopId() == null || StringUtils.isBlank(sendDTO.getMobile()) || sendDTO.getSender() == null
          || shopDTO == null)
        continue;

      if (sendDTO.isCustomerSmsInvitationCodeTemplate()) {
        content = customerSmsTemplate.getContent();
        if (sendDTO.getSender() == SenderType.bcgogo) {
          content = replace(content, SmsConstant.MsgTemplateContentConstant.regUrl, systemInviteCustomerRegUrl.replace(urlHeader, ""));
        } else if (sendDTO.getSender() == SenderType.Shop) {
          content = replace(content, SmsConstant.MsgTemplateContentConstant.regUrl, customerInviteCustomerRegUrl.replace(urlHeader, ""));
        }
      } else {
        content = supplierSmsTemplate.getContent();
        if (sendDTO.getSender() == SenderType.bcgogo) {
          content = replace(content, SmsConstant.MsgTemplateContentConstant.regUrl, systemInviteSupplierRegUrl.replace(urlHeader, ""));
        } else if (sendDTO.getSender() == SenderType.Shop) {
          content = replace(content, SmsConstant.MsgTemplateContentConstant.regUrl, customerInviteSupplierRegUrl.replace(urlHeader, ""));
        }
      }
      shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
      smsJobDTO = new SmsJobDTO();
      if (StringUtils.isNotBlank(sendDTO.getCode())) {
        content = replace(content, SmsConstant.MsgTemplateContentConstant.invitationCode, sendDTO.getTransformedCode());
      } else {
        content = replace(content, SmsConstant.MsgTemplateContentConstant.invitationCode, "");
      }
//      content = replace(content, SmsConstant.MsgTemplateContentConstant.shopName, shopDTO.getName());
      smsJobDTO.setContent(content);
      //todo :需要写入模板中 做成配置
      smsJobDTO.setSmsChannel(SmsChannel.MARKETING);
      smsJobDTO.setSmsId(System.nanoTime());
      smsJobDTO.setShopId(sendDTO.getShopId());
      smsJobDTO.setReceiveMobile(sendDTO.getMobile());
      if (sendDTO.getSendTime() == null) {
        LOG.warn("send time id null:[{}]", sendDTO.toString());
        continue;
      } else {
        smsJobDTO.setStartTime(sendDTO.getSendTime());
      }
//      smsJobDTO.setStartTime(sendDTO.getSendTime() != null ? sendDTO.getSendTime() : System.currentTimeMillis());
      smsJobDTO.setSender(sendDTO.getSender());
      smsJobDTO.setShopName(shopName);
      smsJobDTO.setSmsSendScene(sendDTO.getSmsSendScene());
      smsJobDTOs.add(smsJobDTO);
      mobiles.add(sendDTO.getMobile().trim());
    }
    if(smsDTO!=null){
      smsDTO.setContent(content);
      getNotificationService().saveOrUpdateSms(smsDTO);
      SmsIndexDTO smsIndexDTO=smsDTO.getSmsIndexDTO();
      if(smsIndexDTO!=null){
        smsIndexDTO.setContent(StringUtil.valueOf(smsIndexDTO.getContent())+content);
        getNotificationService().saveOrUpdateSmsIndex(smsDTO.getSmsIndexDTO());
      }
      for(SmsJobDTO jobDTO:smsJobDTOs){
        jobDTO.setSmsId(smsDTO.getId());
      }
    }
    getNotificationService().batchSendSmsAsync(smsJobDTOs);
    smsSendingTimesService.updateInvitationCodeSendingTimes(mobiles);
  }

  //分配账户 短信
  public void sendMsgAllocatedAccount(Long shopId, UserDTO userDTO, String password) throws BcgogoException {
    if (userDTO == null || StringUtils.isBlank(userDTO.getMobile())) return;

    if (!isMessageSwitchOn(shopId, MessageScene.AllOCATED_ACCOUNT_MSG)) return;

    INotificationService notificationService = ServiceManager.getService(INotificationService.class);

    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendAllocatedAccountMsg1);
    //{name}：您好!您已经成功分配了账号，用户名：<{userNo}>，密码:<{ password}>。祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。";
    if (msgTemplate == null) {
      LOG.error("查询短信: " + "sendAllocatedAccountMsg1 is null,please import data of notification_message_template");
      return;
    }
    if (msgTemplate == null) return;
    sendStr = msgTemplate.getContent();
    if (sendStr != null && !"".equals(sendStr)) {
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.userNo, userDTO.getUserNo());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.password, password);
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.name, userDTO.getName());
      LOG.debug("发送短信: " + "sendAllocatedAccountMsg1," + msgTemplate.getContent());
      LOG.debug("NotificationService: " + "sendSmsAsync()");
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(shopId);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setName(userDTO.getName());
      smsJobDTO.setReceiveMobile(userDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_NEW_USER);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsAsync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", userDTO.getMobile(), sendStr);
    }
  }

  //给顾客发送完工短信短信
  public void sendFinishMsgToCustomer(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO) throws SmsException {
    if (repairOrderDTO == null || StringUtils.isBlank(repairOrderDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.FINISH_MSG)) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    if (shopDTO.getLandline() != null) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendFinishMsg1);
      if (msgTemplate == null) {
        LOG.warn("查询短信: " + "sendFinishMsg1 is null,please import data of notification_message_template");
        return;
      }
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shopLandline, shopDTO.getLandline());
      //尊敬的{licenceNo}车主{customer}您好！感谢您对本店的照顾，您的爱车已施工完毕，预计金额{receivable}元。麻烦您尽快前来提车！{shopName}店敬启。详请咨询{shopLandline}。
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shopLandline, shopDTO.getLandline());
    } else {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendFinishMsg2);
      if (msgTemplate == null) {
        LOG.warn("查询短信: " + "sendFinishMsg2 is null,please import data of notification_message_template");
        return;
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("查询短信: " + "sendFinishMsg2," + msgTemplate.getContent());
      }
      //尊敬的{licenceNo}车主{customer}您好！感谢您对本店的照顾，您的爱车已施工完毕，预计金额{receivable}元。麻烦您尽快前来提车！{shopName}店敬启。
      sendStr = msgTemplate.getContent();
    }
    String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
//    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shopName, shopName);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.customer, repairOrderDTO.getCustomerName());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(repairOrderDTO.getTotal()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.licenceNo, repairOrderDTO.getLicenceNo());
    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setName(repairOrderDTO.getCustomerName());
    smsJobDTO.setVehicleLicense(repairOrderDTO.getLicenceNo());
    smsJobDTO.setReceiveMobile(repairOrderDTO.getMobile());
    smsJobDTO.setType(SmsConstant.SMS_TYPE_REPAIR_FINISH);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setShopName(shopName);
    notificationService.sendSmsAsync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);

  }

  //出现欠款 给老板发送短信
  public void sendDebtMsgToBoss(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO, String time) throws SmsException {
    if (shopDTO == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.BOSS_DEBT_MSG)) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg1);
    //欠款备忘：{memoTime}，车辆{licenceNo}消费项目：{services};材料：{productNameAndCounts},应收款{receivable}元，实收{actualCollection}元，欠款{debt}元，预计还款日：{repaymentDate}。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "sendDebtMsg1 is null,please import data of notification_message_template");
      return;
    }
    String sendStr = msgTemplate.getContent();
    String serviceStr = combineContentService(repairOrderDTO);
    if (StringUtils.isBlank(serviceStr)) {
      serviceStr = "无";
    }
    String productNameAndCountStr = combineContentProduct(repairOrderDTO);
    if (StringUtils.isBlank(productNameAndCountStr)) {
      productNameAndCountStr = "无";
    }
    String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.licenceNo, repairOrderDTO.getLicenceNo());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.services, serviceStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.productNameAndCounts, productNameAndCountStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(repairOrderDTO.getTotal()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(repairOrderDTO.getSettledAmount()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.debt, String.valueOf(repairOrderDTO.getDebt()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.repaymentDate, repairOrderDTO.getHuankuanTime());
    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setReceiveMobile(shopDTO.getMobile());
    smsJobDTO.setType(SmsConstant.SMS_TYPE_DISCOUNT_ALERT);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setShopName(shopName);
    notificationService.sendSmsAsync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);

  }

  //出现欠款 给老板发送短信
  public void sendDebtMsgToBoss(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO, SalesOrderDTO salesOrderDTO, List<SalesRemindEventDTO> salesRemindEventDTOs, String time) throws SmsException {
    if (shopDTO == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.BOSS_DEBT_MSG)) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
    if (salesRemindEventDTOs.size() > 0 && null != salesRemindEventDTOs.get(0).getEventContent()) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg2);
      //欠款备忘：{memoTime}，姓名：{customer}，手机：{mobile}；材料：{productNameAndCounts}，应收款{receivable}元，实收{actualCollection}元，欠款{debt}元。
      if (msgTemplate == null) {
        LOG.warn("查询短信: " + "sendDebtMsg2 is null,please import data of notification_message_template");
        return;
      }
      sendStr = msgTemplate.getContent();
    } else {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg3);
      //欠款备忘：{memoTime}，姓名：{customer}，手机：{mobile}；材料：{productNameAndCounts}，应收款{receivable}元，实收{actualCollection}元，欠款{debt}元，预计还款日：{repaymentDate}。
      if (msgTemplate == null) {
        LOG.warn("查询短信: " + "sendDebtMsg3 is null,please import data of notification_message_template");
        return;
      }
      sendStr = msgTemplate.getContent();
      sendStr += DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_CN, salesRemindEventDTOs.get(0).getEventContent());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.repaymentDate, repairOrderDTO.getHuankuanTime());
    }
    String productNameAndCountStr = combineContentProduct(repairOrderDTO);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.customer, salesOrderDTO.getCustomer());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.mobile, salesOrderDTO.getMobile());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.productNameAndCounts, productNameAndCountStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(repairOrderDTO.getTotal()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(repairOrderDTO.getSettledAmount()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.debt, String.valueOf(repairOrderDTO.getDebt()));
    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setReceiveMobile(shopDTO.getMobile());
    smsJobDTO.setType(SmsConstant.SMS_TYPE_DISCOUNT_ALERT);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setShopName(shopName);
    notificationService.sendSmsAsync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);

  }

  //出现欠款 给老板发送短信
  public void sendSalesOrderCustomerDebtMsgToBoss(SalesOrderDTO salesOrderDTO, Long shopId, ShopDTO shopDTO, String time, Long payTime) throws SmsException {
    if (shopDTO == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.BOSS_DEBT_MSG)) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "", payTimeStr = "";
    String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
    if (payTime == null) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg3);
      //欠款备忘：{memoTime}，姓名：{customer}，手机：{mobile}；材料：{productNameAndCounts}，应收款{receivable}元，实收{actualCollection}元，欠款{debt}元。
      if (msgTemplate == null) {
        LOG.warn("查询短信: " + "sendDebtMsg2 is null,please import data of notification_message_template！");
        return;
      }
      sendStr = msgTemplate.getContent();
    } else {
      payTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_CN, payTime);
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg2);
      //欠款备忘：{memoTime}，姓名：{customer}，手机：{mobile}；材料：{productNameAndCounts}，应收款{receivable}元，实收{actualCollection}元，欠款{debt}元，预计还款日：{repaymentDate}。
      if (msgTemplate == null) {
        LOG.warn("查询短信: " + "sendDebtMsg3 is null,please import data of notification_message_template！");
        return;
      }
      sendStr = msgTemplate.getContent();
      sendStr = sendStr.replace(SmsConstant.MsgTemplateContentConstant.repaymentDate, payTimeStr);
    }
    String productNameAndCountStr = combineContentProduct(salesOrderDTO);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.customer, salesOrderDTO.getCustomer());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.mobile, salesOrderDTO.getMobile());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.productNameAndCounts, productNameAndCountStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(salesOrderDTO.getTotal()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(salesOrderDTO.getSettledAmount()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.debt, String.valueOf(salesOrderDTO.getDebt()));
    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setReceiveMobile(shopDTO.getMobile());
    smsJobDTO.setType(SmsConstant.SMS_TYPE_DEBT_ALERT);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setShopName(shopName);
    notificationService.sendSmsAsync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);

  }

  //出现折扣信息 给老板发送短信  针对顾客姓名
  public void sendSalesOrderCustomerCheapMsgToBoss(SalesOrderDTO salesOrderDTO, Long shopId, ShopDTO shopDTO, String time) throws SmsException {
    if (!isMessageSwitchOn(shopId, MessageScene.DISCOUNT_MSG)) return;
    if (shopId == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (shopDTO == null) {
      shopDTO = getConfigService().getShopById(shopId);
    }
    if (shopDTO == null) throw new SmsException("shopDTO nullPointException!");
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDiscountMsg1);
    //折扣备忘：{memoTime}，姓名：{customer}，手机：{mobile}；材料：{productNameAndCounts}，应收款{receivable}元，实收{actualCollection}元，折扣{discount}元。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "sendDiscountMsg1 is null,please import data of notification_message_template");
      return;
    }
    String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
    sendStr = msgTemplate.getContent();
    String productNameAndCountStr = combineContentProduct(salesOrderDTO);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shopLandline, StringUtils.isBlank(shopDTO.getLandline()) ? "无" : shopDTO.getLandline());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.customer, salesOrderDTO.getCustomer());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.mobile, salesOrderDTO.getMobile());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.productNameAndCounts, productNameAndCountStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(NumberUtil.round(salesOrderDTO.getTotal(), NumberUtil.MONEY_PRECISION)));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(NumberUtil.round(salesOrderDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION)));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.discount, String.valueOf(NumberUtil.round(salesOrderDTO.getTotal() - (salesOrderDTO.getSettledAmount() + salesOrderDTO.getDebt()), NumberUtil.MONEY_PRECISION)));
    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setReceiveMobile(shopDTO.getMobile());
    smsJobDTO.setType(SmsConstant.SMS_TYPE_DISCOUNT_ALERT);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setShopName(shopName);
    notificationService.sendSmsAsync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
  }

  //出现折扣信息 给老板发送短信 针对车辆
  public void sendCheapMsgToBoss(RepairOrderDTO repairOrderDTO, Long shopId, ShopDTO shopDTO, String time) throws SmsException {
    if (shopDTO == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.DISCOUNT_MSG)) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDiscountMsg2);
    //欠款备忘：{memoTime}，车辆{licenceNo}消费项目：{services};材料：{productNameAndCounts},应收款{receivable}元，实收{actualCollection}元，折扣{discount}元。
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "sendDiscountMsg2 is null,please import data of notification_message_template");
      return;
    }
    String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
    String sendStr = msgTemplate.getContent();
    String serviceStr = combineContentService(repairOrderDTO);
    if (StringUtils.isBlank(serviceStr)) {
      serviceStr = "无";
    }
    String productNameAndCountStr = combineContentProduct(repairOrderDTO);
    if (StringUtils.isBlank(productNameAndCountStr)) {
      productNameAndCountStr = "无";
    }
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.licenceNo, repairOrderDTO.getLicenceNo());
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.services, serviceStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.productNameAndCounts, productNameAndCountStr);
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(repairOrderDTO.getTotal()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(repairOrderDTO.getSettledAmount()));
    sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.discount, String.valueOf(NumberUtil.round(repairOrderDTO.getTotal() - (repairOrderDTO.getSettledAmount() + repairOrderDTO.getDebt()), NumberUtil.MONEY_PRECISION)));

    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setContent(sendStr);
    smsJobDTO.setReceiveMobile(shopDTO.getMobile());
    smsJobDTO.setType(SmsConstant.SMS_TYPE_DISCOUNT_ALERT);
    smsJobDTO.setStartTime(System.currentTimeMillis());
    smsJobDTO.setSender(SenderType.Shop);
    smsJobDTO.setShopName(shopName);
    notificationService.sendSmsAsync(smsJobDTO);
    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
  }

  //出现欠款 给老板发送短信
  public void sendOwedInfoMessageToBoss(double totalAmount, double owedAmount, String name, String phone, Long shopId, ShopDTO shopDTO, String[] licenseNosArray, double payedAmountOld, String huankuanTime, String dateFormat) {
    if (shopDTO == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.BOSS_DEBT_MSG)) return;
    try {
      long curTime = System.currentTimeMillis();
      String time = DateUtil.convertDateLongToDateString(dateFormat, curTime);
      if (owedAmount > 0) {
        INotificationService notificationService = ServiceManager.getService(INotificationService.class);
        MessageTemplate msgTemplate;
        String sendStr = "";
        if (!StringUtil.judgeSpacesInStrArray(licenseNosArray)) {
          msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg5);
          //欠款备忘：{memoTime}，姓名：{customer}，电话：{phone}，应收款{receivable}元，实收{actualCollection}元，欠款{debt}元，预计还款日：{repaymentDate}。
          if (msgTemplate == null) {
            LOG.warn("查询短信: " + "sendDebtMsg5 is null,please import data of notification_message_template");
            return;
          }
          sendStr = msgTemplate.getContent();
          sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.licenceNo, licenseNosArray[0]);
        } else {
          msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDebtMsg4);
          //欠款备忘：{memoTime}，车辆{licenceNo}，应收款{receivable}元，实收{actualCollection}元，欠款{debt}元，预计还款日：{repaymentDate}。
          if (msgTemplate == null) {
            LOG.warn("查询短信: " + "sendDebtMsg4 is null,please import data of notification_message_template");
            return;
          }
          sendStr = msgTemplate.getContent();
          sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.customer, name);
          sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.phone, phone);
        }
        String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(totalAmount));
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(payedAmountOld));
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.debt, String.valueOf(owedAmount));
        if (StringUtils.isBlank(huankuanTime)) {
          huankuanTime = "无";
        }
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.repaymentDate, huankuanTime);
        LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
        SmsJobDTO smsJobDTO = new SmsJobDTO();
        smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
        smsJobDTO.setShopId(shopId);
        smsJobDTO.setContent(sendStr);
        smsJobDTO.setReceiveMobile(shopDTO.getMobile());
        smsJobDTO.setType(SmsConstant.SMS_TYPE_DEBT_ALERT);
        smsJobDTO.setStartTime(System.currentTimeMillis());
        smsJobDTO.setSender(SenderType.Shop);
        smsJobDTO.setShopName(shopName);
        notificationService.sendSmsAsync(smsJobDTO);
        LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);

      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  //出现折扣信息 给老板发送短信
  public void sendCheapInfoMessageToBoss(double totalAmount, double payedAmount, double owedAmount, String name, String phone,
                                         Long shopId, ShopDTO shopDTO, String[] licenseNosArray, double payedAmountOld, String dateFormat) throws BcgogoException {
    if (shopDTO == null || StringUtils.isBlank(shopDTO.getMobile())) return;
    if (!isMessageSwitchOn(shopId, MessageScene.DISCOUNT_MSG)) return;
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    long curTime = System.currentTimeMillis();
    String time = DateUtil.convertDateLongToDateString(dateFormat, curTime);
    MessageTemplate msgTemplate;
    String sendStr = "";
    if (totalAmount > payedAmount + owedAmount) {
      if (!StringUtil.judgeSpacesInStrArray(licenseNosArray)) {
        msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDiscountMsg4);
        //欠款备忘：{memoTime}，车辆{licenceNo}，应收款{receivable}元，实收{actualCollection}元，折扣{discount}。
        if (msgTemplate == null) {
          LOG.warn("查询短信: " + "sendDiscountMsg4 is null,please import data of notification_message_template");
          return;
        }
        sendStr = msgTemplate.getContent();
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.licenceNo, licenseNosArray[0]);
      } else {
        msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.sendDiscountMsg3);
        //欠款备忘：{memoTime}，姓名：{customer}，电话：{phone}，应收款{receivable}元，实收{actualCollection}元，折扣{discount}。
        if (msgTemplate == null) {
          LOG.warn("查询短信: " + "sendDiscountMsg3 is null,please import data of notification_message_template");
          return;
        }
        sendStr = msgTemplate.getContent();
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.customer, name);
        sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.phone, phone);
      }
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.memoTime, time);
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.receivable, String.valueOf(totalAmount));
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.actualCollection, String.valueOf(payedAmountOld));
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.discount, String.valueOf(NumberUtil.round(totalAmount - payedAmount - owedAmount, NumberUtil.MONEY_PRECISION)));
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
      String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(shopId);
      smsJobDTO.setShopName(shopName);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setVehicleLicense(licenseNosArray[0]);
      smsJobDTO.setReceiveMobile(shopDTO.getMobile());
      smsJobDTO.setType(SmsConstant.SMS_TYPE_DEBT_ALERT);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      smsJobDTO.setName(name);
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsAsync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
    }
  }

  @Override
  public String sendMemberCardBuyMsg(MemberCardOrderDTO memberCardOrderDTO, ShopDTO shopDTO, VelocityContext context) throws Exception {
    if (memberCardOrderDTO == null || shopDTO == null || StringUtils.isBlank(memberCardOrderDTO.getMobile()) || context == null) {
      return null;
    }
    Long shopId = shopDTO.getId();
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.memberBuyCardMsg);
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "memberBuyCardMsg is null,please import data of notification_message_template");
      return null;
    }
    String passwordStr = memberCardOrderDTO.getMemberDTO().getPassword();
    if (StringUtils.isBlank(memberCardOrderDTO.getMemberDTO().getPassword())) {
      passwordStr = "无";
    } else if (TxnConstant.PWD_DEFAULT_RENDER_STR.equals(memberCardOrderDTO.getMemberDTO().getPassword())) {
      passwordStr = "密码未修改";
    }
    context.put(SmsConstant.VelocityMsgTemplateConstant.cardOwnerName, memberCardOrderDTO.getCustomerName());
    context.put(SmsConstant.VelocityMsgTemplateConstant.memberNo, memberCardOrderDTO.getMemberDTO().getMemberNo());
    context.put(SmsConstant.VelocityMsgTemplateConstant.password, passwordStr);
    context.put(SmsConstant.VelocityMsgTemplateConstant.shopName, shopDTO.getName());
    context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, memberCardOrderDTO.getMemberDTO().getBalance());
    if(memberCardOrderDTO.getReceivableDTO() != null){
      double consumePrice = NumberUtil.addition(memberCardOrderDTO.getReceivableDTO().getSettledAmount(),memberCardOrderDTO.getReceivableDTO().getDebt());
      context.put(SmsConstant.VelocityMsgTemplateConstant.consumePrice, NumberUtil.round(consumePrice, 2));
    }
    String myTemplateName = SmsConstant.MsgTemplateTypeConstant.memberBuyCardMsg;
    sendStr = msgTemplate.getContent();
    sendStr = generateSmsUsingVelocity(context, sendStr, myTemplateName);
    return sendStr;
//    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", memberCardOrderDTO.getMobile(), sendStr);
//    SmsJobDTO smsJobDTO = new SmsJobDTO();
//    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//    smsJobDTO.setShopId(shopId);
//    smsJobDTO.setContent(sendStr);
//    smsJobDTO.setReceiveMobile(memberCardOrderDTO.getMobile());
//    smsJobDTO.setType(SmsConstant.SMS_TYPE_BUY_CARD);
//    smsJobDTO.setStartTime(System.currentTimeMillis());
//    smsJobDTO.setName(memberCardOrderDTO.getCustomerName());
//    smsJobDTO.setSender(SenderType.Shop);
//    smsJobDTO.setShopName(StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname());
//    notificationService.sendSmsAsync(smsJobDTO);
//    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", memberCardOrderDTO.getMobile(), sendStr);
  }

  @Override
  public String sendMemberCardRenewMsg(MemberCardOrderDTO memberCardOrderDTO, ShopDTO shopDTO, VelocityContext context) throws Exception {
    if (memberCardOrderDTO == null || shopDTO == null || StringUtils.isBlank(memberCardOrderDTO.getMobile()) || context == null) {
      return null;
    }
    Long shopId = shopDTO.getId();
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.memberRenewCardMsg);
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "memberRenewCardMsg is null,please import data of notification_message_template");
      return null;
    }
    PasswordValidateStatus passwordStatus = memberCardOrderDTO.getMemberDTO().getPasswordStatus();
    boolean passwordChanged = false;
    if (passwordStatus == PasswordValidateStatus.VALIDATE) {
      if (StringUtils.isBlank(memberCardOrderDTO.getMemberDTO().getPassword()) || TxnConstant.PWD_DEFAULT_RENDER_STR.equals(memberCardOrderDTO.getMemberDTO().getPassword())) {
        passwordChanged = false;
      } else {
        passwordChanged = true;
      }
    } else {
      passwordChanged = false;
    }
    context.put(SmsConstant.VelocityMsgTemplateConstant.cardOwnerName, memberCardOrderDTO.getCustomerName());
    context.put(SmsConstant.VelocityMsgTemplateConstant.memberNo, memberCardOrderDTO.getMemberDTO().getMemberNo());
    context.put(SmsConstant.VelocityMsgTemplateConstant.password, memberCardOrderDTO.getMemberDTO().getPassword());
    context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, memberCardOrderDTO.getMemberDTO().getBalance());
    context.put(SmsConstant.VelocityMsgTemplateConstant.passwordChanged, passwordChanged);
    context.put(SmsConstant.VelocityMsgTemplateConstant.shopName, shopDTO.getName());
    if(memberCardOrderDTO.getReceivableDTO() != null){
      double consumePrice = NumberUtil.addition(memberCardOrderDTO.getReceivableDTO().getSettledAmount(),memberCardOrderDTO.getReceivableDTO().getDebt());
      context.put(SmsConstant.VelocityMsgTemplateConstant.consumePrice, NumberUtil.round(consumePrice, 2));
    }
    String myTemplateName = SmsConstant.MsgTemplateTypeConstant.memberRenewCardMsg;
    sendStr = msgTemplate.getContent();
    sendStr = generateSmsUsingVelocity(context, sendStr, myTemplateName);
    return sendStr;
//    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", memberCardOrderDTO.getMobile(), sendStr);
//    ServiceManager.getService(ISe)
//    SmsJobDTO smsJobDTO = new SmsJobDTO();
//    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//    smsJobDTO.setShopId(shopId);
//    smsJobDTO.setContent(sendStr);
//    smsJobDTO.setReceiveMobile(memberCardOrderDTO.getMobile());
//    smsJobDTO.setType(SmsConstant.SMS_TYPE_BUY_CARD);
//    smsJobDTO.setStartTime(System.currentTimeMillis());
//    smsJobDTO.setName(memberCardOrderDTO.getCustomerName());
//    smsJobDTO.setSender(SenderType.Shop);
//    smsJobDTO.setShopName(StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname());
//    notificationService.sendSmsAsync(smsJobDTO);
//    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", memberCardOrderDTO.getMobile(), sendStr);
  }

  /**
   * 结算后给持卡人发送会员信息短信, 此短信使用Velocity模板编写
   *
   * @param shopDTO
   * @throws SmsException
   */
  public String sendMemberMsgToCardOwner(CustomerDTO customerDTO, ShopDTO shopDTO, VelocityContext context) throws SmsException, Exception {
    if (customerDTO == null || shopDTO == null || StringUtils.isBlank(customerDTO.getMobile()) || context == null)
      return null;
    Long shopId = shopDTO.getId();
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.memberConsumeMsg);
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "memberConsumeMsg is null,please import data of notification_message_template");
      return null;
    }
    context.put(SmsConstant.VelocityMsgTemplateConstant.cardOwnerName, customerDTO.getName());
    String shopName = shopDTO.getShortname();   //店面名称
    String shopMobile = shopDTO.getMobile();
    //如果没有shopMobile 使用landline
    if (StringUtils.isBlank(shopMobile)) {
      shopMobile = shopDTO.getLandline();
    }
    if (StringUtils.isBlank(shopName)) {
      shopName = shopDTO.getName();
    }
//    context.put(SmsConstant.VelocityMsgTemplateConstant.shopName,shopName);
    context.put(SmsConstant.VelocityMsgTemplateConstant.shopMobile, shopMobile);
    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeDate, DateUtil.convertDateLongToDateString("MM月dd日", System.currentTimeMillis()));
    sendStr = msgTemplate.getContent();
    String myTemplateName = SmsConstant.MsgTemplateTypeConstant.memberConsumeMsg;

    sendStr = generateSmsUsingVelocity(context, sendStr, myTemplateName);
    return sendStr;
//    LOG.debug("短信开始发送:[mobile:{},][内容:{}]", customerDTO.getMobile(), sendStr);
//
//    SmsJobDTO smsJobDTO = new SmsJobDTO();
//    smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
//    smsJobDTO.setShopId(shopId);
//    smsJobDTO.setContent(sendStr);
//    smsJobDTO.setReceiveMobile(customerDTO.getMobile());
//    smsJobDTO.setType(SmsConstant.SMS_TYPE_REPAIR_FINISH);
//    smsJobDTO.setStartTime(System.currentTimeMillis());
//    smsJobDTO.setName(customerDTO.getName());
//    smsJobDTO.setSender(SenderType.Shop);
//    smsJobDTO.setShopName(StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname());
//    notificationService.sendSmsAsync(smsJobDTO);
//    LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", customerDTO.getMobile(), sendStr);

  }

  private String generateSmsUsingVelocity(VelocityContext context, String sendStr, String myTemplateName) throws Exception {
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
    ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
    ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
    ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
    ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
    try {
      ve.init();
    } catch (Exception e) {
      LOG.error("Velocity初始化时出错", e);
    }
    StringResourceRepository repo = StringResourceLoader.getRepository();
    repo.putStringResource(myTemplateName, sendStr);
    Template t = ve.getTemplate(myTemplateName, "UTF-8");
    StringWriter writer = new StringWriter();
    t.merge(context, writer);
    return writer.toString();
  }

  //组合发送内容 针对服务
  private String combineContentService(RepairOrderDTO repairOrderDTO) {
    String serviceStr = "";
    if(ArrayUtil.isEmpty(repairOrderDTO.getServiceDTOs())) return serviceStr;
    for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
      serviceStr += repairOrderServiceDTO.getService() + SmsConstant.MsgTemplateContentConstant.pauseMark;
    }
    serviceStr = StringUtil.subString(serviceStr);
    return serviceStr;
  }

  //组合发送内容 针对内容  维修美容
  private String combineContentProduct(RepairOrderDTO repairOrderDTO) {
    String productNameAndCountStr = "";
    if(ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs())) {
      return productNameAndCountStr;
    }
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
      productNameAndCountStr += repairOrderItemDTO.getProductName() + repairOrderItemDTO.getAmount() + SmsConstant.MsgTemplateContentConstant.ge;
    }
    productNameAndCountStr = StringUtil.subString(productNameAndCountStr);
    return productNameAndCountStr;
  }

  //组合发送内容 针对内容  销售单
  private String combineContentProduct(SalesOrderDTO salesOrderDTO) {
    String productNameAndCountStr = "";
    for (SalesOrderItemDTO salesOrderItemDTO : salesOrderDTO.getItemDTOs()) {
      productNameAndCountStr += salesOrderItemDTO.getProductName() + salesOrderItemDTO.getAmount() + SmsConstant.MsgTemplateContentConstant.ge;
    }
    productNameAndCountStr = StringUtil.subString(productNameAndCountStr);
    return productNameAndCountStr;
  }

  //发送 客户服务提醒
  public CustomerRemindSms sendCustomerServiceRemindMessage(CustomerRemindSms customerRemindSms) throws BcgogoException {
     if(customerRemindSms.getType()==null) return null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate = null;
    String content = "";
    String licenceNo = customerRemindSms.getLicenceNo();
    String name = customerRemindSms.getName();
    String year = customerRemindSms.getYear();
    String money = customerRemindSms.getMoney();
    String day = customerRemindSms.getDay();
    String mouth = customerRemindSms.getMonth();
    if (StringUtil.isEmpty(customerRemindSms.getAppointName())) {
      customerRemindSms.setAppointName("");
    }
    //组装发送title： 15672837223（张三）
    String title = customerRemindSms.getMobile() + "(" + name + ")";
    ShopDTO shopDTO = configService.getShopById(customerRemindSms.getShopId());
    String shopName = shopDTO.getName();   //店面名称
    String shopMobile = shopDTO.getMobile();
    //如果没有shopMobile 使用landline
    if (shopMobile == null || shopMobile.trim().equals("")) {
      shopMobile = shopDTO.getLandline();
    }
    String userName = customerRemindSms.getUserName();
    //判断发送类型
    int type = customerRemindSms.getType();
    if (type == SmsConstant.CustomerRemindType.guarantee) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.customerRemindGuarantee);
      //尊敬的{licenceNo}车主{carOwnerName}您好！感谢您对本店的一贯照顾，您的爱车保险将于今年{mouth}月{day}日到期，本店可协助您代办保险业务，有多家保险公司产品供您选择，详请咨询{shopMobile},{shopName}店敬启。
    } else if (type == SmsConstant.CustomerRemindType.validateCar) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.customerRemindValidateCar);
      //尊敬的{licenceNo}车主{carOwnerName}您好！感谢您对本店的一贯照顾，您的爱车将于今年{mouth}月{day}日到期验车，本店可为您提供代办验车服务，详请咨询{shopMobile},{shopName}店敬启。
    } else if (type == SmsConstant.CustomerRemindType.birthday) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.customerRemindBirthday);
      //尊敬的{licenceNo}车主{carOwnerName}您好！{mouth}月{day}日是您的生日{userName}携全体员工祝您生日快日、工作顺利，并希望能一如既往的得到您的关照和支持！{shopName}店敬启{shopMobile }。
    } else if (type == SmsConstant.CustomerRemindType.debt) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.customerRemindDebt);
      //尊敬的{licenceNo}车主{carOwnerName}您好！您有{ money}元消费欠款在今年{mouth}月{day}日前到期，麻烦您方便的时候前来结算。您的配合本店将不胜感激！祝您身体健康！工作顺利！{shopName}店敬启，详请咨询{shopMobile }。;
    } else if (type == SmsConstant.CustomerRemindType.keepInGoodRepair) {
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.customerRemindKeepInGoodRepair);
      //尊敬的{licenceNo}车主{carOwnerName}您好！感谢您对本店的一贯照顾，您的爱车保养将于今年{mouth}月{day}日到期，本店可协助您代办保养业务，详请咨询{shopMobile},{shopName}店敬启。
    } else if (type == SmsConstant.CustomerRemindType.appointService) {
      //尊敬的{licenceNo}车主您好！感谢您对本店的一贯照顾，您预约的{appointName}服务时间为今年{mouth}月{day}日，本店竭诚欢迎您的光临，详情致电{shopMobile},{shopName}店敬启。
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.appointService);
    } else if (type == SmsConstant.CustomerRemindType.memberService) {
      //尊敬的{licenceNo}车主您好！感谢您对本店的一贯照顾，您的会员卡中{services}将于今年{mouth}月{day}日到期，本店欢迎您前来办理续卡业务，详情致电{shopMobile},{shopName}店敬启。
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.memberService);
    } else if(type == SmsConstant.CustomerRemindType.maintainMileage){
      //【店铺名称】提醒您：尊敬的车主【车牌号】！您的爱车已接近保养里程，请尽快来店保养，详情致电【联系方式】
       //尊敬的{licenceNo}车主{carOwnerName}您好！感谢您对本店的一贯照顾，您的爱车已接近保养里程，请尽快来店保养，详请咨询{shopMobile},{shopName}店敬启。
      //{shopName}提醒您：尊敬的{licenceNo}车主！您的爱车已接近保养里程，请尽快来店保养，详情致电{shopMobile}
      msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.maintainMileage);
    }else if(type==SmsConstant.CustomerRemindType.settledRemindMsg){
      msgTemplate=notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.settledRemindMsg);
    }
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "客户服务提醒 is null or MessageSwitch is OFF,please import data of notification_message_template or set MessageSwitch ON");
      return customerRemindSms;
    }
    content = msgTemplate.getContent();
    content = replace(content, SmsConstant.MsgTemplateContentConstant.licenceNo, licenceNo);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.name, name);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.appointName, customerRemindSms.getAppointName());
    content = replace(content, SmsConstant.MsgTemplateContentConstant.carOwnerName, name);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.money, money);
/*    content = replace(content, SmsConstant.MsgTemplateContentConstant.shopName, shopName);*/
    content = replace(content, SmsConstant.MsgTemplateContentConstant.shopMobile, shopMobile);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.userName, userName);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.year, year);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.day, day);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.mouth, mouth);
    content = replace(content, SmsConstant.MsgTemplateContentConstant.services, customerRemindSms.getServiceName());
    customerRemindSms.setTitle(title);
    customerRemindSms.setContent(content);
    customerRemindSms.setTemplateFlag(true);

    return customerRemindSms;
  }

  public List<MessageTemplateDTO> searchMessageTemplate(String type, Long shopId, Pager pager) {
    List<MessageTemplateDTO> msgTemplateDTOs = new ArrayList<MessageTemplateDTO>();
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<MessageTemplate> messageTemplates = writer.searchMessageTemplate(type, shopId, pager);
    for (MessageTemplate msgTemplate : messageTemplates) {
      msgTemplateDTOs.add(msgTemplate.toDTO());
    }
    return msgTemplateDTOs;
  }


  public int countMessageTemplate(String type, Long shopId, int pageNo) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.countMessageTemplate(type, shopId, pageNo);
  }

  /**
   * 更新短信模板
   *
   * @param msgTemplateDTO
   */
  public void updateMsgTemplate(MessageTemplateDTO msgTemplateDTO) {
    if (msgTemplateDTO == null) {
      return;
    }
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      MessageTemplate msgTemplate = writer.getMsgTemplateByType(msgTemplateDTO.getType());
      if (msgTemplate == null) {
        return;
      }
      msgTemplate.setType(msgTemplateDTO.getType());
      msgTemplate.setScene(msgTemplateDTO.getScene());
      msgTemplate.setNecessary(msgTemplateDTO.getNecessary());
      msgTemplate.setName(msgTemplateDTO.getName());
      //shopId暂时不处理，现在默认为-1
      msgTemplate.setShopId(-1l);
      msgTemplate.setContent(msgTemplateDTO.getContent());
      //更新MessageTemplate的同时，在Memcached server中添加变更标记
      MemCacheAdapter.set(msgTemplate.assembleKey(), String.valueOf(System.currentTimeMillis()));
      writer.update(msgTemplate);
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 保存短信模板
   *
   * @param msgTemplateDTO
   */
  public void saveMsgTemplate(MessageTemplateDTO msgTemplateDTO) {
    if (msgTemplateDTO == null) {
      return;
    }
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      MessageTemplate msgTemplate = new MessageTemplate();
      msgTemplate.setType(msgTemplateDTO.getType());
      //shopId暂时不处理，现在不做，默认为-1
      msgTemplate.setShopId(-1l);
      msgTemplate.setName(msgTemplateDTO.getName());
      msgTemplate.setScene(msgTemplateDTO.getScene());
      msgTemplate.setNecessary(msgTemplateDTO.getNecessary());
      msgTemplate.setContent(msgTemplateDTO.getContent());
      writer.save(msgTemplate);
      writer.commit(status);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      writer.rollback(status);
    }
  }

  private void getSmsTemplateImplementor(String template, Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    if (!RegexUtils.isMobile(mobile)) {
      LOG.warn("shop[id:{}] mobile[{}] is illegal", receiverId, mobile);
      return;
    }
    MessageTemplate msgTemplate = notificationService.getMsgTemplateByType(template);
    if (msgTemplate == null) {
      LOG.warn("查询短信: " + "salesStocking is null,please import data of notification_message_template");
      return;
    }
    String sendStr = msgTemplate.getContent();
    if (StringUtils.isNotEmpty(sendStr)) {
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.orderNumber, orderNumber);
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", mobile, sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setShopId(receiverId);
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(mobile);
      smsJobDTO.setType(SmsConstant.SMS_TYPE_VERIFICATION);
      smsJobDTO.setStartTime(System.currentTimeMillis());
      ShopDTO sender = getConfigService().getShopById(senderId);
      if (sender != null)
        smsJobDTO.setShopName(StringUtil.isEmpty(sender.getShortname()) ? sender.getName() : sender.getShortname());
      smsJobDTO.setSender(SenderType.Shop);
      notificationService.sendSmsAsync(smsJobDTO);
      LOG.debug("短信保存发送队列（sms_job）:[mobile:{},][内容:{}]", mobile, sendStr);
    }
  }

  @Override
  public void salesAcceptedSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    if (receiverId == null || senderId == null || StringUtils.isBlank(orderNumber))
      throw new SmsException("receiver[ shopId:" + receiverId + "] or sender[ shopId:" + senderId + "] or order[orderNumber:" + orderNumber + "] is empty!");
    if (!isMessageSwitchOn(receiverId, MessageScene.SALES_ACCEPTED)) return;
    getSmsTemplateImplementor(SmsConstant.MsgTemplateTypeConstant.salesAccepted, receiverId, senderId, orderNumber, mobile);
  }

  @Override
  public void salesRefuseSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    if (receiverId == null || senderId == null || StringUtils.isBlank(orderNumber))
      throw new SmsException("receiver[ shopId:" + receiverId + "] or sender[ shopId:" + senderId + "] or order[orderNumber:" + orderNumber + "] is empty!");
    if (!isMessageSwitchOn(receiverId, MessageScene.SALES_REFUSE)) return;
    getSmsTemplateImplementor(SmsConstant.MsgTemplateTypeConstant.salesRefuse, receiverId, senderId, orderNumber, mobile);
  }

  @Override
  public void stockingCancelSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    if (receiverId == null || senderId == null || StringUtils.isBlank(orderNumber))
      throw new SmsException("receiver[ shopId:" + receiverId + "] or sender[ shopId:" + senderId + "] or order[orderNumber:" + orderNumber + "] is empty!");
    if (!isMessageSwitchOn(receiverId, MessageScene.STOCKING_CANCEL)) return;
    getSmsTemplateImplementor(SmsConstant.MsgTemplateTypeConstant.stockingCancel, receiverId, senderId, orderNumber, mobile);
  }

  @Override
  public void shippedCancelSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    if (receiverId == null || senderId == null || StringUtils.isBlank(orderNumber))
      throw new SmsException("receiver[ shopId:" + receiverId + "] or sender[ shopId:" + senderId + "] or order[orderNumber:" + orderNumber + "] is empty!");
    if (!isMessageSwitchOn(receiverId, MessageScene.SHIPPED_CANCEL)) return;
    getSmsTemplateImplementor(SmsConstant.MsgTemplateTypeConstant.shippedCancel, receiverId, senderId, orderNumber, mobile);
  }

  @Override
  public void returnsAcceptedSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    if (receiverId == null || senderId == null || StringUtils.isBlank(orderNumber))
      throw new SmsException("receiver[ shopId:" + receiverId + "] or sender[ shopId:" + senderId + "] or order[orderNumber:" + orderNumber + "] is empty!");
    if (!isMessageSwitchOn(receiverId, MessageScene.RETURNS_ACCEPTED)) return;
    getSmsTemplateImplementor(SmsConstant.MsgTemplateTypeConstant.returnsAccepted, receiverId, senderId, orderNumber, mobile);
  }

  @Override
  public void returnsRefuseSMS(Long receiverId, Long senderId, String orderNumber, String mobile) throws SmsException {
    if (receiverId == null || senderId == null || StringUtils.isBlank(orderNumber))
      throw new SmsException("receiver[ shopId:" + receiverId + "] or sender[ shopId:" + senderId + "] or order[orderNumber:" + orderNumber + "] is empty!");
    if (!isMessageSwitchOn(receiverId, MessageScene.RETURNS_REFUSE)) return;
    getSmsTemplateImplementor(SmsConstant.MsgTemplateTypeConstant.returnsRefuse, receiverId, senderId, orderNumber, mobile);
  }

  /**
   * 判断店面该场景短信开关是否关闭
   *
   * @param shopId
   * @param scene
   * @return
   */
  public boolean isMessageSwitchOff(Long shopId, MessageScene scene) {
    IMessageTemplateService messageTemplateService = ServiceManager.getService(IMessageTemplateService.class);
    MessageSwitchStatus switchStatus = messageTemplateService.getMessageSwitchStatus(shopId, scene);
    if (MessageSwitchStatus.OFF == switchStatus || null == switchStatus) {
      return true;
    }
    return false;
  }

  /**
   * 判断店面该场景短信开关是否关闭
   *
   * @param shopId
   * @param scene
   * @return
   */
  public boolean isMessageSwitchOn(Long shopId, MessageScene scene) {
    IMessageTemplateService messageTemplateService = ServiceManager.getService(IMessageTemplateService.class);
    MessageSwitchStatus switchStatus = messageTemplateService.getMessageSwitchStatus(shopId, scene);
    if (MessageSwitchStatus.ON == switchStatus) {
      LOG.debug("短信开关:[{}:{}]", scene.getScene(), switchStatus.getStatus());
      return true;
    }
    LOG.debug("短信开关:[{}:{}]", scene.getScene(), null == switchStatus ? "null" : switchStatus.getStatus());
    return false;
  }

  @Override
  public void sendBcgogoOrderSms(ShopDTO shopDTO, BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO) throws SmsException {
    String mobile = ConfigUtils.getBcgogoOrderSmsPhoneNumber();
    if(StringUtils.isBlank(mobile)){
      throw new SmsException("客户下单(BcgogoOrder)短信通知BCGOGO客服:sendBcgogoOrderSms mobile is null！");
    }
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    MessageTemplate msgTemplate;
    String sendStr = "";
    msgTemplate = notificationService.getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.bcgogoOrderSms);
    //{shopName}刚刚提交销售订单{bcgogoOrderReceiptNo}，请马上去处理一下吧！可联系{contact} {mobile}。
    if (msgTemplate == null) {
      throw new SmsException("客户下单(BcgogoOrder)短信通知BCGOGO客服:sendBcgogoOrderSms is null,please import data of notification_message_template！");
    }
    sendStr = msgTemplate.getContent();
    if (sendStr != null && !"".equals(sendStr)) {
      String shopName = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
      sendStr = msgTemplate.getContent();
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.shopName, shopName);
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.bcgogoOrderReceiptNo, bcgogoReceivableOrderDTO.getReceiptNo());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.contact, bcgogoReceivableOrderDTO.getContact());
      sendStr = replace(sendStr, SmsConstant.MsgTemplateContentConstant.mobile,bcgogoReceivableOrderDTO.getMobile());
      LOG.debug("短信开始发送:[mobile:{},][内容:{}]", shopDTO.getMobile(), sendStr);
      SmsJobDTO smsJobDTO = new SmsJobDTO();
      smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
      smsJobDTO.setSender(SenderType.bcgogo);  //实时发送  不需要 验证金额
      smsJobDTO.setContent(sendStr);
      smsJobDTO.setReceiveMobile(mobile);
      smsJobDTO.setShopId(ShopConstant.BC_SHOP_ID);
      notificationService.sendSmsSync(smsJobDTO);

    }
  }

  /**
   * 安全 replace
   *
   * @param str
   * @param dest
   * @param orig
   * @return
   * @throws SmsException
   */
  public String replace(String str, String dest, String orig) throws SmsException {
    if (str.isEmpty()) throw new SmsException("replace string nullPointException!");
    return str.replace(dest, (StringUtils.isBlank(orig) ? "" : orig));
  }

  private IConfigService configService;
  private IShopService shopService;

  public IShopService getShopService() {
    if (shopService == null) {
      shopService = ServiceManager.getService(IShopService.class);
    }
    return shopService;
  }

  public IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }
}

