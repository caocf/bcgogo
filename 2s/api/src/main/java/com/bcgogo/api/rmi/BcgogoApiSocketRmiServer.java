package com.bcgogo.api.rmi;

import com.bcgogo.api.*;
import com.bcgogo.common.Result;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import com.bcgogo.enums.app.GsmPointType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.etl.model.EtlDaoManager;
import com.bcgogo.etl.model.EtlWriter;
import com.bcgogo.etl.model.GsmPoint;
import com.bcgogo.etl.model.GsmVehicleInfo;
import com.bcgogo.etl.service.IGsmPointService;
import com.bcgogo.etl.service.IGsmVehicleService;
import com.bcgogo.product.service.IStandardBrandModelService;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.service.app.IHandleAppVehicleFaultCodeService;
import com.bcgogo.txn.service.pushMessage.IVehicleFaultPushMessage;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.app.AppVehicleFaultInfo;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserShopBindingService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * BCGOGO RMI 服务端，用于可能的来自socketReceiver的远程方法调用
 * User: Jimuchen
 * Date: 14-3-10
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */

@Component
public class BcgogoApiSocketRmiServer implements IBcgogoApiSocketRmiServer {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoApiSocketRmiServer.class);

  @Override
  public Result sendFaultCode(String imei, String faultCodes, Long reportTime) {
    try {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(faultCodes)) {
        IHandleAppVehicleFaultCodeService handleAppVehicleFaultCodeService = ServiceManager.getService(IHandleAppVehicleFaultCodeService.class);
        IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
        IVehicleFaultPushMessage vehicleFaultPushMessage = ServiceManager.getService(IVehicleFaultPushMessage.class);
        AppVehicleDTO appVehicleDTO = null;
        AppUserDTO appUserDTO = null;
        VehicleDTO vehicleDTO = null;

        ObdDTO obdDTO = appUserVehicleObdService.getObdByIMei(imei);
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
        vehicleDTO = vehicleService.getVehicleDTOByIMei(imei);
        if (vehicleDTO == null) {
          LOG.error("根据IMei号：{}找不到对应的客户车辆", imei);
        }
        if (vehicleDTO != null || appUserDTO != null) {
          VehicleFaultDTO faultDTO = new VehicleFaultDTO();
          if(appUserDTO != null){
            faultDTO.setUserNo(appUserDTO.getUserNo());
            faultDTO.setVehicleId(appVehicleDTO.getVehicleId());
          }

          faultDTO.setFaultCode(faultCodes);
          if (reportTime == null) {
            reportTime = System.currentTimeMillis();
          }
          faultDTO.setReportTime(reportTime);
          faultDTO.setImei(imei);
          List<AppVehicleFaultInfoDTO> toSendToShopFaultInfoList = new ArrayList<AppVehicleFaultInfoDTO>();
          if(appUserDTO != null){
            toSendToShopFaultInfoList = handleAppVehicleFaultCodeService.saveAppVehicleFaultInfoDTOs(faultDTO, obdDTO, appVehicleDTO);
          }
//          List<AppVehicleFaultInfoDTO> toSendToShopFaultInfoList = handleAppVehicleFaultCodeService.saveAppVehicleFaultInfoDTOs(faultDTO, obdDTO, appVehicleDTO);
          //给店铺，app发消息
          if (CollectionUtils.isNotEmpty(toSendToShopFaultInfoList)) {

            IAppUserShopBindingService appUserShopBindingService = ServiceManager.getService(IAppUserShopBindingService.class);
            Set<Long> receiveShopIds = new HashSet<Long>();
            if(appUserDTO != null ){
              receiveShopIds.addAll(appUserShopBindingService.getBindingShopIds(appUserDTO.getUserNo()));
            }
            if(vehicleDTO != null){
              receiveShopIds.add(vehicleDTO.getShopId());
            }
//            Set<Long> receiveShopIds = appUserShopBindingService.getBindingShopIds(appUserDTO.getUserNo());
            for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : toSendToShopFaultInfoList) {
              if (appVehicleFaultInfoDTO != null && CollectionUtils.isNotEmpty(receiveShopIds)) {
                for (Long receiveShopId : receiveShopIds) {
                  FaultInfoToShopDTO faultInfoToShopDTO = vehicleFaultPushMessage.createFaultInfoToShop(receiveShopId, appVehicleFaultInfoDTO, appUserDTO, appVehicleDTO);
                  VehicleFaultParameter vehicleFaultParameter = new VehicleFaultParameter(
                      appUserDTO, appVehicleFaultInfoDTO, receiveShopId, appVehicleDTO, faultInfoToShopDTO);
                  if(vehicleDTO != null){
                    vehicleFaultPushMessage.createVehicleFaultMessage2Shop(vehicleFaultParameter);
                  }
                }
              }
              if (appVehicleFaultInfoDTO != null && appUserDTO != null) {
                vehicleFaultPushMessage.createVehicleFaultMessage2App(appUserDTO, appVehicleFaultInfoDTO);
              }
            }
          }else if(appUserDTO == null && vehicleDTO != null){//没有注册app的
            IUserService userService = ServiceManager.getService(IUserService.class);
            CustomerDTO customerDTO =  userService.getCustomerInfoByVehicleId(vehicleDTO.getShopId(),vehicleDTO.getId());

            List<FaultInfoToShopDTO> faultInfoToShopDTOs =  saveVehicleFaultCodeWithOutApp(vehicleDTO,customerDTO,faultDTO);
            if(CollectionUtils.isNotEmpty(faultInfoToShopDTOs)){
              for(FaultInfoToShopDTO faultInfoToShopDTO :faultInfoToShopDTOs){
                VehicleFaultParameter vehicleFaultParameter = new VehicleFaultParameter(vehicleDTO,customerDTO,faultInfoToShopDTO);
                vehicleFaultPushMessage.createVehicleFaultMessage2Shop(vehicleFaultParameter);
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

  private List<FaultInfoToShopDTO> saveVehicleFaultCodeWithOutApp(VehicleDTO vehicleDTO,CustomerDTO customerDTO, VehicleFaultDTO faultDTO) {
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

          faultInfoToShop.fromVehicleDTOAndVehicleFaultDTO(vehicleDTO,customerDTO, faultDTO);
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

  @Override
  public Result sendAlert(String imei, String lat, String lon, String gsmPointTypeStr, String uploadTimeStr) {
    if (StringUtils.isNotEmpty(imei) && GsmPointType.parseValue(gsmPointTypeStr) != null) {
      try {
        if (GsmPointType.ZD.equals(GsmPointType.parseValue(gsmPointTypeStr))) {
          return new Result(true);
        } else if (GsmPointType.PZ.equals(GsmPointType.parseValue(gsmPointTypeStr))) {
          handelPZAlert(imei, lat, lon, uploadTimeStr);
          return new Result(true);
        }else if(GsmPointType.WY.equals(GsmPointType.parseValue(gsmPointTypeStr))){
          LOG.error("imei:【{}】【{}】,发生位移报警，暂时不发给店铺，先做异常处理",imei,uploadTimeStr);
          return new Result(true);
        }else{
          handelCommonAlert(imei, lat, lon, GsmPointType.parseValue(gsmPointTypeStr), uploadTimeStr);
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
        return new Result(false);
      }

    }
    return new Result();
  }

  private Result handelCommonAlert(String imei, String lat, String lon, GsmPointType gsmPointType, String uploadTimeStr)throws Exception{
    IVehicleFaultPushMessage vehicleFaultPushMessage = ServiceManager.getService(IVehicleFaultPushMessage.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppVehicleDTO appVehicleDTO = null;
    AppUserDTO appUserDTO = null;
    VehicleDTO vehicleDTO = null;
    ObdDTO obdDTO = appUserVehicleObdService.getObdByIMei(imei);
    if (obdDTO == null) {
      LOG.error("根据IMei号：{}找不到对应的OBD", imei);
    } else {
      ObdUserVehicleDTO obdUserVehicleDTO = appUserVehicleObdService.getBundlingObdUserVehicleDTOByObdId(obdDTO.getId());
      if (obdUserVehicleDTO == null) {
        LOG.error("根据ObdId：{}找不到对应的obdUserVehicleDTO", obdDTO.getId());
      } else {
        appVehicleDTO = appUserService.getAppVehicleDTOById(obdUserVehicleDTO.getAppVehicleId());
        if (appVehicleDTO == null) {
          LOG.error("根据IMei号：{}找不到对应的app车辆", imei);
        }
        if (appVehicleDTO != null && StringUtils.isNotEmpty(appVehicleDTO.getUserNo())) {
          appUserDTO = appUserService.getAppUserByUserNo(appVehicleDTO.getUserNo(), null);
        }
        if (appUserDTO == null) {
          LOG.error("根据appUserNo：{}找不到对客户", imei);
        }
      }
    }
    vehicleDTO = vehicleService.getVehicleDTOByIMei(imei);
    if (vehicleDTO == null) {
      LOG.error("根据IMei号：{}找不到对应的客户车辆", imei);
    }
    if (vehicleDTO != null || appUserDTO != null) {
      FaultInfoToShopDTO faultInfoToShopDTO = new FaultInfoToShopDTO();
      faultInfoToShopDTO.setFaultAlertType(FaultAlertType.parseFromGsmPointStr(gsmPointType));
      faultInfoToShopDTO.setLat(NumberUtil.convertGPSLat(lat));
      faultInfoToShopDTO.setLon(NumberUtil.convertGPSLot(lon));
      faultInfoToShopDTO.setFaultCodeReportTime(NumberUtil.longValue(uploadTimeStr));

      faultInfoToShopDTO.setFaultCodeDescription(FaultAlertType.getContent(faultInfoToShopDTO.getFaultAlertType()));
      if(vehicleDTO != null){
        faultInfoToShopDTO.setShopId(vehicleDTO.getShopId());
        faultInfoToShopDTO.setVehicleId(vehicleDTO.getId());
        faultInfoToShopDTO.setVehicleNo(vehicleDTO.getLicenceNo());
        faultInfoToShopDTO.setVehicleBrand(vehicleDTO.getBrand());
        faultInfoToShopDTO.setVehicleModel(vehicleDTO.getModel());
      }

      if (appUserDTO != null) {
        faultInfoToShopDTO.setAppUserNo(appUserDTO.getUserNo());
        faultInfoToShopDTO.setMobile(appUserDTO.getMobile());
      }
      if (appVehicleDTO != null) {
        faultInfoToShopDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
        faultInfoToShopDTO.setAppVehicleId(appVehicleDTO.getVehicleId());
        faultInfoToShopDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
        faultInfoToShopDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
      }

      faultInfoToShopDTO.setIsSendMessage(YesNo.NO);
      faultInfoToShopDTO.setIsCreateAppointOrder(YesNo.NO);
      faultInfoToShopDTO.setStatus(Status.ACTIVE);
      if(vehicleDTO != null){
        vehicleFaultPushMessage.createAlertInfoToShop(faultInfoToShopDTO);
      }
      CustomerDTO customerDTO = null;
      if(vehicleDTO !=null && appUserDTO ==null){
        IUserService userService = ServiceManager.getService(IUserService.class);
        customerDTO =  userService.getCustomerInfoByVehicleId(vehicleDTO.getShopId(),vehicleDTO.getId());
      }
      vehicleFaultPushMessage.createVehicleFaultAlertMessage2Shop(appUserDTO,vehicleDTO, customerDTO,faultInfoToShopDTO);

    }
    return new Result(true);
  }

  private void handelPZAlert(final String imei, final String lat, final String lon, final String uploadTimeStr) {
   new Thread(new Runnable() {
      @Override
      public void run() {
        //todo by qxy 下面代码下次配合sockReceiver 一起优化

//        IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
        IGsmVehicleService gsmVehicleService = ServiceManager.getService(IGsmVehicleService.class);
        Long uploadTime = NumberUtil.longValue(uploadTimeStr);
        if (System.currentTimeMillis() - uploadTime < 3600000L) {//一个小时前的数据就不处理了
//          List<GsmPoint> pzGSMPoints = gsmPointService.getGsmPointsByImeiGsmPointTypeUploadTime(imei, GsmPointType.PZ,
//              uploadTime - 40000, uploadTime + 40000, GsmVehicleStatus.UN_HANDLE);
//          if (CollectionUtils.isNotEmpty(pzGSMPoints)) {
//            gsmPointService.updateGsmPointStatus(pzGSMPoints, GsmVehicleStatus.HANDLED);
            boolean isCrash = true;
//            for (GsmPoint gsmPoint : pzGSMPoints) {
//              if (gsmPoint != null && NumberUtil.intValue(gsmPoint.getImpactStrength(), 0) >= GSMConstant.MIN_CRASH_STRENGTH) {
//                isCrash = true;
//                break;
//              }
//            }
            if (isCrash) {
              List<GsmVehicleInfo> gsmVehicleInfoList = gsmVehicleService.getGsmVehicleInfoByImeiAndUploadTime(imei, uploadTime - 40000, uploadTime);
              boolean isBeforeHaveSpeed = false;
              boolean isAfterHaveSpeed = false;

              if (CollectionUtils.isNotEmpty(gsmVehicleInfoList)) {
                for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfoList) {
                  if (gsmVehicleInfo != null && gsmVehicleInfo.getUploadTime() != null) {
                    //碰撞前行程速度要大于最小碰撞速度
                    if (gsmVehicleInfo.getUploadTime() <= uploadTime
                        && gsmVehicleInfo.getUploadTime() > uploadTime - 40000
                        && NumberUtil.doubleVal(gsmVehicleInfo.getVss()) >= GSMConstant.MIN_CRASH_SPEED) {
                      isBeforeHaveSpeed = true;
                    }
                  }
                }
              }
              try {
                if (isBeforeHaveSpeed) {
                    Thread.sleep(40000);
                  List<GsmVehicleInfo> afterGsmVehicleInfoList = gsmVehicleService.getGsmVehicleInfoByImeiAndUploadTime(imei, uploadTime, uploadTime + 40000);
                    if (CollectionUtils.isNotEmpty(afterGsmVehicleInfoList)) {
                      for (GsmVehicleInfo gsmVehicleInfo : afterGsmVehicleInfoList) {
                        if (gsmVehicleInfo.getUploadTime() > uploadTime
                            && gsmVehicleInfo.getUploadTime() < uploadTime + 40000
                            && NumberUtil.doubleVal(gsmVehicleInfo.getVss()) <= GSMConstant.MIN_CRASH_STOP_SPEED) {
                          isAfterHaveSpeed = true;
                        }
                      }
                    }
                  if (isAfterHaveSpeed) {
                    handelCommonAlert(imei, lat, lon, GsmPointType.PZ, uploadTimeStr);
                  }
                }
              } catch (Exception e) {
                LOG.error(e.getMessage(), e);
              }
            }
//          }
        }
      }
    }).start();
  }
}
