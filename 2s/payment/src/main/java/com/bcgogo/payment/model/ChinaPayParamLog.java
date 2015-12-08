package com.bcgogo.payment.model;

import com.bcgogo.enums.payment.ChinaPayParamStatus;
import com.bcgogo.enums.payment.ChinaPayScene;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.enums.txn.finance.ReceivableMethod;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.BcgogoReceivableDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderDTO;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-4-1
 * Time: 下午1:37
 */
@Entity
@Table(name = "china_pay_param_log")
public class ChinaPayParamLog extends LongIdentifier {
  private Long ordId;
  private ChinaPayScene chinaPayScene;
  private ChinaPayParamStatus chinaPayParamStatus;
  private String param1;
  private String param2;
  private String param3;
  private String param4;
  private String param5;
  private String param6;
  private String param7;
  private String param8;
  private String param9;
  private String param10;

  public BcgogoReceivableDTO toSoftwareBcgogoReceivableDTO() throws Exception {
    BcgogoReceivableDTO dto = new BcgogoReceivableDTO();
    if (this.getParam1() == null) throw new Exception("chinapay software payment is null!");
    dto.setReceivableMethod(ReceivableMethod.valueOf(this.getParam1()));
    dto.setBcgogoReceivableOrderRecordRelationId(Long.valueOf(this.getParam2()));
    dto.setPaidAmount(Double.valueOf(this.getParam3()));
    dto.setPaymentTime(Long.valueOf(this.getParam4()));
    if (StringUtils.isNotBlank(this.getParam5()))
      dto.setPayeeId(Long.valueOf(this.getParam5()));
    dto.setSubmitterId(Long.valueOf(this.getParam6()));
    //分期需要
    if (StringUtils.isNotBlank(this.getParam7()))
      dto.setInstalmentPlanAlgorithmId(Long.valueOf(this.getParam7()));
    dto.setTotalAmount(Double.valueOf(this.getParam8()));
    return dto;
  }

  public void fromSoftwareBcgogoReceivableDTO(BcgogoReceivableDTO dto) throws Exception {
    this.setParam1(dto.getReceivableMethod() == null ? null : dto.getReceivableMethod().name());
    this.setParam2(String.valueOf(dto.getBcgogoReceivableOrderRecordRelationId()));
    this.setParam3(String.valueOf(dto.getPaidAmount()));
    this.setParam4(String.valueOf(dto.getPaymentTime()));
    if (dto.getPayeeId() != null) this.setParam5(String.valueOf(dto.getPayeeId()));
    this.setParam6(String.valueOf(dto.getSubmitterId()));
    if (dto.getInstalmentPlanAlgorithmId() != null)
      this.setParam7(String.valueOf(dto.getInstalmentPlanAlgorithmId() == null ? null : dto.getInstalmentPlanAlgorithmId()));
    this.setParam8(String.valueOf(dto.getTotalAmount()));
    this.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
  }

  public BcgogoReceivableDTO toHardwareBcgogoReceivableDTO() throws Exception {
    BcgogoReceivableDTO dto = new BcgogoReceivableDTO();
    dto.setShopId(Long.valueOf(this.getParam1()));
    dto.setSubmitterId(Long.valueOf(this.getParam2()));
    dto.setBcgogoReceivableOrderRecordRelationId(Long.valueOf(this.getParam3()));
    dto.setPaidAmount(Double.valueOf(this.getParam4()));
    dto.setPaymentTime(Long.valueOf(this.getParam5()));
    dto.setTotalAmount(Double.valueOf(this.getParam6()));
    return dto;
  }

  public void fromHardwareBcgogoReceivableDTO(BcgogoReceivableDTO dto) throws Exception {
    this.setParam1(String.valueOf(dto.getShopId()));
    this.setParam2(String.valueOf(dto.getSubmitterId()));
    this.setParam3(String.valueOf(dto.getBcgogoReceivableOrderRecordRelationId()));
    this.setParam4(String.valueOf(dto.getPaidAmount()));
    this.setParam5(String.valueOf(dto.getPaymentTime()));
    this.setParam6(String.valueOf(dto.getTotalAmount()));
    this.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
  }
  public void setCombinedPaymentsParam(BcgogoReceivableDTO dto,String bcgogoReceivableOrderRecordRelationIds,String paidAmounts) throws Exception {
    this.setParam1(String.valueOf(dto.getShopId()));
    this.setParam2(String.valueOf(dto.getSubmitterId()));
    this.setParam3(String.valueOf(dto.getPaymentTime()));
    this.setParam9(paidAmounts);
    this.setParam10(bcgogoReceivableOrderRecordRelationIds);
    this.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
  }

  public List<BcgogoReceivableDTO> toCombinedBcgogoReceivableDTOList() throws Exception {
    List<BcgogoReceivableDTO> bcgogoReceivableDTOList = new ArrayList<BcgogoReceivableDTO>();
    if(StringUtils.isNotBlank(this.getParam9()) && StringUtils.isNotBlank(this.getParam10())){
      String[] paidAmountStrs = this.getParam9().split(",");
      String[] bcgogoReceivableOrderRecordRelationIdStrs = this.getParam10().split(",");
      if(paidAmountStrs.length==bcgogoReceivableOrderRecordRelationIdStrs.length){
        for(int i=0;i<bcgogoReceivableOrderRecordRelationIdStrs.length;i++){
          BcgogoReceivableDTO dto = new BcgogoReceivableDTO();
          dto.setShopId(Long.valueOf(this.getParam1()));
          dto.setSubmitterId(Long.valueOf(this.getParam2()));
          dto.setPaymentTime(Long.valueOf(this.getParam3()));

          dto.setPaidAmount(Double.valueOf(paidAmountStrs[i]));
          dto.setBcgogoReceivableOrderRecordRelationId(Long.valueOf(bcgogoReceivableOrderRecordRelationIdStrs[i]));
          bcgogoReceivableDTOList.add(dto);
        }
      }
    }

    return bcgogoReceivableDTOList;
  }


  @Column(name = "ord_id")
  public Long getOrdId() {
    return ordId;
  }

  public void setOrdId(Long ordId) {
    this.ordId = ordId;
  }

  @Column(name = "china_pay_scene")
  @Enumerated(EnumType.STRING)
  public ChinaPayScene getChinaPayScene() {
    return chinaPayScene;
  }

  public void setChinaPayScene(ChinaPayScene chinaPayScene) {
    this.chinaPayScene = chinaPayScene;
  }

  @Column(name = "china_pay_param_status")
  @Enumerated(EnumType.STRING)
  public ChinaPayParamStatus getChinaPayParamStatus() {
    return chinaPayParamStatus;
  }

  public void setChinaPayParamStatus(ChinaPayParamStatus chinaPayParamStatus) {
    this.chinaPayParamStatus = chinaPayParamStatus;
  }

  @Column(name = "param1")
  public String getParam1() {
    return param1;
  }

  public void setParam1(String param1) {
    this.param1 = param1;
  }

  @Column(name = "param2")
  public String getParam2() {
    return param2;
  }

  public void setParam2(String param2) {
    this.param2 = param2;
  }

  @Column(name = "param3")
  public String getParam3() {
    return param3;
  }

  public void setParam3(String param3) {
    this.param3 = param3;
  }

  @Column(name = "param4")
  public String getParam4() {
    return param4;
  }

  public void setParam4(String param4) {
    this.param4 = param4;
  }

  @Column(name = "param5")
  public String getParam5() {
    return param5;
  }

  public void setParam5(String param5) {
    this.param5 = param5;
  }

  @Column(name = "param6")
  public String getParam6() {
    return param6;
  }

  public void setParam6(String param6) {
    this.param6 = param6;
  }

  @Column(name = "param7")

  public String getParam7() {
    return param7;
  }

  public void setParam7(String param7) {
    this.param7 = param7;
  }

  @Column(name = "param8")
  public String getParam8() {
    return param8;
  }

  public void setParam8(String param8) {
    this.param8 = param8;
  }

  @Column(name = "param9")
  public String getParam9() {
    return param9;
  }

  @Column(name = "param9")
  public void setParam9(String param9) {
    this.param9 = param9;
  }

  @Column(name = "param10")
  public String getParam10() {
    return param10;
  }

  public void setParam10(String param10) {
    this.param10 = param10;
  }
}
