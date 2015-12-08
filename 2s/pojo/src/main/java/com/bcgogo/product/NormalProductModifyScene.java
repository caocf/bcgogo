package com.bcgogo.product;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-3-3
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public enum NormalProductModifyScene {
  CONFIRM_UNCHECKED_RELEVANCE("确认复核"),
  RELEVANCE("关联"),
  DELETE_RELEVANCE("取消关联"),
  DELETE("删除"),
  MODIFY("修改"),
  ADD("新增");

  private NormalProductModifyScene(String name){
    this.name=name;
  }
  private String name;

  public String getName() {
    return name;
  }
}
