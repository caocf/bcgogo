package com.bcgogo.user.permission;

import com.bcgogo.user.dto.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-25
 * Time: 上午11:48
 * 返回给页面的部门信息
 */
public class DepartmentResponse {
  private List<Node> results = new ArrayList<Node>();
  private long totalRows = 0;
  private boolean success = true;

  public List<Node> getResults() {
    return results;
  }

  public void setResults(List<Node> results) {
    if (results != null) {
      totalRows = results.size();
    }
    this.results = results;
  }

  public long getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(long totalRows) {
    this.totalRows = totalRows;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
