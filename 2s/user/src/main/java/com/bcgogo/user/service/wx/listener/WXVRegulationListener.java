package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXRequestParam;
import org.apache.velocity.runtime.directive.Break;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-12
 * Time: 下午4:51
 */
public class WXVRegulationListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(WXUnSubscribeListener.class);

  private WXEventObj eventObj;

  public WXVRegulationListener(WXEventObj eventObj){
    this.eventObj=eventObj;
  }

  @Override
  public void run(){
    WXRequestParam param=(WXRequestParam)eventObj.getSource();
    String publicNo=param.getPublicNo();
    String openId=param.getOpenId();
    String eventKey=param.getEventKey();
    String lockKey= ConcurrentScene.WX_HANDLE_V_REGULATION+publicNo+"_"+openId+eventKey;
    try{
      LOG.info("wx:wx v_regulation listener exec");
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_MENU_CLICK, lockKey)){
        LOG.warn("wx:rxMenuClickEvent has been handling");
        return;
      }
      IWXUserService wxUserService=ServiceManager.getService(IWXUserService.class);
      String result=wxUserService.getVRegulationMsg(openId);
      IWXMsgSender sender=ServiceManager.getService(IWXMsgSender.class);
      result=StringUtil.isNotEmpty(result)?result:wxUserService.getVehicleBindRemindMsg(publicNo,openId);
      sender.sendCustomTextMsg(publicNo,openId,result);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_MENU_CLICK,lockKey);
    }
  }

}
