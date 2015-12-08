package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.InsuranceCompanyDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.MoneyUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午2:34
 * To change this template use File | Settings | File Templates.
 */
public class InsuranceOrderDTO extends BcgogoOrderDto {
  private Long editorId;
  private String editor;
  private Long vestDate;  //最后保存时间
  private Long repairOrderId;
  private String repairOrderIdStr;
  private String repairOrderReceiptNo;
  private String policyNo;   //保险单号
  private String reportNo;//报案编号
  private Long insuranceCompanyId; //保险公司
  private String insuranceCompany;

  private OrderStatus status; //保险单状态
 private String statusStr;//insuranceStatus对应的string


  private Long insureStartDate;//投保日期
  private String insureStartDateStr;//投保日期
  private Long insureEndDate;//到期日期
  private String insureEndDateStr;//到期日期
  private Long customerId;//被保险人
  private String customer;
  private String licenceNo;//车牌
  private Long vehicleId;//车牌
  private String driver;//驾驶人
  private String drivingNo;//驾驶证号
  private String mobile;
  private String brand; // 车辆品牌
  private Long brandId; // 车辆品牌
  private String model; //车型
  private Long modelId; //车型
  private String chassisNumber; //车架号
  private String engineNumber; //发动机号
  private String reporter; //报案人
  private String reporterContact; //报案人联系电话
  private Long reportDate; //报案时间
  private String reportDateStr; //报案时间
  private Long accidentDate; //出险时间
  private String accidentDateStr; //出险时间
  private String accidentAddress; //出险地点

  private Long surveyDate; //查勘时间
  private String surveyDateStr; //查勘时间
  private String surveyAddress; //查勘地点
  private String accidentHandling; //事故处理方式
  private String accidentLiability; //事故责任
  private String firstSurveyAddress; //是否第一现场勘查
  private String accidentType; //事故类型
  private String surveyOpinion; //查勘人员意见
  private String accidentCause; //出险原因
  private List<String> relateInsuranceItems; //涉及险种
  private Long estimateDate; //定损时间
  private String estimateDateStr; //定损时间
  private String estimateAddress; //定损地点
  private Double insuranceCost; //施救费
  private Double scrapValue; //扣减残值
  private String scrapApproach; //残值处理方式
  private InsuranceOrderItemDTO[] itemDTOs; //更换项目
  private List<InsuranceOrderServiceDTO> serviceDTOs; //修理项目
  private Double claims; //理赔金额
  private Double claimsPercentage; //赔付比例
  private Double personalClaims; //个人理赔金额
  private Double personalClaimsPercentage; //个人赔付比例
  private Map<String,String> relateInsuranceMap = new HashMap<String, String>();
    //分页条件
  private static final int DEFAULT_PAGE_SIZE = 5;
  private static final int DEFAULT_PAGE_INDEX = 1;
  private Integer pageSize = DEFAULT_PAGE_SIZE;
  private Integer pageNo = DEFAULT_PAGE_INDEX;
  private Integer startPageNo = DEFAULT_PAGE_INDEX;

  //查询条件
  private String startTimeStr;
  private String endTimeStr;
  private Long startTime;
  private Long endTime;
  private String sortStatus;

  private String totalStr;
  private int itemLength;
  private int serviceLength;
  private int length;
  private Long repairDraftOrderId;
  public void fromRepairOrderDTO(RepairOrderDTO repairOrderDTO) {
    if (repairOrderDTO == null) {
      return;
    }
    setRepairOrderId(repairOrderDTO.getId());
    setRepairOrderReceiptNo(repairOrderDTO.getReceiptNo());

    if (!ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs())) {
      List<InsuranceOrderItemDTO> insuranceOrderItemDTOs = new ArrayList<InsuranceOrderItemDTO>();
      for (RepairOrderItemDTO itemDTO : repairOrderDTO.getItemDTOs()) {
        InsuranceOrderItemDTO insuranceOrderItemDTO = new InsuranceOrderItemDTO();
        insuranceOrderItemDTO.fromRepairOrderItemDTO(itemDTO);
        insuranceOrderItemDTOs.add(insuranceOrderItemDTO);
      }
      this.setItemDTOs(insuranceOrderItemDTOs.toArray(new InsuranceOrderItemDTO[insuranceOrderItemDTOs.size()]));
    }
    if (!ArrayUtil.isEmpty(repairOrderDTO.getServiceDTOs())) {
      List<InsuranceOrderServiceDTO> insuranceOrderItemDTOs = new ArrayList<InsuranceOrderServiceDTO>();
      for (RepairOrderServiceDTO serviceDTO : repairOrderDTO.getServiceDTOs()) {
        InsuranceOrderServiceDTO insuranceOrderServiceDTO = new InsuranceOrderServiceDTO();
        insuranceOrderServiceDTO.fromRepairOrderServiceDTO(serviceDTO);
        insuranceOrderItemDTOs.add(insuranceOrderServiceDTO);
      }
      this.setServiceDTOs(insuranceOrderItemDTOs);
    }
  }

  public void setVehicleDTO(VehicleDTO vehicleDTO) {
    if (vehicleDTO == null) {
      return;
    }
    setVehicleId(vehicleDTO.getId());
    setLicenceNo(vehicleDTO.getLicenceNo());
    setBrand(vehicleDTO.getBrand());
    setBrandId(vehicleDTO.getBrandId());
    setModel(vehicleDTO.getModel());
    setModelId(vehicleDTO.getModelId());
    setChassisNumber(vehicleDTO.getChassisNumber());
    setEngineNumber(vehicleDTO.getEngineNo());
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO == null) {
      return;
    }
    setCustomerId(customerDTO.getId());
    setCustomer(customerDTO.getName());
    setMobile(customerDTO.getMobile());
//    setDriver(customerDTO.getName());
//    setReporter(customerDTO.getName());
//    setReporterContact(StringUtils.isNotBlank(customerDTO.getMobile()) ? customerDTO.getMobile() : customerDTO.getLandLine());
  }

  public void initDate() {
    try {
      insureEndDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, getInsureEndDateStr());
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      insureStartDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, getInsureStartDateStr());
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      surveyDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, getSurveyDateStr());
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      estimateDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, getEstimateDateStr());
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      reportDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, getReportDateStr());
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      accidentDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, getAccidentDateStr());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public InsuranceOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(InsuranceOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }


  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
    if(repairOrderId!=null){
        this.repairOrderIdStr=repairOrderId.toString();
    }
  }

    public String getRepairOrderIdStr() {
        return repairOrderIdStr;
    }

    public void setRepairOrderIdStr(String repairOrderIdStr) {
        this.repairOrderIdStr = repairOrderIdStr;
    }

    public String getRepairOrderReceiptNo() {
    return repairOrderReceiptNo;
  }

  public void setRepairOrderReceiptNo(String repairOrderReceiptNo) {
    this.repairOrderReceiptNo = repairOrderReceiptNo;
  }

  public String getPolicyNo() {
    return policyNo;
  }

  public void setPolicyNo(String policyNo) {
    this.policyNo = policyNo;
  }

  public String getReportNo() {
    return reportNo;
  }

  public void setReportNo(String reportNo) {
    this.reportNo = reportNo;
  }

  public Long getInsuranceCompanyId() {
    return insuranceCompanyId;
  }

  public void setInsuranceCompanyId(Long insuranceCompanyId) {
    this.insuranceCompanyId = insuranceCompanyId;
  }

  public String getInsuranceCompany() {
    return insuranceCompany;
  }

  public void setInsuranceCompany(String insuranceCompany) {
    this.insuranceCompany = insuranceCompany;
  }

  public Long getInsureStartDate() {
    return insureStartDate;
  }

  public void setInsureStartDate(Long insureStartDate) {
    this.insureStartDate = insureStartDate;
    setInsureStartDateStr(DateUtil.convertDateLongToString(insureStartDate, DateUtil.YEAR_MONTH_DATE));
  }

  public String getInsureStartDateStr() {
    return insureStartDateStr;
  }

  public void setInsureStartDateStr(String insureStartDateStr) {
    this.insureStartDateStr = insureStartDateStr;
  }

  public Long getInsureEndDate() {
    return insureEndDate;
  }

  public void setInsureEndDate(Long insureEndDate) {
    this.insureEndDate = insureEndDate;
    setInsureEndDateStr(DateUtil.convertDateLongToString(insureEndDate, DateUtil.YEAR_MONTH_DATE));
  }

  public String getInsureEndDateStr() {
    return insureEndDateStr;
  }

  public void setInsureEndDateStr(String insureEndDateStr) {
    this.insureEndDateStr = insureEndDateStr;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getDrivingNo() {
    return drivingNo;
  }

  public void setDrivingNo(String drivingNo) {
    this.drivingNo = drivingNo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
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

  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  public String getEngineNumber() {
    return engineNumber;
  }

  public void setEngineNumber(String engineNumber) {
    this.engineNumber = engineNumber;
  }

  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public String getReporterContact() {
    return reporterContact;
  }

  public void setReporterContact(String reporterContact) {
    this.reporterContact = reporterContact;
  }

  public Long getReportDate() {
    return reportDate;
  }

  public void setReportDate(Long reportDate) {
    this.reportDate = reportDate;
    setReportDateStr(DateUtil.convertDateLongToString(reportDate, DateUtil.YEAR_MONTH_DATE));
  }

  public String getReportDateStr() {
    return reportDateStr;
  }

  public void setReportDateStr(String reportDateStr) {
    this.reportDateStr = reportDateStr;
  }

  public Long getAccidentDate() {
    return accidentDate;
  }

  public void setAccidentDate(Long accidentDate) {
    this.accidentDate = accidentDate;
    setAccidentDateStr(DateUtil.convertDateLongToString(accidentDate, DateUtil.YEAR_MONTH_DATE));
  }

  public String getAccidentDateStr() {
    return accidentDateStr;
  }

  public void setAccidentDateStr(String accidentDateStr) {
    this.accidentDateStr = accidentDateStr;
  }

  public String getAccidentAddress() {
    return accidentAddress;
  }

  public void setAccidentAddress(String accidentAddress) {
    this.accidentAddress = accidentAddress;
  }

  public Long getSurveyDate() {
    return surveyDate;
  }

  public void setSurveyDate(Long surveyDate) {
    this.surveyDate = surveyDate;
    setSurveyDateStr(DateUtil.convertDateLongToString(surveyDate, DateUtil.YEAR_MONTH_DATE));
  }

  public String getSurveyDateStr() {
    return surveyDateStr;
  }

  public void setSurveyDateStr(String surveyDateStr) {
    this.surveyDateStr = surveyDateStr;
  }

  public String getSurveyAddress() {
    return surveyAddress;
  }

  public void setSurveyAddress(String surveyAddress) {
    this.surveyAddress = surveyAddress;
  }

  public String getAccidentHandling() {
    return accidentHandling;
  }

  public void setAccidentHandling(String accidentHandling) {
    this.accidentHandling = accidentHandling;
  }

  public String getAccidentLiability() {
    return accidentLiability;
  }

  public void setAccidentLiability(String accidentLiability) {
    this.accidentLiability = accidentLiability;
  }

  public String getFirstSurveyAddress() {
    return firstSurveyAddress;
  }

  public void setFirstSurveyAddress(String firstSurveyAddress) {
    this.firstSurveyAddress = firstSurveyAddress;
  }

  public String getAccidentType() {
    return accidentType;
  }

  public void setAccidentType(String accidentType) {
    this.accidentType = accidentType;
  }

  public String getSurveyOpinion() {
    return surveyOpinion;
  }

  public void setSurveyOpinion(String surveyOpinion) {
    this.surveyOpinion = surveyOpinion;
  }

  public String getAccidentCause() {
    return accidentCause;
  }

  public void setAccidentCause(String accidentCause) {
    this.accidentCause = accidentCause;
  }

  public List<String> getRelateInsuranceItems() {
    return relateInsuranceItems;
  }

  public void setRelateInsuranceItems(List<String> relateInsuranceItems) {
    this.relateInsuranceItems = relateInsuranceItems;
  }

  public Long getEstimateDate() {
    return estimateDate;
  }

  public void setEstimateDate(Long estimateDate) {
    this.estimateDate = estimateDate;
    setEstimateDateStr(DateUtil.convertDateLongToString(estimateDate, DateUtil.YEAR_MONTH_DATE));
  }

  public String getEstimateDateStr() {
    return estimateDateStr;
  }

  public void setEstimateDateStr(String estimateDateStr) {
    this.estimateDateStr = estimateDateStr;
  }

  public String getEstimateAddress() {
    return estimateAddress;
  }

  public void setEstimateAddress(String estimateAddress) {
    this.estimateAddress = estimateAddress;
  }

  public Double getInsuranceCost() {
    return insuranceCost;
  }

  public void setInsuranceCost(Double insuranceCost) {
    this.insuranceCost = insuranceCost;
  }

  public Double getScrapValue() {
    return scrapValue;
  }

  public void setScrapValue(Double scrapValue) {
    this.scrapValue = scrapValue;
  }

  public String getScrapApproach() {
    return scrapApproach;
  }

  public void setScrapApproach(String scrapApproach) {
    this.scrapApproach = scrapApproach;
  }

  public List<InsuranceOrderServiceDTO> getServiceDTOs() {
    return serviceDTOs;
  }

  public void setServiceDTOs(List<InsuranceOrderServiceDTO> serviceDTOs) {
    this.serviceDTOs = serviceDTOs;
  }

  public Double getClaims() {
    return claims;
  }

  public void setClaims(Double claims) {
    this.claims = claims;
  }


    public Double getPersonalClaims() {
        return personalClaims;
    }

    public void setPersonalClaims(Double personalClaims) {
        this.personalClaims = personalClaims;
    }

    public Double getPersonalClaimsPercentage() {
        return personalClaimsPercentage;
    }

    public void setPersonalClaimsPercentage(Double personalClaimsPercentage) {
        this.personalClaimsPercentage = personalClaimsPercentage;
    }

    public Double getClaimsPercentage() {
    return claimsPercentage;
  }

  public void setClaimsPercentage(Double claimsPercentage) {
    this.claimsPercentage = claimsPercentage;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public void setInsuranceCompanyDTO(InsuranceCompanyDTO insuranceCompanyDTO) {
    this.setInsuranceCompany(insuranceCompanyDTO.getName());
    this.setInsuranceCompanyId(insuranceCompanyDTO.getId());
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
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

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getSortStatus() {
    return sortStatus;
  }

  public void setSortStatus(String sortStatus) {
    this.sortStatus = sortStatus;
  }

  public void initSearchTime() {
    Long startTime, endTime;
    try {
      startTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getStartTimeStr());
    } catch (Exception e) {
      startTime = null;
      this.setStartTimeStr(null);
    }
    try {
      endTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getEndTimeStr());
    } catch (Exception e) {
      endTime = null;
      this.setEndTimeStr(null);
    }
    if (startTime != null && endTime != null) {
      if (startTime > endTime) {
        Long temp = endTime;
        endTime = startTime;
        startTime = temp;
      }
    }
    if (endTime != null) {
      endTime = DateUtil.getInnerDayTime(endTime, 1);
    }
    this.setStartTime(startTime);
    this.setEndTime(endTime);
  }

  public Map<String, String> getRelateInsuranceMap() {
    relateInsuranceMap.put("交通事故责任强制保险","");
    relateInsuranceMap.put("车损险","");
    relateInsuranceMap.put("三者险","");
    relateInsuranceMap.put("车上人员责任险","");
    relateInsuranceMap.put("车身划痕险","");
    relateInsuranceMap.put("玻璃单独破碎险","");
    relateInsuranceMap.put("其他","");

    if(CollectionUtils.isEmpty(this.relateInsuranceItems))
    {
      return relateInsuranceMap;
    }

    for(String str : this.relateInsuranceItems)
    {
      if("交通事故责任强制保险".equals(str))
      {
        relateInsuranceMap.put("交通事故责任强制保险","交通事故责任强制保险");
        continue;
      }
      if("车损险".equals(str))
      {
        relateInsuranceMap.put("车损险","车损险");
        continue;
      }
      if("三者险".equals(str))
      {
        relateInsuranceMap.put("三者险","三者险");
        continue;
      }
      if("车上人员责任险".equals(str))
      {
        relateInsuranceMap.put("车上人员责任险","车上人员责任险");
        continue;
      }
      if("车身划痕险".equals(str))
      {
        relateInsuranceMap.put("车身划痕险","车身划痕险");
        continue;
      }
      if("玻璃单独破碎险".equals(str))
      {
        relateInsuranceMap.put("玻璃单独破碎险","玻璃单独破碎险");
        continue;
      }
      if("其他".equals(str))
      {
        relateInsuranceMap.put("其他","其他");
      }
    }
    return relateInsuranceMap;
  }

  public void setRelateInsuranceMap(Map<String, String> relateInsuranceMap) {
    this.relateInsuranceMap = relateInsuranceMap;
  }

  public String getTotalStr() {

    double total = 0;
    if(!ArrayUtils.isEmpty(this.itemDTOs))
    {
      for(InsuranceOrderItemDTO itemDTO : this.itemDTOs)
      {
        total += NumberUtil.doubleVal(itemDTO.getTotal());
      }
    }

    if(CollectionUtils.isNotEmpty(this.serviceDTOs))
    {
      for(InsuranceOrderServiceDTO serviceDTO : this.serviceDTOs)
      {
        total += NumberUtil.doubleVal(serviceDTO.getTotal());
      }
    }
    return MoneyUtil.toBigType(String.valueOf(total));
  }

  public void setTotalStr(String totalStr) {
    this.totalStr = totalStr;
  }

  public int getLength() {
    int length = 5;
    length += this.getItemLength() + this.getServiceLength();
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getItemLength() {
    int length = 1;
    if(!ArrayUtils.isEmpty(this.itemDTOs))
    {
      length += this.itemDTOs.length;
    }
    return length;
  }

  public void setItemLength(int itemLength) {
    this.itemLength = itemLength;
  }

  public int getServiceLength() {
    int length = 1;
    if(CollectionUtils.isNotEmpty(this.serviceDTOs))
    {
      length += this.serviceDTOs.size();
    }
    return length;
  }

  public void setServiceLength(int serviceLength) {
    this.serviceLength = serviceLength;
  }

  public Long getRepairDraftOrderId() {
    return repairDraftOrderId;
  }

  public void setRepairDraftOrderId(Long repairDraftOrderId) {
    this.repairDraftOrderId = repairDraftOrderId;
  }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public Integer getStartPageNo() {
        return startPageNo;
    }

    public void setStartPageNo(Integer startPageNo) {
        this.startPageNo = startPageNo;
    }

  public static void main(String[]args){
    InsuranceOrderDTO orderDTO=new InsuranceOrderDTO();

  }


}
