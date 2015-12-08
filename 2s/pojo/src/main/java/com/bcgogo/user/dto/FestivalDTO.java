package com.bcgogo.user.dto;

import com.bcgogo.enums.common.Frequency;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-20
 * Time: 上午2:48
 * To change this template use File | Settings | File Templates.
 */
public class FestivalDTO extends ReminderDTO{

  private Integer preDay;
  private Long startRemindDate;
  private Long endRemindDate;
  private Frequency frequency;

  public Integer getPreDay() {
    return preDay;
  }

  public void setPreDay(Integer preDay) {
    this.preDay = preDay;
  }

  public Long getStartRemindDate() {
    return startRemindDate;
  }

  public void setStartRemindDate(Long startRemindDate) {
    this.startRemindDate = startRemindDate;
  }

  public Long getEndRemindDate() {
    return endRemindDate;
  }

  public void setEndRemindDate(Long endRemindDate) {
    this.endRemindDate = endRemindDate;
  }

  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }
}
