package com.bcgogo.user.dto;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.app.TaskType;
import com.bcgogo.enums.notification.OperatorType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-5
 * Time: 上午11:12
 */
public class AppUserCustomerUpdateTaskDTO implements Serializable {
  private Long id;
  private Long operatorId;
  private OperatorType operatorType;
  private ExeStatus exeStatus;
  private Long createTime;
  private Long exeTime;
  private TaskType taskType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public OperatorType getOperatorType() {
    return operatorType;
  }

  public void setOperatorType(OperatorType operatorType) {
    this.operatorType = operatorType;
  }

  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getExeTime() {
    return exeTime;
  }

  public void setExeTime(Long exeTime) {
    this.exeTime = exeTime;
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public void setTaskType(TaskType taskType) {
    this.taskType = taskType;
  }
}
