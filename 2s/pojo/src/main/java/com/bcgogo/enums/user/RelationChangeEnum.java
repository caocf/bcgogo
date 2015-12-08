package com.bcgogo.enums.user;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-28
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public enum RelationChangeEnum {
  UNCHANGED,         //关系未变化
  RELATED_TO_UNRELATED,    //关联变成非关联
  UNRELATED_TO_RELATED,    //非关联变成关联
  RELATED_CHANGED          //关联关系改变了，关联到别人上去了

}
