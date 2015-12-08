package com.bcgogo.notification.model;

import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-19
 * Time: 上午8:04
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "announcement")
public class Announcement extends Reminder {

   public AnnouncementDTO toDTO() throws ParseException {
    AnnouncementDTO announcementDTO=new AnnouncementDTO();
    announcementDTO.setId(this.getId());
    announcementDTO.setTitle(this.getTitle());
    if(this.getContent()!=null){
      announcementDTO.setContent(new String(this.getContent()));

    }
    if(this.getReleaseDate()!=null){
      if(this.getReleaseDate()<=DateUtil.getStartTimeOfToday()){
        announcementDTO.setStatus(AnnouncementDTO.AnnouncementStatus.ANNOUNCED.getName());
      }else if(this.getReleaseDate()>DateUtil.getEndTimeOfToday()){
        announcementDTO.setStatus(AnnouncementDTO.AnnouncementStatus.UNANNOUNCE.getName());
      }else{
        announcementDTO.setStatus(AnnouncementDTO.AnnouncementStatus.ANNOUNCEMENTTING.getName());
      }
    }

    announcementDTO.setReleaseDate(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, this.getReleaseDate()));
    announcementDTO.setReleaseMan(this.getReleaseMan());
    return announcementDTO;
  }

  public  Announcement fromDTO(AnnouncementDTO announcementDTO) throws Exception {
    if(announcementDTO==null){
      return null;
    }
    this.setTitle(announcementDTO.getTitle());
    if(StringUtil.isNotEmpty(announcementDTO.getContent())){
      this.setContent(announcementDTO.getContent().trim().getBytes());
    }
    Long releaseDate=DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, announcementDTO.getReleaseDate());
    //如果是当天发布的，记录发布时间并加上当前时间的时分秒的long值，解决当天发布记录的顺序问题
    if(DateUtil.isCurrentTime(releaseDate)){
      Long time=System.currentTimeMillis()-DateUtil.getStartTimeOfToday();
      this.setReleaseDate(releaseDate+time);
    }else {
      this.setReleaseDate(releaseDate);
    }
    this.setReleaseManId(announcementDTO.getReleaseManId());
    this.setReleaseMan(announcementDTO.getReleaseMan());
    return this;
  }

}
