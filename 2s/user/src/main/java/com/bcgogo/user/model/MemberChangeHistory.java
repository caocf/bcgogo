package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="member_change_history")
public class MemberChangeHistory extends LongIdentifier{
    private Long shopId;
    private Long customerId;
    private Long memberId;
    private String type;
    private Long memberCardId;
    private Integer oldMemberType;
    private Integer newMemberType;
    private Double oldBalance;
    private Double newBalance;
    private Integer oldAccumulatePoints;
    private Integer newAccumulatePoints;
    private String reason;

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
    @Column(name="member_card_id")
    public Long getMemberCardId() {
        return memberCardId;
    }
    @Column(name="type")
    public String getType() {
        return type;
    }
    @Column(name="old_member_type")
    public Integer getOldMemberType() {
        return oldMemberType;
    }
    @Column(name="new_member_type")
    public Integer getNewMemberType() {
        return newMemberType;
    }
    @Column(name="old_balance")
    public Double getOldBalance() {
        return oldBalance;
    }
    @Column(name="new_balance")
    public Double getNewBalance() {
        return newBalance;
    }
    @Column(name="old_accumulate_points")
    public Integer getOldAccumulatePoints() {
        return oldAccumulatePoints;
    }
    @Column(name="new_accumulate_points")
    public Integer getNewAccumulatePoints() {
        return newAccumulatePoints;
    }
    @Column(name="reason")
    public String getReason() {
        return reason;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setMemberCardId(Long memberCardId) {
        this.memberCardId = memberCardId;
    }

    public void setOldMemberType(Integer oldMemberType) {
        this.oldMemberType = oldMemberType;
    }

    public void setNewMemberType(Integer newMemberType) {
        this.newMemberType = newMemberType;
    }

    public void setOldBalance(Double oldBalance) {
        this.oldBalance = oldBalance;
    }

    public void setNewBalance(Double newBalance) {
        this.newBalance = newBalance;
    }

    public void setNewAccumulatePoints(Integer newAccumulatePoints) {
        this.newAccumulatePoints = newAccumulatePoints;
    }

    public void setOldAccumulatePoints(Integer oldAccumulatePoints) {
        this.oldAccumulatePoints = oldAccumulatePoints;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
