package com.bcgogo.user.model;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.MemberCardServiceDTO;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午2:32
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="member_card_service")
public class MemberCardService extends LongIdentifier{
  private Long memberCardId;
  private Long serviceId;
  private String consumeType;
  private Integer times;
  private Integer term;
  private MemberStatus status;

  public MemberCardService()
  {

  }

  @Column(name="service_id")
  public Long getServiceId() {
    return serviceId;
  }
  @Column(name="member_card_id")
  public Long getMemberCardId() {
    return memberCardId;
  }
  @Column(name="consume_type")
  public String getConsumeType() {
    return consumeType;
  }
  @Column(name="times")
  public Integer getTimes() {
    return times;
  }
  @Column(name="term")
  public Integer getTerm() {
    return term;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public MemberStatus getStatus() {
    return status;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
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

  public void setTerm(Integer term) {
    this.term = term;
  }

  public void setStatus(MemberStatus status) {
    this.status = status;
  }

  public MemberCardService(MemberCardServiceDTO memberCardServiceDTO)
  {
    if(null != memberCardServiceDTO)
    {
      this.setConsumeType(memberCardServiceDTO.getConsumeType());
      this.setMemberCardId(memberCardServiceDTO.getMemberCardId());
      this.setServiceId(memberCardServiceDTO.getServiceId());
      this.setTerm(memberCardServiceDTO.getTerm());
      this.setTimes(memberCardServiceDTO.getTimes());
      this.setStatus(memberCardServiceDTO.getStatus());
      if(null != memberCardServiceDTO.getId())
      {
        this.setId(memberCardServiceDTO.getId());
      }
    }
  }

  public MemberCardServiceDTO toDTO()
  {
    MemberCardServiceDTO memberCardServiceDTO = new MemberCardServiceDTO();
    memberCardServiceDTO.setConsumeType(this.consumeType);
    memberCardServiceDTO.setId(this.getId());
    memberCardServiceDTO.setMemberCardId(this.getMemberCardId());
    memberCardServiceDTO.setServiceId(this.getServiceId());
    memberCardServiceDTO.setTerm(this.getTerm());
    memberCardServiceDTO.setTimes(this.getTimes());
    memberCardServiceDTO.setStatus(this.getStatus());
    return memberCardServiceDTO;
  }

  public static Map<Long,MemberCardService> listToMap(List<MemberCardService> memberCardServices)
  {
    Map<Long,MemberCardService> memberCardServiceMap = new HashMap<Long,MemberCardService>();
    if(CollectionUtils.isEmpty(memberCardServices))
    {
      return memberCardServiceMap;
    }
    for(MemberCardService memberCardService : memberCardServices)
    {
      if(null == memberCardService.getServiceId())
      {
        continue;
      }
      memberCardServiceMap.put(memberCardService.getServiceId(),memberCardService);
    }
    return memberCardServiceMap;
  }
}
