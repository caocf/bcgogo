package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.BcgogoOrderItemDto;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderItemDTO;
import com.bcgogo.txn.dto.assistantStat.ShopAchievementConfigDTO;

import javax.persistence.*;

/**
 * 会员业绩统计-店铺未配置的员工业绩统计项
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_achievement_config")
public class ShopAchievementConfig extends LongIdentifier {

  private Long shopId;
  private AssistantRecordType assistantRecordType;
  private Long achievementRecordId;
  private Long lastVestDate;

  public ShopAchievementConfig(){}

  public ShopAchievementConfig(BcgogoOrderDto bcgogoOrderDto,BcgogoOrderItemDto bcgogoOrderItemDto,OrderTypes orderType) {
    if (orderType == OrderTypes.WASH_BEAUTY) {
      WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO) bcgogoOrderDto;
      WashBeautyOrderItemDTO washBeautyOrderItemDTO = (WashBeautyOrderItemDTO) bcgogoOrderItemDto;
      this.setShopId(washBeautyOrderDTO.getShopId());
      this.setAssistantRecordType(AssistantRecordType.SERVICE);
      this.setLastVestDate(washBeautyOrderDTO.getVestDate());
      this.setAchievementRecordId(washBeautyOrderItemDTO.getServiceId());
    }
  }

  public ShopAchievementConfig(Long shopId,Long lastVestDate,AssistantRecordType recordType,Long recordId){
    this.setShopId(shopId);
    this.setLastVestDate(lastVestDate);
    this.setAssistantRecordType(recordType);
    this.setAchievementRecordId(recordId);
  }


  public ShopAchievementConfigDTO toDTO(){
    ShopAchievementConfigDTO shopAchievementConfigDTO = new ShopAchievementConfigDTO();
    shopAchievementConfigDTO.setShopId(getShopId());
    shopAchievementConfigDTO.setAchievementRecordId(getAchievementRecordId());
    shopAchievementConfigDTO.setAssistantRecordType(getAssistantRecordType());
    shopAchievementConfigDTO.setLastVestDate(getLastVestDate());
    return shopAchievementConfigDTO;
  }


  @Column(name = "achievement_record_id")
  public Long getAchievementRecordId() {
    return achievementRecordId;
  }

  public void setAchievementRecordId(Long achievementRecordId) {
    this.achievementRecordId = achievementRecordId;
  }

  @Column(name = "achievement_record_type")
  @Enumerated(EnumType.STRING)
  public AssistantRecordType getAssistantRecordType() {
    return assistantRecordType;
  }


  public void setAssistantRecordType(AssistantRecordType assistantRecordType) {
    this.assistantRecordType = assistantRecordType;
  }


  @Column(name = "last_vest_date")
  public Long getLastVestDate() {
    return lastVestDate;
  }

  public void setLastVestDate(Long lastVestDate) {
    this.lastVestDate = lastVestDate;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

}
