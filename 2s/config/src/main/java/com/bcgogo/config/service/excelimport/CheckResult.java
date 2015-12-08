package com.bcgogo.config.service.excelimport;

import com.bcgogo.utils.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * 数据校验结果
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-6
 * Time: 上午11:03
 * To change this template use File | Settings | File Templates.
 */
public class CheckResult {

  private boolean isPass;
  private String message;
  private List<Map<String, Object>> failDataList;

  public CheckResult() {
    isPass = true;
  }

  public boolean isPass() {
    return isPass;
  }

  public String getMessage() {
    return message;
  }

  public List<Map<String, Object>> getFailDataList() {
    return failDataList;
  }

  public void setPass(boolean pass) {
    isPass = pass;
  }

  public void setMessage(String message) {
    this.message = message;
    if(!StringUtil.isEmpty(this.message)){
      this.isPass = false;
    }else{
      this.isPass = true;
    }
  }

  public void setFailDataList(List<Map<String, Object>> failDataList) {
    this.failDataList = failDataList;
    if(this.failDataList == null || this.failDataList.isEmpty()){
      this.isPass = true;
    }else{
      this.isPass = false;
    }
  }
}
