package com.bcgogo.user.service.wx.listener;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-29
 * Time: 11:39
 */
public class MirrorWXUnSubscribeListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(MirrorWXUnSubscribeListener.class);

  private WXEventObj eventObj;

  public MirrorWXUnSubscribeListener(WXEventObj eventObj) {
    this.eventObj = eventObj;
  }

  @Override
  public void run() {
    try {
      WXRequestParam param = (WXRequestParam) eventObj.getSource();
      rxUnSubscribe(param.getOpenId(), param.getCreateTime());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private String rxUnSubscribe(String openId, Long createTime) throws Exception {
    String lockKey = ConcurrentScene.WX_RESP_HANDLE_UN_SUBSCRIBE.getName() + openId + createTime;
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_UN_SUBSCRIBE, lockKey)) {
        return null;
      }
      LOG.info("wx:rxUnSubscribe,openId is {}", openId);
      IWXUserService iwxUserService = ServiceManager.getService(IWXUserService.class);
      //删除用户
      WXUserDTO userDTO = iwxUserService.getWXUserDTOByOpenId(openId);
      if (userDTO != null) {
        userDTO.setDeleted(DeletedType.TRUE);
        iwxUserService.saveOrUpdateWXUser(userDTO);
      }
      //解除和所有后视镜的关联
      List<AppWXUserDTO> appWXUserDTOs = iwxUserService.getAppWXUserDTO(null, openId);
      if (CollectionUtil.isNotEmpty(appWXUserDTOs)) {
        for (AppWXUserDTO appWXUserDTO : appWXUserDTOs) {
          appWXUserDTO.setDeleted(DeletedType.TRUE);
          iwxUserService.saveOrUpdateAppWXUser(appWXUserDTO);
        }
      }
      //解除该用户添加的所有店铺的事故专员
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<AccidentSpecialistDTO> specialistDTOs = userService.getAccidentSpecialistByOpenId(null, openId);
      if (CollectionUtil.isNotEmpty(specialistDTOs)) {
        for (AccidentSpecialistDTO specialistDTO : specialistDTOs) {
          specialistDTO.setDeleted(DeletedType.TRUE);
        }
        userService.saveOrUpdateAccidentSpecialist(specialistDTOs.toArray(new AccidentSpecialistDTO[specialistDTOs.size()]));
      }
      //记录取消关注
      WXSubscribeRecordDTO recordDTO = new WXSubscribeRecordDTO();
      recordDTO.setPublicNo(userDTO != null ? userDTO.getPublicNo() : null);
      recordDTO.setOpenId(openId);
      recordDTO.setSubscribeTime(System.currentTimeMillis());
      recordDTO.setScene(WXSubscribeScene.UN_SUBSCRIBE);
      recordDTO.setDeleted(DeletedType.FALSE);
      ServiceManager.getService(IWXUserService.class).saveOrUpdateWXSubscribeRecord(recordDTO);
      return null;
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_UN_SUBSCRIBE, lockKey);
    }
  }
}
