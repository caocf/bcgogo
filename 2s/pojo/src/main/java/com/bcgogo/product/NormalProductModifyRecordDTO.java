package com.bcgogo.product;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-3-3
 * Time: 下午5:29
 * To change this template use File | Settings | File Templates.
 */
public class NormalProductModifyRecordDTO{
  private Long id;
  private Long userId;
  private Long normalProductId;
  private Long shopProductId;
  private NormalProductModifyScene scene;

  public NormalProductModifyRecordDTO(Long userId,Long normalProductId,Long shopProductId,NormalProductModifyScene scene){
    this.userId=userId;
    this.normalProductId=normalProductId;
    this.shopProductId=shopProductId;
    this.scene=scene;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  public Long getShopProductId() {
    return shopProductId;
  }

  public void setShopProductId(Long shopProductId) {
    this.shopProductId = shopProductId;
  }

  public NormalProductModifyScene getScene() {
    return scene;
  }

  public void setScene(NormalProductModifyScene scene) {
    this.scene = scene;
  }
}
