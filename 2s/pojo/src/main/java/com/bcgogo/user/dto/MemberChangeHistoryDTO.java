package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
public class MemberChangeHistoryDTO {
    private Long id;
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

    public String getType() {
        return type;
    }

    public Long getMemberCardId() {
        return memberCardId;
    }

    public Integer getNewMemberType() {
        return newMemberType;
    }

    public Integer getOldMemberType() {
        return oldMemberType;
    }

    public Double getOldBalance() {
        return oldBalance;
    }

    public Double getNewBalance() {
        return newBalance;
    }

    public Integer getOldAccumulatePoints() {
        return oldAccumulatePoints;
    }

    public Integer getNewAccumulatePoints() {
        return newAccumulatePoints;
    }

    public String getReason() {
        return reason;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setOldMemberType(Integer oldMemberType) {
        this.oldMemberType = oldMemberType;
    }

    public void setMemberCardId(Long memberCardId) {
        this.memberCardId = memberCardId;
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

    public void setOldAccumulatePoints(Integer oldAccumulatePoints) {
        this.oldAccumulatePoints = oldAccumulatePoints;
    }

    public void setNewAccumulatePoints(Integer newAccumulatePoints) {
        this.newAccumulatePoints = newAccumulatePoints;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
