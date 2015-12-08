package com.bcgogo.user.model;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="member_service")
public class MemberService extends LongIdentifier {
  private Long memberId;
  private Long serviceId;
  private String consumeType;
  private Integer times;
  private Long deadline;
  private String vehicles;
  private MemberStatus status;
  private String remindStatus;

  @Column(name="member_id")
  public Long getMemberId() {
    return memberId;
  }
  @Column(name="service_id")
  public Long getServiceId() {
    return serviceId;
  }
  @Column(name="consume_type")
  public String getConsumeType() {
    return consumeType;
  }
  @Column(name="times")
  public Integer getTimes() {
    return times;
  }
  @Column(name="deadline")
  public Long getDeadline() {
    return deadline;
  }
  @Column(name="vehicles")
  public String getVehicles() {
    return vehicles;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public MemberStatus getStatus() {
    return status;
  }

  @Column(name="remind_status")
  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
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

  public MemberServiceDTO toDTO(){
    MemberServiceDTO memberServiceDTO = new MemberServiceDTO();
    memberServiceDTO.setDeadline(this.getDeadline());
    if(this.getDeadline() == -1){
      memberServiceDTO.setDeadlineStr("无限期");
    }else{
      String deadlineString = DateUtil.dateLongToStr(this.getDeadline(), DateUtil.DATE_STRING_FORMAT_DAY);
      memberServiceDTO.setDeadlineStr(deadlineString);
      if((System.currentTimeMillis()-this .getDeadline())>0)
      {
         memberServiceDTO.setExpired(true);
    }
    }
    memberServiceDTO.setConsumeType(this.getConsumeType());
    memberServiceDTO.setId(this.getId());
    memberServiceDTO.setMemberId(this.getMemberId());
    memberServiceDTO.setServiceId(this.getServiceId());
    if(this.getTimes() == -1){
      memberServiceDTO.setTimesStr("不限次");
    }else {
      memberServiceDTO.setTimesStr(String.valueOf(this.getTimes()));
    }
    memberServiceDTO.setTimes(this.getTimes());
    memberServiceDTO.setVehicles(this.getVehicles());
    memberServiceDTO.setStatus(this.getStatus());
    memberServiceDTO.setRemindStatus(this.getRemindStatus());
    return memberServiceDTO;
  }

  public void fromDTO(MemberServiceDTO memberServiceDTO){
    if(memberServiceDTO != null){
      this.setConsumeType(memberServiceDTO.getConsumeType());
      this.setDeadline(memberServiceDTO.getDeadline());
      this.setMemberId(memberServiceDTO.getMemberId());
      this.setServiceId(memberServiceDTO.getServiceId());
      this.setTimes(memberServiceDTO.getTimes());
      this.setVehicles(memberServiceDTO.getVehicles());
      this.setStatus(memberServiceDTO.getStatus());
      this.setRemindStatus(memberServiceDTO.getRemindStatus());
      if(memberServiceDTO.getId() != null){
        this.setId(memberServiceDTO.getId());
      }
    }
  }

  public MemberService(){

  }

  public MemberService(MemberServiceDTO memberServiceDTO){
    if(memberServiceDTO != null){
      this.setConsumeType(memberServiceDTO.getConsumeType());
      this.setDeadline(memberServiceDTO.getDeadline());
      this.setMemberId(memberServiceDTO.getMemberId());
      this.setServiceId(memberServiceDTO.getServiceId());
      this.setTimes(memberServiceDTO.getTimes());
      this.setVehicles(memberServiceDTO.getVehicles());
      this.setStatus(memberServiceDTO.getStatus());
      this.setRemindStatus(memberServiceDTO.getRemindStatus());
      if(memberServiceDTO.getId() != null){
        this.setId(memberServiceDTO.getId());
      }
    }
  }

  public static Map<Long,MemberService> listToMap(List<MemberService> memberServices)
  {
    Map<Long,MemberService> memberServiceMap = new HashMap<Long, MemberService>();
    if(CollectionUtils.isNotEmpty(memberServices))
    {
      for(MemberService memberService : memberServices)
      {
        if(null != memberService.getServiceId())
        {
          memberServiceMap.put(memberService.getServiceId(),memberService);
        }
      }
    }
    return memberServiceMap;
  }
}
