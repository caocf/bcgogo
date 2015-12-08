package com.bcgogo.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-6-19.
 */
public class ObdSimSearchResult {

  private List<ObdSimBindDTO> results = new ArrayList<ObdSimBindDTO>();
  private long totals = 0;
  private boolean success = true;

  public List<ObdSimBindDTO> getResults() {
    return results;
  }

  public void setResults(List<ObdSimBindDTO> results) {
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
