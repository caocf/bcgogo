package com.bcgogo.client;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.txn.service.client.*;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-22
 * Time: 下午2:28
 */
@Component
public class ClientAssortedMessageGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);
  @Autowired
  private IClientApplyService applyService;
  @Autowired
  private IClientOrderService orderService;
  @Autowired
  private IClientAccessoryBuyingService accessoryBuyingService;
  @Autowired
  private IClientSystemService systemService;
  @Autowired
  private IClientAppointOrderService clientAppointOrderService;

  public ClientAssortedMessageResult getMessages
      (HttpServletRequest request, Long sessionId, Long shopId, String userNo, String apiVersion) {
    ClientAssortedMessageResult result = new ClientAssortedMessageResult(ConfigUtils.getClientNextRequestTimeInterval());
    try {
      if (!ConfigUtils.isPushMessageSwitchOn()) {
        LOG.info("pushMessageSwitch if off");
        ClientAssortedMessage message=new ClientAssortedMessage();
        result.getMessages().add(message);
        return result;
      }
      String basePath = WebUtil.getBasePath(request);
      ClientAssortedMessage message;
      if (shopId != null) {
        //关联消息
        message = applyService.getApplyMessage(shopId, basePath, userNo, apiVersion);
        if (message != null) result.getMessages().add(message);

        //求购咨询 配件报价
        List<ClientAssortedMessage> accessoryBuyingMessages= accessoryBuyingService.getAccessoryBuyingMessages(shopId, basePath, userNo, apiVersion);
        if (CollectionUtil.isNotEmpty(accessoryBuyingMessages)) result.getMessages().addAll(accessoryBuyingMessages);

        //订单消息
        message = orderService.getOrderStatMessage(shopId, basePath, userNo, apiVersion);
        if (message != null) result.getMessages().add(message);

        //预约消息
        message = clientAppointOrderService.getAppointOrderMessages(shopId, basePath, userNo, apiVersion);
        if (message != null) result.getMessages().add(message);
      }
      //系统消息
      message = systemService.getSystemMessages(shopId, basePath, userNo, apiVersion);
      if (message != null) result.getMessages().add(message);
    } catch (Exception e) {
      LOG.debug("method=getMessages");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

}
