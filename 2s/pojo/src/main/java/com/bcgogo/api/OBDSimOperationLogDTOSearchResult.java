package com.bcgogo.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-7-7.
 */
public class OBDSimOperationLogDTOSearchResult {

  private List<OBDSimOperationLogDTO> results = new ArrayList<OBDSimOperationLogDTO>();
  private long totals = 0;
  private boolean success = true;

  public List<OBDSimOperationLogDTO> getResults() {
    return results;
  }

  public void setResults(List<OBDSimOperationLogDTO> results) {
    this.results = results;
  }

  public long getTotals() {
    return totals;
  }

  public void setTotals(long totals) {
    this.totals = totals;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
