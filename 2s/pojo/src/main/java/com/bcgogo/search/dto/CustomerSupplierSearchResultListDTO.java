package com.bcgogo.search.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-28
 * Time: 下午11:49
 * customer supplier 搜索出来的结果集
 */
public class CustomerSupplierSearchResultListDTO {
  //customer
  private List<CustomerSupplierSearchResultDTO> customerSuppliers = new ArrayList<CustomerSupplierSearchResultDTO>();
  private List<CustomerDTO> customerDTOs = new ArrayList<CustomerDTO>();
  private List<CustomerRecordDTO> customerRecordDTOs = new ArrayList<CustomerRecordDTO>();
  private long hasMobileNumFound;//手机用户 Size
  private long hasObdNumFound;//OBD用户
  private List<Long> odbContactIdList = new ArrayList<Long>(); //OBD用户的联系人id
  private long hasAppNumFound;//app用户
  private List<String> appMobileList = new ArrayList<String>(); //OBD用户的手机号码
  private List<String> odbMobileList = new ArrayList<String>(); //OBD用户的手机号码
  private long memberNumFound;//会员 Size
  private Long todayNewCustomerNumFound;//今日新增客户数量
  private String mobiles;//所有查询结果的手机号
  //supplier
  private List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
  private long deptNum;//欠款 人数量
  private Double totalDeposit; //定金总额
  private Double totalDebt;  //欠款总数
  private Double totalReturnDebt;  //应付总数
  private Double totalBalance; //会员储值
  private Double totalConsumption=0.0;//累计消费/累计交易
  private Map<String, Long> counts;
  private long numFound;//total Size
  private long recommendRelatedNum;
  private long unRelatedNum;
  private long selfRelatedNum;
  private long applyRelatedNum;
  private long relatedNum;
  private Pager pager;
  private List<String> licenceNoList;//车牌号，用在今日新增车辆的客户的车辆信息中高亮显示

  private long totalReceivableNumFound;//有应收的客户或者供应商

  //联系人
  private List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
  @Deprecated
  private Long countTodayCustomer; //今日新增客户数量

  public List<ContactDTO> getContactDTOList() {
    return contactDTOList;
  }

  public void setContactDTOList(List<ContactDTO> contactDTOList) {
    this.contactDTOList = contactDTOList;
  }

  public Long getCountTodayCustomer() {
    return countTodayCustomer;
  }

  public void setCountTodayCustomer(Long countTodayCustomer) {
    this.countTodayCustomer = countTodayCustomer;
  }

  public long getHasMobileNumFound() {
    return hasMobileNumFound;
  }

  public void setHasMobileNumFound(long hasMobileNumFound) {
    this.hasMobileNumFound = hasMobileNumFound;
  }

  public long getMemberNumFound() {
    return memberNumFound;
  }

  public void setMemberNumFound(long memberNumFound) {
    this.memberNumFound = memberNumFound;
  }

  public List<CustomerSupplierSearchResultDTO> getCustomerSuppliers() {
    return customerSuppliers;
  }

  public void setCustomerSuppliers(List<CustomerSupplierSearchResultDTO> customerSuppliers) {
    this.customerSuppliers = customerSuppliers;
  }

  public List<CustomerDTO> getCustomerDTOs() {
    return customerDTOs;
  }

  public void setCustomerDTOs(List<CustomerDTO> customerDTOs) {
    this.customerDTOs = customerDTOs;
  }

  public List<CustomerRecordDTO> getCustomerRecordDTOs() {
    return customerRecordDTOs;
  }

  public void setCustomerRecordDTOs(List<CustomerRecordDTO> customerRecordDTOs) {
    this.customerRecordDTOs = customerRecordDTOs;
  }

  public Double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(Double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }

  public long getNumFound() {
    return numFound;
  }

  public void setNumFound(long numFound) {
    this.numFound = numFound;
  }

  public Map<String, Long> getCounts() {
    return counts;
  }

  public void setCounts(Map<String, Long> counts) {
    this.counts = counts;
  }

  public String getMobiles() {
    return mobiles;
  }

  public void setMobiles(String mobiles) {
    this.mobiles = mobiles;
  }

  public List<SupplierDTO> getSupplierDTOs() {
    return supplierDTOs;
  }

  public void setSupplierDTOs(List<SupplierDTO> supplierDTOs) {
    this.supplierDTOs = supplierDTOs;
  }

  public long getDeptNum() {
    return deptNum;
  }

  public void setDeptNum(long deptNum) {
    this.deptNum = deptNum;
  }

  public Double getTotalDeposit() {
    return totalDeposit;
  }

  public void setTotalDeposit(Double totalDeposit) {
    this.totalDeposit = totalDeposit;
  }

  public Double getTotalDebt() {
    return totalDebt;
  }

  public void setTotalDebt(Double totalDebt) {
    this.totalDebt = totalDebt;
  }

  public Long getTodayNewCustomerNumFound() {
    return todayNewCustomerNumFound;
  }

  public void setTodayNewCustomerNumFound(Long todayNewCustomerNumFound) {
    this.todayNewCustomerNumFound = todayNewCustomerNumFound;
  }

  public long getRecommendRelatedNum() {
    return recommendRelatedNum;
  }

  public void setRecommendRelatedNum(long recommendRelatedNum) {
    this.recommendRelatedNum = recommendRelatedNum;
  }

  public long getUnRelatedNum() {
    return unRelatedNum;
  }

  public void setUnRelatedNum(long unRelatedNum) {
    this.unRelatedNum = unRelatedNum;
  }

  public long getSelfRelatedNum() {
    return selfRelatedNum;
  }

  public void setSelfRelatedNum(long selfRelatedNum) {
    this.selfRelatedNum = selfRelatedNum;
  }

  public long getApplyRelatedNum() {
    return applyRelatedNum;
  }

  public void setApplyRelatedNum(long applyRelatedNum) {
    this.applyRelatedNum = applyRelatedNum;
  }

  public long getRelatedNum() {
    return relatedNum;
  }

  public void setRelatedNum(long relatedNum) {
    this.relatedNum = relatedNum;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public Double getTotalConsumption() {
    return totalConsumption;
  }

  public void setTotalConsumption(Double totalConsumption) {
    this.totalConsumption = NumberUtil.round(totalConsumption, 2);
  }

  public void setTotalReceivableNumFound(long totalReceivableNumFound) {
    this.totalReceivableNumFound = totalReceivableNumFound;
  }

  public long getTotalReceivableNumFound() {
    return totalReceivableNumFound;
  }

  public Double getTotalBalance() {
    return totalBalance;
  }

  public void setTotalBalance(Double totalBalance) {
    this.totalBalance = totalBalance;
  }

  public long getHasObdNumFound() {
    return hasObdNumFound;
  }

  public void setHasObdNumFound(long hasObdNumFound) {
    this.hasObdNumFound = hasObdNumFound;
  }

  public List<Long> getOdbContactIdList() {
    return odbContactIdList;
  }

  public void setOdbContactIdList(List<Long> odbContactIdList) {
    this.odbContactIdList = odbContactIdList;
  }

  public List<String> getLicenceNoList() {
    return licenceNoList;
  }

  public void setLicenceNoList(List<String> licenceNoList) {
    this.licenceNoList = licenceNoList;
  }

  public long getHasAppNumFound() {
    return hasAppNumFound;
  }

  public void setHasAppNumFound(long hasAppNumFound) {
    this.hasAppNumFound = hasAppNumFound;
  }

  public List<String> getAppMobileList() {
    return appMobileList;
  }

  public void setAppMobileList(List<String> appMobileList) {
    this.appMobileList = appMobileList;
  }
}