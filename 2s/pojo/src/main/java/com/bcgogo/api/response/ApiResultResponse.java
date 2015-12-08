package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;

/**
 * User: ZhangJuntao
 * Date: 13-10-17
 * Time: 上午11:23
 */
public class ApiResultResponse<T> extends ApiResponse {
  private T result;

  public ApiResultResponse() {
    super();
  }

  public ApiResultResponse(ApiResponse response) {
    super(response);
  }

  public ApiResultResponse(ApiResponse response, T result) {
    super(response);
    setResult(result);
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }
}
