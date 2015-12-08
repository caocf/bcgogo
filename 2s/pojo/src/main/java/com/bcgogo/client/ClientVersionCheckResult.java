package com.bcgogo.client;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午1:29
 * 校验当前客户端是否需要升级
 */
public class ClientVersionCheckResult {
  private Long shopId;
  private String userNo;
  private String localVersion;
  private String recentVersion;  //最新版本
  private Boolean needUpdate;     //是否需要升级
  private String updateUrl;       //更新文件地址

  public ClientVersionCheckResult() {
    super();
  }

  public ClientVersionCheckResult(Long shopId, String userNo, String localVersion, Boolean needUpdate) {
    this.setUserNo(userNo);
    this.setShopId(shopId);
    this.setLocalVersion(localVersion);
    this.setNeedUpdate(needUpdate);
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getLocalVersion() {
    return localVersion;
  }

  public void setLocalVersion(String localVersion) {
    this.localVersion = localVersion;
  }

  public String getRecentVersion() {
    return recentVersion;
  }

  public void setRecentVersion(String recentVersion) {
    this.recentVersion = recentVersion;
  }

  public Boolean getNeedUpdate() {
    return needUpdate;
  }

  public void setNeedUpdate(Boolean needUpdate) {
    this.needUpdate = needUpdate;
  }

  public String getUpdateUrl() {
    return updateUrl;
  }

  public void setUpdateUrl(String updateUrl) {
    this.updateUrl = updateUrl;
  }
}
