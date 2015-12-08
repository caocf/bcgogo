package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserClientInfoDTO;
import com.bcgogo.user.dto.UserLoginLogDTO;
import com.bcgogo.user.service.IRequestMonitorService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计用户端设备信息
 * Author: ndong
 * Date: 2015-1-13
 * Time: 11:15
 */
public class UserClientStatSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(UserClientStatSchedule.class);


  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    try {
      if (isLock()) {
        return;
      }
      IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
      requestMonitorService.calcDeviceFingerScore();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally

    {
      lock = false;
    }

  }
}
