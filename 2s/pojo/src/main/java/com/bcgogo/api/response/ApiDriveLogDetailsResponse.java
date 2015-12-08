package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveLogDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-23
 * Time: 上午10:53
 */
public class ApiDriveLogDetailsResponse extends ApiResponse {

  private List<DriveLogDTO> contentDriveLogs;
  private List<DriveLogDTO> detailDriveLogs;

  public ApiDriveLogDetailsResponse() {
    super();
  }

  public ApiDriveLogDetailsResponse(ApiResponse response) {
    super(response);
  }



  public ApiDriveLogDetailsResponse(ApiResponse apiResponse,
                                    List<DriveLogDTO> contentDriveLogDTOs,
                                    List<DriveLogDTO> detailDriveLogDTOs) {
    super(apiResponse);
    setContentDriveLogs(contentDriveLogDTOs);
    setDetailDriveLogs(detailDriveLogDTOs);

  }

  public List<DriveLogDTO> getContentDriveLogs() {
    return contentDriveLogs;
  }

  public void setContentDriveLogs(List<DriveLogDTO> contentDriveLogs) {
    this.contentDriveLogs = contentDriveLogs;
  }

  public List<DriveLogDTO> getDetailDriveLogs() {
    return detailDriveLogs;
  }

  public void setDetailDriveLogs(List<DriveLogDTO> detailDriveLogs) {
    this.detailDriveLogs = detailDriveLogs;
  }
}
