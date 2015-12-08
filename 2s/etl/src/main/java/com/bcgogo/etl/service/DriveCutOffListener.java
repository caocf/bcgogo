package com.bcgogo.etl.service;

import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-8-5
 * Time: 13:31
 */
public class DriveCutOffListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(DriveCutOffListener.class);

  private GsmVehicleDataDTO data;
  @Autowired
  private IGSMVehicleDataService gsmVehicleDataService;

  public DriveCutOffListener(GsmVehicleDataDTO data) {
    this.data = data;
  }

  @Override
  public void run() {
    try {
      IGSMVehicleDataService gsmVehicleDataService= ServiceManager.getService(IGSMVehicleDataService.class);
      gsmVehicleDataService.generationDriveLogEveryTime(data);
    } catch (Exception e) {
     LOG.error(e.getMessage(),e);
    }
  }

}
