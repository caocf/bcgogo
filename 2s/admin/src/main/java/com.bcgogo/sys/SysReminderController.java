package com.bcgogo.sys;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.notification.model.Announcement;
import com.bcgogo.notification.model.Festival;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.client.IClientSystemService;
import com.bcgogo.txn.service.pushMessage.ISystemPushMessageService;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.FestivalDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.UserService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-24
 * Time: 上午7:39
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/sysReminder.do")
public class SysReminderController {
  private static final Logger LOG = LoggerFactory.getLogger(SysReminderController.class);
  @Autowired
  INotificationService notificationService;
  @Autowired
  IClientSystemService clientSystemService;
  @Autowired
  ISystemPushMessageService systemPushMessageService;
  @Autowired
  IPushMessageService pushMessageService;

  /**
   * 由于公告内容数据量较大，此方法并未加载content字段
   *
   * @param request
   * @param start
   * @param limit
   * @return
   */
  @RequestMapping(params = "method=getAnnouncements")
  @ResponseBody
  public Object getAnnouncements(HttpServletRequest request, int start, int limit) {
    try {
      AllListResult searchResult = new AllListResult();
      AnnouncementDTO announcementIndex = new AnnouncementDTO();
      Pager pager = new Pager();
      pager.setRowStart(start);
      pager.setPageSize(limit);
      announcementIndex.setPager(pager);
      searchResult.setResults(notificationService.getAnnouncementDTOs(announcementIndex));
      searchResult.setTotalRows(notificationService.getAnnoucementCount(announcementIndex));
      searchResult.setSuccess(true);
      return searchResult;
    } catch (Exception e) {
      LOG.error("查询公告列表异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getFestivals")
  @ResponseBody
  public Object getFestivals(HttpServletRequest request, int start, int limit) {
    try {
      AllListResult searchResult = new AllListResult();
      FestivalDTO festivalDTO = new FestivalDTO();
      festivalDTO.setStart(start);
      festivalDTO.setLimit(limit);
      searchResult.setResults(notificationService.getFestivalDTOs(festivalDTO));
      searchResult.setTotalRows(notificationService.getFestivalCount(festivalDTO));
      searchResult.setSuccess(true);
      return searchResult;
    } catch (Exception e) {
      LOG.error("查询公告列表异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveOrUpdateAnnouncement")
  @ResponseBody
  public Object saveOrUpdateAnnouncement(HttpServletRequest request, AnnouncementDTO announcementDTO) {
    try {
      announcementDTO.setReleaseManId(WebUtil.getUserId(request));
      announcementDTO.setReleaseMan(WebUtil.getUserName(request));
      announcementDTO.setShopId(-1L);
      Long announcementId= notificationService.saveOrUpdateAnnouncement(announcementDTO);
      if (announcementId != null) {
        systemPushMessageService.createOrUpdateAnnouncementPushMessage(announcementId, announcementDTO.getContent(), PushMessageLevel.HIGH);
      }
      return announcementId;
    } catch (Exception e) {
      LOG.error("保存公告异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveOrUpdateFestival")
  @ResponseBody
  public Object saveOrUpdateFestival(HttpServletRequest request, FestivalDTO festivalDTO) {
    try {
      festivalDTO.setReleaseManId(WebUtil.getUserId(request));
      festivalDTO.setReleaseMan(WebUtil.getUserName(request));
      festivalDTO.setShopId(-1L);
      Long festivalId = notificationService.saveOrUpdateFestival(festivalDTO);
      if (festivalId != null) {
        systemPushMessageService.createOrUpdateFestivalPushMessage(festivalId, PushMessageLevel.HIGH, festivalDTO.getTitle(),
            System.currentTimeMillis(), // DateUtil.getInnerDayTime(festivalDTO.getReleaseDate(), festivalDTO.getPreDay() * (-1))
            DateUtil.getEndTimeOfDate(festivalDTO.getReleaseDate()));
      }
      return festivalId;
    } catch (Exception e) {
      LOG.error("保存节日异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=deleteAnnouncement")
  @ResponseBody
  public Result deleteAnnouncement(HttpServletRequest request, String announcementId) {
    Result result = new Result();
    result.setSuccess(true);
    if (StringUtil.isEmpty(announcementId)) {
      result.setSuccess(false);
      result.setMsg("公告Id异常!");
      return result;
    }
    try {
      result = notificationService.deleteAnnouncement(result, NumberUtil.longValue(announcementId));
      if(result.isSuccess()) {
        pushMessageService.disabledPushMessageReceiverBySourceId(ShopConstant.BC_SHOP_ID,Long.valueOf(announcementId),null, PushMessageSourceType.ANNOUNCEMENT);
      }
      return result;
    } catch (Exception e) {
      LOG.error("删除公告异常！");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("删除公告异常！");
      return result;
    }
  }

  @RequestMapping(params = "method=deleteFestival")
  @ResponseBody
  public Result deleteFestival(HttpServletRequest request, String festivalId) {
    Result result = new Result();
    result.setSuccess(true);
    if (StringUtil.isEmpty(festivalId)) {
      result.setSuccess(false);
      result.setMsg("公告Id异常!");
      return result;
    }
    try {
      result = notificationService.deleteFestival(result, NumberUtil.longValue(festivalId));
      if(result.isSuccess()) {
        pushMessageService.disabledPushMessageReceiverBySourceId(ShopConstant.BC_SHOP_ID,Long.valueOf(festivalId),null,PushMessageSourceType.FESTIVAL);
      }
      return result;
    } catch (Exception e) {
      LOG.error("删除公告异常！");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("删除公告异常！");
      return result;
    }
  }

  @RequestMapping(params = "method=getAnnouncementById")
  @ResponseBody
  public Object getAnnouncementById(HttpServletRequest request, String announcementId) {
    if (StringUtil.isEmpty(announcementId)) {
      return null;
    }
    try {
      Announcement announcement = notificationService.getAnnouncementById(NumberUtil.longValue(announcementId));
      if (announcement == null || ObjectStatus.DISABLED.equals(announcement.getStatus())) {
        return null;
      }
      AnnouncementDTO announcementDTO = announcement.toDTO();
      UserDTO userDTO = ServiceManager.getService(UserService.class).getUserByUserId(announcementDTO.getReleaseManId());
      if (userDTO != null) {
        announcementDTO.setReleaseMan(userDTO.getUserName());
      }
      return announcementDTO;
    } catch (Exception e) {
      LOG.error("查询公告出现异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getFestivalById")
  @ResponseBody
  public Object getFestivalById(HttpServletRequest request, String festivalId) {
    if (StringUtil.isEmpty(festivalId)) {
      return null;
    }
    try {
      Festival festival = notificationService.getFestivalById(NumberUtil.longValue(festivalId));
      if (festival == null || ObjectStatus.DISABLED.equals(festival.getStatus())) {
        return null;
      }
      FestivalDTO festivalDTO = festival.toDTO();
      UserDTO userDTO = ServiceManager.getService(UserService.class).getUserByUserId(festivalDTO.getReleaseManId());
      if (userDTO != null) {
        festivalDTO.setReleaseMan(userDTO.getUserName());
      }
      return festivalDTO;
    } catch (Exception e) {
      LOG.error("查询公告出现异常！");
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
