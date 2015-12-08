package com.bcgogo.notification.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.InBoxDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity
@Table(name="in_box")
public class InBox extends LongIdentifier{
    private Long receiveShopId;
    private String sendMobile;
    private String content;
    private String rawData;
    private Calendar receiveTime;
    private Long receiveChannel;
    private Long status;
    @Column(name="receive_shop_id")
    public Long getReceiveShopId() {
        return receiveShopId;
    }

    public void setReceiveShopId(Long receiveShopId) {
        this.receiveShopId = receiveShopId;
    }
    @Column(name="send_mobile",length=20)
    public String getSendMobile() {
        return sendMobile;
    }

    public void setSendMobile(String sendMobile) {
        this.sendMobile = sendMobile;
    }
    @Column(name="content",length=500)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Column(name="raw_data")
    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
    @Column(name="receive_time")
    public Calendar getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Calendar receiveTime) {
        this.receiveTime = receiveTime;
    }
    @Column(name="receive_channel")
    public Long getReceiveChannel() {
        return receiveChannel;
    }

    public void setReceiveChannel(Long receiveChannel) {
        this.receiveChannel = receiveChannel;
    }
    @Column(name="status")
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public InBox(){}

    public InBox(InBoxDTO inBoxDTO){
        this.setId(inBoxDTO.getId());
        this.setContent(inBoxDTO.getContent());
        this.setRawData(inBoxDTO.getRawData());
        this.setReceiveChannel(inBoxDTO.getReceiveChannel());
        this.setReceiveShopId(inBoxDTO.getReceiveShopId());
        this.setReceiveTime(inBoxDTO.getReceiveTime());
        this.setSendMobile(inBoxDTO.getSendMobile());
        this.setStatus(inBoxDTO.getStatus());
    }

    public InBox fromDTO(InBoxDTO inBoxDTO){
        this.setId(inBoxDTO.getId());
        this.setContent(inBoxDTO.getContent());
        this.setRawData(inBoxDTO.getRawData());
        this.setReceiveChannel(inBoxDTO.getReceiveChannel());
        this.setReceiveShopId(inBoxDTO.getReceiveShopId());
        this.setReceiveTime(inBoxDTO.getReceiveTime());
        this.setSendMobile(inBoxDTO.getSendMobile());
        this.setStatus(inBoxDTO.getStatus());
        return this;
    }

    public InBoxDTO toDTO(){
        InBoxDTO inBoxDTO = new InBoxDTO();
        inBoxDTO.setId(this.getId());
        inBoxDTO.setContent(this.getContent());
        inBoxDTO.setRawData(this.getRawData());
        inBoxDTO.setReceiveChannel(this.getReceiveChannel());
        inBoxDTO.setReceiveShopId(this.getReceiveShopId());
        inBoxDTO.setReceiveTime(this.getReceiveTime());
        inBoxDTO.setSendMobile(this.getSendMobile());
        inBoxDTO.setStatus(this.getStatus());
        return inBoxDTO;
    }

}