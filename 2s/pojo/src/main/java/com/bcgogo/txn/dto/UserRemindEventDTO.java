package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-21
 * Time: 下午12:02
 * To change this template use File | Settings | File Templates.
 */
public class UserRemindEventDTO implements Serializable {

    private Long id;
    private Long shopId;
    private Long userId;
    private Long eventTyep;
    private Long eventDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventTyep() {
        return eventTyep;
    }

    public void setEventTyep(Long eventTyep) {
        this.eventTyep = eventTyep;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

}
