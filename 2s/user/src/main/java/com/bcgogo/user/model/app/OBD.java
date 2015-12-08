package com.bcgogo.user.model.app;

import com.bcgogo.api.ObdDTO;
import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.*;
import com.bcgogo.model.LongIdentifier;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "obd")
public class OBD extends LongIdentifier {
  private String sn;    //obd编码
  private Long sellShopId;//obd销售店铺的id
  private Long sellTime;  //obd销售日期，安装日期obd销售时间（该obd第一次绑定时间作为销售时间）
  private Long storageTime;  //obd店铺入库日期
  private Double sellPrice;
  private String imei;
  private ObdType obdType;

  private String obdVersion;//obd 版本号
  private String spec;//obd 型号
  private String color;//obd 颜色
  private YesNo openCrash;//是否开通碰撞
  private YesNo openShake;//是否开动震动
  private OBDStatus obdStatus;//obd 状态
  private Long storageId;//仓管员ID
  private Long sellerId;//统购销售员Id
  private Long agentId;//代理商Id
  private Long ownerId;//归属人
  private String ownerName;//归属人名称
  private ObdSimOwnerType ownerType;//归属人类型
  private OBDSimType obdSimType;//obdSim状态 obd单品，sim卡单品，成品
  private String pack;


  public OBD() {
    super();
  }

  public OBD(ObdDTO dto) {
    setId(dto.getId());
    setSn(dto.getSn());
    setSellPrice(dto.getSellPrice());
    setSellTime(dto.getSellTime());
    setSellShopId(dto.getSellShopId());
    setObdType(dto.getObdType());
    setImei(dto.getImei());
  }

  public ObdDTO toDTO() {
    ObdDTO dto = new ObdDTO();
    dto.setId(getId());
    dto.setSn(getSn());
    dto.setSellPrice(getSellPrice());
    dto.setSellTime(getSellTime());
    dto.setSellPrice(getSellPrice());
    dto.setSellShopId(getSellShopId());
    dto.setObdType(getObdType());
    dto.setImei(getImei());

    dto.setObdVersion(getObdVersion());
    dto.setSpec(getSpec());
    dto.setColor(getColor());
    dto.setOpenCrash(getOpenCrash());
    dto.setOpenShake(getOpenShake());
    dto.setObdStatus(getObdStatus());
    dto.setStorageId(getStorageId());
    dto.setSellerId(getSellerId());
    dto.setAgentId(getAgentId());
    dto.setOwnerId(getOwnerId());
    dto.setOwnerName(getOwnerName());
    dto.setOwnerType(getOwnerType());
    return dto;
  }

  public void fromDTO(ObdDTO obdDTO) {
    this.setId(obdDTO.getId());
    this.setSn(obdDTO.getSn());
    this.setSellPrice(obdDTO.getSellPrice());
    this.setSellTime(obdDTO.getSellTime());
    this.setSellPrice(obdDTO.getSellPrice());
    this.setSellShopId(obdDTO.getSellShopId());
    this.setObdType(obdDTO.getObdType());
    this.setImei(obdDTO.getImei());

    this.setObdVersion(obdDTO.getObdVersion());
    this.setSpec(obdDTO.getSpec());
    this.setColor(obdDTO.getColor());
    this.setOpenCrash(obdDTO.getOpenCrash());
    this.setOpenShake(obdDTO.getOpenShake());
    this.setObdStatus(obdDTO.getObdStatus());
    this.setStorageId(obdDTO.getStorageId());
    this.setSellerId(obdDTO.getSellerId());
    this.setAgentId(obdDTO.getAgentId());
    this.setOwnerId(obdDTO.getOwnerId());
    this.setOwnerName(obdDTO.getOwnerName());
    this.setOwnerType(obdDTO.getOwnerType());
  }

  public void fromObdSimBindDTO(ObdSimBindDTO obdSimBindDTO) {
    if (obdSimBindDTO != null) {
      if (ObdMirrorType.MIRROR.equals(obdSimBindDTO.getObdMirrorType())) {
        setObdType(ObdType.MIRROR);
        if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
          setObdStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_MIRROR_OBD_SIM);
        } else {
          setObdStatus(OBDStatus.UN_ASSEMBLE);
          setObdSimType(OBDSimType.SINGLE_MIRROR_OBD);
        }
      } else if (ObdMirrorType.POBD.equals(obdSimBindDTO.getObdMirrorType())) {
        setObdType(ObdType.POBD);
        if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
          setObdStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_GSM_POBD_SIM);
        } else {
          setObdStatus(OBDStatus.UN_ASSEMBLE);
          setObdSimType(OBDSimType.SINGLE_GSM_POBD);
        }
      }  else if (ObdMirrorType.SGSM.equals(obdSimBindDTO.getObdMirrorType())) {
        setObdType(ObdType.SGSM);
        if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
          setObdStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_GSM_OBD_SSIM);
        } else {
          setObdStatus(OBDStatus.UN_ASSEMBLE);
          setObdSimType(OBDSimType.SINGLE_GSM_SOBD);
        }
      }else {
        setObdType(ObdType.GSM);
        if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
          setObdStatus(OBDStatus.WAITING_OUT_STORAGE);
          setObdSimType(OBDSimType.COMBINE_GSM_OBD_SIM);
        } else {
          setObdStatus(OBDStatus.UN_ASSEMBLE);
          setObdSimType(OBDSimType.SINGLE_GSM_OBD);
        }
      }
      setImei(obdSimBindDTO.getImei());
      setObdVersion(obdSimBindDTO.getObdVersion());
      setSpec(obdSimBindDTO.getSpec());
      setColor(obdSimBindDTO.getColor());
      setPack(obdSimBindDTO.getPack());
      setOpenShake(obdSimBindDTO.getOpenShake());
      setOpenCrash(obdSimBindDTO.getOpenCrash());
      setStorageId(obdSimBindDTO.getOwnerId());
      setOwnerName(obdSimBindDTO.getOwnerName());
      setOwnerType(obdSimBindDTO.getOwnerType());
    }
  }


  @Column(name = "sn")
  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  @Column(name = "sell_shop_id")
  public Long getSellShopId() {
    return sellShopId;
  }

  public void setSellShopId(Long sellShopId) {
    this.sellShopId = sellShopId;
  }

  @Column(name = "sell_time")
  public Long getSellTime() {
    return sellTime;
  }

  public void setSellTime(Long sellTime) {
    this.sellTime = sellTime;
  }

  @Column(name = "storage_time")
  public Long getStorageTime() {
    return storageTime;
  }

  public void setStorageTime(Long storageTime) {
    this.storageTime = storageTime;
  }

  @Column(name = "sell_price")
  public Double getSellPrice() {
    return sellPrice;
  }

  public void setSellPrice(Double sellPrice) {
    this.sellPrice = sellPrice;
  }

  @Column(name = "imei")
  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  @Column(name = "obd_type")
  @Enumerated(EnumType.STRING)
  public ObdType getObdType() {
    return obdType;
  }

  public void setObdType(ObdType obdType) {
    this.obdType = obdType;
  }

  @Column(name = "obd_version")
  public String getObdVersion() {
    return obdVersion;
  }

  public void setObdVersion(String obdVersion) {
    this.obdVersion = obdVersion;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "color")
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Column(name = "open_crash")
  @Enumerated(EnumType.STRING)
  public YesNo getOpenCrash() {
    return openCrash;
  }

  public void setOpenCrash(YesNo openCrash) {
    this.openCrash = openCrash;
  }

  @Column(name = "open_shake")
  @Enumerated(EnumType.STRING)
  public YesNo getOpenShake() {
    return openShake;
  }

  public void setOpenShake(YesNo openShake) {
    this.openShake = openShake;
  }

  @Column(name = "obd_status")
  @Enumerated(EnumType.STRING)
  public OBDStatus getObdStatus() {
    return obdStatus;
  }

  public void setObdStatus(OBDStatus obdStatus) {
    this.obdStatus = obdStatus;
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

  @Column(name = "pack")
  public String getPack() {
    return pack;
  }

  public void setPack(String pack) {
    this.pack = pack;
  }

}
