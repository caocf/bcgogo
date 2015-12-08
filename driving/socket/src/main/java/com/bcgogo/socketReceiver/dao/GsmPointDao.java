package com.bcgogo.socketReceiver.dao;

import com.bcgogo.socketReceiver.model.GsmObdStatus;
import com.bcgogo.socketReceiver.model.GsmPoint;
import org.springframework.stereotype.Repository;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 下午5:49
 */
@Repository
public class GsmPointDao extends BaseDao<GsmPoint> {

  public GsmPointDao() {
    super(GsmPoint.class);
  }

}
