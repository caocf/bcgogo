package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AchievementCalculateWay;
import com.bcgogo.enums.assistantStat.AchievementOrderType;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.stat.dto.AssistantStatDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 员工业绩统计查询类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午1:07
 * To change this template use File | Settings | File Templates.
 */
public class AssistantStatSearchDTO implements Serializable {

  private Long shopId;
  private Integer startMonth;
  private Integer endMonth;
  private Integer startYear;
  private Integer endYear;

  private int startPageNo;
  private int maxRows;

  private String assistantOrDepartmentIdStr;//员工或者部门id
  private Long assistantOrDepartmentId;//员工或者部门id
  private String assistantOrDepartmentName;//员工或者部门名称

  private List<AssistantAchievementStatDTO> assistantAchievementStatDTOList = new ArrayList<AssistantAchievementStatDTO>();


  private Long startTime;
  private Long endTime;

  private String startTimeStr;
  private String endTimeStr;

  private AchievementStatType achievementStatType;
  private String achievementStatTypeStr;

  private Set<OrderTypes> orderTypes;

  private String achievementOrderTypeStr;
  private String achievementCalculateWayStr;//提成计算方式
  private String serviceIdStr;//服务id
  private Long serviceId;//服务id
  private String serviceName;//服务名称


  private List<AssistantServiceRecordDTO> serviceRecordDTOList = new ArrayList<AssistantServiceRecordDTO>();

  private List<AssistantProductRecordDTO> productRecordDTOList = new ArrayList<AssistantProductRecordDTO>();

  private List<AssistantMemberRecordDTO> memberRecordDTOList = new ArrayList<AssistantMemberRecordDTO>();

  private List<AssistantBusinessAccountRecordDTO> businessAccountRecordDTOList = new ArrayList<AssistantBusinessAccountRecordDTO>();

  private Set<Long> assistantOrDepartmentIds = new HashSet<Long>();


  private AssistantAchievementStatDTO totalAssistantStatDTO = new AssistantAchievementStatDTO();
  private String pageType;
  private int totalNum;
  private String orderType;

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public int getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(int totalNum) {
    this.totalNum = totalNum;
  }

  public String getPageType() {
    return pageType;
  }

  public void setPageType(String pageType) {
    this.pageType = pageType;
  }

  public void setTime() {
    if (getStartYear() == null) {
      setStartYear(DateUtil.getCurrentYear());
    }
    if (getEndYear() == null) {
      setEndYear(DateUtil.getCurrentYear());
    }
    if (getStartMonth() == null) {
      setStartMonth(DateUtil.getCurrentMonth());
    }
    if (getEndMonth() == null) {
      setEndMonth(DateUtil.getCurrentMonth());
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(getStartYear(), getStartMonth() - 1, 1, 0, 0, 0);
    setStartTime(calendar.getTimeInMillis());
    calendar.set(getEndYear(), getEndMonth() - 1, 1, 0, 0, 0);
    calendar.add(calendar.MONTH, 1);
    calendar.set(calendar.DATE, 1);
    calendar.add(calendar.DATE, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 59);

    setEndTime(calendar.getTimeInMillis());

    if (NumberUtil.isLongNumber(startTimeStr)) {

      setStartTime(Long.valueOf(startTimeStr));
      setStartMonth(DateUtil.getMonth(getStartTime()));
      setStartYear(DateUtil.getYear(getStartTime()));
    }
    if (NumberUtil.isLongNumber(endTimeStr)) {
      setEndTime(Long.valueOf(endTimeStr));
      setEndYear(DateUtil.getYear(getEndTime()));
      setEndMonth(DateUtil.getMonth(getEndTime()));
    }

    if (NumberUtil.isLongNumber(this.getAssistantOrDepartmentIdStr())) {
      this.setAssistantOrDepartmentId(Long.valueOf(getAssistantOrDepartmentIdStr()));
    }

    if (AchievementStatType.ASSISTANT.name().equals(this.getAchievementStatTypeStr())) {
      this.setAchievementStatType(AchievementStatType.ASSISTANT);
    } else if (AchievementStatType.DEPARTMENT.name().equals(this.getAchievementStatTypeStr())) {
      this.setAchievementStatType(AchievementStatType.DEPARTMENT);
    }

    if(NumberUtil.isLongNumber(this.getServiceIdStr())){
      this.setServiceId(Long.valueOf(this.getServiceIdStr()));
    }


  }

  private AssistantRecordType assistantRecordType;
  private Long achievementRecordId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public AssistantRecordType getAssistantRecordType() {
    return assistantRecordType;
  }

  public void setAssistantRecordType(AssistantRecordType assistantRecordType) {
    this.assistantRecordType = assistantRecordType;
  }

  public Long getAchievementRecordId() {
    return achievementRecordId;
  }

  public void setAchievementRecordId(Long achievementRecordId) {
    this.achievementRecordId = achievementRecordId;
  }

  public Integer getStartMonth() {
    return startMonth;
  }

  public void setStartMonth(Integer startMonth) {
    this.startMonth = startMonth;
  }

  public Integer getEndMonth() {
    return endMonth;
  }

  public void setEndMonth(Integer endMonth) {
    this.endMonth = endMonth;
  }

  public Integer getStartYear() {
    return startYear;
  }

  public void setStartYear(Integer startYear) {
    this.startYear = startYear;
  }

  public Integer getEndYear() {
    return endYear;
  }

  public void setEndYear(Integer endYear) {
    this.endYear = endYear;
  }

  public AchievementStatType getAchievementStatType() {
    return achievementStatType;
  }

  public void setAchievementStatType(AchievementStatType achievementStatType) {
    this.achievementStatType = achievementStatType;
  }

  public String getAchievementStatTypeStr() {
    return achievementStatTypeStr;
  }

  public void setAchievementStatTypeStr(String achievementStatTypeStr) {
    this.achievementStatTypeStr = achievementStatTypeStr;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public List<AssistantAchievementStatDTO> getAssistantAchievementStatDTOList() {
    return assistantAchievementStatDTOList;
  }

  public void setAssistantAchievementStatDTOList(List<AssistantAchievementStatDTO> assistantAchievementStatDTOList) {
    this.assistantAchievementStatDTOList = assistantAchievementStatDTOList;

    if (CollectionUtils.isNotEmpty(assistantAchievementStatDTOList)) {
      AssistantAchievementStatDTO assistantAchievementStatDTO = new AssistantAchievementStatDTO();
      for (AssistantAchievementStatDTO statDTO : assistantAchievementStatDTOList) {
        assistantAchievementStatDTO = assistantAchievementStatDTO.add(statDTO);
      }
      setTotalAssistantStatDTO(assistantAchievementStatDTO);
    }
  }

  public Set<OrderTypes> getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(Set<OrderTypes> orderTypes) {
    this.orderTypes = orderTypes;
  }

  public List<AssistantServiceRecordDTO> getServiceRecordDTOList() {
    return serviceRecordDTOList;
  }

  public void setServiceRecordDTOList(List<AssistantServiceRecordDTO> serviceRecordDTOList) {
    this.serviceRecordDTOList = serviceRecordDTOList;
  }

  public List<AssistantProductRecordDTO> getProductRecordDTOList() {
    return productRecordDTOList;
  }

  public void setProductRecordDTOList(List<AssistantProductRecordDTO> productRecordDTOList) {
    this.productRecordDTOList = productRecordDTOList;
  }

  public List<AssistantMemberRecordDTO> getMemberRecordDTOList() {
    return memberRecordDTOList;
  }

  public void setMemberRecordDTOList(List<AssistantMemberRecordDTO> memberRecordDTOList) {
    this.memberRecordDTOList = memberRecordDTOList;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public String getAssistantOrDepartmentIdStr() {
    return assistantOrDepartmentIdStr;
  }

  public void setAssistantOrDepartmentIdStr(String assistantOrDepartmentIdStr) {
    this.assistantOrDepartmentIdStr = assistantOrDepartmentIdStr;
  }

  public Long getAssistantOrDepartmentId() {
    return assistantOrDepartmentId;
  }

  public void setAssistantOrDepartmentId(Long assistantOrDepartmentId) {
    this.assistantOrDepartmentId = assistantOrDepartmentId;
  }

  public AssistantAchievementStatDTO getTotalAssistantStatDTO() {
    return totalAssistantStatDTO;
  }

  public void setTotalAssistantStatDTO(AssistantAchievementStatDTO totalAssistantStatDTO) {
    this.totalAssistantStatDTO = totalAssistantStatDTO;
  }

  public Set<Long> getAssistantOrDepartmentIds() {
    return assistantOrDepartmentIds;
  }

  public void setAssistantOrDepartmentIds(Set<Long> assistantOrDepartmentIds) {
    this.assistantOrDepartmentIds = assistantOrDepartmentIds;
  }

  public String getAchievementOrderTypeStr() {
    return achievementOrderTypeStr;
  }

  public void setAchievementOrderTypeStr(String achievementOrderTypeStr) {
    this.achievementOrderTypeStr = achievementOrderTypeStr;
  }

  public String getAchievementCalculateWayStr() {
    return achievementCalculateWayStr;
  }

  public void setAchievementCalculateWayStr(String achievementCalculateWayStr) {
    this.achievementCalculateWayStr = achievementCalculateWayStr;
  }

  public String getServiceIdStr() {
    return serviceIdStr;
  }

  public void setServiceIdStr(String serviceIdStr) {
    this.serviceIdStr = serviceIdStr;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getAssistantOrDepartmentName() {
    return assistantOrDepartmentName;
  }

  public void setAssistantOrDepartmentName(String assistantOrDepartmentName) {
    this.assistantOrDepartmentName = assistantOrDepartmentName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public List<AssistantBusinessAccountRecordDTO> getBusinessAccountRecordDTOList() {
    return businessAccountRecordDTOList;
  }

  public void setBusinessAccountRecordDTOList(List<AssistantBusinessAccountRecordDTO> businessAccountRecordDTOList) {
    this.businessAccountRecordDTOList = businessAccountRecordDTOList;
  }


  public String getQueryConditionStr() {

    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("统计日期:").append(this.getStartYear()).append("年").append(this.getStartMonth()).append("月至").append(this.getEndYear()).append("年").append(this.getEndMonth()).append("月");
    if (AchievementCalculateWay.CALCULATE_BY_ASSISTANT.name().equals(this.getAchievementCalculateWayStr())) {
      stringBuffer.append(" 统计方式:").append(AchievementCalculateWay.CALCULATE_BY_ASSISTANT.getName());
    } else if (AchievementCalculateWay.CALCULATE_BY_DETAIL.name().equals(this.getAchievementCalculateWayStr())) {
      stringBuffer.append(" 统计方式:").append(AchievementCalculateWay.CALCULATE_BY_DETAIL.getName());
    }
    if (AchievementStatType.ASSISTANT.name().equals(this.getAchievementStatTypeStr())) {
      if(StringUtil.isNotEmpty(this.getAssistantOrDepartmentName())){
        stringBuffer.append(" 员工:").append(this.getAssistantOrDepartmentName());
      }
    } else if (AchievementStatType.DEPARTMENT.name().equals(this.getAchievementStatTypeStr())) {
      if (StringUtil.isNotEmpty(this.getAssistantOrDepartmentName())) {
        stringBuffer.append(" 部门:").append(this.getAssistantOrDepartmentName());
      }
    }



//    if (AchievementOrderType.WASH_BEAUTY.name().equals(this.getAchievementOrderTypeStr())) {
//      stringBuffer.append(" ").append(AchievementOrderType.WASH_BEAUTY.getName());
//    } else if (AchievementOrderType.MEMBER.name().equals(this.getAchievementOrderTypeStr())) {
//      stringBuffer.append(" ").append(AchievementOrderType.MEMBER.getName());
//    } else if (AchievementOrderType.SALES.name().equals(this.getAchievementOrderTypeStr())) {
//      stringBuffer.append(" ").append(AchievementOrderType.SALES.getName());
//    } else if (AchievementOrderType.BUSINESS_ACCOUNT.name().equals(this.getAchievementOrderTypeStr())) {
//      stringBuffer.append(" ").append(AchievementOrderType.BUSINESS_ACCOUNT.getName());
//    } else if (AchievementOrderType.REPAIR_SERVICE.name().equals(this.getAchievementOrderTypeStr())) {
//      stringBuffer.append(" ").append(AchievementOrderType.REPAIR_SERVICE.getName());
//    }

    if (MemberOrderType.RENEW.name().equals(this.getServiceIdStr())) {
      stringBuffer.append(" 会员").append(MemberOrderType.RENEW.getName());
    } else if (MemberOrderType.NEW.name().equals(this.getServiceIdStr())) {
      stringBuffer.append(" 会员").append(MemberOrderType.NEW.getName());
    } else if (StringUtil.isNotEmpty(this.getServiceName())) {
      stringBuffer.append(" 项目:").append(this.getServiceName());
    }
    return stringBuffer.toString();
  }

}
