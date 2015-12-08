package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopOperationTaskDTO;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.shop.ShopOperateTaskScene;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-22
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_operation_task")
public class ShopOperationTask extends LongIdentifier {
  private Long shopId;
  private ShopOperateTaskScene scene;
  private Long createTime;
  private ExeStatus exeStatus;
  private Long executeTime;

  public ShopOperationTask(Long shopId, ShopOperateTaskScene scene, ExeStatus exeStatus) {
    this.shopId = shopId;
    this.scene = scene;
    this.exeStatus = exeStatus;
    this.createTime = System.currentTimeMillis();
    this.executeTime = System.currentTimeMillis();
  }

  public ShopOperationTask() {
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "scene")
  public ShopOperateTaskScene getScene() {
    return scene;
  }

  public void setScene(ShopOperateTaskScene scene) {
    this.scene = scene;
  }

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "exe_status")
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  @Column(name = "execute_time")
  public Long getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(Long executeTime) {
    this.executeTime = executeTime;
  }

  public ShopOperationTaskDTO toDTO(){
    ShopOperationTaskDTO shopOperationTaskDTO = new ShopOperationTaskDTO();
    shopOperationTaskDTO.setId(this.getId());
    shopOperationTaskDTO.setCreateTime(this.getCreateTime());
    shopOperationTaskDTO.setExecuteTime(this.getExecuteTime());
    shopOperationTaskDTO.setScene(this.getScene());
    shopOperationTaskDTO.setShopId(this.getShopId());
    shopOperationTaskDTO.setExeStatus(this.getExeStatus());
    return shopOperationTaskDTO;
  }

  public void fromDTO(ShopOperationTaskDTO shopOperationTaskDTO){
    this.setId(shopOperationTaskDTO.getId());
    this.setCreateTime(shopOperationTaskDTO.getCreateTime());
    this.setExecuteTime(shopOperationTaskDTO.getExecuteTime());
    this.setScene(shopOperationTaskDTO.getScene());
    this.setShopId(shopOperationTaskDTO.getShopId());
    this.setExeStatus(shopOperationTaskDTO.getExeStatus());
  }
}
