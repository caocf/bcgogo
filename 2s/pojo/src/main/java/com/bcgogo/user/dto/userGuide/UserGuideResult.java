package com.bcgogo.user.dto.userGuide;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: hans
 * Date: 13-3-11
 * Time: 下午2:17
 */
public class UserGuideResult<T> implements Serializable {
  private boolean validatorSuccess = true;
  private boolean isContinueGuide = false;
  private long validatorCount = 1;
  private T guide;

  public UserGuideResult() {
    super();
  }

  public UserGuideResult(boolean validatorSuccess) {
    this.validatorSuccess = validatorSuccess;
  }

  public UserGuideResult(boolean validatorSuccess, boolean isContinueGuide, long validatorCount, T guide) {
    this.validatorSuccess = validatorSuccess;
    this.isContinueGuide = isContinueGuide;
    this.validatorCount = validatorCount;
    this.guide = guide;
  }

  public void countIncreasing() {
    validatorCount++;
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

  public T getGuide() {
    return guide;
  }

  public void setGuide(T guide) {
    this.guide = guide;
  }
}
