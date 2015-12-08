package com.bcgogo.remind.message;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.enums.shop.InviteCountStatus;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageTopCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.messageCenter.IMessageService;
import com.bcgogo.txn.service.messageCenter.INoticeService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.userGuide.UserGuideHandler;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-28
 * Time: 上午9:23
 */
public class AbstractMessageController {
  @Autowired
  public IMessageService messageService;
  @Autowired
  public INoticeService noticeService;
  @Autowired
  public IApplyService applyService;
  @Autowired
  public UserGuideHandler userGuideHandler;

  public void setMessageService(IMessageService messageService) {
    this.messageService = messageService;
  }

  public void setNoticeService(INoticeService noticeService) {
    this.noticeService = noticeService;
  }

  public void setApplyService(IApplyService applyService) {
    this.applyService = applyService;
  }

  public void getMessageNum(HttpServletRequest request, ModelMap model) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);

    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    for(PushMessageCategory pushMessageCategory : PushMessageCategory.values()){
      Map<PushMessageReceiverStatus, Integer> map = pushMessageService.getPushMessageCategoryStatNumberInMemCache(pushMessageCategory, shopId, userId);
      if(MapUtils.isNotEmpty(map)){
        for(Map.Entry<PushMessageReceiverStatus,Integer> entry:map.entrySet()){
          model.put(pushMessageCategory+"_"+entry.getKey(), entry.getValue());//RelatedNoticeMessage_UNREAD
        }
      }
    }
  }

}
