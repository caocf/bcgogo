package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午5:11
 * To change this template use File | Settings | File Templates.
 */
public class MemberRechargeOrderDTO {
    private Long id;
    private Long shopId;
    private Long customerId;
    private Long memberId;
    private Double payAmount;
    private Integer rechargeAmount;
    private Double OldBalance;
    private Double newBalance;
    private Integer accumulatePoints;

    public Long getId() {
        return id;
    }

    public Long getShopId() {
        return shopId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public Integer getRechargeAmount() {
        return rechargeAmount;
    }

    public Double getOldBalance() {
        return OldBalance;
    }

    public Double getNewBalance() {
        return newBalance;
    }

    public Integer getAccumulatePoints() {
        return accumulatePoints;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public void setRechargeAmount(Integer rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
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
