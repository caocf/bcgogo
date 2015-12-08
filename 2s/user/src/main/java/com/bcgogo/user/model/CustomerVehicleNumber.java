package com.bcgogo.user.model;

import com.bcgogo.user.dto.CustomerVehicleNumberDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-1
 * Time: 上午10:31
 * To change this template use File | Settings | File Templates.
 */
public class CustomerVehicleNumber {

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

    public CustomerVehicleNumberDTO toDTO(){
        CustomerVehicleNumberDTO customerVehicleNumberDTO = new CustomerVehicleNumberDTO();
        customerVehicleNumberDTO.setCustomerId(this.customerId);
        customerVehicleNumberDTO.setCount(this.count);
        return customerVehicleNumberDTO;
    }
}
