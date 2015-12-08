package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "insurance_order")
public class InsuranceOrder extends LongIdentifier {
  private Long shopId;
  private Long editorId;
  private String editor;
  private Long repairOrderId;
  private String repairOrderReceiptNo;
  private String policyNo;   //保险单号
  private String reportNo;//报案编号
  private Long insuranceCompanyId; //保险公司
  private String insuranceCompany;
  private Long insureStartDate;//投保日期
  private Long insureEndDate;//到期日期
  private Long customerId;//被保险人
  private String customer;
  private String licenceNo;//车牌
  private Long vehicleId;
  private String driver;//驾驶人
  private String drivingNo;//驾驶证号
  private String mobile;
  private String brand; // 车辆品牌
  private String model; //车型
  private String chassisNumber; //车架号
  private String engineNumber; //发动机号
  private String reporter; //报案人
  private String reporterContact; //报案人联系电话
  private Long reportDate; //报案时间
  private Long accidentDate; //出险时间
  private String accidentAddress; //出险地点

  private Long surveyDate; //查勘时间
  private String surveyAddress; //查勘地点
  private String accidentHandling; //事故处理方式
  private String accidentLiability; //事故责任
  private String firstSurveyAddress; //是否第一现场勘查
  private String accidentType; //事故类型
  private String surveyOpinion; //查勘人员意见
  private String accidentCause; //出险原因
  private String relateInsuranceItems; //涉及险种
  private Long estimateDate; //定损时间
  private String estimateAddress; //定损地点
  private Double insuranceCost; //施救费
  private Double scrapValue; //扣减残值
  private String scrapApproach; //残值处理方式
  private Double claims; //理赔金额
  private Double claimsPercentage; //赔付比例
  private Long repairDraftOrderId;  //施工草稿单
  private OrderStatus status;//保险理赔单状态

  private Double personalClaims; //个人理赔金额
  private Double personalClaimsPercentage; //个人赔付比例



    public InsuranceOrderDTO toDTO() {
    InsuranceOrderDTO insuranceOrderDTO = new InsuranceOrderDTO();
    insuranceOrderDTO.setId(getId());
    insuranceOrderDTO.setShopId(getShopId());
    insuranceOrderDTO.setEditor(getEditor());
    insuranceOrderDTO.setRepairOrderId(getRepairOrderId());
    insuranceOrderDTO.setRepairOrderReceiptNo(getRepairOrderReceiptNo());
    insuranceOrderDTO.setPolicyNo(getPolicyNo());
    insuranceOrderDTO.setReportNo(getReportNo());
    insuranceOrderDTO.setInsuranceCompany(getInsuranceCompany());
    insuranceOrderDTO.setInsuranceCompanyId(getInsuranceCompanyId());
    insuranceOrderDTO.setInsureStartDate(getInsureStartDate());
    insuranceOrderDTO.setInsureEndDate(getInsureEndDate());
    insuranceOrderDTO.setCustomer(getCustomer());
    insuranceOrderDTO.setCustomerId(getCustomerId());
    insuranceOrderDTO.setLicenceNo(getLicenceNo());
    insuranceOrderDTO.setVehicleId(getVehicleId());
    insuranceOrderDTO.setDriver(getDriver());
    insuranceOrderDTO.setDrivingNo(getDrivingNo());
    insuranceOrderDTO.setMobile(getMobile());
    insuranceOrderDTO.setBrand(getBrand());
    insuranceOrderDTO.setModel(getModel());
    insuranceOrderDTO.setChassisNumber(getChassisNumber());
    insuranceOrderDTO.setEngineNumber(getEngineNumber());
    insuranceOrderDTO.setReporter(getReporter());
    insuranceOrderDTO.setReporterContact(getReporterContact());
    insuranceOrderDTO.setReportDate(getReportDate());
    insuranceOrderDTO.setAccidentDate(getAccidentDate());
    insuranceOrderDTO.setAccidentAddress(getAccidentAddress());
    insuranceOrderDTO.setSurveyDate(getSurveyDate());
    insuranceOrderDTO.setSurveyAddress(getSurveyAddress());
    insuranceOrderDTO.setAccidentHandling(getAccidentHandling());
    insuranceOrderDTO.setAccidentLiability(getAccidentLiability());
    insuranceOrderDTO.setFirstSurveyAddress(getFirstSurveyAddress());
    insuranceOrderDTO.setAccidentType(getAccidentType());
    insuranceOrderDTO.setSurveyOpinion(getSurveyOpinion());
    insuranceOrderDTO.setAccidentCause(getAccidentCause());
    if (StringUtils.isNotBlank(this.getRelateInsuranceItems())) {
      String[] str = this.getRelateInsuranceItems().split(",");
      insuranceOrderDTO.setRelateInsuranceItems(new ArrayList<String>(Arrays.asList(str)));
    }
    insuranceOrderDTO.setEstimateDate(getEstimateDate());
    insuranceOrderDTO.setEstimateAddress(getEstimateAddress());
    insuranceOrderDTO.setInsuranceCost(getInsuranceCost());
    insuranceOrderDTO.setScrapValue(getScrapValue());
    insuranceOrderDTO.setScrapApproach(getScrapApproach());
    insuranceOrderDTO.setClaims(getClaims());
    insuranceOrderDTO.setClaimsPercentage(getClaimsPercentage());
    insuranceOrderDTO.setPersonalClaims(getPersonalClaims());
    insuranceOrderDTO.setPersonalClaimsPercentage(getPersonalClaimsPercentage());
    insuranceOrderDTO.setRepairDraftOrderId(getRepairDraftOrderId());
    insuranceOrderDTO.setStatusStr(getStatus().getName());
    return insuranceOrderDTO;
  }

  public void fromDTO(InsuranceOrderDTO insuranceOrderDTO) {
    if (insuranceOrderDTO == null) {
      return;
    }
    this.shopId = insuranceOrderDTO.getShopId();
    this.editorId = insuranceOrderDTO.getEditorId();
    this.editor = insuranceOrderDTO.getEditor();
    this.repairOrderId = insuranceOrderDTO.getRepairOrderId();
    this.repairOrderReceiptNo = insuranceOrderDTO.getRepairOrderReceiptNo();
    this.policyNo = insuranceOrderDTO.getPolicyNo();
    this.reportNo = insuranceOrderDTO.getReportNo();
    this.insuranceCompanyId = insuranceOrderDTO.getInsuranceCompanyId();
    this.insuranceCompany = insuranceOrderDTO.getInsuranceCompany();
    this.insureStartDate = insuranceOrderDTO.getInsureStartDate();
    this.insureEndDate = insuranceOrderDTO.getInsureEndDate();
    this.customerId = insuranceOrderDTO.getCustomerId();
    this.customer = insuranceOrderDTO.getCustomer();
    this.licenceNo = insuranceOrderDTO.getLicenceNo();
    this.vehicleId = insuranceOrderDTO.getVehicleId();
    this.driver = insuranceOrderDTO.getDriver();
    this.drivingNo = insuranceOrderDTO.getDrivingNo();
    this.mobile = insuranceOrderDTO.getMobile();
    this.brand = insuranceOrderDTO.getBrand();
    this.model = insuranceOrderDTO.getModel();
    this.chassisNumber = insuranceOrderDTO.getChassisNumber();
    this.engineNumber = insuranceOrderDTO.getEngineNumber();
    this.reporter = insuranceOrderDTO.getReporter();
    this.reporterContact = insuranceOrderDTO.getReporterContact();
    this.reportDate = insuranceOrderDTO.getReportDate();
    this.accidentDate = insuranceOrderDTO.getAccidentDate();
    this.accidentAddress = insuranceOrderDTO.getAccidentAddress();
    this.surveyDate = insuranceOrderDTO.getSurveyDate();
    this.surveyAddress = insuranceOrderDTO.getSurveyAddress();
    this.accidentHandling = insuranceOrderDTO.getAccidentHandling();
    this.accidentLiability = insuranceOrderDTO.getAccidentLiability();
    this.firstSurveyAddress = insuranceOrderDTO.getFirstSurveyAddress();
    this.accidentType = insuranceOrderDTO.getAccidentType();
    this.surveyOpinion = insuranceOrderDTO.getSurveyOpinion();
    this.accidentCause = insuranceOrderDTO.getAccidentCause();
    if (CollectionUtils.isNotEmpty(insuranceOrderDTO.getRelateInsuranceItems())) {
      StringBuffer sb = new StringBuffer();
      for (String str : insuranceOrderDTO.getRelateInsuranceItems()) {
        sb.append(str).append(",");
      }
      this.relateInsuranceItems = sb.substring(0);
    } else {
      this.relateInsuranceItems = null;
    }
    this.estimateDate = insuranceOrderDTO.getEstimateDate();
    this.estimateAddress = insuranceOrderDTO.getEstimateAddress();
    this.insuranceCost = insuranceOrderDTO.getInsuranceCost();
    this.scrapValue = insuranceOrderDTO.getScrapValue();
    this.scrapApproach = insuranceOrderDTO.getScrapApproach();
    this.claims = insuranceOrderDTO.getClaims();
    this.claimsPercentage = insuranceOrderDTO.getClaimsPercentage();
    this.personalClaims=insuranceOrderDTO.getPersonalClaims();
    this.personalClaimsPercentage=insuranceOrderDTO.getPersonalClaimsPercentage();
    this.repairDraftOrderId = insuranceOrderDTO.getRepairDraftOrderId();
    this.status=insuranceOrderDTO.getStatus();


  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  @Column(name = "editor")
  public String  getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  @Column(name = "repair_order_receipt_no")
  public String getRepairOrderReceiptNo() {
    return repairOrderReceiptNo;
  }

  public void setRepairOrderReceiptNo(String repairOrderReceiptNo) {
    this.repairOrderReceiptNo = repairOrderReceiptNo;
  }

  @Column(name = "policy_no")
  public String getPolicyNo() {
    return policyNo;
  }

  public void setPolicyNo(String policyNo) {
    this.policyNo = policyNo;
  }

  @Column(name = "report_no")
  public String getReportNo() {
    return reportNo;
  }

  public void setReportNo(String reportNo) {
    this.reportNo = reportNo;
  }

  @Column(name = "insurance_company_id")
  public Long getInsuranceCompanyId() {
    return insuranceCompanyId;
  }

  public void setInsuranceCompanyId(Long insuranceCompanyId) {
    this.insuranceCompanyId = insuranceCompanyId;
  }

  @Column(name = "insurance_company")
  public String getInsuranceCompany() {
    return insuranceCompany;
  }

  public void setInsuranceCompany(String insuranceCompany) {
    this.insuranceCompany = insuranceCompany;
  }

  @Column(name = "insure_start_date")
  public Long getInsureStartDate() {
    return insureStartDate;
  }

  public void setInsureStartDate(Long insureStartDate) {
    this.insureStartDate = insureStartDate;
  }

  @Column(name = "insure_end_date")
  public Long getInsureEndDate() {
    return insureEndDate;
  }

  public void setInsureEndDate(Long insureEndDate) {
    this.insureEndDate = insureEndDate;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "licence_no")
  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "driver")
  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  @Column(name = "driving_no")
  public String getDrivingNo() {
    return drivingNo;
  }

  public void setDrivingNo(String drivingNo) {
    this.drivingNo = drivingNo;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "chassis_number")
  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  @Column(name = "engine_number")
  public String getEngineNumber() {
    return engineNumber;
  }

  public void setEngineNumber(String engineNumber) {
    this.engineNumber = engineNumber;
  }




  @Column(name = "reporter")
  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  @Column(name = "reporter_contact")
  public String getReporterContact() {
    return reporterContact;
  }

  public void setReporterContact(String reporterContact) {
    this.reporterContact = reporterContact;
  }

  @Column(name = "report_date")
  public Long getReportDate() {
    return reportDate;
  }

  public void setReportDate(Long reportDate) {
    this.reportDate = reportDate;
  }

  @Column(name = "accident_date")
  public Long getAccidentDate() {
    return accidentDate;
  }

  public void setAccidentDate(Long accidentDate) {
    this.accidentDate = accidentDate;
  }

  @Column(name = "accident_address")
  public String getAccidentAddress() {
    return accidentAddress;
  }

  public void setAccidentAddress(String accidentAddress) {
    this.accidentAddress = accidentAddress;
  }

  @Column(name = "survey_date")
  public Long getSurveyDate() {
    return surveyDate;
  }

  public void setSurveyDate(Long surveyDate) {
    this.surveyDate = surveyDate;
  }

  @Column(name = "survey_address")
  public String getSurveyAddress() {
    return surveyAddress;
  }

  public void setSurveyAddress(String surveyAddress) {
    this.surveyAddress = surveyAddress;
  }

  @Column(name = "accident_handling")
  public String getAccidentHandling() {
    return accidentHandling;
  }

  public void setAccidentHandling(String accidentHandling) {
    this.accidentHandling = accidentHandling;
  }

  @Column(name = "accident_liability")
  public String getAccidentLiability() {
    return accidentLiability;
  }

  public void setAccidentLiability(String accidentLiability) {
    this.accidentLiability = accidentLiability;
  }

  @Column(name = "first_survey_address")
  public String getFirstSurveyAddress() {
    return firstSurveyAddress;
  }

  public void setFirstSurveyAddress(String firstSurveyAddress) {
    this.firstSurveyAddress = firstSurveyAddress;
  }

  @Column(name = "accident_type")
  public String getAccidentType() {
    return accidentType;
  }

  public void setAccidentType(String accidentType) {
    this.accidentType = accidentType;
  }

  @Column(name = "survey_opinion")
  public String getSurveyOpinion() {
    return surveyOpinion;
  }

  public void setSurveyOpinion(String surveyOpinion) {
    this.surveyOpinion = surveyOpinion;
  }

  @Column(name = "accident_cause")
  public String getAccidentCause() {
    return accidentCause;
  }

  public void setAccidentCause(String accidentCause) {
    this.accidentCause = accidentCause;
  }

  @Column(name = "relate_insuranceItems")
  public String getRelateInsuranceItems() {
    return relateInsuranceItems;
  }

  public void setRelateInsuranceItems(String relateInsuranceItems) {
    this.relateInsuranceItems = relateInsuranceItems;
  }

  @Column(name = "estimate_date")
  public Long getEstimateDate() {
    return estimateDate;
  }

  public void setEstimateDate(Long estimateDate) {
    this.estimateDate = estimateDate;
  }

  @Column(name = "estimate_address")
  public String getEstimateAddress() {
    return estimateAddress;
  }

  public void setEstimateAddress(String estimateAddress) {
    this.estimateAddress = estimateAddress;
  }

  @Column(name = "insurance_cost")
  public Double getInsuranceCost() {
    return insuranceCost;
  }

  public void setInsuranceCost(Double insuranceCost) {
    this.insuranceCost = insuranceCost;
  }

  @Column(name = "scrap_value")
  public Double getScrapValue() {
    return scrapValue;
  }

  public void setScrapValue(Double scrapValue) {
    this.scrapValue = scrapValue;
  }

  @Column(name = "scrap_approach")
  public String getScrapApproach() {
    return scrapApproach;
  }

  public void setScrapApproach(String scrapApproach) {
    this.scrapApproach = scrapApproach;
  }

  @Column(name = "claims")
  public Double getClaims() {
    return claims;
  }

  public void setClaims(Double claims) {
    this.claims = claims;
  }

  @Column(name = "claims_percentage")
  public Double getClaimsPercentage() {
    return claimsPercentage;
  }

  public void setClaimsPercentage(Double claimsPercentage) {
    this.claimsPercentage = claimsPercentage;
  }

    @Column(name = "person_claims")
    public Double getPersonalClaims() {
        return personalClaims;
    }

    public void setPersonalClaims(Double personalClaims) {
        this.personalClaims = personalClaims;
    }

    @Column(name = "person_claims_percentage")
    public Double getPersonalClaimsPercentage() {
        return personalClaimsPercentage;
    }

    public void setPersonalClaimsPercentage(Double personalClaimsPercentage) {
        this.personalClaimsPercentage = personalClaimsPercentage;
    }

  @Column(name = "repair_draft_order_id")

  public Long getRepairDraftOrderId() {
    return repairDraftOrderId;
  }

  public void setRepairDraftOrderId(Long repairDraftOrderId) {
    this.repairDraftOrderId = repairDraftOrderId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatus() {
        return status;
  }

  public void setStatus(OrderStatus status) {
        this.status = status;
  }



}
