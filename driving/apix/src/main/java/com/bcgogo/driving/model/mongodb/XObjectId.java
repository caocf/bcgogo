package com.bcgogo.driving.model.mongodb;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午2:31
 */
public class XObjectId {
   String $oid;

  public XObjectId(String id) {
    this.$oid = id;
  }

  public String get$oid() {
    return $oid;
  }

  public void set$oid(String $oid) {
    this.$oid = $oid;
  }
}
