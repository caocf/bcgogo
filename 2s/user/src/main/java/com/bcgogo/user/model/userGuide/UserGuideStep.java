package com.bcgogo.user.model.userGuide;

import com.bcgogo.BooleanEnum;
import com.bcgogo.enums.user.userGuide.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 上午9:33
 */
@Entity
@Table(name = "user_guide_step")
public class UserGuideStep extends LongIdentifier {
  private String name;
  private String flowName;
  private String previousStep;
  private String nextStep;
  private BooleanEnum isHead;
  private BooleanEnum isTail;
  private String message;
  private String url;

  public UserGuideStepDTO toDTO() {
    UserGuideStepDTO dto = new UserGuideStepDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setFlowName(this.getFlowName());
    dto.setPreviousStep(this.getPreviousStep());
    dto.setNextStep(this.getNextStep());
    dto.setHead(this.getHead());
    dto.setTail(this.getTail());
    dto.setMessage(this.getMessage());
    dto.setUrl(this.getUrl());
    return dto;
  }

  public void fromDTO(UserGuideStepDTO dto) {
    this.setId(dto.getId());
    this.setName(dto.getName());
    this.setFlowName(dto.getFlowName());
    this.setPreviousStep(dto.getPreviousStep());
    this.setNextStep(dto.getNextStep());
    this.setHead(dto.getHead());
    this.setTail(dto.getTail());
    this.setMessage(dto.getMessage());
    this.setUrl(dto.getUrl());
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "flow_name")
  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  @Column(name = "previous_step")
  public String getPreviousStep() {
    return previousStep;
  }

  public void setPreviousStep(String previousStep) {
    this.previousStep = previousStep;
  }

  @Column(name = "next_step")
  public String getNextStep() {
    return nextStep;
  }

  public void setNextStep(String nextStep) {
    this.nextStep = nextStep;
  }

  @Column(name = "is_head")
  public BooleanEnum getHead() {
    return isHead;
  }

  public void setHead(BooleanEnum head) {
    isHead = head;
  }

  @Column(name = "is_tail")
  public BooleanEnum getTail() {
    return isTail;
  }

  public void setTail(BooleanEnum tail) {
    isTail = tail;
  }


  @Column(name = "message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
