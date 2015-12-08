package com.bcgogo.user.service.wx.listener;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.event.WXEventObj;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.notification.service.WXService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.*;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXSendStatusReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-24
 * Time: 下午6:06
 */
public class WXMassSendJobListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(WXMassSendJobListener.class);

  private WXEventObj eventObj;

  public WXMassSendJobListener(WXEventObj eventObj){
    this.eventObj=eventObj;
  }

  @Override
  public void run() {
    WXRequestParam param=(WXRequestParam)eventObj.getSource();
    rxMassMsgSendStatusReport(param);
  }



  private void rxMassMsgSendStatusReport(WXRequestParam param) {
    String msgId=param.getMsgId();
    if(StringUtil.isEmpty(msgId)) {
      return;
    }
    String lockKey=msgId;
    try{
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WX_EVENT_MASS_MSG_SEND_STATUS_REPORT, lockKey)){
        return;
      }
      IWXService wxService= ServiceManager.getService(WXService.class);
      WXSendStatusReportDTO reportDTO = wxService.getWXSendStatusReportDTOByMsgId(msgId);
      if(reportDTO!=null){
        LOG.warn("wx:mass status report has been existed,msgId is {}",msgId);
        return;
      }
      //save status report
      reportDTO=new WXSendStatusReportDTO();
      reportDTO.setMsgId(msgId);
      reportDTO.setCreateTime(param.getCreateTime());
      reportDTO.setEvent(param.getWxEvent());
      reportDTO.setMsgType(param.getMsgType());
      reportDTO.setPublicNo(param.getPublicNo());
      reportDTO.setFromUserName(param.getOpenId());
      reportDTO.setStatus(param.getStatus());
      reportDTO.setSentCount(param.getSentCount());
      reportDTO.setTotalCount(param.getTotalCount());
      reportDTO.setErrorCount(param.getErrorCount());
      reportDTO.setFilterCount(param.getFilterCount());
      wxService.saveOrUpdateWXSendStatusReport(reportDTO);
      //update WXMsg
      WXMsgDTO wxMsgDTO=wxService.getWXMsgDTOByMsgId(msgId);
      if (wxMsgDTO != null){
        if(reportDTO.getTotalCount()>0){ //todo 逻辑待优化
          wxMsgDTO.setStatus(WXMsgStatus.SUCCESS);
          wxService.saveOrUpdateWXMsg(wxMsgDTO);
        }
      }else {
        LOG.error("wx:rxSendStatusReport can't get msg,msgId={}",msgId);
      }
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.WX_EVENT_MASS_MSG_SEND_STATUS_REPORT,lockKey);
    }
  }

}
