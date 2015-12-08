package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveStatDTO;

import java.util.List;

/**
 * Created by XinyuQiu on 14-4-30.
 */
public class ApiDriveStatResponse extends ApiResponse {
  private DriveStatDTO yearStat;
  private List<DriveStatDTO> monthStats;

  public ApiDriveStatResponse() {
    super();
  }

  public ApiDriveStatResponse(ApiResponse response) {
    super(response);
  }

  public DriveStatDTO getYearStat() {
    return yearStat;
  }

  public void setYearStat(DriveStatDTO yearStat) {
    this.yearStat = yearStat;
  }

  public List<DriveStatDTO> getMonthStats() {
    return monthStats;
  }

  public void setMonthStats(List<DriveStatDTO> monthStats) {
    this.monthStats = monthStats;
  }
}
