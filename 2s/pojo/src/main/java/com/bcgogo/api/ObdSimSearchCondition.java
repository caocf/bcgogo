package com.bcgogo.api;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.app.OBDSimType;
import com.bcgogo.enums.app.OBDStatus;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by XinyuQiu on 14-6-19.
 * for crm
 */
public class ObdSimSearchCondition {

  private static final Logger LOG = LoggerFactory.getLogger(ObdSimSearchCondition.class);

  private boolean hasPager = true;
  private int startPageNo;
  private int start;
  private int limit = 15;
  private Pager pager;
  private Long sellShopId;
  private Long obdId;
  private String imei;
  private String mobile;
  private String startUserDateStr;
  private String endUserDateStr;
  private String ownerName;
  private String[] obdSimTypeStrArr;
  private String obdVersion;
  private String[] obdSimStatusStrArr;
  private String simNo;

  private Long startUserDate;
  private Long endUserDate;
  private UserType[] userTypes;

  private OBDSimType[] obdSimTypes;
  private OBDStatus[] obdStatuses;
  private String[] obdStatusList;
  private String startTimeStr;
  private String endTimeStr;

  private boolean isAdmin;
  private Set<Long> userIds;

  //vehicle info
  private String licenceNo;
  private String vehicleBrand;
  private String vehicleModel;
  private String engineNo;
  private String chassisNumber;

  //generate info from web page
  public void generateSearchInfo(){
    Long startDateTemp = null ,endDateTemp = null;
    if(StringUtils.isNotBlank(getStartUserDateStr())){
      try{
        startDateTemp = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_YEAR_MON,getStartUserDateStr());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }
    if(StringUtils.isNotBlank(getEndUserDateStr())){
      try{
        endDateTemp = DateUtil.getLastDayDateTimeOfMonth(DateUtil.DATE_STRING_FORMAT_YEAR_MON, getEndUserDateStr());
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }
    //开始时间，开始时间小于结束时间的
    if (startDateTemp != null && endDateTemp != null && startDateTemp > endDateTemp) {
      try {
        startDateTemp = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_YEAR_MON, getEndUserDateStr());
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      try {
        endDateTemp = DateUtil.getLastDayDateTimeOfMonth(DateUtil.DATE_STRING_FORMAT_YEAR_MON, getStartUserDateStr());
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    setStartUserDate(startDateTemp);
    setEndUserDate(endDateTemp);
    if(!ArrayUtils.isEmpty(getObdSimTypeStrArr())){
      setObdSimTypes(OBDSimType.convertObdSimTypes(getObdSimTypeStrArr()));
    }else {
      setObdSimTypes(null);
    }

    if(!ArrayUtils.isEmpty(getObdSimStatusStrArr())){
      setObdStatuses(OBDStatus.convertOBDStatus(getObdSimStatusStrArr()));
      setObdSimStatusStrArr(OBDStatus.filterOBDStatus(getObdSimStatusStrArr()));
    }

    if (ArrayUtils.isEmpty(getObdStatuses())) {
      setObdStatuses(OBDStatus.EnabledStatusArr);
      setObdSimStatusStrArr(OBDStatus.EnabledStatusStrArr);
    }
    if(start<0){
      start = 0;
    }
    if(limit <0 || limit > 25){
      limit = 25;
    }
  }

  /**
   * 服务状态类型。免费期，自费期
   */
 public enum UserType{
     FREE,
    NOT_FREE
  }

  public boolean isHasPager() {
    return hasPager;
  }

  public void setHasPager(boolean hasPager) {
    this.hasPager = hasPager;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public Long getSellShopId() {
    return sellShopId;
  }

  public void setSellShopId(Long sellShopId) {
    this.sellShopId = sellShopId;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getStartUserDateStr() {
    return startUserDateStr;
  }

  public void setStartUserDateStr(String startUserDateStr) {
    this.startUserDateStr = startUserDateStr;
  }

  public String getEndUserDateStr() {
    return endUserDateStr;
  }

  public void setEndUserDateStr(String endUserDateStr) {
    this.endUserDateStr = endUserDateStr;
  }

  public UserType[] getUserTypes() {
    return userTypes;
  }

  public void setUserTypes(UserType[] userTypes) {
    this.userTypes = userTypes;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }


  public String getObdVersion() {
    return obdVersion;
  }

  public void setObdVersion(String obdVersion) {
    this.obdVersion = obdVersion;
  }

  public String[] getObdSimTypeStrArr() {
    return obdSimTypeStrArr;
  }

  public void setObdSimTypeStrArr(String[] obdSimTypeStrArr) {
    this.obdSimTypeStrArr = obdSimTypeStrArr;
  }

  public String getSimNo() {
    return simNo;
  }

  public void setSimNo(String simNo) {
    this.simNo = simNo;
  }



  public String[] getObdSimStatusStrArr() {
    return obdSimStatusStrArr;
  }

  public void setObdSimStatusStrArr(String[] obdSimStatusStrArr) {
    this.obdSimStatusStrArr = obdSimStatusStrArr;
  }

  public Long getStartUserDate() {
    return startUserDate;
  }

  public void setStartUserDate(Long startUserDate) {
    this.startUserDate = startUserDate;
  }

  public Long getEndUserDate() {
    return endUserDate;
  }

  public void setEndUserDate(Long endUserDate) {
    this.endUserDate = endUserDate;
  }

  public OBDSimType[] getObdSimTypes() {
    return obdSimTypes;
  }

  public void setObdSimTypes(OBDSimType[] obdSimTypes) {
    this.obdSimTypes = obdSimTypes;
  }

  public OBDStatus[] getObdStatuses() {
    return obdStatuses;
  }

  public void setObdStatuses(OBDStatus[] obdStatuses) {
    this.obdStatuses = obdStatuses;
  }

  public String[] getObdStatusList() {
    return obdStatusList;
  }

  public void setObdStatusList(String[] obdStatusList) {
    this.obdStatusList = obdStatusList;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public static Logger getLog() {
    return LOG;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
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

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  public Set<Long> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<Long> userIds) {
    this.userIds = userIds;
  }
}
