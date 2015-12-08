package com.bcgogo.stat.dto;

import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;

import java.io.Serializable;

public class BusinessAccountDTO implements Serializable {

  //主键ID
  private Long id;

  private String idStr;

  //店面ID
  private Long shopId;

  //记账人
 private String accountEditor ;

  //记账日期
  private Long editDate ;

  //记账日期字符串
  private String editDateStr;

  //记账类别
  private String accountCategory;

  private Long accountCategoryId;//记账类别id


  //凭证号
  private String  docNo;

  //部门
  private String dept;

   //人员
  private String person;

  private Long departmentId;//部门id
  private Long salesManId;//员工id

  //内容
  private String content;

  // 资金使用分类（收入或者支出）
  private MoneyCategory moneyCategory;

  // 现金金额
  private Double cash;

  // 银联卡金额
  private Double unionpay;

  // 支票金额
  private Double check;

 // 现金金额 + 银联卡金额 +支票金额
  private Double total;

  //记账逻辑状态（删除 或者 正常）
  private String status;

  //营业分类
  private String businessCategory;
  private Long businessCategoryId;//营业分类id

  private String businessAccountStr;

  private String moneyCategoryStr;


  public String userName;
  public Long userId;

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getAccountEditor() {
    return accountEditor;
  }

  public void setAccountEditor(String accountEditor) {
    this.accountEditor = accountEditor;
  }

  public String getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(String accountCategory) {
    this.accountCategory = accountCategory;
  }

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  public String getPerson() {
    return person;
  }

  public void setPerson(String person) {
    this.person = person;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getUnionpay() {
    return unionpay;
  }

  public void setUnionpay(Double unionpay) {
    this.unionpay = unionpay;
  }

  public Double getCheck() {
    return check;
  }

  public void setCheck(Double check) {
    this.check = check;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getAccountCategoryId() {
    return accountCategoryId;
  }

  public void setAccountCategoryId(Long accountCategoryId) {
    this.accountCategoryId = accountCategoryId;
  }

  public String getBusinessAccountStr() {
    return businessAccountStr;
  }

  public void setBusinessAccountStr(String businessAccountStr) {
    this.businessAccountStr = businessAccountStr;
  }

  public String getMoneyCategoryStr() {
    return moneyCategoryStr;
  }

  public void setMoneyCategoryStr(String moneyCategoryStr) {
    this.moneyCategoryStr = moneyCategoryStr;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public Long getSalesManId() {
    return salesManId;
  }

  public void setSalesManId(Long salesManId) {
    this.salesManId = salesManId;
  }
}
