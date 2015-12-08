package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StopWatchUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.user.WXAccountDTO;
import com.bcgogo.wx.user.WXSubscribeRecordDTO;
import com.bcgogo.wx.user.WXSubscribeScene;
import com.bcgogo.wx.user.WXUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-27
 * Time: 下午1:48
 */
public class WXSubscribeListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(WXSubscribeListener.class);

  private WXEventObj eventObj;

  public WXSubscribeListener(WXEventObj eventObj){
    this.eventObj=eventObj;
  }

  @Override
  public void run() {
    try {
      WXRequestParam param=(WXRequestParam)eventObj.getSource();
      rxSubscribe(param.getPublicNo(),param.getOpenId(),param.getCreateTime(),param.getEventKey());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  public void rxSubscribe(String publicNo,String openId,Long createTime,String sceneId)throws Exception{
    String lockKey= ConcurrentScene.WX_RESP_HANDLE_SUBSCRIBE+openId+"_"+createTime;
    try{
      StopWatchUtil sw = new StopWatchUtil("wx:rxSubscribe", "start");
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_SUBSCRIBE, lockKey)){
        LOG.warn("wx:rxSubscribe has been handling,openId is {}",openId);
        return;
      }
      IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
      WXUserDTO userDTO=wxUserService.getWXUserFromPlat(publicNo,openId);
      if(userDTO==null||userDTO.getSubscribe()=="0") {
        LOG.error("userDTO isn't exist");
        return;
      }
      if(isUserExist(openId)){
        LOG.info("wx:subscribed user is exist,openId is {}",openId);
        return;
      }
      //save wx user
      userDTO.setPublicNo(publicNo);
      userDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXUser(userDTO);
      //根据sceneId,关联店铺
      Long shopId=null;
      if(StringUtil.isNotEmpty(sceneId)){
        sceneId=sceneId.split("_")[1];
        WXQRCodeDTO qrCodeDTO=wxUserService.getWXQRCodeDTOBySceneId(publicNo,NumberUtil.longValue(sceneId));
        if(qrCodeDTO==null||qrCodeDTO.getShopId()==null){
          LOG.error("wx:rxSubscribe shop scene isn't existed");
          return;
        }
        shopId=qrCodeDTO.getShopId();
        wxUserService.saveShopWXUser(publicNo,openId,shopId);
      }
      //统计粉丝来源
      WXSubscribeRecordDTO recordDTO=new WXSubscribeRecordDTO();
      recordDTO.setPublicNo(publicNo);
      recordDTO.setOpenId(openId);
      recordDTO.setSubscribeTime(System.currentTimeMillis());
      recordDTO.setShopId(shopId);
      if(shopId==null){
        recordDTO.setScene(WXSubscribeScene.SUBSCRIBE);
      }else {
        recordDTO.setScene(WXSubscribeScene.SUBSCRIBE_SHOP);
      }
      recordDTO.setDeleted(DeletedType.FALSE);
      wxUserService.saveOrUpdateWXSubscribeRecord(recordDTO);
      //发送提示消息
      IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
      String content=WXHelper.toStandardWelcomeWord(wxUserService.getShopWelcomeWord(shopId,openId));
      sender.sendCustomTextMsg(publicNo,openId,content);
      sw.stopAndPrintLog();
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_SUBSCRIBE,lockKey);
    }
  }

  private boolean isUserExist(String openId){
    IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
    WXUserDTO userDTO=wxUserService.getWXUserDTOByOpenId(openId);
    return userDTO!=null?true:false;
  }


  /***********************************************************订阅号没有发送客服消息接口，如接口不能及时响应，可用下面处理机制********************************************************************************************/

//  /**
//   * 订阅号没有发送客服消息接口，下面处理机制暂时保留
//   * @param publicNo
//   * @param openId
//   * @param sceneId
//   * @return
//   * @throws Exception
//   */
//  public String rxSubscribeForSubcribe(String publicNo,String openId,Long createTime,Long sceneId) throws Exception {
//    IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
//    IWXUserManager userManager=ServiceManager.getService(IWXUserManager.class);
//    WXAccountDTO accountDTO=userManager.getCachedWXAccount(publicNo);
//    String mKey=ConcurrentScene.WX_RESP_HANDLE_SUBSCRIBE.getName()+openId+"_"+createTime;  //标识同一个消息
//    LOG.info("rxSubscribe,openId is {} ,createTime is {}",openId,createTime);
//    String content=WXConstant.CONTENT_SUBSCRIBE.replace("{NAME}",accountDTO.getName()).replace("{B_URL}",WXHelper.vehicleBindUrl(openId));
//    ExeStatus exeStatus=(ExeStatus)MemCacheAdapter.get(mKey);
//    //微信服务器在五秒内收不到响应会断掉连接
//    if(exeStatus==null){
//      doSubscribeHandle(publicNo,openId,sceneId,createTime);
//      return sender.getTextMsgXml(openId,publicNo,content);
//    }
//    //处理微信重发机制
//    switch (exeStatus){
//      case FINISHED:
//        return sender.getTextMsgXml(openId,publicNo,content);
//      case START:
//        return doTryingSubscribe(publicNo,openId,createTime);
//      case EXCEPTION:
//        content=WXConstant.CONTENT_SCENE_SUBSCRIBE_EXCEPTION.replace("{NAME}", accountDTO.getName());
//        return sender.getTextMsgXml(openId,publicNo,content);
//    }
//    return null;
//  }
//
//
//  private void doSubscribeHandle(String publicNo,String openId,Long sceneId,Long createTime){
//    String mKey= ConcurrentScene.WX_RESP_HANDLE_SUBSCRIBE+openId+"_"+createTime;
//    try{
//      StopWatchUtil sw = new StopWatchUtil("wx:doSubscribeHandle", "start");
//      MemCacheAdapter.set(mKey, ExeStatus.START, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_RESP_HANDLE_START));
//      IWXUserManager userManager= ServiceManager.getService(IWXUserManager.class);
//      WXUserDTO userDTO=userManager.getWXUserFromPlat(publicNo,openId);
//      if(userDTO==null||userDTO.getSubscribe()=="0") {
//        return;
//      }
//      //save wx user
//      userDTO.setPublicNo(publicNo);
//      userDTO.setDeleted(DeletedType.FALSE);
//      userManager.saveOrUpdateWXUser(userDTO);
//      IWXService wxService=ServiceManager.getService(IWXService.class);
//      //根据sceneId,关联店铺
//      Long shopId=null;
//      if(sceneId!=null){
//        WXQRCodeDTO qrCodeDTO=wxService.getWXQRCodeDTO(sceneId);
//        if(qrCodeDTO==null||qrCodeDTO.getShopId()==null){
//          LOG.error("wx:rxSubscribe shop scene isn't existed");
//          return;
//        }
//        shopId=qrCodeDTO.getShopId();
//        userManager.saveShopWXUser(publicNo,openId,shopId);
//      }
//      //统计粉丝来源
//      WXSubscribeRecordDTO recordDTO=new WXSubscribeRecordDTO();
//      recordDTO.setPublicNo(publicNo);
//      recordDTO.setOpenId(openId);
//      recordDTO.setSubscribeTime(System.currentTimeMillis());
//      recordDTO.setShopId(shopId);
//      if(shopId==null){
//        recordDTO.setScene(WXSubscribeScene.SUBSCRIBE);
//      }else {
//        recordDTO.setScene(WXSubscribeScene.SUBSCRIBE_SHOP);
//      }
//      recordDTO.setDeleted(DeletedType.FALSE);
//      wxService.saveOrUpdateWXSubscribeRecord(recordDTO);
//      MemCacheAdapter.set(mKey,ExeStatus.FINISHED,new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_RESP_HANDLE_FINISH));
//      sw.stopAndPrintLog();
//    }catch (Exception e){
//      LOG.error(e.getMessage(),e);
//      MemCacheAdapter.set(mKey,ExeStatus.EXCEPTION,new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_RESP_HANDLE_START));
//    }
//  }

//  private String doTryingSubscribe(String publicNo,String openId,Long createTime) throws Exception {
//    IWXUserManager userManager=ServiceManager.getService(IWXUserManager.class);
//    WXAccountDTO accountDTO=userManager.getCachedWXAccount(publicNo);
//    IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
//    String content=WXConstant.CONTENT_SUBSCRIBE.replace("{NAME}",accountDTO.getName()).replace("{B_URL}", WXHelper.vehicleBindUrl(openId));
//    String mKey=ConcurrentScene.WX_RESP_HANDLE_SUBSCRIBE+openId+"_"+createTime;  //标识同一个消息
//    int tryingCount=1;
//    while (tryingCount<=10){
//      LOG.debug("wx:the "+tryingCount+" times,try to get resp_handle result,and handled msg key is {}",mKey);
//      ExeStatus exeStatus=(ExeStatus)MemCacheAdapter.get(mKey);
//      if(ExeStatus.FINISHED.equals(exeStatus)){
//        return sender.getTextMsgXml(openId,publicNo,content);
//      }else if(ExeStatus.EXCEPTION.equals(exeStatus)){
//        content=WXConstant.CONTENT_SCENE_SUBSCRIBE_EXCEPTION.replace("{NAME}", accountDTO.getName());
//        return sender.getTextMsgXml(openId,publicNo,content);
//      }
//      Thread.sleep(WXConstant.RESP_HANDLE_EXPIRY);
//      tryingCount++;
//    }
//    LOG.error("wx:doTryingSubscribe failed,openId is {}",openId);
//    return null;
//  }



}
