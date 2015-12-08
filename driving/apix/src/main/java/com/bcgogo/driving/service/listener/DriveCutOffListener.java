package com.bcgogo.driving.service.listener;

import com.bcgogo.driving.service.IDriveLogService;
import com.bcgogo.pojox.api.GsmVehicleDataDTO;
import com.bcgogo.driving.service.IGSMVehicleDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-8-5
 * Time: 13:31
 */
@Component
public class DriveCutOffListener extends BcgogoEventListener implements IListener{
  public static final Logger LOG = LoggerFactory.getLogger(DriveCutOffListener.class);

  public DriveCutOffListener(){}

  @Autowired
  private IDriveLogService driveLogService;


  private GsmVehicleDataDTO data;

  public DriveCutOffListener(GsmVehicleDataDTO data) {
    this.data = data;
  }

  @Override
  public void run() {
    try {
      driveLogService.saveDriveLog(data);
    } catch (Exception e) {
     LOG.error(e.getMessage(),e);
    }
  }

}
