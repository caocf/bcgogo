package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.UMPush.YFUMPushAdapter;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.pushMessage.PushMessageContentTemplate;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.notification.velocity.VehicleFaultContext;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageSource;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.service.messageCenter.AbstractMessageService;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.user.model.app.AppVehicleFaultInfo;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-2
 * Time: 下午6:07
 */
@Service
public class VehicleFaultPushMessage extends AbstractMessageService implements IVehicleFaultPushMessage {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleFaultPushMessage.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public boolean createVehicleFaultMessage2Shop(VehicleFaultParameter parameter) throws Exception {
    if (parameter == null) {
      LOG.error("VehicleFaultParameter is null");
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
//      return false;
    }
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(parameter.getTargetShopId());
    VehicleFaultContext myContext = new VehicleFaultContext(parameter, shopDTO);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, myContext);
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT, "VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT");
    String promptContent = generateMsgUsingVelocity(context, PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT_TEXT, "VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT_TEXT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createVehicleFaultMessage2Shop(parameter, appUserDTO, shopDTO, promptContent, PushMessageType.VEHICLE_FAULT_2_SHOP, PushMessageSourceType.VEHICLE_FAULT_2_SHOP);
    pushMessageDTO.setTitle(PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_SHOP_TITLE);
    pushMessageDTO.setContentText(promptContent);
    pushMessageDTO.setContent(content);
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);
   //推送消息
    //推送消息
    if(shopDTO != null && shopDTO.getId() != null){
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<UserDTO> userDTOs = userService.getShopUser(shopDTO.getId());
      if(CollectionUtils.isNotEmpty(userDTOs)){
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        for(UserDTO userDTO : userDTOs){
          if(userDTO != null && userDTO.isNeedToPushMessage()){
            if(appUserService.hasShopFaultRight(shopDTO.getShopVersionId(),userDTO.getUserGroupId())){
              if(StringUtils.isNotBlank(userDTO.getUmDeviceToken())){
                YFUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), userDTO.getUmDeviceToken());
              }
              if(StringUtils.isNotBlank(userDTO.getDeviceToken())){
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
  public boolean createVehicleFaultAlertMessage2Shop(AppUserDTO appUserDTO,VehicleDTO vehicleDTO,CustomerDTO customerDTO, FaultInfoToShopDTO faultInfoToShopDTO) throws Exception {
    if (faultInfoToShopDTO == null) {
      LOG.error("faultInfoToShopDTO is null");
      return false;
    }
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(faultInfoToShopDTO.getShopId());
    VehicleFaultContext myContext = new VehicleFaultContext(appUserDTO,vehicleDTO,customerDTO, faultInfoToShopDTO, shopDTO);
    VelocityContext context = new VelocityContext();
    context.put(VELOCITY_PARAMETER, myContext);
    String content = generateMsgUsingVelocity(context, PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT, "VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT");
    String promptContent = generateMsgUsingVelocity(context, PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT_TEXT, "VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT_TEXT");
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createVehicleFaultAlertMessage2Shop(appUserDTO, vehicleDTO, customerDTO, faultInfoToShopDTO, shopDTO, promptContent,
        PushMessageType.VEHICLE_FAULT_2_SHOP, PushMessageSourceType.VEHICLE_FAULT_2_SHOP);
    pushMessageDTO.setTitle(PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_SHOP_TITLE);
    pushMessageDTO.setContentText(promptContent);
    pushMessageDTO.setContent(content);
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);
    //推送消息
    //推送消息
    if(shopDTO != null && shopDTO.getId() != null){
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<UserDTO> userDTOs = userService.getShopUser(shopDTO.getId());
      if(CollectionUtils.isNotEmpty(userDTOs)){
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        for(UserDTO userDTO : userDTOs){
          if(userDTO != null && userDTO.isNeedToPushMessage()){
            if(appUserService.hasShopFaultRight(shopDTO.getShopVersionId(),userDTO.getUserGroupId())){
              if(StringUtils.isNotBlank(userDTO.getUmDeviceToken())){
                YFUMPushAdapter.sendPushMessage(pushMessageDTO.getTitle(), pushMessageDTO.getPromptContent(), userDTO.getUmDeviceToken());
              }
              if(StringUtils.isNotBlank(userDTO.getDeviceToken())){
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
  public boolean createVehicleFaultMessage2App(AppUserDTO appUserDTO,AppVehicleFaultInfoDTO vehicleFaultInfoDTO) throws Exception {
    String content = PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_APP_CONTENT;
    String promptContent = PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_APP_CONTENT_TEXT;
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.createVehicleFaultMessage2App(appUserDTO, promptContent, PushMessageType.VEHICLE_FAULT_2_APP, PushMessageSourceType.VEHICLE_FAULT_2_APP);
    pushMessageDTO.setTitle(PushMessageContentTemplate.VEHICLE_FAULT_MESSAGE_2_APP_TITLE);
    pushMessageDTO.setContentText(promptContent);
    pushMessageDTO.setContent(content);
    ServiceManager.getService(IPushMessageService.class).createPushMessage(pushMessageDTO, false);
    return true;
  }

  @Override
  public void initFaultCodePushMessage() {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{

      List<PushMessage> pushMessages = writer.getPushMessageByType(PushMessageType.VEHICLE_FAULT_2_SHOP);
      if (CollectionUtils.isNotEmpty(pushMessages)) {
        for (PushMessage pushMessage : pushMessages) {
          FaultInfoToShop faultInfoToShop = new FaultInfoToShop();
          faultInfoToShop.setShopId(pushMessage.getShopId());
          AppUserDTO appUserDTO = appUserService.getAppUserDTOById(pushMessage.getCreatorId());
          if (appUserDTO != null) {
            faultInfoToShop.setAppUserNo(appUserDTO.getUserNo());
            faultInfoToShop.setMobile(appUserDTO.getMobile());
          }
//          String content = "18626219017(18626219017)的爱车（苏E95V29）于2014年01月02日 17时38分 出现故障：发动机位置系统性能 （第1排），(P0008)。";
          String content = pushMessage.getContent();
          String vehicleNo = null, faultCode = null;
          Long vehicleBandId = null, vehicleId = null;
          Pattern vehiclePattern = Pattern.compile("（[a-zA-Z0-9\\u4e00-\\u9fa5]+）");
          Matcher vehicleMatcher = vehiclePattern.matcher(content);
          if (vehicleMatcher.find()) {
            vehicleNo = content.substring(vehicleMatcher.start(0), vehicleMatcher.end(0));
            vehicleNo = vehicleNo.replaceAll("（", "").replaceAll("）", "");
          }
          Pattern codePattern = Pattern.compile("\\([a-zA-Z][\\s\\S]*\\)");
          Matcher codeMatcher = codePattern.matcher(content);
          if (codeMatcher.find()) {
            faultCode = content.substring(codeMatcher.start(0), codeMatcher.end(0));
            faultCode = faultCode.replaceAll("\\(", "").replaceAll("\\)", "");
          }
          faultInfoToShop.setFaultCode(faultCode);
          faultInfoToShop.setVehicleNo(vehicleNo);

          List<PushMessageSource> pushMessageSources =  writer.getPushMessageSourceByMessageId(pushMessage.getId(),PushMessageSourceType.VEHICLE_FAULT_2_SHOP);

          PushMessageSource pushMessageSource = CollectionUtil.getFirst(pushMessageSources);
          if(pushMessageSource == null){
            LOG.error("pushMessageSource == null");
            continue;
          }
          UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
          UserWriter userWriter = userDaoManager.getWriter();
          AppVehicleFaultInfo appVehicleFaultInfo = userWriter.getById(AppVehicleFaultInfo.class,pushMessageSource.getSourceId());
          if (appVehicleFaultInfo == null) {
            LOG.error("appVehicleFaultInfo == null");
            continue;
          }
          vehicleId = appVehicleFaultInfo.getAppVehicleId();
          faultInfoToShop.setAppVehicleFaultInfoId(appVehicleFaultInfo.getId());
          faultInfoToShop.setFaultCodeReportTime(appVehicleFaultInfo.getReportTime());

          if (vehicleId != null) {
            AppVehicle appVehicle = userWriter.getById(AppVehicle.class, vehicleId);
            vehicleBandId = appVehicle.getVehicleBrandId();
            faultInfoToShop.setAppVehicleId(vehicleId);
            faultInfoToShop.setVehicleBrand(appVehicle.getVehicleBrand());
            faultInfoToShop.setVehicleModel(appVehicle.getVehicleModel());
          }else {
            LOG.error("vehicleId is null");
          }
          IAppDictionaryService appDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
          List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOs = appDictionaryService.getDictionaryFaultInfoDTOsByBrandIdAndCode(vehicleBandId, faultCode);
          DictionaryFaultInfoDTO firstDic = CollectionUtil.getFirst(dictionaryFaultInfoDTOs);

          if (firstDic != null) {
            faultInfoToShop.setFaultCodeInfoId(firstDic.getId());
            faultInfoToShop.setFaultCodeCategory(firstDic.getCategory());
            faultInfoToShop.setFaultCodeDescription(firstDic.getDescription());
          }

          faultInfoToShop.setStatus(Status.ACTIVE);
          faultInfoToShop.setIsCreateAppointOrder(YesNo.NO);
          faultInfoToShop.setIsSendMessage(YesNo.NO);
          writer.save(faultInfoToShop);
          pushMessage.setRelatedObjectId(faultInfoToShop.getId());
          writer.update(pushMessage);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public FaultInfoToShopDTO createFaultInfoToShop(Long receiveShopId, AppVehicleFaultInfoDTO appVehicleFaultInfoDTO,
                                                  AppUserDTO appUserDTO, AppVehicleDTO appVehicleDTO) {
    List<FaultInfoToShop> faultInfoToShops = null;
    FaultInfoToShop faultInfoToShop = null;
    if(receiveShopId != null && appVehicleFaultInfoDTO != null){
      if(StringUtil.isNotEmpty(appVehicleFaultInfoDTO.getErrorCode())){
        FaultInfoSearchConditionDTO searchCondition = new FaultInfoSearchConditionDTO();
        searchCondition.setShopId(receiveShopId);
        searchCondition.setVehicleNo(appVehicleDTO.getVehicleNo());
        searchCondition.setCode(appVehicleFaultInfoDTO.getErrorCode());
        IShopFaultInfoService iShopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
        faultInfoToShops = iShopFaultInfoService.getShopFaultInfoByFaultCode(searchCondition);
      }
      if(CollectionUtils.isNotEmpty(faultInfoToShops)){
        TxnWriter writer = txnDaoManager.getWriter();
        Object status = writer.begin();
        try{
          faultInfoToShop = CollectionUtil.getFirst(faultInfoToShops);
          faultInfoToShop.setFaultCodeReportTime(System.currentTimeMillis());
          writer.update(faultInfoToShop);
          writer.commit(status);
          return faultInfoToShop.toDTO();
        } finally {
          writer.rollback(status);
        }
      }else{
        FaultInfoToShopDTO faultInfoToShopDTO = new FaultInfoToShopDTO();
        faultInfoToShopDTO.setIsCreateAppointOrder(YesNo.NO);
        faultInfoToShopDTO.setIsSendMessage(YesNo.NO);
        faultInfoToShopDTO.setStatus(Status.ACTIVE);
        faultInfoToShopDTO.setShopId(receiveShopId);
        faultInfoToShopDTO.setFaultAlertType(FaultAlertType.FAULT_CODE);
        faultInfoToShopDTO.fromAppVehicleFaultInfoDTO(appVehicleFaultInfoDTO);
        if(appUserDTO != null){
          faultInfoToShopDTO.setAppUserNo(appUserDTO.getUserNo());
          faultInfoToShopDTO.setMobile(appUserDTO.getMobile());
        }
        if(appVehicleDTO != null){
          faultInfoToShopDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
          faultInfoToShopDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
          faultInfoToShopDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
        }
        TxnWriter writer = txnDaoManager.getWriter();
        Object status = writer.begin();
        try{
          faultInfoToShop = new FaultInfoToShop();
          faultInfoToShop.fromDTO(faultInfoToShopDTO);
          writer.save(faultInfoToShop);
          writer.commit(status);
          faultInfoToShopDTO.setId(faultInfoToShop.getId());
          return faultInfoToShopDTO;
        } finally {
          writer.rollback(status);
        }
      }
    }else {
      return null;
    }
  }

  @Override
  public FaultInfoToShopDTO createAlertInfoToShop(FaultInfoToShopDTO faultInfoToShopDTO) {
    if (faultInfoToShopDTO != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        FaultInfoToShop faultInfoToShop = new FaultInfoToShop();
        faultInfoToShop.fromDTO(faultInfoToShopDTO);
        writer.save(faultInfoToShop);
        writer.commit(status);
        faultInfoToShopDTO.setId(faultInfoToShop.getId());
        return faultInfoToShopDTO;
      } finally {
        writer.rollback(status);
      }
    } else {
      return null;
    }
  }
}
