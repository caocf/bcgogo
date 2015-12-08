package com.bcgogo.api;

import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.OBDStatus;
import com.bcgogo.enums.app.ObdSimOwnerType;
import com.bcgogo.enums.app.ObdType;

/**
 * User: ZhangJuntao
 * Date: 13-8-22
 * Time: 下午3:37
 */
public class ObdDTO {
  private Long id;
  private String sn;    //obd编码
  private Long sellShopId;//obd销售店铺的id
  private Long sellTime;  //obd销售价格
  private Double sellPrice; //obd销售时间（该obd第一次绑定时间作为销售时间）
  private String imei;
  private ObdType obdType;
  private String obdVersion;//obd 版本号
  private String spec;//obd 型号
  private String color;//obd 颜色
  private String pack;//obd 包装
  private YesNo openCrash;//是否开通碰撞
  private YesNo openShake;//是否开动震动
  private OBDStatus obdStatus;//obd 状态
  private Long storageId;//仓管员ID
  private Long sellerId;//统购销售员Id
  private Long agentId;//代理商Id
  private Long ownerId;//归属人
  private String ownerName;//归属人名称
  private ObdSimOwnerType ownerType;//归属人类型




  public ObdDTO() {
    super();
  }

  public ObdDTO(OBDBindingDTO obdBindingDTO) {
    setSn(obdBindingDTO.getObdSN());
    setSellTime(System.currentTimeMillis());
    setSellShopId(obdBindingDTO.getSellShopId());
    setObdType(ObdType.BLUE_TOOTH);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public Long getSellShopId() {
    return sellShopId;
  }

  public void setSellShopId(Long sellShopId) {
    this.sellShopId = sellShopId;
  }

  public Long getSellTime() {
    return sellTime;
  }

  public void setSellTime(Long sellTime) {
    this.sellTime = sellTime;
  }

  public Double getSellPrice() {
    return sellPrice;
  }

  public void setSellPrice(Double sellPrice) {
    this.sellPrice = sellPrice;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public ObdType getObdType() {
    return obdType;
  }

  public void setObdType(ObdType obdType) {
    this.obdType = obdType;
  }

  public String getObdVersion() {
    return obdVersion;
  }

  public void setObdVersion(String obdVersion) {
    this.obdVersion = obdVersion;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public YesNo getOpenCrash() {
    return openCrash;
  }

  public void setOpenCrash(YesNo openCrash) {
    this.openCrash = openCrash;
  }

  public YesNo getOpenShake() {
    return openShake;
  }

  public void setOpenShake(YesNo openShake) {
    this.openShake = openShake;
  }

  public OBDStatus getObdStatus() {
    return obdStatus;
  }

  public void setObdStatus(OBDStatus obdStatus) {
    this.obdStatus = obdStatus;
  }

  public Long getStorageId() {
    return storageId;
  }

  public void setStorageId(Long storageId) {
    this.storageId = storageId;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public ObdSimOwnerType getOwnerType() {
    return ownerType;
  }

  public void setOwnerType(ObdSimOwnerType ownerType) {
    this.ownerType = ownerType;
  }

  public String getPack() {
    return pack;
  }

  public void setPack(String pack) {
    this.pack = pack;
  }
}
