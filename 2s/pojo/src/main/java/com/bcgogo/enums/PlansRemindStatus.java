package com.bcgogo.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-10
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public enum PlansRemindStatus {
  activity("未提醒"),
  reminded("已提醒"),
  canceled("删除");

  String status;

  PlansRemindStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }

  public static List<PlansRemindStatus> getActivityAndReminded()
  {
    List<PlansRemindStatus> plansRemindStatusList = new ArrayList<PlansRemindStatus>();
    plansRemindStatusList.add(PlansRemindStatus.activity);
    plansRemindStatusList.add(PlansRemindStatus.reminded);
    return plansRemindStatusList;
  }
}
