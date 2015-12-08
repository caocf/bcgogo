package com.bcgogo.user.dto.userGuide;

import com.bcgogo.enums.user.userGuide.Status;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 上午10:52
 */
public class UserGuideHistoryDTO implements Serializable {
  private Long id;
  private String flowName;
  private Long userId;
  private Status status;
  private String currentStep;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getCurrentStep() {
    return currentStep;
  }

  public void setCurrentStep(String currentStep) {
    this.currentStep = currentStep;
  }
}
