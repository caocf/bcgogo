package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-24
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class InventorySchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(InventorySchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    if (lock) {
      return;
    }
  }

  private static String getDateFromLongToString(Long time) {
    if (time == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Date date = new Date(time);
    return sdf.format(date);
  }

}
