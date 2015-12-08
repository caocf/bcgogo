package com.bcgogo.user.model.app;

import com.bcgogo.api.ObdSimDTO;
import com.bcgogo.enums.app.OBDStatus;
import com.bcgogo.enums.app.ObdSimOwnerType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-6-16.
 */
@Entity
@Table(name = "obd_sim_history")
public class ObdSimHistory extends LongIdentifier {

  private Long obdSimId;
  private String simNo;
  private String mobile;
  private Long useDate;//开通日期
  private Integer usePeriod;//服务期
  private OBDStatus status;
  private Long storageId;//仓管员ID
  private Long sellerId;//统购销售员Id
  private Long agentId;//代理商Id
  private Long ownerId;//归属人
  private String ownerName;//归属人名称
  private ObdSimOwnerType ownerType;//归属人类型


  public ObdSimHistory(ObdSim obdSim) {
    setObdSimInfo(obdSim);
  }

  public ObdSimHistory(ObdSimDTO obdSimDTO) {
    ObdSim obdSim=new ObdSim();
    obdSim.fromDTO(obdSimDTO);
    setObdSimInfo(obdSim);
  }

  public void setObdSimInfo(ObdSim obdSim){
     if (obdSim != null) {
      setObdSimId(obdSim.getId());
      setSimNo(obdSim.getSimNo());
      setMobile(obdSim.getMobile());
      setUseDate(obdSim.getUseDate());
      setUsePeriod(obdSim.getUsePeriod());
      setStatus(obdSim.getStatus());
      setStorageId(obdSim.getStorageId());
      setSellerId(obdSim.getSellerId());
      setAgentId(obdSim.getAgentId());
      setOwnerId(obdSim.getOwnerId());
      setOwnerName(obdSim.getOwnerName());
      setOwnerType(obdSim.getOwnerType());
    }
  }

  public ObdSimHistory() {

  }

  @Column(name = "obd_sim_id")
  public Long getObdSimId() {
    return obdSimId;
  }

  public void setObdSimId(Long obdSimId) {
    this.obdSimId = obdSimId;
  }
  @Column(name = "sim_no")
  public String getSimNo() {
    return simNo;
  }

  public void setSimNo(String simNo) {
    this.simNo = simNo;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "use_date")
  public Long getUseDate() {
    return useDate;
  }

  public void setUseDate(Long useDate) {
    this.useDate = useDate;
  }

  @Column(name = "use_period")
  public Integer getUsePeriod() {
    return usePeriod;
  }

  public void setUsePeriod(Integer usePeriod) {
    this.usePeriod = usePeriod;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OBDStatus getStatus() {
    return status;
  }

  public void setStatus(OBDStatus status) {
    this.status = status;
  }

  @Column(name = "storage_id")
  public Long getStorageId() {
    return storageId;
  }

  public void setStorageId(Long storageId) {
    this.storageId = storageId;
  }

  @Column(name = "seller_id")
  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  @Column(name = "agent_id")
  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }

  @Column(name = "owner_id")
  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  @Column(name = "owner_name")
  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  @Column(name = "owner_type")
  @Enumerated(EnumType.STRING)
  public ObdSimOwnerType getOwnerType() {
    return ownerType;
  }

  public void setOwnerType(ObdSimOwnerType ownerType) {
    this.ownerType = ownerType;
  }
}
