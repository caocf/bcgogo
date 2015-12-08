package com.bcgogo.client;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午1:33
 */
public class FeedbackResult {
  private String isSuccess;           //是否成功（true|false）

  public FeedbackResult() {
    super();
  }

  public FeedbackResult(String result) {
    isSuccess = result;
  }

  public String getIsSuccess() {
    return isSuccess;
  }

  public void setIsSuccess(String isSuccess) {
    this.isSuccess = isSuccess;
  }
}
