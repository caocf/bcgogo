package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.common.Pager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-10
 * Time: 上午9:46
 */
public class ApiPageListResponse<T> extends ApiResponse {
  private Pager pager;
  private List<T> results;


  public ApiPageListResponse(ApiResponse response) {
    super(response);
  }

  public ApiPageListResponse(ApiResponse response, List<T> results) {
    super(response);
    setResults(results);
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public List<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }
}
