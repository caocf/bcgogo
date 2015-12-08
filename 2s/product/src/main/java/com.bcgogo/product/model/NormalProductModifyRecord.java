package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.NormalProductModifyRecordDTO;
import com.bcgogo.product.NormalProductModifyScene;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-3-3
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="normal_product_modify_record")
public class NormalProductModifyRecord extends LongIdentifier {
  private Long userId;
  private Long normalProductId;
  private Long shopProductId;
  private NormalProductModifyScene scene;

  public NormalProductModifyRecord(){}

  @Column(name="user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name="normal_product_id")
  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  @Column(name="shop_product_id")
  public Long getShopProductId() {
    return shopProductId;
  }

  public void setShopProductId(Long shopProductId) {
    this.shopProductId = shopProductId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "scene")
  public NormalProductModifyScene getScene() {
    return scene;
  }

  public void setScene(NormalProductModifyScene scene) {
    this.scene = scene;
  }

  public  void fromDTO(NormalProductModifyRecordDTO recordDTO){
    if(recordDTO==null) return;
    this.userId=recordDTO.getUserId();
    this.normalProductId=recordDTO.getNormalProductId();
    this.shopProductId=recordDTO.getShopProductId();
    this.scene=recordDTO.getScene();
  }
}
