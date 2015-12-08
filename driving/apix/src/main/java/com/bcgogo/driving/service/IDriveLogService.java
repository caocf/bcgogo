package com.bcgogo.driving.service;

import com.bcgogo.pojox.api.DriveLogDTO;
import com.bcgogo.pojox.api.GsmTBoxDataDTO;
import com.bcgogo.pojox.api.GsmVehicleDataDTO;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午2:32
 */
public interface IDriveLogService {

  void saveOrUpdateDriveLog(DriveLogDTO driveLogDTO);

  void handleDriveLog(GsmVehicleDataDTO dataDTO);

  void saveDriveLog(GsmVehicleDataDTO cutOffData) throws IOException, IllegalAccessException;

  void generationDriveLog(GsmTBoxDataDTO cutOffData) throws IOException, IllegalAccessException;


}
