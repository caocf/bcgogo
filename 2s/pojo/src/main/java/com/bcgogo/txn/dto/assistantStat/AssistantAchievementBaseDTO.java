package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.assistantStat.AchievementChangeType;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * 员工提成更改历史基础类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:43
 * To change this template use File | Settings | File Templates.
 */
public class AssistantAchievementBaseDTO  implements Serializable {
  private Long id;

  private Long shopId;

  private Long changeTime;//更改时间
  private Long changeUserId; //更改时用户id

  private AchievementType achievementType;//提成方式 按金额 按比率
  private Double achievementAmount;//提成数额

  private AchievementChangeType achievementChangeType;//员工业绩发生变更类型：服务、商品、会员卡、员工部门

  public Long getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(Long changeTime) {
    this.changeTime = changeTime;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getChangeUserId() {
    return changeUserId;
  }

  public void setChangeUserId(Long changeUserId) {
    this.changeUserId = changeUserId;
  }

  public Double getAchievementAmount() {
    return achievementAmount;
  }

  public void setAchievementAmount(Double achievementAmount) {
    this.achievementAmount = NumberUtil.toReserve(achievementAmount, NumberUtil.PRECISION);;
  }

  public AchievementChangeType getAchievementChangeType() {
    return achievementChangeType;
  }

  public void setAchievementChangeType(AchievementChangeType achievementChangeType) {
    this.achievementChangeType = achievementChangeType;
  }

  public AchievementType getAchievementType() {
    return achievementType;
  }

  public void setAchievementType(AchievementType achievementType) {
    this.achievementType = achievementType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
