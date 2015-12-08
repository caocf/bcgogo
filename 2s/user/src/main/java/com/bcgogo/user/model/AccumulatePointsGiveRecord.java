package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午5:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="accumulate_points_give_record")
public class AccumulatePointsGiveRecord extends LongIdentifier{
    private Long shopId;
    private Long customerId;
    private Long memberId;
    private Integer amount;
    private Long executorId;

    @Column(name="shop_id")
    public Long getShopId() {
        return shopId;
    }
    @Column(name="customer_id")
    public Long getCustomerId() {
        return customerId;
    }
    @Column(name="member_id")
    public Long getMemberId() {
        return memberId;
    }

    @Column(name="amount")
    public Integer getAmount() {
        return amount;
    }
    @Column(name="executor_id")
    public Long getExecutorId() {
        return executorId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }
}
