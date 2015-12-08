package com.bcgogo.socketReceiver.dao;

import com.bcgogo.socketReceiver.model.GsmObdStatus;
import com.bcgogo.socketReceiver.model.GsmVehicleInfo;
import org.springframework.stereotype.Repository;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 下午5:49
 */
@Repository
public class GsmVehicleInfoDao extends BaseDao<GsmVehicleInfo> {

  public GsmVehicleInfoDao() {
    super(GsmVehicleInfo.class);
  }

}
