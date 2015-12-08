package com.bcgogo.config;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午10:09
 * 日志 搜索条件
 */
public class CRMOperationLogCondition {
  private Long operateTimeStart;
  private Long operateTimeEnd;
  private Long shopId;
  private String module;
  private String type;
  private String content;
  private String ipAddress;
  private String userNo;
  private int start;
  private int limit;

  public Long getOperateTimeStart() {
    return operateTimeStart;
  }

  public void setOperateTimeStart(Long operateTimeStart) {
    this.operateTimeStart = operateTimeStart;
  }

  public Long getOperateTimeEnd() {
    return operateTimeEnd;
  }

  public void setOperateTimeEnd(Long operateTimeEnd) {
    this.operateTimeEnd = operateTimeEnd;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
