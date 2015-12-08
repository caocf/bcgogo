package com.bcgogo.user.dto;

import com.bcgogo.api.MemberInfoDTO;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.PasswordValidateStatus;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class MemberDTO {
  private Long id;
  private Long shopId;
  private String shopName;
  private Long customerId;
  private String customerIdStr;
  private String type;
  private String memberNo;
  private MemberStatus status;
  private String statusStr;
  private Double balance;//会员储值
  private String balanceStr;
  private Double memberConsumeTotal;    //会员卡累计消费
  private String memberConsumeTotalStr;
  private Integer accumulatePoints;
  private Double serviceDiscount;
  private Double materialDiscount;
  private Long joinDate;
  private Long deadline;
  private String password;
  private List<MemberServiceDTO> memberServiceDTOs;
  private String joinDateStr;
  private String dateKeep;
  private PasswordValidateStatus passwordStatus;
  private String serviceDeadLineStr;
  private Long washServiceId;
  private boolean isVIP;
  private String cardServices;  //该会员的会员卡的 购卡内容

  private String deadlineStr;
  private Double memberDiscount;
  private Long memberConsumeTimes;//会员卡累计消费次数

  public MemberInfoDTO toMemberInfo() {
    MemberInfoDTO dto = new MemberInfoDTO();
    dto.setMemberNo(getMemberNo());
    dto.setType(getType());
    dto.setStatus(getStatus() != null ? getStatus().getStatus() : null);
    dto.setBalance(getBalance());
    dto.setAccumulatePoints(getAccumulatePoints());
    dto.setMaterialDiscount(getMaterialDiscount());
    dto.setMemberDiscount(getMemberDiscount());
    dto.setServiceDiscount(getServiceDiscount());
    dto.setJoinDate(getJoinDate());
    dto.setDeadline(getDeadline());
    dto.setMemberConsumeTotal(getMemberConsumeTotal());
    return dto;
  }

  public Long getWashServiceId() {
    return washServiceId;
  }

  public void setWashServiceId(Long washServiceId) {
    this.washServiceId = washServiceId;
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public String getType() {
    return type;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public MemberStatus getStatus() {
    return status;
  }

  public Double getBalance() {
    return balance;
  }

  public Integer getAccumulatePoints() {
    return accumulatePoints;
  }

  public Double getServiceDiscount() {
    return serviceDiscount;
  }

  public Double getMaterialDiscount() {
    return materialDiscount;
  }

  public Long getJoinDate() {
    return joinDate;
  }

  public Long getDeadline() {
    return deadline;
  }

  public String getPassword() {
    return password;
  }

  public PasswordValidateStatus getPasswordStatus() {
    return passwordStatus;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setCustomerId(Long customerId) {
      if(customerId!=null){
          this.customerIdStr=customerId.toString();
      }
    this.customerId = customerId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setType(String type) {
    if (StringUtils.isNotBlank(type) && "VIP卡".equals(type)) {
      this.isVIP = true;
    } else {
      this.isVIP = false;
    }
    this.type = type;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public void setStatus(MemberStatus status) {
    this.status = status;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public void setAccumulatePoints(Integer accumulatePoints) {
    this.accumulatePoints = accumulatePoints;
  }

  public void setServiceDiscount(Double serviceDiscount) {
    this.serviceDiscount = serviceDiscount;
  }

  public void setMaterialDiscount(Double materialDiscount) {
    this.materialDiscount = materialDiscount;
  }

  public void setJoinDate(Long joinDate) {
    this.joinDate = joinDate;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<MemberServiceDTO> getMemberServiceDTOs() {
    return memberServiceDTOs;
  }

  public void setMemberServiceDTOs(List<MemberServiceDTO> memberServiceDTOs) {
    this.memberServiceDTOs = memberServiceDTOs;
  }

  public String getJoinDateStr() {
    return joinDateStr;
  }

  public void setJoinDateStr(String joinDateStr) {
    this.joinDateStr = joinDateStr;
  }

  public String getDateKeep() {
    return dateKeep;
  }

  public void setDateKeep(String dateKeep) {
    this.dateKeep = dateKeep;
  }

  public String getServiceDeadLineStr() {
    return serviceDeadLineStr;
  }

  public void setServiceDeadLineStr(String serviceDeadLineStr) {
    this.serviceDeadLineStr = serviceDeadLineStr;
  }

  public void setPasswordStatus(PasswordValidateStatus passwordStatus) {
    this.passwordStatus = passwordStatus;
  }

  public boolean getIsVIP() {
    return isVIP;
  }

  public String getCardServices() {
    return cardServices;
  }

  public void setCardServices(String cardServices) {
    this.cardServices = cardServices;
  }

  public String getBalanceStr() {
    return balance == null ? "" : balance.toString();
  }

  public void setBalanceStr(String balanceStr) {
    this.balanceStr = balanceStr;
  }

  public Double getMemberConsumeTotal() {
    return memberConsumeTotal;
  }

  public void setMemberConsumeTotal(Double memberConsumeTotal) {
    this.memberConsumeTotal = memberConsumeTotal;
  }

  public String getMemberConsumeTotalStr() {
    return memberConsumeTotal==null ? "" :memberConsumeTotal.toString();
  }

  public void setMemberConsumeTotalStr(String memberConsumeTotalStr) {
    this.memberConsumeTotalStr = memberConsumeTotalStr;
  }

  public String getDeadlineStr() {
    return deadlineStr;
  }

  public void setDeadlineStr(String deadlineStr) {
    this.deadlineStr = deadlineStr;
  }

  public Double getMemberDiscount() {
    return memberDiscount;
  }

  public void setMemberDiscount(Double memberDiscount) {
    this.memberDiscount = memberDiscount;
  }

    public String getCustomerIdStr() {
        return customerIdStr;
    }

    public void setCustomerIdStr(String customerIdStr) {
        this.customerIdStr = customerIdStr;
    }

  public Long getMemberConsumeTimes() {
    return memberConsumeTimes;
  }

  public void setMemberConsumeTimes(Long memberConsumeTimes) {
    this.memberConsumeTimes = memberConsumeTimes;
  }
}
