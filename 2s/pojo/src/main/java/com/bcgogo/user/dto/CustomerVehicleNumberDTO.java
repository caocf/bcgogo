package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-1
 * Time: 上午10:33
 * To change this template use File | Settings | File Templates.
 */
public class CustomerVehicleNumberDTO {

    private Long customerId;
    private Integer count;

    public Long getCustomerId() {
        return customerId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
