package com.bcgogo.user.dto;

import com.bcgogo.api.ApiMemberServiceDTO;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class MemberServiceDTO {
  private Long id;
  private Long memberId;
  private Long serviceId;
  private String serviceIdStr;
  private String consumeType;
  private Integer times;
  private String timesStr;
  private Long deadline;
  private String deadlineStr;
  private String serviceName;
  private String vehicles;
  private MemberStatus status;
  private String memberNo;
  private ServiceDTO serviceDTO;
  private boolean expired;
  private String remindStatus;

  public String getTimesStr() {
    return timesStr;
  }

  public void setTimesStr(String timesStr) {
    this.timesStr = timesStr;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getDeadlineStr() {
    return deadlineStr;
  }

  public void setDeadlineStr(String deadlineStr) {
    this.deadlineStr = deadlineStr;
  }

  public Long getId() {
    return id;
  }

  public Long getMemberId() {
    return memberId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public String getServiceIdStr() {
    return serviceIdStr;
  }

  public void setServiceIdStr(String serviceIdStr) {
    this.serviceIdStr = serviceIdStr;
  }

  public String getVehicles() {
    return vehicles;
  }

  public String getConsumeType() {
    return consumeType;
  }

  public Integer getTimes() {
    return times;
  }

  public Long getDeadline() {
    return deadline;
  }

  public MemberStatus getStatus() {
    return status;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
    this.serviceIdStr= StringUtil.valueOf(serviceId);
  }

  public void setConsumeType(String consumeType) {
    this.consumeType = consumeType;
  }

  public void setTimes(Integer times) {
    this.times = times;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }

  public void setStatus(MemberStatus status) {
    this.status = status;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public ServiceDTO getServiceDTO() {
    return serviceDTO;
  }

  public void setServiceDTO(ServiceDTO serviceDTO) {
    this.serviceDTO = serviceDTO;
  }

  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }

  public static Map<Long, MemberServiceDTO> listToMap(List<MemberServiceDTO> memberServiceDTOs) {
    Map<Long, MemberServiceDTO> map = new HashMap<Long, MemberServiceDTO>();
    if (CollectionUtils.isNotEmpty(memberServiceDTOs)) {
      for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
        if (null != memberServiceDTO.getServiceId()) {
          map.put(memberServiceDTO.getServiceId(), memberServiceDTO);
        }
      }
    }

    return map;
  }

  public ApiMemberServiceDTO toApiMemberServiceDTO() {
    ApiMemberServiceDTO dto = new ApiMemberServiceDTO();
    dto.setServiceId(getServiceId());
    dto.setConsumeType(getConsumeType());
    dto.setTimes(getTimes());
    dto.setDeadline(getDeadline());
    dto.setServiceName(getServiceName());
    dto.setVehicles(getVehicles());
    dto.setStatus(getStatus() != null ? getStatus().getStatus() : null);
    dto.setExpired(System.currentTimeMillis() > getDeadline());
    return dto;
  }
}
