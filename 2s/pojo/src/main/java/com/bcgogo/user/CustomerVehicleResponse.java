package com.bcgogo.user;

import com.bcgogo.user.dto.AppointServiceDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-15
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class CustomerVehicleResponse implements Serializable {

  private Long customerId;
  private Long vehicleId;
  private String vehicleIdStr;
  private String licenceNo;
  private String brand;
  private String model;
  private String year;
  private String engine;
  private String vin;
  private String engineNo;
  private Long carDate;
  private String carDateStr;
  private Long lastDate;
  private String lastDateStr;
  private Double startMileage;
  private String startMileageStr;

  private Double obdMileage;
  private String obdMileageStr;
  private double totalAmount;
  private double totalArrears;
  private Long maintainTime;   //预约保养时间
  private Long insureTime;      //预约保险时间
  private Long examineTime;    //预约验车时间
  private String maintainTimeStr;
  private Long maintainMileage;
  private String insureTimeStr;
  private String examineTimeStr;
  private String contact;
  private String mobile;
  private String color;
  private String gsmObdImei;
  private String gsmObdImeiMoblie;
  private Long obdId;
  private List<AppointServiceDTO> appointServiceDTOs;

  private Double lastMaintainMileage;//上次保养里程
  private Long lastMaintainTime;//上次保养时间
  private String lastMaintainTimeStr;//上次保养时间

  private Double maintainMileagePeriod;//保养里程周期
  private Long maintainTimePeriod;//保养时间周期
  private Long maintainTimePeriodStr;//保养时间周期
  private Double nextMaintainMileageAccess;//距下次保养里程
  private String nextMaintainMileageAccessStr;//距下次保养里程

  public String getGsmObdImei() {
    return gsmObdImei;
  }

  public void setGsmObdImei(String gsmObdImei) {
    this.gsmObdImei = gsmObdImei;
  }

  public String getGsmObdImeiMoblie() {
    return gsmObdImeiMoblie;
  }

  public void setGsmObdImeiMoblie(String gsmObdImeiMoblie) {
    this.gsmObdImeiMoblie = gsmObdImeiMoblie;
  }

  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }

  public String getObdMileageStr() {
    return obdMileageStr;
  }

  public void setObdMileageStr(String obdMileageStr) {
    this.obdMileageStr = obdMileageStr;
  }

  private Double totalConsume; //累计消费 实收+欠款
  private Long consumeTimes;  //消费次数

  public List<AppointServiceDTO> getAppointServiceDTOs() {
    return appointServiceDTOs;
  }

  public void setAppointServiceDTOs(List<AppointServiceDTO> appointServiceDTOs) {
    this.appointServiceDTOs = appointServiceDTOs;
  }

  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getContact() {

    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public CustomerVehicleResponse() {

  }

  public CustomerVehicleResponse(CustomerVehicleDTO customerVehicleDTO, VehicleDTO vehicleDTO, List<AppointServiceDTO> appointServiceDTOs) {
    if (vehicleDTO != null) {
      this.setLastDate(0L);

      this.setVehicleId(vehicleDTO.getId());                  //车辆id
      this.setLicenceNo(vehicleDTO.getLicenceNo());                  //车牌号
      this.setBrand(vehicleDTO.getBrand());          //品牌
      this.setModel(vehicleDTO.getModel());          //车型
      this.setYear(vehicleDTO.getYear());            //年代
      this.setEngine(vehicleDTO.getEngine());        //排量
      this.setVin(vehicleDTO.getChassisNumber());              //车架号
      this.setEngineNo(vehicleDTO.getEngineNo());
      this.setCarDate(vehicleDTO.getCarDate());      //购车日期
      this.setStartMileage(vehicleDTO.getStartMileage());        //进厂里程
      this.setObdMileage(vehicleDTO.getObdMileage());        //进厂里程

      this.setContact(vehicleDTO.getContact());
      this.setMobile(vehicleDTO.getMobile());
      this.setColor(vehicleDTO.getColor());
      this.setGsmObdImei(vehicleDTO.getGsmObdImei());
      this.setGsmObdImeiMoblie(vehicleDTO.getGsmObdImeiMoblie());
      this.setObdId(vehicleDTO.getObdId());
    }

    if (customerVehicleDTO != null) {
      this.setCustomerId(customerVehicleDTO.getCustomerId());                //客户id
      this.setMaintainTime(customerVehicleDTO.getMaintainTime()); //预约保养时间
      this.setExamineTime(customerVehicleDTO.getExamineTime());//预约检车时间
      this.setInsureTime(customerVehicleDTO.getInsureTime()); //预约保险时间
      this.setMaintainMileage(customerVehicleDTO.getMaintainMileage()); //预约里程

      this.setConsumeTimes(NumberUtil.longValue(customerVehicleDTO.getConsumeTimes()));
      this.setTotalConsume(NumberUtil.toReserve(customerVehicleDTO.getTotalConsume(), NumberUtil.MONEY_PRECISION));

      this.setLastMaintainMileage(customerVehicleDTO.getLastMaintainMileage());
      this.setLastMaintainTime(customerVehicleDTO.getLastMaintainTime());
      this.setMaintainTimePeriod(customerVehicleDTO.getMaintainTimePeriod());
      this.setMaintainMileagePeriod(customerVehicleDTO.getMaintainMileagePeriod());
      this.setNextMaintainMileageAccess(customerVehicleDTO.getNextMaintainMileageAccess());
    }
    this.appointServiceDTOs = appointServiceDTOs;
  }

  public void setMaintainTimeStr(String maintainTimeStr) {
    this.maintainTimeStr = maintainTimeStr;
  }

  public void setInsureTimeStr(String insureTimeStr) {
    this.insureTimeStr = insureTimeStr;
  }

  public void setExamineTimeStr(String examineTimeStr) {
    this.examineTimeStr = examineTimeStr;
  }

  public String getMaintainTimeStr() {
    return DateUtil.convertDateLongToDateString("yyyy-MM-dd", maintainTime);
  }

  public String getInsureTimeStr() {
    return DateUtil.convertDateLongToDateString("yyyy-MM-dd", insureTime);
  }

  public String getExamineTimeStr() {
    return DateUtil.convertDateLongToDateString("yyyy-MM-dd", examineTime);
  }

  public Long getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(Long maintainTime) {
    this.maintainTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", maintainTime);
    this.maintainTime = maintainTime;
  }

  public Long getInsureTime() {
    return insureTime;
  }

  public void setInsureTime(Long insureTime) {
    this.insureTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", insureTime);
    this.insureTime = insureTime;
  }

  public Long getExamineTime() {
    return examineTime;
  }

  public void setExamineTime(Long examineTime) {
    this.examineTimeStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd", examineTime);
    this.examineTime = examineTime;
  }



  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
    if (vehicleId != null) {
      setVehicleIdStr(vehicleId.toString());
    } else {
      setVehicleIdStr("");
    }
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

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public Long getCarDate() {
    return carDate;
  }

  public void setCarDate(Long carDate) {
    this.carDate = carDate;
    if (carDate != null && carDate > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date d = new Date(carDate);
      carDateStr = sdf.format(d);
    }
  }

  public String getCarDateStr() {
    return carDateStr;
  }

  public void setCarDateStr(String carDateStr) {
    this.carDateStr = carDateStr;
  }

  public Long getLastDate() {
    return lastDate;
  }

  public void setLastDate(Long lastDate) {
    this.lastDate = lastDate;
  }

  public String getLastDateStr() {
    if (this.getLastDate() == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date d = new Date(this.getLastDate());
    return sdf.format(d);
  }

  public void setLastDateStr(String lastDateStr) {
    this.lastDateStr = lastDateStr;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public double getTotalArrears() {
    return totalArrears;
  }

  public void setTotalArrears(double totalArrears) {
    this.totalArrears = totalArrears;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getStartMileageStr() {
    return startMileageStr;
  }

  public void setStartMileageStr(String startMileageStr) {
    this.startMileageStr = startMileageStr;
  }


  public Double getTotalConsume() {
    return totalConsume;
  }

  public void setTotalConsume(Double totalConsume) {
    this.totalConsume = totalConsume;
  }

  public Long getConsumeTimes() {
    return consumeTimes;
  }

  public void setConsumeTimes(Long consumeTimes) {
    this.consumeTimes = consumeTimes;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public Double getLastMaintainMileage() {
    return lastMaintainMileage;
  }

  public void setLastMaintainMileage(Double lastMaintainMileage) {
    this.lastMaintainMileage = lastMaintainMileage;
  }

  public Long getLastMaintainTime() {
    return lastMaintainTime;
  }

  public void setLastMaintainTime(Long lastMaintainTime) {
    this.lastMaintainTime = lastMaintainTime;
    if(lastMaintainTime != null){
       this.setLastMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY,lastMaintainTime));
     }
  }

  public String getLastMaintainTimeStr() {
    return lastMaintainTimeStr;
  }

  public void setLastMaintainTimeStr(String lastMaintainTimeStr) {
    this.lastMaintainTimeStr = lastMaintainTimeStr;
  }

  public Double getMaintainMileagePeriod() {
    return maintainMileagePeriod;
  }

  public void setMaintainMileagePeriod(Double maintainMileagePeriod) {
    this.maintainMileagePeriod = maintainMileagePeriod;

  }

  public Long getMaintainTimePeriod() {
    return maintainTimePeriod;
  }

  public void setMaintainTimePeriod(Long maintainTimePeriod) {
    this.maintainTimePeriod = maintainTimePeriod;
    if (maintainTimePeriod != null) {
      this.setMaintainTimePeriodStr(maintainTimePeriod);
    }
  }

  public Long getMaintainTimePeriodStr() {
    return maintainTimePeriodStr;
  }

  public void setMaintainTimePeriodStr(Long maintainTimePeriodStr) {
    this.maintainTimePeriodStr = maintainTimePeriodStr;
  }

  public Double getNextMaintainMileageAccess() {
    return nextMaintainMileageAccess;
  }

  public void setNextMaintainMileageAccess(Double nextMaintainMileageAccess) {
    this.nextMaintainMileageAccess = nextMaintainMileageAccess;
    if (nextMaintainMileageAccess != null) {
      if (nextMaintainMileageAccess >= 0) {
        this.setNextMaintainMileageAccessStr("还剩" + nextMaintainMileageAccess + "公里");
      } else {
        this.setNextMaintainMileageAccessStr("超出" + Math.abs(nextMaintainMileageAccess) + "公里");
      }

    }
  }

  public String getNextMaintainMileageAccessStr() {
    return nextMaintainMileageAccessStr;
  }

  public void setNextMaintainMileageAccessStr(String nextMaintainMileageAccessStr) {
    this.nextMaintainMileageAccessStr = nextMaintainMileageAccessStr;
  }
}