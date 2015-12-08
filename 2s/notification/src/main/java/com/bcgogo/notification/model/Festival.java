package com.bcgogo.notification.model;

import com.bcgogo.enums.common.Frequency;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.FestivalDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午8:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "festival")
public class Festival extends Reminder {
  private int preDay;
  private Long startRemindDate;
  private Long endRemindDate;
  private Frequency frequency;

  @Column(name = "pre_day")
  public int getPreDay() {
    return preDay;
  }

  public void setPreDay(int preDay) {
    this.preDay = preDay;
  }

  @Column(name = "start_remind_date")
  public Long getStartRemindDate() {
    return startRemindDate;
  }

  public void setStartRemindDate(Long startRemindDate) {
    this.startRemindDate = startRemindDate;
  }

  @Column(name = "end_remind_date")
  public Long getEndRemindDate() {
    return endRemindDate;
  }

  public void setEndRemindDate(Long endRemindDate) {
    this.endRemindDate = endRemindDate;
  }

  @Column(name = "frequency")
  @Enumerated(EnumType.STRING)
  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency){
    this.frequency = frequency;
  }

  public FestivalDTO toDTO() throws ParseException {
    FestivalDTO festivalDTO=new FestivalDTO();
    festivalDTO.setId(this.getId());
    festivalDTO.setTitle(this.getTitle());
    festivalDTO.setPreDay(this.getPreDay());
    if(this.getContent()!=null)
      festivalDTO.setContent(new String(this.getContent()));
    if(this.getReleaseDate()!=null&&this.getReleaseDate()<=DateUtil.getEndTimeOfToday()){
      festivalDTO.setStatus(AnnouncementDTO.AnnouncementStatus.ANNOUNCED.getName());
    }else {
      festivalDTO.setStatus(AnnouncementDTO.AnnouncementStatus.UNANNOUNCE.getName());
    }
    festivalDTO.setReleaseDate(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getReleaseDate()));
    festivalDTO.setReleaseMan(this.getReleaseMan());
    return festivalDTO;
  }

  public  Festival  fromDTO(FestivalDTO festivalDTO) throws Exception {
    if(festivalDTO==null){
      return null;
    }
    if(StringUtil.isNotEmpty(festivalDTO.getTitle())) this.setTitle(festivalDTO.getTitle().trim());
    Long releaseDate=DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, festivalDTO.getReleaseDate());
    //如果是当天发布的，记录发布时间并加上当前时间的时分秒的long值，解决当天发布记录的顺序问题
    if(DateUtil.isCurrentTime(releaseDate)){
      Long time=System.currentTimeMillis()-DateUtil.getStartTimeOfToday();
      this.setReleaseDate(releaseDate+time);
    }else {
      this.setReleaseDate(releaseDate);
    }
    this.setFrequency(festivalDTO.getFrequency());
    if(festivalDTO.getPreDay()==null) festivalDTO.setPreDay(0);
    this.setPreDay(festivalDTO.getPreDay());
    //根据提前时间计算开始和结束的提醒时间
    this.setStartRemindDate(DateUtil.getInnerDayTime(this.getReleaseDate(),festivalDTO.getPreDay()*(-1)));
    this.setEndRemindDate(DateUtil.getEndTimeOfDate(festivalDTO.getReleaseDate()));
    this.setReleaseManId(festivalDTO.getReleaseManId());
    this.setReleaseMan(festivalDTO.getReleaseMan());
    return this;
  }

}
