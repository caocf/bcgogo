package com.bcgogo.wx;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-23
 * Time: 上午10:05
 * To change this template use File | Settings | File Templates.
 */
public enum WXArticleCategory {
  FESTIVAL("节日"),
  PROMOTION("营销"),
  ;
  private String type;

  WXArticleCategory(String type){
    this.type=type;
  }

}
