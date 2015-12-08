package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 上午10:53
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageBuildTaskDTO {
  private Long id;
  private Long shopId;
  private Long seedId;
  private PushMessageScene scene;
  private Long createTime;
  private ExeStatus exeStatus;
  private Long executeTime;

  public PushMessageBuildTaskDTO() {
    this.exeStatus = ExeStatus.READY;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getSeedId() {
    return seedId;
  }

  public void setSeedId(Long seedId) {
    this.seedId = seedId;
  }

  public PushMessageScene getScene() {
    return scene;
  }

  public void setScene(PushMessageScene scene) {
    this.scene = scene;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  public Long getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(Long executeTime) {
    this.executeTime = executeTime;
  }
}
