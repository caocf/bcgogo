package com.bcgogo.txn.model;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午7:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "member_card_order")
public class MemberCardOrder extends LongIdentifier{
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
  private Long vestDate;
  private String memo;
  private Double worth;
  private Double memberBalance;
  private String oldMemberNo;
  private Double oldMemberDiscount;
  private String oldMemberType;
  private MemberStatus oldMemberStatus;
  private Double memberDiscount;
  private Long statementAccountOrderId;//对账单id

  private MemberOrderType memberOrderType;

  @Column(name="statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }
  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }
  @Column(name="no")
  public String getNo() {
    return no;
  }
  @Column(name="customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  @Column(name="customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name="executor_id")
  public Long getExecutorId() {
    return executorId;
  }
  @Column(name="dept_id")
  public Long getDeptId() {
    return deptId;
  }
  @Column(name="total")
  public Double getTotal() {
    return total;
  }
  @Column(name="editor_id")
  public Long getEditorId() {
    return editorId;
  }
  @Column(name="edit_date")
  public Long getEditDate() {
    return editDate;
  }
  @Column(name="reviewer_id")
  public Long getReviewerId() {
    return reviewerId;
  }
  @Column(name="review_date")
  public Long getReviewDate() {
    return reviewDate;
  }
  @Column(name="invalidator_id")
  public Long getInvalidatorId() {
    return invalidatorId;
  }
  @Column(name="invalidate_date")
  public Long getInvalidateDate() {
    return invalidateDate;
  }
  @Column(name="vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  @Column(name="worth")
  public Double getWorth() {
    return worth;
  }

  @Column(name="member_balance")
  public Double getMemberBalance() {
    return memberBalance;
  }

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

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  @Column(name="old_member_no")
  public String getOldMemberNo() {
    return oldMemberNo;
  }

  public void setOldMemberNo(String oldMemberNo) {
    this.oldMemberNo = oldMemberNo;
  }

  @Column(name="old_member_discount")
  public Double getOldMemberDiscount() {
    return oldMemberDiscount;
  }

  public void setOldMemberDiscount(Double oldMemberDiscount) {
    this.oldMemberDiscount = oldMemberDiscount;
  }

  @Column(name="member_discount")
  public Double getMemberDiscount() {
    return memberDiscount;
  }

  public void setMemberDiscount(Double memberDiscount) {
    this.memberDiscount = memberDiscount;
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

  @Column(name="old_member_type")
  public String getOldMemberType() {
    return oldMemberType;
  }

  public void setOldMemberType(String oldMemberType) {
    this.oldMemberType = oldMemberType;
  }

  @Column(name="old_member_status")
  @Enumerated(EnumType.STRING)
  public MemberStatus getOldMemberStatus() {
    return oldMemberStatus;
  }

  public void setOldMemberStatus(MemberStatus oldMemberStatus) {
    this.oldMemberStatus = oldMemberStatus;
  }

  @Column(name="customer_contact")
  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  @Column(name="member_order_type")
  @Enumerated(EnumType.STRING)
  public MemberOrderType getMemberOrderType() {
    return memberOrderType;
  }

  public void setMemberOrderType(MemberOrderType memberOrderType) {
    this.memberOrderType = memberOrderType;
  }

  public MemberCardOrder()
  {

  }

  public MemberCardOrder(MemberCardOrderDTO memberCardOrderDTO)
  {
    if(null== memberCardOrderDTO)
    {
      return;
    }

    this.setId(memberCardOrderDTO.getId());
    this.setCustomerId(memberCardOrderDTO.getCustomerId());
    this.setCustomer(memberCardOrderDTO.getCustomerName());
    this.setReviewDate(memberCardOrderDTO.getReviewDate());
    this.setDeptId(memberCardOrderDTO.getDeptId());
    this.setEditDate(memberCardOrderDTO.getEditDate());
    this.setEditorId(memberCardOrderDTO.getEditorId());
    this.setInvalidateDate(memberCardOrderDTO.getInvalidateDate());
    this.setInvalidatorId(memberCardOrderDTO.getInvalidatorId());
    this.setMemo(memberCardOrderDTO.getMemo());
    this.setNo(memberCardOrderDTO.getNo());
    this.setReviewerId(memberCardOrderDTO.getReviewerId());
    this.setShopId(memberCardOrderDTO.getShopId());
    this.setTotal(memberCardOrderDTO.getTotal());
    this.setExecutorId(memberCardOrderDTO.getExecutorId());
    this.setVestDate(memberCardOrderDTO.getVestDate());
    this.setWorth(memberCardOrderDTO.getWorth());
    this.setMemberBalance(memberCardOrderDTO.getMemberBalance());
    this.setOldMemberNo(memberCardOrderDTO.getOldMemberNo());
    this.setMemberDiscount(memberCardOrderDTO.getMemberDiscount());
    this.setOldMemberDiscount(memberCardOrderDTO.getOldMemberDiscount());
    this.setCustomerCompany(memberCardOrderDTO.getCompany());
    this.setCustomerContact(memberCardOrderDTO.getContact());
    this.setCustomerMobile(memberCardOrderDTO.getMobile());
    this.setCustomerLandline(memberCardOrderDTO.getLandline());
    this.setCustomerAddress(memberCardOrderDTO.getAddress());
    this.setOldMemberStatus(memberCardOrderDTO.getOldMemberStatus());
    this.setOldMemberType(memberCardOrderDTO.getOldMemberType());
    this.setStatementAccountOrderId(memberCardOrderDTO.getStatementAccountOrderId());
    this.setMemberOrderType(memberCardOrderDTO.getMemberOrderType());
  }

  public MemberCardOrderDTO toDTO()
  {
    if(null == this)
    {
      return null;
    }
    MemberCardOrderDTO memberCardOrderDTO = new MemberCardOrderDTO();
    memberCardOrderDTO.setId(this.getId());
    memberCardOrderDTO.setShopId(this.getShopId());
    memberCardOrderDTO.setNo(this.getNo());
    memberCardOrderDTO.setCustomerId(this.getCustomerId());
    memberCardOrderDTO.setCustomerName(this.getCustomer());
    memberCardOrderDTO.setExecutorId(this.getExecutorId());
    memberCardOrderDTO.setDeptId(this.getDeptId());
    memberCardOrderDTO.setTotal(this.getTotal());
    memberCardOrderDTO.setEditorId(this.getEditorId());
    memberCardOrderDTO.setEditDate(this.getEditDate());
    memberCardOrderDTO.setReviewerId(this.getReviewerId());
    memberCardOrderDTO.setReviewDate(this.getReviewDate());
    memberCardOrderDTO.setInvalidatorId(this.getInvalidatorId());
    memberCardOrderDTO.setInvalidateDate(this.getInvalidateDate());
    memberCardOrderDTO.setVestDate(this.getVestDate());
    memberCardOrderDTO.setVestDateStr(DateUtil.dateLongToStr(this.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY));
    memberCardOrderDTO.setMemo(this.getMemo());
    memberCardOrderDTO.setWorth(this.getWorth());
    memberCardOrderDTO.setMemberBalance(this.getMemberBalance());
    memberCardOrderDTO.setCreationDate(this.getCreationDate());
    memberCardOrderDTO.setOldMemberNo(this.getOldMemberNo());
    memberCardOrderDTO.setOldMemberDiscount(this.getOldMemberDiscount());
    memberCardOrderDTO.setMemberDiscount(this.getMemberDiscount());
    memberCardOrderDTO.setCompany(getCustomerCompany());
    memberCardOrderDTO.setContact(getCustomerContact());
    memberCardOrderDTO.setMobile(getCustomerMobile());
    memberCardOrderDTO.setLandline(getCustomerLandline());
    memberCardOrderDTO.setAddress(getCustomerAddress());
    memberCardOrderDTO.setOldMemberStatus(getOldMemberStatus());
    memberCardOrderDTO.setOldMemberType(getOldMemberType());
    memberCardOrderDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    memberCardOrderDTO.setMemberOrderType(getMemberOrderType());
    return memberCardOrderDTO;
  }
}
