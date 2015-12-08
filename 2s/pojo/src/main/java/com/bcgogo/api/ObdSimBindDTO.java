package com.bcgogo.api;


import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.*;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

/**
 * Created by XinyuQiu on 14-6-16.
 */
public class ObdSimBindDTO {
  private Long id;
  private String idStr;
  private Long obdId;
  private String obdIdStr;

  private Long simId;
  private Long appVehicleId;
  private String simIdStr;
  private Long obdHistoryId;
  private Long simHistoryId;
  private ObdSimBindStatus status;//obd sim 绑定关系的状态   ENABLED,DISABLED
  private Long bindTime;


  private Long ownerId;//归属人
  private String ownerName;//归属人名称
  private ObdSimOwnerType ownerType;//归属人类型
  private OBDSimType obdSimType;//
  //OBD,后视镜类别区别
  private ObdMirrorType obdMirrorType;
  //obd info
  private ObdType obdType;


  private String imei;
  private String obdVersion;//obd 版本号
  private String spec;//obd 规格
  private String color;//obd 颜色
  private String pack;//obd 包装
  private YesNo openCrash;//是否开通碰撞
  private YesNo openShake;//是否开动震动
  private OBDStatus obdStatus;
  private String obdStatusStr;//obd 状态
  private String storageTimeStr;  //obd店铺入库日期
  private String sellTimeStr;
  private Long sellShopId;//obd销售店铺的id

  //sim info
  private String simNo;
  private String mobile; //sim卡号
  private Long useDate;//开通日期
  private String useDateStr;
  private String useEndDateStr; //服务截止
  private Integer usePeriod;//服务期

  //customer info
  private Long customerId;
  private String customerIdStr;
  private String customerName;
  private String customerMobile;

  //operation info
  private Long operateShopId;
  private Long operateUserId;
  private String operateUserName;
  //vehicle info
  private Long vehicleId;
  private String vehicleIdStr;
  private String licenceNo;
  private String vehicleBrand;
  private String vehicleModel;

  //crm operation
  private boolean isEdit;
  private boolean isDelete;
  private boolean isPackage;
  private boolean isLog;
  private boolean isSplit;
  private boolean isSell;
  private boolean isReturn;
  private boolean isOutStorage;


  public void setObdDTO(ObdDTO dto) {
    if(dto != null ){
      setObdId(dto.getId());
      setImei(dto.getImei());
      setObdVersion(dto.getObdVersion());
      setSpec(dto.getSpec());
      setColor(dto.getColor());
      setPack(dto.getPack());
      setOpenShake(dto.getOpenShake());
      setOpenCrash(dto.getOpenCrash());
      setObdStatus(dto.getObdStatus());
    }
  }



  public void setObdSimDTO(ObdSimDTO dto) {
    if (dto != null) {
      setSimId(dto.getId());
      setSimNo(dto.getSimNo());
      setMobile(dto.getMobile());
      setUseDate(dto.getUseDate());
      setUseDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_YEAR_MON, dto.getUseDate()));
      setUsePeriod(dto.getUsePeriod());
    }

  }
  public String generateCombineContent() {
    StringBuilder sb = new StringBuilder();
    sb.append("IMIE号/版本/规格/颜色/包装/碰撞/震动/SIM卡编号/手机号码/开通年月/服务期\r\n")
        .append(StringUtil.valueOf(getImei())).append("/")
        .append(StringUtil.valueOf(getObdVersion())).append("/")
        .append(StringUtil.valueOf(getSpec())).append("/")
        .append(StringUtil.valueOf(getColor())).append("/")
        .append(StringUtil.valueOf(getPack())).append("/")
        .append(StringUtil.valueOf(getOpenCrash())).append("/")
        .append(StringUtil.valueOf(getOpenShake())).append("/")
        .append(StringUtil.valueOf(getSimNo())).append("/")
        .append(StringUtil.valueOf(getMobile())).append("/")
        .append(StringUtil.valueOf(getUseDateStr())).append("/")
        .append(StringUtil.valueOf(getUsePeriod())).append("/");
    return sb.toString();
  }

  public String generateSingleOBDContent() {
    StringBuilder sb = new StringBuilder();
    sb.append("IMIE号/版本/规格/颜色/包装/碰撞/震动\r\n")
        .append(StringUtil.valueOf(getImei())).append("/")
        .append(StringUtil.valueOf(getObdVersion())).append("/")
        .append(StringUtil.valueOf(getSpec())).append("/")
        .append(StringUtil.valueOf(getColor())).append("/")
        .append(StringUtil.valueOf(getPack())).append("/")
        .append(StringUtil.valueOf(getOpenCrash())).append("/")
        .append(StringUtil.valueOf(getOpenShake())).append("/");
    return sb.toString();
  }

  public String generateSingleOBDSimContent() {
    StringBuilder sb = new StringBuilder();
    sb.append("SIM卡编号/手机号码/开通年月/服务期\r\n")
        .append(StringUtil.valueOf(getSimNo())).append("/")
        .append(StringUtil.valueOf(getMobile())).append("/")
        .append(StringUtil.valueOf(getUseDateStr())).append("/")
        .append(StringUtil.valueOf(getUsePeriod())).append("/");
    return sb.toString();
  }


  public void setPermissionDTO(ObdOperationPermissionDTO permissionDTO) {
    if(permissionDTO == null){
      permissionDTO = new ObdOperationPermissionDTO();
    }
    this.setLog(permissionDTO.isLog());
    this.setEdit(OBDStatus.EditStatusSet.contains(this.getObdStatus()) && permissionDTO.isEdit());
    this.setDelete(OBDStatus.UN_ASSEMBLE.equals(this.getObdStatus()) && permissionDTO.isDelete());
    this.setPackage(OBDStatus.UN_ASSEMBLE.equals(this.getObdStatus()) && permissionDTO.isPackage());
    this.setSplit(OBDStatus.WAITING_OUT_STORAGE.equals(this.getObdStatus()) && permissionDTO.isSplit());
    this.setOutStorage(OBDStatus.WAITING_OUT_STORAGE.equals(this.getObdStatus()) && permissionDTO.isOutStorage());
    this.setSell((OBDStatus.AGENT.equals(this.getObdStatus())
        || OBDStatus.PICKED.equals(this.getObdStatus())) && permissionDTO.isSell());
    this.setReturn( OBDStatus.returnableStatusSet.contains(this.getObdStatus())&& permissionDTO.isReturn());
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
    if(obdId != null){
      this.setObdIdStr(obdId.toString());
    }else {
      this.setObdIdStr("");
    }
  }

  public Long getSimId() {
    return simId;
  }

  public void setSimId(Long simId) {
    this.simId = simId;
    if(simId != null){
      setSimIdStr(simId.toString());
    }else{
      setSimIdStr("");
    }
  }

  public String getSimIdStr() {
    return simIdStr;
  }

  public void setSimIdStr(String simIdStr) {
    this.simIdStr = simIdStr;
  }

  public Long getObdHistoryId() {
    return obdHistoryId;
  }

  public void setObdHistoryId(Long obdHistoryId) {
    this.obdHistoryId = obdHistoryId;
  }

  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  public Long getSimHistoryId() {
    return simHistoryId;
  }

  public void setSimHistoryId(Long simHistoryId) {
    this.simHistoryId = simHistoryId;
  }

  public ObdSimBindStatus getStatus() {
    return status;
  }

  public void setStatus(ObdSimBindStatus status) {
    this.status = status;
  }

  public String getStorageTimeStr() {
    return storageTimeStr;
  }

  public void setStorageTimeStr(String storageTimeStr) {
    this.storageTimeStr = storageTimeStr;
  }

  public String getSellTimeStr() {
    return sellTimeStr;
  }

  public void setSellTimeStr(String sellTimeStr) {
    this.sellTimeStr = sellTimeStr;
  }

  public Long getSellShopId() {
    return sellShopId;
  }

  public void setSellShopId(Long sellShopId) {
    this.sellShopId = sellShopId;
  }

  public Long getBindTime() {
    return bindTime;
  }

  public void setBindTime(Long bindTime) {
    this.bindTime = bindTime;
  }

  public OBDStatus getObdStatus() {
    return obdStatus;
  }

  public void setObdStatus(OBDStatus obdStatus) {
    this.obdStatus = obdStatus;
  }

  public String getObdStatusStr() {
    return obdStatusStr;
  }

  public void setObdStatusStr(String obdStatusStr) {
    this.obdStatusStr = obdStatusStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getObdIdStr() {
    return obdIdStr;
  }

  public void setObdIdStr(String obdIdStr) {
    this.obdIdStr = obdIdStr;
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

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
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

  public String getSimNo() {
    return simNo;
  }

  public void setSimNo(String simNo) {
    this.simNo = simNo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Long getUseDate() {
    return useDate;
  }

  public void setUseDate(Long useDate) {
    this.useDate = useDate;
  }

  public String getUseDateStr() {
    return useDateStr;
  }

  public void setUseDateStr(String useDateStr) {
    this.useDateStr = useDateStr;
  }

  public String getUseEndDateStr() {
    return useEndDateStr;
  }

  public void setUseEndDateStr(String useEndDateStr) {
    this.useEndDateStr = useEndDateStr;
  }

  public Integer getUsePeriod() {
    return usePeriod;
  }

  public void setUsePeriod(Integer usePeriod) {
    this.usePeriod = usePeriod;
  }

  public OBDSimType getObdSimType() {
    return obdSimType;
  }

  public void setObdSimType(OBDSimType obdSimType) {
    this.obdSimType = obdSimType;
  }

  public String getPack() {
    return pack;
  }

  public void setPack(String pack) {
    this.pack = pack;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    setCustomerIdStr(StringUtil.valueOf(customerId));
    this.customerId = customerId;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    setVehicleIdStr(StringUtil.valueOf(vehicleId));
    this.vehicleId = vehicleId;
  }

  public String getVehicleIdStr() {
    return vehicleIdStr;
  }

  public void setVehicleIdStr(String vehicleIdStr) {
    this.vehicleIdStr = vehicleIdStr;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }



  public Long getOperateShopId() {
    return operateShopId;
  }

  public void setOperateShopId(Long operateShopId) {
    this.operateShopId = operateShopId;
  }

  public Long getOperateUserId() {
    return operateUserId;
  }

  public void setOperateUserId(Long operateUserId) {
    this.operateUserId = operateUserId;
  }

  public String getOperateUserName() {
    return operateUserName;
  }

  public void setOperateUserName(String operateUserName) {
    this.operateUserName = operateUserName;
  }

  public boolean isEdit() {
    return isEdit;
  }

  public void setEdit(boolean isEdit) {
    this.isEdit = isEdit;
  }

  public boolean isDelete() {
    return isDelete;
  }

  public void setDelete(boolean isDelete) {
    this.isDelete = isDelete;
  }

  public boolean isPackage() {
    return isPackage;
  }

  public void setPackage(boolean isPackage) {
    this.isPackage = isPackage;
  }

  public boolean isLog() {
    return isLog;
  }

  public void setLog(boolean isLog) {
    this.isLog = isLog;
  }

  public boolean isSplit() {
    return isSplit;
  }

  public void setSplit(boolean isSplit) {
    this.isSplit = isSplit;
  }

  public boolean isSell() {
    return isSell;
  }

  public void setSell(boolean isSell) {
    this.isSell = isSell;
  }

  public boolean isReturn() {
    return isReturn;
  }

  public void setReturn(boolean isReturn) {
    this.isReturn = isReturn;
  }

  public boolean isOutStorage() {
    return isOutStorage;
  }

  public void setOutStorage(boolean isOutStorage) {
    this.isOutStorage = isOutStorage;
  }


  public ObdMirrorType getObdMirrorType() {
    return obdMirrorType;
  }

  public void setObdMirrorType(ObdMirrorType obdMirrorType) {
    this.obdMirrorType = obdMirrorType;
  }

  public ObdType getObdType() {
    return obdType;
  }

  public void setObdType(ObdType obdType) {
    this.obdType = obdType;
  }
}
