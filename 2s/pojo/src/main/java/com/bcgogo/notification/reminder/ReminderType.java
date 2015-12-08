package com.bcgogo.notification.reminder;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午2:46
 * To change this template use File | Settings | File Templates.
 */
public enum ReminderType {
  TRIAL_USE_DAYS("试用到期公告"),
  ANNOUNCEMENT("系统公告"),
  FESTIVAL("节日");

  String name;

  ReminderType(String name){
    this.name = name;
  }

  public String getName(){
    return this.name;
  }

}
