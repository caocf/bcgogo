package com.bcgogo.enums.app;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 上午11:08
 */
public enum EnquiryStatus {
  DISABLED("已删除"),
  SAVED("已保存"),
  SENT("已发送");

  private final String name;

  private EnquiryStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Map<String,EnquiryStatus> enquiryStatusMap ;
  static {
      if(enquiryStatusMap == null){
        enquiryStatusMap = new HashMap<String, EnquiryStatus>();
        for(EnquiryStatus enquiryStatus : EnquiryStatus.values()){
          enquiryStatusMap.put(enquiryStatus.name(),enquiryStatus);
        }
      }
  }

  public static Set<EnquiryStatus> generateStatus(String status) {
    Set<EnquiryStatus> statusSet = new HashSet<EnquiryStatus>();
    if(StringUtils.isNotBlank(status)){
       String[] statusArr =  status.split(",");
      for(String  statusStr : statusArr){
        EnquiryStatus enquiryStatus = enquiryStatusMap.get(statusStr);
        if(enquiryStatus != null){
          statusSet.add(enquiryStatus);
        }
      }
    }
    return statusSet;
  }
}
