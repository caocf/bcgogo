package com.bcgogo.remind.message;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.MessageDayRange;
import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.remind.dto.MessageReceiverDTO;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.service.messageCenter.IMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-24
 * Time: 下午12:24
 */
@Controller
@RequestMapping("/stationMessage.do")
public class StationMessageController extends AbstractMessageController{
  private static final Logger LOG = LoggerFactory.getLogger(StationMessageController.class);
  private static final String SEND_STATION_MESSAGE_LIST = "/remind/pushMessage/sendStationMessageList";
  private static final String ADD_STATION_MESSAGE = "/remind/pushMessage/addStationMessage";

  @RequestMapping(params = "method=showSendStationMessageList")
  public String showSendStationMessageList(HttpServletRequest request, ModelMap model) {
    getMessageNum(request,model);
    model.addAttribute("messageDayRanges", MessageDayRange.values());
    return SEND_STATION_MESSAGE_LIST;
  }

  @RequestMapping(params = "method=searchSenderStationMessages")
  @ResponseBody
  public Object searchSenderStationMessages(HttpServletRequest request, SearchMessageCondition searchMessageCondition) {
    try {
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      Long userGroupId = WebUtil.getUserGroupId(request);
      Long userId = WebUtil.getUserId(request);

      searchMessageCondition.setShopVersionId(shopVersionId);
      searchMessageCondition.setShopId(shopId);
      searchMessageCondition.setUserId(userId);
      searchMessageCondition.setUserGroupId(userGroupId);
      List<PushMessageDTO> pushMessageDTOList = pushMessageService.searchSenderPushMessageDTOs(searchMessageCondition);
      Integer total = pushMessageService.countSenderMessages(searchMessageCondition);
      Pager pager = new Pager(total,searchMessageCondition.getStartPageNo(), searchMessageCondition.getMaxRows());
      List<Object> result = new ArrayList<Object>();
      result.add(pushMessageDTOList);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("stationMessage.do?method=searchSenderStationMessages");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=createStationMessage")
  public String createStationMessage(ModelMap model, HttpServletRequest request, String messageReceivers) {
    this.getMessageNum(request,model);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setMessageReceivers(messageReceivers);
    messageDTO.setShopId(WebUtil.getShopId(request));
    messageDTO.setType(MessageType.PROMOTIONS_MESSAGE);
    List<MessageReceiverDTO> messageReceiverDTOList = new ArrayList<MessageReceiverDTO>();
    if (StringUtils.isNotBlank(messageDTO.getMessageReceivers())) {
      String[] customerIds = StringUtils.split(messageDTO.getMessageReceivers(), ",");
      Set<Long> receiverIdSet = new HashSet<Long>();
      for (String customerId : customerIds) {
        if (StringUtils.isNotBlank(customerId)) {
          receiverIdSet.add(Long.valueOf(customerId));
        }
      }
      Map<Long, CustomerDTO> customerDTOMap = customerService.getCustomerByIdSet(WebUtil.getShopId(request), receiverIdSet);

      for (Long receiverId : receiverIdSet) {
        CustomerDTO customerDTO = customerDTOMap.get(receiverId);
        if (customerDTO != null) {
          MessageReceiverDTO messageReceiverDTO = new MessageReceiverDTO();

          messageReceiverDTO.setReceiveMobile(customerDTO.getMobile());
          messageReceiverDTO.setReceiverId(receiverId);
          messageReceiverDTO.setReceiverName(customerDTO.getName());
          messageReceiverDTO.setReceiverShopId(customerDTO.getCustomerShopId());
          messageReceiverDTOList.add(messageReceiverDTO);
        }
      }
    }
    messageDTO.setMessageReceiverDTOList(messageReceiverDTOList);
    model.put("messageValidTimePeriods", MessageValidTimePeriod.values());
    model.put("messageDTO", messageDTO);
    return ADD_STATION_MESSAGE;

  }

  @RequestMapping(params = "method=deleteStationMessage")
  @ResponseBody
  public Object deleteStationMessage(HttpServletRequest request, String pushMessageIds) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      IMessageService messageService = ServiceManager.getService(IMessageService.class);
      String[] messageIdsStr = StringUtil.isEmpty(pushMessageIds) ? null : pushMessageIds.split(",");
      if (!ArrayUtils.isEmpty(messageIdsStr)) {
        messageService.deleteSenderPushMessage(shopId, WebUtil.getUserId(request), ArrayUtil.convertToLong(messageIdsStr));
      }
      return result;
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.error("stationMessage.do?method=deleteStationMessage,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }
}
