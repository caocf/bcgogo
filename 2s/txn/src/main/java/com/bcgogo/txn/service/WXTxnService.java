package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.notification.service.WXService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderItemDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.user.service.wx.impl.WXAccountService;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXMsgStatus;
import com.bcgogo.wx.WXShopBillDTO;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.message.template.WXKWMsgTemplate;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-18
 * Time: 下午1:56
 */
@Component
public class WXTxnService implements IWXTxnService {

  public static final Logger LOG = LoggerFactory.getLogger(WXTxnService.class);

  @Override
  public void doWXRemindEvent() throws Exception {
    IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
    ITxnService txnService = ServiceManager.getService(TxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IWXService wxService = ServiceManager.getService(WXService.class);
    Long startTime = DateUtil.getStartTimeOfToday();
    Long endTime = DateUtil.getEndTimeOfToday();
    List<RemindEventDTO> remindEventDTOs = txnService.getWXRemindEvent(startTime, endTime);
    if (CollectionUtil.isEmpty(remindEventDTOs)) return;
    for (RemindEventDTO dto : remindEventDTOs) {
      String openId = dto.getOpenId();
      WXUserDTO userDTO = wxUserService.getWXUserDTOByOpenId(openId);
      if (userDTO == null) {
        continue;
      }
      ShopDTO shopDTO = configService.getShopById(dto.getShopId());
      WXAccountDTO accountDTO = ServiceManager.getService(WXAccountService.class).getWXAccountDTOByShopId(dto.getShopId());
      String publicNo = accountDTO == null ? WXHelper.getDefaultPublicNo() : accountDTO.getPublicNo();
      WXMsgTemplate template = WXHelper.getAppointRemindTemplate(publicNo, dto.getLicenceNo(), dto.getOpenId(), dto.getEventStatus(), dto.getRemindTimeStr(), shopDTO.getLandline());
      Result result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(userDTO.getPublicNo(), template);
      if (!result.isSuccess()) {
        LOG.error(result.getMsg());
        continue;
      }
      txnService.updateRemindEventWXRemindStatus(dto.getId(), YesNo.YES.toString());
      //记录消息
      WXMsgDTO msg = new WXMsgDTO();
      msg.setMsgId(StringUtil.valueOf(result.getData()));
      msg.setOpenId(openId);
      msg.setSendTime(System.currentTimeMillis());
      msg.setRemark(result.getMsg());
      msg.setFromShopId(shopDTO.getId());
      if (result.isSuccess()) {
        msg.setStatus(WXMsgStatus.SENT);
      } else {
        msg.setStatus(WXMsgStatus.LOCAL_FAILED);
      }
      msg.setCategory(WXMCategory.TEMPLATE);
      wxService.saveOrUpdateWXMsg(msg);
      wxService.saveWXMsgReceiver(openId, msg.getId());
      //save WXShopBill
      WXShopBillDTO billDTO = new WXShopBillDTO();
      billDTO.setMsgId(msg.getId());
      billDTO.setShopId(shopDTO.getId());
      billDTO.setVestDate(System.currentTimeMillis());
      billDTO.setScene(SmsSendScene.WX_APPOINT_REMIND_TEMPLATE);
      billDTO.setAmount(1);
      IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
      WXShopAccountDTO shopAccount = accountService.getWXShopAccountDTOByShopId(shopDTO.getId());
      if (shopAccount != null && shopAccount.getBalance() > 0) {
        billDTO.setTotal(WXConstant.MSG_PRICE);
        shopAccount.setBalance(NumberUtil.subtract(shopAccount.getBalance(), WXConstant.MSG_PRICE));
      } else {
        billDTO.setTotal(0D);
      }
      wxService.saveOrUpdateWXShopBill(billDTO);
      accountService.saveOrUpdateWXShopAccountDTO(shopAccount);
    }
  }

  public void sendConsumeMsg(SalesOrderDTO salesOrderDTO) {
    try {
      if (!WXHelper.validateWXShopAccount(salesOrderDTO.getShopId())) return;
      if (salesOrderDTO.getMemberAmount() != null && salesOrderDTO.getMemberAmount() > 0d) {
        String consume = salesOrderDTO.getMemberAmount() + "元";
        sendMemberConsumeMsg(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getUserName(), salesOrderDTO.getId(), OrderTypes.SALE, consume,
          salesOrderDTO.getAccountMemberNo(), salesOrderDTO.getLicenceNo(), salesOrderDTO.getShopName());
      } else if (StringUtil.isNotEmpty(salesOrderDTO.getLicenceNo())) {
        sendConsumeMsg(salesOrderDTO.getShopId(), salesOrderDTO.getUserId(), salesOrderDTO.getUserName(), salesOrderDTO.getId(), OrderTypes.SALE, salesOrderDTO.getLicenceNo(),
          salesOrderDTO.getTotal(), salesOrderDTO.getShopName());
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void sendConsumeMsg(RepairOrderDTO orderDTO) {
    try {
      if (!WXHelper.validateWXShopAccount(orderDTO.getShopId())) return;
      String shopName = ServiceManager.getService(IConfigService.class).getShopById(orderDTO.getShopId()).getName();
      if (orderDTO.getMemberAmount() != null && orderDTO.getMemberAmount() > 0d) {
        String consume = orderDTO.getMemberAmount() + "元";
        sendMemberConsumeMsg(orderDTO.getShopId(), orderDTO.getUserId(), orderDTO.getUserName(), orderDTO.getId(), OrderTypes.REPAIR, consume,
          orderDTO.getAccountMemberNo(), orderDTO.getLicenceNo(), shopName);
      } else if (StringUtil.isNotEmpty(orderDTO.getVechicle())) {
        sendConsumeMsg(orderDTO.getShopId(), orderDTO.getUserId(), orderDTO.getUserName(), orderDTO.getId(), OrderTypes.REPAIR, orderDTO.getVechicle(),
          orderDTO.getTotal(), orderDTO.getShopName());
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void sendConsumeMsg(WashBeautyOrderDTO orderDTO) {
    try {
      if (!WXHelper.validateWXShopAccount(orderDTO.getShopId())) return;
      StringBuilder sb = new StringBuilder();
      if (orderDTO.getMemberAmount() != null && orderDTO.getMemberAmount() > 0d) {
        sb.append(orderDTO.getMemberAmount()).append("元").append(" ");
      }
      WashBeautyOrderItemDTO[] itemDTOs = orderDTO.getWashBeautyOrderItemDTOs();
      for (WashBeautyOrderItemDTO itemDTO : itemDTOs) {
        if (ConsumeType.TIMES.equals(itemDTO.getConsumeTypeStr())) {
          sb.append("计次划卡");
        }
      }
      String shopName = ServiceManager.getService(IConfigService.class).getShopById(orderDTO.getShopId()).getName();
      String consume = sb.toString();
      if (StringUtil.isNotEmpty(consume)) {
        sendMemberConsumeMsg(orderDTO.getShopId(), orderDTO.getUserId(), orderDTO.getUserName(), orderDTO.getId(), OrderTypes.WASH_BEAUTY, consume,
          orderDTO.getAccountMemberNo(), orderDTO.getLicenceNo(), shopName);
      } else if (StringUtil.isNotEmpty(orderDTO.getVechicle())) {
        sendConsumeMsg(orderDTO.getShopId(), orderDTO.getUserId(), orderDTO.getUserName(), orderDTO.getId(), OrderTypes.WASH_BEAUTY, orderDTO.getVechicle(),
          orderDTO.getTotal(), shopName);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  private void sendConsumeMsg(Long shopId, Long userId, String userName, Long orderId, OrderTypes orderTypes, String vehicleNo, double total, String shopName) throws Exception {
    List<WXUserDTO> userDTOs = ServiceManager.getService(IWXUserService.class).getWXUserDTOByVehicleNo(vehicleNo);
    if (CollectionUtil.isEmpty(userDTOs)) {
      return;
    }
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    for (WXUserDTO userDTO : userDTOs) {
      //给绑定该车牌号的微信号发信息
      String openId = userDTO.getOpenid();
      if (userDTO == null) {
        continue;
      }
      String dateTime = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.STANDARD);
      WXKWMsgTemplate template = WXHelper.getOrderConsumerTemplate(userDTO.getPublicNo(), orderTypes, vehicleNo, openId, dateTime, String.valueOf(total), shopName);
      if (template == null) {
        continue;
      }
      template.setUrl(WXHelper.orderDetailUrl(orderId, orderTypes.toString(), vehicleNo));
      Result result = sender.sendTemplateMsg(userDTO.getPublicNo(), template);
      afterSendConsumeMsg(shopId, userId, userName, userDTO.getPublicNo(), userDTO.getOpenid(), template.getUrl(), template.getRemark(), result);
    }
  }

  /**
   * 发送会员消费信息
   *
   * @param shopId
   * @param userId
   * @param userName
   * @param orderId
   * @param orderTypes
   * @param consume
   * @param memberNo
   * @param consume_vehicleNo
   * @param shopName
   * @throws Exception
   */
  public void sendMemberConsumeMsg(Long shopId, Long userId, String userName, Long orderId, OrderTypes orderTypes, String consume,
                                   String memberNo, String consume_vehicleNo, String shopName) throws Exception {
    LOG.debug("wx:to send member consumeMsg.consume_vehicleNo is {},memberNo is {}", consume_vehicleNo, memberNo);
    //查询会员卡主人
    MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getEnabledMemberDTOByMemberNo(memberNo, shopId);
    CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(memberDTO.getCustomerId());
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    Set<String> vehicleSet = new HashSet<String>();
    vehicleSet.add(consume_vehicleNo);//发给消费车牌号
    List<VehicleDTO> vehicleDTOList = vehicleService.getVehicleListByCustomerId(customerDTO.getId());
    for (VehicleDTO vehicleDTO : vehicleDTOList) {
      if (StringUtil.isEmpty(vehicleDTO.getLicenceNo()) || vehicleSet.contains(vehicleDTO.getLicenceNo())) {
        continue;
      }
      vehicleSet.add(vehicleDTO.getLicenceNo());
    }
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
    Set<String> wxUserIdSet = new HashSet<String>();
    for (String vehicleNo : vehicleSet) {
      List<WXUserDTO> userDTOs = wxUserService.getWXUserDTOByVehicleNo(vehicleNo);
      if (CollectionUtil.isEmpty(userDTOs)) continue;
      for (WXUserDTO userDTO : userDTOs) {
        if (userDTO == null || wxUserIdSet.contains(userDTO.getOpenid())) {
          continue;
        }
        wxUserIdSet.add(userDTO.getOpenid());
        //给绑定该车牌号的微信号发信息
        String openId = userDTO.getOpenid();
        String dateTime = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.STANDARD);
        WXKWMsgTemplate template = WXHelper.getMemberConsumerTemplate(userDTO.getPublicNo(), openId, memberNo, consume, dateTime, shopName);
        if (template == null) {
          continue;
        }
        template.setUrl(WXHelper.orderDetailUrl(orderId, orderTypes.toString(), vehicleNo));
        Result result = sender.sendTemplateMsg(userDTO.getPublicNo(), template);
        afterSendConsumeMsg(shopId, userId, userName, userDTO.getPublicNo(), userDTO.getOpenid(), template.getUrl(), template.getRemark(), result);
      }
    }
    LOG.debug("wx:send member consumeMsg success");
  }

  private void afterSendConsumeMsg(Long shopId, Long userId, String userName, String publicNo, String openId, String url, String description, Result result) {
    IWXService wxService = ServiceManager.getService(WXService.class);
    //记录消息
    WXMsgDTO msg = new WXMsgDTO();
    msg.setFromShopId(shopId);
    msg.setUserId(userId);
    msg.setUserName(userName);
    msg.setMsgId(StringUtil.valueOf(result.getData()));
    msg.setOpenId(openId);
    msg.setSendTime(System.currentTimeMillis());
    msg.setRemark(result.getMsg());
    msg.setTitle("消费");
    msg.setDescription(description);
    msg.setUrl(url);
    if (result.isSuccess()) {
      msg.setStatus(WXMsgStatus.SENT);
    } else {
      msg.setStatus(WXMsgStatus.LOCAL_FAILED);
    }
    msg.setCategory(WXMCategory.TEMPLATE);
    wxService.saveOrUpdateWXMsg(msg);
    //save WXShopBill
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    WXShopAccountDTO shopAccount = accountService.getWXShopAccountDTOByShopId(shopId);
    WXShopBillDTO billDTO = new WXShopBillDTO();
    billDTO.setMsgId(msg.getId());
    billDTO.setShopId(shopId);
    billDTO.setVestDate(System.currentTimeMillis());
    billDTO.setScene(SmsSendScene.WX_CONSUME_TEMPLATE);
    billDTO.setAmount(1);
    if (shopAccount != null && shopAccount.getBalance() > 0) {
      billDTO.setTotal(WXConstant.MSG_PRICE);
      shopAccount.setBalance(NumberUtil.subtract(shopAccount.getBalance(), WXConstant.MSG_PRICE));
    } else {
      billDTO.setTotal(0D);
    }
    wxService.saveOrUpdateWXShopBill(billDTO);
    accountService.saveOrUpdateWXShopAccountDTO(shopAccount);
    //save receiver
    wxService.saveWXMsgReceiver(openId, msg.getId());
    // 关联成为该店铺微信用户
    IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
    ShopWXUserDTO sUserDTO = wxUserService.getShopWXUser(shopId, openId);
    if (sUserDTO == null) {
      wxUserService.saveShopWXUser(publicNo, openId, shopId);
      //统计粉丝来源
      WXSubscribeRecordDTO recordDTO = new WXSubscribeRecordDTO();
      recordDTO.setPublicNo(publicNo);
      recordDTO.setOpenId(openId);
      recordDTO.setSubscribeTime(System.currentTimeMillis());
      recordDTO.setShopId(shopId);
      recordDTO.setScene(WXSubscribeScene.CONSUME);
      recordDTO.setDeleted(DeletedType.FALSE);
      ServiceManager.getService(IWXUserService.class).saveOrUpdateWXSubscribeRecord(recordDTO);
    }
  }


}
