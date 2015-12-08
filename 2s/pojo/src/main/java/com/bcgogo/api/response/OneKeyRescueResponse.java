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
public class OneKeyRescueResponse extends ApiResponse {
  private List<InsuranceCompanyDTO> insuranceCompanyDTOs = new ArrayList<InsuranceCompanyDTO>();
  private InsuranceCompanyDTO insuranceCompanyDTO = new InsuranceCompanyDTO();
  private List<String> accident_mobile;
  private String mirror_mobile;

  public OneKeyRescueResponse() {
    super();
  }

  public OneKeyRescueResponse(ApiResponse response) {
    super(response);
  }

  public List<InsuranceCompanyDTO> getInsuranceCompanyDTOs() {
    return insuranceCompanyDTOs;
  }

  public void setInsuranceCompanyDTOs(List<InsuranceCompanyDTO> insuranceCompanyDTOs) {
    this.insuranceCompanyDTOs = insuranceCompanyDTOs;
  }

  public List<String> getAccident_mobile() {
    return accident_mobile;
  }

  public void setAccident_mobile(List<String> accident_mobile) {
    this.accident_mobile = accident_mobile;
  }

  public String getMirror_mobile() {
    return mirror_mobile;
  }

  public void setMirror_mobile(String mirror_mobile) {
    this.mirror_mobile = mirror_mobile;
  }

  public InsuranceCompanyDTO getInsuranceCompanyDTO() {
    return insuranceCompanyDTO;
  }

  public void setInsuranceCompanyDTO(InsuranceCompanyDTO insuranceCompanyDTO) {
    this.insuranceCompanyDTO = insuranceCompanyDTO;
  }
}
