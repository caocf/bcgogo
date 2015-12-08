package com.bcgogo.cache;

import com.bcgogo.notification.reminder.ReminderType;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午5:24
 * To change this template use File | Settings | File Templates.
 */
public class UserData{

  private Long shopId;
  private Long userId;
  private ReminderType readType;
  private UserReadRecordDTO readRecordDTO;
  private Long syncTime;
  private String invalidDate;

  public UserData(){
  }

  public UserData(Long shopId,Long userId){

  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public ReminderType getReadType() {
    return readType;
  }

  public void setReadType(ReminderType readType) {
    this.readType = readType;
  }

  public UserReadRecordDTO getReadRecordDTO() {
    return readRecordDTO;
  }

  public void setReadRecordDTO(UserReadRecordDTO readRecordDTO) {
    this.readRecordDTO = readRecordDTO;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }
}
