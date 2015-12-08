package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.notification.service.WXService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXMsgStatus;
import com.bcgogo.wx.WXRequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-24
 * Time: 下午6:07
 */
public class WXTemplateSendJobListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(WXTemplateSendJobListener.class);

  private WXEventObj eventObj;

  public WXTemplateSendJobListener(WXEventObj eventObj){
    this.eventObj=eventObj;
  }

  @Override
  public void run() {
    WXRequestParam param=(WXRequestParam)eventObj.getSource();
    rxTemplateSendStatusReport(param.getMsgId(),param.getStatus());
  }

  /**
   * 消息模版发送成功状态报告
   * @param msgId
   * @param statusStr
   * @return
   */
  private void rxTemplateSendStatusReport(String msgId,String statusStr) {
    if(StringUtil.isEmpty(msgId)) return;
    String lockKey=msgId;
    try{
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_TEMPLATE_SEND_STATUS_REPORT, lockKey)){
        return;
      }
      IWXService wxService= ServiceManager.getService(WXService.class);
      WXMsgDTO wxMsgDTO=wxService.getWXMsgDTOByMsgId(msgId);
      if(wxMsgDTO==null){
        LOG.error("wx:rxSendStatusReport can't get msg,msgId={}",msgId);
        return;
      }
      WXMsgStatus statusEnum=WXMsgStatus.getStatusEnum(statusStr);
      if(WXMsgStatus.SUCCESS.equals(statusEnum)){
        wxMsgDTO.setStatus(WXMsgStatus.SUCCESS);
        wxMsgDTO.setRemark("发送成功");
      }else {
        wxMsgDTO.setStatus(WXMsgStatus.FAILED);
        wxMsgDTO.setRemark(statusStr);
      }
      wxService.saveOrUpdateWXMsg(wxMsgDTO);
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_TEMPLATE_SEND_STATUS_REPORT,lockKey);
    }
  }

}
