package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-9-10
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
public enum DraftOrderStatus {
  DRAFT_NEW("新建"),
  DRAFT_READ_ONLY("只读"),
  DRAFT_READ_WRITE("可读写"),
  DRAFT_SAVED("已保存"),
  DRAFT_REPEAL("作废");

  private final String name;
  private DraftOrderStatus(String name){
    this.name = name;
  }

  public String getName(){
    return name;
  }
}
