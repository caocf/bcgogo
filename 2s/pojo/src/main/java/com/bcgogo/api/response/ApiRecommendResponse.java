package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/20
 * Time: 9:31.
 */
public class ApiRecommendResponse extends ApiResponse {

    public ApiRecommendResponse () {
        super();
    }

    public ApiRecommendResponse (ApiResponse apiResponse){
        super(apiResponse);
    }

    private long data;

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }
}
