package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
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
 * Date: 14-10-24
 * Time: 下午5:36
 */
public class WXUnSubscribeListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(WXUnSubscribeListener.class);

  private WXEventObj eventObj;

  public WXUnSubscribeListener(WXEventObj eventObj){
    this.eventObj=eventObj;
  }

  @Override
  public void run() {
    try {
      WXRequestParam param=(WXRequestParam)eventObj.getSource();
      rxUnSubscribe(param.getOpenId(),param.getCreateTime());
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }

  private String rxUnSubscribe(String openId,Long createTime) throws Exception {
    String lockKey= ConcurrentScene.WX_RESP_HANDLE_UN_SUBSCRIBE.getName()+openId+createTime;
    try{
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_UN_SUBSCRIBE, lockKey)){
        return null;
      }
      LOG.info("wx:rxUnSubscribe,openId is {}",openId);
      IWXUserService userService= ServiceManager.getService(IWXUserService.class);
      //删除用户
      WXUserDTO userDTO=userService.getWXUserDTOByOpenId(openId);
      if(userDTO!=null){
        userDTO.setDeleted(DeletedType.TRUE);
        userService.saveOrUpdateWXUser(userDTO);
      }
      //解除车的关联
      List<WXUserVehicleDTO> userVehicleDTOs=userService.getWXUserVehicleByOpenId(openId);
      if(CollectionUtil.isNotEmpty(userVehicleDTOs)){
        for(WXUserVehicleDTO dto:userVehicleDTOs){
          dto.setDeleted(DeletedType.TRUE);
          userService.saveOrUpdateWXUserVehicle(dto);
        }
      }
      //解除和店铺的关联
      List<ShopWXUserDTO> shopWXUserDTOs= userService.getShopWXUserByOpenId(openId);
      if(CollectionUtil.isNotEmpty(shopWXUserDTOs)){
        for(ShopWXUserDTO dto:shopWXUserDTOs){
          dto.setDeleted(DeletedType.TRUE);
          userService.saveOrUpdateShopWXUser(dto);
        }
      }
      IWXUserService wxUserService=ServiceManager.getService(IWXUserService.class);
      //记录取消关注
      WXSubscribeRecordDTO recordDTO=new WXSubscribeRecordDTO();
      recordDTO.setPublicNo(userDTO!=null?userDTO.getPublicNo():null);
      recordDTO.setOpenId(openId);
      recordDTO.setSubscribeTime(System.currentTimeMillis());
      recordDTO.setScene(WXSubscribeScene.UN_SUBSCRIBE);
      recordDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXSubscribeRecord(recordDTO);
      return null;
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_UN_SUBSCRIBE,lockKey);
    }
  }
}
