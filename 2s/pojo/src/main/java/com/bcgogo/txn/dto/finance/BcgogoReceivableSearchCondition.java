package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-20
 * Time: 上午10:57
 */
public class BcgogoReceivableSearchCondition {
  private static final String SEARCH_STRATEGY = "SearchByPendingReviewAndPaid";
  private BcgogoReceivableStatus status;
  private String[] receivableStatuses;
  private String[] paymentTypes;
  private PaymentType paymentType;
  private String[] paymentMethods;
  private String[] paymentStatuses;
  private String[] buyChannels;

  private String[] bargainStatuses;
  private Long[] shopVersionIds;

  private Long[] bcgogoProductIds;
  private Long startTime;
  private Long endTime;

  private Long bcgogoReceivableOrderId;
  private String startTimeStr;
  private String endTimeStr;

  private String receiptNo;
  private String shopName;
  private String followName;       //跟进人
  private String payeeName;       //收款人
  private List<Long> shopIds;
  private int start;
  private int limit;
  private int startPageNo=1;
  private int maxRows=10;
  private String searchStrategy = "";
  private String groupField;
  private Integer searchMonths;

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public Long[] getShopVersionIds() {
    return shopVersionIds;
  }

  public void setShopVersionIds(Long[] shopVersionIds) {
    this.shopVersionIds = shopVersionIds;
  }

  public String[] getBargainStatuses() {
    return bargainStatuses;
  }

  public void setBargainStatuses(String[] bargainStatuses) {
    this.bargainStatuses = bargainStatuses;
  }

  public Long getBcgogoReceivableOrderId() {
    return bcgogoReceivableOrderId;
  }

  public void setBcgogoReceivableOrderId(Long bcgogoReceivableOrderId) {
    this.bcgogoReceivableOrderId = bcgogoReceivableOrderId;
  }

  public String getGroupField() {
    return groupField;
  }

  public void setGroupField(String groupField) {
    this.groupField = groupField;
  }

  public String[] getBuyChannels() {
    return buyChannels;
  }
  public BuyChannels[] getBuyChannelsEnum() {
    if(!ArrayUtils.isEmpty(buyChannels)){
      List<BuyChannels> buyChannelsList = new ArrayList<BuyChannels>();
      for(String str:buyChannels)
        buyChannelsList.add(BuyChannels.valueOf(str));
      return buyChannelsList.toArray(new BuyChannels[buyChannelsList.size()]);
    }
    return null;
  }

  public void setBuyChannels(String[] buyChannels) {
    this.buyChannels = buyChannels;
  }

  public String[] getPaymentStatuses() {
    return paymentStatuses;
  }

  public void setPaymentStatuses(String[] paymentStatuses) {
    this.paymentStatuses = paymentStatuses;
  }
  public PaymentStatus[] getPaymentStatusesEnum() {
    if(!ArrayUtils.isEmpty(paymentStatuses)){
      List<PaymentStatus> paymentStatusList = new ArrayList<PaymentStatus>();
      for(String str:paymentStatuses)
        paymentStatusList.add(PaymentStatus.valueOf(str));
      return paymentStatusList.toArray(new PaymentStatus[paymentStatusList.size()]);
    }
    return null;
  }
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
    if (StringUtils.isNotBlank(startTimeStr)){
      try {
        startTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, startTimeStr);
      } catch (ParseException e) {
      }
    }else{
      startTime = null;
    }
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
    if (StringUtils.isNotBlank(endTimeStr)){
      try {
        endTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, endTimeStr)+DateUtil.DAY_MILLION_SECONDS-1;
      } catch (ParseException e) {
      }
    }else{
      endTime = null;
    }
  }

  public void setSearchByPendingReviewAndPaid() {
    this.searchStrategy = SEARCH_STRATEGY;
  }

  public boolean isSearchByPendingReviewAndPaid() {
    return (this.searchStrategy.equals(SEARCH_STRATEGY));
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

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public BcgogoReceivableStatus getStatus() {
    return status;
  }

  public void setStatus(BcgogoReceivableStatus status) {
    this.status = status;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public String[] getPaymentTypes() {
    return paymentTypes;
  }

  public void setPaymentTypes(String[] paymentTypes) {
    this.paymentTypes = paymentTypes;
  }
  public PaymentType[] getPaymentTypesEnum() {
    if(!ArrayUtils.isEmpty(paymentTypes)){
      List<PaymentType> paymentTypeList = new ArrayList<PaymentType>();
      for(String str:paymentTypes)
        paymentTypeList.add(PaymentType.valueOf(str));
      return paymentTypeList.toArray(new PaymentType[paymentTypeList.size()]);
    }
    return null;
  }
  public List<Long> getShopIds() {
    return shopIds;
  }

  public void setShopIds(List<Long> shopIds) {
    this.shopIds = shopIds;
  }

  public String[] getReceivableStatuses() {
    return receivableStatuses;
  }

  public void setReceivableStatuses(String[] receivableStatuses) {
    this.receivableStatuses = receivableStatuses;
  }

  public String getFollowName() {
    return followName;
  }

  public void setFollowName(String followName) {
    this.followName = followName;
  }

  public String[] getPaymentMethods() {
    return paymentMethods;
  }

  public void setPaymentMethods(String[] paymentMethods) {
    this.paymentMethods = paymentMethods;
  }

  public Long[] getBcgogoProductIds() {
    return bcgogoProductIds;
  }

  public void setBcgogoProductIds(Long[] bcgogoProductIds) {
    this.bcgogoProductIds = bcgogoProductIds;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public Integer getSearchMonths() {
    return searchMonths;
  }

  public void setSearchMonths(Integer searchMonths) {
    this.searchMonths = searchMonths;
    if(searchMonths!=null){
      Calendar cal = Calendar.getInstance();
      try {
        cal.setTimeInMillis(DateUtil.getTheDayTime());
        switch (searchMonths){
          case 1:
            this.startTime = DateUtil.getLastMonthTime(cal);
            break;
          case 3:
            this.startTime = DateUtil.getLastThreeMonthTime(cal);
            break;
          case 6:
            this.startTime = DateUtil.getLastHalfYearTime(cal);
            break;
          case 12:
            this.startTime = DateUtil.getLastYearTime(cal);
            break;
        }
      } catch (ParseException e) {
      }

    }
  }
}
