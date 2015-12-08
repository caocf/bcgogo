package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.MemberCardReturnDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-16
 * Time: 上午10:07
 */
@Entity
@Table(name = "member_card_return")
public class MemberCardReturn extends LongIdentifier {
  private Long shopId;
  private String no;
  private Long customerId;
  private String customer;
  private String customerCompany;
  private String customerContact;
  private String customerMobile;
  private String customerLandline;
  private String customerAddress;
  private Long executorId;
  private Long deptId;
  private Double total;
  private Long editorId;
  private Long editDate;
  private Long reviewerId;
  private Long reviewDate;
  private Long invalidatorId;
  private Long invalidateDate;
  private Long lastMemberCardOrderId;
  private Double lastBuyTotal;
  private Long lastBuyDate;
  private String memberNo;
  private String memberCardName;
  private Long returnDate;
  private String memo;
  private Double memberBalance;
  private Long  statementAccountOrderId;//对账单id

  @Column(name = "statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Column(name = "no")
  public String getNo() {
    return no;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  @Column(name = "executor_id")
  public Long getExecutorId() {
    return executorId;
  }

  @Column(name = "dept_id")
  public Long getDeptId() {
    return deptId;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  @Column(name = "editor_id")
  public Long getEditorId() {
    return editorId;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  @Column(name = "reviewer_id")
  public Long getReviewerId() {
    return reviewerId;
  }

  @Column(name = "review_date")
  public Long getReviewDate() {
    return reviewDate;
  }

  @Column(name = "invalidator_id")
  public Long getInvalidatorId() {
    return invalidatorId;
  }

  @Column(name = "invalidate_date")
  public Long getInvalidateDate() {
    return invalidateDate;
  }

  @Column(name = "last_member_card_order_id")
  public Long getLastMemberCardOrderId() {
    return lastMemberCardOrderId;
  }

  public void setLastMemberCardOrderId(Long lastMemberCardOrderId) {
    this.lastMemberCardOrderId = lastMemberCardOrderId;
  }

  @Column(name = "return_date")
  public Long getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(Long returnDate) {
    this.returnDate = returnDate;
  }

  @Column(name = "member_balance")
  public Double getMemberBalance() {
    return memberBalance;
  }

  @Column(name="last_buy_total")
  public Double getLastBuyTotal() {
    return lastBuyTotal;
  }

  public void setLastBuyTotal(Double lastBuyTotal) {
    this.lastBuyTotal = lastBuyTotal;
  }

  @Column(name="last_buy_date")
  public Long getLastBuyDate() {
    return lastBuyDate;
  }

  public void setLastBuyDate(Long lastBuyDate) {
    this.lastBuyDate = lastBuyDate;
  }

  @Column(name="member_no")
  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  @Column(name="member_card_name")
  public String getMemberCardName() {
    return memberCardName;
  }

  public void setMemberCardName(String memberCardName) {
    this.memberCardName = memberCardName;
  }

  @Column(name="memo")
  public String getMemo() {
    return memo;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public void setExecutorId(Long executorId) {
    this.executorId = executorId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  public void setInvalidateDate(Long invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  @Column(name="customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name="customer_company")
  public String getCustomerCompany() {
    return customerCompany;
  }

  public void setCustomerCompany(String customerCompany) {
    this.customerCompany = customerCompany;
  }

  @Column(name="customer_mobile")
  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  @Column(name="customer_landline")
  public String getCustomerLandline() {
    return customerLandline;
  }

  public void setCustomerLandline(String customerLandline) {
    this.customerLandline = customerLandline;
  }

  @Column(name="customer_address")
  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  @Column(name="customer_contact")
  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  public MemberCardReturn() {
  }

  public MemberCardReturn(MemberCardReturnDTO memberCardReturnDTO) {
    this.shopId = memberCardReturnDTO.getShopId();
    this.no = memberCardReturnDTO.getNo();
    this.customerId = memberCardReturnDTO.getCustomerId();
    this.customer = memberCardReturnDTO.getCustomer();
    this.customerAddress = memberCardReturnDTO.getCustomerAddress();
    this.customerCompany = memberCardReturnDTO.getCustomerCompany();
    this.customerContact = memberCardReturnDTO.getCustomerContact();
    this.customerLandline = memberCardReturnDTO.getCustomerLandline();
    this.customerMobile = memberCardReturnDTO.getCustomerMobile();
    this.executorId = memberCardReturnDTO.getExecutorId();
    this.deptId = memberCardReturnDTO.getDeptId();
    this.total = memberCardReturnDTO.getTotal();
    this.editorId = memberCardReturnDTO.getEditorId();
    this.editDate = memberCardReturnDTO.getEditDate();
    this.reviewerId = memberCardReturnDTO.getReviewerId();
    this.reviewDate = memberCardReturnDTO.getReviewDate();
    this.invalidatorId = memberCardReturnDTO.getInvalidatorId();
    this.invalidateDate = memberCardReturnDTO.getInvalidateDate();
    this.lastMemberCardOrderId = memberCardReturnDTO.getLastMemberCardOrderId();
    this.returnDate = memberCardReturnDTO.getReturnDate();
    this.memo = memberCardReturnDTO.getMemo();
    this.memberBalance = memberCardReturnDTO.getMemberBalance();
    this.lastBuyTotal = memberCardReturnDTO.getLastBuyTotal();
    this.lastBuyDate = memberCardReturnDTO.getLastBuyDate();
    this.memberNo = memberCardReturnDTO.getMemberNo();
    this.memberCardName = memberCardReturnDTO.getMemberCardName();
    this.statementAccountOrderId = memberCardReturnDTO.getStatementAccountOrderId();
  }

  public MemberCardReturnDTO toDTO() {
    if (null == this) {
      return null;
    }
    MemberCardReturnDTO memberCardReturnDTO = new MemberCardReturnDTO();
    memberCardReturnDTO.setId(getId());
    memberCardReturnDTO.setShopId(shopId);
    memberCardReturnDTO.setNo(no);
    memberCardReturnDTO.setCustomerId(customerId);
    memberCardReturnDTO.setCustomer(customer);
    memberCardReturnDTO.setCustomerAddress(customerAddress);
    memberCardReturnDTO.setCustomerCompany(customerCompany);
    memberCardReturnDTO.setCustomerContact(customerContact);
    memberCardReturnDTO.setCustomerLandline(customerLandline);
    memberCardReturnDTO.setCustomerMobile(customerMobile);
    memberCardReturnDTO.setExecutorId(executorId);
    memberCardReturnDTO.setDeptId(deptId);
    memberCardReturnDTO.setTotal(total);
    memberCardReturnDTO.setEditorId(editorId);
    memberCardReturnDTO.setEditDate(editDate);
    memberCardReturnDTO.setReviewerId(reviewerId);
    memberCardReturnDTO.setReviewDate(reviewDate);
    memberCardReturnDTO.setInvalidatorId(invalidatorId);
    memberCardReturnDTO.setInvalidateDate(invalidateDate);
    memberCardReturnDTO.setReturnDate(returnDate);
    memberCardReturnDTO.setMemo(memo);
    memberCardReturnDTO.setLastMemberCardOrderId(lastMemberCardOrderId);
    memberCardReturnDTO.setMemberBalance(memberBalance);
    memberCardReturnDTO.setLastBuyTotal(lastBuyTotal);
    memberCardReturnDTO.setLastBuyDate(lastBuyDate);
    memberCardReturnDTO.setMemberNo(memberNo);
    memberCardReturnDTO.setMemberCardName(memberCardName);
    memberCardReturnDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    return memberCardReturnDTO;
  }
}
