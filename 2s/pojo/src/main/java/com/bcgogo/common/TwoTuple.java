package com.bcgogo.common;

/**
 * Created with IntelliJ IDEA.
 * User: wenjun
 * Date: 13-10-25
 * Time: 上午10:03
 * To change this template use File | Settings | File Templates.
 */
public class TwoTuple<A, B> {
  public final A first;
  public final B second;

  public TwoTuple(A a, B b) {
    first = a;
    second = b;
  }

  public String toString() {
    return "(" + first + "," + second + ")";
  }
}
