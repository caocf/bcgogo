package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.dto.InsuranceCompanyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJie
 * Date: 15-04-23
 * Time: 下午3:01
 */
public class InsuranceCompanyResponse extends ApiResponse {
  private List<InsuranceCompanyDTO> insuranceCompanyDTOs = new ArrayList<InsuranceCompanyDTO>();

  public InsuranceCompanyResponse() {
    super();
  }

  public InsuranceCompanyResponse(ApiResponse response) {
    super(response);
  }

  public List<InsuranceCompanyDTO> getInsuranceCompanyDTOs() {
    return insuranceCompanyDTOs;
  }

  public void setInsuranceCompanyDTOs(List<InsuranceCompanyDTO> insuranceCompanyDTOs) {
    this.insuranceCompanyDTOs = insuranceCompanyDTOs;
  }
}
