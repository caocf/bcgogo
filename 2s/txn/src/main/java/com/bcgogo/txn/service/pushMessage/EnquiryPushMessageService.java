package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.AppointConstant;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.notification.velocity.EnquiryVelocityContext;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Zhangjuntao
 * Date: 13-11-15
 * Time: 上午10:47
 */
@Component
public class EnquiryPushMessageService extends AbstractMessageService implements IEnquiryPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(ApplyPushMessageService.class);
  private static final String VELOCITY_PARAMETER = "context";
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public boolean createAppSubmitEnquiryMessageToShop(AppEnquiryParameter parameter) throws Exception {
    if (parameter == null) {
      LOG.error("ShopAppointParameter is null");
      return false;
    }
    String result = parameter.validate();
    if (StringUtil.isNotEmpty(result)) {
      LOG.error(result);
      return false;
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(parameter.getAppUserNo(), null);
    if (appUserDTO == null) {
      LOG.error("appUserDTO is null!");
      return false;
    }
    EnquiryVelocityContext enquiryVelocityContext = new EnquiryVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    enquiryVelocityContext.setShopDTO(shopDTO);
    enquiryVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, enquiryVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT, "APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT, "APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT_TEXT, "APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT_TEXT");

    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppSubmitEnquiryMessageToShop(parameter, appUserDTO, promptContent, PushMessageType.APP_SUBMIT_ENQUIRY, PushMessageSourceType.APP_SUBMIT_ENQUIRY);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.enquiryId, parameter.getEnquiryId().toString());
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getEnquiryId().toString() + "," + parameter.getShopId().toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTO.setTitle(AppointConstant.APP_SUBMIT_ENQUIRY_MESSAGE_TITLE);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setContent(content);
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, true);
    return true;
  }

  @Override
  public boolean createShopQuoteEnquiryMessageToApp(ShopQuoteEnquiryParameter parameter) throws Exception {
    if (parameter == null) {
      LOG.error("ShopAppointParameter is null");
      return false;
    }
    String result = parameter.validate();
    if (StringUtil.isNotEmpty(result)) {
      LOG.error(result);
      return false;
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(parameter.getAppUserNo(), null);
    if (appUserDTO == null) {
      LOG.error("appUserDTO is null!");
      return false;
    }
    EnquiryVelocityContext enquiryVelocityContext = new EnquiryVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    enquiryVelocityContext.setShopDTO(shopDTO);
    enquiryVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, enquiryVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_QUOTE_TO_APP_MESSAGE_CONTENT, "SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT, "SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT_TEXT, "SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT_TEXT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createShopQuoteEnquiryMessageToApp(parameter, appUserDTO, promptContent, PushMessageType.SHOP_QUOTE_TO_APP, PushMessageSourceType.SHOP_QUOTE_TO_APP);
    pushMessageDTO.setTitle(AppointConstant.SHOP_QUOTE_TO_APP_MESSAGE_TITLE);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setContent(content);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getEnquiryId() + "," + parameter.getShopId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);
    return true;
  }

}
