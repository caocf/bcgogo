package com.bcgogo.user.model.app;

import com.bcgogo.api.ObdDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.OBDSimType;
import com.bcgogo.enums.app.OBDStatus;
import com.bcgogo.enums.app.ObdSimOwnerType;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-6-16.
 */
@Entity
@Table(name = "obd_history")
public class ObdHistory extends LongIdentifier {
  private Long obdId;
  private String sn;    //obd编码
  private Long sellShopId;//obd销售店铺的id
  private Long sellTime;  //obd销售价格
  private String imei;
  private ObdType obdType;
  private String obdVersion;//obd 版本号
  private String spec;//obd 型号
  private String color;//obd 颜色
  private YesNo openCrash;//是否开通碰撞
  private YesNo openShake;//是否开动震动
  private OBDStatus obdStatus;//obd状态
  private Long storageId;//仓管员ID
  private Long sellerId;//统购销售员Id
  private Long agentId;//代理商Id
  private Long ownerId;//归属人
  private String ownerName;//归属人名称
  private ObdSimOwnerType ownerType;//归属人类型
  private Long storageTime;  //obd店铺入库日期
  private OBDSimType obdSimType;//obdSim状态 obd单品，sim卡单品，成品
  private String pack;

  public ObdHistory(OBD obd) {
    setOBDInfo(obd);
  }

  public ObdHistory(ObdDTO obdDTO) {
    if(obdDTO==null) return;
    OBD obd=new OBD();
    obd.fromDTO(obdDTO);
    setOBDInfo(obd);
  }

  public void setOBDInfo(OBD obd){
    if(obd != null){
      setObdId(obd.getId());
      setSn(obd.getSn());
      setSellShopId(obd.getSellShopId());
      setSellTime(obd.getSellTime());
      setImei(obd.getImei());
      setObdType(obd.getObdType());
      setObdVersion(obd.getObdVersion());
      setSpec(obd.getSpec());
      setColor(obd.getColor());
      setOpenCrash(obd.getOpenCrash());
      setOpenShake(obd.getOpenShake());
      setObdStatus(obd.getObdStatus());
      setStorageId(obd.getStorageId());
      setSellerId(obd.getSellerId());
      setAgentId(obd.getAgentId());
      setOwnerId(obd.getOwnerId());
      setOwnerName(obd.getOwnerName());
      setOwnerType(obd.getOwnerType());
      setStorageTime(obd.getStorageTime());
      setObdSimType(obd.getObdSimType());
      setPack(obd.getPack());
    }
  }

  public ObdHistory(){

  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
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

  @Column(name = "storage_time")
  public Long getStorageTime() {
    return storageTime;
  }

  public void setStorageTime(Long storageTime) {
    this.storageTime = storageTime;
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
