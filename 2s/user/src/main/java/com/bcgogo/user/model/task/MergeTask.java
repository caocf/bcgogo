package com.bcgogo.user.model.task;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.MergeType;

import javax.persistence.*;

/**
 * 定时钟执行的任务表，批量执行，执行完后删除
 * User: ndong
 * Date: 12-11-13
 * Time: 上午8:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "merge_task")
public class MergeTask  extends LongIdentifier {

  private Long shopId;
  private Long childId;    //customerId;
  private Long parentId;
  private ExeStatus exeStatus;
  private MergeType mergeType;
  private Long createdTime;



  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "created_time")
  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Column(name = "child_id")
  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "exe_status")
  @Enumerated(EnumType.STRING)
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

    @Column(name = "merge_type")
  @Enumerated(EnumType.STRING)
  public MergeType getMergeType() {
    return mergeType;
  }

  public void setMergeType(MergeType mergeType) {
    this.mergeType = mergeType;
  }




  public static MergeTask createTask(Long shopId,Long parentId,Long childId,MergeType type){
    MergeTask task=new MergeTask();
    task.setShopId(shopId);
    task.setParentId(parentId);
    task.setChildId(childId);
    task.setExeStatus(ExeStatus.READY);
    task.setMergeType(type);
    task.setCreatedTime(System.currentTimeMillis());
    return task;
  }
}
