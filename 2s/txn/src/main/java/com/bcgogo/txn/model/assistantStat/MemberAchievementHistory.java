package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.assistantStat.AchievementMemberType;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.enums.user.MemberCardType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.MemberAchievementHistoryDTO;

import javax.persistence.*;

/**
 * 会员业绩统计-会员员工业绩提成记录表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "member_achievement_history")
public class MemberAchievementHistory extends LongIdentifier {

  private Long shopId;
  private Long memberCardId;
  private MemberCardType memberCardType;
  private AchievementType achievementType;//提成方式 按金额 按比率
  private Double achievementAmount;//提成数额
  private Long changeTime;//更改时间
  private Long changeUserId;//更改时用户id

  private AchievementMemberType achievementMemberType;

  private MemberOrderType memberOrderType;//购卡还是续卡

  public MemberAchievementHistoryDTO toDTO() {
    MemberAchievementHistoryDTO memberAchievementHistoryDTO = new MemberAchievementHistoryDTO();
    memberAchievementHistoryDTO.setId(getId());
    memberAchievementHistoryDTO.setShopId(getShopId());
    memberAchievementHistoryDTO.setMemberCardId(getMemberCardId());
    memberAchievementHistoryDTO.setMemberCardType(getMemberCardType());
    memberAchievementHistoryDTO.setAchievementType(getAchievementType());
    memberAchievementHistoryDTO.setAchievementAmount(getAchievementAmount());
    memberAchievementHistoryDTO.setChangeTime(getChangeTime());
    memberAchievementHistoryDTO.setChangeUserId(getChangeUserId());
    memberAchievementHistoryDTO.setAchievementMemberType(getAchievementMemberType());
    memberAchievementHistoryDTO.setMemberOrderType(getMemberOrderType());
    return memberAchievementHistoryDTO;
  }

  public MemberAchievementHistory fromDTO(MemberAchievementHistoryDTO memberAchievementHistoryDTO) {
    this.setId(memberAchievementHistoryDTO.getId());
    this.setShopId(memberAchievementHistoryDTO.getShopId());
    this.setMemberCardId(memberAchievementHistoryDTO.getMemberCardId());
    this.setMemberCardType(memberAchievementHistoryDTO.getMemberCardType());
    this.setAchievementAmount(memberAchievementHistoryDTO.getAchievementAmount());
    this.setAchievementType(memberAchievementHistoryDTO.getAchievementType());
    this.setChangeTime(memberAchievementHistoryDTO.getChangeTime());
    this.setChangeUserId(memberAchievementHistoryDTO.getChangeUserId());
    this.setAchievementMemberType(memberAchievementHistoryDTO.getAchievementMemberType());
    this.setMemberOrderType(memberAchievementHistoryDTO.getMemberOrderType());
    return this;
  }

  @Column(name = "member_card_id")
  public Long getMemberCardId() {
    return memberCardId;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
  }

  @Column(name = "member_card_type")
  @Enumerated(EnumType.STRING)
  public MemberCardType getMemberCardType() {
    return memberCardType;
  }

  public void setMemberCardType(MemberCardType memberCardType) {
    this.memberCardType = memberCardType;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "achievement_type")
  @Enumerated(EnumType.STRING)
  public AchievementType getAchievementType() {
    return achievementType;
  }

  public void setAchievementType(AchievementType achievementType) {
    this.achievementType = achievementType;
  }

  @Column(name = "achievement_amount")
  public Double getAchievementAmount() {
    return achievementAmount;
  }

  public void setAchievementAmount(Double achievementAmount) {
    this.achievementAmount = achievementAmount;
  }

  @Column(name = "change_time")
  public Long getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(Long changeTime) {
    this.changeTime = changeTime;
  }

  @Column(name = "change_user_id")
  public Long getChangeUserId() {
    return changeUserId;
  }

  public void setChangeUserId(Long changeUserId) {
    this.changeUserId = changeUserId;
  }

  @Column(name = "achievement_member_type")
  @Enumerated(EnumType.STRING)
  public AchievementMemberType getAchievementMemberType() {
    return achievementMemberType;
  }

  public void setAchievementMemberType(AchievementMemberType achievementMemberType) {
    this.achievementMemberType = achievementMemberType;
  }

  @Column(name = "member_order_type")
  @Enumerated(EnumType.STRING)
  public MemberOrderType getMemberOrderType() {
    return memberOrderType;
  }

  public void setMemberOrderType(MemberOrderType memberOrderType) {
    this.memberOrderType = memberOrderType;
  }
}
