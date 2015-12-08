package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午9:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="accumulate_points_rule")
public class AccumulatePointsRule extends LongIdentifier{
    private Long shopId;
    private Integer accumulateRatio;
    private Integer consumeRatio;

    @Column(name="shop_id")
    public Long getShopId() {
        return shopId;
    }
    @Column(name="accumulate_ratio")
    public Integer getAccumulateRatio() {
        return accumulateRatio;
    }
    @Column(name="consume_ratio")
    public Integer getConsumeRatio() {
        return consumeRatio;
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
