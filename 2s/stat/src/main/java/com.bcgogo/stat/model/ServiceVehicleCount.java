package com.bcgogo.stat.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.ServiceVehicleCountDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User:zhangchuanlong
 * Date: 12-2-9
 * Time: 下午5:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "service_vehicle_count")
public class ServiceVehicleCount extends LongIdentifier {

    private Long shopId;
    private Long count;
    private Long serviceTime;
    public ServiceVehicleCount(){};
    public ServiceVehicleCount(ServiceVehicleCountDTO svcDTO)
    {
        this.setShopId(svcDTO.getShopId());
        this.setServiceTime(svcDTO.getServiceTime());
        this.setCount(svcDTO.getCount());
    }

    @Column(name = "shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Column(name = "service_time")
    public Long getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(Long serviceTime) {
        this.serviceTime = serviceTime;
    }

    @Column(name = "count")
    public Long getCount() {
        return count;
    }
    public void setCount(Long count) {
        this.count = count;
    }
}
