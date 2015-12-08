package com.bcgogo.txn.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午9:45
 * To change this template use File | Settings | File Templates.
 */
public class AccumulatePointRuleDTO {
    private Long id;
    private Long shopId;
    private Integer accumulateRatio;
    private Integer consumeRatio;

    public Long getId() {
        return id;
    }

    public Long getShopId() {
        return shopId;
    }

    public Integer getAccumulateRatio() {
        return accumulateRatio;
    }

    public Integer getConsumeRatio() {
        return consumeRatio;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public void setAccumulateRatio(Integer accumulateRatio) {
        this.accumulateRatio = accumulateRatio;
    }

    public void setConsumeRatio(Integer consumeRatio) {
        this.consumeRatio = consumeRatio;
    }
}
