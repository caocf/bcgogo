package com.bcgogo.api;

import com.bcgogo.user.dto.ConsumingDTO;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/16
 * Time: 16:17.
 */
public class AppConsumingDTO extends ApiResponse{

    public AppConsumingDTO () {
        super();
    }

    public AppConsumingDTO (ApiResponse apiResponse){
        super(apiResponse);
    }

    private ConsumingDTO data;

    public ConsumingDTO getData() {
        return data;
    }

    public void setData(ConsumingDTO data) {
        this.data = data;
    }
}
