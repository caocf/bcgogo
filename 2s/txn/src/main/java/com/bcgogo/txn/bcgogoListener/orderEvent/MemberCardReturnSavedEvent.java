package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.txn.dto.MemberCardReturnDTO;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-18
 * Time: 上午11:55
 */
public class MemberCardReturnSavedEvent extends OrderSavedEvent{

  private MemberCardReturnDTO memberCardReturnDTO;   //会员卡退卡信息

  public MemberCardReturnDTO getMemberCardReturnDTO() {
    return memberCardReturnDTO;
  }

  public void setMemberCardReturnDTO(MemberCardReturnDTO memberCardReturnDTO) {
    this.memberCardReturnDTO = memberCardReturnDTO;
  }

  public MemberCardReturnSavedEvent(MemberCardReturnDTO memberCardReturnDTO) {
    super(memberCardReturnDTO);
    this.memberCardReturnDTO = memberCardReturnDTO;
  }
}
