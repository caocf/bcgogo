package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.MemberCardOrderDTO;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-8-14
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardOrderSavedEvent extends OrderSavedEvent{

  private MemberCardOrderDTO memberCardOrderDTO;//会员卡购卡信息

  public MemberCardOrderDTO getMemberCardOrderDTO() {
    return memberCardOrderDTO;
  }

  public void setMemberCardOrderDTO(MemberCardOrderDTO memberCardOrderDTO) {
    this.memberCardOrderDTO = memberCardOrderDTO;
  }

  public MemberCardOrderSavedEvent(MemberCardOrderDTO memberCardOrderDTO) {
    super(memberCardOrderDTO);
    this.memberCardOrderDTO = memberCardOrderDTO;
  }
}
