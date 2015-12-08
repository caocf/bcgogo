package com.bcgogo.config;

import com.bcgogo.config.dto.ShopDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-4
 * Time: 下午11:59
 */
public class ShopSearchResult {
  private List<ShopDTO> results = new ArrayList<ShopDTO>();
  private long totals = 0;
  private boolean success = true;

  public List<ShopDTO> getResults() {
    return results;
  }

  public void setResults(List<ShopDTO> results) {
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
