package com.bcgogo.api;

import com.bcgogo.enums.app.ObdReturnMsg;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XinyuQiu on 14-7-14.
 */
public class ObdSimReturnDTO {

  private static final Logger LOG = LoggerFactory.getLogger(ObdSimReturnDTO.class);

  private String returnImei;
  private String returnDateStr;
  private Long returnDate;

  private String returnMsgStr;
  private ObdReturnMsg returnMsgEnum;

  private String operationName;
  private Long operationUserId;
  private Long operationShopId;

  public void generateInfo(){
    if(StringUtils.isNotBlank(returnDateStr)){
      try{
        setReturnDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,returnDateStr));
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }
    if(StringUtils.isNotBlank(returnMsgStr)){
      setReturnMsgEnum(ObdReturnMsg.getEnumByName(returnMsgStr));
    }
  }


  public static Logger getLog() {
    return LOG;
  }

  public String getReturnImei() {
    return returnImei;
  }

  public void setReturnImei(String returnImei) {
    this.returnImei = returnImei;
  }

  public String getReturnDateStr() {
    return returnDateStr;
  }

  public void setReturnDateStr(String returnDateStr) {
    this.returnDateStr = returnDateStr;
  }

  public Long getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(Long returnDate) {
    this.returnDate = returnDate;
  }

  public String getReturnMsgStr() {
    return returnMsgStr;
  }

  public void setReturnMsgStr(String returnMsgStr) {
    this.returnMsgStr = returnMsgStr;
  }

  public ObdReturnMsg getReturnMsgEnum() {
    return returnMsgEnum;
  }

  public void setReturnMsgEnum(ObdReturnMsg returnMsgEnum) {
    this.returnMsgEnum = returnMsgEnum;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public Long getOperationUserId() {
    return operationUserId;
  }

  public void setOperationUserId(Long operationUserId) {
    this.operationUserId = operationUserId;
  }

  public Long getOperationShopId() {
    return operationShopId;
  }

  public void setOperationShopId(Long operationShopId) {
    this.operationShopId = operationShopId;
  }
}
