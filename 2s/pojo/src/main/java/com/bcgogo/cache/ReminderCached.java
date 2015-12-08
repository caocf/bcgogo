package com.bcgogo.cache;

import com.bcgogo.notification.reminder.ReminderType;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存中缓存提醒的必要数据字段的数据结构
 * User: ndong
 * Date: 13-1-3
 * Time: 上午7:57
 * To change this template use File | Settings | File Templates.
 */
public class  ReminderCached{

  private Long reminderId;
  private Long lastReleaseDate;
  private ReminderType reminderType;
  private List<ReminderData> reminderDatas=new ArrayList<ReminderData>();  //存当天提醒的内容
  private Long syncTime;
  private Long lastIntervalTime;

  public Long getReminderId() {
    return reminderId;
  }

  public void setReminderId(Long reminderId) {
    this.reminderId = reminderId;
  }

  public Long getLastReleaseDate() {
    return lastReleaseDate;
  }

  public void setLastReleaseDate(Long lastReleaseDate) {
    this.lastReleaseDate = lastReleaseDate;
  }

  public ReminderType getReminderType() {
    return reminderType;
  }

  public void setReminderType(ReminderType reminderType) {
    this.reminderType = reminderType;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  public List<ReminderData> getReminderDatas() {
    return reminderDatas;
  }

  public Long getLastIntervalTime() {
    return lastIntervalTime;
  }

  public void setLastIntervalTime(Long lastIntervalTime) {
    this.lastIntervalTime = lastIntervalTime;
  }
}

