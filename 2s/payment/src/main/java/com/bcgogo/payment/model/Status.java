package com.bcgogo.payment.model;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-13
 * Time: 上午11:28
 * To change this template use File | Settings | File Templates.
 */
public enum Status {

  PENDING("PENDING"),
  COMPLETED("COMPLETED"),
  CANCELLED("CANCELLED");

  String name;

  Status(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
