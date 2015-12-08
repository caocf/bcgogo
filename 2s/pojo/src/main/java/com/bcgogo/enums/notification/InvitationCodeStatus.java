package com.bcgogo.enums.notification;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午5:15
 */
public enum InvitationCodeStatus {
  EFFECTIVE, //有效
  OVERDUE,  //过期
  INVALID_OVERDUE, //过期无效
  BE_USED;   //已使用

  //回收
  public static boolean isRecycle(Long createTime, String config, InvitationCodeStatus status) {
    if (status == BE_USED || status == INVALID_OVERDUE) return true;
    if (StringUtils.isBlank(config)) {
      config = "60";
    }
    return System.currentTimeMillis() - createTime > Long.valueOf(config) * 24 * 60 * 60 * 1000;
  }

  //邀请码过期时间
  public static boolean isOverdue(Long createTime, String config) {
    if (StringUtils.isBlank(config)) config = "10";
    return System.currentTimeMillis() - createTime > Long.valueOf(config) * 24 * 60 * 60 * 1000;
  }
}
