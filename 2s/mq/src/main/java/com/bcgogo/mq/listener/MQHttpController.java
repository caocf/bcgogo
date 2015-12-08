package com.bcgogo.mq.listener;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.mq.message.MQMessageDTO;
import com.bcgogo.mq.message.MQMessageItemDTO;
import com.bcgogo.mq.message.MQTalkMessageDTO;
import com.bcgogo.mq.service.MQHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统MQ暂时支持http协议.
 * Author: ndong
 * Date: 15-6-8
 * Time: 下午7:28
 */
@Controller
public class MQHttpController {
  private static final Logger LOG = LoggerFactory.getLogger(MQHttpController.class);

  @ResponseBody
  @RequestMapping(value = "/mirror/isOnLine/{userName}", method = RequestMethod.GET)
  public ApiResponse isOnLine(@PathVariable("userName") String userName) {
    try {
      ApiResponse response = new ApiResponse();
      boolean result = MQHelper.isOnLine(userName);
      LOG.info("userName:{} isOnLine:{}", userName, result);
      response.setMessage(result ? "ONLINE" : null);
      return response;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.FAILED);
    }
  }


  @ResponseBody
  @RequestMapping(value = "/mirror/push", method = RequestMethod.POST)
  public ApiResponse push(MQTalkMessageDTO talkMessageDTO) throws Exception {
    MQHelper.push(talkMessageDTO);
    return MessageCode.toApiResponse(MessageCode.SUCCESS);
  }


}
