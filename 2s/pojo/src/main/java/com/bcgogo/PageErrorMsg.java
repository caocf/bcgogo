package com.bcgogo;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-24
 * Time: 下午10:08
 */
public class PageErrorMsg {
  private String level;   //info,error
  private String errorMsg;
  private String prompt;

  public PageErrorMsg() {

  }

  public PageErrorMsg(String errorMsg, String prompt) {
    this("info", errorMsg, prompt);
  }

  public PageErrorMsg(String level, String errorMsg, String prompt) {
    this.level = level;
    this.errorMsg = errorMsg;
    this.prompt = prompt;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }
}
