package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.user.ImpactAndVideoDTO;

import java.util.List;

/**
 * Created by Administrator on 2015/10/29.
 */
public class ApiVehicleToVideoResponse extends ApiResponse {

    public ApiVehicleToVideoResponse() {
        setMessageCode(MessageCode.SUCCESS);
    }

    private List<ImpactAndVideoDTO> data;

    public List<ImpactAndVideoDTO> getData() {
        return data;
    }

    public void setData(List<ImpactAndVideoDTO> data) {
        this.data = data;
    }
}
