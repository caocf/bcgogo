package com.bcgogo.search.dto;

import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;

/**
 * User: ZhangJuntao
 * Date: 12-5-11
 * Time: 上午9:15
 * 搜索条件DTO
 */
public class VehicleSearchConditionDTO {
  public enum StatsFields {
    IS_MOBILE_VEHICLE("is_mobile_vehicle"),
    OBD_ID("obd_id"),
    VEHICLE_TOTAL_CONSUME_AMOUNT("vehicle_total_consume_amount");

    private final String name;
    private StatsFields(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public enum SearchStrategy {
    SEARCH_STRATEGY_STATS,
    SEARCH_STRATEGY_STATS_FACET,
    SEARCH_STRATEGY_HAS_MOBILE,
    SEARCH_STRATEGY_OBD,
  }

  private Long shopId;
  private String customerInfo;
  private String licenceNo;
  private String vehicleModel;//型号
  private String vehicleBrand; //品牌
  private String vehicleColor; //颜色
  private String engineNo; //发动机号码
  private String chassisNumber;//车架号码
  private Long vehicleLastConsumeTimeStart;
  private String vehicleLastConsumeTimeStartStr;
  private Long vehicleLastConsumeTimeEnd;
  private String vehicleLastConsumeTimeEndStr;

  private Double maintainIntervalsMileage;
  private Integer maintainIntervalsDay;

  private String[] statsFields;
  private String[] facetFields;

  private SearchStrategy[] searchStrategies; //查找策略
  private String sort;                    //排序规则
  private int start;
  private int rows = 15;//默认15
  //ajaxPaging.tag 对应的接口
  private String sortStatus;
  private int startPageNo = 1;
  private int maxRows = 15;//默认15
  private JoinSearchConditionDTO joinSearchConditionDTO;

  //行车日志
  private String gsmObdImei;
  private String gsmObdImeiMoblie;
  private Long lastDriveTimeStart;
  private String lastDriveTimeStartStr;
  private Long lastDriveTimeEnd;
  private String lastDriveTimeEndStr;


  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
    if(StringUtils.isNotBlank(sortStatus)){
      this.setSort(TxnConstant.sortCommandMap.get(sortStatus));
    }else {
      this.setSort(null);
    }
  }

  public String[] getStatsFields() {
    return statsFields;
  }

  public void setStatsFields(String[] statsFields) {
    this.statsFields = statsFields;
  }

  public String[] getFacetFields() {
    return facetFields;
  }

  public void setFacetFields(String[] facetFields) {
    this.facetFields = facetFields;
  }

  public String getVehicleLastConsumeTimeStartStr() {
    return vehicleLastConsumeTimeStartStr;
  }

  public void setVehicleLastConsumeTimeStartStr(String vehicleLastConsumeTimeStartStr) {
    this.vehicleLastConsumeTimeStartStr = vehicleLastConsumeTimeStartStr;
    if (StringUtils.isNotBlank(vehicleLastConsumeTimeStartStr)){
      try {
        this.vehicleLastConsumeTimeStart = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, vehicleLastConsumeTimeStartStr);
      } catch (ParseException e) {
      }
    }
  }

  public String getVehicleLastConsumeTimeEndStr() {
    return vehicleLastConsumeTimeEndStr;
  }

  public void setVehicleLastConsumeTimeEndStr(String vehicleLastConsumeTimeEndStr) {
    this.vehicleLastConsumeTimeEndStr = vehicleLastConsumeTimeEndStr;
    if (StringUtils.isNotBlank(vehicleLastConsumeTimeEndStr)){
      try {
        this.vehicleLastConsumeTimeEnd = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, vehicleLastConsumeTimeEndStr)+DateUtil.DAY_MILLION_SECONDS-1;
      } catch (ParseException e) {
      }
    }
  }

  public Double getMaintainIntervalsMileage() {
    return maintainIntervalsMileage;
  }

  public void setMaintainIntervalsMileage(Double maintainIntervalsMileage) {
    this.maintainIntervalsMileage = maintainIntervalsMileage;
  }


  public Integer getMaintainIntervalsDay() {
    return maintainIntervalsDay;
  }

  public void setMaintainIntervalsDay(Integer maintainIntervalsDay) {
    this.maintainIntervalsDay = maintainIntervalsDay;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  public SearchStrategy[] getSearchStrategies() {
    return searchStrategies;
  }

  public void setSearchStrategies(SearchStrategy[] searchStrategies) {
    this.searchStrategies = searchStrategies;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.start = (startPageNo - 1) * maxRows;
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
    this.rows = maxRows;
  }

  public JoinSearchConditionDTO getJoinSearchConditionDTO() {
    return joinSearchConditionDTO;
  }

  public void setJoinSearchConditionDTO(JoinSearchConditionDTO joinSearchConditionDTO) {
    this.joinSearchConditionDTO = joinSearchConditionDTO;
  }

  public Long getVehicleLastConsumeTimeStart() {
    return vehicleLastConsumeTimeStart;
  }

  public void setVehicleLastConsumeTimeStart(Long vehicleLastConsumeTimeStart) {
    this.vehicleLastConsumeTimeStart = vehicleLastConsumeTimeStart;
  }

  public Long getVehicleLastConsumeTimeEnd() {
    return vehicleLastConsumeTimeEnd;
  }

  public void setVehicleLastConsumeTimeEnd(Long vehicleLastConsumeTimeEnd) {
    this.vehicleLastConsumeTimeEnd = vehicleLastConsumeTimeEnd;
  }

  public String getCustomerInfo() {
    return customerInfo;
  }

  public void setCustomerInfo(String customerInfo) {
    this.customerInfo = customerInfo;
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

  public Long getLastDriveTimeStart() {
    return lastDriveTimeStart;
  }

  public void setLastDriveTimeStart(Long lastDriveTimeStart) {
    this.lastDriveTimeStart = lastDriveTimeStart;
  }

  public String getLastDriveTimeStartStr() {
    return lastDriveTimeStartStr;
  }

  public void setLastDriveTimeStartStr(String lastDriveTimeStartStr) {
    this.lastDriveTimeStartStr = lastDriveTimeStartStr;
    if (StringUtils.isNotBlank(lastDriveTimeStartStr)) {
      try {
        this.lastDriveTimeStart = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, lastDriveTimeStartStr);
        this.setLastDriveTimeStart(DateUtil.getStartTimeOfTimeDay(lastDriveTimeStart));
      } catch (ParseException e) {
      }
    }
  }

  public Long getLastDriveTimeEnd() {
    return lastDriveTimeEnd;
  }

  public void setLastDriveTimeEnd(Long lastDriveTimeEnd) {
    this.lastDriveTimeEnd = lastDriveTimeEnd;
  }

  public String getLastDriveTimeEndStr() {
    return lastDriveTimeEndStr;
  }

  public void setLastDriveTimeEndStr(String lastDriveTimeEndStr) {
    this.lastDriveTimeEndStr = lastDriveTimeEndStr;
    if (StringUtils.isNotBlank(lastDriveTimeEndStr)) {
      try {
        this.lastDriveTimeEnd = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, lastDriveTimeEndStr);
        this.setLastDriveTimeEnd(DateUtil.getEndOfDate(lastDriveTimeEnd));
      } catch (ParseException e) {
      }
    }
  }

  public String getGsmObdImeiMoblie() {
    return gsmObdImeiMoblie;
  }

  public void setGsmObdImeiMoblie(String gsmObdImeiMoblie) {
    this.gsmObdImeiMoblie = gsmObdImeiMoblie;
  }

  public String getGsmObdImei() {
    return gsmObdImei;
  }

  public void setGsmObdImei(String gsmObdImei) {
    this.gsmObdImei = gsmObdImei;
  }
}
