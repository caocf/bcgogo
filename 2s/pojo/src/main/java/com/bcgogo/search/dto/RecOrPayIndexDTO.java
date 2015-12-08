package com.bcgogo.search.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.DebtDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询应付或应收款
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-16
 * Time: 上午8:42
 * To change this template use File | Settings | File Templates.
 */
public class RecOrPayIndexDTO {
  private Long shopId;
  private Long txnOrderId;
  private String txnOrderIdStr;
  private String customerOrSupplierIdStr;
  private Long customerOrSupplierId;
  private List<Long> customerOrSupplierIds=new ArrayList<Long>();
  private String customerOrSupplierName;
  private String vehicleNumber;
  private String startDateStr;
  private Long startDate;    //消费时间
  private String endDateStr;
  private Long endDate;
  private Long startRepayDate;   //还款时间
  private String startRepayDateStr;
  private Long endRepayDate;
  private String endRepayDateStr;
  private String startPageNo;
  private String receiptNo;
  private OrderTypes orderType;
  private int pageSize;
  private String totalArrears;
  private Pager pager;
  //排序规则
  private String orderByFlag;  //desc or asc
  private String orderByField;

  private String receiver; //查询是客户还是供应商

  private Long province;
  private Long city;
  private Long region;

  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public Long getCity() {

    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getProvince() {

    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private String[] orderTypeArray;

  public String[] getOrderTypeArray() {
    return orderTypeArray;
  }

  public void setOrderTypeArray(String[] orderTypeArray) {
    this.orderTypeArray = orderTypeArray;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getTxnOrderId() {
    return txnOrderId;
  }

  public void setTxnOrderId(Long txnOrderId) {
    this.txnOrderId = txnOrderId;
  }

  public String getTxnOrderIdStr() {
    return txnOrderIdStr;
  }

  public void setTxnOrderIdStr(String txnOrderIdStr) {
    this.txnOrderIdStr = txnOrderIdStr;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  public String getCustomerOrSupplierIdStr() {
    return customerOrSupplierIdStr;
  }

  public List<Long> getCustomerOrSupplierIds() {
    return customerOrSupplierIds;
  }

  public void setCustomerOrSupplierIds(List<Long> customerOrSupplierIds) {
    this.customerOrSupplierIds = customerOrSupplierIds;
  }

  /**
   * generate customer or supplier ids;
   * @param baseDTOs
   */
  public void generateCustomerOrSupplierIds(List<BaseDTO> baseDTOs) {
    if(CollectionUtils.isEmpty(baseDTOs)){
      customerOrSupplierIds.add(-1l);
      return;
    }
    for(BaseDTO baseDTO:baseDTOs){
      customerOrSupplierIds.add(baseDTO.getId());
    }
  }

  public void setCustomerOrSupplierIdStr(String customerOrSupplierIdStr) {
    this.customerOrSupplierIdStr = customerOrSupplierIdStr;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public String getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }

  public void setStartPageNo(String startPageNo) {
    this.startPageNo = startPageNo;
  }

  public String getStartPageNo() {
    return startPageNo;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public Long getStartRepayDate() {
    return startRepayDate;
  }

  public void setStartRepayDate(Long startRepayDate) {
    this.startRepayDate = startRepayDate;
  }

  public String getStartRepayDateStr() {
    return startRepayDateStr;
  }

  public void setStartRepayDateStr(String startRepayDateStr) {
    this.startRepayDateStr = startRepayDateStr;
  }

  public Long getEndRepayDate() {
    return endRepayDate;
  }

  public void setEndRepayDate(Long endRepayDate) {
    this.endRepayDate = endRepayDate;
  }

  public String getEndRepayDateStr() {
    return endRepayDateStr;
  }

  public void setEndRepayDateStr(String endRepayDateStr) {
    this.endRepayDateStr = endRepayDateStr;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  /**
   * 转换前台传过来的Id等String类型
   * @throws ParseException
   */
  public void convertHandler() throws ParseException {
    startDate=DateUtil.getStartTimeOfDate(getStartDateStr());
    endDate=DateUtil.getEndTimeOfDate(getEndDateStr());
    startRepayDate=DateUtil.getStartTimeOfDate(getStartRepayDateStr());
    endRepayDate=DateUtil.getEndTimeOfDate(getEndRepayDateStr());
    customerOrSupplierId=NumberUtil.longValue(getCustomerOrSupplierIdStr());
    if(StringUtil.isNotEmpty(this.getReceiptNo()))
      this.setReceiptNo(this.getReceiptNo().trim().toUpperCase());
    if(StringUtil.isNotEmpty(this.getVehicleNumber()))
      this.setVehicleNumber(this.getVehicleNumber().trim());
     if(StringUtil.isNotEmpty(this.getCustomerOrSupplierName()))
       this.setCustomerOrSupplierName(getCustomerOrSupplierName().trim());
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public String getOrderByFlag() {
    return orderByFlag;
  }

  public void setOrderByFlag(String orderByFlag) {
    this.orderByFlag = orderByFlag;
  }

  public String getOrderByField() {
    return orderByField;
  }

  public void setOrderByField(String orderByField) {
    this.orderByField = orderByField;
  }

  public String getTotalDebts(List result){
    Double totalArrears=0D;
    Map<String,Object> data= (Map<String,Object>)result.get(0);
    List<DebtDTO> debtDTOs=( List<DebtDTO>)data.get("receivables");
    if( CollectionUtils.isNotEmpty(debtDTOs)){
      for(DebtDTO debtDTO:debtDTOs){
        totalArrears+=debtDTO.getDebt();
      }
      return String.valueOf(totalArrears);
    }
    return  "0";
  }

  public String getTotalPayables(List result){
    Double totalArrears=0D;
    Map<String,Object> data= (Map<String,Object>)result.get(0);
    List<DebtDTO> debtDTOs=( List<DebtDTO>)data.get("receivables");
    if( CollectionUtils.isNotEmpty(debtDTOs)){
      for(DebtDTO debtDTO:debtDTOs){
        totalArrears+=debtDTO.getDebt();
      }
      return String.valueOf(totalArrears);
    }
    return  "0";
  }

  public String getTotalArrears() {
    return totalArrears;
  }

  public void setTotalArrears(String totalArrears) {
    this.totalArrears = totalArrears;
  }

  public OrderSearchConditionDTO toOrderSearchConditionDTO() {
    OrderSearchConditionDTO orderSearchConditionDTO = new OrderSearchConditionDTO();
    orderSearchConditionDTO.setShopId(this.getShopId());
    orderSearchConditionDTO.setCustomerOrSupplierName("供应商/客户".equals(this.getCustomerOrSupplierName()) ? "" : this.getCustomerOrSupplierName());
    orderSearchConditionDTO.setVehicle(this.getVehicleNumber());
    orderSearchConditionDTO.setOrderType(getOrderTypeArray());
    orderSearchConditionDTO.setOrderStatusRepeal("NO");
    orderSearchConditionDTO.setStartTime(this.getStartDate());
    orderSearchConditionDTO.setEndTime(this.getEndDate());
    orderSearchConditionDTO.setRowStart((NumberUtil.intValue(this.getStartPageNo(), 1) - 1) * this.getPageSize());
    orderSearchConditionDTO.setPageRows(this.getPageSize());
    orderSearchConditionDTO.setSort("created_time desc");
    orderSearchConditionDTO.setFacetFields(new String[]{"order_type"});
    orderSearchConditionDTO.setNotPaid(true);

    orderSearchConditionDTO.setSearchStrategy(new String[]{OrderSearchConditionDTO.SEARCHSTRATEGY_CURRENT_PAGE_STATS, OrderSearchConditionDTO.SEARCHSTRATEGY_STATS});
    orderSearchConditionDTO.setStatsFields(new String[]{"order_debt_amount","total_cost_price"});
    orderSearchConditionDTO.setPageStatsFields(new String[]{"order_debt_amount"});
    orderSearchConditionDTO.setProvince(this.getProvince());
    orderSearchConditionDTO.setCity(this.getCity());
    orderSearchConditionDTO.setRegion(this.getRegion());
    return orderSearchConditionDTO;
  }

}
