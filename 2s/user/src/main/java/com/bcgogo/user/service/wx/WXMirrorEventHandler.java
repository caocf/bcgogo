package com.bcgogo.user.service.wx;

import com.bcgogo.event.WXEventObj;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.thread.ThreadPool;
import com.bcgogo.user.service.wx.listener.MirrorWXScanListener;
import com.bcgogo.user.service.wx.listener.MirrorWXSubscribeListener;
import com.bcgogo.user.service.wx.listener.MirrorWXUnSubscribeListener;
import com.bcgogo.wx.WXEvent;
import com.bcgogo.wx.WXRequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-13
 * Time: 14:31
 */
@Component
public class WXMirrorEventHandler implements IWXHandler {
  public static final Logger LOG = LoggerFactory.getLogger(WXMirrorEventHandler.class);

  @Override
  public String doHandle(WXRequestParam param) throws Exception {
    WXEvent wxEvent = param.getWxEvent();
    if (wxEvent == null) {
      LOG.error("wx;wxEvent is null");
      return null;
    }
    IWXMsgSender sender = ServiceManager.getService(IWXMsgSender.class);
    LOG.info("wxEvent,wxEvent is \n" + wxEvent);
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
      default:
        return sender.getEmptyMsg();
    }
  }

  private void notifyUnSubscribe(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new MirrorWXUnSubscribeListener(new WXEventObj(param)));
  }

  private void notifyScan(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new MirrorWXScanListener(new WXEventObj(param)));
  }

  private void notifySubscribe(WXRequestParam param) {
    Executor executor = ThreadPool.getInstance();
    executor.execute(new MirrorWXSubscribeListener(new WXEventObj(param)));
  }

}
