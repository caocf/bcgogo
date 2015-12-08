package com.bcgogo.user.permission;

import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.ResourceDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 */
public class ResourceSearchResult {
  private List<ResourceDTO> results = new ArrayList<ResourceDTO>();
  private long totalRows = 0;
  private boolean success = true;

  public List<ResourceDTO> getResults() {
    return results;
  }

  public void setResults(List<ResourceDTO> results) {
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
