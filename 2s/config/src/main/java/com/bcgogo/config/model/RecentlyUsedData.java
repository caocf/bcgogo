package com.bcgogo.config.model;

import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-8-7
 * Time: 下午1:32
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "recently_used_data")
public class RecentlyUsedData extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private Long dataId;
  private Double count;
  private RecentlyUsedDataType type;
  private Long time;

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name="count")
  public Double getCount() {
    return count;
  }

  public void setCount(Double count) {
    this.count = count;
  }

  @Column(name="data_id")
  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }
  @Column(name="type")
  @Enumerated(EnumType.STRING)
  public RecentlyUsedDataType getType() {
    return type;
  }

  public void setType(RecentlyUsedDataType type) {
    this.type = type;
  }

  @Column(name="time")
  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public RecentlyUsedDataDTO toDTO(){
    RecentlyUsedDataDTO recentlyUsedDataDTO = new RecentlyUsedDataDTO();
    recentlyUsedDataDTO.setType(this.getType());
    recentlyUsedDataDTO.setDataId(this.getDataId());
    recentlyUsedDataDTO.setId(this.getId());
    recentlyUsedDataDTO.setShopId(this.getShopId());
    recentlyUsedDataDTO.setCount(this.getCount());
    recentlyUsedDataDTO.setTime(this.getTime());
    return recentlyUsedDataDTO;
  }
}
