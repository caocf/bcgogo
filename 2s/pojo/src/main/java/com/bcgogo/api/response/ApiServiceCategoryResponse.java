package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.config.dto.ServiceCategoryDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-13
 * Time: 下午2:53
 * To change this template use File | Settings | File Templates.
 */
public class ApiServiceCategoryResponse extends ApiResponse {
  List<ServiceCategoryDTO> serviceCategoryDTOList = new ArrayList<ServiceCategoryDTO>();

  public ApiServiceCategoryResponse() {
    super();
  }

  public ApiServiceCategoryResponse(ApiResponse response) {
    super(response);
  }


  public List<ServiceCategoryDTO> getServiceCategoryDTOList() {
    return serviceCategoryDTOList;
  }

  public void setServiceCategoryDTOList(List<ServiceCategoryDTO> serviceCategoryDTOList) {
    this.serviceCategoryDTOList = serviceCategoryDTOList;
  }
}
