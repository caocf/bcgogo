package com.bcgogo.api;

import com.bcgogo.config.dto.ShopsDTO;
import com.bcgogo.enums.app.MessageCode;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/9
 * Time: 19:15.
 */
public class ApiShopsDTO extends ApiResponse {

    public ApiShopsDTO() {
        super();
    }

    public ApiShopsDTO(ApiResponse response) {
        super(response);
    }

    private List<ShopsDTO> data;

    public List<ShopsDTO> getData() {
        return data;
    }

    public void setData(List<ShopsDTO> data) {
        this.data = data;
    }
}
