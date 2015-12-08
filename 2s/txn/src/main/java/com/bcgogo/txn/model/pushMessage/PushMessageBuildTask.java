package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-13
 * Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "push_message_build_task")
public class PushMessageBuildTask  extends LongIdentifier {
  private Long shopId;
  private Long seedId;
  private PushMessageScene scene;
  private Long createTime;
  private ExeStatus exeStatus;
  private Long executeTime;

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
  @Column(name="scene")
  @Enumerated(EnumType.STRING)
  public PushMessageScene getScene() {
    return scene;
  }

  public void setScene(PushMessageScene scene) {
    this.scene = scene;
  }

  @Column(name="create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name="exe_status")
  @Enumerated(EnumType.STRING)
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  @Column(name="execute_time")
  public Long getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(Long executeTime) {
    this.executeTime = executeTime;
  }
  @Column(name="seed_id")
  public Long getSeedId() {
    return seedId;
  }

  public void setSeedId(Long seedId) {
    this.seedId = seedId;
  }

  public PushMessageBuildTaskDTO toDTO(){
    PushMessageBuildTaskDTO pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
    pushMessageBuildTaskDTO.setId(this.getId());
    pushMessageBuildTaskDTO.setCreateTime(this.getCreateTime());
    pushMessageBuildTaskDTO.setExecuteTime(this.getExecuteTime());
    pushMessageBuildTaskDTO.setExeStatus(this.getExeStatus());
    pushMessageBuildTaskDTO.setScene(this.getScene());
    pushMessageBuildTaskDTO.setSeedId(this.getSeedId());
    pushMessageBuildTaskDTO.setShopId(this.getShopId());
    return pushMessageBuildTaskDTO;
  }

  public void fromDTO(PushMessageBuildTaskDTO pushMessageBuildTaskDTO){
    this.setCreateTime(pushMessageBuildTaskDTO.getCreateTime());
    this.setExecuteTime(pushMessageBuildTaskDTO.getExecuteTime());
    this.setExeStatus(pushMessageBuildTaskDTO.getExeStatus());
    this.setScene(pushMessageBuildTaskDTO.getScene());
    this.setSeedId(pushMessageBuildTaskDTO.getSeedId());
    this.setShopId(pushMessageBuildTaskDTO.getShopId());
  }
}
