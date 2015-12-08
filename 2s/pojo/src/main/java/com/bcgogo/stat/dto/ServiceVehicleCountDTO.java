package com.bcgogo.stat.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: 001
 * Date: 12-2-9
 * Time: 下午7:32
 * To change this template use File | Settings | File Templates.
 */
public class ServiceVehicleCountDTO implements Serializable {

    private Long shopId;
    private Long count;
    private Long serviceTime;
    public ServiceVehicleCountDTO(){}
    public ServiceVehicleCountDTO(Long shopId,Long serviceTime,Long count)
    {
        this.shopId=shopId;
        this.count=count;
        this.serviceTime=serviceTime;
    }
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(Long serviceTime) {
        this.serviceTime = serviceTime;
    }


}
