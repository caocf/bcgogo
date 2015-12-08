package com.bcgogo.common;

import java.util.List;

/**
 * 全部List返回类（非分页）
 * User: Jimuchen
 * Date: 13-1-10
 * Time: 上午9:28
 */
public class AllListResult<T> extends ListResult<T> {
  protected int totalRows;

  public AllListResult() {
  }

  public AllListResult(List<T> results, boolean success, int totalRows) {
    super(results, success);
    this.totalRows = totalRows;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }
}
