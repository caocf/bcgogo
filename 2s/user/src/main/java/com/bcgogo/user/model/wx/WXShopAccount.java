package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.wx.user.WXShopAccountDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-3
 * Time: 上午10:31
 */
@Entity
@Table(name = "wx_shop_account")
public class WXShopAccount extends LongIdentifier {
  private Long shopId;
  private Long accountId;
  private Double balance;
  private Long expireDate;
  private String remark;
  private DeletedType deleted=DeletedType.FALSE;

  public void fromDTO(WXShopAccountDTO accountDTO){
    this.setId(accountDTO.getId());
    this.setShopId(accountDTO.getShopId());
    this.setAccountId(accountDTO.getAccountId());
    this.setBalance(NumberUtil.round(accountDTO.getBalance()));
    this.setExpireDate(accountDTO.getExpireDate());
    this.setRemark(accountDTO.getRemark());
    this.setDeleted(accountDTO.getDeleted());
  }

  public WXShopAccountDTO toDTO(){
    WXShopAccountDTO shopAccountDTO=new WXShopAccountDTO();
    shopAccountDTO.setId(getId());
    shopAccountDTO.setShopId(getShopId());
    shopAccountDTO.setAccountId(getAccountId());
    shopAccountDTO.setBalance(NumberUtil.round(getBalance()));
    shopAccountDTO.setExpireDate(getExpireDate());
    shopAccountDTO.setRemark(getRemark());
    shopAccountDTO.setDeleted(getDeleted());
    return shopAccountDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "account_id")
  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  @Column(name = "balance")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Column(name = "expire_date")
  public Long getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(Long expireDate) {
    this.expireDate = expireDate;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
