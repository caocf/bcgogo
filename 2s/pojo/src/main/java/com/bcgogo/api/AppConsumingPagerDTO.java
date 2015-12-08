package com.bcgogo.api;

import com.bcgogo.user.dto.ConsumingPageDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/17
 * Time: 10:44.
 */
public class AppConsumingPagerDTO extends ApiResponse{

    public AppConsumingPagerDTO (){
        super();
    }

    public AppConsumingPagerDTO(ApiResponse apiResponse){
        super(apiResponse);
    }

    private List<ConsumingPageDTO> data;

    public List<ConsumingPageDTO> getData() {
        return data;
    }

    public void setData(List<ConsumingPageDTO> data) {
        this.data = data;
    }
}
