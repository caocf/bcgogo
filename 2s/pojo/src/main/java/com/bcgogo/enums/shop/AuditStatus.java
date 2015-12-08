package com.bcgogo.enums.shop;

/**
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:11
 * 店铺审核结果
 */
public enum AuditStatus {
  AGREE, DISAGREE;

  public static boolean isAuditPass(AuditStatus status) {
    return AGREE == status;
  }
}
