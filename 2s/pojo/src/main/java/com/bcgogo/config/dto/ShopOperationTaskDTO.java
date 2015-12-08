package com.bcgogo.config.dto;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.shop.ShopOperateTaskScene;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-22
 * Time: 上午10:25
 * To change this template use File | Settings | File Templates.
 */
public class ShopOperationTaskDTO {
  private Long id;
  private Long shopId;
  private ShopOperateTaskScene scene;
  private Long createTime;
  private ExeStatus exeStatus;
  private Long executeTime;

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

  public ShopOperateTaskScene getScene() {
    return scene;
  }

  public void setScene(ShopOperateTaskScene scene) {
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
