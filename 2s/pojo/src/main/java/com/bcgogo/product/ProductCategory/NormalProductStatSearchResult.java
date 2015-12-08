package com.bcgogo.product.ProductCategory;

import com.bcgogo.txn.dto.NormalProductInventoryStatDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-29
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class NormalProductStatSearchResult {
  private List<NormalProductInventoryStatDTO> results = new ArrayList<NormalProductInventoryStatDTO>();
  private long totalRows = 0;
  private boolean success = true;

  public List<NormalProductInventoryStatDTO> getResults() {
    return results;
  }

  public void setResults(List<NormalProductInventoryStatDTO> results) {
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
