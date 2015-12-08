package com.bcgogo.socketReceiver.dao;

import com.bcgogo.socketReceiver.model.GsmObdStatus;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class GsmObdStatusDao extends BaseDao<GsmObdStatus> {

  public GsmObdStatusDao() {
    super(GsmObdStatus.class);
  }

}
