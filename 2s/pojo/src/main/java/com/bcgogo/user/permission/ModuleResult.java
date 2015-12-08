package com.bcgogo.user.permission;

import com.bcgogo.user.dto.permission.ModuleDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 * 返回给前台的 module
 */
public class ModuleResult {
  private List<ModuleDTO> results = new ArrayList<ModuleDTO>();
  private long totalRows = 0;
  private boolean success = true;

  public List<ModuleDTO> getResults() {
    return results;
  }

  public void setResults(List<ModuleDTO> results) {
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
