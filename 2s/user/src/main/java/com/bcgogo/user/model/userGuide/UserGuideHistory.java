
package com.bcgogo.user.model.userGuide;

import com.bcgogo.enums.user.userGuide.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.userGuide.UserGuideHistoryDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 上午9:33
 */
@Entity
@Table(name = "user_guide_history")
public class UserGuideHistory extends LongIdentifier {
  private String flowName;
  private Long userId;
  private Status status;
  private String currentStep;

  public UserGuideHistoryDTO toDTO() {
    UserGuideHistoryDTO dto = new UserGuideHistoryDTO();
    dto.setId(this.getId());
    dto.setFlowName(this.getFlowName());
    dto.setUserId(this.getUserId());
    dto.setStatus(this.getStatus());
    dto.setCurrentStep(this.getCurrentStep());
    return dto;
  }

  public void fromDTO(UserGuideHistoryDTO dto) {
    this.setId(dto.getId());
    this.setFlowName(dto.getFlowName());
    this.setUserId(dto.getUserId());
    this.setStatus(dto.getStatus());
    this.setCurrentStep(dto.getCurrentStep());
  }

  @Column(name = "flow_name")
  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "current_step")
  public String getCurrentStep() {
    return currentStep;
  }

  public void setCurrentStep(String currentStep) {
    this.currentStep = currentStep;
  }
}
