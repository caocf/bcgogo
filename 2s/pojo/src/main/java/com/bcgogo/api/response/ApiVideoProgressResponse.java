package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.enums.app.MessageCode;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-20
 * Time: 16:19
 */
public class ApiVideoProgressResponse extends ApiResponse {
  public String percentage;

  public ApiVideoProgressResponse() {
    setMessageCode(MessageCode.SUCCESS);
  }

}
