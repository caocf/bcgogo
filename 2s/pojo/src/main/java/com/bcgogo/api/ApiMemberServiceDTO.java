package com.bcgogo.api;

import com.bcgogo.utils.DateUtil;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午5:30
 */
public class ApiMemberServiceDTO {
  private Long serviceId;//服务ID（后台数据主键）
  private String consumeType;//消费类型
  private Integer times;//剩余次数
  private String timesStr;//剩余次数
  private Long deadline;//有效期
  private String deadlineStr;//有效期
  private String serviceName;//服务名称
  private String vehicles;//限定服务车辆
  private String status;//状态
  private boolean expired;//是否过期

  public ApiMemberServiceDTO() {
  }

  public ApiMemberServiceDTO(Long serviceId, String consumeType, Integer times, Long deadline, String serviceName, String vehicles, String status, boolean expired) {
    this.serviceId = serviceId;
    this.consumeType = consumeType;
    this.setTimes(times);
    this.setDeadline(deadline);
    this.serviceName = serviceName;
    this.vehicles = vehicles;
    this.status = status;
    this.expired = expired;
  }

  public String getTimesStr() {
    return timesStr;
  }

  public void setTimesStr(String timesStr) {
    this.timesStr = timesStr;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(String consumeType) {
    this.consumeType = consumeType;
  }

  public Integer getTimes() {
    return times;
  }

  public void setTimes(Integer times) {
    this.times = times;
    if (times == -1) {
      setTimesStr("不限次 ");
    } else {
      setTimesStr(String.valueOf(times));
    }
  }

  public Long getDeadline() {
    return deadline;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
    if (deadline == -1) {
      setDeadlineStr("不限期");
    } else {
      setDeadlineStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, deadline));
    }
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getVehicles() {
    return vehicles;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  public String getDeadlineStr() {
    return deadlineStr;
  }

  public void setDeadlineStr(String deadlineStr) {
    this.deadlineStr = deadlineStr;
  }
}
