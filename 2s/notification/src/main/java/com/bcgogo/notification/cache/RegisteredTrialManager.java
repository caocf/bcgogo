package com.bcgogo.notification.cache;

import com.bcgogo.cache.UserReadRecordDTO;
import com.bcgogo.notification.model.Announcement;
import com.bcgogo.notification.reminder.ReminderType;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-2-21
 * Time: 上午2:05
 * To change this template use File | Settings | File Templates.
 */
public class RegisteredTrialManager extends ReminderManager{
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

  @Override
  public UserReadRecordDTO getUserReadRecord() throws ParseException {
    if(shopId==null||userId==null) return null;
    return super.getUserReadRecord(ReminderType.TRIAL_USE_DAYS,shopId, userId);
  }

  /**
   *  shop现在没放缓存，方法当前返回null
   * @return
   * @throws Exception
   */
  @Override
  public List<Announcement> getCurrentReminders() throws Exception {
    return null;
  }

  public void clear(){
    this.setShopId(null);
    this.setUserId(null);
  }

}
