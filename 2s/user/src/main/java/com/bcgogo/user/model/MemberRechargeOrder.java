package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="member_recharge_order")
public class MemberRechargeOrder extends LongIdentifier{
    private Long shopId;
    private Long customerId;
    private Long memberId;
    private Double payAmount;
    private Integer rechargeAmount;
    private Double OldBalance;
    private Double newBalance;
    private Integer accumulatePoints;

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
    @Column(name="pay_amount")
    public Double getPayAmount() {
        return payAmount;
    }
    @Column(name="recharge_amount")
    public Integer getRechargeAmount() {
        return rechargeAmount;
    }
    @Column(name="old_balance")
    public Double getOldBalance() {
        return OldBalance;
    }
    @Column(name="new_balance")
    public Double getNewBalance() {
        return newBalance;
    }
    @Column(name="accumulate_points")
    public Integer getAccumulatePoints() {
        return accumulatePoints;
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

    public void setRechargeAmount(Integer rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public void setOldBalance(Double oldBalance) {
        OldBalance = oldBalance;
    }

    public void setNewBalance(Double newBalance) {
        this.newBalance = newBalance;
    }

    public void setAccumulatePoints(Integer accumulatePoints) {
        this.accumulatePoints = accumulatePoints;
    }
}
