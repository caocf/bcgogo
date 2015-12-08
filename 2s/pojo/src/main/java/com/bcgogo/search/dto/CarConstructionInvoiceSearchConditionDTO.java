package com.bcgogo.search.dto;

/**
 * Created with IntelliJ IDEA.
 * User: wenjun
 * Date: 13-10-16
 * Time: 上午9:30
 * To change this template use File | Settings | File Templates.
 */
public class CarConstructionInvoiceSearchConditionDTO {
  private String repairRemindEventTypes;
  private String orderStatus;
  private int startPageNo = 1;
  private int maxRows = 5;

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public String getRepairRemindEventTypes() {
    return repairRemindEventTypes;
  }

  public void setRepairRemindEventTypes(String repairRemindEventTypes) {
    this.repairRemindEventTypes = repairRemindEventTypes;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
}
