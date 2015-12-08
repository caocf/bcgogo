package com.bcgogo.notification.cache;

import com.bcgogo.cache.ReminderCached;
import com.bcgogo.cache.ReminderData;
import com.bcgogo.cache.UserReadRecordDTO;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.Reminder;
import com.bcgogo.notification.model.UserReadRecord;
import com.bcgogo.notification.reminder.ReminderType;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午3:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class ReminderManager{

  private static final Logger LOG = LoggerFactory.getLogger(ReminderManager.class);
  public static Long SYNC_INTERVAL = 60000L;
  private static Map<String,ReminderCached> reminderMap = new LinkedHashMap<String,ReminderCached>();

  //同一时间段所有用户不会同时登陆，可以缓存部分用户信息
//  protected static  UserDataLinkedStack<UserData> userStack = new UserDataLinkedStack<UserData>();

  private INotificationService notificationService=null;
  private static NotificationWriter writer=null;

  public static Map<String, ReminderCached> getReminderMap() {
    return reminderMap;
  }

  public static void setReminderMap(Map<String, ReminderCached> reminderMap) {
    ReminderManager.reminderMap = reminderMap;
  }

  public INotificationService getNotificationService() {
    if(notificationService==null){
      notificationService= ServiceManager.getService(INotificationService.class);
    }
    return notificationService;
  }

  protected static NotificationWriter getWriter(){
    if(writer==null){
      writer= ServiceManager.getService(NotificationDaoManager.class).getWriter();
      return writer;
    }
    return writer;
  }

  /**
   *
   * @param type
   * @throws ParseException
   */
  public static void setSynTime(ReminderType type) throws ParseException {
    MemCacheAdapter.set(assembleReminderSyncKey(type), System.currentTimeMillis());
  }

  public static Long getSynTime(ReminderType type){
    if(type==null) return null;
    return NumberUtil.longValue(String.valueOf(MemCacheAdapter.get(assembleReminderSyncKey(type))), 0L);
  }

  /**
   * 将用户的已读记录存入memcached,失效日期为当天
   * @param readRecord
   * @throws ParseException
   */
  public static void setReadRecord(UserReadRecord readRecord) throws ParseException {
    if(readRecord==null) return;
    String key =assembleUserKey(readRecord.getReminderType(),readRecord.getShopId(),readRecord.getUserId());
    UserReadRecordDTO readRecordDTO=new UserReadRecordDTO();
    readRecordDTO.setLastReadDate(readRecord.getLastReadDate());
    MemCacheAdapter.set(key, readRecordDTO,new Date(DateUtil.getEndTimeOfToday()));
  }


  /**
   * 缓存数据库中数据，每隔一分钟检查memcach是否提醒已更新
   * @return
   * @throws ParseException
   */
  public  ReminderCached getReminderCachedDataByType(ReminderType type) throws Exception {
    ReminderCached cachedData=reminderMap.get(type.toString());
    if(cachedData==null||!DateUtil.isCurrentTime(cachedData.getSyncTime())){
      boolean flag=refreshReminderCached(type);
      if(!flag) return null;
      return reminderMap.get(type.toString());
    }
    if (System.currentTimeMillis() - cachedData.getLastIntervalTime()>= SYNC_INTERVAL){
      if (getSynTime(type)> cachedData.getSyncTime()) {
        refreshReminderCached(type);
        cachedData = reminderMap.get(type.toString());
      }
      //保证一分钟之内不再读memcache
      cachedData.setLastIntervalTime(System.currentTimeMillis());
    }
    return cachedData;
  }



  public Boolean  refreshReminderCached(ReminderType type) throws Exception {
    List<? extends Reminder> reminders= getCurrentReminders();
    ReminderCached reminderCached=new ReminderCached();
    if(CollectionUtils.isNotEmpty(reminders)){
      ReminderData reminderData=null;
      for(Reminder reminder:reminders){
        if(reminder==null||reminder.getReleaseDate()==null){
          continue;
        }
        //发布时间取当天提醒最大的发布时间
        if(reminderCached.getLastReleaseDate()==null){
          reminderCached.setLastReleaseDate(reminder.getReleaseDate());
        }else if(reminder.getReleaseDate()>reminderCached.getLastReleaseDate()){
          reminderCached.setLastReleaseDate(reminder.getReleaseDate());
        }
        reminderData=new ReminderData();
        reminderData.setTitle(reminder.getTitle());
        reminderCached.getReminderDatas().add(reminderData);
      }
    }
    reminderCached.setReminderType(type);
    reminderCached.setSyncTime(System.currentTimeMillis());
    reminderCached.setLastIntervalTime(System.currentTimeMillis());
    reminderMap.put(type.toString(),reminderCached);
    return true;
  }

  /**
   * 按照MemCache-->数据库的顺序取数据,并缓存取到的数据
   * @param shopId
   * @param userId
   * @return
   */
  public  UserReadRecordDTO getUserReadRecord(ReminderType type,Long shopId,Long userId) throws ParseException {
    UserReadRecordDTO readRecordDTO=null;
    if(shopId==null||userId==null){
      return readRecordDTO;
    }
    String key = assembleUserKey(type, shopId, userId);
    readRecordDTO = (UserReadRecordDTO) MemCacheAdapter.get(key);
    if(readRecordDTO==null){
      UserReadRecord readRecord=getNotificationService().getUserReadRecord(type,shopId,userId);
      if(readRecord==null) return null;
      setReadRecord(readRecord);
      readRecordDTO=new UserReadRecordDTO();
      readRecordDTO.setLastReadDate(readRecord.getLastReadDate());
    }
    return readRecordDTO;
  }

  /**
   * 获取要提醒的项目
   * @return
   * @throws ParseException
   */
  public  abstract List<? extends Reminder> getCurrentReminders() throws Exception;

  /**
   * 获取用户读取提醒的记录
   * @return
   */
  public abstract UserReadRecordDTO getUserReadRecord() throws ParseException;

  public static String assembleReminderSyncKey(ReminderType type){
    return MemcachePrefix.sysReminder.getValue()+type.toString().toLowerCase()+"_last_sync_date";
  }

  public static String assembleUserKey(ReminderType type, Long shopId,Long userId) {
    if(shopId==null||userId==null) return null;
    return MemcachePrefix.sysReminder.getValue()+"user_data_"+type.toString().toLowerCase()+"_"+String.valueOf(shopId)+"_"+String.valueOf(userId);
  }

}
