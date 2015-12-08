package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DictionaryFaultInfoDTO;

/**
 * User: ZhangJie
 * Date: 15-05-04
 * Time: 下午3:01
 */
public class FaultCodeResponse extends ApiResponse {
  private DictionaryFaultInfoDTO dictionaryFaultInfoDTO = new DictionaryFaultInfoDTO();

  public FaultCodeResponse() {
    super();
  }

  public FaultCodeResponse(ApiResponse response) {
    super(response);
  }

  public DictionaryFaultInfoDTO getDictionaryFaultInfoDTO() {
    return dictionaryFaultInfoDTO;
  }

  public void setDictionaryFaultInfoDTO(DictionaryFaultInfoDTO dictionaryFaultInfoDTO) {
    this.dictionaryFaultInfoDTO = dictionaryFaultInfoDTO;
  }
}
