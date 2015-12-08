package com.bcgogo.enums.shop;

import com.bcgogo.utils.ArrayUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-22
 * Time: 下午1:06
 * To change this template use File | Settings | File Templates.
 */
public enum InviteStatus {
//  PENDING,
//  ACCEPTED,
//  REFUSED,
//  DELETED;

  PENDING("待处理"),
  ACCEPTED("已接受"),
  REFUSED("已拒绝"),
  DELETED("已删除"),
  ALL("所有未删除"),
  OPPOSITES_PENDING("对方已申请");  //目前仅用于新手指引


  private final String value;

  private InviteStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  private static Map<String, InviteStatus> lookup = new HashMap<String, InviteStatus>();

  static {
    for (InviteStatus inviteStatus : InviteStatus.values()) {
      lookup.put(inviteStatus.name(), inviteStatus);
    }
  }

  public static InviteStatus parseName(String status) {
    InviteStatus inviteStatus = lookup.get(status);
    if(inviteStatus == null)
      throw new IllegalArgumentException("InviteStatus " + status +"不存在!");
    return inviteStatus;
  }

  public static List<InviteStatus> parseNameList(String status) {
    List<InviteStatus> inviteStatuses = new ArrayList<InviteStatus>();
    if(StringUtils.isBlank(status)){
      return inviteStatuses;
    }
    String[] statusArray = status.split(",");
    if (!ArrayUtil.isEmpty(statusArray)) {
      for (String statusStr : statusArray) {
        if (parseName(statusStr) != null) {
          inviteStatuses.add(parseName(statusStr));
        }
      }
    }
    return inviteStatuses;
  }
}
