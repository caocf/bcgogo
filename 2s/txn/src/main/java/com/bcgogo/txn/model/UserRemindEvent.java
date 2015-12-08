package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-21
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="user_remind_event")
public class UserRemindEvent extends LongIdentifier{
    public UserRemindEvent(){}

    private Long shopId;
    private Long userId;
    private Long eventType;
    private Long eventDate;

    @Column(name = "shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "event_type")
    public Long getEventType() {
        return eventType;
    }

    public void setEventType(Long eventType) {
        this.eventType = eventType;
    }

    @Column(name = "event_date")
    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

}
