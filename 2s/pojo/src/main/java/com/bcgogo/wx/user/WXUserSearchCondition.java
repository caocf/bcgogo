package com.bcgogo.wx.user;

/**
 * Created by XinyuQiu on 14-9-25.
 */
public class WXUserSearchCondition {
  private Long shopId;
  private String publicNo;
  private String nickName;
  private String keyWord;
  private String remark;
  private String vehicleNo;
  private int pageSize = 15;
  private String startPageNo;

  private int currentPage;


  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getKeyWord() {
    return keyWord;
  }

  public void setKeyWord(String keyWord) {
    this.keyWord = keyWord;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public String getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(String startPageNo) {
    this.startPageNo = startPageNo;
  }
}
