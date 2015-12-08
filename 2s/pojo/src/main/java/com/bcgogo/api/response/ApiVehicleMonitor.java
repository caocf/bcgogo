package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/10
 * Time: 14:58.
 */
public class ApiVehicleMonitor extends ApiResponse{

    public ApiVehicleMonitor() {
        super();
    }

    public ApiVehicleMonitor(ApiResponse response) {
        super(response);
    }

    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
