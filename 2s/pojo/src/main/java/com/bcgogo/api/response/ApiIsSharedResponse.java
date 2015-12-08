package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/20
 * Time: 9:33.
 */
public class ApiIsSharedResponse extends ApiResponse{

    public ApiIsSharedResponse () {
        super();
    }

    public ApiIsSharedResponse (ApiResponse apiResponse){
        super(apiResponse);
    }

    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
