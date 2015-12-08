package com.bcgogo.pojo;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-5
 * Time: 15:33
 */
public enum Command {
  //打印命令
  PRINT("print"),
  //升级客户端
  UPDATE("update"),
  ;

  private String name;

  Command(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
