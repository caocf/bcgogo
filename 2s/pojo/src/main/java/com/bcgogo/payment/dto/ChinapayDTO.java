package com.bcgogo.payment.dto;

import java.io.Serializable;

public class ChinapayDTO implements Serializable {
  public Long id;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTransId() {
    return transId;
  }

  public void setTransId(Long transId) {
    this.transId = transId;
  }

  public Long getChinapayType() {
    return chinapayType;
  }

  public void setChinapayType(Long chinapayType) {
    this.chinapayType = chinapayType;
  }

  public String getMerId() {
    return merId;
  }

  public void setMerId(String merId) {
    this.merId = merId;
  }

  public String getBusiId() {
    return busiId;
  }

  public void setBusiId(String busiId) {
    this.busiId = busiId;
  }

  public String getOrdId() {
    return ordId;
  }

  public void setOrdId(String ordId) {
    this.ordId = ordId;
  }

  public String getOrdAmt() {
    return ordAmt;
  }

  public void setOrdAmt(String ordAmt) {
    this.ordAmt = ordAmt;
  }

  public String getCuryId() {
    return this.curyId;
  }

  public void setCuryId(String curyId) {
    this.curyId = curyId;
  }

  public String getInterfaceVersion() {
    return this.interfaceVersion;
  }

  public void setInterfaceVersion(String interfaceVersion) {
    this.interfaceVersion = interfaceVersion;
  }

  public String getBgRetUrl() {
    return bgRetUrl;
  }

  public void setBgRetUrl(String bgRetUrl) {
    this.bgRetUrl = bgRetUrl;
  }

  public String getPageRetUrl() {
    return pageRetUrl;
  }

  public void setPageRetUrl(String pageRetUrl) {
    this.pageRetUrl = pageRetUrl;
  }

  public String getGateId() {
    return gateId;
  }

  public void setGateId(String gateId) {
    this.gateId = gateId;
  }

  public String getParam1() {
    return param1;
  }

  public void setParam1(String param1) {
    this.param1 = param1;
  }

  public String getParam2() {
    return param2;
  }

  public void setParam2(String param2) {
    this.param2 = param2;
  }

  public String getParam3() {
    return param3;
  }

  public void setParam3(String param3) {
    this.param3 = param3;
  }

  public String getParam4() {
    return param4;
  }

  public void setParam4(String param4) {
    this.param4 = param4;
  }

  public String getParam5() {
    return param5;
  }

  public void setParam5(String param5) {
    this.param5 = param5;
  }

  public String getParam6() {
    return param6;
  }

  public void setParam6(String param6) {
    this.param6 = param6;
  }

  public String getParam7() {
    return param7;
  }

  public void setParam7(String param7) {
    this.param7 = param7;
  }

  public String getParam8() {
    return param8;
  }

  public void setParam8(String param8) {
    this.param8 = param8;
  }

  public String getParam9() {
    return param9;
  }

  public void setParam9(String param9) {
    this.param9 = param9;
  }

  public String getParam10() {
    return param10;
  }

  public void setParam10(String param10) {
    this.param10 = param10;
  }

  public String getOrdDesc() {
    return ordDesc;
  }

  public void setOrdDesc(String ordDesc) {
    this.ordDesc = ordDesc;
  }

  public String getShareType() {
    return shareType;
  }

  public void setShareType(String shareType) {
    this.shareType = shareType;
  }

  public String getShareData() {
    return shareData;
  }

  public void setShareData(String shareData) {
    this.shareData = shareData;
  }

  public String getPriv1() {
    return priv1;
  }

  public void setPriv1(String priv1) {
    this.priv1 = priv1;
  }

  public String getCustomIp() {
    return customIp;
  }

  public void setCustomIp(String customIp) {
    this.customIp = customIp;
  }

  public String getChkValue() {
    return chkValue;
  }

  public void setChkValue(String chkValue) {
    this.chkValue = chkValue;
  }

  public String getPayStat() {
    return payStat;
  }

  public void setPayStat(String payStat) {
    this.payStat = payStat;
  }

  public String getPayTime() {
    return payTime;
  }

  public void setPayTime(String payTime) {
    this.payTime = payTime;
  }

  public String getRefNum() {
    return refNum;
  }

  public void setRefNum(String refNum) {
    this.refNum = refNum;
  }

  public String getRefAmt() {
    return refAmt;
  }

  public void setRefAmt(String refAmt) {
    this.refAmt = refAmt;
  }

  public String getRefTime() {
    return refTime;
  }

  public void setRefTime(String refTime) {
    this.refTime = refTime;
  }

  public String getResponeseCode() {
    return responeseCode;
  }

  public void setResponeseCode(String responeseCode) {
    this.responeseCode = responeseCode;
  }

  public String getMessage() {
    return Message;
  }

  public void setMessage(String message) {
    Message = message;
  }


  @Override
  public String toString() {
    return "ChinapayDTO{" +
        "id=" + id +
        ", transId=" + transId +
        ", chinapayType=" + chinapayType +
        ", merId='" + merId + '\'' +
        ", busiId='" + busiId + '\'' +
        ", ordId='" + ordId + '\'' +
        ", ordAmt='" + ordAmt + '\'' +
        ", curyId='" + curyId + '\'' +
        ", interfaceVersion='" + interfaceVersion + '\'' +
        ", bgRetUrl='" + bgRetUrl + '\'' +
        ", pageRetUrl='" + pageRetUrl + '\'' +
        ", gateId='" + gateId + '\'' +
        ", param1='" + param1 + '\'' +
        ", param2='" + param2 + '\'' +
        ", param3='" + param3 + '\'' +
        ", param4='" + param4 + '\'' +
        ", param5='" + param5 + '\'' +
        ", param6='" + param6 + '\'' +
        ", param7='" + param7 + '\'' +
        ", param8='" + param8 + '\'' +
        ", param9='" + param9 + '\'' +
        ", param10='" + param10 + '\'' +
        ", ordDesc='" + ordDesc + '\'' +
        ", shareType='" + shareType + '\'' +
        ", shareData='" + shareData + '\'' +
        ", priv1='" + priv1 + '\'' +
        ", customIp='" + customIp + '\'' +
        ", chkValue='" + chkValue + '\'' +
        ", payStat='" + payStat + '\'' +
        ", payTime='" + payTime + '\'' +
        ", refNum='" + refNum + '\'' +
        ", refAmt='" + refAmt + '\'' +
        ", refTime='" + refTime + '\'' +
        ", responeseCode='" + responeseCode + '\'' +
        ", Message='" + Message + '\'' +
        '}';
  }


}
