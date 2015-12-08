package com.bcgogo.txn.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.request.MultiFaultRequest;
import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.Switch;
import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.product.service.IStandardBrandModelService;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.service.pushMessage.IVehicleFaultPushMessage;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppVehicleFaultInfo;
import com.bcgogo.user.model.app.OBD;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserShopBindingService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.wx.IWXAccountService;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.message.template.WXMsgTemplate;
import com.bcgogo.wx.user.AppWXUserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-3
 * Time: 上午11:48
 */
@Component
public class HandleAppVehicleFaultCodeService implements IHandleAppVehicleFaultCodeService {
  private static final Logger LOG = LoggerFactory.getLogger(HandleAppVehicleFaultCodeService.class);

  @Override
  public ApiResponse handleVehicleFaultInfo(VehicleFaultDTO faultDTO) throws Exception {
    String result = faultDTO.validate();
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);

    if (faultDTO.isSuccess(result)) {
      UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
      UserWriter writer = userDaoManager.getWriter();
      OBD obd = writer.getObdBySn(faultDTO.getObdSN());
      if (obd == null) {
        return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, "OBD不存在");
      }
      AppVehicleDTO appVehicleDTO = appUserVehicleObdService.getAppVehicleById(faultDTO.getVehicleId());
      if (appVehicleDTO == null) {
        return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, "车辆不存在");
      }
      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(faultDTO.getUserNo(), null);
      if (appUserDTO == null) {
        return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, "用户不存在");
      }
      List<AppVehicleFaultInfoDTO> toSendToShopFaultInfoList = saveAppVehicleFaultInfoDTOs(faultDTO, obd.toDTO(), appVehicleDTO);
      LOG.warn("需要发消息的故障信息：{}", JsonUtil.objectToJson(toSendToShopFaultInfoList));
      if (CollectionUtils.isNotEmpty(toSendToShopFaultInfoList)) {
        IVehicleFaultPushMessage vehicleFaultPushMessage = ServiceManager.getService(IVehicleFaultPushMessage.class);
        IAppUserShopBindingService appUserShopBindingService = ServiceManager.getService(IAppUserShopBindingService.class);
        Set<Long> receiveShopIds = appUserShopBindingService.getBindingShopIds(appUserDTO.getUserNo());
        for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : toSendToShopFaultInfoList) {
          if (appVehicleFaultInfoDTO != null && CollectionUtils.isNotEmpty(receiveShopIds)) {
            for (Long receiveShopId : receiveShopIds) {
              FaultInfoToShopDTO faultInfoToShopDTO = vehicleFaultPushMessage.createFaultInfoToShop(receiveShopId, appVehicleFaultInfoDTO, appUserDTO, appVehicleDTO);
              VehicleFaultParameter vehicleFaultParameter = new VehicleFaultParameter(
                appUserDTO, appVehicleFaultInfoDTO, receiveShopId, appVehicleDTO, faultInfoToShopDTO);
              vehicleFaultPushMessage.createVehicleFaultMessage2Shop(vehicleFaultParameter);
            }
          }
        }
      }
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_SUCCESS);
    } else {
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, result);
    }
  }

  @Override
  public List<AppVehicleFaultInfoDTO> saveAppVehicleFaultInfoDTOs(VehicleFaultDTO faultDTO, ObdDTO obdDTO, AppVehicleDTO appVehicleDTO) {
    IAppDictionaryService appDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    List<AppVehicleFaultInfoDTO> toSendToShopFaultInfoList = new ArrayList<AppVehicleFaultInfoDTO>();
    try {
      LOG.warn("收到故障码信息：{}", JsonUtil.objectToJson(faultDTO));
      String[] vehicleFaultCodeArray = faultDTO.getFaultCode().split(",");
      Set<String> vehicleFaultCodeSet = new HashSet<String>(); //收到的全部的故障码
      Set<String> handledFaultCodeSet = new HashSet<String>();//已经处理的故障码
      for (String faultCode : vehicleFaultCodeArray) {
        if (StringUtils.isNotEmpty(faultCode)) {
          vehicleFaultCodeSet.add(faultCode);
        }
      }
      Set<ErrorCodeTreatStatus> errorCodeTreatStatuses = new HashSet<ErrorCodeTreatStatus>();
      errorCodeTreatStatuses.add(ErrorCodeTreatStatus.UNTREATED);
      List<AppVehicleFaultInfo> appVehicleFaultInfoList = writer.getAppVehicleFaultInfo(faultDTO.getUserNo(),
        faultDTO.getVehicleId(), vehicleFaultCodeSet, errorCodeTreatStatuses);
      if (CollectionUtils.isNotEmpty(appVehicleFaultInfoList)) {
        for (AppVehicleFaultInfo appVehicleFaultInfo : appVehicleFaultInfoList) {
          if (appVehicleFaultInfo != null) {
            //当前的 appVehicleFaultInfo 已经处理，则认为重复数 更新为已删除
            if (handledFaultCodeSet.contains(appVehicleFaultInfo.getErrorCode())) {
              appVehicleFaultInfo.setStatus(ErrorCodeTreatStatus.DELETED);
              writer.update(appVehicleFaultInfo);
            } else {   //未处理的故障码如果还未处理，有报相同的故障，则更新下时间
              appVehicleFaultInfo.setReportTime(System.currentTimeMillis());
              appVehicleFaultInfo.setObdId(obdDTO.getId());
              writer.update(appVehicleFaultInfo);
              handledFaultCodeSet.add(appVehicleFaultInfo.getErrorCode());
              toSendToShopFaultInfoList.add(appVehicleFaultInfo.toDTO());   //返回上一级，更新faultShopInfo表
            }
          }
        }
      }
      //全部故障码 排除 已经处理过的故障码，再做新增操作
      vehicleFaultCodeSet.removeAll(handledFaultCodeSet);

      if (CollectionUtils.isNotEmpty(vehicleFaultCodeSet)) {
        Map<String, List<DictionaryFaultInfoDTO>> faultInfoMap = appDictionaryService.getDictionaryFaultInfoMapByBrandIdAndCodes(
          appVehicleDTO.getVehicleBrandId(), vehicleFaultCodeSet);
        for (String faultCode : vehicleFaultCodeSet) {
          if (StringUtil.isEmpty(faultCode)) {
            continue;
          }
          AppVehicleFaultInfo appVehicleFaultInfo = new AppVehicleFaultInfo(faultDTO);
          appVehicleFaultInfo.setErrorCode(faultCode);
          appVehicleFaultInfo.setObdId(obdDTO.getId());
          appVehicleFaultInfo.setStatus(ErrorCodeTreatStatus.UNTREATED);
          appVehicleFaultInfo.setReportTime(faultDTO.getReportTime() == null ? System.currentTimeMillis() : faultDTO.getReportTime());
          appVehicleFaultInfo.setAppVehicleId(appVehicleDTO.getVehicleId());
          appVehicleFaultInfo.setFaultInfoContent(faultInfoMap.get(faultCode));
          writer.save(appVehicleFaultInfo);
          AppVehicleFaultInfoDTO appVehicleFaultInfoDTO = appVehicleFaultInfo.toDTO();
          appVehicleFaultInfoDTO.setCategoryBackgroundInfo(faultInfoMap.get(faultCode));
          toSendToShopFaultInfoList.add(appVehicleFaultInfoDTO);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return toSendToShopFaultInfoList;
  }

  @Override
  public ApiResponse handleMultiVehicleFaultInfo(MultiFaultRequest multiFaultRequest) throws Exception {
    ApiResponse apiResponse = validateAndGenerateApiResponse(multiFaultRequest);
    if (apiResponse != null && MessageCode.APP_VEHICLE_FAULT_SUCCESS.getCode() == apiResponse.getMsgCode()) {
      for (VehicleFaultDTO vehicleFaultDTO : multiFaultRequest.getVehicleFaults()) {
        apiResponse = handleVehicleFaultInfo(vehicleFaultDTO);
        if (MessageCode.APP_VEHICLE_FAULT_FAIL.getCode() == apiResponse.getMsgCode()) {
          break;
        }
      }
    }
    return apiResponse;
  }

  private ApiResponse validateAndGenerateApiResponse(MultiFaultRequest multiFaultRequest) {
    if (multiFaultRequest == null || ArrayUtils.isEmpty(multiFaultRequest.getVehicleFaults())) {
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_FAIL, ValidateMsg.APP_VEHICLE_FAULT_CODE_EMPTY);
    }
    for (VehicleFaultDTO vehicleFaultDTO : multiFaultRequest.getVehicleFaults()) {
      vehicleFaultDTO.setUserNo(multiFaultRequest.getAppUserNo());
    }
    return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_SUCCESS);
  }

  @Override
  public Result sendFaultCode(String imei, String faultCodes, Long reportTime) {
    try {
      if (StringUtils.isEmpty(imei) || StringUtils.isEmpty(faultCodes)) {
        return new Result(false, "illegal param");
      }
      //validate data
      IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      IVehicleFaultPushMessage vehicleFaultPushMessage = ServiceManager.getService(IVehicleFaultPushMessage.class);
      AppVehicleDTO appVehicleDTO = null;
      AppUserDTO appUserDTO = null;
      ObdDTO obdDTO = appUserVehicleObdService.getObd_MirrorByIMei(imei);
      if (obdDTO == null) {
        LOG.error("根据IMei号：{}找不到对应的OBD", imei);
      } else {
        ObdUserVehicleDTO obdUserVehicleDTO = appUserVehicleObdService.getBundlingObdUserVehicleDTOByObdId(obdDTO.getId());
        if (obdUserVehicleDTO == null) {
          LOG.warn("根据ObdId：{}找不到对应的obdUserVehicleDTO", obdDTO.getId());
        } else {
          appVehicleDTO = appUserService.getAppVehicleDTOById(obdUserVehicleDTO.getAppVehicleId());
          if (appVehicleDTO == null) {
            LOG.warn("根据IMei号：{}找不到对应的app车辆", imei);
          }
          if (appVehicleDTO != null && StringUtils.isNotEmpty(appVehicleDTO.getUserNo())) {
            appUserDTO = appUserService.getAppUserByUserNo(appVehicleDTO.getUserNo(), null);
          }
          if (appUserDTO == null) {
            LOG.warn("根据appUserNo：{}找不到对客户", imei);
          }
        }
      }
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByIMei(imei);
      if (vehicleDTO == null) {
        LOG.error("根据IMei号：{}找不到对应的客户车辆", imei);
        return new Result(false, "根据IMei号，找不到对应的客户车辆");
      }
      if (appUserDTO == null) {
        return new Result(false, "根据IMei号，用户信息。");
      }
      //filter faultCode
      String[] faultCodeArray = faultCodes.split(",");
      Set<String> faultCodeSet = new HashSet<String>();//已经处理的故障码
      for (String faultCode : faultCodeArray) {
        faultCodeSet.add(faultCode);
      }
      IAppDictionaryService appDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
      Map<String, List<DictionaryFaultInfoDTO>> faultInfoMap = appDictionaryService.getDictionaryFaultInfoMapByBrandIdAndCodes(
        appVehicleDTO.getVehicleBrandId(), faultCodeSet);
      StringBuilder sb = new StringBuilder();
      int len = faultCodeArray.length;
      for (int i = 0; i < len; i++) {
        String faultCode = faultCodeArray[i];
        DictionaryFaultInfoDTO dictionary = CollectionUtil.getFirst(faultInfoMap.get(faultCode));
        if (dictionary != null && "网络通讯系统".equals(dictionary.getCategory())) {
          continue;
        }
        sb.append(faultCode);
        if (i < len - 1) {
          sb.append(",");
        }
      }
      //do save
      VehicleFaultDTO faultDTO = new VehicleFaultDTO();
      faultDTO.setUserNo(appUserDTO.getUserNo());
      faultDTO.setVehicleId(appVehicleDTO.getVehicleId());
      faultDTO.setFaultCode(sb.toString());
      if (reportTime == null) {
        reportTime = System.currentTimeMillis();
      }
      faultDTO.setReportTime(reportTime);
      faultDTO.setImei(imei);
      List<AppVehicleFaultInfoDTO> toSendToShopFaultInfoList = new ArrayList<AppVehicleFaultInfoDTO>();
      toSendToShopFaultInfoList = saveAppVehicleFaultInfoDTOs(faultDTO, obdDTO, appVehicleDTO);
      //给店铺，app发消息
      if (CollectionUtils.isNotEmpty(toSendToShopFaultInfoList)) {
        IAppUserShopBindingService appUserShopBindingService = ServiceManager.getService(IAppUserShopBindingService.class);
        Set<Long> receiveShopIds = new HashSet<Long>();
        if (appUserDTO != null) {
          receiveShopIds.addAll(appUserShopBindingService.getBindingShopIds(appUserDTO.getUserNo()));
        }
        if (vehicleDTO != null) {
          receiveShopIds.add(vehicleDTO.getShopId());
        }
        for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : toSendToShopFaultInfoList) {
          if (appVehicleFaultInfoDTO != null && CollectionUtils.isNotEmpty(receiveShopIds) && StringUtil.isNotEmpty(appVehicleFaultInfoDTO.getContent())&& StringUtil.isNotEmpty(appVehicleFaultInfoDTO.getBackgroundInfo())) {  //故障码描述不存在的过滤掉
            for (Long receiveShopId : receiveShopIds) {
              FaultInfoToShopDTO faultInfoToShopDTO = vehicleFaultPushMessage.createFaultInfoToShop(receiveShopId, appVehicleFaultInfoDTO, appUserDTO, appVehicleDTO);
              VehicleFaultParameter vehicleFaultParameter = new VehicleFaultParameter(
                appUserDTO, appVehicleFaultInfoDTO, receiveShopId, appVehicleDTO, faultInfoToShopDTO);
              if (vehicleDTO != null) {
                vehicleFaultPushMessage.createVehicleFaultMessage2Shop(vehicleFaultParameter);
              }
            }
          }
          if (appVehicleFaultInfoDTO != null && appUserDTO != null) {
            vehicleFaultPushMessage.createVehicleFaultMessage2App(appUserDTO, appVehicleFaultInfoDTO);
          }
        }
      } else if (appUserDTO == null && vehicleDTO != null) {//没有注册app的
        IUserService userService = ServiceManager.getService(IUserService.class);
        CustomerDTO customerDTO = userService.getCustomerInfoByVehicleId(vehicleDTO.getShopId(), vehicleDTO.getId());
        List<FaultInfoToShopDTO> faultInfoToShopDTOs = saveVehicleFaultCodeWithOutApp(vehicleDTO, customerDTO, faultDTO);
        if (CollectionUtils.isNotEmpty(faultInfoToShopDTOs)) {
          for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
            VehicleFaultParameter vehicleFaultParameter = new VehicleFaultParameter(vehicleDTO, customerDTO, faultInfoToShopDTO);
            vehicleFaultPushMessage.createVehicleFaultMessage2Shop(vehicleFaultParameter);
          }
        }
      }
      //发送微信车辆异常通知
      String faultSwitch = ServiceManager.getService(IConfigService.class).getConfig("fault_code_notify_switch", ShopConstant.BC_SHOP_ID);
      LOG.info("fault_code_notify_switch= {} ", faultSwitch);
      if (Switch.ON.toString().equals(faultSwitch)) {
        IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
        IWXAccountService accountService = ServiceManager.getService(IWXAccountService.class);
        List<AppWXUserDTO> userDTOs = wxUserService.getAppWXUserDTO(appUserDTO.getUserNo(), null);
        if (CollectionUtil.isNotEmpty(userDTOs)) {
          IAppDictionaryService iAppDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
          Set<String> codes = new HashSet<String>();
          String[] vehicleFaultCodeArray = faultDTO.getFaultCode().split(",");
          for (String vehicleFaultCode : vehicleFaultCodeArray) {
            codes.add(vehicleFaultCode);
          }
          Map<String, List<DictionaryFaultInfoDTO>> dictionaryFaultInfoMap = iAppDictionaryService.getDictionaryFaultInfoMapByBrandIdAndCodes(vehicleDTO.getBrandId(), codes);
          for (String faultCode : dictionaryFaultInfoMap.keySet()) {
            DictionaryFaultInfoDTO dictionary = CollectionUtil.getFirst(dictionaryFaultInfoMap.get(faultCode));
            if (dictionary == null) {
              continue;
            }
            WXMsgTemplate template = null;
            Result result = null;
            for (AppWXUserDTO userDTO : userDTOs) {
              String data = DateUtil.convertDateLongToDateString(DateUtil.ALL, reportTime);
              String publicNo = accountService.getWXAccountByOpenId(userDTO.getOpenId()).getPublicNo();
              if(StringUtil.isNotEmpty(dictionary.getDescription())){  //故障码描述不存在的过滤掉
                    template = WXHelper.getMirrorVehicleFaultRemindTemplate(publicNo, userDTO.getOpenId(), vehicleDTO.getLicenceNo(), dictionary.getCategory(),
                    dictionary.getDescription(), data);
//                    result = ServiceManager.getService(IWXMsgSender.class).sendTemplateMsg(publicNo, template);  //todo 暂时不再提醒
//                if (result!=null&&!result.isSuccess()) {
//                  LOG.error("send mirrorVehicleFaultRemindTemplate failed,msg is", result.getMsg());
//                }
              }
            }
          }
        }
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }
    return new Result(true);
  }

  private List<FaultInfoToShopDTO> saveVehicleFaultCodeWithOutApp(VehicleDTO vehicleDTO, CustomerDTO customerDTO, VehicleFaultDTO faultDTO) {
    IAppDictionaryService appDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    List<FaultInfoToShopDTO> faultInfoToShopDTOs = new ArrayList<FaultInfoToShopDTO>();
    try {
      LOG.warn("收到故障码信息：{}", JsonUtil.objectToJson(faultDTO));
      String[] vehicleFaultCodeArray = faultDTO.getFaultCode().split(",");
      Set<String> vehicleFaultCodeSet = new HashSet<String>(); //收到的全部的故障码
      Set<String> handledFaultCodeSet = new HashSet<String>();//已经处理的故障码
      for (String faultCode : vehicleFaultCodeArray) {
        if (StringUtils.isNotEmpty(faultCode)) {
          vehicleFaultCodeSet.add(faultCode);
        }
      }

      List<FaultInfoToShop> faultInfoToShops = writer.getUnHandledFaultInfoToShopsByVehicleNoFaultCode(vehicleDTO.getLicenceNo(),
        vehicleDTO.getShopId(), vehicleFaultCodeSet);
      if (CollectionUtils.isNotEmpty(faultInfoToShops)) {
        for (FaultInfoToShop faultInfoToShop : faultInfoToShops) {
          if (faultInfoToShop != null) {
            //当前的 appVehicleFaultInfo 已经处理，则认为重复数 更新为已删除
            if (handledFaultCodeSet.contains(faultInfoToShop.getFaultCode())) {
//              appVehicleFaultInfo.setStatus(ErrorCodeTreatStatus.DELETED);
//              writer.update(appVehicleFaultInfo);
            } else {
              faultInfoToShop.setFaultCodeReportTime(faultDTO.getReportTime());
              writer.update(faultInfoToShop);
              handledFaultCodeSet.add(faultInfoToShop.getFaultCode());
            }
          }
        }
      }
      //全部故障码 排除 已经处理过的故障码，再做新增操作
      vehicleFaultCodeSet.removeAll(handledFaultCodeSet);

      if (CollectionUtils.isNotEmpty(vehicleFaultCodeSet)) {

        Long commonVehicleBrandId = null;
        if (StringUtils.isNotEmpty(vehicleDTO.getBrand())) {
          IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
          StandardVehicleBrandDTO standardVehicleBrandDTO = standardBrandModelService.getStandardVehicleBrandDTOByName(vehicleDTO.getBrand());
          if (standardVehicleBrandDTO != null) {
            commonVehicleBrandId = standardVehicleBrandDTO.getId();
          }
        }
        Map<String, List<DictionaryFaultInfoDTO>> faultInfoMap = appDictionaryService.getDictionaryFaultInfoMapByBrandIdAndCodes(
          commonVehicleBrandId, vehicleFaultCodeSet);

        for (String faultCode : vehicleFaultCodeSet) {
          if (StringUtil.isEmpty(faultCode)) {
            continue;
          }
          FaultInfoToShop faultInfoToShop = new FaultInfoToShop();

          faultInfoToShop.fromVehicleDTOAndVehicleFaultDTO(vehicleDTO, customerDTO, faultDTO);
          faultInfoToShop.setFaultCodeInfo(faultCode, faultInfoMap.get(faultCode));


//          AppVehicleFaultInfo appVehicleFaultInfo = new
//          appVehicleFaultInfo.setErrorCode(faultCode);
//          appVehicleFaultInfo.setObdId(obdDTO.getId());
//          appVehicleFaultInfo.setStatus(ErrorCodeTreatStatus.UNTREATED);
//          appVehicleFaultInfo.setReportTime(faultDTO.getReportTime() == null ? System.currentTimeMillis() : faultDTO.getReportTime());
//          appVehicleFaultInfo.setAppVehicleId(appVehicleDTO.getVehicleId());
//          appVehicleFaultInfo.setFaultInfoContent(faultInfoMap.get(faultCode));

//          AppVehicleFaultInfoDTO appVehicleFaultInfoDTO = appVehicleFaultInfo.toDTO();
//          appVehicleFaultInfoDTO.setCategoryBackgroundInfo(faultInfoMap.get(faultCode));
//          toSendToShopFaultInfoList.add(appVehicleFaultInfoDTO);
          writer.save(faultInfoToShop);
          faultInfoToShopDTOs.add(faultInfoToShop.toDTO());
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    return faultInfoToShopDTOs;
  }


}
