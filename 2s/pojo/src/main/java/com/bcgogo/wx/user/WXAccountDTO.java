package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXAccountType;
import com.bcgogo.wx.WXConstant;

import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-15
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
public class WXAccountDTO implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId=-1L;
  private String name;
  private String appId;
  private String secret;
  private byte[] appSecretByte;
  private Blob appSecretBlob;
  private String token= WXConstant.token;
  private String encodingKey;
  private String publicNo;
  private WXAccountType accountType;
  private String remark;
  private DeletedType deleted=DeletedType.FALSE;
  //使用的店铺名
  private WXShopAccountDTO[] shopAccountDTOs;
  private String shopNames;

//  public WXAccountDTO toDTO(){
//    WXAccountDTO accountDTO=new WXAccountDTO();
//    return accountDTO;
//  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    setIdStr(StringUtil.valueOf(id));
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }


  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public byte[] getAppSecretByte() {
    return appSecretByte;
  }

  public void setAppSecretByte(byte[] appSecretByte) {
    this.appSecretByte = appSecretByte;
  }

  public Blob getAppSecretBlob() {
    return appSecretBlob;
  }

  public void setAppSecretBlob(Blob appSecretBlob) {
    this.appSecretBlob = appSecretBlob;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getEncodingKey() {
    return encodingKey;
  }

  public void setEncodingKey(String encodingKey) {
    this.encodingKey = encodingKey;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public WXShopAccountDTO[] getShopAccountDTOs() {
    return shopAccountDTOs;
  }

  public void setShopAccountDTOs(WXShopAccountDTO[] shopAccountDTOs) {
    this.shopAccountDTOs = shopAccountDTOs;
  }

  public String getShopNames() {
    return shopNames;
  }

  public void setShopNames(String shopNames) {
    this.shopNames = shopNames;
  }

  public WXAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(WXAccountType accountType) {
    this.accountType = accountType;
  }
}
