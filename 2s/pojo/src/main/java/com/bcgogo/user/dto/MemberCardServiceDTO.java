package com.bcgogo.user.dto;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardServiceDTO implements Serializable{
  private Long id;
  private Long memberCardId;
  private Long serviceId;
  private String consumeType;
  private Integer times;
  private Integer term;
  private String serviceName;
  private MemberStatus status;
  private String timesStr;
  private String termStr;

  public Long getId() {
    return id;
  }

  public Long getMemberCardId() {
    return memberCardId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public String getConsumeType() {
    return consumeType;
  }

  public Integer getTimes() {
    return times;
  }

  public Integer getTerm() {
    return term;
  }

  public String getServiceName() {
    return serviceName;
  }

  public MemberStatus getStatus() {
    return status;
  }

  public void setId(Long id) {
    this.id = id;
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
    if(NumberUtil.isEqualNegativeOne(times))
    {
      this.timesStr = "不限次";
  }
    else if(null != times)
    {
      this.timesStr = String.valueOf(times.intValue());
    }
  }

  public void setTerm(Integer term) {
    this.term = term;

    if(NumberUtil.isEqualNegativeOne(term))
    {
      this.termStr = "无期限";
  }
    else if(null != term)
    {
      this.termStr = String.valueOf(term.intValue());
    }
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public void setStatus(MemberStatus status) {
    this.status = status;
  }

  public String getTimesStr() {
    return timesStr;
  }

  public void setTimesStr(String timesStr) {
    this.timesStr = timesStr;
    if("不限次".equals(timesStr))
    {
      this.times = -1;
    }
    else if(StringUtils.isNotBlank(timesStr))
    {
      this.times = Integer.valueOf(timesStr);
    }
  }

  public String getTermStr() {
    return termStr;
  }

  public void setTermStr(String termStr) {
    this.termStr = termStr;

    if("无期限".equals(termStr) || "无限期".equals(termStr))
    {
      this.term = -1;
    }
    else if(StringUtils.isNotBlank(termStr))
    {
      this.term = Integer.valueOf(termStr);
    }
  }

  //把list转换成map，根据serviceId来拿MmeberCardServiceDTO,主要防治两层for 循环来查询
  public static Map<Long,MemberCardServiceDTO> listToMap(List<MemberCardServiceDTO> memberCardServiceDTOs)
  {
    Map<Long,MemberCardServiceDTO> map = new HashMap<Long, MemberCardServiceDTO>();
    if(CollectionUtils.isNotEmpty(memberCardServiceDTOs))
    {
      for(MemberCardServiceDTO memberCardServiceDTO : memberCardServiceDTOs)
      {
        if(null != memberCardServiceDTO.getServiceId())
        {
          map.put(memberCardServiceDTO.getServiceId(),memberCardServiceDTO);
        }
      }
    }
    return map;
  }
}
