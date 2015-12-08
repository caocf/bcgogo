package com.bcgogo.api;

import com.bcgogo.user.dto.ConsumingDetailsDTO;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/18
 * Time: 10:47.
 */
public class AppConsumingDetailsDTO extends ApiResponse{

    public AppConsumingDetailsDTO (){
        super();
    }

    public AppConsumingDetailsDTO(ApiResponse apiResponse){
        super(apiResponse);
    }

    private ConsumingDetailsDTO data;

    public ConsumingDetailsDTO getData() {
        return data;
    }

    public void setData(ConsumingDetailsDTO data) {
        this.data = data;
    }
}
