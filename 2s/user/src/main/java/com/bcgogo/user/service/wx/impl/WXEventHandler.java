package com.bcgogo.user.service.wx.impl;

import com.bcgogo.config.cache.ShopConfigCacheManager;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.thread.ThreadPool;
import com.bcgogo.user.service.wx.IWXHandler;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.user.service.wx.impl.WXMsgSender;
import com.bcgogo.user.service.wx.listener.*;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXEvent;
import com.bcgogo.wx.WXRequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * 接收事件推送
 * User: ndong
 * Date: 14-9-1
 * Time: 下午10:00
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WXEventHandler implements IWXHandler {
  public static final Logger LOG = LoggerFactory.getLogger(WXMsgSender.class);

  /**
   * 微信服务器在五秒内收不到响应会断掉连接,重新推送请求过来
   * 回复空字符串""。用异步线程处理,客服消息回复
   *
   * @param param
   * @return
   * @throws Exception
   */
  @Override
  public String doHandle(WXRequestParam param) throws Exception {
    WXEvent wxEvent = param.getWxEvent();
    if (wxEvent == null) {
      LOG.error("wx;wxEvent is null");
      return null;
    }
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    switch (wxEvent) {
      case SUBSCRIBE:
        notifySubscribe(param);
        return sender.getEmptyMsg();
      case SCAN:
        notifyScan(param);
        return sender.getEmptyMsg();
      case UN_SUBSCRIBE:
        notifyUnSubscribe(param);
        return sender.getEmptyMsg();
      case MENU_CLICK:
        return rxMenuClickEvent(param);
      case MENU_VIEW:
        LOG.info("menu view data is {}", param);
        return sender.getEmptyMsg();
      case TEMPLATE_SEND_JOB_FINISH:
        notifyTemplateSendJob(param);
        return sender.getEmptyMsg();
      case MASS_SEND_JOB_FINISH:
        notifyMassSendJob(param);
        return sender.getEmptyMsg();
      case LOCATION:
        return sender.getEmptyMsg();
      default:
        LOG.error("wx:new event type,{}", wxEvent);
        return sender.getEmptyMsg();
    }
  }

  private String rxMenuClickEvent(WXRequestParam param) throws Exception {
    String publicNo = param.getPublicNo();
    String openId = param.getOpenId();
    String eventKey = param.getEventKey();
    LOG.info("wx:MENU_CLICK,eventKey is {},and openId is {}", eventKey, openId);
    //车辆绑定
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
    String result = null;
    if (WXConstant.MENU_EVENT_KEY_BIND.equals(eventKey)) {
      String content = WXConstant.CONTENT_VEHICLE_BIND_S1.replace("{B_URL}", WXHelper.vehicleBindUrl(openId));
      result = sender.getTextMsgXml(openId, publicNo, content);
    } else if (WXConstant.MENU_EVENT_BILL_HISTORY.equals(eventKey)) {  //历史账单
      result = wxUserService.getHistoryBill(publicNo, openId);
    } else if (WXConstant.MENU_EVENT_MEMBER_CARD.equals(eventKey)) {  //会员卡
      result = wxUserService.getMemberCardNews(publicNo, openId);
    } else if (WXConstant.MENU_EVENT_VEHICLE_LIST.equals(eventKey)) { //我的车辆
      result = wxUserService.getVehicleNews(publicNo, openId);
    } else if (WXConstant.MENU_EVENT_VEHICLE_VIOLATE.equals(eventKey)) {   //违章查询
      notifyVRegulation(param);
    } else if (eventKey.startsWith(WXConstant.MENU_TYPE_TEXT)) {
      Long shopId= NumberUtil.longValue(eventKey.substring(WXConstant.MENU_TYPE_TEXT.length()));
      ShopConfig configDTO = ShopConfigCacheManager.getConfig(shopId, ShopConfigScene.WX_MENU_TEXT);
      result = sender.getTextMsgXml(openId, publicNo,configDTO.getValue());
    }
    return StringUtil.isEmpty(result) ? sender.getEmptyMsg() : result;
  }

  private void notifyVRegulation(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new WXVRegulationListener(new WXEventObj(param)));
  }

  private void notifyUnSubscribe(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new WXUnSubscribeListener(new WXEventObj(param)));
  }

  private void notifyTemplateSendJob(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new WXTemplateSendJobListener(new WXEventObj(param)));
  }

  private void notifyMassSendJob(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new WXMassSendJobListener(new WXEventObj(param)));
  }

  private void notifyScan(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new WXScanListener(new WXEventObj(param)));
  }

  private void notifySubscribe(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new WXSubscribeListener(new WXEventObj(param)));
  }


}
