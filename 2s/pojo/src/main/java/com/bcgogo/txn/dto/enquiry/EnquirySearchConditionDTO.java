package com.bcgogo.txn.dto.enquiry;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-13
 * Time: 下午3:25
 */
public class EnquirySearchConditionDTO {
  private static final Logger LOG = LoggerFactory.getLogger(EnquirySearchConditionDTO.class);

  private String customerSearchWord; //客户名，车牌号， 手机号
  private String receiptNo;
  private Long enquiryTimeStart;//      询价时间
  private String enquiryTimeStartStr;
  private Long enquiryTimeEnd;//
  private String enquiryTimeEndStr;
  private Long responseTimeStart;//     报价时间
  private String responseTimeStartStr;
  private Long responseTimeEnd;//
  private String responseTimeEndStr;
  private EnquiryShopResponseStatus[] responseStatuses;
  private Long[] customerIds;//客户Id如果前台下拉选择的则查改客户
  private String[] appUserNos;
  private Long shopId;//查询店铺的id
  //在线退货前台传过来的pager分页  与单据查询中心不兼容
  private int maxRows = 15;
  private int startPageNo = 1;

  public String getCustomerSearchWord() {
    return customerSearchWord;
  }

  public void setCustomerSearchWord(String customerSearchWord) {
    this.customerSearchWord = customerSearchWord;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getEnquiryTimeStart() {
    return enquiryTimeStart;
  }

  public void setEnquiryTimeStart(Long enquiryTimeStart) {
    this.enquiryTimeStart = enquiryTimeStart;
  }

  public String getEnquiryTimeStartStr() {
    return enquiryTimeStartStr;
  }

  public void setEnquiryTimeStartStr(String enquiryTimeStartStr) {
    this.enquiryTimeStartStr = enquiryTimeStartStr;
    if (StringUtils.isNotEmpty(enquiryTimeStartStr)) {
      try {
        enquiryTimeStart = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, enquiryTimeStartStr);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      enquiryTimeStart = null;
    }
  }

  public Long getEnquiryTimeEnd() {
    return enquiryTimeEnd;
  }

  public void setEnquiryTimeEnd(Long enquiryTimeEnd) {
    this.enquiryTimeEnd = enquiryTimeEnd;
  }

  public String getEnquiryTimeEndStr() {
    return enquiryTimeEndStr;
  }

  public void setEnquiryTimeEndStr(String enquiryTimeEndStr) {
    this.enquiryTimeEndStr = enquiryTimeEndStr;
    if (StringUtils.isNotEmpty(enquiryTimeEndStr)) {
      try {
        enquiryTimeEnd = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, enquiryTimeEndStr);
        enquiryTimeEnd += (24 * 3600 * 1000 - 1);   //结束日期应在当天末。
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      enquiryTimeEnd = null;
    }
  }

  public Long getResponseTimeStart() {
    return responseTimeStart;
  }

  public void setResponseTimeStart(Long responseTimeStart) {
    this.responseTimeStart = responseTimeStart;
  }

  public String getResponseTimeStartStr() {
    return responseTimeStartStr;

  }

  public void setResponseTimeStartStr(String responseTimeStartStr) {
    this.responseTimeStartStr = responseTimeStartStr;
    if (StringUtils.isNotEmpty(responseTimeStartStr)) {
      try {
        responseTimeStart = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, responseTimeStartStr);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      responseTimeStart = null;
    }
  }

  public Long getResponseTimeEnd() {
    return responseTimeEnd;
  }

  public void setResponseTimeEnd(Long responseTimeEnd) {
    this.responseTimeEnd = responseTimeEnd;
  }

  public String getResponseTimeEndStr() {
    return responseTimeEndStr;
  }

  public void setResponseTimeEndStr(String responseTimeEndStr) {
    this.responseTimeEndStr = responseTimeEndStr;
    if (StringUtils.isNotEmpty(responseTimeEndStr)) {
      try {
        responseTimeEnd = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, responseTimeEndStr);
        responseTimeEnd += (24 * 3600 * 1000 - 1);   //结束日期应在当天末。
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      responseTimeEnd = null;
    }
  }

  public EnquiryShopResponseStatus[] getResponseStatuses() {
    return responseStatuses;
  }

  public void setResponseStatuses(EnquiryShopResponseStatus[] responseStatuses) {
    this.responseStatuses = responseStatuses;
  }

  public Long[] getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(Long[] customerIds) {
    this.customerIds = customerIds;
  }

  public String[] getAppUserNos() {
    return appUserNos;
  }

  public void setAppUserNos(String[] appUserNos) {
    this.appUserNos = appUserNos;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public void setAppUserNosFromAppUserCustomers(List<AppUserCustomerDTO> appUserCustomerDTOList) {
    if (CollectionUtil.isNotEmpty(appUserCustomerDTOList)) {
      Set<String> appUserNos = new HashSet<String>();
       for(AppUserCustomerDTO appUserCustomerDTO: appUserCustomerDTOList){
         if(StringUtils.isNotBlank(appUserCustomerDTO.getAppUserNo())) {
           appUserNos.add(appUserCustomerDTO.getAppUserNo());
         }
       }
      if (CollectionUtil.isNotEmpty(appUserNos)) {
        setAppUserNos(appUserNos.toArray(new String[appUserNos.size()]));
      }
    }
  }
}
