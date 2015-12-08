package com.bcgogo.txn.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午6:55
 * To change this template use File | Settings | File Templates.
 */
public class ReceptionServiceTimesDTO {
    private Long id;
    private Long receptionRecordId;
    private Long serviceId;
    private Integer times;
    private Double originAmount;
    private Long shopId;

		public Long getShopId() {
			return shopId;
		}

		public void setShopId(Long shopId) {
			this.shopId = shopId;
		}

    public Long getId() {
        return id;
    }

    public Long getReceptionRecordId() {
        return receptionRecordId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Integer getTimes() {
        return times;
    }

    public Double getOriginAmount() {
        return originAmount;
    }

    public void setId(Long id) {
        this.id = id;
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

