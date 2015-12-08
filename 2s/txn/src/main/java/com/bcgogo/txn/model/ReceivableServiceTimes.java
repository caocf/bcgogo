package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "receivable_service_times")
public class ReceivableServiceTimes extends LongIdentifier{
    private Long receivableId;
    private Long serviceId;
    private Integer times;
    private Double originAmount;
    private Long shopId;


    @Column(name="shop_id")
		public Long getShopId() {
			return shopId;
		}

		public void setShopId(Long shopId) {
			this.shopId = shopId;
		}

    @Column(name="receivable_id")
    public Long getReceivableId() {
        return receivableId;
    }
    @Column(name="service_id")
    public Long getServiceId() {
        return serviceId;
    }
    @Column(name="times")
    public Integer getTimes() {
        return times;
    }
    @Column(name="origin_amount")
    public Double getOriginAmount() {
        return originAmount;
    }

    public void setReceivableId(Long receivableId) {
        this.receivableId = receivableId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public void setOriginAmount(Double originAmount) {
        this.originAmount = originAmount;
    }
}
