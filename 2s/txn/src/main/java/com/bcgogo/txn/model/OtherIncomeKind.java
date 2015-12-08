package com.bcgogo.txn.model;

import com.bcgogo.enums.KindStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.OtherIncomeKindDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-11
 * Time: 上午9:28
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="other_income_kind")
public class OtherIncomeKind extends LongIdentifier {
  private Long shopId;
  private String kindName;
  private KindStatus status;
  private Long useTimes;//使用次数


  @Column(name="use_times")
  public Long getUseTimes() {
    return useTimes;
  }

  public void setUseTimes(Long useTimes) {
    this.useTimes = useTimes;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="kind_name")
  public String getKindName() {
    return kindName;
  }

  public void setKindName(String kindName) {
    this.kindName = kindName;
  }

  @Enumerated(EnumType.STRING)
  public KindStatus getStatus() {
    return status;
  }

  public void setStatus(KindStatus status) {
    this.status = status;
  }

  public OtherIncomeKind(){
  }

  public OtherIncomeKind(OtherIncomeKindDTO otherIncomeKindDTO)
  {
    if(null == otherIncomeKindDTO)
    {
      return;
    }

    this.shopId = otherIncomeKindDTO.getShopId();
    this.setId(otherIncomeKindDTO.getId());
    this.kindName = otherIncomeKindDTO.getKindName();
  }

  public OtherIncomeKindDTO toDTO()
  {
    OtherIncomeKindDTO otherIncomeKindDTO= new OtherIncomeKindDTO();

    otherIncomeKindDTO.setId(this.getId());
    otherIncomeKindDTO.setKindName(this.getKindName());
    otherIncomeKindDTO.setShopId(this.getShopId());
    otherIncomeKindDTO.setUseTimes(NumberUtil.longValue(this.getUseTimes()));
    return otherIncomeKindDTO;
  }
}
