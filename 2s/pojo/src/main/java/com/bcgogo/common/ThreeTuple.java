package com.bcgogo.common;

/**
 * Created with IntelliJ IDEA.
 * User: wenjun
 * Date: 13-10-25
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 * 只作为返回值调用
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
  public final C third;

  public ThreeTuple(A a, B b, C c) {
    super(a, b);
    third = c;
  }

  public String toString() {
    return "(" + first + "," + second + "," + third + ")";
  }
}
