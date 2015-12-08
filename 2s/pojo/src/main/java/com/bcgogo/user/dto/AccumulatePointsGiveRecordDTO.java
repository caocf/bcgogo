package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午5:08
 * To change this template use File | Settings | File Templates.
 */
public class AccumulatePointsGiveRecordDTO {
    private Long id;
    private Long shopId;
    private Long customerId;
    private Long memberId;
    private Integer amount;
    private Long executorId;

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

    public Integer getAmount() {
        return amount;
    }

    public Long getExecutorId() {
        return executorId;
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

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }
}
