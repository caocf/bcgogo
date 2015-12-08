package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.assistantStat.AssistantRecordType;

import java.io.Serializable;

/**
 * 员工业绩记录基础类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午1:07
 * To change this template use File | Settings | File Templates.
 */
public class ShopAchievementConfigDTO implements Serializable {

  private Long shopId;
  private AssistantRecordType assistantRecordType;
  private Long achievementRecordId;
  private Long lastVestDate;

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

  public Long getLastVestDate() {
    return lastVestDate;
  }

  public void setLastVestDate(Long lastVestDate) {
    this.lastVestDate = lastVestDate;
  }
}
