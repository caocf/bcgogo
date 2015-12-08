package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.EnquiryDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 下午2:49
 */
public class EnquiryOrderDetailResponse extends ApiResponse {
  private EnquiryDTO enquiryDTO;

  public EnquiryDTO getEnquiryDTO() {
    return enquiryDTO;
  }

  public void setEnquiryDTO(EnquiryDTO enquiryDTO) {
    this.enquiryDTO = enquiryDTO;
  }
}
