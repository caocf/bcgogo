package com.bcgogo.tag;

import com.bcgogo.cache.ReminderCached;
import com.bcgogo.cache.ReminderData;
import com.bcgogo.cache.UserReadRecordDTO;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.notification.cache.AnnouncementManager;
import com.bcgogo.notification.cache.FestivalManager;
import com.bcgogo.notification.cache.RegisteredTrialManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 * 判断用户是否已读公告
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-31
 * Time: 上午7:33
 * To change this template use File | Settings | File Templates.
 */
public class HasNewReminder extends ConditionalTagSupport {

  private static final Logger LOG = LoggerFactory.getLogger(HasNewReminder.class);
  private  AnnouncementManager announcementManager;
  private  FestivalManager festivalManager;
  private  RegisteredTrialManager registeredTrialManager;
  private ShopDTO shopDTO;
  private UserReadRecordDTO readRecord;

  public boolean hasNewAnnouncement(HttpServletRequest request){
    try {
      announcementManager=new AnnouncementManager();
      ReminderCached cached = announcementManager.getLatestAnnouncement();
      if(cached==null||CollectionUtil.isEmpty(cached.getReminderDatas())){
        return false;
      }
      announcementManager.setShopId(WebUtil.getShopId(request));
      announcementManager.setUserId(WebUtil.getUserId(request));
      readRecord=announcementManager.getUserReadRecord();
      if(readRecord==null||readRecord.getLastReadDate()==null||cached.getLastReleaseDate()>readRecord.getLastReadDate()){
        request.setAttribute("newAnnouncementFlag",true);
        request.setAttribute("announce_lastReleaseDate",String.valueOf(cached.getLastReleaseDate()));
        return true;
      }
      return false;
    } catch (Exception e) {
      LOG.error("读取公告记录出现异常！");
      LOG.error(e.getMessage(),e);
    }finally {
      announcementManager.clear();
    }
    return false;
  }

  public boolean hasNewFestival(HttpServletRequest request){
    try {
      festivalManager=new FestivalManager();
      ReminderCached cached=festivalManager.getLatestFestival();
      if(cached==null||CollectionUtil.isEmpty(cached.getReminderDatas())){
        return false;
      }
      festivalManager.setShopId(WebUtil.getShopId(request));
      festivalManager.setUserId(WebUtil.getUserId(request));
      readRecord= festivalManager.getUserReadRecord();
      if(readRecord==null||readRecord.getLastReadDate()==null||cached.getLastReleaseDate()>readRecord.getLastReadDate()){
        StringBuffer sb=new StringBuffer();
        for(ReminderData data:cached.getReminderDatas()){
          sb.append(data.getTitle());
          sb.append("，");
        }
        request.setAttribute("festivals",sb.substring(0,sb.length()-1));
        request.setAttribute("festival_lastReleaseDate",String.valueOf(cached.getLastReleaseDate()));
        return true;
      }
      return false;
    } catch (Exception e) {
      LOG.error("读取节日记录出现异常！");
      LOG.error(e.getMessage(),e);
    }finally {
      festivalManager.clear();
    }
    return false;
  }

  //试用版 使用时间提醒
  private boolean hasRegisteredTrialRemind(HttpServletRequest request) {
    try{
      registeredTrialManager=new RegisteredTrialManager();
      registeredTrialManager.setShopId(WebUtil.getShopId(request));
      registeredTrialManager.setUserId(WebUtil.getUserId(request));
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      shopDTO = configService.getShopById(WebUtil.getShopId(request));
      if(shopDTO==null||!ShopStatus.isRegistrationTrial(shopDTO.getShopStatus())){
        return false;
      }
      if(shopDTO.getTrialStartTime()==null||shopDTO.getTrialEndTime()==null||shopDTO.getTrialEndTime()<System.currentTimeMillis()){
        return false;
      }
      readRecord=registeredTrialManager.getUserReadRecord();
      if(readRecord==null||readRecord.getLastReadDate()==null||!DateUtil.isCurrentTime(readRecord.getLastReadDate())){
        int day = (int)Math.floor ((shopDTO.getTrialEndTime() - System.currentTimeMillis()) / (1000 * 24 * 60 * 60.0));
        request.setAttribute("trialUseDays", day);
        request.setAttribute("trialUseDays_lastReleaseDate",String.valueOf(System.currentTimeMillis()));
        request.setAttribute("chargeType",shopDTO.getChargeType().toString());
        return true;
      }
      return false;
    }catch (Exception e){
      LOG.error("读取试用体验提醒出现异常！");
      LOG.error(e.getMessage(),e);
    }finally {
      registeredTrialManager.clear();
    }
    return false;
  }

  protected boolean condition() {
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    boolean flag = false;
    if (hasNewAnnouncement(request)) {
      flag = true;
    }
    if (hasNewFestival(request)) {
      flag = true;
    }
    if (hasRegisteredTrialRemind(request)) {
      flag = true;
    }
    return flag;
  }

  @Override
  public int doStartTag() throws JspException {
    if (condition()) {
      return EVAL_BODY_INCLUDE;
    } else
      return SKIP_BODY;
  }
}
