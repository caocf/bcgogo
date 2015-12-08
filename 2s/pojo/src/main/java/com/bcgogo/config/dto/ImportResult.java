package com.bcgogo.config.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */
public class ImportResult {

    private boolean isSuccess = false;
    private Integer successCount;
    private Integer failCount;
    private Integer totalCount;
    private String message;

  public ImportResult() {
    super();
  }

  public ImportResult(String message) {
    this.message = message;
  }

  public boolean isSuccess() {
        return isSuccess;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
      if(this.successCount > 0){
        this.isSuccess = true;
      }
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
