package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午7:38
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardOrderDTO extends BcgogoOrderDto{
  private static final Logger LOG = LoggerFactory.getLogger(MemberCardOrderDTO.class);
  private String no;
  private Long customerId;
  private String company;
  private String landline;
  private String address;
  private Long executorId;
  private Long deptId;
  private Double total;   //购卡金额
  private Long editorId;
  private Long editDate;
  private String editDateStr;
  private Long reviewerId;
  private Long reviewDate;
  private Long invalidatorId;
  private Long invalidateDate;
  private Long vestDate;
  private String vestDateStr;
  private List<MemberCardOrderItemDTO> memberCardOrderItemDTOs;
  private List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs;
  private List<MemberCardOrderServiceDTO> newMemberCardOrderServiceDTOs;
  private String memo;
  private ReceivableDTO receivableDTO;
  private MemberDTO memberDTO;
  private String memberCardName;
  private String repayTime;
  private String mobile;
  private String customerName;
  private String contact;
  //营业统计会员相关
  private String orderNo;//单据号
  private String memberNo;       //会员号码
  private String memberCardType;  //会员卡类型 :计次卡  或 储值卡
  private Double memberAmount;  //办卡金额
  private Double settledAmount;  //实收金额
  private String salesMan;  //购卡续卡的销售人员
  private MemberCardOrderItemDTO[] itemDTOs;

  public String getOrderNo() {
    return orderNo;
  }
  private Double worth;     //购卡时新増的储值金额
  private Double memberBalance;   //购卡时的剩余金额

  private Long creationDate;

//  public Long getId() {
//    return id;
//  }
  private String oldMemberNo;

  private Double memberDiscount;

  private Double oldMemberDiscount;

  private String memberType;

  private String oldMemberType;
  private MemberStatus oldMemberStatus;

  private Boolean sendMemberMsg;

  private MemberOrderType memberOrderType;

    public Boolean getSendMemberMsg() {
        return sendMemberMsg;
    }

    public void setSendMemberMsg(Boolean sendMemberMsg) {
        this.sendMemberMsg = sendMemberMsg;
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
    this.memberAmount = NumberUtil.doubleVal(memberAmount);
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = NumberUtil.doubleVal(settledAmount);
  }

  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  public String getNo() {
    return no;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Long getExecutorId() {
    return executorId;
  }

  public Long getDeptId() {
    return deptId;
  }

  public Double getTotal() {
    return total;
  }

  public Long getEditorId() {
    return editorId;
  }

  public Long getEditDate() {
    return editDate;
  }

  public Long getReviewerId() {
    return reviewerId;
  }

  public Long getReviewDate() {
    return reviewDate;
  }

  public Long getInvalidatorId() {
    return invalidatorId;
  }

  public Long getInvalidateDate() {
    return invalidateDate;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public List<MemberCardOrderItemDTO> getMemberCardOrderItemDTOs() {
    return memberCardOrderItemDTOs;
  }

  public List<MemberCardOrderServiceDTO> getMemberCardOrderServiceDTOs() {
    return memberCardOrderServiceDTOs;
  }

  public String getMemo() {
    return memo;
  }

  public ReceivableDTO getReceivableDTO() {
    return receivableDTO;
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public String getRepayTime() {
    return repayTime;
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

  public void setMemberCardOrderItemDTOs(List<MemberCardOrderItemDTO> memberCardOrderItemDTOs) {
    this.memberCardOrderItemDTOs = memberCardOrderItemDTOs;
  }

  public void setMemberCardOrderServiceDTOs(List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs) {
    this.memberCardOrderServiceDTOs = memberCardOrderServiceDTOs;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public void setReceivableDTO(ReceivableDTO receivableDTO) {
    this.receivableDTO = receivableDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
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

  public List<MemberCardOrderServiceDTO> getNewMemberCardOrderServiceDTOs() {
    return newMemberCardOrderServiceDTOs;
  }

  public void setNewMemberCardOrderServiceDTOs(List<MemberCardOrderServiceDTO> newMemberCardOrderServiceDTOs) {
    this.newMemberCardOrderServiceDTOs = newMemberCardOrderServiceDTOs;
  }

  public String getMemberCardName() {
    return memberCardName;
  }

  public void setMemberCardName(String memberCardName) {
    this.memberCardName = memberCardName;
  }

  public Double getWorth() {
    return worth;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  public String getOldMemberNo() {
    return oldMemberNo;
  }

  public void setOldMemberNo(String oldMemberNo) {
    this.oldMemberNo = oldMemberNo;
  }

  public Double getMemberDiscount() {
    return memberDiscount;
  }

  public void setMemberDiscount(Double memberDiscount) {
    this.memberDiscount = memberDiscount;
  }

  public Double getOldMemberDiscount() {
    return oldMemberDiscount;
  }

  public void setOldMemberDiscount(Double oldMemberDiscount) {
    this.oldMemberDiscount = oldMemberDiscount;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public MemberOrderType getMemberOrderType() {
    return memberOrderType;
  }

  public void setMemberOrderType(MemberOrderType memberOrderType) {
    this.memberOrderType = memberOrderType;
  }

  @Override
  public String toString() {
    return "MemberCardOrderDTO{" +
        "id='" + getId() + '\'' +
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
        ", vestDate=" + vestDate +
        ", vestDateStr='" + vestDateStr + '\'' +
        ", memberCardOrderItemDTOs=" + memberCardOrderItemDTOs +
        ", memberCardOrderServiceDTOs=" + memberCardOrderServiceDTOs +
        ", newMemberCardOrderServiceDTOs=" + newMemberCardOrderServiceDTOs +
        ", memo='" + memo + '\'' +
        ", receivableDTO=" + receivableDTO +
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
        '}';
  }

  public MemberCardOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(MemberCardOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public String getOldMemberType() {
    return oldMemberType;
  }

  public void setOldMemberType(String oldMemberType) {
    this.oldMemberType = oldMemberType;
  }

  public MemberStatus getOldMemberStatus() {
    return oldMemberStatus;
  }

  public void setOldMemberStatus(MemberStatus oldMemberStatus) {
    this.oldMemberStatus = oldMemberStatus;
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.MEMBER_BUY_CARD);
    orderIndexDTO.setMemo(this.getMemo());
    List<MemberCardOrderServiceDTO> memberCardServiceDTOs = this.getNewMemberCardOrderServiceDTOs();
    if (CollectionUtils.isEmpty(memberCardServiceDTOs)) {
      if (StringUtils.isNotBlank(this.getMemberCardName())) {
        orderIndexDTO.setOrderContent("会员卡(" + this.getMemberCardName() + ")");
      } else {
        orderIndexDTO.setOrderContent("会员卡");
      }
    } else {
      StringBuffer orderContent = new StringBuffer();
      Integer balanceTimes = 0;
      List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
      for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardServiceDTOs) {
        if (memberCardOrderServiceDTO == null) {
          continue;
        }
        itemIndexDTOs.add(memberCardOrderServiceDTO.toItemIndexDTO(this));

        balanceTimes = memberCardOrderServiceDTO.getBalanceTimes();
        if (balanceTimes == null) {
          orderContent.append("(").append(memberCardOrderServiceDTO.getServiceName()).append(",").append(0).append("次)");
        } else if (balanceTimes == -1) {
          orderContent.append("(").append(memberCardOrderServiceDTO.getServiceName()).append(",").append("无限次").append(")");
        } else {
          orderContent.append("(").append(memberCardOrderServiceDTO.getServiceName()).append(",").append(balanceTimes).append("次)");
        }
      }
      orderIndexDTO.setItemIndexDTOList(itemIndexDTOs);
      orderIndexDTO.setOrderContent(orderContent.toString());
    }
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setCustomerOrSupplierId(this.getCustomerId());
    orderIndexDTO.setCustomerOrSupplierName(this.getCustomerName());
    orderIndexDTO.setContactNum(this.getMobile());
    if(getMemberDTO()!=null){
      orderIndexDTO.setMemberNo(getMemberDTO().getMemberNo());
      orderIndexDTO.setMemberType(getMemberDTO().getType());
      orderIndexDTO.setMemberStatus(getMemberDTO().getStatus()==null?"":getMemberDTO().getStatus().getStatus());
    }
    orderIndexDTO.setMemberBalance(this.getMemberBalance()); //储值余额
    orderIndexDTO.setWorth(this.getWorth());                 //储值新增金额
    if (null != orderIndexDTO.getArrears() && orderIndexDTO.getArrears() > 0) {
      try {
        orderIndexDTO.setPaymentTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getRepayTime()));
      } catch (ParseException e) {
        LOG.error(e.getMessage(), e);
      }
    }
    //操作员
    orderIndexDTO.setCreationDate(this.getCreationDate());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setSalesMans(StringUtils.isBlank(this.getSalesMan()) ? TxnConstant.ASSISTANT_NAME : this.getSalesMan());
    orderIndexDTO.setOrderTotalAmount(this.getTotal());
    orderIndexDTO.setOrderId(this.getId());
    if (receivableDTO != null) {
      orderIndexDTO.setDiscount(receivableDTO.getDiscount());
      orderIndexDTO.setOrderTotalAmount(receivableDTO.getTotal());
      orderIndexDTO.setOrderDebt(receivableDTO.getDebt());
      //欠款
      orderIndexDTO.setArrears(receivableDTO.getDebt());
      orderIndexDTO.setOrderSettled(receivableDTO.getSettledAmount());
      //支付方式 存入solr
      List<PayMethod> payMethods = new ArrayList<PayMethod>();
      if (receivableDTO.getCash() != null && receivableDTO.getCash() > 0) { //现金
        payMethods.add(PayMethod.CASH);
      }
      if (receivableDTO.getBankCard() != null && receivableDTO.getBankCard() > 0) { //银行卡
        payMethods.add(PayMethod.BANK_CARD);
      }
      if (receivableDTO.getCheque() != null && receivableDTO.getCheque() > 0) {// 支票
        payMethods.add(PayMethod.CHEQUE);
      }
      if (this.getStatementAccountOrderId() != null) {//对账支付
        payMethods.add(PayMethod.STATEMENT_ACCOUNT);
      }

      //会员购卡续卡产生欠款，使用会员储值进行欠款结算 这个购卡续卡单据就有会员消费
      if (receivableDTO.getMemberId() != null && receivableDTO.getMemberId() != 0) {
        payMethods.add(PayMethod.MEMBER_BALANCE_PAY);
        orderIndexDTO.setAccountMemberId(receivableDTO.getMemberId());
        orderIndexDTO.setAccountMemberNo(receivableDTO.getMemberNo());
        orderIndexDTO.setMemberBalancePay(NumberUtil.doubleVal(receivableDTO.getMemberBalancePay()));
      }
      orderIndexDTO.setPayMethods(payMethods);
    }
    return orderIndexDTO;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    if(customerDTO==null){
      return;
    }
    setCompany(customerDTO.getCompany());
    setMobile(customerDTO.getMobile());
    setLandline(customerDTO.getLandLine());
    setContact(customerDTO.getContact());
    setAddress(customerDTO.getAddress());
  }
}
