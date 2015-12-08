package com.bcgogo.common;

import com.bcgogo.utils.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xzhu  07/08/2012
 */

public class Pair<K, V> implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = -7450539197853112132L;
  private K key;
  private V value;

  public Pair() {
  }

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pair)) return false;

    final Pair pair = (Pair) o;

    if (key != null ? !key.equals(pair.key) : pair.key != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return (key != null ? key.hashCode() : 0);
  }

  @Override
  public String toString() {
    return "[" + key + ',' + value + "]";
  }

  public static void main(String args[]) {
    List<Pair<String,String>> list = new ArrayList<Pair<String, String>>();
    list.add(new Pair<String, String>("name","peter"));
    list.add(new Pair<String, String>("bran","台湾"));
    System.out.println(JsonUtil.listToJson(list));
    }
}
