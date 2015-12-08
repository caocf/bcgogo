package com.bcgogo.user.model.app;

import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.api.ObdSimDTO;
import com.bcgogo.enums.app.OBDSimType;
import com.bcgogo.enums.app.OBDStatus;
import com.bcgogo.enums.app.ObdMirrorType;
import com.bcgogo.enums.app.ObdSimOwnerType;
import com.bcgogo.model.LongIdentifier;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-6-16.
 */
@Entity
@Table(name = "obd_sim")
public class ObdSim extends LongIdentifier {
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
  private OBDSimType obdSimType;

  public ObdSimDTO toDTO() {
    ObdSimDTO dto = new ObdSimDTO();
    dto.setId(getId());
    dto.setSimNo(getSimNo());
    dto.setMobile(getMobile());
    dto.setUseDate(getUseDate());
    dto.setUsePeriod(getUsePeriod());
    dto.setStatus(getStatus());
    dto.setStorageId(getStorageId());
    dto.setSellerId(getSellerId());
    dto.setAgentId(getAgentId());
    dto.setOwnerId(getOwnerId());
    dto.setOwnerName(getOwnerName());
    dto.setOwnerType(getOwnerType());
    return dto;
  }

  public void fromDTO(ObdSimDTO obdSimDTO){
    if(obdSimDTO==null) return;
    this.setId(obdSimDTO.getId());
    this.setSimNo(obdSimDTO.getSimNo());
    this.setMobile(obdSimDTO.getMobile());
    this.setUseDate(obdSimDTO.getUseDate());
    this.setUsePeriod(obdSimDTO.getUsePeriod());
    this.setStatus(obdSimDTO.getStatus());
    this.setStorageId(obdSimDTO.getStorageId());
    this.setSellerId(obdSimDTO.getSellerId());
    this.setAgentId(obdSimDTO.getAgentId());
    this.setOwnerId(obdSimDTO.getOwnerId());
    this.setOwnerName(obdSimDTO.getOwnerName());
    this.setOwnerType(obdSimDTO.getOwnerType());
  }

  public void fromObdSimBindDTO(ObdSimBindDTO obdSimBindDTO) {
    if(obdSimBindDTO != null){
      if(StringUtils.isNotBlank(obdSimBindDTO.getImei())){
        if(ObdMirrorType.MIRROR.equals(obdSimBindDTO.getObdMirrorType())){
          setStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_MIRROR_OBD_SIM);
        }else  if(ObdMirrorType.POBD.equals(obdSimBindDTO.getObdMirrorType())){
          setStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_GSM_POBD_SIM);
        } else  if(ObdMirrorType.SGSM.equals(obdSimBindDTO.getObdMirrorType())){
          setStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_GSM_OBD_SSIM);
        } else{
          setStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_GSM_OBD_SIM);
        }
      }else {
        setStatus(OBDStatus.UN_ASSEMBLE);
        setObdSimType(OBDSimType.SINGLE_SIM);
      }
      setSimNo(obdSimBindDTO.getSimNo());
      setMobile(obdSimBindDTO.getMobile());
      setUseDate(obdSimBindDTO.getUseDate());
      setUsePeriod(obdSimBindDTO.getUsePeriod());
      setStorageId(obdSimBindDTO.getOwnerId());
      setOwnerName(obdSimBindDTO.getOwnerName());
      setOwnerId(obdSimBindDTO.getOwnerId());
      setOwnerType(obdSimBindDTO.getOwnerType());
    }
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

  @Column(name = "obd_sim_type")
  @Enumerated(EnumType.STRING)
  public OBDSimType getObdSimType() {
    return obdSimType;
  }

  public void setObdSimType(OBDSimType obdSimType) {
    this.obdSimType = obdSimType;
  }
}
