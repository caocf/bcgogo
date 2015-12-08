package com.bcgogo.config;

import com.bcgogo.config.dto.CRMOperationLogDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
public class CRMOperationLogResult {
  private List<CRMOperationLogDTO> results = new ArrayList<CRMOperationLogDTO>();
  private boolean success=true;
  private long totalRows;

  public List<CRMOperationLogDTO> getResults() {
    return results;
  }

  public void setResults(List<CRMOperationLogDTO> results) {
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
