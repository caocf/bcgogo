package com.bcgogo.common;

import java.util.List;

/**
 * List结果返回类
 * User: Jimuchen
 * Date: 13-1-10
 * Time: 上午9:19
 */
public abstract class ListResult<T> extends Result{
  protected List<T> results;

  public ListResult() {
  }

  public ListResult(List<T> results, boolean success) {
    super(success);
    this.results = results;
  }

  public List<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }
}
