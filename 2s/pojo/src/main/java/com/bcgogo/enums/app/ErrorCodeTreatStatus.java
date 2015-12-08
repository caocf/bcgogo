package com.bcgogo.enums.app;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 手机端发送车辆故障信息给后台，故障信息是否处理的枚举
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-30
 * Time: 上午9:48
 * To change this template use File | Settings | File Templates.
 */
public enum ErrorCodeTreatStatus {
  UNTREATED("未处理"),//未处理
  FIXED("已修复"),//已修复
  IGNORED("已忽略"),//已忽略
  DELETED("已删除")//已删除
  ;
  private final String name;

  private ErrorCodeTreatStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Map<String, ErrorCodeTreatStatus> errorCodeTreatStatusMap;

  static {
    if (errorCodeTreatStatusMap == null) {
      errorCodeTreatStatusMap = new HashMap<String, ErrorCodeTreatStatus>();
      for (ErrorCodeTreatStatus errorCodeTreatStatus : ErrorCodeTreatStatus.values()) {
        errorCodeTreatStatusMap.put(errorCodeTreatStatus.name(), errorCodeTreatStatus);
      }
    }
  }

  public static Set<ErrorCodeTreatStatus> generateStatus(String status) {
    Set<ErrorCodeTreatStatus> statusSet = new HashSet<ErrorCodeTreatStatus>();
    if (StringUtils.isNotBlank(status)) {
      String[] statusArr = status.split(",");
      for (String statusStr : statusArr) {
        ErrorCodeTreatStatus errorCodeTreatStatus = errorCodeTreatStatusMap.get(statusStr);
        if (errorCodeTreatStatus != null) {
          statusSet.add(errorCodeTreatStatus);
        }
      }
    }
    return statusSet;
  }

}
