package com.bcgogo.mq.service;

import com.bcgogo.activemq.MQProductHelper;
import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppUserLoginInfoDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.constant.MQConstant;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.mq.SocketSessionManager;
import com.bcgogo.mq.enums.MProtocolType;
import com.bcgogo.mq.message.MQMessageDTO;
import com.bcgogo.mq.message.MQMessageItemDTO;
import com.bcgogo.mq.message.MQTalkMessageDTO;
import com.bcgogo.mq.protocol.MMsgProtocol;
import com.bcgogo.mq.protocol.MProtocol;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import org.apache.commons.collections.MapUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-14
 * Time: 上午10:24
 */
public class MQHelper {
  private static final Logger LOG = LoggerFactory.getLogger(MQHelper.class);

//  public static IoSession getIoSessionByAppUserNo(String appUserNo) {
//    AppUserLoginInfoDTO loginInfoDTO = ServiceManager.getService(IAppUserService.class).getAppUserLoginInfoByUserNo(appUserNo, AppUserType.MIRROR);
//    if (loginInfoDTO == null || StringUtil.isEmpty(loginInfoDTO.getMqSessionId())) {
//      return null;
//    }
//    return SocketSessionManager.getSessionById(loginInfoDTO.getMqSessionId());
//  }

  public static boolean isOnLine(String userName) {
    IoSession session = SocketSessionManager.getSessionByFromUserName(userName);
    return session != null;
  }


  public void disconnect(String userName) {
    IoSession session = SocketSessionManager.getSessionByFromUserName(userName);
    if (session != null) {
      SocketSessionManager.removeSession(session.getId());
      session.close(true);
    }
  }

  public ApiResponse send(MQTalkMessageDTO talkMessageDTO) throws Exception {
    //1.存储客户端发送的消息
    LOG.info("savePushMessage,data is {}", JsonUtil.objectCHToJson(talkMessageDTO));
    PushMessageDTO pushMessageDTO = MQHelper.savePushMessage(talkMessageDTO);
    //2.如果接收方在线,将消息转发到对应接收者
    LOG.info("sendMsg to user");
    sendMsg(pushMessageDTO.getId(), talkMessageDTO);
    return MessageCode.toApiResponse(MessageCode.SUCCESS);
  }

  public void produce(String subject, String msg) {
    MQProductHelper.produce(subject, msg);
  }

  public static ApiResponse push(MQTalkMessageDTO talkMessageDTO) throws Exception {
    //1.存储客户端发送的消息
    LOG.info("savePushMessage,data is {}", JsonUtil.objectCHToJson(talkMessageDTO));
    PushMessageDTO pushMessageDTO = MQHelper.savePushMessage(talkMessageDTO);
    //2.如果接收方在线,将消息转发到对应接收者
    MQMessageDTO messageDTO = new MQMessageDTO();
    messageDTO.setAppUserNo(talkMessageDTO.getAppUserNo());
    messageDTO.setSendTime(System.currentTimeMillis());
    MQMessageItemDTO itemDTO = new MQMessageItemDTO();
    itemDTO.setToUserName(talkMessageDTO.getAppUserNo());
    itemDTO.setFromUserName(talkMessageDTO.getFromUserName());
    itemDTO.setToUserName(talkMessageDTO.getToUserName());
    itemDTO.setContent(talkMessageDTO.getContent());
    itemDTO.setMsgId(pushMessageDTO.getId());
    List<MQMessageItemDTO> itemDTOs = new ArrayList<MQMessageItemDTO>();
    itemDTOs.add(itemDTO);
    messageDTO.setItemDTOs(itemDTOs);

    IoSession session = SocketSessionManager.getSessionByFromUserName(messageDTO.getAppUserNo());
    if (session == null || !session.isConnected()) {
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
    String msgJson = JsonUtil.objectToJson(messageDTO);
    LOG.info("mq:MQHelper push msg,sessionId:{},msg:{}", session.getId(), msgJson);
    MMsgProtocol msgProtocol = new MMsgProtocol(MQConstant.MIRROR_MSG_DATA, ByteUtil.complementZero(msgJson.getBytes("UTF-8")));
    MProtocol protocol = new MProtocol(MProtocolType.MIRROR, msgProtocol.toProtocol());
    session.write(protocol);
    return MessageCode.toApiResponse(MessageCode.SUCCESS);
  }

  /**
   * 接收后视镜发送的消息
   *
   * @param messageDTO
   * @throws Exception
   */
  public static void receiveMirrorMsg(MQMessageDTO messageDTO) throws Exception {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<MQMessageItemDTO> itemDTOs = messageDTO.getItemDTOs();
    if (CollectionUtil.isEmpty(itemDTOs)) {
      LOG.warn("the item of messageDTO is empty!");
      return;
    }
    for (MQMessageItemDTO itemDTO : itemDTOs) {
      if (itemDTO.getType() == MQConstant.pushMessageTypeMap.get(PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.toString())) {
        IoSession receiverSession = SocketSessionManager.getSessionByFromUserName(itemDTO.getToUserName());
        if (receiverSession == null || !receiverSession.isConnected()) {
          pushTemplateMsgToWXUser(PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.toString(), messageDTO.getAppUserNo(), itemDTO);
        } else {
          //与微信直接通话
          LOG.info("{} is online,and send msg", itemDTO.getToUserName());
          MProtocol protocolTemp = new MProtocol(MProtocolType.WEB_SOCKET, JsonUtil.objectToJson(itemDTO.toMQTalkMessageDTO()).getBytes("UTF-8"));
          receiverSession.write(protocolTemp);
        }
        //保存对话
        LOG.info("save MSG_FROM_WX_USER_TO_MIRROR talk msg");
        String appUserNo = itemDTO.getFromUserName();
        String openId = itemDTO.getToUserName();
        AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
        List<AppVehicleDTO> appVehicleDTOs = new ArrayList<AppVehicleDTO>();
        appVehicleDTOs.add(appVehicleDTO);
        AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
        appUserDTO.setAppVehicleDTOs(appVehicleDTOs);
        ServiceManager.getService(IAppointPushMessageService.class).saveTalkMessage2App(openId, appUserDTO, itemDTO.getContent(), PushMessageType.MSG_FROM_MIRROR_TO_WX_USER);
      }
    }
  }


  /**
   * 前端-->服务器-->后视镜
   *
   * @param session
   * @param talkMessageDTO
   * @throws Exception
   */
  public static void receiveWebSocketMsg(IoSession session, MQTalkMessageDTO talkMessageDTO) throws Exception {
    //客户端登录处理
    if (MQConstant.CLIENT_LOGIN.equals(talkMessageDTO.getContent())) {
      IoSession expireSession = SocketSessionManager.getSessionByFromUserName(talkMessageDTO.getFromUserName());
      if (expireSession != null) {
        SocketSessionManager.removeSession(expireSession.getId());
        expireSession.close(true);
      }
      session.setAttribute("fromUserName", talkMessageDTO.getFromUserName());
      SocketSessionManager.addSession(session.getId(), session);
      return;
    }
    //1.存储客户端发送的消息
    PushMessageDTO pushMessageDTO = savePushMessage(talkMessageDTO);
    //2.如果接收方在线,将消息转发到对应接收者
    sendMsg(pushMessageDTO.getId(), talkMessageDTO);
  }

  public static PushMessageDTO savePushMessage(MQTalkMessageDTO talkMessageDTO) throws Exception {
    //ShopTalkMessage
    if (PushMessageType.MSG_FROM_WX_USER_TO_SHOP.equals(talkMessageDTO.getType()) || PushMessageType.MSG_FROM_SHOP_TO_WX_USER.equals(talkMessageDTO.getType())) {
      ServiceManager.getService(IPushMessageService.class).saveWXShopTalkMessageDTO(talkMessageDTO);
    }
    //save PushMessage
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    String appUserNo = talkMessageDTO.getAppUserNo();
    String openId = talkMessageDTO.getFromUserName();
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    List<AppVehicleDTO> appVehicleDTOs = new ArrayList<AppVehicleDTO>();
    appVehicleDTOs.add(appVehicleDTO);
    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appUserNo);
    appUserDTO.setAppVehicleDTOs(appVehicleDTOs);
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    return CollectionUtil.getFirst(appointPushMessageService.saveTalkMessage2App(openId, appUserDTO, talkMessageDTO.getContent(), talkMessageDTO.getType()));
  }

  /**
   * @param messageId
   * @param talkMessageDTO
   * @throws UnsupportedEncodingException
   */
  public static void sendMsg(Long messageId, MQTalkMessageDTO talkMessageDTO) throws Exception {
    List<MQMessageItemDTO> itemDTOs = new ArrayList<MQMessageItemDTO>();
    MQMessageItemDTO itemDTO = new MQMessageItemDTO();
    itemDTO.setMsgId(messageId);
    itemDTO.setTitle("对话消息");
    itemDTO.setContent(talkMessageDTO.getContent());
    itemDTO.setFromUserName(talkMessageDTO.getFromUserName());
    itemDTO.setToUserName(talkMessageDTO.getToUserName());
    itemDTOs.add(itemDTO);
    //push msg
    IoSession receiverSession = SocketSessionManager.getSessionByFromUserName(talkMessageDTO.getToUserName());
    if (PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.equals(talkMessageDTO.getType())
      && receiverSession != null && receiverSession.isConnected()) {
      LOG.info("user:{} is online,send msg from wx_user to mirror:{}", talkMessageDTO.getToUserName(), JsonUtil.objectToJson(itemDTO.toMQMessageDTO()));
      MMsgProtocol msgProtocol = new MMsgProtocol(MQConstant.MIRROR_MSG_DATA, ByteUtil.complementZero(JsonUtil.objectToJson(itemDTO.toMQMessageDTO()).getBytes("UTF-8")));
      MProtocol protocol = new MProtocol(MProtocolType.MIRROR, msgProtocol.toProtocol());
      receiverSession.write(protocol);
    } else if (PushMessageType.MSG_FROM_SHOP_TO_WX_USER.equals(talkMessageDTO.getType())) {
      if (receiverSession != null && receiverSession.isConnected()) {
        LOG.info("wx_user:{} is online,send msg:{}", talkMessageDTO.getToUserName(), JsonUtil.objectToJson(itemDTO.toMQTalkMessageDTO()));
        MProtocol protocol = new MProtocol(MProtocolType.WEB_SOCKET, JsonUtil.objectToJson(itemDTO.toMQTalkMessageDTO()).getBytes("UTF-8"));
        receiverSession.write(protocol);
      } else {
        LOG.info("wx_user:{} is offline,send  pushTemplateMsgToWXUser");
        pushTemplateMsgToWXUser(PushMessageType.MSG_FROM_WX_USER_TO_SHOP.toString(), talkMessageDTO.getAppUserNo(), itemDTO);
      }

    }

  }

  public static void pushTemplateMsgToWXUser(String type, String appUserNo, MQMessageItemDTO itemDTO) throws Exception {
    LOG.info("{} is offline,and send notify templateMsg", itemDTO.getToUserName());
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
    AppVehicleDTO vehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
    if (vehicleDTO == null) {
      LOG.error("appUserNo:{},AppVehicle info exception", appUserNo);
      return;
    }
    String timeStr = DateUtil.convertDateLongToDateString(DateUtil.ALL, System.currentTimeMillis());
    String publicNo = wxUserService.getAppUserWXQRCodeDTOByAppUserNo(appUserNo).getPublicNo();
    WXMsgTemplate template = WXHelper.getNotifyTemplate(publicNo, itemDTO.getToUserName(), itemDTO.getContent(), vehicleDTO.getVehicleNo(), timeStr);
    if (template != null) {
      template.setUrl(WXHelper.mirrorPvMsgUrl(type, itemDTO.getToUserName(), appUserNo));
      ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(publicNo, template);
    }
  }

}
