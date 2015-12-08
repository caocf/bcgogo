package com.bcgogo.api;

import com.bcgogo.config.dto.TrafficPackageDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/12
 * Time: 16:19.
 */
public class ApiTrafficPackage extends ApiResponse{

    public ApiTrafficPackage() {
        super();
    }

    public ApiTrafficPackage(ApiResponse response) {
        super(response);
    }

    private List<TrafficPackageDTO> data;

    public List<TrafficPackageDTO> getData() {
        return data;
    }

    public void setData(List<TrafficPackageDTO> data) {
        this.data = data;
    }
}
