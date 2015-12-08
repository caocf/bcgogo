package com.bcgogo.notification.service;


import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.enums.notification.StatStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.*;
import com.bcgogo.notification.model.*;
import com.bcgogo.notification.reminder.ReminderType;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.txn.dto.PromotionMsgJobDTO;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.FestivalDTO;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

public interface INotificationService {

  void batchSaveSmsJob(List<SmsJob> smsJobs);

  public int countShopSmsJobs(long shopId);

  void deleteSmsJobById(Long jobId);

  Long saveOrUpdateSms(SmsDTO smsDTO);

 Long saveOrUpdateSmsIndex(SmsIndexDTO smsIndexDTO);

  SmsIndexDTO getSmsIndexDTOBySmsId(Long shopId,Long smsId);

  SmsDTO getSmsDTOById(Long shopId,Long smsId);

  List<SmsDTO> querySms(SmsSearchCondition condition);

  List<SmsDTO> getSmsDTOByIds(Long shopId,Long... smsIds);

  public void updateSmsJob(List<SmsJob> smsJobs);

  void deleteSms(Long shopId,Long... smsIds);
  /**
   * 保存定时钟
   *
   * @param smsJobDTO mobile 11位 多个号码用","分开
   */
  public void sendSmsAsync(SmsJobDTO smsJobDTO) throws SmsException;

  /**
   * 批量发送
   *
   * @param smsJobDTOs List<SmsJobDTO>
   * @throws SmsException
   */
  public void batchSendSmsAsync(List<SmsJobDTO> smsJobDTOs) throws SmsException;

  /**
   * 实时发送
   *
   * @param smsJobDTO mobile 11位 多个号码用","分开
   */
  public void sendSmsSync(SmsJobDTO smsJobDTO) throws SmsException;

  public List<InBoxDTO> getShopInBoxs(long shopId, int pageNo, int pageSize);

  public int countShopInBox(long shopId);

  List<OutBox> getOutBox(int pageSize, long startId);

  //OutBox
  public List<OutBoxDTO> getOutBoxByShopAndMobile(long shopId, String mobile, int pageNo, int pageSize);

  public List<OutBoxDTO> getShopOutBoxs(long shopId, int pageNo, int pageSize);

  List<SmsDTO> getSmsDTOList(SmsSearchCondition condition);

  int countSms(SmsSearchCondition condition);

  public int countShopOutBox(long shopId);

  public int countOutBoxNumberByShopIdAndMobile(long shopId, String mobile);

  public List<SmsJob> getSmsJobsByStartTime(long startTime);

  public List<SmsJobDTO> getSmsJobsByShopId(long shopId, int pageNo, int pageSize);

  public MessageTemplate getMsgTemplateByType(String type);

  MessageTemplateDTO getShopMsgTemplateDTOById(Long shopId,Long templateId);

  List<MessageTemplateDTO> getShopMsgTemplateDTOByName(Long shopId,String name);

  List<MessageTemplateDTO> getShopMsgTemplate(Long shopId,String keyWord,Pager pager);

  int countShopMsgTemplate(Long shopId,String keyWord);

  Long saveShopMsgTemplate(MessageTemplateDTO templateDTO) throws BcgogoException;

  void deleteSmsTemplate(Long[] templateIds) throws BcgogoException;

  public void setMessageTemplate(String type, String value, Long shopId);

  public List<MessageTemplateDTO> getDistinctSceneMessageTemplateDTOAndSwitchStatus(Long shopId);

  public MessageSwitchDTO getMessageSwitchDTOByShopIdAndScene(Long shopId, MessageScene scene);

  public MessageSwitchDTO SaveOrUpdateMessageSwitch(Long shopId, MessageScene scene, MessageSwitchStatus status);

  List<AnnouncementDTO> getAnnouncementDTOs(AnnouncementDTO announcementIndex) throws ParseException;

  List<FestivalDTO> getFestivalDTOs(FestivalDTO festivalIndex) throws ParseException;

  Integer getFestivalCount(FestivalDTO festivalDTO);

  Integer getAnnoucementCount(AnnouncementDTO announcementIndex);

  Long saveOrUpdateAnnouncement(AnnouncementDTO announcementDTO) throws Exception;

  Long saveOrUpdateFestival(FestivalDTO festivalDTO) throws Exception;

  Announcement getAnnouncementById(Long announcementId);

  Festival getFestivalById(Long festivalId);

  Announcement getLastAnnouncementByToday() throws ParseException;

  Result deleteAnnouncement(Result result, Long announcementId) throws ParseException;

  Result deleteFestival(Result result, Long festivalId) throws ParseException;

  void updateUserReadRecord(UserReadRecord readRecord);

  List<Festival> getCurrentFestivals() throws Exception;

  UserReadRecord getUserReadRecord(ReminderType type, Long shopId, Long userId);

  List<PromotionMsgJobDTO> getCurrentPromotionMsgJobDTO();

  void updateFinishedPromotionMsgJob(List<Long> jobIdList);

  void savePromotionMsgJob(Result result,PromotionMsgJob job);

  List<SmsJob> getSmsJobsBySmsId(Long smsId, Long id,int sendTimes, int limit);

  List<SmsJob> getSmsJobsBySmsId(Long shopId,Long... smsIds);

  List<OutBox> getOutBoxByPager(int pageSize, int startPageNo);

  List<OutBox> getOutBoxByStatStatus(int pageSize, StatStatus statStatus);

  void updateOutBoxStatStatus(Set<Long> outBoxIds, StatStatus statStatus);
}
