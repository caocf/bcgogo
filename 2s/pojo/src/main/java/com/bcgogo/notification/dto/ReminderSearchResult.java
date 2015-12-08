package com.bcgogo.notification.dto;

import com.bcgogo.user.dto.ReminderDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-26
 * Time: 上午3:10
 * To change this template use File | Settings | File Templates.
 */
public class ReminderSearchResult {

  private List<ReminderDTO> results = new ArrayList<ReminderDTO>();
  private long totalRows = 0;
  private boolean success = true;

  public List<ReminderDTO> getResults() {
    return results;
  }

  public void setResults(List<ReminderDTO> results) {
    this.results = results;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public long getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(long totalRows) {
    this.totalRows = totalRows;
  }
}
