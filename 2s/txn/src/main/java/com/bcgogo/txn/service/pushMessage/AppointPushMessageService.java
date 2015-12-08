package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.Apns.GsmAPNSAdapter;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.service.UMPush.GSMUMPushAdapter;
import com.bcgogo.config.service.UMPush.YFUMPushAdapter;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.AppointConstant;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.notification.velocity.AppointVelocityContext;
import com.bcgogo.notification.velocity.ShopAdvertVelocityContext;
import com.bcgogo.notification.velocity.ViolateRegulationRecordVelocityContext;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageSourceDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.SysAppointParameter;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.IAppointOrderService;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-9-9
 * Time: 上午9:52
 */
@Component
public class AppointPushMessageService extends AbstractMessageService implements IAppointPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(ApplyPushMessageService.class);
  private static final String VELOCITY_PARAMETER = "context";
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public boolean createShopAcceptAppointMessage(ShopAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = shopAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_ACCEPT_APPOINT_MESSAGE_CONTENT, "SHOP_ACCEPT_APPOINT_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createShopAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.SHOP_ACCEPT_APPOINT, PushMessageSourceType.SHOP_ACCEPT_APPOINT);
    pushMessageDTO.setTitle(AppointConstant.SHOP_ACCEPT_APPOINT_MESSAGE_TITLE);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getAppointOrderId() + "," + parameter.getShopId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);

    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return true;
  }

  private AppUserDTO shopAppointValidate(ShopAppointParameter parameter) {
    if (parameter == null) {
      LOG.error("ShopAppointParameter is null");
      return null;
    }
    String result = parameter.validate();
    if (StringUtil.isNotEmpty(result)) {
      LOG.error(result);
      return null;
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(parameter.getAppUserNo(), null);
    if (appUserDTO == null) {
      LOG.error("appUserDTO is null!");
      return null;
    }
    return appUserDTO;
  }

  @Override
  public boolean createShopRejectAppointMessage(ShopAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = shopAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_REJECT_APPOINT_MESSAGE_CONTENT, "SHOP_REJECT_APPOINT_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createShopAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.SHOP_REJECT_APPOINT, PushMessageSourceType.SHOP_REJECT_APPOINT);
    pushMessageDTO.setTitle(AppointConstant.SHOP_REJECT_APPOINT_MESSAGE_TITLE);
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);

    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return true;
  }

  @Override
  public boolean createShopCancelAppointMessage(ShopAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = shopAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_CANCEL_APPOINT_MESSAGE_CONTENT, "SHOP_CANCEL_APPOINT_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createShopAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.SHOP_CANCEL_APPOINT, PushMessageSourceType.SHOP_CANCEL_APPOINT);
    pushMessageDTO.setTitle(AppointConstant.SHOP_CANCEL_APPOINT_MESSAGE_TITLE);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getAppointOrderId() + "," + parameter.getShopId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);

    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return true;
  }

  @Override
  public boolean createShopFinishAppointMessage(ShopAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = shopAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_FINISH_APPOINT_MESSAGE_CONTENT, "SHOP_FINISH_APPOINT_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createShopAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.SHOP_FINISH_APPOINT, PushMessageSourceType.SHOP_FINISH_APPOINT);
    pushMessageDTO.setTitle(AppointConstant.SHOP_FINISH_APPOINT_MESSAGE_TITLE);
    //单据ID shopId
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getAppointOrderId() + "," + parameter.getShopId() + "," + parameter.getServiceOrderId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));

    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);
    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return true;
  }

  @Override
  public boolean createShopChangeAppointMessage(ShopAppointParameter parameter) throws Exception {
    if (parameter == null) {
      LOG.error("ShopAppointParameter is null");
      return false;
    }
    String result = parameter.changeAppointValidate();
    if (StringUtil.isNotEmpty(result)) {
      LOG.error(result);
      return false;
    }
    if (!parameter.needSenChangeMessage()) {
      return false;
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(parameter.getAppUserNo(), null);
    if (appUserDTO == null) {
      LOG.error("appUserDTO is null!");
      return false;
    }
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_CHANGE_APPOINT_MESSAGE_CONTENT, "SHOP_CHANGE_APPOINT_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createShopAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.SHOP_CHANGE_APPOINT, PushMessageSourceType.SHOP_CHANGE_APPOINT);
    pushMessageDTO.setTitle(AppointConstant.SHOP_CHANGE_APPOINT_MESSAGE_TITLE);
    //单据ID shopId
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getAppointOrderId() + "," + parameter.getShopId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);

    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return true;
  }

  @Override
  public boolean createAppCancelAppointMessage(AppAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = appAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_CANCEL_APPOINT_MESSAGE_CONTENT, "APP_CANCEL_APPOINT_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.APP_CANCEL_APPOINT_MESSAGE_CONTENT, "APP_CANCEL_APPOINT_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.APP_CANCEL_APPOINT_MESSAGE_CONTENT_TEXT, "APP_CANCEL_APPOINT_MESSAGE_CONTENT_TEXT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.APP_CANCEL_APPOINT, PushMessageSourceType.APP_CANCEL_APPOINT);
    pushMessageDTO.setTitle(AppointConstant.APP_CANCEL_APPOINT_MESSAGE_TITLE);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setContent(content);
    //单据ID shopId
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, parameter.getAppointOrderId().toString());
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getAppointOrderId() + "," + parameter.getShopId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, true);
    return true;
  }

  private AppUserDTO appAppointValidate(AppAppointParameter parameter) {
    if (parameter == null) {
      LOG.error("ShopAppointParameter is null");
      return null;
    }
    String result = parameter.validate();
    if (StringUtil.isNotEmpty(result)) {
      LOG.error(result);
      return null;
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(parameter.getAppUserNo(), null);
    if (appUserDTO == null) {
      LOG.error("appUserDTO is null!");
      return null;
    }
    return appUserDTO;
  }

  private AppUserDTO sysAppointValidate(SysAppointParameter parameter) {
    if (parameter == null) {
      LOG.error("ShopAppointParameter is null");
      return null;
    }
    String result = parameter.validate();
    if (StringUtil.isNotEmpty(result)) {
      LOG.error(result);
      return null;
    }
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(parameter.getAppUserNo(), null);
    if (appUserDTO == null) {
      LOG.error("appUserDTO is null!");
      return null;
    }
    return appUserDTO;
  }


  @Override
  public boolean createAppApplyAppointMessage(AppAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = appAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_APPLY_APPOINT_MESSAGE_CONTENT, "APP_APPLY_APPOINT_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.APP_APPLY_APPOINT_MESSAGE_CONTENT, "APP_APPLY_APPOINT_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.APP_APPLY_APPOINT_MESSAGE_CONTENT_TEXT, "APP_APPLY_APPOINT_MESSAGE_CONTENT_TEXT");

    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.APP_APPLY_APPOINT, PushMessageSourceType.APP_APPLY_APPOINT);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, parameter.getAppointOrderId().toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTO.setTitle(AppointConstant.APP_APPLY_APPOINT_MESSAGE_TITLE);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setContent(content);
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, true);
    //推送消息
    if (shopDTO != null && shopDTO.getId() != null) {
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<UserDTO> userDTOs = userService.getShopUser(shopDTO.getId());
      if (CollectionUtils.isNotEmpty(userDTOs)) {
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        for (UserDTO userDTO : userDTOs) {
          if (userDTO != null && userDTO.isNeedToPushMessage()) {
            if (appUserService.hasAppointRight(shopDTO.getShopVersionId(), userDTO.getUserGroupId())) {
              if (StringUtils.isNotBlank(userDTO.getUmDeviceToken())) {
                YFUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), userDTO.getUmDeviceToken());
              }
              if (StringUtils.isNotBlank(userDTO.getDeviceToken())) {
                //todo IOS 待开发
              }
            }
          }
        }
      }
    }
    return true;
  }

  @Override
  public boolean createSysAcceptAppointMessage(SysAppointParameter parameter) throws Exception {
    AppUserDTO appUserDTO = sysAppointValidate(parameter);
    if (appUserDTO == null) return false;
    AppointVelocityContext appointVelocityContext = new AppointVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getShopId());
    appointVelocityContext.setShopDTO(shopDTO);
    appointVelocityContext.from(parameter);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SYS_ACCEPT_APPOINT_MESSAGE_CONTENT, "SYS_ACCEPT_APPOINT_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SYS_ACCEPT_APPOINT_MESSAGE_CONTENT, "SYS_ACCEPT_APPOINT_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SYS_ACCEPT_APPOINT_MESSAGE_CONTENT_TEXT, "SYS_ACCEPT_APPOINT_MESSAGE_CONTENT_TEXT");

    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createSysAppointMessage(parameter, appUserDTO, promptContent, PushMessageType.SYS_ACCEPT_APPOINT, PushMessageSourceType.SYS_ACCEPT_APPOINT);
    pushMessageDTO.setContent(content);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setTitle(AppointConstant.SYS_ACCEPT_APPOINT_MESSAGE_TITLE);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, parameter.getAppointOrderId().toString());
    paramMap.put(PushMessageParamsKeyConstant.AppParams, parameter.getAppointOrderId() + "," + shopDTO.getId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);
    return true;
  }

  @Override
  public void createOverdueAppointRemindMessage(int limit) throws Exception {
    IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
    Long[] intervals = ConfigUtils.getOverdueAppointRemindIntervals();
    int start = 0;
    List<AppointOrderDTO> appointOrderDTOList;
    Long currentTime = System.currentTimeMillis(),
      upTime = currentTime + intervals[1],
      downTime = currentTime + intervals[0];
    int count = 0;
    while (true) {
      appointOrderDTOList = appointOrderService.getRemindedAppointOrder(upTime, downTime, start, limit);
      if (appointOrderDTOList.size() == 0 || count > 100) {
        if (count > 100) {
          LOG.error("createOverdueAppointRemindMessage 循环超过100次，appointOrderDTOList：{}，start：{} ，limit：{} ，upTime：{}，downTime：{} 。"
            , new Object[]{appointOrderDTOList.toString(), start, limit, upTime, downTime});
        }
        break;
      }
      createOverdueAppointRemindMessage(appointOrderDTOList);
      start += limit;
      count++;
    }
  }

  private void createOverdueAppointRemindMessage(List<AppointOrderDTO> appointOrderDTOList) throws Exception {
    List<PushMessageDTO> pushMessageDTOList2App = new ArrayList<PushMessageDTO>();
    List<PushMessageDTO> pushMessageDTOList2Shop = new ArrayList<PushMessageDTO>();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> orderIds = new HashSet<Long>();
    Set<Long> shopIds = new HashSet<Long>();
    for (AppointOrderDTO orderDTO : appointOrderDTOList) {
      appUserNoSet.add(orderDTO.getAppUserNo());
      orderIds.add(orderDTO.getId());
      shopIds.add(orderDTO.getShopId());
    }
    Map<String, AppUserDTO> appUserDTOMap = appUserService.getAppUserMapByUserNo(appUserNoSet);
    Map<Long, ShopDTO> shopDTOMap = shopService.getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    for (AppointOrderDTO orderDTO : appointOrderDTOList) {
      if (!orderDTO.validateOverdueAppointRemindParams()) {
        continue;
      }
      if (orderDTO.getAppointTime() < System.currentTimeMillis()) {
        createOverdueAppointRemindMessage2App(pushMessageDTOList2App, shopDTOMap, appUserDTOMap, orderDTO);
        createOverdueAppointRemindMessage2Shop(pushMessageDTOList2Shop, appUserDTOMap, orderDTO);
      } else {
        createSoonExpireAppointRemindMessage2App(pushMessageDTOList2App, shopDTOMap, appUserDTOMap, orderDTO);
        createSoonExpireAppointRemindMessage2Shop(pushMessageDTOList2Shop, appUserDTOMap, orderDTO);
      }
    }
    pushMessageService.createPushMessageList(pushMessageDTOList2App, false);
    pushMessageService.createPushMessageList(pushMessageDTOList2Shop, true);
  }

  //已过期
  private void createOverdueAppointRemindMessage2Shop(List<PushMessageDTO> pushMessageDTOList, Map<String, AppUserDTO> appUserDTOMap, AppointOrderDTO orderDTO) throws Exception {
    AppUserDTO appUserDTO = appUserDTOMap.get(orderDTO.getAppUserNo());
    if (appUserDTO == null) return;
    AppointVelocityContext appointVelocityContext = orderDTO.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT, "OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT, "OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT_TEXT, "OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT_TEXT");

    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createOverdueAppointRemindMessage2Shop(orderDTO, appUserDTO, promptContent, PushMessageType.OVERDUE_APPOINT_TO_SHOP, PushMessageSourceType.OVERDUE_APPOINT_TO_SHOP);
    pushMessageDTO.setTitle(AppointConstant.OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_TITLE);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setContent(content);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, orderDTO.getId().toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTOList.add(pushMessageDTO);
  }

  //未过期
  private void createSoonExpireAppointRemindMessage2Shop(List<PushMessageDTO> pushMessageDTOList, Map<String, AppUserDTO> appUserDTOMap, AppointOrderDTO orderDTO) throws Exception {
    AppUserDTO appUserDTO = appUserDTOMap.get(orderDTO.getAppUserNo());
    if (appUserDTO == null) return;
    AppointVelocityContext appointVelocityContext = orderDTO.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT, "SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT");
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT, "SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT");
    String contentText = generateMsgUsingVelocity(context, PushMessageContentTemplate.SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT_TEXT, "SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT_TEXT");

    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createOverdueAppointRemindMessage2Shop(orderDTO, appUserDTO, promptContent, PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP, PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_SHOP);
    pushMessageDTO.setTitle(AppointConstant.SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_TITLE);
    pushMessageDTO.setContentText(contentText);
    pushMessageDTO.setContent(content);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, orderDTO.getId().toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTOList.add(pushMessageDTO);
  }

  //预约已过期
  private void createOverdueAppointRemindMessage2App(List<PushMessageDTO> pushMessageDTOList, Map<Long, ShopDTO> shopDTOMap, Map<String, AppUserDTO> appUserDTOMap, AppointOrderDTO orderDTO) throws Exception {
    ShopDTO shopDTO = shopDTOMap.get(orderDTO.getShopId());
    if (shopDTO == null) return;
    AppUserDTO appUserDTO = appUserDTOMap.get(orderDTO.getAppUserNo());
    if (appUserDTO == null) return;
    shopDTO.setAreaName(ServiceManager.getService(IConfigService.class).getShopAreaInfoByShopDTO(shopDTO));
    AppointVelocityContext appointVelocityContext = orderDTO.toAppointVelocityContext();
    appointVelocityContext.setShopDTO(shopDTO);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.OVERDUE_APPOINT_REMIND_APP_MESSAGE_CONTENT, "OVERDUE_APPOINT_REMIND_APP_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createOverdueAppointRemindMessage2App(orderDTO, appUserDTO, promptContent, PushMessageType.OVERDUE_APPOINT_TO_APP, PushMessageSourceType.OVERDUE_APPOINT_TO_APP);
    pushMessageDTO.setTitle(AppointConstant.OVERDUE_APPOINT_REMIND_APP_MESSAGE_TITLE);
    //单据ID shopId
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, orderDTO.getId().toString());
    paramMap.put(PushMessageParamsKeyConstant.AppParams, orderDTO.getId() + "," + shopDTO.getId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTOList.add(pushMessageDTO);

    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
  }

  //预约未过期
  private void createSoonExpireAppointRemindMessage2App(List<PushMessageDTO> pushMessageDTOList, Map<Long, ShopDTO> shopDTOMap, Map<String, AppUserDTO> appUserDTOMap, AppointOrderDTO orderDTO) throws Exception {
    ShopDTO shopDTO = shopDTOMap.get(orderDTO.getShopId());
    if (shopDTO == null) return;
    AppUserDTO appUserDTO = appUserDTOMap.get(orderDTO.getAppUserNo());
    if (appUserDTO == null) return;
    AppointVelocityContext appointVelocityContext = orderDTO.toAppointVelocityContext();
    appointVelocityContext.setShopDTO(shopDTO);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SOON_EXPIRE_APPOINT_REMIND_APP_MESSAGE_CONTENT, "SOON_EXPIRE_APPOINT_REMIND_APP_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createOverdueAppointRemindMessage2App(orderDTO, appUserDTO, promptContent, PushMessageType.SOON_EXPIRE_APPOINT_TO_APP, PushMessageSourceType.SOON_EXPIRE_APPOINT_TO_APP);
    pushMessageDTO.setTitle(AppointConstant.SOON_EXPIRE_APPOINT_REMIND_APP_MESSAGE_TITLE);
    //单据ID shopId
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppointOrderId, orderDTO.getId().toString());
    paramMap.put(PushMessageParamsKeyConstant.AppParams, orderDTO.getId() + "," + shopDTO.getId());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTOList.add(pushMessageDTO);
  }

  //=================================里程=================================
  @Override
  public void createAppVehicleMaintainMileageMessage(int limit) throws Exception {
    Double[] intervals = ConfigUtils.getAppVehicleMaintainMileageIntervals();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<AppVehicleDTO> appVehicleDTOList;
    Integer remindTimesLimit = ConfigUtils.getAppVehicleNextMaintainMileagePushMessageRemindTimesLimit();
    int start = 0;
    while (true) {
      appVehicleDTOList = appUserService.getMaintainMileageApproachingAppVehicle(intervals, start, limit, remindTimesLimit);
      if (appVehicleDTOList.size() == 0) {
        break;
      }
      createAppVehicleMaintainMileageMessage(appVehicleDTOList);
      start += limit;
    }
  }

  private void createAppVehicleMaintainMileageMessage(List<AppVehicleDTO> appVehicleDTOList) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> sourceIdSet = new HashSet<Long>();
    for (AppVehicleDTO dto : appVehicleDTOList) {
      appUserNoSet.add(dto.getUserNo());
      sourceIdSet.add(dto.getVehicleId());
    }
    Map<String, Set<Long>> appUserNoShopIdsMap = ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserNoShopIdsMapByAppUserCustomer(appUserNoSet);
    Map<Long, PushMessageSourceDTO> unreadMessageSource2AppMap = ServiceManager.getService(IPushMessageService.class)
      .getUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_MAINTAIN_MILEAGE);
    Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap = ServiceManager.getService(IPushMessageService.class)
      .getCombinationKeyUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP,
        PushMessageSourceType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP);
    Map<String, AppUserDTO> appUserDTOMap = ServiceManager.getService(IAppUserService.class).getAppUserMapByUserNo(appUserNoSet);
    Double[] intervals = ConfigUtils.getAppVehicleMaintainMileageIntervals();
    Double left = Math.abs(intervals[0]), right = Math.abs(intervals[1]);
    for (AppVehicleDTO dto : appVehicleDTOList) {
      if (StringUtil.isEmpty(dto.getVehicleNo())) {
        continue;
      }
      AppUserDTO appUserDTO = appUserDTOMap.get(dto.getUserNo());
      if (appUserDTO == null) return;
      //创建消息给app
      PushMessageDTO pushMessageDTO;
      if (unreadMessageSource2AppMap.get(dto.getVehicleId()) == null) {
        pushMessageDTO = createAppVehicleMaintainMileageMessage2App(dto, appUserDTO);
        pushMessageDTOList.add(pushMessageDTO);
      }
      Set<Long> shopIds = appUserNoShopIdsMap.get(dto.getUserNo());
      if (CollectionUtil.isNotEmpty(shopIds)) {
        //创建消息给shop
        pushMessageDTOList.addAll(createAppVehicleMaintainMileageMessage2Shop(dto, appUserDTO, shopIds, unreadMessageSource2ShopMap, left, right));
      }
    }
    ServiceManager.getService(IPushMessageService.class).createPushMessageList(pushMessageDTOList, false);
  }

  private PushMessageDTO createAppVehicleMaintainMileageMessage2App(AppVehicleDTO dto, AppUserDTO appUserDTO) throws Exception {
    AppointVelocityContext appointVelocityContext = dto.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_CONTENT, "APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, promptContent, PushMessageType.APP_VEHICLE_MAINTAIN_MILEAGE, PushMessageSourceType.APP_VEHICLE_MAINTAIN_MILEAGE);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, ServiceScope.OVERHAUL_AND_MAINTENANCE.toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_TITLE);
    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return pushMessageDTO;
  }

  private List<PushMessageDTO> createAppVehicleMaintainMileageMessage2Shop(AppVehicleDTO appVehicleDTO,
                                                                           AppUserDTO appUserDTO, Set<Long> shopIds,
                                                                           Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap,
                                                                           Double left, Double right) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();

    if (appVehicleDTO.getNextMaintainMileage() == null && appVehicleDTO.getMaintainPeriod() != null) {
      appVehicleDTO.setNextMaintainMileage(appVehicleDTO.getLastMaintainMileage() + appVehicleDTO.getMaintainPeriod());
    }

    for (Long shopId : shopIds) {
      PushMessageSourceDTO sourceDTO = unreadMessageSource2ShopMap.get(shopId + "_" + appVehicleDTO.getVehicleId());
      if (sourceDTO != null) {
        if (appVehicleDTO.getCurrentMileage() > appVehicleDTO.getNextMaintainMileage()) {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP)
            return pushMessageDTOList;
        } else {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP)
            return pushMessageDTOList;
        }
      }
      AppointVelocityContext appointVelocityContext = appVehicleDTO.toAppointVelocityContext();
      appointVelocityContext.setAppVehicleMaintainMileageLeftLimit(left);
      appointVelocityContext.setAppVehicleMaintainMileageRightLimit(right);
      appointVelocityContext.setAppUser(appUserDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, appointVelocityContext);
//      String content = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_CONTENT_TEXT, "APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_CONTENT_TEXT");
      String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_CONTENT, "APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_CONTENT");
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setCreateTime(System.currentTimeMillis());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
      pushMessageDTO.createAppVehicleMessage2Shop(appVehicleDTO, appUserDTO, shopId, promptContent,
        (appVehicleDTO.getCurrentMileage() > appVehicleDTO.getNextMaintainMileage() ? PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP : PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP),
        (appVehicleDTO.getCurrentMileage() > appVehicleDTO.getNextMaintainMileage() ? PushMessageSourceType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP : PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP)
      );
      pushMessageDTO.setContent(promptContent);
      pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_MAINTAIN_MILEAGE_MESSAGE_2_SHOP_TITLE);
      pushMessageDTOList.add(pushMessageDTO);
      //推送消息

      if (shopId != null) {
        ShopDTO shopDTO = configService.getShopById(shopId.longValue());
        IUserService userService = ServiceManager.getService(IUserService.class);
        List<UserDTO> userDTOs = userService.getShopUser(shopDTO.getId());
        if (CollectionUtils.isNotEmpty(userDTOs)) {
          IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
          for (UserDTO userDTO : userDTOs) {
            if (userDTO != null && userDTO.isNeedToPushMessage()) {
              if (appUserService.hasRemindTodoRight(shopDTO.getShopVersionId(), userDTO.getUserGroupId())) {
                if (StringUtils.isNotBlank(userDTO.getUmDeviceToken())) {
                  YFUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), userDTO.getUmDeviceToken());
                }
                if (StringUtils.isNotBlank(userDTO.getDeviceToken())) {
                  //todo IOS 待开发
                }
              }
            }
          }
        }
      }
    }
    return pushMessageDTOList;
  }

  //=================================保养=================================
  @Override
  public void createAppVehicleMaintainTimeMessage(int limit) throws Exception {
    Long[] intervals = ConfigUtils.getAppVehicleMaintainTimeIntervals();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<AppVehicleDTO> appVehicleDTOList;
    int start = 0;
    while (true) {
      appVehicleDTOList = appUserService.getMaintainTimeApproachingAppVehicle(intervals, start, limit);
      if (appVehicleDTOList.size() == 0) {
        break;
      }
      createAppVehicleMaintainTimeMessage(appVehicleDTOList);
      start += limit;
    }
  }

  private void createAppVehicleMaintainTimeMessage(List<AppVehicleDTO> appVehicleDTOList) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> sourceIdSet = new HashSet<Long>();
    for (AppVehicleDTO dto : appVehicleDTOList) {
      appUserNoSet.add(dto.getUserNo());
      sourceIdSet.add(dto.getVehicleId());
    }
    Map<String, AppUserDTO> appUserDTOMap = ServiceManager.getService(IAppUserService.class).getAppUserMapByUserNo(appUserNoSet);
    Map<Long, PushMessageSourceDTO> unreadMessageSource2AppMap = ServiceManager.getService(IPushMessageService.class)
      .getUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_MAINTAIN_TIME);
    Map<String, Set<Long>> appUserNoShopIdsMap = ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserNoShopIdsMapByAppUserCustomer(appUserNoSet);
    Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap = ServiceManager.getService(IPushMessageService.class)
      .getCombinationKeyUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP,
        PushMessageSourceType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP);
    for (AppVehicleDTO dto : appVehicleDTOList) {
      if (StringUtil.isEmpty(dto.getVehicleNo())) {
        continue;
      }
      AppUserDTO appUserDTO = appUserDTOMap.get(dto.getUserNo());
      if (appUserDTO == null) return;
      if (unreadMessageSource2AppMap.get(dto.getVehicleId()) == null) {
        PushMessageDTO pushMessageDTO = createAppVehicleMaintainTimeMessage2App(appUserDTO, dto);
        pushMessageDTOList.add(pushMessageDTO);
      }
      Set<Long> shopIds = appUserNoShopIdsMap.get(dto.getUserNo());
      if (CollectionUtil.isNotEmpty(shopIds)) {
        //创建消息给shop
        pushMessageDTOList.addAll(createAppVehicleMaintainTimeMessage2Shop(dto, appUserDTO, shopIds, unreadMessageSource2ShopMap));
      }
    }
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    pushMessageService.createPushMessageList(pushMessageDTOList, false);
  }

  private PushMessageDTO createAppVehicleMaintainTimeMessage2App(AppUserDTO appUserDTO, AppVehicleDTO dto) throws Exception {
    AppointVelocityContext appointVelocityContext = dto.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_MAINTAIN_TIME_MESSAGE_CONTENT, "APP_VEHICLE_MAINTAIN_TIME_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, promptContent, PushMessageType.APP_VEHICLE_MAINTAIN_TIME, PushMessageSourceType.APP_VEHICLE_MAINTAIN_TIME);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, ServiceScope.OVERHAUL_AND_MAINTENANCE.toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));

    pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_MAINTAIN_TIME_MESSAGE_TITLE);
    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return pushMessageDTO;
  }

  private List<PushMessageDTO> createAppVehicleMaintainTimeMessage2Shop(AppVehicleDTO appVehicleDTO,
                                                                        AppUserDTO appUserDTO, Set<Long> shopIds,
                                                                        Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Long currentTime = System.currentTimeMillis();
    for (Long shopId : shopIds) {
      PushMessageSourceDTO sourceDTO = unreadMessageSource2ShopMap.get(shopId + "_" + appVehicleDTO.getVehicleId());
      if (sourceDTO != null) {
        if (appVehicleDTO.getNextMaintainTime() >= currentTime) {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP)
            return pushMessageDTOList;
        } else {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP)
            return pushMessageDTOList;
        }
      }
      AppointVelocityContext appointVelocityContext = appVehicleDTO.toAppointVelocityContext();
      appointVelocityContext.setDay(getDay(appVehicleDTO.getNextMaintainTime(), currentTime));
      appointVelocityContext.setCurrentTime(currentTime);
      appointVelocityContext.setAppUser(appUserDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, appointVelocityContext);
      String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_CONTENT, "APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_CONTENT");
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setCreateTime(System.currentTimeMillis());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
      pushMessageDTO.createAppVehicleMessage2Shop(appVehicleDTO, appUserDTO, shopId, promptContent,
        (appVehicleDTO.getNextMaintainTime() >= currentTime ? PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP : PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP),
        (appVehicleDTO.getNextMaintainTime() >= currentTime ? PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP : PushMessageSourceType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP)
      );
      pushMessageDTO.setContent(promptContent);
      pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_TITLE);
      pushMessageDTOList.add(pushMessageDTO);
    }
    return pushMessageDTOList;
  }

  private int getDay(Long nextTime, Long currentTime) {
    return
      nextTime >= currentTime
        //未到日期  进位      不足n天
        ? new BigDecimal((nextTime - currentTime) / (1000.0 * 60 * 60 * 24)).setScale(0, RoundingMode.UP).intValue()
        //超过next  舍位      超过n天
        : new BigDecimal((currentTime - nextTime) / (1000.0 * 60 * 60 * 24)).setScale(0, RoundingMode.DOWN).intValue();
  }

//  public static void main(String[] args) throws Exception {
//    AppointPushMessageService service = new AppointPushMessageService();
//    AppUserDTO appUserDTO = new AppUserDTO();
//    appUserDTO.setUserNo("15854512554");
//    appUserDTO.setName("hans");
//    appUserDTO.setMobile("15854512554");
//    AppVehicleDTO dto = new AppVehicleDTO();
//    dto.setVehicleNo("苏A23423");
//    Long currentTime = System.currentTimeMillis();
//    dto.setNextMaintainTime(currentTime - 1000 * 60 * 60 * 24l-1);
//    AppointVelocityContext appointVelocityContext = dto.toAppointVelocityContext();
//    appointVelocityContext.setDay(service.getDay(dto.getNextMaintainTime(), currentTime));
//    appointVelocityContext.setCurrentTime(currentTime);
//    appointVelocityContext.setAppUser(appUserDTO);
//    VelocityContext context = new VelocityContext();
//    context.put(VELOCITY_PARAMETER, appointVelocityContext);
//    String promptContent = service.generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_CONTENT, "APP_VEHICLE_MAINTAIN_TIME_MESSAGE_2_SHOP_CONTENT");
//    System.out.println(promptContent);
//  }

  //=================================保险=================================
  @Override
  public void createAppVehicleInsuranceTimeMessage(int limit) throws Exception {
    Long[] intervals = ConfigUtils.getAppVehicleInsuranceTimeIntervals();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<AppVehicleDTO> appVehicleDTOList;
    int start = 0;
    while (true) {
      appVehicleDTOList = appUserService.getInsuranceTimeApproachingAppVehicle(intervals, start, limit);
      if (appVehicleDTOList.size() == 0) {
        break;
      }
      createAppVehicleInsuranceTimeMessage(appVehicleDTOList);
      start += limit;
    }
  }

  public void createAppVehicleInsuranceTimeMessage(List<AppVehicleDTO> appVehicleDTOList) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> sourceIdSet = new HashSet<Long>();
    for (AppVehicleDTO dto : appVehicleDTOList) {
      appUserNoSet.add(dto.getUserNo());
      sourceIdSet.add(dto.getVehicleId());
    }
    Map<String, AppUserDTO> appUserDTOMap = ServiceManager.getService(IAppUserService.class).getAppUserMapByUserNo(appUserNoSet);
    Map<Long, PushMessageSourceDTO> unreadMessageSource2AppMap = ServiceManager.getService(IPushMessageService.class)
      .getUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_INSURANCE_TIME);
    Map<String, Set<Long>> appUserNoShopIdsMap = ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserNoShopIdsMapByAppUserCustomer(appUserNoSet);
    Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap = ServiceManager.getService(IPushMessageService.class)
      .getCombinationKeyUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP,
        PushMessageSourceType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP);
    for (AppVehicleDTO dto : appVehicleDTOList) {
      if (StringUtil.isEmpty(dto.getVehicleNo())) {
        continue;
      }
      AppUserDTO appUserDTO = appUserDTOMap.get(dto.getUserNo());
      if (appUserDTO == null) return;
      if (unreadMessageSource2AppMap.get(dto.getVehicleId()) == null) {
        PushMessageDTO pushMessageDTO = createAppVehicleInsuranceTimeMessage2App(dto, appUserDTO);
        pushMessageDTOList.add(pushMessageDTO);
      }
      Set<Long> shopIds = appUserNoShopIdsMap.get(dto.getUserNo());
      if (CollectionUtil.isNotEmpty(shopIds)) {
        //创建消息给shop
        pushMessageDTOList.addAll(createAppVehicleInsuranceTimeMessage2Shop(dto, appUserDTO, shopIds, unreadMessageSource2ShopMap));
      }
    }
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    pushMessageService.createPushMessageList(pushMessageDTOList, false);
  }

  private PushMessageDTO createAppVehicleInsuranceTimeMessage2App(AppVehicleDTO dto, AppUserDTO appUserDTO) throws Exception {
    AppointVelocityContext appointVelocityContext = dto.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_INSURANCE_TIME_MESSAGE_CONTENT, "APP_VEHICLE_INSURANCE_TIME_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, promptContent, PushMessageType.APP_VEHICLE_INSURANCE_TIME, PushMessageSourceType.APP_VEHICLE_INSURANCE_TIME);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, ServiceScope.INSURANCE.toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_INSURANCE_TIME_MESSAGE_TITLE);
    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return pushMessageDTO;
  }

  private List<PushMessageDTO> createAppVehicleInsuranceTimeMessage2Shop(AppVehicleDTO appVehicleDTO,
                                                                         AppUserDTO appUserDTO, Set<Long> shopIds,
                                                                         Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Long currentTime = System.currentTimeMillis();
    for (Long shopId : shopIds) {
      PushMessageSourceDTO sourceDTO = unreadMessageSource2ShopMap.get(shopId + "_" + appVehicleDTO.getVehicleId());
      if (sourceDTO != null) {
        if (appVehicleDTO.getNextInsuranceTime() >= currentTime) {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP)
            return pushMessageDTOList;
        } else {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP)
            return pushMessageDTOList;
        }
      }
      AppointVelocityContext appointVelocityContext = appVehicleDTO.toAppointVelocityContext();
      appointVelocityContext.setDay(getDay(appVehicleDTO.getNextInsuranceTime(), currentTime));
      appointVelocityContext.setCurrentTime(currentTime);
      appointVelocityContext.setAppUser(appUserDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, appointVelocityContext);
      String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_INSURANCE_TIME_MESSAGE_2_SHOP_CONTENT, "APP_VEHICLE_INSURANCE_TIME_MESSAGE_2_SHOP_CONTENT");
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setCreateTime(System.currentTimeMillis());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
      pushMessageDTO.createAppVehicleMessage2Shop(appVehicleDTO, appUserDTO, shopId, promptContent,
        (appVehicleDTO.getNextInsuranceTime() >= currentTime ? PushMessageType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP : PushMessageType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP),
        (appVehicleDTO.getNextInsuranceTime() >= currentTime ? PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP : PushMessageSourceType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP)
      );
      pushMessageDTO.setContent(promptContent);
      pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_INSURANCE_TIME_MESSAGE_2_SHOP_TITLE);
      pushMessageDTOList.add(pushMessageDTO);
    }
    return pushMessageDTOList;
  }

  //=================================验车=================================
  @Override
  public void createAppVehicleExamineTimeMessage(int limit) throws Exception {
    Long[] intervals = ConfigUtils.getAppVehicleExamineTimeIntervals();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    List<AppVehicleDTO> appVehicleDTOList;
    int start = 0;
    while (true) {
      appVehicleDTOList = appUserService.getExamineTimeApproachingAppVehicle(intervals, start, limit);
      if (appVehicleDTOList.size() == 0) {
        break;
      }
      createAppVehicleExamineTimeMessage(appVehicleDTOList);
      start += limit;
    }
  }

  public void createAppVehicleExamineTimeMessage(List<AppVehicleDTO> appVehicleDTOList) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> sourceIdSet = new HashSet<Long>();
    for (AppVehicleDTO dto : appVehicleDTOList) {
      appUserNoSet.add(dto.getUserNo());
      sourceIdSet.add(dto.getVehicleId());
    }
    Map<String, AppUserDTO> appUserDTOMap = ServiceManager.getService(IAppUserService.class).getAppUserMapByUserNo(appUserNoSet);
    Map<Long, PushMessageSourceDTO> unreadMessageSource2AppMap = ServiceManager.getService(IPushMessageService.class)
      .getUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_EXAMINE_TIME);
    Map<String, Set<Long>> appUserNoShopIdsMap = ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserNoShopIdsMapByAppUserCustomer(appUserNoSet);
    Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap = ServiceManager.getService(IPushMessageService.class)
      .getCombinationKeyUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP,
        PushMessageSourceType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP);
    for (AppVehicleDTO dto : appVehicleDTOList) {
      if (StringUtil.isEmpty(dto.getVehicleNo())) {
        continue;
      }
      AppUserDTO appUserDTO = appUserDTOMap.get(dto.getUserNo());
      if (appUserDTO == null) return;
      if (unreadMessageSource2AppMap.get(dto.getVehicleId()) == null) {
        PushMessageDTO pushMessageDTO = createAppVehicleExamineTimeMessage2App(dto, appUserDTO);
        pushMessageDTOList.add(pushMessageDTO);
      }
      Set<Long> shopIds = appUserNoShopIdsMap.get(dto.getUserNo());
      if (CollectionUtil.isNotEmpty(shopIds)) {
        //创建消息给shop
        pushMessageDTOList.addAll(createAppVehicleExamineTimeMessage2Shop(dto, appUserDTO, shopIds, unreadMessageSource2ShopMap));
      }
    }
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    pushMessageService.createPushMessageList(pushMessageDTOList, false);
  }

  private List<PushMessageDTO> createAppVehicleExamineTimeMessage2Shop(AppVehicleDTO appVehicleDTO,
                                                                       AppUserDTO appUserDTO, Set<Long> shopIds,
                                                                       Map<String, PushMessageSourceDTO> unreadMessageSource2ShopMap) throws Exception {
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Long currentTime = System.currentTimeMillis();
    for (Long shopId : shopIds) {
      PushMessageSourceDTO sourceDTO = unreadMessageSource2ShopMap.get(shopId + "_" + appVehicleDTO.getVehicleId());
      if (sourceDTO != null) {
        if (appVehicleDTO.getNextExamineTime() >= currentTime) {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP)
            return pushMessageDTOList;
        } else {
          if (sourceDTO.getType() == PushMessageSourceType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP)
            return pushMessageDTOList;
        }
      }
      AppointVelocityContext appointVelocityContext = appVehicleDTO.toAppointVelocityContext();
      appointVelocityContext.setDay(getDay(appVehicleDTO.getNextExamineTime(), currentTime));
      appointVelocityContext.setCurrentTime(currentTime);
      appointVelocityContext.setAppUser(appUserDTO);
      VelocityContext context = new VelocityContext();
      context.put(VELOCITY_PARAMETER, appointVelocityContext);
      String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_EXAMINE_TIME_MESSAGE_2_SHOP_CONTENT, "APP_VEHICLE_EXAMINE_TIME_MESSAGE_2_SHOP_CONTENT");
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setCreateTime(System.currentTimeMillis());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
      pushMessageDTO.createAppVehicleMessage2Shop(appVehicleDTO, appUserDTO, shopId, promptContent,
        (appVehicleDTO.getNextExamineTime() >= currentTime ? PushMessageType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP : PushMessageType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP),
        (appVehicleDTO.getNextExamineTime() >= currentTime ? PushMessageSourceType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP : PushMessageSourceType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP)
      );
      pushMessageDTO.setContent(promptContent);
      pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_EXAMINE_TIME_MESSAGE_2_SHOP_TITLE);
      pushMessageDTOList.add(pushMessageDTO);
    }
    return pushMessageDTOList;
  }


  private PushMessageDTO createAppVehicleExamineTimeMessage2App(AppVehicleDTO dto, AppUserDTO appUserDTO) throws Exception {
    AppointVelocityContext appointVelocityContext = dto.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_EXAMINE_TIME_MESSAGE_CONTENT, "APP_VEHICLE_EXAMINE_TIME_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, promptContent, PushMessageType.APP_VEHICLE_EXAMINE_TIME, PushMessageSourceType.APP_VEHICLE_EXAMINE_TIME);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, ServiceScope.INSURANCE.toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_EXAMINE_TIME_MESSAGE_TITLE);
    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return pushMessageDTO;
  }

  @Override
  public PushMessageDTO createAppFaultCodeMessage2App(AppVehicleDTO dto, AppUserDTO appUserDTO) throws Exception {
    AppointVelocityContext appointVelocityContext = dto.toAppointVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, appointVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_EXAMINE_TIME_MESSAGE_CONTENT, "APP_VEHICLE_EXAMINE_TIME_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, promptContent, PushMessageType.APP_VEHICLE_EXAMINE_TIME, PushMessageSourceType.APP_VEHICLE_EXAMINE_TIME);
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put(PushMessageParamsKeyConstant.AppParams, ServiceScope.INSURANCE.toString());
    pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
    pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_EXAMINE_TIME_MESSAGE_TITLE);
    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return pushMessageDTO;
  }

  @Override
  public List<PushMessageDTO> createShopAdvertMessage2App(AdvertDTO advertDTO) throws Exception {

    Long shopId = advertDTO.getShopId();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    List<String> appUserNoList = vehicleService.getVehicleImeiByShopId(shopId);
    if (CollectionUtil.isEmpty(appUserNoList)) {
      return null;
    }

    Set<String> stringSet = new HashSet<String>();
    for (String appUserNo : appUserNoList) {
      stringSet.add(appUserNo);
    }

    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    Map<String, AppUserDTO> appUserDTOMap = appUserService.getAppUserMapByUserNo(stringSet);
    if (MapUtils.isEmpty(appUserDTOMap)) {
      return null;
    }

    ShopAdvertVelocityContext shopAdvertVelocityContext = advertDTO.toShopAdvertVelocityContext();
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
    shopAdvertVelocityContext.setShopName(shopDTO.getName());

    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, shopAdvertVelocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.SHOP_ADVERT_APP_MESSAGE_CONTENT, "SHOP_ADVERT_APP_MESSAGE_CONTENT");

    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();

    for (AppUserDTO appUserDTO : appUserDTOMap.values()) {
      PushMessageDTO pushMessageDTO = new PushMessageDTO();
      pushMessageDTO.setCreateTime(System.currentTimeMillis());
      pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
      pushMessageDTO.createShopAdvertAppMessage(advertDTO, appUserDTO, promptContent, PushMessageType.SHOP_ADVERT_TO_APP, PushMessageSourceType.SHOP_ADVERT_TO_APP);
      Map<String, String> paramMap = new HashMap<String, String>();
      paramMap.put(PushMessageParamsKeyConstant.AppParams, advertDTO.getId() + ";" + advertDTO.getShopId());
      pushMessageDTO.setParams(JsonUtil.mapToJson(paramMap));
      pushMessageDTO.setTitle(AppointConstant.SHOP_ADVERT_APP_MESSAGE_CONTENT_TITLE);
      pushMessageDTOList.add(pushMessageDTO);

      //发送IOS推送消息
      if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
        GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
      }
      //发送安卓友盟推送
      if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
        GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
      }

    }

    if (CollectionUtil.isNotEmpty(pushMessageDTOList)) {
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      pushMessageService.createPushMessageList(pushMessageDTOList, false);
    }

    return pushMessageDTOList;
  }

  @Override
  public List<PushMessageDTO> saveTalkMessage2App(String fromUserNo, AppUserDTO appUserDTO, String content, PushMessageType type) throws Exception {
    AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserDTO.getAppVehicleDTOs());
    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setCreator(fromUserNo);
    pushMessageDTO.setContent(content);
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    PushMessageSourceType sourceType = null;
    sourceType = PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.equals(type) ? PushMessageSourceType.MSG_FROM_WX_USER_TO_MIRROR : PushMessageSourceType.MSG_FROM_MIRROR_TO_WX_USER;
    pushMessageDTO.createAppVehicleMessage(appVehicleDTO, appUserDTO, content, type, sourceType);
    pushMessageDTO.setTitle(AppointConstant.APP_TALI);
    pushMessageDTO.setShopId(appUserDTO.getRegistrationShopId());
    pushMessageDTOList.add(pushMessageDTO);
    ServiceManager.getService(IPushMessageService.class).createPushMessageList(pushMessageDTOList, false);
    return pushMessageDTOList;
  }

  private PushMessageDTO createTalkMessage2App(String fromUserNo, AppVehicleDTO dto, AppUserDTO appUserDTO, String content) throws Exception {
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setCreator(fromUserNo);
    pushMessageDTO.setContent(content);
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, content, PushMessageType.MSG_FROM_WX_USER_TO_MIRROR, PushMessageSourceType.MSG_FROM_WX_USER_TO_MIRROR);
    pushMessageDTO.setTitle(AppointConstant.APP_TALI);
    return pushMessageDTO;
  }

  @Override
  public List<PushMessageDTO> sendVRegulationRecordMessage2App(AppVehicleDTO appVehicleDTO, AppUserDTO appUserDTO) throws Exception {

    List<PushMessageDTO> pushMessageDTOList = new ArrayList<PushMessageDTO>();
    Set<String> appUserNoSet = new HashSet<String>();
    Set<Long> sourceIdSet = new HashSet<Long>();
    appUserNoSet.add(appVehicleDTO.getUserNo());
    sourceIdSet.add(appVehicleDTO.getVehicleId());
    Map<Long, PushMessageSourceDTO> unreadMessageSource2AppMap = ServiceManager.getService(IPushMessageService.class)
      .getUnreadPushMessageSourceMapBySourceIds(sourceIdSet, PushMessageSourceType.APP_VEHICLE_MAINTAIN_MILEAGE);
    //创建消息给app
    PushMessageDTO pushMessageDTO;
    if (unreadMessageSource2AppMap.get(appVehicleDTO.getVehicleId()) == null) {
      pushMessageDTO = createVRegulationRecordMessage2App(appVehicleDTO, appUserDTO);
      pushMessageDTOList.add(pushMessageDTO);
    }
    ServiceManager.getService(IPushMessageService.class).createPushMessageList(pushMessageDTOList, false);

    return pushMessageDTOList;
  }

  private PushMessageDTO createVRegulationRecordMessage2App(AppVehicleDTO dto, AppUserDTO appUserDTO) throws Exception {
    ViolateRegulationRecordVelocityContext velocityContext = new ViolateRegulationRecordVelocityContext();
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, velocityContext);
    String promptContent = generateMsgUsingVelocity(context, AppointConstant.APP_VEHICLE_VIOLATE_REGULATION_RECORD_MESSAGE_CONTENT, "APP_VEHICLE_VIOLATE_REGULATION_RECORD_MESSAGE_CONTENT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createAppVehicleMessage(dto, appUserDTO, promptContent, PushMessageType.VIOLATE_REGULATION_RECORD_2_APP, PushMessageSourceType.VIOLATE_REGULATION_RECORD_2_APP);
    pushMessageDTO.setTitle(AppointConstant.APP_VEHICLE_VIOLATE_REGULATION_RECORD);

    //发送IOS推送消息
    if (StringUtils.isNotBlank(appUserDTO.getDeviceToken())) {
      GsmAPNSAdapter.sendPushMessage(pushMessageDTO.getPromptContent(), appUserDTO.getDeviceToken());
    }
    //发送安卓友盟推送
    if (StringUtils.isNotBlank(appUserDTO.getUmDeviceToken())) {
      GSMUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), appUserDTO.getUmDeviceToken());
    }
    return pushMessageDTO;
  }


  //创建过期预约单提醒消息
  @Override
  public void scheduleAppointOverdueRemindMsg() {
    try {
      ServiceManager.getService(IAppointPushMessageService.class).createOverdueAppointRemindMessage(1000);
      List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
      for (ShopDTO shopDTO : shopDTOList) {
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopDTO.getId());
        if (CollectionUtils.isNotEmpty(userIds)) {
          for (Long userId : userIds) {
            ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(shopDTO.getId(), userId, PushMessageCategory.values());
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

}
