package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.qr.WXQRCodeSearchCondition;
import com.bcgogo.wx.user.AppUserWXQRCodeDTO;
import com.bcgogo.wx.user.AppWXUserDTO;
import com.bcgogo.wx.user.WXSubscribeRecordDTO;
import com.bcgogo.wx.user.WXSubscribeScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-29
 * Time: 09:29
 */
public class MirrorWXScanListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(MirrorWXScanListener.class);

  private WXEventObj eventObj;

  public MirrorWXScanListener(WXEventObj eventObj) {
    this.eventObj = eventObj;
  }

  @Override
  public void run() {
    try {
      WXRequestParam param = (WXRequestParam) eventObj.getSource();
      Long sceneId = NumberUtil.longValue(param.getEventKey());
      rxScan(param.getPublicNo(), param.getOpenId(), sceneId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 已经关注后扫描
   *
   * @param publicNo
   * @param openId
   * @param sceneId
   * @return
   * @throws java.io.IOException
   */
  private void rxScan(String publicNo, String openId, Long sceneId) throws Exception {
    if (sceneId == null) return;
    String lockKey = sceneId + "_" + publicNo + "_" + openId;
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_SCAN, lockKey)) {
        return;
      }
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      LOG.info("rxScan publicNo:{},sceneId:{}", publicNo, sceneId);
      WXQRCodeDTO qrCodeDTO = wxUserService.getWXQRCodeDTOBySceneId(publicNo, NumberUtil.longValue(sceneId));
      if (QRScene.ACCIDENT.equals(qrCodeDTO.getScene())) { //添加事故专员
        IUserService userService = ServiceManager.getService(IUserService.class);
        if (isAccidentSpecialistExist(qrCodeDTO.getShopId(), openId)) {
          return;
        }
        List<AccidentSpecialistDTO> specialistDTOs = userService.getAccidentSpecialistByOpenId(qrCodeDTO.getShopId(), null);
        if (CollectionUtil.isNotEmpty(specialistDTOs) && specialistDTOs.size() >= 3) {
          return;
        }
        AccidentSpecialistDTO specialistDTO = new AccidentSpecialistDTO();
        specialistDTO.setOpenId(openId);
        specialistDTO.setDeleted(DeletedType.FALSE);
        specialistDTO.setShopId(qrCodeDTO.getShopId());
        userService.saveOrUpdateAccidentSpecialist(specialistDTO);
        LOG.info("specialistDTO save end：specialistDTO_id is " + specialistDTO.getId());
        //统计粉丝来源
        WXSubscribeRecordDTO recordDTO = new WXSubscribeRecordDTO();
        recordDTO.setPublicNo(publicNo);
        recordDTO.setOpenId(openId);
        recordDTO.setSubscribeTime(System.currentTimeMillis());
        recordDTO.setScene(WXSubscribeScene.ACCIDENG_SPECIALIST);
        recordDTO.setDeleted(DeletedType.FALSE);
        wxUserService.saveOrUpdateWXSubscribeRecord(recordDTO);
        //发送提醒消息
        IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
        String content = WXConstant.CONTENT_ACCIDENT_SPECIALIST;
        sender.sendCustomTextMsg(publicNo, openId, content);

      } else {  //后视镜添加微信用户
        WXQRCodeSearchCondition condition = new WXQRCodeSearchCondition();
        condition.setPublicNo(publicNo);
        condition.setSceneId(sceneId);
        condition.setScene(QRScene.MIRROR_USER);
        AppUserWXQRCodeDTO codeDTO = wxUserService.getAppUserWXQRCodeDTOBySceneId(NumberUtil.longValue(sceneId));
        if (codeDTO == null || StringUtil.isEmpty(codeDTO.getAppUserNo())) {
          LOG.warn("mirror:scan AppUserWXQRCode isn't existed");
          return;
        }
        LOG.info("mirror:rxScan,sceneId is {},and openId is {}", sceneId, openId);
        if (isAppWXUserExist(codeDTO.getAppUserNo(), openId)) {
          LOG.info("mirror:app wxUser is existed");
          return;
        }
        LOG.info("mirror:rxScan,save AppWXUser");
        AppWXUserDTO appWXUserDTO = new AppWXUserDTO();
        appWXUserDTO.setOpenId(openId);
        appWXUserDTO.setAppUserNo(codeDTO.getAppUserNo());
        appWXUserDTO.setDeleted(DeletedType.FALSE);
        wxUserService.saveOrUpdateAppWXUser(appWXUserDTO);

        //统计粉丝来源
        WXSubscribeRecordDTO recordDTO = new WXSubscribeRecordDTO();
        recordDTO.setPublicNo(publicNo);
        recordDTO.setOpenId(openId);
        recordDTO.setSubscribeTime(System.currentTimeMillis());
        recordDTO.setScene(WXSubscribeScene.SCAN);
        recordDTO.setDeleted(DeletedType.FALSE);
        wxUserService.saveOrUpdateWXSubscribeRecord(recordDTO);
        //发送提醒消息
        IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
        String content = WXConstant.CONTENT_SUBSCRIBE_LIYIXING;
        sender.sendCustomTextMsg(publicNo, openId, content);
      }

    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_SCAN, lockKey);
    }
  }

  private boolean isAppWXUserExist(String appUserNo, String openId) {
    IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
    AppWXUserDTO userDTO = CollectionUtil.getFirst(wxUserService.getAppWXUserDTO(appUserNo, openId));
    return userDTO != null ? true : false;
  }

  private boolean isAccidentSpecialistExist(Long shopId, String openId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    AccidentSpecialistDTO specialistDTO = CollectionUtil.getFirst(userService.getAccidentSpecialistByOpenId(shopId, openId));
    return specialistDTO != null ? true : false;
  }

}
