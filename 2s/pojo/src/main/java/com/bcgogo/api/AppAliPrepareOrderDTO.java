package com.bcgogo.api;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 14:31.
 */
public class AppAliPrepareOrderDTO extends ApiResponse{

    public AppAliPrepareOrderDTO (){
        super();
    }

    public AppAliPrepareOrderDTO(ApiResponse apiResponse){
        super(apiResponse);
    }

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
