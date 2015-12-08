package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.common.Pager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-18
 * Time: 上午10:34
 */
public class EnquiryListResponse extends ApiResponse {
  private List<EnquiryDTO> result;
  private Pager pager;

  public EnquiryListResponse() {
     super();
   }

  public EnquiryListResponse(ApiResponse response) {
    super(response);
  }

  public List<EnquiryDTO> getResult() {
    return result;
  }

  public void setResult(List<EnquiryDTO> result) {
    this.result = result;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }
}
