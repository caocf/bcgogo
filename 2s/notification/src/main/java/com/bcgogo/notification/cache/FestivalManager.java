package com.bcgogo.notification.cache;


import com.bcgogo.cache.ReminderCached;
import com.bcgogo.cache.UserReadRecordDTO;
import com.bcgogo.notification.model.Festival;
import com.bcgogo.notification.model.Reminder;
import com.bcgogo.notification.reminder.ReminderType;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午3:34
 * To change this template use File | Settings | File Templates.
 */
public class FestivalManager extends ReminderManager {

  private Long shopId;
  private Long userId;

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


  /**
   * 获取最新的节日
   * @return
   * @throws ParseException
   */
  public ReminderCached getLatestFestival() throws Exception {
    return super.getReminderCachedDataByType(ReminderType.FESTIVAL);
  }

  @Override
  public UserReadRecordDTO getUserReadRecord() throws ParseException {
    if(shopId==null||userId==null) return null;
    return super.getUserReadRecord(ReminderType.FESTIVAL, shopId, userId);
  }

  @Override
  public List<Festival> getCurrentReminders() throws Exception {
    return getNotificationService().getCurrentFestivals();

  }

  public void clear(){
    this.setShopId(null);
    this.setUserId(null);
  }

}
