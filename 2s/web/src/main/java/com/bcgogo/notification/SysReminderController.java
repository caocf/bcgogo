package com.bcgogo.notification;

import com.bcgogo.common.Pager;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.notification.cache.AnnouncementManager;
import com.bcgogo.notification.model.Announcement;
import com.bcgogo.notification.model.UserReadRecord;
import com.bcgogo.notification.reminder.ReminderType;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.UserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-20
 * Time: 上午7:41
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/sysReminder.do")
public class SysReminderController {
  private static final Logger LOG = LoggerFactory.getLogger(SysReminderController.class);
  private static final String PAGE_SYSANNOUNCEMENT="/sysAnnouncement";

  @Autowired
  private INotificationService notificationService;

  @RequestMapping(params = "method=getAnnoucementTitleList")
  @ResponseBody
  public Object getAnnoucementTitleList(String startPageNo,String pageSize){
    if(!NumberUtil.isNumber(startPageNo)||!NumberUtil.isNumber(pageSize)){
      return null;
    }
    try{
      AnnouncementDTO announcementIndex=new AnnouncementDTO();
      announcementIndex.setEndDate(DateUtil.getEndTimeOfToday());
      Pager pager= new Pager(notificationService.getAnnoucementCount(announcementIndex), NumberUtil.intValue(startPageNo, 1),Integer.valueOf(pageSize));
      pager.setPageSize(Integer.valueOf(pageSize));
      announcementIndex.setPager(pager);
      List<AnnouncementDTO> announcementDTOs= notificationService.getAnnouncementDTOs(announcementIndex);
      Map<String,List<String>> titleList=new LinkedHashMap<String,List<String>>();
      List<String> announcementTitle=null;
      if(CollectionUtils.isNotEmpty(announcementDTOs)){
        for(AnnouncementDTO announcementDTO:announcementDTOs){
          announcementTitle=new ArrayList<String>();
          announcementTitle.add(announcementDTO.getTitle());
          announcementTitle.add(announcementDTO.getReleaseDate());
          titleList.put(String.valueOf(announcementDTO.getId()), announcementTitle);
        }
      }
      List<Object> result = new ArrayList<Object>();
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("titleList",titleList);
      result.add(data);
      result.add(announcementIndex.getPager());
      return result;
    }catch (Exception e){
      LOG.error("获取系统公告异常！");
      LOG.error(e.getMessage(),e);
      return null;
    }
  }


  @RequestMapping(params = "method=toSysAnnouncement")
  public String toSysAnnouncement(HttpServletRequest request,ModelMap modelMap){
    try{

      Announcement announcement=notificationService.getLastAnnouncementByToday();
      if(announcement==null){
        return PAGE_SYSANNOUNCEMENT;
      }
      modelMap.addAttribute("nAnnouncement",announcement.toDTO());
      UserReadRecord readRecord=new UserReadRecord();
      readRecord.setReminderType(ReminderType.ANNOUNCEMENT);
      readRecord.setLastReadDate(announcement.getReleaseDate());
      updateUserReadRecord(request,readRecord);
      return PAGE_SYSANNOUNCEMENT;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  /**
   *  用户关闭或打开提醒，保存当前用户已读记录
   * @param request
   * @param readRecord
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=updateUserReadRecord")
  public String updateUserReadRecord(HttpServletRequest request,UserReadRecord readRecord){
    try{
      readRecord.setShopId(WebUtil.getShopId(request));
      readRecord.setUserId(WebUtil.getUserId(request));
      notificationService.updateUserReadRecord(readRecord);
      AnnouncementManager.setReadRecord(readRecord);
      return "succ";
    }catch (Exception e){
      LOG.error("更新用户读取记录异常！");
      LOG.error(e.getMessage(),e);
      return "error";
    }
  }

  @RequestMapping(params = "method=getAnnouncementById")
  @ResponseBody
  public Object getAnnouncementById(HttpServletRequest request,String announcementId){
    if(StringUtil.isEmpty(announcementId)){
      return null;
    }
    try{
      Announcement announcement= notificationService.getAnnouncementById(NumberUtil.longValue(announcementId));
      if(announcement==null|| ObjectStatus.DISABLED.equals(announcement.getStatus())){
        return null;
      }
      AnnouncementDTO announcementDTO=announcement.toDTO();
      UserDTO userDTO=ServiceManager.getService(UserService.class).getUserByUserId(announcementDTO.getReleaseManId());
      if(userDTO!=null){
        announcementDTO.setReleaseMan(userDTO.getUserName());
      }
      return announcementDTO;
    }catch (Exception e){
      LOG.error("查询公告出现异常！");
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

}
