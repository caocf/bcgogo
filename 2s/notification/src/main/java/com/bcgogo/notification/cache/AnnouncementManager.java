package com.bcgogo.notification.cache;

import com.bcgogo.cache.ReminderCached;
import com.bcgogo.cache.UserReadRecordDTO;
import com.bcgogo.notification.model.Announcement;
import com.bcgogo.notification.model.Reminder;
import com.bcgogo.notification.reminder.ReminderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-20
 * Time: 上午3:53
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementManager extends ReminderManager {

  private static final Logger LOG = LoggerFactory.getLogger(AnnouncementManager.class);
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
   * 获取最新的公告
   * @return
   * @throws ParseException
   */
  public ReminderCached getLatestAnnouncement() throws Exception {
    return super.getReminderCachedDataByType(ReminderType.ANNOUNCEMENT);
  }


  /**
   *  数据库中查询

   * @return
   * @throws ParseException
   */
  @Override
  public List<Announcement> getCurrentReminders() throws ParseException {
    List<Announcement> announcements=new ArrayList<Announcement>();
    Announcement announcement=getNotificationService().getLastAnnouncementByToday();
    if(announcement!=null){
      announcements.add(announcement);
    }
    return announcements;
  }

  @Override
  public UserReadRecordDTO getUserReadRecord() throws ParseException {
    if(shopId==null||userId==null) return null;
    return super.getUserReadRecord(ReminderType.ANNOUNCEMENT,shopId, userId);
  }

  public void clear(){
    this.setShopId(null);
    this.setUserId(null);
  }

}
