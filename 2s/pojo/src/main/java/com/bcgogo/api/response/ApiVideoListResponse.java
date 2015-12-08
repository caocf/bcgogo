package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.user.ImpactVideoDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-9
 * Time: 10:41
 */
public class ApiVideoListResponse extends ApiResponse {

  public ApiVideoListResponse() {
    setMessageCode(MessageCode.SUCCESS);
  }

  private List<ImpactVideoDTO> videoDTOs;

  public List<ImpactVideoDTO> getVideoDTOs() {
    return videoDTOs;
  }

  public void setVideoDTOs(List<ImpactVideoDTO> videoDTOs) {
    this.videoDTOs = videoDTOs;
  }
}
