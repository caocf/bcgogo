package com.bcgogo.stat.model;

import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantBusinessAccountRecordDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;

/**
 * 营业外记账表
 */
@Entity
@Table(name = "business_account")
public class BusinessAccount extends LongIdentifier {

  //店面ID
  private Long shopId;

  //记账人
  private String accountEditor ;

  //记账日期
  private Long editDate ;

  //记账类别（固定5项：房租、工资提成、水电杂项、其他、营业外收入）
  private String accountCategory;

  private Long accountCategoryId;//记账类别id

  //凭证号
  private String  docNo;

  //部门
  private String dept;

  private Long departmentId;//部门id
  private Long salesManId;//员工id

  //人员
  private String person;

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

  @Column(name = "business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name = "business_category")
  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "account_editor")
  public String getAccountEditor() {
    return accountEditor;
  }

  public void setAccountEditor(String accountEditor) {
    this.accountEditor = accountEditor;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate ;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "account_category")
  public String getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(String accountCategory) {
    this.accountCategory = accountCategory;
  }

  @Column(name = "doc_no")
  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  @Column(name = "dept")
  public String getDept() {
    return dept;
  }

  public void setDept(String dept) {
    this.dept = dept;
  }

  @Column(name = "person")
  public String getPerson() {
    return person;
  }

  public void setPerson(String person) {
    this.person = person;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "money_category")
  @Enumerated(EnumType.STRING)
  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  @Column(name = "cash")
  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  @Column(name = "unionpay")
  public Double getUnionpay() {
    return unionpay;
  }

  public void setUnionpay(Double unionpay) {
    this.unionpay = unionpay;
  }


  @Column(name = "check_amount")
  public Double getCheck() {
    return check;
  }

  public void setCheck(Double check) {
    this.check = check;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "account_category_id")
  public Long getAccountCategoryId() {
    return accountCategoryId;
  }

  public void setAccountCategoryId(Long accountCategoryId) {
    this.accountCategoryId = accountCategoryId;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "sales_man_id")
  public Long getSalesManId() {
    return salesManId;
  }

  public void setSalesManId(Long salesManId) {
    this.salesManId = salesManId;
  }

  public BusinessAccountDTO toDTO() {

    BusinessAccountDTO businessAccountDTO = new BusinessAccountDTO();
    businessAccountDTO.setAccountCategory(this.accountCategory);
    businessAccountDTO.setAccountEditor(this.accountEditor);
    businessAccountDTO.setCash(this.cash);
    businessAccountDTO.setCheck(this.check);
    businessAccountDTO.setContent(this.content);
    businessAccountDTO.setEditDate(editDate);
    businessAccountDTO.setDept(this.dept);
    businessAccountDTO.setDocNo(this.docNo);
    businessAccountDTO.setId(this.getId());
    businessAccountDTO.setMoneyCategory(this.moneyCategory);
    businessAccountDTO.setPerson(this.person);
    businessAccountDTO.setShopId(this.shopId);
    businessAccountDTO.setUnionpay(this.unionpay);
    businessAccountDTO.setStatus(this.status);
    businessAccountDTO.setIdStr(this.getId().toString());
    businessAccountDTO.setTotal(this.total);
    businessAccountDTO.setBusinessCategory(this.businessCategory);
    String editDateStr = DateUtil.dateLongToStr(this.editDate, DateUtil.YEAR_MONTH_DATE);
    businessAccountDTO.setEditDateStr(editDateStr);
    businessAccountDTO.setAccountCategoryId(this.getAccountCategoryId());
    businessAccountDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    businessAccountDTO.setSalesManId(this.getSalesManId());
    businessAccountDTO.setDepartmentId(this.getDepartmentId());

    return businessAccountDTO;
  }

  public AssistantBusinessAccountRecordDTO toRecordDTO() {

    AssistantBusinessAccountRecordDTO recordDTO = new AssistantBusinessAccountRecordDTO();
    recordDTO.setShopId(getShopId());
    recordDTO.setVestDate(getEditDate());
    recordDTO.setAssistantId(getSalesManId());
    recordDTO.setAssistantName(getPerson());
    recordDTO.setDepartmentId(getDepartmentId());
    recordDTO.setDepartmentName(getDept());
    recordDTO.setContent(getContent());
    recordDTO.setTotal(getTotal());
    recordDTO.setAccountCategory(getAccountCategory());
    recordDTO.setBusinessCategory(getBusinessCategory());
    recordDTO.setBusinessCategoryId(getBusinessCategoryId());
    recordDTO.setDocNo(getDocNo());
    recordDTO.setBusinessAccountId(getId());

    return recordDTO;
  }

  public BusinessAccount fromDTO(BusinessAccountDTO businessAccountDTO) throws Exception
  {
    if(businessAccountDTO == null )
    {
        return this;
    }
    this.setId(businessAccountDTO.getId() )  ;
    this.setAccountCategory(businessAccountDTO.getAccountCategory()  )  ;
    this.setAccountEditor(businessAccountDTO.getAccountEditor()  );
    this.setCash(businessAccountDTO.getCash()  )  ;
    this.setCheck(businessAccountDTO.getCheck()  );
    this.setContent(businessAccountDTO.getContent()  );
    this.setEditDate(businessAccountDTO.getEditDate());
    this.setDept(businessAccountDTO.getDept()  );
    this.setDocNo(businessAccountDTO.getDocNo()  );
    this.setMoneyCategory(businessAccountDTO.getMoneyCategory());
    this.setPerson(businessAccountDTO.getPerson()  );
    this.setShopId(businessAccountDTO.getShopId()  );
    this.setUnionpay(businessAccountDTO.getUnionpay()  );
    this.setStatus(businessAccountDTO.getStatus()  );
    this.setTotal(businessAccountDTO.getTotal());
    this.setBusinessCategory(businessAccountDTO.getBusinessCategory());
    String editDateStr =  businessAccountDTO.getEditDateStr();
    if( editDateStr != null)
    {
        this.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,editDateStr));
    }

    this.setAccountCategoryId(businessAccountDTO.getAccountCategoryId());
    this.setBusinessCategoryId(businessAccountDTO.getBusinessCategoryId());
    this.setDepartmentId(businessAccountDTO.getDepartmentId());
    this.setSalesManId(businessAccountDTO.getSalesManId());

    return this;
  }




}
