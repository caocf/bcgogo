package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午6:47
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="reception_service_times")
public class ReceptionServiceTimes extends LongIdentifier{
    private Long receptionRecordId;
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

    @Column(name="reception_record_id")
    public Long getReceptionRecordId() {
        return receptionRecordId;
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

    public void setReceptionRecordId(Long receptionRecordId) {
        this.receptionRecordId = receptionRecordId;
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
