package com.bcgogo.wx.qr;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-8
 * Time: 下午4:14
 */
public class WXQRCodeSearchCondition {
  private String publicNo;
  private QRScene scene;
  private Long shopId;
  private Long sceneId;

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public QRScene getScene() {
    return scene;
  }

  public void setScene(QRScene scene) {
    this.scene = scene;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getSceneId() {
    return sceneId;
  }

  public void setSceneId(Long sceneId) {
    this.sceneId = sceneId;
  }
}
