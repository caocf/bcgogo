package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.api.response.ApiDriveLogDetailsResponse;
import com.bcgogo.api.response.ApiDriveLogsResponse;
import com.bcgogo.api.response.ApiMirrorDriveLogDetailResponse;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.enums.app.DriveLogStatus;
import com.bcgogo.enums.app.DriveStatStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-9
 * Time: 上午11:02
 */
@Controller
@RequestMapping("/driveLog/*")
public class DriveLogController {
  private static final Logger LOG = LoggerFactory.getLogger(DriveLogController.class);

  //新增行车日志
  @ResponseBody
  @RequestMapping(value = "/newDriveLog", method = RequestMethod.PUT)
  public ApiResponse saveDriveLog(HttpServletRequest request, HttpServletResponse response, @RequestBody DriveLogDTO driveLogDTO) {
    IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse = driveLogService.validateSaveDriveLog(driveLogDTO);
      if (MessageCode.DRIVE_LOG_SAVED_SUCCESS.getCode() == apiResponse.getMsgCode()) {
        driveLogDTO.setAppUserNo(appUserNo);
        driveLogDTO.setStatus(DriveLogStatus.ENABLED);
        driveLogDTO.setDriveStatStatus(DriveStatStatus.UN_STATISTIC);
        if (driveLogDTO.getEndTime() != null && driveLogDTO.getStartTime() != null && driveLogDTO.getEndTime() > driveLogDTO.getStartTime()) {
          driveLogDTO.setTravelTime((driveLogDTO.getEndTime() - driveLogDTO.getStartTime()) / 1000);
        }
        driveLogDTO.setLastUpdateTime(System.currentTimeMillis());
        apiResponse = driveLogService.handleSaveDriveLog(driveLogDTO, driveLogDTO.getId() == null);
        if (apiResponse != null && apiResponse instanceof ApiResultResponse) {
          ApiResultResponse<DriveLogDTO> apiResultResponse = (ApiResultResponse<DriveLogDTO>) apiResponse;
          if (apiResultResponse.getResult() != null) {
            apiResultResponse.getResult().setPlaceNotes(null);
          }
        }
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error("driveLog/newDriveLog" + e.getMessage(), e);
      LOG.error("driveLogDTO :{}", driveLogDTO);
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_SAVED_EXCEPTION);
    }
  }

  //查询行车目录列表
  @ResponseBody
  @RequestMapping(value = "/driveLogContents/{startTime}/{endTime}", method = RequestMethod.GET)
  public ApiResponse getDriveLogs(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("startTime") Long startTime,
                                  @PathVariable("endTime") Long endTime) {
    try {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.DRIVE_LOG_CONTENTS_SUCCESS);
      List<DriveLogDTO> driveLogDTOs = driveLogService.getDriveLogDTOsByStartTime(appUserNo, startTime, endTime);

      ApiDriveLogsResponse apiDriveLogsResponse = new ApiDriveLogsResponse(apiResponse, driveLogDTOs);
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(appUserNo));
      if (appVehicleDTO != null) {
        apiDriveLogsResponse.setWorstOilWear(appVehicleDTO.getWorstOilWear());
        apiDriveLogsResponse.setBestOilWear(appVehicleDTO.getBestOilWear());
        apiDriveLogsResponse.setTotalOilWear(appVehicleDTO.getAvgOilWear());
      }
      return apiDriveLogsResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_CONTENTS_EXCEPTION);
    }
  }

  //更新行车日志  todo by qxy
  @ResponseBody
  @RequestMapping(value = "/existedDriveLog", method = RequestMethod.PUT)
  public ApiResponse updateDriveLog(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody DriveLogDTO driveLogDTO) {
    return new ApiResponse();
  }


  //查询行车日志详情
  @ResponseBody
  @RequestMapping(value = "/detail/{contentIds}/{detailIds}", method = RequestMethod.GET)
  public ApiResponse getDriveLogDetail(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("contentIds") String contentIdStr,
                                       @PathVariable("detailIds") String detailIdStr) {
    try {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.DRIVE_LOG_DETAIL_SUCCESS);
      Set<Long> contentIds = NumberUtil.parseLongValuesToSet(contentIdStr, NumberUtil.SPLIT_REGEX);
      List<DriveLogDTO> contentDriveLogDTOs = driveLogService.getDriveLogDetailDTOsByIds(appUserNo, contentIds, false);

      Set<Long> detailIds = NumberUtil.parseLongValuesToSet(detailIdStr, NumberUtil.SPLIT_REGEX);
      List<DriveLogDTO> detailDriveLogDTOs = driveLogService.getDriveLogDetailDTOsByIds(appUserNo, detailIds, true);

      return new ApiDriveLogDetailsResponse(apiResponse, contentDriveLogDTOs, detailDriveLogDTOs);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_DETAIL_EXCEPTION);
    }
  }

  //查询行车日志详情
  @ResponseBody
  @RequestMapping(value = "mirror/detail/{driveLogId}", method = RequestMethod.GET)
  public ApiResponse getDriveLogDetail(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("driveLogId") Long driveLogId) {
    try {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      DriveLogDTO driveLogDTO = driveLogService.getDriveLogDetail(appUserNo, driveLogId);
      ApiMirrorDriveLogDetailResponse driveLogDetailResponse = new ApiMirrorDriveLogDetailResponse(driveLogDTO);
      driveLogDetailResponse.setMessageCode(MessageCode.DRIVE_LOG_DETAIL_SUCCESS);
      return driveLogDetailResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_DETAIL_EXCEPTION);
    }
  }

  /**
   * 删除行车日志
   * @param request
   * @param response
   * @param driveLogId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "mirror/driveLog/delete/{driveLogId}", method = RequestMethod.GET)
  public ApiResponse deleteDriveLog(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("driveLogId") Long driveLogId) {
    try {
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      DriveLogDTO driveLogDTO = driveLogService.getDriveLogDTOById(driveLogId);
      if(driveLogDTO==null){
        return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_NOT_EXIST);
      }
      driveLogDTO.setStatus(DriveLogStatus.DISABLED);
      driveLogService.saveOrUpdateDriveLog(driveLogDTO);
      return MessageCode.toApiResponse(MessageCode.SUCCESS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_DELETE_EXCEPTION);
    }
  }

}
