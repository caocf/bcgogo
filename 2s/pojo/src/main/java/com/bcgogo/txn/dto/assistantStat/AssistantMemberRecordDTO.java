package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.user.MemberCardType;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.dto.MemberCardOrderItemDTO;
import com.bcgogo.txn.dto.MemberCardReturnDTO;
import com.bcgogo.txn.dto.MemberCardReturnItemDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * 员工会员销售记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午1:07
 * To change this template use File | Settings | File Templates.
 */
public class AssistantMemberRecordDTO extends AssistantRecordDTO {


  private String memberNo;

  private Long memberCardId;
  private String memberCardName;
  private MemberCardType memberCardType;

  private Double memberCardTotal;//会员卡设置的金额
  private Double total; //会员卡购卡续卡单据的金额

  private String memberCardTypeStr;

  private Long memberAchievementHistoryId;

  private MemberOrderType memberOrderType;//购卡还是续卡
  private String memberOrderTypeStr;//购卡还是续卡

  public AssistantMemberRecordDTO() {
  }


  public AssistantMemberRecordDTO(MemberCardOrderDTO memberCardOrderDTO, MemberCardOrderItemDTO memberCardOrderItemDTO) {
    this.setShopId(memberCardOrderDTO.getShopId());
    this.setOrderId(memberCardOrderDTO.getId());
    this.setOrderType(OrderTypes.MEMBER_BUY_CARD);
    this.setMemberNo(memberCardOrderDTO.getMemberNo());

    this.setVestDate(memberCardOrderDTO.getVestDate());

    this.setCustomer(memberCardOrderDTO.getCustomerName());
    this.setCustomerId(memberCardOrderDTO.getCustomerId());

    this.setMemberCardId(memberCardOrderItemDTO.getCardId());

    this.setMemberCardTotal(memberCardOrderItemDTO.getPrice());
    this.setTotal(memberCardOrderDTO.getTotal());

    this.setMemberOrderType(memberCardOrderDTO.getMemberOrderType());

  }

   public AssistantMemberRecordDTO(MemberCardReturnDTO memberCardReturnDTO, MemberCardReturnItemDTO memberCardOrderItemDTO) {
    this.setShopId(memberCardReturnDTO.getShopId());
    this.setOrderId(memberCardReturnDTO.getId());
    this.setOrderType(OrderTypes.MEMBER_RETURN_CARD);

    this.setVestDate(memberCardReturnDTO.getVestDate());

    this.setCustomer(memberCardReturnDTO.getCustomer());
    this.setCustomerId(memberCardReturnDTO.getCustomerId());

    this.setMemberNo(memberCardReturnDTO.getMemberNo());
    this.setMemberCardId(memberCardOrderItemDTO.getCardId());
    this.setMemberCardName(memberCardReturnDTO.getMemberCardName());
    this.setMemberCardTotal(NumberUtil.doubleVal(memberCardReturnDTO.getLastBuyTotal()));
    this.setTotal(-NumberUtil.doubleVal(memberCardReturnDTO.getTotal()));
  }


  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public Long getMemberCardId() {
    return memberCardId;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
  }

  public String getMemberCardName() {
    return memberCardName;
  }

  public void setMemberCardName(String memberCardName) {
    this.memberCardName = memberCardName;
  }

  public MemberCardType getMemberCardType() {
    return memberCardType;
  }

  public void setMemberCardType(MemberCardType memberCardType) {
    this.memberCardType = memberCardType;
  }

  public Double getMemberCardTotal() {
    return memberCardTotal;
  }

  public void setMemberCardTotal(Double memberCardTotal) {
    this.memberCardTotal = NumberUtil.toReserve(memberCardTotal,NumberUtil.PRECISION);;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = NumberUtil.toReserve(total,NumberUtil.PRECISION);;
  }

  public Long getMemberAchievementHistoryId() {
    return memberAchievementHistoryId;
  }

  public void setMemberAchievementHistoryId(Long memberAchievementHistoryId) {
    this.memberAchievementHistoryId = memberAchievementHistoryId;
  }

  public String getMemberCardTypeStr() {
    return memberCardTypeStr;
  }

  public void setMemberCardTypeStr(String memberCardTypeStr) {
    this.memberCardTypeStr = memberCardTypeStr;
  }

  public MemberOrderType getMemberOrderType() {
    return memberOrderType;
  }

  public void setMemberOrderType(MemberOrderType memberOrderType) {
    this.memberOrderType = memberOrderType;
  }

  public String getMemberOrderTypeStr() {
    return memberOrderTypeStr;
  }

  public void setMemberOrderTypeStr(String memberOrderTypeStr) {
    this.memberOrderTypeStr = memberOrderTypeStr;
  }
}
