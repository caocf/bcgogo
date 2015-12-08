package com.bcgogo.notification.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: lijie
 * Date: 11-11-10
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
public class InBoxDTO implements Serializable {
    private Long id;
    private Long receiveShopId;
    private String sendMobile;
    private String content;
    private String rawData;
    private Calendar receiveTime;
    private Long receiveChannel;
    private Long status;

    private String name;
    private String licenceNo;
    private String sendTime;

    public String getSendTime() {
        if(this.getReceiveTime()==null){
            return "";
        }
        int year = this.getReceiveTime().get(Calendar.YEAR);
        int month =  this.getReceiveTime().get(Calendar.MONTH)+1;
        int day = this.getReceiveTime().get(Calendar.DAY_OF_MONTH);
        int hour = this.getReceiveTime().get(Calendar.HOUR_OF_DAY);
        int minute = this.getReceiveTime().get(Calendar.MINUTE);
        int second = this.getReceiveTime().get(Calendar.SECOND);
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiveShopId() {
        return receiveShopId;
    }

    public void setReceiveShopId(Long receiveShopId) {
        this.receiveShopId = receiveShopId;
    }

    public String getSendMobile() {
        return sendMobile;
    }

    public void setSendMobile(String sendMobile) {
        this.sendMobile = sendMobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public Calendar getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Calendar receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getReceiveChannel() {
        return receiveChannel;
    }

    public void setReceiveChannel(Long receiveChannel) {
        this.receiveChannel = receiveChannel;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
