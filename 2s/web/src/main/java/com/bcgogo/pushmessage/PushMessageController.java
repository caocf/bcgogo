package com.bcgogo.pushmessage;

import com.bcgogo.common.CookieUtil;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.MessageDayRange;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.remind.dto.message.MessageNumberResult;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.remind.message.AbstractMessageController;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageFeedbackRecordDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;
import com.bcgogo.txn.service.pushMessage.IApplyPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 下午5:30
 */
@Controller
@RequestMapping("/pushMessage.do")
public class PushMessageController extends AbstractMessageController {
  private static final Logger LOG = LoggerFactory.getLogger(PushMessageController.class);
  @Autowired
  private IPushMessageService pushMessageService;

  @RequestMapping(params = "method=getPushMessageData")
  @ResponseBody
  public Object getPushMessageData(HttpServletRequest request,String uuid) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Long shopId=WebUtil.getShopId(request);
    Long userGroupId=WebUtil.getUserGroupId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userGroupId == null) throw new Exception("userGroup id is null!");
      ShopDTO shopDTO = configService.getShopById(shopId);
      int pushMessagePopRequestInterval = NumberUtil.intValue(configService.getConfig(ConfigConstant.PUSH_MESSAGE_POP_REQUEST_INTERVAL, ConfigConstant.CONFIG_SHOP_ID),600000);
      int pushMessagePopHideInterval= NumberUtil.intValue(configService.getConfig(ConfigConstant.PUSH_MESSAGE_POP_HIDE_INTERVAL, ConfigConstant.CONFIG_SHOP_ID),30000);
      Map<String, Object> result = new HashMap<String, Object>();
        result.put("uuid",uuid);//消息组件必须的
        result.put("requestInterval",pushMessagePopRequestInterval);//消息组件必须的
        result.put("hideInterval",pushMessagePopHideInterval);//消息组件必须的
        result.put("serverTime",String.valueOf(System.currentTimeMillis()));

      List<PushMessageType> pushMessageTypes = getPromptPushMessageTypes(userGroupId, shopDTO);
      PushMessageDTO pushMessageDTO = pushMessageService.getLatestPushMessageDTO(shopId,shopId, shopDTO.getShopKind(), pushMessageTypes.toArray(new PushMessageType[pushMessageTypes.size()]));

      if(pushMessageDTO!=null){
        pushMessageService.generatePushMessagePromptRedirectUrl(pushMessageDTO,false);


        result.put("title",pushMessageDTO.getTitle());//消息组件必须的
        List<PushMessageDTO> dataList = new ArrayList<PushMessageDTO>();
        dataList.add(pushMessageDTO);
        result.put("data",dataList);
      }
      return result;
    } catch (Exception e) {
      LOG.debug("/pushMessage.do?method=getPushMessageData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  private List<PushMessageType> getPromptPushMessageTypes(Long userGroupId, ShopDTO shopDTO) {
    List<PushMessageType> pushMessageTypeList = new ArrayList<PushMessageType>();

    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);

    pushMessageTypeList.add(PushMessageType.MATCHING_RECOMMEND_SUPPLIER);
    pushMessageTypeList.add(PushMessageType.MATCHING_RECOMMEND_CUSTOMER);

    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE")) {
      pushMessageTypeList.add(PushMessageType.ACCESSORY);
      pushMessageTypeList.add(PushMessageType.ACCESSORY_PROMOTIONS);
      pushMessageTypeList.add(PushMessageType.RECOMMEND_ACCESSORY_BY_QUOTED);
      pushMessageTypeList.add(PushMessageType.BUYING_MATCH_ACCESSORY);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER")) {
      pushMessageTypeList.add(PushMessageType.BUYING_INFORMATION);
      pushMessageTypeList.add(PushMessageType.BUSINESS_CHANCE_SELL_WELL);
      pushMessageTypeList.add(PushMessageType.BUSINESS_CHANCE_LACK);
      pushMessageTypeList.add(PushMessageType.QUOTED_BUYING_IGNORED);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.SALES_ACCESSORY.BASE")) {
      pushMessageTypeList.add(PushMessageType.ACCESSORY_MATCH_RESULT);
    }
    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER")) {
      pushMessageTypeList.add(PushMessageType.QUOTED_BUYING_INFORMATION);
      pushMessageTypeList.add(PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
    }

    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST")) {
      pushMessageTypeList.add(PushMessageType.SYS_ACCEPT_APPOINT);
      pushMessageTypeList.add(PushMessageType.APP_CANCEL_APPOINT);
      pushMessageTypeList.add(PushMessageType.APP_APPLY_APPOINT);
      pushMessageTypeList.add(PushMessageType.OVERDUE_APPOINT_TO_SHOP);
      pushMessageTypeList.add(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);
    }

    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,
        "WEB.SCHEDULE.ENQUIRY_LIST")) {
      pushMessageTypeList.add(PushMessageType.APP_SUBMIT_ENQUIRY);
    }

//    if (privilegeService.verifierUserGroupResource(shopDTO.getShopVersionId(), userGroupId, ResourceType.menu,"")) {
      pushMessageTypeList.add(PushMessageType.VEHICLE_FAULT_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP);
    pushMessageTypeList.add(PushMessageType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP);
//    }

    //      pushMessageTypeList.remove(PushMessageType.MATCHING_RECOMMEND_SUPPLIER); 汽配汽修都有
    //版本权限过滤
    if(ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())){//汽配需要过滤
      pushMessageTypeList.remove(PushMessageType.VEHICLE_FAULT_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP);
      pushMessageTypeList.remove(PushMessageType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP);

      pushMessageTypeList.remove(PushMessageType.APP_SUBMIT_ENQUIRY);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
      pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_INFORMATION);
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION_MATCH_RESULT);
      pushMessageTypeList.remove(PushMessageType.SYS_ACCEPT_APPOINT);
      pushMessageTypeList.remove(PushMessageType.APP_CANCEL_APPOINT);
      pushMessageTypeList.remove(PushMessageType.APP_APPLY_APPOINT);
      pushMessageTypeList.remove(PushMessageType.OVERDUE_APPOINT_TO_SHOP);
      pushMessageTypeList.remove(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP);
    }else{
      pushMessageTypeList.remove(PushMessageType.BUYING_INFORMATION);
      pushMessageTypeList.remove(PushMessageType.QUOTED_BUYING_IGNORED);
      pushMessageTypeList.remove(PushMessageType.MATCHING_RECOMMEND_CUSTOMER);
      pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
    }
    //--------需求特意过滤--------------
    pushMessageTypeList.remove(PushMessageType.ACCESSORY);
    pushMessageTypeList.remove(PushMessageType.ACCESSORY_PROMOTIONS);
    pushMessageTypeList.remove(PushMessageType.ACCESSORY_MATCH_RESULT);
    pushMessageTypeList.remove(PushMessageType.BUYING_MATCH_ACCESSORY);

    return pushMessageTypeList;
  }
  @RequestMapping(params = "method=processPushMessageFeedback")
  @ResponseBody
  public Object processPushMessageFeedback(HttpServletRequest request,Long messageId,PushMessageReceiverStatus pushMessageReceiverStatus,PushMessageType pushMessageType,PushMessageFeedbackType pushMessageFeedbackType,Long pushMessageReceiverId) {
    Long shopId=WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (messageId == null) throw new Exception("MessageId is null!");
      if (pushMessageType == null) throw new Exception("pushMessageDTO pushMessageType is null!");
      if (pushMessageReceiverStatus == null) throw new Exception("pushMessageReceiverDTO status is null!");
      if (pushMessageReceiverId == null) throw new Exception("pushMessageReceiverDTO Id is null!");
      if(PushMessageReceiverStatus.READ.equals(pushMessageReceiverStatus) && pushMessageFeedbackType==null){
        throw new Exception("pushMessageReceiverDTO status is READ,PushMessageFeedbackRecordDTO pushMessageFeedbackType is null!");
      }
      PushMessageFeedbackRecordDTO pushMessageFeedbackRecordDTO = null;
      if(pushMessageFeedbackType!=null){
        pushMessageFeedbackRecordDTO = new PushMessageFeedbackRecordDTO();
        pushMessageFeedbackRecordDTO.setMessageId(messageId);
        pushMessageFeedbackRecordDTO.setType(pushMessageFeedbackType);
        pushMessageFeedbackRecordDTO.setCreateTime(System.currentTimeMillis());
        pushMessageFeedbackRecordDTO.setShopId(shopId);
      }

      PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
      pushMessageReceiverDTO.setId(pushMessageReceiverId);
      pushMessageReceiverDTO.setStatus(pushMessageReceiverStatus);
      pushMessageService.processPushMessageFeedback(pushMessageFeedbackRecordDTO, pushMessageReceiverDTO);

      IApplyPushMessageService applyPushMessageService = ServiceManager.getService(IApplyPushMessageService.class);
      if (PushMessageFeedbackType.WEB_NO_HIT.equals(pushMessageFeedbackType) || PushMessageFeedbackType.WEB_HIT.equals(pushMessageFeedbackType) ) {
        applyPushMessageService.addCancelRecommendAssociatedCount(messageId);
      }

      if(pushMessageFeedbackType!=null && !PushMessageFeedbackType.WEB_NO_HIT.equals(pushMessageFeedbackType) && !PushMessageFeedbackType.CLIENT_NO_HIT.equals(pushMessageFeedbackType)){
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopId);
        if(CollectionUtils.isNotEmpty(userIds)){
          for(Long userId : userIds){
            pushMessageService.updatePushMessageCategoryStatNumberInMemCache(shopId, userId,PushMessageCategory.valueOfPushMessageType(pushMessageType));
          }
        }
      }

      return new Result();
    } catch (Exception e) {
      LOG.debug("/pushMessage.do?method=processPushMessageFeedback");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }



  @RequestMapping(params = "method=deletePushMessageByPushMessageReceiverId")
  @ResponseBody
  public Object deletePushMessageByPushMessageReceiverId(HttpServletRequest request, Long[] pushMessageReceiverIds) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      if (!ArrayUtils.isEmpty(pushMessageReceiverIds)) {
        pushMessageService.deletePushMessageReceiverById(shopId,pushMessageReceiverIds);
      }
      return result;
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.error("pushMessage.do?method=deletePushMessageByPushMessageReceiverId,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

  /**
   * @param request
   * @return
   */
  @RequestMapping(params = "method=searchReceiverPushMessage")
  @ResponseBody
  public Object searchReceiverPushMessage(HttpServletRequest request, SearchMessageCondition searchMessageCondition) {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      Long userGroupId = WebUtil.getUserGroupId(request);
      Long userId = WebUtil.getUserId(request);

      IApplyService applyService = ServiceManager.getService(IApplyService.class);
      searchMessageCondition.setShopVersionId(shopVersionId);
      searchMessageCondition.setShopId(shopId);
      searchMessageCondition.setUserId(userId);
      searchMessageCondition.setUserGroupId(userGroupId);
      List<PushMessageType> pushMessageTypeList = getPushMessageTypes(searchMessageCondition);
      List<PushMessageDTO> pushMessageDTOList = pushMessageService.searchReceiverPushMessageDTOList(pushMessageTypeList,searchMessageCondition);
      Map<String,List<PushMessageDTO>> dataMap = new LinkedHashMap<String, List<PushMessageDTO>>();
      List<PushMessageDTO> items = null;
      if(CollectionUtils.isNotEmpty(pushMessageDTOList)){
        Iterator<PushMessageDTO> iterator = pushMessageDTOList.iterator();
        while (iterator.hasNext()){
          PushMessageDTO pushMessageDTO = iterator.next();
          if(PushMessageCategory.RelatedApplyMessage.equals(PushMessageCategory.valueOfPushMessageType(pushMessageDTO.getType()))){
            ShopRelationInviteDTO shopRelationInviteDTO = applyService.getShopRelationInviteDTOByInvitedShopIdAndId(shopId, pushMessageDTO.getRelatedObjectId());
            if(shopRelationInviteDTO!=null){
              pushMessageDTO.setShopRelationInviteDTO(shopRelationInviteDTO);
            }else{
              iterator.remove();
              continue;
            }
          }
          items = dataMap.get(pushMessageDTO.getCreateDateStr());
          if(items==null){
            items = new ArrayList<PushMessageDTO>();
          }
          items.add(pushMessageDTO);
          dataMap.put(pushMessageDTO.getCreateDateStr(),items);
        }
      }
      Integer total = pushMessageService.countReceiverPushMessageDTO(pushMessageTypeList,searchMessageCondition);
      Pager pager = new Pager(total,searchMessageCondition.getStartPageNo(), searchMessageCondition.getMaxRows());
      List<Object> result = new ArrayList<Object>();
      result.add(dataMap);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("pushMessage.do?method=searchReceiverPushMessage");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
  @RequestMapping(params = "method=receiverPushMessageList")
  public String receiverPushMessageList(HttpServletRequest request, ModelMap model, SearchMessageCondition searchMessageCondition) {
    model.addAttribute("messageDayRanges", MessageDayRange.values());
    model.addAttribute("searchMessageCondition",searchMessageCondition);
    model.addAttribute("isWholesalerVersion",ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));

    if(searchMessageCondition.getCategory()!=null){
      model.addAttribute("currPage",searchMessageCondition.getCategory());
    }else if(searchMessageCondition.getTopCategory()!=null && searchMessageCondition.getCategory()==null){
      model.addAttribute("currPage",searchMessageCondition.getTopCategory());
    }else if(searchMessageCondition.getTopCategory()==null && searchMessageCondition.getCategory()==null){
      model.addAttribute("currPage","ReceiverMessage");
    }
    this.getMessageNum(request,model);
    return "/remind/pushMessage/pushMessageList";
  }

  private List<PushMessageType> getPushMessageTypes(SearchMessageCondition searchMessageCondition) {
    List<PushMessageType> pushMessageTypeList = new ArrayList<PushMessageType>();
    if(searchMessageCondition.getCategory()!=null){
      pushMessageTypeList.addAll(PushMessageCategory.pushMessageCategoryTypeMap.get(searchMessageCondition.getCategory()));
    }else if(searchMessageCondition.getTopCategory()!=null && searchMessageCondition.getCategory()==null){
      List<PushMessageCategory> pushMessageCategoryList = PushMessageTopCategory.pushMessageCategoryMap.get(searchMessageCondition.getTopCategory());
      for(PushMessageCategory pushMessageCategory : pushMessageCategoryList){
        pushMessageTypeList.addAll(PushMessageCategory.pushMessageCategoryTypeMap.get(pushMessageCategory));
      }
    }else if(searchMessageCondition.getTopCategory()==null && searchMessageCondition.getCategory()==null){
      pushMessageTypeList.addAll(PushMessageCategory.getAllPushMessageTypeListInCategory());
    }
    pushMessageService.filterPushMessageTypes(searchMessageCondition.getUserGroupId(),searchMessageCondition.getShopVersionId(),pushMessageTypeList);
    return pushMessageTypeList;
  }

  @RequestMapping(params = "method=getPushMessageCategoryStatNumber")
  @ResponseBody
  public Object getPushMessageCategoryStatNumber(HttpServletRequest request) {
    try {
      Long shopId = WebUtil.getShopId(request);
      Long userId = WebUtil.getUserId(request);
      if(shopId==null) throw new Exception("shop Id is null!");
      if(userId==null) throw new Exception("user Id is null!");
      Map<String,Integer> data = new HashMap<String, Integer>();
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      for (PushMessageCategory pushMessageCategory : PushMessageCategory.values()) {
        Map<PushMessageReceiverStatus, Integer> map = pushMessageService.getPushMessageCategoryStatNumberInMemCache(pushMessageCategory, shopId, userId);
        if (MapUtils.isNotEmpty(map)) {
          for (Map.Entry<PushMessageReceiverStatus, Integer> entry : map.entrySet()) {
            data.put(pushMessageCategory + "_" + entry.getKey(), entry.getValue());//RelatedNoticeMessage_UNREAD
          }
        }
      }
      return new Result(data);
    } catch (Exception e) {
      LOG.error("pushMessage.do?method=getPushMessageCategoryStatNumber");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=readPushMessageByPushMessageReceiverId")
  @ResponseBody
  public Object readPushMessageByPushMessageReceiverId(HttpServletRequest request, Long[] pushMessageReceiverIds) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      if (!ArrayUtils.isEmpty(pushMessageReceiverIds)) {
        pushMessageService.readPushMessageReceiverById(shopId, pushMessageReceiverIds);
      }
      return result;
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.error("pushMessage.do?method=readPushMessageByPushMessageReceiverId,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

  @RequestMapping(params = "method=readPushMessageByPushMessageCategory")
  @ResponseBody
  public Object readPushMessageByPushMessageCategory(HttpServletRequest request,PushMessageTopCategory topCategory, PushMessageCategory category) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      List<PushMessageType> pushMessageTypeList = new ArrayList<PushMessageType>();
      if(category!=null){
        pushMessageTypeList.addAll(PushMessageCategory.pushMessageCategoryTypeMap.get(category));
      }else if(topCategory!=null &&category==null){
        List<PushMessageCategory> pushMessageCategoryList = PushMessageTopCategory.pushMessageCategoryMap.get(topCategory);
        for(PushMessageCategory pmc : pushMessageCategoryList){
          pushMessageTypeList.addAll(PushMessageCategory.pushMessageCategoryTypeMap.get(pmc));
        }
      }else if(topCategory==null && category==null){
        pushMessageTypeList.addAll(PushMessageCategory.getAllPushMessageTypeListInCategory());
      }

      if (CollectionUtils.isNotEmpty(pushMessageTypeList)) {
        pushMessageService.readPushMessageReceiverByPushMessageType(shopId, WebUtil.getUserId(request), pushMessageTypeList.toArray(new PushMessageType[pushMessageTypeList.size()]));
      }
      return result;
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.error("pushMessage.do?method=readPushMessageByPushMessageCategory,shopId :{}" + e.getMessage(), shopId, e);
      return new Result("网络异常", false);
    }
  }

  @RequestMapping(params = "method=getNumberOfMessageCenter")
  @ResponseBody
  public Object getNumberOfMessageCenter(HttpServletRequest request, HttpServletResponse response) {
    MessageNumberResult result = new MessageNumberResult();
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    try {
      Integer applyMessageCount = 0,noticeMessageCount=0,stationMessageCount=0;
      List<PushMessageType> applyMessageTypeList = new ArrayList<PushMessageType>();
      List<PushMessageType> noticeMessageTypeList = new ArrayList<PushMessageType>();
      List<PushMessageType> stationMessageTypeList = new ArrayList<PushMessageType>();
      for(PushMessageCategory pushMessageCategory : PushMessageCategory.values()){
        Map<PushMessageReceiverStatus, Integer> map = pushMessageService.getPushMessageCategoryStatNumberInMemCache(pushMessageCategory, shopId, userId);
        if(MapUtils.isNotEmpty(map)){
          switch (PushMessageTopCategory.valueOfPushMessageCategory(pushMessageCategory)) {
            case ApplyMessage: {
              applyMessageCount+= NumberUtil.intValue(map.get(PushMessageReceiverStatus.UNREAD));
              applyMessageTypeList.addAll(pushMessageCategory.getAllPushMessageTypeListInCategory());
              break;
            }
            case NoticeMessage: {
              noticeMessageCount+= NumberUtil.intValue(map.get(PushMessageReceiverStatus.UNREAD));
              noticeMessageTypeList.addAll(pushMessageCategory.getAllPushMessageTypeListInCategory());
              break;
            }
            case StationMessage: {
              stationMessageCount+= NumberUtil.intValue(map.get(PushMessageReceiverStatus.UNREAD));
              stationMessageTypeList.addAll(pushMessageCategory.getAllPushMessageTypeListInCategory());
              break;
            }

          }
        }
      }
      boolean isLock = !PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/pushMessage.do?method=receiverPushMessageList");

      result.getRequest().put("count", String.valueOf(applyMessageCount));
      result.getRequest().put("unreadDetailsUrl", "pushMessage.do?method=receiverPushMessageList&topCategory=ApplyMessage&receiverStatus=UNREAD");
      result.getRequest().put("allDetailsUrl", "pushMessage.do?method=receiverPushMessageList&topCategory=ApplyMessage");
      result.getRequest().put("lock", String.valueOf(isLock || CollectionUtils.isEmpty(applyMessageTypeList)));
      result.getNotification().put("count", String.valueOf(noticeMessageCount));
      result.getNotification().put("unreadDetailsUrl", "pushMessage.do?method=receiverPushMessageList&topCategory=NoticeMessage&receiverStatus=UNREAD");
      result.getNotification().put("allDetailsUrl", "pushMessage.do?method=receiverPushMessageList&topCategory=NoticeMessage");
      result.getNotification().put("lock", String.valueOf(isLock || CollectionUtils.isEmpty(noticeMessageTypeList)));
      result.getNews().put("count", String.valueOf(stationMessageCount));
      result.getNews().put("unreadDetailsUrl", "pushMessage.do?method=receiverPushMessageList&topCategory=StationMessage&category=&receiverStatus=UNREAD");
      result.getNews().put("allDetailsUrl", "pushMessage.do?method=receiverPushMessageList&topCategory=StationMessage");
      result.getNews().put("lock", String.valueOf(isLock || CollectionUtils.isEmpty(stationMessageTypeList)));
      CookieUtil.rebuildCookiesForUserGuide(request, response, false);
      result.setNoticeTotalNumber(applyMessageCount + stationMessageCount + noticeMessageCount);
      return result;
    } catch (Exception e) {
      LOG.error("pushMessage.do?method=getNumberOfMessageCenter,shopId :{}" + e.getMessage(), shopId, e);
      return result;
    }
  }
}
