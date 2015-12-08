package com.bcgogo.user.permission;

import com.bcgogo.user.dto.permission.UserGroupDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午3:07
 */
public class UserGroupSearchResult {
  private List<UserGroupDTO> results = new ArrayList<UserGroupDTO>();
  private long totalRows = 0;
  private Long countSystemDefault;
  private Long countCustom;
  private boolean success = true;

  public Long getCountCustom() {
    return countCustom;
  }

  public void setCountCustom(Long countCustom) {
    this.countCustom = countCustom;
  }

  public Long getCountSystemDefault() {

    return countSystemDefault;
  }

  public void setCountSystemDefault(Long countSystemDefault) {
    this.countSystemDefault = countSystemDefault;
  }

  public List<UserGroupDTO> getResults() {
    return results;
  }

  public void setResults(List<UserGroupDTO> results) {
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
