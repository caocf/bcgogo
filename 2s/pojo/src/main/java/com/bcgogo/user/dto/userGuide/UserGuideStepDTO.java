package com.bcgogo.user.dto.userGuide;

import com.bcgogo.BooleanEnum;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 上午10:53
 */
public class UserGuideStepDTO implements Serializable {
  private Long id;
  private String name;
  private String flowName;
  private String previousStep;
  private String nextStep; //有多个
  private BooleanEnum isHead;
  private BooleanEnum isTail;
  private String message;
  private String url;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public String getPreviousStep() {
    return previousStep;
  }

  public void setPreviousStep(String previousStep) {
    this.previousStep = previousStep;
  }

  public String getNextStep() {
    return nextStep;
  }

  public void setNextStep(String nextStep) {
    this.nextStep = nextStep;
  }

  public BooleanEnum getHead() {
    return isHead;
  }

  public void setHead(BooleanEnum head) {
    isHead = head;
  }

  public BooleanEnum getTail() {
    return isTail;
  }

  public void setTail(BooleanEnum tail) {
    isTail = tail;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "UserGuideStepDTO{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", flowName='" + flowName + '\'' +
        ", previousStep='" + previousStep + '\'' +
        ", nextStep='" + nextStep + '\'' +
        ", isHead=" + isHead +
        ", isTail=" + isTail +
        ", message='" + message + '\'' +
        '}';
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
