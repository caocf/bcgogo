package com.bcgogo.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


/**
 * wrap data and msg ,then send to client
 *
 * @author xzhu
 */
public class Result implements Serializable{

  public static final String SUCCESS="success";

  private static final long serialVersionUID = 6920105849582836361L;

  private String title;
  private String msg="Operation Finished";
  private boolean success = true;
  private Integer total;
  private Object data;
  private String dataStr; //传输id用object的data，值会发生变化
  private Object[] dataList;
  private String operation;

  public Result() {
    super();
  }

  public Result(boolean success) {
    this.success = success;
  }

  public Result(String title, String msg, boolean success) {
    this.title = title;
    this.msg = msg;
    this.success = success;
  }

  public Result(String title, String msg, boolean success, Operation operation){
    this.title = title;
    this.msg = msg;
    this.success = success;
    this.operation = operation == null?"":operation.getValue();
  }

  public Result(String msg, boolean success) {
    super();
    this.msg = msg;
    this.success = success;
  }
  public Result(boolean success, Operation operation) {
    super();
    this.success = success;
    this.operation = operation == null?"":operation.getValue();
  }
  public Result(boolean success,Object data) {
    super();
    this.data = data;
    this.success = success;
  }

  public Result(String msg,boolean success,String operation,Object data) {
    super();
    this.msg = msg;
    this.success = success;
    this.data=data;
    this.operation = operation;
  }
  public Result(String msg,boolean success,Object data) {
    super();
    this.msg = msg;
    this.success = success;
    this.data=data;
  }
  public Result(Object data) {
    this.data=data;
  }

  public Result(String msg, Integer total, Object data) {
    super();
    this.msg = msg;
    this.total = total;
    this.data = data;
  }

  public Result LogErrorMsg(String msg){
    this.success=false;
    this.msg=msg;
    return this;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Integer getTotal() {
    return total;
  }

  public Object getData() {
    return data;
  }



  public void setMsg(boolean isSuccess,String msg) {
    this.success=isSuccess;
    this.msg = msg;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getDataStr() {
    return dataStr;
  }

  public void setDataStr(String dataStr) {
    this.dataStr = dataStr;
  }

  public Object[] getDataList() {
    return dataList;
  }

  public void setDataList(Object[] dataList) {
    this.dataList = dataList;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public enum Operation {
    UPDATE_SUPPLIER_INVENTORY("update_supplier_inventory"),
    UPDATE_PRODUCT_INVENTORY("update_product_inventory"),
    REFRESH_REPAIR_ORDER("refresh_repair_order"),
    CONFIRM_DELETED_PRODUCT("confirm_deleted_product"),
    ALERT_WHOLESALER_PRODUCT_NOT_EXIST("alert_wholesaler_product_not_exist"),
    MODIFY_COMMODITY_CODE("MODIFY_COMMODITY_CODE"),
    ALERT("ALERT"),
    CONFIRM_RELATED_SUPPLIER("CONFIRM_RELATED_SUPPLIER"),
    CONFIRM("CONFIRM"),
    REDIRECT_SHOW("REDIRECT_SHOW"),
    ALERT_SALE_LACK("ALERT_SALE_LACK"),
    ALLOCATE_OR_PURCHASE("ALLOCATE_OR_PURCHASE"),
    CONFIRM_ALLOCATE_RECORD("CONFIRM_ALLOCATE_RECORD"),
    TO_CHOOSE_STOREHOUSE("TO_CHOOSE_STOREHOUSE"),
    ALERT_REDIRECT("ALERT_REDIRECT"),
    UPDATE_SUPPLIER_RELATION("UPDATE_SUPPLIER_RELATION"),
    UPDATE_PRODUCT_SALES_STATUS("UPDATE_PRODUCT_SALES_STATUS"),
    CONFIRM_RESTORE_SUPPLIER("CONFIRM_RESTORE_SUPPLIER"),
    NOT_ENOUGH_INVENTORY("NOT_ENOUGH_INVENTORY"),
    PROMPT_EXIST_REPAIR_ORDER("PROMPT_EXIST_REPAIR_ORDER");

    String value;

    public String getValue() {
      return value;
    }

    private Operation(String value) {
      this.value = value;
    }

  }

  @Override
  public String toString() {
    return "Result{" +
        "title='" + title + '\'' +
        ", msg='" + msg + '\'' +
        ", success=" + success +
        ", total=" + total +
        ", data=" + data +
        ", dataStr='" + dataStr + '\'' +
        ", dataList=" + Arrays.toString(dataList) +
        ", operation='" + operation + '\'' +
        '}';
  }
}
