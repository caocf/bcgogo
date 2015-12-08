package com.bcgogo.user.model.app;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.app.TaskType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.AppUserCustomerUpdateTaskDTO;

import javax.persistence.*;

/**
 * 手机端用户、客户资料变更 生成task
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-9
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "app_user_customer_update_task")
public class AppUserCustomerUpdateTask extends LongIdentifier {
  private Long operatorId;
  private OperatorType operatorType;
  private ExeStatus exeStatus;
  private Long createTime;
  private Long exeTime;
  private TaskType taskType;

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }


  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "operator_type")
  @Enumerated(EnumType.STRING)
  public OperatorType getOperatorType() {
    return operatorType;
  }

  public void setOperatorType(OperatorType operatorType) {
    this.operatorType = operatorType;
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
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name = "exe_time")
  public Long getExeTime() {
    return exeTime;
  }

  public void setExeTime(Long exeTime) {
    this.exeTime = exeTime;
  }

  @Column(name = "task_type")
  @Enumerated(EnumType.STRING)
  public TaskType getTaskType() {
    return taskType;
  }

  public void setTaskType(TaskType taskType) {
    this.taskType = taskType;
  }

  public AppUserCustomerUpdateTaskDTO toDTO(){
    AppUserCustomerUpdateTaskDTO dto = new AppUserCustomerUpdateTaskDTO();
    dto.setId(getId());
    dto.setCreateTime(getCreateTime());
    dto.setExeStatus(getExeStatus());
    dto.setExeTime(getExeTime());
    dto.setOperatorId(getOperatorId());
    dto.setOperatorType(getOperatorType());
    return dto;
  }
}
