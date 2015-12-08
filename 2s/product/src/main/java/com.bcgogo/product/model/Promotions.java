package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "promotions")
public class Promotions extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private String userName;
  private Long saveTime;
  private String name;
  private Long startTime;
  private Long endTime;//空 就是不限期限
  private String timeFlag;
  private String description;
  private ShopKind shopKind;
  private PromotionsEnum.PromotionsTypes type;
  private PromotionsEnum.PromotionsRanges range;//促销范围
  private PromotionsEnum.PromotionStatus status;
  private PromotionsEnum.PromotionsLimiter promotionsLimiter;  //促销限制条件;

  private DeletedType deleted = DeletedType.FALSE;//默认都为false

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="user_name")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_kind")
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  @Column(name="save_time")
  public Long getSaveTime() {
    return saveTime;
  }

  public void setSaveTime(Long saveTime) {
    this.saveTime = saveTime;
  }

  @Column(name="start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Column(name="end_time")
  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @Column(name="description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

    @Column(name="time_flag")
  public String getTimeFlag() {
    return timeFlag;
  }

  public void setTimeFlag(String timeFlag) {
    this.timeFlag = timeFlag;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionsTypes getType() {
    return type;
  }

  public void setType(PromotionsEnum.PromotionsTypes type) {
    this.type = type;
  }

  @Column(name = "promotions_range")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionsRanges getRange() {
    return range;
  }

  public void setRange(PromotionsEnum.PromotionsRanges range) {
    this.range = range;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionStatus getStatus() {
    return status;
  }

  public void setStatus(PromotionsEnum.PromotionStatus status) {
    this.status = status;
  }

  @Column(name = "promotions_limiter")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionsLimiter getPromotionsLimiter() {
    return promotionsLimiter;
  }

  public void setPromotionsLimiter(PromotionsEnum.PromotionsLimiter promotionsLimiter) {
    this.promotionsLimiter = promotionsLimiter;
  }


  public PromotionsDTO toDTO(){
    PromotionsDTO promotionsDTO = new PromotionsDTO();
    promotionsDTO.setDeleted(this.getDeleted());
    promotionsDTO.setId(this.getId());
    promotionsDTO.setEndTime(this.getEndTime());
    promotionsDTO.setStartTime(this.getStartTime());
    promotionsDTO.setName(this.getName());
    promotionsDTO.setRange(this.getRange());
    promotionsDTO.setType(this.getType());
    promotionsDTO.setStatus(this.getStatus());
    promotionsDTO.setShopId(this.getShopId());
    promotionsDTO.setUserId(this.getUserId());
    promotionsDTO.setUserName(this.getUserName());
    promotionsDTO.setSaveTimeStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD, this.getSaveTime()));
    promotionsDTO.setDescription(this.getDescription());
    promotionsDTO.setPromotionsLimiter(this.getPromotionsLimiter());
    promotionsDTO.setTimeFlag(this.getTimeFlag());
    if(this.getStatus()!=null)
      promotionsDTO.setStatusStr(this.getStatus().getName());
    if(this.getType()!=null)
      promotionsDTO.setTypeStr(this.getType().getName());
    return promotionsDTO;
  }
  public Promotions fromDTO(PromotionsDTO promotionsDTO) throws ParseException {
    this.setShopId(promotionsDTO.getShopId());
    this.setUserId(promotionsDTO.getUserId());
    this.setUserName(promotionsDTO.getUserName());
    this.setEndTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,promotionsDTO.getEndTimeStr()));
    this.setStartTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, promotionsDTO.getStartTimeStr()));
    this.setName(promotionsDTO.getName());
    this.setRange(promotionsDTO.getRange());
    this.setType(promotionsDTO.getType());
    this.setTimeFlag(promotionsDTO.getTimeFlag());
    this.setDescription(promotionsDTO.getDescription());
    this.setPromotionsLimiter(promotionsDTO.getPromotionsLimiter());
    this.setStatus(PromotionsEnum.PromotionStatus.parseToPromotionStatus(promotionsDTO.getStatusStr()));
    return this;
  }

}
