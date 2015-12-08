package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.qr.WXQRCodeSearchCondition;
import com.bcgogo.wx.user.ShopWXUserDTO;
import com.bcgogo.wx.user.WXSubscribeRecordDTO;
import com.bcgogo.wx.user.WXSubscribeScene;
import com.bcgogo.wx.user.WXUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-24
 * Time: 下午6:11
 */
public class WXScanListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(WXScanListener.class);

  private WXEventObj eventObj;

  public WXScanListener(WXEventObj eventObj){
    this.eventObj=eventObj;
  }

  @Override
  public void run() {
    try {
      WXRequestParam param=(WXRequestParam)eventObj.getSource();
      Long sceneId= NumberUtil.longValue(param.getEventKey());
      rxScan(param.getPublicNo(),param.getOpenId(),sceneId);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }

  /**
   * 已经关注后扫描
   * @param publicNo
   * @param openId
   * @param sceneId
   * @return
   * @throws java.io.IOException
   */
  private void rxScan(String publicNo,String openId,Long sceneId) throws Exception {
    if(sceneId==null) return;
    String lockKey=sceneId+"_"+publicNo+"_"+openId;
    try{
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_SCAN, lockKey)){
        return;
      }
      IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
      LOG.info("wx:rxScan,sceneId is {},and openId is {}",sceneId,openId);
      WXQRCodeSearchCondition condition=new WXQRCodeSearchCondition();
      condition.setPublicNo(publicNo);
      condition.setSceneId(sceneId);
      WXQRCodeDTO qrCodeDTO= CollectionUtil.getFirst(wxUserService.getWXQRCodeDTO(condition));
      if(qrCodeDTO==null||qrCodeDTO.getShopId()==null){
        LOG.warn("wx:scan shop scene isn't existed");
        return;
      }
      Long shopId=qrCodeDTO.getShopId();
      //发送提醒消息
      IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
      String content=WXHelper.toStandardWelcomeWord(wxUserService.getShopWelcomeWord(shopId, openId));
      sender.sendCustomTextMsg(publicNo,openId,content);
      if(isShopUserExist(shopId,openId)){
        LOG.info("wx:scan shop user is existed");
        return;
      }
      LOG.info("wx:rxScan,save shop WXUser");
      wxUserService.saveShopWXUser(publicNo,openId,shopId);
      //统计粉丝来源
      WXSubscribeRecordDTO recordDTO=new WXSubscribeRecordDTO();
      recordDTO.setPublicNo(publicNo);
      recordDTO.setOpenId(openId);
      recordDTO.setSubscribeTime(System.currentTimeMillis());
      recordDTO.setShopId(shopId);
      recordDTO.setScene(WXSubscribeScene.SCAN);
      recordDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXSubscribeRecord(recordDTO);

    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_SCAN,lockKey);
    }
  }

  public boolean isShopUserExist(Long shopId,String openId){
    IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
    ShopWXUserDTO shopWXUserDTO= wxUserService.getShopWXUser(shopId, openId);
    return shopWXUserDTO!=null?true:false;
  }

}
