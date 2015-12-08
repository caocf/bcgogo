package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.assistantStat.AchievementMemberType;
import com.bcgogo.enums.user.MemberCardType;

/**
 * 员工业绩统计-会员卡提成历史记录表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:41
 * To change this template use File | Settings | File Templates.
 */
public class MemberAchievementHistoryDTO extends AssistantAchievementBaseDTO {
  private Long memberCardId; //会员卡id
  private MemberCardType memberCardType; //会员卡类型
  private AchievementMemberType achievementMemberType;

  private MemberOrderType memberOrderType;//购卡还是续卡

  public Long getMemberCardId() {
    return memberCardId;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
  }

  public MemberCardType getMemberCardType() {
    return memberCardType;
  }

  public void setMemberCardType(MemberCardType memberCardType) {
    this.memberCardType = memberCardType;
  }

  public AchievementMemberType getAchievementMemberType() {
    return achievementMemberType;
  }

  public void setAchievementMemberType(AchievementMemberType achievementMemberType) {
    this.achievementMemberType = achievementMemberType;
  }

  public MemberOrderType getMemberOrderType() {
    return memberOrderType;
  }

  public void setMemberOrderType(MemberOrderType memberOrderType) {
    this.memberOrderType = memberOrderType;
  }
}
