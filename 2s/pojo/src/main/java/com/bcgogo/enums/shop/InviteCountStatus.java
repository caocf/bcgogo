package com.bcgogo.enums.shop;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-28
 * Time: 上午9:09
 * To change this template use File | Settings | File Templates.
 */
public enum InviteCountStatus {
  CUSTOMER_INVITE_PENDING,
  CUSTOMER_INVITE_ALL,//不包括删除
  SUPPLIER_INVITE_PENDING,
  SUPPLIER_INVITE_ALL;

  public static InviteCountStatus getCountStatus(InviteType inviteType, InviteStatus status) {
    InviteCountStatus inviteCountStatus = null;
    if (inviteType == null || status == null) {
      return inviteCountStatus;
    }
    if (InviteType.CUSTOMER_INVITE.equals(inviteType) && InviteStatus.PENDING.equals(status)) {
      inviteCountStatus = CUSTOMER_INVITE_PENDING;
    } else if (InviteType.CUSTOMER_INVITE.equals(inviteType) && InviteStatus.ALL.equals(status)) {
      inviteCountStatus = CUSTOMER_INVITE_ALL;
    } else if (InviteType.SUPPLIER_INVITE.equals(inviteType) && InviteStatus.PENDING.equals(status)) {
      inviteCountStatus = SUPPLIER_INVITE_PENDING;
    } else if (InviteType.SUPPLIER_INVITE.equals(inviteType) && InviteStatus.ALL.equals(status)) {
      inviteCountStatus = SUPPLIER_INVITE_ALL;
    }
    return inviteCountStatus;
  }

}
