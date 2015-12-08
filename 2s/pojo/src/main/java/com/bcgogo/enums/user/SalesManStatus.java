package com.bcgogo.enums.user;

/**
 * 每个员工的状态：在职 离职 试用
 * Created by IntelliJ IDEA.
 * User: LW
 * Date: 12-7-16
 * Time: 上午9:26
 * To change this template use File | Settings | File Templates.
 */
public enum SalesManStatus {
	DELETED("删除"),
	DEMISSION("离职"),  //离职
  INSERVICE("在职"), //在职
  ONTRIAL("试用");      //试用

  String name;

  SalesManStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
