package com.bcgogo.socketReceiver.service.impl;

import com.bcgogo.socketReceiver.dao.BaseDao;
import com.bcgogo.socketReceiver.dao.GsmObdStatusDao;
import com.bcgogo.socketReceiver.model.GsmObdStatus;
import com.bcgogo.socketReceiver.service.IGsmObdStatusService;
import com.bcgogo.socketReceiver.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-28
 * Time: 下午5:51
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GsmObdStatusService extends BaseService<GsmObdStatus> implements IGsmObdStatusService {
  @Autowired
  GsmObdStatusDao dao;

  @Override
  public BaseDao<GsmObdStatus> getDAO() {
    return dao;
  }
}
