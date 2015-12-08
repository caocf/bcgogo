package com.bcgogo.user.dto;

import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.txn.dto.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderItemDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: XJ
 * Date: 10/14/11
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class VehicleDTO implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long licenceAreaId;
  private String licenceNo;
  private String licenceNoRevert;
  private String engineNo;   //发动机号码
  private String vin;
  private Long carDate;
  private String carDateStr;
  private Long carId;
  private String brand;
  private String mfr;
  private String model;
  private String year;
  private String engine;
  private String trim;
  private String color;
  private String memo;

  //新增字段
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private Long engineId;

  private Long virtualBrandId;  //以下四个为虚拟字段
  private Long virtualModelId;
  private Long virtualYearId;
  private Long virtualEngineId;
  private String chassisNumber; //车架号

  private String brandPinYin;
  private String modelPinYin;

  private Double startMileage;
  private VehicleStatus status;

  private String contact;
  private String mobile;

  private Long customerId;
  private CustomerDTO customerDTO;

  //只在前台展示的时候用到了预约信息
  private Long maintainTime;   //预约保养时间
  private String maintainTimeStr;
  private Long insureTime;      //预约保险时间
  private String insureTimeStr;
  private Long examineTime;    //预约验车时间
  private String examineTimeStr;
  private Long maintainMileage;//保养里程
  private Integer vehicleTotalConsumeCount;
  private Double vehicleTotalConsumeAmount;
  private Long vehicleLastConsumeTime;
  private String vehicleLastConsumeTimeStr;
  private Double maintainIntervalsMileage;
  private Integer maintainIntervalsDays;
  private Long createdTime;
  private Double obdMileage;    //OBD最新里程
  private Long mileageLastUpdateTime;//里程最后更新时间
  private Double lastObdMileage;//OBD上报的上一次里程

  private Long lastConsumeOrderId;
  private String lastConsumeOrderIdStr;
  private OrderTypes lastConsumeOrderType;
  private Long obdId;
  private String gsmObdImei;
  private String gsmObdImeiMoblie;
  private boolean isApp;
  private boolean isObd;
  private Double maintainMileagePeriod;//保养里程周期

  //用于构建vehicleSolr
  public VehicleDTO (RepairOrderDTO repairOrderDTO) {
    if (repairOrderDTO != null) {
      if (repairOrderDTO.getBrandId() != null) {
        this.setId(repairOrderDTO.getBrandId());
      }
      if (repairOrderDTO.getModelId() != null) {
        this.setId(repairOrderDTO.getModelId());
      }
      if (repairOrderDTO.getYearId() != null) {
        this.setId(repairOrderDTO.getYearId());
      }
      if (repairOrderDTO.getEngineId() != null) {
        this.setId(repairOrderDTO.getEngineId());
      }
      this.setBrand(repairOrderDTO.getBrand());
      this.setModel(repairOrderDTO.getModel());
      this.setYear(repairOrderDTO.getYear());
      this.setEngine(repairOrderDTO.getEngine());
      this.setVirtualBrandId(repairOrderDTO.getBrandId());
      this.setVirtualModelId(repairOrderDTO.getModelId());
      this.setVirtualYearId(repairOrderDTO.getYearId());
      this.setVirtualEngineId(repairOrderDTO.getEngineId());
      this.setBrandPinYin(PinyinUtil.converterToFirstSpell(repairOrderDTO.getBrand()).toLowerCase());
      this.setModelPinYin(PinyinUtil.converterToFirstSpell(repairOrderDTO.getModel()).toLowerCase());
    }
  }

  //用于构建vehicleSolr
  public VehicleDTO (AppointOrderDTO appointOrderDTO) {
    if (appointOrderDTO != null) {
      if (appointOrderDTO.getVehicleBrandId() != null) {
        this.setId(appointOrderDTO.getVehicleBrandId());
      }
      if (appointOrderDTO.getVehicleModelId() != null) {
        this.setId(appointOrderDTO.getVehicleModelId());
      }
      this.setBrand(appointOrderDTO.getVehicleBrand());
      this.setModel(appointOrderDTO.getVehicleModel());
      this.setVirtualBrandId(appointOrderDTO.getVehicleBrandId());
      this.setVirtualModelId(appointOrderDTO.getVehicleModelId());
      this.setBrandPinYin(PinyinUtil.converterToFirstSpell(appointOrderDTO.getVehicleBrand()).toLowerCase());
      this.setModelPinYin(PinyinUtil.converterToFirstSpell(appointOrderDTO.getVehicleModel()).toLowerCase());
    }
  }

   //用于构建vehicleSolr
    public VehicleDTO(ProductDTO productDTO) {
      if (productDTO != null) {
        if (productDTO.getProductVehicleBrandId() != null) {
          this.setId(productDTO.getProductVehicleBrandId());
        }
        if (productDTO.getProductVehicleModelId() != null) {
          this.setId(productDTO.getProductVehicleModelId());
        }
        if (productDTO.getProductVehicleYearId() != null) {
          this.setId(productDTO.getProductVehicleYearId());
        }
        if (productDTO.getProductVehicleEngineId() != null) {
          this.setId(productDTO.getProductVehicleEngineId());
        }
        this.setBrand(productDTO.getProductVehicleBrand());
        this.setModel(productDTO.getProductVehicleModel());
        this.setYear(productDTO.getProductVehicleYear());
        this.setEngine(productDTO.getProductVehicleEngine());
        this.setVirtualBrandId(productDTO.getProductVehicleBrandId());
        this.setVirtualModelId(productDTO.getProductVehicleModelId());
        this.setVirtualYearId(productDTO.getProductVehicleYearId());
        this.setVirtualEngineId(productDTO.getProductVehicleEngineId());
        this.setBrandPinYin(PinyinUtil.converterToFirstSpell(productDTO.getProductVehicleBrand()).toLowerCase());
        this.setModelPinYin(PinyinUtil.converterToFirstSpell(productDTO.getProductVehicleModel()).toLowerCase());
      }
    }
   //用于构建vehicleSolr
  public VehicleDTO (SalesOrderItemDTO salesOrderItemDTO) {
    if (salesOrderItemDTO != null) {
      if (salesOrderItemDTO.getVehicleBrandId() != null) {
        this.setId(salesOrderItemDTO.getVehicleBrandId());
      }
      if (salesOrderItemDTO.getVehicleModelId() != null) {
        this.setId(salesOrderItemDTO.getVehicleModelId());
      }
      if (salesOrderItemDTO.getVehicleYearId() != null) {
        this.setId(salesOrderItemDTO.getVehicleYearId());
      }
      if (salesOrderItemDTO.getVehicleEngineId() != null) {
        this.setId(salesOrderItemDTO.getVehicleEngineId());
      }
      this.setBrand(salesOrderItemDTO.getVehicleBrand());
      this.setModel(salesOrderItemDTO.getVehicleModel());
      this.setYear(salesOrderItemDTO.getVehicleYear());
      this.setEngine(salesOrderItemDTO.getVehicleEngine());
      this.setVirtualBrandId(salesOrderItemDTO.getVehicleBrandId());
      this.setVirtualModelId(salesOrderItemDTO.getVehicleModelId());
      this.setVirtualYearId(salesOrderItemDTO.getVehicleYearId());
      this.setVirtualEngineId(salesOrderItemDTO.getVehicleEngineId());
      this.setBrandPinYin(PinyinUtil.converterToFirstSpell(salesOrderItemDTO.getBrand()).toLowerCase());
      this.setModelPinYin(PinyinUtil.converterToFirstSpell(salesOrderItemDTO.getModel()).toLowerCase());
    }
  }

  //用于构建vehicleSolr
  public VehicleDTO (PurchaseInventoryItemDTO purchaseInventoryItemDTO) {
    if (purchaseInventoryItemDTO != null) {
      if (purchaseInventoryItemDTO.getVehicleBrandId() != null) {
        this.setId(purchaseInventoryItemDTO.getVehicleBrandId());
      }
      if (purchaseInventoryItemDTO.getVehicleModelId() != null) {
        this.setId(purchaseInventoryItemDTO.getVehicleModelId());
      }
      if (purchaseInventoryItemDTO.getVehicleYearId() != null) {
        this.setId(purchaseInventoryItemDTO.getVehicleYearId());
      }
      if (purchaseInventoryItemDTO.getVehicleEngineId() != null) {
        this.setId(purchaseInventoryItemDTO.getVehicleEngineId());
      }
      this.setBrand(purchaseInventoryItemDTO.getVehicleBrand());
      this.setModel(purchaseInventoryItemDTO.getVehicleModel());
      this.setYear(purchaseInventoryItemDTO.getVehicleYear());
      this.setEngine(purchaseInventoryItemDTO.getVehicleEngine());
      this.setVirtualBrandId(purchaseInventoryItemDTO.getVehicleBrandId());
      this.setVirtualModelId(purchaseInventoryItemDTO.getVehicleModelId());
      this.setVirtualYearId(purchaseInventoryItemDTO.getVehicleYearId());
      this.setVirtualEngineId(purchaseInventoryItemDTO.getVehicleEngineId());
      this.setBrandPinYin(PinyinUtil.converterToFirstSpell(purchaseInventoryItemDTO.getBrand()).toLowerCase());
      this.setModelPinYin(PinyinUtil.converterToFirstSpell(purchaseInventoryItemDTO.getModel()).toLowerCase());
    }
  }



  public VehicleDTO(WashBeautyOrderDTO washBeautyOrderDTO) {
    if (washBeautyOrderDTO != null) {
      if (washBeautyOrderDTO.getBrandId() != null) {
        this.setId(washBeautyOrderDTO.getBrandId());
      }
      if (washBeautyOrderDTO.getModelId() != null) {
        this.setId(washBeautyOrderDTO.getModelId());
      }

      this.setBrand(washBeautyOrderDTO.getBrand());
      this.setModel(washBeautyOrderDTO.getModel());
      this.setVirtualBrandId(washBeautyOrderDTO.getBrandId());
      this.setVirtualModelId(washBeautyOrderDTO.getModelId());
      this.setBrandPinYin(PinyinUtil.converterToFirstSpell(washBeautyOrderDTO.getBrand()).toLowerCase());
      this.setModelPinYin(PinyinUtil.converterToFirstSpell(washBeautyOrderDTO.getModel()).toLowerCase());
    }
  }

  public Integer getMaintainIntervalsDays() {
    return maintainIntervalsDays;
  }

  public void setMaintainIntervalsDays(Integer maintainIntervalsDays) {
    this.maintainIntervalsDays = maintainIntervalsDays;
  }

  public Double getMaintainIntervalsMileage() {
    return maintainIntervalsMileage;
  }

  public void setMaintainIntervalsMileage(Double maintainIntervalsMileage) {
    this.maintainIntervalsMileage = maintainIntervalsMileage;
  }

  public Integer getVehicleTotalConsumeCount() {
    return vehicleTotalConsumeCount;
  }

  public void setVehicleTotalConsumeCount(Integer vehicleTotalConsumeCount) {
    this.vehicleTotalConsumeCount = vehicleTotalConsumeCount;
  }

  public Double getVehicleTotalConsumeAmount() {
    return vehicleTotalConsumeAmount;
  }

  public void setVehicleTotalConsumeAmount(Double vehicleTotalConsumeAmount) {
    this.vehicleTotalConsumeAmount = vehicleTotalConsumeAmount;
  }

  public Long getVehicleLastConsumeTime() {
    return vehicleLastConsumeTime;
  }

  public void setVehicleLastConsumeTime(Long vehicleLastConsumeTime) {
    this.vehicleLastConsumeTime = vehicleLastConsumeTime;
    if (vehicleLastConsumeTime != null) {
      this.vehicleLastConsumeTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, this.vehicleLastConsumeTime);
    }
  }

  public String getVehicleLastConsumeTimeStr() {
    return vehicleLastConsumeTimeStr;
  }

  public void setVehicleLastConsumeTimeStr(String vehicleLastConsumeTimeStr) {
    this.vehicleLastConsumeTimeStr = vehicleLastConsumeTimeStr;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  public VehicleDTO() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }else {
      setIdStr("");
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getLicenceAreaId() {
    return licenceAreaId;
  }

  public void setLicenceAreaId(Long licenceAreaId) {
    this.licenceAreaId = licenceAreaId;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getLicenceNoRevert() {
    return licenceNoRevert;
  }

  public void setLicenceNoRevert(String licenceNoRevert) {
    this.licenceNoRevert = licenceNoRevert;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
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
		if (carDate != null) {
			this.setCarDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, this.carDate));
		}
	}

  public Long getCarId() {
    return carId;
  }

  public void setCarId(Long carId) {
    this.carId = carId;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
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

  public String getTrim() {
    return trim;
  }

  public void setTrim(String trim) {
    this.trim = trim;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }

  public Long getVirtualBrandId() {
    return virtualBrandId;
  }

  public void setVirtualBrandId(Long virtualBrandId) {
    this.virtualBrandId = virtualBrandId;
  }

  public Long getVirtualModelId() {
    return virtualModelId;
  }

  public void setVirtualModelId(Long virtualModelId) {
    this.virtualModelId = virtualModelId;
  }

  public Long getVirtualYearId() {
    return virtualYearId;
  }

  public void setVirtualYearId(Long virtualYearId) {
    this.virtualYearId = virtualYearId;
  }

  public Long getVirtualEngineId() {
    return virtualEngineId;
  }

  public void setVirtualEngineId(Long virtualEngineId) {
    this.virtualEngineId = virtualEngineId;
  }

  public String getBrandPinYin() {
    return brandPinYin;
  }

  public void setBrandPinYin(String brandPinYin) {
    this.brandPinYin = brandPinYin;
  }

  public String getModelPinYin() {
    return modelPinYin;
  }

  public void setModelPinYin(String modelPinYin) {
    this.modelPinYin = modelPinYin;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public VehicleStatus getStatus() {
    return status;
  }

  public void setStatus(VehicleStatus status) {
    this.status = status;
  }

	public String getCarDateStr() {
		return carDateStr;
	}

	public void setCarDateStr(String carDateStr) {
		this.carDateStr = carDateStr;
	}

  public Long getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(Long maintainTime) {
    this.maintainTime = maintainTime;
    if(maintainTime!=null){
      this.maintainTimeStr=DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, maintainTime);
      try {
        this.maintainIntervalsDays = DateUtil.fieldDifference(maintainTime, DateUtil.getTheDayTime(), Calendar.DATE);
        if(maintainTime<DateUtil.getTheDayTime()){
          this.maintainIntervalsDays= this.maintainIntervalsDays*-1;
        }
      } catch (ParseException e) {
      }

    }
  }

  public String getMaintainTimeStr() {
    return maintainTimeStr;
  }

  public void setMaintainTimeStr(String maintainTimeStr) {
    this.maintainTimeStr = maintainTimeStr;
  }

  public Long getInsureTime() {
    return insureTime;
  }

  public void setInsureTime(Long insureTime) {
    this.insureTime = insureTime;
    if(insureTime!=null){
      this.insureTimeStr=DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,insureTime);
    }
  }

  public String getInsureTimeStr() {
    return insureTimeStr;
  }

  public void setInsureTimeStr(String insureTimeStr) {
    this.insureTimeStr = insureTimeStr;
  }

  public Long getExamineTime() {
    return examineTime;
  }

  public void setExamineTime(Long examineTime) {
    this.examineTime = examineTime;
  }

  public String getExamineTimeStr() {
    return examineTimeStr;
  }

  public void setExamineTimeStr(String examineTimeStr) {
    this.examineTimeStr = examineTimeStr;
  }

  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public Long getMileageLastUpdateTime() {
    return mileageLastUpdateTime;
  }

  public void setMileageLastUpdateTime(Long mileageLastUpdateTime) {
    this.mileageLastUpdateTime = mileageLastUpdateTime;
  }

  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }

  public CustomerDTO getCustomerDTO() {
    return customerDTO;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    this.customerDTO = customerDTO;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public Long getLastConsumeOrderId() {
    return lastConsumeOrderId;
  }

  public void setLastConsumeOrderId(Long lastConsumeOrderId) {
    this.lastConsumeOrderId = lastConsumeOrderId;
    if(lastConsumeOrderId!=null) lastConsumeOrderIdStr=lastConsumeOrderId.toString();
  }

  public String getLastConsumeOrderIdStr() {
    return lastConsumeOrderIdStr;
  }

  public void setLastConsumeOrderIdStr(String lastConsumeOrderIdStr) {
    this.lastConsumeOrderIdStr = lastConsumeOrderIdStr;
  }

  public OrderTypes getLastConsumeOrderType() {
    return lastConsumeOrderType;
  }

  public void setLastConsumeOrderType(OrderTypes lastConsumeOrderType) {
    this.lastConsumeOrderType = lastConsumeOrderType;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

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

  public Double getLastObdMileage() {
    return lastObdMileage;
  }

  public void setLastObdMileage(Double lastObdMileage) {
    this.lastObdMileage = lastObdMileage;
  }

  public void setCarDTO(CarDTO carDTO) {
    if (carDTO == null) {
      return;
    }
    if (StringUtils.isNotBlank(carDTO.getId()) && NumberUtils.isNumber(carDTO.getId())) {
      this.setId(Long.parseLong(carDTO.getId()));
    }
    this.setLicenceNo(carDTO.getLicenceNo());
    this.setBrand(carDTO.getBrand());
    this.setModel(carDTO.getModel());
    this.setYear(carDTO.getYear());
    this.setEngine(carDTO.getEngine());
    this.setChassisNumber(carDTO.getChassisNumber());
    this.setEngineNo(carDTO.getEngineNo());
    this.setStartMileage(carDTO.getStartMileage());
    this.setObdMileage(carDTO.getObdMileage());
    this.setContact(carDTO.getContact());
    this.setMobile(carDTO.getMobile());
    this.setColor(carDTO.getColor());
    this.setMaintainMileagePeriod(carDTO.getMaintainMileagePeriod());
    this.setGsmObdImei(carDTO.getGsmObdImei());
    this.setGsmObdImeiMoblie(carDTO.getGsmObdImeiMoblie());
  }

	public VehicleDTO clone() {
		VehicleDTO vehicleDTO = new VehicleDTO();
		vehicleDTO.setId(id);
		vehicleDTO.setShopId(shopId);
		vehicleDTO.setLicenceAreaId(licenceAreaId);
		vehicleDTO.setLicenceNo(licenceNo);
		vehicleDTO.setLicenceNoRevert(licenceNoRevert);
		vehicleDTO.setEngineNo(engineNo);
		vehicleDTO.setVin(vin);
		vehicleDTO.setCarDate(carDate);
		vehicleDTO.setCarId(carId);
		vehicleDTO.setBrand(brand);
		vehicleDTO.setMfr(mfr);
		vehicleDTO.setModel(model);
		vehicleDTO.setYear(year);
		vehicleDTO.setEngine(engine);
		vehicleDTO.setTrim(trim);
		vehicleDTO.setColor(color);
		vehicleDTO.setMemo(memo);
		vehicleDTO.setBrandId(brandId);
		vehicleDTO.setMfrId(mfrId);
		vehicleDTO.setModelId(modelId);
		vehicleDTO.setYearId(yearId);
		vehicleDTO.setEngineId(engineId);
		vehicleDTO.setVirtualBrandId(virtualBrandId);
		vehicleDTO.setVirtualModelId(virtualModelId);
		vehicleDTO.setVirtualYearId(virtualYearId);
		vehicleDTO.setVirtualEngineId(virtualEngineId);
		vehicleDTO.setChassisNumber(chassisNumber);
		vehicleDTO.setBrandPinYin(brandPinYin);
		vehicleDTO.setModelPinYin(modelPinYin);
    vehicleDTO.setStartMileage(startMileage);
    vehicleDTO.setContact(contact);
    vehicleDTO.setMobile(mobile);
    vehicleDTO.setObdMileage(obdMileage);
		return vehicleDTO;
	}

  /**
   * 比较不同，只比较：
   * vehicleId, licenceNo, brand, model
   * @param vehicleDTO
   * @return
   */
  public boolean compareSame(VehicleDTO vehicleDTO) {
    if(vehicleDTO == null){
      return false;
    }
    if(!NumberUtil.compare(vehicleDTO.getId(), getId())){
      return false;
    }
    if(!StringUtil.compareSame(vehicleDTO.getLicenceNo(), getLicenceNo())){
      return false;
    }
    if(!StringUtil.compareSame(vehicleDTO.getBrand(), getBrand())){
      return false;
    }
    if(!StringUtil.compareSame(vehicleDTO.getModel(), getModel())){
      return false;
    }
    return true;
  }

  public void fromWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) {
    this.setShopId(washBeautyOrderDTO.getShopId());
    this.setLicenceNo(washBeautyOrderDTO.getLicenceNo());
    this.setBrand(washBeautyOrderDTO.getBrand());
    this.setModel(washBeautyOrderDTO.getModel());
    this.setBrandId(washBeautyOrderDTO.getBrandId());
    this.setModelId(washBeautyOrderDTO.getModelId());
    this.setContact(washBeautyOrderDTO.getVehicleContact());
    this.setMobile(washBeautyOrderDTO.getVehicleMobile());
    this.setColor(washBeautyOrderDTO.getVehicleColor());
    this.setEngineNo(washBeautyOrderDTO.getVehicleEngineNo());
    this.setChassisNumber(washBeautyOrderDTO.getVehicleChassisNo());
  }

  public void fromAppointOrderDTO(AppointOrderDTO appointOrderDTO) {
    if(appointOrderDTO != null){
      this.setShopId(appointOrderDTO.getShopId());
      this.setLicenceNo(appointOrderDTO.getVehicleNo());
      this.setBrand(appointOrderDTO.getVehicleBrand());
      this.setModel(appointOrderDTO.getVehicleModel());
      this.setBrandId(appointOrderDTO.getVehicleBrandId());
      this.setModelId(appointOrderDTO.getVehicleModelId());
      this.setContact(appointOrderDTO.getVehicleContact());
      this.setMobile(appointOrderDTO.getVehicleMobile());
    }
  }

  public void setCustomerVehicleInfo(CustomerVehicleDTO customerVehicleDTO) {
    if(customerVehicleDTO != null){
      this.setExamineTime(customerVehicleDTO.getExamineTime());
      this.setInsureTime(customerVehicleDTO.getInsureTime());
      this.setMaintainTime(customerVehicleDTO.getMaintainTime());
      this.setExamineTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,customerVehicleDTO.getExamineTime()));
      this.setMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,customerVehicleDTO.getMaintainTime()));
      this.setInsureTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,customerVehicleDTO.getInsureTime()));
      this.setMaintainMileage(customerVehicleDTO.getMaintainMileage());
    }
  }

  public void from(CustomerVehicleDTO dto) {
    setMaintainMileage(dto.getMaintainMileage());
    setMaintainTime(dto.getMaintainTime());
    setExamineTime(dto.getExamineTime());
    setInsureTime(dto.getInsureTime());
  }

  public AppVehicleDTO toAppVehicleDTO() {
    AppVehicleDTO dto = new AppVehicleDTO();
    dto.setContact(getContact());
    dto.setMobile(getMobile());
    dto.setVehicleNo(getLicenceNo());
    dto.setVehicleModel(getModel());
    dto.setVehicleBrand(getBrand());
    dto.setCurrentMileage(getStartMileage());
    if (getMaintainMileage() != null)
      dto.setNextMaintainMileage(Double.valueOf(getMaintainMileage()));
    dto.setNextMaintainTime(getMaintainTime());
    dto.setNextInsuranceTime(getInsureTime());
    dto.setNextExamineTime(getExamineTime());

    dto.setEngineNo(getEngineNo());
    dto.setVehicleVin(getChassisNumber());
    return dto;
  }

  public void addFromAppVehicleDTO(AppVehicleDTO appVehicleDTO) {
    if (appVehicleDTO != null) {
      this.setLicenceNo(appVehicleDTO.getVehicleNo());
      this.setBrand(appVehicleDTO.getVehicleBrand());
      this.setModel(appVehicleDTO.getVehicleModel());
      this.setObdMileage(appVehicleDTO.getCurrentMileage());
      this.setMobile(appVehicleDTO.getMobile());
      this.setContact(appVehicleDTO.getContact());
    }
  }

  public boolean getIsApp() {
    return isApp;
  }

  public void setIsApp(boolean isApp) {
    this.isApp = isApp;
  }

  public boolean getIsObd() {
    return isObd;
  }

  public void setIsObd(boolean isObd) {
    this.isObd = isObd;
  }

  public Double getMaintainMileagePeriod() {
    return maintainMileagePeriod;
  }

  public void setMaintainMileagePeriod(Double maintainMileagePeriod) {
    this.maintainMileagePeriod = maintainMileagePeriod;
  }
}
