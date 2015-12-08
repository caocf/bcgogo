package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午7:38
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardReturnDTO extends BcgogoOrderDto{
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
  private String editDateStr;
  private Long reviewerId;
  private Long reviewDate;
  private Long invalidatorId;
  private Long invalidateDate;
  private Long lastMemberCardOrderId;
  private Double lastBuyTotal;
  private Long lastBuyDate;
  private String lastBuyDateStr;
  private Long returnDate;
  private String returnDateStr;
  private List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs;
  private List<MemberCardReturnItemDTO> memberCardReturnItemDTOs;
  private String memo;
  private ReceptionRecordDTO receptionRecordDTO;
  private MemberDTO memberDTO;
  private String memberCardName;
  private String repayTime;
  private String mobile;
  private String customerName;
  //营业统计会员相关
  private String orderNo;//单据号
  private String memberNo;       //会员号码
  private String memberCardType;  //会员卡类型 :计次卡  或 储值卡
  private Double memberAmount;  //办卡金额
  private Double settledAmount;  //实收金额
  private String salesMan;  //操作员

  private Double lastRecharge;
  private Double memberBalance;   //储值余额

  private Long creationDate;

  public OrderIndexDTO toOrderIndexDTO(){
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setId(this.getId());
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setMemberNo(this.getMemberNo());
    orderIndexDTO.setMemberType(this.getMemberCardType());
    orderIndexDTO.setMemberLastBuyDate(this.getLastBuyDate());
    orderIndexDTO.setMemberLastBuyTotal(this.getLastBuyTotal());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setMemberBalance(this.getMemberBalance()); //储值余额
    orderIndexDTO.setWorth(this.getLastRecharge());   //储值新增金额
    orderIndexDTO.setMemberType(this.getMemberCardType());
    orderIndexDTO.setOrderType(OrderTypes.MEMBER_RETURN_CARD);
    orderIndexDTO.setOrderStatus(OrderStatus.MEMBERCARD_ORDER_STATUS);
    orderIndexDTO.setCreationDate(this.getReturnDate());
    List<MemberCardReturnServiceDTO> memberCardServiceDTOs = this.getMemberCardReturnServiceDTOs();
    List<MemberCardReturnItemDTO> memberCardReturnItemDTOList = this.getMemberCardReturnItemDTOs();
    if (CollectionUtils.isNotEmpty(memberCardReturnItemDTOList))
      orderIndexDTO.setMemberLastRecharge(memberCardReturnItemDTOList.get(0).getLastRecharge());
    if (CollectionUtils.isEmpty(memberCardServiceDTOs)) {
      if (StringUtils.isNotBlank(this.getMemberCardName())) {
        orderIndexDTO.setOrderContent("会员卡(" + this.getMemberCardName() + ")");
      } else {
        orderIndexDTO.setOrderContent("会员卡");
      }
    } else {
      StringBuffer orderContent = new StringBuffer();
      Integer balanceTimes = 0;
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      for (MemberCardReturnServiceDTO memberCardReturnServiceDTO : memberCardServiceDTOs) {
        if(memberCardReturnServiceDTO==null) continue;
        itemIndexDTOList.add(memberCardReturnServiceDTO.toItemIndexDTO(this));

        balanceTimes = memberCardReturnServiceDTO.getRemainTimes();
        if (balanceTimes == null) {
          orderContent.append("(").append(memberCardReturnServiceDTO.getServiceName()).append(",").append(0).append("次)");
        } else if (balanceTimes == -1) {
          orderContent.append("(").append(memberCardReturnServiceDTO.getServiceName()).append(",").append("无限次").append(")");
        } else {
          orderContent.append("(").append(memberCardReturnServiceDTO.getServiceName()).append(",").append(balanceTimes).append("次)");
        }
      }
      orderIndexDTO.setOrderContent(orderContent.toString());
      orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
    }
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setOrderSettled(this.getSettledAmount());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerId());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomerName());
    orderIndexDTO.setContactNum(this.getMobile());
    orderIndexDTO.setMemberBalance(this.getMemberBalance());
    orderIndexDTO.setWorth(this.getLastRecharge());   //上次储值金额
    return orderIndexDTO;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getExecutorId() {
    return executorId;
  }

  public void setExecutorId(Long executorId) {
    this.executorId = executorId;
  }

  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
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

  public Long getReviewerId() {
    return reviewerId;
  }

  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  public Long getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Long reviewDate) {
    this.reviewDate = reviewDate;
  }

  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public void setInvalidatorId(Long invalidatorId) {
    this.invalidatorId = invalidatorId;
  }

  public Long getInvalidateDate() {
    return invalidateDate;
  }

  public void setInvalidateDate(Long invalidateDate) {
    this.invalidateDate = invalidateDate;
  }

  public Long getLastBuyDate() {
    return lastBuyDate;
  }

  public void setLastBuyDate(Long lastBuyDate) {
    if(lastBuyDate!=null)
      lastBuyDateStr = DateUtil.dateLongToStr(lastBuyDate, DateUtil.DATE_STRING_FORMAT_DAY);
    this.lastBuyDate = lastBuyDate;
  }

  public String getLastBuyDateStr() {
    return lastBuyDateStr;
  }

  public void setLastBuyDateStr(String lastBuyDateStr) {
    this.lastBuyDateStr = lastBuyDateStr;
  }

  public Long getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(Long returnDate) {
    if(returnDate!=null){
      returnDateStr = DateUtil.dateLongToStr(returnDate, DateUtil.DATE_STRING_FORMAT_DAY);
    }
    this.returnDate = returnDate;
  }

  public String getReturnDateStr() {
    return returnDateStr;
  }

  public void setReturnDateStr(String returnDateStr) {
    this.returnDateStr = returnDateStr;
  }

  public List<MemberCardReturnServiceDTO> getMemberCardReturnServiceDTOs() {
    return memberCardReturnServiceDTOs;
  }

  public void setMemberCardReturnServiceDTOs(List<MemberCardReturnServiceDTO> memberCardReturnServiceDTOs) {
    this.memberCardReturnServiceDTOs = memberCardReturnServiceDTOs;
  }

  public List<MemberCardReturnItemDTO> getMemberCardReturnItemDTOs() {
    return memberCardReturnItemDTOs;
  }

  public void setMemberCardReturnItemDTOs(List<MemberCardReturnItemDTO> memberCardReturnItemDTOs) {
    this.memberCardReturnItemDTOs = memberCardReturnItemDTOs;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public ReceptionRecordDTO getReceptionRecordDTO() {
    return receptionRecordDTO;
  }

  public void setReceptionRecordDTO(ReceptionRecordDTO receptionRecordDTO) {
    this.receptionRecordDTO = receptionRecordDTO;
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  public String getMemberCardName() {
    return memberCardName;
  }

  public void setMemberCardName(String memberCardName) {
    this.memberCardName = memberCardName;
  }

  public String getRepayTime() {
    return repayTime;
  }

  public void setRepayTime(String repayTime) {
    this.repayTime = repayTime;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getMemberCardType() {
    return memberCardType;
  }

  public void setMemberCardType(String memberCardType) {
    this.memberCardType = memberCardType;
  }

  public Double getMemberAmount() {
    return memberAmount;
  }

  public void setMemberAmount(Double memberAmount) {
    this.memberAmount = memberAmount;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  public MemberCardOrderItemDTO[] getItemDTOs() {
    return null;
  }

  public void setItemDTOs(MemberCardOrderItemDTO[] itemDTOs) {
//    this.itemDTOs = itemDTOs;
  }

  public Double getLastRecharge() {
    return lastRecharge;
  }

  public void setLastRecharge(Double lastRecharge) {
    this.lastRecharge = lastRecharge;
  }

  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public Long getLastMemberCardOrderId() {
    return lastMemberCardOrderId;
  }

  public void setLastMemberCardOrderId(Long lastMemberCardOrderId) {
    this.lastMemberCardOrderId = lastMemberCardOrderId;
  }

  public Double getLastBuyTotal() {
    return lastBuyTotal;
  }

  public void setLastBuyTotal(Double lastBuyTotal) {
    this.lastBuyTotal = lastBuyTotal;
  }

  public Long getVestDate(){
    return returnDate;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public String getCustomerCompany() {
    return customerCompany;
  }

  public void setCustomerCompany(String customerCompany) {
    this.customerCompany = customerCompany;
  }

  public String getCustomerContact() {
    return customerContact;
  }

  public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public String getCustomerLandline() {
    return customerLandline;
  }

  public void setCustomerLandline(String customerLandline) {
    this.customerLandline = customerLandline;
  }

  public String getCustomerAddress() {
    return customerAddress;
  }

  public void setCustomerAddress(String customerAddress) {
    this.customerAddress = customerAddress;
  }

  @Override
  public String toString() {
    return "MemberCardReturnDTO{" +
        "no='" + no + '\'' +
        ", customerId=" + customerId +
        ", executorId=" + executorId +
        ", deptId=" + deptId +
        ", total=" + total +
        ", editorId=" + editorId +
        ", editDate=" + editDate +
        ", editDateStr='" + editDateStr + '\'' +
        ", reviewerId=" + reviewerId +
        ", reviewDate=" + reviewDate +
        ", invalidatorId=" + invalidatorId +
        ", invalidateDate=" + invalidateDate +
        ", lastBuyTotal=" + lastBuyTotal +
        ", lastBuyDate=" + lastBuyDate +
        ", lastBuyDateStr='" + lastBuyDateStr + '\'' +
        ", returnDate=" + returnDate +
        ", returnDateStr='" + returnDateStr + '\'' +
        ", memberCardReturnServiceDTOs=" + memberCardReturnServiceDTOs +
        ", memo='" + memo + '\'' +
        ", receptionRecordDTO=" + receptionRecordDTO +
        ", memberDTO=" + memberDTO +
        ", memberCardName='" + memberCardName + '\'' +
        ", repayTime='" + repayTime + '\'' +
        ", mobile='" + mobile + '\'' +
        ", customerName='" + customerName + '\'' +
        ", orderNo='" + orderNo + '\'' +
        ", memberNo='" + memberNo + '\'' +
        ", memberCardType='" + memberCardType + '\'' +
        ", memberAmount=" + memberAmount +
        ", settledAmount=" + settledAmount +
        ", salesMan='" + salesMan + '\'' +
        ", lastRecharge=" + lastRecharge +
        ", memberBalance=" + memberBalance +
        ", creationDate=" + creationDate +
        '}';
  }

  public void setReceivableDTO(ReceivableDTO receivableDTO) {
    if (receivableDTO != null) {
     this.setSettledAmount(receivableDTO.getSettledAmount());
    }
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    customerId = customerDTO.getId();
    customer = customerDTO.getName();
    customerAddress = customerDTO.getAddress();
    customerCompany = customerDTO.getCompany();
    customerContact = customerDTO.getContact();
    customerLandline = customerDTO.getLandLine();
    customerMobile = customerDTO.getMobile();
  }
}
