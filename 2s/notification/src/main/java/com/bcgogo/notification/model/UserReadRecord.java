package com.bcgogo.notification.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.reminder.ReminderType;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-20
 * Time: 上午2:22
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user_read_record")
public class UserReadRecord extends LongIdentifier {

  private Long shopId;
  private Long userId;
  private Long lastReadDate;
  private ReminderType reminderType;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "last_read_date")
  public Long getLastReadDate() {
    return lastReadDate;
  }

  public void setLastReadDate(Long lastReadDate) {
    this.lastReadDate = lastReadDate;
  }

   @Column(name = "reminder_type")
  @Enumerated(EnumType.STRING)
  public ReminderType getReminderType() {
    return reminderType;
  }

  public void setReminderType(ReminderType reminderType) {
    this.reminderType = reminderType;
  }
}
