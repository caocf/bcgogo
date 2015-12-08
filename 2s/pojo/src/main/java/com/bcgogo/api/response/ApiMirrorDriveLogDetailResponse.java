package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveLogDTO;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-12
 * Time: 16:24
 */
public class ApiMirrorDriveLogDetailResponse extends ApiResponse {
  private DriveLogDTO driveLogDTO;

  public ApiMirrorDriveLogDetailResponse(DriveLogDTO driveLogDTO) {
    this.driveLogDTO = driveLogDTO;
  }

  public DriveLogDTO getDriveLogDTO() {
    return driveLogDTO;
  }

  public void setDriveLogDTO(DriveLogDTO driveLogDTO) {
    this.driveLogDTO = driveLogDTO;
  }
}
