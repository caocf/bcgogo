package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.user.dto.CouponDTO;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/12
 * Time: 9:14.
 */
public class ApiCouponResponse extends ApiResponse {

    public ApiCouponResponse (){
        super();
    }

    public ApiCouponResponse(ApiResponse apiResponse){
        super(apiResponse);
    }

    private double data;

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
