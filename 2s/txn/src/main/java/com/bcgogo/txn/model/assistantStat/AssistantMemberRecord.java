package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.user.MemberCardType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.AssistantMemberRecordDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;

/**
 * 会员业绩统计-员工会员卡销售记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "assistant_member_record")
public class AssistantMemberRecord extends LongIdentifier {

  private Long shopId;
  private Long orderId;
  private OrderTypes orderType;
  private Long vestDate;

  private Long assistantId;
  private String assistantName;

  private Long departmentId;
  private String departmentName;

  private Long customerId;
  private String customer;

  private String memberNo;

  private Long memberCardId;
  private String memberCardName;
  private MemberCardType memberCardType;

  private Double memberCardTotal;//会员卡设置的金额
  private Double total; //会员卡购卡续卡单据的金额

  private Double achievement;

  private Long memberAchievementHistoryId;

  private MemberOrderType memberOrderType;//购卡还是续卡

  private Double achievementByAssistant;//根据员工的配置 获取的提成
  private String achievementCalculateWay;//员工的提成计算方式 用文字描述
  private String achievementByAssistantCalculateWay;//根据员工的配置 获取的提成 的计算方式 用文字描述
  private Long statTime;//统计时间


  public AssistantMemberRecordDTO toDTO() {
    AssistantMemberRecordDTO assistantMemberRecordDTO = new AssistantMemberRecordDTO();
    assistantMemberRecordDTO.setId(getId());
    assistantMemberRecordDTO.setShopId(getShopId());
    assistantMemberRecordDTO.setOrderId(getOrderId());
    assistantMemberRecordDTO.setOrderType(getOrderType());
    assistantMemberRecordDTO.setVestDate(getVestDate());

    assistantMemberRecordDTO.setAssistantId(getAssistantId());
    assistantMemberRecordDTO.setAssistantName(getAssistantName());
    assistantMemberRecordDTO.setDepartmentId(getDepartmentId());
    assistantMemberRecordDTO.setDepartmentName(getDepartmentName());

    assistantMemberRecordDTO.setCustomer(getCustomer());
    assistantMemberRecordDTO.setCustomerId(getCustomerId());

    assistantMemberRecordDTO.setMemberNo(getMemberNo());
    assistantMemberRecordDTO.setMemberCardId(getMemberCardId());
    assistantMemberRecordDTO.setMemberCardName(getMemberCardName());
    assistantMemberRecordDTO.setMemberCardTotal(getMemberCardTotal());
    assistantMemberRecordDTO.setMemberCardType(getMemberCardType());
    assistantMemberRecordDTO.setTotal(getTotal());

    assistantMemberRecordDTO.setMemberOrderType(getMemberOrderType());
    assistantMemberRecordDTO.setMemberOrderTypeStr(getMemberOrderType() == null?"退卡":getMemberOrderType().getName());

    assistantMemberRecordDTO.setAchievement(getAchievement());
    assistantMemberRecordDTO.setMemberAchievementHistoryId(getMemberAchievementHistoryId());

    if (getVestDate() != null) {
      assistantMemberRecordDTO.setVestDateStr(DateUtil.dateLongToStr(getVestDate(), DateUtil.DATE_STRING_FORMAT_CN));
    }

    if (getOrderType() != null) {
      assistantMemberRecordDTO.setOrderTypeStr(getOrderType().getName());
    }

    if (getOrderId() != null) {
      assistantMemberRecordDTO.setOrderIdStr(getOrderId().toString());
    }

    if(getMemberCardType() != null){
      if(getMemberCardType() == MemberCardType.STORED_CARD){
        assistantMemberRecordDTO.setMemberCardTypeStr("储值卡");
      }else if(getMemberCardType() == MemberCardType.TIMES_CARD){
        assistantMemberRecordDTO.setMemberCardTypeStr("计次卡");
      }
    }

    assistantMemberRecordDTO.setAchievementByAssistant(getAchievementByAssistant());
    assistantMemberRecordDTO.setAchievementCalculateWay(getAchievementCalculateWay());
    assistantMemberRecordDTO.setAchievementByAssistantCalculateWay(getAchievementByAssistantCalculateWay());

    return assistantMemberRecordDTO;
  }


  public AssistantMemberRecord fromDTO(AssistantMemberRecordDTO assistantMemberRecordDTO) {

    this.setShopId(assistantMemberRecordDTO.getShopId());
    this.setOrderId(assistantMemberRecordDTO.getOrderId());
    this.setOrderType(assistantMemberRecordDTO.getOrderType());
    this.setVestDate(assistantMemberRecordDTO.getVestDate());

    this.setAssistantId(assistantMemberRecordDTO.getAssistantId());
    this.setAssistantName(assistantMemberRecordDTO.getAssistantName());
    this.setDepartmentId(assistantMemberRecordDTO.getDepartmentId());
    this.setDepartmentName(assistantMemberRecordDTO.getDepartmentName());

    this.setCustomer(assistantMemberRecordDTO.getCustomer());
    this.setCustomerId(assistantMemberRecordDTO.getCustomerId());

    this.setMemberNo(assistantMemberRecordDTO.getMemberNo());
    this.setMemberCardId(assistantMemberRecordDTO.getMemberCardId());
    this.setMemberCardName(assistantMemberRecordDTO.getMemberCardName());
    this.setMemberCardTotal(assistantMemberRecordDTO.getMemberCardTotal());
    this.setMemberCardType(assistantMemberRecordDTO.getMemberCardType());

    this.setTotal(assistantMemberRecordDTO.getTotal());
    this.setMemberCardTotal(assistantMemberRecordDTO.getMemberCardTotal());

    this.setAchievement(assistantMemberRecordDTO.getAchievement());
    this.setMemberAchievementHistoryId(assistantMemberRecordDTO.getMemberAchievementHistoryId());
    this.setMemberOrderType(assistantMemberRecordDTO.getMemberOrderType());

    this.setAchievementByAssistant(assistantMemberRecordDTO.getAchievementByAssistant());
    this.setAchievementCalculateWay(assistantMemberRecordDTO.getAchievementCalculateWay());
    this.setAchievementByAssistantCalculateWay(assistantMemberRecordDTO.getAchievementByAssistantCalculateWay());

    return this;
  }


  @Column(name = "assistant_id")
  public Long getAssistantId() {
    return assistantId;

  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  @Column(name = "assistant_name")
  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "department_name")
  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }


  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }


  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "member_no")
  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  @Column(name = "member_card_id")
  public Long getMemberCardId() {
    return memberCardId;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
  }

  @Column(name = "member_card_name")
  public String getMemberCardName() {
    return memberCardName;
  }

  public void setMemberCardName(String memberCardName) {
    this.memberCardName = memberCardName;
  }

  @Column(name = "member_card_type")
  @Enumerated(EnumType.STRING)
  public MemberCardType getMemberCardType() {
    return memberCardType;
  }

  public void setMemberCardType(MemberCardType memberCardType) {
    this.memberCardType = memberCardType;
  }

  @Column(name = "member_card_total")
  public Double getMemberCardTotal() {
    return memberCardTotal;
  }

  public void setMemberCardTotal(Double memberCardTotal) {
    this.memberCardTotal = memberCardTotal;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "achievement")
  public Double getAchievement() {
    return achievement;
  }

  public void setAchievement(Double achievement) {
    this.achievement = achievement;
  }

  @Column(name = "member_achievement_history_id")
  public Long getMemberAchievementHistoryId() {
    return memberAchievementHistoryId;
  }

  public void setMemberAchievementHistoryId(Long memberAchievementHistoryId) {
    this.memberAchievementHistoryId = memberAchievementHistoryId;
  }

  @Column(name="member_order_type")
  @Enumerated(EnumType.STRING)
  public MemberOrderType getMemberOrderType() {
    return memberOrderType;
  }

  public void setMemberOrderType(MemberOrderType memberOrderType) {
    this.memberOrderType = memberOrderType;
  }

  @Column(name="achievement_by_assistant")
  public Double getAchievementByAssistant() {
    return achievementByAssistant;
  }

  public void setAchievementByAssistant(Double achievementByAssistant) {
    this.achievementByAssistant = achievementByAssistant;
  }

  @Column(name="achievement_calculate_way")
  public String getAchievementCalculateWay() {
    return achievementCalculateWay;
  }

  public void setAchievementCalculateWay(String achievementCalculateWay) {
    this.achievementCalculateWay = achievementCalculateWay;
  }

  @Column(name="achievement_by_assistant_calculate_way")
  public String getAchievementByAssistantCalculateWay() {
    return achievementByAssistantCalculateWay;
  }

  public void setAchievementByAssistantCalculateWay(String achievementByAssistantCalculateWay) {
    this.achievementByAssistantCalculateWay = achievementByAssistantCalculateWay;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }
}
