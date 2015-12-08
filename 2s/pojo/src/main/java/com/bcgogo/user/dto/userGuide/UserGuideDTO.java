package com.bcgogo.user.dto.userGuide;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-5
 * Time: 上午10:08
 */
public class UserGuideDTO {
  private UserGuideFlowDTO currentFlow;
  private UserGuideStepDTO currentStep;
  private UserGuideStepDTO nextStep;
  private List<UserGuideStepDTO> nextStepList;
  private boolean validatorSuccess = true;
  private boolean isContinueGuide = false;
  private long validatorCount = 1;

  public UserGuideFlowDTO getCurrentFlow() {
    return currentFlow;
  }

  public void setCurrentFlow(UserGuideFlowDTO currentFlow) {
    this.currentFlow = currentFlow;
  }

  public UserGuideStepDTO getCurrentStep() {
    return currentStep;
  }

  public void setCurrentStep(UserGuideStepDTO currentStep) {
    this.currentStep = currentStep;
  }

  public UserGuideStepDTO getNextStep() {
    return nextStep;
  }

  public void setNextStep(UserGuideStepDTO nextStep) {
    this.nextStep = nextStep;
  }

  public List<UserGuideStepDTO> getNextStepList() {
    return nextStepList;
  }

  public void setNextStepList(List<UserGuideStepDTO> nextStepList) {
    this.nextStepList = nextStepList;
  }

  public boolean isValidatorSuccess() {
    return validatorSuccess;
  }

  public void setValidatorSuccess(boolean validatorSuccess) {
    this.validatorSuccess = validatorSuccess;
  }

  public boolean isContinueGuide() {
    return isContinueGuide;
  }

  public void setContinueGuide(boolean continueGuide) {
    isContinueGuide = continueGuide;
  }

  public long getValidatorCount() {
    return validatorCount;
  }

  public void setValidatorCount(long validatorCount) {
    this.validatorCount = validatorCount;
  }
}
