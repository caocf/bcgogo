package com.bcgogo.payment.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.payment.dto.ChinapayDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-13
 * Time: 上午9:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "chinapay")
public class Chinapay extends LongIdentifier {
  public Long transId;
  public Long chinapayType;
  public String merId;
  public String busiId;
  public String ordId;
  public String ordAmt;
  private String curyId;
  private String interfaceVersion;
  public String bgRetUrl;
  public String pageRetUrl;
  public String gateId;
  public String param1;
  public String param2;
  public String param3;
  public String param4;
  public String param5;
  public String param6;
  public String param7;
  public String param8;
  public String param9;
  public String param10;
  public String ordDesc;
  public String shareType;
  public String shareData;
  public String priv1;
  public String customIp;
  public String chkValue;
  public String payStat;
  public String payTime;
  public String refNum;
  public String refAmt;
  public String refTime;
  public String responeseCode;
  public String Message;


  @Column(name = "trans_id")
  public Long gettransId() {
    return transId;
  }

  public void setTransId(Long transId) {
    this.transId = transId;
  }

  @Column(name = "chinapay_type")
  public Long getChinapayType() {
    return chinapayType;
  }

  public void setChinapayType(Long chinapayType) {
    this.chinapayType = chinapayType;
  }

  @Column(name = "merId", length = 20)
  public String getMerId() {
    return merId;
  }

  public void setMerId(String merId) {
    this.merId = merId;
  }

  @Column(name = "busiId", length = 20)
  public String getBusiId() {
    return busiId;
  }

  public void setBusiId(String busiId) {
    this.busiId = busiId;
  }

  @Column(name = "ordId", length = 50)
  public String getOrdId() {
    return ordId;
  }

  public void setOrdId(String ordId) {
    this.ordId = ordId;
  }

  @Column(name = "ordAmt", length = 20)
  public String getOrdAmt() {
    return ordAmt;
  }

  @Column(name = "curyId", length = 20)
  public String getCuryId() {
    return this.curyId;
  }

  public void setCuryId(String curyId) {
    this.curyId = curyId;
  }

  @Column(name = "interfaceVersion", length = 20)
  public String getInterfaceVersion() {
    return this.interfaceVersion;
  }

  public void setInterfaceVersion(String interfaceVersion) {
    this.interfaceVersion = interfaceVersion;
  }

  public void setOrdAmt(String ordAmt) {
    this.ordAmt = ordAmt;
  }

  @Column(name = "bgRetUrl", length = 200)
  public String getBgRetUrl() {
    return bgRetUrl;
  }

  public void setBgRetUrl(String bgRetUrl) {
    this.bgRetUrl = bgRetUrl;
  }

  @Column(name = "pageRetUrl", length = 200)
  public String getPageRetUrl() {
    return pageRetUrl;
  }

  public void setPageRetUrl(String pageRetUrl) {
    this.pageRetUrl = pageRetUrl;
  }

  @Column(name = "gateId", length = 20)
  public String getGateId() {
    return gateId;
  }

  public void setGateId(String gateId) {
    this.gateId = gateId;
  }

  @Column(name = "param1", length = 200)
  public String getParam1() {
    return param1;
  }

  public void setParam1(String param1) {
    this.param1 = param1;
  }

  @Column(name = "param2", length = 200)
  public String getParam2() {
    return param2;
  }

  public void setParam2(String param2) {
    this.param2 = param2;
  }

  @Column(name = "param3", length = 200)
  public String getParam3() {
    return param3;
  }

  public void setParam3(String param3) {
    this.param3 = param3;
  }

  @Column(name = "param4", length = 200)
  public String getParam4() {
    return param4;
  }

  public void setParam4(String param4) {
    this.param4 = param4;
  }

  @Column(name = "param5", length = 200)
  public String getParam5() {
    return param5;
  }

  public void setParam5(String param5) {
    this.param5 = param5;
  }

  @Column(name = "param6", length = 200)
  public String getParam6() {
    return param6;
  }

  public void setParam6(String param6) {
    this.param6 = param6;
  }

  @Column(name = "param7", length = 200)
  public String getParam7() {
    return param7;
  }

  public void setParam7(String param7) {
    this.param7 = param7;
  }

  @Column(name = "param8", length = 200)
  public String getParam8() {
    return param8;
  }

  public void setParam8(String param8) {
    this.param8 = param8;
  }

  @Column(name = "param9", length = 200)
  public String getParam9() {
    return param9;
  }

  public void setParam9(String param9) {
    this.param9 = param9;
  }

  @Column(name = "param10", length = 200)
  public String getParam10() {
    return param10;
  }

  public void setParam10(String param10) {
    this.param10 = param10;
  }

  @Column(name = "ordDesc", length = 500)
  public String getOrdDesc() {
    return ordDesc;
  }

  public void setOrdDesc(String ordDesc) {
    this.ordDesc = ordDesc;
  }

  @Column(name = "shareType", length = 20)
  public String getShareType() {
    return shareType;
  }

  public void setShareType(String shareType) {
    this.shareType = shareType;
  }

  @Column(name = "shareData", length = 500)
  public String getShareData() {
    return shareData;
  }

  public void setShareData(String shareData) {
    this.shareData = shareData;
  }

  @Column(name = "priv1", length = 200)
  public String getPriv1() {
    return priv1;
  }

  public void setPriv1(String priv1) {
    this.priv1 = priv1;
  }

  @Column(name = "customIp", length = 200)
  public String getCustomIp() {
    return customIp;
  }

  public void setCustomIp(String customIp) {
    this.customIp = customIp;
  }

  @Column(name = "chkValue", length = 500)
  public String getChkValue() {
    return chkValue;
  }

  public void setChkValue(String chkValue) {
    this.chkValue = chkValue;
  }

  @Column(name = "payStat", length = 20)
  public String getPayStat() {
    return payStat;
  }

  public void setPayStat(String payStat) {
    this.payStat = payStat;
  }

  @Column(name = "payTime", length = 20)
  public String getPayTime() {
    return payTime;
  }

  public void setPayTime(String payTime) {
    this.payTime = payTime;
  }

  @Column(name = "refNum", length = 20)
  public String getRefNum() {
    return refNum;
  }

  public void setRefNum(String refNum) {
    this.refNum = refNum;
  }

  @Column(name = "refAmt", length = 20)
  public String getRefAmt() {
    return refAmt;
  }

  public void setRefAmt(String refAmt) {
    this.refAmt = refAmt;
  }

  @Column(name = "refTime", length = 20)
  public String getRefTime() {
    return refTime;
  }

  public void setRefTime(String refTime) {
    this.refTime = refTime;
  }

  @Column(name = "responeseCode", length = 20)
  public String getResponeseCode() {
    return responeseCode;
  }

  public void setResponeseCode(String responeseCode) {
    this.responeseCode = responeseCode;
  }

  @Column(name = "Message", length = 500)
  public String getMessage() {
    return Message;
  }

  public void setMessage(String message) {
    this.Message = message;
  }


  public Chinapay() {

  }

  public Chinapay(ChinapayDTO chinapayDTO) {
    this.setId(chinapayDTO.getId());
    this.setTransId(chinapayDTO.getTransId());
    this.setChinapayType(chinapayDTO.getChinapayType());
    this.setMerId(chinapayDTO.getMerId());
    this.setBusiId(chinapayDTO.getBusiId());
    this.setOrdId(chinapayDTO.getOrdId());
    this.setOrdAmt(chinapayDTO.getOrdAmt());
    this.setCuryId(chinapayDTO.getCuryId());
    this.setInterfaceVersion(chinapayDTO.getInterfaceVersion());
    this.setBgRetUrl(chinapayDTO.getBgRetUrl());
    this.setPageRetUrl(chinapayDTO.getPageRetUrl());
    this.setGateId(chinapayDTO.getGateId());
    this.setParam1(chinapayDTO.getParam1());
    this.setParam2(chinapayDTO.getParam2());
    this.setParam3(chinapayDTO.getParam3());
    this.setParam4(chinapayDTO.getParam4());
    this.setParam5(chinapayDTO.getParam5());
    this.setParam6(chinapayDTO.getParam6());
    this.setParam7(chinapayDTO.getParam7());
    this.setParam8(chinapayDTO.getParam8());
    this.setParam9(chinapayDTO.getParam9());
    this.setParam10(chinapayDTO.getParam10());
    this.setOrdDesc(chinapayDTO.getOrdDesc());
    this.setShareType(chinapayDTO.getShareType());
    this.setShareData(chinapayDTO.getShareData());
    this.setPriv1(chinapayDTO.getPriv1());
    this.setCustomIp(chinapayDTO.getCustomIp());
    this.setChkValue(chinapayDTO.getChkValue());
    this.setPayStat(chinapayDTO.getPayStat());
    this.setPayTime(chinapayDTO.getPayTime());
    this.setRefNum(chinapayDTO.getRefNum());
    this.setRefAmt(chinapayDTO.getRefAmt());
    this.setRefTime(chinapayDTO.getRefTime());
    this.setResponeseCode(chinapayDTO.getResponeseCode());
    this.setMessage(chinapayDTO.getMessage());
  }

  public Chinapay fromDTO(ChinapayDTO chinapayDTO) {
    this.setId(chinapayDTO.getId());
    this.setTransId(chinapayDTO.getTransId());
    this.setMerId(chinapayDTO.getMerId());
    this.setBusiId(chinapayDTO.getBusiId());
    this.setOrdId(chinapayDTO.getOrdId());
    this.setOrdAmt(chinapayDTO.getOrdAmt());
    this.setCuryId(chinapayDTO.getCuryId());
    this.setInterfaceVersion(chinapayDTO.getInterfaceVersion());
    this.setBgRetUrl(chinapayDTO.getBgRetUrl());
    this.setPageRetUrl(chinapayDTO.getPageRetUrl());
    this.setGateId(chinapayDTO.getGateId());
    this.setParam1(chinapayDTO.getParam1());
    this.setParam2(chinapayDTO.getParam2());
    this.setParam3(chinapayDTO.getParam3());
    this.setParam4(chinapayDTO.getParam4());
    this.setParam5(chinapayDTO.getParam5());
    this.setParam6(chinapayDTO.getParam6());
    this.setParam7(chinapayDTO.getParam7());
    this.setParam8(chinapayDTO.getParam8());
    this.setParam9(chinapayDTO.getParam9());
    this.setParam10(chinapayDTO.getParam10());
    this.setOrdDesc(chinapayDTO.getOrdDesc());
    this.setShareType(chinapayDTO.getShareType());
    this.setShareData(chinapayDTO.getShareData());
    this.setPriv1(chinapayDTO.getPriv1());
    this.setCustomIp(chinapayDTO.getCustomIp());
    this.setChkValue(chinapayDTO.getChkValue());
    this.setPayStat(chinapayDTO.getPayStat());
    this.setPayTime(chinapayDTO.getPayTime());
    this.setChinapayType(chinapayDTO.getChinapayType());
    this.setRefNum(chinapayDTO.getRefNum());
    this.setRefAmt(chinapayDTO.getRefAmt());
    this.setRefTime(chinapayDTO.getRefTime());
    this.setResponeseCode(chinapayDTO.getResponeseCode());
    this.setMessage(chinapayDTO.getMessage());

    return this;
  }

  public ChinapayDTO toDTO() {
    ChinapayDTO chinapayDTO = new ChinapayDTO();

    chinapayDTO.setId(this.getId());
    chinapayDTO.setTransId(this.gettransId());
    chinapayDTO.setChinapayType(this.getChinapayType());
    chinapayDTO.setMerId(this.getMerId());
    chinapayDTO.setBusiId(this.getBusiId());
    chinapayDTO.setOrdId(this.getOrdId());
    chinapayDTO.setOrdAmt(this.getOrdAmt());
    chinapayDTO.setCuryId(this.getCuryId());
    chinapayDTO.setInterfaceVersion(this.getInterfaceVersion());
    chinapayDTO.setBgRetUrl(this.getBgRetUrl());
    chinapayDTO.setPageRetUrl(this.getPageRetUrl());
    chinapayDTO.setGateId(this.getGateId());
    chinapayDTO.setParam1(this.getParam1());
    chinapayDTO.setParam2(this.getParam2());
    chinapayDTO.setParam3(this.getParam3());
    chinapayDTO.setParam4(this.getParam4());
    chinapayDTO.setParam5(this.getParam5());
    chinapayDTO.setParam6(this.getParam6());
    chinapayDTO.setParam7(this.getParam7());
    chinapayDTO.setParam8(this.getParam8());
    chinapayDTO.setParam9(this.getParam9());
    chinapayDTO.setParam10(this.getParam10());
    chinapayDTO.setOrdDesc(this.getOrdDesc());
    chinapayDTO.setShareType(this.getShareType());
    chinapayDTO.setShareData(this.getShareData());
    chinapayDTO.setPriv1(this.getPriv1());
    chinapayDTO.setCustomIp(this.getCustomIp());
    chinapayDTO.setChkValue(this.getChkValue());
    chinapayDTO.setPayStat(this.getPayStat());
    chinapayDTO.setPayTime(this.getPayTime());
    chinapayDTO.setRefNum(this.getRefNum());
    chinapayDTO.setRefAmt(this.getRefAmt());
    chinapayDTO.setRefTime(this.getRefTime());
    chinapayDTO.setResponeseCode(this.getResponeseCode());
    chinapayDTO.setMessage(this.getMessage());

    return chinapayDTO;
  }

}
