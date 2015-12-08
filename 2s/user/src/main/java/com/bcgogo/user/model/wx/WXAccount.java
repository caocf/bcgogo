package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.JPAUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.wx.WXAccountType;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.user.WXAccountDTO;

import javax.persistence.*;
import java.io.IOException;
import java.sql.Blob;

/**
 * 微信账户
 * User: ndong
 * Date: 14-9-15
 * Time: 上午9:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_account")
public class WXAccount extends LongIdentifier {
  private Long shopId= ShopConstant.BC_SHOP_ID;
  private String name;
  private String appId;
  private Blob appSecret;
  private String token= WXConstant.token;
  private String encodingKey;
  private String publicNo;
  private String remark;
  private WXAccountType accountType;
  private DeletedType deleted=DeletedType.FALSE;

  public WXAccountDTO toDTO() throws IOException {
    WXAccountDTO accountDTO=new WXAccountDTO();
    accountDTO.setId(getId());
    accountDTO.setShopId(getShopId());
    accountDTO.setName(getName());
    accountDTO.setAppId(getAppId());
    accountDTO.setAppSecretBlob(getAppSecret());
    accountDTO.setAppSecretByte(JPAUtil.blobToBytes(getAppSecret()));
    accountDTO.setToken(getToken());
    accountDTO.setEncodingKey(getEncodingKey());
    accountDTO.setPublicNo(getPublicNo());
    accountDTO.setRemark(getRemark());
    accountDTO.setAccountType(getAccountType());
    accountDTO.setDeleted(getDeleted());
    return accountDTO;
  }

  public void fromDTO(WXAccountDTO accountDTO){
    this.setId(accountDTO.getId());
    this.setShopId(accountDTO.getShopId());
    this.setName(accountDTO.getName());
    this.setAppId(accountDTO.getAppId());
    this.setAppSecret(accountDTO.getAppSecretBlob());
    this.setToken(accountDTO.getToken());
    this.setEncodingKey(accountDTO.getEncodingKey());
    this.setPublicNo(accountDTO.getPublicNo());
    this.setRemark(accountDTO.getRemark());
    this.setAccountType(accountDTO.getAccountType());
    this.setDeleted(accountDTO.getDeleted());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "app_id")
  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  @Column(name = "app_secret")
  public Blob getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(Blob appSecret) {
    this.appSecret = appSecret;
  }


  @Column(name = "token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Column(name = "encoding_key")
  public String getEncodingKey() {
    return encodingKey;
  }

  public void setEncodingKey(String encodingKey) {
    this.encodingKey = encodingKey;
  }

  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Column(name = "account_type")
  @Enumerated(EnumType.STRING)
  public WXAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(WXAccountType accountType) {
    this.accountType = accountType;
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
