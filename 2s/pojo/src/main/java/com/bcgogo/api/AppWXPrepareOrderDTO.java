package com.bcgogo.api;

import com.bcgogo.appPay.wxPay.WXPrepareOrder;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/30
 * Time: 9:55.
 */
public class AppWXPrepareOrderDTO extends ApiResponse{

    public AppWXPrepareOrderDTO(){
        super();
    }

    public AppWXPrepareOrderDTO(ApiResponse apiResponse){
        super(apiResponse);
    }

    private WXPrepareOrder data;

    public WXPrepareOrder getData() {
        return data;
    }

    public void setData(WXPrepareOrder data) {
        this.data = data;
    }
}
