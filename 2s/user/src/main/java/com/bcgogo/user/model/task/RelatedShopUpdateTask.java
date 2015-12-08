package com.bcgogo.user.model.task;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.RelatedShopUpdateTaskDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午2:48
 */
@Entity
@Table(name = "relation_shop_update_task")
public class RelatedShopUpdateTask extends LongIdentifier {
  private Long shopId;
  private ExeStatus exeStatus;
  private Long createdTime;
  private Long finishTime;


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "exe_status")
  @Enumerated(EnumType.STRING)
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  @Column(name = "created_time")
  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Column(name = "finish_time")
  public Long getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Long finishTime) {
    this.finishTime = finishTime;
  }

  public void fromDTO(RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO) {
    if(relatedShopUpdateTaskDTO != null){
      setId(relatedShopUpdateTaskDTO.getId());
      setShopId(relatedShopUpdateTaskDTO.getShopId());
      setCreatedTime(relatedShopUpdateTaskDTO.getCreatedTime());
      setExeStatus(relatedShopUpdateTaskDTO.getExeStatus());
      setFinishTime(relatedShopUpdateTaskDTO.getFinishTime());
    }
  }

  public RelatedShopUpdateTaskDTO toDTO() {
    RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO = new RelatedShopUpdateTaskDTO();
    relatedShopUpdateTaskDTO.setShopId(this.getShopId());
    relatedShopUpdateTaskDTO.setCreatedTime(this.getCreatedTime());
    relatedShopUpdateTaskDTO.setExeStatus(this.getExeStatus());
    relatedShopUpdateTaskDTO.setId(this.getId());
    relatedShopUpdateTaskDTO.setFinishTime(this.getFinishTime());
    return relatedShopUpdateTaskDTO;
  }
}


