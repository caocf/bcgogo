package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveStatDTO;
import com.bcgogo.enums.app.DriveStatStatus;

import java.util.List;

/**
 * Created by XinyuQiu on 14-5-4.
 */
public interface IDriveStatService {

  ApiResponse getYearDriveStat(String appUserNo);

  List<DriveStatDTO> getDriveStatDTOsByStatDate(String appUserNo, Long startTime, Long endTime);

  void statDriveLog(int limit);

  DriveStatDTO getDriveStatDTOByYearAndMonth(String appUserNo, int year, int month);

  void saveOrUpdateDriveStatServiceDTO(DriveStatDTO driveStatDTO);

  void updateDriveLogStatStatus(Long id, DriveStatStatus statistic);
}
