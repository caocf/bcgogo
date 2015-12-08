package com.bcgogo.user.permission;

import com.bcgogo.user.dto.permission.RoleDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 * 返回给前台的 module
 */
public class RoleResult {
  private List<RoleDTO> results = new ArrayList<RoleDTO>();
  private long totalRows = 0;
  private boolean success = true;

  public List<RoleDTO> getResults() {
    return results;
  }

  public void setResults(List<RoleDTO> results) {
    if (results != null) this.setTotalRows(results.size());
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
