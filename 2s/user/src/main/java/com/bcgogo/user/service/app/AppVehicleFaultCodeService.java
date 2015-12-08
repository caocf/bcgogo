package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.api.AppVehicleFaultInfoOperateDTO;
import com.bcgogo.api.request.FaultCodeListRequest;
import com.bcgogo.api.response.AppVehicleFaultInfoListResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppVehicleFaultInfo;
import com.bcgogo.user.model.app.AppVehicleFaultInfoOperateLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-28
 * Time: 上午10:10
 */
@Component
public class AppVehicleFaultCodeService implements IAppVehicleFaultCodeService {
  private static final Logger LOG = LoggerFactory.getLogger(AppVehicleFaultCodeService.class);
   @Autowired
   private UserDaoManager userDaoManager;

  @Override
  public ApiResponse handleFaultCode(String appUserNo, AppVehicleFaultInfoOperateDTO appVehicleFaultInfoOperateDTO) {
    Set<ErrorCodeTreatStatus> allowableStatus = new HashSet<ErrorCodeTreatStatus>();
    allowableStatus.add(ErrorCodeTreatStatus.DELETED);
    allowableStatus.add(ErrorCodeTreatStatus.IGNORED);
    allowableStatus.add(ErrorCodeTreatStatus.FIXED);
    if (StringUtils.isEmpty(appUserNo)) {
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_OPERATE_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
    }
    if (appVehicleFaultInfoOperateDTO == null || ArrayUtils.isEmpty(appVehicleFaultInfoOperateDTO.getAppVehicleFaultInfoDTOs())) {
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_OPERATE_FAIL, ValidateMsg.APP_VEHICLE_FAULT_CODE_EMPTY);
    } else {
      AppVehicleFaultInfoDTO[] appVehicleFaultInfoDTOs = appVehicleFaultInfoOperateDTO.getAppVehicleFaultInfoDTOs();
      for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : appVehicleFaultInfoDTOs) {
        if (appVehicleFaultInfoDTO != null && appVehicleFaultInfoDTO.getId() != null) {
          if (!allowableStatus.contains(appVehicleFaultInfoDTO.getStatus())) {
            return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_OPERATE_FAIL, ValidateMsg.APP_VEHICLE_FAULT_CODE_OPERATION_ILLEGAL);
          }
        }
      }
    }

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (AppVehicleFaultInfoDTO appVehicleFaultInfoDTO : appVehicleFaultInfoOperateDTO.getAppVehicleFaultInfoDTOs()) {
        if (appVehicleFaultInfoDTO != null) {
          List<AppVehicleFaultInfo> appVehicleFaultInfoList = writer.getAppVehicleFaultInfo(
              appUserNo,
              appVehicleFaultInfoDTO.getAppVehicleId(),
              appVehicleFaultInfoDTO.getErrorCode(),
              appVehicleFaultInfoDTO.getLastStatus());
          if (CollectionUtils.isNotEmpty(appVehicleFaultInfoList)) {
            for (AppVehicleFaultInfo appVehicleFaultInfo : appVehicleFaultInfoList) {
              if (appVehicleFaultInfo != null) {
                AppVehicleFaultInfoOperateLog appVehicleFaultInfoOperateLog = new AppVehicleFaultInfoOperateLog();
                appVehicleFaultInfoOperateLog.setAppVehicleFaultInfoId(appVehicleFaultInfo.getId());
                appVehicleFaultInfoOperateLog.setLastStatus(appVehicleFaultInfo.getStatus());
                appVehicleFaultInfo.setStatus(appVehicleFaultInfoDTO.getStatus());
                appVehicleFaultInfo.setLastOperateTime(System.currentTimeMillis());
                writer.update(appVehicleFaultInfo);

                appVehicleFaultInfoOperateLog.setOperateUserNo(appUserNo);
                appVehicleFaultInfoOperateLog.setNewStatus(appVehicleFaultInfo.getStatus());
                appVehicleFaultInfoOperateLog.setOperateTime(appVehicleFaultInfo.getLastOperateTime());
                writer.save(appVehicleFaultInfoOperateLog);
              }
            }
          }
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_OPERATE_SUCCESS);
  }



  @Override
  public ApiResponse getFaultInfoList( FaultCodeListRequest faultCodeListRequest) throws Exception{
    if(StringUtils.isEmpty(faultCodeListRequest.getAppUserNo())){
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_LIST_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
    }
    if(ArrayUtils.isEmpty(faultCodeListRequest.getStatus())){
      return MessageCode.toApiResponse(MessageCode.APP_VEHICLE_FAULT_CODE_LIST_FAIL, ValidateMsg.APP_VEHICLE_FAULT_CODE_SEARCH_ILLEGAL);
    }
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);

//    AppVehicleDTO defaultAppVehicleDTO = appUserService.getDefaultAppVehicleByAppUserNo(faultCodeListRequest.getAppUserNo());
//    Long defaultAppVehicleId = null;
//    if(defaultAppVehicleDTO != null && defaultAppVehicleDTO.getVehicleId() != null){
//      defaultAppVehicleId =  defaultAppVehicleDTO.getVehicleId();
//    }
    int count = countAppVehicleFaultInfoDTOs(faultCodeListRequest.getAppUserNo(),faultCodeListRequest.getVehicleId(),
        faultCodeListRequest.getStatus());
    Pager pager = new Pager(count, faultCodeListRequest.getPageNo(), faultCodeListRequest.getPageSize());
    List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOs = searchAppVehicleFaultInfoDTOs(faultCodeListRequest.getAppUserNo(),
        faultCodeListRequest.getVehicleId(), pager, faultCodeListRequest.getStatus());
    ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_LIST_SUCCESS);
    AppVehicleFaultInfoListResponse apiResultResponse = new AppVehicleFaultInfoListResponse(apiResponse);
    apiResultResponse.setResult(appVehicleFaultInfoDTOs);
    apiResultResponse.setPager(pager);
    return apiResultResponse;
  }



  @Override
  public List<AppVehicleFaultInfoDTO> searchAppVehicleFaultInfoDTOs(String appUserNo,Long defaultAppVehicleId, Pager pager,
                                                                    ErrorCodeTreatStatus[] status) {
    List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOs = new ArrayList<AppVehicleFaultInfoDTO>();
    if (StringUtils.isEmpty(appUserNo) || pager == null) {
      return appVehicleFaultInfoDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppVehicleFaultInfo> appVehicleFaultInfoList = writer.searchAppVehicleFaultInfoList(appUserNo,defaultAppVehicleId, pager, status);
    if (CollectionUtils.isNotEmpty(appVehicleFaultInfoList)) {
      for (AppVehicleFaultInfo appVehicleFaultInfo : appVehicleFaultInfoList) {
        if (appVehicleFaultInfo != null) {
          appVehicleFaultInfoDTOs.add(appVehicleFaultInfo.toDTO());
        }
      }
    }
    return appVehicleFaultInfoDTOs;
  }

  public int countAppVehicleFaultInfoDTOs(String appUserNo, Long defaultAppVehicleId, ErrorCodeTreatStatus[] status) {
    if (StringUtils.isEmpty(appUserNo)) {
      return 0;
    }
    return userDaoManager.getWriter().countAppVehicleFaultInfoList(appUserNo,defaultAppVehicleId, status);
  }

  @Override
  public List<AppVehicleFaultInfoDTO> findAppVehicleFaultInfoDTOs(String appUserNo,String status) {
    List<AppVehicleFaultInfoDTO> appVehicleFaultInfoDTOs = new ArrayList<AppVehicleFaultInfoDTO>();
    if (StringUtils.isEmpty(appUserNo)) {
      return appVehicleFaultInfoDTOs;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppVehicleFaultInfo> appVehicleFaultInfoList = writer.findAppVehicleFaultInfoList(appUserNo, status);
    if (CollectionUtils.isNotEmpty(appVehicleFaultInfoList)) {
      for (AppVehicleFaultInfo appVehicleFaultInfo : appVehicleFaultInfoList) {
        if (appVehicleFaultInfo != null) {
          appVehicleFaultInfoDTOs.add(appVehicleFaultInfo.toDTO());
        }
      }
    }
    return appVehicleFaultInfoDTOs;
  }

  @Override
  public AppVehicleFaultInfo getAppVehicleFaultInfoById(Long id) {
    if(id == null){
      return null;
    }
    AppVehicleFaultInfo appVehicleFaultInfo = userDaoManager.getWriter().getById(AppVehicleFaultInfo.class,id);
    if(appVehicleFaultInfo != null){
      return appVehicleFaultInfo;
    }
    return null;
  }

  @Override
  public void updateAppVehicleFaultInfo(AppVehicleFaultInfo appVehicleFaultInfo) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(appVehicleFaultInfo);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

}
