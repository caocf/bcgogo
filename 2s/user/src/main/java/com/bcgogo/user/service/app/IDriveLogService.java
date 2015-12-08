package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.api.DriveLogPlaceNoteDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.app.DriveLogStatus;
import com.bcgogo.enums.app.DriveStatStatus;
import com.bcgogo.etl.model.GsmVehicleInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-13
 * Time: 上午9:15
 */
public interface IDriveLogService {

  ApiResponse validateSaveDriveLog(DriveLogDTO driveLogDTO);

  ApiResponse handleSaveDriveLog(DriveLogDTO driveLogDTO, boolean isUpdatePlaceNote);

  List<DriveLogDTO> getDriveLogContents(String appUserNo, Long startTime, Long endTime);

  List<DriveLogDTO> getDriveLogDTOsByStartTime(String appUserNo, Long startTime, Long endTime);

  DriveLogDTO getDriveLogDetail(String appUserNo, Long driveLogId);

  List<DriveLogDTO> getDriveLogDetailDTOsByIds(String appUserNo, Set<Long> contactIds, boolean isContainPlaceNotes);

  void generateDriveLogByGsmVehicleInfo(int limit, Set<String> imeis);

  List<DriveLogDTO> getDriveLogDetailByStatusOrderByEndTimeAsc(String userNo, DriveLogStatus driveLogStatus, int limit);

  List<DriveLogDTO> getDriveLogContentsByStatusAndStatStatus(String userNo, Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus);

  List<DriveLogDTO> getDriveLogContentsByStatusAndStatStatus(Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus, int limit);

  Map<String, List<DriveLogDTO>> getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(Set<String> userNos, DriveLogStatus driveLogStatus);

  DriveLogDTO finishNotLastDriveLog(List<DriveLogDTO> driveLogDTOs);

  void saveOrUpdateLastDriveLog(DriveLogDTO lastDriveLogDTO, List<GsmVehicleInfo> gsmVehicleInfoList);

  List<DriveLogDTO> getDriveLogDTOsByImeiTime(String imei, Long startTime, Long endTime, Pager pager);

  int countDriveLogDTOsByImeiTime(String imei, Long startTime, Long endTime);

  public List<DriveLogPlaceNoteDTO> getDriveLogPlaceNoteByLogIds(String appUserNo, Set<Long> driveLogIds);

  public DriveLogDTO getDriveLogDTOWidthPlaceNoteById(Long driveLogId);

  DriveLogDTO getDriveLogByAppUserNoAndId(String appUserNo, Long driveLogId);

  DriveLogDTO getDriveLogDTOById(Long driveLogId);

  //后视镜根据时间查询行车轨迹
  List<DriveLogDTO> getDriveLogDTOsByTime(String appUserNo, Long startTime, Long endTime);

  List<DriveLogDTO> getDriveLogDTOsByTime_wx(String appUserNo, Long startTime, Long endTime);

  List<DriveLogDTO> getLastDriveLog(String appUserNo, int limit);

  List<DriveLogDTO> getDriveLogDTOList(String appUserNo, Long startTime, Long endTime);

  void saveOrUpdateDriveLog(DriveLogDTO driveLogDTO);

  void generateDriveLog(DriveLogDTO driveLogDTO) throws IOException;


}
