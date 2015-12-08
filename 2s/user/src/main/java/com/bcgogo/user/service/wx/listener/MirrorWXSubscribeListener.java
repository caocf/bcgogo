package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.model.wx.AppUserWXQRCode;
import com.bcgogo.user.model.wx.AppWXUser;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StopWatchUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-28
 * Time: 17:49
 */
public class MirrorWXSubscribeListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(MirrorWXSubscribeListener.class);

  private WXEventObj eventObj;

  public MirrorWXSubscribeListener(WXEventObj eventObj) {
    this.eventObj = eventObj;
  }

  @Override
  public void run() {
    try {
      WXRequestParam param = (WXRequestParam) eventObj.getSource();
      rxSubscribe(param.getPublicNo(), param.getOpenId(), param.getCreateTime(), param.getEventKey());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public void rxSubscribe(String publicNo, String openId, Long createTime, String sceneId) throws Exception {
    String lockKey = ConcurrentScene.WX_RESP_HANDLE_SUBSCRIBE + openId + "_" + createTime;
    try {
      LOG.info("mirror rxSubscribe,sceneId:{}",sceneId);
      StopWatchUtil sw = new StopWatchUtil("mirror:rxSubscribe", "start");
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_SUBSCRIBE, lockKey)) {
        LOG.warn("mirror:rxSubscribe has been handling,openId is {}", openId);
        return;
      }
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      WXUserDTO userDTO = wxUserService.getWXUserFromPlat(publicNo, openId);
      if (userDTO == null || userDTO.getSubscribe() == "0") {
        return;
      }
      if (isUserExist(openId)) {
        LOG.info("mirror:subscribed user has been existed,openId is {}", openId);
        return;
      }
      //save wx user
      userDTO.setPublicNo(publicNo);
      userDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXUser(userDTO);
      //根据sceneId,关联appUser
      if (StringUtil.isNotEmpty(sceneId)) {
        sceneId = sceneId.split("_")[1];
        WXQRCodeDTO qrCodeDTO = wxUserService.getWXQRCodeDTOBySceneId(publicNo, NumberUtil.longValue(sceneId));
        if (qrCodeDTO == null) {
          LOG.error("mirror:rxSubscribe ,WXQRCodeDTO don't exist");
          return;
        }
        if (QRScene.ACCIDENT.equals(qrCodeDTO.getScene())) { //添加事故专员
          if (isAccidentSpecialistExist(qrCodeDTO.getShopId(), openId)) {
            return;
          }
          IUserService userService = ServiceManager.getService(IUserService.class);
          List<AccidentSpecialistDTO> specialistDTOs = userService.getAccidentSpecialistByOpenId(qrCodeDTO.getShopId(), null);
          if (CollectionUtil.isNotEmpty(specialistDTOs) && specialistDTOs.size() >= 3) {
            return;
          }
          AccidentSpecialistDTO specialistDTO = new AccidentSpecialistDTO();
          specialistDTO.setOpenId(openId);
          specialistDTO.setShopId(qrCodeDTO.getShopId());
          specialistDTO.setDeleted(DeletedType.FALSE);
          ServiceManager.getService(IUserService.class).saveOrUpdateAccidentSpecialist(specialistDTO);
        } else {  //后视镜添加微信用户
          AppUserWXQRCodeDTO codeDTO = wxUserService.getAppUserWXQRCodeDTOBySceneId(NumberUtil.longValue(sceneId));
          if (codeDTO == null || StringUtil.isEmpty(codeDTO.getAppUserNo())) {
            LOG.error("mirror:rxSubscribe ,AppUserWXQRCode don't exist");
            return;
          }
          //添加appUser和openId的关联
          AppWXUserDTO appWXUserDTO = new AppWXUserDTO();
          appWXUserDTO.setOpenId(openId);
          appWXUserDTO.setAppUserNo(codeDTO.getAppUserNo());
          appWXUserDTO.setDeleted(DeletedType.FALSE);
          wxUserService.saveOrUpdateAppWXUser(appWXUserDTO);
        }
      }
      //统计粉丝来源
      WXSubscribeRecordDTO recordDTO = new WXSubscribeRecordDTO();
      recordDTO.setPublicNo(publicNo);
      recordDTO.setOpenId(openId);
      recordDTO.setSubscribeTime(System.currentTimeMillis());
      recordDTO.setScene(WXSubscribeScene.SUBSCRIBE);
      recordDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXSubscribeRecord(recordDTO);
      //发送提示消息
      IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
      String content = WXConstant.CONTENT_SUBSCRIBE_LIYIXING;
      sender.sendCustomTextMsg(publicNo, openId, content);
      sw.stopAndPrintLog();
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_SUBSCRIBE, lockKey);
    }
  }

  private boolean isUserExist(String openId) {
    IWXUserService userService = ServiceManager.getService(IWXUserService.class);
    WXUserDTO userDTO = userService.getWXUserDTOByOpenId(openId);
    return userDTO != null ? true : false;
  }

  private boolean isAccidentSpecialistExist(Long shopId, String openId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    AccidentSpecialistDTO specialistDTO = CollectionUtil.getFirst(userService.getAccidentSpecialistByOpenId(shopId, openId));
    return specialistDTO != null ? true : false;
  }


}
